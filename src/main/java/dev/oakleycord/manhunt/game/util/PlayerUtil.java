package dev.oakleycord.manhunt.game.util;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.enums.GameTeam;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class PlayerUtil {

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

    public static void resetPlayer(Player player, boolean respawn) {
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

        if (respawn) {
            if (player.getBedSpawnLocation() != null)
                player.teleport(player.getBedSpawnLocation());
            else player.teleport(ManHunt.GAME.getOverworld().getSpawnLocation().add(0, 1, 0));
        }

        if (ManHunt.GAME != null) {
            if (ManHunt.GAME.getRunners().hasEntry(player.getName())) {
                ManHunt.GAME.setTeam(player, GameTeam.SPECTATORS);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
            }

            if (ManHunt.GAME.getHunters().hasEntry(player.getName()))
                player.getInventory().addItem(new ItemStack(Material.COMPASS));
        }
    }
}