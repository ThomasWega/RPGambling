package me.wega.rpgambling.data;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private static final Map<OfflinePlayer, PlayerData> data = new HashMap<>();

    private OfflinePlayer player;
    @Getter
    private int chips;
    @Getter
    private long timeSpentInCasino;

    public static PlayerData get(OfflinePlayer player) {
        PlayerData playerData = data.get(player);
        return playerData != null ? playerData : new PlayerData(player);
    }

    private PlayerData(OfflinePlayer player) {
        this.player = player;
        timeSpentInCasino = 0;
        chips = 0;
    }
}
