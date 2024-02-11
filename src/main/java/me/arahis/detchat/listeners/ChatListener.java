package me.arahis.detchat.listeners;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatListener implements Listener {
    private final DETChatPlugin plugin;
    private final ChatMessageBuilder cMB;
    private final TextHandler tH;

    public ChatListener() {
        plugin = DETChatPlugin.getPlugin();
        cMB = plugin.getChatMessageBuilder();
        tH = plugin.getTextHandler();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncPlayerChatEvent event) {

        if(event.isCancelled()) return;

        event.getRecipients().clear();

        ComponentBuilder builder = new ComponentBuilder();

        DETChatPlugin plugin = DETChatPlugin.getPlugin();
        ChatMessageBuilder cMB = plugin.getChatMessageBuilder();
        TextHandler tH = plugin.getTextHandler();

        String message = event.getMessage();
        Player player = event.getPlayer();

        String format = Config.getString("chat.format.global");
        String playerPrefix = PlaceholderAPI.setPlaceholders(player, Config.getString("chat.prefix"));
        String divider = Config.getString("chat.divider");

        int localChatDistance = Config.getInt("chat.local-distance");

        boolean isGlobal = message.startsWith("!");
        boolean isColored = player.hasPermission("detchat.coloredchat");

        if (isColored) {
            message = tH.color(message);
        }

        List<Player> recipients;

        if (isGlobal) {
            recipients = new ArrayList<>(Bukkit.getOnlinePlayers());
            message = message.substring(1);
        } else {

            format = Config.getString("chat.format.local");
            event.setCancelled(true);

            recipients = Bukkit.getOnlinePlayers().stream().filter(p -> {
                if (p.getWorld().equals(player.getWorld())) {
                    return p.getLocation().distanceSquared(player.getLocation()) <= Math.pow(localChatDistance, 2);
                }
                return false;
            }).collect(Collectors.toList());
        }

        format = tH.color(format);
        playerPrefix = tH.color(playerPrefix);
        divider = tH.color(divider);

        event.setMessage(message);

        BaseComponent[] playerComponent = cMB.buildPlayerComponent(player);
        BaseComponent[] messageComponent = cMB.buildMessageComponent(message);
        BaseComponent[] toSend;

        Set<Player> mentionedPlayers = plugin.getMentionHandler().getPlayerListFromMessage(message);

        sendMessageForMentionedPlayers(isColored, message, mentionedPlayers, playerPrefix, format, divider, playerComponent);

        toSend = builder.append(format)
                .append(playerPrefix)
                .append(playerComponent)
                .append(divider)
                .reset()
                .append(messageComponent)
                .create();

        recipients.forEach(r -> {
            if (!mentionedPlayers.contains(r)) {
                r.spigot().sendMessage(toSend);
            }
        });

        if (recipients.isEmpty() || recipients.size() == 1) {
            tH.sendMessage(player, tH.color("&cВас никто не услышал!"));
        }
    }

    private void sendMessageForMentionedPlayers(boolean isColored, String message, Set<Player> mentionedPlayers, String playerPrefix, String format, String divider, BaseComponent[] playerComponent) {
        for (Player p : mentionedPlayers) {
            StringBuilder sb = new StringBuilder();
            for (String s : message.split(" ")) {
                if (s.equals(p.getName())) {
                    sb.append(tH.color("&6" + p.getName() + "&r")).append(" ");
                } else {
                    sb.append(s).append(" ");
                }
            }

            String messageForMentioned = sb.toString().trim();

            if (isColored) {
                messageForMentioned = tH.color(messageForMentioned);
            }

            p.getWorld().playSound(p, Sound.UI_BUTTON_CLICK, 1f, 1f);

            p.spigot().sendMessage(new ComponentBuilder().append(format)
                    .append(playerPrefix)
                    .append(playerComponent)
                    .append(divider)
                    .reset()
                    .append(cMB.buildMessageComponent(messageForMentioned))
                    .create());
        }
    }
}
