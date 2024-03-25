package me.wega.rpgambling.machines;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
@Getter
public abstract class GamblingMachine {
    private final Location location;
}
