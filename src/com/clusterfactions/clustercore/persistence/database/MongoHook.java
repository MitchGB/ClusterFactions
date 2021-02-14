package com.clusterfactions.clustercore.persistence.database;

import java.lang.reflect.Field;

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
	@SuppressWarnings("rawtypes")
	MongoCollection collection = null;
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
	
	@SuppressWarnings("unchecked")
	public void saveData(String id, Object data, String collectionName) {
		this.collection = mongoDatabase.getCollection(collectionName);
		Document found = (Document) collection.find(new Document("_id", id)).first();
		if(found != null) {
			Field[] allFields = data.getClass().getDeclaredFields(); 
	    	for(Field field : allFields) {
	    		field.setAccessible(true);
	    		try {

	    			Bson updatedValue = new Document(field.getName(), field.get(data));
	        		if(field.getAnnotation(DoNotSerialize.class) != null) continue;
	    			if(field.getAnnotation(AlternateSerializable.class) != null) 
	    				updatedValue = new Document(field.getName(), ((VariableSerializer)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(data)));
	    			
	    			Bson updateOperation = new Document("$set", updatedValue);
	    			collection.updateOne(found, updateOperation);
	    		}catch(Exception e) {
	    		}
	    	}
		}
		else {
			Document document = new Document("_id", id);
			Field[] allFields = data.getClass().getDeclaredFields(); 
	    	for(Field field : allFields) {
	    		field.setAccessible(true);
	    		try {
	    			if(field.get(data) == null) continue;
	    			if(field.getAnnotation(DoNotSerialize.class) != null) continue;
	    			if(field.getAnnotation(AlternateSerializable.class) != null) {
	    				document.append(field.getName(), ((VariableSerializer)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(data)) );
	    				continue;
	    			}
	    			
	    			document.append(field.getName(), field.get(data));

	    		}catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	try {
			collection.insertOne(document);
	    	}catch(Exception e) {
	    		
	    	};
		}
		
		
		
	}
	
	public boolean valueExists(String varName, String varValue, String collectionName) {

		this.collection = mongoDatabase.getCollection(collectionName);
		Document document = (Document) collection.find(new Document(varName, varValue)).first();
		if(document == null)
			return false;
		return true;
	}
	
	public <T> T getObject(String id, Class<T> clazz, String collectionName) {
		this.collection = mongoDatabase.getCollection(collectionName);
		T obj = null;
		try {
		obj = clazz.getDeclaredConstructor().newInstance();
		}catch(Exception e) {}
		Field[] allFields = clazz.getDeclaredFields(); 
		Document document = (Document) collection.find(new Document("_id", id)).first();
		for(Field field : allFields) {
    		field.setAccessible(true);

    		Object val = null;
    		try {
    		if(field.getAnnotation(DoNotSerialize.class) != null) continue;
			if(field.getAnnotation(AlternateSerializable.class) != null) {
				val = ((VariableSerializer)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).deserialize(document.getString(field.getName()));
			}
			else
				 val = document.get(field.getName());
			
    		}catch(Exception e) {}
    		
    		try {
    		field.set(obj, val);
    		if(field.getName().equals("playerUUID") && val == null)
    			field.set(obj, id);

    		}catch(Exception e) {}
		}
		return obj;

	}
	
	public void disable() {
		mongoClient.close();
	}
}
