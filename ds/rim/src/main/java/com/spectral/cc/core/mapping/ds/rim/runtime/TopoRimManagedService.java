/**
 * Mapping RIM managed service
 * provide a Mapping DS Web Service, REST Service and Memory Service
 *
 * Copyright (C) 2013  Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spectral.cc.core.mapping.ds.rim.runtime;

import com.spectral.cc.core.mapping.ds.rim.cfg.TopoDSCfgLoader;
import com.spectral.cc.core.mapping.ds.service.TopoSce;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Updated;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

@Component(managedservice="com.spectral.cc.core.mapping.ds.rim.runtime.TopoRimManagedService")
@Instantiate
public class TopoRimManagedService {

    private static final Logger log = LoggerFactory.getLogger(TopoRimManagedService.class);
    private static final String TOPO_DS_SERVICE_NAME = "Mapping DS RIM service";
    private ServiceRegistration topoSceRegistration = null;

    private final BundleContext bundleContext;

    public TopoRimManagedService(BundleContext context) {
        this.bundleContext = context;
    }

    @Validate
    public void validate() {
        log.debug("{} is started...", new Object[]{TOPO_DS_SERVICE_NAME});
    }

    private void stop() {
        if (topoSceRegistration!=null) {
            log.debug("Unregister TopoSce Service...");
            topoSceRegistration.unregister();
        }
        TopoRIMRuntime.stop();
    }

    @Invalidate
    public void invalidate() {
        log.info("Stopping " + TOPO_DS_SERVICE_NAME);
        stop();
        log.info(TOPO_DS_SERVICE_NAME + " has been succesfully stopped");
    }

    @Updated
    public synchronized void updated(Dictionary properties) {
        if (!Thread.currentThread().toString().contains("iPOJO")) {
            log.warn("Another thread ({}) than iPOJO tries to get RIM updated ...", new Object[]{Thread.currentThread().toString()});
            return;
        }
        log.debug("{} is being updated by {}", new Object[]{TOPO_DS_SERVICE_NAME, Thread.currentThread().toString()});
        if (properties != null) {
            stop();
            try {
                log.debug("Loading configuration : {}", new Object[]{properties.toString()});
                TopoDSCfgLoader.load(properties);
            } catch (IOException e) {
                log.error("Error while loading {} configuration ! Check following root cause :", new Object[]{TOPO_DS_SERVICE_NAME});
                e.printStackTrace();
                return;
            }
            try {
                log.debug("Starting {} runtime ...", new Object[]{TOPO_DS_SERVICE_NAME});
                if (TopoRIMRuntime.start(properties)) {
                    log.debug("Registring OSGI {} ...",TOPO_DS_SERVICE_NAME);
                    this.topoSceRegistration = this.bundleContext.registerService(TopoSce.class.getName(), TopoRIMRuntime.getTopoSce(), null);
                    log.debug("{} has been succesfully updated...",TOPO_DS_SERVICE_NAME);
                }
            } catch (ClassNotFoundException | InstantiationException
                             | IllegalAccessException | IOException e) {
                log.error("Error while starting and/or registring {} ! Check following root cause : ",TOPO_DS_SERVICE_NAME);
                e.printStackTrace();
            }
        } else {
            log.error("Configuration error for service pid {}. NULL dictionnary...", new Object[]{TopoRimManagedService.class.getName()});
        }
    }
}
