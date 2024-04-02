package me.wega.rpgambling.machines.crash;

import com.github.stefvanschie.inventoryframework.adventuresupport.StringHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.machines.BetMenu;
import me.wega.rpgambling.utils.ItemBuilder;
import me.wega.rpgambling.utils.SkullCreator;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class CrashMachineMenu extends ChestGui {
    private final CrashMachine crashMachine;
    private final StaticPane placeBetsPane = new StaticPane(1, 5, 3, 1);
    private final StaticPane closePane = new StaticPane(5, 5, 3, 1);
    private final OutlinePane betsPane = new OutlinePane(1, 1, 7, 3);
    private final Economy vault = RPGambling.getInstance().getVault();
    private final Player player;

    public CrashMachineMenu(CrashMachine crashMachine, Player player) {
        super(6, StringHolder.deserialize("&f⻔⻔⻔⻔⻔⻔⻔⻔\uE40A"));
        this.crashMachine = crashMachine;
        this.player = player;
        this.initialize();
    }

    private void initialize() {
        addPane(placeBetsPane);
        addPane(closePane);
        addPane(betsPane);

        placeBetsPane.fillWith(getPlaceBetItem(), event -> {
            event.setCancelled(true);
            new BetMenu(crashMachine, e -> {
                double betAmount = crashMachine.getBet(player);
                if (!vault.has(player, betAmount)) {
                    player.sendMessage("Not enough funds");
                    return;
                }

                vault.withdrawPlayer(player, betAmount);
                new CrashMachineMenu(crashMachine, player).show(player);

            }).show(player);
        });

        closePane.fillWith(getCloseItem(), event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        });

        /*
         I know, I know. This is super stupid, but I wasn't able to find a different way to update
         every open gui for the machine while still keeping every menu personal to only one player
        */
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getViewerCount() == 0) cancel();
                loadBets();
            }
        }.runTaskTimer(RPGambling.getInstance(), 4L, 20L);
    }

    @SuppressWarnings("DataFlowIssue")
    private void loadBets() {
        betsPane.clear();
        crashMachine.getBets().entrySet().stream()
                .filter(entry -> Bukkit.getPlayer(entry.getKey()) != null)
                .sorted(Map.Entry.comparingByKey(
                        (u1, u2) -> u1.equals(player.getUniqueId()) ? -1
                                : u2.equals(player.getUniqueId()) ? 1
                                : Double.compare(
                                crashMachine.getBet(Bukkit.getPlayer(u2)),
                                crashMachine.getBet(Bukkit.getPlayer(u1))
                        )
                ))
                .forEach(entry -> betsPane.addItem(getBetButton(Bukkit.getPlayer(entry.getKey()), entry.getValue())));

        this.update();
        crashMachine.handleCountdown(player);
    }

    private GuiItem getBetButton(Player betPlayer, double betAmount) {
        if (betPlayer.equals(player))
            return new GuiItem(new ItemBuilder(SkullCreator.itemFromUuid(betPlayer.getUniqueId()))
                    .displayName(Component.text(betPlayer.getName()))
                    .lore(List.of(
                            Component.text("bet: " + betAmount),
                            Component.text("Click to remove your bet")
                    ))
                    .hideFlags()
                    .build(),
                    event -> {
                        Player better2 = ((Player) event.getWhoClicked());
                        event.setCancelled(true);
                        if (!better2.equals(betPlayer)) return;
                        crashMachine.removeBet(betPlayer);
                        vault.depositPlayer(betPlayer, betAmount);
                        better2.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    }
            );

        return new GuiItem(new ItemBuilder(SkullCreator.itemFromUuid(betPlayer.getUniqueId()))
                .displayName(Component.text(betPlayer.getName()))
                .lore(List.of(
                        Component.text("bet: " + betAmount)
                ))
                .hideFlags()
                .build(), event -> event.setCancelled(true)
        );
    }

    private ItemStack getCloseItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(Component.text("Close"))
                .customModel(3)
                .hideFlags()
                .build();
    }

    private ItemStack getPlaceBetItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(Component.text("Make a bet"))
                .customModel(3)
                .hideFlags()
                .build();
    }
}
