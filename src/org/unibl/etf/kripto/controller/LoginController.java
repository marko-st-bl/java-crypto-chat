package org.unibl.etf.kripto.controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.unibl.etf.kripto.model.User;
import org.unibl.etf.kripto.model.UserDAO;
import org.unibl.etf.kripto.util.PropertiesUtil;

public class LoginController {
	
	public static Properties PROPS = PropertiesUtil.loadProperties();
	public final static Object obj = new Object();

	public LoginController() {
		super();
	}

	public User login(String username, String password) {
		User retVal = null;
		User user = UserDAO.login(username, password);
		if (user != null) {
			user.setCertificate();
			retVal = user;
			String pathString = PROPS.getProperty("inbox.path") + File.separator + user.getUsername();
			Path path = Paths.get(pathString);
			try {
				if(!Files.exists(path)) {
					Files.createDirectory(path);
					synchronized(obj) {
							obj.wait();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UserController.listModel.removeElement(user.getUsername());
		}
		return retVal;
	}

}
