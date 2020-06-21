package main.java.player;

import main.java.PlayertoSql;
import main.java.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerManager
{
    public void onPlayerJoin(final Player p)
    {
        if (DatabaseManager.getInstance().isPlayerexist(p.getUniqueId().toString()))
        {
            loadPlayer(p);
        }
        else
        {
            DatabaseManager.getInstance().createPlayer(p);
            savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
        }
    }

    public void onPlayerQuit(String uuid, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest)
    {
        savePlayer(uuid, inventory, armor, offhand, enderchest);
    }


    private void savePlayer(String uuid, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest)
    {
        DatabaseManager.getInstance().savePlayerInventoryData(uuid, inventory);
        DatabaseManager.getInstance().savePlayerArmor(uuid, armor, offhand);
        DatabaseManager.getInstance().savePlayerEnderchestData(uuid, enderchest);
    }


    private void loadPlayer(Player p)
    {
        DatabaseManager.getInstance().updatePlayerData(p);
        ResultSet rs;
        rs = DatabaseManager.getInstance().loadPlayerInventoryData(p.getUniqueId().toString());
        String[] values = new String[36];
        try
        {
            rs.next();
            for (int i = 0; i < 36; i++)
            {

                if (i < 10)
                {
                    values[i] = rs.getNString("slot_0" + i + "_id");
                }
                else
                {
                    values[i] = rs.getNString("slot_" + i + "_id");
                }

                p.getInventory().setContents(ItemManager.setItemstackData(values));
            }
            rs.close();
            rs = DatabaseManager.getInstance().loadPlayerEnderchestData(p.getUniqueId().toString());
            rs.next();
            values = new String[27];
            for (int i = 0; i < 27; i++)
            {

                if (i < 10)
                {
                    values[i] = rs.getNString("slot_0" + i + "_id");
                }
                else
                {
                    values[i] = rs.getNString("slot_" + i + "_id");
                }

                p.getEnderChest().setContents(ItemManager.setItemstackData(values));
            }
            rs.close();
            rs = DatabaseManager.getInstance().loadPlayerArmor(p.getUniqueId().toString());
            rs.next();
            values = new String[4];
            String[] values2 = new String[1];
            values[0] = rs.getNString("slot_00_id");
            values[1] = rs.getNString("slot_01_id");
            values[2] = rs.getNString("slot_02_id");
            values[3] = rs.getNString("slot_03_id");
            values2[0] = rs.getNString("slot_04_id");

            p.getInventory().setArmorContents(ItemManager.setItemstackData(values));
            p.getInventory().setExtraContents(ItemManager.setItemstackData(values2));

        }
        catch (SQLException ex)
        {
            PlayertoSql.getInstance().getLogger().severe(ex.getMessage());
        }
    }
}
