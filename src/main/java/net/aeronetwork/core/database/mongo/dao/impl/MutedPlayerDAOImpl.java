package net.aeronetwork.core.database.mongo.dao.impl;

import net.aeronetwork.core.database.mongo.dao.MutedPlayerDAO;
import net.aeronetwork.core.player.punishment.player.MutedPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

public class MutedPlayerDAOImpl extends BasicDAO<MutedPlayer, ObjectId> implements MutedPlayerDAO {

    public MutedPlayerDAOImpl(Class<MutedPlayer> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public MutedPlayer getByUuid(UUID uuid) {
        Query<MutedPlayer> query = createQuery()
                .field("uuid").equal(uuid);
        return query.get();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<MutedPlayer> query = createQuery()
                .field("uuid").equal(uuid);
        UpdateOperations<MutedPlayer> update = createUpdateOperations()
                .set(field, value);

        update(query, update);
    }
}
