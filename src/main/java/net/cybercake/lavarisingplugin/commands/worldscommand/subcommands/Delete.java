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
import org.h2.mvstore.DataUtils;

import java.util.List;

public class Delete extends SubCommand {

    public Delete() {
        super("delete", "worlds.deleteworld", "Deletes a specified world.", "/worlds delete <worldName>", "remove");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if (args.length < 2) {
            sender.sendMessage(UChat.component("&cInvalid usage! &7" + this.getUsage())); return;
        }

        try {
            if(!CommandManager.worldExist(args[1])) {
                sender.sendMessage(UChat.component("&cInvalid world: &8" + args[0])); return;
            }
            if(Spigot.getMainWorld().equals(Bukkit.getWorld(args[1]))) {
                sender.sendMessage(UChat.component("&cYou cannot delete the main world!")); return;
            }

            sender.sendMessage(UChat.component("&7&oDeleting the world " + args[1] + "&7&o... please wait!"));

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getWorld().equals(Bukkit.getWorld(args[1]))) {
                    player.teleport(SetSpawn.getWorldSpawn(Spigot.getMainWorld()));
                }
            }

            CommandManager.deleteWorld(Bukkit.getWorld(args[1]).getWorldFolder());

            Bukkit.unloadWorld(Bukkit.getWorld(args[1]), false);

            if(Bukkit.getWorld(args[1]) == null) {
                sender.sendMessage(UChat.component("&6Successfully deleted the world name &a" + args[1] + "&6!"));

                YamlUtils.setCustomYml("worlds", "worlds." + args[1], null);

                Spigot.playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            }else if(Bukkit.getWorld(args[1]) != null) {
                sender.sendMessage(UChat.component("&cFailed to delete the world &8" + args[1] + "&c!"));

                Spigot.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            }
        } catch (Exception exception) {
            Spigot.error(sender, "during the world deletion process for {name}", exception);
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
