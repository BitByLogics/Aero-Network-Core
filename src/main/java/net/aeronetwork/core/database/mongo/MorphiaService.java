package net.aeronetwork.core.database.mongo;

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
public class MorphiaService {

    private MongoClient client;
    private Morphia morphia;
    private Datastore datastore;

    public MorphiaService(String datastoreName, Class clazz) {
        List<MongoCredential> credentials = new ArrayList<>(
                Arrays.asList(MongoCredential.createCredential("root", "admin", "example_password".toCharArray()))
        );
        this.client = new MongoClient(new ServerAddress("127.0.0.1", 12345), credentials);
        this.morphia = new Morphia();

        this.morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return clazz.getClassLoader();
            }
        });

        this.datastore = morphia.createDatastore(client, datastoreName);
    }
}
