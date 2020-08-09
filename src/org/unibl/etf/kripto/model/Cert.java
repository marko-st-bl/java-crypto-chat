package org.unibl.etf.kripto.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.unibl.etf.kripto.util.PropertiesUtil;


public class Cert {
	
	public static final Properties PROPS = PropertiesUtil.loadProperties();
	
	public static X509Certificate rootCA;
	public static X509CRL CRL;
	public static CertificateFactory certFactory;
	
	private X509Certificate certificate;
	private PrivateKey privateKey;

	static {
		Security.addProvider(new BouncyCastleProvider());
		try {
			certFactory= CertificateFactory.getInstance("X.509", "BC");
			rootCA = (X509Certificate) certFactory.generateCertificate(new FileInputStream(PROPS.getProperty("ca.cert")));
			CRL = (X509CRL) certFactory.generateCRL(new FileInputStream(PROPS.getProperty("crl.path")));
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CRLException e) {
			e.printStackTrace();
		}
	}

	public Cert() {
		super();
	}
	
	public Cert(String username) {
		try {
			String certPath = PROPS.getProperty("cer.path") + File.separator + username + PROPS.getProperty("cer.extension");
			this.certificate = (X509Certificate) certFactory.generateCertificate(new FileInputStream(certPath));
			
			char[] keystorePassword = "password".toCharArray();
			char[] keyPassword = "password".toCharArray();
			
			String pkPath = PROPS.getProperty("pk.path") + File.separator + username + PROPS.getProperty("pk.extension");
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(new FileInputStream(pkPath), keystorePassword);
			this.privateKey = (PrivateKey) keystore.getKey(username, keyPassword);

		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public static X509Certificate getUserCertificate(String name) {
		X509Certificate certificate = null;
		String path = PROPS.getProperty("cer.path") + File.separator + name + ".cer";
		try {
			certificate = (X509Certificate) certFactory.generateCertificate(new FileInputStream(path));
		} catch(CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return certificate;
		
	}
	
	public static boolean isValid(X509Certificate certificate) {
		boolean retVal = true;
		try {
			certificate.verify(rootCA.getPublicKey());
		} catch(Exception e) {
			retVal = false;
		}
		return retVal;
	}
	
	public boolean isRevoked() {
        X509CRLEntry revokedCertificate = CRL.getRevokedCertificate(certificate.getSerialNumber());
        return (revokedCertificate != null);
    }
	
	public boolean isValidOnDate() {
        try {
            certificate.checkValidity();
            return true;
        } catch (CertificateExpiredException e) {
            //System.out.println("Istekao certifikat");
            return false;
        } catch (CertificateNotYetValidException e) {
            //System.out.println("Certifikat nije jos validan");
            return false;
        }
	}
	
}
