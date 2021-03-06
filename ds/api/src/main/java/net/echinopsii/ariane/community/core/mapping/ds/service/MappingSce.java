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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.Dictionary;
import java.util.Set;

public interface MappingSce {
    String Q_MAPPING_SCE_SERVICE = "ARIANE_MAPPING_SERVICE_Q";

    String GLOBAL_PARAM_OBJ_ID = "ID";
    String GLOBAL_PARAM_OBJ_NONE = "NONE";
    String GLOBAL_PARAM_PAYLOAD = "payload";
    String GLOBAL_PARAM_SELECTOR = "selector";
    String GLOBAL_PARAM_PROP_NAME = "propertyName";
    String GLOBAL_PARAM_PROP_TYPE = "propertyType";
    String GLOBAL_PARAM_PROP_VALUE = "propertyValue";
    String GLOBAL_PARAM_PROP_FIELD = "propertyField";

    String OP_GET_NODE_BY_NAME = "getNodeByName";
    String OP_GET_GATE_BY_NAME = "getGateByName";
    String OP_GET_ENDPOINTS_BY_SELECTOR = "getEndpointsBySelector";
    String OP_GET_LINKS_BY_SOURCE_EP = "getLinksBySourceEP";
    String OP_GET_LINKS_BY_DESTINATION_EP = "getLinksByDestinationEP";
    String OP_GET_LINK_BY_SOURCE_EP_AND_DESTINATION_EP = "getLinkBySourceEPandDestinationEP";
    String OP_GET_LINK_BY_SOURCE_EP_AND_TRANSPORT = "getMulticastLinkBySourceEPAndTransport";

    MapSce getMapSce();

    SProxClusterSce<? extends Cluster> getClusterSce();

    SProxContainerSce<? extends Container> getContainerSce();

    SProxGateSce<? extends Gate> getGateSce();

    SProxNodeSce<? extends Node> getNodeSce();

    SProxEndpointSce<? extends Endpoint> getEndpointSce();

    SProxLinkSce<? extends Link> getLinkSce();

    SProxTransportSce<? extends Transport> getTransportSce();

    Node getNodeByName(Container container, String nodeName) throws MappingDSException;

    Gate getGateByName(Container container, String nodeName) throws MappingDSException;

    Set<Endpoint> getEndpointsBySelector(Container container, String selector) throws MappingDSException;

    Set<Endpoint> getEndpointsBySelector(Node node, String selector) throws MappingDSException;

    Set<Link> getLinksBySourceEP(Endpoint endpoint) throws MappingDSException;

    Set<Link> getLinksByDestinationEP(Endpoint endpoint) throws MappingDSException;

    Link getLinkBySourceEPandDestinationEP(Endpoint esource, Endpoint edest) throws MappingDSException;

    Link getMulticastLinkBySourceEPAndTransport(Endpoint esource, Transport transport) throws MappingDSException;

    boolean init(Dictionary<Object, Object> properties);

    boolean start();

    boolean stop();
}