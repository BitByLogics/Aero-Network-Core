package net.aeronetwork.api.database.mongo.dao;

import net.aeronetwork.api.player.UndefinedPlayer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

public interface PlayerDAO<E extends UndefinedPlayer> extends DAO<E, ObjectId> {

    E getByUuid(UUID uuid);

    void updateField(E player, String field, Object value);
}
