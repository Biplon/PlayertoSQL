package main.java.struct;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

// object to store armor and offhand items if player could not save
public class PTSPlayerArmor
{
    private final UUID uuid;
    private final ItemStack[] inventoryArmor;

    private final ItemStack[] inventoryOffhand;

    public PTSPlayerArmor(UUID uuid, ItemStack[] inventoryArmor, ItemStack[] inventoryOffhand)
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

    public UUID getUuid()
    {
        return uuid;
    }
}
