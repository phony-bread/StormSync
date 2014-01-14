package frostbyte.lib.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class Fetcher 
{
   final URL url;
   
   public Fetcher(URL url)
   {
       this.url = url;
   }
   
   public ArrayList<Message> fetchFeed()
   {
       ArrayList<Message> messages = new ArrayList<>();
       try
       {
           String title = "";
           String description = "";
           
           XMLInputFactory inputFactory = XMLInputFactory.newInstance();
           InputStream in = read();
           XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
           while(eventReader.hasNext())
           {
               XMLEvent event = eventReader.nextEvent();
               if(event.isStartElement())
               {
                   String localPart = event.asStartElement().getName().getLocalPart();
                   switch(localPart)
                   {
                       case "title":
                           title = getCharacterData(event, eventReader);
                           break;
                       case "description":
                           description = getCharacterData(event, eventReader);
                           break;
                   }
               }
               else if(event.isEndElement())
               {
                   if(event.asEndElement().getName().getLocalPart().equals("item"))
                   {
                       title = title.replaceAll("\t", "  ");
                       description = description.replaceAll("\t", "  ");
                       Message message = new Message(title, description);
                       messages.add(message);
                   }
               }
           }
       }
       catch(XMLStreamException e)
       {
           throw new RuntimeException(e);
       }
       return messages;
   }
   
   private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException
   {
       String result = "";
       event = eventReader.nextEvent();
       if(event.isCharacters())
       {
           result = event.asCharacters().getData();
       }
       return result;
   }
   
   private InputStream read()
   {
       try
       {
           return url.openStream();
       }
       catch (IOException e)
       {
           throw new RuntimeException(e);
       }
   }
}
