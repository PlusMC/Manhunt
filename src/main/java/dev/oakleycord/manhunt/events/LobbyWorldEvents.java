package dev.oakleycord.manhunt.events;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
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


public class LobbyWorldEvents implements Listener {
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
        player.reset();
        player.setGameMode(GameMode.SURVIVAL);

        if (SpeedRuns.hasGame()) { //prevent players joining void world during game
            World world = SpeedRuns.getGame().getWorldHandler().getWorldOverworld();
            player.teleport(world.getSpawnLocation().add(0, 1, 0));
            return;
        }

        initGame();
    }

    //fix this to make it so that it teleports the player to the mh world
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        assert Bukkit.getScoreboardManager() != null;
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 1, 0.5));
        player.reset();
        player.setGameMode(GameMode.SURVIVAL);

        if (SpeedRuns.hasGame()) {
            World world = SpeedRuns.getGame().getWorldHandler().getWorldOverworld();
            player.teleport(world.getSpawnLocation().add(0, 1, 0));
            return;
        }

        if (SpeedRuns.getHost() == null)
            SpeedRuns.setHost(SpeedRuns.getDatabase().getUserFromCache(player.getUniqueId()));

        initGame();
    }


    public void initGame() {
        Bukkit.getScheduler().runTaskLater(SpeedRuns.getInstance(), () -> { //timeout game creation so player can join before game initializes
            if (SpeedRuns.hasGame()) return;
            SpeedRuns.createGame();
            AbstractRun game = SpeedRuns.getGame();
            game.pregame();

            Bukkit.getScheduler().runTaskLater(SpeedRuns.getInstance(), () -> { //timeout teleport to avoid errors
                for (Player p : Bukkit.getOnlinePlayers()) {
                    World world = game.getWorldHandler().getWorldOverworld();
                    p.teleport(world.getSpawnLocation().add(0, 1, 0));
                }
            }, 20);
        }, 60);
    }
}
