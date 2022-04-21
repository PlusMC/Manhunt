package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockRain extends Logic {
    private final Random random;
    private final List<Material> blocks;

    public BlockRain(MHGame game) {
        super(game);
        this.random = new Random();
        this.blocks = Arrays.stream(Material.values()).filter(material -> material.isBlock() && material != Material.END_PORTAL && material != Material.END_GATEWAY).toList();
    }

    @Override
    public void update(long tick) {
        MHGame game = getGame();

        for (Player player : game.getPlayers()) {
            if (game.getSpectators().hasEntry(player.getName())) continue;

            double max = 20;
            double min = -20;

            double x = player.getLocation().getX() + (random.nextDouble() * (max - min) + min);
            double z = player.getLocation().getZ() + (random.nextDouble() * (max - min) + min);
            double y = player.getLocation().getY() + 30;

            Location loc = new Location(player.getWorld(), x, y, z);
            Material material = blocks.get(random.nextInt(blocks.size()));

            FallingBlock block = player.getWorld().spawnFallingBlock(loc, material.createBlockData());
            block.setHurtEntities(true);
            block.setDropItem(false);
        }
    }
}
