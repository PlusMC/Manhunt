package dev.oakleycord.manhunt.game.events;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.ManHuntGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.Iterator;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!ManHuntGame.isManHuntWorld(event.getEntity().getWorld())) return;
        if (ManHunt.GAME == null) return;
        if (ManHunt.GAME.getState() == ManHuntGame.GameState.PREGAME) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player player) {
            if (player.getHealth() - event.getFinalDamage() > 0) return;
            event.setCancelled(true);

            Arrays.stream(player.getInventory().getContents()).forEach(item -> {
                if (item != null)
                    player.getWorld().dropItem(player.getLocation(), item);
            });
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel());

            player.getWorld().strikeLightningEffect(player.getLocation());
            Bukkit.broadcastMessage("§c" + player.getDisplayName() + " has died!");
            ManHunt.resetPlayer(player, true);
        }
    }

    private void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String key : progress.getAwardedCriteria())
                progress.revokeCriteria(key);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ManHuntGame.isManHuntWorld(event.getBlock().getWorld())) return;
        if (ManHunt.GAME == null) return;
        if (ManHunt.GAME.getState() != ManHuntGame.GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ManHuntGame.isManHuntWorld(event.getBlock().getWorld())) return;
        if (ManHunt.GAME == null) return;
        if (ManHunt.GAME.getState() != ManHuntGame.GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        assert event.getTo().getWorld() != null;
        if (!ManHuntGame.isManHuntWorld(event.getTo().getWorld())) return;
        if (ManHunt.GAME == null) return;
        if (ManHunt.GAME.getState() != ManHuntGame.GameState.PREGAME) return;

        if (!isOutsideOfBorder(event.getPlayer()))
            return;

        event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation().add(0, 1, 0));
    }

    //thanks some guy on spigot
    private boolean isOutsideOfBorder(Player p) {
        Location loc = p.getLocation();
        WorldBorder border = p.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (ManHunt.GAME == null) return;
        if (!ManHuntGame.isManHuntWorld(event.getPlayer().getWorld())) {
            assert Bukkit.getScoreboardManager() != null;
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }

        player.setScoreboard(ManHunt.GAME.getScoreboard());
        if (!ManHunt.GAME.inTeam(player)) {
            resetAdvancements(player);
            ManHunt.resetPlayer(player, false);
            if (ManHunt.GAME.getState() == ManHuntGame.GameState.PREGAME)
                ManHunt.GAME.setTeam(player, ManHuntGame.GameTeam.HUNTERS);
            else ManHunt.GAME.setTeam(player, ManHuntGame.GameTeam.SPECTATORS);
        }
        ManHunt.GAME.getScoreboardHandler().updateScoreboard(0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (ManHunt.GAME == null) return;
        if (!ManHuntGame.isManHuntWorld(event.getPlayer().getWorld())) {
            assert Bukkit.getScoreboardManager() != null;
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }

        player.setScoreboard(ManHunt.GAME.getScoreboard());
        if (!ManHunt.GAME.inTeam(player)) {
            resetAdvancements(player);
            ManHunt.resetPlayer(player, false);
            if (ManHunt.GAME.getState() == ManHuntGame.GameState.PREGAME)
                ManHunt.GAME.setTeam(player, ManHuntGame.GameTeam.HUNTERS);
            else ManHunt.GAME.setTeam(player, ManHuntGame.GameTeam.SPECTATORS);
        }
        ManHunt.GAME.getScoreboardHandler().updateScoreboard(0);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!ManHuntGame.isManHuntWorld(event.getEntity().getWorld())) return;
        if (ManHunt.GAME == null) return;
        if (ManHunt.GAME.getState() != ManHuntGame.GameState.INGAME) return;

        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) return;

        ManHunt.GAME.postGame(ManHuntGame.GameTeam.RUNNERS);
    }


}
