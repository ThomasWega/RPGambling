package me.wega.rpgambling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ChatCallback<T> implements Listener {
    private final Plugin plugin;
    private static final Map<UUID, ChatCallback<?>> runningChatConsumers = new HashMap<>();
    private final AbstractParser<T>[] parsers;
    private Consumer<T> inputConsumer;
    private BiConsumer<AbstractParser<T>, String> unparsedConsumer;
    private Runnable cancelConsumer;
    private final Player player;

    public ChatCallback(Plugin plugin, Player player, AbstractParser<T>... parsers) {
        this.plugin = plugin;
        this.player = player;
        this.parsers = parsers;
        runningChatConsumers.put(player.getUniqueId(), this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ChatCallback<T> onInput(Consumer<T> callback) {
        inputConsumer = callback;
        return this;
    }

    public ChatCallback<T> onCancel(Runnable callback) {
        cancelConsumer = callback;
        return this;
    }

    public ChatCallback<T> onUnparsable(BiConsumer<AbstractParser<T>, String> callback) {
        unparsedConsumer = callback;
        return this;
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!player.equals(event.getPlayer())) return;
        cancel();
    }

    @EventHandler
    private void onPlayerChat(PlayerChatEvent event) {
        if (inputConsumer == null && cancelConsumer == null && unparsedConsumer == null) return;

        event.setCancelled(true);
        String msg = event.getMessage();

        if (msg.equals("cancel") && cancelConsumer != null) {
            cancelConsumer.run();
            cancel();
            return;
        }

        // Execute the callback with the parsed value
        T parsedValue = null;
        for (AbstractParser<T> parser : parsers) {
            parsedValue = parser.parse(msg);

            if (parsedValue == null) {
                if (unparsedConsumer != null) {
                    unparsedConsumer.accept(parser, msg);
                    return;
                }
                return;
            }
        }

        if (inputConsumer != null)
            inputConsumer.accept(parsedValue);
        cancel();
    }

    private void cancel() {
        inputConsumer = null;
        cancelConsumer = null;
        unparsedConsumer = null;
        runningChatConsumers.remove(player.getUniqueId());
    }

    @Getter
    public static abstract class AbstractParser<T> {
        public abstract T parse(String input);
    }

    public static class StringParser extends AbstractParser<String> {
        @Override
        public String parse(String input) {
            return input;
        }
    }

    public static class IntParser extends AbstractParser<Integer> {
        @Override
        public Integer parse(String input) {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class DoubleParser extends AbstractParser<Double> {
        @Override
        public Double parse(String input) {
            try {
                return Double.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class MinDoubleParser extends AbstractParser<Double> {
        private final double min;

        @Override
        public Double parse(String input) {
            try {
                double dNum = Double.parseDouble(input);
                if (dNum < min)
                    return null;
                return dNum;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
