package me.arahis.detchat.listeners;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final DETChatPlugin plugin;
    private final TextHandler tH;
    private final ChatMessageBuilder cMB;

    public JoinQuitListener() {
        plugin = DETChatPlugin.getPlugin();
        tH = plugin.getTextHandler();
        cMB = plugin.getChatMessageBuilder();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){

        event.setJoinMessage("");

        Player player = event.getPlayer();
        String joinMessage = Config.getString("chat.join-message");
        String firstJoinMessage = Config.getString("chat.first-join-message");

        String playerPrefixes = Config.getString("chat.prefix");
        playerPrefixes = tH.color(PlaceholderAPI.setPlaceholders(player, playerPrefixes));

        BaseComponent[] playerComponent = cMB.buildPlayerComponent(player);
        BaseComponent[] messageComponent = player.hasPlayedBefore() ? cMB.buildMessageComponent(joinMessage) : cMB.buildMessageComponent(firstJoinMessage);

        ComponentBuilder builder = new ComponentBuilder();

        builder.append(playerPrefixes).append(playerComponent).append(" ").reset().append(messageComponent);

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(builder.create());
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        event.setQuitMessage("");

        Player player = event.getPlayer();
        String quitMessage = Config.getString("chat.quit-message");

        String playerPrefixes = Config.getString("chat.prefix");
        playerPrefixes = tH.color(PlaceholderAPI.setPlaceholders(player, playerPrefixes));

        BaseComponent[] playerComponent = cMB.buildPlayerComponent(player);
        BaseComponent[] messageComponent = cMB.buildMessageComponent(quitMessage);

        ComponentBuilder builder = new ComponentBuilder();

        builder.append(playerPrefixes).append(playerComponent).append(" ").reset().append(messageComponent);

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(builder.create());
        }
    }

}
