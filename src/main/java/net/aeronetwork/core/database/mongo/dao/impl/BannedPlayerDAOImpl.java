package net.aeronetwork.core.database.mongo.dao.impl;

import net.aeronetwork.core.database.mongo.dao.BannedPlayerDAO;
import net.aeronetwork.core.player.punishment.player.BannedPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

public class BannedPlayerDAOImpl extends BasicDAO<BannedPlayer, ObjectId> implements BannedPlayerDAO {

    public BannedPlayerDAOImpl(Class<BannedPlayer> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public BannedPlayer getByUuid(UUID uuid) {
        Query<BannedPlayer> query = createQuery()
                .field("uuid").equal(uuid);
        return query.get();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<BannedPlayer> query = createQuery()
                .field("uuid").equal(uuid);
        UpdateOperations<BannedPlayer> update = createUpdateOperations()
                .set(field, value);

        update(query, update);
    }
}
