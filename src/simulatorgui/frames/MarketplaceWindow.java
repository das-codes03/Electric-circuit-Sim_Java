package simulatorgui.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.formdev.flatlaf.ui.FlatLineBorder;

import simulatorgui.Driver;
import simulatorgui.UserManager;

class CircuitEntry extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328302189466555797L;

	public CircuitEntry(String title, String desc, long circuitId, String username, BufferedImage img,
			MarketplaceWindow win) {
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.setBackground(new Color(15, 15, 15));
		setLayout(null);
		this.setMinimumSize(new Dimension(800, 200));
		this.setMaximumSize(new Dimension(800, 200));
		this.setSize(new Dimension(800, 200));
		this.setPreferredSize(new Dimension(800, 200));

		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		if (img != null) {
			lblNewLabel.setIcon(
					new ImageIcon(img.getScaledInstance(img.getWidth() / 4, img.getHeight() / 4, Image.SCALE_SMOOTH)));
		} else {
			lblNewLabel.setText("NO IMAGE LOADED");
		}
		lblNewLabel.setLocation(20, 20);
		lblNewLabel.setSize(new Dimension(512 / 2, 288 / 2));
		lblNewLabel.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Color.red));
		add(lblNewLabel);

		JLabel userlbl = new JLabel("by: " + username);
		userlbl.setBounds(650, 40, 100, 20);
		add(userlbl);

		JLabel lblNewLabel_1 = new JLabel(title);
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 15));
		lblNewLabel_1.setBounds(424 - 100, 11, 240, 28);

		add(lblNewLabel_1);

		JTextPane textArea = new JTextPane();
		textArea.setText(desc);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(getBackground());
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setBorder(null);
//		textArea.setBackground(new Color(0, 0, 0));
		scroll.setBounds(424 - 100, 50, 293, 120);
		add(scroll);
		scroll.setWheelScrollingEnabled(false);
		JButton btn = new JButton("Import");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.getDriver().createFromData(UserManager.getCircuit(circuitId));
				win.dispose();
			}
		});
		btn.setLocation(650, 80);
		btn.setSize(100, 30);
		add(btn);
	}
}

public class MarketplaceWindow extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2863067769783068101L;
	JPanel panel_2;
	JPanel panel_4;

	public MarketplaceWindow() {
		this.setTitle("Circuits inventory");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		JTabbedPane tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);
		JPanel globalCircuits = new JPanel();
		tabbedPane.addTab("Global circuits", globalCircuits);
		

		globalCircuits.setLayout(new BoxLayout(globalCircuits, BoxLayout.X_AXIS));

		JPanel panel_1 = new JPanel();

		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		globalCircuits.add(panel_1);
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		
		panel_2 = new JPanel();
		
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JPanel personalcircuits = new JPanel();
		tabbedPane.addTab("My circuits", personalcircuits);

		personalcircuits.setLayout(new BoxLayout(personalcircuits, BoxLayout.X_AXIS));

		JPanel panel_3 = new JPanel();

		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		personalcircuits.add(panel_3);
		JScrollPane scrollPane2 = new JScrollPane();
		panel_3.add(scrollPane2);

		panel_4 = new JPanel();
		scrollPane2.setViewportView(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		displayPersonalCircuits();
		displayGlobalCircuits();
		panel_2.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pack();
		this.setResizable(false);
		
		this.setSize(800, 800);
		this.setVisible(true);
	}

	public void displayPersonalCircuits() {
		if(!UserManager.isLoggedIn()) {
			panel_4.add(new JLabel("Login to you account to view circuits."));
			return;
		}
		Connection con = UserManager.getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
			return;
		}

		String command = "SELECT * from savedCircuits where userId = " + UserManager.getUserId();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {
			boolean flag = false;
			
			while (rs.next()) {
				flag = true;
				String title = rs.getString("title");
				String desc = rs.getString("descr");
				long circuitId = rs.getLong("circuitId");
				String userName = UserManager.getUserName(rs.getLong("userId"));

				Blob is = rs.getBlob("circuitImage");
				BufferedImage image = null;
				if (is != null)
					image = ImageIO.read(is.getBinaryStream());

				CircuitEntry ent = new CircuitEntry(title, desc, circuitId, userName, image, this);
				panel_4.add(ent);

			}
			if(!flag) {
				panel_4.add(new JLabel("You haven't saved any circuits online!"));
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public void displayGlobalCircuits() {
		Connection con = UserManager.getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
			return;
		}

		String command = "SELECT * from savedCircuits where isPublic = 1";
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {
			boolean flag = false;
			while (rs.next()) {
				flag = true;
				String title = rs.getString("title");
				String desc = rs.getString("descr");
				long circuitId = rs.getLong("circuitId");
				String userName = UserManager.getUserName(rs.getLong("userId"));

				Blob is = rs.getBlob("circuitImage");
				BufferedImage image = null;
				if (is != null)
					image = ImageIO.read(is.getBinaryStream());

				CircuitEntry ent = new CircuitEntry(title, desc, circuitId, userName, image, this);
				panel_2.add(ent);

			}
			if(!flag) {
				panel_2.add(new JLabel("No circuits found!"));
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
}
