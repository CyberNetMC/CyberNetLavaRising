package net.cybercake.lavarisingplugin.commands.maincommand.subcommands;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.lavarisingplugin.commands.maincommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.maincommand.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {

    public Help() { super("help", "", "Prints this help message.", "/cybernetlavarising help", new String[]{"?", "info"}); }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        // Overridden and moved to main CommandManager class
        sender.sendMessage(UChat.component("&cAn error occurred!"));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }

}
