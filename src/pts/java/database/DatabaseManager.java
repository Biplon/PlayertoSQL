package pts.java.database;

import pts.java.PlayertoSql;
import pts.java.player.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class DatabaseManager
{
    static DatabaseManager instance;

    static Connection connection;

    static String dbname;
    static String playerTableName;
    static String playerInventoryTableName;
    static String playerInventoryArmorTableName;
    static String playerEnderChestTableName;

    static String date;

    static String updateInventoryStatement;
    static String updateArmorStatement;
    static String updateEnderChestStatement;

    public static byte inventoryLength = 36;
    public static byte enderChestLength = 27;

    public DatabaseManager()
    {
        instance = this;
        loadConfigValues();
        createStatementStrings();
        PlayertoSql.getInstance().getLogger().info("Connecting to a selected database...");
        createConnection(false);
        PlayertoSql.getInstance().getLogger().info("Connected database successfully...");
        setupTables();
    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    //create all statements and put it on vars
    private void createStatementStrings()
    {
        updateInventoryStatement = "Update " + dbname + "." + playerInventoryTableName + " SET ";
        for (int i = 0; i < inventoryLength; i++)
        {
            if (i < 10)
            {
                updateInventoryStatement += "slot_0" + i + "_id= ?,";
            }
            else
            {
                updateInventoryStatement += "slot_" + i + "_id= ?,";
            }
        }
        updateInventoryStatement = updateInventoryStatement.substring(0, updateInventoryStatement.length() - 1);
        updateInventoryStatement += " where uuid_player= ?;";

        updateArmorStatement = "Update " + dbname + "." + playerInventoryArmorTableName + " SET " +
                "slot_00_id= ?," +
                "slot_01_id= ?," +
                "slot_02_id= ?," +
                "slot_03_id= ?," +
                "slot_04_id= ?" +
                " where uuid_player= ?;";

        updateEnderChestStatement = "Update " + dbname + "." + playerEnderChestTableName + " SET ";
        for (int i = 0; i < enderChestLength; i++)
        {
            if (i < 10)
            {
                updateEnderChestStatement += "slot_0" + i + "_id= ?,";
            }
            else
            {
                updateEnderChestStatement += "slot_" + i + "_id= ?,";
            }
        }
        updateEnderChestStatement = updateEnderChestStatement.substring(0, updateEnderChestStatement.length() - 1);
        updateEnderChestStatement += " where uuid_player= ?;";
    }

    //load all config values
    private void loadConfigValues()
    {
        dbname = PlayertoSql.getInstance().getConfig().getString("database.mysql.databaseName");
        playerTableName = PlayertoSql.getInstance().getConfig().getString("database.mysql.tableNameplayer");
        playerInventoryTableName = PlayertoSql.getInstance().getConfig().getString("database.mysql.tableNameinventory");
        playerInventoryArmorTableName = PlayertoSql.getInstance().getConfig().getString("database.mysql.tableNamearmor");
        playerEnderChestTableName = PlayertoSql.getInstance().getConfig().getString("database.mysql.tableNameenderchest");

        Date dt = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(dt);
    }

    //create connection with db
    private void createConnection(boolean reconnect)
    {
        String ssl = "";
        String username = PlayertoSql.getInstance().getConfig().getString("database.mysql.user");
        String password = PlayertoSql.getInstance().getConfig().getString("database.mysql.password");
        if (PlayertoSql.getInstance().getConfig().getBoolean("database.mysql.host"))
        {
            ssl = "&sslMode=REQUIRED";
        }
        String server = "jdbc:mysql://" + PlayertoSql.getInstance().getConfig().getString("database.mysql.host") + ":" + PlayertoSql.getInstance().getConfig().getString("database.mysql.port") + "/" + dbname + "?autoReconnect=true&allowMultiQueries=true&rewriteBatchedStatements=true&connectTimeout=0" + ssl;
        try
        {
            connection = DriverManager.getConnection(server, username, password);
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
            if (reconnect)
            {
                PlayertoSql.getInstance().getLogger().warning("Database can not reconnect");
            }
        }

    }

    //close db connection
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

    //create inventory part of query for create table
    private String createInventoryQueryString()
    {
        StringBuilder inventorySlotsForQuery = new StringBuilder();
        for (int i = 0; i < inventoryLength; i++)
        {
            if (i < 10)
            {
                inventorySlotsForQuery.append("`slot_0").append(i).append("_id` LONGTEXT NULL,");
            }
            else
            {
                inventorySlotsForQuery.append("`slot_").append(i).append("_id` LONGTEXT NULL,");
            }
        }
        return inventorySlotsForQuery.toString();
    }

    //create enderchest part of query for create table
    private String createEnderQuery()
    {
        StringBuilder enderInventorySlotsForQuery = new StringBuilder();
        for (int i = 0; i < enderChestLength; i++)
        {
            if (i < 10)
            {
                enderInventorySlotsForQuery.append("`slot_0").append(i).append("_id` LONGTEXT NULL,");
            }
            else
            {
                enderInventorySlotsForQuery.append("`slot_").append(i).append("_id` LONGTEXT NULL,");
            }
        }
        return enderInventorySlotsForQuery.toString();
    }

    //create all db tables if no exist
    private void setupTables()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query = null;
                try
                {
                    data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerTableName + "" +
                            " (`uuid_player` CHAR(128) NOT NULL," +
                            "`name` CHAR(128) NOT NULL," +
                            "`last_login` DATE NOT NULL," +
                            " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB  DEFAULT CHARSET=utf8" +
                            " ROW_FORMAT = DYNAMIC ;";

                    query = connection.prepareStatement(data);
                    query.execute();

                    data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerInventoryTableName + " " +
                            "(`uuid_player` CHAR(128) NOT NULL," +
                            "" + createInventoryQueryString() + "" +
                            " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB  DEFAULT CHARSET=utf8 " +
                            " ROW_FORMAT = DYNAMIC;";

                    query = connection.prepareStatement(data);
                    query.execute();

                    data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerInventoryArmorTableName + " " +
                            "(`uuid_player` CHAR(128) NOT NULL," +
                            "`slot_00_id` LONGTEXT NULL," +
                            "`slot_01_id` LONGTEXT NULL," +
                            "`slot_02_id` LONGTEXT NULL," +
                            "`slot_03_id` LONGTEXT NULL," +
                            "`slot_04_id` LONGTEXT NULL," + //offhand
                            " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB  DEFAULT CHARSET=utf8" +
                            " ROW_FORMAT = DYNAMIC;";

                    query = connection.prepareStatement(data);
                    query.execute();

                    data = "CREATE TABLE IF NOT EXISTS " + dbname + "." + playerEnderChestTableName + " " +
                            "(`uuid_player` CHAR(128) NOT NULL," +
                            "" + createEnderQuery() + "" +
                            " PRIMARY KEY (`uuid_player`)) ENGINE = InnoDB  DEFAULT CHARSET=utf8" +
                            " ROW_FORMAT = DYNAMIC;";

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
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }

    //create a new player entry in db
    public void createPlayer(Player p)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "Insert into " + dbname + "." + playerTableName + " (uuid_player, name, last_login) values('" + p.getUniqueId() + "','" + p.getName() + "','" + date + "'); ";
                    data += "Insert into " + dbname + "." + playerInventoryTableName + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                    data += "Insert into " + dbname + "." + playerInventoryArmorTableName + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                    data += "Insert into " + dbname + "." + playerEnderChestTableName + " (uuid_player) values('" + p.getUniqueId() + "'); ";
                    query = connection.prepareStatement(data);
                    query.addBatch();
                    query.executeBatch();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                    PlayertoSql.getInstance().getLogger().severe(data);
                }
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }

    //update player entry in db
    public void updatePlayerData(Player p)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "Update " + dbname + "." + playerTableName + " Set name='" + p.getName() + "', last_login='" + date + "' where uuid_player='" + p.getUniqueId() + "'; ";
                    query = connection.prepareStatement(data);
                    query.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                    PlayertoSql.getInstance().getLogger().severe(data);
                }
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }

    //check if player exist in db
    public boolean isPlayerExist(String uuid)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "SELECT uuid_player FROM " + dbname + "." + playerTableName + " where uuid_player='" + uuid + "' ; ";

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
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return false;
    }

    //save player inventory return true if not saved
    public boolean isPlayerInventoryDataNotSaved(UUID uuid, ItemStack[] items)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                try
                {
                    PreparedStatement query = connection.prepareStatement(updateInventoryStatement);
                    for (int i = 1; i <= inventoryLength; i++)
                    {
                        query.setString(i,ItemManager.getItemStackData(items[i-1]));
                    }
                    query.setString(37, uuid.toString());
                    query.execute();
                    query.close();
                    return false;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                    return true;
                }
            }
            return true;
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return true;
    }

    //save player enderchest return true if not saved
    public boolean isPlayerEnderchestDataNotSaved(UUID uuid, ItemStack[] items)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                try
                {
                    PreparedStatement query = connection.prepareStatement(updateEnderChestStatement);
                    for (int i = 1; i <= enderChestLength; i++)
                    {
                        query.setString(i,ItemManager.getItemStackData(items[i-1]));
                    }
                    query.setString(28, uuid.toString());
                    query.execute();
                    query.close();
                    return false;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                    return true;
                }
            }
            return true;
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return true;
    }

    //save player armor and offhand return true if not saved
    public boolean isPlayerArmorNotSaved(UUID uuid, ItemStack[] itemsArmor, ItemStack[] offhand)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                try
                {
                    PreparedStatement query = connection.prepareStatement(updateArmorStatement);

                    query.setString(1,  ItemManager.getItemStackData(itemsArmor[0]));
                    query.setString(2,  ItemManager.getItemStackData(itemsArmor[1]));
                    query.setString(3,  ItemManager.getItemStackData(itemsArmor[2]));
                    query.setString(4,  ItemManager.getItemStackData(itemsArmor[3]));
                    query.setString(5, ItemManager.getItemStackData(offhand[0]));
                    query.setString(6, uuid.toString());
                    query.execute();
                    query.close();
                    return false;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error load player inventory! Error: " + e.getMessage());
                    PlayertoSql.getInstance().getLogger().severe(updateArmorStatement);
                    return true;
                }
            }
            return true;
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return true;
    }

    //load player inventory return the result set from query
    public ResultSet loadPlayerInventoryData(UUID uuid)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "SELECT * FROM " + dbname + "." + playerInventoryTableName + " where uuid_player='" + uuid.toString() + "'; ";

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
            else
            {
                createConnection(true);
            }
            return null;
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return null;
    }

    //load player armor and offhand return the result set from query
    public ResultSet loadPlayerArmor(UUID uuid)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "SELECT * FROM " + dbname + "." + playerInventoryArmorTableName + " where uuid_player='" + uuid.toString() + "'; ";

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
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return null;

    }

    //load player enderchest return the result set from query
    public ResultSet loadPlayerEnderchestData(UUID uuid)
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "SELECT * FROM " + dbname + "." + playerEnderChestTableName + " where uuid_player='" + uuid.toString() + "'; ";

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
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return null;
    }

    public void checkConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection(true);
            }
            if (connection != null && !connection.isClosed())
            {
                String data = "";
                PreparedStatement query;
                try
                {
                    data = "SELECT * FROM " + dbname + "." + playerEnderChestTableName + " LIMIT 1; ";

                    query = connection.prepareStatement(data);
                    query.executeQuery();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    PlayertoSql.getInstance().getLogger().severe("Error check connection! Error: " + e.getMessage());
                }
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }
}
