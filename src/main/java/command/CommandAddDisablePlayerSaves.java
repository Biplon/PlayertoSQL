package main.java.command;

import main.java.PlayertoSql;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class CommandAddDisablePlayerSaves implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if (args.length == 1)
        {
            if (commandSender instanceof Player)
            {
                Player player = (Player) commandSender;
                if (!player.hasPermission("pts.ptsdiplsa"))
                {
                    return false;
                }
            }
            for (Player p : getServer().getOnlinePlayers())
            {
                if (p.getName().equals(args[0]))
                {
                    PlayertoSql.getInstance().getPlayerManager().addDisablePlayerSave(p.getUniqueId().toString());
                    return true;
                }
            }
        }
        return false;
    }
}
