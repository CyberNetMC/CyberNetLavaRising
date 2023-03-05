package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.items.Item;
import net.cybercake.cyberapi.player.CyberPlayer;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ReturnToLobby implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!Item.compare(player.getInventory().getItemInMainHand().clone(), Main.lobbyItem)) return;
        event.setCancelled(true);
        if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

        if(State.equals(State.Game.PREGAME) || State.equals(State.Game.COUNTING)) {
            player.sendMessage(UChat.component("&6Sending you to &alobby&6!"));
            CyberPlayer cyberPlayer = new CyberPlayer(player);
            cyberPlayer.connect("lobby");
        } else {
            String logMsg = "Server tried to send player to lobby using ReturnToLobby item even though the server's state is not PREGAME, COUNTING";
            Log.warn(logMsg);
            player.sendMessage(UChat.component(ChatColor.YELLOW + "[" + Time.getFormattedDate("HH:mm:ss") + " WARN] [LavaRising] " + logMsg));
        }
    }

}
