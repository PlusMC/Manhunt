package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.enums.GameTeam;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EnderEye extends Logic {
    public EnderEye(MHGame game) {
        super(game);
    }

    @Override
    public void update(long tick) {
        MHGame game = getGame();

        if (game.getRunners().getSize() == 0)
            game.postGame(GameTeam.HUNTERS);

        if (tick % 5 != 0) return;
        for (String entry : game.getRunners().getEntries()) {
            Player player = Bukkit.getPlayerExact(entry);
            if (player == null) continue;
            if (player.getInventory().contains(Material.ENDER_EYE)) {
                game.postGame(GameTeam.RUNNERS);
                return;
            }
        }
    }
}
