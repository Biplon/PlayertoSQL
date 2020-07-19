package pts.java.event;

import pts.java.PlayertoSql;
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
        //get player from quit event
        Player p = event.getPlayer();
        //start async player save
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false));
    }
}
