package net.aeronetwork.core.database.mongo.dao;

import net.aeronetwork.core.player.AeroPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

public interface PlayerDAO extends DAO<AeroPlayer, ObjectId> {

    AeroPlayer getByUuid(UUID uuid);

    AeroPlayer getByAccountName(String accountName);

    void updateField(AeroPlayer player, String field, Object value);
}
