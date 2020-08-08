package pts.java;

import pts.java.api.PlayerManagement;
import pts.java.command.*;
import pts.java.database.DatabaseManager;
import pts.java.event.PlayerJoin;
import pts.java.event.PlayerQuit;
import pts.java.player.AutosaveManager;
import pts.java.player.PlayerManager;
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

    public static PlayertoSql getInstance()
    {
        return instance;
    }

    public PlayerManager getPlayerManager()
    {
        return playerManager;
    }

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
            if (getConfig().getBoolean("general.autosave"))
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
        DatabaseManager.getInstance().closeConnection();
        HandlerList.unregisterAll(this);
        Bukkit.getLogger().info("[PlayertoSql] has been disabled!");
    }

}
