package me.wega.rpgambling.machines.slot;

import lombok.Getter;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
public class SlotMachine extends GamblingMachine {

    public SlotMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }

    public SlotMachine(Location location) {
        super(location);
    }
}
