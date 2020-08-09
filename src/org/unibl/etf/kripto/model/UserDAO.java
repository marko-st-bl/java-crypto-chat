package org.unibl.etf.kripto.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

import org.unibl.etf.kripto.util.PasswordUtil;
import org.unibl.etf.kripto.util.PropertiesUtil;

public class UserDAO {

	private static final Properties PROPS = PropertiesUtil.loadProperties();
	private static ArrayList<User> users = new ArrayList<>();

	public static User login(String username, String password) {
		User retVal = null;
		readUsersFromFile();
		
		for(User u : users) {
			if(u.getUsername().equals(username)) {
				String passwordHash = PasswordUtil.getHash(u.getSalt(), password);
				if(u.getPasswordHash().equals(passwordHash)) {
					retVal = u;
				}
			}
		}
		return retVal;
	}

	public static void writeUsersToFile() throws Exception {
		User admin = new User("admin", "admin");
		User marko = new User("marko", "marko");
		User jovana = new User("jovana", "jovana");
		User test = new User("test", "test");
		User date = new User("date", "date");
		User hacker = new User("hacker", "hacker");

		File users = new File(PROPS.getProperty("users.path"));
		PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(users), StandardCharsets.UTF_8)));
		pw.println(admin);
		pw.println(marko);
		pw.println(jovana);
		pw.println(test);
		pw.println(date);
		pw.println(hacker);
		pw.close();

	}

	public static void readUsersFromFile() {
		BufferedReader in = null;
		String line = "";
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(PROPS.getProperty("users.path")), StandardCharsets.UTF_8));
			while((line = in.readLine()) != null) {
				String userData[] = line.split(":");
				users.add(new User(userData[0], userData[1], userData[2]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}