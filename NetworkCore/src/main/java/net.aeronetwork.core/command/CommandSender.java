package net.aeronetwork.core.command;

public interface CommandSender {

    void sendMessage(String message);

    void sendWarning(String warning);

    void sendError(String error);
}
