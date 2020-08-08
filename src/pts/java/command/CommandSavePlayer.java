package pts.java.command;

import pts.java.PlayertoSql;
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
        //check is player and have perms
        if (commandSender instanceof Player)
        {
            Player player = (Player) commandSender;
            if (!player.hasPermission("pts.ptsadmin"))
            {
                return false;
            }
        }
        //search for player
        for (Player p : getServer().getOnlinePlayers())
        {
            if (p.getName().equals(args[0]))
            {
                //1 args save player async
                if (args.length == 1)
                {
                    Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                            PlayertoSql.getInstance().getPlayerManager().savePlayer(p, p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(), false, "Command"));
                    return true;
                }
                //2 args save player sync 1 tick delay
                else if (args.length == 2)
                {
                    Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), () -> PlayertoSql.getInstance().getPlayerManager().savePlayer(p, p.getInventory().getStorageContents(), p.getInventory().getArmorContents(), p.getInventory().getExtraContents(), p.getEnderChest().getContents(), false, "Command"));
                    return true;
                }
            }
        }
        return false;
    }
}
