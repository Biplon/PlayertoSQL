package main.java.api;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerManagement
{
    public static PlayerManagement instance;

    public PlayerManagement()
    {
        instance = this;
    }

    public static PlayerManagement getInstance()
    {
        return instance;
    }
    //check if player is not on disabledPlayerSaved list
    //if not then start a task with 1 tick delay to save player
    @SuppressWarnings("unused")
    public boolean savePlayerSync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledPlayerSaved.contains(p.getUniqueId()))
        {
            Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), ()->  PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false));
            return true;
        }
        return false;
    }
    //start a task with 1 tick delay to save player
    @SuppressWarnings("unused")
    public boolean savePlayerIgnoreDisableSync(Player p)
    {
        Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), ()-> PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),true));
        return true;
    }
    //check if player is not on disabledPlayerSaved list
    //if not then start a async task to save player
    @SuppressWarnings("unused")
    public boolean savePlayerAsync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledPlayerSaved.contains(p.getUniqueId()))
        {
            Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),false));
            return true;
        }
        return false;
    }
    //start a async task to save player
    @SuppressWarnings("unused")
    public boolean savePlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().savePlayer(p.getUniqueId(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(),true));
        return true;
    }
    //add player to DisablePlayerSave list
    @SuppressWarnings("unused")
    public boolean disablePlayerSave(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().addDisablePlayerSave(p.getUniqueId());
        return true;
    }
    //remove player to DisablePlayerSave list
    @SuppressWarnings("unused")
    public boolean enablePlayerSave(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().removeDisablePlayerSave(p.getUniqueId());
        return true;
    }
    //check if player is not on disabledPlayerLoaded list
    //if not then start a task with 1 tick delay to load player
    @SuppressWarnings("unused")
    public boolean loadPlayerSync(Player p)
    {
        if (!PlayertoSql.getInstance().getPlayerManager().disabledPlayerLoaded.contains(p.getUniqueId()))
        {
            Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), ()->  PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,false));
            return true;
        }
        return false;
    }
    //start a task with 1 tick delay to load player
    @SuppressWarnings("unused")
    public boolean loadPlayerIgnoreDisableSync(Player p)
    {
        Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), ()->  PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,true));
        return true;
    }
    //check if player is not on disabledPlayerLoaded list
    //if not then start a async task to load player
    @SuppressWarnings("unused")
    public boolean loadPlayerAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,false));
        return true;
    }
    //start a async task to load player
    @SuppressWarnings("unused")
    public boolean loadPlayerIgnoreDisableAsync(Player p)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                PlayertoSql.getInstance().getPlayerManager().loadPlayer(p,true));
        return true;
    }
    //add player to DisablePlayerLoad list
    @SuppressWarnings("unused")
    public boolean disablePlayerLoad(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().addDisablePlayerLoad(p.getUniqueId());
        return true;
    }
    //remove player to DisablePlayerLoad list
    @SuppressWarnings("unused")
    public boolean enablePlayerLoad(Player p)
    {
        PlayertoSql.getInstance().getPlayerManager().removeDisablePlayerLoad(p.getUniqueId());
        return true;
    }
}
