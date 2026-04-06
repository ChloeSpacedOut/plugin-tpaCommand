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

public class Toggle {

    private static void toggle(Player commandSender, boolean denyingRequests, File playerConfigs, PlayerConfigsCache playerConfigsCache) {

        final UUID commandSenderID = commandSender.getUniqueId();
        YamlConfiguration modifyPlayerConfigs = YamlConfiguration.loadConfiguration(playerConfigs);

        PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
        playerConfig.setDenyingRequests(denyingRequests);
        playerConfigsCache.setPlayerConfig(commandSenderID,playerConfig);

        modifyPlayerConfigs.set(commandSenderID + ".denyingRequests",denyingRequests);

        if (denyingRequests) {
            commandSender.sendRichMessage("<grey>Teleport requests are now disabled");
        } else {
            commandSender.sendRichMessage("<grey>Teleport requests are now enabled");
        }


        try {
            modifyPlayerConfigs.save(playerConfigs);
        } catch (IOException e) {
            commandSender.sendRichMessage("<red>Error saving to player configs file! Please contact a staff member and report this error!");;
        }
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean denyingRequests, File playerConfigs, PlayerConfigsCache playerConfigsCache) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ctx -> {
                    final Player commandSender = (Player) ctx.getSource().getSender();
                    toggle(commandSender,denyingRequests,playerConfigs,playerConfigsCache);
                    return Command.SINGLE_SUCCESS;

                })
                .then(Commands.literal("-i")
                        .executes(ctx -> {
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            final UUID commandSenderID = commandSender.getUniqueId();

                            toggle(commandSender,denyingRequests,playerConfigs,playerConfigsCache);

                            PlayerConfig playerConfig = playerConfigsCache.getPlayerConfig(commandSenderID);
                            commandSender.sendMessage(Info.getInfoMessage(playerConfig));

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
