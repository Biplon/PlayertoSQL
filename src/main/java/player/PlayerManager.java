package main.java.player;

import main.java.ConfigManager;
import main.java.PlayertoSql;
import main.java.database.DatabaseManager;
import main.java.struct.PTSPlayerArmor;
import main.java.struct.PTSPlayerEnderchest;
import main.java.struct.PTSPlayerInventory;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PlayerManager
{
    private final ArrayList<PTSPlayerInventory> unsavedPlayerInventory = new ArrayList();
    private final ArrayList<PTSPlayerArmor> unsavedPlayerArmor = new ArrayList();
    private final ArrayList<PTSPlayerEnderchest> unsavedPlayerEnderchest = new ArrayList();

    private final ArrayList<String> disabledplayersaves = new ArrayList();
    private final ArrayList<String> disabledplayerload = new ArrayList();

    Date time = new Date();
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh-mm");

    public void onPlayerJoin(Player p)
    {
        if (DatabaseManager.getInstance().isPlayerexist(p.getUniqueId().toString()))
        {
            if (!disabledplayerload.contains(p.getUniqueId().toString()))
            {
                loadPlayer(p);
            }
        }
        else
        {
            DatabaseManager.getInstance().createPlayer(p);
            savePlayer(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
        }
    }

    public void onPlayerQuit(String uuid, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest)
    {
        if (!disabledplayersaves.contains(uuid))
        {
            savePlayer(uuid, inventory, armor, offhand, enderchest);
        }
        if(ConfigManager.getConfigvalueBool("general.playerfile"))
        {
            savePlayerfile(uuid, inventory, armor, offhand, enderchest);
        }
    }

    public void clearPlayerfile()
    {
        File folder = new File(PlayertoSql.getInstance().getDataFolder()+"/ptssaves/");
        try
        {
            FileUtils.cleanDirectory(folder);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void addDisablePlayerSave(String uuid)
    {
        disabledplayersaves.add(uuid);
    }

    public void removeDisablePlayerSave(String uuid)
    {
        disabledplayersaves.remove(uuid);
    }

    public void addDisablePlayerLoad(String uuid)
    {
        disabledplayerload.add(uuid);
    }

    public void removeDisablePlayerLoad(String uuid)
    {
        disabledplayerload.remove(uuid);
    }

    private void savePlayerfile(String uuid, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest)
    {
        try
        {
            File save_file = new File(PlayertoSql.getInstance().getDataFolder()+"/ptssaves/"+ uuid +".txt");
            if (!save_file.exists())
            {
                save_file.getParentFile().mkdirs();
                save_file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(save_file, true));
            bw.write("=======["+sdf.format(time)+"]=======");
            bw.newLine();
            bw.write("[inventory]");
            bw.newLine();
            for (ItemStack item: inventory)
            {
                if (item !=null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[armor]");
            bw.newLine();
            for (ItemStack item: armor)
            {
                if (item !=null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[offhand]");
            bw.newLine();
            for (ItemStack item: offhand)
            {
                if (item !=null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[enderchest]");
            bw.newLine();
            for (ItemStack item: enderchest)
            {
                if (item !=null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    public void savePlayer(String uuid, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest)
    {
        if (!DatabaseManager.getInstance().savePlayerInventoryData(uuid, inventory))
        {
            unsavedPlayerInventory.add(new PTSPlayerInventory(uuid, inventory));
        }
        if (!DatabaseManager.getInstance().savePlayerArmor(uuid, armor, offhand))
        {
            unsavedPlayerArmor.add(new PTSPlayerArmor(uuid, armor, offhand));
        }
        if (!DatabaseManager.getInstance().savePlayerEnderchestData(uuid, enderchest))
        {
            unsavedPlayerEnderchest.add(new PTSPlayerEnderchest(uuid, enderchest));
        }

    }

    public void trySaveMissingPlayerData(boolean shutdown)
    {
        for (int i = 0; i < unsavedPlayerInventory.size(); i++)
        {
            if (unsavedPlayerInventory.get(i) != null)
            {
                if (!DatabaseManager.getInstance().savePlayerInventoryData(unsavedPlayerInventory.get(i).getUuid(), unsavedPlayerInventory.get(i).getInventory()))
                {
                    if (shutdown)
                    {
                        writePlayerSaveFailed(unsavedPlayerInventory.get(i).getUuid(), unsavedPlayerInventory.get(i).getInventory(), null, "Inventory");
                    }

                }
                else
                {
                    unsavedPlayerInventory.set(i, null);
                }
            }

        }
        for (int i = 0; i < unsavedPlayerArmor.size(); i++)
        {
            if (unsavedPlayerArmor.get(i) != null)
            {
                if (!DatabaseManager.getInstance().savePlayerArmor(unsavedPlayerArmor.get(i).getUuid(), unsavedPlayerArmor.get(i).getInventoryArmor(), unsavedPlayerArmor.get(i).getInventoryOffhand()))
                {
                    if (shutdown)
                    {
                        writePlayerSaveFailed(unsavedPlayerArmor.get(i).getUuid(), unsavedPlayerArmor.get(i).getInventoryArmor(), unsavedPlayerArmor.get(i).getInventoryOffhand(), "Armor");
                    }

                }
                else
                {
                    unsavedPlayerArmor.set(i, null);
                }
            }

        }
        for (int i = 0; i < unsavedPlayerEnderchest.size(); i++)
        {
            if (unsavedPlayerEnderchest.get(i) != null)
            {
                if (!DatabaseManager.getInstance().savePlayerInventoryData(unsavedPlayerEnderchest.get(i).getUuid(), unsavedPlayerEnderchest.get(i).getInventoryEnderchest()))
                {
                    if (shutdown)
                    {
                        writePlayerSaveFailed(unsavedPlayerEnderchest.get(i).getUuid(), unsavedPlayerEnderchest.get(i).getInventoryEnderchest(), null, "Enderchest");
                    }

                }
                else
                {
                    unsavedPlayerEnderchest.set(i, null);
                }
            }

        }
        removeNullfromLists();
    }

    private void removeNullfromLists()
    {
        unsavedPlayerEnderchest.removeIf(Objects::isNull);
        unsavedPlayerArmor.removeIf(Objects::isNull);
        unsavedPlayerInventory.removeIf(Objects::isNull);
    }

    private void writePlayerSaveFailed(String uuid, ItemStack[] items1, ItemStack[] items2, String inventorytype)
    {
        PlayertoSql.getInstance().getLogger().severe("Error player inventory can not save! UUID:" + uuid + " Items in " + inventorytype + ": ");
        for (ItemStack i : items1)
        {
            PlayertoSql.getInstance().getLogger().severe(i.toString());
        }
        if (items2 != null)
        {
            for (ItemStack i : items2)
            {
                PlayertoSql.getInstance().getLogger().severe(i.toString());
            }
        }

    }

    private boolean isPlayerSaved(String uuid, String inventorytyp)
    {
        switch (inventorytyp)
        {
            case "inventory":
                for (PTSPlayerInventory value : unsavedPlayerInventory)
                {
                    if (value.getUuid().equals(uuid))
                    {
                        return false;
                    }
                }
                break;
            case "enderchest":
                for (PTSPlayerEnderchest value : unsavedPlayerEnderchest)
                {
                    if (value.getUuid().equals(uuid))
                    {
                        return false;
                    }
                }
                break;
            case "armor":
                for (PTSPlayerArmor value : unsavedPlayerArmor)
                {
                    if (value.getUuid().equals(uuid))
                    {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private void removeUnsavedPlayer(String uuid, String inventorytyp)
    {
        switch (inventorytyp)
        {
            case "inventory":
                for (int i = 0; i < unsavedPlayerInventory.size(); i++)
                {
                    if (unsavedPlayerInventory.get(i).getUuid().equals(uuid))
                    {
                        unsavedPlayerInventory.remove(i);
                        return;
                    }
                }
                break;
            case "enderchest":
                for (int i = 0; i < unsavedPlayerEnderchest.size(); i++)
                {
                    if (unsavedPlayerEnderchest.get(i).getUuid().equals(uuid))
                    {
                        unsavedPlayerEnderchest.remove(i);
                        return;
                    }
                }
                break;
            case "armor":
                for (int i = 0; i < unsavedPlayerArmor.size(); i++)
                {
                    if (unsavedPlayerArmor.get(i).getUuid().equals(uuid))
                    {
                        unsavedPlayerArmor.remove(i);
                        return;
                    }
                }
                break;
        }
    }

    private void loadPlayer(Player p)
    {
        DatabaseManager.getInstance().updatePlayerData(p);
        ResultSet rs;
        String[] values;

        if (isPlayerSaved(p.getUniqueId().toString(), "inventory"))
        {
            try
            {

                rs = DatabaseManager.getInstance().loadPlayerInventoryData(p.getUniqueId().toString());
                values = new String[DatabaseManager.inventorylenght];
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


            }
            catch (SQLException ex)
            {
                PlayertoSql.getInstance().getLogger().severe(ex.getMessage());
            }

        }
        else
        {
            for (PTSPlayerInventory value : unsavedPlayerInventory)
            {
                if (value.getUuid().equals(p.getUniqueId().toString()))
                {
                    p.getInventory().setContents(value.getInventory());
                    removeUnsavedPlayer(p.getUniqueId().toString(), "inventory");
                }
            }

        }
        if (isPlayerSaved(p.getUniqueId().toString(), "enderchest"))
        {
            try
            {
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


            }
            catch (SQLException ex)
            {
                PlayertoSql.getInstance().getLogger().severe(ex.getMessage());
            }

        }
        else
        {
            for (PTSPlayerEnderchest value : unsavedPlayerEnderchest)
            {
                if (value.getUuid().equals(p.getUniqueId().toString()))
                {
                    p.getEnderChest().setContents(value.getInventoryEnderchest());
                    removeUnsavedPlayer(p.getUniqueId().toString(), "enderchest");
                }
            }

        }
        if (isPlayerSaved(p.getUniqueId().toString(), "armor"))
        {
            try
            {
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
                rs.close();


            }
            catch (SQLException ex)
            {
                PlayertoSql.getInstance().getLogger().severe(ex.getMessage());
            }

        }
        else
        {
            for (PTSPlayerArmor value : unsavedPlayerArmor)
            {
                if (value.getUuid().equals(p.getUniqueId().toString()))
                {
                    p.getInventory().setArmorContents(value.getInventoryArmor());
                    p.getInventory().setExtraContents(value.getInventoryOffhand());
                    removeUnsavedPlayer(p.getUniqueId().toString(), "armor");
                }
            }

        }

    }
}
