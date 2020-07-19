package pts.java.event;

import pts.java.ConfigManager;
import pts.java.PlayertoSql;
import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener
{
    @EventHandler
    public void onLogin(final PlayerJoinEvent event)
    {
        //on player join start async loading
        Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().onPlayerJoin(event.getPlayer()), ConfigManager.joinLoadDelay);
    }
}
