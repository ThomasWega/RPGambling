package me.wega.rpgambling.command;

import dev.jorel.commandapi.CommandAPICommand;
import me.wega.rpgambling.data.PlayerData;
import me.wega.rpgambling.machines.crash.CrashMachine;
import me.wega.rpgambling.machines.slot.SlotMachine;
import me.wega.rpgambling.machines.slot.SlotMachineMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class GamblingCommand {
    public GamblingCommand() {
        register();
    }

    private void register() {
        new CommandAPICommand("admingambling")
                .withAliases("gambling admin");
        new CommandAPICommand("gambling")
                .withSubcommand(new CommandAPICommand("test")
                        .executesPlayer((sender, args) -> {
                            new SlotMachineMenu(slotMachineS).show(sender);
                           // new CrashMenu(crashMachineS).show(sender);
                        }))
            .withPermission("gambling.user")
            .withSubcommand(new CommandAPICommand("admin")
                .withPermission("gambling.admin")
                .withSubcommand(new CommandAPICommand("chips"))
                .withSubcommand(new CommandAPICommand("owner"))
                .withSubcommand(new CommandAPICommand("rig"))
            )
            .withSubcommand(new CommandAPICommand("chips").executesPlayer((player, args) -> {
                player.sendMessage("You have total of " + PlayerData.get(player).getChips() + " chips.");
            }))
            .withSubcommand(new CommandAPICommand("time").executesPlayer((player, args) -> {
                player.sendMessage("You have spent " + PlayerData.get(player).getPrettyTimeSpentInCasino() + " in the casino.");
            }))
            .register();
    }


    // FIXME REMOVE (only for testing)
    private static final CrashMachine crashMachineS = new CrashMachine(new Location(Bukkit.getWorld("world"), 2, 2, 2));
    private static final SlotMachine slotMachineS = new SlotMachine(new Location(Bukkit.getWorld("world"), 2, 2, 2));
}
