package pts.java.command;

import pts.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class CommandLoadPlayer implements CommandExecutor
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
                //1 args load player async
                if (args.length == 1)
                {
                    //start async task
                    Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                            PlayertoSql.getInstance().getPlayerManager().loadPlayer(p, false));
                }
                //2 args load player sync
                else if (args.length == 2)
                {
                    //start sync task 1 tick delay
                    Bukkit.getScheduler().runTask(PlayertoSql.getInstance(), () -> PlayertoSql.getInstance().getPlayerManager().loadPlayer(p, false));
                }
                return true;
            }
        }
        return false;
    }
}


