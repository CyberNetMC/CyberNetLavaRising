package net.cybercake.lavarisingplugin.commands.worldscommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    private final String name;
    private final String permission;
    private final String[] aliases;
    private final String description;
    private final String usage;

    public SubCommand(String name, String permission, String description, String usage, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
    }

    public String getName() { return this.name; }

    public String getPermission() { return this.permission; }

    public String[] getAliases() { return this.aliases; }

    public String getDescription() { return this.description; }

    public String getUsage() { return this.usage; }

    public abstract void perform(CommandSender sender, String[] args, Command command);

    public abstract List<String> tab(CommandSender sender, String[] args);

}