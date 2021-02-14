package com.clusterfactions.clustercore.persistence.serialization;

import com.clusterfactions.clustercore.core.permission.PermissionGroup;

//Base64 Encoder
public class PermissionGroupSerializer extends VariableSerializer{
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof PermissionGroup)) return null;
		return ((PermissionGroup) obj).getGroupID() ;
		
	}

	@Override
	public Object deserialize(String str) {
		return PermissionGroup.getGroup(str);
	}
    

}