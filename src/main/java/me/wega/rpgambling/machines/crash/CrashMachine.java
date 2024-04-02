package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import lombok.Setter;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public class CrashMachine extends GamblingMachine {
    private final Random random = new Random();
    private final double countdownTimeSec = 10d;
    private final Set<Player> countdownPlayers = new HashSet<>();
    // TODO roundup!
    private double crashAmount = 0;
    private int startCountdown = 0;
    private boolean crashRunning = false;
    private @Nullable BukkitTask countdownTask;
    private final Map<Player, Double> stopAmounts = new HashMap<>();
    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }
    public CrashMachine(Location location) {
        super(location);
    }

    public double getStopAmount(Player player) {
        return stopAmounts.get(player);
    }

    public boolean hasStopped(Player player) {
        return stopAmounts.containsKey(player);
    }

    public void handleCountdown(Player player) {
        countdownPlayers.add(player);
        if (countdownTask != null) return;

        CrashMachine machine = this;
        this.countdownTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (getBetsCount() <= 0) {
                    cancel();
                    return;
                }
                if (startCountdown < countdownTimeSec) {
                    countdownPlayers.forEach(p -> p.sendMessage("Game is starting in " + (countdownTimeSec - startCountdown) + "s"));
                    startCountdown++;
                    return;
                }

                countdownPlayers.forEach(p -> {
                    p.sendMessage("STARTING GAME");
                    new CrashMachineGameMenu(machine, p).show(p);
                });
                cancel();
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                startCountdown = 0;
                countdownPlayers.clear();
                countdownTask = null;
                super.cancel();
            }
        }.runTaskTimer(RPGambling.getInstance(), 40L, 20L);
    }

    private final Set<Consumer<Double>> crashCallbacks = new HashSet<>();

    public boolean stopCrash(Player player) {
        if (!isCrashRunning())  return false;

        stopAmounts.put(player, crashAmount);
        return true;
    }

    public void startCrash(Consumer<Double> crashCallback) {
        crashCallbacks.add(crashCallback);
        if (crashRunning) return;

        crashRunning = true;
        new BukkitRunnable() {
            final double divider = random.nextDouble(0.4, 1);
            final double num = random.nextDouble();
            final double multiplier = num / divider;

            @Override
            public void run() {
                if (crashAmount >= multiplier) {
                    crashCallbacks.forEach(callback -> callback.accept(crashAmount));
                    stopAmounts.clear();
                    cancel();
                }
                crashAmount += 0.01;
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                crashRunning = false;
                crashAmount = 0;
                crashCallbacks.clear();
                super.cancel();
            }
        }.runTaskTimer(RPGambling.getInstance(), 2L, 2L);
    }
}
