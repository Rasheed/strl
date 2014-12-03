package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;

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

		GraphHopper hopper = new GraphHopper().forServer()
				.setOSMFile("src/main/resources/test-osm2.xml")
				.setStoreOnFlush(false)
				.setEncodingManager(new EncodingManager("CAR,FOOT"))
				.setCHEnable(false).init(new CmdArgs());

		hopper.importOrLoad();

		/*System.out.println(hopper.getGraph().toDetailsString());

		System.out.println(hopper.getGraph().getAllEdges());

		AllEdgesIterator edges = hopper.getGraph().getAllEdges();

		while (edges.next()) {
			System.out.println(edges);
		}*/

        GHResponse response = hopper.route(new GHRequest(52.0, 9.0, 41.2, 10.431).setVehicle("car"));
        
        System.out.println(response.getPoints());
        
		// System.out.println(path.getJsonObject());

		// server.setHandler(root);

		// server.start();
		// server.join();
	}
}
