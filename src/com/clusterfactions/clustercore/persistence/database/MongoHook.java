  
package com.clusterfactions.clustercore.persistence.database;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.persistence.serialization.VariableSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoHook {
	MongoCollection<Document> collection = null;
	MongoClient mongoClient;
	MongoDatabase mongoDatabase;
	
	private static final String ip = "192.168.250.100";
	private static final int port = 27017;
	
	
	
	public MongoHook() {		
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClusterCore.getInstance(), new Runnable() {
        @Override
        public void run() {
        	init();
        }
	}, 1);
	}
	
	private void init() {
		try {
			Bukkit.getConsoleSender().sendMessage("Connecting to MongoDB");
			mongoClient = new MongoClient(new ServerAddress(ip, port));			
			mongoDatabase = mongoClient.getDatabase("clustercore");
			collection = mongoDatabase.getCollection("players");		
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public boolean valueExists(String varName, String varValue, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document document = (Document) collection.find(new Document(varName, varValue)).first();
		return document != null;
	}
	
	public void saveObject(String id, String columnName, Object data, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document found = (Document) collection.find(new Document("_id", id)).first();	    
		if(found == null) {
			Document document = new Document("_id", id);
			document.append(columnName, data);
			collection.insertOne(document);
		}else {
			Bson updatedValue = new Document(columnName, data);			
			Bson updateOperation = new Document("$set", updatedValue);
			collection.updateOne(found, updateOperation);
		}
	}
	public String getObject(String id, String columnName, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document document = (Document) collection.find(new Document("_id", id)).first();
		if(document == null) return null;
		return document.getString(columnName);
	}
	
	
	@SuppressWarnings("unchecked")
	public void saveData(String id, Object data, String collectionName) {
		collection = mongoDatabase.getCollection(collectionName);
		
		Map<String, Object> variableMap = new HashMap<>();
		
    	for(Field field : data.getClass().getDeclaredFields()) {
    		try {
    			field.setAccessible(true);
    			Object value = field.get(data);
        		if(field.getAnnotation(DoNotSerialize.class) != null) continue;
        		if(field.getAnnotation(AlternateSerializable.class) != null) 
        			value = ((VariableSerializer<Object>)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(data));
        		variableMap.put(field.getName(), value);
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	Document document = (Document) collection.find(new Document("_id", id)).first() ;
    	for(Entry<String, Object> entrySet : variableMap.entrySet()) {
    		if(document != null){
    			Bson bsonValue = new Document(entrySet.getKey(), entrySet.getValue());
    			Bson bsonOperation = new Document("$set", bsonValue);
    			collection.updateOne(document, bsonOperation);
    		} else {
    			Document newValue = new Document("_id", id);
    			newValue.append(entrySet.getKey(), entrySet.getValue());
    			collection.insertOne(newValue);
    		}
    	}
	}
	
	public <T> T getObject(String id, Class<T> clazz, String collectionName) {
		T object = null;
		try {
			collection = mongoDatabase.getCollection(collectionName);
			object = clazz.getDeclaredConstructor().newInstance();
		
			Document document = (Document) collection.find(new Document("_id", id)).first();
		
			for(Field field : clazz.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					Object value = null;
    		
					if(field.getAnnotation(DoNotSerialize.class) != null) continue;
					if(field.getAnnotation(AlternateSerializable.class) != null) 
						value = ((VariableSerializer<?>)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).deserialize(document.getString(field.getName()));
					else
						value = document.get(field.getName());
    			
					field.set(object, value);

				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return object;

	}
}