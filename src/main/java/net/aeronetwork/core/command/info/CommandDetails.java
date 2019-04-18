package net.aeronetwork.core.command.info;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains all data about an executed command.
 */
@Data
@AllArgsConstructor
public class CommandDetails {

    private String executedCommandName;
    private String[] args;
}
