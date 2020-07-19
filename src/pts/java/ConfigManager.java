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
        joinLoadDelay = getConfigValueInt("general.loaddelay");
        playerFile = getConfigValueBool("general.playerfile");
        autosaveInterval = getConfigValueInt("general.autosaveinterval");
        playercreate = getConfigValueBool("general.playercreate");
    }

    //try to get String value
    public static String getConfigValueString(String value)
    {
        if (!PlayertoSql.getInstance().getConfig().contains(value))
        {
            PlayertoSql.getInstance().getLogger().severe("Value: " + value + " not found in config.yml of" + PlayertoSql.getInstance().getName());
            return "ValueNotFound";
        }
        else
        {
            return PlayertoSql.getInstance().getConfig().getString(value);
        }
    }

    //try to get Int value
    public static int getConfigValueInt(String value)
    {
        if (!PlayertoSql.getInstance().getConfig().contains(value))
        {
            PlayertoSql.getInstance().getLogger().severe("Value: " + value + " not found in config.yml of" + PlayertoSql.getInstance().getName());
            return 0;
        }
        else
        {
            return PlayertoSql.getInstance().getConfig().getInt(value);
        }
    }

    //try to get Boolean
    public static Boolean getConfigValueBool(String value)
    {
        if (!PlayertoSql.getInstance().getConfig().contains(value))
        {
            PlayertoSql.getInstance().getLogger().severe("Value: " + value + " not found in config.yml of" + PlayertoSql.getInstance().getName());
            return false;
        }
        else
        {
            return PlayertoSql.getInstance().getConfig().getBoolean(value);
        }
    }
}
