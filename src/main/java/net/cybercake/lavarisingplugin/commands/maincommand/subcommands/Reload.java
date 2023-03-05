package net.cybercake.lavarisingplugin.commands.maincommand.subcommands;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.commands.maincommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.maincommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;

import java.util.List;

public class Reload extends SubCommand {

    public Reload() {
        super("reload", "admin.lavarising.maincmd.reload", "Reloads the plugin's configuration files!", "/cybernetlavarising reload", "rl");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        long mss = System.currentTimeMillis();

        Exception exception = null;
        String exceptionFile = "";

        try { Main.get().reloadConfig(); } catch (Exception ex) { exception = ex; exceptionFile = "config.yml"; }

        if(exception != null) {
            Spigot.error(sender, "whilst trying to reload the " + exceptionFile + " file!", exception);
            Spigot.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }else{
            Main.playersToStart = Main.getMainConfig().getInt("minToStart");

            sender.sendMessage(UChat.component("&6You successfully reloaded the configuration files in &a" + (System.currentTimeMillis()-mss) + "&ams&6!"));

            Spigot.playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
