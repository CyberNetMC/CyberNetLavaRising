package net.cybercake.lavarisingplugin;

import me.lucko.commodore.CommodoreProvider;
import net.cybercake.cyberapi.*;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.exceptions.BetterStackTraces;
import net.cybercake.cyberapi.generalutils.FileUtils;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.cyberapi.items.GUI;
import net.cybercake.lavarisingplugin.commands.*;
import net.cybercake.lavarisingplugin.commands.maincommand.CommandListeners;
import net.cybercake.lavarisingplugin.commands.maincommand.CommandManager;
import net.cybercake.lavarisingplugin.disabled.DisabledUntil;
import net.cybercake.lavarisingplugin.disabled.Listeners;
import net.cybercake.lavarisingplugin.listeners.*;
import net.cybercake.lavarisingplugin.runnables.TabList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public final class Main extends Spigot {

    public static Main instance;

    public static final boolean useCommodore = true;
    public static boolean disabled = false;
    public static int disabledUntilRestart = -1;
    public static long unixStarted = 0;
    public static int yLevelSphere = 100;
    public static int playersToStart = 3;

    public static World world;
    public static ItemStack lobbyItem;

    public static ArrayList<Material> blacklistedItems = new ArrayList<>();
    public static ArrayList<Material> instaBreak = new ArrayList<>();

    public static Main get() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        long ms = System.currentTimeMillis();
        CyberAPI.initSpigot(this, true);
        State.set(State.Game.SERVER_STARTING);

        lobbyItem = GUI.item(Material.BARRIER, 1, true, "&6Back to Lobby &a(Right Click)", "&7Return to the main lobby!");

        if(!YamlUtils.customYmlExist("data")) {
            YamlUtils.setCustomYml("data", "lastSeed", 0);
            YamlUtils.setCustomYml("data", "amount", 0);
        }
        String newWorldName = "lavarising-" + YamlUtils.getCustomYmlInt("data", "amount");
        print("Creating world with name '" + newWorldName + "'...");
        WorldCreator creator = new WorldCreator(newWorldName);
        creator.type(WorldType.valueOf(Main.getMainConfig().getString("world.type")));
        long providedSeed = Main.getMainConfig().getLong("world.seed");
        if(providedSeed != 0 && YamlUtils.getCustomYmlLong("data", "lastSeed") != providedSeed) creator.seed(providedSeed);
        creator.environment(World.Environment.valueOf(Main.getMainConfig().getString("world.environment")));
        creator.createWorld();
        world = Bukkit.getWorld(newWorldName);

        print("Checking if the world has failed to regenerate properly...");
        if(YamlUtils.getCustomYmlLong("data", "lastSeed") == world.getSeed()) {
            disabled = true;
            disabledUntilRestart = 16;
            regenFailed();

            print("Loading listeners attributed to the plugin...");
            registerListener(new Listeners());

            print("Loading tasks and runnables attributed to the plugin...");
            registerRunnable(new DisabledUntil(), 20L);

            Log.info(ChatColor.GREEN + "Successfully enabled CyberNetLavaRising [v" + getDescription().getVersion() + "] in " + (System.currentTimeMillis()-ms) + "ms");
            return;
        }

        print("Loading blacklisted items...");
        ArrayList<String> blacklistedStrings = new ArrayList<>(Main.getMainConfig().getStringList("blacklistedPlaceBlocks"));
        for(String blacklisted : blacklistedStrings) {
            if(blacklisted.startsWith("any:")) {
                blacklisted = blacklisted.substring(4);
                Log.warn("... blacklisting any item that contains '" + blacklisted + "'...");
                for(Material material : Material.values()) {
                    if(!material.toString().contains(blacklisted)) continue;

                    blacklistedItems.add(material);
                    Log.warn("... blacklisting any item that contains '" + blacklisted + "' -> " + material + "...");
                }
            }else if(!blacklisted.startsWith("any:")) {
                blacklistedItems.add(Material.valueOf(blacklisted));
                Log.warn("... blacklisted item " + Material.valueOf(blacklisted));
            }
        }

        print("Loading instabreak items...");
        ArrayList<String> instaBreakStrings = new ArrayList<>(Main.getMainConfig().getStringList("instabreak"));
        for(String insta : instaBreakStrings) {
            if(insta.startsWith("any:")) {
                insta = insta.substring(4);
                Log.warn("... allowing instabreak to any item that contains '" + insta + "'...");
                for(Material material : Material.values()) {
                    if(!material.toString().contains(insta)) continue;

                    instaBreak.add(material);
                    Log.warn("... allowing instabreak to any item that contains '" + insta + "' -> " + material + "...");
                }
            }else if(!insta.startsWith("any:")) {
                instaBreak.add(Material.valueOf(insta));
                Log.warn("... allowing instabreak " + Material.valueOf(insta));
            }
        }

        print("Loading the configuration...");
        saveDefaultConfig();
        reloadConfig();
        playersToStart = Main.getMainConfig().getInt("minToStart");
        PlayerPVP.pvpEnablesYLevel = Main.getMainConfig().getInt("pvp.yLevelEnables");

        print("Loading listeners attributed to the plugin...");
        registerListener(new Scoreboard());
        registerListener(new JoinLeaveEvent());
        registerListener(new WorldGuard());
        registerListener(new CommandListeners());
        registerListener(new MoveEvent());
        registerListener(new ReturnToLobby());
        registerListener(new PlayerPVP());
        registerListener(new DeathEvent());

        print("Loading commands and tab completers attributed to the plugin...");
        registerCommandAndTab("worlds", new net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager(), useCommodore);
        registerCommandAndTab("getstate", new GetState(), useCommodore);
        registerCommandAndTab("cybernetlavarising", new CommandManager(), useCommodore);
        registerCommandAndTab("start", new StartCMD(), useCommodore);
        registerCommandAndTab("seed", new Seed(), useCommodore);
        registerCommandAndTab("lava", new Lava(), useCommodore);
        registerCommandAndTab("pvp", new PvP(), useCommodore);

        print("Loading tasks and runnables attributed to the plugin...");
        registerRunnable(new Scoreboard(), 1L);
        registerRunnable(new TabList(), 10L);

        print("Setting up basic world settings...");
        world.setSpawnLocation(new Location(world, 0.5, 100, 0.5, 0, 0));
        world.setTime(6000);
        world.setDifficulty(Difficulty.PEACEFUL);

        print("Setting up WorldBorder...");
        WorldBorder wb = world.getWorldBorder();
        wb.setSize(Main.getMainConfig().getDouble("worldBorder"));
        wb.setCenter(0.5, 0.5);
        wb.setDamageAmount(10);
        wb.setDamageBuffer(1);
        wb.setWarningDistance(0);

        print("Setting up gamerules...");
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.SPAWN_RADIUS, 0);

        print("Generating a sphere...");
        yLevelSphere = Spigot.getTopBlock(new Location(world, 0.0, 0.0, 0.0)).getBlockY()+32;
        if(world.getEnvironment().equals(World.Environment.NETHER)) {
            yLevelSphere = Spigot.getTopBlock(new Location(world, 0.0, 0.0, 0.0), 124).getBlockY();
        }
        command("/world " + world.getName()); // sphere
        command("/pos1 0," + yLevelSphere + ",0"); // sphere
        command("/pos2 0," + yLevelSphere + ",0"); // sphere
        command("/sphere air 9"); // sphere
        command("/sphere glass 8 -h"); // sphere

        print("Generating safety boundary and ceiling barrier border...");
        command("/pos1 -51,319,51"); // safety boundary
        command("/pos2 51,-64,-51"); // safety boundary
        command("/walls barrier"); // safety boundary
        command("/pos1 -50,302,50"); // ceiling
        command("/pos2 50,319,-50"); // ceiling
        command("/set barrier"); // ceiling

        print("Removing blacklisted blocks...");
        command("/pos2 50,-64,-50");
        for(String cmd : Main.getMainConfig().getStringList("removedBlocks")) {
            command(cmd);
        }

        File file = new File(getDataFolder().getParentFile().getParentFile(), "lavarising-" + (YamlUtils.getCustomYmlInt("data", "amount")-3));
        if(file.exists()) {
            Log.info("Deleting old world at location: " + file.getAbsolutePath());
            try {
                FileUtils.delete(file);
            } catch (NullPointerException exception) {
                Log.error("An error occurred deleting the old world file: " + exception);
                BetterStackTraces.print(exception);
            }
        }else{
            Log.error("Could not find an old world to delete with the value of " + (YamlUtils.getCustomYmlInt("data", "amount")-3) + "! Expected path: " + file.getAbsolutePath());
        }

        Log.info("Storing basic information in data.yml...");
        YamlUtils.setCustomYml("data", "lastSeed", world.getSeed());

        State.set(State.Game.PREGAME);

        Log.info(ChatColor.GREEN + "Successfully enabled CyberNetLavaRising [v" + getDescription().getVersion() + "] in " + (System.currentTimeMillis()-ms) + "ms");
        unixStarted = Time.getUnix();
    }

    @Override
    public void onDisable() {
        long ms = System.currentTimeMillis();
        State.set(State.Game.SERVER_CLOSING);

        print("Kicking all online players...");
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(UChat.component("&cThe server is currently restarting to load a new map... please try rejoining in around a minute!")));

        print("Storing basic information in data.yml...");
        YamlUtils.setCustomYml("data", "amount", (YamlUtils.getCustomYmlInt("data", "amount")+1));

        print("Removing all bossbars...");
        Bukkit.getBossBars().forEachRemaining(bossbar -> Bukkit.removeBossBar(bossbar.getKey()));

        Log.info(ChatColor.RED + "Successfully disabled CyberNetLavaRising [v" + getDescription().getVersion() + "] in " + (System.currentTimeMillis()-ms) + "ms");
    }

    public static void logEvent(String msg) {
        Log.info("-----------------------------------------");
        Log.info(msg);
        Log.info("THIS EVENT HAS BEEN LOGGED!");
        Log.info("-----------------------------------------");
    }

    public void command(String cmd) { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd); }
    public void print(String msg) {
        Log.info(msg);
    }

    public static void regenFailed() {
        Log.error("--------------------------------------------------------------------");
        Log.error("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        Log.error(" ");
        Log.error("THE SERVER FAILED TO RE-GENERATE THE WORLD FILE!");
        Log.error(" ");
        Log.error("THE SERVER WILL NOT ALLOWED PLAYERS TO JOIN UNTIL THIS IS FIXED!");
        Log.error(" ");
        Log.error("FIXED OR NOT, THE SERVER WILL RESTART IN " + disabledUntilRestart + " SECONDS!");
        Log.error(" ");
        Log.error("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        Log.error("--------------------------------------------------------------------");
    }

    public static void registerCommandAndTab(String name, Object commandExecutor, boolean withCommodore) {
        try {
            registerCommand(name, (CommandExecutor)commandExecutor);
            registerTabCompleter(name, (TabCompleter)commandExecutor);
            if(withCommodore) {
                if(CommodoreProvider.isSupported()) {
                    Commodore.registerCommodoreCommand(Bukkit.getPluginCommand(name), name);
                }
            }
        } catch (Exception exception) {
            Log.error(getPrefix() + " An error occurred whilst loading the command: /" + name + ": " + ChatColor.DARK_GRAY + exception);
        }
    }

}
