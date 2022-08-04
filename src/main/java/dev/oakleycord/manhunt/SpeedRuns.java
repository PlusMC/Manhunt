package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.events.LobbyWorldEvents;
import dev.oakleycord.manhunt.events.WorldEvents;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManhuntPrivate;
import dev.oakleycord.manhunt.game.ManhuntPublic;
import dev.oakleycord.manhunt.game.SoloRun;
import dev.oakleycord.manhunt.game.commands.GameSettings;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.plusmc.pluslib.bukkit.handlers.BoardHandler;
import org.plusmc.pluslib.bukkit.handlers.GUIHandler;
import org.plusmc.pluslib.bukkit.handlers.MultiWorldHandler;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.PlusCommandManager;
import org.plusmc.pluslib.bukkit.managing.PlusItemManager;
import org.plusmc.pluslib.bukkit.managing.TickingManager;
import org.plusmc.pluslibcore.mongo.DatabaseHandler;
import org.plusmc.pluslibcore.reflection.bungeebukkit.config.ConfigEntry;
import org.plusmc.pluslibcore.reflection.bungeebukkit.config.InjectConfigBukkit;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public final class SpeedRuns extends JavaPlugin {
    private static InjectConfigBukkit config;

    private static final List<PlusCommand> COMMANDS = List.of(
            new InitGame(),
            new StartGame(),
            new GameSettings()
    );

    private static final List<Listener> LISTENERS = List.of(
            new WorldEvents()
    );

    private AbstractRun game;
    private DatabaseHandler db;
    private BoardHandler boardHandler;
    private MultiWorldHandler worldHandler;
    private @ConfigEntry String gameType;
    private @ConfigEntry String endGameAction;
    public @ConfigEntry String lobbyServer;

    public static void createGame() {
        getInstance().game = switch (getInstance().gameType.toLowerCase()) {
            case "solo" -> new SoloRun();
            case "manhunt-public" -> new ManhuntPublic(config.section("Manhunt-Public"));
            case "manhunt-private" -> new ManhuntPrivate(null, config.section("Manhunt-Private"));
            default -> throw new IllegalArgumentException("Invalid game type");
        };
    }

    public static String getEndGameAction() {
        return getInstance().endGameAction;
    }

    public static String getLobbyServer() {
        return getInstance().lobbyServer;
    }

    public static boolean hasGame() {
        return getGame() != null;
    }

    public static AbstractRun getGame() {
        return getInstance().game;
    }

    public static BoardHandler getBoardHandler() {
        if (getInstance().boardHandler == null)
            getInstance().boardHandler = new BoardHandler(getInstance());
        return getInstance().boardHandler;
    }

    public static SpeedRuns getInstance() {
        return JavaPlugin.getPlugin(SpeedRuns.class);
    }

    public static void removeGame() {
        if (!hasGame()) return;
        getInstance().game = null;
    }

    public static DatabaseHandler getDatabase() {
        return getInstance().db;
    }

    public static boolean dbNotFound() {
        return getDatabase() == null || !getDatabase().isLoaded();
    }

    @Override
    public void onDisable() {
        worldHandler.unregisterAllEvents();
    }

    @Override
    public void onEnable() {
        for (Listener listener : LISTENERS)
            getServer().getPluginManager().registerEvents(listener, this);

        BaseManager.createManager(PlusCommandManager.class, this);
        BaseManager.createManager(PlusItemManager.class, this);
        BaseManager.createManager(TickingManager.class, this);
        new GUIHandler(this);

        worldHandler = new MultiWorldHandler(this, Bukkit.getWorlds().get(0));
        worldHandler.registerEvents(new LobbyWorldEvents());

        saveDefaultConfig();

        config = new InjectConfigBukkit(new File(getDataFolder(), "config.yml"));
        config.inject(this);

        db = DatabaseHandler.getInstance();

        for (PlusCommand cmd : COMMANDS)
            BaseManager.registerAny(cmd, this);


        registerEnchant();
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
