package pts.java.player;

import pts.java.ConfigManager;
import pts.java.PlayertoSql;
import pts.java.database.DatabaseManager;
import pts.java.struct.PTSPlayerArmor;
import pts.java.struct.PTSPlayerEnderchest;
import pts.java.struct.PTSPlayerInventory;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class PlayerManager
{
    private final ArrayList<PTSPlayerInventory> unsavedPlayerInventory = new ArrayList();
    private final ArrayList<PTSPlayerArmor> unsavedPlayerArmor = new ArrayList();
    private final ArrayList<PTSPlayerEnderchest> unsavedPlayerEnderchest = new ArrayList();

    public final ArrayList<UUID> disabledPlayerSaved = new ArrayList();
    public final ArrayList<UUID> disabledPlayerLoaded = new ArrayList();

    private final ArrayList<UUID> playerloaded = new ArrayList<>();

    //date time for logfiles
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

    //if player join check exist he in the database. If not create entry
    public void onPlayerJoin(Player p)
    {
        if (p.isOnline())
        {
            if (DatabaseManager.getInstance().isPlayerExist(p.getUniqueId().toString()))
            {
                loadPlayer(p, false);
            }
            else
            {
                if (ConfigManager.playercreate)
                {
                    DatabaseManager.getInstance().createPlayer(p);
                    playerloaded.add(p.getUniqueId());
                }
                else
                {
                    PlayertoSql.getInstance().getLogger().info(p.getName() + " not create! Create player disabled");
                }
            }
        }
        else
        {
            PlayertoSql.getInstance().getLogger().warning(p.getName() + " not create/load! Player is offline");
        }
    }

    //clear player log files
    public void clearPlayerFile()
    {
        File folder = new File(PlayertoSql.getInstance().getDataFolder() + "/ptssaves/");
        File folder2 = new File(PlayertoSql.getInstance().getDataFolder() + "/ptsload/");
        try
        {
            FileUtils.cleanDirectory(folder);
            FileUtils.cleanDirectory(folder2);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //add player to disabledPlayerSaved list
    public void addDisablePlayerSave(UUID uuid)
    {
        disabledPlayerSaved.add(uuid);
    }

    //remove player from disabledPlayerSaved list
    public void removeDisablePlayerSave(UUID uuid)
    {
        disabledPlayerSaved.remove(uuid);
    }

    //add player to disabledPlayerLoaded list
    public void addDisablePlayerLoad(UUID uuid)
    {
        disabledPlayerLoaded.add(uuid);
    }

    //remove player from disabledPlayerLoaded list
    public void removeDisablePlayerLoad(UUID uuid)
    {
        disabledPlayerLoaded.remove(uuid);
    }

    //write player savefile if enabled
    private void savePlayerFile(Player p, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest, String savetyp)
    {
        UUID uuid = p.getUniqueId();
        try
        {
            File save_file = new File(PlayertoSql.getInstance().getDataFolder() + "/ptssaves/" + p.getName() + "-" + uuid.toString() + ".txt");
            if (!save_file.exists())
            {
                save_file.getParentFile().mkdirs();
                save_file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(save_file, true));
            Date time = new Date();
            bw.write("=[" + savetyp + "]======[" + sdf.format(time) + "]=======");
            bw.newLine();
            bw.write("[inventory]");
            bw.newLine();
            for (ItemStack item : inventory)
            {
                if (item != null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[armor]");
            bw.newLine();
            for (ItemStack item : armor)
            {
                if (item != null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[offhand]");
            bw.newLine();
            for (ItemStack item : offhand)
            {
                if (item != null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.write("[enderchest]");
            bw.newLine();
            for (ItemStack item : enderchest)
            {
                if (item != null)
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

    private void savePlayerFileLoad(Player p, ItemStack[] tmp, ItemStack[] tmp2, String type)
    {
        UUID uuid = p.getUniqueId();
        try
        {
            File save_file = new File(PlayertoSql.getInstance().getDataFolder() + "/ptsload/" + p.getName() + "-" + uuid.toString() + ".txt");
            if (!save_file.exists())
            {
                save_file.getParentFile().mkdirs();
                save_file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(save_file, true));
            Date time = new Date();
            bw.write("=[load " + type + "]======[" + sdf.format(time) + "]=======");
            bw.newLine();
            for (ItemStack item : tmp)
            {
                if (item != null)
                {
                    bw.write(item.toString());
                    bw.newLine();
                }
            }
            bw.newLine();
            if (tmp2 != null)
            {
                for (ItemStack item : tmp2)
                {
                    if (item != null)
                    {
                        bw.write(item.toString());
                        bw.newLine();
                    }
                }
            }
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //check if player not on disabledPlayerSaved list or the list is ignored then player load
    public void savePlayer(Player p, ItemStack[] inventory, ItemStack[] armor, ItemStack[] offhand, ItemStack[] enderchest, boolean ignoreList, String savetyp)
    {
        UUID uuid = p.getUniqueId();
        if (ConfigManager.playercreate || DatabaseManager.getInstance().isPlayerExist(uuid.toString()))
        {
            if (playerloaded.contains(uuid))
            {
                if (!disabledPlayerSaved.contains(uuid) || ignoreList)
                {
                    if (DatabaseManager.getInstance().isPlayerInventoryDataNotSaved(uuid, inventory))
                    {
                        unsavedPlayerInventory.add(new PTSPlayerInventory(uuid, inventory));
                    }
                    if (DatabaseManager.getInstance().isPlayerArmorNotSaved(uuid, armor, offhand))
                    {
                        unsavedPlayerArmor.add(new PTSPlayerArmor(uuid, armor, offhand));
                    }
                    if (DatabaseManager.getInstance().isPlayerEnderchestDataNotSaved(uuid, enderchest))
                    {
                        unsavedPlayerEnderchest.add(new PTSPlayerEnderchest(uuid, enderchest));
                    }
                }
                if (ConfigManager.playerFile)
                {
                    savePlayerFile(p, inventory, armor, offhand, enderchest, savetyp);
                }
                if (savetyp.equals("quitsave"))
                {
                    playerloaded.remove(uuid);
                }
            }
            else
            {
                PlayertoSql.getInstance().getLogger().info(p.getName() + " could not be saved because it was not loaded!");
            }
        }
    }

    //check if player not on disabledPlayerLoaded list or the list is ignored then player load
    public void loadPlayer(Player p, boolean ignoreList)
    {
        if (!disabledPlayerLoaded.contains(p.getUniqueId()) || ignoreList)
        {
            DatabaseManager.getInstance().updatePlayerData(p);
            ResultSet rs;
            ItemStack[] values;

            if (isPlayerSaved(p.getUniqueId(), "inventory"))
            {
                try
                {
                    rs = DatabaseManager.getInstance().loadPlayerInventoryData(p.getUniqueId());
                    values = new ItemStack[DatabaseManager.inventoryLength];
                    rs.next();
                    for (int i = 0; i < DatabaseManager.inventoryLength; i++)
                    {
                        if (i < 10)
                        {
                            values[i] = ItemManager.setItemStackData(rs.getNString("slot_0" + i + "_id"));
                        }
                        else
                        {
                            values[i] = ItemManager.setItemStackData(rs.getNString("slot_" + i + "_id"));
                        }
                    }
                    p.getInventory().setStorageContents(values);
                    if (ConfigManager.playerFile)
                    {
                        savePlayerFileLoad(p, values, null, "inventory");
                    }
                    rs.close();
                }
                catch (Exception ex)
                {
                    PlayertoSql.getInstance().getLogger().warning(ex.getMessage());
                }
            }
            else
            {
                for (PTSPlayerInventory value : unsavedPlayerInventory)
                {
                    if (value.getUuid() == p.getUniqueId())
                    {
                        p.getInventory().setContents(value.getInventory());
                        removeUnsavedPlayer(p.getUniqueId(), "inventory");
                    }
                }
            }
            if (isPlayerSaved(p.getUniqueId(), "enderchest"))
            {
                try
                {
                    rs = DatabaseManager.getInstance().loadPlayerEnderchestData(p.getUniqueId());
                    rs.next();
                    values = new ItemStack[DatabaseManager.enderChestLength];
                    for (int i = 0; i < DatabaseManager.enderChestLength; i++)
                    {

                        if (i < 10)
                        {
                            values[i] = ItemManager.setItemStackData(rs.getNString("slot_0" + i + "_id"));
                        }
                        else
                        {
                            values[i] = ItemManager.setItemStackData(rs.getNString("slot_" + i + "_id"));
                        }
                    }
                    p.getEnderChest().setContents(values);
                    if (ConfigManager.playerFile)
                    {
                        savePlayerFileLoad(p, values, null, "enderchest");
                    }
                    rs.close();
                }
                catch (Exception ex)
                {
                    PlayertoSql.getInstance().getLogger().warning(ex.getMessage());
                }
            }
            else
            {
                for (PTSPlayerEnderchest value : unsavedPlayerEnderchest)
                {
                    if (value.getUuid() == p.getUniqueId())
                    {
                        p.getEnderChest().setContents(value.getInventoryEnderChest());
                        removeUnsavedPlayer(p.getUniqueId(), "enderchest");
                    }
                }
            }
            if (isPlayerSaved(p.getUniqueId(), "armor"))
            {
                try
                {
                    rs = DatabaseManager.getInstance().loadPlayerArmor(p.getUniqueId());
                    rs.next();
                    values = new ItemStack[4];
                    ItemStack[] values2 = new ItemStack[1];
                    values[0] = ItemManager.setItemStackData(rs.getNString("slot_00_id"));
                    values[1] = ItemManager.setItemStackData(rs.getNString("slot_01_id"));
                    values[2] = ItemManager.setItemStackData(rs.getNString("slot_02_id"));
                    values[3] = ItemManager.setItemStackData(rs.getNString("slot_03_id"));
                    values2[0] = ItemManager.setItemStackData(rs.getNString("slot_04_id"));


                    p.getInventory().setArmorContents(values);

                    p.getInventory().setExtraContents(values2);
                    if (ConfigManager.playerFile)
                    {
                        savePlayerFileLoad(p, values, values2, "armor offhand");
                    }
                    rs.close();
                }
                catch (Exception ex)
                {
                    PlayertoSql.getInstance().getLogger().warning(ex.getMessage());
                }
            }
            else
            {
                for (PTSPlayerArmor value : unsavedPlayerArmor)
                {
                    if (value.getUuid() == p.getUniqueId())
                    {
                        p.getInventory().setArmorContents(value.getInventoryArmor());
                        p.getInventory().setExtraContents(value.getInventoryOffhand());
                        removeUnsavedPlayer(p.getUniqueId(), "armor");
                    }
                }
            }
            if (!playerloaded.contains(p.getUniqueId()))
            {
                playerloaded.add(p.getUniqueId());
            }
        }
        else
        {
            PlayertoSql.getInstance().getLogger().info("Loading for " + p.getName() + " is disabled!");
        }
    }

    //try to save player they not saved. if shutdown and player could not saved write in log
    public void trySaveMissingPlayerData(boolean shutdown)
    {
        for (int i = 0; i < unsavedPlayerInventory.size(); i++)
        {
            if (unsavedPlayerInventory.get(i) != null)
            {
                if (DatabaseManager.getInstance().isPlayerInventoryDataNotSaved(unsavedPlayerInventory.get(i).getUuid(), unsavedPlayerInventory.get(i).getInventory()))
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
                if (DatabaseManager.getInstance().isPlayerArmorNotSaved(unsavedPlayerArmor.get(i).getUuid(), unsavedPlayerArmor.get(i).getInventoryArmor(), unsavedPlayerArmor.get(i).getInventoryOffhand()))
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
                if (DatabaseManager.getInstance().isPlayerEnderchestDataNotSaved(unsavedPlayerEnderchest.get(i).getUuid(), unsavedPlayerEnderchest.get(i).getInventoryEnderChest()))
                {
                    if (shutdown)
                    {
                        writePlayerSaveFailed(unsavedPlayerEnderchest.get(i).getUuid(), unsavedPlayerEnderchest.get(i).getInventoryEnderChest(), null, "Enderchest");
                    }
                }
                else
                {
                    unsavedPlayerEnderchest.set(i, null);
                }
            }
        }
        removeNullFromLists();
    }

    //removed all nulls from lists
    private void removeNullFromLists()
    {
        unsavedPlayerEnderchest.removeIf(Objects::isNull);
        unsavedPlayerArmor.removeIf(Objects::isNull);
        unsavedPlayerInventory.removeIf(Objects::isNull);
    }

    //write the log if player not saved
    private void writePlayerSaveFailed(UUID uuid, ItemStack[] items1, ItemStack[] items2, String inventoryType)
    {
        PlayertoSql.getInstance().getLogger().severe("Error player inventory can not save! UUID:" + uuid.toString() + " Items in " + inventoryType + ": ");
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

    //checked if player saved or not
    private boolean isPlayerSaved(UUID uuid, String inventoryType)
    {
        switch (inventoryType)
        {
            case "inventory":
                for (PTSPlayerInventory value : unsavedPlayerInventory)
                {
                    if (value.getUuid() == uuid)
                    {
                        return false;
                    }
                }
                break;
            case "enderchest":
                for (PTSPlayerEnderchest value : unsavedPlayerEnderchest)
                {
                    if (value.getUuid() == uuid)
                    {
                        return false;
                    }
                }
                break;
            case "armor":
                for (PTSPlayerArmor value : unsavedPlayerArmor)
                {
                    if (value.getUuid() == uuid)
                    {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    //removed player from unsaved list
    private void removeUnsavedPlayer(UUID uuid, String inventoryType)
    {
        switch (inventoryType)
        {
            case "inventory":
                for (int i = 0; i < unsavedPlayerInventory.size(); i++)
                {
                    if (unsavedPlayerInventory.get(i).getUuid() == uuid)
                    {
                        unsavedPlayerInventory.remove(i);
                        return;
                    }
                }
                break;
            case "enderchest":
                for (int i = 0; i < unsavedPlayerEnderchest.size(); i++)
                {
                    if (unsavedPlayerEnderchest.get(i).getUuid() == uuid)
                    {
                        unsavedPlayerEnderchest.remove(i);
                        return;
                    }
                }
                break;
            case "armor":
                for (int i = 0; i < unsavedPlayerArmor.size(); i++)
                {
                    if (unsavedPlayerArmor.get(i).getUuid() == uuid)
                    {
                        unsavedPlayerArmor.remove(i);
                        return;
                    }
                }
                break;
        }
    }
}
