package gay.monke.world;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.java_websocket.WebSocket;

import gay.monke.packet.EntityInfoPacket;
import gay.monke.packet.EntityPosPacket;
import gay.monke.packet.Packet;

public class PlayerMonke extends Monke {
	
	public WebSocket connection;
	public boolean needsInfoUpdate = false;

	public PlayerMonke(int id, World world, WebSocket con) {
		super(id, 0, world, null);
		this.y = 0;
		this.connection = con;
	}
	
	public void applyPacket(Packet p) {
		if(p instanceof EntityPosPacket) {
			Entity e = world.getEntity(((EntityPosPacket) p).getID());
			if(e instanceof PlayerMonke) {
				if(((EntityPosPacket) p).getID() == this.getID()) {
					this.x = ((EntityPosPacket) p).getX();
					this.y = ((EntityPosPacket) p).getY();
					this.left = ((EntityPosPacket) p).getLeft();
				} else {
					world.reportModifiedClient(7, this);//7 is good because its like yeah you are definitely hacking but like it doesnt actually matter
				}
			} else if(e instanceof Monke) {
				world.reportModifiedClient(7, this);
			} else if(e instanceof Banana) {
				world.reportModifiedClient(7, this);
			} else {
				world.reportModifiedClient(7, this);
			}
		} else if(p instanceof EntityInfoPacket) {
			if(((EntityInfoPacket) p).getCode() == 0) {
				ByteBuffer b = ByteBuffer.wrap(((EntityInfoPacket) p).getInfo());
				b.order(ByteOrder.LITTLE_ENDIAN);
				this.skin = b.getShort();
				this.world.broadcastPacket(new EntityInfoPacket(this.getID(), 0, ((EntityInfoPacket) p).getInfo()), this);
			} else if(((EntityInfoPacket) p).getCode() == 1) {
				this.name = ((EntityInfoPacket) p).getString();
				this.world.broadcastPacket(new EntityInfoPacket(this.getID(), 1, ((EntityInfoPacket) p).getInfo()), this);
				System.out.println("[" + connection.getRemoteSocketAddress() + "] = (" + name + ")");
			}
		} else {
			System.out.println("What if you modded the monke.gay servers... jk... unless? [" + p + "]");
		}
	}
	
	public void update() {
		if(ticks > 150) {
			canThrow = true;
		}
		ticks++;
		//do nothing because for now there shall be no regulation. Anarchy shall reign supreme. SUBSCRIBE TO TECHNOBLADE. <- this holds up a month later <- true 3 months
	}
	
	@Override
	public int getType() {
		return 2;
	}

}
