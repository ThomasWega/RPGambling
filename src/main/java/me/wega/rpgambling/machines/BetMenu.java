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
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BetMenu extends ChestGui {
    private final Consumer<Double> betResult;
    private final OutlinePane betsPane = new OutlinePane(2, 1, 5, 1);
    private final StaticPane customBetPane = new StaticPane(4, 3, 1, 1);
    private final Map<Integer, Material> betsPresets = Collections.unmodifiableMap(new LinkedHashMap<>() {{
        put(100, Material.IRON_NUGGET);
        put(250, Material.GOLD_NUGGET);
        put(500, Material.GOLD_INGOT);
        put(1000, Material.DIAMOND);
        put(2500, Material.EMERALD);
    }});

    public BetMenu(Consumer<Double> betResult) {
        super(5, "Select bet amount");
        this.betResult = betResult;
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
                .displayName(Component.text(betAmount))
                .hideFlags()
                .build(),
                event -> {
                    event.setCancelled(true);
                    betResult.accept(betAmount);
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
                    // TODO round up bet
                    new ChatCallback<>(RPGambling.getInstance(), player, ChatCallback.Parser.DOUBLE)
                            .onCondition(bet -> {
                                if (bet < minBet) {
                                    player.sendMessage("Minimum bet is " + minBet);
                                    player.sendMessage("Write how much to bet or 'cancel'");
                                    return false;
                                }
                                return true;
                            })
                            .onSuccess(betResult)
                            .onCancel(player1 -> {
                                player1.sendMessage("cancelled betting");
                                player1.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                            })
                            .onFail(s -> {
                                player.sendMessage(s + " is not a valid number!");
                                player.sendMessage("Write how much to bet or 'cancel'");
                            });
                }
        );
    }
}
