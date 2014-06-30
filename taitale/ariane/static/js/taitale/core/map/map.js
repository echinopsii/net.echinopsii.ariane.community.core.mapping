// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Map                             │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │										 											  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of						  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the						  │ \\
// │ GNU Affero General Public License for more details.								  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.				  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

define(
    [
        'taitale-helper',
        'taitale-dictionaries',
        'taitale-params',
        'taitale-map-matrix',
        'taitale-container',
        'taitale-node',
        'taitale-endpoint',
        'taitale-transport',
        'taitale-link',
        'taitale-tree',
        'taitale-map-options'
    ],
    function(helper, dictionaries, params, mapMatrix, container, node, endpoint, transport, link, tree) {
        function map(options) {
            var mapWidth  = 0,
                mapHeight = 0,
                mapmatrix = new mapMatrix(options),
                mbrdSpan  = params.map_mbrdSpan,
                zoneSpan  = params.map_zoneSpan,
                linkColor = params.map_linkColor,
                linkBckg  = params.map_linkBckg,
                helper_   = new helper();

            var containerRegistry             = [],
                nodeRegistry                  = [],
                endpointRegistry              = [],
                transportRegistry             = [],
                linkRegistry                  = [],
                treeObjects                   = [],
                sortOrdering                  = 1,
                minMaxLinkedObjectsComparator = function(treeObj1, treeObj2) {
                    return (treeObj2.getLinkedTreeObjectsCount() - treeObj1.getLinkedTreeObjectsCount())*sortOrdering;
                };

            var lTree = null;
            var dic   = new dictionaries();

            this.addContainer = function(JSONContainerDesc) {
                var x=0, y=0;
                if (JSONContainerDesc.containerProperties!=null && JSONContainerDesc.containerProperties.manualCoord!=null) {
                    x=JSONContainerDesc.containerProperties.manualCoord.x;
                    y=JSONContainerDesc.containerProperties.manualCoord.y;
                }
                containerRegistry.push(new container(JSONContainerDesc, x, y));
            };

            this.findContainerByID = function(containerID) {
                for (var i = 0, ii = containerRegistry.length; i < ii; i++ ) {
                    if (containerRegistry[i].getID()==containerID) {
                        return containerRegistry[i];
                    }
                }
            };

            this.addNode = function(JSONNodeDesc) {
                var container = null;
                for (var i = 0, ii = containerRegistry.length; i < ii; i++ ) {
                    var tmpContainer = containerRegistry[i];
                    if (tmpContainer.ID === JSONNodeDesc.nodeContainerID) {
                        container = tmpContainer;
                        break;
                    }
                }
                if (container != null) {
                    nodeRegistry.push(new node(JSONNodeDesc, container));
                } else {
                    helper_.addMsgToGrowl(
                        {
                            severity: 'warn',
                            summary: 'Map parse warning',
                            detail: 'Incorrect JSON map data. Container '.concat(JSONNodeDesc.nodeContainerID).concat(' for node ').concat(JSONNodeDesc.nodeID).concat(' is missing ! <br> This node will be ignored'),
                            sticky: true
                        });
                }
            };

            this.addEndpoint = function(JSONEndpointDesc) {
                var node = null;
                for (var i = 0, ii = nodeRegistry.length; i < ii; i++ ) {
                    var tmpNode = nodeRegistry[i];
                    if (tmpNode.ID === JSONEndpointDesc.endpointParentNodeID) {
                        node = tmpNode;
                        break;
                    }
                }
                if (node != null) {
                    endpointRegistry.push(new endpoint(JSONEndpointDesc,node));
                } else {
                    helper_.addMsgToGrowl(
                        {
                            severity: 'warn',
                            summary: 'Map parse warning',
                            detail: 'Incorrect JSON map data. Node '.concat(JSONEndpointDesc.endpointParentNodeID).concat(' for endpoint ').concat(JSONEndpointDesc.endpointID).concat(' is missing ! <br> This endpoint will be ignored'),
                            sticky: true
                        });
                }
            };

            this.addTransport = function(JSONTransportDesc) {
                transportRegistry.push(new transport(JSONTransportDesc));
            }

            this.addLink = function (JSONLinkDesc) {
                var sEP = null, dEP = null, TR = null;
                for (var i = 0, ii = endpointRegistry.length; i < ii ; i++) {
                    var tmpEP = endpointRegistry[i];
                    if (tmpEP.epID === JSONLinkDesc.linkSEPID)
                        sEP = tmpEP;
                    else if (tmpEP.epID === JSONLinkDesc.linkTEPID)
                        dEP = tmpEP;

                    if (sEP!=null && dEP!=null)
                        break;
                }

                for (var i = 0, ii = transportRegistry.length; i < ii; i++) {
                    var tmpTR = transportRegistry[i];
                    if (tmpTR.getID() === JSONLinkDesc.linkTRPID) {
                        TR = tmpTR;
                        break;
                    }
                }

                if (sEP!=null && dEP!=null /*UP JSON TESTS FIRST - && TR!=null*/) {
                    linkRegistry.push(new link(JSONLinkDesc.linkID, sEP, dEP, TR, linkColor, linkBckg));
                } else if (sEP!=null && TR!=null && TR.isMulticast()){
                    linkRegistry.push(new link(JSONLinkDesc.linkID, sEP, null, TR, linkColor, linkBckg));
                } else {
                    if (TR==null) {
                        helper_.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Map parse warning',
                                detail: 'Incorrect JSON map data. Transport '.concat(JSONLinkDesc.linkTRPID).concat(' for link ').concat(JSONLinkDesc.linkID).concat(' is missing ! <br> This link will be ignored.'),
                                sticky: true
                            });
                    } else if (TR.isMulticast() && sEP==null) {
                        helper_.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Map parse warning',
                                detail: 'Incorrect JSON map data. Source endpoint '.concat(JSONLinkDesc.linkSEPID).concat(' for multicast link ').concat(JSONLinkDesc.linkID).concat(' is missing ! <br> This link will be ignored.'),
                                sticky: true
                            });
                    } else if (!TR.isMulticast() && (sEP==null || dEP==null)) {
                        helper_.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Map parse warning',
                                detail: 'Incorrect JSON map data. Source endpoint '.concat(JSONLinkDesc.linkSEPID).concat(' or target endpoint ').concat(JSONLinkDesc.linkTEPID).
                                    concat('<br>for multicast link ').concat(JSONLinkDesc.linkID).concat(' is missing ! <br> This link will be ignored.'),
                                sticky: true
                            });
                    }
                }
            };

            this.parseJSON = function(JSONmapDesc) {
                for (var i = 0, ii = JSONmapDesc.containers.length; i < ii; i++ ) {
                    this.addContainer(JSONmapDesc.containers[i]);
                }

                for (var i = 0, ii = JSONmapDesc.nodes.length; i < ii; i++ ) {
                    this.addNode(JSONmapDesc.nodes[i]);
                }

                for (var i = 0, ii = JSONmapDesc.endpoints.length; i < ii; i++ ) {
                    this.addEndpoint(JSONmapDesc.endpoints[i]);
                }

                if (JSONmapDesc.transports!=null) {
                    for (var i = 0, ii = JSONmapDesc.transports.length; i < ii; i++) {
                        this.addTransport(JSONmapDesc.transports[i]);
                    }
                }

                for (var i = 0, ii = JSONmapDesc.links.length; i < ii; i++ ) {
                    this.addLink(JSONmapDesc.links[i]);
                }
            };

            this.buildMap = function() {
                // first : place nodes in container
                for (var j = 0, jj = nodeRegistry.length; j < jj; j++) {
                    nodeRegistry[j].placeInContainer();
                }
                // second : define container size & max size
                for (var j = 0, jj = containerRegistry.length; j < jj; j++) {
                    containerRegistry[j].setSize();
                    containerRegistry[j].setMaxSize();
                }
                var layout = options.getLayout();
                switch (layout) {
                    case dic.mapLayout.NTWWW:
                        // third 0 : populate DC, Area and Lan registries and enrich the objects
                        for (var j = 0, jj = containerRegistry.length; j < jj; j++) {
                            mapmatrix.populateLayoutRegistries(containerRegistry[j]);
                        }
                        // third 1 : place container and the linked bus into the map matrix
                        for (var j = 0, jj = containerRegistry.length; j < jj; j++) {
                            mapmatrix.addContainerZone(containerRegistry[j]);
                        }
                        // third 2 : define map objects size and position
                        this.defineSize();
                        this.definePoz();
                        break;
                    case dic.mapLayout.MANUAL:
                        mapWidth  = 1800;
                        mapHeight = 800;
                        for (var j = 0, jj = containerRegistry.length; j < jj; j++) {
                            containerRegistry[j].definedNodesPoz();
                        }
                        break;
                    case dic.mapLayout.TREE:
                        // third 0 : sort all tree lists
                        sortOrdering = options.getRootTreeSorting();
                        for (var i = 0, ii = treeObjects.length; i<ii; i++) {
                            treeObjects[i].setSortOrdering(options.getSubTreesSorting());
                            treeObjects[i].sortLinkedTreeObjects();
                        }
                        containerRegistry.sort(minMaxLinkedObjectsComparator);
                        // third 1 : define the tree with objects
                        // TODO: manage multi tree
                        lTree = new tree();
                        lTree.loadTree(containerRegistry[0]);
                        // third 2 : define map objects position
                        lTree.definePoz();
                        break;
                }
                // fourth : define the links
                for (var i = 0, ii = linkRegistry.length; i < ii; i++) {
                    linkRegistry[i].linkEp();
                }
            };

            this.defineSize = function () {
                mapmatrix.defineMtxZoneSize();
                mapmatrix.defineMapContentSize();
                mapWidth = mbrdSpan*2 + mapmatrix.getMapContentSize().width + mapmatrix.getMtxSize().x*(zoneSpan-1);
                mapHeight = mbrdSpan*2 + mapmatrix.getMapContentSize().height + mapmatrix.getMtxSize().y*(zoneSpan-1);
            };

            this.definePoz = function () {
                mapmatrix.defineMtxZonePoz(mbrdSpan, zoneSpan);
            };

            this.getMapSize = function () {
                return {
                    width : mapWidth,
                    height: mapHeight
                };
            };

            this.setMapWidth = function(width) {
                mapWidth = width;
            };

            this.setMapHeight = function(height) {
                mapHeight = height;
            };

            this.isMapElementMoving = function () {
                for (var i = 0, ii = containerRegistry.length; i < ii; i++) {
                    if (containerRegistry[i].isMoving)
                        return true;
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (var j = 0, jj = linkedBus.length; j<jj; j++) {
                        if (linkedBus[j].isMoving)
                            return true;
                    }
                }
                for (var i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    if (nodeRegistry[i].isMoving)
                        return true;
                }
                for (var i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    if (endpointRegistry[i].isMoving)
                        return true;
                }
                return mapmatrix.isMoving();
            };

            this.isEditionMode = function() {
                return (options.getMode()==dic.mapMode.EDITION)
            };

            this.print = function (r) {
                if (options.getLayout()===dic.mapLayout.NTWWW)
                    mapmatrix.printMtx(r);

                for (var i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].print(r);
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (var j = 0, jj = linkedBus.length; j<jj; j++)
                        linkedBus[j].print(r);
                }

                for (var i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    nodeRegistry[i].print(r);
                }

                for (var i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].print(r);
                }

                for (var i = 0, ii = linkRegistry.length; i < ii; i++) {
                    linkRegistry[i].print(r);
                }

                for (var i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].toFront();
                }

                for (var i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    nodeRegistry[i].toFront();
                }

                for (var i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].toFront();
                }
            };

            this.displayDC = function(display) {
                mapmatrix.displayDC(display);
            };

            this.displayArea = function(display) {
                mapmatrix.displayArea(display);
            };

            this.displayLan = function(display) {
                mapmatrix.displayLan(display);
            };

            this.rebuildMapTreeLayout = function() {
                lTree.reloadTree(containerRegistry[0]);
                lTree.definePoz();
                for (var i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].resetPoz();
                }
                for (var i = 0, ii = linkRegistry.length; i < ii; i++) {
                    linkRegistry[i].linkEp();
                }
            };

            this.sortRootTree = function(sort) {
                sortOrdering = sort;
                containerRegistry.sort(minMaxLinkedObjectsComparator);
            };

            this.sortSubTrees = function(sort) {
                for (var i = 0, ii = containerRegistry.length; i<ii; i++) {
                    containerRegistry[i].setSortOrdering(sort);
                    containerRegistry[i].sortLinkedTreeObjects();
                }
            };
        };

        return map;
    });