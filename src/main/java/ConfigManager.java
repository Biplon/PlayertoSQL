package main.java;

import java.io.File;

public class ConfigManager
{
    protected static void loadConfig()
    {

        File configFile = new File("plugins" + File.separator + PlayertoSql.getInstance().getName() + File.separator + "config.yml");
        if (!configFile.exists())
        {
            PlayertoSql.getInstance().getLogger().info("Creating config ...");
            PlayertoSql.getInstance().saveDefaultConfig();
        }
        try
        {
            PlayertoSql.getInstance().getLogger().info("Loading the config ...");
            PlayertoSql.getInstance().getConfig().load(configFile);
        }
        catch (Exception e)
        {
            PlayertoSql.getInstance().getLogger().severe("Could not load the config! Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getConfigvalue(String value)
    {
        if (!PlayertoSql.getInstance().getConfig().contains(value))
        {
            PlayertoSql.getInstance().getLogger().severe("Value: " + value + " not found in config.yml of" + PlayertoSql.getInstance().getName());
            return "valuenotfound";
        }
        else
        {
            return PlayertoSql.getInstance().getConfig().getString(value);
        }
    }
}
