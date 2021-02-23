package com.clusterfactions.clustercore.persistence.serialization;

import java.util.Locale;

//Base64 Encoder
public class LocaleSerializer extends VariableSerializer<Locale>{

	@Override
	public String serialize(Locale obj) {
    	if(obj == null) return null;
    	return obj.toLanguageTag();
	}

	@Override
	public Locale deserialize(String str) {
    	if(str==null || str.isEmpty() ) return null;
    	return Locale.forLanguageTag(str);
	}
    

}