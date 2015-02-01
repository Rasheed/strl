package com.strl.routing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.RoutingAlgorithm;
import com.graphhopper.routing.RoutingAlgorithmFactorySimple;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.ShortestWeighting;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.Helper;
import com.strl.util.StrlWeighting;
import com.strl.util.WalkabilityWeighting;

public class WalkabilityRoutingTest {

	private FlagEncoder footEncoder;

	EncodingManager createEncodingManager(boolean restrictedOnly) {
		EncodingManager encodingManager = new EncodingManager("FOOT", 4);
		footEncoder = encodingManager.getEncoder("FOOT");
		return encodingManager;
	}

	// 0---1
	// | /
	// 2--3--4
	// | | |
	// 5--6--7
	public static void initGraph(Graph g) {
		g.edge(0, 1, 6, true).setWalkability(10.0);
		g.edge(0, 2, 4, true).setWalkability(6.0);
		g.edge(1, 3, 2, true).setWalkability(5.0);
		g.edge(2, 3, 2, true).setWalkability(7.0);
		g.edge(3, 4, 7, true).setWalkability(8.0);
		g.edge(2, 5, 10, true).setWalkability(5.0);
		g.edge(3, 6, 12, true).setWalkability(3.0);
		g.edge(4, 7, 9, true).setWalkability(9.0);
		g.edge(5, 6, 3, true).setWalkability(3.0);
		g.edge(6, 7, 6, true).setWalkability(4.0);
	}

	protected GraphStorage createGraph(EncodingManager em) {
		return new GraphBuilder(em).create();
	}

	public RoutingAlgorithm createAlgo(Graph g, AlgorithmOptions opts) {
		return new RoutingAlgorithmFactorySimple().createAlgo(g, opts);
	}

	//@Test
	public void testBasicWalkabilityWeightingPath() {
		GraphStorage g = createGraph(createEncodingManager(true));
		initGraph(g);
		Path p = createAlgo(
				g,
				new AlgorithmOptions()
						.setWeighting(new WalkabilityWeighting(footEncoder))
						.setAlgorithm(AlgorithmOptions.DIJKSTRA_BI)
						.setFlagEncoder(footEncoder)
						.setTraversalMode(TraversalMode.EDGE_BASED_2DIR))
				.calcPath(0, 7);
		assertEquals(Helper.createTList(0,1,3,4,7), p.calcNodes());
	}
	
	@Test
	public void testBasicStrlWeightingPath() {
		GraphStorage g = createGraph(createEncodingManager(true));
		initGraph(g);
		Path p = createAlgo(
				g,
				new AlgorithmOptions()
						.setWeighting(new StrlWeighting(footEncoder))
						.setAlgorithm(AlgorithmOptions.DIJKSTRA_BI)
						.setFlagEncoder(footEncoder)
						.setTraversalMode(TraversalMode.EDGE_BASED_2DIR))
				.calcPath(0, 7);
		assertEquals(Helper.createTList(0,1,3,4,7), p.calcNodes());
	}
	
}
