package pts.java.command;

import pts.java.PlayertoSql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class CommandAddDisablePlayerLoad implements CommandExecutor
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
        if (args.length == 1)
        {
            //search for player
            for (Player p : getServer().getOnlinePlayers())
            {
                if (p.getName().equals(args[0]))
                {
                    //add player to DisablePlayerLoad list
                    PlayertoSql.getInstance().getPlayerManager().addDisablePlayerLoad(p.getUniqueId());
                    return true;
                }
            }
        }
        return false;
    }
}


