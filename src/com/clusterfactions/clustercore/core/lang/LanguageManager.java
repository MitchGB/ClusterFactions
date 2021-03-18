package com.clusterfactions.clustercore.core.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import com.clusterfactions.clustercore.util.Colors;

public class LanguageManager {
	public final static Locale[] locales = {Locale.ENGLISH};

	public String getString(Locale locale, Lang lang) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("com.clusterfactions.clustercore.core.lang.langs", locale);
			return Colors.parseColors(bundle.getString(lang.name()));
		}catch(Exception e) {
			return "UNDEFINED";
		}
	}
	
}
