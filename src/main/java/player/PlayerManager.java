package main.java.player;

import main.java.PlayertoSql;
import main.java.database.DatabaseManager;
import main.java.struct.PTSPlayerArmor;
import main.java.struct.PTSPlayerEnderchest;
import main.java.struct.PTSPlayerInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerManager
{
    private final ArrayList<PTSPlayerInventory> unsavedPlayerInventory = new ArrayList();
    private final ArrayList<PTSPlayerArmor> unsavedPlayerArmor = new ArrayList();
    private final ArrayList<PTSPlayerEnderchest> unsavedPlayerEnderchest = new ArrayList();

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
        if (!DatabaseManager.getInstance().savePlayerInventoryData(uuid, inventory))
        {
            unsavedPlayerInventory.add(new PTSPlayerInventory(uuid,inventory));
        }
        if (!DatabaseManager.getInstance().savePlayerArmor(uuid, armor, offhand))
        {
            unsavedPlayerArmor.add(new PTSPlayerArmor(uuid,armor,offhand));
        }
        if (!DatabaseManager.getInstance().savePlayerEnderchestData(uuid, enderchest))
        {
            unsavedPlayerEnderchest.add(new PTSPlayerEnderchest(uuid,enderchest));
        }

    }

    public void trySaveMissingPlayerData()
    {
        for (PTSPlayerInventory inv: unsavedPlayerInventory)
        {
            if (!DatabaseManager.getInstance().savePlayerInventoryData(inv.getUuid(), inv.getInventory()))
            {
                writePlayerSaveFailed(inv.getUuid(), inv.getInventory(),null,"Inventory");
            }
        }
        for (PTSPlayerArmor armor: unsavedPlayerArmor)
        {
            if (!DatabaseManager.getInstance().savePlayerArmor(armor.getUuid(), armor.getInventoryArmor(),armor.getInventoryOffhand()))
            {
                writePlayerSaveFailed(armor.getUuid(), armor.getInventoryArmor(),armor.getInventoryOffhand(),"Armor");
            }
        }
        for (PTSPlayerEnderchest enderchest: unsavedPlayerEnderchest)
        {
            if (!DatabaseManager.getInstance().savePlayerEnderchestData(enderchest.getUuid(), enderchest.getInventoryEnderchest()))
            {
                writePlayerSaveFailed(enderchest.getUuid(), enderchest.getInventoryEnderchest(),null,"Enderchest");
            }
        }
    }

    private void writePlayerSaveFailed(String uuid,ItemStack[] items1,ItemStack[] items2,String inventorytype)
    {
        PlayertoSql.getInstance().getLogger().severe("Error player inventory can not save! UUID:"+uuid+" Items in "+inventorytype+": ");
        for (ItemStack i:items1)
        {
            PlayertoSql.getInstance().getLogger().severe(i.toString());
        }
        if (items2 !=null)
        {
            for (ItemStack i:items2)
            {
                PlayertoSql.getInstance().getLogger().severe(i.toString());
            }
        }

    }

    private void loadPlayer(Player p)
    {
        DatabaseManager.getInstance().updatePlayerData(p);
        ResultSet rs;
        rs = DatabaseManager.getInstance().loadPlayerInventoryData(p.getUniqueId().toString());
        String[] values = new String[DatabaseManager.inventorylenght];
        try
        {
            rs.next();
            for (int i = 0; i < DatabaseManager.inventorylenght; i++)
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
            values = new String[DatabaseManager.enderchestlenght];
            for (int i = 0; i < DatabaseManager.enderchestlenght; i++)
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
