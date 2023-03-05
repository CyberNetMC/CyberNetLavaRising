package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerPVP implements Listener {

    public static boolean pvpEnabled = false;
    public static int pvpEnablesYLevel = 0;

    public static HashMap<String, BukkitRunnable> playersPerRunnable = new HashMap<>();

    public static HashMap<String, String> inCombatWith = new HashMap<>();
    public static HashMap<String, Long> inCombatUntil = new HashMap<>();

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player victim)) return;
        if(!(event.getDamager() instanceof Player attacker)) return;
        if(!State.equals(State.Game.ACTIVE)) return;

        if(!pvpEnabled) {
            event.setCancelled(true);

            attacker.sendActionBar(UChat.component("&cPvP has NOT been enabled yet!"));

            return;
        }

        setPvP(attacker, victim);
        setPvP(victim, attacker);
    }

    public void setPvP(Player player, Player other) {
        if(playersPerRunnable.containsKey(player.getName())) {
            playersPerRunnable.get(player.getName()).cancel();
            playersPerRunnable.remove(player.getName());
            inCombatWith.remove(player.getName());
            inCombatUntil.remove(player.getName());
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(inCombatUntil.get(player.getName())-System.currentTimeMillis() <= 0) {
                    playersPerRunnable.remove(player.getName());
                    inCombatWith.remove(player.getName());
                    inCombatUntil.remove(player.getName());
                    this.cancel();
                }
            }
        };
        inCombatWith.put(player.getName(), other.getName());
        inCombatUntil.put(player.getName(), System.currentTimeMillis()+16000L);
        runnable.runTaskTimer(Main.get(), 0L, 10L);
        playersPerRunnable.put(player.getName(), runnable);
    }

    public void message(boolean pvp) {
        sendAll(UChat.getSeperator(pvp ? ChatColor.GREEN : ChatColor.RED));
        if(pvp) {
            sendAll(UChat.chat("&a&lPvP has been enabled!"));;
            sendAll(UChat.chat("&7&oPlayers can now fight among each other! Good luck ;)"));
            Bukkit.getOnlinePlayers().forEach(player -> Spigot.sendTitle(player, "&a&lPvP", "&a&lhas been enabled!", 0, 80, 20));
        }else{
            sendAll(UChat.chat("&c&lPvP has been disabled!"));;
            sendAll(UChat.chat("&7&oWell hey, at least your 'teammate' cannot stab you in the back now!"));;
            Bukkit.getOnlinePlayers().forEach(player -> Spigot.sendTitle(player, "&c&lPvP", "&c&lhas been disabled!", 0, 80, 20));
        }
        sendAll(UChat.getSeperator(pvp ? ChatColor.GREEN : ChatColor.RED));
    }

    public void sendAll(String msg) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(UChat.component(msg)));
    }

}
