package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.PlayerConfig;
import com.choespacedout.tpaCommand.PlayerConfigsCache;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Info {

    private static TextComponent autoAcceptMessage(String tpaMode, String mode) {
        if (mode.equals("ALL")) {
            return Component.text("\n[SWITCH]")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD,true)
                    .clickEvent(ClickEvent.runCommand("/tpautoaccept " + tpaMode + " whitelisted -i"))
                    .append(Component.text(" Auto-accepting " + tpaMode + " requests from everyone")
                            .decoration(TextDecoration.BOLD,false)
                            .color(NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("")));

        } else if (mode.equals("WHITELISTED")) {
            return Component.text("\n[SWITCH]")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD,true)
                    .clickEvent(ClickEvent.runCommand("/tpautoaccept " + tpaMode + " none -i"))
                    .append(Component.text(" Auto-accepting " + tpaMode + " requests from whitelisted")
                            .decoration(TextDecoration.BOLD,false)
                            .color(NamedTextColor.YELLOW)
                            .clickEvent(ClickEvent.runCommand("")));
        } else {
            return Component.text("\n[SWITCH]")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD,true)
                    .clickEvent(ClickEvent.runCommand("/tpautoaccept " + tpaMode + " all -i"))
                    .append(Component.text(" Auto-accepting " + tpaMode + " requests from nobody")
                            .decoration(TextDecoration.BOLD,false)
                            .color(NamedTextColor.RED)
                            .clickEvent(ClickEvent.runCommand("")));
        }
    }

    public static TextComponent getInfoMessage(PlayerConfig playerConfig) {

        String allowingRequests = playerConfig.getAllowingRequests();

        TextComponent message = Component.text("--------------------- [ TPA INFO ] ---------------------")
                .color(NamedTextColor.GRAY);

        if (allowingRequests.equals("ALL")) {
            message = message.append(Component.text("\n[SWITCH] ")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD,true)
                    .clickEvent(ClickEvent.runCommand("/tpallow whitelisted -i")))
                    .append(Component.text("Allowing teleport requests from everyone")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD,false));

        } else if (allowingRequests.equals("WHITELISTED")) {
            message = message.append(Component.text("\n[SWITCH] ")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD,true)
                    .clickEvent(ClickEvent.runCommand("/tpallow none -i")))
                    .append(Component.text("Allowing teleport requests from whitelisted players")
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.BOLD,false));
        } else {
            message = message.append(Component.text("\n[SWITCH] ")
                            .color(NamedTextColor.GOLD)
                            .decoration(TextDecoration.BOLD,true)
                            .clickEvent(ClickEvent.runCommand("/tpallow all -i")))
                    .append(Component.text("Allowing teleport requests from nobody")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.BOLD,false));
        }

        String autoTpaAccept = playerConfig.getAutoTpaAccepting();
        if (autoTpaAccept == null) {
            autoTpaAccept = "NONE";
        }
        message = message.append(autoAcceptMessage("tpa",autoTpaAccept));

        String autoTpaHereAccept = playerConfig.getAutoTpaHereAccepting();
        if (autoTpaHereAccept == null) {
            autoTpaHereAccept = "NONE";
        }
        message = message.append(autoAcceptMessage("tpahere",autoTpaHereAccept));

        message = message.append(Component.text("\n-----------------------------------------------------")
                .color(NamedTextColor.GRAY));

        List<String> blockedPlayers = playerConfig.getBlockedPlayers();

        message = message.append(Component.text("\nBlocked players:")
                .color(NamedTextColor.WHITE));

        for (int i = 0; i < blockedPlayers.size(); i++) {
            UUID blockedPlayer = UUID.fromString(blockedPlayers.get(i));
            String blockedPlayerName = Objects.requireNonNull(Bukkit.getOfflinePlayer(blockedPlayer).getName());
            message = message.append(Component.text("\n• " + blockedPlayerName)
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(" [UNBLOCK]")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD,true)
                            .clickEvent(ClickEvent.runCommand("/tpunblock " + blockedPlayerName + " -i"))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Unblocks this player\n").color(NamedTextColor.GRAY).append(
                                            Component.text("/tpunblock " + blockedPlayerName + " -i").color(NamedTextColor.DARK_GRAY))
                            ))));
        }

        List<String> whitelistedPlayers = playerConfig.getWhitelistedPlayers();

        message = message.append(Component.text("\nWhitelisted players:")
                .color(NamedTextColor.WHITE));

        for (int i = 0; i < whitelistedPlayers.size(); i++) {
            UUID whitelistedPlayer = UUID.fromString(whitelistedPlayers.get(i));
            String whitelistedPlayerName = Objects.requireNonNull(Bukkit.getOfflinePlayer(whitelistedPlayer).getName());
            message = message.append(Component.text("\n• " + whitelistedPlayerName)
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(" [UNWHITELIST]")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD,true)
                            .clickEvent(ClickEvent.runCommand("/tpunwhitelist " + whitelistedPlayerName + " -i"))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Unwhitelists this player\n").color(NamedTextColor.GRAY).append(
                                            Component.text("/tpunwhitelist " + whitelistedPlayerName + " -i").color(NamedTextColor.DARK_GRAY))
                            ))));
        }

        message = message.append(Component.text("\n-----------------------------------------------------")
                .color(NamedTextColor.GRAY));

        return message;
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ctx -> {
                    final Player commandSender = (Player) ctx.getSource().getSender();
                    final UUID commandSenderID = commandSender.getUniqueId();

                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                    commandSender.sendMessage(getInfoMessage(playerConfig));

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
