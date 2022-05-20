package dev.oakleycord.manhunt;

import dev.oakleycord.manhunt.events.VoidWorldEvents;
import dev.oakleycord.manhunt.events.WorldEvents;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.SoloRun;
import dev.oakleycord.manhunt.game.commands.GameSettings;
import dev.oakleycord.manhunt.game.commands.InitGame;
import dev.oakleycord.manhunt.game.commands.StartGame;
import dev.oakleycord.manhunt.game.items.GameSettingsItem;
import dev.oakleycord.manhunt.game.items.StartGameItem;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.plusmc.pluslib.bukkit.handlers.BoardHandler;
import org.plusmc.pluslib.bukkit.handlers.GUIHandler;
import org.plusmc.pluslib.bukkit.handlers.MultiWorldHandler;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;
import org.plusmc.pluslib.bukkit.managed.PlusItem;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.PlusCommandManager;
import org.plusmc.pluslib.bukkit.managing.PlusItemManager;
import org.plusmc.pluslib.bukkit.managing.TickingManager;
import org.plusmc.pluslib.mongo.DatabaseHandler;
import org.plusmc.pluslib.reflection.config.ConfigSpigot;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

//I FUCKING HATE DREAM
public final class SpeedRuns extends JavaPlugin {

    private static final List<PlusCommand> COMMANDS = List.of(
            new InitGame(),
            new StartGame(),
            new GameSettings()
    );

    private static final List<Listener> LISTENERS = List.of(
            new WorldEvents()
    );

    private static final List<PlusItem> ITEMS = List.of(
            new GameSettingsItem(),
            new StartGameItem()
    );

    private static AbstractRun game;
    private static DatabaseHandler db;
    private static BoardHandler boardHandler;
    private static MultiWorldHandler worldHandler;
    private static SpeedRunConfig config;

    public static void createGame() {
        game = switch (config.gameType().toLowerCase()) {
            case "solo" -> new SoloRun();
            case "manhunt" -> new ManHunt();
            default -> throw new IllegalArgumentException("Invalid game type");
        };
    }

    public static boolean hasGame() {
        return getGame() != null;
    }

    public static AbstractRun getGame() {
        return game;
    }

    public static BoardHandler getBoardHandler() {
        if (boardHandler == null)
            boardHandler = new BoardHandler(getInstance());
        return boardHandler;
    }

    public static SpeedRuns getInstance() {
        return JavaPlugin.getPlugin(SpeedRuns.class);
    }

    public static void removeGame() {
        if (!hasGame()) return;
        game = null;
    }

    public static DatabaseHandler getDatabase() {
        return db;
    }

    public static boolean dbNotFound() {
        return db == null || !db.isLoaded();
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
        worldHandler.registerEvents(new VoidWorldEvents());

        saveDefaultConfig();
        db = DatabaseHandler.getInstance();
        ConfigSpigot configYaml = new ConfigSpigot(new File(getDataFolder(), "config.yml"));
        config = configYaml.read(SpeedRunConfig.class);


        for (PlusCommand cmd : COMMANDS)
            BaseManager.registerAny(cmd, this);

        for (PlusItem item : ITEMS)
            BaseManager.registerAny(item, this);


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

    private record SpeedRunConfig(
            String gameType
    ) {

    }
}
