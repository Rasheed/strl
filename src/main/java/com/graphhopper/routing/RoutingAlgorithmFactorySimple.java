/*
 * Copyright 2014 Peter Karich.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.routing;

import com.graphhopper.storage.Graph;

/**
 * A simple factory creating normal algorithms (RoutingAlgorithm) without preparation.
 * <p>
 * @author Peter Karich
 */
public class RoutingAlgorithmFactorySimple implements RoutingAlgorithmFactory
{
    @Override
    public RoutingAlgorithm createAlgo( Graph g, AlgorithmOptions opts )
    {
        AbstractRoutingAlgorithm algo;
        String algoStr = opts.getAlgorithm();
        if (AlgorithmOptions.DIJKSTRA_BI.equalsIgnoreCase(algoStr))
        {
            algo = new DijkstraBidirectionRef(g, opts.getFlagEncoder(), opts.getWeighting(), opts.getTraversalMode());
        } else if (AlgorithmOptions.DIJKSTRA.equalsIgnoreCase(algoStr))
        {
            algo = new Dijkstra(g, opts.getFlagEncoder(), opts.getWeighting(), opts.getTraversalMode());
        } else if (AlgorithmOptions.ASTAR_BI.equalsIgnoreCase(algoStr))
        {
            algo = new AStarBidirection(g, opts.getFlagEncoder(), opts.getWeighting(), opts.getTraversalMode()).
                    setApproximation(opts.getHints().getBool(AlgorithmOptions.ASTAR_BI + ".approximation", false)).
                    setApproximationFactor(opts.getHints().getDouble(AlgorithmOptions.ASTAR_BI + ".approximation_factor", 1.2));

        } else if (AlgorithmOptions.DIJKSTRA_ONE_TO_MANY.equalsIgnoreCase(algoStr))
        {
            algo = new DijkstraOneToMany(g, opts.getFlagEncoder(), opts.getWeighting(), opts.getTraversalMode());
        } else
        {
            algo = new AStar(g, opts.getFlagEncoder(), opts.getWeighting(), opts.getTraversalMode());
        }

        return algo;
    }
}
