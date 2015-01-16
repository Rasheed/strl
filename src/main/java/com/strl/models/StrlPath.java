package com.strl.models;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.PointList;

public class StrlPath {
	private JSONObject json = new JSONObject();
	private PointList fastestroute;
	private double shortestdistance;

	public StrlPath(GHResponse response) {
		try {
			json = writeJson(response);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJsonObject() {
		return json;
	}

	private JSONObject writeJson(GHResponse rsp) throws JSONException,
			IOException {

		json.append("geopoints", rsp.getPoints());
		json.append("routedistance", rsp.getDistance());
		//json.append("walkability", rsp.getWalkability());

		return json;
	}

	public StrlPath computeStandardRoute(Double fromLat, Double fromLon,
			Double toLat, Double toLon) {
		GraphHopper hopper = new GraphHopper().forServer()
				.setOSMFile("src/main/resources/test-osm.xml")
				.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
				.init(new CmdArgs());
		hopper.importOrLoad();

		GHResponse response = hopper.route(new GHRequest(fromLat, fromLon,
				toLat, toLon).setVehicle("foot"));
		
		try {
			json.append("shortestroute", response.getPoints());
			json.append("shortestdistance", response.getDistance());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return this;
	}
}
