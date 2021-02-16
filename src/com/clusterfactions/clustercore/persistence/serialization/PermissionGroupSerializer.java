package com.clusterfactions.clustercore.persistence.serialization;

import com.clusterfactions.clustercore.core.permission.PermissionGroup;

//Base64 Encoder
public class PermissionGroupSerializer extends VariableSerializer<PermissionGroup>{
	@Override
	public String serialize(PermissionGroup obj) {
		if(!(obj instanceof PermissionGroup)) return null;
		return ((PermissionGroup) obj).getGroupID() ;
		
	}

	@Override
	public PermissionGroup deserialize(String str) {
		return PermissionGroup.getGroup(str);
	}
    

}