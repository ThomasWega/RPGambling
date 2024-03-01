package me.wega.rpgambling.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHandler extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "RPGambling";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wega";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
}
