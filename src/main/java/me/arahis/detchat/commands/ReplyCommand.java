package me.arahis.detchat.commands;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.managers.PMManager;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor, TabCompleter {

    private final DETChatPlugin plugin;
    private final TextHandler tH;
    private final ChatMessageBuilder cMB;
    private final PMManager pMM;

    public ReplyCommand() {

        plugin = DETChatPlugin.getPlugin();
        tH = plugin.getTextHandler();
        cMB = plugin.getChatMessageBuilder();
        pMM = plugin.getPmManager();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length < 1) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.empty-messages"));
            return true;
        }

        if (!(sender instanceof Player)) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.only-for-player"));
            return true;
        }

        Player target = (Player) sender;
        UUID targetUuid = target.getUniqueId();
        Player player = Bukkit.getPlayer(pMM.getPlayerUuid(targetUuid));

        if(player == null) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.dont-have-conversation"));
            return true;
        }

        String message = tH.buildStringFromArray(0, args);

        String generalPlayerPrefix = Config.getString("chat.prefix");
        String playerPrefix = PlaceholderAPI.setPlaceholders(player, generalPlayerPrefix);
        String targetPrefix = PlaceholderAPI.setPlaceholders(target, generalPlayerPrefix);
        playerPrefix = tH.color(playerPrefix);
        targetPrefix = tH.color(targetPrefix);

        String playerDivider = Config.getString("chat.pm.player-divider");
        String messageDivider = Config.getString("chat.pm.message-divider");

        String playerColor = Config.getString("chat.pm.player-name-color");
        String symbolColor = Config.getString("chat.pm.symbol-color");

        boolean isColored = target.hasPermission("detchat.coloredchat");

        if (isColored) {
            message = tH.color(message);
        }

        BaseComponent[] messageComponent = cMB.buildMessageComponent(message);

        ComponentBuilder toSender = new ComponentBuilder();
        toSender.append(tH.color(playerColor + "Вы"))
                .append(tH.color(symbolColor + playerDivider))
                .append(tH.color(playerPrefix + playerColor + player.getName()))
                .append(tH.color(symbolColor + messageDivider))
                .append(messageComponent);

        ComponentBuilder toTarget = new ComponentBuilder();
        toTarget.append(tH.color(targetPrefix + playerColor + target.getName()))
                .append(tH.color(symbolColor + playerDivider))
                .append(tH.color(playerColor + "Вы"))
                .append(tH.color(symbolColor + messageDivider))
                .append(messageComponent);

        target.spigot().sendMessage(toSender.create());
        player.spigot().sendMessage(toTarget.create());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(tH.color(String.format("%sИгрок &r%s%s%s %sотправил сообщение", symbolColor, targetPrefix, playerColor, target.getName(), symbolColor))).create());
        player.getWorld().playSound(player, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.8f, 1.2f);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return new ArrayList<>();
    }
}
