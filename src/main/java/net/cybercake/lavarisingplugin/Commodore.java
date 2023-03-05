package net.cybercake.lavarisingplugin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.command.PluginCommand;

import java.io.IOException;

public class Commodore {

    public static void registerCommodoreCommand(PluginCommand pluginCommand, String fileName) throws IOException {
        LiteralCommandNode<?> timeCommand = CommodoreFileFormat.parse(Main.get().getResource("commodore/" + fileName + ".commodore"));
        CommodoreProvider.getCommodore(Main.get()).register(pluginCommand, timeCommand);
    }
}
