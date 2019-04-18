package net.aeronetwork.core.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.Getter;
import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.config.CoreConfig;
import net.aeronetwork.core.docker.container.AeroContainer;
import net.aeronetwork.core.docker.container.impl.GenericAeroContainer;
import net.aeronetwork.core.docker.file.FileConstants;
import net.aeronetwork.core.server.AeroServer;
import net.aeronetwork.core.server.ServerType;
import net.aeronetwork.core.util.Callback;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Defines all the processes related to the production environment for
 * Aero Network. Most processes are executed asynchronously to prevent
 * blockage, and to keep up with demand during peak hours.
 */
@Getter
public class NetworkManager {

    private DockerClient dockerClient;
    private Map<String, String> imageCache;
    private Cache<String, List<DockerPackage>> packageCache;

    private List<AeroContainer> activeContainers;
    private List<AeroServer> activeServers = Lists.newCopyOnWriteArrayList();

    private File rootDirectory;

    private Gson gson;
    private Gson prettyGson;

    private ScheduledExecutorService serverUpdater;
    private ScheduledExecutorService serverManager;

    private CoreConfig coreConfig;
    private Map<ServerType, String> serverPrefixes = Maps.newConcurrentMap();
    private final String PREFIX = "aero_";
    private final String SERVER_INFO_KEY = "aero_servers";

    /**
     * Constructs a new instance of NetworkManager, setting up all required
     * tasks. To get the global reference of this class, use {@link NetworkCore#NETWORK_MANAGER},
     * creating another instance of this class could lead to many issues runtime.
     */
    public NetworkManager() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
        this.imageCache = Maps.newConcurrentMap();
        this.packageCache = CacheBuilder.newBuilder()
                .build();

        this.activeContainers = Lists.newCopyOnWriteArrayList();

        this.gson = new Gson();
        this.prettyGson = new GsonBuilder().setPrettyPrinting().create();

        this.serverUpdater = Executors.newSingleThreadScheduledExecutor();
        this.serverManager = Executors.newSingleThreadScheduledExecutor();

        this.serverPrefixes.put(ServerType.SERVER_LOBBY, "kitkat");
        this.serverPrefixes.put(ServerType.SERVER_GAME, "aero");
        this.serverPrefixes.put(ServerType.SESSION_PROXY, "hershey");
        this.serverPrefixes.put(ServerType.TRANSPORT_PROXY, "rolo");

