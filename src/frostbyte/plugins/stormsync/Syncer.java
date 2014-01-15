package frostbyte.plugins.stormsync;

import java.util.logging.Level;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


public class Syncer extends BukkitRunnable
{
    private final StormSync plugin;
    private final Server server;
    
    public Syncer(StormSync plugin, World world)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
    
    @Override
    public void run()
    {
        if(plugin.shouldSync())
        {
            Parser.parse(StormSync.fetcher.fetchFeed(), plugin);
            if(StormSync.log)
                server.getLogger().log(Level.OFF, "Synchronizing weather...");
        }
    }
}
