package net.aeronetwork.api.database.redis.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import net.aeronetwork.api.database.redis.command.info.RedisCommandDetails;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Defines a base for all Redis commands.
 */
@Getter
public abstract class RedisCommand {

    private String name;
    private String description;
    private String usage;
    private List<String> aliases;

    public RedisCommand(@NonNull String name, @NonNull String description, @NonNull String usage, @Nullable List<String> aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = (aliases == null ? Lists.newArrayList() : aliases);
    }

    /**
     * Called when an incoming Redis message matches the
     * command's name or an alias.
     *
     * @param details All details about the executed command.
     */
    public abstract void execute(RedisCommandDetails details);
}
