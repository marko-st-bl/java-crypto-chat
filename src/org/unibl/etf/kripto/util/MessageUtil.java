package org.unibl.etf.kripto.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.unibl.etf.kripto.model.Cert;
import org.unibl.etf.kripto.view.Main;

public class MessageUtil {

	public static final Properties PROPS = PropertiesUtil.loadProperties();

	public static void sendMessage(String message, String receiver, String encryptionAlg, String hashAlg) {
		Cert certificate = Main.USER.getCertificate();
		if (Cert.isValid(certificate.getCertificate())) {
			if (!certificate.isRevoked()) {
				if (certificate.isValidOnDate()) {
					byte[] messageToBeSigned = message.getBytes();
					byte[] signedMessage = BouncyCastleCrypto.signData(messageToBeSigned,
							Main.USER.getCertificate().getCertificate(), Main.USER.getCertificate().getPrivateKey(),
							hashAlg);
					if (Cert.isValid(Cert.getUserCertificate(receiver))) {
						byte[] encryptedMessage = BouncyCastleCrypto.encryptData(signedMessage,
								Cert.getUserCertificate(receiver), encryptionAlg);
						String messagePath = PROPS.getProperty("inbox.path") + File.separator + receiver
								+ File.separator + new Date().getTime();
						Path path = Paths.get(messagePath);
						try {
							Files.write(path, encryptedMessage);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "Receivers identyty can not be truseted.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Your certificate is expired.", "Error!",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Your certificate is revoked.", "Error!",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Your certificate is not issued by CA.", "Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String readMessage(Path messagePath) {
		String retVal = "";
		// Path path = Paths.get(messagePath);
		try {
			byte[] encryptedMessage = Files.readAllBytes(messagePath);
			byte[] decryptedMessage = BouncyCastleCrypto.decryptData(encryptedMessage,
					Main.USER.getCertificate().getPrivateKey());
			if (BouncyCastleCrypto.verifSignData(decryptedMessage)) {
				retVal = new String(BouncyCastleCrypto.parseSignedData(decryptedMessage));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public static void sendStegMessage(String message, String path, String receiver, String encryptionAlg, String hashAlg, boolean isLast) {
		Steganography steg = new Steganography();
		String stegan = PROPS.getProperty("steg.path") + File.separator + new File(path).getName();
		byte[] messageToBeSigned = message.getBytes();
		byte[] signedMessage = BouncyCastleCrypto.signData(messageToBeSigned,
				Main.USER.getCertificate().getCertificate(), Main.USER.getCertificate().getPrivateKey(),
				hashAlg);
		byte[] encryptedMessage = BouncyCastleCrypto.encryptData(signedMessage,
				Cert.getUserCertificate(receiver), encryptionAlg);
		steg.encode(path, stegan, encryptedMessage);
		String protocol = isLast ==true ? Protocol.LAST_MESSAGE : Protocol.FIRST_MESSAGE;
		String messagePath = protocol + Protocol.SEPARATOR + Main.USER.username + Protocol.SEPARATOR + stegan;
		sendMessage(messagePath, receiver, encryptionAlg, hashAlg);
	}

}
