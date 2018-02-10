package com.xpgaming.xPPayDay;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BaseCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player pl = (Player) src;
        pl.sendMessage(Text.of("\u00A7f[\u00A7ePayDay\u00A7f] \u00A7eSo far, you have earned \u00A76" + Main.getCurrentPayment(pl) + " coins\u00A7e!"));
        return null;
    }
}
