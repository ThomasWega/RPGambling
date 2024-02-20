package me.wega.rpgambling.config;

import me.wega.rpgambling.RPGambling;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigManager {
    public Map<String, String> messages = new HashMap<>();

    public void load() {
        RPGambling plugin = RPGambling.getInstance();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection messagesSection = Objects.requireNonNull(config.getConfigurationSection("Messages"));
        messagesSection.getValues(false).forEach((key, value) -> {
            try {
                messages.put(key, value.toString());
            } catch (ClassCastException e) {
                plugin.getLogger().severe("Message must be text");
            }
        });

        // TODO load all other configuration data
    }
}
