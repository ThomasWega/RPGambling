package me.wega.rpgambling;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatConsumer<T> implements Listener {
    private final Plugin plugin;
    private final Map<UUID, Pair<Function<String, T>, Consumer<T>>> inputConsumers = new HashMap<>();
    private final Map<UUID, Consumer<String>> unparsedConsumers = new HashMap<>();

    private final Map<UUID, Runnable> cancelConsumers = new HashMap<>();

    public ChatConsumer(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ChatConsumer<T> onInput(Player player, Function<String, T> parser, Consumer<T> callback) {
        inputConsumers.put(player.getUniqueId(), Pair.of(parser, callback));
        return this;
    }

    public ChatConsumer<T> onCancel(Player player, Runnable callback) {
        cancelConsumers.put(player.getUniqueId(), callback);
        return this;
    }

    public ChatConsumer<T> onUnparsable(Player player, Consumer<String> callback) {
        unparsedConsumers.put(player.getUniqueId(), callback);
        return this;
    }

    @EventHandler()
    private void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!inputConsumers.containsKey(uuid) && !cancelConsumers.containsKey(uuid) && !unparsedConsumers.containsKey(uuid)) return;

        event.setCancelled(true);
        String msg = event.getMessage();

        if (msg.equals("cancel") && cancelConsumers.containsKey(uuid)) {
            cancelConsumers.get(uuid).run();
            cancelConsumers.remove(uuid);
            inputConsumers.remove(uuid);
            unparsedConsumers.remove(uuid);
            return;
        }

        // Execute the callback with the parsed value
        Pair<Function<String, T>, Consumer<T>> pair = inputConsumers.get(uuid);
        T parsedValue = pair.getLeft().apply(msg);

        if (parsedValue == null) {
            if (unparsedConsumers.containsKey(uuid)) {
                unparsedConsumers.get(uuid).accept(msg);
                return;
            }
            return;
        }

        pair.getRight().accept(parsedValue);
        inputConsumers.remove(uuid);
        cancelConsumers.remove(uuid);
        unparsedConsumers.remove(uuid);
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
