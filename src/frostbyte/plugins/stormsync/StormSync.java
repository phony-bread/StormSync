package frostbyte.plugins.stormsync;

/******************
 * Plugin Name: Storm Sync
 * Main Class: StormSync.java
 * Author: _FrostByte_
 * Version: 1.0b
 ******************/

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class StormSync extends JavaPlugin
{
    private int taskNum;
    private int delay;
    private String worldName;
    private String URL;
    private String VERSION;
    
    static BukkitScheduler scheduler;
    static Server server;
    static World world;
    static final Random random = new Random();
    
    File configFile = new File(this.getDataFolder() + "/config.yml");
    
    @Override
    public void onEnable()
    {
        VERSION = getDescription().getVersion();
        saveDefaultConfig();
        loadConfig();
        System.out.println("Delay: " + delay);
        System.out.println("URL: " + URL);
        try
        {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        }
        catch(IOException e)
        {
            this.getServer().getLogger().log(Level.WARNING, "Could not connect to MCStats.org, Stats tracking disabled");
        }
        taskNum = scheduler.scheduleSyncRepeatingTask(this, new Syncer(this), delay, delay);
    }
    
    @Override
    public void onDisable()
    {
        scheduler.cancelTask(taskNum);
        super.onDisable();
    }
    
    /**
     * <p>Loads/reloads instance data from the config.</p>
     */
    public void loadConfig()
    {
        URL = getConfig().getString("url");
        delay = getConfig().getInt("delay");
        worldName = getConfig().getString("worldname");
        server = this.getServer();
        world = server.getWorld(worldName);
        scheduler = this.getServer().getScheduler();
    }
    
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("stormsync"))
        {
            if(args.length<1)
                cs.sendMessage("Need arguments!");
            else
            {
                if(args[0].equalsIgnoreCase("version"))
                {
                    cs.sendMessage(ChatColor.GREEN + "Storm Sync v" + ChatColor.WHITE + VERSION);
                    cs.sendMessage(ChatColor.DARK_GREEN + "By " + ChatColor.LIGHT_PURPLE + "_FrostByte_");
                    cs.sendMessage(ChatColor.DARK_GREEN + "dev.bukkit.org/bukkit-plugins/storm-sync/");
                }
                else if(args[0].equalsIgnoreCase("reload"))
                {
                    loadConfig();
                    cs.sendMessage(ChatColor.AQUA + "Storm Sync config reloaded!");
                }
                else if(args[0].equalsIgnoreCase("rain")||args[0].equalsIgnoreCase("wet"))
                {
                    makeRain();
                }
                else if(args[0].equalsIgnoreCase("thunder")||args[0].equalsIgnoreCase("lightning")||args[0].equalsIgnoreCase("storm"))
                {
                    makeStorm();
                }
                else if(args[0].equalsIgnoreCase("sunny")||args[0].equalsIgnoreCase("clear")||args[0].equalsIgnoreCase("sun"))
                {
                    makeSunny();
                }
                else
                    cs.sendMessage(ChatColor.RED + "Unknown argument! Try reload or version.");
            }
            return true;
        }
        return false;
    }
    
    public void makeRain()
    {
        world.setStorm(true);
        delay();
    }
    
    public void makeStorm()
    {
        world.setThundering(true);
        delay();
        world.setThunderDuration(delay+200);
    }
    
    public void makeSunny()
    {
        world.setThundering(false);
        world.setStorm(false);
        delay();
    }
    
    public void delay()
    {
        world.setWeatherDuration(delay+200);
    }
}