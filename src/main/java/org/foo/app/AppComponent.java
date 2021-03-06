/*
 * Copyright 2021-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.foo.app;

import org.onosproject.cfg.ComponentConfigService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Own import statements
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.behaviour.DeviceCpuStats;

import java.util.Iterator;
import java.util.List;
// Own import statements

import java.util.Dictionary;
import java.util.Properties;

import static org.onlab.util.Tools.get;

/**
 * Skeletal ONOS application component.
 */@Component(immediate = true,
           service = {SomeInterface.class},
           property = {
               "someProperty=Some Default String Value",
           })
public class AppComponent implements SomeInterface {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Some configurable property. */
    private String someProperty;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ComponentConfigService cfgService; 

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Activate
    protected void activate() {
        cfgService.registerProperties(getClass());
        log.info("Hello World: starting app");
	
	if(deviceService == null) {
	    log.info("DeviceService is null");

	    DeviceCpuStats stats = new DeviceCpuStats();
	    
	    if(stats != null) {
		log.info("DeviceCpuStats: " + stats.getUsed());
	    } else {
		log.info("DeviceCpuStats is null");
	    }
		
	    return;
	}

	Iterable<Device> devices = deviceService.getDevices();

	if(!devices.iterator().hasNext()) {
	    log.info("Empty list of devices");

	    return;
	}

	log.info("There are devices");

	for(Device d : devices) {
	    log.info("Device ID: " + d.id().toString());

	    List<Port> ports = deviceService.getPorts(d.id());
	    for(Port p : ports) {
	    	log.info("Getting info for port " + p.number());
		
		PortStatistics portStat = deviceService.getStatisticsForPort(d.id(), p.number());
		PortStatistics portDeltaStat = deviceService.getDeltaStatisticsForPort(d.id(), p.number());

		if(portStat != null) {
		    log.info("portStat bytes received: " + portStat.bytesReceived());
		} else {
		    log.info("Unable to read portStat");
		}

		if(portDeltaStat != null) {
		    log.info("portDeltaStat bytes received: " + portDeltaStat.bytesReceived());
		} else {
		    log.info("Unable to read portDeltaStat");
		}
	    }
	}
    }

    @Deactivate
    protected void deactivate() {
        cfgService.unregisterProperties(getClass(), false);
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary<?, ?> properties = context != null ? context.getProperties() : new Properties();
        if (context != null) {
            someProperty = get(properties, "someProperty");
        }
        log.info("Reconfigured");
    }

    @Override
    public void someMethod() {
        log.info("Invoked");
    }

}
