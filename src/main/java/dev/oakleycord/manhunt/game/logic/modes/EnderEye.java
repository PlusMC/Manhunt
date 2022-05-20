package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EnderEye extends Logic {
    public EnderEye(AbstractRun game) {
        super(game);
    }

    @Override
    public void tick(long tick) {
        AbstractRun game = getGame();

        if (tick % 5 != 0) return;
        for (Player player : game.getPlayers()) {
            if (player.getInventory().contains(Material.ENDER_EYE)) {
                if (game instanceof ManHunt manHunt) {
                    if (manHunt.getGameTeam(player) == ManHunt.MHTeam.RUNNERS)
                        manHunt.win(ManHunt.MHTeam.RUNNERS);

                } else {
                    game.postGame();
                }
                return;
            }
        }
    }
}
