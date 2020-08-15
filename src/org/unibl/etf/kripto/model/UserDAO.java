package org.unibl.etf.kripto.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.unibl.etf.kripto.util.BouncyCastleCrypto;
import org.unibl.etf.kripto.util.Hash;
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
	
	/*
	 * This function is not used by app, its meant to crate file with users.
	 * In order to use to funtion you must provide rootCA private key and cert
	 * in private and certs directories.
	 */
	public static void writeUsersToFile() throws Exception {
		User admin = new User("admin", "admin");
		User marko = new User("marko", "marko");
		User jovana = new User("jovana", "jovana");
		User test = new User("test", "test");
		User date = new User("date", "date");
		User hacker = new User("hacker", "hacker");
		
		String output="";
		output += admin + "\n";
		output += marko + "\n";
		output += jovana + "\n";
		output += test + "\n";
		output += date + "\n";
		output += hacker + "\n";

		String key = PROPS.getProperty("users.pass");
		Cert root = new Cert("rootCA");
		byte [] signed = BouncyCastleCrypto.signData(output.getBytes(), root.getCertificate(), 
				root.getPrivateKey(), Hash.SHA256.toString());
		byte [] enc = BouncyCastleCrypto.aesecbEncrypt(BouncyCastleCrypto.defineKeyForAES(key.getBytes()), signed);
		Path path=Paths.get(PROPS.getProperty("users.path"));
		Files.write(path, enc);

	}

	public static void readUsersFromFile() {
		Security.addProvider(new BouncyCastleProvider());
		Path path=Paths.get(PROPS.getProperty("users.path"));
		String key = PROPS.getProperty("users.pass");
		try {
			byte[] enc = Files.readAllBytes(path);
			byte[] dec = BouncyCastleCrypto.aesecbDecryption(BouncyCastleCrypto.defineKeyForAES(key.getBytes()), enc);
			if(BouncyCastleCrypto.verifSignData(dec)) {
				String usersData = new String(BouncyCastleCrypto.parseSignedData(dec));
				String[] lines = usersData.split("\n");
				for(int i=0; i< lines.length; i++) {
					String[] line=lines[i].split(":");
					users.add(new User(line[0], line[1], line[2]));
				}
			} else {
				JOptionPane.showMessageDialog(null, "Unauthorized change of users file.", "Invalid file!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
	}

}