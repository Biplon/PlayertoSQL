package pts.java.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import pts.java.PlayertoSql;

public class OnDrop implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent ev)
    {
        if (PlayertoSql.getInstance().getPlayerManager().disableDrop.contains(ev.getPlayer()))
        {
            ev.setCancelled(true);
        }
    }
}
