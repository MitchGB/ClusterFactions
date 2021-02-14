package com.clusterfactions.clustercore.util;

public class NumberUtil {
	public static boolean canParse(String s)
	{
		try{
		    Integer.valueOf(s);
		} catch(NumberFormatException e) {
		    return false;
		}
		return true;
	}
	
	public static int random(int min, int max)
	{
		return (int) ((Math.random() * ((max - min) + 1)) + min);
	}
	
	public static float random(float min, float max)
	{
		return (float) ((Math.random() * ((max - min) + 1)) + min);
	}
	
	public static float clamp(float input, float rangeMin, float rangeMax)
	{
		if(input > rangeMax)
			return rangeMax;
		if(input < rangeMin)
			return rangeMin;
		return input;
	}
	
	public static double clamp(double input, double rangeMin, double rangeMax)
	{
		if(input > rangeMax)
			return rangeMax;
		if(input < rangeMin)
			return rangeMin;
		return input;
	}
	
	public static int clamp(int input, int rangeMin, int rangeMax)
	{
		if(input > rangeMax)
			return rangeMax;
		if(input < rangeMin)
			return rangeMin;
		return input;
	}
	
	public static int clampMin(int input, int rangeMin) {
		return clamp(input, rangeMin, input);
	}

	public static float clampMin(float input, float rangeMin) {
		return clamp(input, rangeMin, input);
	}
	
	public static double clampMax(double input, double rangeMax) {
		return clamp(input, input, rangeMax);
	}
	
	public static float clampRadius(float input) {
		while(input > 360)
			input -= 360;
		while(input < 0)
			input += 360;
		
		return input;
	}
	
	public static double log(int x, int base) {
		return Math.log(x) / Math.log(base);
	}

	public static int roundDown(double number, double place) {
		return (int) (Math.floor(number / place) * place);
	}

}
