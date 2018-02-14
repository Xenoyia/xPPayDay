package com.xpgaming.xPPayDay.utils;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.xpgaming.xPPayDay.Main;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class Config {
    private static Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }
    private CommentedConfigurationNode config;
    @Inject
    @ConfigDir(sharedRoot = false) private File configDir;
    String path = "config"+File.separator+"xPPayDay";
    private File configFile = new File(path, "xPPayDay.conf");
    private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(configFile).build();

    public void configCreate() throws ObjectMappingException, IOException {
        try {
            if(!configFile.getParentFile().exists()) configFile.getParentFile().mkdir();
            configFile.createNewFile();
            configLoad();
            CommentedConfigurationNode payday = config.getNode("payday").setComment("xP// PayDay coded by Xenoyia with help from XpanD! Check out mc.xpgaming.com!");
            payday.getNode("general","payout").setComment("The amount of in-game economy to be paid out if you're online 100% during the payout period.");
            payday.getNode("general","payout").setValue(100.00);
            payday.getNode("general","time-in-minutes").setComment("How long the global payout period is, in minutes. 60 = 1 hour.");
            payday.getNode("general","time-in-minutes").setValue(60);
            payday.getNode("general","global-msg").setComment("TRUE to display a message globally when the payout occurs, or FALSE to keep it local.");
            payday.getNode("general","global-msg").setValue(true);
            payday.getNode("lang").setComment("You can use colour codes here - &a, &b, &c etc.");
            payday.getNode("lang","prefix").setValue("&ePayDay&f");
            payday.getNode("lang","global-payout-message").setValue("&eIt's pay day! Time to make it rain!");
            payday.getNode("lang","local-payout-message").setComment("%c% is translated to the amount of economy. If you use dollars, you can do $%c%.");
            payday.getNode("lang","local-payout-message").setValue("&eYou earned %c% coins, thanks for playing!");
            payday.getNode("lang","reload-message").setValue("&e&lConfig reloaded!");
            configSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup(File configFile, ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;
        this.configFile = configFile;
        if (!configFile.exists()) {
            try {
                configCreate();
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            configLoad();
    }

    public CommentedConfigurationNode getConfig() {
        return config;
    }

    public void configLoad() {
        if (!configFile.exists()) {
            try {
                configCreate();
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            try {
                config = configLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void configSave() {
        try {
            configLoader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAndLoadConfig() {
        configSave();
        configLoad();
    }
}
