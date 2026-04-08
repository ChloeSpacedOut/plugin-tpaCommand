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

public class SetAllowing {

    private static void setAllowing(Player commandSender, String allowingRequests, File playerConfigs, PlayerConfigsCache playerConfigsCache) {

        final UUID commandSenderID = commandSender.getUniqueId();
        YamlConfiguration modifyPlayerConfigs = YamlConfiguration.loadConfiguration(playerConfigs);

        PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
        playerConfig.setAllowingRequests(allowingRequests);
        playerConfigsCache.setPlayerConfig(commandSenderID,playerConfig);

        modifyPlayerConfigs.set(commandSenderID + ".allowingRequests",allowingRequests);

        try {
            modifyPlayerConfigs.save(playerConfigs);
        } catch (IOException e) {
            commandSender.sendRichMessage("<red>Error saving to player configs file! Please contact a staff member and report this error!");;
            return;
        }

        if (allowingRequests.equals("ALL")) {
            commandSender.sendRichMessage("<grey>Allowing teleport requests from everyone");
        } else if ((allowingRequests.equals("WHITELISTED"))) {
            commandSender.sendRichMessage("<grey>Allowing teleport requests from whitelisted players");
        } else {
            commandSender.sendRichMessage("<grey>Allowing teleport requests from nobody");
        }
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .then(Commands.literal("all")
                        .executes(ctx -> {
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            setAllowing(commandSender,"ALL",playerConfigs,playerConfigsCache);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("-i")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    final UUID commandSenderID = commandSender.getUniqueId();

                                    setAllowing(commandSender,"ALL",playerConfigs,playerConfigsCache);

                                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                    commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("whitelisted")
                        .executes(ctx -> {
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            setAllowing(commandSender,"WHITELISTED",playerConfigs,playerConfigsCache);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("-i")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    final UUID commandSenderID = commandSender.getUniqueId();

                                    setAllowing(commandSender,"WHITELISTED",playerConfigs,playerConfigsCache);

                                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                    commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("none")
                        .executes(ctx -> {
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            setAllowing(commandSender,"NONE",playerConfigs,playerConfigsCache);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("-i")
                                .executes(ctx -> {
                                    final Player commandSender = (Player) ctx.getSource().getSender();
                                    final UUID commandSenderID = commandSender.getUniqueId();

                                    setAllowing(commandSender,"NONE",playerConfigs,playerConfigsCache);

                                    PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                                    commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                                    return Command.SINGLE_SUCCESS;
                                })))

                .build();
    }
}
