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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.domain.*;
import com.spectral.cc.core.mapping.ds.service.MapSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MapSceImpl implements MapSce {

    private static final Logger log = LoggerFactory.getLogger(MapSceImpl.class);
    private MappingSceImpl sce = null;

    public MapSceImpl(MappingSceImpl sce_) {
        this.sce = sce_;
    }

    private static void addContainerToResultMap(Container container,  MapImpl map) {
        map.addContainer(container);
        map.addCluster(container.getContainerCluster());
    }

    private static void addNodeToResultMap(Node node, MapImpl map) {
        log.debug("Add node to result map : " + node.getNodeName());
        map.addNode(node);
        if (node.getNodeContainer()!=null)
            map.addContainer(node.getNodeContainer());
        if (node.getNodeParentNode()!=null)
            addNodeToResultMap(node.getNodeParentNode(), map);
    }

    private static void addEndpointToResultMap(Endpoint endpoint, MapImpl map) {
        log.debug("Add endpoint to result map : " + endpoint.getEndpointURL());
        map.addEndpoint(endpoint);
        if (endpoint.getEndpointParentNode()!=null)
            addNodeToResultMap(endpoint.getEndpointParentNode(), map);
    }

    private static void addLinkToResultMap(Link link, MapImpl map) {
        log.debug("Add link to result map : " + link.getLinkID());
        map.addLink(link);
        if (link.getLinkTransport()!=null)
            map.addTransport(link.getLinkTransport());
    }

    @Override
    public MapImpl getMap(String mapperQuery) {
        MapImpl map = new MapImpl();
        Map<String, String> minimalMap = MappingDSGraphDB.executeQuery(mapperQuery);
        for (String id : minimalMap.keySet()) {
            String type = minimalMap.get(id);
            switch (type) {
                case MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE:
                    Container container = sce.getContainerSce().getContainer(new Long(id.substring(1, id.length())));
                    addContainerToResultMap(container, map);
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                    Node node = sce.getNodeSce().getNode(new Long(id.substring(1,id.length())));
                    addNodeToResultMap(node, map);
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                    Endpoint endpoint = sce.getEndpointSce().getEndpoint(new Long(id.substring(1,id.length())));
                    addEndpointToResultMap(endpoint, map);
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                    Transport transport = sce.getTransportSce().getTransport(new Long(id.substring(1,id.length())));
                    log.debug("Add transport to result map : " + transport.getTransportName());
                    map.addTransport(transport);
                    break;
                case MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY:
                    Link link = sce.getLinkSce().getLink(new Long(id.substring(1,id.length())));
                    addLinkToResultMap(link, map);
                    break;
                default:
                    log.error("Unsupported type {} for object {} in minimal map return !", type, id.substring(1,id.length()));
                    break;
            }
        }
        return map;
    }
}