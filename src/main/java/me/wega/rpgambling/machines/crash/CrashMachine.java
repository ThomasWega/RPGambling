package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
public class CrashMachine extends GamblingMachine {
    // TODO remove bet on quit
    private LinkedHashMap<UUID, Double> bets = new LinkedHashMap<>();

    public CrashMachine(Location location) {
        super(location);
    }

    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location);
        this.bets = bets;
    }

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

    public void removeBet(Player player) {
        // TODO add vault support
        bets.remove(player.getUniqueId());
    }
}
