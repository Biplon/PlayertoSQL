package main.java;

import main.java.database.DatabaseManager;
import main.java.event.PlayerJoin;
import main.java.event.PlayerQuit;
import main.java.player.AutosaveManager;
import main.java.player.CommandHandler;
import main.java.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayertoSql extends JavaPlugin
{
    static PlayertoSql instance;

    AutosaveManager autosaveManager;

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
            this.getCommand("ptsclear").setExecutor(new CommandHandler());
            if (ConfigManager.getConfigvalueString("general.autosave").equals("true"))
            {
                autosaveManager = new AutosaveManager();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[PlayertoSql] has been enabled!");
    }

    @Override
    public void onDisable()
    {
        playerManager.trySaveMissingPlayerData(true);
        if (ConfigManager.getConfigvalueString("general.autosave").equals("true"))
        {
            autosaveManager.onShutDownautosave();
        }
        DatabaseManager.getInstance().closeConnection();
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info("[PlayertoSql] has been disabled!");
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
