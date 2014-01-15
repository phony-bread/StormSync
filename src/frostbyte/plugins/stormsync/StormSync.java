package frostbyte.plugins.stormsync;

/******************
 * Plugin Name: Storm Sync
 * Main Class: StormSync.java
 * Author: _FrostByte_
 * Version: 1.0b
 ******************/

import frostbyte.lib.rss.Fetcher;
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
import frostbyte.lib.rss.Message;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class StormSync extends JavaPlugin
{
    private int taskNum;
    private int delay;
    private String worldName;
    private String URL;
    private String VERSION;
    private boolean shouldSync;
    static BukkitScheduler scheduler;
    static Server server;
    static World world;
    static boolean log;
    static URL feedUrl;
    static Logger logger;
    static Fetcher fetcher;
    static ConsoleCommandSender console;
    static List<Message> entries = new ArrayList<>();
    static final Random random = new Random();
    
    File configFile = new File(this.getDataFolder() + "/config.yml");
    
    @Override
    public void onEnable()
    {
        VERSION = getDescription().getVersion();
        saveDefaultConfig();
        loadConfig();
        try
        {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        }
        catch(IOException e)
        {
            this.getServer().getLogger().log(Level.WARNING, "Could not connect to MCStats.org, Stats tracking disabled");
        }
        try
        {
           this.feedUrl = new URL(URL);
           fetcher = new Fetcher(feedUrl);
           System.out.println("Feed fetch successful...");
           
        taskNum = scheduler.scheduleSyncRepeatingTask(this, new Syncer(this, world), delay, delay);
        }
        catch(MalformedURLException e)
        {
            console.sendMessage("[StormSync] " + ChatColor.YELLOW + URL + ChatColor.RED + " is a malformed URL. An XML RSS feed is needed. Storm Sync disabled.");
            onDisable();
        }
    }
    
    @Override
    public void onDisable()
    {
        scheduler.cancelTask(taskNum);
        setEnabled(false);
        super.onDisable();
    }
    
    /**
     * <p>Loads/reloads instance data from the config.</p>
     */
    public void loadConfig()
    {
        URL = getConfig().getString("url");
        shouldSync = getConfig().getBoolean("startsynced");
        delay = getConfig().getInt("delay");
        delay = delay * 20;
        log = getConfig().getBoolean("log");
        worldName = getConfig().getString("worldname");
        server = this.getServer();
        logger = server.getLogger();
        world = server.getWorld(worldName);
        scheduler = this.getServer().getScheduler();
        console = server.getConsoleSender();
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
                else if(args[0].equalsIgnoreCase("sync"))
                {
                    Parser.parse(fetcher.fetchFeed(), this);
                    shouldSync = true;
                }
                else if(args[0].equalsIgnoreCase("unsync"))
                {
                    shouldSync = false;
                }
                else if(args[0].equalsIgnoreCase("synconce"))
                {
                    Parser.parse(fetcher.fetchFeed(), this);
                }
                else if(args[0].equalsIgnoreCase("testfeed"))
                {
                    entries = fetcher.fetchFeed();
                    int i = 0;
                    for(Message m:entries)
                    {
                        System.out.println("Entry: " + i);
                        System.out.println("Current: " + m.isCurrent());
                        System.out.println("Title: " + m.getTitle());
                        System.out.println("Desc:  " + m.getDesc() + "\n");
                        i++;
                    }
                }
                else
                    cs.sendMessage(ChatColor.RED + "Unknown argument! Try reload or version.");
            }
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("forecast"))
        {
            entries = fetcher.fetchFeed();
            if(cs instanceof Player)
            {
                for(Message m:entries)
                {
                    if(!m.isCurrent())
                        cs.sendMessage(m.toString());
                }
            }
            else if(cs instanceof ConsoleCommandSender)
            {
                for(Message m:entries)
                {
                    if(!m.isCurrent())
                        server.broadcastMessage(m.toString());
                }
            }
            return true;
        }
        return false;
    }
    
    public void makeRain()
    {
        world.setStorm(true);
        delay();
        if(log)
            logger.log(Level.OFF, "[StormSync] Setting weather to rainy in world: {0}", worldName);
    }
    
    public void makeStorm()
    {
        world.setThundering(true);
        delay();
        world.setThunderDuration(delay+200);
        if(log)
            logger.log(Level.OFF, "[StormSync] Setting weather to stormy in world: {0}", worldName);
    }
    
    public void makeSunny()
    {
        world.setThundering(false);
        world.setStorm(false);
        delay();
        if(log)
            logger.log(Level.OFF, "[StormSync] Setting weather to clear in world: {0}", worldName);
    }
    
    public void delay()
    {
        world.setWeatherDuration(delay+200);
    }
    
    public boolean shouldSync()
    {
        return this.shouldSync;
    }
}