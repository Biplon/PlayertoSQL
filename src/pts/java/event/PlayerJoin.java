package pts.java.event;

import org.bukkit.event.EventPriority;
import pts.java.ConfigManager;
import pts.java.PlayertoSql;
import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pts.java.player.PlayerManager;

public class PlayerJoin implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(final PlayerJoinEvent event)
    {
        //on player join start async loading
        Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().onPlayerJoin(event.getPlayer()), ConfigManager.joinLoadDelay);
    }
}
