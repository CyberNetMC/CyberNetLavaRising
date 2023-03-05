package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.chat.UTabComp;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {

    public Help() { super("help", "", "Prints this help message.", "/worlds help", "?", "info"); }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        // Overridden and moved to main CommandManager class
        sender.sendMessage(UChat.chat("&cAn error occurred!"));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return UTabComp.emptyList;
    }
}
