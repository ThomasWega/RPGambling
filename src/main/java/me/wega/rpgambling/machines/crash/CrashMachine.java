package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import lombok.Setter;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
@Setter
public class CrashMachine extends GamblingMachine {
    private boolean isCountingDown;

    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }

    public CrashMachine(Location location) {
        super(location);
    }
}
