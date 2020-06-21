package main.java.player;


import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import main.java.PlayertoSql;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemManager
{
    public static String[] getItemstackData(ItemStack[] item)
    {
        String[] values = new String[item.length];
        for (int i = 0; i < item.length; i++)
        {
            if ((item[i] != null) && (item[i].getType() != Material.AIR))
            {
                try
                {
                    values[i] = StreamSerializer.getDefault().serializeItemStack(item[i]);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    public static ItemStack[] setItemstackData(String[] string)
    {
        ItemStack[] itemStacks = new ItemStack[string.length];
        for (int i = 0; i < string.length; i++)
        {
            if (string[i] != null && !string[i].equals(""))
            {
                try
                {
                    itemStacks[i] = StreamSerializer.getDefault().deserializeItemStack(string[i]);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return itemStacks;
    }


}
