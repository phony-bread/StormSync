package frostbyte.plugins.stormsync;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


public class Syncer extends BukkitRunnable
{
    private final StormSync plugin;
    private final Server server;
    private final World world;
    
    public Syncer(StormSync plugin, World world)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.world = world;
    }
    
    @Override
    public void run()
    {
    }
}
