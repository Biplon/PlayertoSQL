package main.java.api;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Playermanagement
{
    public static Playermanagement instance;

    public Playermanagement()
    {
        instance = this;
    }

    public static Playermanagement getInstance()
    {
        return instance;
    }

    public boolean savePlayerSync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayersaves.contains(p.getUniqueId().toString()))
        {
            PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false);
            return true;
        }
        return false;
    }

    public boolean savePlayerIgnoreDisableSync(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),true);
        return true;
    }

    public boolean savePlayerAsync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledplayersaves.contains(p.getUniqueId().toString()))
        {
            Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false));
            return true;
        }
        return false;
    }

    public boolean savePlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),true));
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
            PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,false);
            return true;
        }
        return false;
    }

    public boolean loadPlayerIgnoreDisableSync(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,true);
        return true;
    }

    public boolean loadPlayerAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,false));
        return true;
    }

    public boolean loadPlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,true));
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
