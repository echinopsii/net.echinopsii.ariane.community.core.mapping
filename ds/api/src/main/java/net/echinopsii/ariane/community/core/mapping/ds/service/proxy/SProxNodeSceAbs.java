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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxGate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.GateJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class SProxNodeSceAbs<N extends Node> implements SProxNodeSce {

    public static DeserializedPushResponse pushDeserializedNode(NodeJSON.JSONDeserializedNode jsonDeserializedNode,
                                                                Session mappingSession,
                                                                SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Container reqNodeContainer = null;
        Node reqNodeParentNode = null;
        List<Node> reqNodeChildNodes = new ArrayList<>();
        List<Node> reqNodeTwinNodes = new ArrayList<>();
        List<Endpoint> reqNodeEndpoints = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedNode.getNodeContainerID()!=null) {
            if (mappingSession!=null) reqNodeContainer = mappingSce.getContainerSce().getContainer(mappingSession, jsonDeserializedNode.getNodeContainerID());
            else reqNodeContainer = mappingSce.getContainerSce().getContainer(jsonDeserializedNode.getNodeContainerID());
            if (reqNodeContainer == null) ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedNode.getNodeContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeParentNodeID()!=null) {
            if (mappingSession!=null) reqNodeParentNode = mappingSce.getNodeSce().getNode(mappingSession, jsonDeserializedNode.getNodeParentNodeID());
            else reqNodeParentNode = mappingSce.getNodeSce().getNode(jsonDeserializedNode.getNodeParentNodeID());
            if (reqNodeParentNode == null) ret.setErrorMessage("Request Error : parent node with provided ID " + jsonDeserializedNode.getNodeParentNodeID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeChildNodesID()!=null && jsonDeserializedNode.getNodeChildNodesID().size() > 0 ) {
            for (String id : jsonDeserializedNode.getNodeChildNodesID()) {
                Node childNode ;
                if (mappingSession!=null) childNode = mappingSce.getNodeSce().getNode(mappingSession, id);
                else childNode = mappingSce.getNodeSce().getNode(id);
                if (childNode != null) reqNodeChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeTwinNodesID()!=null && jsonDeserializedNode.getNodeTwinNodesID().size() > 0 ) {
            for (String id : jsonDeserializedNode.getNodeTwinNodesID()) {
                Node twinNode ;
                if (mappingSession!=null) twinNode = mappingSce.getNodeSce().getNode(mappingSession, id);
                else twinNode = mappingSce.getNodeSce().getNode(id);
                if (twinNode != null) reqNodeTwinNodes.add(twinNode);
                else {
                    ret.setErrorMessage("Request Error : twin node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeEndpointsID()!=null && jsonDeserializedNode.getNodeEndpointsID().size() > 0) {
            for (String id : jsonDeserializedNode.getNodeEndpointsID()) {
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = mappingSce.getEndpointSce().getEndpoint(id);
                if (endpoint != null) reqNodeEndpoints.add(endpoint);
                else {
                    ret.setErrorMessage("Request Error : endpoint with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeProperties()!=null && jsonDeserializedNode.getNodeProperties().size() > 0) {
            for (PropertiesJSON.TypedPropertyField deserializedProperty : jsonDeserializedNode.getNodeProperties()) {
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

        // LOOK IF NODE MAYBE UPDATED OR CREATED
        Node deserializedNode = null;
        if (ret.getErrorMessage() == null && jsonDeserializedNode.getNodeID() != null) {
            if (mappingSession!=null) deserializedNode = mappingSce.getNodeSce().getNode(mappingSession, jsonDeserializedNode.getNodeID());
            else deserializedNode = mappingSce.getNodeSce().getNode(jsonDeserializedNode.getNodeID());
            if (deserializedNode == null)
                ret.setErrorMessage("Request Error : node with provided ID " + jsonDeserializedNode.getNodeID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedNode == null && reqNodeContainer != null && jsonDeserializedNode.getNodeName() != null)
            if (mappingSession!=null) deserializedNode = mappingSce.getNodeByName(mappingSession, reqNodeContainer, jsonDeserializedNode.getNodeName());
            else deserializedNode = mappingSce.getNodeByName(reqNodeContainer, jsonDeserializedNode.getNodeName());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqNodeName = jsonDeserializedNode.getNodeName();
            String reqContainerID = jsonDeserializedNode.getNodeContainerID();
            String reqParentNodeID = jsonDeserializedNode.getNodeParentNodeID();
            if (deserializedNode == null)
                if (mappingSession!=null) deserializedNode = mappingSce.getNodeSce().createNode(mappingSession, reqNodeName, reqContainerID, reqParentNodeID);
                else deserializedNode = mappingSce.getNodeSce().createNode(reqNodeName, reqContainerID, reqParentNodeID);
            else {
                if (reqNodeName != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeName(mappingSession, reqNodeName);
                    else deserializedNode.setNodeName(reqNodeName);
                if (reqNodeContainer != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeContainer(mappingSession, reqNodeContainer);
                    else deserializedNode.setNodeContainer(reqNodeContainer);
                if (reqNodeParentNode != null)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).setNodeParentNode(mappingSession, reqNodeParentNode);
                    else deserializedNode.setNodeParentNode(reqNodeParentNode);
            }

            if (jsonDeserializedNode.getNodeChildNodesID()!=null) {
                List<Node> childNodesToDelete = new ArrayList<>();
                for (Node existingChildNode : deserializedNode.getNodeChildNodes())
                    if (!reqNodeChildNodes.contains(existingChildNode))
                        childNodesToDelete.add(existingChildNode);
                for (Node childNodeToDelete : childNodesToDelete)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).removeNodeChildNode(mappingSession, childNodeToDelete);
                    else deserializedNode.removeNodeChildNode(childNodeToDelete);

                for (Node childNodeReq : reqNodeChildNodes)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addNodeChildNode(mappingSession, childNodeReq);
                    else deserializedNode.addNodeChildNode(childNodeReq);
            }

            if (jsonDeserializedNode.getNodeTwinNodesID()!=null) {
                List<Node> twinNodesToDelete = new ArrayList<>();
                for (Node existingTwinNode : deserializedNode.getTwinNodes())
                    if (!reqNodeTwinNodes.contains(existingTwinNode))
                        twinNodesToDelete.add(existingTwinNode);
                for (Node twinNodeToDelete : twinNodesToDelete) {
                    if (mappingSession!=null) {
                        ((SProxNode)deserializedNode).removeTwinNode(mappingSession, twinNodeToDelete);
                        ((SProxNode)twinNodeToDelete).removeTwinNode(mappingSession, deserializedNode);
                    } else {
                        deserializedNode.removeTwinNode(twinNodeToDelete);
                        twinNodeToDelete.removeTwinNode(deserializedNode);
                    }
                }

                for (Node twinNodeReq : reqNodeTwinNodes) {
                    if (mappingSession!=null) {
                        ((SProxNode)deserializedNode).addTwinNode(mappingSession, twinNodeReq);
                        ((SProxNode)twinNodeReq).addTwinNode(mappingSession, deserializedNode);
                    } else {
                        deserializedNode.addTwinNode(twinNodeReq);
                        twinNodeReq.addTwinNode(deserializedNode);
                    }
                }
            }

            if (jsonDeserializedNode.getNodeEndpointsID()!=null) {
                List<Endpoint> endpointsToDelete = new ArrayList<>();
                for (Endpoint existingEndpoint : deserializedNode.getNodeEndpoints())
                    if (!reqNodeEndpoints.contains(existingEndpoint))
                        endpointsToDelete.add(existingEndpoint);
                for (Endpoint endpointToDelete : endpointsToDelete)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).removeEndpoint(mappingSession, endpointToDelete);
                    else deserializedNode.removeEndpoint(endpointToDelete);

                for (Endpoint endpointReq : reqNodeEndpoints)
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addEndpoint(mappingSession, endpointReq);
                    else deserializedNode.addEndpoint(endpointReq);
            }

            if (jsonDeserializedNode.getNodeProperties()!=null) {
                if (deserializedNode.getNodeProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedNode.getNodeProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxNode)deserializedNode).removeNodeProperty(mappingSession, propertyToDelete);
                        else deserializedNode.removeNodeProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxNode)deserializedNode).addNodeProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedNode.addNodeProperty(propertyKey, reqProperties.get(propertyKey));
            }
            ret.setDeserializedObject(deserializedNode);
        }

        return ret;
    }

    public static DeserializedPushResponse pushDeserializedGate(GateJSON.JSONDeserializedGate jsonDeserializedGate,
                                                                Session mappingSession,
                                                                SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Container reqNodeContainer = null;
        List<Node> reqNodeChildNodes = new ArrayList<>();
        List<Node> reqNodeTwinNodes = new ArrayList<>();
        List<Endpoint> reqNodeEndpoints = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedGate.getNode().getNodeContainerID()!=null) {
            if (mappingSession!=null) reqNodeContainer = mappingSce.getContainerSce().getContainer(mappingSession, jsonDeserializedGate.getNode().getNodeContainerID());
            else reqNodeContainer = mappingSce.getContainerSce().getContainer(jsonDeserializedGate.getNode().getNodeContainerID());
            if (reqNodeContainer == null) ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedGate.getNode().getNodeContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeChildNodesID()!=null && jsonDeserializedGate.getNode().getNodeChildNodesID().size() > 0 ) {
            for (String id : jsonDeserializedGate.getNode().getNodeChildNodesID()) {
                Node childNode ;
                if (mappingSession!=null) childNode = mappingSce.getNodeSce().getNode(mappingSession, id);
                else childNode = mappingSce.getNodeSce().getNode(id);
                if (childNode != null) reqNodeChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeTwinNodesID()!=null && jsonDeserializedGate.getNode().getNodeTwinNodesID().size() > 0 ) {
            for (String id : jsonDeserializedGate.getNode().getNodeTwinNodesID()) {
                Node twinNode ;
                if (mappingSession!=null) twinNode = mappingSce.getNodeSce().getNode(mappingSession, id);
                else twinNode = mappingSce.getNodeSce().getNode(id);
                if (twinNode != null) reqNodeTwinNodes.add(twinNode);
                else {
                    ret.setErrorMessage("Request Error : twin node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeEndpointsID()!=null && jsonDeserializedGate.getNode().getNodeEndpointsID().size() > 0) {
            for (String id : jsonDeserializedGate.getNode().getNodeTwinNodesID()) {
                Endpoint endpoint ;
                if (mappingSession!=null) endpoint = mappingSce.getEndpointSce().getEndpoint(mappingSession, id);
                else endpoint = mappingSce.getEndpointSce().getEndpoint(id);
                if (endpoint != null) reqNodeEndpoints.add(endpoint);
                else {
                    ret.setErrorMessage("Request Error : endpoint with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeProperties()!=null && jsonDeserializedGate.getNode().getNodeProperties().size() > 0) {
            for (PropertiesJSON.TypedPropertyField deserializedProperty : jsonDeserializedGate.getNode().getNodeProperties()) {
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

        // LOOK IF NODE MAYBE UPDATED OR CREATED
        Gate deserializedGate = null;
        if (ret.getErrorMessage() == null && jsonDeserializedGate.getNode().getNodeID() != null) {
            if (mappingSession!=null) deserializedGate = mappingSce.getGateSce().getGate(mappingSession, jsonDeserializedGate.getNode().getNodeID());
            else deserializedGate = mappingSce.getGateSce().getGate(jsonDeserializedGate.getNode().getNodeID());
            if (deserializedGate == null)
                ret.setErrorMessage("Request Error : gate with provided ID " + jsonDeserializedGate.getNode().getNodeID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedGate == null && reqNodeContainer != null && jsonDeserializedGate.getNode().getNodeName() != null)
            if (mappingSession!=null) deserializedGate = mappingSce.getGateByName(mappingSession, reqNodeContainer, jsonDeserializedGate.getNode().getNodeName());
            else deserializedGate = mappingSce.getGateByName(reqNodeContainer, jsonDeserializedGate.getNode().getNodeName());

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqNodeName = jsonDeserializedGate.getNode().getNodeName();
            String reqGateURL = jsonDeserializedGate.getGateURL();
            boolean reqGateIsPrimaryAdmin = jsonDeserializedGate.isGateIsPrimaryAdmin();
            String reqContainerID = jsonDeserializedGate.getNode().getNodeContainerID();
            if (deserializedGate == null)
                if (mappingSession!=null) deserializedGate = mappingSce.getGateSce().createGate(mappingSession, reqGateURL, reqNodeName, reqContainerID, reqGateIsPrimaryAdmin);
                else deserializedGate = mappingSce.getGateSce().createGate(reqGateURL, reqNodeName, reqContainerID, reqGateIsPrimaryAdmin);
            else {
                if (reqNodeName != null)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).setNodeName(mappingSession, reqNodeName);
                    else deserializedGate.setNodeName(reqNodeName);
                if (reqNodeContainer != null)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).setNodeContainer(mappingSession, reqNodeContainer);
                    else deserializedGate.setNodeContainer(reqNodeContainer);

                if (reqGateIsPrimaryAdmin) {
                    Endpoint primaryAdminEp = deserializedGate.getNodePrimaryAdminEndpoint();
                    if ((primaryAdminEp == null && reqGateURL != null) ||
                            (primaryAdminEp != null && !primaryAdminEp.getEndpointURL().equals(reqGateURL))) {
                        if (mappingSession != null)
                            primaryAdminEp = mappingSce.getEndpointSce().getEndpointByURL(mappingSession, reqGateURL);
                        else primaryAdminEp = mappingSce.getEndpointSce().getEndpointByURL(reqGateURL);

                        if (primaryAdminEp == null)
                            if (mappingSession != null)
                                primaryAdminEp = mappingSce.getEndpointSce().createEndpoint(mappingSession, reqGateURL, deserializedGate.getNodeID());
                            else
                                primaryAdminEp = mappingSce.getEndpointSce().createEndpoint(reqGateURL, deserializedGate.getNodeID());

                        if (mappingSession != null)
                            ((SProxGate) deserializedGate).setNodePrimaryAdminEnpoint(mappingSession, primaryAdminEp);
                        else deserializedGate.setNodePrimaryAdminEnpoint(primaryAdminEp);
                        deserializedGate.getNodeContainer().setContainerPrimaryAdminGate(deserializedGate);
                    }
                }
            }

            if (jsonDeserializedGate.getNode().getNodeChildNodesID()!=null) {
                List<Node> childNodesToDelete = new ArrayList<>();
                for (Node existingChildNode : deserializedGate.getNodeChildNodes())
                    if (!reqNodeChildNodes.contains(existingChildNode))
                        childNodesToDelete.add(existingChildNode);
                for (Node childNodeToDelete : childNodesToDelete)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).removeNodeChildNode(mappingSession, childNodeToDelete);
                    else deserializedGate.removeNodeChildNode(childNodeToDelete);

                for (Node childNodeReq : reqNodeChildNodes)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addNodeChildNode(mappingSession, childNodeReq);
                    else deserializedGate.addNodeChildNode(childNodeReq);
            }

            if (jsonDeserializedGate.getNode().getNodeTwinNodesID()!=null) {
                List<Node> twinNodesToDelete = new ArrayList<>();
                for (Node existingTwinNode : deserializedGate.getTwinNodes())
                    if (!reqNodeTwinNodes.contains(existingTwinNode))
                        twinNodesToDelete.add(existingTwinNode);
                for (Node twinNodeToDelete : twinNodesToDelete) {
                    if (mappingSession!=null) {
                        ((SProxGate)deserializedGate).removeTwinNode(mappingSession, twinNodeToDelete);
                        ((SProxGate)twinNodeToDelete).removeTwinNode(mappingSession, deserializedGate);
                    } else {
                        deserializedGate.removeTwinNode(twinNodeToDelete);
                        twinNodeToDelete.removeTwinNode(deserializedGate);
                    }
                }

                for (Node twinNodeReq : reqNodeTwinNodes) {
                    if (mappingSession!=null) {
                        ((SProxGate)deserializedGate).addTwinNode(mappingSession, twinNodeReq);
                        ((SProxGate)twinNodeReq).addTwinNode(mappingSession, deserializedGate);
                    } else {
                        deserializedGate.addTwinNode(twinNodeReq);
                        twinNodeReq.addTwinNode(deserializedGate);
                    }
                }
            }

            if (jsonDeserializedGate.getNode().getNodeEndpointsID()!=null) {
                List<Endpoint> endpointsToDelete = new ArrayList<>();
                for (Endpoint existingEndpoint : deserializedGate.getNodeEndpoints())
                    if (!reqNodeEndpoints.contains(existingEndpoint))
                        endpointsToDelete.add(existingEndpoint);
                for (Endpoint endpointToDelete : endpointsToDelete)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).removeEndpoint(mappingSession, endpointToDelete);
                    else deserializedGate.removeEndpoint(endpointToDelete);

                for (Endpoint endpointReq : reqNodeEndpoints)
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addEndpoint(mappingSession, endpointReq);
                    else deserializedGate.addEndpoint(endpointReq);
            }

            if (jsonDeserializedGate.getNode().getNodeProperties()!=null) {
                if (deserializedGate.getNodeProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedGate.getNodeProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        if (mappingSession!=null) ((SProxGate)deserializedGate).removeNodeProperty(mappingSession, propertyToDelete);
                        else deserializedGate.removeNodeProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    if (mappingSession!=null) ((SProxGate)deserializedGate).addNodeProperty(mappingSession, propertyKey, reqProperties.get(propertyKey));
                    else deserializedGate.addNodeProperty(propertyKey, reqProperties.get(propertyKey));
            }
            ret.setDeserializedObject(deserializedGate);
        }

        return ret;
    }

    @Override
    public N createNode(Session session, String nodeName, String containerID, String parentNodeID) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, OP_CREATE_NODE, new Object[]{nodeName, containerID, parentNodeID});
        return ret;
    }

    @Override
    public void deleteNode(Session session, String nodeID) throws MappingDSException {
        if (session != null && session.isRunning())
            session.execute(this, OP_DELETE_NODE, new Object[]{nodeID});
    }

    @Override
    public N getNode(Session session, String id) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, OP_GET_NODE, new Object[]{id});
        return ret;
    }

    @Override
    public N getNodeByEndpointURL(Session session, String endpointURL) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, OP_GET_NODE_BY_EPURL, new Object[]{endpointURL});
        return ret;
    }

    @Override
    public N getNodeByName(Session session, Node parentNode, String nodeName) throws MappingDSException {
        N ret = null;
        if (session != null && session.isRunning())
            ret = (N) session.execute(this, OP_GET_NODE_BY_NAME, new Object[]{parentNode, nodeName});
        return ret;
    }

    @Override
    public Set<N> getNodes(Session session, String key, Object value) throws MappingDSException {
        Set<N> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<N>) session.execute(this, OP_GET_NODES, new Object[]{key, value});
        return ret;
    }

    @Override
    public Set<N> getNodes(Session session, String selector) throws MappingDSException {
        Set<N> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<N>) session.execute(this, OP_GET_NODES, new Object[]{selector});
        return ret;
    }


}
