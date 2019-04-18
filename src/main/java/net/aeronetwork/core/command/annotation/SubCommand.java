package net.aeronetwork.core.command.annotation;

import net.aeronetwork.core.command.CommandConstants;
import net.aeronetwork.core.player.rank.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as a valid sub-command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    String name();

    String desc();

    String usage();

    String[] aliases() default {};

    Rank rankRequired() default Rank.DEFAULT;

    int minArgs() default CommandConstants.IGNORE;

    int maxArgs() default CommandConstants.IGNORE;

    boolean playerOnly() default false;

    boolean allowOp() default false;
}
