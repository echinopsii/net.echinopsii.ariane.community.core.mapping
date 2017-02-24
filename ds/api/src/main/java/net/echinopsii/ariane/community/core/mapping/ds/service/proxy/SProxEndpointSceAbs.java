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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxEndpoint;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.EndpointSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.*;

public abstract class SProxEndpointSceAbs<E extends Endpoint> implements SProxEndpointSce {

    public static DeserializedPushResponse pushDeserializedEndpoint(EndpointJSON.JSONDeserializedEndpoint jsonDeserializedEndpoint,
                                                                    Session mappingSession,
                                                                    SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Node reqEndpointParentNode = null;
        List<Endpoint> reqEndpointTwinEndpoints = new ArrayList<>();
        HashMap<String, Object> reqEndpointProperties = new HashMap<>();

        if (jsonDeserializedEndpoint.getEndpointParentNodeID() != null) {
            if (mappingSession!=null) reqEndpointParentNode =  mappingSce.getNodeSce().getNode(mappingSession, jsonDeserializedEndpoint.getEndpointParentNodeID());
            else reqEndpointParentNode =  mappingSce.getNodeSce().getNode(jsonDeserializedEndpoint.getEndpointParentNodeID());
            if (reqEndpointParentNode==null) ret.setErrorMessage("Request Error : node with provided ID " + jsonDeserializedEndpoint.getEndpointParentNodeID() + " was not found.");
        } else ret.setErrorMessage("Request Error : no parent node ID provided...");

        if (ret.getErrorMessage()==null && jsonDeserializedEndpoint.getEndpointTwinEndpointsID()!=null && jsonDeserializedEndpoint.getEndpointTwinEndpointsID().size() > 0) {
            for (String id : jsonDeserializedEndpoint.getEndpointTwinEndpointsID()) {
                Endpoint twinEndpoint ;
                if (mappingSession!=null) twinEndpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, id);
                else twinEndpoint = mappingSce.getEndpointSce().getEndpoint(id);
                if (twinEndpoint != null)
                    reqEndpointTwinEndpoints.add(twinEndpoint);
                else {
                    ret.setErrorMessage("Request Error : twin endpoint with provided ID " + id + " was not found.");
                    break;
                }
            }
        }

