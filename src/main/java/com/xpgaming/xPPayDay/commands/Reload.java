package com.xpgaming.xPPayDay.commands;

import com.xpgaming.xPPayDay.Main;
import com.xpgaming.xPPayDay.utils.Config;
import com.xpgaming.xPPayDay.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Reload implements CommandExecutor {
    private static Optional<ConsoleSource> getConsole() {
        if (Sponge.isServerAvailable())
            return Optional.of(Sponge.getServer().getConsole());
        else
            return Optional.empty();
    }

    public void consoleMsg(String str) {
        getConsole().ifPresent(console ->
                console.sendMessage(Text.of(str)));
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Main main = Main.getInstance();
        consoleMsg(String.valueOf(main.hashCode()));
        main.reloadTask();
        Config.getInstance().configLoad();
        Main.getInstance().reloadTask();
        src.sendMessage(Text.of("\u00A7f["+Utils.formatText(Config.getInstance().getConfig().getNode("payday","lang","prefix").getString())+"] "+Utils.formatText(Config.getInstance().getConfig().getNode("payday","lang","reload-message").getString())));
        return CommandResult.success();
    }

}