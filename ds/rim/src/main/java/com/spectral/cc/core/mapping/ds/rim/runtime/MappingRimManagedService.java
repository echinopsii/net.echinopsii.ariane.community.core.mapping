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

import com.spectral.cc.core.mapping.ds.rim.cfg.MappingDSCfgLoader;
import com.spectral.cc.core.mapping.ds.service.MappingSce;
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

@Component(managedservice="com.spectral.cc.core.mapping.ds.rim.runtime.MappingRimManagedService")
@Instantiate
public class MappingRimManagedService {

    private static final Logger log = LoggerFactory.getLogger(MappingRimManagedService.class);
    private static final String MAPPING_DS_SERVICE_NAME = "CC Mapping DS";
    private static Dictionary<Object, Object> config = null;

    private final BundleContext bundleContext;
    private ServiceRegistration mappingSceRegistration = null;
    private boolean isStarted = false;

    public MappingRimManagedService(BundleContext context) {
        this.bundleContext = context;
    }

    private void start() {
        try {
            log.debug("Loading configuration : {}", new Object[]{config.toString()});
            MappingDSCfgLoader.load(config);
        } catch (IOException e) {
            log.error("Error while loading {} configuration ! Check following root cause :", new Object[]{MAPPING_DS_SERVICE_NAME});
            e.printStackTrace();
            return;
        }

        try {
            if (MappingRIMRuntime.start(config)) {
                log.debug("Registring service {} ...", MAPPING_DS_SERVICE_NAME);
                mappingSceRegistration = bundleContext.registerService(MappingSce.class.getName(), MappingRIMRuntime.getMappingSce(), null);
                isStarted = true;
            }
        } catch (ClassNotFoundException | InstantiationException
                         | IllegalAccessException | IOException e) {
            log.error("Error while starting and/or registring {} ! Check following root cause : ", MAPPING_DS_SERVICE_NAME);
            e.printStackTrace();
        }
    }

    @Validate
    public void validate() throws InterruptedException {
        while(config==null) {
            log.debug("Config is missing for {}. Sleep some times...", MAPPING_DS_SERVICE_NAME);
            Thread.sleep(10);
        }
        start();
        log.info("{} is started", new Object[]{MAPPING_DS_SERVICE_NAME});
    }

    private void stop() {
        if (mappingSceRegistration!=null) {
            log.debug("Unregister MappingSce Service...");
            mappingSceRegistration.unregister();
        }
        MappingRIMRuntime.stop();
    }

    @Invalidate
    public void invalidate() {
        log.info("{} is stopping. Could take some time...", MAPPING_DS_SERVICE_NAME);
        stop();
        log.info("{} is stopped", MAPPING_DS_SERVICE_NAME);
    }

    @Updated
    public void updated(final Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{MAPPING_DS_SERVICE_NAME, Thread.currentThread().toString()});
        if (MappingDSCfgLoader.isValid(properties)) {
            config = properties;
            if (isStarted) {
                final Runnable applyConfigUpdate = new Runnable() {
                    @Override
                    public void run() {
                        log.debug("{} will be restart to apply configuration changes...", MAPPING_DS_SERVICE_NAME);
                        stop();
                        start();
                    }
                };
                new Thread(applyConfigUpdate).start();
            }
        }
    }
}
