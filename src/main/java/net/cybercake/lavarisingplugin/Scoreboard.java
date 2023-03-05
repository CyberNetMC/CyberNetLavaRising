package net.cybercake.lavarisingplugin;

import fr.mrmicky.fastboard.FastBoard;
import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.cyberapi.generalutils.StringUtils;
import net.cybercake.lavarisingplugin.commands.Lava;
import net.cybercake.lavarisingplugin.commands.StartCMD;
import net.cybercake.lavarisingplugin.listeners.PlayerPVP;
import net.cybercake.lavarisingplugin.runnables.MainTask;
import net.cybercake.lavarisingplugin.runnables.StartSequence;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class Scoreboard implements Listener, Runnable {

    public static HashMap<String, FastBoard> boards = new HashMap<>();

    public boolean grabbedConfig = false;

    public long timeToPvP = 0L;

    @Override
    public void run() {
        timeToPvP = (MainTask.lavaRiseStatic*(PlayerPVP.pvpEnablesYLevel-MainTask.currentLevel)*1000L)+((MainTask.lavaRiseNext-Time.getUnix())*1000L)-(MainTask.lavaRiseStatic*1000L);

        for(Player player : Bukkit.getOnlinePlayers()) {
            FastBoard board = get(player);

            if(State.equals(State.Game.PREGAME) || State.equals(State.Game.COUNTING)) {
                if(StartCMD.manualControl) {
                    if((StartCMD.timeEnd - StartCMD.timeStart) <= 0) {
                        player.sendActionBar(UChat.component(ChatColor.AQUA + StartCMD.whoTookControl + " &chas lost control over start due to it timing out..."));
                        new StartCMD().replaceWithAuto();
                    }
                    player.sendActionBar(UChat.component(ChatColor.AQUA + StartCMD.whoTookControl + " &fhas control over the start sequence for &c" + (Time.toBetterDurationDisplay(StartCMD.timeEnd, Time.getUnix(), true))));
                }

                board.updateTitle(UChat.chat("&d&lCN - LavaRising"));
                board.updateLines(UChat.listChat(
                        Time.getFormattedDate("MMM d, yyyy"),
                        Time.getFormattedDate("h:mm:ss a"),
                        UChat.getSeperator(ChatColor.GREEN, 25),
                        " ",
                        "&6Players: &e" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(),
                        " ",
                        (State.equals(State.Game.PREGAME) ? "&6Waiting for " + (StartCMD.manualControl ? StartCMD.whoTookControl : (Main.playersToStart-Bukkit.getOnlinePlayers().size()) + " " + (Main.playersToStart-Bukkit.getOnlinePlayers().size() == 1 ? "player" : "players")) + "..." : "&6Starting in &e" + (StartSequence.getTimeUntil()+1) + "&es"),
                        " ",
                        "&6Mode: &eClassic",
                        "&6Version: &ev" + Main.get().getDescription().getVersion(),
                        " ",
                        "&e" + Main.getMainConfig().getString("ip"),
                        UChat.getSeperator(ChatColor.GREEN, 25)
                ));
            }else if(State.equals(State.Game.ACTIVE)) {
                board.updateTitle(UChat.chat("&d&lCN - LavaRising"));
                board.updateLines(UChat.listChat(
                        Time.getFormattedDate("MMM d, yyyy"),
                        Time.getFormattedDate("h:mm:ss a"),
                        UChat.getSeperator(ChatColor.GREEN, 25),
                        " ",
                        "&6Next Event:",
                        (Lava.paused ? "&c&lLAVA PAUSED!" : "&ePvP Enables (" + Time.formatTimeColons(timeToPvP) + ")"),
                        " ",
                        "&6Lava Rises Every: &e" + MainTask.lavaRiseStatic + "&es" + (Lava.paused ? " &c&l(P)" : ""),
                        "&6Next Lava Rise: &e" + (Lava.paused ? "&c&lPAUSED" : (MainTask.lavaRiseNext-Time.getUnix() <= -1 ? "0" : MainTask.lavaRiseNext-Time.getUnix()) + "&es"),
                        " ",
                        (PlayerPVP.playersPerRunnable.containsKey(player.getName()) ? "&6Combat With: &e" + PlayerPVP.inCombatWith.get(player.getName()) : "&6Alive: &e" + Bukkit.getOnlinePlayers().size() + "/" + StartSequence.playersWhenStarted),
                        (PlayerPVP.playersPerRunnable.containsKey(player.getName()) ? "&6For &e" + time(PlayerPVP.inCombatUntil.get(player.getName())-System.currentTimeMillis()) + " &eseconds"  : "&6Eliminations: &e0"),
                        " ",
                        "&e" + Main.getMainConfig().getString("ip"),
                        UChat.getSeperator(ChatColor.GREEN, 25)
                ));

            }
        }
    }

    public String time(long number) {

        int SECOND = 1000;        // no. of ms in a second
        int MINUTE = SECOND * 60; // no. of ms in a minuts

        long seconds = ((number % MINUTE) / SECOND);

        return (seconds) + "." + String.valueOf(number % 1000).charAt(0);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FastBoard board = new FastBoard(player);
        boards.put(player.getName(), board);

    }

    public static FastBoard get(Player player) {
        return boards.get(player.getName());
    }

}
