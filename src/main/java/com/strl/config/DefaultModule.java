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
package com.strl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.TranslationMap;

/**
 * @author Peter Karich
 */
public class DefaultModule extends AbstractModule
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultModule()
    {
    }

    @Override
    protected void configure()
    {
        try
        {
        	
        	GraphHopper hopper = new GraphHopper().forServer().
            		setOSMFile("src/main/resources/central.xml")
            		.setEncodingManager(new EncodingManager(EncodingManager.FOOT))
            		.init(new CmdArgs());
        	hopper.importOrLoad();
        	
            logger.info("loaded graph at:" + hopper.getGraphHopperLocation()
                    + ", source:" + hopper.getOSMFile()
                    + ", acceptWay:" + hopper.getEncodingManager()
                    + ", class:" + hopper.getGraph().getClass().getSimpleName());

            bind(GraphHopper.class).toInstance(hopper);

            bind(TranslationMap.class).toInstance(hopper.getTranslationMap());
        } catch (Exception ex)
        {
            throw new IllegalStateException("Couldn't load graph", ex);
        }
    }
}
