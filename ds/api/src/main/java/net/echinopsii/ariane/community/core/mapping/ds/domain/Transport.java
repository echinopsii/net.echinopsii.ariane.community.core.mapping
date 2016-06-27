/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
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

package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;

import java.util.HashMap;

public interface Transport {
	String TOKEN_TP_ID = MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"ID";
	String TOKEN_TP_NAME = MappingDSGraphPropertyNames.DD_TRANSPORT_NAME_KEY;
	String TOKEN_TP_PRP = MappingDSGraphPropertyNames.DD_TRANSPORT_PROPS_KEY;

	String OP_SET_TRANSPORT_NAME = "setTransportName";
	String OP_ADD_TRANSPORT_PROPERTY = "addTransportProperty";
	String OP_REMOVE_TRANSPORT_PROPERTY = "removeTransportProperty";

	String  getTransportID();
	void    setTransportID(String ID);
	
	/*
	 * MUST BE UNIQUE
	 */
	String getTransportName();
	void   setTransportName(String name) throws MappingDSException;

    HashMap<String, Object> getTransportProperties();
    void addTransportProperty(String propertyKey, Object value) throws MappingDSException;
    void removeTransportProperty(String propertyKey) throws MappingDSException;

}
