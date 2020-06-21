package main.java;

import main.java.database.DatabaseManager;
import main.java.event.PlayerJoin;
import main.java.event.PlayerQuit;
import main.java.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class PlayertoSql extends JavaPlugin
{
    static PlayertoSql instance;

    static PlayerManager playerManager;

    public void onEnable()
    {
        instance = this;
        try
        {
            ConfigManager.loadConfig();
            new DatabaseManager();
            playerManager = new PlayerManager();
            PluginManager pm = getServer().getPluginManager();
            pm.registerEvents(new PlayerJoin(), this);
            pm.registerEvents(new PlayerQuit(), this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[PlayertoSql] has been enabled!");
    }

    public static PlayertoSql getInstance()
    {
        return instance;
    }

    public PlayerManager getPlayerManager()
    {
        return playerManager;
    }
}
