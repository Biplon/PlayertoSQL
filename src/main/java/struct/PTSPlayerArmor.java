package main.java.struct;

import org.bukkit.inventory.ItemStack;

public class PTSPlayerArmor
{
    private final String uuid;
    private final ItemStack[] inventoryArmor;

    private final ItemStack[] inventoryOffhand;

    public PTSPlayerArmor(String uuid, ItemStack[] inventoryArmor, ItemStack[] inventoryOffhand)
    {
        this.uuid = uuid;
        this.inventoryArmor = inventoryArmor;
        this.inventoryOffhand = inventoryOffhand;
    }

    public ItemStack[] getInventoryArmor()
    {
        return inventoryArmor;
    }

    public ItemStack[] getInventoryOffhand()
    {
        return inventoryOffhand;
    }

    public String getUuid()
    {
        return uuid;
    }

}
