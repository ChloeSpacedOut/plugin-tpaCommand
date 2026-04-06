package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.StoredRequests;
import com.choespacedout.tpaCommand.TeleportRequest;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cancel {
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, StoredRequests storedRequests) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("tpa.use"))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            final Player targetPlayer = playerSelector.resolve(ctx.getSource()).getFirst();
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            final UUID targetPlayerID = targetPlayer.getUniqueId();
                            final UUID commandSenderID = commandSender.getUniqueId();

                            HashMap<UUID, TeleportRequest> requests = storedRequests.requests;

                            for (int i = 0; i < requests.size();i++) {
                                TeleportRequest request = (TeleportRequest) requests.values().toArray()[i];
                                if (request.senderID == commandSenderID && request.targetID == targetPlayerID) {
                                    commandSender.sendRichMessage("<gray>Cancelled teleport request to " + targetPlayer.getName());
                                    targetPlayer.sendRichMessage("<gray>Teleport request from " + commandSender.getName() + " has been cancelled");
                                    storedRequests.remove(request.id);
                                    return Command.SINGLE_SUCCESS;
                                }
                            }
                            commandSender.sendRichMessage("<red>You did not have any teleport requests to " + targetPlayer.getName());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}
