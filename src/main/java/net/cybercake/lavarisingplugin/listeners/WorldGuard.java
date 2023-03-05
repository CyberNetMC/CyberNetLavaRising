package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.Log;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.cyberapi.player.CyberPlayer;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.commands.Lava;
import net.cybercake.lavarisingplugin.runnables.MainTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class WorldGuard implements Listener {

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getFinalDamage() <= 6.0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
        if(State.equals(State.Game.ACTIVE) && event.getBlock().getLocation().getBlockY() <= MainTask.currentLevel-1) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(UChat.component("&cYou cannot place blocks below the lava!"));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOffhandSwitch(PlayerSwapHandItemsEvent event) {
        if(!State.equals(State.Game.ACTIVE)) {
            event.setCancelled(true);
        }
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {


        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
            Spigot.sendTitle(event.getPlayer(), "&c&lPAUSED!", "&7An admin has paused this game... please wait!", 0, 100, 20);
        }
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) return;

        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobTargetEntity(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player)) return;

        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(Lava.paused && Lava.everyonePaused) {
            event.setCancelled(true);
            Log.warn("An error occurred whilst kicking " + event.getPlayer().getName() + "! Reason: " + event.getReason());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!(event.isCancelled()) && Main.blacklistedItems.contains(event.getPlayer().getInventory().getItemInMainHand().getType()) && !event.getPlayer().hasPermission("admin.lavarising.bypassplacecheck")) {
            event.setCancelled(true);
            if(event.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("BUCKET")) {
                event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.IRON_INGOT, event.getPlayer().getInventory().getItemInMainHand().getAmount()*3));
                return;
            }
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
        }
    }

    @EventHandler
    public void onBreak(BlockDamageEvent event) {
        if(State.equals(State.Game.ACTIVE) && Main.instaBreak.contains(event.getBlock().getType())) {
            event.setInstaBreak(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 2F, 1F);
        }
    }

}
