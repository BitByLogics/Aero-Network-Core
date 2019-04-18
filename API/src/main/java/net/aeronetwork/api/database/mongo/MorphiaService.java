package net.aeronetwork.api.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class MorphiaService {

    private MongoClient client;
    private Morphia morphia;
    private Datastore datastore;

    @Deprecated
    public MorphiaService(String datastoreName, Class clazz) {
        List<MongoCredential> credentials = new ArrayList<>(
                Arrays.asList(MongoCredential.createCredential("root", "admin", "aeroiscool!".toCharArray()))
        );
        this.client = new MongoClient(new ServerAddress("162.251.166.138", 12345), credentials);
        this.morphia = new Morphia();

        this.morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return clazz.getClassLoader();
            }
        });

        this.datastore = morphia.createDatastore(client, datastoreName);
    }

    public MorphiaService(
            String host,
            int port,
            String userName,
            String database,
            String password,
            String datastoreName,
            Class storedClass) {
        List<MongoCredential> credentials = new ArrayList<>(
                Arrays.asList(MongoCredential.createCredential(userName, database, password.toCharArray()))
        );
        this.client = new MongoClient(new ServerAddress(host, port), credentials);
        this.morphia = new Morphia();

        this.morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return storedClass.getClassLoader();
            }
        });

        this.datastore = morphia.createDatastore(client, datastoreName);
    }
}
