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
public class WhitelistedPlayerArgument implements CustomArgumentType<String,String> {

    PlayerConfigsCache playerConfigCache;
    boolean isUnwhitelisting;
    public WhitelistedPlayerArgument(PlayerConfigsCache newPlayerConfigCache, boolean newIsUnwhitelisting) {
        playerConfigCache = newPlayerConfigCache;
        isUnwhitelisting = newIsUnwhitelisting;
    }

    private static final DynamicCommandExceptionType ERROR_NO_PLAYER = new DynamicCommandExceptionType(name -> {
        return MessageComponentSerializer.message().serialize(Component.text("No player was found"));
    });

    private static final DynamicCommandExceptionType ERROR_NO_WHITELISTED_PLAYER = new DynamicCommandExceptionType(name -> {
        return MessageComponentSerializer.message().serialize(Component.text("No whitelisted player was found"));
    });

    private static final SimpleCommandExceptionType ERROR_BAD_SOURCE = new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(Component.text("The source needs to be a CommandSourceStack")));


    @Override
    public String parse(StringReader stringReader) {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> String parse(StringReader reader, S source) throws CommandSyntaxException {

        if (isUnwhitelisting) {
            if (!(source instanceof CommandSourceStack stack)) {
                throw ERROR_BAD_SOURCE.create();
            }

            UUID commandSenderID = ((CommandSourceStack) source).getExecutor().getUniqueId();
            final List<String> whitelistedPlayers = playerConfigCache.getPlayerConfig(commandSenderID).getWhitelistedPlayers();
            final String whitelistedPlayerName = getNativeType().parse(reader);
            OfflinePlayer whitelistedPlayer = Bukkit.getOfflinePlayerIfCached(whitelistedPlayerName);
            UUID whitelistedPlayerUUID;

            try {
                whitelistedPlayerUUID = whitelistedPlayer.getUniqueId();
            } catch (Exception e) {
                throw ERROR_NO_WHITELISTED_PLAYER.create(whitelistedPlayerName);
            }

            if (!whitelistedPlayers.contains(whitelistedPlayerUUID.toString())) {
                throw ERROR_NO_WHITELISTED_PLAYER.create(whitelistedPlayerName);
            }

            return whitelistedPlayerName;
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

        if (isUnwhitelisting) {
            if (!(ctx.getSource() instanceof CommandSourceStack stack)) {
                return builder.buildFuture();
            }

            UUID commandSenderID = stack.getExecutor().getUniqueId();

            final List<String> whitelistedPlayers = playerConfigCache.getPlayerConfig(commandSenderID).getWhitelistedPlayers();

            for (int i = 0; i < whitelistedPlayers.size(); i++) {
                UUID playerID = UUID.fromString(whitelistedPlayers.get(i));
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