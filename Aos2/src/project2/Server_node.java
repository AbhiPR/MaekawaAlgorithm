package project2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Server_node implements Runnable {
	private static int id;
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
//!!!!!!!!!!!!!
	public static void compareClk(String w){
		ArrayList<Integer> y=Node.getvClock();
		String[] x=w.split(", ");
		for(int z=0;z<y.size();z++)
			y.set(z,Math.max(Integer.parseInt(x[z]),y.get(z)));
		
		y.set(id, y.get(id)+1);
		Node.setvClock(y);
	}
//!!!!!!!!!!!!
	
	public static void setRec(boolean r)
	{
		rec=r;
		
	}

	public void cs() throws IOException,FileNotFoundException, UnsupportedEncodingException {
		long s = System.currentTimeMillis();
		
		//count++;
		/*
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output.txt"),true))) {
					    //bw.write("c="+count+" "+id+" "+Node.getClock()+"\n");
					    bw.write(id+" Enter "+Node.getClock()+"\n");
					    while (System.currentTimeMillis() < s + cstime) {
			// inter-request delay
			
		}
						
					    bw.write(id+" Exit"+Node.getClock()+"\n");
					    bw.close();
					    }catch (IOException ex) {
					    
					    }*/
			
	Writer writer;
		FileOutputStream FoutStream=new FileOutputStream("output-"+id+".txt", true);
		try{		
        
                              writer = new BufferedWriter(new OutputStreamWriter(FoutStream, "UTF-8"));
                              writer.append(id+" Enter "+Node.getvClock().toString()+"\n");
			      while (System.currentTimeMillis() < s + cstime) {
			// inter-request delay
			
		}


                              writer.close();        
      }catch(IOException ioe){ioe.printStackTrace();}finally {FoutStream.close();}

//~~~Testing
		/*Writer writer;
		
		Socket c = new Socket("dc43.utdallas.edu",2000); //determined by testing config
		PrintWriter output = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));
		//output.print(String.valueOf(id)+" "+String.valueOf(cstime)+"\r\n");
		output.print(id+" Enter "+Node.getClock()+" "+cstime+"\n");		
		output.flush();
		c.close();*/
		//~~~	
	
	

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
					//request.add(s.split("\\s+", 2)[1]);
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));

					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					request.add(s.split("\\s+")[1]+" "+vc.split(", ")[Integer.parseInt(s.split("\\s+")[1])]);
					compareClk(vc); 				
					//!!!!!!!!!!

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
								//Node.setClock(Node.getClock()+1);
								//c.connect(host, port, "g " + id+" "+ String.valueOf(Node.getClock())); // add clock
								//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "g " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!									
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
									//Node.setClock(Node.getClock()+1);
									//c.connect(host, port, "f " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
									//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "f " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!										

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
									//Node.setClock(Node.getClock()+1);
									//c.connect(host, port, "i " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
									
									//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "i " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!
									break;
								}
							}
						}

					}

				} else if (s.split("\\s+")[0].equals("i")) { // inquire
					System.out.println(id+" "+s);//`````
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));
					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					
					compareClk(vc); 				
					//!!!!!!!!!!
					if (failed) {
						for (String i : all_nodes) {
							if (i.split("\\s+")[0].equals(s.split("\\s+")[1])) {
								String host = i.split("\\s+")[1];
								String port = i.split("\\s+")[2];
								Client_node c = new Client_node(quorum, all_nodes, id);
								//Node.setClock(Node.getClock()+1);
								//c.connect(host, port, "y " + id+" "+String.valueOf(Node.getClock())); // add clock
								//yield = true;
								//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "y " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!
								break;
							}
						}
					}

				} else if (s.split("\\s+")[0].equals("f")) { // failed
					System.out.println(id+" "+s);//```
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));

					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					
					compareClk(vc); 				
					//!!!!!!!!!!
					failed = true;
				}

				else if (s.split("\\s+")[0].equals("y")) { // yield
					System.out.println(id+" "+s);//```
					
					String top_req = request.element();
					grant=top_req; //#############
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));
					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					
					compareClk(vc); 				
					//!!!!!!!!!!	
					for (String i : all_nodes) {
						if (i.split("\\s+")[0].equals(top_req.split("\\s+")[0])) {
							String host = i.split("\\s+")[1];
							String port = i.split("\\s+")[2];
							Client_node c = new Client_node(quorum, all_nodes, id);
							//Node.setClock(Node.getClock()+1);
							//c.connect(host, port, "g " + id+" "+String.valueOf(Node.getClock())); // add clock
							

							//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "g " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!
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
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));
					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					
					compareClk(vc); 				
					//!!!!!!!!!!
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
								//Node.setClock(Node.getClock()+1);
								//c.connect(host, port, "g " + id+" "+String.valueOf(Node.getClock())); // add clock
								//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "g " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!
								break;
							}
						}
					}

				} else if (s.split("\\s+")[0].equals("g")) { // grant
					System.out.println(id+" "+s+" "+request);//`````
					//compareClk(Integer.parseInt(s.split("\\s+")[2]));
					//!!!!!!!!!!!	
					String vc=s.split("\\s+",3)[2].replace("]", "").replace("[", "");
					
					compareClk(vc); 				
					//!!!!!!!!!!
										
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
									//Node.setClock(Node.getClock()+1);
									//c.connect(host, port, "r " + id+" "+String.valueOf(Node.getClock())); // add
																		// clock
								//!!!!!!!!!!!!
								ArrayList<Integer> cv=Node.getvClock();
								cv.set(id, cv.get(id)+1);
								Node.setvClock(cv);
								c.connect(host, port, "r " + id+" "+ Node.getvClock().toString());
								//!!!!!!!!!!!								
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
