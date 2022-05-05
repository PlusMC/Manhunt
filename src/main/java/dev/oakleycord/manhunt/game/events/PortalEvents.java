package dev.oakleycord.manhunt.game.events;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.END_PORTAL;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;

public class PortalEvents implements Listener {

    //surprisingly, this worked first try
    @EventHandler
    public void onPortalTeleport(PlayerPortalEvent e) {
        World from = e.getFrom().getWorld();

        if (from == null) return;
        if (!OtherUtil.isManHunt(from)) return;
        MHGame game = ManHunt.getGame();
        if (game == null) return;

        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        if (toLoc == null) return;

        if (e.getCause() == NETHER_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                fromLoc.multiply(1 / 8D);
                fromLoc.setWorld(game.getNether());
                e.setTo(fromLoc);
            } else if (from.getEnvironment() == World.Environment.NETHER) {
                fromLoc.multiply(8.0D);
                fromLoc.setWorld(game.getOverworld());
                e.setTo(fromLoc);
            }
        } else if (e.getCause() == END_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                toLoc.setWorld(game.getEnd());
                e.setTo(toLoc);
            } else if (from.getEnvironment() == World.Environment.THE_END) {
                toLoc.setWorld(game.getOverworld());
                e.setTo(toLoc);
            }
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        World from = e.getFrom().getWorld();

        if (from == null) return;
        if (!OtherUtil.isManHunt(from)) return;
        MHGame game = ManHunt.getGame();
        if (game == null) return;

        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        if (toLoc == null) return;

        if (e.getFrom().getBlock().getType() == Material.NETHER_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                fromLoc.multiply(1 / 8D);
                fromLoc.setWorld(game.getNether());
                e.setTo(fromLoc);
            } else if (from.getEnvironment() == World.Environment.NETHER) {
                fromLoc.multiply(8.0D);
                fromLoc.setWorld(game.getOverworld());
                e.setTo(fromLoc);
            }
        } else if (e.getFrom().getBlock().getType() == Material.END_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                toLoc.setWorld(game.getEnd());
                e.setTo(toLoc);
            } else if (from.getEnvironment() == World.Environment.THE_END) {
                toLoc.setWorld(game.getOverworld());
                e.setTo(toLoc);
            }
        }
    }
}
