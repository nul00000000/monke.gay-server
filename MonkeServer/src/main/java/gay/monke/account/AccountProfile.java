package gay.monke.account;

import java.util.ArrayList;

public class AccountProfile {
	
	public int id;
	public String username;
	public short level;
	public short xp;
	public ArrayList<IUnlockable> unlocks;
	public short streak;
	public long lastPlayTime;
	public int highscore;
	public ArrayList<Integer> friendIds;
	
	public int timezoneOffset;
	
	public AccountProfile(int id, String username, short level, short xp, short streak, int timezoneOffset, long lastPlayTime) {
		this.id = id;
		this.username = username;
		this.level = level;
		this.xp = xp;
		this.streak = streak;
		this.lastPlayTime = lastPlayTime;
	}
	
	private int xpNeededForLevel(int level) {
		return (int) (1000 * Math.log(level + Math.E));
	}
	
	public void addXp(int amount) {
		int t = amount + xp;
		short l = level;
		int xn;
		while(t >= (xn = xpNeededForLevel(l))) {
			t -= xn;
			l++;
		}
		xp = (short) t;
		level = l;
	}
	
	public boolean isValid() {
		return username != null && level >= 0 && xp >= 0 && streak >= 0;
	}

}
