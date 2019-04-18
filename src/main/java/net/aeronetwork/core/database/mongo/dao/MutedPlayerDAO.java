package net.aeronetwork.core.database.mongo.dao;

import net.aeronetwork.core.player.punishment.player.MutedPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

public interface MutedPlayerDAO extends DAO<MutedPlayer, ObjectId> {

    MutedPlayer getByUuid(UUID uuid);

    void updateField(UUID uuid, String field, Object value);
}
