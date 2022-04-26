package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.*;

public class GodlySlime extends Logic {
    public final static List<FallingBlock> fallingBlocks = new ArrayList<>();
    private final Slime slime;
    private final int MAX_SIZE = 14;
    private final long DELAY = 200;
    private final Map<LivingEntity, Long> lastDamage;
    private long startTime = -1;
    private int suckedUp = 0;

    public GodlySlime(MHGame game) {
        super(game);
        lastDamage = new HashMap<>();

        slime = game.getOverworld().spawn(game.getOverworld().getSpawnLocation(), Slime.class);
        slime.setAI(false);
        slime.setGravity(false);
        slime.setInvulnerable(true);
        slime.setSize(2);
        slime.setRemoveWhenFarAway(false);
    }

    @Override
    public void update(long tick) {
        MHGame game = getGame();
        //get nearby blocks in a radius of 10
        for (Iterator<FallingBlock> iterator = fallingBlocks.iterator(); iterator.hasNext(); ) {
            FallingBlock fallingBlock = iterator.next();
            if (!fallingBlock.isValid() || fallingBlock.getTicksLived() > 200) {
                fallingBlock.remove();
                iterator.remove();
                fallingBlocks.remove(fallingBlock);
            }
        }

        if (startTime == -1) {
            startTime = tick;
            game.getPlayers().forEach(player -> {
                player.sendMessage("§6Godly Slime has appeared! It be released in §e" + (DELAY / 20) + "§6 seconds!");
                player.sendMessage("§6It will suck up everything and grow in size!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
            });
        }

        long timeLeft = DELAY - (tick - startTime);

        if (timeLeft == 0) {
            game.getPlayers().forEach(player -> {
                player.sendMessage("§4The Godly Slime has been released!");
                player.playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 0.25f);
            });
        }

        if (timeLeft <= 0) {
            suck(tick);
            moveTowards();
        }
    }

    public void moveTowards() {
        Player nearest = getNearestPlayer(slime.getLocation());
        if (nearest != null) {
            Vector vector = nearest.getLocation().toVector().subtract(slime.getLocation().toVector());
            vector.normalize();
            vector.multiply(0.0025 * nearest.getLocation().distance(slime.getLocation()));
            slime.teleport(slime.getLocation().setDirection(vector).add(vector));
        }
    }

    public void suck(long tick) {
        Location center = slime.getLocation();
        center.setY(slime.getBoundingBox().getCenterY());

        for (Entity entity : slime.getNearbyEntities(slime.getSize(), slime.getSize(), slime.getSize())) {
            if (entity instanceof Player player) {
                if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE)
                    continue;
            }

            if (entity instanceof LivingEntity) {
                if (tick - lastDamage.getOrDefault(entity, 0L) < 20)
                    continue;
            }

            if (slime.getBoundingBox().overlaps(entity.getBoundingBox())) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.damage(2.5);
                    lastDamage.put(livingEntity, tick);
                    suckedUp++;
                    continue;
                }

                entity.remove();
                continue;
            }

            Vector vector = center.toVector().subtract(entity.getLocation().toVector());
            double multiplier = center.distance(entity.getLocation()) * 10D / slime.getSize();

            Vector velocity = entity.getVelocity();
            velocity.setY(velocity.getY() * 0.5);

            entity.setVelocity(velocity.add(vector.normalize().multiply(1 / multiplier)));

            if (tick % 12 == 0) {
                if (entity instanceof LivingEntity) {
                    ParticleUtil.playGrabEffect(entity, center, Color.fromRGB(0, 255, 0));
                    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 0.25f);
                }
            }
        }


        List<Block> blocks = getBlocks(center, slime.getSize());
        for (Block block : blocks) {
            double chance = ((double) slime.getSize() / 50) / center.distance(block.getLocation());
            if (chance > Math.random()) {
                BlockData data = block.getBlockData().clone();
                block.setType(Material.AIR);
                if (isBlockExposed(block) && fallingBlocks.size() < 50 && Math.random() < 0.75) {
                    FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), data);
                    fallingBlocks.add(fallingBlock);
                    fallingBlock.setDropItem(false);
                    fallingBlock.getWorld().playSound(fallingBlock.getLocation(), fallingBlock.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
                }
                suckedUp++;

            }
        }

        if (suckedUp > Math.pow(2, slime.getSize())) {
            if (slime.getSize() <= MAX_SIZE)
                slime.setSize(slime.getSize() + 1);
            suckedUp = 0;
        }
    }


    private boolean isBlockExposed(Block block) {
        return block.getRelative(BlockFace.UP).getType() == Material.AIR || block.getRelative(BlockFace.DOWN).getType() == Material.AIR || block.getRelative(BlockFace.NORTH).getType() == Material.AIR || block.getRelative(BlockFace.SOUTH).getType() == Material.AIR || block.getRelative(BlockFace.EAST).getType() == Material.AIR || block.getRelative(BlockFace.WEST).getType() == Material.AIR;
    }


    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double distance = Double.MAX_VALUE;
        for (Player player : getGame().getPlayers()) {
            double d = location.distance(player.getLocation());
            if (!getGame().getRunners().hasEntry(player.getName()) && !getGame().getRunners().hasEntry(player.getName()))
                continue;
            if (d < distance) {
                distance = d;
                nearest = player;
            }
        }
        return nearest;
    }

    public List<Block> getBlocks(Location start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++) {
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    if (loc.distance(start) <= radius && loc.getBlock().getType() != Material.AIR) {
                        blocks.add(loc.getBlock());
                    }
                }
            }
        }
        return blocks;
    }
}
