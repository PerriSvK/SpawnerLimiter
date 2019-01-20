package sk.perri.kc.spawnerlimiter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LimiterExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if ((sender.hasPermission("spawnerlimiter.cmd") || sender.hasPermission("spawnerlimiter.cmd.me"))
            && args.length == 0)
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("Tento prikaz je iba pre hracov. Vyuzite: /spawnerlimiter <nick>");
                return true;
            }

            final String[] group = {"default"};
            Main.self.limits.forEach((k, v) ->
            {
                if(sender.hasPermission("spawnerlimiter.group."+k) && v > Main.self.limits.get(group[0]))
                    group[0] = k;
            });

            String res = Main.self.msg.get("info");
            if(res.contains("%s"))
                res = res.replace("%s", group[0]);

            if(res.contains("%d"))
                res = res.replace("%d", Integer.toString(Main.self.limits.get(group[0])-Main.self.db.getCount((Player) sender)));
            sender.sendMessage(res);
        }
        else if(((sender.hasPermission("spawnerlimiter.cmd")) || sender.hasPermission("spawnerlimiter.cmd.other"))
            && args.length == 1)
        {
            Player sub = Bukkit.getServer().getPlayer(args[0]);
            if(sub == null)
                sub = Bukkit.getOfflinePlayer(args[0]).getPlayer();

            final String[] group = {"default"};
            Player finalSub = sub;
            Main.self.limits.forEach((k, v) ->
            {
                if(finalSub.hasPermission("spawnerlimiter.group."+k) && v > Main.self.limits.get(group[0]))
                    group[0] = k;
            });

            String res = String.format(Main.self.msg.get("info-other"), args[0], group[0],Main.self.limits.get(group[0])- Main.self.db.getCount(sub));
            sender.sendMessage(res);
        }
        else
        {
            sender.sendMessage(Main.self.msg.get("no-perm"));
        }

        return true;
    }
}
