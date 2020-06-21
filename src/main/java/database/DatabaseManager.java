package main.java.database;

import main.java.ConfigManager;
import main.java.PlayertoSql;
import main.java.player.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.Date;

public class DatabaseManager
{
    static DatabaseManager instance;

    static Connection connection;

    static String dbname;
    static String playertablename;
    static String playerinventorytablename;
    static String playerintentoryarmortablename;
    static String playerenderchesttablename;

    static String date;

    static StringBuilder inventoryslotsforquery = new StringBuilder();
    static StringBuilder enderinventoryslotsforquery = new StringBuilder();

    static String updateinventoryStatment = "";
    static String updatearmorStatment = "";
    static String updateenderchestStatment = "";

    public DatabaseManager()
    {
        instance = this;
        try
        {
            loadConfigValues();
            createStatmentStrings();
            PlayertoSql.getInstance().getLogger().info("Connecting to a selected database...");
            createConnection(false);
            PlayertoSql.getInstance().getLogger().info("Connected database successfully...");
            createSlotqueryString();
            setupTables();
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            PlayertoSql.getInstance().getLogger().severe("SQLException: " + ex.getMessage());
        }


    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    private void createStatmentStrings()
    {
        updateinventoryStatment = "Update " + dbname + "." + playerinventorytablename + " SET ";
        for (int i = 0; i < 36; i++)
        {
            if (i < 10)
            {
                updateinventoryStatment += "slot_0" + i + "_id= ?,";
            }
            else
            {
                updateinventoryStatment += "slot_" + i + "_id= ?,";
            }

        }
        updateinventoryStatment = updateinventoryStatment.substring(0, updateinventoryStatment.length() - 1);
        updateinventoryStatment += " where uuid_player= ?;";

        updatearmorStatment = "Update " + dbname + "." + playerintentoryarmortablename + " SET " +
                "slot_00_id= ?," +
                "slot_01_id= ?," +
                "slot_02_id= ?," +
                "slot_03_id= ?," +
                "slot_04_id= ?" +
                " where uuid_player= ?;";


        updateenderchestStatment = "Update " + dbname + "." + playerenderchesttablename + " SET ";
        for (int i = 0; i < 27; i++)
        {
            if (i < 10)
            {
                updateenderchestStatment += "slot_0" + i + "_id= ?,";
            }
            else
            {
                updateenderchestStatment += "slot_" + i + "_id= ?,";
            }

        }
        updateenderchestStatment = updateenderchestStatment.substring(0, updateenderchestStatment.length() - 1);
        updateenderchestStatment += " where uuid_player= ?;";
    }

    private void createSlotqueryString()
    {
        for (int i = 0; i < 36; i++)
        {
            if (i < 10)
            {
                inventoryslotsforquery.append("`slot_0").append(i).append("_id` Text(1000) NULL,");
            }
            else
            {
                inventoryslotsforquery.append("`slot_").append(i).append("_id` Text(1000) NULL,");
            }

        }

        for (int i = 0; i < 27; i++)
        {
            if (i < 10)
            {
                enderinventoryslotsforquery.append("`slot_0").append(i).append("_id` Text(1000) NULL,");
            }
            else
            {
                enderinventoryslotsforquery.append("`slot_").append(i).append("_id` Text(1000) NULL,");
            }

        }
    }

    private void loadConfigValues()
    {
        dbname = ConfigManager.getConfigvalue("database.mysql.databaseName");
        playertablename = ConfigManager.getConfigvalue("database.mysql.tableNameplayer");
        playerinventorytablename = ConfigManager.getConfigvalue("database.mysql.tableNameinventory");
        playerintentoryarmortablename = ConfigManager.getConfigvalue("database.mysql.tableNamearmor");
        playerenderchesttablename = ConfigManager.getConfigvalue("database.mysql.tableNameenderchest");

        Date dt = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(dt);
    }

    private void createConnection(boolean reconnect) throws SQLException, ClassNotFoundException
    {
        String username = ConfigManager.getConfigvalue("database.mysql.user");
        String password = ConfigManager.getConfigvalue("database.mysql.password");
        String server = "jdbc:mysql://" + ConfigManager.getConfigvalue("database.mysql.host") + ":" + ConfigManager.getConfigvalue("database.mysql.port") + "/" + dbname + "?allowMultiQueries=true&rewriteBatchedStatements=true";

        connection = DriverManager.getConnection(server, username, password);
    }

    public void closeConnection()
    {
        try
        {
            PlayertoSql.getInstance().getLogger().info("Closing database connection...");
            connection.close();
            connection = null;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Connection getConnection()
    {
        checkConnection();
        return connection;
    }

    private void checkConnection()
    {
        try
        {
            if (connection == null)
            {
                PlayertoSql.getInstance().getLogger().warning(PlayertoSql.getInstance().getName() + " Database connection lost");
                createConnection(true);
            }
            if (!connection.isValid(3))
            {
                PlayertoSql.getInstance().getLogger().warning(PlayertoSql.getInstance().getName() + " Database connection is idle or terminated. Try to reconnect...");
                createConnection(true);
            }
            if (connection.isClosed())
            {
                PlayertoSql.getInstance().getLogger().warning(PlayertoSql.getInstance().getName() + " Database connection is closed. Try to reconnect...");
                createConnection(true);
            }
        }
        catch (Exception e)
        {
            PlayertoSql.getInstance().getLogger().severe(PlayertoSql.getInstance().getName() + " Could not reconnect to Database! Error: " + e.getMessage());
        }
    }

    private void setupTables()
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {


                data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playertablename + "" +
                        " (`uuid_player` CHAR(128) NOT NULL," +
                        "`name` CHAR(128) NOT NULL," +
                        "`last_login` DATE NOT NULL," +
                        " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB;";

                query = connection.prepareStatement(data);
                query.execute();

                data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerinventorytablename + " " +
                        "(`uuid_player` CHAR(128) NOT NULL," +
                        "" + inventoryslotsforquery + "" +
                        " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB;";

                query = connection.prepareStatement(data);
                query.execute();

                data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerintentoryarmortablename + " " +
                        "(`uuid_player` CHAR(128) NOT NULL," +
                        "`slot_00_id` Text(1000) NULL," +
                        "`slot_01_id` Text(1000) NULL," +
                        "`slot_02_id` Text(1000) NULL," +
                        "`slot_03_id` Text(1000) NULL," +
                        "`slot_04_id` Text(1000) NULL," + //offhand
                        " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB;";

                query = connection.prepareStatement(data);
                query.execute();

                data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerenderchesttablename + " " +
                        "(`uuid_player` CHAR(128) NOT NULL," +
                        "" + enderinventoryslotsforquery + "" +
                        " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB;";

                query = connection.prepareStatement(data);
                query.execute();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error creating tables! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
            }
            finally
            {
                try
                {
                    if (query != null)
                    {
                        query.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean createPlayer(Player p)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {
                data = "Insert into " + dbname + "." + playertablename + " (uuid_player, name, last_login) values('" + p.getUniqueId() + "','" + p.getName() + "','" + date + "'); ";
                data += "Insert into " + dbname + "." + playerinventorytablename + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                data += "Insert into " + dbname + "." + playerintentoryarmortablename + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                data += "Insert into " + dbname + "." + playerenderchesttablename + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                query = connection.prepareStatement(data);
                query.addBatch();
                return (query.executeBatch().length > 0);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return false;
            }
        }
        return false;
    }

    public boolean updatePlayerData(Player p)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {
                data = "Update " + dbname + "." + playertablename + " Set name='" + p.getName() + "', last_login='" + date + "' where uuid_player='" + p.getUniqueId() + "'; ";
                query = connection.prepareStatement(data);
                query.execute();
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return false;
            }
        }
        return false;
    }

    public boolean isPlayerexist(String uuid)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query;
            try
            {
                data = "SELECT uuid_player FROM " + dbname + "." + playertablename + " where uuid_player='" + uuid + "' ; ";

                query = connection.prepareStatement(data);
                return query.executeQuery().next();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return false;
            }
        }
        return false;
    }

