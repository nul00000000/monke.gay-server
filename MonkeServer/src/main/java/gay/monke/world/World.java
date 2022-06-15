package gay.monke.world;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.java_websocket.WebSocket;

import gay.monke.Server;
import gay.monke.packet.BananaSpawnPacket;
import gay.monke.packet.ChatPacket;
import gay.monke.packet.EntityInfoPacket;
import gay.monke.packet.EntityPosPacket;
import gay.monke.packet.EntityStatusPacket;
import gay.monke.packet.Packet;
import gay.monke.packet.WorldSizePacket;

public class World {
	
	public int numMonkes = 5;
	public float worldSize = 24000;
	
	private ArrayList<Banana> bananas;
	private ArrayList<Monke> monkes;
	public ArrayList<BananaFountain> fountains;
	
	private Server server;
	private Random random;
	
	private ArrayList<String> names;
	
	public World(Server server) {
		this.server = server;
		bananas = new ArrayList<>();
		monkes = new ArrayList<>();
		fountains = new ArrayList<>();
		random = new Random();
		this.names = new ArrayList<>();
		Scanner s = new Scanner(World.class.getResourceAsStream("/gay/monke/utility/names.csv"), "utf-16be");
		
		while(s.hasNextLine()) {
			String[] objs = s.nextLine().split(",");
			int weight = Integer.parseInt(objs[1]);
			for(int i = 0; i < weight; i++) {
				this.names.add(objs[0]);
			}
		}
		s.close();
		for(int i = 0; i < numMonkes; i++) {
			this.addAIMonke(random.nextFloat() * this.worldSize - this.worldSize / 2, 0);
		}
		
	}
	
	public String getRandomName() {
		return names.get(random.nextInt(names.size()));
	}
	
	/**
	 * Called whenever a modified client is suspected, function decides action on risk level
	 * @param level Risk level, ranging from 0 to 9, with 0 being very low risk and 9 being a "ban this man immedietely like damn howd he get through"
	 * @param player The player in question
	 */
	public void reportModifiedClient(int level, PlayerMonke player) {
		System.out.println("Level " + level + " MCW on player (" + player.name + ", " + player.connection.getRemoteSocketAddress() + ")");
	}
	
	public int getNextID() {
		int a = -1;
		boolean b = false;
		outer:
		while(!b && a < 65536) {
			b = true;
			a++;
			for(Monke m : monkes) {
				if(a == m.getID()) {
					b = false;
					continue outer;
				}
			}
			for(Banana m : bananas) {
				if(a == m.getID()) {
					b = false;
					continue outer;
				}
			}
		}
		return a;
	}
	
	public Entity getEntity(int id) {
		for(Entity e : bananas) {
			if(e.getID() == id) {
				return e;
			}
		}
		for(Entity e : monkes) {
			if(e.getID() == id) {
				return e;
			}
		}
		return null;
	}
	
	public Entity removeEntity(int id) {
		Entity r = null;
		for(Entity e : bananas) {
			if(e.getID() == id) {
				r = e;
			}
		}
		for(Entity e : monkes) {
			if(e.getID() == id) {
				r = e;
			}
		}
		if(r != null) {
			this.broadcastPacket(new EntityStatusPacket(r, false), null);
			if(r instanceof Monke) {
				monkes.remove(r);
			} else if(r instanceof Banana) {
				bananas.remove(r);
			}
		}
		return r;
	}
	
	public Monke getByName(String name) {
		for(Monke e : monkes) {
			if(e.name.equals(name)) {
				return e;
			}
		}
		return null;
	}
	
	public boolean removeEntity(Entity e) {
		boolean ret = false;
		if(e instanceof Monke) {
			ret = monkes.remove(e);
		} else if(e instanceof Banana) {
			ret = bananas.remove(e);
		} else {
			System.out.println("Tried to remove entity not of known type");
			return false;
		}
		if(ret) {
			Packet p = new EntityStatusPacket(e.getID(), false, e.getType());
			this.broadcastPacket(p, null);
			if(e instanceof PlayerMonke) {
				server.sendPacket(((PlayerMonke) e).connection, p);
				server.closeConnection(((PlayerMonke) e).connection, 0);
			}
		}
		return ret;
	}
	
