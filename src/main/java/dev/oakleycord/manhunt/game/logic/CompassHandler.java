package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.ManHuntGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import javax.annotation.Nullable;

public class CompassHandler {
    public ManHuntGame game;

    public CompassHandler(ManHuntGame game) {
        this.game = game;
    }

    public void updateCompass(long tick) {
        if (tick % 10 != 0) return;
        for (Player player : game.getPlayers()) {
            Player nearestPlayer = getNearestPlayer(player);

            if (nearestPlayer == null) continue;

            trackCompass(player, nearestPlayer);

            if (!player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aCompass is pointing to the nearest Runner."));
        }
    }


    private void trackCompass(Player player, Player nearestPlayer) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.COMPASS) continue;

            CompassMeta compassMeta = (CompassMeta) item.getItemMeta();
            if (compassMeta == null) continue;

            Location lodestoneLocation = nearestPlayer.getLocation();

            compassMeta.setLodestoneTracked(false);
            compassMeta.setLodestone(lodestoneLocation);

            item.setItemMeta(compassMeta);
        }
    }

    @Nullable
    private Player getNearestPlayer(Player player) {
        Location playerLocation = player.getLocation();

        double distance = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (!game.getRunners().hasEntry(otherPlayer.getName())) continue;

            if (otherPlayer.equals(player)) continue;

            double otherPlayerDistance = playerLocation.distance(otherPlayer.getLocation());

            if (otherPlayerDistance < distance) {
                distance = otherPlayerDistance;
                nearestPlayer = otherPlayer;
            }
        }

        return nearestPlayer;
    }


}
