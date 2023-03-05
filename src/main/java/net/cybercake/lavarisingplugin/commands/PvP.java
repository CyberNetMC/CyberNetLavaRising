package net.cybercake.lavarisingplugin.commands;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.chat.UTabComp;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.listeners.PlayerPVP;
import net.cybercake.lavarisingplugin.runnables.MainTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PvP implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!State.equals(State.Game.ACTIVE)) {
            sender.sendMessage(UChat.component("&cYou cannot set lava variables while the game is not in state 'ACTIVE'")); return true;
        }

        if(args.length <= 0) {
            sender.sendMessage(UChat.component("&6&lCOMMANDS:"));
            sender.sendMessage(UChat.component("&b/pvp toggle"));
            sender.sendMessage(UChat.component("&b/pvp setenables <integer (y level)>"));
            sender.sendMessage(UChat.component("&b/pvp when"));
        }else if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("toggle")) {
                Main.logEvent("A PLAYER (" + sender.getName() + ") HAS TOGGLED THE STATE OF PLAYER PVP!");

                PlayerPVP.pvpEnabled = !PlayerPVP.pvpEnabled;
                new PlayerPVP().message(PlayerPVP.pvpEnabled);
            }else if(args[0].equalsIgnoreCase("setenables")) {
                if(args.length < 2) sender.sendMessage(UChat.component("&cInvalid usage! &7/pvp setenables <integer (y level)>"));
                else if(!NumberUtils.isInteger(args[1]) || !NumberUtils.isBetweenEquals(Integer.parseInt(args[1]), -64, 319)) sender.sendMessage(UChat.component("&cYou must enter a valid integer between &b-64 &cand &b319&c!"));
                else {
                    Main.logEvent("A PLAYER (" + sender.getName() + ") SET PVP TO ENABLE AT Y = " + args[1]);
                    PlayerPVP.pvpEnablesYLevel = Integer.parseInt(args[1]);
                }
            }else if(args[0].equalsIgnoreCase("when")) {
                long timeUntilPvP = (MainTask.lavaRiseStatic*(PlayerPVP.pvpEnablesYLevel-MainTask.currentLevel)*1000L)+((MainTask.lavaRiseNext-Time.getUnix())*1000L)-(MainTask.lavaRiseStatic*1000L);

                sender.sendMessage(UChat.component("&fPVP currently enables when lava gets to &by = " + PlayerPVP.pvpEnablesYLevel + "&f!"));
                sender.sendMessage(UChat.component("&fThis will be in approximately &b" + Time.formatTimeColons(timeUntilPvP) + "&f!"));
                sender.sendMessage(UChat.component("&7Technical Details = (base=" + timeUntilPvP + "&7)"));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length < 3 && args[0].equalsIgnoreCase("setenables")) {
            return UTabComp.tabCompletionsContains(args[1], UTabComp.getIntegers(args[1], -64, 319));
        }
        if(args.length < 2) {
            return UTabComp.tabCompletionsContains(args[0], UTabComp.tabCompletionsContains(args[0], Arrays.asList("toggle", "setenables", "when")));
        }
        return UTabComp.emptyList;
    }
}
