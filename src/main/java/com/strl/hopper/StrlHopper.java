package com.strl.hopper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.RoutingAlgorithm;
import com.graphhopper.routing.StrlRoutingAlgorithm;
import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.routing.util.WeightingMap;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.shapes.GHPoint;
import com.strl.util.WalkabilityWeighting;

public class StrlHopper extends GraphHopper{
	public StrlHopper() {
    	this.forServer().
        		setOSMFile("src/main/resources/central-strl.xml")
        		.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
        		.setInMemory()
        		.init(new CmdArgs());
        this.importOrLoad();
   }
	
	@Override
	public Weighting createWeighting(WeightingMap wMap, FlagEncoder encoder) {
		return new WalkabilityWeighting(encoder, 0.5, 0.5);
	}
	
	 protected List<Path> getPaths( GHRequest request, GHResponse rsp )
	    {
	        String vehicle = request.getVehicle();
	        if (vehicle.isEmpty())
	            vehicle = "foot";

	        if (!encodingManager.supports(vehicle))
	        {
	            rsp.addError(new IllegalArgumentException("Vehicle " + vehicle + " unsupported. "
	                    + "Supported are: " + getEncodingManager()));
	            return Collections.emptyList();
	        }

	        TraversalMode tMode;
	        String tModeStr = request.getHints().get("traversal_mode", traversalMode.toString());
	        try
	        {
	            tMode = TraversalMode.fromString(tModeStr);
	        } catch (Exception ex)
	        {
	            rsp.addError(ex);
	            return Collections.emptyList();
	        }

	        List<GHPoint> points = request.getPoints();
	        if (points.size() < 2)
	        {
	            rsp.addError(new IllegalStateException("At least 2 points has to be specified, but was:" + points.size()));
	            return Collections.emptyList();
	        }

	        visitedSum.set(0);

	        FlagEncoder encoder = encodingManager.getEncoder(vehicle);
	        EdgeFilter edgeFilter = new DefaultEdgeFilter(encoder);
	        Weighting weighting = new WalkabilityWeighting(encoder, 0.5, 2);

	        StopWatch sw = new StopWatch().start();
	        List<QueryResult> qResults = new ArrayList<QueryResult>(points.size());
	        for (int placeIndex = 0; placeIndex < points.size(); placeIndex++)
	        {
	            GHPoint point = points.get(placeIndex);
	            QueryResult res = locationIndex.findClosest(point.lat, point.lon, edgeFilter);
	            if (!res.isValid())
	                rsp.addError(new IllegalArgumentException("Cannot find point " + placeIndex + ": " + point));

	            qResults.add(res);
	        }

	        if (rsp.hasErrors())
	            return Collections.emptyList();

	        String debug = "idLookup:" + sw.stop().getSeconds() + "s";
	        QueryGraph qGraph = new QueryGraph(graph);
	        qGraph.lookup(qResults);

	        List<Path> paths = new ArrayList<Path>(points.size() - 1);
	        QueryResult fromQResult = qResults.get(0);
	        for (int placeIndex = 1; placeIndex < points.size(); placeIndex++)
	        {
	            QueryResult toQResult = qResults.get(placeIndex);
	            sw = new StopWatch().start();
	            RoutingAlgorithm algo = new StrlRoutingAlgorithm(qGraph, encoder, weighting, tMode);
	            debug += ", algoInit:" + sw.stop().getSeconds() + "s";

	            sw = new StopWatch().start();
	            Path path = algo.calcPath(fromQResult.getClosestNode(), toQResult.getClosestNode());
	            if (path.getMillis() < 0)
	                throw new RuntimeException("Time was negative. Please report as bug and include:" + request);

	            paths.add(path);
	            debug += ", " + algo.getName() + "-routing:" + sw.stop().getSeconds() + "s, " + path.getDebugInfo();

	            visitedSum.addAndGet(algo.getVisitedNodes());
	            fromQResult = toQResult;
	        }

	        if (rsp.hasErrors())
	            return Collections.emptyList();

	        if (points.size() - 1 != paths.size())
	            throw new RuntimeException("There should be exactly one more places than paths. places:" + points.size() + ", paths:" + paths.size());

	        rsp.setDebugInfo(debug);
	        return paths;
	    }
}
