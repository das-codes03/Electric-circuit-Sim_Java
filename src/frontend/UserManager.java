package frontend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import frontend.CircuitData.PackedData;

public class UserManager {
	private static Long userId = null;
	public static long getUserId() {
		if (userId == null) {
			return -1;
		}
		return userId;
	}

	public static boolean isLoggedIn() {
		return userId != null;
	}

	public static Connection con = null;

	public static Connection getConnection() {
		if (con == null) {
			try {
				connectToDatabase();
				return con;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return con;
	}

	private static void connectToDatabase() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Class loaded");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/simDatabase", "root", "prince2003");
		System.out.println("Connection established");
	}

	public static boolean createAccount(String userName, String pwd) {
		Connection con = getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please check your internet connection");
		}
		String command = "SELECT * from userInformation where userName = '" + userName + "'";
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {
			if (rs.next()) {
				JOptionPane.showMessageDialog(null, "User already exists, try using a different username");
				return false;
			} else {
				String createAccCommand = "insert into userInformation (userName, pwd) values (?,?)";
				PreparedStatement stmt2 = con.prepareStatement(createAccCommand);
				stmt2.setString(1, userName);
				stmt2.setString(2, pwd);
				stmt2.executeUpdate();
				JOptionPane.showMessageDialog(null, "Account " + userName + " created successfully");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean login(String username, String password) {
		Connection con = getConnection();
		if (con == null) {

			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please check your internet connection");

		}

		String command = "SELECT * FROM userInformation WHERE userName = '" + username + "'";
		try {
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {

				while (rs.next()) {
					String pwd = rs.getString("pwd");
					if (pwd.equals(password)) {
						userId = rs.getLong("userId");
						JOptionPane.showMessageDialog(null, "Logged in as " + username + " successfully!");
						Driver.getDriver().mainWin.profileName.setText(username);
						return true;
					} else {
						JOptionPane.showMessageDialog(null, "Invalid password.");
						return false;
					}
				}
				JOptionPane.showMessageDialog(null, "Invalid username");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static boolean uploadCircuit(long userId, String title, String description, PackedData data,
			BufferedImage img, boolean isPublic) {
		
		Connection con = UserManager.getConnection();
		if (con == null) {
			JOptionPane.showMessageDialog(null, "Counldn't connect to server. Please try again later.");
			return false;
		}

		String command = "insert into savedCircuits(userId, isPublic, title, descr, circuitFile, circuitImage) values(?,?,?,?,?,?)";

		try (PreparedStatement stmt = con.prepareStatement(command)) {
			stmt.setLong(1, UserManager.getUserId());
			stmt.setBoolean(2, isPublic);
			stmt.setString(3, title);
			stmt.setString(4, description);
			stmt.setObject(5, data);
			Blob blob = con.createBlob();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			byte[] imageInByte = baos.toByteArray();
			blob.setBytes(1, imageInByte);
			stmt.setBlob(6, blob);
			stmt.executeUpdate();
			System.out.println("Uploaded circuit");
			return true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static void logout() {
		if(!isLoggedIn()) {
			JOptionPane.showMessageDialog(null, "No user is logged in!");
		}else {
			String usrName = getUserName(getUserId());
			userId = null;
			JOptionPane.showMessageDialog(null, "Logged out of " + usrName);
		}
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
					return (PackedData) objectIn.readObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return (PackedData) rs.getObject("circuitFile");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "No circuit was found.");
		return null;
	}
	public static String getUserName(long userId) {
		Connection con = UserManager.getConnection();
		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select userName from userInformation where userId = " + userId);) {
			if (rs.next()) {
				return rs.getString("userName");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean validatePwd(String pwd) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(pwd);
		return matcher.matches();
	}
	public static boolean validateUsername(String usr) {
		String regex =  "^[a-zA-Z0-9]+$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(usr);
		return matcher.matches();
	}
}
