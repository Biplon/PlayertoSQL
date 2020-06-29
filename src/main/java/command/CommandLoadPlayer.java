package main.java.command;

import main.java.PlayertoSql;
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
        if (args.length == 1)
        {
            if (commandSender instanceof Player)
            {
                Player player = (Player) commandSender;
                if (!player.hasPermission("pts.ptsloadplayer"))
                {
                    return false;
                }
            }
            for(Player p : getServer().getOnlinePlayers())
            {
                if(p.getName().equals(args[0]))
                {
                    Bukkit.getScheduler().runTaskAsynchronously(PlayertoSql.getInstance(), () ->
                    {
                        PlayertoSql.getInstance().getPlayerManager().onPlayerJoin (p);
                    });
                    return true;
                }
            }

        }
        return false;
    }
}


