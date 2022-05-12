package dev.oakleycord.manhunt.events;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static dev.oakleycord.manhunt.game.util.PlayerUtil.resetPlayer;

public class VoidWorldEvents implements Listener {
    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        event.setCancelled(true);
        if (event.getEntity() instanceof Player player && player.isOp())
            event.setCancelled(false);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
        if (event.getPlayer().isOp()) event.setCancelled(false);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
        if (event.getPlayer().isOp()) event.setCancelled(false);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        assert Bukkit.getScoreboardManager() != null;
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
    }

    //fix this to make it so that it teleports the player to the mh world
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        assert Bukkit.getScoreboardManager() != null;
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 1, 0.5));
        PlayerUtil.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);

        if (ManHunt.hasGame()) return;
        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> {
            if (ManHunt.hasGame()) return;
            ManHunt.createGame();
            MHGame game = ManHunt.getGame();
            game.pregame();
            for (Player p : Bukkit.getOnlinePlayers()) {
                World world = game.getWorldHandler().getWorldOverworld();
                world.getSpawnLocation().getChunk().load();
                p.teleport(world.getSpawnLocation().add(0, 1, 0));
            }
        }, 20);
    }
}
