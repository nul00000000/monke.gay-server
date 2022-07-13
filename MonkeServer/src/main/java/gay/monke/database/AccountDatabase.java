package gay.monke.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gay.monke.account.AccountProfile;
import gay.monke.packet.TokenPacket;

public class AccountDatabase {
	
	private Connection conn;
	private PreparedStatement getTokenStatement;
	private PreparedStatement getProfileStatement;
	private PreparedStatement updateProfileStatement;
	private PreparedStatement checkConnectionStatement;
	
	public AccountDatabase() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://192.168.1.48/users", AccountConstants.SQL_USER, AccountConstants.SQL_PASS);
			boolean isValid = conn.isValid(0);
			System.out.println(isValid ? "Successfully connected to account database" : "Failed to connect to account database");
			getTokenStatement = conn.prepareStatement("SELECT * FROM tokens WHERE id = ? AND token = ?");
			getProfileStatement = conn.prepareStatement("SELECT * FROM profiles WHERE id = ?");
			updateProfileStatement = conn.prepareStatement("UPDATE profiles SET level=?, xp=?, streak=?, lastPlayTime=?, highscore=? WHERE id = ?");
			checkConnectionStatement = conn.prepareStatement("SELECT * FROM logins WHERE id = 0");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkConnection() {
		try {
			checkConnectionStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateProfile(AccountProfile profile) {
		if(!profile.isValid()) {
			return;
		}
		try {
			updateProfileStatement.setShort(1, profile.level);
			updateProfileStatement.setShort(2, profile.xp);
			updateProfileStatement.setShort(3, profile.streak);
			updateProfileStatement.setLong(4, profile.lastPlayTime);
			updateProfileStatement.setInt(5, profile.highscore);
			updateProfileStatement.setInt(6, profile.id);
			updateProfileStatement.execute();
		} catch(SQLException e) {
			System.out.println("[SQL ERROR] Error updating profile, SQLState: " + e.getSQLState());
		}
	}
	
	public AccountProfile getProfileWithToken(TokenPacket packet) {
		try {
			getTokenStatement.setInt(1, packet.getId());
			getTokenStatement.setInt(2, packet.getToken());
			ResultSet rs = getTokenStatement.executeQuery();
			if(rs.next()) {
				getProfileStatement.setInt(1, packet.getId());
				ResultSet p = getProfileStatement.executeQuery();
				if(p.next()) {
					return new AccountProfile(packet.getId(), p.getString("username"), p.getShort("level"), p.getShort("xp"), p.getShort("streak"), packet.getTimezoneOffset(), p.getLong("lastPlayTime"));
				} else {
					System.out.println("[ERROR] Could not find profile for token with id " + packet.getId());
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
