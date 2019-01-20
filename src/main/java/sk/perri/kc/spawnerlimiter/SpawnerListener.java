package sk.perri.kc.spawnerlimiter;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerListener implements Listener
{
    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event)
    {
        if(event.getBlockPlaced().getType() != Material.MOB_SPAWNER || event.getPlayer().hasPermission("spawnerlimiter.bypass"))
            return;

        final int[] limit = {Main.self.limits.get("default")};
        Main.self.limits.forEach((k, v) ->
        {
            if(event.getPlayer().hasPermission("spawnerlimiter.group."+k) && v > limit[0])
                limit[0] = v;
        });

        int count = Main.self.db.getCount(event.getPlayer());

        if(count < limit[0])
        {
            Main.self.db.addRecord(event.getPlayer(), event.getBlockPlaced().getLocation());
            event.getPlayer().sendMessage(String.format(Main.self.msg.get("remaining"), limit[0]-count-1));
        }
        else
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.self.msg.get("too-many"));
        }
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event)
    {
        if(event.getBlock().getType() != Material.MOB_SPAWNER)
            return;

        Main.self.db.nullRecord(event.getBlock().getLocation());
    }
}
