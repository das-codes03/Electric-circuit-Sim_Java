package frontend;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import frontend.CircuitData.PackedData;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class UploadCircuitWizard extends JFrame{
	private JTextField textField;
	public UploadCircuitWizard() {
		this.setVisible(true);
		this.setSize(500,300);
		this.setTitle("Upload circuit");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		JScrollPane scr = new JScrollPane();
		getContentPane().add(scr);
		JPanel mainPane = new JPanel();
		scr.setViewportView(mainPane);
		mainPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Upload circuit");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPane.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		mainPane.add(panel);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(300, 30));
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel_1 = new JLabel("Name: ");
		panel_2.add(lblNewLabel_1);
		
		textField = new JTextField();
		panel_2.add(textField);
		textField.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_2 = new JLabel("Description");
		panel_3.add(lblNewLabel_2);
		
		JTextArea textArea = new JTextArea();
		textArea.setRows(6);
		panel_3.add(textArea);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Is public?");
		panel_3.add(chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("Upload");
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				uploadCircuit(UserManager.getUserId(), textField.getText(), textArea.getText(), Driver.getDriver().getPackedData(), null, true);
			}
		});
		panel_1.add(btnNewButton);
		
	}
	
	public static void uploadCircuit(long userId, String title, String description, PackedData data, Image img, boolean isPublic) {
	
			Connection con = UserManager.getConnection();
			if(con == null) {
				JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
				return;
			}
			
			String command = "insert into savedCircuits(userId, isPublic, title, descr, circuitFile, dateOfUpload) values(?,?,?,?,?,?)";
			
			try(PreparedStatement stmt = con.prepareStatement(command)){
				stmt.setLong(1, UserManager.getUserId());
				stmt.setBoolean(2,isPublic);
				stmt.setString(3, title);
				stmt.setString(4, description);
				stmt.setObject(5, data);
				stmt.setDate(6, Date.valueOf(LocalDate.now()));		//TODO: use date from internet not
				stmt.executeUpdate();
				System.out.println("Uploaded circuit");
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
