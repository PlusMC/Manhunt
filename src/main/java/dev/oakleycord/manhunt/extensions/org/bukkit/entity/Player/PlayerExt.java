package dev.oakleycord.manhunt.extensions.org.bukkit.entity.Player;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.plusmc.pluslibcore.mongo.UserSR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


@Extension
public class PlayerExt {
    private static final List<UUID> wasRunner = new ArrayList<>();

    public static void resetAdvancements(@This Player thiz) {
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            AdvancementProgress progress = thiz.getAdvancementProgress(advancement);
            for (String key : progress.getAwardedCriteria())
                progress.revokeCriteria(key);
        }
    }


    //thanks some guy on spigot
    public static boolean isOutsideOfBorder(@This Player thiz) {
        Location loc = thiz.getLocation();
        WorldBorder border = thiz.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX();
        double z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    public static void reset(@This Player thiz, boolean respawn) {
        reset(thiz, respawn, false);
    }

    public static void reset(@This Player thiz, boolean respawn, boolean wasDeath) {
        AbstractRun game = SpeedRuns.getGame();
        if (respawn) {
            if (thiz.getBedSpawnLocation() != null) {
                thiz.teleport(thiz.getBedSpawnLocation());
            } else thiz.teleport(game.getWorldHandler().getWorldOverworld().getSpawnLocation().add(0, 1, 0));
        }

        thiz.setHealth(20);
        thiz.setFoodLevel(20);
        thiz.setFireTicks(0);
        thiz.setFallDistance(0);
        thiz.setSaturation(20);
        thiz.setExhaustion(0);
        thiz.setRemainingAir(thiz.getMaximumAir());
        thiz.setExp(0);
        thiz.setLevel(0);
        thiz.setTotalExperience(0);
        thiz.setGlowing(false);
        thiz.getInventory().clear();
        thiz.setVelocity(new Vector(0, 0, 0));
        thiz.getActivePotionEffects().forEach(potion -> thiz.removePotionEffect(potion.getType()));
        Bukkit.getBossBars().forEachRemaining(bossBar -> bossBar.removePlayer(thiz));


        if (game instanceof ManHunt manHunt) {
            if (manHunt.getRunners().hasEntry(thiz.getName()) && wasDeath) {
                incrementDeaths(thiz, ManHunt.MHTeam.RUNNERS);
                manHunt.setTeam(thiz, ManHunt.MHTeam.SPECTATORS);
                thiz.getWorld().playSound(thiz.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
                wasRunner.add(thiz.getUniqueId());
            }

            if (manHunt.getHunters().hasEntry(thiz.getName())) {
                if (wasDeath)
                    incrementDeaths(thiz, ManHunt.MHTeam.HUNTERS);
                thiz.getInventory().addItem(new ItemStack(Material.COMPASS));
            }
        }
    }

    public static void incrementDeaths(@This Player thiz, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addDeathHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addDeathRunner();
        });
    }

    public static void incrementLoses(@This Player thiz, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addLossHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addLossRunner();
        });

    }

    public static boolean wasRunner(@This Player thiz) {
        return wasRunner.contains(thiz.getUniqueId());
    }

    public static void incrementKills(@This Player thiz, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addKillHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addKillRunner();
        });
    }

    public static void incrementWins(@This Player thiz, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addWinHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addWinRunner();
        });
    }

    public static void finishedRun(@This Player thiz, Mode mode, long time) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> {
            UserSR userMH = user.getUserMH();
            float timeSeconds = time / 1000f;
            timeSeconds = Math.round(timeSeconds * 100f) / 100f;
            user.getPlayer().sendMessage("§aYou finished " + mode.name() + "% in " + timeSeconds + "s!");
            if (time < userMH.getPersonalBests().getOrDefault(mode.name(), Long.MAX_VALUE)) {
                user.getPlayer().sendMessage("§aNew personal best!");
                user.getPlayer().playSound("entity.player.levelup", 1, 2f);
            }
            userMH.addPersonalBest(mode.name(), time);
        });
    }

    public static void rewardPoints(@This Player thiz, long points, String reason) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(thiz.getUniqueId(), user -> user.addPoints(points, reason));
    }

    public static void reset(@This Player thiz) {
        reset(thiz, false, false);
    }


}