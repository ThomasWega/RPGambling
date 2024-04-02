package me.wega.rpgambling.machines.crash;

import com.github.stefvanschie.inventoryframework.adventuresupport.StringHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.utils.ItemBuilder;
import me.wega.rpgambling.utils.SkullCreator;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class CrashMachineGameMenu extends ChestGui {
    private final CrashMachine crashMachine;
    private final StaticPane stopPane = new StaticPane(3, 5, 3, 1);
    private final OutlinePane betsPane = new OutlinePane(1, 1, 7, 3);
    private final Economy vault = RPGambling.getInstance().getVault();
    private final Player player;

    public CrashMachineGameMenu(CrashMachine crashMachine, Player player) {
        super(6, StringHolder.deserialize("&f⻔⻔⻔⻔⻔⻔⻔⻔\uE40C"));
        this.crashMachine = crashMachine;
        this.player = player;
        this.initialize();
    }

    private void initialize() {
        addPane(stopPane);
        addPane(betsPane);

        this.loadBets();
        crashMachine.startCrash();

        /*
         I know, I know. This is super stupid, but I wasn't able to find a different way to update
         every open gui for the machine while still keeping every menu personal to only one player
        */
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getViewerCount() == 0) cancel();
                stopPane.clear();
                stopPane.fillWith(getStopItem(), event -> {
                    event.setCancelled(true);
                    // TODO finish stop
                });
                update();
            }
        }.runTaskTimer(RPGambling.getInstance(), 1L, 1L);
    }

    @SuppressWarnings("DataFlowIssue")
    private void loadBets() {
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

    private GuiItem getBetButton(Player betPlayer, double betAmount) {
        return new GuiItem(new ItemBuilder(SkullCreator.itemFromUuid(betPlayer.getUniqueId()))
                .displayName(Component.text(betPlayer.getName()))
                .lore(List.of(
                        Component.text("bet: " + betAmount)
                ))
                .hideFlags()
                .build(), event -> event.setCancelled(true)
        );
    }

    private ItemStack getStopItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(Component.text("Current value - " + crashMachine.getCrashAmount()))
                .customModel(3)
                .hideFlags()
                .build();
    }
}
