package com.example;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.strl.hopper.StrlHopper;

/**
 *
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String webappDirLocation = "src/main/webapp/";

		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		
		Server server = new Server(Integer.valueOf(webPort));
		WebAppContext root = new WebAppContext();

		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);

		// Parent loader priority is a class loader setting that Jetty accepts.
		// By default Jetty will behave like most web containers in that it will
		// allow your application to replace non-server libraries that are part
		// of the
		// container. Setting parent loader priority to true changes this
		// behavior.
		// Read more here:
		// http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);

    	StrlHopper hopper = new StrlHopper();
		
		/*GHResponse response = hopper.route(new GHRequest(51.524559, -0.13404, 51.500729, -0.124625)
				.setVehicle("foot"));
        		
		System.out.println(response.getPoints());
        
		System.out.println(path.getJsonObject());

		server.setHandler(root);

		server.start();
		server.join();*/
    	printGraph(hopper.getGraph());
	}
	
	public static void printGraph(GraphStorage graph) {
		System.out.println(graph.toDetailsString()+"\n");

		AllEdgesIterator edges = graph.getAllEdges();
		FileWriter writer = null;
		try {
			writer = new FileWriter("normaliseddistanceslog.csv");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int i = 0;
		double min = 0.000161410286871274;
		double max = 1.003009027;
		while (edges.next()) {
			try
			{
		 
				double oneoverd = 1/edges.getDistance();
				
				double normalised = Math.log(oneoverd) - Math.log(max) / Math.log(max - min);
				
			    writer.append(String.valueOf(normalised));
			    writer.append('\n');
			    
			}
			catch(IOException e)
			{
			     e.printStackTrace();
			} 
		}
		
	    try {
			writer.flush();
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			System.out.println("finished to file");		
			/*while (edges.next()) {
			int basenode = edges.getBaseNode();
			int adjnode = edges.getAdjNode();
			
			System.out.println(basenode +" " +adjnode + " walk "+ edges.getWalkability() + " distance " + edges.getDistance());
			System.out.print("From node: "+ basenode+ " Geo "+na.getLat(basenode) +", " + na.getLon(basenode) );
			System.out.print(" To node " +adjnode + " Geo " + +na.getLat(adjnode) +", " + na.getLon(adjnode) );

			System.out.println("\n"+edges.getWalkability());
		}*/
	}
}
