/**
 * Mapping Datastore Runtime Injectection Manager :
 * provide a Mapping DS configuration parser, factories and registry to inject
 * Mapping DS interface implementation dependencies.
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

package com.spectral.cc.core.mapping.ds.rim.registry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.io.InputStream;

public class TopoDSRegistryService {

    public static void registerTopoDS(String bundleName_, InputStream jsonIS_) throws JsonParseException, JsonMappingException, IOException {
        TopoDSRegistryEntity entity = new TopoDSRegistryEntity(bundleName_, jsonIS_);
        TopoDSRegistry.addEntityToRegistry(entity);
    }

    public static void unregisterTopoDS(String bundleName_) {
        TopoDSRegistry.delEntityFromRegistry(bundleName_);
    }

    public static boolean isRegistered(String bundleName_) {
        return TopoDSRegistry.containsEntity(bundleName_);
    }

    public static TopoDSRegistryEntity getRegisteredTopoDS(String bundleName_) {
        return TopoDSRegistry.getEntityFromRegistry(bundleName_);
    }
}
