package dev.oakleycord.manhunt.game.events;


import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.HashMap;

import static dev.oakleycord.manhunt.game.util.PlayerUtil.isOutsideOfBorder;
import static dev.oakleycord.manhunt.game.util.PlayerUtil.resetAdvancements;

public class MHEvents implements Listener {
    private final HashMap<Player, Entity> lastDamaged = new HashMap<>();
    private final MHGame game;

    public MHEvents(MHGame game) {
        this.game = game;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        lastDamaged.put(player, event.getDamager());
        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> lastDamaged.remove(player), 50);
        if (player.getHealth() - event.getFinalDamage() > 0) return;
        Bukkit.broadcastMessage("§c" + player.getName() + " was killed by " + event.getDamager().getName() + "!");

        if (!(event.getDamager() instanceof Player damager)) return;
        PlayerUtil.incrementKills(damager, game.getGameTeam(player));
        PlayerUtil.rewardPoints(damager, 25, "§aKill");
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (game.getState() == GameState.PREGAME) event.setCancelled(true);

        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);

        Arrays.stream(player.getInventory().getContents()).forEach(item -> {
            if (item != null)
                player.getWorld().dropItem(player.getLocation(), item);
        });

        player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel());

        player.getWorld().strikeLightningEffect(player.getLocation());
        PlayerUtil.resetPlayer(player, true, true);

        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
                || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) return;

        if (!lastDamaged.containsKey(player)) {
            Bukkit.broadcastMessage("§c" + player.getDisplayName() + " has died!");
            return;
        }

        Entity lastDamager = lastDamaged.get(player);
        Bukkit.broadcastMessage("§c" + player.getName() + " was killed by " + lastDamager.getName() + "!");

        if (!(lastDamager instanceof Player playerKiller)) return;
        PlayerUtil.incrementKills(playerKiller, game.getGameTeam(playerKiller));
        PlayerUtil.rewardPoints(playerKiller, 25, "§aKill");
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (game.getState() != GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (game.getState() != GameState.PREGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getTo().getWorld() == null) return;

        if (game.getState() != GameState.PREGAME) return;

        if (!isOutsideOfBorder(event.getPlayer()))
            return;

        event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation().add(0, 1, 0));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        player.setScoreboard(game.getScoreboard());
        if (!game.hasTeam(player)) {
            resetAdvancements(player);
            PlayerUtil.resetPlayer(player);
            if (game.getState() == GameState.PREGAME)
                game.setTeam(player, GameTeam.HUNTERS);
            else game.setTeam(player, GameTeam.SPECTATORS);
        }
        game.getScoreboardHandler().tick(0);
    }

    //fix this to make it so that it teleports the player to the mh world
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setScoreboard(game.getScoreboard());
        if (!game.hasTeam(player)) {
            resetAdvancements(player);
            PlayerUtil.resetPlayer(player);
            if (game.getState() == GameState.PREGAME)
                game.setTeam(player, GameTeam.HUNTERS);
            else game.setTeam(player, GameTeam.SPECTATORS);
        }
        game.getScoreboardHandler().tick(0);
    }
}
