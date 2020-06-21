package main.java.event;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerQuit implements Listener
{
    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event)
    {

        Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), () ->
        {
            if (event.getPlayer() != null)
            {
                Player p = event.getPlayer();
                PlayertoSql.getInstance().getPlayerManager().onPlayerQuit(p.getUniqueId().toString(),p.getInventory().getStorageContents(),p.getInventory().getArmorContents(),p.getInventory().getExtraContents(),p.getEnderChest().getContents());
            }
        }, 2L);

    }
}
