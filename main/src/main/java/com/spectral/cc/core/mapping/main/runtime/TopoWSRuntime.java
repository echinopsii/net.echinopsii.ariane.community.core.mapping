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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.spectral.cc.core.mapping.ds.service.TopoSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TopoWSRuntime {
    private static final Logger log = LoggerFactory.getLogger(TopoWSRuntime.class);

    private static TopoSce topoSce = null;
    private static JsonFactory jFactory = new JsonFactory();

    public final static void start(TopoSce topoService_) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonParseException, JsonMappingException, IOException {
        topoSce = topoService_;
        if (topoSce == null) {
            log.error("failed to get dependency services!");
        }
    }

    public final static void stop() {
        //if any cache save or clean it here
    }

    public final static TopoSce getTopoSce() {
        return topoSce;
    }

    public static JsonFactory getjFactory() {
        return jFactory;
    }
}