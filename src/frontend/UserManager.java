package frontend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class UserManager {

	private static Long userId = null;
	public static long getUserId() {
		if(userId == null) {
			return -1;
		}
		return userId;
	}
	public static Connection con = null;

	public static Connection getConnection() {
		if(con == null) {
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

	public static boolean login(String username, String password) {
		if (con == null) {
			try {
				connectToDatabase();
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Counldn't connect to server. Please check your internet connection");
			}
		}

		String command = "SELECT * FROM userInformation WHERE userName = '" + username + "'";
		try {
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(command)) {

				while (rs.next()) {
					String pwd = rs.getString("pwd");
					if (pwd.equals(password)) {
						userId = rs.getLong("userId");
						JOptionPane.showMessageDialog(null, "Logged in as " + username + " successfully!");
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
}
