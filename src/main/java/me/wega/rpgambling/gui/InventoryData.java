package me.wega.rpgambling.gui;

import org.bukkit.inventory.InventoryHolder;

public interface InventoryData extends InventoryHolder {
    /**
     * @return Whether the inventory should be closed
     */
    boolean onClose();

    void onClick(int slot);
}
