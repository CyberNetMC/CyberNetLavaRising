package net.cybercake.lavarisingplugin.commands;

import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.chat.UTabComp;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.runnables.MainTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lava implements CommandExecutor, TabCompleter {

    public static boolean paused = false;
    public static long whenPaused = 0;
    public static int yLevelWhenPaused = 0;

    public static boolean everyonePaused = false;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!State.equals(State.Game.ACTIVE)) {
            sender.sendMessage(UChat.component("&cYou cannot set lava variables while the game is not in state 'ACTIVE'")); return true;
        }

        if(args.length <= 0) {
            sender.sendMessage(UChat.component("&6&lCOMMANDS:"));
            sender.sendMessage(UChat.component("&b/lava setlevel <integer (y level)>"));
            sender.sendMessage(UChat.component("&b/lava settimestatic <integer (time between lava rising)>"));
            sender.sendMessage(UChat.component("&b/lava settimeuntil <integer (time until next rise)>"));
            sender.sendMessage(UChat.component("&b/lava pause"));
            sender.sendMessage(UChat.component("&b/lava listtasks"));
            sender.sendMessage(UChat.component("&b/lava canceltask <taskID>"));
        }else if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("setlevel")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/lava setlevel <level>"));
                else if(!NumberUtils.isInteger(args[1]) || !NumberUtils.isBetweenEquals(Integer.parseInt(args[1]), -64, 319)) sender.sendMessage(UChat.component("&cYou must enter a valid integer between &b-64 &cand &b319&c!"));
                else {
                    Main.logEvent("A PLAYER (" + sender.getName() + ") HAS SET THE Y LEVEL OF THE LAVA TO " + args[1]);
                    MainTask.currentLevel = Integer.parseInt(args[1]);
                }
            }else if(args[0].equalsIgnoreCase("settimestatic")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/lava settimestatic <time>"));
                else if(!NumberUtils.isInteger(args[1]) || !NumberUtils.isBetweenEquals(Integer.parseInt(args[1]), 0, 9999)) sender.sendMessage(UChat.component("&cYou must enter a valid integer between &b0 &cand &b9,999&c!"));
                else{
                    Main.logEvent("A PLAYER (" + sender.getName() + ") HAS SET THE TIME-STATIC TO " + args[1]);
                    MainTask.lavaRiseStatic = Integer.parseInt(args[1]);
                }
            }else if(args[0].equalsIgnoreCase("settimeuntil")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/lava settimeuntil <time>"));
                else if(!NumberUtils.isInteger(args[1]) || !NumberUtils.isBetweenEquals(Integer.parseInt(args[1]), 0, 9999)) sender.sendMessage(UChat.component("&cYou must enter a valid integer between &b0 &cand &b9,999&c!"));
                else {
                    Main.logEvent("A PLAYER (" + sender.getName() + ") HAS SET THE TIME-NEXT TO " + args[1]);
                    MainTask.lavaRiseNext = Time.getUnix()+Integer.parseInt(args[1]);
                }
            }else if(args[0].equalsIgnoreCase("pause")) {


                Main.logEvent("A PLAYER (" + sender.getName() + ") HAS PAUSED/UNPAUSED THE LAVA!");
                paused = !paused;
                if(!paused) {
                    sender.sendMessage(UChat.component("&aYou unpaused the lava!"));
                    MainTask.lavaRiseNext = Time.getUnix()+whenPaused;
                    MainTask.currentLevel = yLevelWhenPaused-1;
                    everyonePaused = false;
                } else if(paused) {
                    sender.sendMessage(UChat.component("&cYou paused the lava!"));
                    if(args.length >= 2 && args[1].equalsIgnoreCase("everybody")) {
                        sender.sendMessage(UChat.component("&cYou also paused everybody in the game!"));

                        everyonePaused = true;
                    }
                    MainTask.pausedBossbarProgress = 0.0;
                    MainTask.pausedBossbarUp = true;
                    whenPaused = Time.getUnix()-MainTask.lavaRiseNext;
                    yLevelWhenPaused = MainTask.currentLevel;
                }
            }else if(args[0].equalsIgnoreCase("listtasks")) {
                sender.sendMessage(UChat.component("&6&lLAVA TASKS CURRENTLY ACTIVE:"));
                for(BukkitRunnable runnable : MainTask.lavaRunnables) {
                    sender.sendMessage(UChat.component(
                            "" +
                                    "&b" + runnable.getTaskId() + " &8| " +
                                    "&eST " + Time.formatTimeMs(System.currentTimeMillis()-MainTask.lavaRunnableStarted.get(runnable), true) + " &8| " +
                                    "&cY " + MainTask.lavaRunnableYLevel.get(runnable)));
                }
            }else if(args[0].equalsIgnoreCase("stoptask") || args[0].equalsIgnoreCase("endtask") || args[0].equalsIgnoreCase("deletetask") || args[0].equalsIgnoreCase("canceltask")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/lava stoptask <taskID>"));
                else if(!NumberUtils.isInteger(args[1])) sender.sendMessage(UChat.component("&cYou must enter a valid integer!"));
                else if(!MainTask.taskIDToRunnableObject.containsKey(Integer.parseInt(args[1]))) sender.sendMessage(UChat.component("&cYou must enter a valid task ID!"));
                else {
                    sender.sendMessage(UChat.component("&cYou deleted task with ID &b" + args[1]));
                    new MainTask().cancelTask(MainTask.taskIDToRunnableObject.get(Integer.parseInt(args[1])));
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length < 3 && args[0].equalsIgnoreCase("setlevel")) {
            return UTabComp.tabCompletionsContains(args[1], UTabComp.getIntegers(args[1], -64, 319));
        }
        if(args.length < 3 && (args[0].equalsIgnoreCase("settimestatic") || args[0].equalsIgnoreCase("settimeuntil"))) {
            return UTabComp.tabCompletionsContains(args[1], UTabComp.getIntegers(args[1], 0, 9999));
        }
        if(args.length < 3 && args[0].equalsIgnoreCase("pause")) {
            return UTabComp.tabCompletionsContains(args[1], Arrays.asList("everybody"));
        }
        if(args.length < 3 && args[0].equalsIgnoreCase("canceltask")) {
            ArrayList<String> returned = new ArrayList<>();
            for(int taskID : MainTask.taskIDToRunnableObject.keySet()) {
                returned.add(String.valueOf(taskID));
            }
            return UTabComp.tabCompletionsContains(args[1], returned);
        }
        if(args.length < 2) {
            return UTabComp.tabCompletionsContains(args[0], UTabComp.tabCompletionsContains(args[0], Arrays.asList("setlevel", "settimestatic", "settimeuntil", "pause", "listtasks", "canceltask")));
        }
        return UTabComp.emptyList;
    }
}
