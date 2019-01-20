package sk.perri.kc.spawnerlimiter;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin
{
    static Main self;

    Database db;
    Map<String, Integer> limits = new HashMap<>();
    Map<String, String> msg = new HashMap<>();
    boolean ipBased = true;

    public void onEnable()
    {
        self = this;

        if(!getDataFolder().exists())
            getDataFolder().mkdirs();

        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getScheduler().runTaskLater(this, ()-> db = new Database(
            getConfig().getString("database.host"), getConfig().getString("database.user"),
            getConfig().getString("database.pass"), getConfig().getString("database.db")), 40);

        getConfig().getConfigurationSection("groups").getKeys(false).forEach(k ->
            limits.put(k, getConfig().getInt("groups."+k)));

        getConfig().getConfigurationSection("msg").getKeys(false).forEach(k ->
            msg.put(k, ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg."+k))));

        ipBased = getConfig().getBoolean("ip-based");

        getCommand("spawnerlimiter").setExecutor(new LimiterExecutor());
        getServer().getPluginManager().registerEvents(new SpawnerListener(), this);

        getLogger().info("Plugin sa aktivoval");
    }

    public void onDisable()
    {
        db.close();
        getLogger().info("Plugin sa deaktivoval");
    }
}
