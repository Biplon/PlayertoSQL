package pts.java.event;

import com.mysql.jdbc.Buffer;
import org.bukkit.Bukkit;
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
            Bukkit.getLogger().warning(ev.getPlayer() + " Ist ein arsch");
            ev.setCancelled(true);
        }
    }
}
