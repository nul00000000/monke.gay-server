package gay.monke.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.protocol.Resultset;

import gay.monke.account.AccountProfile;

public class AccountDatabase {
	
	private Connection conn;
	private PreparedStatement getTokenStatement;
	private PreparedStatement getProfileStatement;
	
	public AccountDatabase() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://192.168.1.48/users", AccountConstants.SQL_USER, AccountConstants.SQL_PASS);
			boolean isValid = conn.isValid(0);
			System.out.println(isValid ? "Successfully connected to account database" : "Failed to connect to account database");
			getTokenStatement = conn.prepareStatement("SELECT * FROM tokens WHERE id = ? AND token = ?");
			getProfileStatement = conn.prepareStatement("SELECT * FROM profiles WHERE id = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public AccountProfile getProfileWithToken(int id, int token) {
		try {
			getTokenStatement.setInt(1, id);
			getTokenStatement.setInt(2, token);
			ResultSet rs = getTokenStatement.executeQuery();
			if(rs.next()) {
				getProfileStatement.setInt(1, id);
				ResultSet p = getProfileStatement.executeQuery();
				if(p.next()) {
					return new AccountProfile(id, p.getString("username"), p.getShort("level"), p.getShort("xp"), p.getShort("streak"));
				} else {
					System.out.println("[ERROR] Could not find profile for token with id " + id);
					return null;
				}
			}
			return null;
		} catch (SQLException e) {
			System.out.println("[SQL ERROR] Error getting profile, SQLState: " + e.getSQLState());
			return null;
		}
	}

}
