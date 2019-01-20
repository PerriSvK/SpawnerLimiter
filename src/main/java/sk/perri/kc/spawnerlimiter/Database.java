package sk.perri.kc.spawnerlimiter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Arrays;

public class Database
{
    /*
     * +---------------------------------------------------+
     * |                   sl_records                      |
     * +---------------------------------------------------+
     * |   nick  |    ip   | active | location |    date   |
     * +---------+---------+--------+----------+-----------+
     * | VARCHAR | VARCHAR |   INT  |   TEXT   | TIMESTAMP |
     * +---------------------------------------------------+
     */

    private Connection conn = null;

    Database(String host, String user, String pass, String db)
    {
        try
        {
            conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+db+"?useSSL=no", user, pass);
            Bukkit.getLogger().info("[SpawnerLimiter] [I] Pripojene ku databaze "+db);
            createTable();
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Neviem sa pripojit ku databaze e: "+e.toString());
            Bukkit.getPluginManager().disablePlugin(Main.self);
        }
    }

    void addRecord(Player player, Location location)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO sl_records VALUES (?, ?, 1, ?, DEFAULT )");
            ps.setString(1, player.getName());
            ps.setString(2, player.getAddress().getHostName());
            ps.setString(3, String.join(";", Arrays.asList(location.getWorld().getName(),
                Integer.toString(location.getBlockX()), Integer.toString(location.getBlockY()),
                Integer.toString(location.getBlockZ()))));
            ps.execute();
        }
        catch (Exception e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Error pri zapisovani do DB!");
        }
    }

    void nullRecord(Location location)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("UPDATE sl_records SET active='0' WHERE location=?");
            ps.setString(1, String.join(";", Arrays.asList(location.getWorld().getName(),
                Integer.toString(location.getBlockX()), Integer.toString(location.getBlockY()),
                Integer.toString(location.getBlockZ()))));

            ps.execute();
        }
        catch (Exception e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Error pri nulovani v DB!");
        }
    }

    int getCount(Player player)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS pocet FROM sl_records WHERE "+
                (Main.self.ipBased ? "(nick=? OR ip=?)" : "nick=?")+" AND active > 0");
            ps.setString(1, player.getName());
            if(Main.self.ipBased)
                ps.setString(2, player.getAddress().getHostName());

            ps.execute();

            ResultSet rs = ps.getResultSet();
            rs.next();
            int pocet = rs.getInt("pocet");
            rs.close();
            ps.close();

            return pocet;
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Error pri zistovani poctu v DB! e: "+e.getMessage());
            return 0;
        }
    }

    void createTable()
    {
        try
        {
            String sql = "CREATE TABLE IF NOT EXISTS sl_records\n" +
                "(\n" +
                "    nick VARCHAR(20) NOT NULL,\n" +
                "    ip VARCHAR(50) NOT NULL,\n" +
                "    active INT(2) DEFAULT 1 NOT NULL,\n" +
                "    location VARCHAR(200) NOT NULL,\n" +
                "    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL\n" +
                ");";

            conn.createStatement().execute(sql);
        }
        catch (Exception e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Error pri vytvarani tabulky v DB! e: "+e.toString());
        }
    }

    void close()
    {
        try
        {
            if(conn != null)
                conn.close();
            conn = null;
        }
        catch (Exception e)
        {
            Bukkit.getLogger().warning("[SpawnerLimiter] [E] Error pri zatvarani spojenia s DB! e: "+e.toString());
        }
    }
}
