package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.PlayerConfig;
import com.choespacedout.tpaCommand.PlayerConfigsCache;
import com.choespacedout.tpaCommand.arguments.BlockedPlayerArgument;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetBlocked {

    private static void setBlocked(Player commandSender, OfflinePlayer targetPlayer, boolean isUnblocking, File playerConfigs, PlayerConfigsCache playerConfigsCache) {

        final String targetPlayerID = targetPlayer.getUniqueId().toString();

        if (targetPlayer.equals(commandSender)) {
            final TextComponent textComponent = Component.text("You can not block teleport requests from yourself").color(NamedTextColor.RED);
            commandSender.sendMessage(textComponent);
            return;
        }

        UUID playerID = commandSender.getUniqueId();

        PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(playerID);

        List<String> blockedPlayers = playerConfig.getBlockedPlayers();

        if (blockedPlayers == null) {
            blockedPlayers = new ArrayList<>();
        }

        final boolean isBlocked = blockedPlayers.contains(targetPlayerID);

        YamlConfiguration modifyPlayerConfigs = YamlConfiguration.loadConfiguration(playerConfigs);

        if (isUnblocking) {
            if (isBlocked) {
                final TextComponent textComponent = Component.text("Unblocked " + targetPlayer.getName() + " from teleport requests").color(NamedTextColor.GRAY);
                commandSender.sendMessage(textComponent);
                blockedPlayers.remove(targetPlayerID);

                playerConfig.setBlockedPlayers(blockedPlayers);
                playerConfigsCache.setPlayerConfig(playerID,playerConfig);

                modifyPlayerConfigs.set(playerID + ".blockedPlayers",blockedPlayers);
            } else {
                final TextComponent textComponent = Component.text(targetPlayer.getName() + " is not blocked from teleport requests").color(NamedTextColor.RED);
                commandSender.sendMessage(textComponent);
            }

        } else {
            if (isBlocked) {
                final TextComponent textComponent = Component.text(targetPlayer.getName() + " is already blocked from teleport requests").color(NamedTextColor.RED);
                commandSender.sendMessage(textComponent);
            } else {
                final TextComponent textComponent = Component.text("Blocked " + targetPlayer.getName() + " from teleport requests").color(NamedTextColor.GRAY);
                commandSender.sendMessage(textComponent);
                blockedPlayers.add(targetPlayerID);

                playerConfig.setBlockedPlayers(blockedPlayers);
                playerConfigsCache.setPlayerConfig(playerID,playerConfig);

                modifyPlayerConfigs.set(playerID + ".blockedPlayers",blockedPlayers);
            }
        }

        try {
            modifyPlayerConfigs.save(playerConfigs);
        } catch (IOException e) {
            commandSender.sendRichMessage("<red>Error saving to player configs file! Please contact a staff member and report this error!");
        }
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isUnblocking, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .then(Commands.argument("target", new BlockedPlayerArgument(playerConfigsCache, isUnblocking))
                        .executes(ctx -> {
                            final String targetPlayerName = StringArgumentType.getString(ctx,"target");
                            final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
                            final Player commandSender = (Player) ctx.getSource().getSender();

                            setBlocked(commandSender,targetPlayer,isUnblocking,playerConfigs,playerConfigsCache);

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("-i")
                                .executes(ctx -> {
                                    final String targetPlayerName = StringArgumentType.getString(ctx,"target");
                                    final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    final UUID commandSenderID = (commandSender.getUniqueId());

                                    setBlocked(commandSender,targetPlayer,isUnblocking,playerConfigs,playerConfigsCache);

                                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                    commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();
    }
}
