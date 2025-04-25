package me.nitkanikita21.customblocks.core.listener;

import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@RequiredArgsConstructor
public class VanillaCommandListener implements Listener {
    private final ServerBlockManager serverBlockManager;

    @EventHandler(ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        if(command.equalsIgnoreCase("stop")){
            event.setCancelled(true);
            serverBlockManager.saveAll();
            Bukkit.getServer().shutdown();
        }
        if(command.equalsIgnoreCase("save-all")) {
            serverBlockManager.saveAll();
        }
    }

}
