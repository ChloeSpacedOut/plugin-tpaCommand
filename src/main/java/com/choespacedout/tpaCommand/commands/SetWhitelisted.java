package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.PlayerConfig;
import com.choespacedout.tpaCommand.PlayerConfigsCache;
import com.choespacedout.tpaCommand.arguments.WhitelistedPlayerArgument;
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

public class SetWhitelisted {

    private static void setWhitelisted(Player commandSender, OfflinePlayer targetPlayer, boolean isUnwhitelisting, File playerConfigs, PlayerConfigsCache playerConfigsCache) {

        final String targetPlayerID = targetPlayer.getUniqueId().toString();

        if (targetPlayer.equals(commandSender)) {
            final TextComponent textComponent = Component.text("You can not whitelist teleport requests for yourself").color(NamedTextColor.RED);
            commandSender.sendMessage(textComponent);
            return;
        }

        UUID playerID = commandSender.getUniqueId();

        PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(playerID);

        List<String> whitelistedPlayers = playerConfig.getWhitelistedPlayers();

        if (whitelistedPlayers == null) {
            whitelistedPlayers = new ArrayList<>();
        }

        final boolean isWhitelisted = whitelistedPlayers.contains(targetPlayerID);

        YamlConfiguration modifyPlayerConfigs = YamlConfiguration.loadConfiguration(playerConfigs);

        if (isUnwhitelisting) {
            if (isWhitelisted) {
                final TextComponent textComponent = Component.text("Unwhitelisted " + targetPlayer.getName() + " from teleport requests").color(NamedTextColor.GRAY);
                commandSender.sendMessage(textComponent);
                whitelistedPlayers.remove(targetPlayerID);

                playerConfig.setWhitelistedPlayers(whitelistedPlayers);
                playerConfigsCache.setPlayerConfig(playerID,playerConfig);

                modifyPlayerConfigs.set(playerID + ".whitelistedPlayers",whitelistedPlayers);
            } else {
                final TextComponent textComponent = Component.text(targetPlayer.getName() + " is not whitelisted for teleport requests").color(NamedTextColor.RED);
                commandSender.sendMessage(textComponent);
            }

        } else {
            if (isWhitelisted) {
                final TextComponent textComponent = Component.text(targetPlayer.getName() + " is already whitelisted for teleport requests").color(NamedTextColor.RED);
                commandSender.sendMessage(textComponent);
            } else {
                final TextComponent textComponent = Component.text("Whitelisted " + targetPlayer.getName() + " for teleport requests").color(NamedTextColor.GRAY);
                commandSender.sendMessage(textComponent);
                whitelistedPlayers.add(targetPlayerID);

                playerConfig.setWhitelistedPlayers(whitelistedPlayers);
                playerConfigsCache.setPlayerConfig(playerID,playerConfig);

                modifyPlayerConfigs.set(playerID + ".whitelistedPlayers",whitelistedPlayers);
            }
        }

        try {
            modifyPlayerConfigs.save(playerConfigs);
        } catch (IOException e) {
            commandSender.sendRichMessage("<red>Error saving to player configs file! Please contact a staff member and report this error!");
        }
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isUnwhitelisting, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .then(Commands.argument("target", new WhitelistedPlayerArgument(playerConfigsCache, isUnwhitelisting))
                        .executes(ctx -> {
                            final String targetPlayerName = StringArgumentType.getString(ctx,"target");
                            final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
                            final Player commandSender = (Player) ctx.getSource().getSender();

                            setWhitelisted(commandSender,targetPlayer,isUnwhitelisting,playerConfigs,playerConfigsCache);

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("-i")
                                .executes(ctx -> {
                                    final String targetPlayerName = StringArgumentType.getString(ctx,"target");
                                    final OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    final UUID commandSenderID = (commandSender.getUniqueId());

                                    setWhitelisted(commandSender,targetPlayer,isUnwhitelisting,playerConfigs,playerConfigsCache);

                                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                    commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();
    }
}
