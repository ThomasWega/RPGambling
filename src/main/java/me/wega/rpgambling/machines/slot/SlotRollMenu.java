package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.rpgambling.RPGambling;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SlotRollMenu extends ChestGui {
    private static final GuiItem melon = new GuiItem(new ItemStack(Material.PINK_DYE));
    private static final GuiItem lemon = new GuiItem(new ItemStack(Material.YELLOW_DYE));
    private static final GuiItem cherry = new GuiItem(new ItemStack(Material.RED_DYE));
    private static final GuiItem grape = new GuiItem(new ItemStack(Material.PURPLE_DYE));
    private static final GuiItem star = new GuiItem(new ItemStack(Material.WHITE_DYE));

    private final List<GuiItem> items = List.of(melon, lemon, cherry, grape, star);

    private final Map<Integer, LinkedList<GuiItem>> columns = new HashMap<>(5);
    private final Map<Integer, StaticPane> columnPanes = new HashMap<>(5);
    private final Random random = new Random();
    boolean spinning = false;

    public SlotRollMenu() {
        super(5, "Slots");
        this.initialize();

        this.setOnBottomClick(event -> {
            System.out.println(spinning);
            if (!spinning)
                rollTimes(20);
        });
    }

    private void initialize() {
        for (int i = 0; i < 5; i++) {
            StaticPane column = new StaticPane(i + 1, 1, 1, 3);
            columnPanes.put(i, column);
            this.addPane(column);
        }

        for (int i = 0; i < 5; i++) {
            LinkedList<GuiItem> columnItems = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                GuiItem item = items.get(j).copy();
                columnItems.add(item);
                columnPanes.get(i).addItem(item, 0, j);
            }
            columns.put(i, columnItems);
        }
    }

    public void rollTimes(int times) {
        spinning = true;
        for (int i = 0; i < times; i++) {
            long delay = (long) (3L * Math.exp(0.17 * i));  // Using an exponential function to increase delay
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(RPGambling.getInstance(), () -> {
                this.rollOnce();
                this.update();
                if ((finalI + 1) == times)
                    Bukkit.getScheduler().runTaskLater(RPGambling.getInstance(), () -> spinning = false, 10L);
            }, delay);
        }
    }

    public void rollOnce() {
        for (int i = 0; i < 5; i++) {
            LinkedList<GuiItem> columnItems = columns.get(i);
            columnItems.addFirst(getRandomItem());
            columnItems.removeLast();
            for (int j = 0; j < 3; j++) {
                columnPanes.get(i).addItem(columnItems.get(j), 0, j);
            }
        }
    }

    private GuiItem getRandomItem() {
        return items.get(random.nextInt(items.size())).copy();
    }
}
