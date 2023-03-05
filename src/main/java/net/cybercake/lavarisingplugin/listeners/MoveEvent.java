package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.Log;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class MoveEvent implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!State.get().equals(State.Game.ACTIVE)) return;
        if(player.getLocation().subtract(0, 1, 0).getBlock().isEmpty()) return;
        if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        Log.info("Remove invisibility and slow falling from " + player.getName() + "...");
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SLOW_FALLING);

        Log.info("Adding items to " + player.getName() + "'s inventory (starting items)...");
        for(String str : Main.getMainConfig().getConfigurationSection("startingItems").getKeys(false)) {
            ItemStack item = new ItemStack(
                    Material.valueOf(
                            Main.getMainConfig().getString(
                                    "startingItems." + str + ".material"
                            )
                    ),
                    Main.getMainConfig().getInt(
                            "startingItems." + str + ".amount"
                    )
            );
            player.getInventory().addItem(item);
        }
    }

}
