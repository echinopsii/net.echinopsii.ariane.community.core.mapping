/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
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
package com.spectral.cc.core.mapping.main.runtime;

import com.spectral.cc.core.mapping.ds.service.MappingSce;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiServiceTracker implements Runnable {

    private ServiceTracker mappingSceTracker = null;
    private BundleContext context;

    public OsgiServiceTracker(BundleContext context_) {
        this.context = context_;
    }

    @Override
    public void run() {
        mappingSceTracker = new ServiceTracker(context, MappingSce.class.getName(), null);
        try {
            if (mappingSceTracker != null) {
                mappingSceTracker.open();
                MappingSce mappingSce = (MappingSce) mappingSceTracker.waitForService(60000);
                if (mappingSce != null) {
                    MappingWSRuntime.start(mappingSce);
                    OsgiActivator.log.info(OsgiActivator.MAPPING_WS_SERVICE_NAME + " has been succesfully started !");
                } else {
                    OsgiActivator.log.warn(OsgiActivator.MAPPING_WS_SERVICE_NAME + " was not able to find its dependencies services (waiting  60 seconds).");
                }
            }
        } catch (Exception e) {
            OsgiActivator.log.error(OsgiActivator.MAPPING_WS_SERVICE_NAME + " failed to start !!!");
        } finally {
            if (mappingSceTracker != null) {
                mappingSceTracker.close();
            }
        }
    }
}
