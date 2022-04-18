package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.game.ManHuntGame;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.MHDebug;
import dev.oakleycord.manhunt.game.commands.SetTeam;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.events.PlayerEvents;
import dev.oakleycord.manhunt.game.events.PortalEvents;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.plusmc.pluslib.managed.PlusCommand;
import org.plusmc.pluslib.managing.BaseManager;
import org.plusmc.pluslib.managing.PlusCommandManager;
import org.plusmc.pluslib.managing.TickingManager;

import java.util.List;

//I FUCKING HATE DREAM
public final class ManHunt extends JavaPlugin {

    private static final List<PlusCommand> COMMANDS = List.of(
            new InitGame(),
            new SetTeam(),
            new StartGame(),
            new MHDebug()
    );
    public static ManHuntGame GAME;
    private static PlusCommandManager cmdManager;
    private static TickingManager tickManager;

    public static TickingManager getTickingManager() {
        return tickManager;
    }


    public static ManHunt getInstance() {
        return JavaPlugin.getPlugin(ManHunt.class);
    }

    public static String formatTime(long time) {
        //format time (ms) to hh:mm:ss
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    public static void resetPlayer(Player player, boolean respawn) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setGlowing(false);
        player.getInventory().clear();
        player.setVelocity(new Vector(0, 0, 0));

        if (respawn) {
            if (player.getBedSpawnLocation() != null)
                player.teleport(player.getBedSpawnLocation());
            else player.teleport(ManHunt.GAME.getOverworld().getSpawnLocation().add(0, 1, 0));
        }

        if (ManHunt.GAME != null) {
            if (ManHunt.GAME.getRunners().hasEntry(player.getName())) {
                ManHunt.GAME.setTeam(player, ManHuntGame.GameTeam.SPECTATORS);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5f);
            }

            if (ManHunt.GAME.getHunters().hasEntry(player.getName()))
                player.getInventory().addItem(new ItemStack(Material.COMPASS));
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new PortalEvents(), this);
        cmdManager = BaseManager.createManager(PlusCommandManager.class, this);
        tickManager = BaseManager.createManager(TickingManager.class, this);
        for (PlusCommand cmd : COMMANDS)
            cmdManager.register(cmd);

    }

    @Override
    public void onDisable() {
    }
}
