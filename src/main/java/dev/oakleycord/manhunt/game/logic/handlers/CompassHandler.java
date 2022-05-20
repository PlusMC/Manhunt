package dev.oakleycord.manhunt.game.logic.handlers;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.logic.Logic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import javax.annotation.Nullable;

public class CompassHandler extends Logic {

    public CompassHandler(AbstractRun game) {
        super(game);
    }

    public void tick(long tick) {
        if (tick % 10 != 0) return;
        for (Player player : getGame().getPlayers()) {
            Player nearestPlayer = getNearestPlayer(player);

            if (nearestPlayer == null) continue;

            trackCompass(player, nearestPlayer);

            if (player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aCompass is pointing to the nearest Runner."));
        }
    }

    @Nullable
    private Player getNearestPlayer(Player player) {
        Location playerLocation = player.getLocation();

        double distance = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (getGame() instanceof ManHunt manHunt && !manHunt.getRunners().hasEntry(otherPlayer.getName()) || otherPlayer.equals(player))
                continue;

            double otherPlayerDistance = playerLocation.distance(otherPlayer.getLocation());

            if (otherPlayerDistance < distance) {
                distance = otherPlayerDistance;
                nearestPlayer = otherPlayer;
            }
        }

        return nearestPlayer;
    }

    private void trackCompass(Player player, Player nearestPlayer) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.COMPASS || item.getItemMeta() == null) continue;

            CompassMeta compassMeta = (CompassMeta) item.getItemMeta();

            Location lodestoneLocation = nearestPlayer.getLocation();

            compassMeta.setLodestoneTracked(false);
            compassMeta.setLodestone(lodestoneLocation);

            item.setItemMeta(compassMeta);
        }
    }


}
