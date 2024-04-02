package me.wega.rpgambling.machines.crash;

import lombok.Getter;
import lombok.Setter;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.machines.GamblingMachine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public class CrashMachine extends GamblingMachine {
    private final Map<Player, BukkitTask> runningCountdowns = new HashMap<>();
    private final Random random = new Random();
    private boolean isCountingDown;
    // TODO roundup!
    private double crashAmount = 0;
    private int startCountdown = 0;
    private boolean crashStarted = false;

    public CrashMachine(Location location, LinkedHashMap<UUID, Double> bets) {
        super(location, bets);
    }

    public CrashMachine(Location location) {
        super(location);
    }

    public void handleCountdown(Player player) {
        if (runningCountdowns.containsKey(player)) return;
        if (getBetsCount() <= 0) return;

        CrashMachine machine = this;
        runningCountdowns.put(player, new BukkitRunnable() {
                    final double countdownTimeSec = 10d;

                    @Override
                    public void run() {
                        if (getBetsCount() <= 0) cancel();
                        if (!hasBet(player)) cancel();
                        if (startCountdown < countdownTimeSec) {
                            player.sendMessage("Game is starting in " + (countdownTimeSec - startCountdown) + "s");
                            startCountdown++;
                            return;
                        }

                        cancel();
                        player.sendMessage("STARTING GAME");
                        new CrashMachineGameMenu(machine, player).show(player);
                    }

                    @Override
                    public synchronized void cancel() throws IllegalStateException {
                        startCountdown = 0;
                        runningCountdowns.remove(player);
                        super.cancel();
                    }
                }.runTaskTimer(RPGambling.getInstance(), 40L, 20L)
        );
    }

    public void startCrash(Consumer<Double> crashCallback) {
        if (crashStarted) return;

        crashStarted = true;
        new BukkitRunnable() {
            final double divider = random.nextDouble(0.4, 1);
            final double num = random.nextDouble();
            final double multiplier = num / divider;

            @Override
            public void run() {
                if (crashAmount >= multiplier) {
                    crashCallback.accept(crashAmount);
                    cancel();
                }
                crashAmount += 0.01;
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                crashStarted = false;
                super.cancel();
            }
        }.runTaskTimer(RPGambling.getInstance(), 2L, 2L);
    }
}
