package me.arahis.detchat.commands;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoreChatCommand implements CommandExecutor, TabCompleter {

    private final DETChatPlugin plugin;
    private final TextHandler tH;

    public CoreChatCommand(DETChatPlugin plugin) {
        this.plugin = plugin;
        tH = plugin.getTextHandler();
    }

    List<SubCommand> subCommands = new ArrayList<>();

    public CoreChatCommand addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.unknown-command"));
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (args[0].equals(subCommand.getName())) {

                if(!subCommand.isConsoleUsable()) {
                    tH.sendErrorWithPrefix(sender, Config.getString("messages.only-for-players"));
                    return true;
                }

                String permission = subCommand.getPermission();

                if(permission != null) {
                    if(!sender.hasPermission(permission)) {
                        tH.sendErrorWithPrefix(sender, Config.getString("messages.no-permission"));
                        return true;
                    }
                }

                subCommand.execute(sender, args);

            }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> options;
        options = subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        }

        for (SubCommand subCommand : subCommands) {
            if (args[0].equals(subCommand.getName())) {
                return subCommand.getTabSuggests(sender, args);
            }
        }

        return null;
    }
}
