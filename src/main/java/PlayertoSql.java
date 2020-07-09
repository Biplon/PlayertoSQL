package main.java;

import main.java.api.PlayerManagement;
import main.java.command.*;
import main.java.database.DatabaseManager;
import main.java.event.PlayerJoin;
import main.java.event.PlayerQuit;
import main.java.player.AutosaveManager;
import main.java.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayertoSql extends JavaPlugin
{
    static PlayertoSql instance;

    AutosaveManager autosaveManager;

    static PlayerManager playerManager;

    //on enable plugin load config reg events and commands check if autosave an start it
    public void onEnable()
    {
        instance = this;
        try
        {
            ConfigManager.loadConfig();
            new DatabaseManager();
            playerManager = new PlayerManager();
            new PlayerManagement();
            regEvents();
            regCommands();
            if (ConfigManager.getConfigValueBool("general.autosave"))
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

    //register all commands
    private void regCommands()
    {
        Objects.requireNonNull(this.getCommand("ptsloadplayer")).setExecutor(new CommandLoadPlayer());
        Objects.requireNonNull(this.getCommand("ptsdipllo")).setExecutor(new CommandAddDisablePlayerLoad());
        Objects.requireNonNull(this.getCommand("ptsenpllo")).setExecutor(new CommandRemoveDisablePlayerLoad());
        Objects.requireNonNull(this.getCommand("ptsclear")).setExecutor(new CommandClearPlayerFiles());
        Objects.requireNonNull(this.getCommand("ptsdiplsa")).setExecutor(new CommandAddDisablePlayerSaves());
        Objects.requireNonNull(this.getCommand("ptsenplsa")).setExecutor(new CommandRemoveDisablePlayerSaves());
        Objects.requireNonNull(this.getCommand("ptssaveplayer")).setExecutor(new CommandSavePlayer());
    }

    //register all events
    private void regEvents()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerQuit(), this);
    }

    //if plugin disable try to save unsaved player. If autosave active save all player they online
    //close database connection and unregister all events
    @Override
    public void onDisable()
    {
        playerManager.trySaveMissingPlayerData(true);
        if (ConfigManager.getConfigValueString("general.autosave").equals("true"))
        {
            autosaveManager.onShutdownAutosave();
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
