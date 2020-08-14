package org.unibl.etf.kripto.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.kripto.controller.MessageController;
import org.unibl.etf.kripto.controller.UserController;
import org.unibl.etf.kripto.model.User;
import org.unibl.etf.kripto.util.PropertiesUtil;
import org.unibl.etf.kripto.util.Protocol;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import org.unibl.etf.kripto.util.Encryption;
import org.unibl.etf.kripto.util.Hash;
import org.unibl.etf.kripto.util.MessageUtil;

@SuppressWarnings("serial")
public class Main extends JFrame {
	
	private static final Properties PROPS = PropertiesUtil.loadProperties();
	
	public static User USER;


	public static boolean isRunning = true;

	private UserController userController;
	private MessageController messageController;
	private JPanel contentPane;
	
	public static Session SESSION = new Session();
	
	JComboBox<String> comboBoxEncryption;
	JComboBox<String> comboBoxHash;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.addWindowListener(new java.awt.event.WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							UserController.isRunning = false;
							MessageController.isRunning = false;
							System.out.println("Exiting app...");
							String userInbox = PROPS.getProperty("inbox.path") + File.separator + USER.getUsername();
							Path userInboxPath = Paths.get(userInbox);
							if(Files.exists(userInboxPath)) {
								try {
									Files.walk(userInboxPath)
									  .sorted(Comparator.reverseOrder())
									  .map(Path::toFile)
									  .forEach(File::delete);
								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
							System.exit(0);
						}
					});
					LoginDialog login =new LoginDialog(frame);
					login.setVisible(true);
					if(login.isSucceded()) {
						frame.setVisible(true);
						frame.messageController = new MessageController();
						frame.messageController.start();
						login.dispose();
					}else {
						System.exit(0);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() throws IOException{
		setTitle("SecureChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		userController = new UserController();
		userController.start();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 191, 527);
		contentPane.add(scrollPane);
		
		JList<String> onlineUsersList = new JList<>(UserController.listModel);
		scrollPane.setViewportView(onlineUsersList);
		
		JButton btnNewButton = new JButton("REQUEST SESSION");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String receiver = onlineUsersList.getSelectedValue();
				if(null != receiver) {
					String message = Protocol.SESSION_START + Protocol.SEPARATOR + USER.username;
					MessageUtil.sendMessage(message, receiver, comboBoxEncryption.getSelectedItem().toString(), comboBoxHash.getSelectedItem().toString());
				} else {
					JOptionPane.showMessageDialog(null, "You must select user from list.");
				}
			}
		});
		btnNewButton.setBounds(587, 481, 183, 59);
		contentPane.add(btnNewButton);
		
		JLabel lblEncryption = new JLabel("Encryption:");
		lblEncryption.setBounds(215, 481, 73, 16);
		contentPane.add(lblEncryption);
		
		JLabel lblHash = new JLabel("Hash:");
		lblHash.setBounds(417, 481, 56, 16);
		contentPane.add(lblHash);
		
		comboBoxEncryption = new JComboBox<>();
		comboBoxEncryption.setModel(new DefaultComboBoxModel(Encryption.values()));
		comboBoxEncryption.setBounds(215, 510, 73, 22);
		contentPane.add(comboBoxEncryption);
		
		comboBoxHash = new JComboBox<>();
		comboBoxHash.setModel(new DefaultComboBoxModel(Hash.values()));
		comboBoxHash.setBounds(417, 510, 73, 22);
		contentPane.add(comboBoxHash);
	}
}
