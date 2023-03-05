package net.cybercake.lavarisingplugin.runnables;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.CenteredMessage;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.listeners.PlayerPVP;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StartSequence {

    public static Long gameStarted = 0L;
    public static int playersWhenStarted = 0;
    public static BossBar bossBar;

    public void runOnStart() {
        long ms = System.currentTimeMillis();
        World world = Main.world;

        Log.info("Starting game...");
        State.set(State.Game.ACTIVE);

        Log.info("Removing advancements and adding all recipes...");
        command("advancement revoke @a everything");
        command("recipe give @a *");

        Log.info("Showing information to the players...");
        sendAll(UChat.getSeperator(ChatColor.GOLD));
        sendAllCent("&6&lLAVA RISING");
        sendAllCent("&e &e &eWelcome to Lava Rising, assuming this is your first time reading this message. Anyway, welcome. In this game, your goal is to survive like normal Minecraft, and that's it! Except for ONE TWIST; lava rises every x seconds (depending on what time in the game it is, it progressively gets faster). You can look at the sidebar to see how long until the lava rises again and how long it takes in general, as well as your eliminations and other players. Have fun and please follow the rules for Lava Rising, which can be found in &7/rules&e!");
        sendAll(" ");
        sendAllCent("&7&oYou can access this message at anytime using &f/help&7&o!");
        sendAll(UChat.getSeperator(ChatColor.GOLD));
        Bukkit.getOnlinePlayers().forEach(player -> Spigot.sendTitle(player, "&6&lLAVA RISING!", "&eEnjoy the game! Read chat for how to play.", 0, 100, 40));

        Log.info("Changing some gamerules and world options...");
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

        world.setTime(0);
        world.setDifficulty(Difficulty.valueOf(Main.getMainConfig().getString("difficulty")));

        Bukkit.getOnlinePlayers().forEach(player -> player.setVelocity(new Vector(0, 15, 0)));

        Log.info("Removing the sphere...");
        command("/world " + world.getName());
        command("/pos1 0," + Main.yLevelSphere + ",0");
        command("/pos2 0," + Main.yLevelSphere + ",0");
        command("/sphere air 9");

        Log.info("Applying potion effects...");
        int absorptionAmount = Main.getMainConfig().getInt("absorptionAmount");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Spigot.getTopBlock(new Location(world, 0.5, 0, 0.5), 200).add(0, 100, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 2400, 90, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 90, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, absorptionAmount, false, false, true));
        }

        Log.info("Applying other misc player data...");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.getInventory().setHeldItemSlot(0);

            player.setSaturation(20);
            player.setHealth(20.0);
            player.setFoodLevel(20);

            player.setTotalExperience(0);

            player.closeInventory();

        }

        BukkitRunnable advancements = new BukkitRunnable() {
            @Override
            public void run() {
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
                world.setGameRule(GameRule.KEEP_INVENTORY, false);
                Bukkit.getOnlinePlayers().forEach(player -> player.setTotalExperience(0));
            }
        };
        advancements.runTaskLater(Main.get(), 100L);

        gameStarted = Time.getUnix();
        playersWhenStarted = Bukkit.getOnlinePlayers().size();
        MainTask.lavaRiseStatic = 10;
        MainTask.lavaRiseNext = Time.getUnix()+15;
        MainTask.currentLevel = -64;
        PlayerPVP.pvpEnabled = false;
        bossBar = Bukkit.createBossBar(new NamespacedKey(Main.get(), "lavarising"), UChat.chat("&8Loading..."), BarColor.RED, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);
        Bukkit.getOnlinePlayers().forEach(player -> bossBar.addPlayer(player));

        Main.registerRunnable(new MainTask(), 2L);

        PlayerPVP.pvpEnablesYLevel = Main.getMainConfig().getInt("pvp.yLevelEnables");

        Log.info("It took " + (System.currentTimeMillis()-ms) + "ms to start the game!");
    }

    public void sendAll(String msg) {
        Bukkit.getOnlinePlayers().forEach(player ->  player.sendMessage(UChat.component(msg)));
    }

    public void sendAllCent(String msg) {
        sendAll(CenteredMessage.get(msg));
    }

    private static int timeStart;
    private static BukkitRunnable runnable;

    public void start(int untilStart) {
        if(!State.get().equals(State.Game.PREGAME)) {
            Log.warn("Game attempted to start even though game is not in the PREGAME phase! (event cancelled)"); return;
        }

        State.set(State.Game.COUNTING);

        timeStart = untilStart;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(timeStart <= 0) {
                    runnable.cancel();
                    runOnStart();
                    return;
                }

                if(timeStart == 900) {
                    timeLeftTitle(ChatColor.GREEN, "15 minutes until the game starts!");
                }else if(timeStart == 600) {
                    timeLeftTitle(ChatColor.GREEN, "10 minutes until the game starts!");
                }else if(timeStart == 300) {
                    timeLeftTitle(ChatColor.GREEN, "5 minutes until the game starts!");
                }else if(timeStart == 120) {
                    timeLeftTitle(ChatColor.GREEN, "2 minutes until the game starts!");
                }else if(timeStart == 60) {
                    timeLeftTitle(ChatColor.GREEN, "Only 60 seconds left!");
                }else if(timeStart == 30) {
                    timeLeftTitle(ChatColor.GREEN, "Are you ready?");
                }else if(NumberUtils.isBetweenEquals(timeStart, 11, 15)) {
                    timeLeftTitle(ChatColor.GREEN, "Prepare yourselves!");
                }else if(NumberUtils.isBetweenEquals(timeStart, 6, 10)) {
                    timeLeftTitle(ChatColor.GOLD,"Get ready!");
                }else if(timeStart < 6) {
                    timeLeftTitle(ChatColor.RED, "Here we go!");
                }else if(timeStart == untilStart) {
                    timeLeftTitle(ChatColor.DARK_GREEN, "Starting soon!");
                }

                timeStart--;
            }
        };
        runnable.runTaskTimer(Main.get(), 20L, 20L);
    }

    public void cancel(String why) {
        Log.info("Attempting to cancel game start... (why='" + why + "')");
        if(!State.get().equals(State.Game.COUNTING)) {
            Log.warn("Game attempted to cancel the countdown even though it's not currently counting down (event cancelled)"); return;
        }

        State.set(State.Game.PREGAME);

        runnable.cancel();

        if(!why.endsWith("[S]")) Bukkit.getOnlinePlayers().forEach(player -> Spigot.sendTitle(player, "&c&lSTART CANCELLED!", "&c" + why, 0, 60, 20));
        if(!why.endsWith("[S]")) Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_DEATH, 2F, 1F));
        Log.info("Game start has been cancelled due to '" + why + "'");
        if(why.endsWith("[S]")) Log.info("This action was done SILENTLY!");
    }

    public void timeLeftTitle(ChatColor color, String subtitleMsg) {
        Bukkit.getOnlinePlayers().forEach(player -> Spigot.sendTitle(player, color + String.valueOf(timeStart), ChatColor.YELLOW + subtitleMsg, 0, 40, 20));
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2F, 1F));
    }

    public static int getTimeUntil() {
        return timeStart;
    }

    public void command(String cmd) { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd); }
    public void print(String msg) {
        Log.info(msg);
    }

}
