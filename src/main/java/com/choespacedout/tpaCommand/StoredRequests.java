package com.choespacedout.tpaCommand;

import java.util.HashMap;
import java.util.UUID;

public class StoredRequests {
    public HashMap<UUID,TeleportRequest> requests = new HashMap<>();

    public void add(UUID id, TeleportRequest teleportRequest) {
        try {
            requests.remove(id);
            requests.put(id,teleportRequest);
        } catch(Exception e) {
            requests.put(id,teleportRequest);
        }
    }

    public void remove(UUID id) {
        try {
            requests.remove(id);
        } catch (Exception ignored) {}
    }
}
