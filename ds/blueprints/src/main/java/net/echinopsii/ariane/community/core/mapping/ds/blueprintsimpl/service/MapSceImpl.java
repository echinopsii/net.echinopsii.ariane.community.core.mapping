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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.MapImpl;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.ds.json.service.MapJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.MapSce;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MapSceImpl implements MapSce {

    private static final Logger log = MomLoggerFactory.getLogger(MapSceImpl.class);
    private MappingSceImpl sce = null;

    public MapSceImpl(MappingSceImpl sce_) {
        this.sce = sce_;
    }

    private static void addContainerToResultMap(Container container,  MapImpl map) {
        map.addContainer(container);
        if (container.getContainerCluster()!=null)
            map.addCluster(container.getContainerCluster());
    }

    private static void addNodeToResultMap(Node node, MapImpl map) {
        log.debug("Add node to result map : " + node.getNodeName());
        map.addNode(node);
        if (node.getNodeContainer()!=null)
            addContainerToResultMap(node.getNodeContainer(), map);
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

    private MapImpl getMap(String mapperQuery) throws MappingDSException {
        MapImpl map = new MapImpl();
        Map<String, String> minimalMap = MappingDSGraphDB.executeQuery(mapperQuery);

        for (String id : minimalMap.keySet()) {
            String type = minimalMap.get(id);
            switch (type) {
                case MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE:
                    Container container = sce.getContainerSce().getContainer(id.substring(1, id.length()));
                    if (container != null) addContainerToResultMap(container, map);
                    else log.warn("Container " + id + " not found !");
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE:
                case MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE:
                    Node node = sce.getNodeSce().getNode(id.substring(1,id.length()));
                    if (node != null) addNodeToResultMap(node, map);
                    else log.warn("Node " + id + " not found !");
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE:
                    Endpoint endpoint = sce.getEndpointSce().getEndpoint(id.substring(1,id.length()));
                    if (endpoint != null) addEndpointToResultMap(endpoint, map);
                    else log.warn("Endpoint " + id + " not found !");
                    break;
                case MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE:
                    Transport transport = sce.getTransportSce().getTransport(id.substring(1,id.length()));
                    if (transport != null) map.addTransport(transport);
                    else log.warn("Transport " + id + " not found !");
                    break;
                case MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY:
                    Link link = sce.getLinkSce().getLink(id.substring(1,id.length()));
                    if (link != null) addLinkToResultMap(link, map);
                    else log.warn("Link " + id + " not found !");
                    break;
                default:
                    log.error("Unsupported type {} for object {} in minimal map return !", type, id.substring(1,id.length()));
                    break;
            }
        }

        HashMap<String, ArrayList<Container>> containerByCluster = new HashMap<>();
        for (Container container : map.getContainers()) {
            if (container.getContainerCluster()!=null) {
                if (containerByCluster.get(container.getContainerCluster().getClusterName())==null) {
                    ArrayList<Container> containers = new ArrayList<>();
                    containers.add(container);
                    containerByCluster.put(container.getContainerCluster().getClusterName(), containers);
                } else containerByCluster.get(container.getContainerCluster().getClusterName()).add(container);
            }
        }

        for (ArrayList<Container> containers : containerByCluster.values()) {
            if (containers.size()>1) {
                ArrayList<Endpoint> clusterEndpoints = new ArrayList<>();
                for (Container container : containers) {
                    for (Gate gate : container.getContainerGates()) {
                        if (gate.getNodeName().contains("cluster")) {
                            addNodeToResultMap(gate, map);
                            for (Endpoint endpoint : gate.getNodeEndpoints()) {
                                addEndpointToResultMap(endpoint, map);
                                for (Endpoint endpointToLink : clusterEndpoints) {
                                    Link clusterLink = sce.getGlobalRepo().findLinkBySourceEPandDestinationEP((EndpointImpl)endpoint, (EndpointImpl)endpointToLink);
                                    if (clusterLink==null) clusterLink = sce.getGlobalRepo().findLinkBySourceEPandDestinationEP((EndpointImpl)endpointToLink, (EndpointImpl)endpoint);
                                    if (clusterLink!=null) addLinkToResultMap(clusterLink, map);
                                }
                                clusterEndpoints.add(endpoint);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public String getMapJSON(String mapperQuery) throws MappingDSException, IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        net.echinopsii.ariane.community.core.mapping.ds.service.tools.Map map = this.getMap(mapperQuery);
        MapJSON.allMap2JSON((HashSet<Container>) map.getContainers(),
                    (HashSet<Node>) map.getNodes(),
                    (HashSet<Endpoint>) map.getEndpoints(),
                    (HashSet<Link>) map.getLinks(),
                    (HashSet<Transport>) map.getTransports(), outStream);
        return ToolBox.getOuputStreamContent(outStream, "UTF-8");
    }
}