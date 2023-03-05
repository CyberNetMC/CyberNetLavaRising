package net.cybercake.lavarisingplugin.listeners;

import net.cybercake.cyberapi.Log;
import net.cybercake.lavarisingplugin.Main;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(Main.getMainConfig().getStringList("deaths").contains(PlainTextComponentSerializer.plainText().serialize(event.deathMessage()))) {

        }
    }

}
