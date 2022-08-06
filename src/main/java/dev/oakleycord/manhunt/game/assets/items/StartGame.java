package dev.oakleycord.manhunt.game.assets.items;

import dev.oakleycord.manhunt.SpeedRuns;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.plusmc.pluslib.bukkit.managed.PlusItem;

import static net.md_5.bungee.api.ChatColor.BOLD;
import static net.md_5.bungee.api.ChatColor.GREEN;

public class StartGame implements PlusItem {
    @Override
    public Material getMaterial() {
        return Material.LIME_WOOL;
    }

    @Override
    public String getName() {
        return GREEN + "" + BOLD + "Start Game";
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public String getID() {
        return "startGame";
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        event.getPlayer().sendMessage(GREEN + "Starting game...");
        SpeedRuns.getGame().startGame();
        event.setCancelled(true);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
