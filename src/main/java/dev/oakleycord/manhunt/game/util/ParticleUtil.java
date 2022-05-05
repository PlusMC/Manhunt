package dev.oakleycord.manhunt.game.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ParticleUtil {

    private ParticleUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static void playGrabEffect(Entity entity, Location target, Color color) {
        double radius = entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ();
        radius = radius / 2;
        double centerX = entity.getBoundingBox().getCenterX();
        double y = entity.getBoundingBox().getCenterY();
        double centerZ = entity.getBoundingBox().getCenterZ();
        Location closest = null;
        for (double i = 0; i < 360; i += 1) {
            double x = radius * Math.cos(i);
            double z = radius * Math.sin(i);
            Location loc = new Location(entity.getWorld(), centerX + x, y, centerZ + z);
            if (closest == null || loc.distance(target) < closest.distance(target)) {
                closest = loc;
            }
            entity.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, new Particle.DustOptions(color, 1));
        }

        drawLine(closest, target, 0.1, color);
    }


    //random guy from spigot
    public static void drawLine(Location point1, Location point2, double space, Color color) {
        Validate.notNull(point1, "Point1 cannot be null");
        Validate.notNull(point1.getWorld(), "World cannot be null");
        World world = point1.getWorld();
        Validate.isTrue(point2.getWorld().equals(world), "Lines cannot be in different worlds!");
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            world.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 1, new Particle.DustOptions(color, 1));
            length += space;
        }
    }

    public static void helix(Entity entity, Color color) {
        double startingHeight = entity.getBoundingBox().getMinY();
        double maxHeight = entity.getBoundingBox().getMaxY();
        double radius = entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ();
        radius = radius / 2;
        double centerX = entity.getBoundingBox().getCenterX();
        double centerZ = entity.getBoundingBox().getCenterZ();
        for (double i = startingHeight; i < maxHeight; i += entity.getHeight() / 100) {
            double x = radius * Math.cos(i * 5);
            double z = radius * Math.sin(i * 5);
            Location loc = new Location(entity.getWorld(), centerX + x, i, centerZ + z);
            entity.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, new Particle.DustOptions(color, 1));
        }
    }

    public static void helixTicked(Entity entity, Color color, long tick) {
        double startingHeight = entity.getBoundingBox().getMinY();
        double maxHeight = entity.getBoundingBox().getMaxY();
        double radius = entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ();
        radius = radius / 2;
        double centerX = entity.getBoundingBox().getCenterX();
        double centerZ = entity.getBoundingBox().getCenterZ();


        double height = startingHeight + ((entity.getHeight() / maxHeight) * (tick % maxHeight));
        double x = radius * Math.cos(tick * 5D);
        double z = radius * Math.sin(tick * 5D);
        Location loc = new Location(entity.getWorld(), centerX + x, height, centerZ + z);
        entity.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, new Particle.DustOptions(color, 1));
    }
}
