package frontend;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

public class circuitBar extends JPanel{
	public circuitBar() {
		setLayout(null);
		
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setLocation(0, 6);
		lblNewLabel.setSize(new Dimension(402, 288));
		lblNewLabel.setMinimumSize(new Dimension(512, 288));
		lblNewLabel.setMaximumSize(new Dimension(512, 288));
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 15));
		lblNewLabel_1.setBounds(424, 11, 240, 28);
		add(lblNewLabel_1);
		
		JTextPane txtrHi = new JTextPane();
		txtrHi.setText("hi");
		txtrHi.setBounds(434, 50, 293, 221);
		add(txtrHi);
	}
}
