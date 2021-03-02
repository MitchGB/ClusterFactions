package com.clusterfactions.clustercore.util.unicode;

import java.lang.reflect.Field;

public class CharRepo {

	public static final String NEG1 ="\uF801";
	public static final String NEG2 ="\uF802";
	public static final String NEG3 ="\uF803";
	public static final String NEG4 ="\uF804";
	public static final String NEG5 ="\uF805";
	public static final String NEG6 ="\uF806";
	public static final String NEG7 ="\uF807";
	public static final String NEG8 ="\uF808";
	public static final String NEG16 ="\uF809";
	public static final String NEG32 ="\uF80A";
	public static final String NEG64 ="\uF80B";
	public static final String NEG128="\uF80C";
	public static final String NEG256="\uF80D";
	public static final String NEG512="\uF80E";
	public static final String NEG1024="\uF80F";
	
	public static final String POS1 ="\uF821";
	public static final String POS2 ="\uF822";
	public static final String POS3 ="\uF823";
	public static final String POS4 ="\uF824";
	public static final String POS5 ="\uF825";
	public static final String POS6 ="\uF826";
	public static final String POS7 ="\uF827";
	public static final String POS8 ="\uF828";
	public static final String POS16 ="\uF829";
	public static final String POS32 ="\uF82A";
	public static final String POS64 ="\uF82B";
	public static final String POS128="\uF82C";
	public static final String POS256="\uF82D";
	public static final String POS512="\uF82E";
	public static final String POS1024="\uF82F";
	
	//TAGS
	public static final String RANK_ADMIN_TAG = "\uB001";
	public static final String RANK_MODERATOR_TAG = "";
	public static final String RANK_PLAYER_TAG = "\uB002";
	public static final String RANK_OLYMPIAN_TAG = "\uB003";
	public static final String RANK_EMPYREAN_TAG = "\uB004";
	public static final String RANK_ANGELIC_TAG = "\uB005";
	public static final String RANK_DIVINE_TAG = "\uB006";
	public static final String RANK_CELESTIAL_TAG = "\uB007";
	
	
	public static final String FACTION_LEADER_TAG = "";
	public static final String FACTION_COLEADER_TAG = "";
	public static final String FACTION_MODERATOR_TAG = "";
	
	public static final String FACTION_CHAT_TAG = "\uC001";
	public static final String ALLY_CHAT_TAG = "\uC002";
	
	//UI
	public static final String MENU_CONTAINER_54 = NEG8 +"\uF001";
	public static final String MENU_CONTAINER_27 = NEG8+"\uF002";

	public static final String CRAFTING_TABLE_OVERRIDE_CONTAINER_36 = NEG8 + "\uF004";
	
	public static final String FURNACE_OVERRIDE_CONTAINER_27 = NEG8 + "\uF003";
	
	private static final int FURNACE_PROGRESS_ARROW_OFFSET = 8;
	public static final String FURNACE_PROGRESS_ARROW_0 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD101";
	public static final String FURNACE_PROGRESS_ARROW_1 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD102";
	public static final String FURNACE_PROGRESS_ARROW_2 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD103";
	public static final String FURNACE_PROGRESS_ARROW_3 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD104";
	public static final String FURNACE_PROGRESS_ARROW_4 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD105";
	public static final String FURNACE_PROGRESS_ARROW_5 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD106";
	public static final String FURNACE_PROGRESS_ARROW_6 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD107";
	public static final String FURNACE_PROGRESS_ARROW_7 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD108";
	public static final String FURNACE_PROGRESS_ARROW_8 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD109";
	public static final String FURNACE_PROGRESS_ARROW_9 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10A";
	public static final String FURNACE_PROGRESS_ARROW_10 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10B";
	public static final String FURNACE_PROGRESS_ARROW_11 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10C";
	public static final String FURNACE_PROGRESS_ARROW_12 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10D";
	public static final String FURNACE_PROGRESS_ARROW_13 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10E";
	public static final String FURNACE_PROGRESS_ARROW_14 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD10F";
	public static final String FURNACE_PROGRESS_ARROW_15 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD111";
	public static final String FURNACE_PROGRESS_ARROW_16 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD112";
	public static final String FURNACE_PROGRESS_ARROW_17 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD113";
	public static final String FURNACE_PROGRESS_ARROW_18 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD114";
	public static final String FURNACE_PROGRESS_ARROW_19 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD115";
	public static final String FURNACE_PROGRESS_ARROW_20 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD116";
	public static final String FURNACE_PROGRESS_ARROW_21 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD117";
	public static final String FURNACE_PROGRESS_ARROW_22 = getPos(FURNACE_PROGRESS_ARROW_OFFSET) + "\uD118";
	

