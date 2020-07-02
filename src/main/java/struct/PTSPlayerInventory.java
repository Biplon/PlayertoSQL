package main.java.struct;

import org.bukkit.inventory.ItemStack;

public class PTSPlayerInventory
{
    private final String uuid;
    private final ItemStack[] inventory;

    public PTSPlayerInventory(String uuid, ItemStack[] inventory)
    {
        this.uuid = uuid;
        this.inventory = inventory;
    }

    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public String getUuid()
    {
        return uuid;
    }
}
