package com.strl.hopper;

import java.util.List;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.routing.util.WeightingMap;
import com.graphhopper.util.CmdArgs;
import com.strl.util.StrlWeighting;
import com.strl.util.WalkabilityWeighting;

public class StrlHopper extends GraphHopper{
	
	String weighting = "";
	
	public StrlHopper(String weighting) {
		this.weighting = weighting;
		String filePath = "src/main/resources/centrallondon" + weighting + ".xml";	
    	this.forServer().
        		setOSMFile(filePath)
        		.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
        		.setInMemory()
        		.init(new CmdArgs());
        this.importOrLoad();
        
   }
	
	@Override
	public Weighting createWeighting(WeightingMap wMap, FlagEncoder encoder) {
		if(weighting.equalsIgnoreCase("walkability")) {
			return new WalkabilityWeighting(encoder);
		} else {
			return new StrlWeighting(encoder);
		}
	}
	
	@Override
	protected List<Path> getPaths( GHRequest request, GHResponse rsp ) {
		return super.getPaths(request.setWeighting(this.weighting), rsp);
	}
	
}
