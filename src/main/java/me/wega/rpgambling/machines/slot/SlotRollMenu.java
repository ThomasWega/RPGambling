package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SlotRollMenu extends ChestGui {
    private static final ItemStack melon = new ItemStack(Material.PINK_DYE);
    private static final ItemStack lemon = new ItemStack(Material.YELLOW_DYE);
    private static final ItemStack cherry = new ItemStack(Material.RED_DYE);
    private static final ItemStack grape = new ItemStack(Material.PURPLE_DYE);
    private static final ItemStack star = new ItemStack(Material.WHITE_DYE);

    private final List<ItemStack> items = List.of(melon, lemon, cherry, grape, star);

    private Set<GuiItem> row1Items = new LinkedHashSet<>();
    private final StaticPane row1 = new StaticPane(1, 1, 8, 1);
    private Set<GuiItem> row2Items = new LinkedHashSet<>();
    private final StaticPane row2 = new StaticPane(1, 2, 8, 1);
    private final StaticPane row3 = new StaticPane(1, 3, 8, 1);


    public SlotRollMenu() {
        super(5, "Slots");
        this.initialize();

        this.addPane(row1);
        this.addPane(row2);
        this.addPane(row3);

        this.setOnBottomClick(event -> {
            System.out.println("ROLL");
            this.roll();
            this.update();
        });
    }

    private void initialize() {
        for (int i = 0; i < 5; i++) {
            GuiItem g1 = new GuiItem(getRandomItem());
            GuiItem g2 = new GuiItem(getRandomItem());
            GuiItem g3 = new GuiItem(getRandomItem());

            row1Items.add(g1);
            row2Items.add(g2);

            row1.addItem(g1, i, 0);
            row2.addItem(g2, i, 0);
            row3.addItem(g3, i, 0);
        }
    }

    public void roll() {
        // Roll for row3
        row3.clear();
        int i3 = 0;
        for (GuiItem item : row2Items) {
            row3.addItem(item, i3, 0);
            i3++;
        }

        // Roll for row2
        row2.clear();
        row2Items = new LinkedHashSet<>();
        int i2 = 0;
        for (GuiItem item : row1Items) {
            row2.addItem(item, i2, 0);
            row2Items.add(item);
            i2++;
        }

        // Roll for row1
        row1.clear();
        row1Items = new LinkedHashSet<>();
        for (int i = 0; i < 5; i++) {
            GuiItem guiItem = new GuiItem(getRandomItem());
            row1.addItem(guiItem, i, 0);
            row1Items.add(guiItem);
        }
    }


    private final Random random = new Random();

    private ItemStack getRandomItem() {
        return items.get(random.nextInt(items.size()));
    }
}
