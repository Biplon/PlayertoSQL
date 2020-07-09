package main.java.command;

import main.java.PlayertoSql;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClearPlayerFiles implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        //check is player and have perms
        if (commandSender instanceof Player)
        {
            Player player = (Player) commandSender;
            if (player.hasPermission("pts.ptsadmin"))
            {
                //start async task to clear playerLogFiles
                Bukkit.getScheduler().runTaskLaterAsynchronously(PlayertoSql.getInstance(), () -> PlayertoSql.getInstance().getPlayerManager().clearPlayerFile(), 2L);
                return true;
            }
        }
        return false;
    }
}
