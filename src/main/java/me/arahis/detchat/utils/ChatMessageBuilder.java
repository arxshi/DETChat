package me.arahis.detchat.utils;

import me.arahis.detchat.DETChatPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public class ChatMessageBuilder {

    private final DETChatPlugin plugin;
    private final TextHandler tH;

    public ChatMessageBuilder() {
        plugin = DETChatPlugin.getPlugin();
        tH = plugin.getTextHandler();
    }

    public BaseComponent[] whenHoveredPlayerComponent(Player player) {
        ComponentBuilder builder = new ComponentBuilder();

        String playerName = Config.getString("chat.player-name-in-hover");
        playerName = tH.color(playerName);

        builder.append(PlaceholderAPI.setPlaceholders(player, playerName))
                .append("\n\n")
                .append(tH.color("&7Наиграно: " + PlaceholderAPI.setPlaceholders(player, "%playtime_time_hours%") + " ч."))
                .append("\n\n")
                .append(tH.color("&aНажмите, чтобы написать."));

        return builder.create();
    }

    public BaseComponent[] buildPlayerComponent(Player player) {
        ComponentBuilder builder = new ComponentBuilder();

        String playerName = PlaceholderAPI.setPlaceholders(player, Config.getString("chat.player-name-in-chat"));

        playerName = tH.color(playerName);

        builder.append(playerName)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, whenHoveredPlayerComponent(player)))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + player.getName() + " "));

        return builder.create();
    }

    public BaseComponent[] whenHoveredMessageComponent() {
        ComponentBuilder builder = new ComponentBuilder();
        Date date = new Date();

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);

        builder.append(tH.color("&7Отправлено &a" + time));

        return builder.create();
    }

    public BaseComponent[] buildMessageComponent(String message) {
        ComponentBuilder builder = new ComponentBuilder();

        for(String s : message.split(" ")) {
            if(isUrl(s)) {
                builder.append(tH.color("&9&n" + s))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Нажмите, чтобы перейти по ссылке")))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, s))
                        .append(" ")
                        .reset();
            } else {
                builder.reset().append(s).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, whenHoveredMessageComponent())).append(" ");
            }
        }



        return new ComponentBuilder().append(builder.create(), ComponentBuilder.FormatRetention.ALL).event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)).create();
    }

    private boolean isUrl(String s) {
        try {
            new URL(s);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
