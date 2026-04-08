package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.PlayerConfig;
import com.choespacedout.tpaCommand.PlayerConfigsCache;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SetAutoAccept {
    private static void setAutoAccept(Player commandSender, boolean isTpaHere, String mode, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        final UUID commandSenderID = commandSender.getUniqueId();
        YamlConfiguration modifyPlayerConfigs = YamlConfiguration.loadConfiguration(playerConfigs);

        PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);

        if (!isTpaHere) {
            playerConfig.setAutoTpaAccepting(mode);
            modifyPlayerConfigs.set(commandSenderID + ".autoTpaAccepting",mode);
        } else {
            playerConfig.setAutoTpaHereAccepting(mode);
            modifyPlayerConfigs.set(commandSenderID + ".autoTpaHereAccepting",mode);
        }


        playerConfigsCache.setPlayerConfig(commandSenderID,playerConfig);

        try {
            modifyPlayerConfigs.save(playerConfigs);
        } catch (IOException e) {
            commandSender.sendRichMessage("<red>Error saving to player configs file! Please contact a staff member and report this error!");
            return;
        }

        if (!isTpaHere) {
            if (mode.equals("ALL")) {
                commandSender.sendRichMessage("<grey>Teleport requests will now be automatically accepted from everyone");
            } else if(mode.equals("WHITELISTED")) {
                commandSender.sendRichMessage("<grey>Teleport requests will now be automatically accepted from whitelisted players");
            } else {
                commandSender.sendRichMessage("<grey>Teleport requests will now be never automatically accepted");
            }
        } else {
            if (mode.equals("ALL")) {
                commandSender.sendRichMessage("<grey>Teleport here requests will now be automatically accepted from everyone");
            } else if(mode.equals("WHITELISTED")) {
                commandSender.sendRichMessage("<grey>Teleport here requests will now be automatically accepted from whitelisted players");
            } else {
                commandSender.sendRichMessage("<grey>Teleport here requests will now be never automatically accepted");
            }
        }

    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .then(Commands.literal("tpa")
                        .then(Commands.literal("all")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,false,"ALL",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,false,"ALL",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("whitelisted")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,false,"WHITELISTED",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,false,"WHITELISTED",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("none")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,false,"NONE",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,false,"NONE",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                )
                .then(Commands.literal("tpahere")
                        .then(Commands.literal("none")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,true,"NONE",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,true,"NONE",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("whitelisted")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,true,"WHITELISTED",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,true,"WHITELISTED",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("all")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    setAutoAccept(commandSender,true,"ALL",playerConfigs,playerConfigsCache);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.literal("-i")
                                        .executes(ctx -> {
                                            final Player commandSender = (Player) ctx.getSource().getSender();
                                            final UUID commandSenderID = commandSender.getUniqueId();
                                            setAutoAccept(commandSender,true,"ALL",playerConfigs,playerConfigsCache);

                                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                )

                .build();
    }
}
