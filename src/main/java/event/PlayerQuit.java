package main.java.event;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener
{
    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
        {
            Player p = event.getPlayer();
            PlayertoSql.getInstance().getPlayerManager().onPlayerQuit(p.getUniqueId().toString(), p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
        });
    }
}
