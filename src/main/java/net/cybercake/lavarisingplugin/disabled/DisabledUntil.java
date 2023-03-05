package net.cybercake.lavarisingplugin.disabled;

import net.cybercake.lavarisingplugin.Main;
import org.bukkit.Bukkit;

public class DisabledUntil implements Runnable{

    @Override
    public void run() {
        if(Main.disabled) {
            if(Main.disabledUntilRestart <= 0) {
                Bukkit.shutdown();
            }
            Main.disabledUntilRestart--;
        }
    }
}
