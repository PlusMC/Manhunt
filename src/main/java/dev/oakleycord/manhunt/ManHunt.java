package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.MHDebug;
import dev.oakleycord.manhunt.game.commands.SetTeam;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.events.PlayerEvents;
import dev.oakleycord.manhunt.game.events.PortalEvents;
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
            new StartGame(),
            new MHDebug()
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
