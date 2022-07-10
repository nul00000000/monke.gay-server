package gay.monke.account;

import java.util.ArrayList;

public class AccountProfile {
	
	public int id;
	public String username;
	public short level;
	public short xp;
	public ArrayList<IUnlockable> unlocks;
	public short streak;
	public long lastPlayedTime;
	public int highscore;
	public ArrayList<Integer> friendIds;
	
	public AccountProfile(int id, String username, short level, short xp, short streak) {
		this.id = id;
		this.username = username;
		this.level = level;
		this.xp = xp;
		this.streak = streak;
	}
	
	public boolean isValid() {
		return username != null && level >= 0 && xp >= 0 && streak >= 0;
	}

}
