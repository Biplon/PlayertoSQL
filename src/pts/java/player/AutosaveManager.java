package pts.java.player;

import pts.java.ConfigManager;
import pts.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pts.java.database.DatabaseManager;

public class AutosaveManager
{
    PlayerManager pm;

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
        Bukkit.getScheduler().runTaskTimerAsynchronously(PlayertoSql.getInstance(), this::autosave, ConfigManager.autosaveInterval * 60 * 20L, ConfigManager.autosaveInterval * 60 * 20L);
    }

    //try to autosave player
    private void autosave()
    {
        DatabaseManager.getInstance().checkConnection();
        if (!Bukkit.getOnlinePlayers().isEmpty())
        {
            //for each player check if online and save

            for (Player p : Bukkit.getOnlinePlayers())
            {
                //check if player online
                if (p != null && p.isOnline())
                {
                    pm.savePlayer(p, p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(), false, "autosave");
                }
            }
        }
        //if all player saved try to save player unsaved player
        PlayertoSql.getInstance().getPlayerManager().trySaveMissingPlayerData(false);
    }
}
