package me.wega.rpgambling.machines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        bets.put(player.getUniqueId(), amount);
    }

    public double getBet(Player player) {
        return bets.getOrDefault(player.getUniqueId(), 0d);
    }

    public double getBetOrDefault(Player player) {
        return bets.getOrDefault(player.getUniqueId(), 100d);
    }

    public void removeBet(Player player) {
        bets.remove(player.getUniqueId());
    }

    public int getBetsCount() {
        return bets.size();
    }

    public void refreshBets() {
        bets.clear();
    }

    public double getTotalBetsAmount() {
        return bets.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public Set<Player> getBetters() {
        return bets.keySet().stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toSet());
    }
}
