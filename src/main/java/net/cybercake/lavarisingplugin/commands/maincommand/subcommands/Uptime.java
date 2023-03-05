package net.cybercake.lavarisingplugin.commands.maincommand.subcommands;

import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.generalutils.NumberUtils;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.commands.maincommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.maincommand.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class Uptime extends SubCommand {

    public Uptime() {
        super("uptime", "admin.lavarising.maincmd.uptime", "View the uptime of the server.", "/cybernetlavarising uptime", "serveruptime");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        String uptime = "&6The server has been online for &a" + Time.getBetterTimeFromLongs(Time.getUnix(), Main.unixStarted, false) + "&6!";
        if(args.length > 1) {
            if(args[1].equals("seconds")) {
                sender.sendMessage(UChat.chat("&6The server has been online for &a" + NumberUtils.formatLong(Time.getUnix()- Main.unixStarted) + " &aseconds&6!"));
            }else if(args[1].equals("minutes")) {
                sender.sendMessage(UChat.chat("&6The server has been online for &a" + NumberUtils.formatLong((Time.getUnix()-Main.unixStarted)/60) + " &aminutes&6!"));
            }else if(args[1].equals("hours")) {
                sender.sendMessage(UChat.chat("&6The server has been online for &a" + NumberUtils.formatLong(((Time.getUnix()-Main.unixStarted)/60)/60) + " &ahours&6!"));
            }else if(args[1].equals("days")) {
                sender.sendMessage(UChat.chat("&6The server has been online for &a" + NumberUtils.formatLong((((Time.getUnix() - Main.unixStarted) / 60) / 60) / 24) + " &adays&6!"));
            }else{
                sender.sendMessage(UChat.chat("&cInvalid time unit: &8" + args[1]));
            }
            return;
        }

        sender.sendMessage(UChat.chat(uptime));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(Arrays.asList("seconds", "minutes", "hours", "days"), args[1]);
        }
        return CommandManager.emptyList;
    }
}