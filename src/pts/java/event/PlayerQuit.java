package pts.java.event;

import org.bukkit.inventory.ItemStack;
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
        ItemStack[] inv = p.getInventory().getStorageContents();
        ItemStack[] inva =  p.getInventory().getArmorContents();
        ItemStack[] invae = p.getInventory().getExtraContents();
        ItemStack[] inven = p.getEnderChest().getContents();
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), inv,inva,invae ,inven ,false));
        p.getInventory().clear();
    }
}
