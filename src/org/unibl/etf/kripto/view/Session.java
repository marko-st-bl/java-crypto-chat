package org.unibl.etf.kripto.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import org.unibl.etf.kripto.util.Encryption;
import org.unibl.etf.kripto.util.Hash;
import org.unibl.etf.kripto.util.MessageUtil;
import org.unibl.etf.kripto.util.PropertiesUtil;
import org.unibl.etf.kripto.util.Protocol;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Properties;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Session extends JFrame {

	private static final Properties PROPS = PropertiesUtil.loadProperties();

	private JPanel contentPane;
	private JTextField textFieldMessage;
	private JTextArea textAreaMessages;
	private JCheckBox chckbxLastMessage;
	private JFileChooser imageChooser;
	private JButton btnSend;

	public String user;
	public boolean isActive;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Session frame = new Session();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Session() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textAreaMessages = new JTextArea();
		textAreaMessages.setEditable(false);
		textAreaMessages.setBounds(12, 13, 758, 400);
		contentPane.add(textAreaMessages);

		textFieldMessage = new JTextField();
		textFieldMessage.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textFieldMessage.setBounds(12, 426, 758, 53);
		contentPane.add(textFieldMessage);
		textFieldMessage.setColumns(10);

		imageChooser = new JFileChooser();
		imageChooser.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "png", "bmp"));
		imageChooser.setCurrentDirectory(new File(PROPS.getProperty("image.path")));

		JLabel lblEncryption = new JLabel("Encryption:");
		lblEncryption.setBounds(12, 492, 74, 16);
		contentPane.add(lblEncryption);

		JComboBox<String> comboBoxEncryption = new JComboBox<>();
		comboBoxEncryption.setModel(new DefaultComboBoxModel(Encryption.values()));
		comboBoxEncryption.setBounds(12, 518, 86, 22);
		contentPane.add(comboBoxEncryption);

		JLabel lblHash = new JLabel("Hash:");
		lblHash.setBounds(134, 492, 56, 16);
		contentPane.add(lblHash);

		JComboBox<String> comboBoxHash = new JComboBox<>();
		comboBoxHash.setModel(new DefaultComboBoxModel(Hash.values()));
		comboBoxHash.setBounds(134, 518, 86, 22);
		contentPane.add(comboBoxHash);

		btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String encryptionAlg = comboBoxEncryption.getSelectedItem().toString();
				String hashAlg = comboBoxHash.getSelectedItem().toString();
				String textMessage = textFieldMessage.getText();
				if (isActive == true) {
					if (textAreaMessages.getText().equals("") || chckbxLastMessage.isSelected()) {
						if (imageChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							String path = imageChooser.getSelectedFile().getAbsolutePath().toString();
							String message = Main.USER.username + Protocol.SEPARATOR + textFieldMessage.getText();
							boolean isLast = chckbxLastMessage.isSelected();
							MessageUtil.sendStegMessage(message, path, user, encryptionAlg, hashAlg, isLast);
							if(isLast) {
								isActive = false;
							}
						}
					} else {
						String message = Protocol.MESSAGE + Protocol.SEPARATOR + Main.USER.username + Protocol.SEPARATOR
								+ textMessage;
						MessageUtil.sendMessage(message, user, encryptionAlg, hashAlg);
					}
					textAreaMessages.append(Main.USER.username + ": " + textMessage + "\n");
					textFieldMessage.setText("");
				} else {
					JOptionPane.showMessageDialog(null, "User left session.");
					btnSend.setEnabled(false);
				}
			}
		});
		btnSend.setBounds(652, 492, 118, 50);
		contentPane.add(btnSend);

		chckbxLastMessage = new JCheckBox("Last message");
		chckbxLastMessage.setBounds(249, 517, 113, 25);
		contentPane.add(chckbxLastMessage);
	}

	public Session(String user) {
		this();
		this.user = user;
		this.isActive = true;
		this.setTitle("Session with " + user);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String message = Protocol.END_SESSION + Protocol.SEPARATOR + Main.USER.username;
				MessageUtil.sendMessage(message, user, "AES128", "SHA256");
				setUser("");
				setActive(false);
			}
		});
	}

	public JTextArea getTextArea() {
		return textAreaMessages;
	}

	public JTextArea getTextAreaMessages() {
		return textAreaMessages;
	}

	public void setTextAreaMessages(JTextArea textAreaMessages) {
		this.textAreaMessages = textAreaMessages;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void disableSendButton() {
		btnSend.setEnabled(false);
	}

}
