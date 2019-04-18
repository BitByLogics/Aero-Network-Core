package net.aeronetwork.core.database.mongo.dao.impl;

import net.aeronetwork.core.database.mongo.dao.PlayerDAO;
import net.aeronetwork.core.player.AeroPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

public class PlayerDAOImpl extends BasicDAO<AeroPlayer, ObjectId> implements PlayerDAO {

    public PlayerDAOImpl(Class<AeroPlayer> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public AeroPlayer getByUuid(UUID uuid) {
        Query<AeroPlayer> query = createQuery()
                .field("uuid").equal(uuid);
        return query.get();
    }

    @Override
    public AeroPlayer getByAccountName(String accountName) {
        Query<AeroPlayer> query = createQuery()
                .field("accountName").equal(accountName);
        return query.get();
    }

    @Override
    public void updateField(AeroPlayer player, String field, Object value) {
        Query<AeroPlayer> query = createQuery()
                .field("uuid").equal(player.getUuid());

        UpdateOperations<AeroPlayer> update = createUpdateOperations()
                .set(field, value);

        update(query, update);
    }
}
