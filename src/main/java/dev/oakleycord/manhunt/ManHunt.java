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
import org.plusmc.pluslib.bukkit.managed.PlusCommand;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.GUIManager;
import org.plusmc.pluslib.bukkit.managing.PlusCommandManager;
import org.plusmc.pluslib.bukkit.managing.TickingManager;
import org.plusmc.pluslib.mongo.DatabaseHandler;
import org.plusmc.pluslib.mongo.User;

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

    private static MHGame game;
    private static DatabaseHandler db;

    public static MHGame getGame() {
        return game;
    }

    public static void createGame() {
        if (!hasGame())
            game = new MHGame();
    }

    public static void removeGame() {
        if (!hasGame()) return;
        game = null;
    }

    public static DatabaseHandler getDatabase() {
        return db;
    }

    public static User getUser(String uuid) {
        return db.getUser(uuid);
    }

    public static boolean hasDB() {
        return db != null;
    }

    public static boolean hasGame() {
        return getGame() != null;
    }

    public static ManHunt getInstance() {
        return JavaPlugin.getPlugin(ManHunt.class);
    }

    private static void registerEnchant() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.set(null, true);
            Enchantment.registerEnchantment(OtherUtil.EMPTY_ENCHANT);
        } catch (Exception e) {
            //ignore
        }
    }

    private static void startDatabase() {
        try {
            db = new DatabaseHandler();
            getInstance().getLogger().info("Database Loaded!");
        } catch (Exception ex) {
            db = null;
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

        startDatabase();

        registerEnchant();
    }

    @Override
    public void onDisable() {
        if (db != null)
            db.shutdown();
    }
}