        setup();
    }

    /**
     * Sets up everything required to have a functioning and scalable network.
     *
     * The first process to in this multi-processed setup is to define the root
     * directory for where the config, packages, and resources will be loaded from.
     * If a resource is not available, it will be created with any applicable default
     * values.
     *
     * Pre-built image fetching and caching is immediately executed. An Aero image
     * takes the form {@link NetworkManager#PREFIX}[instance].
     *
     * Following image caching, all running containers will be fetched and parsed
     * to find Aero containers. If the container is an Aero container, the status
     * of the container will be checked. All active containers will be cached, while
     * all inactive containers will be removed immediately after the check phase.
     *
     * Once the above processes are completed, Aero deployment processes are started.
     * Before any other process can be executed, all packages need to be loaded and
     * parsed. Once this completes, any packages without images will have images
     * created.
     *
     * Persistent management processes are then executed at timed intervals. The first
     * timed process manages the updating of servers, which removes all inactive containers
     * locally cached, on the host system, and from Redis.
     *
     * @see FileConstants
     * @see NetworkManager#loadPackage(String)
     * @see NetworkManager#createImage(DockerPackage)
     */
    private void setup() {
        this.rootDirectory = new File(Paths.get("").toUri());

        // Create all directories and files
        Arrays.stream(FileConstants.values()).forEach(file -> {
            File f = new File(this.rootDirectory.getAbsolutePath() + file.getRelativePath());
            if(!f.exists()) {
                switch(file.getFileType()) {
                    case DIRECTORY:
                        f.mkdir();
                        break;
                    case FILE:
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        // Get or create config
        try {
            File config = new File(getRootDirectory().getAbsolutePath() + File.separatorChar + "config.json");
            if(config.exists()) {
                this.coreConfig = gson.fromJson(new FileReader(config), CoreConfig.class);
            } else {
                this.coreConfig = new CoreConfig();
                // For prettying printing
                FileWriter writer = new FileWriter(config);
                this.prettyGson.toJson(this.coreConfig, writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Cache all images
        dockerClient.listImagesCmd().exec().forEach(image -> {
            if(image.getRepoTags() != null && image.getRepoTags().length >= 1 &&
                    image.getRepoTags()[0].startsWith(PREFIX)) {
                String[] tag = image.getRepoTags()[0].split(":");
                this.imageCache.put(tag[0], image.getId());
            }
        });

        // Fetch previously started servers
        List<Container> containers = dockerClient.listContainersCmd().exec();
        List<String> invalidateIds = Lists.newArrayList();

        containers.forEach(container -> {
            if(isValidName(container)) {
                if(container.getStatus().toLowerCase().startsWith("active")) {
                    String id = container.getId();
                    String aeroId = container.getNames()[0].replaceFirst("/", "");
                    int port = container.getPorts().length >= 1 && container.getPorts()[0].getPublicPort() != null ?
                            container.getPorts()[0].getPublicPort() : 0;

                    this.activeContainers.add(new GenericAeroContainer(id, aeroId, port));
                } else if(container.getStatus().toLowerCase().startsWith("exited") ||
                        container.getStatus().toLowerCase().startsWith("created")) {
                    invalidateIds.add(container.getId());
                }
            }
        });

        System.out.println("Invalidating old containers.");
        invalidateIds.forEach(id -> dockerClient.removeContainerCmd(id).exec());
        System.out.println("Invalidated " + invalidateIds.size() + " container(s).");

        System.out.println("Loading all packages.");
        loadAllPackages();
        System.out.println("Loaded all packages.");

        System.out.println("Building images for packages without images.");
        this.packageCache.asMap().forEach((instance, pkgs) -> pkgs.forEach(pkg -> {
            if(this.imageCache.getOrDefault(constructImageKey(pkg), null) == null)
                createImage(pkg);
        }));
        System.out.println("Finished building images for packages without images.");

        System.out.println("Starting server updater thread.");
        this.serverUpdater.scheduleAtFixedRate(() -> {
            try {
                List<Container> fetchedContainers = getDockerClient().listContainersCmd().exec();

                // Invalidating containers that don't exist (locally)
                getActiveContainers().forEach(container -> {
                    CompletableFuture<Boolean> contains = new CompletableFuture<>();
                    fetchedContainers.forEach(c -> {
                        if(c.getId().equals(container.getDockerId()))
                            contains.complete(true);
                    });

                    if(!contains.getNow(false)) {
                        getActiveContainers().remove(container);
                        AeroServer server = getServerByAeroId(container.getAeroId());
                        if(server != null)
                            getActiveServers().remove(server);
                    }
                });

                // Invalidating containers that are no longer running
                fetchedContainers.forEach(container -> {
                    if(container.getNames().length >= 1 && isValidName(container)) {
                        String status = container.getStatus().toLowerCase();
                        if(!status.equalsIgnoreCase("active") &&
                                !status.equalsIgnoreCase("restarting")) {
                            Executors.newSingleThreadExecutor().submit(() ->
                                    getDockerClient().removeContainerCmd(container.getId())
                                            .withForce(true)
                                            .exec()
                            );
                            AeroContainer matched = getContainerByDockerId(container.getId());
                            if(matched != null) {
                                this.getActiveContainers().remove(matched);
                                AeroServer server = getServerByAeroId(matched.getAeroId());
                                if(server != null)
                                    getActiveServers().remove(server);
                            }
                            NetworkCore.SERVER_MANAGER.unregisterServer(container.getNames()[0]);
                        }
                    }
                });
                try (Jedis jedis = NetworkCore.REDIS_MANAGER.getJedisPool().getResource()) {
                    List<AeroServer> newActiveServers = Lists.newArrayList();
                    List<String> invalidIds = Lists.newArrayList();
                    jedis.hgetAll(SERVER_INFO_KEY).forEach((id, server) -> {
                        AeroServer as = gson.fromJson(server, AeroServer.class);
                        if(getContainerByAeroId(as.getAeroId()) == null) {
                            invalidIds.add(as.getAeroId());
                        } else {
                            newActiveServers.add(as);
                        }
                    });

                    if(invalidIds.size() >= 1)
                        jedis.hdel(SERVER_INFO_KEY, invalidIds.toArray(new String[0]));
                    System.out.println("Invalidated " + invalidIds.size() + " container(s) from Redis!");
                    this.activeServers = newActiveServers;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.SECONDS);

        System.out.println("Starting server manager thread.");
        this.serverManager.scheduleAtFixedRate(() ->
            packageCache.asMap().forEach((instance, pkgs) -> pkgs.forEach(pkg -> {
                List<AeroServer> servers = getServersById(pkg.getIds().get(0));
                if(servers.size() < pkg.getCache()) {
                    // Just start one package regardless of what the difference is (to prevent overloading)
                    System.out.println("Upscaling to meet cache requirement for instance " + instance + ":" +
                            pkg.getIds().get(0));
                    startPackage(pkg, new Callback<String>() {
                        @Override
                        public void info(String s) {
                            System.out.println(s);
                        }

                        @Override
                        public void error(String s) {
                            System.err.println(s);
                        }
                    });
                }
            })), 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Shuts down the manager by ending all threads, and invalidating the package
     * cache.
     */
    public void shutdown() {
        this.serverUpdater.shutdown();
        this.serverManager.shutdown();
        this.packageCache.invalidateAll();
    }

    /**
     * Gets a filed based on a specified {@link FileConstants} constant. All returned
     * files are relative to the root directory of the executed JAR.
     *
     * @param constant The file to get.
     * @return A new {@link File} linking to path of a {@link FileConstants} constant.
     */
    public File getFile(FileConstants constant) {
        return new File(this.rootDirectory.getAbsolutePath() + constant.getRelativePath());
    }

    /**
     * Gets a package by a specific ID (ignoring case). Since packages are able to have
     * more than one ID, this method may not return the package that is being looked for.
     *
     * @param id The ID of the package.
     * @return The first {@link DockerPackage} found, or null if there are no packages with
     * the specific ID.
     */
    public DockerPackage getPackage(String id) {
        CompletableFuture<DockerPackage> dockerPkg = new CompletableFuture<>();
        packageCache.asMap().forEach((pkgId, pkgs) ->
            pkgs.forEach(pkg -> {
                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                pkg.getIds().forEach(id2 -> {
                    if(id2.equalsIgnoreCase(id))
                        contains.complete(true);
                });

                if(contains.getNow(false)) {
                    dockerPkg.complete(pkg);
                }
            })
        );

        return dockerPkg.getNow(null);
    }

    /**
     * Gets a {@link AeroContainer} by the specified Docker ID.
     *
     * @param dockerId The Docker ID of the container.
     * @return The {@link AeroContainer} associated with the ID, or null if no container
     * was matched.
     */
    public AeroContainer getContainerByDockerId(String dockerId) {
        return this.activeContainers.stream()
                .filter(container -> container.getDockerId().equals(dockerId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a {@link AeroContainer} by the specified Aero ID.
     *
     * @param aeroId The Aero ID of the container.
     * @return The {@link AeroContainer} associated with the ID, or null if no container
     * was matched.
     */
    public AeroContainer getContainerByAeroId(String aeroId) {
        return this.activeContainers.stream()
                .filter(container -> container.getAeroId().equals(aeroId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a {@link AeroContainer} by the specified port.
     *
     * @param port The port of the container.
     * @return The {@link AeroContainer} associated with the ID, or null if no container
     * was matched.
     */
    public AeroContainer getContainerByPort(int port) {
        return this.activeContainers.stream()
                .filter(container -> container.getBoundPort() == port)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a {@link AeroServer} by the specified Aero ID.
     *
     * @param aeroId the Aero ID of the server.
     * @return The {@link AeroServer} associated with tie ID, or null if no container was
     * matched.
     */
    public AeroServer getServerByAeroId(String aeroId) {
        return this.activeServers.stream()
                .filter(server -> server.getAeroId().equals(aeroId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all servers deployed with the specified instance.
     *
     * @param instance The instance to look for.
     * @return An {@link ArrayList} containing all deployed servers with the specified
     * instance.
     */
    public List<AeroServer> getServersByInstance(String instance) {
        return this.activeServers.stream()
                .filter(server -> server.getInstance().equals(instance))
                .collect(Collectors.toList());
    }

    /**
     * Gets all servers deployed with the specified ID. IDs are case-sensitive.
     *
     * @param id The ID to look for.
     * @return An {@link ArrayList} containing all deployed servers with the specified
     * ID.
     */
    public List<AeroServer> getServersById(String id) {
        return this.activeServers.stream()
                .filter(server -> server.getIds().contains(id))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the specified containers name is a valid Aero name.
     *
     * @param container The container to check.
     * @return TRUE if the name is valid, or FALSE otherwise.
     */
    public boolean isValidName(Container container) {
        if(container.getNames().length >= 1) {
            CompletableFuture<Boolean> valid = new CompletableFuture<>();
            getServerPrefixes().values().forEach(prefix -> {
                if(container.getNames()[0].startsWith(prefix))
                    valid.complete(true);
            });
            return valid.getNow(false);
        }
        return false;
    }

    public void loadPackage(String instance) {
        try {
            File mainPkgDirectory = getFile(FileConstants.PACKAGES_DIRECTORY);
            File pkgDirectory = new File(mainPkgDirectory.getAbsolutePath() + File.separatorChar + instance);
            if(pkgDirectory.exists()) {
                if(pkgDirectory.isDirectory()) {
                    File jarFile = Arrays.stream(pkgDirectory.listFiles())
                            .filter(File::isFile)
                            .filter(file -> file.getName().endsWith(".jar"))
                            .findFirst().orElse(null);
                    if(jarFile != null) {
                        JarFile jar = new JarFile(jarFile);
                        Enumeration<JarEntry> entries = jar.entries();
                        List<DockerPackage> packages = Lists.newArrayList();

                        while(entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            if(entry.getName().startsWith("deploy") && entry.getName().endsWith(".json")) {
                                InputStream in = jar.getInputStream(entry);
                                DockerPackage dockerPackage =
                                        gson.fromJson(new InputStreamReader(in), DockerPackage.class);
                                dockerPackage.setInstance(instance);
                                dockerPackage.setHoldingJar(jarFile.getName().replaceAll(".jar", "") + ".jar");
                                packages.add(dockerPackage);
                            }
                        }
                        packageCache.put(instance, packages);

                    } else {
                        System.out.println("JAR file does not exist for package " + instance + ". " +
                                "Looking for blank_package.json to treat the package as a blank package.");
                        File blankPackageFile = new File(pkgDirectory.getAbsolutePath() +
                                File.separatorChar + "blank_package.json");
                        if(blankPackageFile.exists()) {
                            System.out.println("Found blank_package.json, creating a blank package if it can be parsed.");
                            DockerPackage pkg = gson.fromJson(new FileReader(blankPackageFile), DockerPackage.class);
                            if(pkg != null) {
                                pkg.setInstance(instance);
                                packageCache.put(instance, Lists.newArrayList(pkg));
                            } else throw new JsonParseException("Could not parse blank_package.json!");
                        } else throw new UnsupportedOperationException("Package " + instance + " does not " +
                                "contain a JAR file, nor a blank_package.txt file");
                    }
                } else throw new UnsupportedOperationException("Package cannot be a file");
            } else throw new NullPointerException("Package does not exist");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllPackages() {
        Arrays.stream(getFile(FileConstants.PACKAGES_DIRECTORY).listFiles())
                .filter(File::isDirectory)
                .forEach(file -> loadPackage(file.getName()));
    }

    public String constructImageKey(DockerPackage pkg) {
        return PREFIX + pkg.getInstance() + "_" + pkg.getIds().get(0);
    }

    public String newAeroId(DockerPackage pkg) {
        Random r = new Random();
        String id;
        do {
            id = getServerPrefixes().get(pkg.getServerType()) + r.nextInt(10_000);
        } while(getContainerByAeroId(id) != null);

        return id;
    }

    public int newBoundPort() {
        if(activeContainers.size() >= (this.coreConfig.getPortRangeMax() - this.coreConfig.getPortRangeMin()))
            throw new StackOverflowError("Active containers exceed port range limits");

        Random r = new Random();
        int port;
        do {
            port = this.coreConfig.getPortRangeMin() +
                    r.nextInt(this.coreConfig.getPortRangeMax() - this.coreConfig.getPortRangeMin() + 1);
        } while(getContainerByPort(port) != null);

        return port;
    }

    public int newBoundPort(DockerPackage pkg) {
        if(pkg.isPortRangeValid()) {
            Random r = new Random();

            int port;
            do {
                port = pkg.getPortRangeMin() + r.nextInt(pkg.getPortRangeMax() - pkg.getPortRangeMin() + 1);
            } while(getContainerByPort(port) != null);

            return port;
        }
        return newBoundPort();
    }

    public CompletableFuture<String> createImage(DockerPackage pkg) {
        CompletableFuture<String> imageIdCompletable = new CompletableFuture<>();
        File instanceDirectory = new File(getFile(FileConstants.PACKAGES_DIRECTORY).getAbsolutePath() +
                File.separatorChar + pkg.getInstance());

        List<String> dockerFile = Lists.newArrayList();

        // Image + arguments
        dockerFile.add("FROM alpine:3.5");
        dockerFile.add("\nARG SERVER_ROOT=aero/");

        // Environment variables
        if(pkg.getEnvVars() != null)
            pkg.getEnvVars().forEach(var -> dockerFile.add("\nENV " + var));

        // RUN processes
        dockerFile.add("\nRUN apk add --update openjdk8-jre-base");
        dockerFile.add("\nRUN apk add zip");
        dockerFile.add("\nRUN mkdir $SERVER_ROOT");
        dockerFile.add("\nADD resources/" + pkg.getServerType().getJarName() + " $SERVER_ROOT");

        pkg.getServerType().getConfigFiles().forEach(file ->
                dockerFile.add("\nADD resources/" + file + " $SERVER_ROOT"));

        String worldZip = "world.zip";
        if(pkg.getWorld() != null) {
            File defaultWorld = new File(instanceDirectory.getAbsolutePath() + File.separatorChar + pkg.getWorld());
            dockerFile.add("\nADD packages/" + instanceDirectory.getName() + "/" +
                    defaultWorld.getName() + " $SERVER_ROOT");
            worldZip = defaultWorld.getName();
        } else {
            dockerFile.add("\nADD resources/world.zip $SERVER_ROOT");
        }

        // Root
        if(pkg.getRoot() != null) {
            File root = new File(instanceDirectory.getAbsolutePath() + File.separatorChar + pkg.getRoot());
            if(root.exists()) {
                dockerFile.add("\nADD packages/" + instanceDirectory.getName() + "/" + pkg.getRoot() +
                        " $SERVER_ROOT");
            } else {
                System.err.println("Root for instance " + instanceDirectory.getName() + ", package " +
                        pkg.getIds().get(0) + " is specified but doesn't exist!");
            }
        }

        dockerFile.add("\nWORKDIR $SERVER_ROOT");
        dockerFile.add("\nRUN unzip " + worldZip);

        if(pkg.getRoot() != null)
            dockerFile.add("\nRUN unzip " + pkg.getRoot());

        // Plugin related processes
        dockerFile.add("\nRUN mkdir plugins/");
        dockerFile.add("\nADD resources/AeroCore.jar plugins/");
        if(pkg.getServerType().getRequiredPlugins() != null) {
            pkg.getServerType().getRequiredPlugins().forEach(plugin ->
                dockerFile.add("\nADD resources/" + plugin + ".jar plugins/"));
        }
        if(pkg.getServerType().getRequiredConfigs() != null)
            pkg.getServerType().getRequiredConfigs().forEach(config ->
                dockerFile.add("\nADD resources/" + config + ".zip plugins/"));

        if(pkg.getHoldingJar() != null)
            dockerFile.add("\nADD packages/" + pkg.getInstance().trim() + "/" +
                    pkg.getHoldingJar().trim() + " plugins/");

        System.out.println(pkg.getInstance() + "/" + pkg.getHoldingJar());

        // Additional plugins
        if(pkg.getIncludedPlugins() != null)
            pkg.getIncludedPlugins().forEach(plugin ->
                    dockerFile.add("\nADD resources/" + plugin + ".jar plugins/"));

        // Unzip any and all configs
        dockerFile.add("\nWORKDIR plugins/");
        if(pkg.getServerType().getRequiredConfigs() != null)
            pkg.getServerType().getRequiredConfigs().forEach(config ->
                    dockerFile.add("\nRUN unzip " + config + ".zip"));
        dockerFile.add("\nWORKDIR /$SERVER_ROOT");

        // CMD and EXPOSE
        dockerFile.add("\nCMD java " + pkg.getServerType().getCommandLineArguments() + " -jar " +
                pkg.getServerType().getJarName() +
                (pkg.getServerType().getPostCommandLineArguments() != null &&
                        !pkg.getServerType().getPostCommandLineArguments().isEmpty() ?
                        " " + pkg.getServerType().getPostCommandLineArguments().trim() :
                        ""));
        dockerFile.add("\nEXPOSE 25565");

        File file = new File(getRootDirectory().getAbsolutePath() + File.separatorChar +
                "Dockerfile_" + pkg.getInstance() + "_" + pkg.getIds().get(0));

        try {
            FileWriter writer = new FileWriter(file);
            for(String process : dockerFile)
                writer.write(process);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            System.out.println("CREATING IMAGE FOR PACKAGE instance=" + pkg.getInstance() + ", package=" +
                    pkg.getIds().get(0));

            String id = dockerClient.buildImageCmd()
                    .withTags(Sets.newHashSet(constructImageKey(pkg)))
                    .withDockerfile(file)
                    .exec(new BuildImageResultCallback() {
                        @Override
                        public void onNext(BuildResponseItem item) {
                            super.onNext(item);
                            System.out.println(!item.isBuildSuccessIndicated() ? "Building..." : "BUILD FINISHED");
                            if(item.getErrorDetail() != null) {
                                System.err.println(item.getErrorDetail().getMessage());
                            }
                            if(item.isBuildSuccessIndicated()) {
                                System.out.println("IMAGE BUILD: " + item.getImageId() + " Successful!");
                            }
                        }
                    }).awaitImageId();
            System.out.println("IMAGE ID: " + id);

            // Invalidate old image if it exists
            imageCache.remove(constructImageKey(pkg));

            // Cache image
            imageCache.put(constructImageKey(pkg), id);

            // Delete file
            file.delete();

            imageIdCompletable.complete(id);
        });

        return imageIdCompletable;
    }

    /**
     * Starts a package using the cached image for the package. All setup
     * is done synchronously, while the starting and deploying of the package
     * container is done asynchronously.
     *
     * This method manages the binding of ports (using the global range, or the
     * package's specified range if it's not set to -1), and the environment variables
     * (ex. IP, bound port, instance name, private server [boolean]).
     *
     * @param pkg
     * @param callback
     */
    public synchronized void startPackage(DockerPackage pkg, Callback<String> callback) {
        if(this.imageCache.getOrDefault(constructImageKey(pkg), null) != null) {
            callback.info("Preparing port bindings");
            Ports portBindings = new Ports();
            int boundPort = newBoundPort(pkg);
            portBindings.bind(ExposedPort.tcp(25565), Ports.Binding.bindPort(boundPort));

            callback.info("Preparing image");
            String imageId = this.imageCache.getOrDefault(constructImageKey(pkg), null);

            callback.info("Setting up environment");

            String aeroId = newAeroId(pkg);

            List<String> envVars = Lists.newArrayList(
                    "AERO_ID=" + aeroId,
                    "AERO_IP=none",
                    "AERO_BOUND_PORT=" + boundPort,
                    "AERO_INSTANCE_NAME=" + pkg.getInstance() + ":" + pkg.getIds().get(0),
                    "AERO_PRIVATE_SERVER=false"
            );

            if(imageId != null) {
                callback.info("Image fetch done via cache");
                Executors.newSingleThreadExecutor().submit(() -> {
                    callback.info("Moving to asynchronous operations");
                    callback.info("Preparing container");

                    try {
                        CreateContainerResponse response = dockerClient.createContainerCmd(imageId)
                                .withPortBindings(portBindings)
                                .withEnv(envVars)
                                .withName(aeroId)
                                .withRestartPolicy(RestartPolicy.alwaysRestart())
                                .exec();

                        callback.info("Created container with ID=" + response.getId());

                        callback.info("Starting container");
                        dockerClient.startContainerCmd(aeroId).exec();
                        callback.info("Started container");

                        AeroContainer container = new GenericAeroContainer(response.getId(), aeroId, boundPort);
                        this.activeContainers.add(container);

                        callback.info("Validating server");
                        NetworkCore.SERVER_MANAGER.registerServer(
                                aeroId,
                                pkg.getInstance(),
                                pkg.getIds(),
                                coreConfig.getIp(),
                                boundPort,
                                pkg.getServerType()
                        );
                        callback.info("Validated server");

                        callback.info("Final results: \n" + this.prettyGson.toJson(container));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                callback.error("Image ID does not exist for " +
                        pkg.getInstance() + ": " + pkg.getIds().get(0) + "!");
            }
        } else {
            callback.error("Image for package " + pkg.getInstance() + ": " +
                    pkg.getIds().get(0) + " does not exist.");
        }
    }

    /**
     * Stops a container, removing it from the cache.
     *
     * @param container The container to stop and remove.
     * @return A {@link CompletableFuture} returning whether the server was
     * stopped or not.
     */
    public synchronized CompletableFuture<Boolean> stopContainer(AeroContainer container) {
        CompletableFuture<Boolean> completed = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                getDockerClient().removeContainerCmd(container.getDockerId())
                        .withForce(true)
                        .exec();
                completed.complete(true);

//                getActiveContainers().remove(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return completed;
    }

    /**
     * Stops a server, removing it from cached servers and Redis.
     *
     * @param server The server to stop and remove.
     * @return A {@link CompletableFuture} returning whether the server was
     * stopped or not.
     */
    public synchronized CompletableFuture<Boolean> stopServer(AeroServer server) {
        CompletableFuture<Boolean> completed;
        AeroContainer container = getContainerByAeroId(server.getAeroId());
        if(container != null) {
            completed = stopContainer(container);
        } else {
            completed = new CompletableFuture<>();
            completed.complete(false);
        }
        return completed;
    }
}
