package simulatorgui.frames;

import java.awt.Dialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import simulatorgui.Driver;
import simulatorgui.UserManager;

public class UploadCircuitWizard extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4772063480293858510L;
	private JTextField textField;
	public UploadCircuitWizard(final BufferedImage img) {
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle("Upload circuit");
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon(img.getScaledInstance(img.getWidth()/2, img.getHeight()/2, Image.SCALE_SMOOTH)));
		lblNewLabel.setBounds(10, 11, img.getWidth()/2, img.getHeight()/2);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Title");
		lblNewLabel_1.setBounds(392+150, 21, 48, 14);
		getContentPane().add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(392+150, 36, 208, 30);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Description");
		lblNewLabel_2.setBounds(392+150, 77, 92, 14);
		getContentPane().add(lblNewLabel_2);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(392+150, 92, 208, 228);
		getContentPane().add(textPane);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Is public?");
		chckbxNewCheckBox.setBounds(402+150, 327, 99, 23);
		getContentPane().add(chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("UPLOAD!");
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UserManager.uploadCircuit(UserManager.getUserId(), textField.getText(), textPane.getText(), Driver.getDriver().getPackedData(), img, chckbxNewCheckBox.isSelected());
					JOptionPane.showMessageDialog(null, "Circuit was uploaded!");
					dispose();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		btnNewButton.setBounds(511+150, 327, 89, 23);
		getContentPane().add(btnNewButton);
		
		this.setSize(622+150,401);
		this.setResizable(false);
		this.setVisible(true);
	}
}
