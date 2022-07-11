package gay.monke.world.learner;

public class Link {
	
	public int node1;
	public int node2;
	public int type;
	public double weight;
	
	public Link(int node1, int node2, int type, double weight) {
		this.node1 = node1;
		this.node2 = node2;
		this.type = type;
	}
	
	public static double calculate(Link link, double input) {
		switch(link.type) {
		default:
			return input;
		case 0:
			return input * link.weight;
		}
	}

}
