package me.wega.rpgambling.machines.crash;

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

import java.util.List;
import java.util.Map;

public class CrashMachineMenu extends ChestGui {
    private final CrashMachine crashMachine;
    private final StaticPane buttonAddBetPane = new StaticPane(0, 5, 9, 1);
    private final OutlinePane betsPane = new OutlinePane(2, 1, 5, 3);
    private final Economy vault = RPGambling.getInstance().getVault();

    public CrashMachineMenu(CrashMachine crashMachine) {
        super(6, "Crash");
        this.crashMachine = crashMachine;
        this.initialize();
    }

    private void initialize() {
        addPane(buttonAddBetPane);
        addPane(betsPane);

        buttonAddBetPane.addItem(getPlaceBetButton(), 2, 0);
        buttonAddBetPane.addItem(getPlaceBetButton(), 3, 0);
        buttonAddBetPane.addItem(getCloseButton(), 5, 0);
        buttonAddBetPane.addItem(getCloseButton(), 6, 0);

        /*
         I know, I know. This is super stupid, but I wasn't able to find a different way to update
         every open gui for the machine while still keeping every menu personal to only one player
        */
        Bukkit.getScheduler().runTaskTimer(RPGambling.getInstance(), this::loadBets, 4L, 20L);
    }

    @SuppressWarnings("DataFlowIssue")
    private void loadBets() {
        if (getViewerCount() == 0) return;
        Player player = ((Player) getViewers().get(0));
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
    }

    private GuiItem getBetButton(Player player, double betAmount) {
        Player better = ((Player) getViewers().get(0));
        if (player == better)
            return new GuiItem(new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                    .displayName(Component.text(player.getName()))
                    .lore(List.of(
                            Component.text("bet: " + betAmount),
                            Component.text("Click to remove your bet")
                    ))
                    .build(),
                    event -> {
                        Player better2 = ((Player) event.getWhoClicked());
                        event.setCancelled(true);
                        if (!better2.equals(better)) return;
                        crashMachine.removeBet(better2);
                        vault.depositPlayer(player, betAmount);
                        better2.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    }
            );

        return new GuiItem(new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                .displayName(Component.text(player.getName()))
                .lore(List.of(
                        Component.text("bet: " + betAmount)
                ))
                .build()
        );
    }

    private GuiItem getCloseButton() {
        return new GuiItem(new ItemBuilder(Material.PAPER)
                .displayName(Component.text("Close"))
                .build(),
                event -> {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
        );
    }

    private GuiItem getPlaceBetButton() {
        return new GuiItem(new ItemBuilder(Material.PAPER)
                .displayName(Component.text("Make a bet"))
                .build(),
                event -> {
                    event.setCancelled(true);
                    Player player = ((Player) event.getWhoClicked());
                    new BetMenu(crashMachine, e -> {
                        double betAmount = crashMachine.getBet(player);
                        if (!vault.has(player, betAmount)) {
                            player.sendMessage("Not enough funds");
                            return;
                        }

                        vault.withdrawPlayer(player, betAmount);
                        new CrashMachineMenu(crashMachine).show(player);

                    }).show(player);
                });
    }
}
