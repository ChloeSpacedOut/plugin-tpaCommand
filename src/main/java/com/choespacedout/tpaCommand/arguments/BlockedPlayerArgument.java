package com.choespacedout.tpaCommand.arguments;

import com.choespacedout.tpaCommand.PlayerConfigsCache;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class BlockedPlayerArgument implements CustomArgumentType<String,String> {

    PlayerConfigsCache playerConfigCache;
    boolean isUnblocking;
    public BlockedPlayerArgument(PlayerConfigsCache newPlayerConfigCache, boolean newIsUnblocking) {
        playerConfigCache = newPlayerConfigCache;
        isUnblocking = newIsUnblocking;
    }

    private static final DynamicCommandExceptionType ERROR_NO_PLAYER = new DynamicCommandExceptionType(name -> {
        return MessageComponentSerializer.message().serialize(Component.text("No player was found"));
    });

    private static final DynamicCommandExceptionType ERROR_NO_BLOCKED_PLAYER = new DynamicCommandExceptionType(name -> {
        return MessageComponentSerializer.message().serialize(Component.text("No blocked player was found"));
    });

    private static final SimpleCommandExceptionType ERROR_BAD_SOURCE = new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(Component.text("The source needs to be a CommandSourceStack")));


    @Override
    public String parse(StringReader stringReader) {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> String parse(StringReader reader, S source) throws CommandSyntaxException {

        if (isUnblocking) {
            if (!(source instanceof CommandSourceStack stack)) {
                throw ERROR_BAD_SOURCE.create();
            }

            UUID commandSenderID = ((CommandSourceStack) source).getExecutor().getUniqueId();
            final List<String> blockedPlayers = playerConfigCache.getPlayerConfig(commandSenderID).getBlockedPlayers();
            final String blockedPlayerName = getNativeType().parse(reader);
            OfflinePlayer blockedPlayer = Bukkit.getOfflinePlayer(blockedPlayerName);
            UUID blockedPlayerUUID = blockedPlayer.getUniqueId();

            if (!blockedPlayers.contains(blockedPlayerUUID.toString())) {
                throw ERROR_NO_BLOCKED_PLAYER.create(blockedPlayerName);
            }

            return blockedPlayerName;
        } else {
            final String playerName = getNativeType().parse(reader);

            List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
            if (!players.contains(Bukkit.getPlayer(playerName))) {
                throw ERROR_NO_PLAYER.create(playerName);
            }

            return playerName;
        }
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> ctx, final SuggestionsBuilder builder) {

        if (isUnblocking) {
            if (!(ctx.getSource() instanceof CommandSourceStack stack)) {
                return builder.buildFuture();
            }

            UUID commandSenderID = stack.getExecutor().getUniqueId();

            final List<String> blockedPlayers = playerConfigCache.getPlayerConfig(commandSenderID).getBlockedPlayers();

            for (int i = 0; i < blockedPlayers.size(); i++) {
                UUID playerID = UUID.fromString(blockedPlayers.get(i));
                String playerName = Bukkit.getOfflinePlayer(playerID).getName();

                if (playerName.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(playerName);
                }
            }

            return builder.buildFuture();
        } else {
            List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();

            for (int i = 0; i < players.size(); i++) {
                String playerName = players.get(i).getName();
                if (playerName.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(playerName);
                }
            }
            return builder.buildFuture();
        }
    }
}