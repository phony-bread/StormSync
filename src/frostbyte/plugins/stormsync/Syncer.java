package frostbyte.plugins.stormsync;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


public class Syncer extends BukkitRunnable
{
    private final StormSync plugin;
    private final Server server;
    
    public Syncer(StormSync plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
    
    @Override
    public void run()
    {
        List<World> worlds = server.getWorlds();
        server.broadcastMessage("Sync called");
    }
}
