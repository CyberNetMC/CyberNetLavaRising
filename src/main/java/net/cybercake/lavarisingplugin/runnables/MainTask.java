package net.cybercake.lavarisingplugin.runnables;

import net.cybercake.cyberapi.Time;
import net.cybercake.cyberapi.chat.UChat;
import net.cybercake.cyberapi.instances.Spigot;
import net.cybercake.lavarisingplugin.Main;
import net.cybercake.lavarisingplugin.State;
import net.cybercake.lavarisingplugin.commands.Lava;
import net.cybercake.lavarisingplugin.listeners.PlayerPVP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTask implements Runnable {

    public static long lavaRiseStatic;
    public static long lavaRiseNext;
    public static int currentLevel;

    public static double pausedBossbarProgress;
    public static boolean pausedBossbarUp;

    public static ArrayList<BukkitRunnable> lavaRunnables = new ArrayList<>();
    public static HashMap<Integer, BukkitRunnable> taskIDToRunnableObject = new HashMap<>();
    public static HashMap<BukkitRunnable, Integer> lavaRunnableYLevel = new HashMap<>();
    public static HashMap<BukkitRunnable, Long> lavaRunnableStarted = new HashMap<>();

    @Override
    public void run() {
        if(!State.equals(State.Game.ACTIVE)) return;

        if((lavaRiseNext - Time.getUnix()) <= 0 && !Lava.paused) {
            lavaRiseNext = Time.getUnix()+lavaRiseStatic;
            currentLevel++;

            if(currentLevel == PlayerPVP.pvpEnablesYLevel) {
                PlayerPVP.pvpEnabled = true;
                new PlayerPVP().message(true);
                Bukkit.getOnlinePlayers().forEach(player -> Spigot.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 2F, 2F));

                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(UChat.component(" &b>>> &fThe lava has been sped up to rise every &c5 seconds&f!")));
                MainTask.lavaRiseStatic = 5;
            }else if(currentLevel == PlayerPVP.pvpEnablesYLevel-1 || currentLevel == PlayerPVP.pvpEnablesYLevel-2 || currentLevel == PlayerPVP.pvpEnablesYLevel-4) {
                Bukkit.getOnlinePlayers().forEach(player -> Spigot.playSound(player, Sound.UI_BUTTON_CLICK, 2F, 2F));
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(UChat.component("&6&lPvP enables in &c&l" + (lavaRiseStatic*(PlayerPVP.pvpEnablesYLevel-currentLevel)) + " &c&lseconds&6&l!")));
            }

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 2F, 1F));

            Location location = new Location(Main.world, -50.0, MainTask.currentLevel-1, -51.0);
            double wbSize = Main.world.getWorldBorder().getSize();
            double forLoopAmount = Math.ceil((wbSize*wbSize)/lavaRiseStatic/20);
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(!lavaRunnables.contains(this)) {
                        lavaRunnableStarted.put(this, System.currentTimeMillis());
                        lavaRunnables.add(this);
                        lavaRunnableYLevel.put(this, location.getBlockY());
                        taskIDToRunnableObject.put(this.getTaskId(), this);
                    }

                    if(Lava.paused) return;
                    for(int i=0; i<forLoopAmount; i++) {
                        if(Lava.paused) return;

                        location.setZ(location.getBlockZ()+1);
                        if(location.getBlockX() >= 51) {
                            cancelTask(this);
                            return;
                        }
                        if(location.getBlockZ() >= 51) {
                            location.setX(location.getBlockX()+1);
                            location.setZ(-50.0);
                        }

                        location.getBlock().setType(Material.LAVA);
                    }
                }
            };
            runnable.runTaskTimer(Main.get(), 0L, 1L);
        }
        if(Lava.paused) {
            Lava.whenPaused = lavaRiseNext - Time.getUnix();
            if(pausedBossbarUp) {
                pausedBossbarProgress = pausedBossbarProgress+0.01;
                if(pausedBossbarProgress >= 1.0) {
                    pausedBossbarUp = false;
                }
            }else if(!pausedBossbarUp) {
                pausedBossbarProgress = pausedBossbarProgress-0.01;
                if(pausedBossbarProgress <= 0.0) {
                    pausedBossbarUp = true;
                }
            }
            try {
                StartSequence.bossBar.setProgress(pausedBossbarProgress);
                StartSequence.bossBar.setTitle(UChat.chat("&4The lava is currently &c&lPAUSED&4!"));
            } catch (Exception exception) {
                StartSequence.bossBar.setProgress(0.0);
                StartSequence.bossBar.setTitle(UChat.chat("&c&lERR: &8" + exception));
            }

            return;
        }

        try {
            double parsedDoubleStatic = Double.parseDouble(String.valueOf(lavaRiseStatic));
            double parsedDoubleNext = Double.parseDouble(String.valueOf(lavaRiseNext-Time.getUnix()));
            StartSequence.bossBar.setProgress(Math.min((parsedDoubleNext / parsedDoubleStatic), 1.0));

            StartSequence.bossBar.setTitle(UChat.chat("&6The lava will rise in &c" + (lavaRiseNext-Time.getUnix()) + " &cseconds&6! Currently at &cy = " + currentLevel));
        } catch (Exception exception) {
            StartSequence.bossBar.setProgress(0.0);
            StartSequence.bossBar.setTitle(UChat.chat("&c&lERR: &8" + exception));
        }
    }

    public void cancelTask(BukkitRunnable task) {
        lavaRunnables.remove(task);
        taskIDToRunnableObject.remove(task.getTaskId());
        lavaRunnableYLevel.remove(task);
        lavaRunnableStarted.remove(task);
        task.cancel();
    }
}
