package project2;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.net.Socket;
import java.util.NoSuchElementException;

public class Node implements Runnable {

	private ServerSocket server_socket = null;
	private int identifier;
	private int port;
	private String hostname;
	private String[] all_nodes;
	private int number_of_nodes;
	private ArrayList<String> quorum = new ArrayList<String>();
	public static String config_file_path;
	private String[] info;
	private int type;
	private int delay;
	private int req_no;
	private int cstime;
	
	private static volatile int clock=0;
	private static volatile boolean rel = true;
//!!!!!!!!!!!!!!!!!
	private static volatile ArrayList<Integer> vclk = new ArrayList<Integer>(); //!!!!
	
	
	public synchronized static void setvClock(ArrayList<Integer> clk) //!!!!!!
	{
		vclk=clk;
	}
	
	public synchronized static ArrayList<Integer> getvClock()  //!!!!!!
	{
		return vclk;
	}
//!!!!!!!!!!!!!
	public Node(int type, int id, String config_path) {

		this.type = type;
		this.identifier = id;
		config_file_path = config_path;
		
	}
	
	public static void setRelease(boolean r)
	{
		rel=r;
	}
	
	public synchronized static void setClock(int c)
	{
		clock=c;
	}
	public synchronized static int getClock()
	{
		return clock;
	}
	

	public void run() {

		if (type == 1)
			try {

				runServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			try {
				runClient();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

	private void runClient() throws UnknownHostException, IOException {
		find(config_file_path, identifier);
if(!(vclk.size()==number_of_nodes))
{
for(int k=0;k<number_of_nodes;k++)
	    	vclk.add(0);
}
//System.out.println("client");
		
		while (true) {
			Client_node c = new Client_node(quorum, all_nodes, identifier);
			for (int z = 0; z < req_no; z++) {
				long s = System.currentTimeMillis();
				while (System.currentTimeMillis() < s + delay) {
					// inter-request delay
				}
				c.req(); // send request message to its quorum
				
				while(rel){
					//next request waits till the cs is completed
				}

			}
			Server_node.setRec(false); // terminates server
			//System.out.println("end");
			break;
		}

		Thread.currentThread().interrupt();
		return;
	}

	private void runServer() throws IOException {
		find(config_file_path, identifier);
if(!(vclk.size()==number_of_nodes))
{
for(int k=0;k<number_of_nodes;k++)
	    	vclk.add(0);
}

//System.out.println("server");
		listen();
		//display();

	}

	private void listen() throws IOException {

		server_socket = new ServerSocket(port);

		while (true) {
			Socket s = server_socket.accept();

			Server_node snode = new Server_node(s, this.identifier, this.all_nodes, this.quorum, this.cstime);

			Thread t = new Thread(snode);
			t.start();

		}

	}

	// parsing config file
	private void find(String path, int id) throws FileNotFoundException {

		Scanner scan_path = new Scanner(new File(path));
		identifier = id;
		String nextLine = scan_path.nextLine().trim();
		while (nextLine.equals("") || nextLine.charAt(0) == '#') {
			nextLine = scan_path.nextLine().trim();
		}
		if (nextLine.contains("#")) {
			number_of_nodes = Integer.parseInt(nextLine.split("#")[0].substring(0, 1).trim().split("\\s+")[0]);
		} else {
			number_of_nodes = Integer.parseInt(nextLine.trim().split("\\s+")[0]);
			delay = Integer.parseInt(nextLine.trim().split("\\s+")[1]);
			cstime = Integer.parseInt(nextLine.trim().split("\\s+")[2]);
			req_no = Integer.parseInt(nextLine.trim().split("\\s+")[3]);
		}
		this.all_nodes = new String[number_of_nodes];
		
		nextLine = scan_path.nextLine().trim();
		while (nextLine.equals("") || nextLine.charAt(0) == '#') {
			nextLine = scan_path.nextLine().trim();
		}
		for (int i = 0; i < number_of_nodes; i++) {
			if (nextLine.contains("#")) {
				this.all_nodes[i] = nextLine.split("#")[0].trim();
			} else {
				this.all_nodes[i] = nextLine.trim();

			}

			if (identifier == i) {
				this.info = all_nodes[i].split("\\s+");
				hostname = info[1];
				port = Integer.parseInt(info[2]);

			}

			nextLine = scan_path.nextLine();
		}

		while (nextLine.equals("") || nextLine.charAt(0) == '#') {
			nextLine = scan_path.nextLine();
		}
		String c;
		for (int i = 0; i < number_of_nodes; i++) {
			if (nextLine.contains("#")) {
				c = nextLine.split("#")[0].trim();
			} else {
				c = nextLine.replaceAll("\\s+", " ").trim();
			}

			if (i == identifier) {
				String nodes[] = c.split(" ");
				for (String j : nodes)
					this.quorum.add(j);
			}
			try {
				nextLine = scan_path.nextLine();
			} catch (NoSuchElementException e) {
				break;
			}
		}

	}
	//debug statement
	public void display() {
		System.out.println("id: " + (identifier) + "\nNumber of node: " + number_of_nodes + "\nall nodes: "
				+ Arrays.toString(all_nodes).replaceAll("\\t+", " ") + "\nnode_info" + Arrays.toString(info)
				+ "\nhost & port :" + hostname + " " + port + "\nquorums: " + quorum + "\ncst: " + cstime+ "\ndelay: " + delay+ "\nreq_no: " + req_no);
	}
}
