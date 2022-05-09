package dev.oakleycord.manhunt.game.items;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.plusmc.pluslib.bukkit.managed.PlusItem;

public class StartGameItem implements PlusItem {

    @Override
    public String getID() {
        return "start_game";
    }

    @Override
    public String getName() {
        return "§aStart Game";
    }

    @Override
    public String[] getLore() {
        return new String[]{"§bStarts the game"};
    }

    @Override
    public Material getMaterial() {
        return Material.LIME_CONCRETE;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (!OtherUtil.isManHunt(event.getPlayer().getWorld())) return;
        MHGame game = ManHunt.getGame();
        if (game.getRunners().getSize() == 0) {
            event.getPlayer().sendMessage("§cThere are no runners!");
            return;
        }
        game.startGame();
    }
}