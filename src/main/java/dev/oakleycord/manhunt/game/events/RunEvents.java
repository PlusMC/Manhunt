package dev.oakleycord.manhunt.game.events;


import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class RunEvents implements Listener {
    private final HashMap<Player, Entity> lastDamaged = new HashMap<>();
    private final AbstractRun game;

    public RunEvents(AbstractRun game) {
        this.game = game;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        lastDamaged.put(player, event.getDamager());
        Bukkit.getScheduler().runTaskLater(SpeedRuns.getInstance(), () -> lastDamaged.remove(player), 50);
        if (player.getHealth() - event.getFinalDamage() > 0) return;
        Bukkit.broadcastMessage("§c" + player.getName() + " was killed by " + event.getDamager().getName() + "!");

        if (!(event.getDamager() instanceof Player damager)) return;
        if (game instanceof ManHunt manHunt)
            damager.incrementKills(manHunt.getGameTeam(player));

        damager.rewardPoints(25, "§aKill");
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (game.getState() == GameState.PREGAME) event.setCancelled(true);

        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);

        Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).forEach(item -> {
            if (item.getType() != Material.COMPASS)
                player.getWorld().dropItem(player.getLocation(), item);
        });

        player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel());

        player.getWorld().strikeLightningEffect(player.getLocation());
        player.reset(true, true);

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
        if (game instanceof ManHunt manHunt)
            playerKiller.incrementKills(manHunt.getGameTeam(playerKiller));

        playerKiller.rewardPoints(25, "§aKill");
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

        if (!event.getPlayer().isOutsideOfBorder())
            return;

        event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation().add(0, 1, 0));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        game.onPlayerJoin(event.getPlayer());
    }

    //fix this to make it so that it teleports the player to the mh world
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        game.onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        game.onPlayerQuit(event.getPlayer());
    }
}
