package com.strl.services;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;
import com.strl.hopper.StrlHopper;
import com.strl.models.StrlPath;

@Path("/path")
@Produces(MediaType.APPLICATION_JSON)
public class PathService {
	
	@GET
	@Path("strl/{fromLat}/{fromLon}/{toLat}/{toLon}")
	public List<Double[]> getStrlPath(
			@PathParam("fromLat") Double fromLat,
			@PathParam("fromLon") Double fromLon, 
			@PathParam("toLat") Double toLat,
			@PathParam("toLon") Double toLon) {
		StrlHopper hopper = new StrlHopper("strl");
        GHResponse response = hopper.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return response.getPoints().toGeoJson();
    }
	
	@GET
	@Path("walkable/{fromLat}/{fromLon}/{toLat}/{toLon}")
	public List<Double[]> getWalkableRoute(
			@PathParam("fromLat") Double fromLat,
			@PathParam("fromLon") Double fromLon, 
			@PathParam("toLat") Double toLat,
			@PathParam("toLon") Double toLon){
		StrlHopper hopper = new StrlHopper("walkability");
        GHResponse response = hopper.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return response.getPoints().toGeoJson();
    }
	
	@GET
	@Path("{fromLat}/{fromLon}/{toLat}/{toLon}")
	public List<Double[]> getPath(
			@PathParam("fromLat") Double fromLat,
			@PathParam("fromLon") Double fromLon, 
			@PathParam("toLat") Double toLat,
			@PathParam("toLon") Double toLon) {
    	GraphHopper hopper = new GraphHopper().forServer().
        		setOSMFile("src/main/resources/centrallondon.osm.xml")
        		.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
        		.init(new CmdArgs());
        hopper.importOrLoad();

        GHResponse response = hopper.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return response.getPoints().toGeoJson();
    }
	
	@GET
	@Path("all/{fromLat}/{fromLon}/{toLat}/{toLon}")
	public StrlPath getAllPaths(
			@PathParam("fromLat") Double fromLat,
			@PathParam("fromLon") Double fromLon, 
			@PathParam("toLat") Double toLat,
			@PathParam("toLon") Double toLon) {
		
		return new StrlPath(fromLat, fromLon, toLat, toLon);
    }
	
	@GET
	@Path("instructions/{fromLat}/{fromLon}/{toLat}/{toLon}")
	public List<Map<String, Object>> getInstructions(
			@PathParam("fromLat") Double fromLat,
			@PathParam("fromLon") Double fromLon, 
			@PathParam("toLat") Double toLat,
			@PathParam("toLon") Double toLon) {
    	GraphHopper hopper = new GraphHopper().forServer().
        		setOSMFile("src/main/resources/central.xml")
        		.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
        		.init(new CmdArgs());
        hopper.importOrLoad();

        //GHResponse response = hopper.route(new GHRequest(51.524559, -0.13404, 51.500729, -0.124625));
        GHResponse response = hopper.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return response.getInstructions().createJson();
    }
	
	
}
