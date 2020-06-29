package main.java.command;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class CommandSavePlayer implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if (args.length == 1)
        {
            if (commandSender instanceof Player)
            {
                Player player = (Player) commandSender;
                if (!player.hasPermission("pts.ptssaveplayer"))
                {
                    return false;
                }
            }
            for(Player p : getServer().getOnlinePlayers())
            {
                if(p.getName().equals(args[0]))
                {
                    PlayertoSql.getInstance().getPlayerManager().addDisablePlayerSave(p.getUniqueId().toString());
                    Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    {
                        PlayertoSql.getInstance().getPlayerManager().onPlayerQuit(p.getUniqueId().toString(), p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents());
                    });
                    return true;
                }
            }

        }
        return false;
    }
}
