package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import lombok.Getter;
import me.wega.rpgambling.utils.ItemBuilder;
import me.wega.rpgambling.RPGambling;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

@Getter
public enum SlotItem {
    SEVEN(501, 0.125, 3, 10, 20, 50),
    GRAPES(508, 0.125, 0, 5, 10, 20),
    MELON(505, 0.125, 0, 5, 10, 20),
    PLUM(504, 0.125, 0, 3, 5, 10),
    ORANGE(507, 0.125, 0, 3, 5, 10),
    LEMON(503, 0.125, 0, 3, 5, 10),
    CHERRIES(502, 0.125, 0, 0, 3, 5),
    STAR(506, 0.125, 0, 0, 5, 15);

    private final int customModelData;
    private final double chance;
    private final int[] multipliers; // Multipliers for 2, 3, 4, and 5 counts
    public static final NamespacedKey itemKey = new NamespacedKey(RPGambling.getInstance(), "item-key");

    SlotItem(int customModelData, double chance, int mul2, int mul3, int mul4, int mul5) {
        this.customModelData = customModelData;
        this.chance = chance;
        this.multipliers = new int[]{mul2, mul3, mul4, mul5};
    }

    public int getMultiplier(int count) {
        if (count < 2)
            return 0;

        System.out.println(Arrays.toString(multipliers));
        return multipliers[count - 2];
    }

    public GuiItem getGuiItem() {
        return new GuiItem(new ItemBuilder(Material.PAPER)
                .displayName(Component.empty())
                .customModel(customModelData)
                .hideFlags()
                .container(itemKey, PersistentDataType.STRING, name())
                .build(),
                event -> event.setCancelled(true)
        );
    }
}