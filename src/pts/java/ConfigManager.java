package pts.java;

import java.io.File;

public class ConfigManager
{
    public static int joinLoadDelay;

    public static boolean playerFile;

    public static int autosaveInterval;

    public static boolean playercreate;

    //load config file and get values for var. If file not exist create default
    protected static void loadConfig()
    {
        //get config file path
        File configFile = new File("plugins" + File.separator + PlayertoSql.getInstance().getName() + File.separator + "config.yml");
        //if file not exist create default
        if (!configFile.exists())
        {
            PlayertoSql.getInstance().getLogger().info("Creating config ...");
            PlayertoSql.getInstance().saveDefaultConfig();
        }
        try
        {
            //try to load config
            PlayertoSql.getInstance().getLogger().info("Loading the config ...");
            PlayertoSql.getInstance().getConfig().load(configFile);
        }
        catch (Exception e)
        {
            PlayertoSql.getInstance().getLogger().severe("Could not load the config! Error: " + e.getMessage());
            e.printStackTrace();
        }

        //get values
        joinLoadDelay = PlayertoSql.getInstance().getConfig().getInt("general.loaddelay");
        playerFile = PlayertoSql.getInstance().getConfig().getBoolean("general.playerfile");
        autosaveInterval = PlayertoSql.getInstance().getConfig().getInt("general.autosaveinterval");
        playercreate = PlayertoSql.getInstance().getConfig().getBoolean("general.playercreate");
    }
}
