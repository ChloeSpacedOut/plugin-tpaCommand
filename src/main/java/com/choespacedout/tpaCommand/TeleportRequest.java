package com.choespacedout.tpaCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportRequest {

    public final Long sendTime = System.currentTimeMillis();

    public final UUID id;
    public final UUID senderID;
    public final UUID targetID;
    public final String senderName;
    public final String targetName;
    public final boolean shouldTpaHere;

    public TeleportRequest(UUID newID, Player sender, Player target, boolean newShouldTpaHere) {
        id = newID;
        senderID = sender.getUniqueId();
        targetID = target.getUniqueId();
        senderName = sender.getName();
        targetName = target.getName();
        shouldTpaHere = newShouldTpaHere;

        final TextComponent senderMessage = Component.text("Sent a teleport request to " + targetName).color(NamedTextColor.GREEN);
        sender.sendMessage(senderMessage);

        TextComponent targetMessage;
        if (shouldTpaHere) {
            targetMessage = Component.text(senderName + " has requested you teleport to them\n").color(NamedTextColor.GRAY);
        } else {
            targetMessage = Component.text(senderName + " has requested to teleport to you\n").color(NamedTextColor.GRAY);
        }

        target.sendMessage(targetMessage.append(Component.text("[ACCEPT] ")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.BOLD, true)
                .clickEvent(ClickEvent.runCommand("/tpaccept " + senderName))
                .hoverEvent(HoverEvent.showText(
                        Component.text("Accepts this request\n").color(NamedTextColor.GRAY).append(
                                Component.text("/tpaccept " + senderName).color(NamedTextColor.DARK_GRAY))
                        )

        )).append((Component.text("[DENY] ")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.BOLD, true)
                .clickEvent(ClickEvent.runCommand("/tpdeny " + senderName))
                .hoverEvent(HoverEvent.showText(
                        Component.text("Denies this request\n").color(NamedTextColor.GRAY).append(
                            Component.text("/tpdeny " + senderName).color(NamedTextColor.DARK_GRAY)))
                )

        )).append((Component.text("[BLOCK] ")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.BOLD, true)
                .clickEvent(ClickEvent.runCommand("/tpblock " + senderName))
                .hoverEvent(HoverEvent.showText(
                        Component.text("Blocks this user from sending more teleport requests\n").color(NamedTextColor.GRAY).append(
                                Component.text("/tpblock " + senderName).color(NamedTextColor.DARK_GRAY)))
                        )
                )

        ));

    }

    public void accept() {
        final Player sender = Bukkit.getPlayer(senderID);
        final Player target = Bukkit.getPlayer(targetID);
        if (sender == null) {
            final TextComponent textComponent = Component.text("Could not complete teleport request as " + senderName + " is no longer online").color(NamedTextColor.RED);
            target.sendMessage(textComponent);
            return;
        }

        if (shouldTpaHere) {
            target.teleport(sender.getLocation());
            final TextComponent targetMessage = Component.text("Teleported you to " + senderName).color(NamedTextColor.GREEN);
            target.sendMessage(targetMessage);
            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_TELEPORT,1.0F,1.0F);
            final TextComponent senderMessage = Component.text("Teleported " + targetName + " to you").color(NamedTextColor.GREEN);
            sender.sendMessage(senderMessage);
        } else {
            sender.teleport(target.getLocation());
            final TextComponent targetMessage = Component.text("Teleported " + senderName + " to you").color(NamedTextColor.GREEN);
            target.sendMessage(targetMessage);
            final TextComponent senderMessage = Component.text("Teleported you to " + targetName).color(NamedTextColor.GREEN);
            sender.sendMessage(senderMessage);
            sender.playSound(target.getLocation(), Sound.ENTITY_PLAYER_TELEPORT,1.0F,1.0F);
        }
    }

    public void deny() {
        final Player sender = Bukkit.getPlayer(senderID);
        final Player target = Bukkit.getPlayer(targetID);

        if (sender != null) {
            final TextComponent targetMessage = Component.text(targetName + " has denied your teleport request").color(NamedTextColor.GRAY);
            sender.sendMessage(targetMessage);
        }

        if (target != null) {
            final TextComponent targetMessage = Component.text("Denied teleport request from " + senderName).color(NamedTextColor.GRAY);
            target.sendMessage(targetMessage);
        }
    }




}
