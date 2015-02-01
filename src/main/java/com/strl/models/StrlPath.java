package com.strl.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;
import com.strl.hopper.StrlHopper;

@XmlRootElement
public class StrlPath {
	private List<Double[]> fastestroutepoints;
	private List<Double[]> walkableroutepoints;
	private List<Double[]> strlroutepoints;

	public StrlPath() {}
	
	public StrlPath(Double fromLat, Double fromLon, Double toLat, Double toLon) {
		this.fastestroutepoints = getFastestRoute(fromLat, fromLon, toLat,
				toLon);
		this.walkableroutepoints = getWalkableRoute(fromLat, fromLon, toLat,
				toLon);
		this.strlroutepoints = getStrlRoute(fromLat, fromLon, toLat, toLon);
	}

	private List<Double[]> getStrlRoute(Double fromLat, Double fromLon,
			Double toLat, Double toLon) {
		StrlHopper hopper = new StrlHopper("strl");
		GHResponse response = hopper.route(new GHRequest(fromLat, fromLon,
				toLat, toLon).setVehicle("foot"));
		return response.getPoints().toGeoJson();
	}

	private List<Double[]> getWalkableRoute(Double fromLat, Double fromLon,
			Double toLat, Double toLon) {
		StrlHopper hopper = new StrlHopper("walkability");
		GHResponse response = hopper.route(new GHRequest(fromLat, fromLon,
				toLat, toLon).setVehicle("foot"));
		return response.getPoints().toGeoJson();
	}

	private List<Double[]> getFastestRoute(Double fromLat, Double fromLon,
			Double toLat, Double toLon) {
		GraphHopper hopper = new GraphHopper().forServer()
				.setOSMFile("src/main/resources/centrallondon.osm.xml")
				.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
				.init(new CmdArgs());
		hopper.importOrLoad();

		GHResponse response = hopper.route(new GHRequest(fromLat, fromLon,
				toLat, toLon).setVehicle("foot"));
		return response.getPoints().toGeoJson();
	}
	
	public List<Double[]> getFastestroutepoints() {
		return fastestroutepoints;
	}
	public List<Double[]> getWalkableroutepoints() {
		return walkableroutepoints;
	}
	public List<Double[]> getStrlroutepoints() {
		return strlroutepoints;
	}
}
