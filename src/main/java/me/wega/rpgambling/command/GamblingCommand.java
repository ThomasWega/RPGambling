package me.wega.rpgambling.command;

import dev.jorel.commandapi.CommandAPICommand;
import me.wega.rpgambling.data.PlayerData;

public class GamblingCommand {
    public GamblingCommand() {
        register();
    }

    private void register() {
        new CommandAPICommand("admingambling").withAliases("gambling admin");
        new CommandAPICommand("gambling")
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
}
