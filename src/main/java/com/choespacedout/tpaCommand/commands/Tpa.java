package com.choespacedout.tpaCommand.commands;

import com.choespacedout.tpaCommand.PlayerConfig;
import com.choespacedout.tpaCommand.PlayerConfigsCache;
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

public class Tpa {

    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName, boolean isTpaHere, PlayerConfigsCache playerConfigsCache, StoredRequests storedRequests) {
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

                            PlayerConfig targetConfig = playerConfigsCache.getPlayerConfig(targetPlayerID);

                            String allowingRequests = targetConfig.getAllowingRequests();

                            if (allowingRequests == null) {
                                allowingRequests = "ALL";
                            }

                            List<String> blockedPlayers = targetConfig.getBlockedPlayers();

                            if (blockedPlayers == null) {
                                blockedPlayers = new ArrayList<>();
                            }

                            final boolean isBlocked = blockedPlayers.contains(commandSenderID.toString());

                            List<String> whitelistedPlayers = targetConfig.getWhitelistedPlayers();

                            if (whitelistedPlayers == null) {
                                whitelistedPlayers = new ArrayList<>();
                            }

                            final boolean isWhitelisted = whitelistedPlayers.contains(commandSenderID.toString());

                            boolean isDenyingRequests = allowingRequests.equals("NONE") || (allowingRequests.equals("WHITELISTED") && !isWhitelisted);

                            if ((isBlocked || isDenyingRequests) || (isWhitelisted && isBlocked)) {
                                commandSender.sendRichMessage("<red>This player is not accepting teleport requests");
                                return Command.SINGLE_SUCCESS;
                            }


                            String tpaAutoAcceptMode;
                            if (!isTpaHere) {
                                tpaAutoAcceptMode = targetConfig.getAutoTpaAccepting();
                            } else {
                                tpaAutoAcceptMode = targetConfig.getAutoTpaHereAccepting();
                            }

                            if (tpaAutoAcceptMode == null) {
                                tpaAutoAcceptMode = "NONE";
                            }

                            boolean isAutoAccept = tpaAutoAcceptMode.equals("ALL") || (tpaAutoAcceptMode.equals("WHITELISTED") && isWhitelisted);

                            UUID id = UUID.randomUUID();
                            TeleportRequest teleportRequest = new TeleportRequest(id, commandSender,targetPlayer,isTpaHere,isAutoAccept);

                            if (isAutoAccept) {
                                teleportRequest.accept();
                                return Command.SINGLE_SUCCESS;
                            }

                            storedRequests.add(id,teleportRequest);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
