package me.wega.rpgambling;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatCallback<T> implements Listener {
    private final Plugin plugin;
    private static final Map<UUID, ChatCallback<?>> runningChatConsumers = new HashMap<>();
    private final Function<String, T> parser;
    private Consumer<T> inputConsumer;
    private Consumer<String> unparsedConsumer;
    private Runnable cancelConsumer;
    private final Player player;

    public ChatCallback(Plugin plugin, Player player, Function<String, T> parser) {
        this.plugin = plugin;
        this.player = player;
        this.parser = parser;
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

    public ChatCallback<T> onUnparsable(Consumer<String> callback) {
        unparsedConsumer = callback;
        return this;
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!player.equals(event.getPlayer())) return;
        cancel();
    }

    // event is cancelled because of some plugin
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
        T parsedValue = parser.apply(msg);

        if (parsedValue == null) {
            if (unparsedConsumer != null) {
                unparsedConsumer.accept(msg);
                return;
            }
            return;
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
    public static class Parser {
        public static final Function<String, String> STRING = input -> input;
        public static final Function<String, Integer> INT = input -> {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };
        public static final Function<String, Double> DOUBLE = input -> {
            try {
                return Double.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };
    }
}
