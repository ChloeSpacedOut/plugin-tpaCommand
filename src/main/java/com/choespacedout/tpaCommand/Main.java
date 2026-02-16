package com.choespacedout.tpaCommand;

import com.choespacedout.tpaCommand.commands.SetBlocked;
import com.choespacedout.tpaCommand.commands.Tpa;
import com.choespacedout.tpaCommand.commands.TpaResolve;
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

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this, this);
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Tpa.createCommand("tpa", false,this), "Creates a teleport request");
            commands.registrar().register(Tpa.createCommand("tpahere", true,this), "Creates a teleport request to your position");
            commands.registrar().register(TpaResolve.createCommand("tpaccept", false), "Accepts a teleport request");
            commands.registrar().register(TpaResolve.createCommand("tpdeny", true), "Denies a teleport request");
            commands.registrar().register(SetBlocked.createCommand("tpblock", false,this), "Blocks a player from sending teleport requests");
            commands.registrar().register(SetBlocked.createCommand("tpunblock", true,this), "Unblocks a player from sending teleport requests");
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void serverTickStart(ServerTickStartEvent e) {
        Long sysTime = System.currentTimeMillis();
        final HashMap<UUID, TeleportRequest> requests = StoredRequests.requests;
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
                StoredRequests.remove(id);
            }
        }
    }
}
