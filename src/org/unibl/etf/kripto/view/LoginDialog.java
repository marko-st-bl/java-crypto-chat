package org.unibl.etf.kripto.view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.kripto.controller.LoginController;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	
	private LoginController  loginController;

	private final JPanel contentPanel = new JPanel();
	private JTextField textUsername;
	private JPasswordField passwordField;

	private boolean succeded = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginDialog dialog = new LoginDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginDialog() {
		this(null);
		setTitle("Login");
		setLocationRelativeTo(null);
	}

	public LoginDialog(JFrame frame) {
		super(frame, "Login", true);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		loginController = new LoginController();

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(70, 93, 87, 16);
		contentPanel.add(lblUsername);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(70, 122, 70, 16);
		contentPanel.add(lblPassword);

		textUsername = new JTextField();
		textUsername.setBounds(169, 90, 116, 22);
		contentPanel.add(textUsername);
		textUsername.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(169, 122, 116, 22);
		contentPanel.add(passwordField);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = textUsername.getText();
				String password = new String(passwordField.getPassword());
				if (username != null && password != null && !password.equals("") && !username.equals("")) {
					Main.USER = loginController.login(username, password);
					textUsername.setText("");
					passwordField.setText("");
					if (Main.USER == null) {
						JOptionPane.showMessageDialog(frame, "Failed login, try again.");
						succeded = false;
					} else {
						succeded = true;
						dispose();
					}
				}
			}
		});
		btnLogin.setBounds(169, 157, 116, 25);
		contentPanel.add(btnLogin);
	}

	public boolean isSucceded() {
		return succeded;
	}

}
