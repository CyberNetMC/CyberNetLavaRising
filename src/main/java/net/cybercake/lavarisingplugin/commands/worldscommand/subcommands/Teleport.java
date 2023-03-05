package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Teleport extends SubCommand {

    public Teleport() {
        super("tp", "worlds.teleport", "Teleports you or another player to a specified world.", "/worlds tp <world> [player]", "teleport", "spawn");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(!(sender instanceof Player player)) {
            Log.error("&cOnly players can execute this command!"); return;
        }

        if(args.length == 1) {
            teleportPlayers(player, player, player.getWorld());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F); return;
        }

        World world = Bukkit.getWorld(args[1]);
        if(world == null) {
            sender.sendMessage(UChat.component("&cInvalid world: &8" + args[1])); return;
        }

        if(args.length == 2) {
            teleportPlayers(player, player, world);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F); return;
        }

        Player target = Bukkit.getPlayerExact(args[2]);
        if(target == null) {
            sender.sendMessage(UChat.component("&cInvalid player: &8" + args[2])); return;
        }

        if(args.length == 3) {
            teleportPlayers(player, target, world);
        }
    }

    public static void teleportPlayers(Player msgTo, Player teleportWho, World worldSpawn) {
        teleportWho.teleport(SetSpawn.getWorldSpawn(worldSpawn));
        msgTo.sendMessage(UChat.component("&6You teleported &a" + (msgTo == teleportWho ? "yourself" : teleportWho.getName()) + " &6to &f" + worldSpawn.getName() + "&6's spawn!"));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getWorldNames(args[1]), args[1]);
        }else if(args.length == 3) {
            return CommandManager.createReturnList(Spigot.getOnlinePlayersUsernames(), args[2]);
        }
        return CommandManager.emptyList;
    }
}

