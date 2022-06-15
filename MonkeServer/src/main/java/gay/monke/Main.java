package gay.monke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import org.java_websocket.WebSocket;

import gay.monke.packet.EntityStatusPacket;
import gay.monke.packet.Packet;
import gay.monke.world.Monke;
import gay.monke.world.World;

public class Main {
	
	private Server server;
	private BufferedReader console;
	private boolean acceptingConnections = true;
	
	private ArrayList<WebSocket> queue;
	private World world;
	private Thread serverThread;
	
	//private Random random;
		
	private void run() {
		//this.random = new Random();
		world = new World(server);
				
		double TPS = 60;
		double SPT = 1.0 / TPS;
		double a = 0;
		double b = 0;
		
		while(true) {
			a = System.nanoTime() / 1000000000.0;
			checkConsole();
			checkConnections();
			update();
			b = System.nanoTime() / 1000000000.0 - a;
			if(b < SPT) {
				try {
					Thread.sleep((long) ((SPT - b) * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void checkConsole() {
		try {
			if(console.ready()) {
				String command = console.readLine().trim().toLowerCase();
				String[] args = command.split(" ");
				if(args[0].equals("stop")) {
					server.shutdown();
					System.exit(0);
				} else if(args[0].equals("banana")) {
					Monke m = world.getByName(args[1]);
					try {
						m.bananas = Integer.parseInt(args[2]);
					} catch(NumberFormatException e) {
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private boolean parseBoolean(String in) {
//		return in.equals("true") || in.equals("1");
//	}
	
	private void checkConnections() {
		if(acceptingConnections) {
			WebSocket con = server.acceptConnection();
			int a = 0;
			while(con != null && a < 100) {
				queue.add(con);
				con = server.acceptConnection();
				a++;
			}
		}
		for(int i = 0; i < queue.size(); i++) {
			WebSocket con = queue.get(i);
			int h = server.available(con);
			if(h == 1) {
				Packet p = server.readPacket(con);
				if(p instanceof EntityStatusPacket) {
					if(((EntityStatusPacket) p).getSpawned()) { 
						if(((EntityStatusPacket) p).getEntityType() == 0) {
							world.addPlayerMonke(con);
							System.out.println("+[" + con.getRemoteSocketAddress() + "] joined server");
							queue.remove(i);
							i--;
						} else {
							System.err.println("Apparently a banana joined the game or something??? [" + Date.from(Instant.now()).toString() + "]");
						}
					}
				} else {
					server.backlogPacket(con, p);
				}
			} else if(h == -1) {
				queue.remove(i);
				i--;
			} else if(h == 0) {
				//do nothing
			} else {
				System.err.println("Unrecognized availablity status: " + h);
			}
		}
	}
	
	private void update() {
		//this also updates the players
		world.update();
	}
	
	private Main() {
		server = new Server(new InetSocketAddress(8080));
		console = new BufferedReader(new InputStreamReader(System.in));
		queue = new ArrayList<>();
		serverThread = new Thread(server);
		serverThread.start();
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.run();
	}

}