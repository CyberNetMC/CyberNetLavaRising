package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.YamlUtils;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.h2.mvstore.DataUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SetSpawn extends SubCommand {

    public SetSpawn() {
        super("setspawn", "worlds.setspawn", "Sets the spawn of a specific world.", "/worlds setspawn [<x> <y> <z> <yaw> <pitch> [world]]", "setlocation");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(UChat.component("Invalid usage! &7" + this.getUsage())); return;
            }
            YamlUtils.setCustomYml("worlds", "worlds." + player.getWorld().getName() + ".spawnLocation", player.getLocation());
            player.sendMessage(UChat.component("&6Successfully set the spawn location to &ayour &6location!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
        }else if(args.length > 1){
            if(NumberUtils.isBetweenEquals(args.length, 2, (sender instanceof Player ? 5 : 6))) {
                sender.sendMessage(UChat.component("&cFailed to parse location, are your arguments correct?"));
            } else if(args.length >= (sender instanceof Player ? 6 : 7)) {
                if(!(NumberUtils.isDouble(args[1])) || !(NumberUtils.isDouble(args[2])) || !(NumberUtils.isDouble(args[3])) || !(NumberUtils.isDouble(args[4]) || !(NumberUtils.isDouble(args[5])))) { sender.sendMessage(UChat.component("&cInvalid decimal: &8(argument unknown)")); }

                World world;
                if(sender instanceof Player player) {
                    if(args.length >= 7) {
                        world = Bukkit.getWorld(args[6]);
                        if(world == null) { sender.sendMessage(UChat.component("&cInvalid world: &8" + args[6])); return; }
                    }else{
                        world = player.getWorld();
                    }
                }else{
                    world = Bukkit.getWorld(args[6]);
                    if(world == null) { sender.sendMessage(UChat.component("&cInvalid world: &8" + args[6])); return; }
                }

                Location location = new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));

                YamlUtils.setCustomYml("worlds", "worlds." + location.getWorld().getName() + ".spawnLocation", location);
                sender.sendMessage(UChat.component("&6Successfully set the spawn location of &e" + world.getName() + " &6to &3x=" + args[1] + " &ey=" + args[2] + " &az=" + args[3] + " &dyaw=" + args[4] + " &6pitch=" + args[5] + "&6!"));

                if(sender instanceof Player player) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
                }
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length == 7) {
            return CommandManager.createReturnList(CommandManager.getWorldNames(args[6]), args[6]);
        }else if(args.length == 6) {
            return CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getPitch())), args[5]);
        }else if(args.length == 5) {
            return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getYaw()) + " " + Math.round(player.getLocation().getPitch())), args[4]);
        }else if(args.length == 4) {
            return CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getZ())), args[3]);
        }else if(args.length == 3) {
            return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[2]);
        }else if(args.length == 2) {
            return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getX()) + " " + Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[1]);
        }else if(args.length == 1) {
            return CommandManager.createReturnList(Collections.singletonList("setLocation"), args[0]);
        }
        return CommandManager.emptyList;
    }

    public static Location getWorldSpawn(World world) {
        if(YamlUtils.getCustomYmlLocation("worlds", "worlds." + world.getName() + ".spawnLocation") == null) {
            YamlUtils.setCustomYml("worlds", "worlds." + world.getName() + ".spawnLocation", world.getSpawnLocation());
            return world.getSpawnLocation();
        }
        return YamlUtils.getCustomYmlLocation("worlds", "worlds." + world.getName() + ".spawnLocation");
    }

    public static Location getWorldSpawn(@NotNull String worldName) {
        return getWorldSpawn(Bukkit.getWorld(worldName));
    }
}
