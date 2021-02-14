package com.clusterfactions.clustercore.persistence.serialization;

public abstract class VariableSerializer {
	public abstract String serialize(Object obj);
	
	public abstract Object deserialize(String str);
}
