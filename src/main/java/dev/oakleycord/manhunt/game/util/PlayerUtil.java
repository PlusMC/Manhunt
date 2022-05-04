package dev.oakleycord.manhunt.game.util;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.plusmc.pluslib.mongo.User;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {
    private static List<UUID> wasRunner;

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
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
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

        MHGame game = ManHunt.getGame();
        if (respawn) {
            if (player.getBedSpawnLocation() != null)
                player.teleport(player.getBedSpawnLocation());
            else player.teleport(game.getOverworld().getSpawnLocation().add(0, 1, 0));
        }

        if (ManHunt.hasGame() && wasDeath) {
            if (game.getRunners().hasEntry(player.getName())) {
                game.setTeam(player, GameTeam.SPECTATORS);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
                wasRunner.add(player.getUniqueId());
                incrementLoses(player, GameTeam.RUNNERS);
            }

            if (game.getHunters().hasEntry(player.getName()))
                player.getInventory().addItem(new ItemStack(Material.COMPASS));

            incrementDeaths(player);
        }
    }

    public static void resetPlayer(Player player, boolean respawn) {
        resetPlayer(player, respawn, false);
    }

    public static void resetPlayer(Player player) {
        resetPlayer(player, false, false);
    }

    public static boolean wasRunner(Player player) {
        return wasRunner.contains(player.getUniqueId());
    }

    public static void incrementDeaths(Player player) {
        if (!ManHunt.hasDB()) return;
        User user = ManHunt.getUser(player.getUniqueId().toString());
        user.getUserMH().addDeath();
        ManHunt.getDatabase().saveUser(user);
    }

    public static void incrementKills(Player player) {
        if (!ManHunt.hasDB()) return;
        User user = ManHunt.getUser(player.getUniqueId().toString());
        user.getUserMH().addKill();
        ManHunt.getDatabase().saveUser(user);
    }

    public static void incrementWins(Player player, GameTeam team) {
        if (!ManHunt.hasDB()) return;
        User user = ManHunt.getUser(player.getUniqueId().toString());
        if (team == GameTeam.HUNTERS)
            user.getUserMH().addWinHunter();
        else if (team == GameTeam.RUNNERS)
            user.getUserMH().addWinRunner();
        ManHunt.getDatabase().saveUser(user);
    }

    public static void incrementLoses(Player player, GameTeam team) {
        if (!ManHunt.hasDB()) return;
        User user = ManHunt.getUser(player.getUniqueId().toString());
        if (team == GameTeam.HUNTERS)
            user.getUserMH().addLossHunter();
        else if (team == GameTeam.RUNNERS)
            user.getUserMH().addLossRunner();
        ManHunt.getDatabase().saveUser(user);
    }

    public static void rewardPoints(Player player, long points, String reason) {
        if (!ManHunt.hasDB()) return;
        User user = ManHunt.getUser(player.getUniqueId().toString());
        user.addPoints(points, reason);
        ManHunt.getDatabase().saveUser(user);
    }
}
