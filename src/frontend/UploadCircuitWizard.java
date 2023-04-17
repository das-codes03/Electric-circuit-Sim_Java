package frontend;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
	private Dimension imgSize = new Dimension(1024,576);
	private JTextField textField;
	private BufferedImage img = null;
	public UploadCircuitWizard() {
		this.setVisible(true);
		this.setSize(500,300);
		this.setTitle("Upload circuit");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		var canvas = Driver.getDriver().mainWin.renderCanvas;
				
		img =canvas.getSnapshot(canvas.getZoomToFit(imgSize.width,imgSize.height), imgSize.width, imgSize.height);
		JScrollPane scr = new JScrollPane();
		getContentPane().add(scr);
		JPanel mainPane = new JPanel();
		scr.setViewportView(mainPane);
		mainPane.setLayout(new BorderLayout(0, 0));
		
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
				UserManager.uploadCircuit(UserManager.getUserId(), textField.getText(), textArea.getText(), Driver.getDriver().getPackedData(), img, chckbxNewCheckBox.isSelected());
			}
		});
		panel_1.add(btnNewButton);
		
		JLabel lblNewLabel_3 = new JLabel(new ImageIcon(img.getScaledInstance(imgSize.width/2, imgSize.height/2, Image.SCALE_SMOOTH)));
		mainPane.add(lblNewLabel_3, BorderLayout.WEST);
		
	}
	
	
}
