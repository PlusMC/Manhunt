package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.commands.*;
import dev.oakleycord.manhunt.game.events.PlayerEvents;
import dev.oakleycord.manhunt.game.events.PortalEvents;
import dev.oakleycord.manhunt.game.events.WorldEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
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
            new SetGameMode(),
            new StartGame(),
            new MHDebug(),
            new AddModifier(),
            new RemoveModifier()
    );

    private static final List<Listener> LISTENERS = List.of(
            new PlayerEvents(),
            new WorldEvents(),
            new PortalEvents()
    );

    public static MHGame GAME;
    private static PlusCommandManager cmdManager;
    private static TickingManager tickManager;

    public static TickingManager getTickingManager() {
        return tickManager;
    }


    public static ManHunt getInstance() {
        return JavaPlugin.getPlugin(ManHunt.class);
    }

    @Override
    public void onEnable() {
        for (Listener listener : LISTENERS)
            getServer().getPluginManager().registerEvents(listener, this);

        cmdManager = BaseManager.createManager(PlusCommandManager.class, this);
        tickManager = BaseManager.createManager(TickingManager.class, this);
        for (PlusCommand cmd : COMMANDS)
            BaseManager.registerAny(cmd, this);

    }

    @Override
    public void onDisable() {
    }
}
