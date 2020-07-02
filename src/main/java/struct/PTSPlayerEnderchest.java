package main.java.struct;

import org.bukkit.inventory.ItemStack;

public class PTSPlayerEnderchest
{
    private final String uuid;
    private final ItemStack[] inventoryEnderchest;

    public PTSPlayerEnderchest(String uuid, ItemStack[] inventoryEnderchest)
    {
        this.uuid = uuid;
        this.inventoryEnderchest = inventoryEnderchest;
    }

    public ItemStack[] getInventoryEnderchest()
    {
        return inventoryEnderchest;
    }

    public String getUuid()
    {
        return uuid;
    }
}
