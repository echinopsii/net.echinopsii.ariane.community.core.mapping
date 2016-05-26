/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
 *
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
package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;

import java.util.HashMap;

public abstract class TransportAbs implements Transport {
    private String transportID = null;
    private String transportName = null;
    private HashMap<String, Object> transportProperties = new HashMap<>();

    @Override
    public String getTransportID() {
        return this.transportID;
    }

    @Override
    public void setTransportID(String ID) {
        this.transportID = ID;
    }

    @Override
    public String getTransportName() {
        return this.transportName;
    }

    @Override
    public void setTransportName(String name) throws MappingDSException {
        this.transportName = name;
    }

    @Override
    public HashMap<String, Object> getTransportProperties() {
        return transportProperties;
    }

    @Override
    public void addTransportProperty(String propertyKey, Object value) throws MappingDSException {
        this.transportProperties.put(propertyKey, value);
    }

    @Override
    public void removeTransportProperty(String propertyKey) throws MappingDSException {
        this.transportProperties.remove(propertyKey);
    }
}