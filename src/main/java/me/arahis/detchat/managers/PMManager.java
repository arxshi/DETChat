package me.arahis.detchat.managers;

import java.util.HashMap;
import java.util.UUID;

public class PMManager {

    HashMap<UUID, UUID> pair = new HashMap<>();

    public void addPair(UUID playerUuid, UUID targetUuid) {
        pair.put(targetUuid, playerUuid);
    }

    public void removePair(UUID targetUuid) {
        pair.remove(targetUuid);
    }

    public UUID getPlayerUuid(UUID targetUuid) {
        return pair.get(targetUuid);
    }


}
