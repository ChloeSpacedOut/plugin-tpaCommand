package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.StoredRequests;
import com.choespacedout.tpaCommand.TeleportRequest;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TpaResolve {
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isDenied) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ctx -> {
                    final Player commandSender = (Player) ctx.getSource().getSender();
                    final UUID commandSenderID = commandSender.getUniqueId();
                    HashMap<UUID, TeleportRequest> requests = StoredRequests.requests;
                    List<TeleportRequest> sentRequests = new ArrayList<>();

                    for (int i = 0; i < requests.size();i++) {
                        TeleportRequest request = (TeleportRequest) requests.values().toArray()[i];
                        if (request.targetID == commandSenderID) {
                            sentRequests.add(request);
                        }
                    }

                    if (sentRequests.size() == 0) {
                        final TextComponent textComponent = Component.text("You have no teleport requests").color(NamedTextColor.RED);
                        commandSender.sendMessage(textComponent);
                    } else if (sentRequests.size() == 1) {
                        TeleportRequest request = sentRequests.getFirst();
                        if (isDenied) {
                            request.deny();
                        } else {
                            request.accept();
                        }
                        StoredRequests.remove(request.id);

                    } else {
                        final TextComponent textComponent = Component.text("You have multiple teleport requests! Please specify which request to accept").color(NamedTextColor.RED);
                        commandSender.sendMessage(textComponent);
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("sender", ArgumentTypes.player())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("sender", PlayerSelectorArgumentResolver.class);
                            final Player targetPlayer = playerSelector.resolve(ctx.getSource()).getFirst();
                            final UUID targetPlayerID = targetPlayer.getUniqueId();
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            HashMap<UUID, TeleportRequest> requests = StoredRequests.requests;

                            for (int i = 0; i < requests.size();i++) {
                                TeleportRequest request = (TeleportRequest) requests.values().toArray()[i];
                                if (request.senderID == targetPlayerID) {
                                    if (isDenied) {
                                        request.deny();
                                    } else {
                                        request.accept();
                                    }
                                    StoredRequests.remove(request.id);
                                    return Command.SINGLE_SUCCESS;
                                }
                            }

                            final TextComponent textComponent = Component.text("That player has no active teleport request with you").color(NamedTextColor.RED);
                            commandSender.sendMessage(textComponent);
                            return Command.SINGLE_SUCCESS;
                        })

                )
                .build();
    }
}
