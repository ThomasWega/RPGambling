package me.wega.rpgambling.machines;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.rpgambling.ChatCallback;
import me.wega.rpgambling.RPGambling;
import me.wega.rpgambling.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BetMenu extends ChestGui {
    private final GamblingMachine machine;
    private final Consumer<InventoryClickEvent> afterBetAction;
    private final OutlinePane betsPane = new OutlinePane(2, 1, 5, 1);
    private final StaticPane customBetPane = new StaticPane(4, 3, 1, 1);
    private final Map<Integer, Material> betsPresets = Collections.unmodifiableMap(new LinkedHashMap<>() {{
        put(100, Material.IRON_NUGGET);
        put(250, Material.GOLD_NUGGET);
        put(500, Material.GOLD_INGOT);
        put(1000, Material.DIAMOND);
        put(2500, Material.EMERALD);
    }});

    public BetMenu(GamblingMachine machine, Consumer<InventoryClickEvent> afterBetAction) {
        super(5, "Select bet amount");
        this.machine = machine;
        this.afterBetAction = afterBetAction;
        this.initialize();
    }

    private void initialize() {
        addPane(betsPane);
        addPane(customBetPane);

        betsPresets.forEach((key, value) -> betsPane.addItem(getBetItem(value, key)));
        customBetPane.addItem(getCustomBetItem(), 0, 0);
    }

    private GuiItem getBetItem(Material material, double betAmount) {
        return new GuiItem(new ItemBuilder(material)
                .hideFlags()
                .displayName(Component.text(betAmount))
                .build(),
                event -> {
                    event.setCancelled(true);
                    Player player = ((Player) event.getWhoClicked());
                    machine.setBet(player, betAmount);
                    afterBetAction.accept(event);
                }
        );
    }

    private final double minBet = 100;

    private GuiItem getCustomBetItem() {
        return new GuiItem(new ItemBuilder(Material.HONEYCOMB)
                .displayName(Component.text("Custom bet"))
                .hideFlags()
                .build(),
                event -> {
                    Player player = ((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    player.sendMessage("Write how much to bet or 'cancel'");
                    new ChatCallback<>(RPGambling.getInstance(), player, ChatCallback.Parsers.DOUBLE)
                            .onSuccess(bet -> {
                                if (bet < minBet) {
                                    player.sendMessage("Minimum bet is " + minBet);
                                    player.sendMessage("Write how much to bet or 'cancel'");
                                    return false;
                                }
                                player.sendMessage("Placed bet of " + bet);
                                machine.setBet(player, bet);
                                afterBetAction.accept(event);
                                return true;
                            })
                            .onCancel(player1 -> {
                                player1.sendMessage("cancelled betting");
                                afterBetAction.accept(event);
                            })
                            .onFail(s -> {
                                player.sendMessage(s + " is not a valid number!");
                                player.sendMessage("Write how much to bet or 'cancel'");
                            });
                }
        );
    }
}
