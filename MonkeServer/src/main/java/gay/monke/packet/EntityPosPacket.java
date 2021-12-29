package gay.monke.packet;

import java.nio.ByteBuffer;

import gay.monke.world.Entity;
import gay.monke.world.Monke;

/**
 * IDS UNDER 256 ARE RESERVED FOR PLAYERS<br><br>
 * Contains data in the struture: {<4:id(uint16)><4:x(float)><4:y(float)><4:rot(float)><2:bananas(uint16)>}
 * @author nul00000000
 *
 */
public class EntityPosPacket extends Packet {
	
	public static final int length = 19;
	
	private static final byte[] prefix = Packet.createPrefix("ep");
	private float x;
	private float y;
	private float rot;
	private int bananas;
	private int id;
	private boolean left;
	
	public EntityPosPacket() {
		this(0, 0, 0, 0, 0, false);
	}
	
	public EntityPosPacket(int id, float x, float y, float rot, int bananas, boolean left) {
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.id = id;
		this.bananas = bananas;
		this.left = left;
	}
	
	public EntityPosPacket(EntityPosPacket copy) {
		this(copy.id, copy.x, copy.y, copy.rot, copy.bananas, copy.left);
	}
	
	public EntityPosPacket(int id, EntityPosPacket copy) {
		this(id, copy.x, copy.y, copy.rot, copy.bananas, copy.left);
	}
	
	public EntityPosPacket(Entity entity) {
		this.id = entity.getID();
		this.x = entity.getX();
		this.y = entity.getY();
		this.rot = entity.getRot();
		if(entity instanceof Monke) {
			this.bananas = ((Monke) entity).bananas;
			this.left = ((Monke) entity).left;
		} else {
			this.left = false;
			this.bananas = 0;
		}
	}

	public EntityPosPacket(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		this.id = buffer.getInt();
		this.x = buffer.getFloat();
		this.y = buffer.getFloat();
		this.rot = buffer.getFloat();
		this.bananas = buffer.getShort();
		this.left = buffer.get() != 0;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getRot() {
		return rot;
	}
	
	public int getID() {
		return id;
	}
	
	public int getBananas() {
		return bananas;
	}
	
	public boolean getLeft() {
		return left;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}
	
	/**
	 * Encodes a EntityPosPacket into an byte buffer
	 */
	@Override
	public byte[] encode(byte[] dest) {
		if(dest.length < length + 2) {
			throw new IllegalArgumentException("Destination array has length under " + length);
		}
		ByteBuffer buffer = ByteBuffer.wrap(dest);
		buffer.put(prefix);
		buffer.putInt(id);
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(rot);
		buffer.putShort((short) bananas);
		buffer.put(left ? (byte)1 : (byte)0);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
