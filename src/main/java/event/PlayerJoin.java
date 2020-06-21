package main.java.event;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener
{
    @EventHandler
    public void onLogin(final PlayerJoinEvent event)
    {
            final Player p = event.getPlayer();
            Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), () ->
            {
                if (p != null)
                {
                    if (p.isOnline())
                    {
                        PlayertoSql.getInstance().getPlayerManager().onPlayerJoin(p);
                    }
                }
            }, 5L);

    }
}
