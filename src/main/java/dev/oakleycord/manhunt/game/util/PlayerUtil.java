package dev.oakleycord.manhunt.game.util;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {
    private static final List<UUID> wasRunner = new ArrayList<>();

    private PlayerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String key : progress.getAwardedCriteria())
                progress.revokeCriteria(key);
        }
    }

    //thanks some guy on spigot
    public static boolean isOutsideOfBorder(Player p) {
        Location loc = p.getLocation();
        WorldBorder border = p.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX();
        double z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    public static void resetPlayer(Player player, boolean respawn) {
        resetPlayer(player, respawn, false);
    }

    public static void resetPlayer(Player player, boolean respawn, boolean wasDeath) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setGlowing(false);
        player.getInventory().clear();
        player.setVelocity(new Vector(0, 0, 0));
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        Bukkit.getBossBars().forEachRemaining(bossBar -> bossBar.removePlayer(player));

        AbstractRun game = SpeedRuns.getGame();
        if (respawn) {
            if (player.getBedSpawnLocation() != null)
                player.teleport(player.getBedSpawnLocation());
            else player.teleport(game.getWorldHandler().getWorldOverworld().getSpawnLocation().add(0, 1, 0));
        }

        if (wasDeath && game instanceof ManHunt manHunt) {
            if (manHunt.getRunners().hasEntry(player.getName())) {
                incrementDeaths(player, ManHunt.MHTeam.RUNNERS);
                manHunt.setTeam(player, ManHunt.MHTeam.SPECTATORS);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
                wasRunner.add(player.getUniqueId());
            }

            if (manHunt.getHunters().hasEntry(player.getName())) {
                incrementDeaths(player, ManHunt.MHTeam.HUNTERS);
                player.getInventory().addItem(new ItemStack(Material.COMPASS));
            }
        }
    }

    public static void incrementDeaths(Player player, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(player.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addDeathHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addDeathRunner();
        });
    }

    public static void incrementLoses(Player player, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(player.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addLossHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addLossRunner();
        });

    }

    public static void resetPlayer(Player player) {
        resetPlayer(player, false, false);
    }

    public static boolean wasRunner(Player player) {
        return wasRunner.contains(player.getUniqueId());
    }

    public static void incrementKills(Player player, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(player.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addKillHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addKillRunner();
        });
    }

    public static void incrementWins(Player player, ManHunt.MHTeam team) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(player.getUniqueId(), user -> {
            if (team == ManHunt.MHTeam.HUNTERS)
                user.getUserMH().addWinHunter();
            else if (team == ManHunt.MHTeam.RUNNERS)
                user.getUserMH().addWinRunner();
        });
    }

    public static void rewardPoints(Player player, long points, String reason) {
        if (SpeedRuns.dbNotFound()) return;
        SpeedRuns.getDatabase().asyncUserAction(player.getUniqueId(), user -> user.addPoints(points, reason));
    }
}
