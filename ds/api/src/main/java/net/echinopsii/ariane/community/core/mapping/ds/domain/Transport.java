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
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.HashMap;

public interface Transport {
	public String  getTransportID();
	
	/*
	 * MUST BE UNIQUE
	 */
	public String getTransportName();
	public void   setTransportName(Session session, String name) throws MappingDSException;
	public void   setTransportName(String name);

    public HashMap<String, Object> getTransportProperties();
	public void addTransportProperty(Session session, String propertyKey, Object value) throws MappingDSException;
    public void addTransportProperty(String propertyKey, Object value);
	public void removeTransportProperty(Session session, String propertyKey) throws MappingDSException;
    public void removeTransportProperty(String propertyKey);

}
