package project2;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;		
import java.util.ArrayList;
import java.util.Collections;


public class Client_node {

	private ArrayList<String> quorum = null;
	private ArrayList<String> quorum_cpy = null;
	private String[] all_nodes;
	private int id;
	private Socket soc_Client = null;

	public Client_node(ArrayList<String> quorum, String[] all_nodes, int id) {

		this.quorum = quorum;
		this.all_nodes = all_nodes;
		this.id = id;

	}

	public void req() throws UnknownHostException, IOException {
		//Collections.copy(quorum_cpy,quorum);
		for (String e:quorum) {
			for (String i : all_nodes) {
				if (i.split("\\s+")[0].equals(e)) {
					String host = i.split("\\s+")[1];
					String port = i.split("\\s+")[2];
					//Node.setClock(Node.getClock()+1);
					//String k = "req " + String.valueOf(id)+" "+String.valueOf(Node.getClock());
					

					//!!!!!!!!!!!!!!!!!
					ArrayList<Integer> cv=Node.getvClock();
					cv.set(id, cv.get(id)+1);
					Node.setvClock(cv);
					String k = "req " + String.valueOf(id)+" "+Node.getvClock().toString();
					//!!!!!!!!!!!!!!!

					connect(host, port, k);
					
					
				}
			}

		}
		Node.setRelease(true);
	}

	public void connect(String h, String p, String msg) throws UnknownHostException, IOException {

		int port = Integer.parseInt(p);
		soc_Client = new Socket(h, port);
		PrintWriter output = new PrintWriter(new OutputStreamWriter(soc_Client.getOutputStream()));
		output.print(msg + "\r\n");
		output.flush();
		soc_Client.close();

	}

}
