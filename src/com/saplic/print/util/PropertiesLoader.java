package com.saplic.print.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
	
	public static Properties appProperties = new Properties();
	 //public static final String CONFIG_FILE = "config.properties";
	 public static final String CONFIG_FILE = "config/config.properties";
	
	public static void loadProperties() {
		try {
			appProperties.load(new FileInputStream(CONFIG_FILE));
			//appProperties.load(new FileInputStream(CONFIG_FILE));
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

}
