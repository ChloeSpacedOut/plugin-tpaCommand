package com.choespacedout.tpaCommand;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class PlayerConfigsCache {
    private HashMap<UUID,PlayerConfig> playerConfigsCache;

    private HashMap<UUID,PlayerConfig> cacheFromFile(File playerConfigsFile) {
        HashMap<UUID,PlayerConfig> newPlayerConfigsCache = new HashMap<>();

        YamlConfiguration modifyConfigFile = YamlConfiguration.loadConfiguration(playerConfigsFile);
        Set<String> players;
        try {
            players = modifyConfigFile.getKeys(false);
        } catch (Exception e) {
            players = new HashSet<>(Arrays.asList());
        }

        List<String> playerList = players.stream().toList();

        for (int i = 0; i < players.size(); i++) {
            UUID playerID = UUID.fromString(playerList.get(i));
            List<String> blockedPlayers = modifyConfigFile.getStringList(playerID + ".blockedPlayers");
            boolean denyingRequests = modifyConfigFile.getBoolean(playerID + ".denyingRequests");
            PlayerConfig playerConfig = new PlayerConfig(blockedPlayers,denyingRequests);
            newPlayerConfigsCache.put(playerID,playerConfig);
        }
        return newPlayerConfigsCache;
    }

    public PlayerConfigsCache(File playerConfigsFile) {
        playerConfigsCache = cacheFromFile(playerConfigsFile);

    }

    public PlayerConfig getPlayerConfig(UUID playerID) {
        final PlayerConfig playerConfig = playerConfigsCache.get(playerID);
        if (playerConfig == null) {
            return new PlayerConfig(new ArrayList<>(),false);
        } else {
            return playerConfigsCache.get(playerID);
        }
    }

    public void setPlayerConfig(UUID playerID, PlayerConfig playerConfig) {
        playerConfigsCache.put(playerID,playerConfig);
    }
}