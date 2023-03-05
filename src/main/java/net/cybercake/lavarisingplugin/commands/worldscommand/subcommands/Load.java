package net.cybercake.lavarisingplugin.commands.worldscommand.subcommands;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.YamlUtils;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.exceptions.BetterStackTraces;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.commands.worldscommand.CommandManager;
import net.cybercake.lavarisingplugin.commands.worldscommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Load extends SubCommand {

    public Load() {
        super("load", "worlds.load", "Loads a world or creates a world based on if it exists or not.", "/worlds load <worldName> <worldType>", "import", "create");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if (args.length < 3) {
            sender.sendMessage(UChat.component("&cInvalid usage! &7" + this.getUsage())); return;
        }

        try {
            WorldType type = null;
            if(args.length == 3) {
                for(WorldType types : WorldType.values()) {
                    if(args[2].toLowerCase(Locale.ROOT).equals(types.getName().toLowerCase(Locale.ROOT))) {
                        type = types;
                        break;
                    }
                }
            }

            if(type == null) {
                ArrayList<String> types = new ArrayList<>();
                for(WorldType type1 : WorldType.values()) {
                    types.add(type1.getName().toLowerCase(Locale.ROOT));
                }

                sender.sendMessage(UChat.component("&cInvalid world type! Valid are: &7" + types)); return;
            }

            if(CommandManager.worldExist(args[1])) {
                sender.sendMessage(UChat.component("&cA world is already loaded with the name " + args[1])); return;
            }else{
                File file = new File(Paths.get("").toAbsolutePath() + "/" + args[1]);
                if(file.exists() && !(folderHasDat(file))) {
                    sender.sendMessage(UChat.component("&cThe following file does not seem to be a world: &8" + args[1]));
                }
            }

            sender.sendMessage(UChat.component("&7&oCreating or loading a new world... please wait!"));

            WorldCreator worldCreator = new WorldCreator(args[1].toLowerCase(Locale.ROOT));
            worldCreator.type(type);
            worldCreator.createWorld();
            if(Bukkit.getWorld(args[1]) != null) {
                sender.sendMessage(UChat.component("&6Successfully created or loaded a new world names &a" + args[1] + "&6!"));

                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".name", args[1].toLowerCase(Locale.ROOT));
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".key", Bukkit.getWorld(args[1]).getKey().toString());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loaded", true);
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedBy", sender.getName());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedOriginal", Time.getUnix());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".type", type.getName().toUpperCase(Locale.ROOT));
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".spawnLocation", Bukkit.getWorld(args[1]).getSpawnLocation());

                if(sender instanceof Player player) {
                    player.teleport(SetSpawn.getWorldSpawn(args[1]));
                }
            }else if(Bukkit.getWorld(args[1]) == null) {
                sender.sendMessage(UChat.component("&c"));
            }
        } catch (Exception exception) {
            Spigot.error(sender, "during the world creation process for {name}", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(allWorldsPlusUnloaded(true), args[1]);
        }else if(args.length == 3) {
            ArrayList<String> types = new ArrayList<>();
            for(WorldType type : WorldType.values()) {
                types.add(type.getName().toLowerCase(Locale.ROOT));
            }
            return CommandManager.createReturnList(types, args[2]);
        }
        return CommandManager.emptyList;
    }

    public static void setIfNull(String path, Object toWhat) {
        if(YamlUtils.getCustomYmlObject("worlds", path) == null) {
            YamlUtils.setCustomYml("worlds", path, toWhat);
        }
    }

    public static ArrayList<String> allWorldsPlusUnloaded(boolean onlyUnloaded) {
        ArrayList<String> worlds = new ArrayList<>();
        for(String normalFolder : new File(Paths.get("").toAbsolutePath() + "/").list()) {
            if(onlyUnloaded) {
                if(folderHasDat(new File(Paths.get("").toAbsolutePath() + "/" + normalFolder + "/")) && Bukkit.getWorld(normalFolder) == null) {
                    worlds.add(normalFolder);
                }
            }else{
                if(folderHasDat(new File(Paths.get("").toAbsolutePath() + "/" + normalFolder + "/"))) {
                    worlds.add(normalFolder);
                }
            }
        }
        return worlds;
    }

    public static boolean folderHasDat(File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.toLowerCase(Locale.ROOT).endsWith(".dat"));
        return files != null && files.length > 0;
    }

    public static void loadWorld(String worldName) {
        // Make sure the world does not exist and is unloaded
        if(Bukkit.getWorld(worldName) != null) {
            return;
        }

        try {
            // Create a new world
            WorldCreator worldCreator = new WorldCreator(worldName.toLowerCase(Locale.ROOT));
            worldCreator.createWorld();

            // If successful, should pop out in console it has been
            if(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName))) {
                Log.info("Successfully loaded new world " + worldName);
            } else{
                Log.info("An error occurred whilst trying to load the world " + worldName + " [...] failed to find!");
            }
        } catch (Exception exception) {
            // If unsuccessful, show internal error
            Log.error("An error occurred whilst trying to load the world " + worldName);
            Log.error(" ");
            BetterStackTraces.print(exception);
        }
    }
}