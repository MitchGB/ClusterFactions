package com.clusterfactions.clustercore.core.lang;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
	public final static Locale[] locales = {Locale.ENGLISH};

	public String getString(Locale locale, Lang lang) {
		ResourceBundle bundle = ResourceBundle.getBundle("com.clusterfactions.clustercore.core.lang.langs", locale);
		return bundle.getString(lang.name());
	}
	
}
