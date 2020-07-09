package main.java.player;

import main.java.ConfigManager;
import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        //check if player online
        if (!Bukkit.getOnlinePlayers().isEmpty())
        {
            //get all player
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            //for each player check if online and save
            for (Player p : onlinePlayers)
            {
                if (p.isOnline())
                {
                    pm.savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false);
                }
            }
        }
        //if all player saved try to save player unsaved player
        PlayertoSql.getInstance().getPlayerManager().trySaveMissingPlayerData(false);
    }

    public void onShutdownAutosave()
    {
        //get all player
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        //for each player check if online and save
        for (Player p : onlinePlayers)
        {
            if (p.isOnline())
            {
                pm.savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false);
            }
        }
    }
}
