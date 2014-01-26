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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectral.cc.core.mapping.ds.rim.factory.MappingDSRegisteredClassType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MappingDSRegistryEntity {

    private String bundleName = null;
    private HashMap<String, String> factoriesObjectMap = null;
    private ObjectMapper jsonMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public MappingDSRegistryEntity(String bundleName_, InputStream jsonIS_) throws JsonParseException, JsonMappingException, IOException {
        this.bundleName = bundleName_;
        this.jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        this.factoriesObjectMap = jsonMapper.readValue(jsonIS_, HashMap.class);
    }

    public String getBundleName() {
        return this.bundleName;
    }

    public HashMap<String, String> getFactoriesObjectMap() {
        return this.factoriesObjectMap;
    }

    public String getClusterFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.CLUSTER_FACTORY.getId());
    }

    public String getContainerFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.CONTAINER_FACTORY.getId());
    }

    public String getEndpointFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.ENDPOINT_FACTORY.getId());
    }

    public String getGatetFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.GATE_FACTORY.getId());
    }

    public String getLinkFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.LINK_FACTORY.getId());
    }

    public String getNodeFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.NODE_FACTORY.getId());
    }

    public String getTransportFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.TRANSPORT_FACTORY.getId());
    }

    public String getMappingSceFactoryClassName() {
        return this.factoriesObjectMap.get(MappingDSRegisteredClassType.MAPPING_SCE_FACTORY.getId());
    }
}