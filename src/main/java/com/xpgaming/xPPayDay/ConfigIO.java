package com.xpgaming.xPPayDay;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

// Thanks to XpanD for 99.9% of the code heres

public class ConfigIO {
    public static void checkConfigDir()
    {
        try
        {
            Files.createDirectory(Paths.get(Main.commandConfigPath));
        }
        catch (IOException ignored) {}
    }

    public static void registerCommands()
    {

        CommandSpec reload = CommandSpec.builder()
                .permission("xppayday.reload")
                .description(Text.of("Reload the config!"))
                .executor(new ReloadConfig())
                .build();

        CommandSpec payDayCommands = CommandSpec.builder()
                .permission("xppayday.check")
                .description(Text.of("Check your earnings!"))
                .executor(new BaseCommand())
                .child(reload, "reload", "r", "rel")
                .build();

        PluginContainer payDayPlugin = Sponge.getPluginManager().getPlugin("xppayday").orElse(null);
        Sponge.getCommandManager().register(payDayPlugin, payDayCommands, "payday", "xppayday");
    }

    public static void loadAllCommandConfigs() {
        tryCreateConfig("xPPayDay", Main.configPath);
        CommentedConfigurationNode commandConfig = null;
        try {
            commandConfig = Main.primaryConfigLoader.load();
        } catch (IOException F) {
            F.printStackTrace();
        }

        Main.payout =
                interpretDouble(commandConfig.getNode("payout").getString());
        Main.timeInMins =
                interpretInteger(commandConfig.getNode("time-in-minutes").getString());

    }

    private static Integer interpretInteger(String input)
    {
        if (input != null && input.matches("-?[1-9]\\d*|0"))
            return Integer.parseInt(input);
        else
            return null;
    }

    private static Double interpretDouble(String input)
    {
        if (input != null)
        {
            Scanner readDouble = new Scanner(input);
            if (readDouble.hasNextDouble())
                return readDouble.nextDouble();
        }
        return null;
    }

    private static void tryCreateConfig(String callSource, Path checkPath)
    {
        if (Files.notExists(checkPath))
        {
                try
                {
                    Files.copy(ConfigIO.class.getResourceAsStream("/assets/xPPayDay.conf"),
                            Paths.get(Main.primaryPath, "xPPayDay.conf"));
                }
                catch (IOException F)
                {
                    F.printStackTrace();
                }
        }
    }
}