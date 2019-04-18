package net.aeronetwork.core.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.aeronetwork.core.command.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class CommandManager {

    private List<Command> commands;
    private ExecutorService commandService;

    public CommandManager() {
        this.commands = Lists.newCopyOnWriteArrayList();

        // Default implementations
        commands.add(new EndAllServerProcessesCommand());
        commands.add(new HelpCommand());
        commands.add(new InvalidateRedisCommand());
        commands.add(new ListContainerCommand());
        commands.add(new ListServerCommand());
        commands.add(new ReloadAllPackagesCommand());
        commands.add(new ReloadPackageCommand());
        commands.add(new StartServerCommand());
        commands.add(new StopCommand());
        commands.add(new StopContainerCommand());
        commands.add(new StopServerCommand());
        commands.add(new ListPackagesCommand());

        init();
    }

    public void registerCommand(Command command) {
        this.commands.add(command);
    }

    public Command getCommand(String command) {
        return this.commands.stream()
                .filter(c -> c.getName().equalsIgnoreCase(command) || c.isAliasExist(command))
                .findFirst()
                .orElse(null);
    }

    public boolean executeCommand(CommandSender sender, String command) {
        String[] array = command.split(" ");
        if(array.length > 0) {
            String[] args = (array.length > 1 ? Arrays.copyOfRange(array, 1, array.length) : new String[0]);
            Command c = getCommand(array[0]);
            if(c != null) {
                c.execute(sender, args);
                return true;
            }
        }
        return false;
    }

    public void init() {
        this.commandService = Executors.newSingleThreadExecutor();
        this.commandService.submit(() -> {
            try {
                Scanner s = new Scanner(System.in);
                while(true) {
                    String command = s.nextLine();
                    boolean executed = executeCommand(new CommandSender() {
                        @Override
                        public void sendMessage(String message) {
                            System.out.println(message);
                        }

                        @Override
                        public void sendWarning(String warning) {
                            System.out.println("[WARNING] " + warning);
                        }

                        @Override
                        public void sendError(String error) {
                            System.err.println(error);
                        }
                    }, command);

                    if(!executed)
                        System.err.println("Invalid command! Use \"help\" for help.");

                    System.out.print("> ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
