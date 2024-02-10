package me.arahis.detchat.commands;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.utils.TextHandler;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    protected final DETChatPlugin plugin = DETChatPlugin.getPlugin();
    protected final TextHandler tH = plugin.getTextHandler();

    public abstract String getName();
    public abstract String getPermission();
    public abstract String getUsage();
    public abstract Boolean isConsoleUsable();
    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> getTabSuggests(CommandSender sender, String[] args);

}
