package net.cybercake.lavarisingplugin.commands.worldscommand;

import net.cybercake.cyberapi.chat.CenteredMessage;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.lavarisingplugin.commands.worldscommand.subcommands.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandManager implements CommandExecutor, TabCompleter {

    // I am going to be using the CommandManager from https://gitlab.com/kodysimpson/command-manager-spigot, though slightly modified

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    public static ArrayList<String> emptyList = new ArrayList<>();

    private final static String pluginPermission = "worlds";
    private final static String pluginTitle = "WORLDS";
    private final static String noPermissionMsg = "&cYou don't have permission to use this!";

    public CommandManager() {
        subcommands.add(new Help());
        subcommands.add(new net.cybercake.lavarisingplugin.commands.worldscommand.subcommands.List());
        subcommands.add(new Teleport());
        subcommands.add(new Load());
        subcommands.add(new Delete());
        subcommands.add(new Unload());
        subcommands.add(new Gamerule());
        subcommands.add(new SetSpawn());
    }

    // Note for later: please clan this up and remove the arrow code :D

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            sender.sendMessage(UChat.chat(noPermissionMsg));
        }else if(args.length == 0) {
            getSubCommand("list").perform(sender, new String[]{""}, command);
        }else {
            boolean ran = false;
            if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("info")) {
                printHelpMsg(sender);
            }else{
                for (SubCommand cmd : getSubcommands()) {
                    boolean use = args[0].equalsIgnoreCase(cmd.getName());
                    if(!use) {
                        for(String alias : cmd.getAliases()) {
                            if (args[0].equalsIgnoreCase(alias)) {
                                use = true;
                                break;
                            }
                        }
                    }
                    if (use) {
                        if(sender.hasPermission(pluginPermission + ".*")) {
                            cmd.perform(sender, args, command);
                        }else if (cmd.getPermission().equalsIgnoreCase("")) {
                            cmd.perform(sender, args, command);
                        } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                            cmd.perform(sender, args, command);
                        } else {
                            sender.sendMessage(UChat.component(noPermissionMsg));
                        }
                        ran = true;
                    }
                }
                if(!ran) {
                    sender.sendMessage(UChat.component("&cUnknown sub-command: &8" + args[0]));
                }
            }
        }



        return true;
    }

    public SubCommand getSubCommand(String subCommandName) {
        for(SubCommand command : getSubcommands()) {
            if(command.getName().equalsIgnoreCase(subCommandName)) {
                return command;
            }
        }
        return null;
    }

    public void printHelpMsg(CommandSender sender) {
        if(sender instanceof Player) {
            sender.sendMessage(UChat.getSeperator(ChatColor.BLUE));
        }
        sender.sendMessage(CenteredMessage.get("&d&l" + pluginTitle + " COMMANDS:"));
        for(String cmdStr : getSubCommandsOnlyWithPerms(sender)) {
            if(sender.hasPermission(pluginPermission + ".*")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            }else if (getSubCommand(cmdStr).getPermission().equalsIgnoreCase("")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            } else if (!getSubCommand(cmdStr).getPermission().equalsIgnoreCase("") && sender.hasPermission(getSubCommand(cmdStr).getPermission())) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            }
        }
        if(sender instanceof Player) {
            sender.sendMessage(UChat.getSeperator(ChatColor.BLUE));
        }
    }

    @SuppressWarnings("deprecation")
    public static void printHelpMsgSpecific(CommandSender sender, String description, String usage, String permission, String aliases) {
        if(permission.equalsIgnoreCase("")) {
            permission = "Everyone";
        }

        String ifAliases = "";
        if(!aliases.equalsIgnoreCase("[]")) {
            ifAliases = "\n&6Aliases: &f" + aliases;
        }
        BaseComponent component = new TextComponent(UChat.chat("&b" + usage));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(UChat.chat("&6Command: &f" + usage + "\n&6Description: &f" + description + "\n&6Permission: &f" + permission + ifAliases)).create()));
        sender.spigot().sendMessage(component);
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public ArrayList<String> getSubCommandsOnlyWithPerms(CommandSender sender) {
        ArrayList<String> cmdNames = new ArrayList<>();
        for(SubCommand cmd : getSubcommands()) {
            if(sender.hasPermission(pluginPermission + ".*")) {
                cmdNames.add(cmd.getName());
            }else if(cmd.getPermission().equalsIgnoreCase("")) {
                cmdNames.add(cmd.getName());
            }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                cmdNames.add(cmd.getName());
            }
        }
        return cmdNames;
    }

    public static ArrayList<String> getWorldNames(String argument) {
        ArrayList<String> allWorlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            allWorlds.add(world.getName());
        }
        if(allWorlds.contains(argument)) {
            return CommandManager.emptyList;
        }
        return allWorlds;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            return emptyList;
        }else if(args.length <= 1) {
            return createReturnList(getSubCommandsOnlyWithPerms(sender), args[0]);
        }else{
            try {
                for(SubCommand cmd : getSubcommands()) {
                    for(int i = 1; i < 100; i++) {
                        boolean use = args[0].equalsIgnoreCase(cmd.getName());
                        if(!use) {

                            for(String cmdAlias : cmd.getAliases()) {
                                if (args[0].equalsIgnoreCase(cmdAlias)) {
                                    use = true;
                                    break;
                                }
                            }
                        }
                        if(use) {
                            if(args.length - 1 == i) {
                                if(sender.hasPermission(pluginPermission + ".*")) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else if(cmd.getPermission().equalsIgnoreCase("")) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else{
                                    return emptyList;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return emptyList;
            }
        }
        return emptyList;
    }

    public static boolean worldExist(String name) {
        if(Bukkit.getWorld(name) != null) {
            return true;
        }
        return false;
    }

    public static boolean deleteWorld(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        return(path.delete());
    }

    public static List<String> createReturnList(List<String> completions, String currentArg) {
        if (currentArg.length() <= 0) { return completions; }

        currentArg = currentArg.toLowerCase(Locale.ROOT);
        List<String> returnedCompletions = new ArrayList<>();

        for (String str : completions) {
            if (str.toLowerCase(Locale.ROOT).contains(currentArg)) {
                returnedCompletions.add(str);
            }
        }

        return returnedCompletions;
    }
}
