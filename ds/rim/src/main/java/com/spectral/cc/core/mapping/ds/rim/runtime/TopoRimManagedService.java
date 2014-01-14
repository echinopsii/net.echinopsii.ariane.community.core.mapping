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
    private static Dictionary<Object, Object> config = null;

    private final BundleContext bundleContext;
    private ServiceRegistration topoSceRegistration = null;
    private boolean isStarted = false;

    public TopoRimManagedService(BundleContext context) {
        this.bundleContext = context;
    }

    private void start() {
        try {
            log.info("Loading configuration : {}", new Object[]{config.toString()});
            TopoDSCfgLoader.load(config);
        } catch (IOException e) {
            log.error("Error while loading {} configuration ! Check following root cause :", new Object[]{TOPO_DS_SERVICE_NAME});
            e.printStackTrace();
            return;
        }

        try {
            log.info("Starting {} runtime ...", new Object[]{TOPO_DS_SERVICE_NAME});
            if (TopoRIMRuntime.start(config)) {
                log.info("Registring service {} ...",TOPO_DS_SERVICE_NAME);
                topoSceRegistration = bundleContext.registerService(TopoSce.class.getName(), TopoRIMRuntime.getTopoSce(), null);
                isStarted = true;
            }
        } catch (ClassNotFoundException | InstantiationException
                         | IllegalAccessException | IOException e) {
            log.error("Error while starting and/or registring {} ! Check following root cause : ",TOPO_DS_SERVICE_NAME);
            e.printStackTrace();
        }
    }

    @Validate
    public void validate() throws InterruptedException {
        log.info("{} is starting...", new Object[]{TOPO_DS_SERVICE_NAME});
        while(config==null) {
            log.warn("Config is missing for {}. Sleep some times...", TOPO_DS_SERVICE_NAME);
            Thread.sleep(10);
        }
        start();
        log.info("{} is started...", new Object[]{TOPO_DS_SERVICE_NAME});
    }

    private void stop() {
        if (topoSceRegistration!=null) {
            log.info("Unregister TopoSce Service...");
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
    public void updated(final Dictionary properties) {
        log.info("{} is being updated by {}", new Object[]{TOPO_DS_SERVICE_NAME, Thread.currentThread().toString()});
        if (TopoDSCfgLoader.isValid(properties)) {
            config = properties;
            if (isStarted) {
                final Runnable applyConfigUpdate = new Runnable() {
                    @Override
                    public void run() {
                        log.info("{} will be restart to apply configuration changes...",TOPO_DS_SERVICE_NAME);
                        stop();
                        start();
                    }
                };
                new Thread(applyConfigUpdate).start();
            }
        }
    }
}
