package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gamerule extends SubCommand {

    public Gamerule() {
        super("gamerule", "worlds.gamerule", "Change the gamerule of a specified world.", "/worlds gamerule <gamerule> [value] [world]", "gamerules", "rule");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length < 2) {
            sender.sendMessage(UChat.component("&cInvalid arguments! &7" + this.getUsage())); return;
        }

        if(GameRule.getByName(args[1]) == null) {
            sender.sendMessage(UChat.component("&cInvalid gamerule: &8" + args[1])); return;
        }

        if(args.length == 2 && sender instanceof Player player) {
            player.sendMessage(UChat.component("&6Gamerule &a" + args[1] + " &6is currently set to &f" + player.getWorld().getGameRuleValue(GameRule.getByName(args[1])) + " &6in world &e" + player.getWorld().getName())); return;
        }else if(args.length == 2) {
            Log.error(UChat.chat("&cInvalid usage! &7" + this.getUsage())); return;
        }

        if(stringToGamerule(args[1]).getType() == Boolean.class && !(args[2].equals("true") || args[2].equals("false"))) {
            sender.sendMessage(UChat.component("&cExpected boolean (true/false), but found &b" + args[2] + "&c!")); return;
        }
        if(stringToGamerule(args[1]).getType() == Integer.class && !(NumberUtils.isInteger(args[2]))) {
            sender.sendMessage(UChat.component("&cExpected integer, but found &b" + args[2] + "&c!")); return;
        }

        if(args.length == 3 && sender instanceof Player player) {
            player.getWorld().setGameRuleValue(args[1], args[2]);
            player.sendMessage(UChat.component("&6Set gamerule &a" + args[1] + " &6to &f" + args[2] + " &6in world &e" + player.getWorld().getName() + "&6!")); return;
        }else if(args.length == 3) {
            Log.error(UChat.chat("&cInvalid usage! &7" + this.getUsage())); return;
        }

        if(!Bukkit.getWorlds().contains(Bukkit.getWorld(args[3]))) {
            sender.sendMessage(UChat.component("&cInvalid world: &8" + args[3])); return;
        }

        if(args.length == 4) {
            Bukkit.getWorld(args[3]).setGameRuleValue(args[1], args[2]);
            sender.sendMessage(UChat.component("&6Set gamerule &a" + args[1] + " &6to &f" + args[2] + " &6in world &e" + args[3] + "&6!"));
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(getGamerules(), args[1]);
        }else if(args.length == 3) {
            if(!getGamerules().contains(args[1])) {
                return CommandManager.emptyList;
            }

            if(stringToGamerule(args[1]).getType() == Boolean.class) {
                return CommandManager.createReturnList(Arrays.asList("true", "false"), args[2]);
            }
        }else if(args.length == 4) {
            if(stringToGamerule(args[1]).getType() == Boolean.class && !(args[2].equals("true") || args[2].equals("false"))) {
                return CommandManager.emptyList;
            }
            if(stringToGamerule(args[1]).getType() == Integer.class && !(NumberUtils.isInteger(args[2]))) {
                return CommandManager.emptyList;
            }
            return CommandManager.createReturnList(CommandManager.getWorldNames(args[3]), args[3]);
        }
        return CommandManager.emptyList;
    }

    public static GameRule<?> stringToGamerule(String original) {
        for(GameRule<?> gamerule : GameRule.values()) {
            if(gamerule.getName().equalsIgnoreCase(original)) {
                return gamerule;
            }
        }
        return null;
    }

    public static ArrayList<String> getGamerules() {
        ArrayList<String> gameRules = new ArrayList<>();
        for(GameRule<?> gamerule : GameRule.values()) {
            gameRules.add(gamerule.getName());
        }
        return gameRules;
    }
}
