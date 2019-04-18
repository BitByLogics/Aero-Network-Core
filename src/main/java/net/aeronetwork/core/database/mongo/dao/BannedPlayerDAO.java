package net.aeronetwork.core.database.mongo.dao;

import net.aeronetwork.core.player.punishment.player.BannedPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

public interface BannedPlayerDAO extends DAO<BannedPlayer, ObjectId> {

    BannedPlayer getByUuid(UUID uuid);

    void updateField(UUID uuid, String field, Object value);
}