	/**
	 * Only use externally <- (3-5 (??) months later) lol
	 * @param speed i am speed
	 * @param angle did you fall from geometry class because you look like a severely depressed student
	 * @param thrower im the king of the throwers, oh the top lane VIP
	 * @return banana
	 */
	public Banana addBanana(float speed, float x, float y, float angle, Monke thrower) {
		Banana banana = new Banana(this.getNextID(), x, y, speed, angle, thrower, this, true);
		bananas.add(banana);
		this.broadcastPacket(new EntityStatusPacket(banana, true), thrower);
		this.broadcastPacket(new EntityPosPacket(banana), thrower);
		return banana;
	}
	
	public Banana addBanana(float speed, float x, float y, float angle, Monke thrower, boolean canKill) {
		Banana banana = new Banana(this.getNextID(), x, y, speed, angle, thrower, this, canKill);
		bananas.add(banana);
		this.broadcastPacket(new EntityStatusPacket(banana, true), thrower);
		this.broadcastPacket(new EntityPosPacket(banana), thrower);
		return banana;
	}
	
	public Monke addAIMonke(float x, float y) {
		Monke monke = new Monke(this.getNextID(), x, y, this, random);
		monkes.add(monke);
		this.broadcastPacket(new EntityStatusPacket(monke, true), null);
		this.broadcastPacket(new EntityPosPacket(monke), null);
		ByteBuffer bb = ByteBuffer.wrap(new byte[32]);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort((short) monke.skin);
		this.broadcastPacket(new EntityInfoPacket(monke.getID(), 0, bb.array()), null);
		this.broadcastPacket(new EntityInfoPacket(monke.getID(), 1, monke.name), null);
		return monke;
	}
	
	public PlayerMonke addPlayerMonke(WebSocket session) {
		PlayerMonke player = new PlayerMonke(this.getNextID(), this, session);
		this.monkes.add(player);
		ByteBuffer bb = ByteBuffer.wrap(new byte[32]);
		bb.putShort((short) player.getID());
		server.sendPacket(player.connection, new EntityInfoPacket(player.getID(), 2, bb.array()));
		for(Monke eb : this.monkes) {
			if(eb != player) {
				server.sendPacket(player.connection, new EntityStatusPacket(eb, true));
				ByteBuffer bb2 = ByteBuffer.wrap(new byte[32]);
				bb2.order(ByteOrder.LITTLE_ENDIAN);
				bb2.putShort((short) eb.skin);
				server.sendPacket(player.connection, new EntityInfoPacket(eb.getID(), 0, bb2.array()));
				server.sendPacket(player.connection, new EntityInfoPacket(eb.getID(), 1, eb.name));
				if(eb instanceof PlayerMonke) {
					server.sendPacket(((PlayerMonke) eb).connection, new EntityStatusPacket(player, true));
				}
			}
		}
		for(Banana eb : this.bananas) {
			server.sendPacket(player.connection, new EntityStatusPacket(eb, true));
		}
		server.sendPacket(player.connection, new WorldSizePacket(this));
		//server.sendPacket(player.connection, new ChatPacket(69, 0x43, "wow thats a bit sussy"));
		return player;
	}
	
	public void broadcastPacket(Packet packet, Monke exclude) {
		for(int i = 0; i < monkes.size(); i++) {
			Monke m = monkes.get(i);
			if(m instanceof PlayerMonke && m != exclude) {
				server.sendPacket(((PlayerMonke) m).connection, packet);
			}
		}
	}
	
