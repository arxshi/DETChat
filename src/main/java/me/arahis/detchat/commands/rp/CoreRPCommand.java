package me.arahis.detchat.commands.rp;

import me.arahis.detchat.DETChatPlugin;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.Config;
import me.arahis.detchat.utils.TextHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CoreRPCommand implements CommandExecutor, TabCompleter {

    private final DETChatPlugin plugin;
    private final TextHandler tH;
    private final ChatMessageBuilder cMB;

    public CoreRPCommand() {
        plugin = DETChatPlugin.getPlugin();
        tH = plugin.getTextHandler();
        cMB = plugin.getChatMessageBuilder();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.only-for-players"));
            return true;
        }

        if(args.length < 1) {
            tH.sendErrorWithPrefix(sender, Config.getString("messages.empty-message"));
            return true;
        }

        Player player = (Player) sender;

        String message = tH.buildStringFromArray(0, args);
        String format = Config.getString("chat.format.global");
        String playerPrefix = PlaceholderAPI.setPlaceholders(player, Config.getString("chat.prefix"));

        boolean isGlobal = label.startsWith("g");
        boolean isColored = player.hasPermission("detchat.coloredchat");

        int localDistance = Config.getInt("chat.local-distance");

        if(isColored) {
            message = tH.color(message);
        }

        List<Player> recipients;
        if(isGlobal) {
            recipients = new ArrayList<>(Bukkit.getOnlinePlayers());
        } else {
            recipients = Bukkit.getOnlinePlayers().stream().filter(p -> {
                if(p.getWorld().equals(player.getWorld())) {
                    return p.getLocation().distanceSquared(player.getLocation()) < Math.pow(localDistance, 2);
                }
                return false;
            }).collect(Collectors.toList());
            format = Config.getString("chat.format.local");
        }

        format = tH.color(format);
        playerPrefix = tH.color(playerPrefix);

        ComponentBuilder builder = new ComponentBuilder();
        builder.append(format);

        BaseComponent[] messageComponent = cMB.buildMessageComponent(message);
        BaseComponent[] playerComponent = cMB.buildPlayerComponent(player);

        if(label.endsWith("me")) {
            builder.append("* ")
                    .append(playerPrefix)
                    .append(playerComponent)
                    .append(" ")
                    .reset()
                    .append(messageComponent)
                    .italic(true)
                    .append("*")
                    .reset();
        }

        if(label.endsWith("do")) {
            builder.append("* ")
                    .append(messageComponent)
                    .italic(true)
                    .append("*")
                    .reset()
                    .append(" (")
                    .append(playerPrefix)
                    .append(playerComponent)
                    .append(")")
                    .reset();
        }

        if(label.endsWith("try")) {
            builder.append(playerPrefix)
                    .append(playerComponent)
                    .append(" ")
                    .reset()
                    .append(messageComponent)
                    .reset()
                    .append(getResultFromTry());

        }

        for (Player p : recipients) {
            p.spigot().sendMessage(builder.create());
        }

        if(recipients.isEmpty() || recipients.size() == 1) {
            tH.sendMessage(player, tH.color("&cВас никто не услышал!"));
        }

        return true;
    }

    private String getResultFromTry() {

        List<String> options = Arrays.asList(tH.color("&f[&a✔&f]&r"), tH.color("&f[&c✘&f]&r"));
        Random random = new Random();

        return options.get(random.nextInt(2));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
