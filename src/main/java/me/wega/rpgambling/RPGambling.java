package me.wega.rpgambling;

import lombok.Getter;
import me.wega.rpgambling.commands.GamblingCommand;
import me.wega.rpgambling.config.ConfigManager;
import me.wega.rpgambling.handlers.WorldGuardHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RPGambling extends JavaPlugin {
    @Getter
    private WorldGuardHandler worldGuard;
    @Getter
    private Economy vault;
    @Getter
    private ConfigManager configManager;
    @Getter
    private static RPGambling instance;

    @Override
    public void onLoad() {
        instance = this;
        worldGuard = new WorldGuardHandler();
    }

    @Override
    public void onEnable() {
        vault = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        configManager = new ConfigManager();
        configManager.load();
        registerCommands();
    }

    private void registerCommands() {
        new GamblingCommand();
    }
}
