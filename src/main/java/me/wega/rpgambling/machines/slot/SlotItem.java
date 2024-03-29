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
    MELON(Material.PINK_DYE, 0.25),
    LEMON(Material.YELLOW_DYE, 0.3),
    CHERRY(Material.RED_DYE, 0.15),
    GRAPE(Material.PURPLE_DYE, 0.25),
    STAR(Material.WHITE_DYE, 0.05);

    private final Material material;
    private final double chance;
    public static final NamespacedKey itemKey = new NamespacedKey(RPGambling.getInstance(), "item-key");


    SlotItem(Material material, double chance) {
        this.material = material;
        this.chance = chance;
    }

    public GuiItem getGuiItem() {
        return new GuiItem(new ItemBuilder(material)
                .displayName(Component.empty())
                .hideFlags()
                .container(itemKey, PersistentDataType.STRING, name())
                .build(),
                event -> event.setCancelled(true)
        );
    }

}

