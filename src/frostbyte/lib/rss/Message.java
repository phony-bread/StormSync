package frostbyte.lib.rss;

public class Message 
{
   private final String title, description;
   
   public Message(String title, String description)
   {
       this.title = title;
       this.description = description;
   }
   
   public String getTitle()
   {
       return title;
   }
   
   public String getDesc()
   {
       return description;
   }
   
   public boolean isCurrent()
   {
       return title.equalsIgnoreCase("Current Weather");
   }
   
   @Override
   public String toString()
   {
       return title + ": " + description + ".";
   }
}
