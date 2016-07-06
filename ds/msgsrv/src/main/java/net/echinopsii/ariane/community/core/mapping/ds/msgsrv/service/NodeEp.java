/**
 * Mapping Messaging Server
 * Session service messaging endpoint
 * Copyright (C) 27/05/16 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Endpoint;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxNode;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.EndpointJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.NodeJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.MappingMsgsrvBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.msgsrv.momsp.MappingMsgsrvMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.NodeSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxNodeSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;

public class NodeEp {

    static class NodeWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Object oOperation = message.get(MappingSce.GLOBAL_OPERATION_FDN);
            String operation;
            String sid;
            String nid;
            String name;
            String pc_id;
            String pn_id;
            String payload;
            String cn_id;
            String tn_id;
            String ep_id;
            String selector;
            String ep_url;
            String prop_field;
            String prop_name;
            Session session = null;


            if (oOperation==null)
                operation = MappingSce.GLOBAL_OPERATION_NOT_DEFINED;
            else
                operation = oOperation.toString();

            sid = (String) message.get(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID);
            if (sid != null) {
                session = MappingMsgsrvBootstrap.getMappingSce().getSessionRegistry().get(sid);
                if (session == null) {
                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : session with provided id not found");
                    return message;
                }
            }

            try {
                switch (operation) {
                    case NodeSce.OP_CREATE_NODE:
                        name = (String) message.get(NodeSce.PARAM_NODE_NAME);
                        pc_id = (String) message.get(Container.TOKEN_CT_ID);
                        pn_id = (String) message.get(NodeSce.PARAM_NODE_PNID);
                        payload = (String) message.get(MappingSce.GLOBAL_PARAM_PAYLOAD);
                        if (payload!=null) {
                            DeserializedPushResponse deserializationResponse = SProxNodeSceAbs.pushDeserializedNode(
                                    NodeJSON.JSON2Node(payload),
                                    session,
                                    MappingMsgsrvBootstrap.getMappingSce()
                            );
                            if (deserializationResponse.getErrorMessage()!=null) {
                                String result = deserializationResponse.getErrorMessage();
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else if (deserializationResponse.getDeserializedObject()!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                NodeJSON.oneNode2JSON((Node)deserializationResponse.getDeserializedObject(), outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                String result = "ERROR while deserializing !";
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SERVER_ERR);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            }
                        } else if (name != null && pc_id != null) {
                            Container pcont = null;
                            if (session != null) pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                            else pcont = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);

                            if (pcont!=null) {
                                Node node = null;
                                Node pnode = null;
                                if (session != null) pnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, pn_id);
                                else pnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(pn_id);

                                if (pnode==null && !pn_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                    message.put(MomMsgTranslator.MSG_ERR, "Parent node not found with provided ID.");
                                    return message;
                                }

                                if (session != null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().createNode(session, name, pc_id, (pnode!=null) ? pn_id : null);
                                else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().createNode(name,pc_id, (pnode!=null) ? pn_id : null);

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_BODY, result);
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Parent container not found with provided ID.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node name and/or parent container ID not provided.");
                        }
                        break;
                    case NodeSce.OP_DELETE_NODE:
                        nid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (nid!=null) {
                            if (session!=null) MappingMsgsrvBootstrap.getMappingSce().getNodeSce().deleteNode(session, nid);
                            else MappingMsgsrvBootstrap.getMappingSce().getNodeSce().deleteNode(nid);

                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node ID not provided.");
                        }
                        break;
                    case NodeSce.OP_GET_NODE:
                    case NodeSce.OP_GET_NODE_BY_EPURL:
                    case NodeSce.OP_GET_NODE_BY_NAME:
                        nid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        ep_url = (String) message.get(Endpoint.TOKEN_EP_URL);
                        name = (String) message.get(NodeSce.PARAM_NODE_NAME);
                        pn_id = (String) message.get(NodeSce.PARAM_NODE_PNID);
                        if (nid!=null || ep_url!=null || (name!=null && pn_id!=null)) {
                            Node node = null;
                            if (nid != null && operation.equals(NodeSce.OP_GET_NODE)) {
                                if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, nid);
                                else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(nid);
                            } else if (ep_url != null && operation.equals(NodeSce.OP_GET_NODE_BY_EPURL)) {
                                if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodeByEndpointURL(session, ep_url);
                                else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodeByEndpointURL(ep_url);
                            } else if (name != null && pn_id!=null && operation.equals(NodeSce.OP_GET_NODE_BY_NAME)) {
                                Node parentNode = null;
                                if (session!=null) parentNode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, pn_id);
                                else parentNode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(pn_id);
                                if (parentNode!=null) {
                                    if (session != null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodeByName(session, parentNode, name);
                                    else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodeByName(parentNode, name);
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Parent node not found with provided ID.");
                                    return message;
                                }
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                switch (operation) {
                                    case NodeSce.OP_GET_NODE:
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node ID not provided.");
                                        break;
                                    case NodeSce.OP_GET_NODE_BY_EPURL:
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : enpoint URL not provided.");
                                        break;
                                    case NodeSce.OP_GET_NODE_BY_NAME:
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node name not provided.");
                                        break;
                                }
                                return message;
                            }

                            if (node!=null) {
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                                message.put(MomMsgTranslator.MSG_ERR, "Node not found.");
                                return message;
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            switch (operation) {
                                case NodeSce.OP_GET_NODE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node ID not provided.");
                                    break;
                                case NodeSce.OP_GET_NODE_BY_EPURL:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : enpoint URL not provided.");
                                    break;
                                case NodeSce.OP_GET_NODE_BY_NAME:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node name or parent node ID not provided.");
                                    break;
                            }
                        }
                        break;
                    case NodeSce.OP_GET_NODES:
                        selector = (message.get(MappingSce.GLOBAL_PARAM_SELECTOR)==null || ((String) message.get(MappingSce.GLOBAL_PARAM_SELECTOR)).equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) ? null : (String) message.get(MappingSce.GLOBAL_PARAM_SELECTOR);
                        prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;

                        HashSet<Node> nodes;
                        if (prop_field!=null) {
                            PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                            Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                            if (session != null) nodes = (HashSet<Node>) MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodes(session, typedPropertyField.getPropertyName(), value);
                            else nodes = (HashSet<Node>) MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodes(typedPropertyField.getPropertyName(), value);
                        } else {
                            if (session != null) nodes = (HashSet<Node>) MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodes(session, selector);
                            else nodes = (HashSet<Node>) MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNodes(selector);
                        }

                        if (nodes!=null) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            NodeJSON.manyNodes2JSONWithTypedProps(nodes, outStream);
                            String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                            message.put(MomMsgTranslator.MSG_BODY, result);
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_NOT_FOUND);
                            message.put(MomMsgTranslator.MSG_ERR, "Nodes not found.");
                        }
                        break;
                    case Node.OP_ADD_NODE_PROPERTY:
                    case Node.OP_REMOVE_NODE_PROPERTY:
                        nid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        if (nid!=null) {
                            Node node;
                            if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, nid);
                            else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(nid);

                            if (node != null) {
                                if (operation.equals(Node.OP_ADD_NODE_PROPERTY)) {
                                    prop_field = (message.containsKey(MappingSce.GLOBAL_PARAM_PROP_FIELD)) ? message.get(MappingSce.GLOBAL_PARAM_PROP_FIELD).toString() : null;
                                    if (prop_field!=null) {
                                        PropertiesJSON.TypedPropertyField typedPropertyField = PropertiesJSON.typedPropertyFieldFromJSON(prop_field);
                                        Object value = ToolBox.extractPropertyObjectValueFromString(typedPropertyField.getPropertyValue(), typedPropertyField.getPropertyType());
                                        if (session != null) ((SProxNode)node).addNodeProperty(session, typedPropertyField.getPropertyName(), value);
                                        else node.addNodeProperty(typedPropertyField.getPropertyName(), value);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property field not provided.");
                                        return message;
                                    }
                                } else {
                                    prop_name = (String) message.get(MappingSce.GLOBAL_PARAM_PROP_NAME);
                                    if (prop_name!=null) {
                                        if (session!=null) ((SProxNode)node).removeNodeProperty(session, prop_name);
                                        else node.removeNodeProperty(prop_name);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : property name not provided.");
                                        return message;
                                    }
                                }

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");

                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, result);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Node not found.");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node ID not provided.");
                        }
                        break;
                    case Node.OP_SET_NODE_NAME:
                    case Node.OP_SET_NODE_CONTAINER:
                    case Node.OP_SET_NODE_PARENT_NODE:
                    case Node.OP_ADD_ENDPOINT:
                    case Node.OP_REMOVE_ENDPOINT:
                    case Node.OP_ADD_NODE_CHILD_NODE:
                    case Node.OP_REMOVE_NODE_CHILD_NODE:
                    case Node.OP_ADD_TWIN_NODE:
                    case Node.OP_REMOVE_TWIN_NODE:
                        nid = (String) message.get(MappingSce.GLOBAL_PARAM_OBJ_ID);
                        name = (String) message.get(NodeSce.PARAM_NODE_NAME);
                        pc_id = (String) message.get(Container.TOKEN_CT_ID);
                        pn_id = (String) message.get(NodeSce.PARAM_NODE_PNID);
                        cn_id = (String) message.get(NodeSce.PARAM_NODE_CNID);
                        tn_id = (String) message.get(NodeSce.PARAM_NODE_TNID);
                        ep_id = (String) message.get(Endpoint.TOKEN_EP_ID);
                        if (nid!=null && (
                                name!=null || pc_id!=null || pn_id!=null || cn_id!=null || tn_id!=null || ep_id!=null
                            )) {

                            Node node;
                            if (session!=null) node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, nid);
                            else node = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(nid);

                            if (node != null) {
                                if (operation.equals(Node.OP_SET_NODE_NAME) && name != null) {
                                    if (session!=null) ((SProxNode)node).setNodeName(session, name);
                                    else node.setNodeName(name);
                                } else if (operation.equals(Node.OP_SET_NODE_CONTAINER) && pc_id != null) {
                                    Container parentContainer ;
                                    if (session!=null) parentContainer = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(session, pc_id);
                                    else parentContainer = MappingMsgsrvBootstrap.getMappingSce().getContainerSce().getContainer(pc_id);

                                    if (parentContainer!=null || pc_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        Container previousParentContainer = node.getNodeContainer();
                                        if (previousParentContainer==null || (previousParentContainer!=null && !previousParentContainer.equals(parentContainer))) {
                                            if (session != null)
                                                ((SProxNode) node).setNodeContainer(session, parentContainer);
                                            else node.setNodeContainer(parentContainer);

                                            if (previousParentContainer != null) {
                                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                                ContainerJSON.oneContainer2JSONWithTypedProps(previousParentContainer, outStream);
                                                String resultPcont = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                                message.put(Node.JOIN_PREVIOUS_PCONTAINER, resultPcont);
                                            }

                                            if (parentContainer != null) {
                                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                                ContainerJSON.oneContainer2JSONWithTypedProps(parentContainer, outStream);
                                                String resultPcont = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                                message.put(Node.JOIN_CURRENT_PCONTAINER, resultPcont);
                                            }
                                        }
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent container with provided id not found");
                                        return message;
                                    }
                                } else if (operation.equals(Node.OP_SET_NODE_PARENT_NODE) && pn_id != null) {
                                    Node parentNode ;
                                    if (session!=null) parentNode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, pn_id);
                                    else parentNode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(pn_id);

                                    if (parentNode!=null || pn_id.equals(MappingSce.GLOBAL_PARAM_OBJ_NONE)) {
                                        Node previousParentNode = node.getNodeParentNode();
                                        if (session!=null) ((SProxNode)node).setNodeParentNode(session, parentNode);
                                        else node.setNodeParentNode(parentNode);

                                        if (previousParentNode!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            NodeJSON.oneNode2JSONWithTypedProps(previousParentNode, outStream);
                                            String resultPnode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Node.JOIN_PREVIOUS_PNODE, resultPnode);
                                        }

                                        if (parentNode!=null) {
                                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                            NodeJSON.oneNode2JSONWithTypedProps(parentNode, outStream);
                                            String resultPnode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                            message.put(Node.JOIN_CURRENT_PNODE, resultPnode);
                                        }
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parent node with provided id not found");
                                        return message;
                                    }
                                } else if ((operation.equals(Node.OP_ADD_ENDPOINT) || operation.equals(Node.OP_REMOVE_ENDPOINT)) && ep_id != null) {
                                    Endpoint endpoint ;
                                    if (session!=null) endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(session, ep_id);
                                    else endpoint = MappingMsgsrvBootstrap.getMappingSce().getEndpointSce().getEndpoint(ep_id);

                                    if (endpoint!=null) {
                                        if (operation.equals(Node.OP_ADD_ENDPOINT)) {
                                            if (session != null) ((SProxNode) node).addEndpoint(session, endpoint);
                                            else node.addEndpoint(endpoint);
                                        } else {
                                            if (session != null) ((SProxNode) node).removeEndpoint(session, endpoint);
                                            else node.removeEndpoint(endpoint);
                                        }

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        EndpointJSON.oneEndpoint2JSONWithTypedProps(endpoint, outStream);
                                        String resultEp = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(MappingDSGraphPropertyNames.DD_NODE_EDGE_ENDPT_KEY, resultEp);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : endpoint with provided id not found");
                                        return message;
                                    }
                                } else if ((operation.equals(Node.OP_ADD_NODE_CHILD_NODE) || operation.equals(Node.OP_REMOVE_NODE_CHILD_NODE)) && cn_id != null) {
                                    Node cnode;
                                    if (session!=null) cnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, cn_id);
                                    else cnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(cn_id);

                                    if (cnode!=null) {
                                        if (operation.equals(Node.OP_ADD_NODE_CHILD_NODE)) {
                                            if (session != null) ((SProxNode) node).addNodeChildNode(session, cnode);
                                            else node.addNodeChildNode(cnode);
                                        } else {
                                            if (session != null) ((SProxNode) node).removeNodeChildNode(session, cnode);
                                            else node.removeNodeChildNode(cnode);
                                        }

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        NodeJSON.oneNode2JSONWithTypedProps(cnode, outStream);
                                        String resultCn = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(MappingDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, resultCn);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : child node with provided id not found");
                                        return message;
                                    }
                                } else if ((operation.equals(Node.OP_ADD_TWIN_NODE) || operation.equals(Node.OP_REMOVE_TWIN_NODE)) && tn_id != null) {
                                    Node tnode;
                                    if (session!=null) tnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(session, tn_id);
                                    else tnode = MappingMsgsrvBootstrap.getMappingSce().getNodeSce().getNode(tn_id);

                                    if (tnode!=null) {
                                        if (operation.equals(Node.OP_ADD_TWIN_NODE)) {
                                            if (session != null) ((SProxNode) node).addTwinNode(session, tnode);
                                            else node.addTwinNode(tnode);
                                        } else {
                                            if (session != null) ((SProxNode) node).removeTwinNode(session, tnode);
                                            else node.removeTwinNode(tnode);
                                        }

                                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                        NodeJSON.oneNode2JSONWithTypedProps(tnode, outStream);
                                        String resultTn = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                        message.put(MappingDSGraphPropertyNames.DD_NODE_EDGE_TWIN_KEY, resultTn);
                                    } else {
                                        message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                        message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : twin node with provided id not found");
                                        return message;
                                    }
                                } else {
                                    message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : parameter inconsistent with operation");
                                    return message;
                                }

                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                NodeJSON.oneNode2JSONWithTypedProps(node, outStream);
                                String resultNode = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SUCCESS);
                                message.put(MomMsgTranslator.MSG_BODY, resultNode);
                            } else {
                                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                                message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : node with provided id not found");
                            }
                        } else {
                            message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_BAD_REQ);
                            switch (operation) {
                                case Node.OP_SET_NODE_NAME:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or name not provided.");
                                    break;
                                case Node.OP_SET_NODE_CONTAINER:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or parent container id not provided.");
                                    break;
                                case Node.OP_SET_NODE_PARENT_NODE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or parent node id not provided.");
                                    break;
                                case Node.OP_ADD_ENDPOINT:
                                case Node.OP_REMOVE_ENDPOINT:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or endpoint id not provided.");
                                    break;
                                case Node.OP_ADD_NODE_CHILD_NODE:
                                case Node.OP_REMOVE_NODE_CHILD_NODE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or child node id not provided.");
                                    break;
                                case Node.OP_ADD_TWIN_NODE:
                                case Node.OP_REMOVE_TWIN_NODE:
                                    message.put(MomMsgTranslator.MSG_ERR, "Bad request (" + operation + ") : id or twin node id not provided.");
                                    break;
                            }
                        }
                        break;
                    case MappingSce.GLOBAL_OPERATION_NOT_DEFINED:
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                        break;
                    default:
                        message.put(MomMsgTranslator.MSG_RC, 1);
                        message.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.put(MomMsgTranslator.MSG_RC, MappingSce.MAPPING_SCE_RET_SERVER_ERR);
                message.put(MomMsgTranslator.MSG_ERR, "Internal server error (" + operation + ") : " + e.getMessage());
            }

            return message;
        }
    }

    public static void start() {
        if (MappingMsgsrvMomSP.getSharedMoMConnection() != null && MappingMsgsrvMomSP.getSharedMoMConnection().isConnected())
            MappingMsgsrvMomSP.getSharedMoMConnection().getServiceFactory().msgGroupRequestService(
                    NodeSce.Q_MAPPING_NODE_SERVICE, new NodeWorker()
            );
    }
}
