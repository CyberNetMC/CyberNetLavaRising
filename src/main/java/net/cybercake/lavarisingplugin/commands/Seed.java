package net.cybercake.lavarisingplugin.commands;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.chat.UTabComp;
import net.cybercake.lavarisingplugin.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Seed implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String text = UChat.chat("&3&lSeed: &f" + Main.world.getSeed());

        Component component =
                Component.text(text)
                        .hoverEvent(HoverEvent.showText(UChat.component("&3&l> &bClick here to copy the seed for &f" + Main.world.getName() + "&b!")))
                        .clickEvent(ClickEvent.copyToClipboard(String.valueOf(Main.world.getSeed())));

        if(sender instanceof Player player) {
            player.sendMessage(component); return true;
        }
        sender.sendMessage(text);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return UTabComp.emptyList;
    }
}
