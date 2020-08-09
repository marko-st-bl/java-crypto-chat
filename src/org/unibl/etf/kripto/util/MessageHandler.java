package org.unibl.etf.kripto.util;

import javax.swing.JOptionPane;

import org.unibl.etf.kripto.view.Main;
import org.unibl.etf.kripto.view.Session;

public class MessageHandler {

	public static void handleMessage(String message) {
		if (message.startsWith(Protocol.SESSION_START)) {
			String response;
			String messageFrom = message.split(Protocol.SEPARATOR)[1];
			if (Main.SESSION.isActive() == true) {
				response = Protocol.USER_BUSY + Protocol.SEPARATOR + Main.USER.username;
			} else {
				String promt = "User: " + messageFrom + " wants to start session. Accept?";
				int dialogResult = JOptionPane.showConfirmDialog(null, promt, "Confirm", JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					response = Protocol.SESSION_ACCEPTED + Protocol.SEPARATOR + Main.USER.getUsername();
					Main.SESSION = new Session(messageFrom);
					Main.SESSION.setVisible(true);
				} else {
					response = Protocol.SESSION_DECLINED + Protocol.SEPARATOR + Main.USER.getUsername();
				}
			}
			MessageUtil.sendMessage(response, messageFrom, "AES128", "SHA256");
			
		} else if (message.startsWith(Protocol.SESSION_DECLINED)) {
			String user = message.split(Protocol.SEPARATOR)[1];
			JOptionPane.showMessageDialog(null, "User: " + user + " declined session request.");
		} else if (message.startsWith(Protocol.SESSION_ACCEPTED)) {
			String user = message.split(Protocol.SEPARATOR)[1];
			Main.SESSION = new Session(user);
			Main.SESSION.setVisible(true);
		} else if (message.startsWith(Protocol.MESSAGE)) {
			String[] params = message.split(Protocol.SEPARATOR);
			String messageLine = params[1] + ": " + params[2] + "\n";
			Main.SESSION.getTextArea().append(messageLine);
		} else if (message.startsWith(Protocol.USER_BUSY)) {
			JOptionPane.showMessageDialog(null, "User " + message.split(Protocol.SEPARATOR)[1] + " is busy. Try again later.");
		} else if(message.startsWith(Protocol.END_SESSION)) {
			if(null != Main.SESSION && Main.SESSION.getUser().equals(message.split(Protocol.SEPARATOR)[1])) {
				Main.SESSION.setActive(false);
				JOptionPane.showMessageDialog(null, "User: " + message.split(Protocol.SEPARATOR)[1] + " left session.");
			} 
		} else if(message.startsWith(Protocol.FIRST_MESSAGE)) {
			Steganography steg = new Steganography();
			String[] params = message.split(Protocol.SEPARATOR);
			byte[] rawMessage = steg.decode(params[2]);
			byte[] decryptedMessage = BouncyCastleCrypto.decryptData(rawMessage, Main.USER.getCertificate().getPrivateKey());
			if(BouncyCastleCrypto.verifSignData(decryptedMessage)) {
				String parsedMessage = new String(BouncyCastleCrypto.parseSignedData(decryptedMessage));
				String[] messageParams = parsedMessage.split(Protocol.SEPARATOR);
				String messageLine = messageParams[0] + ": " + messageParams[1] + "\n";
				Main.SESSION.getTextArea().append(messageLine);
			}
		} else if(message.startsWith(Protocol.LAST_MESSAGE)) {
			Steganography steg = new Steganography();
			String[] params = message.split(Protocol.SEPARATOR);
			byte[] rawMessage = steg.decode(params[2]);
			byte[] decryptedMessage = BouncyCastleCrypto.decryptData(rawMessage, Main.USER.getCertificate().getPrivateKey());
			if(BouncyCastleCrypto.verifSignData(decryptedMessage)) {
				String parsedMessage = new String(BouncyCastleCrypto.parseSignedData(decryptedMessage));
				String[] messageParams = parsedMessage.split(Protocol.SEPARATOR);
				String messageLine = messageParams[0] + ": " + messageParams[1] + "\n";
				Main.SESSION.getTextArea().append(messageLine);
				Main.SESSION.isActive = false;
			}
		} else {
			JOptionPane.showMessageDialog(null, "Protcol error.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {

	}

}
