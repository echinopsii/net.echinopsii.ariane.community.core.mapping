/**
 * Mapping RIM starter
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.spectral.cc.core.mapping.ds.rim.cfg.TopoDSCfgLoader;
import com.spectral.cc.core.mapping.ds.rim.factory.service.TopoSceFactory;
import com.spectral.cc.core.mapping.ds.service.TopoSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

public class TopoRIMRuntime {
    private static final Logger log = LoggerFactory.getLogger(TopoRIMRuntime.class);

    private static TopoSce topoSce = null;
    private static JsonFactory jFactory = new JsonFactory();

    private static boolean started = false;

    public final static boolean start(Dictionary<Object, Object> properties) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonParseException, JsonMappingException, IOException {
        if (TopoDSCfgLoader.getDefaultCfgEntity() == null) {
            log.debug("Load configuration from internal conf...");
            String topoDScfgFileName = "topo.ds.rim.cfg.json";
            TopoDSCfgLoader.load(new TopoRIMRuntime().getClass().getResourceAsStream("/" + topoDScfgFileName));
        }
        topoSce = TopoSceFactory.make(TopoDSCfgLoader.getDefaultCfgEntity().getBundleName());
        if (topoSce.init(properties)) {
            if (topoSce.start()) {
                started = true;
            } else {
                log.error("A problem occured while starting Main Mapping DS service...");
            }
        } else {
            log.error("A problem occured while initializing Main Mapping DS service...");
        }
        return started;
    }

    public final static void stop() {
        if (started) {
            topoSce.stop();
            topoSce = null;
            started = false;
        }
    }

    public final static TopoSce getTopoSce() {
        return topoSce;
    }

    public static JsonFactory getjFactory() {
        return jFactory;
    }
}