package gay.monke;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import gay.monke.packet.Packet;

public class Server extends WebSocketServer {
	
	private ArrayList<WebSocket> incomingConnections;
	public ArrayList<WebSocket> outgoingConnections;
	private HashMap<WebSocket, ArrayList<Packet>> incoming;
	
	public Server(InetSocketAddress address) {
		super(address);
		this.incoming = new HashMap<>();
		this.incomingConnections = new ArrayList<>();
		this.outgoingConnections = new ArrayList<>();
	}
	
	public void shutdown(int closecode) {
		for(WebSocket ws : incoming.keySet()) {
			ws.close();	
		}
		try {
			this.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection(WebSocket con, int closeCode) {
		con.close();
		incoming.remove(con);
	}
	
	public void shutdown() {
		this.shutdown(CloseFrame.NORMAL);
	}
	
	/**
	 * @return The connected client, or null if none was found
	 */
	public WebSocket acceptConnection() {
		if(incomingConnections.size() > 0) {
			WebSocket ws = incomingConnections.get(0);
			incomingConnections.remove(0);
			return ws;
		} else {
			return null;
		}
	}
	
	public void backlogPacket(WebSocket s, Packet packet) {
		incoming.get(s).add(packet);
	}
	
	/**
	 * @param to
	 * @param packet
	 * @return true if packet was sent successfully, false if not, suggesting the connection was closed
	 */
	public boolean sendPacket(WebSocket to, Packet packet) {
		try {
			to.send(packet.encode());
			return true;
		} catch(IllegalArgumentException e) {
			System.out.println("tried to send null data from " + packet);
			return false;
		} catch(WebsocketNotConnectedException e) {
			System.out.println("tried to send data to unconnected websocket");
			return false;
		}
	}
	
	public void broadcastPacket(Packet packet) {
		for(WebSocket s : incoming.keySet()) {
			s.send(packet.encode());
		}
	}
	
	/**
	 * Only checks that there is some unread data, not that a full packet is ready
	 * @return if a packet is ready to be read, if this is false, {@code readPacket()} will block until one arrives
	 */
	public int available(WebSocket from) {
		ArrayList<Packet> p = incoming.get(from);
		if(p == null) {
			return -1;
		} else {
			return p.size() > 0 ? 1 : 0;
		}
	}
	
	private byte[] prefixMut = new byte[2];
		
	/**
	 * Will block until full packet is received
	 * @return the next Packet sent from the server
	 */
	public Packet readPacket(WebSocket from) {
		Packet r = incoming.get(from).get(0);
		incoming.get(from).remove(0);
		return r;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		incoming.put(conn, new ArrayList<>());
		incomingConnections.add(conn);
		System.out.println("+[" + conn.getRemoteSocketAddress() + "]");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("-[" + conn.getRemoteSocketAddress() + "]");
		incomingConnections.remove(conn); //returns true if connection has not been accepted
		incoming.remove(conn);
		outgoingConnections.add(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		ByteBuffer bb = null;
		try {
			bb = ByteBuffer.wrap(message.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		bb.get(prefixMut);
		byte[] data = new byte[Packet.getLengthFromPrefix(prefixMut)];
		bb.get(data);
		incoming.get(conn).add(Packet.decode(prefixMut, data));
	}
	
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		message.get(prefixMut);
//		try {
//			System.out.write(prefixMut);
//			System.out.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		int l = Packet.getLengthFromPrefix(prefixMut);
		if(l == -1) {
			try {
				System.err.println("Prefix [" + new String(prefixMut, "UTF-8") + "] not recognized");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return;
		} else if (l != message.limit() - 2) {
			try {
				System.err.println("Wrong amount of data for prefix [" + new String(prefixMut, "UTF-8") + "], expected length " + l + " but got " + 
						message.limit());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return;
		}
		byte[] data = new byte[l];
		message.get(data);
		incoming.get(conn).add(Packet.decode(prefixMut, data));
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		System.out.println("Server started on " + this.getAddress());
	}

}
