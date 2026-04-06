package com.choespacedout.tpaCommand;

import com.choespacedout.tpaCommand.commands.*;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    StoredRequests storedRequests;
    File playerConfigs;


    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this, this);
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        storedRequests = new StoredRequests();
        playerConfigs = new File(getDataFolder(),"playerConfigs.yml");

        if (!playerConfigs.exists()) {
            try {
                playerConfigs.createNewFile();
            } catch (IOException exception) {
                System.out.println("Could not load warps file! TpaCommand could not finish loading!");
                return;
            }
        }
        PlayerConfigsCache playerConfigsCache = new PlayerConfigsCache(playerConfigs);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Tpa.createCommand("tpa", false,playerConfigsCache,storedRequests), "Creates a teleport request");
            commands.registrar().register(Tpa.createCommand("tpahere", true,playerConfigsCache,storedRequests), "Creates a teleport request to your position");
            commands.registrar().register(Cancel.createCommand("tpacancel",storedRequests), "Cancels a teleport request");
            commands.registrar().register(TpaResolve.createCommand("tpaccept", false,storedRequests), "Accepts a teleport request");
            commands.registrar().register(TpaResolve.createCommand("tpdeny", true,storedRequests), "Denies a teleport request");
            commands.registrar().register(SetBlocked.createCommand("tpblock", false,playerConfigs,playerConfigsCache), "Blocks a player from sending teleport requests");
            commands.registrar().register(SetBlocked.createCommand("tpunblock", true,playerConfigs,playerConfigsCache), "Unblocks a player from sending teleport requests");
            commands.registrar().register(Toggle.createCommand("tpenable", false,playerConfigs,playerConfigsCache), "Toggles on the ability to receive teleport requests");
            commands.registrar().register(Toggle.createCommand("tpdisable", true,playerConfigs,playerConfigsCache), "Toggles off the ability to receive teleport requests");
            commands.registrar().register(Info.createCommand("tpinfo",playerConfigsCache), "Provides info about your current teleport request settings");
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void serverTickStart(ServerTickStartEvent e) {

        final HashMap<UUID, TeleportRequest> requests = storedRequests.requests;

        for (int i = 0; i < requests.size();i++) {
            UUID id = (UUID) requests.keySet().toArray()[i];
            TeleportRequest request = (TeleportRequest) requests.values().toArray()[i];
            int timeout = this.getConfig().getInt("TpaTimeout");
            if ((request.sendTime + timeout) < System.currentTimeMillis()) {
                final Player sender = Bukkit.getPlayer(request.senderID);
                final Player target = Bukkit.getPlayer(request.targetID);
                if (sender != null) {
                    final TextComponent textComponent = Component.text("Your teleport request to " + request.targetName + " has expired").color(NamedTextColor.GRAY);
                    sender.sendMessage(textComponent);
                }
                if (target != null) {
                    final TextComponent textComponent = Component.text("Your teleport request from " + request.senderName + " has expired").color(NamedTextColor.GRAY);
                    target.sendMessage(textComponent);
                }
                storedRequests.remove(id);
            }
        }
    }
}
