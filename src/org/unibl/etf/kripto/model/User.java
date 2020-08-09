package org.unibl.etf.kripto.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.util.encoders.Hex;

public class User {
	
	public String username;
	public String salt;
	public String passwordHash;
	
	private Cert certificate;
	
	public User() {
		super();
	}
	/*
	public User(String username, String salt, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		super();
		this.username = username;
		this.salt = salt.getBytes(StandardCharsets.UTF_8);
		this.passwordHash = getHash(password);
	}
	*/
	
	public User(String username, String password) throws NoSuchAlgorithmException {
		super();
		this.username = username;
		SecureRandom random = new SecureRandom();
        byte[] saltBytes = random.generateSeed(64);
        this.salt = new String(Hex.encode(saltBytes));
        this.passwordHash = getHash(password);
	}
	
	public User(String username, String salt, String passwordHash) {
		super();
		this.username = username;
		this.salt = salt;
		this.passwordHash = passwordHash;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getSalt() {
		return this.salt;
	}
	
	public String getPasswordHash() {
		return this.passwordHash;
	}
	
	public void setCertificate() {
		this.certificate = new Cert(username);
	}
	
	public Cert getCertificate() {
		return this.certificate;
	}
	
	private String getHash(String password) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(salt.getBytes(StandardCharsets.UTF_8));
		return new String(Hex.encode(digest.digest(password.getBytes(StandardCharsets.UTF_8))));
	}
	
	@Override
	public String toString() {
		return this.username+":"+ this.salt+ ":" + this.passwordHash;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException  {
		User admin = new User("admin", "admin");
			System.out.println(admin);
	}

	
	
	

}
