package dev.oakleycord.manhunt.game.events;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import dev.oakleycord.manhunt.game.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

import static dev.oakleycord.manhunt.game.util.PlayerUtil.*;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!OtherUtil.isManHunt(event.getEntity().getWorld())) return;
        if (!ManHunt.hasGame()) return;
        MHGame game = ManHunt.getGame();
        if (game.getState() == GameState.PREGAME) event.setCancelled(true);

        if (event.getEntity() instanceof Player player) {
            if (player.getHealth() - event.getFinalDamage() > 0) return;
            event.setCancelled(true);

            boolean isQuickGame = game.getModifiers().contains(Modifier.QUICK_GAME);
            if (isQuickGame)
                player.getInventory().clear();

            Arrays.stream(player.getInventory().getContents()).forEach(item -> {
                if (item != null)
                    player.getWorld().dropItem(player.getLocation(), item);
            });
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel());


            player.getWorld().strikeLightningEffect(player.getLocation());
            Bukkit.broadcastMessage("§c" + player.getDisplayName() + " has died!");
            PlayerUtil.resetPlayer(player, true);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!OtherUtil.isManHunt(event.getBlock().getWorld())) return;
        if (!ManHunt.hasGame()) return;
        if (ManHunt.getGame().getState() != GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!OtherUtil.isManHunt(event.getBlock().getWorld())) return;
        if (!ManHunt.hasGame()) return;
        if (ManHunt.getGame().getState() != GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        assert event.getTo().getWorld() != null;

        if (Bukkit.getWorlds().get(0).equals(event.getTo().getWorld()) && event.getTo().getY() <= 0) {
            Location to = event.getTo();
            Location spawn = event.getPlayer().getWorld().getSpawnLocation();
            to.setX(spawn.getX());
            to.setY(spawn.getY() + 100 + event.getPlayer().getVelocity().getY());
            to.setZ(spawn.getZ());
            event.setTo(to);
            return;
        }

        if (!OtherUtil.isManHunt(event.getTo().getWorld())) return;
        if (!ManHunt.hasGame()) return;
        if (ManHunt.getGame().getState() != GameState.PREGAME) return;

        if (!isOutsideOfBorder(event.getPlayer()))
            return;

        event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation().add(0, 1, 0));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!ManHunt.hasGame()) return;
        if (!OtherUtil.isManHunt(event.getPlayer().getWorld())) {
            assert Bukkit.getScoreboardManager() != null;
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            resetPlayer(player, false);
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }

        MHGame game = ManHunt.getGame();
        player.setScoreboard(game.getScoreboard());
        if (!game.hasTeam(player)) {
            resetAdvancements(player);
            PlayerUtil.resetPlayer(player, false);
            if (game.getState() == GameState.PREGAME)
                game.setTeam(player, GameTeam.HUNTERS);
            else game.setTeam(player, GameTeam.SPECTATORS);
        }
        game.getScoreboardHandler().tick(0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!OtherUtil.isManHunt(event.getPlayer().getWorld()) || !ManHunt.hasGame()) {
            assert Bukkit.getScoreboardManager() != null;
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0, 1, 0));
            PlayerUtil.resetPlayer(player, false);
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }

        MHGame game = ManHunt.getGame();
        player.setScoreboard(game.getScoreboard());
        if (!game.hasTeam(player)) {
            resetAdvancements(player);
            PlayerUtil.resetPlayer(player, false);
            if (game.getState() == GameState.PREGAME)
                game.setTeam(player, GameTeam.HUNTERS);
            else game.setTeam(player, GameTeam.SPECTATORS);
        }
        game.getScoreboardHandler().tick(0);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!OtherUtil.isManHunt(event.getEntity().getWorld())) return;
        if (!ManHunt.hasGame()) return;
        MHGame game = ManHunt.getGame();
        if (game.getState() != GameState.INGAME) return;

        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) return;

        game.setDragonKilled(true);
    }
}
