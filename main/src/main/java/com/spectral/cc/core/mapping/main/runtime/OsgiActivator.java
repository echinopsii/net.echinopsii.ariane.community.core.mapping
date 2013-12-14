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

import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class OsgiActivator implements BundleActivator {

    protected static final String TOPO_WS_SERVICE_NAME = "Mapping Web Service";
    protected static final Logger log = LoggerFactory.getLogger(OsgiActivator.class);

    protected static ArrayList<MainMenuEntity> mainPortalMainMenuEntityList = new ArrayList<MainMenuEntity>() ;

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting " + TOPO_WS_SERVICE_NAME);
        new Thread(new OsgiServiceTracker(context)).start();
        new Thread(new Registrator()).start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.info("Stopping " + TOPO_WS_SERVICE_NAME);
        TopoWSRuntime.stop();
        log.info(TOPO_WS_SERVICE_NAME + " has been succesfully stopped");
    }
}