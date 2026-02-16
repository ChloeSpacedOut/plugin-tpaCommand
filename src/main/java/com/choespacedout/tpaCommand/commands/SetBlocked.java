package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.Main;
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

public class SetBlocked {
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isUnblocking, Main pluginInstance) {
        return Commands.literal(commandName)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            final Player targetPlayer = playerSelector.resolve(ctx.getSource()).getFirst();
                            final String targetPlayerId = targetPlayer.getUniqueId().toString();
                            final Player commandSender = (Player) ctx.getSource().getSender();

                            if (targetPlayer.equals(commandSender)) {
                                final TextComponent textComponent = Component.text("You can not block teleport request from yourself").color(NamedTextColor.RED);
                                commandSender.sendMessage(textComponent);
                                return Command.SINGLE_SUCCESS;
                            }

                            final NamespacedKey key = new NamespacedKey(pluginInstance, "com.chloespacedout.tpaCommand.block." + targetPlayerId);
                            PersistentDataContainer blockedPlayer = commandSender.getPersistentDataContainer();
                            final boolean isBlocked = Boolean.TRUE.equals(blockedPlayer.get(key, PersistentDataType.BOOLEAN));


                            if (isUnblocking) {
                                if (isBlocked) {
                                    blockedPlayer.set(key,PersistentDataType.BOOLEAN,Boolean.FALSE);
                                    final TextComponent textComponent = Component.text("Unblocked " + targetPlayer.getName() + " from teleport requests").color(NamedTextColor.GRAY);
                                    commandSender.sendMessage(textComponent);
                                } else {
                                    final TextComponent textComponent = Component.text(targetPlayer.getName() + " is not blocked from teleport requests").color(NamedTextColor.RED);
                                    commandSender.sendMessage(textComponent);
                                }


                            } else {
                                if (isBlocked) {
                                    final TextComponent textComponent = Component.text(targetPlayer.getName() + " is already blocked from teleport requests").color(NamedTextColor.RED);
                                    commandSender.sendMessage(textComponent);
                                } else {
                                    blockedPlayer.set(key,PersistentDataType.BOOLEAN,Boolean.TRUE);
                                    final TextComponent textComponent = Component.text("Blocked " + targetPlayer.getName() + " from teleport requests").color(NamedTextColor.GRAY);
                                    commandSender.sendMessage(textComponent);
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
