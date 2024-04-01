package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.adventuresupport.StringHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.rpgambling.ChatConsumer;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SlotRollMenu extends ChestGui {
    private final Map<Integer, LinkedList<GuiItem>> columns = new HashMap<>(5);
    private final Map<Integer, StaticPane> columnPanes = new HashMap<>(5);
    private final StaticPane rollPane = new StaticPane(4, 5, 3, 1);
    private final StaticPane betPane = new StaticPane(0, 3, 2, 3);
    private final Random random = new Random();
    boolean spinning = false;

    // TODO add SlotMachine class
    public SlotRollMenu() {
        super(6, StringHolder.deserialize("&f⻔⻔⻔⻔⻔⻔⻔⻔\uE66C"));
        this.initialize();
    }

    private void initialize() {
        for (int i = 0; i < 5; i++) {
            StaticPane column = new StaticPane(i + 3, 1, 1, 3);
            columnPanes.put(i, column);
            this.addPane(column);
        }

        for (int i = 0; i < 5; i++) {
            LinkedList<GuiItem> columnItems = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                GuiItem item = SlotItem.values()[j].getGuiItem().copy();
                columnItems.add(item);
                columnPanes.get(i).addItem(item, 0, j);
            }
            columns.put(i, columnItems);
        }

        this.addPane(rollPane);
        this.addPane(betPane);

        rollPane.fillWith(getRollItem(), event -> {
            event.setCancelled(true);
            if (!spinning) {
                Player player = ((Player) event.getWhoClicked());
                rollTimes(player, 20);
            }
        });

        betPane.fillWith(getChooseBetItem(), event -> {
            event.setCancelled(true);
            Player player = ((Player) event.getWhoClicked());
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            player.sendMessage("Write how much to bet or 'cancel'");
            new ChatConsumer<Double>(RPGambling.getInstance())
                    .onInput(player, ChatConsumer.Parser.DOUBLE, bet -> {
                        player.sendMessage("Placed bet of " + bet);
                        // TODO place an actual bet
                        new SlotRollMenu().show(player);
                    })
                    .onCancel(player, () -> {
                        player.sendMessage("cancelled betting");
                        new SlotRollMenu().show(player);
                    })
                    .onUnparsable(player, s -> {
                        player.sendMessage(s + " is not a valid number!");
                        player.sendMessage("Write how much to bet or 'cancel'");
                    });
        });
    }

    public void rollTimes(Player player, int times) {
        spinning = true;
        for (int i = 0; i < times; i++) {
            long delay = (long) (3L * Math.exp(0.17 * i));  // Using an exponential function to increase delay
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(RPGambling.getInstance(), () -> {
                this.rollOnce();
                this.update();
                if ((finalI + 1) == times)
                    Bukkit.getScheduler().runTaskLater(RPGambling.getInstance(), () -> {
                        this.handleWin(player);
                        spinning = false;
                    }, 10L);
            }, delay);
        }
    }

    public void rollOnce() {
        for (int i = 0; i < 5; i++) {
            LinkedList<GuiItem> columnItems = columns.get(i);
            columnItems.addFirst(this.getRandomButton());
            columnItems.removeLast();
            for (int j = 0; j < 3; j++) {
                columnPanes.get(i).addItem(columnItems.get(j), 0, j);
            }
        }
    }

    private void handleWin(Player player) {
        Map<SlotItem, Long> itemCounts = getWinningButtons().stream()
                .map(GuiItem::getItem)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getPersistentDataContainer)
                .map(pdc -> pdc.get(SlotItem.itemKey, PersistentDataType.STRING))
                .map(SlotItem::valueOf)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int multiplier = 0;


        for (SlotItem item : itemCounts.keySet()) {
            int count = Math.toIntExact(itemCounts.get(item));
            multiplier = Math.max(multiplier, item.getMultiplier(count));
        }

        if (multiplier > 0) {
            double reward = 0; // TODO use bet
            player.sendMessage("Congratulations! You won " + multiplier + "x your bet, which is " + reward + "!");
        } else {
            player.sendMessage("Better luck next time!");
        }
    }


    private List<GuiItem> getWinningButtons() {
        List<GuiItem> winningItems = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            winningItems.add(columns.get(i).get(1));

        return winningItems;
    }

    private GuiItem getRandomButton() {
        double randomNumber = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (SlotItem slotItem : SlotItem.values()) {
            cumulativeProbability += slotItem.getChance();
            if (randomNumber <= cumulativeProbability) {
                return slotItem.getGuiItem().copy();
            }
        }

        return null;
    }

    private ItemStack getChooseBetItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(Component.empty())
                .customModel(3)
                .hideFlags()
                .build();
    }

    private ItemStack getRollItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(Component.empty())
                .customModel(3)
                .hideFlags()
                .build();
    }
}
