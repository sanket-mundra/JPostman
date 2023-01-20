package com.postman.collection;


import java.util.ArrayList;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.net.URI;
import java.util.Set;
import java.util.Iterator;

//import java.util.UUID;
// foo
public class PostmanItem extends PostmanCollectionElement  {
    
    private String description; 
    private ArrayList<PostmanEvent> event = null;
    private PostmanRequest request = null;
    private ArrayList<PostmanResponse> response = null;
    private ArrayList<PostmanItem> item;
    private String name; 
    private transient ArrayList<ValidationMessage> validationMessages;
    

    //private transient String key = UUID.randomUUID().toString();
    private transient PostmanItem parent = null;
    @Override
    public String getKey() {
        
        //return this.key;
        return this.name;
    }

    

    

 

    
    public PostmanItem(String name, PostmanItem parent) {
        this(name);
        this.setParent(parent);

    }
    public PostmanItem(String name)
    {
        this.setName(name);
        
    }

    public PostmanItem() {
        
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ArrayList<PostmanEvent> getEvents() {
        
        return event;
    }

    public PostmanEvent getEvent(enumEventType evtType) {
        if(event == null) {
            return null;
        }
        for(int i = 0; i < event.size(); i++)
        {
            if(event.get(i).getEventType() == evtType) {
                return event.get(i);
            }
        }
        return null;
    }

    public void setEvents(ArrayList<PostmanEvent> events) {
        this.event = events;
    }
    public PostmanRequest getRequest() {
        return request;
    }
    public void setRequest(PostmanRequest request) {
        this.request = request;
    }
    public ArrayList<PostmanResponse> getResponses() {
        return response;
    }
    public void setResponses(ArrayList<PostmanResponse> response) {
        this.response = response;
    }
    public ArrayList<PostmanItem> getItems() {
        return item;
    }

    public enumPostmanItemType getItemType() {
        if(this.request == null)
        {
            return enumPostmanItemType.FOLDER;
        }
        else
        {
            return enumPostmanItemType.REQUEST;
        }
    }
    public void setItems(ArrayList<PostmanItem> items) {
        this.item = items;
    }

    public PostmanItem getItem(String key) {
        return this.getItem(key, false);
    }

    public PostmanItem getItem(String key, boolean parent) {
        PostmanItem result = null;
        if (this.item == null)
        {
            return null;
        }
        //recursively traverse items looking for name == key
        for (PostmanItem curItem: item) {
            //System.out.println("Parsing: " + this.getName() + " PARENT: " + parent);
            if (item == null)
              return null;
            if (curItem.getKey().equals(key))
            {
                if (!parent) {
                    result = curItem;
                }
                else
                {
                    result = (PostmanItem) this;
                }
                
                break;
            }
            else
            {
                result = curItem.getItem(key, parent);
                if (result != null) {
                    break;
                }
            }
            
        }
    
        return result;
    }
   
   public void addResponse(PostmanResponse resp) throws Exception {
    if(this.response == null) {
        this.response = new ArrayList<PostmanResponse>();
    }
    this.response.add(resp);
       }

   


    public void addItems(ArrayList<PostmanItem> newItems) throws Exception {
        if(this.item == null) {
            this.item = new ArrayList<PostmanItem>();
        }
        this.item.addAll(newItems);
    }


    
    

    public void setParent(PostmanItem parent)
    {
        this.parent = parent;
    }

    public PostmanItem getParent()
    {
        return this.parent;
    }

    

    public ArrayList<PostmanItem> getItemsOfType(enumPostmanItemType ofType) {
        if(item == null)
        {
            return null;
        }
        ArrayList<PostmanItem> alItems = this.getItemsOfTypeImpl(ofType);
        return alItems;
    
    }

    public boolean hasItem(PostmanItem theItem) {
        if(item == null) {
            return false;
        }
        for(PostmanItem curItem: item)
        {
            if(curItem.equals(theItem));
            return true;
        }
        
        return false;
    }

    private ArrayList<PostmanItem> getItemsOfTypeImpl(enumPostmanItemType ofType)
    {
            ArrayList<PostmanItem> results = new ArrayList<PostmanItem>();
            

            if(item == null)
            {
                return null;
            }
            
            for (PostmanItem curItem: item)
            {
                if(curItem.getItemType() == ofType)
                {
                    results.add(curItem);
                    //System.out.println(curItem.getName());
                }
                try
                {
                    results.addAll(curItem.getItemsOfTypeImpl(ofType));

                }
                catch(Exception e)
                {

                }
                
            }

            
            return results;

            
    }


    public void setEvent(PostmanEvent newEvent) {
        if(event == null) {
            event = new ArrayList<PostmanEvent>();
        }
        if(this.getEvent(newEvent.getEventType()) == null) {
            event.add(newEvent);
        }
        else {
            event.remove(this.getEvent(newEvent.getEventType()));
            event.add(newEvent);
        }
    }

    public void addItem(PostmanItem newItem) throws Exception {
        
        if(newItem.equals(this)) {
            throw new Exception("Cannot add an object to itself, lolz");
        }
        if(this.getItemType() == enumPostmanItemType.REQUEST)
        {
            throw new Exception("Cannot add items to Requests");
        }
        if(this.item == null) {
            this.item = new ArrayList<PostmanItem>();
        }
        this.item.add(newItem);

    }

    public void addItem(PostmanItem newItem, int position) throws Exception {
        if(this.hasItem(newItem))
        {
            throw new Exception ("Item is already present");
        }
        //If the newitem already owns this item, it's a circular recursion
        if(newItem.getItem(this.getKey()) != null)

        {
            throw new Exception("Item [" + newItem.getKey() + "] already contains this item [" + this.getKey() );
        }
        this.item.add(position, newItem);
        
         
    }
    public void removeItem(PostmanItem oldItem) throws Exception
    {
        this.removeItem(oldItem.getKey());
    }

    public void setPreRequestScript(String code) throws Exception {
        PostmanScript prScript = new PostmanScript("text/javascript",code);
        PostmanEvent prEvent = new PostmanEvent(enumEventType.PRE_REQUEST, prScript);
        this.setEvent(prEvent);
    }

    public void setTestScript(String code) throws Exception {
        PostmanScript prScript = new PostmanScript("text/javascript",code);
        PostmanEvent prEvent = new PostmanEvent(enumEventType.TEST, prScript);
        this.setEvent(prEvent);
    }

    public void setPreRequestScript(String code, String type) {

    }

    public void removeItem(String key) throws Exception {
        if(item == null) {
            return;
        }
        for(PostmanItem curItem: item) {
            if(curItem.getKey().equals(key)) {
                this.item.remove(curItem);
            }

        }
        
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name){ 
        this.name = name;
    }


    


   
    
    
    
    
}
