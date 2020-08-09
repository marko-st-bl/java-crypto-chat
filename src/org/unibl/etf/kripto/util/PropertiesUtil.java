package org.unibl.etf.kripto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class PropertiesUtil {
	
	private static final String CONFIG="conf"+File.separator+"config.properties";
	//private static final Logger LOGGER=Logger.getLogger(PropertiesUtil.class.getName());
	
	public static Properties loadProperties() {
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(CONFIG));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//LOGGER.config("Properties loaded.");
		return prop;
	}
	public static void setNotificationNumber(int id) {
		Properties prop=loadProperties();
		prop.setProperty("notification.number",new Integer(id).toString());
		try (FileOutputStream fos=new FileOutputStream(CONFIG)){
			prop.store(fos, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}

