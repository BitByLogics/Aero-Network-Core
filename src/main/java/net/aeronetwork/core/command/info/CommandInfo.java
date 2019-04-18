package net.aeronetwork.core.command.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.annotation.SubCommand;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Contains all information about an AeroCommand.
 */
@AllArgsConstructor
@Data
public class CommandInfo {

    private String name;
    private List<String> aliases;
    private Class<? extends Command> commandClass;
    private Map<SubCommand, Method> subCommandMethods;

    /**
     * Checks if the specified command matches the command
     * name or any alias that is being stored in this class.
     *
     * @param command The command to check.
     * @return TRUE if the command matches the command name
     * or an alias stored in this class, or FALSE otherwise.
     */
    public boolean isValid(String command) {
        if(this.name.equalsIgnoreCase(command)) return true;
        return aliases.stream()
                .filter(alias -> alias.equalsIgnoreCase(command))
                .findFirst()
                .orElse(null) != null;
    }

    /**
     * Checks if the specified sub-command exists.
     *
     * @param subCommand The sub command to check.
     * @return TRUE if the sub command exists, or FALSE
     * otherwise.
     */
    public boolean isSubCommand(String subCommand) {
        return getSubCommandMethod(subCommand) != null;
    }

    /**
     * Gets the method associated with the specified
     * sub-command.
     *
     * @param subCommand The name of the sub-command.
     * @return The associated sub-command method for the
     * specified sub-command name, or null if none.
     */
    public Method getSubCommandMethod(String subCommand) {
        CompletableFuture<Method> exists = new CompletableFuture<>();
        subCommandMethods.forEach((k, v) -> {
            if(k.name().equalsIgnoreCase(subCommand) || Arrays.stream(k.aliases()).anyMatch(alias -> alias.equalsIgnoreCase(subCommand)))
                exists.complete(v);
        });
        return exists.getNow(null);
    }
}
