package me.arahis.detchat.utils;

import me.arahis.detchat.DETChatPlugin;

public class Config {
    public static String getString(String path) {
        return DETChatPlugin.getPlugin().getConfig().getString(path);
    }

    public static Integer getInt(String path) {
        return DETChatPlugin.getPlugin().getConfig().getInt(path);
    }
}
