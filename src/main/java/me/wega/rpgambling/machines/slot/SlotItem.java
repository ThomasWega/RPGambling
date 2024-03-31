package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import lombok.Getter;
import me.wega.rpgambling.utils.ItemBuilder;
import me.wega.rpgambling.RPGambling;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

@Getter
public enum SlotItem {
    SEVEN(501, 0.125),
    CHERRIES(502, 0.125),
    LEMON(503, 0.125),
    PLUM(504, 0.125),
    MELON(505, 0.125),
    STAR(506, 0.125),
    ORANGE(507, 0.125),
    GRAPES(508, 0.125);


    private final int customModelData;
    private final double chance;
    public static final NamespacedKey itemKey = new NamespacedKey(RPGambling.getInstance(), "item-key");


    SlotItem(int customModelData, double chance) {
        this.customModelData = customModelData;
        this.chance = chance;
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

