package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
public class CrashMachine extends GamblingMachine {

    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }

    public CrashMachine(Location location) {
        super(location);
    }
}
