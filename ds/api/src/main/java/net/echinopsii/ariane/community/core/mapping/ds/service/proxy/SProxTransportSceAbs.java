/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.service.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxTransport;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.TransportJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.TransportSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.*;

public abstract class SProxTransportSceAbs<T extends Transport> implements SProxTransportSce {
    public static DeserializedPushResponse pushDeserializedTransport(TransportJSON.JSONDeserializedTransport jsonDeserializedTransport,
                                                                     Session mappingSession,
                                                                     SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedTransport.getTransportProperties()!=null && jsonDeserializedTransport.getTransportProperties().size() > 0) {
            for (PropertiesJSON.TypedPropertyField deserializedProperty : jsonDeserializedTransport.getTransportProperties()) {
                try {
                    Object oValue = ToolBox.extractPropertyObjectValueFromString(deserializedProperty.getPropertyValue(), deserializedProperty.getPropertyType());
                    reqProperties.put(deserializedProperty.getPropertyName(), oValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.setErrorMessage("Request Error : invalid property " + deserializedProperty.getPropertyName() + ".");
                    break;
                }
            }
        }

        // LOOK IF TRANSPORT MAYBE UPDATED OR CREATED
        Transport deserializedTransport = null;
        HashSet<String> propsKeySet = null;
        if (ret.getErrorMessage() == null && jsonDeserializedTransport.getTransportID()!=null) {
            if (mappingSession!=null) deserializedTransport = mappingSce.getTransportSce().getTransport(mappingSession, jsonDeserializedTransport.getTransportID());
            else deserializedTransport = mappingSce.getTransportSce().getTransport(jsonDeserializedTransport.getTransportID());
            if (deserializedTransport==null) ret.setErrorMessage("Request Error : transport with provided ID " + jsonDeserializedTransport.getTransportID() + " was not found.");
            else if (deserializedTransport.getTransportProperties()!=null) {
                propsKeySet = new HashSet<>(deserializedTransport.getTransportProperties().keySet());
            }
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            if (deserializedTransport == null)
                if (mappingSession!=null) deserializedTransport = mappingSce.getTransportSce().createTransport(mappingSession, jsonDeserializedTransport.getTransportName());
                else deserializedTransport = mappingSce.getTransportSce().createTransport(jsonDeserializedTransport.getTransportName());
            else {
                if (jsonDeserializedTransport.getTransportName()!=null)
                    if (mappingSession!=null) ((SProxTransport)deserializedTransport).setTransportName(mappingSession, jsonDeserializedTransport.getTransportName());
                    else deserializedTransport.setTransportName(jsonDeserializedTransport.getTransportName());
            }

            if (jsonDeserializedTransport.getTransportProperties()!=null) {
                if (propsKeySet!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : propsKeySet)
                        if (!reqProperties.containsKey(propertyKey)) propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxTransport)deserializedTransport).removeTransportProperty(mappingSession, propertyToDelete);
                        else deserializedTransport.removeTransportProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxTransport)deserializedTransport).addTransportProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedTransport.addTransportProperty(propertyKey, reqProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedTransport);
        }
        return ret;
    }

    @Override
    public T createTransport(Session session, String transportName) throws MappingDSException {
        T ret = null;
        if (session!=null && session.isRunning())
            ret = (T)session.execute(this, TransportSce.OP_CREATE_TRANSPORT, new Object[]{transportName});
        return ret;
    }

    @Override
    public void deleteTransport(Session session, String transportID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, TransportSce.OP_DELETE_TRANSPORT, new Object[]{transportID});
    }

    @Override
    public T getTransport(Session session, String transportID) throws MappingDSException {
        T ret = null;
        if (session!=null && session.isRunning())
            ret = (T) session.execute(this, TransportSce.OP_GET_TRANSPORT, new Object[]{transportID});
        return ret;
    }

    @Override
    public Set<T> getTransports(Session session, String selector) throws MappingDSException {
        Set<T> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<T>) session.execute(this, TransportSce.OP_GET_TRANSPORTS, new Object[]{selector});
        return ret;
    }
}