	private static final int FURNACE_PROGRESS_FUEL_OFFSET = 135;
	public static final String FURNACE_PROGRESS_FUEL_0 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD201";
	public static final String FURNACE_PROGRESS_FUEL_1 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD202";
	public static final String FURNACE_PROGRESS_FUEL_2 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD203";
	public static final String FURNACE_PROGRESS_FUEL_3 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD204";
	public static final String FURNACE_PROGRESS_FUEL_4 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD205";
	public static final String FURNACE_PROGRESS_FUEL_5 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD206";
	public static final String FURNACE_PROGRESS_FUEL_6 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD207";
	public static final String FURNACE_PROGRESS_FUEL_7 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD208";
	public static final String FURNACE_PROGRESS_FUEL_8 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD209";
	public static final String FURNACE_PROGRESS_FUEL_9 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20A";
	public static final String FURNACE_PROGRESS_FUEL_10 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20B";
	public static final String FURNACE_PROGRESS_FUEL_11 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20C";
	public static final String FURNACE_PROGRESS_FUEL_12 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20D";
	public static final String FURNACE_PROGRESS_FUEL_13 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20E";
	public static final String FURNACE_PROGRESS_FUEL_14 = getNeg(FURNACE_PROGRESS_FUEL_OFFSET) + "\uD20F";
	
	public static final String TEST_BUTTON = "\uF80C" + "\uF80A" + "\uD001";
	public static final String UI_PERMISSIONS_FACTIONS_BUTTON = "\uF80C" + "\uF80A" + "\uD002";
	
	public static String getNeg(int pixel) {
		String binary = new StringBuilder(Integer.toBinaryString(pixel)).reverse().toString();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(char c : binary.toCharArray()){
			if(c != '0')
			{
				sb.append(NegativeChar.getCharByWeight((int)Math.pow(2, index)).s );
			}
			index++;
		}
		
		return sb.toString();
	}
	
	public static String getPos(int pixel) {
		String binary = new StringBuilder(Integer.toBinaryString(pixel)).reverse().toString();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(char c : binary.toCharArray()){
			if(c != '0')
			{
				sb.append(PositiveChar.getCharByWeight((int)Math.pow(2, index)).s );
			}
			index++;
		}
		
		return sb.toString();
	}
	
	public static String fromName(String name) {
		try {
			Field f = CharRepo.class.getField(name);
			return(String.valueOf(f.get(null)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private enum NegativeChar{
		NEG1(1, CharRepo.NEG1),
		NEG2(2, CharRepo.NEG2),
		NEG4(4, CharRepo.NEG4),
		NEG8(8, CharRepo.NEG8),
		NEG16(16, CharRepo.NEG16),
		NEG32(32, CharRepo.NEG32),
		NEG64(64, CharRepo.NEG64),
		NEG128(128, CharRepo.NEG128),
		NEG256(256, CharRepo.NEG256),
		NEG512(512, CharRepo.NEG512),
		NEG1024(1024, CharRepo.NEG1024);
		
		private int weight;
		private String s;
		NegativeChar(int weight, String s){
			this.weight = weight;
			this.s = s;
		}
		
		static NegativeChar getCharByWeight(int weight)
		{
			for(NegativeChar c : NegativeChar.values())
				if(c.weight==weight)
					return c;
			return null;
		}
	}
	
	private enum PositiveChar{
		POS1(1, CharRepo.POS1),
		POS2(2, CharRepo.POS2),
		POS4(4, CharRepo.POS4),
		POS8(8, CharRepo.POS8),
		POS16(16, CharRepo.POS16),
		POS32(32, CharRepo.POS32),
		POS64(64, CharRepo.POS64),
		POS128(128, CharRepo.POS128),
		POS256(256, CharRepo.POS256),
		POS512(512, CharRepo.POS512),
		POS1024(1024, CharRepo.POS1024);
		
		private int weight;
		private String s;
		PositiveChar(int weight, String s){
			this.weight = weight;
			this.s = s;
		}
		
		static PositiveChar getCharByWeight(int weight)
		{
			for(PositiveChar c : PositiveChar.values())
				if(c.weight==weight)
					return c;
			return null;
		}
	}
}

























