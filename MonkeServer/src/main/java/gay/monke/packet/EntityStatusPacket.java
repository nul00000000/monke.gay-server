package gay.monke.packet;

import java.nio.ByteBuffer;

import gay.monke.world.Entity;

/**
 * Contains data in the struture: {<4:id(uint16)><1:spawned/despawned(boolean)><1:entitytype(uint8)>}
 * @author nul00000000
 *
 */
public class EntityStatusPacket extends Packet {
	
	public static final int length = 6;
	
	private static final byte[] prefix = Packet.createPrefix("es");
	private int id;
	private boolean spawned;
	private int entityType;
	
	public EntityStatusPacket(int id, boolean spawned, int type) {
		this.id = id;
		this.spawned = spawned;
		this.entityType = type;
	}
	
	public EntityStatusPacket(Entity eb, boolean spawned) {
		this.id = eb.getID();
		this.spawned = spawned;
		this.entityType = eb.getType();
	}
	
	public EntityStatusPacket(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		id = buf.getInt();
		spawned = buf.get() != 0;
		entityType = Byte.toUnsignedInt(buf.get());
	}
	
	public int getID() {
		return id;
	}
	
	public boolean getSpawned() {
		return spawned;
	}
	
	public int getEntityType() {
		return entityType;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer buf = ByteBuffer.wrap(dest);
		buf.put(prefix);
		buf.putInt(id);
		buf.put((byte) (spawned ? 1 : 0));
		buf.put((byte) entityType);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
