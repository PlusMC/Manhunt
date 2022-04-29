package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class KingSlime extends Logic {
    public final static List<FallingBlock> fallingBlocks = new ArrayList<>();
    private final int MAX_SIZE = 14;
    private final long DELAY = 200;
    private final Map<LivingEntity, Long> lastDamage;
    private Slime slime;
    private long startTime = -1;
    private int suckedUp = 0;

    public KingSlime(MHGame game) {
        super(game);
        lastDamage = new HashMap<>();

        spawnSlime();
    }

    public void spawnSlime() {
        Scoreboard board = getGame().getScoreboard();
        Team team = board.getTeam("blueTeam");
        if (team == null)
            team = board.registerNewTeam("blueTeam");
        team.setColor(ChatColor.GREEN);


        slime = getGame().getOverworld().spawn(getGame().getOverworld().getSpawnLocation().add(0, 1, 0), Slime.class);
        team.addEntry(slime.getUniqueId().toString());
        slime.setAI(false);
        slime.setGravity(false);
        slime.setInvulnerable(true);
        slime.setSize(2);
        slime.setGlowing(true);
        slime.setRemoveWhenFarAway(false);
    }

    @Override
    public void unload() {
        slime.remove();
        fallingBlocks.forEach(FallingBlock::remove);
    }

    @Override
    public void tick(long tick) {
        MHGame game = getGame();
        for (Iterator<FallingBlock> iterator = fallingBlocks.iterator(); iterator.hasNext(); ) {
            FallingBlock fallingBlock = iterator.next();
            if (!fallingBlock.isValid() || fallingBlock.getTicksLived() > 200) {
                fallingBlock.remove();
                iterator.remove();
                fallingBlocks.remove(fallingBlock);
            }
        }

        if (!slime.isValid()) {
            spawnSlime();
        }

        if (startTime == -1) {
            startTime = tick;
            game.getPlayers().forEach(player -> {
                player.sendMessage("§6King Slime has appeared! It be released in §e" + (DELAY / 20) + "§6 seconds!");
                player.sendMessage("§6It will suck up everything in it's and grow in size!");
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
            for (int i = 0; i < slime.getSize() * 2; i++) {
                ParticleUtil.helixTicked(slime, Color.fromRGB(15, 200, 15), tick + i);
            }

            suck(tick);
            moveTowards();
        }
    }

    public void moveTowards() {
        Entity nearest = getNearestPlayer(slime.getLocation());
        if (nearest != null && nearest.getLocation().distance(slime.getLocation()) > 15 * slime.getSize()) {
            Vector vector = nearest.getLocation().toVector().subtract(slime.getLocation().toVector());
            vector.normalize();
            vector.multiply(0.25 * nearest.getLocation().distance(slime.getLocation()));
            slime.teleport(slime.getLocation().setDirection(vector).add(vector));
        } else {
            nearest = getNearestLiving(slime, slime.getSize() * 2);
            if (nearest == null) wonder();
            if (nearest == slime) return;
            Vector vector = nearest.getLocation().toVector().subtract(slime.getLocation().toVector());
            vector.normalize();
            vector.multiply(0.0025 * nearest.getLocation().distance(slime.getLocation()));
            slime.teleport(slime.getLocation().setDirection(vector).add(vector));
        }
    }

    public void wonder() {
        Vector vector = new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
        vector.normalize();
        vector.multiply(0.25);
        slime.teleport(slime.getLocation().setDirection(vector).add(vector));
    }

    public void suck(long tick) {
        Location center = slime.getLocation();
        center.setY(slime.getBoundingBox().getCenterY());

        for (Entity entity : slime.getNearbyEntities(slime.getSize(), slime.getSize(), slime.getSize())) {
            if (entity.equals(slime)) continue;
            if (entity instanceof Player player) {
                if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE)
                    continue;
            }

            if (entity instanceof LivingEntity) {
                if (tick - lastDamage.getOrDefault(entity, 0L) < 100) {
                    if (tick % 12 == 0) {
                        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.75f);
                        ParticleUtil.playGrabEffect(entity, center, Color.fromRGB(25, 0, 0));
                    }
                    continue;
                }
            }

            if (slime.getBoundingBox().overlaps(entity.getBoundingBox())) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.damage(slime.getSize(), slime);
                    Vector vector = livingEntity.getLocation().toVector().subtract(center.toVector());
                    vector.normalize();
                    livingEntity.setVelocity(vector);
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
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
            if (!getGame().getRunners().hasEntry(player.getName()) && !getGame().getHunters().hasEntry(player.getName()))
                continue;
            if (d < distance) {
                distance = d;
                nearest = player;
            }
        }
        return nearest;
    }

    private LivingEntity getNearestLiving(Entity entity, double searchRadius) {
        LivingEntity nearest = null;
        double distance = Double.MAX_VALUE;
        for (Entity e : entity.getNearbyEntities(searchRadius, searchRadius, searchRadius)) {
            if (!(e instanceof LivingEntity))
                continue;

            double d = entity.getLocation().distance(e.getLocation());
            if (d < distance) {
                distance = d;
                nearest = (LivingEntity) e;
            }
        }
        return nearest;
    }

    private List<Block> getBlocks(Location start, int radius) {
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
