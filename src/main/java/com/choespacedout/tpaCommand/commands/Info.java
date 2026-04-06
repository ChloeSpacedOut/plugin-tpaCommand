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
    public static TextComponent getInfoMessage(PlayerConfig playerConfig) {

        boolean isDenyingRequests = playerConfig.getDenyingRequests();

        TextComponent message = Component.text("--------------- [ TPA INFO ] ---------------")
                .color(NamedTextColor.GRAY);

        if (isDenyingRequests) {
            message = message.append(Component.text("\nTeleport requests currently disabled")
                    .color(NamedTextColor.RED)
                    .append(Component.text(" [ENABLE]")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD,true)
                            .clickEvent(ClickEvent.runCommand("/tpenable" + " -i"))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Enables teleport requests\n").color(NamedTextColor.GRAY).append(
                                            Component.text("/tpenable" + " -i").color(NamedTextColor.DARK_GRAY))
                            ))));

        } else {
            message = message.append(Component.text("\nTeleport requests currently enabled")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(" [DISABLE]")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.BOLD,true)
                            .clickEvent(ClickEvent.runCommand("/tpdisable" + " -i"))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Disables teleport requests\n").color(NamedTextColor.GRAY).append(
                                            Component.text("/tpdisable" + " -i").color(NamedTextColor.DARK_GRAY))
                            ))));
        }

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

        message = message.append(Component.text("\n-----------------------------------------")
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
