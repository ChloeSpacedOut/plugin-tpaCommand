package com.choespacedout.tpaCommand;

import java.util.List;

public class PlayerConfig {
    private List<String> blockedPlayers;
    private boolean denyingRequests;

    public PlayerConfig(List<String> newBlockedPlayers, boolean newDenyingRequests) {
        blockedPlayers = newBlockedPlayers;
        denyingRequests = newDenyingRequests;
    }

    public List<String> getBlockedPlayers() {
        return blockedPlayers;
    }

    public void setBlockedPlayers(List<String> newBlockedPlayers) {
        blockedPlayers = newBlockedPlayers;
    }

    public boolean getDenyingRequests() {
        return denyingRequests;
    }

    public void setDenyingRequests(boolean newDenyingRequests) {
        denyingRequests = newDenyingRequests;
    }


}
