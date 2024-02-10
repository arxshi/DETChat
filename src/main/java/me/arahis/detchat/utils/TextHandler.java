package me.arahis.detchat.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TextHandler {

    public static String prefix;
    private final String messagePrefixColor;
    private final String errorPrefixColor;

    public TextHandler() {
        prefix = Config.getString("options.prefix");
        messagePrefixColor = Config.getString("options.message-prefix-color");
        errorPrefixColor = Config.getString("options.error-prefix-color");
    }

    public void sendMessageWithPrefix(CommandSender sender, String message) {
        sendMessage(sender, color(messagePrefixColor + prefix + message));
    }

    public void sendErrorWithPrefix(CommandSender sender, String error) {
        sendMessage(sender, color(errorPrefixColor + prefix + error));
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public String buildStringFromArray(int pos, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public String color(String toTranslate) {
        return ChatColor.translateAlternateColorCodes('&', toTranslate);
    }

}
