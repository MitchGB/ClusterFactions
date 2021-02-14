package com.clusterfactions.clustercore.core.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public enum PermissionGroup {
	
	ADMIN("ADMIN", "요", null, "*", "hythecraft.admin"),
	DEVELOPER("DEV", "&c&lDEV", ADMIN),
	DEFAULT("DEFAULT", "", null);
	
	
	PermissionGroup(String groupID, String groupPrefix, PermissionGroup inheritance, String... permissions)
	{
		this.groupID = groupID;
		this.groupPrefix = groupPrefix;
		this.permissions = Arrays.asList(permissions);
		this.inheritance = inheritance;
	}
	
	@Getter @Setter private String groupID;
	@Getter @Setter private String groupPrefix;
	@Getter @Setter private List<String> permissions;
	@Getter @Setter private PermissionGroup inheritance;
	
	public static PermissionGroup getGroup(String ID) {
		for(PermissionGroup g : PermissionGroup.values())
		{
			if(g.groupID.equalsIgnoreCase(ID))
				return g;
		}
		return null;
	}
	
	public List<String> getAllPerms(){
		if(inheritance == null) {
			return permissions;
		}else{
			List<String> perms = new ArrayList<>();
			perms.addAll(permissions);
			perms.addAll(inheritance.getAllPerms());
			return perms;
		}
	}
	
	public static List<String> getAllList(){
		List<String> ret = new ArrayList<>();
		for(PermissionGroup t : PermissionGroup.values())
			ret.add(t.toString());
		return ret;
	}
}
