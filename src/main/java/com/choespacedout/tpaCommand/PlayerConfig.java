package com.choespacedout.tpaCommand;

import java.util.List;

public class PlayerConfig {
    private List<String> blockedPlayers;
    private List<String> whitelistedPlayers;
    private String allowingRequests;
    private String autoTpaAccepting;
    private String autoTpaHereAccepting;

    public PlayerConfig(List<String> newBlockedPlayers, List<String> newWhitelistedPlayers, String newAllowingRequests, String newAutoTpaAccepting, String newAutoTpaHereAccepting) {
        blockedPlayers = newBlockedPlayers;
        whitelistedPlayers = newWhitelistedPlayers;
        allowingRequests = newAllowingRequests;
        autoTpaAccepting = newAutoTpaAccepting;
        autoTpaHereAccepting = newAutoTpaHereAccepting;
    }

    public List<String> getBlockedPlayers() {
        return blockedPlayers;
    }

    public void setBlockedPlayers(List<String> newBlockedPlayers) {
        blockedPlayers = newBlockedPlayers;
    }

    public List<String> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public void setWhitelistedPlayers(List<String> newWhitelistedPlayers) {
        whitelistedPlayers = newWhitelistedPlayers;
    }

    public String getAllowingRequests() {
        return allowingRequests;
    }

    public void setAllowingRequests(String newAllowingRequests) {
        allowingRequests = newAllowingRequests;
    }

    public String getAutoTpaAccepting() {
        return autoTpaAccepting;
    }

    public void setAutoTpaAccepting(String newAutoTpaAccept) {
        autoTpaAccepting = newAutoTpaAccept;
    }

    public String getAutoTpaHereAccepting() {
        return autoTpaHereAccepting;
    }

    public void setAutoTpaHereAccepting(String newAutoTpaHereAccept) {
        autoTpaHereAccepting = newAutoTpaHereAccept;
    }


}
