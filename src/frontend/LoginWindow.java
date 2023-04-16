package frontend;

import javax.swing.JInternalFrame;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;

public class LoginWindow extends JInternalFrame{
	private JTextField textField;
	private JTextField textField_1;
	public LoginWindow() {
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(91dlu;default)"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel = new JLabel("LOGIN");
		getContentPane().add(lblNewLabel, "4, 2, center, default");
		
		JLabel lblNewLabel_1 = new JLabel("Username:");
		getContentPane().add(lblNewLabel_1, "2, 4, right, default");
		
		textField = new JTextField();
		getContentPane().add(textField, "4, 4, fill, default");
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Password:");
		getContentPane().add(lblNewLabel_2, "2, 6, right, default");
		
		textField_1 = new JTextField();
		getContentPane().add(textField_1, "4, 6, fill, default");
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Login");
		getContentPane().add(btnNewButton, "4, 8");
		
		JButton btnNewButton_2 = new JButton("Create new account");
		getContentPane().add(btnNewButton_2, "4, 10");
	}
	public static void login(String username, String password) {
		
	}
}
