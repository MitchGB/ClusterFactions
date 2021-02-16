package com.clusterfactions.clustercore.persistence.serialization;

public abstract class VariableSerializer<T> {
	public abstract String serialize(T obj);
	
	public abstract T deserialize(String str);
}
