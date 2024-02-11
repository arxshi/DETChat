package me.arahis.detchat.commands;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.managers.PMManager;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PMCommand implements CommandExecutor, TabCompleter {

    private final DETChatPlugin plugin;
    private final TextHandler tH;
    private final ChatMessageBuilder cMB;
    private final PMManager pMM;

    public PMCommand() {
        plugin = DETChatPlugin.getPlugin();
        tH = plugin.getTextHandler();
        cMB = plugin.getChatMessageBuilder();
        pMM = plugin.getPmManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String senderName;
        UUID playerUuid = null;

        String generalPlayerPrefix = Config.getString("chat.prefix");
        String playerPrefix = "";

        boolean isColored = true;

        if (sender instanceof ConsoleCommandSender) {
            senderName = tH.color("&d[CONSOLE]");
        } else {
            Player player = (Player) sender;
            senderName = player.getName();
            playerUuid = player.getUniqueId();
            playerPrefix = PlaceholderAPI.setPlaceholders(player, generalPlayerPrefix);
            playerPrefix = tH.color(playerPrefix);
            if (!player.hasPermission("detchat.coloredchat")) {
                isColored = false;
            }
        }

        if (args.length < 1) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.wrong-usage"));
            tH.sendErrorWithPrefix(sender, command.getUsage());
            return true;
        }

        if (args.length < 2) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.empty-message"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.unknown-player"));
            return true;
        }

        String targetName = target.getName();
        UUID targetUuid = target.getUniqueId();
        String targetPrefix = PlaceholderAPI.setPlaceholders(target, generalPlayerPrefix);
        targetPrefix = tH.color(targetPrefix);


        String playerDivider = Config.getString("chat.pm.player-divider");
        String messageDivider = Config.getString("chat.pm.message-divider");
        String playerColor = Config.getString("chat.pm.player-name-color");
        String symbolColor = Config.getString("chat.pm.symbol-color");

        String message = tH.buildStringFromArray(1, args);

        if (isColored) {
            message = tH.color(message);
        }

        BaseComponent[] messageComponent = cMB.buildMessageComponent(message);

        ComponentBuilder toSender = new ComponentBuilder();
        toSender.append(tH.color(playerColor + "Вы"))
                .append(tH.color(symbolColor + playerDivider))
                .append(tH.color(targetPrefix + playerColor + targetName))
                .append(tH.color(symbolColor + messageDivider))
                .append(messageComponent);

        ComponentBuilder toTarget = new ComponentBuilder();
        toTarget.append(tH.color(playerPrefix + playerColor + senderName + symbolColor + playerDivider + playerColor + "Вы"))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(tH.color("&aНажмите, чтобы ответить."))))
                .append(tH.color(symbolColor + messageDivider))
                .append(messageComponent);

        if (playerUuid != null) {
            pMM.addPair(playerUuid, targetUuid);
            pMM.addPair(targetUuid, playerUuid);
        }

        sender.spigot().sendMessage(toSender.create());
        target.spigot().sendMessage(toTarget.create());

        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(tH.color(String.format("%sИгрок &r%s%s%s %sотправил сообщение", symbolColor, playerPrefix, playerColor, senderName, symbolColor))).create());
        target.getWorld().playSound(target, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.8f, 1.2f);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
