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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.*;

public abstract class SProxContainerSceAbs<C extends Container> implements SProxContainerSce {
    public static DeserializedPushResponse pushDeserializedContainer(ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer,
                                                                     Session mappingSession,
                                                                     SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Gate reqPrimaryAdminGate = null;
        Cluster reqContainerCluster = null;
        Container reqContainerParent = null;
        List<Container> reqContainerChildContainers = new ArrayList<>();
        List<Node> reqContainerChildNodes = new ArrayList<>();
        List<Gate> reqContainerChildGates = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedContainer.getContainerPrimaryAdminGateID()!=null) {
            if (mappingSession!=null) reqPrimaryAdminGate = mappingSce.getGateSce().getGate(mappingSession, jsonDeserializedContainer.getContainerPrimaryAdminGateID());
            else reqPrimaryAdminGate = mappingSce.getGateSce().getGate(jsonDeserializedContainer.getContainerPrimaryAdminGateID());
            if (reqPrimaryAdminGate == null) ret.setErrorMessage("Request Error : gate with provided ID " + jsonDeserializedContainer.getContainerPrimaryAdminGateID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerClusterID()!=null) {
            if (mappingSession!=null) reqContainerCluster = mappingSce.getClusterSce().getCluster(mappingSession, jsonDeserializedContainer.getContainerClusterID());
            else reqContainerCluster = mappingSce.getClusterSce().getCluster(jsonDeserializedContainer.getContainerClusterID());
            if (reqContainerCluster == null) ret.setErrorMessage("Request Error: cluster with provided ID " + jsonDeserializedContainer.getContainerClusterID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerParentContainerID()!=null) {
            if (mappingSession!=null) reqContainerParent = mappingSce.getContainerSce().getContainer(mappingSession, jsonDeserializedContainer.getContainerParentContainerID());
            else reqContainerParent = mappingSce.getContainerSce().getContainer(jsonDeserializedContainer.getContainerParentContainerID());
            if (reqContainerParent == null) ret.setErrorMessage("Request Error: parent container with provided ID " + jsonDeserializedContainer.getContainerParentContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerChildContainersID()!=null && jsonDeserializedContainer.getContainerChildContainersID().size() > 0) {
            for (String id : jsonDeserializedContainer.getContainerChildContainersID()) {
                Container childContainer;
                if (mappingSession!=null) childContainer = mappingSce.getContainerSce().getContainer(mappingSession, id);
                else childContainer = mappingSce.getContainerSce().getContainer(id);
                if (childContainer!=null) reqContainerChildContainers.add(childContainer);
                else {
                    ret.setErrorMessage("Request Error : child container with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerNodesID()!=null && jsonDeserializedContainer.getContainerNodesID().size()>0) {
            for (String id : jsonDeserializedContainer.getContainerNodesID()) {
                Node childNode;
                if (mappingSession!=null) childNode = mappingSce.getNodeSce().getNode(mappingSession, id);
                else childNode = mappingSce.getNodeSce().getNode(id);
                if (childNode != null) reqContainerChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerGatesID()!=null && jsonDeserializedContainer.getContainerGatesID().size()>0) {
            for (String id : jsonDeserializedContainer.getContainerGatesID()) {
                Gate childGate;
                if (mappingSession!=null) childGate = mappingSce.getGateSce().getGate(mappingSession, id);
                else childGate = mappingSce.getGateSce().getGate(id);
                if (childGate != null) reqContainerChildGates.add(childGate);
                else {
                    ret.setErrorMessage("Request Error : child gate with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerProperties()!=null && jsonDeserializedContainer.getContainerProperties().size() > 0) {
            for (PropertiesJSON.TypedPropertyField deserializedProperty : jsonDeserializedContainer.getContainerProperties()) {
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
        // LOOK IF CONTAINER MAYBE UPDATED OR CREATED
        Container deserializedContainer = null;
        HashSet<Container> childContainers = null;
        HashSet<Node> containerNodes = null;
        HashSet<Gate> containerGates = null;
        HashSet<String> propsKeySet = null;
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerID()!=null) {
            if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().getContainer(mappingSession, jsonDeserializedContainer.getContainerID());
            else deserializedContainer = mappingSce.getContainerSce().getContainer(jsonDeserializedContainer.getContainerID());
            if (deserializedContainer==null)
                ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedContainer.getContainerID() + " was not found.");
            else {
                childContainers = new HashSet<>(deserializedContainer.getContainerChildContainers());
                containerNodes = new HashSet<>(deserializedContainer.getContainerNodes());
                containerGates = new HashSet<>(deserializedContainer.getContainerGates());
                propsKeySet = new HashSet<>(deserializedContainer.getContainerProperties().keySet());
            }
        }

        if (ret.getErrorMessage() == null && deserializedContainer == null && jsonDeserializedContainer.getContainerGateURI() != null)
            if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().getContainerByPrimaryAdminURL(mappingSession, jsonDeserializedContainer.getContainerGateURI());
            else deserializedContainer = mappingSce.getContainerSce().getContainerByPrimaryAdminURL(jsonDeserializedContainer.getContainerGateURI());

        /*
        if (ret.getErrorMessage() == null && deserializedContainer!=null) {
            if (!deserializedContainer.getContainerPrimaryAdminGateURL().equals(jsonDeserializedContainer.getContainerGateURI()) ||
                !deserializedContainer.getContainerPrimaryAdminGate().getNodeName().equals(jsonDeserializedContainer.getContainerGateName())
                    ) {
                ret.setErrorMessage("Request Error : gate definition doesn't match with container " + jsonDeserializedContainer.getContainerID() + " !");
            }
        }
        */

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqContainerName = jsonDeserializedContainer.getContainerName();
            String reqContainerCompany = jsonDeserializedContainer.getContainerCompany();
            String reqContainerProduct = jsonDeserializedContainer.getContainerProduct();
            String reqContainerType = jsonDeserializedContainer.getContainerType();

            if (deserializedContainer == null) {
                String reqContainerGURI = jsonDeserializedContainer.getContainerGateURI();
                String reqContainerGName = jsonDeserializedContainer.getContainerGateName();
                if (reqContainerName == null)
                    if (reqContainerParent != null)
                        if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().createContainer(mappingSession, reqContainerGURI, reqContainerGName, reqContainerParent);
                        else deserializedContainer = mappingSce.getContainerSce().createContainer(reqContainerGURI, reqContainerGName, reqContainerParent);
                    else
                        if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().createContainer(mappingSession, reqContainerGURI, reqContainerGName);
                        else deserializedContainer = mappingSce.getContainerSce().createContainer(reqContainerGURI, reqContainerGName);
                else
                    if (reqContainerParent != null)
                        if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().createContainer(mappingSession, reqContainerName, reqContainerGURI, reqContainerGName, reqContainerParent);
                        else deserializedContainer = mappingSce.getContainerSce().createContainer(reqContainerName, reqContainerGURI, reqContainerGName, reqContainerParent);
                    else
                        if (mappingSession!=null) deserializedContainer = mappingSce.getContainerSce().createContainer(mappingSession, reqContainerName, reqContainerGURI, reqContainerGName);
                        else deserializedContainer = mappingSce.getContainerSce().createContainer(reqContainerName, reqContainerGURI, reqContainerGName);
            } else {
                if (reqContainerName != null)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerName(mappingSession, reqContainerName);
                    else deserializedContainer.setContainerName(reqContainerName);
                if (reqPrimaryAdminGate != null)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerPrimaryAdminGate(mappingSession, reqPrimaryAdminGate);
                    else deserializedContainer.setContainerPrimaryAdminGate(reqPrimaryAdminGate);
            }

            reqContainerChildNodes.add(deserializedContainer.getContainerPrimaryAdminGate());
            reqContainerChildGates.add(deserializedContainer.getContainerPrimaryAdminGate());

            if (reqContainerCluster != null)
                if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerCluster(mappingSession, reqContainerCluster);
                else deserializedContainer.setContainerCluster(reqContainerCluster);
            if (reqContainerCompany != null)
                if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerCompany(mappingSession, reqContainerCompany);
                else deserializedContainer.setContainerCompany(reqContainerCompany);
            if (reqContainerProduct != null)
                if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerProduct(mappingSession, reqContainerProduct);
                else deserializedContainer.setContainerProduct(reqContainerProduct);
            if (reqContainerType != null)
                if (mappingSession!=null) ((SProxContainer)deserializedContainer).setContainerType(mappingSession, reqContainerType);
                else deserializedContainer.setContainerType(reqContainerType);

            if (jsonDeserializedContainer.getContainerChildContainersID() != null) {
                List<Container> childContainersToDelete = new ArrayList<>();
                if (childContainers!=null)
                    for (Container containerToDel : childContainers)
                        if (!reqContainerChildContainers.contains(containerToDel)) childContainersToDelete.add(containerToDel);
                for (Container containerToDel : childContainersToDelete)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).removeContainerChildContainer(mappingSession, containerToDel);
                    else deserializedContainer.removeContainerChildContainer(containerToDel);
                for (Container containerToAdd : reqContainerChildContainers)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).addContainerChildContainer(mappingSession, containerToAdd);
                    else deserializedContainer.addContainerChildContainer(containerToAdd);
            }

            if (jsonDeserializedContainer.getContainerNodesID() != null) {
                List<Node> nodesToDelete = new ArrayList<>();
                if (containerNodes!=null)
                    for (Node nodeToDel : containerNodes)
                        if (!reqContainerChildNodes.contains(nodeToDel)) nodesToDelete.add(nodeToDel);
                for (Node nodeToDel : nodesToDelete)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).removeContainerNode(mappingSession, nodeToDel);
                    else deserializedContainer.removeContainerNode(nodeToDel);
                for (Node nodeToAdd : reqContainerChildNodes)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).addContainerNode(mappingSession, nodeToAdd);
                    else deserializedContainer.addContainerNode(nodeToAdd);
            }

            if (jsonDeserializedContainer.getContainerGatesID() != null) {
                List<Gate> gatesToDelete = new ArrayList<>();
                if (containerGates != null)
                    for (Gate gateToDel : containerGates)
                        if (!reqContainerChildGates.contains(gateToDel)) gatesToDelete.add(gateToDel);
                for (Gate gateToDel : gatesToDelete)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).removeContainerGate(mappingSession, gateToDel);
                    else deserializedContainer.removeContainerGate(gateToDel);
                for (Gate gateToAdd : reqContainerChildGates)
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).addContainerGate(mappingSession, gateToAdd);
                    else deserializedContainer.addContainerGate(gateToAdd);
            }

            if (jsonDeserializedContainer.getContainerProperties()!=null) {
                if (propsKeySet!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : propsKeySet)
                        if (!reqProperties.containsKey(propertyKey)) propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxContainer)deserializedContainer).removeContainerProperty(mappingSession, propertyToDelete);
                        else deserializedContainer.removeContainerProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxContainer)deserializedContainer).addContainerProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedContainer.addContainerProperty(propertyKey, reqProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedContainer);
        }
        return ret;
    }

    @Override
    public C createContainer(Session session, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_CREATE_CONTAINER, new Object[]{primaryAdminURL, primaryAdminGateName});
        return ret;
    }

    @Override
    public C createContainer(Session session, String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_CREATE_CONTAINER, new Object[]{name, primaryAdminURL, primaryAdminGateName});
        return ret;
    }

    @Override
    public C createContainer(Session session, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_CREATE_CONTAINER, new Object[]{primaryAdminURL, primaryAdminGateName, parentContainer});
        return ret;
    }

    @Override
    public C createContainer(Session session, String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_CREATE_CONTAINER, new Object[]{name, primaryAdminURL, primaryAdminGateName, parentContainer});
        return ret;
    }

    @Override
    public void deleteContainer(Session session, String primaryAdminURL) throws MappingDSException {
        if (session != null && session.isRunning())
            session.execute(this, ContainerSce.OP_DELETE_CONTAINER, new Object[]{primaryAdminURL});
    }

    @Override
    public C getContainer(Session session, String id) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_GET_CONTAINER, new Object[]{id});
        return ret;
    }

    @Override
    public C getContainerByPrimaryAdminURL(Session session, String primaryAdminURL) throws MappingDSException {
        C ret = null;
        if (session != null && session.isRunning())
            ret = (C) session.execute(this, ContainerSce.OP_GET_CONTAINER_BY_PRIMARY_ADMIN_URL, new Object[]{primaryAdminURL});
        return ret;
    }

    @Override
    public Set<C> getContainers(Session session, String selector) throws MappingDSException {
        Set<C> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<C>) session.execute(this, ContainerSce.OP_GET_CONTAINERS, new Object[]{selector});
        return ret;
    }
}
