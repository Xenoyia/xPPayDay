package com.xpgaming.xPPayDay;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ReloadConfig implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ConfigIO.checkConfigDir();
        ConfigIO.loadAllCommandConfigs();
        ConfigIO.registerCommands();
        Main.reloadTask();
        Player pl = (Player) src;
        pl.sendMessage(Text.of("\u00A7f[\u00A7ePayDay\u00A7f] \u00A7eSuccessfully reloaded the config!"));
        pl.sendMessage(Text.of("\u00A7f[\u00A7ePayDay\u00A7f] \u00A7ePayouts are now "+Main.payout+" and take "+Main.timeInMins+" minutes."));
        return null;
    }
}
