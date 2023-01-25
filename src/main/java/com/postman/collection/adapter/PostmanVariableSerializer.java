package com.postman.collection.adapter;

import com.postman.collection.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;

import com.google.gson.JsonSerializationContext;


import java.lang.reflect.Type;

public class PostmanVariableSerializer implements JsonSerializer<PostmanVariable> {
     
    @Override
    public JsonElement serialize(PostmanVariable src, Type typeOfSrc, JsonSerializationContext context) {
        
        JsonObject jsonMap = new JsonObject();
        jsonMap.addProperty("key", src.getKey());
        String strKey = "value";
        if(src.getType() != null && src.getType().equals("boolean") && src.getValue() != null) {
            switch (src.getValue()) {
                case "true": {
                    jsonMap.addProperty(strKey,true);
                    break;
                }
                case "false": {
                    jsonMap.addProperty(strKey,false);
                    break;
                }
                default: {
                    jsonMap.addProperty(strKey, src.getValue());
                    break;
                }
                    
                    
                }
            
        }
        else {
            jsonMap.addProperty(strKey, src.getValue());
        }
        if(src.getType() != null) {
            jsonMap.addProperty("type", src.getType());
        }
        if(src.getDescription() != null) {
            jsonMap.addProperty("description", src.getDescription());
        }

        return jsonMap;
    }

}
