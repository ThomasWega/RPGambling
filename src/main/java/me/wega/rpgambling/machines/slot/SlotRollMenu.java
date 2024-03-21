package me.wega.rpgambling.machines.slot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class SlotRollMenu extends ChestGui {
    private static final ItemStack melon = new ItemStack(Material.PINK_DYE);
    private static final ItemStack lemon = new ItemStack(Material.YELLOW_DYE);
    private static final ItemStack cherry = new ItemStack(Material.RED_DYE);
    private static final ItemStack grape = new ItemStack(Material.PURPLE_DYE);
    private static final ItemStack star = new ItemStack(Material.WHITE_DYE);

    private final List<ItemStack> items = List.of(melon, lemon, cherry, grape, star);

    private final StaticPane row1 = new StaticPane(1, 1, 8, 1);
    private final StaticPane row2 = new StaticPane(1, 2, 8, 1);
    private final StaticPane row3 = new StaticPane(1, 3, 8, 1);


    public SlotRollMenu() {
        super(5, "Slots");
        this.initialize();

        this.addPane(row1);
        this.addPane(row2);
        this.addPane(row3);

        this.setOnBottomClick(event -> {
            this.roll();
            this.update();
        });
    }

    private void initialize() {
        for (int i = 0; i < 5; i++) {
            row1.addItem(new GuiItem(getRandomItem()), i, 0);
            row2.addItem(new GuiItem(getRandomItem()), i, 0);
            row3.addItem(new GuiItem(getRandomItem()), i, 0);
        }
    }

    public void roll() {
        int i2 = 0;

        // FIXME doesnt return items in order!!
        for (GuiItem item : row2.getItems()) {
            row3.addItem(item, i2, 0);
            i2++;
        }

        row2.clear();

        int i1 = 0;
        for (GuiItem item : row1.getItems()) {
            row2.addItem(item, i1, 0);
            i1++;
        }

        for (int i = 0; i < 5; i++) {
            row1.addItem(new GuiItem(getRandomItem()), i, 0);
        }
    }


    private final Random random = new Random();

    private ItemStack getRandomItem() {
        return items.get(random.nextInt(items.size()));
    }
}
