package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.chat.CenteredMessage;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.StringJoiner;

public class List extends SubCommand {

    public List() {
        super("list", "worlds.list", "Prints a list of all loaded worlds.", "/worlds list [world]", new String[]{"worldlist"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            sender.sendMessage(UChat.getSeperator(ChatColor.BLUE));
            sender.sendMessage(CenteredMessage.get("&d&lWORLDS AND THEIR PLAYERS: &7(" + Bukkit.getWorlds().size() + "&7)"));
            for(World world : Bukkit.getWorlds()) {
                StringJoiner joiner = new StringJoiner(", ");
                ArrayList<String> getPlayer = new ArrayList<>();
                for(Player player : world.getPlayers()) {
                    getPlayer.add(player.getName());
                }
                getPlayer.forEach(joiner::add);

                TextComponent component = new TextComponent(UChat.chat("&b" + world.getName() + " &e(" + getPlayer.size() + ")&f: " + joiner));

                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(UChat.chat("&6Players &7(" + getPlayer.size() + "&7)&6: &a" + joiner + "\n&f\n&fClick here to teleport to &b\"" + world.getName() + "&b\"&f!"))));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worlds tp " + world.getName() + " " + sender.getName()));

                sender.sendMessage(component);
            }
            sender.sendMessage(UChat.getSeperator(ChatColor.BLUE));
        }

    }

    @Override
    public java.util.List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getWorldNames(args[1]), args[1]);

        }
        return CommandManager.emptyList;
    }
}
