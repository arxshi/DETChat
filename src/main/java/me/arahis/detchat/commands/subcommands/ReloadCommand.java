package me.arahis.detchat.commands.subcommands;

import me.arahis.detchat.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "detchat.reload";
    }

    @Override
    public String getUsage() {
        return "/chat reload";
    }

    @Override
    public Boolean isConsoleUsable() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.saveConfig();
        tH.sendMessageWithPrefix(sender, "Chat configuration reloaded!");
    }

    @Override
    public List<String> getTabSuggests(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
