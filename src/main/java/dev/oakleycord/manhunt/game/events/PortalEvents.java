package dev.oakleycord.manhunt.game.events;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalEvents implements Listener {

    @EventHandler
    public void onPortalTeleport(PlayerPortalEvent e) {
        World from = e.getFrom().getWorld();

        if (from == null) return;
        if (!OtherUtil.isManHunt(from)) return;
        MHGame game = ManHunt.GAME;
        if (game == null) return;

        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();

        switch (e.getCause()) {
            case NETHER_PORTAL -> {
                switch (from.getEnvironment()) {
                    case NORMAL -> {
                        fromLoc.multiply(1 / 8D);
                        fromLoc.setWorld(game.getNether());
                        e.setTo(fromLoc);
                    }
                    case NETHER -> {
                        fromLoc.multiply(8.0D);
                        fromLoc.setWorld(game.getOverworld());
                        e.setTo(fromLoc);
                    }
                }
            }

            case END_PORTAL -> {
                switch (from.getEnvironment()) {
                    case NORMAL -> {
                        toLoc.setWorld(game.getEnd());
                        e.setTo(toLoc);
                    }
                    case THE_END -> {
                        toLoc.setWorld(game.getOverworld());
                        e.setTo(toLoc);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        World from = e.getFrom().getWorld();

        if (from == null) return;
        if (!OtherUtil.isManHunt(from)) return;
        MHGame game = ManHunt.GAME;
        if (game == null) return;

        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();

        switch (e.getFrom().getBlock().getType()) {
            case NETHER_PORTAL -> {
                switch (from.getEnvironment()) {
                    case NORMAL -> {
                        fromLoc.multiply(1 / 8D);
                        fromLoc.setWorld(game.getNether());
                        e.setTo(fromLoc);
                    }
                    case NETHER -> {
                        fromLoc.multiply(8.0D);
                        fromLoc.setWorld(game.getOverworld());
                        e.setTo(fromLoc);
                    }
                }
            }

            case END_PORTAL -> {
                switch (from.getEnvironment()) {
                    case NORMAL -> {
                        toLoc.setWorld(game.getEnd());
                        e.setTo(toLoc);
                    }
                    case THE_END -> {
                        toLoc.setWorld(game.getOverworld());
                        e.setTo(toLoc);
                    }
                }
            }
        }
    }
}
