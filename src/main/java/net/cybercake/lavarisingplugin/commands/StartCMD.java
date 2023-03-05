package net.cybercake.lavarisingplugin.commands;

import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.chat.UTabComp;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.runnables.StartSequence;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class StartCMD implements CommandExecutor, TabCompleter {

    public static boolean manualControl = false;
    public static String whoTookControl = "AUTOMATIC";
    public static Long timeStart = 0L;
    public static Long timeEnd = 0L;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!State.equals(State.Game.PREGAME, State.Game.COUNTING)) {
            sender.sendMessage(UChat.component("&cYou can only use this command while in the pregame lobby!")); return true;
        }

        if(args.length <= 0) {
            sender.sendMessage(UChat.component("&aOkay, that's nice you want to start the game."));
            sender.sendMessage(UChat.component("&7&oBut like, actually confirm it by doing &f/start confirm"));
            sender.sendMessage(UChat.component(" "));
            sender.sendMessage(UChat.component("&6&lCOMMANDS:"));
            sender.sendMessage(UChat.component("&b/start confirm"));
            sender.sendMessage(UChat.component("&b/start cancel"));
            sender.sendMessage(UChat.component("&b/start takecontrol"));
            sender.sendMessage(UChat.component("&b/start settime <integer (seconds)>"));
        }else if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("takecontrol")) {
                boolean consoleTakeover = false;
                if(sender instanceof Player && manualControl && !whoTookControl.equals(sender.getName())) {
                    sender.sendMessage(UChat.component("&cYou don't have control of the start sequence!")); return true;
                }else if(!(sender instanceof Player) && manualControl && !whoTookControl.equals(sender.getName())) {
                    consoleTakeover = true;
                }
                whoTookControl = sender.getName();
                manualControl = !manualControl;
                timeStart = Time.getUnix();
                timeEnd = Time.getUnix()+900;
                boolean finalConsoleTakeover = consoleTakeover;
                Bukkit.getOnlinePlayers().forEach(
                        player -> player.sendMessage(UChat.component(
                                (manualControl ?
                                        "&aThe game's start sequence is now under manual control by &b" + sender.getName()
                                        : (finalConsoleTakeover ? "&cCONSOLE has taken over the game sequence!" : "&b" + sender.getName() + " &chas forfeited control over the game's start sequence"))
                        ))
                );
                if(!manualControl) {
                    replaceWithAuto();
                }else if(manualControl) {
                    new StartSequence().cancel(sender.getName() + " has taken control over the start sequence![S]");
                }
                return true;
            }

            if(sender instanceof Player player && !(whoTookControl.equalsIgnoreCase(player.getName()))) {
                player.sendMessage(UChat.component("&cYou do not currently have control over the game's start sequence!"));
                return true;
            }

            if(args[0].equalsIgnoreCase("confirm")) {
                Main.logEvent("A PLAYER HAS FORCE-STARTED THE GAME: " + sender.getName() + "!");
                sender.sendMessage(UChat.component("&7Starting game due to player request..."));
                new StartSequence().start(0);
            }else if(args[0].equalsIgnoreCase("cancel")) {
                Main.logEvent("A PLAYER HAS FORCE-ENDED THE GAME COUNTDOWN: " + sender.getName() + "!");
                sender.sendMessage(UChat.component("&7Force ending the start countdown due to player request..."));
                sender.sendMessage(UChat.component("&bPlease note that this will prevent the game starting for 15 minutes until your interaction! (then it will go back to auto)"));
                new StartSequence().cancel(sender.getName() + " manually cancelled the countdown!");
            }else if(args[0].equalsIgnoreCase("settime")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/start settime <time>"));
                else if(!NumberUtils.isInteger(args[1]) || !NumberUtils.isBetweenEquals(Integer.parseInt(args[1]), 1, 1000)) sender.sendMessage(UChat.component("&cYou must enter a valid integer!"));
                else {
                    Main.logEvent("A PLAYER HAS FORCEFULLY SET THE TIME UNTIL START: " + sender.getName() + " set to " + args[1]);
                    sender.sendMessage(UChat.component("&7Setting the time until start to " + args[1] + "..."));
                    new StartSequence().cancel("(resetting - ignore)[S]");
                    new StartSequence().start(Integer.parseInt(args[1]));
                }

            }
        }

        return true;
    }

    public void replaceWithAuto() {
        if(Bukkit.getOnlinePlayers().size() < Main.getMainConfig().getInt("minToStart")) {
            new StartSequence().cancel("Not enough players!");
        }else {
            new StartSequence().start(30);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length < 3 && args[0].equalsIgnoreCase("settime")) {
            return UTabComp.tabCompletionsContains(args[1], UTabComp.getIntegers(args[1], 1, 1000));
        }
        if(args.length < 2) {
            return UTabComp.tabCompletionsContains(args[0], UTabComp.tabCompletionsContains(args[0], Arrays.asList("cancel", "confirm", "settime", "takecontrol")));
        }
        return UTabComp.emptyList;
    }
}
