package com.xpgaming.xPPayDay.commands;

import com.xpgaming.xPPayDay.Main;
import com.xpgaming.xPPayDay.utils.Config;
import com.xpgaming.xPPayDay.utils.Utils;
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
        if(src instanceof Player) {
            Player pl = (Player) src;
            src.sendMessage(Text.of("\u00A7f["
                    + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "prefix").getString())
                    + "] "+ Utils.formatText(Config.getInstance().getConfig().getNode("payday","lang","check-payout-message")
                    .getString().replaceAll("%c%", Main.getInstance().getCurrentPayment(pl).toString()))));
        } else {
            src.sendMessage(Text.of("You need to be a player to run this command!"));
        }
        return CommandResult.success();
    }
}
