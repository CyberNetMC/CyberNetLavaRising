package net.cybercake.lavarisingplugin.runnables;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.player.CyberPlayer;
import net.cybercake.lavarisingplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabList implements Runnable {

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CyberPlayer cyberPlayer = new CyberPlayer(player);

            player.sendPlayerListHeader(UChat.component(PlaceholderAPI.setPlaceholders(player,
                    "&6You are currently playing on &a&lLava Rising\n\n" +
                    "&6&lInformation:\n" +
                    "&b&l> &fPlayers: &a" + Bukkit.getOnlinePlayers().size() + "&2/&a" + Bukkit.getMaxPlayers() + " &b&l<\n" +
                            "&e&l> &fTPS: %spark_tps_5s% &e&l<\n" +
                            "&d&l> &fPing: " + cyberPlayer.getColoredPing() + " &d&l<\n"
            )));
            player.sendPlayerListFooter(UChat.component(PlaceholderAPI.setPlaceholders(player,
                    "\n&e" + Main.getMainConfig().getString("ip") + " &7(" + Bukkit.getMinecraftVersion() + ")"
            )));
        }
    }
}
