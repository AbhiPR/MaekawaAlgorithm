package project2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Server_node implements Runnable {
	private int id;
	private String[] all_nodes;
	private ArrayList<String> quorum;
	private Socket soc_server;
	private static String grant = "";
	private static ArrayList<String> grant_set = new ArrayList<String>();
	private boolean failed = false;
	private boolean yield = false;
	private boolean grant_msg = false;
	private int cstime;
	private static volatile boolean rec=true;
	static Comparator<String> comparator = new comp();
	static PriorityQueue<String> request = new PriorityQueue<String>(1, comparator);
	static int count=0;

	public Server_node(Socket a, int id, String[] all_nodes, ArrayList<String> quorum, int cstime) {

		this.soc_server = a;
		this.id = id;
		this.all_nodes = all_nodes;
		this.quorum = quorum;
		this.cstime = cstime;
	}

	//public Server_node() {
		

	//}

	/*public int compare(String x, String y) { // request priority

		if (Integer.parseInt(x.split("\\s+")[1]) < Integer.parseInt(y.split("\\s+")[1])) {
			return -1;
		} else if (Integer.parseInt(x.split("\\s+")[1]) > Integer.parseInt(y.split("\\s+")[1])) {
			return 1;
		} else {
			if (Integer.parseInt(x.split("\\s+")[0]) < Integer.parseInt(y.split("\\s+")[0])) {
				return -1;
			} else if (Integer.parseInt(x.split("\\s+")[0]) > Integer.parseInt(y.split("\\s+")[0])) {
				return 1;
			}
		}
		return 0;
	}*/

	/*
	 * public void outfile() throws FileNotFoundException,
	 * UnsupportedEncodingException {
	 * 
	 * int s = Node.config_file_path.lastIndexOf('/'); int d =
	 * Node.config_file_path.lastIndexOf('.'); String filename =
	 * Node.config_file_path.substring(s + 1, d);
	 * 
	 * File f = new File(filename + "-" + id + ".out"); PrintWriter writer = new
	 * PrintWriter(f, "UTF-8"); String p = Node.getParent(); ArrayList<String> c
	 * = Node.getChild(); if (!c.isEmpty()) Collections.sort(c); if
	 * (p.equals("")) writer.println("*"); else writer.println(p); if
	 * (c.isEmpty()) writer.println("*"); else for (String i : c) writer.print(i
	 * + " ");
	 * 
	 * writer.close(); }
	 */
	
	public static void setRec(boolean r)
	{
		rec=r;
		
	}

	public void cs() throws FileNotFoundException, UnsupportedEncodingException {
		long s = System.currentTimeMillis();
		while (System.currentTimeMillis() < s + cstime) {
			// inter-request delay
			
		}
		count++;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output.txt"),true))) {
					    bw.write("c="+count+" "+id+" "+Node.getClock()+"\n");
					    bw.close();
					    }catch (IOException ex) {
					    
					    }
	
	

	}
	public void compareClk(int x){
		int y=Node.getClock();
		if(y>x)
			Node.setClock(y+1);
		else if(y==x)
			Node.setClock(x+1);
		else
			Node.setClock(x+1);
		
		
	}

	@Override
	public void run() {

		BufferedReader br;
		String k;

		String s;

		try {
			br = new BufferedReader(new InputStreamReader(soc_server.getInputStream()));

			while (true) {
				s = "";
				while ((k = br.readLine()) != null) {
					s = k;
				}

				if (s.split("\\s+")[0].equals("req")) { // request
					System.out.println(id+" "+s); //~~~~
					request.add(s.split("\\s+", 2)[1]);
					compareClk(Integer.parseInt(s.split("\\s+")[2]));

					if (grant.equals("")) {
						grant = s.split("\\s+", 2)[1];
						for (String i : all_nodes) {
							if (i.split("\\s+")[0].equals(s.split("\\s+")[1])) {
								String host = i.split("\\s+")[1];
								String port = i.split("\\s+")[2];
								Client_node c = new Client_node(quorum, all_nodes, id);
								long t = System.currentTimeMillis();
							while (System.currentTimeMillis() < t + 20) { //@@@@
								
							}
								Node.setClock(Node.getClock()+1);
								c.connect(host, port, "g " + id+" "+ String.valueOf(Node.getClock())); // add clock
								break;
							}
						}

					} else {
						// compare priority
						if (!s.split("\\s+", 2)[1].equals(request.element())) { // currently
																				// higher
																				// priority
																				// req
																				// -send
																				// fail
							for (String i : all_nodes) {
								if (i.split("\\s+")[0].equals(s.split("\\s+")[1])) {
									String host = i.split("\\s+")[1];
									String port = i.split("\\s+")[2];
									Client_node c = new Client_node(quorum, all_nodes, id);
									Node.setClock(Node.getClock()+1);
									c.connect(host, port, "f " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
									break;
								}
							}
						}
						// currently lower priority req- send inquire
						else {
							for (String i : all_nodes) {
								if (i.split("\\s+")[0].equals(grant.split("\\s+")[0])) { //########
									String host = i.split("\\s+")[1];
									String port = i.split("\\s+")[2];
									Client_node c = new Client_node(quorum, all_nodes, id);
									Node.setClock(Node.getClock()+1);
									c.connect(host, port, "i " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
									break;
								}
							}
						}

					}

				} else if (s.split("\\s+")[0].equals("i")) { // inquire
					System.out.println(id+" "+s);//`````
					compareClk(Integer.parseInt(s.split("\\s+")[2]));
					if (failed) {
						for (String i : all_nodes) {
							if (i.split("\\s+")[0].equals(s.split("\\s+")[1])) {
								String host = i.split("\\s+")[1];
								String port = i.split("\\s+")[2];
								Client_node c = new Client_node(quorum, all_nodes, id);
								Node.setClock(Node.getClock()+1);
								c.connect(host, port, "y " + id+" "+String.valueOf(Node.getClock())); // add clock
								//yield = true;
								break;
							}
						}
					}

				} else if (s.split("\\s+")[0].equals("f")) { // failed
					System.out.println(id+" "+s);//```
					compareClk(Integer.parseInt(s.split("\\s+")[2]));
					failed = true;
				}

				else if (s.split("\\s+")[0].equals("y")) { // yield
					System.out.println(id+" "+s);//```
					
					String top_req = request.element();
					grant=top_req; //#############
					compareClk(Integer.parseInt(s.split("\\s+")[2]));

					for (String i : all_nodes) {
						if (i.split("\\s+")[0].equals(top_req.split("\\s+")[0])) {
							String host = i.split("\\s+")[1];
							String port = i.split("\\s+")[2];
							Client_node c = new Client_node(quorum, all_nodes, id);
							Node.setClock(Node.getClock()+1);
							c.connect(host, port, "g " + id+" "+String.valueOf(Node.getClock())); // add clock
							//request.add(s.split("\\s+", 2)[1]);
							break;
						}
					}

				} else if (s.split("\\s+")[0].equals("r")) {
					
					//request.remove();
					
				   	for(String a:request)
					{
						if(a.split("\\s+")[0].equals(s.split("\\s+")[1]))
							{
							request.remove(a);
							break;
							}
					}
					System.out.println(id+" "+s+" "+request);

					grant=""; //#############
					compareClk(Integer.parseInt(s.split("\\s+")[2]));

					if (!request.isEmpty()) {
						String top_req = request.element();
						grant=top_req;
						for (String i : all_nodes) {
							if (i.split("\\s+")[0].equals(top_req.split("\\s+")[0])) {
								String host = i.split("\\s+")[1];
								String port = i.split("\\s+")[2];
								Client_node c = new Client_node(quorum, all_nodes, id);
								long t = System.currentTimeMillis();
							while (System.currentTimeMillis() < t + 20) { //@@@@
								
							}
								Node.setClock(Node.getClock()+1);
								c.connect(host, port, "g " + id+" "+String.valueOf(Node.getClock())); // add clock

								break;
							}
						}
					}

				} else if (s.split("\\s+")[0].equals("g")) { // grant
					System.out.println(id+" "+s+" "+request);//`````
					compareClk(Integer.parseInt(s.split("\\s+")[2]));
					
										
					/*for(String a:request)
					{
						if(a.split("\\s+")[0].equals(s.split("\\s+")[1]))
							{
							request.remove(a);
							break;
							}
					}*/

					grant_msg = true;
					grant_set.add(s.split("\\s+")[1]);
					
					if (grant_set.size() == quorum.size()) {

						ArrayList<String> g_copy = new ArrayList<String>();
						ArrayList<String> q_copy = new ArrayList<String>();
						
						g_copy.addAll(grant_set);
						q_copy.addAll(quorum);

						//Collections.copy(g_copy, grant_set);
						//Collections.copy(q_copy, quorum);
						Collections.sort(g_copy);
						Collections.sort(q_copy);
						
						if (q_copy.equals(g_copy)) { // check- grant message is
										// received from all the
														// quorums
							//System.out.println("cs");
							System.out.println("cs "+id+" "+request);//````````
							cs(); // critical section
							grant_set.clear();
							failed=false;
							//System.out.println(id+" req "+request);
							//int a=Node.getClock();
							for (String i : all_nodes) {
								if (quorum.contains(i.split("\\s+")[0])) {
									String host = i.split("\\s+")[1];
									String port = i.split("\\s+")[2];
									Client_node c = new Client_node(quorum, all_nodes, id);
									Node.setClock(Node.getClock()+1);
									c.connect(host, port, "r " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
								}
							}
							g_copy.clear();
							q_copy.clear();
							/*long t = System.currentTimeMillis();
							while (System.currentTimeMillis() < t + 1000) { //@@@@
								
							}*/
							Node.setRelease(false);// start next set of requests
							
						}
					}

				}

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		try {
			// outfile();
			soc_server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;

	}

}
