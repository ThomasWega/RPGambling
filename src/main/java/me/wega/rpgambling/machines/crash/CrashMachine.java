package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import lombok.Setter;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
@Setter
public class CrashMachine extends GamblingMachine {
    private boolean isCountingDown;
    // TODO roundup!
    private double crashAmount = 0;

    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }

    public CrashMachine(Location location) {
        super(location);
    }


    public void handleCountdown(Player player) {
        if (getBetsCount() <= 0 || isCountingDown()) return;
        setCountingDown(true);


        CrashMachine machine = this;
        new BukkitRunnable() {
            final double countdownTimeSec = 10d;
            int i = 0;
            @Override
            public void run() {
                if (getBetsCount() <= 0) cancel();
                if (i < countdownTimeSec) {
                    player.sendMessage("Game is starting in " + (countdownTimeSec - i) + "s");
                    i++;
                    return;
                }

                cancel();
                player.sendMessage("STARTING GAME");
                new CrashMachineGameMenu(machine, player).show(player);
            }
        }.runTaskTimer(RPGambling.getInstance(), 20L, 20L);
    }

    public void startCrash() {
        new BukkitRunnable() {
            @Override
            public void run() {
                crashAmount += 0.01;
            }
        }.runTaskTimer(RPGambling.getInstance(), 2L, 2L);
    }
}
