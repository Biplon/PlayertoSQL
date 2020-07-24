package pts.java.player;

import org.bukkit.inventory.ItemStack;
import pts.java.ConfigManager;
import pts.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AutosaveManager
{
    PlayerManager pm;

    List<Player> ap = new ArrayList<>();

    public AutosaveManager()
    {
        //get playerManager
        pm = PlayertoSql.getInstance().getPlayerManager();

        //start autosave task
        runTask();
    }

    //start async timer task for autosave
    private void runTask()
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PlayertoSql.getInstance(), this::prepareautosave, ConfigManager.autosaveInterval * 60 * 20L, ConfigManager.autosaveInterval * 60 * 20L);
    }


    private void prepareautosave()
    {
        ap = new ArrayList<>(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), this::autosave, 100);
    }
    //try to autosave player
    private void autosave()
    {
        if (!Bukkit.getOnlinePlayers().isEmpty())
        {
            //for each player check if online and save

            for (Player p : ap)
            {
                //check if player online
                if (p != null && p.isOnline())
                {
                    pm.savePlayer(p, p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false,"autosave");
                }
                else
                {
                    PlayertoSql.getInstance().getLogger().warning("Player not auto saved! Player is offline");
                }
            }
        }
        ap.clear();
        //if all player saved try to save player unsaved player
        PlayertoSql.getInstance().getPlayerManager().trySaveMissingPlayerData(false);
    }

    /*
    public void onShutdownAutosave()
    {
        //get all player
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        //for each player check if online and save

        for (Player p : onlinePlayers)
        {
            if (p.isOnline())
            {
                ItemStack[] inv = p.getInventory().getStorageContents();
                ItemStack[] inva =  p.getInventory().getArmorContents();
                ItemStack[] invae = p.getInventory().getExtraContents();
                ItemStack[] inven = p.getEnderChest().getContents();
                pm.savePlayer(p, inv,inva,invae ,inven ,false,"autosaveSD");
            }
            else
            {
                PlayertoSql.getInstance().getLogger().warning("Player not auto saved! Player is offline");
            }
        }
    }

     */
}
