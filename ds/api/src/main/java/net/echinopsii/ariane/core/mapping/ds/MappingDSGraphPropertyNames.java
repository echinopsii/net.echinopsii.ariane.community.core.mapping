/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
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

package net.echinopsii.ariane.core.mapping.ds;

public final class MappingDSGraphPropertyNames {

    public final static String DD_GRAPH_VERTEX_ID = "MappingGraphVertexID";
    public final static String DD_GRAPH_EDGE_ID = "MappingGraphEdgeID";
    public final static String DD_GRAPH_VERTEX_FREE_IDS_KEY = "MappingGraphFreeVertexIDs";
    public final static String DD_GRAPH_EDGE_FREE_IDS_KEY = "MappingGraphFreeEdgeIDs";
    public final static String DD_GRAPH_VERTEX_MAXCUR_KEY = "MappingGraphVertexMaxCursor";
    public final static String DD_GRAPH_EDGE_MAXCUR_KEY = "MappingGraphEdgeMaxCursor";
    public final static String DD_GRAPH_VERTEX_TYPE_KEY = "MappingGraphVertexType"; //could be cluster, container, gate, node, endpoint, transport
    public final static String DD_GRAPH_EDGE_OWNS_LABEL_KEY = "owns";
    public final static String DD_GRAPH_EDGE_TWIN_LABEL_KEY = "twin";
    public final static String DD_GRAPH_EDGE_LINK_LABEL_KEY = "link";

    public final static String DD_TYPE_CLUSTER_VALUE = "cluster";
    public static final String DD_CLUSTER_NAME_KEY = "clusterName";
    public static final String DD_CLUSTER_EDGE_CONT_KEY = "clusterContainers";

    public final static String DD_TYPE_CONTAINER_VALUE = "container";
    public static final String DD_CONTAINER_COMPANY_KEY = "containerCompany";
    public static final String DD_CONTAINER_PRODUCT_KEY = "containerProduct";
    public static final String DD_CONTAINER_TYPE_KEY = "containerType";
    public static final String DD_CONTAINER_PROPS_KEY = "containerProperties";
    public static final String DD_CONTAINER_PAGATE_KEY = "containerPrimaryAdminGate";
    public static final String DD_CONTAINER_GATEURI_KEY = "containerGateURI";
    public static final String DD_CONTAINER_CLUSTER_KEY = "containerCluster";
    public static final String DD_CONTAINER_PCONTER_KEY = "containerParentContainer";
    public static final String DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY = "containerChildContainers";
    public static final String DD_CONTAINER_EDGE_NODE_KEY = "containerNodes";
    public static final String DD_CONTAINER_EDGE_GATE_KEY = "containerGates";

    public static final String DD_TYPE_NODE_VALUE = "node";
    public static final String DD_NODE_NAME_KEY = "nodeName";
    public static final String DD_NODE_DEPTH_KEY = "nodeDepth";
    public static final String DD_NODE_PROPS_KEY = "nodeProperties";
    public static final String DD_NODE_CONT_KEY = "nodeContainer";
    public static final String DD_NODE_PNODE_KEY = "nodeParentNode";
    public static final String DD_NODE_EDGE_CHILD_KEY = "nodeChildNode";
    public static final String DD_NODE_EDGE_TWIN_KEY = "nodeTwinNode";
    public static final String DD_NODE_EDGE_ENDPT_KEY = "nodeEndpoint";

    public static final String DD_TYPE_GATE_VALUE = "gate";
    public static final String DD_GATE_PAEP_KEY = "containerGatePrimaryAdminEndpoint";

    public static final String DD_TYPE_ENDPOINT_VALUE = "endpoint";
    public static final String DD_ENDPOINT_URL_KEY = "endpointURL";
    public static final String DD_ENDPOINT_PROPS_KEY = "endpointProperties";
    public static final String DD_ENDPOINT_PNODE_KEY = "endpointParentNode";
    public static final String DD_ENDPOINT_EDGE_TWIN_KEY = "endpointTwinEndpoint";

    public static final String DD_TYPE_TRANSPORT_VALUE = "transport";
    public static final String DD_TRANSPORT_NAME_KEY = "transportName";
    public static final String DD_TRANSPORT_PROPS_KEY = "transportProperties";

    public static final String DD_LINK_SOURCE_EP_KEY = "linkEndpointSource";
    public static final String DD_LINK_SOURCE_EP_REST_KEY = "linkSEPID";
    public static final String DD_LINK_TARGET_EP_KEY = "linkEndpointTarget";
    public static final String DD_LINK_TARGET_EP_REST_KEY = "linkTEPID";
    public static final String DD_LINK_UPLINK_KEY = "linkUpLink";
    public static final String DD_LINK_SUBLINKS_KEY = "linkSubLinks";
    public static final String DD_LINK_TRANSPORT_KEY = "linkTransport";
    public static final String DD_LINK_TRANSPORT_REST_KEY = "linkTRPID";
}
