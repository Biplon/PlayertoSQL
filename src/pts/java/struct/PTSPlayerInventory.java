package pts.java.struct;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

// object to store inventory items if player could not save
public class PTSPlayerInventory
{
    private final UUID uuid;
    private final ItemStack[] inventory;

    public PTSPlayerInventory(UUID uuid, ItemStack[] inventory)
    {
        this.uuid = uuid;
        this.inventory = inventory;
    }

    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
