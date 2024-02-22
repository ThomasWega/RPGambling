package me.wega.rpgambling.commands;

import dev.jorel.commandapi.CommandAPICommand;

public class GamblingCommand {
    public GamblingCommand() {
        register();
    }

    private void register() {
        new CommandAPICommand("admingambling").withAliases("")
        new CommandAPICommand("gambling")
            .withPermission("gambling.user")
            .withSubcommand(new CommandAPICommand("admin")
                .withPermission("gambling.admin")
                .withSubcommand(new CommandAPICommand(("chips")))
                .withSubcommand()
                .withSubcommand()
            )
            .withSubcommand(new CommandAPICommand("chips"))
            .withSubcommand(new CommandAPICommand("time"))
            .register();
    }
}
