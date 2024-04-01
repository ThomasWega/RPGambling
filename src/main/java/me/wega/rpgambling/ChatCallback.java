package me.wega.rpgambling;

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
import java.util.function.Predicate;

public class ChatCallback<T> implements Listener {
    private static final Map<UUID, ChatCallback<?>> runningChatConsumers = new HashMap<>();
    private final Parser<T> parser;
    private Consumer<T> onSuccess;
    private Consumer<Player> onCancel;
    private Consumer<String> onFail;
    private Predicate<T> onCondition;
    private final Player player;

    public ChatCallback(Plugin plugin, Player player, Parser<T> parser) {
        this.player = player;
        this.parser = parser;
        ChatCallback<?> previousCallback = runningChatConsumers.put(player.getUniqueId(), this);
        if (previousCallback != null)
            previousCallback.cancel();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ChatCallback<T> onSuccess(Consumer<T> callback) {
        onSuccess = callback;
        return this;
    }

    public ChatCallback<T> onCancel(Consumer<Player> callback) {
        onCancel = callback;
        return this;
    }

    public ChatCallback<T> onFail(Consumer<String> callback) {
        onFail = callback;
        return this;
    }

    public ChatCallback<T> onCondition(Predicate<T> condition) {
        onCondition = condition;
        return this;
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!player.equals(event.getPlayer())) return;
        cancel();
    }

    @EventHandler
    private void onPlayerChat(PlayerChatEvent event) {
        if (onSuccess == null && onCancel == null) return;

        event.setCancelled(true);
        String msg = event.getMessage();

        if (msg.equalsIgnoreCase("cancel") && onCancel != null) {
            onCancel.accept(player);
            cancel();
            return;
        }

        T parsedValue = parser.parse(msg);
        if (parsedValue == null) {
            onFail.accept(msg);
            return;
        }

        if (!onCondition.test(parsedValue)) return;

        if (onSuccess != null) {
            onSuccess.accept(parsedValue);
            cancel();
        }
    }

    public void cancel() {
        onSuccess = null;
        onCancel = null;
        runningChatConsumers.remove(player.getUniqueId());
    }

    public interface Parser<T> {
        T parse(String input);
    }

    public static class Parsers {
        public static final Parser<String> STRING = input -> input;

        public static final Parser<Integer> INTEGER = input -> {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };

        public static final Parser<Double> DOUBLE = input -> {
            try {
                return Double.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };
    }
}
