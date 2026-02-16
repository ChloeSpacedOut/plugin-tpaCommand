package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.Main;
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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class Tpa {

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isTpaHere, Main pluginInstance) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("tpa.use"))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            final Player targetPlayer = playerSelector.resolve(ctx.getSource()).getFirst();
                            final Player commandSender = (Player) ctx.getSource().getSender();
                            final UUID targetPlayerID = targetPlayer.getUniqueId();
                            final UUID commandSenderID = commandSender.getUniqueId();

                            HashMap<UUID, TeleportRequest> requests = StoredRequests.requests;
                            for (int i = 0; i < requests.size();i++) {
                                TeleportRequest request = (TeleportRequest) requests.values().toArray()[i];
                                if (request.senderID == commandSenderID && request.targetID == targetPlayerID) {
                                    final TextComponent textComponent = Component.text("You have already sent " + targetPlayer.getName() + " a teleport request").color(NamedTextColor.RED);
                                    commandSender.sendMessage(textComponent);
                                    return Command.SINGLE_SUCCESS;
                                }
                            }

                            if (targetPlayer.equals(commandSender)) {
                                final TextComponent textComponent = Component.text("You can not send a teleport request to yourself").color(NamedTextColor.RED);
                                commandSender.sendMessage(textComponent);
                                return Command.SINGLE_SUCCESS;
                            }

                            final NamespacedKey key = new NamespacedKey(pluginInstance, "com.chloespacedout.tpaCommand.block." + commandSenderID.toString());
                            final PersistentDataContainer blockedPlayer = targetPlayer.getPersistentDataContainer();
                            final boolean isBlocked = Boolean.TRUE.equals(blockedPlayer.get(key, PersistentDataType.BOOLEAN));

                            if (isBlocked) {
                                final TextComponent textComponent = Component.text("This player has you blocked from teleport requests").color(NamedTextColor.RED);
                                commandSender.sendMessage(textComponent);
                                return Command.SINGLE_SUCCESS;
                            }


                            UUID id = UUID.randomUUID();
                            TeleportRequest teleportRequest = new TeleportRequest(id, commandSender,targetPlayer,isTpaHere);
                            StoredRequests.add(id,teleportRequest);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
