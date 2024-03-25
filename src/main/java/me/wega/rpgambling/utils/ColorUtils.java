package me.wega.rpgambling.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the conversion of colors
 */
public final class ColorUtils {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    private ColorUtils() {
    }

    /**
     * Translates the colors of the given Component text
     * and returns the colored text. Supports both normal colors
     * and HEX colors.
     *
     * @param component Component text to translate colors on
     * @return Component text with translated colors
     */
    public static TextComponent color(@NotNull Component component) {
        return legacySerializer.deserialize(
                legacySerializer.serialize(component)
        );
    }

    /**
     * Translates the colors of the given String text
     * and returns the colored text. Supports both normal colors
     * and HEX colors.
     *
     * @param string String text to translate colors on
     * @return Component text with translated colors
     */
    public static TextComponent color(@NotNull String string) {
        return legacySerializer.deserialize(string);
    }

    /**
     * Removes the color from the given Component and returns String
     * with unformatted colors
     *
     * @param text Component to remove color from
     * @return String with unformatted colors
     */
    public static String stripColor(@NotNull Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }
}
