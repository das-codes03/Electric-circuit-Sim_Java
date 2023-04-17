package frontend;

import javax.swing.JInternalFrame;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.Font;

public class LoginWindow extends JFrame {
	private JTextField textField;
	private JPasswordField textField_1;
	private JTextField textField_2;
	private JPasswordField textField_3;
	private JPasswordField textField_4;

	public LoginWindow() {
		this.setSize(300, 250);
		this.setResizable(false);
		this.setTitle("USER");
		this.setVisible(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Login", null, panel, null);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Username: ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 55, 84, 14);
		panel.add(lblNewLabel);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(10, 90, 84, 14);
		panel.add(lblPassword);

		textField = new JTextField();
		textField.setBounds(94, 47, 179, 30);
		panel.add(textField);
		textField.setColumns(10);

		textField_1 = new JPasswordField();
		textField_1.setColumns(10);
		textField_1.setBounds(94, 82, 179, 30);
		panel.add(textField_1);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(UserManager.login(textField.getText(), String.copyValueOf(textField_1.getPassword()))) {
					dispose();
				}
			}
		});
		btnNewButton.setBounds(141, 125, 89, 23);
		panel.add(btnNewButton);

		JLabel lblNewLabel_2 = new JLabel("WELCOME!");
		lblNewLabel_2.setFont(new Font("Verdana", Font.BOLD, 14));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 11, 263, 25);
		panel.add(lblNewLabel_2);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Create new acccount", null, panel_1, null);
		panel_1.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Enter password: ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(13, 66, 99, 14);
		panel_1.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Enter username: ");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1_1.setBounds(13, 25, 99, 14);
		panel_1.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("Confirm password:");
		lblNewLabel_1_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1_2.setBounds(13, 105, 99, 14);
		panel_1.add(lblNewLabel_1_2);

		textField_2 = new JTextField();
		textField_2.setBounds(119, 17, 151, 30);
		panel_1.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JPasswordField();
		textField_3.setColumns(10);
		textField_3.setBounds(119, 58, 151, 30);
		panel_1.add(textField_3);

		textField_4 = new JPasswordField();
		textField_4.setColumns(10);
		textField_4.setBounds(119, 97, 151, 30);
		panel_1.add(textField_4);

		JButton btnNewButton_1 = new JButton("Create account");
		btnNewButton_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(UserManager.createAccount(textField_2.getText(),String.copyValueOf(textField_4.getPassword()))) {
					UserManager.login(textField_2.getText(),String.copyValueOf(textField_4.getPassword()));
					dispose();
				}
			}
		});
		btnNewButton_1.setBounds(129, 138, 124, 23);
		panel_1.add(btnNewButton_1);
	}

	
}
