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
        'taitale-btree',
        'taitale-map-options'
    ],
    function(helper, dictionaries, params, mapMatrix, container, node, endpoint, transport, link, tree, btree) {
        function map(options) {
            var mapWidth  = 0,
                mapHeight = 0,
                mapTopLeftX = 0,
                mapTopLeftY = 0,
                mapBottomRightX = 0,
                mapBottomRightY = 0,
                mapmatrix = new mapMatrix(options),
                mbrdSpan  = params.map_mbrdSpan,
                zoneSpan  = params.map_zoneSpan,
                linkColor = params.map_linkColor,
                linkBckg  = params.map_linkBckg,
                helper_   = new helper(),
                options_  = options;

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
                var cont, x=0, y=0;
                //noinspection JSUnresolvedVariable
                if (JSONContainerDesc.containerProperties!=null && JSONContainerDesc.containerProperties.manualCoord!=null) {
                    //noinspection JSUnresolvedVariable
                    x=JSONContainerDesc.containerProperties.manualCoord.x;
                    //noinspection JSUnresolvedVariable
                    y=JSONContainerDesc.containerProperties.manualCoord.y;
                }
                cont = new container(JSONContainerDesc, x, y);
                containerRegistry.push(cont);
                treeObjects.push(cont)
            };

            /*
            this.findContainerByID = function(containerID) {
                for (var i = 0, ii = containerRegistry.length; i < ii; i++ ) {
                    if (containerRegistry[i].getID()==containerID) {
                        return containerRegistry[i];
                    }
                }
            };
            */

            var childNodesWaitingParent = [];
            this.addObject = function(JSONNodeDesc) {
                var container = null;
                var i, ii, j, jj;

                for (i = 0, ii = containerRegistry.length; i < ii; i++ ) {
                    var tmpContainer = containerRegistry[i];
                    //noinspection JSUnresolvedVariable
                    if (tmpContainer.ID === JSONNodeDesc.nodeContainerID) {
                        container = tmpContainer;
                        break;
                    }
                }

                if (container != null) {
                    var nodeToPush = new node(JSONNodeDesc, container);
                    nodeRegistry.push(nodeToPush);
                    if (nodeToPush.npID!=0)
                        childNodesWaitingParent.push(nodeToPush);

                    var childNodesFoundParent = [];

                    for (j = 0, jj = childNodesWaitingParent.length; j < jj; j++) {
                        for (i = 0, ii = nodeRegistry.length; i < ii; i++) {
                            var possibleParentNode = nodeRegistry[i];
                            var waitingParentNode = childNodesWaitingParent[j];
                            if (possibleParentNode.ID==waitingParentNode.npID) {
                                waitingParentNode.nodeParentNode = possibleParentNode;
                                childNodesFoundParent.push(waitingParentNode);
                            }
                        }
                    }

                    for (i = 0, ii = childNodesFoundParent.length; i < ii; i++)
                        childNodesWaitingParent.splice(childNodesWaitingParent.indexOf(childNodesFoundParent[i]),1);
                } else {
                    //noinspection JSUnresolvedVariable
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
                    //noinspection JSUnresolvedVariable
                    if (tmpNode.ID === JSONEndpointDesc.endpointParentNodeID) {
                        node = tmpNode;
                        break;
                    }
                }
                if (node != null) {
                    endpointRegistry.push(new endpoint(JSONEndpointDesc,node));
                } else {
                    //noinspection JSUnresolvedVariable
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
            };

            this.addLink = function (JSONLinkDesc) {
                var i, ii;
                var sEP = null, dEP = null, TR = null;
                for (i = 0, ii = endpointRegistry.length; i < ii ; i++) {
                    var tmpEP = endpointRegistry[i];
                    //noinspection JSUnresolvedVariable
                    if (tmpEP.epID === JSONLinkDesc.linkSEPID)
                        sEP = tmpEP;
                    else { //noinspection JSUnresolvedVariable
                        if (tmpEP.epID === JSONLinkDesc.linkTEPID)
                                                dEP = tmpEP;
                    }

                    if (sEP!=null && dEP!=null)
                        break;
                }

                for (i = 0, ii = transportRegistry.length; i < ii; i++) {
                    var tmpTR = transportRegistry[i];
                    //noinspection JSUnresolvedVariable
                    if (tmpTR.getID() === JSONLinkDesc.linkTRPID) {
                        TR = tmpTR;
                        break;
                    }
                }

                if (sEP!=null && dEP!=null /*UP JSON TESTS FIRST - && TR!=null*/) {
                    //noinspection JSUnresolvedVariable
                    linkRegistry.push(new link(JSONLinkDesc.linkID, sEP, dEP, TR, linkColor, linkBckg));
                } else if (sEP!=null && TR!=null && TR.isMulticast()){
                    //noinspection JSUnresolvedVariable
                    linkRegistry.push(new link(JSONLinkDesc.linkID, sEP, null, TR, linkColor, linkBckg));
                } else {
                    if (TR==null) {
                        //noinspection JSUnresolvedVariable
                        helper_.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Map parse warning',
                                detail: 'Incorrect JSON map data. Transport '.concat(JSONLinkDesc.linkTRPID).concat(' for link ').concat(JSONLinkDesc.linkID).concat(' is missing ! <br> This link will be ignored.'),
                                sticky: true
                            });
                    } else if (TR.isMulticast() && sEP==null) {
                        //noinspection JSUnresolvedVariable
                        helper_.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Map parse warning',
                                detail: 'Incorrect JSON map data. Source endpoint '.concat(JSONLinkDesc.linkSEPID).concat(' for multicast link ').concat(JSONLinkDesc.linkID).concat(' is missing ! <br> This link will be ignored.'),
                                sticky: true
                            });
                    } else if (!TR.isMulticast() && (sEP==null || dEP==null)) {
                        //noinspection JSUnresolvedVariable
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
                var i, ii;
                for (i = 0, ii = JSONmapDesc.containers.length; i < ii; i++ )
                    this.addContainer(JSONmapDesc.containers[i]);

                for (i = 0, ii = JSONmapDesc.nodes.length; i < ii; i++ )
                    this.addObject(JSONmapDesc.nodes[i]);

                for (i = 0, ii = nodeRegistry.length; i < ii; i++)
                    nodeRegistry[i].defineHeapNodes();

                //noinspection JSUnresolvedVariable
                for (i = 0, ii = JSONmapDesc.endpoints.length; i < ii; i++ )
                    //noinspection JSUnresolvedVariable
                    this.addEndpoint(JSONmapDesc.endpoints[i]);

                //noinspection JSUnresolvedVariable
                if (JSONmapDesc.transports!=null)
                    //noinspection JSUnresolvedVariable
                    for (i = 0, ii = JSONmapDesc.transports.length; i < ii; i++)
                        //noinspection JSUnresolvedVariable
                        this.addTransport(JSONmapDesc.transports[i]);

                for (i = 0, ii = JSONmapDesc.links.length; i < ii; i++ )
                    this.addLink(JSONmapDesc.links[i]);

                for (i = 0, ii = transportRegistry.length; i < ii; i++) {
                    var mbusRegistry = transportRegistry[i].getMulticastBusRegistry()
                    for (var j = 0, jj = mbusRegistry.length; j < jj; j++) {
                        treeObjects.push(mbusRegistry[j])
                    }
                }
            };

            this.buildMap = function() {
                var i, ii, j, jj;

                // first : place nodes in container (first placement)
                for (j = 0, jj = nodeRegistry.length; j < jj; j++)
                    nodeRegistry[j].placeIn();

                // second : define container max size
                for (j = 0, jj = containerRegistry.length; j < jj; j++)
                    containerRegistry[j].defineMaxSize();



                // third : layout policy
                var layout = options.getLayout();
                switch (layout) {
                    case dic.mapLayout.NTWWW:
                        containerRegistry.sort(minMaxLinkedObjectsComparator);
                        // third 0 : populate DC, Area and Lan registries and enrich the objects
                        for (j = 0, jj = containerRegistry.length; j < jj; j++)
                            mapmatrix.populateLayoutRegistries(containerRegistry[j]);
                        // third 1 : place container and the linked bus into the map matrix
                        for (j = 0, jj = containerRegistry.length; j < jj; j++)
                            mapmatrix.addContainerZone(containerRegistry[j]);
                        // third 2 : define map objects max size and first position
                        mapmatrix.defineMtxZoneMaxSize();
                        mapmatrix.defineMapContentMaxSize();
                        mapmatrix.defineMtxZoneFirstPoz(mbrdSpan, zoneSpan);
                        break;

                    case dic.mapLayout.MANUAL:
                    //    mapWidth  = 1800;
                    //    mapHeight = 800;
                    //    for (j = 0, jj = containerRegistry.length; j < jj; j++)
                    //        containerRegistry[j].definedNodesPoz();
                        break;

                    case dic.mapLayout.TREE:
                    case dic.mapLayout.BBTREE:

                        // third 0 : sort all tree lists
                        sortOrdering = options.getRootTreeSorting();
                        for (i = 0, ii = treeObjects.length; i<ii; i++) {
                            treeObjects[i].setSortOrdering(options.getSubTreesSorting());
                            treeObjects[i].sortLinkedTreeObjects();
                        }
                        containerRegistry.sort(minMaxLinkedObjectsComparator);
                        treeObjects.sort(minMaxLinkedObjectsComparator);
                        // third 1 : define the tree with objects
                        // TODO: manage multi tree
                        if (layout == dic.mapLayout.BBTREE){
                            lTree = new btree();
                            lTree.loadTree(treeObjects[0]);
                        } else {
                            lTree = new tree();
                            lTree.loadTree(containerRegistry[0]);
                        }

                        // third 2 : define map objects position
                        lTree.definePoz();
                        break;
                }

                this.updateObjectFinalPozAndSize();
                this.updateMapSize();

                // fourth : define the links
                for (i = 0, ii = linkRegistry.length; i < ii; i++)
                    linkRegistry[i].linkEp();
            };

            this.updateObjectFinalPozAndSize = function () {
                var j, jj;
                var layout = options.getLayout();

                if (layout === dic.mapLayout.NTWWW)
                    mapmatrix.optimizeMtxCoord();

                // Set final container size
                for (j = 0, jj = containerRegistry.length; j < jj; j++) {
                    containerRegistry[j].defineMtxIntermediatePoz();
                    containerRegistry[j].defineSize();
                }

                if (layout === dic.mapLayout.NTWWW) {
                    // Set final map matrix size and poz
                    mapmatrix.defineMtxZoneSize();
                    mapmatrix.defineMapContentSize();
                    mapmatrix.defineMtxZoneIntermediatePoz(mbrdSpan, zoneSpan);

                    for (j = 0, jj = containerRegistry.length; j < jj; j++) {
                        containerRegistry[j].defineMtxIntermediatePoz();
                        containerRegistry[j].clean();
                        containerRegistry[j].defineSize();
                    }

                    // Set final map matrix size and poz
                    mapmatrix.defineMtxZoneSize();
                    mapmatrix.defineMapContentSize();
                    mapmatrix.defineMtxZoneFinalPoz(mbrdSpan, zoneSpan);
                    mapmatrix.defineMtxZoneSize();
                    mapmatrix.defineMapContentSize();
                } else if (layout == dic.mapLayout.TREE || layout == dic.mapLayout.BBTREE) {
                    for (j = 0, jj=containerRegistry.length; j < jj; j++) {
                        containerRegistry[j].clean();
                        containerRegistry[j].defineSize();
                    }
                    lTree.definePoz();
                }
            };

            this.updateMapSize = function () {
                var layout = options.getLayout();
                //noinspection FallthroughInSwitchStatementJS
                switch (layout) {
                    case dic.mapLayout.NTWWW:
                        mapmatrix.defineMapContentSize();

                        mapWidth = mbrdSpan*2 + mapmatrix.getMapContentSize().width;
                        mapHeight = mbrdSpan*2 + mapmatrix.getMapContentSize().height;

                        mapTopLeftX = mapmatrix.getTopLeftCoords().topLeftX;
                        mapTopLeftY = mapmatrix.getTopLeftCoords().topLeftY;

                        mapBottomRightX = mapmatrix.getBottomRightCoords().bottomRightX;
                        mapBottomRightY = mapmatrix.getBottomRightCoords().bottomRightY;

                        break;

                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.TREE:
                    case dic.mapLayout.BBTREE:
                        var i, ii, j, jj;
                        if (containerRegistry.length > 0) {
                            mapTopLeftX = containerRegistry[0].rectTopLeftX;
                            mapTopLeftY = containerRegistry[0].rectTopLeftY;
                            mapBottomRightX = containerRegistry[0].rectBottomRightX;
                            mapBottomRightY = containerRegistry[0].rectBottomRightY;
                            for (i = 1, ii=containerRegistry.length; i < ii; i++) {
                                var container = containerRegistry[i];
                                if (container.rectTopLeftX < mapTopLeftX)
                                    mapTopLeftX = container.rectTopLeftX;
                                if (container.rectTopLeftY < mapTopLeftY)
                                    mapTopLeftY = container.rectTopLeftY;
                                if (container.rectBottomRightX > mapBottomRightX)
                                    mapBottomRightX = container.rectBottomRightX;
                                if (container.rectBottomRightY > mapBottomRightY)
                                    mapBottomRightY = container.rectBottomRightY;
                            }

                            for (i=0, ii=transportRegistry.length; i < ii; i++) {
                                var transport = transportRegistry[i];
                                if (transport.isMulticast()) {
                                    var multicastBusRegistry = transport.getMulticastBusRegistry();
                                    for(j=0, jj=multicastBusRegistry.length; j < jj; j++) {
                                        var multicastBus = multicastBusRegistry[j];
                                        var topLeftCoord = multicastBus.getBusCoords();
                                        var size = multicastBus.getBusSize();
                                        if (topLeftCoord.x < mapTopLeftX)
                                            mapTopLeftX = topLeftCoord.x;
                                        if (topLeftCoord.y < mapTopLeftY)
                                            mapTopLeftY = topLeftCoord.y;
                                        if (topLeftCoord.x + size.width > mapBottomRightX)
                                            mapBottomRightX = topLeftCoord.x + size.width;
                                        if (topLeftCoord.y + size.height > mapBottomRightY)
                                            mapBottomRightY = topLeftCoord.y + size.height;
                                    }
                                }
                            }
                        }
                        if (mapTopLeftX<0)
                            mapWidth = mapBottomRightX - mapTopLeftX + 2*mbrdSpan;
                        else
                            mapWidth = mapBottomRightX + 2*mbrdSpan;
                        if (mapTopLeftY<0)
                            mapHeight = mapBottomRightY - mapTopLeftY + 2*mbrdSpan;
                        else
                            mapHeight = mapBottomRightY + 2*mbrdSpan;
                        break;
                }
            };

            //this.definePoz = function () {
            //    mapmatrix.defineMtxZonePoz(mbrdSpan, zoneSpan);
            //};

            this.getMapSize = function () {
                return {
                    width : mapWidth,
                    height: mapHeight
                };
            };

            this.getTopLeftCoords = function() {
                return {
                    topLeftX: mapTopLeftX,
                    topLeftY: mapTopLeftY
                }
            };

            //this.setMapWidth = function(width) {
            //    mapWidth = width;
            //};

            //this.setMapHeight = function(height) {
            //    mapHeight = height;
            //};

            this.isMapElementMoving = function () {
                var i, ii;
                for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                    if (containerRegistry[i].isMoving)
                        return true;
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (var j = 0, jj = linkedBus.length; j<jj; j++) {
                        if (linkedBus[j].isMoving)
                            return true;
                    }
                }
                for (i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    if (nodeRegistry[i].isMoving)
                        return true;
                }
                for (i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    if (endpointRegistry[i].isMoving)
                        return true;
                }
                return mapmatrix.isMoving();
            };

            this.isEditionMode = function() {
                return (options.getMode()==dic.mapMode.EDITION)
            };

            this.print = function (r) {
                var i, ii, j, jj;
                if (options.getLayout()===dic.mapLayout.NTWWW)
                    mapmatrix.printMtx(r);

                for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].print(r);
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (j = 0, jj = linkedBus.length; j<jj; j++)
                        linkedBus[j].print(r);
                }

                for (i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    nodeRegistry[i].print(r);
                }

                for (i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].print(r);
                }

                for (i = 0, ii = linkRegistry.length; i < ii; i++) {
                    linkRegistry[i].print(r);
                }

                for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].toFront();
                }

                for (i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    nodeRegistry[i].toFront();
                }

                for (i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].toFront();
                }

                if (this.isEditionMode())
                    this.editionMode(options_);
                mapmatrix.displayDC(options_.displayDC);
                mapmatrix.displayArea(options_.displayAREA);
                mapmatrix.displayLan(options_.displayLAN);
            };

            this.editionMode = function(options_) {
                var i, ii;
                var editionMode = (options_.getMode()==dic.mapMode.EDITION);
                for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].setEditionMode(editionMode);
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (var j = 0, jj = linkedBus.length; j<jj; j++)
                        linkedBus[j].mbus.setEditionMode(editionMode);
                }
                return mapmatrix.setEditionMode(editionMode);
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
                var i, ii;
                lTree.reloadTree(containerRegistry[0]);
                lTree.definePoz();
                for (i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].resetPoz();
                }
                for (i = 0, ii = linkRegistry.length; i < ii; i++) {
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

            var moveTreeMap = function(dx, dy) {
                var i, ii, j, jj, multicastBusRegistry;

                for (i = 0, ii=containerRegistry.length; i < ii; i++) containerRegistry[i].moveInit();
                for (i = 0, ii = transportRegistry.length; i < ii; i++) {
                    if (transportRegistry[i].isMulticast()) {
                        multicastBusRegistry = transportRegistry[i].getMulticastBusRegistry();
                        for(j=0, jj=multicastBusRegistry.length; j < jj; j++)
                            multicastBusRegistry[j].mbus.moveInit();
                    }
                }

                for (i = 0, ii=containerRegistry.length; i < ii; i++) containerRegistry[i].move(dx,dy);
                for (i = 0, ii = transportRegistry.length; i < ii; i++) {
                    if (transportRegistry[i].isMulticast()) {
                        multicastBusRegistry = transportRegistry[i].getMulticastBusRegistry();
                        for(j=0, jj=multicastBusRegistry.length; j < jj; j++)
                            multicastBusRegistry[j].mbus.move(dx,dy);
                    }
                }

                for (i = 0, ii=containerRegistry.length; i < ii; i++) containerRegistry[i].up();
                for (i = 0, ii = transportRegistry.length; i < ii; i++) {
                    if (transportRegistry[i].isMulticast()) {
                        multicastBusRegistry = transportRegistry[i].getMulticastBusRegistry();
                        for(j=0, jj=multicastBusRegistry.length; j < jj; j++)
                            multicastBusRegistry[j].mbus.up();
                    }
                }
            };

            this.rePozTo0Canvas = function() {
                var layout = options.getLayout();
                var dx = (mapTopLeftX - mbrdSpan == 0 ) ? 0 : -mapTopLeftX + mbrdSpan;
                var dy = (mapTopLeftY - mbrdSpan == 0 ) ? 0 : -mapTopLeftY + mbrdSpan;
                switch (layout) {
                    case dic.mapLayout.NTWWW:
                        mapmatrix.translate(dx, dy);
                        break;
                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.TREE:
                        moveTreeMap(dx, dy);
                        break;
                }
            };

            this.reInitToInitalPoz = function() {
                var layout = options.getLayout();
                var dx = (mapTopLeftX - mbrdSpan == 0 ) ? 0 : mapTopLeftX - mbrdSpan;
                var dy = (mapTopLeftY - mbrdSpan == 0 ) ? 0 : mapTopLeftY - mbrdSpan;
                switch (layout) {
                    case dic.mapLayout.NTWWW:
                        mapmatrix.translate(dx, dy);
                        break;
                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.TREE:
                        moveTreeMap(dx, dy);
                        break;
                }
            }
        }

        return map;
    });