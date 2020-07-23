package pts.java.player;

import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pts.java.PlayertoSql;

import java.io.*;
import java.util.Objects;

public class ItemManager
{
    //serializeItemStack itemStack and return string array with values
    public static String[] getItemStackData(ItemStack[] item)
    {
        String[] values = new String[item.length];
        for (int i = 0; i < item.length; i++)
        {
            if ((item[i] != null) && (item[i].getType() != Material.AIR))
            {
                try
                {
                    Objects.requireNonNull(item[i].getItemMeta()).getLocalizedName();
                    values[i] = StreamSerializer.getDefault().serializeItemStack(item[i]);
                }
                catch (Exception e)
                {
                    PlayertoSql.getInstance().getLogger().warning(e.getMessage());
                }
            }
        }
        return values;
    }

    //deserializeItemStack string and return itemStack array with values
    public static ItemStack[] setItemStackData(String[] string)
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
                catch (Exception e)
                {
                    PlayertoSql.getInstance().getLogger().warning(e.getMessage());
                }
            }
        }
        return itemStacks;
    }
}
