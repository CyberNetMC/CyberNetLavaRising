package net.cybercake.lavarisingplugin.disabled;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.lavarisingplugin.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if(Main.disabled) {
            Main.regenFailed();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, UChat.component("" +
                    UChat.getSeperator(ChatColor.DARK_RED) + "\n" +
                    UChat.chat("&c&lYou are not allowed to join this server!") + "\n" +
                    UChat.chat("&7An error occurred with regenerating the world. Contact an administrator to allow you to join this server again! The server will automatically attempt to repair itself via a restart in " + Main.disabledUntilRestart + " seconds.") + "\n" +
                    UChat.getSeperator(ChatColor.DARK_RED)
            ));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(Main.disabled) {
            Main.regenFailed();
        }
    }

}
