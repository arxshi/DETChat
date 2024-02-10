package me.arahis.detchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MentionHandler {

    public Set<Player> getPlayerListFromMessage(String message) {

        Set<Player> players = new HashSet<>();

        for(Player p : Bukkit.getOnlinePlayers()) {
            for (String s : message.split(" ")) {
                if(s.equals(p.getName())) {
                    players.add(p);
                }
            }
        }

        return players;
    }

}
