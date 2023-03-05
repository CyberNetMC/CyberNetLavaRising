package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.YamlUtils;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Unload extends SubCommand {

    public Unload() {
        super("unload", "worlds.unload", "Unloads a specified world.", "/worlds unload <world>", "");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            sender.sendMessage(UChat.component("&cInvalid arguments! &7" + this.getUsage())); return;
        }
        if(!Bukkit.getWorlds().contains(Bukkit.getWorld(args[1]))) {
            sender.sendMessage(UChat.component("&cInvalid world: &8" + args[1])); return;
        }
        if(Spigot.getMainWorld().equals(Bukkit.getWorld(args[1]))) {
            sender.sendMessage(UChat.component("&cYou cannot unload the main world!")); return;
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld().equals(Bukkit.getWorld(args[1]))) {
                player.teleport(SetSpawn.getWorldSpawn(Spigot.getMainWorld()));
            }
        }

        try {
            YamlUtils.setCustomYml("worlds", "worlds." + args[1] + ".loaded", false);
            Bukkit.unloadWorld(args[1], true);
            sender.sendMessage(UChat.component("&6You successfully unloaded the world named &a" + args[1] + "&6!"));
            if(sender instanceof Player player) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            }
        } catch (Exception exception) {
            Spigot.error(sender, "whilst trying to unload world " + args[1] + " by {name}", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getWorldNames(args[1]), args[1]);
        }
        return CommandManager.emptyList;
    }
}
