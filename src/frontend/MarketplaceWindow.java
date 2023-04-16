package frontend;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import frontend.CircuitData.PackedData;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

class CircuitEntry extends JPanel {
	private long userId, circuitId;
	private String description, username;

	public CircuitEntry() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel imgPanel = new JPanel();
		this.add(imgPanel);
		JPanel descPanel = new JPanel();
		this.add(descPanel);
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel();
		descPanel.add(titleLabel);
		JTextArea descText = new JTextArea();
		descPanel.add(descText);
		descText.setEditable(false);
		JLabel userName = new JLabel();
		descPanel.add(userName);
		JButton downloadButton = new JButton("USE");
		descPanel.add(downloadButton);
	}
}

public class MarketplaceWindow extends JFrame {
	public MarketplaceWindow() {
		this.setVisible(true);
		this.setSize(200, 200);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);

		JPanel panel_3 = new JPanel();
		scrollPane_1.setViewportView(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);

		JPanel panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		displayGlobalCircuits();
	}

	public static PackedData getCircuit(long circuitId) {
		Connection con = UserManager.getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
			return null;
		}
		String command = "SELECT * from savedCircuits where circuitId = " + circuitId;
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {
			while (rs.next()) {
				byte[] buf = rs.getBytes("circuitFile");
				ObjectInputStream objectIn = null;
				if (buf != null)
					objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
				try {
					return (PackedData)objectIn.readObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return (PackedData) rs.getObject("circuitFile");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "No circuit was found.");
		return null;
	}

	public void displayGlobalCircuits() {
		Connection con = UserManager.getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
			return;
		}

		String command = "SELECT * from savedCircuits where isPublic = 1";
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {
			while (rs.next()) {
				System.out.println(rs.getString("circuitId"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