	public void update() {
		for(int i = 0; i < monkes.size(); i++) {
			if(monkes.get(i) instanceof PlayerMonke) {
				PlayerMonke m = (PlayerMonke) monkes.get(i);
				if(server.outgoingConnections.contains(m.connection)) {
					this.removeEntity(m);
					server.outgoingConnections.remove(m.connection);
					i--;
					continue;
				}
				if(server.available(m.connection) < 0) {
					this.removeEntity(m);
					continue;
				}
				while(server.available(m.connection) > 0) {
					Packet read = server.readPacket(m.connection);
					if(read instanceof EntityPosPacket || read instanceof EntityInfoPacket) {
						m.applyPacket(read);
					} else if(read instanceof BananaSpawnPacket) {
						if(m.bananas > 0) {
							Banana b = this.addBanana(30.0f, m.getX(), m.getY() - m.getHeight() / 2, ((BananaSpawnPacket) read).getAngle(), m);
							m.bananas--;
							this.server.sendPacket(m.connection, new BananaSpawnPacket(b.getID(), 0));
						}
					} else {
						System.err.println("Unrecognized packet: " + read);
					}
				}
			}
		}
		
		for(int j = 0; j < monkes.size(); j++) {
			Monke m = monkes.get(j);
			float x1 = m.x - m.getWidth() / 2;
			float xw1 = m.x + m.getWidth() / 2;
			float y1 = m.y - m.getHeight();
			float yh1 = m.y;
			for(int i = 0; i < monkes.size(); i++) {
				Monke m1 = monkes.get(i);
				float x2 = m1.x - m1.getWidth() / 2;
				float xw2 = m1.x + m1.getWidth() / 2;
				float y2 = m1.y - m1.getHeight();
				float yh2 = m1.y;
				float x5 = Math.max(x1, x2);
				float y5 = Math.max(y1, y2);
				float x6 = Math.min(xw1, xw2);
				float y6 = Math.min(yh1, yh2);
				if(m != m1 && x5 < x6 && y5 < y6) {
					if(x6 - x5 < y6 - y5) {
						if(m.x > m1.x) {
							m.dx = xw2 - x1;
						} else {
							m.dx = x2 - xw1;
						}
					} else {
						m.onGround = true;
						m1.onGround = true;
						if(m.y > m1.y) {
							m.dy = yh2 - y1;
						} else {
							m.dy = y2 - yh1;
						}
					}
				}
			}
			m.update();
			for(int i = 0; i < bananas.size(); i++) {
				Banana b = bananas.get(i);
				if(m != b.thrower && m.canThrow && m.x - m.getWidth() / 2 < b.x + 20 && m.x + m.getWidth() / 2 > b.x - 20 && 
						m.y - m.getHeight() < b.y + 20 && m.y > b.y - 20) {
					if(b.onGround || !b.canKill) {
						this.removeEntity(b);
						i--;
						m.bananas++;
						if(m instanceof PlayerMonke) {
							server.sendPacket(((PlayerMonke) m).connection, new EntityPosPacket(m));
						}
					} else if(b.canKill) {
						fountains.add(new BananaFountain(m.x, m.y, m.bananas / 2, this, random));
						this.removeEntity(m);
						this.broadcastPacket(new ChatPacket(0, ChatPacket.P_BROADCAST | ChatPacket.P_PLAYER_STATUS, 
								(b.thrower != null ? b.thrower.name : "Server") + " killed " + m.name), null);
						j--;
					}
				}
			}
		}
		while(monkes.size() < numMonkes) {
			this.addAIMonke(random.nextFloat() * this.worldSize - this.worldSize / 2, 0);
		}
		for(int i = 0; i < fountains.size(); i++) {
			BananaFountain bf = fountains.get(i);
			bf.update();
			if(bf.bananas <= 0) {
				fountains.remove(i);
				i--;
			}
		}
		for(int i = 0; i < bananas.size(); i++) {
			if(bananas.get(i).age > 1000) {
				this.broadcastPacket(new EntityStatusPacket(bananas.remove(i), false), null);//haha this is technically faster
				i--;
			} else {
				bananas.get(i).update();
			}
		}
		for(Monke m : monkes) {
			this.broadcastPacket(new EntityPosPacket(m), m);
		}
		for(Banana b : bananas) {
			this.broadcastPacket(new EntityPosPacket(b), null);
		}
//		if(random.nextDouble() < 0.01) {
//			this.broadcastPacket(new ChatPacket(this.monkes.get(random.nextInt(this.monkes.size())).getID(), 0x0, "me when i test the chat"), null);
//		}
	}

}
