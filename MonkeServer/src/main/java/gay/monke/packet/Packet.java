package gay.monke.packet;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * All subclasses should have a copy constructor that creates a instance that returns the same values from all functions<br><br>
 * A Packet's encoded byte structure should be structured: {<prefix>{data}} length of message can be inferred from prefix<br><br>
 * A Packet's decoding byte structure should be structured: {{data}} length of message is known by type
 * @author nul00000000
 */
public abstract class Packet {
	
	/**
	 * Creates default instance of Packet<br><br>
	 * Should be implemented in all subclasses
	 */
	public Packet() {
		
	}
	
	/**
	 * Creates instance of Packet with variables extrated from a byte array<br><br>
	 * This function must be consistent with a {@link Packet(Packet)} constructor, the same byte array must create instances that return identical values from all functions.<br><br>
	 * Must be implemented in all subclasses, as it is the only way to decode info
	 */
	public Packet(byte[] data) {
		
	}
	
//	public static float fromBytes(byte[] data, int index) {
//		int a = ((data[index]) & 0xff) | ((data[index + 1] << 8) & 0xff00) | ((data[index + 2] << 16) & 0xff0000) | ((data[index + 3] << 24) & 0xff000000);
//		return Float.intBitsToFloat(a);
//	}
	
	protected static byte[] createPrefix(String prefix) {
		if(prefix.length() != 2) {
			return null;
		}
		try {
			return prefix.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;//this should never run because UTF-8 is required to be on every system i think
		}
	}
	
	public static byte[] encodeString(String encode) {
		try {
			return encode.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;//this should never run because UTF-8 is required to be on every system i think
		}
	}
	
	public static Packet decode(Class<? extends Packet> type, byte[] data) {
		try {
			Constructor<? extends Packet> c = type.getConstructor(byte[].class);
			Packet instance = c.newInstance(data);
//			if(type == null || c == null || instance == null) {
//				System.out.println("Type: " + type);
//				System.out.println("Constructor: " + c);
//				System.out.println("Instance: " + instance);
//			}
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	private static byte[] concat(byte[] prefix, byte[] other) {
//		byte[] ret = new byte[prefix.length + other.length];
//		for(int i = 0; i < prefix.length; i++) {
//			ret[i] = prefix[i];
//		}
//		for(int i = prefix.length; i < ret.length; i++) {
//			ret[i] = other[i - prefix.length];
//		}
//		return ret;
//	}
	
	public static Packet decode(byte[] prefix, byte[] data) {
		return decode(getTypeFromPrefix(prefix), data);
	}
	
	public static int getLengthFromPrefix(byte[] p) {
		String prefix = null;
		try {
			prefix = new String(p, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		switch(prefix) {
		default:
			return -1;
		case "ep":
			return EntityPosPacket.length;
		case "ws":
			return WorldSizePacket.length;
		case "es":
			return EntityStatusPacket.length;
		case "ei":
			return EntityInfoPacket.length;
		case "bs":
			return BananaSpawnPacket.length;
		case "cp":
			return ChatPacket.length;
		}
	}
	
	public static Class<? extends Packet> getTypeFromPrefix(byte[] p) {
		String prefix = null;
		try {
			prefix = new String(p, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		switch(prefix) {
		default:
			return null;
		case "ep":
			return EntityPosPacket.class;
		case "ws":
			return WorldSizePacket.class;
		case "es":
			return EntityStatusPacket.class;
		case "ei":
			return EntityInfoPacket.class;
		case "bs":
			return BananaSpawnPacket.class;
		case "cp":
			return ChatPacket.class;
		}
	}
	
	private static byte[] prefixMut = new byte[2];
	
	public static int getLengthFromPrefix(byte a, byte b) {
		prefixMut[0] = a;
		prefixMut[1] = a;
		return getLengthFromPrefix(prefixMut);
	}
	
	/**
	 * @return A full, sendable array of bytes containing the information and identifying prefix
	 * @author nul00000000
	 */
	public abstract byte[] encode();
	
	/**
	 * @param Where the bytes will be placed
	 * @return A full, sendable array of bytes containing the information and identifying prefix
	 * @author nul00000000
	 */
	public abstract byte[] encode(byte[] dest);
	
	/**
	 * @return The 2 byte identifying prefix for the Packet
	 * @author nul00000000
	 */
	public abstract byte[] getPrefix();

}
