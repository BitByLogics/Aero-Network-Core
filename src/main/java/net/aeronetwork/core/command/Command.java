package net.aeronetwork.core.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Defines the base for all Aero Bukkit/Spigot commands.
 */
@Getter
public abstract class Command {

    private String name;
    private String description;
    private String usage;
    private List<String> aliases;
    @Setter
    private Rank rank;
    @Setter
    private boolean allowedInGame = true;
    @Setter
    private boolean playerOnly = false;
    @Setter
    private boolean allowOp = false;

    public Command(@NonNull String name, @NonNull String description, @NonNull String usage, @Nullable List<String> aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = (aliases == null ? Lists.newArrayList() : aliases);
        this.rank = Rank.DEFAULT;
    }

    /**
     * Called when an incoming command matches the specified
     * command name or alias.
     *
     * @param player The player who executed the command, or null
     *               if it was a console issuer.
     * @param details All details about the executed command.
     */
    public abstract void execute(AeroPlayer player, CommandDetails details);
}
