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
        pm = PlayertoSql.getInstance().getPlayerManager();
        runTask();
    }

    private void runTask()
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PlayertoSql.getInstance(), () -> autosave(), ConfigManager.getConfigvalueInt("general.autosaveinterval") * 60 * 20L, ConfigManager.getConfigvalueInt("general.autosaveinterval") * 60 * 20L);
    }

    private void autosave()
    {
        if (!Bukkit.getOnlinePlayers().isEmpty())
        {
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player p : onlinePlayers)
            {
                if (p.isOnline())
                {
                    pm.savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
                }
            }
            onlinePlayers.clear();
        }
        PlayertoSql.getInstance().getPlayerManager().trySaveMissingPlayerData(false);
    }

    public void onShutDownautosave()
    {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (Player p : onlinePlayers)
        {
            if (p.isOnline())
            {
                pm.savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
            }
        }
    }
}
