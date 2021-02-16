package com.clusterfactions.clustercore.core.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.clusterfactions.clustercore.util.unicode.CharRepo;

import lombok.Getter;
import lombok.Setter;

public enum PermissionGroup {
	
	ADMIN("ADMIN", CharRepo.RANK_ADMIN_TAG, null, "*", "clustercore.admin"),
	DEVELOPER("DEV", "&c&lDEV", ADMIN),
	OLYMPIAN("OLYMPIAN", CharRepo.RANK_OLYMPIAN_TAG, null),
	EMPYREAN("EMPYREAN", CharRepo.RANK_EMPYREAN_TAG, null),
	ANGELIC("ANGELIC", CharRepo.RANK_ANGELIC_TAG, null),
	DIVINE("DIVINE", CharRepo.RANK_DIVINE_TAG, null),
	CELESTIAL("CELESTIAL", CharRepo.RANK_CELESTIAL_TAG, null),
	MEMBER("MEMBER", CharRepo.RANK_PLAYER_TAG , null);
	
	
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
