package com.clusterfactions.clustercore.persistence.serialization;

import java.util.ArrayList;
import java.util.List;

//Base64 Encoder
public class ByteListSerializer extends VariableSerializer<byte[][]>{
	@Override
	public String serialize(byte[][] obj) {
		if(!(obj instanceof byte[][])) return "";
		
		byte[][] byteArray = obj;
		String[][] strArray = new String[byteArray.length][byteArray[0].length];
		int xIndex = 0;
		for(byte[] bt : byteArray) {

			int zIndex = 0;
			for(byte b : bt)
			{
				strArray[xIndex][zIndex] = b + "";
				zIndex++;
			}
			xIndex++;
		}
		//1,2,3,4:1,2,3,4:1,2,3,4:1,2,3,4
		StringBuilder builder = new StringBuilder();
		for(String[] st : strArray)
		{
			for(String s : st)
			{
				
				builder.append(s);
				builder.append(",");
			}
			builder.append(":");
		}
		
		return builder.toString();
		
	}

	@Override
	public byte[][] deserialize(String str) {
		List<List<Byte>> byteList = new ArrayList<>();
		String[] f = str.split(":");
		for(String s : f)
		{
			List<Byte> bL = new ArrayList<>();
			String[] t = s.split(",");
			for(String fo : t)
			{
				byte b = Byte.valueOf(fo);
				bL.add(b);
			}
			byteList.add(bL);
		}
		byte[][] ret = new byte[byteList.size()][byteList.get(0).size()];
		for(int i = 0; i < byteList.size(); i++) {
			for(int j = 0; j < byteList.get(0).size(); j++) {
				ret[i][j] = byteList.get(i).get(j);
			}
		}
		
		return ret;
	}
}






























