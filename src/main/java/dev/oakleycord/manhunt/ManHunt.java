package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.commands.GameSettings;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.MHDebug;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.events.PlayerEvents;
import dev.oakleycord.manhunt.game.events.WorldEvents;
import dev.oakleycord.manhunt.game.items.GameSettingsItem;
import dev.oakleycord.manhunt.game.items.StartGameItem;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.plusmc.pluslib.bukkit.handlers.BoardHandler;
import org.plusmc.pluslib.bukkit.handlers.GUIHandler;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;
import org.plusmc.pluslib.bukkit.managed.PlusItem;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.PlusCommandManager;
import org.plusmc.pluslib.bukkit.managing.PlusItemManager;
import org.plusmc.pluslib.bukkit.managing.TickingManager;
import org.plusmc.pluslib.mongo.DatabaseHandler;

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
            new WorldEvents()
    );

    private static final List<PlusItem> ITEMS = List.of(
            new GameSettingsItem(),
            new StartGameItem()
    );

    private static MHGame game;
    private static DatabaseHandler db;
    private static BoardHandler boardHandler;

    public static void createGame() {
        if (!hasGame())
            game = new MHGame();
    }

    public static boolean hasGame() {
        return getGame() != null;
    }

    public static MHGame getGame() {
        return game;
    }

    public static BoardHandler getBoardHandler() {
        if (boardHandler == null)
            boardHandler = new BoardHandler(getInstance());
        return boardHandler;
    }

    public static ManHunt getInstance() {
        return JavaPlugin.getPlugin(ManHunt.class);
    }

    public static void removeGame() {
        if (!hasGame()) return;
        game = null;
    }

    public static DatabaseHandler getDatabase() {
        return db;
    }

    public static boolean hasDB() {
        return db != null && db.isLoaded();
    }

    @Override
    public void onEnable() {
        for (Listener listener : LISTENERS)
            getServer().getPluginManager().registerEvents(listener, this);

        BaseManager.createManager(PlusCommandManager.class, this);
        BaseManager.createManager(PlusItemManager.class, this);
        BaseManager.createManager(TickingManager.class, this);
        new GUIHandler(this);


        for (PlusCommand cmd : COMMANDS)
            BaseManager.registerAny(cmd, this);

        for (PlusItem item : ITEMS)
            BaseManager.registerAny(item, this);

        startDatabase();

        registerEnchant();
    }

    private static void startDatabase() {
        DatabaseHandler.createInstance();
        db = DatabaseHandler.getInstance();
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
}
