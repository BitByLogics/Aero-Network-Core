package net.aeronetwork.core.command;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Command {

    private String name;
    private String description;
    private String usage;
    private List<String> aliases;

    public Command(String name, String description, String usage, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = (aliases == null ? Lists.newArrayList() : aliases);
    }

    public final boolean isAliasExist(String alias) {
        return this.aliases.stream()
                .filter(alias::equalsIgnoreCase)
                .findFirst()
                .orElse(null) != null;
    }

    public abstract void execute(CommandSender sender, String[] args);
}
