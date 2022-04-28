package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.commands.GameSettings;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.MHDebug;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.events.PlayerEvents;
import dev.oakleycord.manhunt.game.events.PortalEvents;
import dev.oakleycord.manhunt.game.events.WorldEvents;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.plusmc.pluslib.managed.PlusCommand;
import org.plusmc.pluslib.managing.BaseManager;
import org.plusmc.pluslib.managing.GUIManager;
import org.plusmc.pluslib.managing.PlusCommandManager;
import org.plusmc.pluslib.managing.TickingManager;

import java.lang.reflect.Field;
import java.util.List;

//I FUCKING HATE DREAM
public final class ManHunt extends JavaPlugin {

    private static final List<PlusCommand> COMMANDS = List.of(
            new InitGame(),
            new StartGame(),
            new GameSettings(),
            new MHDebug()
    );

    private static final List<Listener> LISTENERS = List.of(
            new PlayerEvents(),
            new WorldEvents(),
            new PortalEvents()
    );

    public static MHGame GAME;


    public static ManHunt getInstance() {
        return JavaPlugin.getPlugin(ManHunt.class);
    }

    private static void registerEnchant(Enchantment enchant) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        for (Listener listener : LISTENERS)
            getServer().getPluginManager().registerEvents(listener, this);

        BaseManager.createManager(PlusCommandManager.class, this);
        BaseManager.createManager(TickingManager.class, this);
        BaseManager.createManager(GUIManager.class, this);

        for (PlusCommand cmd : COMMANDS)
            BaseManager.registerAny(cmd, this);

        registerEnchant(OtherUtil.EMPTY_ENCHANT);
    }

    @Override
    public void onDisable() {
    }
}
