package com.clusterfactions.clustercore.util.unicode;

import lombok.Getter;

public enum CharRepo {
	
	MENU_CONTAINER("\uF808"+"섥"),
	TEST_BUTTON("\uF80C" + "\uF80A" + "不"),
	
	INSET1("\uF801"),
	INSET2("\uF802"),
	INSET3("\uF803"),
	INSET4("\uF804"),
	INSET5("\uF805"),
	INSET6("\uF806"),
	INSET7("\uF807"),
	INSET8("\uF808"),
	INSET9("\uF809");
	
	
	private CharRepo(String unicode)
	{
		this.ch = unicode;
	}
	
	@Getter private String ch;
	
}
