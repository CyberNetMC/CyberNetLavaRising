package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.cyberapi.player.CyberPlayer;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.commands.StartCMD;
import net.cybercake.lavarisingplugin.runnables.StartSequence;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class JoinLeaveEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CyberPlayer cyberPlayer = new CyberPlayer(player);

        switch(State.get()) {
            case PREGAME, COUNTING -> {

                event.joinMessage(UChat.component("&8[&a+&8] " + cyberPlayer.getDisplayName() + " &ejoined your game! &6(&e" + Bukkit.getOnlinePlayers().size() + "&6/&e" + Bukkit.getMaxPlayers() + "&6)"));

                player.getInventory().setHeldItemSlot(0);
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);

                for(PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }

                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.setTotalExperience(0);
                player.setExp(0);
                player.setLevel(0);
                player.teleport(new Location(Main.world, 0.5, Main.yLevelSphere-2, 0.5, 0, 0));
                
                player.getInventory().setItem(8, Main.lobbyItem);

                if(Bukkit.getOnlinePlayers().size() >= Main.getMainConfig().getInt("minToStart")) {
                    if(!StartCMD.manualControl) new StartSequence().start(Main.getMainConfig().getInt("timeTillStartOnceFilled"));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CyberPlayer cyberPlayer = new CyberPlayer(player);

        switch(State.get()) {
            case PREGAME, COUNTING -> {
                event.quitMessage(UChat.component("&8[&c-&8] " + cyberPlayer.getDisplayName() + " &eleft your game! &6(&e" + Bukkit.getOnlinePlayers().size() + "&6/&e" + Bukkit.getMaxPlayers() + "&6)"));

                if(State.equals(State.Game.COUNTING)) Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                    Log.info("Checking if start should be cancelled... (" + Bukkit.getOnlinePlayers().size() + " < " + Main.getMainConfig().getInt("minToStart") + ") [" + (Bukkit.getOnlinePlayers().size() < Main.getMainConfig().getInt("minToStart")) + "]");
                    if(Bukkit.getOnlinePlayers().size() < Main.getMainConfig().getInt("minToStart")) {
                        if(!StartCMD.manualControl) new StartSequence().cancel("Not enough players!");
                    }
                }, 5L);
            }
        }
    }

}
