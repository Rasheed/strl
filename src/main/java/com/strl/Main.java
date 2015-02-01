package com.strl;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.DistanceCalc;
import com.strl.hopper.StrlHopper;
import com.strl.reader.WalkabilityReader;

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
		StrlHopper hopper = new StrlHopper("strl");
		GHResponse response = hopper.route(new GHRequest(51.51606049455287,-0.1336812973022461,51.514384661317756,-0.13728618621826172));
        		
		System.out.println(response);        
    	printGraph(hopper.getGraph());

        server.setHandler(root);

		server.start();
		server.join();
	}
	
	public static void printGraph(GraphStorage graph) {
		System.out.println(graph.toDetailsString()+"\n");

		AllEdgesIterator edges = graph.getAllEdges();
		double maxwalk = 0;
		double maxdistance = 0;
		while (edges.next()) {
			int basenode = edges.getBaseNode();
			int adjnode = edges.getAdjNode();
			if(edges.getWalkability() > maxwalk) {
			//System.out.println(basenode +" " +adjnode + " walk "+ edges.getWalkability() + " distance " + edges.getDistance());
			maxwalk = edges.getWalkability();
			}
			if(edges.getDistance() > maxdistance) {
				maxdistance = edges.getDistance();
			}
			//System.out.print("From node: "+ basenode+ " Geo "+na.getLat(basenode) +", " + na.getLon(basenode) );
			//System.out.print(" To node " +adjnode + " Geo " + +na.getLat(adjnode) +", " + na.getLon(adjnode) );

			//System.out.println("\n"+edges.getWalkability());
		}
		System.out.println(maxwalk);
		System.out.println("dist" + maxdistance);
	}
}
