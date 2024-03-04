package me.wega.rpgambling.listener;

import me.wega.rpgambling.gui.InventoryData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof InventoryData)) return;
        boolean close = ((InventoryData) holder).onClose();
        if (event.getReason() != InventoryCloseEvent.Reason.PLAYER || !close) return;
        event.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != event.getClickedInventory()) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof InventoryData)) return;
        ((InventoryData) holder).onClick(event.getSlot());
        event.setCancelled(true);
    }
}
