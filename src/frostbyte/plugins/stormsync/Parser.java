package frostbyte.plugins.stormsync;

import frostbyte.lib.rss.Message;
import java.util.List;

public class Parser 
{
    private Parser() {}
    
    public static void parse(List<Message> mList, StormSync plugin)
    {
        String desc = "";
        for(Message message:mList)
        {
            if(message.isCurrent())
                desc = message.getDesc();
        }
        if(desc.contains("thunder")||desc.contains("lightning")||desc.contains("storm")||desc.contains("Thunder")||desc.contains("Lightning")||desc.contains("Storm"))
        {
            plugin.makeStorm();
            return;
        }
        if(desc.contains("rain")||desc.contains("shower")||desc.contains("flurries")||desc.contains("snow")||desc.contains("wet")||desc.contains("Rain")||desc.contains("Shower")||desc.contains("Flurries")||desc.contains("Snow")||desc.contains("Wet"))
        {
            plugin.makeRain();
            return;
        }
        else
        {
            plugin.makeSunny();
            return;
        }
    }
}
