package me.arahis.detchat;

import me.arahis.detchat.commands.CoreChatCommand;
import me.arahis.detchat.commands.PMCommand;
import me.arahis.detchat.commands.ReplyCommand;
import me.arahis.detchat.commands.rp.CoreRPCommand;
import me.arahis.detchat.commands.subcommands.ReloadCommand;
import me.arahis.detchat.listeners.ChatListener;
import me.arahis.detchat.listeners.JoinQuitListener;
import me.arahis.detchat.managers.PMManager;
import me.arahis.detchat.utils.ChatMessageBuilder;
import me.arahis.detchat.utils.MentionHandler;
import me.arahis.detchat.utils.TextHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DETChatPlugin extends JavaPlugin {

    private static DETChatPlugin plugin;
    private TextHandler textHandler;
    private ChatMessageBuilder chatMessageBuilder;
    private PMManager pmManager;
    private MentionHandler mentionHandler;
    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        plugin = this;
        textHandler = new TextHandler();
        chatMessageBuilder = new ChatMessageBuilder();
        pmManager = new PMManager();
        mentionHandler = new MentionHandler();

        CoreChatCommand coreChatCommand = new CoreChatCommand(plugin);
        coreChatCommand.addSubCommand(new ReloadCommand());

        getCommand("chat").setExecutor(coreChatCommand);
        getCommand("chat").setTabCompleter(coreChatCommand);

        getCommand("me").setExecutor(new CoreRPCommand());

        PMCommand pmCommand = new PMCommand();
        getCommand("pm").setExecutor(pmCommand);
        getCommand("pm").setTabCompleter(pmCommand);

        ReplyCommand replyCommand = new ReplyCommand();
        getCommand("reply").setExecutor(replyCommand);
        getCommand("reply").setTabCompleter(replyCommand);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);

    }

    public MentionHandler getMentionHandler() {
        return mentionHandler;
    }
    public ChatMessageBuilder getChatMessageBuilder() {
        return chatMessageBuilder;
    }

    public TextHandler getTextHandler() {
        return textHandler;
    }

    public static DETChatPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {



    }

    public PMManager getPmManager() {
        return pmManager;
    }
}
