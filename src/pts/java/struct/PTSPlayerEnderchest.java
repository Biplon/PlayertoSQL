package pts.java.struct;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

// object to store enderchest items if player could not save
public class PTSPlayerEnderchest
{
    private final UUID uuid;
    private final ItemStack[] inventoryEnderChest;

    public PTSPlayerEnderchest(UUID uuid, ItemStack[] inventoryEnderchest)
    {
        this.uuid = uuid;
        this.inventoryEnderChest = inventoryEnderchest;
    }

    public ItemStack[] getInventoryEnderChest()
    {
        return inventoryEnderChest;
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
