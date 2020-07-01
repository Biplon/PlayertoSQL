package main.java.api;

import main.java.PlayertoSql;
import main.java.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Playermanagement
{

    public boolean savePlayerSync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayersaves.contains(p.getUniqueId().toString()))
        {
            PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
            return true;
        }
        return false;
    }

    public boolean savePlayerIgnoreDisableSync(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
        return true;
    }

    public boolean savePlayerAsync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayersaves.contains(p.getUniqueId().toString()))
        {
            Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents()));
            return true;
        }
        return false;
    }

    public boolean savePlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents()));
        return true;
    }

    public boolean disablePlayerSave(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().addDisablePlayerSave(p.getUniqueId().toString());
        return true;
    }

    public boolean enablePlayerSave(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().removeDisablePlayerSave(p.getUniqueId().toString());
        return true;
    }

    public boolean loadPlayerSync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayerload.contains(p.getUniqueId().toString()))
        {
            PlayertoSql.getInstance().getPlayerManager().loadPlayer(p);
            return true;
        }
        return false;
    }

    public boolean loadPlayerIgnoreDisableSync(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().loadPlayer(p);
        return true;
    }

    public boolean loadPlayerAsync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayerload.contains(p.getUniqueId().toString()))
        {
            Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    PlayertoSql.getInstance().getPlayerManager().loadPlayer(p));
            return true;
        }
        return false;
    }

    public boolean loadPlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().loadPlayer(p));
        return true;
    }

    public boolean disablePlayerLoad(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().addDisablePlayerLoad(p.getUniqueId().toString());
        return true;
    }

    public boolean enablePlayerLoad(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().removeDisablePlayerLoad(p.getUniqueId().toString());
        return true;
    }
}
