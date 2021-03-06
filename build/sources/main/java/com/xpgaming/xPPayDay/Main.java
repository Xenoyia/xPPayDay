package com.xpgaming.xPPayDay;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(id = Main.id, name = Main.name, version = "0.1")
public class Main {
    public static final String id = "xppayday";
    public static final String name = "xP// PayDay";
    private static final Logger log = LoggerFactory.getLogger(name);
    HashMap<String, Integer> onlinePlayerCounter = new HashMap<String, Integer>();


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
        if(uOpt.isPresent()) {
            UniqueAccount account = uOpt.get();
            TransactionResult result = account.deposit(economyService.getDefaultCurrency(), amount, Cause.source(this).build());
            if (!(result.getResult() == ResultType.SUCCESS)) {
                p.sendMessage(Text.of("\u00A7f[\u00A7cPayDay\u00A7f] \u00A7cUnable to give money, something broke!"));
            }
        }
    }

    @Listener (beforeModifications = true)
    public void onGameInitialization(GameInitializationEvent event) {
        Task task = Task.builder().execute(() -> {
            if(!Sponge.getServer().getOnlinePlayers().isEmpty()) {
                Collection<Player> playersOnline = Sponge.getServer().getOnlinePlayers();
                for(Player p : playersOnline) {
                        if (onlinePlayerCounter.containsKey(p.getName())) {
                            int i = onlinePlayerCounter.get(p.getName());
                            i = i+1;
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
                .submit(this);

        Task task2 = Task.builder().execute(() -> {
            Sponge.getServer().getBroadcastChannel().send(Text.of("\u00A7f[\u00A7exP//\u00A7f] \u00A7eIt's pay day! Time to make it rain!"));
            if(!Sponge.getServer().getOnlinePlayers().isEmpty()) {
                for(String name : onlinePlayerCounter.keySet()) {
                    Optional<Player> p = Sponge.getServer().getPlayer(name);
                    if(p.isPresent()) {
                        Player pl = p.get();
                        if (pl.isOnline()) {
                            double percentage = ((double)onlinePlayerCounter.get(name) / 3600.00);
                            BigDecimal finalPaymentAmount = BigDecimal.valueOf(percentage * 22).setScale(2, RoundingMode.CEILING);
                            addMoney(pl, finalPaymentAmount);
                            pl.sendMessage(Text.of("\u00A7f[\u00A7exP//\u00A7f] \u00A7eYou earned \u00A76" + finalPaymentAmount + " coins\u00A7e, thanks for playing!"));
                        }
                    }
                }
                onlinePlayerCounter.clear();
            }
        })
                .interval(1, TimeUnit.HOURS)
                .name("xP// PayDay Payment")
                .submit(this);
        log.info("Loaded v0.1!");
    }
}
