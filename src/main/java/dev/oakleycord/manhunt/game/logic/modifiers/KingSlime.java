package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class KingSlime extends Logic {
    private static final int MAX_SIZE = 14;
    private static final long DELAY = 200;
    private final List<FallingBlock> fallingBlocks;
    private final Map<Entity, Long> lastDamage;
    private final BlockListener blockListener;
    private Slime slime;
    private long startTime = -1;
    private int suckedUp = 0;

    public KingSlime(MHGame game) {
        super(game);
        lastDamage = new HashMap<>();
        blockListener = new BlockListener();
        fallingBlocks = new ArrayList<>();
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

        if (slime.isDead()) {
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
                player.sendMessage("§4King Slime has been released!");
                player.playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 0.25f);
            });
        }

        if (timeLeft <= 0) {
            for (int i = 0; i < slime.getSize() * 2; i++)
                ParticleUtil.helixTicked(slime, Color.fromRGB(15, 200, 15), tick + i);


            suck(tick);
            moveTowards();
        }
    }

    public void spawnSlime() {
        MHGame game = getGame();
        Scoreboard board = game.getScoreboard();
        Team team = board.getTeam("blueTeam");
        if (team == null)
            team = board.registerNewTeam("blueTeam");
        team.setColor(ChatColor.GREEN);

        int size = 3;
        Location spawn = game.getWorldHandler().getWorldOverworld().getSpawnLocation().add(0, 1, 0);
        if (slime != null) {
            size = slime.getSize();
            spawn = slime.getLocation();
            slime.remove();
        }


        slime = game.getWorldHandler().getWorldOverworld().spawn(spawn, Slime.class);
        team.addEntry(slime.getUniqueId().toString());
        slime.setAI(false);
        slime.setGravity(false);
        slime.setInvulnerable(true);
        slime.setSize(size);
        slime.setGlowing(true);
        slime.setRemoveWhenFarAway(false);
    }

    private void suck(long tick) {
        suckBlocks();
        suckEntities(tick);

        if (suckedUp > Math.pow(2, slime.getSize())) {
            if (slime.getSize() <= MAX_SIZE)
                slime.setSize(slime.getSize() + 1);
            suckedUp = 0;
        }
    }

    public void moveTowards() {
        Entity nearest = getNearestPlayer(slime.getLocation());
        if (nearest != null) {
            Vector vector = nearest.getLocation().toVector().subtract(slime.getLocation().toVector());
            vector.normalize();
            vector.multiply(0.0035 * nearest.getLocation().distance(slime.getLocation()));
            Location loc = slime.getLocation().setDirection(vector).add(vector);
            loc.getChunk().load(true);
            slime.teleport(loc);
            Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> {
                if (!slime.getLocation().getChunk().equals(loc.getChunk()))
                    System.out.println(loc.getChunk().isLoaded());
            }, 10);
        }
    }

    private void suckBlocks() {
        Location center = getCenter();
        for (Block block : getBlocks(center, slime.getSize())) {
            double chance = ((double) slime.getSize() / 50) / center.distance(block.getLocation());
            if (chance < Math.random()) continue;
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

    private void suckEntities(long tick) {
        Location center = getCenter();

        for (Entity entity : slime.getNearbyEntities(slime.getSize(), slime.getSize(), slime.getSize())) {
            if (entity.equals(slime) || entity instanceof Player player && (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE))
                continue;

            if (slime.getBoundingBox().overlaps(entity.getBoundingBox()))
                damageEntity(entity, tick);

            if (entity instanceof LivingEntity living)
                playGrabEffect(living, tick);


            //use last damage as a pull cooldown
            if (tick - lastDamage.getOrDefault(entity, 0L) > 100)
                pullEntity(entity, center);
        }
    }

    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double distance = Double.MAX_VALUE;
        MHGame game = getGame();
        for (Player player : game.getPlayers()) {
            double d = location.distance(player.getLocation());
            if (!game.getRunners().hasEntry(player.getName()) && !game.getHunters().hasEntry(player.getName()))
                continue;
            if (d < distance) {
                distance = d;
                nearest = player;
            }
        }
        return nearest;
    }

    private Location getCenter() {
        Location center = slime.getLocation();
        center.setY(slime.getBoundingBox().getCenterY());
        return center;
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

    private boolean isBlockExposed(Block block) {
        return block.getRelative(BlockFace.UP).getType() == Material.AIR || block.getRelative(BlockFace.DOWN).getType() == Material.AIR || block.getRelative(BlockFace.NORTH).getType() == Material.AIR || block.getRelative(BlockFace.SOUTH).getType() == Material.AIR || block.getRelative(BlockFace.EAST).getType() == Material.AIR || block.getRelative(BlockFace.WEST).getType() == Material.AIR;
    }

    private void damageEntity(Entity entity, long tick) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(slime.getSize(), slime);
            Vector vector = livingEntity.getLocation().toVector().subtract(getCenter().toVector());
            vector.normalize();
            livingEntity.setVelocity(vector);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
            lastDamage.put(livingEntity, tick);
            suckedUp++;
            return;
        }

        entity.remove();
    }

    private void playGrabEffect(LivingEntity entity, long tick) {
        if (tick % 12 != 0) return;
        Location center = getCenter();
        if (tick - lastDamage.getOrDefault(entity, 0L) < 100) {
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.75f);
            ParticleUtil.playGrabEffect(entity, center, Color.fromRGB(25, 0, 0));
            return;
        }

        ParticleUtil.playGrabEffect(entity, center, Color.fromRGB(0, 255, 0));
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 0.25f);

    }

    private void pullEntity(Entity entity, Location location) {
        Vector vector = location.toVector().subtract(entity.getLocation().toVector());
        double multiplier = location.distance(entity.getLocation()) * 10D / slime.getSize();

        Vector velocity = entity.getVelocity();
        velocity.setY(velocity.getY() * 0.5);

        entity.setVelocity(velocity.add(vector.normalize().multiply(1 / multiplier)));
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

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(blockListener, ManHunt.getInstance());
        spawnSlime();
    }

    @Override
    public void unload() {
        slime.remove();
        fallingBlocks.forEach(FallingBlock::remove);
        HandlerList.unregisterAll(blockListener);
    }

    private class BlockListener implements Listener {
        @EventHandler
        public void onBlockLand(EntityChangeBlockEvent event) {
            if (!(event.getEntity() instanceof FallingBlock blockEntity))
                return;
            if (!fallingBlocks.contains(blockEntity)) return;
            event.setCancelled(true);
            fallingBlocks.remove(blockEntity);
        }
    }

}
