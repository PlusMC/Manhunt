package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockRain extends Logic {
    public static final List<FallingBlock> blocks = new ArrayList<>();
    private final Random random;
    private final List<Material> materials;
    private final List<Material> disabledMaterials = Arrays.asList(
            Material.AIR,
            Material.END_PORTAL,
            Material.END_GATEWAY,
            Material.NETHER_PORTAL,
            Material.KELP_PLANT,
            Material.KELP,
            Material.SEA_PICKLE,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS
    );

    public BlockRain(MHGame game) {
        super(game);
        this.random = new Random();
        this.materials = Arrays.stream(Material.values()).filter(material -> material.isBlock() && !disabledMaterials.contains(material)).toList();
    }

    @Override
    public void tick(long tick) {
        MHGame game = getGame();

        blocks.removeIf(block -> {
            if (block.getTicksLived() > 250) {
                block.remove();
            }
            return !block.isValid();
        });

        for (Player player : game.getPlayers()) {
            if (game.getSpectators().hasEntry(player.getName())) continue;
            if (blocks.size() >= 100) break;


            double max = 20;
            double min = -20;

            double x = player.getLocation().getX() + (random.nextDouble() * (max - min) + min);
            double z = player.getLocation().getZ() + (random.nextDouble() * (max - min) + min);
            double y = player.getLocation().getY() + 30;

            Location loc = new Location(player.getWorld(), x, y, z);
            loc = loc.getBlock().getLocation().subtract(0.5, 0, 0.5);
            Material material = materials.get(random.nextInt(materials.size()));

            BlockData blockData = material.createBlockData();

            if (blockData instanceof Rotatable rotatable)
                rotatable.setRotation(BlockFace.values()[random.nextInt(4)]);

            if (blockData instanceof Directional directional)
                directional.setFacing(directional.getFaces().toArray(new BlockFace[0])[random.nextInt(directional.getFaces().size())]);

            if (blockData instanceof Waterlogged waterlogged)
                waterlogged.setWaterlogged(false);

            FallingBlock block = player.getWorld().spawnFallingBlock(loc, blockData);
            block.setDropItem(false);
            blocks.add(block);
        }
    }
}
