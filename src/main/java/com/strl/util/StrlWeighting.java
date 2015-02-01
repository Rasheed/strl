/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.strl.util;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.EdgeIteratorState;

/**
 * Calculates the most walkable route with the specified vehicle (VehicleEncoder). Calculates the weight
 * in seconds.
 * <p/>
 * @author Rasheed Wihaib
 */
public class StrlWeighting implements Weighting
{
    /*
     * Converting to seconds is not necessary but makes adding other penalities easier (e.g. turn
     * costs or traffic light costs etc)
     */
    protected final static double SPEED_CONV = 3.6;
    protected final FlagEncoder encoder;
    private final double maxSpeed;
	private final double MAX_WALKABILITY = 4.62;
	private final double MAX_DISTANCE = 4413.508;

    public StrlWeighting( FlagEncoder encoder )
    {
        this.encoder = encoder;
        maxSpeed = encoder.getMaxSpeed() * SPEED_CONV;
    }

    @Override
    public double getMinWeight( double distance )
    {
    	System.out.println(distance);
        return distance / maxSpeed;
    }

    @Override
    public double calcWeight( EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId )
    {        
    	double speed = reverse ? encoder.getReverseSpeed(edge.getFlags()) : encoder.getSpeed(edge.getFlags());
        if (speed == 0) {
            return Double.POSITIVE_INFINITY;       
        }        

        double walkability = ((edge.getWalkability()/100)/MAX_WALKABILITY) * 100; 
        
        if(walkability < 0 ) {
        	walkability = 0;
        }
        
        //System.out.println("walkability percent" + walkability);
        double dist = ((edge.getDistance())/MAX_DISTANCE) * 100;
        
        //System.out.println("dist percent : "+dist);
        double weight = (100 - (walkability)) + (dist);
        
        //System.out.println("weight " + weight);
        return weight;
    }

    @Override
    public String toString()
    {
        return "STRL|" + encoder;
    }
}
