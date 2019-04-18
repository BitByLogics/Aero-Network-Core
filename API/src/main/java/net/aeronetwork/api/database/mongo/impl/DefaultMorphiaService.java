package net.aeronetwork.api.database.mongo.impl;

import net.aeronetwork.api.database.mongo.MorphiaService;

public class DefaultMorphiaService extends MorphiaService {

    public DefaultMorphiaService(String datastoreName, Class clazz) {
        super(datastoreName, clazz);
    }

    public DefaultMorphiaService(String host, int port, String userName, String database, String password, String datastoreName, Class storedClass) {
        super(host, port, userName, database, password, datastoreName, storedClass);
    }
}
