  
package com.clusterfactions.clustercore.persistence.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
		Document document = collection.find(new Document(varName, varValue)).first();
		return document != null;
	}
	
	public List<String> getAllList(String columnName, String collectionName){
		collection = mongoDatabase.getCollection(collectionName);
	    FindIterable<Document> iterDoc = collection.find();
	    MongoCursor<Document> it = iterDoc.iterator();
	    List<String> ret = new ArrayList<>();
	    while (it.hasNext()) {
	    	Document doc = new Document(it.next());
	    	ret.add(doc.getString(columnName));
	    }
	    return ret;
	}
	
	public void saveValue(String id, String columnName, Object data, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document found = collection.find(new Document("_id", id)).first();	    
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
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String id, String columnName, Class<T> clazz, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document document = (Document) collection.find(new Document("_id", id)).first();
		if(document == null) return null;
		return (T) document.get(columnName);
	}
	
	
	public void deleteData(String id, String collectionName) {
		collection = mongoDatabase.getCollection(collectionName);
		Document document = new Document("_id", id);
		collection.deleteOne(document);
	}
	
	@SuppressWarnings("unchecked")
	public void saveData(String id, Object data, String collectionName) {
		collection = mongoDatabase.getCollection(collectionName);
		
		Map<String, Object> variableMap = new HashMap<>();
		
    	for(Field field : data.getClass().getDeclaredFields()) {
    		try {
    			field.setAccessible(true);
    			Object value = field.get(data);
    			if(field.getName().contains("SWITCH_TABLE")) continue;
        		if(field.getAnnotation(DoNotSerialize.class) != null) continue;
        		if(field.getAnnotation(AlternateSerializable.class) != null) 
        			value = ((VariableSerializer<Object>)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(data));
        		variableMap.put(field.getName(), value);
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	Document document = collection.find(new Document("_id", id)).first();
    	if(document == null){
			Document newValue = new Document("_id", id);
			collection.insertOne(newValue);
			document = collection.find(new Document("_id", id)).first();
    	}
    	for(Entry<String, Object> entrySet : variableMap.entrySet()) {
    		if(entrySet.getValue() == null) continue;
    		Bson bsonValue = new Document(entrySet.getKey(), entrySet.getValue());
    		Bson bsonOperation = new Document("$set", bsonValue);
    		collection.updateOne(document, bsonOperation);
    	}
	}
	
	public <T> T getObject(String searchValue, String columnName, Class<T> clazz, String collectionName, boolean ignoreCase){
		T object = null;
		try {
			collection = mongoDatabase.getCollection(collectionName);
			object = clazz.getDeclaredConstructor().newInstance();
		
			Document document = collection.find(new Document(columnName, searchValue)).first();
			if(document == null) return object;
			for(Field field : clazz.getDeclaredFields()) {
				try {
					if(field == null) continue;
					if(field.getName().isEmpty()) continue;
					
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
	
	public <T> T getObject(String searchValue, String columnName, Class<T> clazz, String collectionName){
		T object = null;
		try {
			collection = mongoDatabase.getCollection(collectionName);
			object = clazz.getDeclaredConstructor().newInstance();
		
			Document document = collection.find(new Document(columnName, searchValue)).first();
			if(document == null) return object;
			for(Field field : clazz.getDeclaredFields()) {
				try {
					if(field == null) continue;
					if(field.getName().isEmpty()) continue;
					
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
	
	public <T> T getObject(String id, Class<T> clazz, String collectionName) {
		return getObject(id, "_id", clazz, collectionName);

	}
}