package dev.oakleycord.manhunt.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldEvents implements Listener {
    @EventHandler //memory optimization mainly for void world (no players)
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }
}
