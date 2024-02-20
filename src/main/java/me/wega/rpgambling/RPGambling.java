package me.wega.rpgambling;

import lombok.Getter;
import me.wega.rpgambling.commands.GamblingCommand;
import me.wega.rpgambling.handlers.WorldGuardHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RPGambling extends JavaPlugin {
    @Getter
    private WorldGuardHandler worldGuard;
    @Getter
    private Economy vault;
    @Getter
    private static RPGambling instance;

    @Override
    public void onEnable() {
        instance = this;
        worldGuard = new WorldGuardHandler();
        vault = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        PluginCommand command = Objects.requireNonNull(getCommand("gambling"));
        GamblingCommand gamblingCommand = new GamblingCommand();
        command.setExecutor(gamblingCommand);
        command.setTabCompleter(gamblingCommand);
    }
}
