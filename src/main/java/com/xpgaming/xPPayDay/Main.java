package com.xpgaming.xPPayDay;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xpgaming.xPPayDay.commands.BaseCommand;
import com.xpgaming.xPPayDay.commands.Reload;
import com.xpgaming.xPPayDay.utils.Config;
import com.xpgaming.xPPayDay.utils.Utils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(id = Main.id, name = Main.name, version = "0.2")
public class Main {
    private static Main instance = null;
    public static Main getInstance() {
        return instance;
    }

    public static final String id = "xppayday";
    public static final String name = "xP// PayDay";

    private static String separator = FileSystems.getDefault().getSeparator();
    public static String primaryPath = "config" + separator;
    public static String commandConfigPath = "config" + separator + "xPPayDay" + separator;
    public static Path configPath = Paths.get(primaryPath, "xPPayDay.conf");

    public static ConfigurationLoader<CommentedConfigurationNode> primaryConfigLoader =
            HoconConfigurationLoader.builder().setPath(configPath).build();

    private Logger logger;

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public HashMap<String, Integer> onlinePlayerCounter = new HashMap<>();

    private final Cache<UUID, Tuple<Integer, Double>> map =
            CacheBuilder.newBuilder().expireAfterAccess(25, TimeUnit.MINUTES).build();

    private static EconomyService economyService;

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    public void addMoney(Player p, BigDecimal amount) {
        Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(p.getUniqueId());
        if (uOpt.isPresent()) {
            UniqueAccount account = uOpt.get();
            TransactionResult result = account.deposit(economyService.getDefaultCurrency(), amount, Sponge.getCauseStackManager().getCurrentCause());
            if (!(result.getResult() == ResultType.SUCCESS)) {
                p.sendMessage(Text.of("\u00A7f[" + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "prefix").getString()).toString() + "] \u00A7cUnable to give money, something broke!"));
            }
        }
    }

    CommandSpec reload = CommandSpec.builder()
            .permission("xppayday.reload")
            .description(Text.of("Reload the config!"))
            .executor(new Reload())
            .build();

    CommandSpec payDayCommands = CommandSpec.builder()
            .permission("xppayday.check")
            .description(Text.of("Base command!"))
            .executor(new BaseCommand())
            .child(reload, "reload", "r", "rel")
            .build();

    String path = "config" + File.separator + "xPPayDay";

    File configFile = new File(path, "xPPayDay.conf");
    ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(configFile).build();

    private static Optional<ConsoleSource> getConsole() {
        if (Sponge.isServerAvailable())
            return Optional.of(Sponge.getServer().getConsole());
        else
            return Optional.empty();
    }


    @Listener
    public void onConstructionEvent(GameConstructionEvent event) {
        Main.instance = this;
        //DO NOT DO ANYTHING HERE FOR IT WILL BRING DESTRUCTION AND DEATH;
    }

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        consoleMsg("§b       _____   ____   _____                 _             ");
        consoleMsg("§b      |  __ \\ / / /  / ____|               (_)            ");
        consoleMsg("§b __  _| |__) / / /  | |  __  __ _ _ __ ___  _ _ __   __ _ ");
        consoleMsg("§b \\ \\/ |  ___/ / /   | | |_ |/ _` | '_ ` _ \\| | '_ \\ / _` |");
        consoleMsg("§b  >  <| |  / / /    | |__| | (_| | | | | | | | | | | (_| |");
        consoleMsg("§b /_/\\_|_| /_/_/      \\_____|\\__,_|_| |_| |_|_|_| |_|\\__, |");
        consoleMsg("§b                                                     __/ |");
        consoleMsg("§b                                                    |___/ ");
        consoleMsg("");
        consoleMsg("§f[§6xP//§f] §ePayDay - Loaded v0.2!");
        consoleMsg("§f[§6xP//§f] §eBy Xenoyia with help from XpanD!");

        Config.getInstance().setup(configFile, configLoader);
        Sponge.getCommandManager().register(Sponge.getPluginManager().getPlugin("xppayday").get().getInstance().get(), payDayCommands, "payday", "xppayday");
    }
    
    public void consoleMsg(String str) {
        getConsole().ifPresent(console ->
                console.sendMessage(Text.of(str)));
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        Task task = Task.builder().execute(() -> {
            if (!Sponge.getServer().getOnlinePlayers().isEmpty()) {
                Collection<Player> playersOnline = Sponge.getServer().getOnlinePlayers();
                for (Player p : playersOnline) {
                    if (onlinePlayerCounter.containsKey(p.getName())) {
                        int i = onlinePlayerCounter.get(p.getName());
                        i = i + 1;
                        onlinePlayerCounter.remove(p.getName());
                        onlinePlayerCounter.put(p.getName(), i);
                    } else {
                        onlinePlayerCounter.put(p.getName(), 1);
                    }
                }
            }
        })
                .interval(1, TimeUnit.SECONDS)
                .name("xP// PayDay Counter")
                .submit(Sponge.getPluginManager().getPlugin("xppayday").get().getInstance().get());

        reloadTask();
    }

    public void reloadTask() {
        Sponge.getScheduler().getTasksByName("xP// PayDay Payment").forEach(t -> t.cancel());

        Task task2 = Task.builder().execute(() -> {
            if(Config.getInstance().getConfig().getNode("payday", "general", "global-msg").getBoolean()) Sponge.getServer().getBroadcastChannel().send(Text.of("\u00A7f[" + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "prefix").getString()) + "] " + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "global-payout-message").getString())));
            if (!Sponge.getServer().getOnlinePlayers().isEmpty()) {
                for (String name : onlinePlayerCounter.keySet()) {
                    Optional<Player> p = Sponge.getServer().getPlayer(name);
                    if (p.isPresent()) {
                        Player pl = p.get();
                        if (pl.isOnline()) {
                            double percentage = ((double) onlinePlayerCounter.get(name) / (Config.getInstance().getConfig().getNode("payday", "general", "time-in-minutes").getInt() * 60));
                            BigDecimal finalPaymentAmount = BigDecimal.valueOf(percentage * Config.getInstance().getConfig().getNode("payday", "general", "payout").getDouble()).setScale(2, RoundingMode.CEILING);
                            addMoney(pl, finalPaymentAmount);
                            pl.sendMessage(Text.of("\u00A7f[" + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "prefix").getString()) + "] " + Utils.formatText(Config.getInstance().getConfig().getNode("payday", "lang", "local-payout-message").getString().replaceAll("%c%", finalPaymentAmount.toString()))));
                        }
                    }
                }
                onlinePlayerCounter.clear();
            }
        })
                //Default payout times
                .interval(Config.getInstance().getConfig().getNode("payday", "general", "time-in-minutes").getInt(), TimeUnit.MINUTES)
                .name("xP// PayDay Payment")
                .submit(Sponge.getPluginManager().getPlugin("xppayday").get().getInstance().get());
    }

    public BigDecimal getCurrentPayment(Player pl) {
                double percentage = ((double)onlinePlayerCounter.get(pl.getName()) / (Config.getInstance().getConfig().getNode("payday", "general", "time-in-minutes").getInt() * 60));
                BigDecimal finalPaymentAmount = BigDecimal.valueOf(percentage * Config.getInstance().getConfig().getNode("payday", "general", "payout").getDouble()).setScale(2, RoundingMode.CEILING);
                return finalPaymentAmount;
            }
}