    public boolean savePlayerInventoryData(String uuid, ItemStack[] items)
    {
        if (connection != null)
        {
            try
            {
                String[] tmp = ItemManager.getItemstackData(items);
                PreparedStatement query = connection.prepareStatement(updateinventoryStatment);
                for (int i = 1; i <= 36; i++)
                {
                    query.setString(i, tmp[i - 1]);
                }
                query.setString(37, uuid);
                query.execute();
                query.close();
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(updateinventoryStatment);
                return false;
            }
        }


        return false;
    }

    public boolean savePlayerEnderchestData(String uuid, ItemStack[] items)
    {
        if (connection != null)
        {
            try
            {
                String[] tmp = ItemManager.getItemstackData(items);
                PreparedStatement query = connection.prepareStatement(updateenderchestStatment);
                for (int i = 1; i <= 27; i++)
                {
                    query.setString(i, tmp[i - 1]);
                }
                query.setString(28, uuid);
                query.execute();
                query.close();
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(updateenderchestStatment);
                return false;
            }
        }
        return false;
    }

    public boolean savePlayerArmor(String uuid, ItemStack[] itemsarmor, ItemStack[] offhand)
    {
        if (connection != null)
        {
            try
            {
                String[] tmp = ItemManager.getItemstackData(itemsarmor);
                PreparedStatement query = connection.prepareStatement(updatearmorStatment);

                query.setString(1, tmp[0]);
                query.setString(2, tmp[1]);
                query.setString(3, tmp[2]);
                query.setString(4, tmp[3]);
                query.setString(5, ItemManager.getItemstackData(offhand)[0]);
                query.setString(6, uuid);
                query.execute();
                query.close();
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(updatearmorStatment);
                return false;
            }
        }
        return false;
    }

    public ResultSet loadPlayerInventoryData(String uuid)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {
                data = "SELECT * FROM " + dbname + "." + playerinventorytablename + " where uuid_player='" + uuid + "'; ";

                query = connection.prepareStatement(data);
                return query.executeQuery();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return null;
            }
        }
        return null;
    }

    public ResultSet loadPlayerArmor(String uuid)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {
                data = "SELECT * FROM " + dbname + "." + playerintentoryarmortablename + " where uuid_player='" + uuid + "'; ";

                query = connection.prepareStatement(data);
                return query.executeQuery();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return null;
            }
        }
        return null;
    }

    public ResultSet loadPlayerEnderchestData(String uuid)
    {
        if (connection != null)
        {
            String data = "";
            PreparedStatement query = null;
            try
            {
                data = "SELECT * FROM " + dbname + "." + playerenderchesttablename + " where uuid_player='" + uuid + "'; ";

                query = connection.prepareStatement(data);
                return query.executeQuery();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                PlayertoSql.getInstance().getLogger().severe(data);
                return null;
            }
        }
        return null;
    }

}