        if (ret.getErrorMessage()==null && jsonDeserializedEndpoint.getEndpointProperties()!=null && jsonDeserializedEndpoint.getEndpointProperties().size() > 0) {
            for (PropertiesJSON.TypedPropertyField deserializedProperty : jsonDeserializedEndpoint.getEndpointProperties()) {
                try {
                    Object oValue = ToolBox.extractPropertyObjectValueFromString(deserializedProperty.getPropertyValue(), deserializedProperty.getPropertyType());
                    reqEndpointProperties.put(deserializedProperty.getPropertyName(), oValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.setErrorMessage("Request Error : invalid property " + deserializedProperty.getPropertyName() + ".");
                    break;
                }
            }
        }

        // LOOK IF NODE MAYBE UPDATED OR CREATED
        Endpoint deserializedEndpoint = null;
        if (ret.getErrorMessage() == null && jsonDeserializedEndpoint.getEndpointID()!=null) {
            if (mappingSession!=null) deserializedEndpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, jsonDeserializedEndpoint.getEndpointID());
            else deserializedEndpoint = mappingSce.getEndpointSce().getEndpoint(jsonDeserializedEndpoint.getEndpointID());
            if (deserializedEndpoint==null)
                ret.setErrorMessage("Request Error : endpoint with provided ID " + jsonDeserializedEndpoint.getEndpointID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedEndpoint==null && jsonDeserializedEndpoint.getEndpointURL() != null)
            if (mappingSession!=null) deserializedEndpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, jsonDeserializedEndpoint.getEndpointID());
            else deserializedEndpoint = mappingSce.getEndpointSce().getEndpoint(jsonDeserializedEndpoint.getEndpointID());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqEndpointURL = jsonDeserializedEndpoint.getEndpointURL();
            String reqEndpointParentNodeID = jsonDeserializedEndpoint.getEndpointParentNodeID();
            if (deserializedEndpoint == null)
                if (mappingSession != null) deserializedEndpoint = mappingSce.getEndpointSce().createEndpoint(mappingSession, reqEndpointURL, reqEndpointParentNodeID);
                else deserializedEndpoint = mappingSce.getEndpointSce().createEndpoint(reqEndpointURL, reqEndpointParentNodeID);
            else {
                if (reqEndpointURL!=null)
                    if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).setEndpointURL(mappingSession, reqEndpointURL);
                    else deserializedEndpoint.setEndpointURL(reqEndpointURL);
                if (reqEndpointParentNode!=null)
                    if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).setEndpointParentNode(mappingSession, reqEndpointParentNode);
                    else deserializedEndpoint.setEndpointParentNode(reqEndpointParentNode);
            }

            if (jsonDeserializedEndpoint.getEndpointTwinEndpointsID()!=null) {
                List<Endpoint> twinEndpointsToDelete = new ArrayList<>();
                for (Endpoint existingTwinEndpoint : deserializedEndpoint.getTwinEndpoints())
                    if (!reqEndpointTwinEndpoints.contains(existingTwinEndpoint))
                        twinEndpointsToDelete.add(existingTwinEndpoint);
                for (Endpoint twinEndpointToDelete : twinEndpointsToDelete) {
                    if (mappingSession!=null) {
                        ((SProxEndpoint)deserializedEndpoint).removeTwinEndpoint(mappingSession, twinEndpointToDelete);
                        ((SProxEndpoint)twinEndpointToDelete).removeTwinEndpoint(mappingSession, deserializedEndpoint);
                    } else {
                        deserializedEndpoint.removeTwinEndpoint(twinEndpointToDelete);
                        twinEndpointToDelete.removeTwinEndpoint(deserializedEndpoint);
                    }
                }

                for (Endpoint twinEndpointToAdd : reqEndpointTwinEndpoints) {
                    if (mappingSession!=null) {
                        ((SProxEndpoint)deserializedEndpoint).addTwinEndpoint(mappingSession, twinEndpointToAdd);
                        ((SProxEndpoint)twinEndpointToAdd).addTwinEndpoint(mappingSession, deserializedEndpoint);
                    } else {
                        deserializedEndpoint.addTwinEndpoint(twinEndpointToAdd);
                        twinEndpointToAdd.addTwinEndpoint(deserializedEndpoint);
                    }
                }
            }

            if (jsonDeserializedEndpoint.getEndpointProperties()!=null) {
                Set<String> propsKeySet = deserializedEndpoint.getEndpointProperties().keySet();
                if (propsKeySet!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : propsKeySet)
                        if (!reqEndpointProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyKeyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).removeEndpointProperty(mappingSession, propertyKeyToDelete);
                        else deserializedEndpoint.removeEndpointProperty(propertyKeyToDelete);
                }

                for (String propertyKey : reqEndpointProperties.keySet())
                    if (mappingSession!=null) ((SProxEndpoint)deserializedEndpoint).addEndpointProperty(mappingSession, propertyKey, reqEndpointProperties.get(propertyKey));
                    else deserializedEndpoint.addEndpointProperty(propertyKey, reqEndpointProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedEndpoint);
        }

        return ret;
    }

    @Override
    public E createEndpoint(Session session, String url, String parentNodeID) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, EndpointSce.OP_CREATE_ENDPOINT, new Object[]{url, parentNodeID});
        return ret;
    }

    @Override
    public void deleteEndpoint(Session session, String endpointID) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, EndpointSce.OP_DELETE_ENDPOINT, new Object[]{endpointID});
    }

    @Override
    public E getEndpoint(Session session, String id) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, EndpointSce.OP_GET_ENDPOINT, new Object[]{id});
        return ret;
    }

    @Override
    public E getEndpointByURL(Session session, String URL) throws MappingDSException {
        E ret = null;
        if (session!=null && session.isRunning())
            ret = (E) session.execute(this, EndpointSce.OP_GET_ENDPOINT_BY_URL, new Object[]{URL});
        return ret;
    }

    @Override
    public Set<E> getEndpoints(Session session, String selector) throws MappingDSException {
        Set<E> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<E>) session.execute(this, EndpointSce.OP_GET_ENDPOINTS, new Object[]{selector});
        return ret;
    }

    @Override
    public Set<E> getEndpoints(Session session, String key, Object value) throws MappingDSException {
        Set<E> ret = null;
        if (session!=null && session.isRunning())
            ret = (Set<E>) session.execute(this, EndpointSce.OP_GET_ENDPOINTS, new Object[]{key, value});
        return ret;
    }
}
