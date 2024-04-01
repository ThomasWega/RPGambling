package me.wega.rpgambling.machines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public abstract class GamblingMachine {
    private final Location location;
    private LinkedHashMap<UUID, Double> bets = new LinkedHashMap<>();

    public boolean hasBet(Player player) {
        return bets.containsKey(player.getUniqueId());
    }

    public void setBet(Player player, double amount) {
        // TODO add vault support
        bets.put(player.getUniqueId(), amount);
    }

    public double getBet(Player player) {
        return bets.getOrDefault(player.getUniqueId(), 0d);
    }

    public double getBetOrDefault(Player player) {
        return bets.getOrDefault(player.getUniqueId(), 100d);
    }

    public void removeBet(Player player) {
        // TODO add vault support
        bets.remove(player.getUniqueId());
    }
}
