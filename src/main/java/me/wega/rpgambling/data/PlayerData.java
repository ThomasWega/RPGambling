package me.wega.rpgambling.data;

import me.wega.rpgambling.RPGambling;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private static final Map<Player, PlayerData> data = new HashMap<>();
    private static final NamespacedKey chipsKey = new NamespacedKey(RPGambling.getInstance(), "chips");
    private static final NamespacedKey casinoTimeKey = new NamespacedKey(RPGambling.getInstance(), "time_spent_in_casino");

    private final PersistentDataContainer container;

    public static PlayerData get(Player player) {
        PlayerData playerData = data.get(player);
        if (playerData == null) {
            playerData = new PlayerData(player);
            data.put(player, playerData);
        }
        return playerData;
    }

    private PlayerData(Player player) {
        container = player.getPersistentDataContainer();
    }

    public int getChips() {
        return container.getOrDefault(chipsKey, PersistentDataType.INTEGER, 0);
    }

    public void setChips(int chips) {
        container.set(chipsKey, PersistentDataType.INTEGER, chips);
    }

    public boolean withdrawChips(int amount) {
        int chips = getChips();
        if (chips < amount) {
            return false;
        }
        setChips(chips - amount);
        return true;
    }

    public void depositChips(int amount) {
        setChips(getChips() + amount);
    }

    public int getTimeSpentInCasino() {
        return container.getOrDefault(casinoTimeKey, PersistentDataType.INTEGER, 0);
    }

    public void setTimeSpentInCasino(int timeSpentInCasino) {
        container.set(casinoTimeKey, PersistentDataType.INTEGER, timeSpentInCasino);
    }

    public void addTimeSpentInCasino(int timeSpentInCasino) {
        setTimeSpentInCasino(getTimeSpentInCasino() + timeSpentInCasino);
    }
}
