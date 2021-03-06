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
        'taitale-tree-groups',
        'taitale-container',
        'taitale-node',
        'taitale-endpoint',
        'taitale-transport',
        'taitale-link'
    ],
    function(helper, dictionaries, params, mapMatrix, treeGroups, container, node, endpoint, transport, link) {
        function map(options) {
            var mapWidth  = 0,
                mapHeight = 0,
                mapTopLeftX = 0,
                mapTopLeftY = 0,
                mapBottomRightX = 0,
                mapBottomRightY = 0,
                mapmatrix = new mapMatrix(options),
                treegrps  = new treeGroups(options),
                mbrdSpan  = params.map_mbrdSpan,
                zoneSpan  = params.map_zoneSpan,
                linkColor = params.map_linkColor,
                linkBckg  = params.map_linkBckg,
                helper_   = new helper(),
                options_  = options;

            var containerRegistry             = [],
                rootContainerRegistry         = [],
                nodeRegistry                  = [],
                endpointRegistry              = [],
                transportRegistry             = [],
                linkRegistry                  = [],
                mapObjects                    = [],
                sortOrdering                  = 1,
                minMaxLinkedObjectsComparator = function(treeObj1, treeObj2) {
                    return (treeObj2.getLinkedTreeObjectsCount() - treeObj1.getLinkedTreeObjectsCount())*sortOrdering;
                };

            var dic   = new dictionaries();

            var applications = [],
                isApplicationRegistered = function(app) {
                    var i, ii, isRegistered = false;
                    for (i=0, ii=applications.length; i<ii; i++) {
                        if (applications[i] != null && applications[i].name === app.name &&
                            applications[i].color === app.color) {
                            isRegistered = true;
                            break;
                        }
                    }
                    return isRegistered;
                },
                teams = [],
                isTeamRegistered = function(team) {
                    var i, ii, isRegistered = false;
                    for (i=0, ii=teams.length; i<ii; i++) {
                        if (teams[i] != null && teams[i].name === team.name &&
                            teams[i].color === team.color) {
                            isRegistered = true;
                            break;
                        }
                    }
                    return isRegistered;
                };

            var childContainersWaitingParent = [];
            this.addContainer = function(JSONContainerDesc) {
                var cont, steam, x=0, y=0;
                var i, ii, j, jj;
                //noinspection JSUnresolvedVariable
                if (JSONContainerDesc.containerProperties!=null && JSONContainerDesc.containerProperties.manualCoord!=null) {
                    //noinspection JSUnresolvedVariable
                    x=JSONContainerDesc.containerProperties.manualCoord.x;
                    //noinspection JSUnresolvedVariable
                    y=JSONContainerDesc.containerProperties.manualCoord.y;
                }
                cont = new container(JSONContainerDesc, x, y); steam = cont.getSupportTeam();
                containerRegistry.push(cont);
                if (steam !=null && !isTeamRegistered(steam)) teams.push(steam);

                if (cont.cpID!=0) childContainersWaitingParent.push(cont);
                else {
                    rootContainerRegistry.push(cont);
                    mapObjects.push(cont);
                }


                var childContainersFoundParent = [];
                for (j = 0, jj = childContainersWaitingParent.length; j < jj; j++) {
                    for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                        var possibleParentContainer = containerRegistry[i];
                        var waitingParentContainer = childContainersWaitingParent[j];
                        if (possibleParentContainer.ID==waitingParentContainer.cpID) {
                            waitingParentContainer.containerParentC = possibleParentContainer;
                            childContainersFoundParent.push(waitingParentContainer);
                        }
                    }
                }
                for (i = 0, ii = childContainersFoundParent.length; i < ii; i++)
                    childContainersWaitingParent.splice(childContainersWaitingParent.indexOf(childContainersFoundParent[i]),1);
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
            this.addNode = function(JSONNodeDesc) {
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
                    var nodeToPush = new node(JSONNodeDesc, container), papp = nodeToPush.getPrimaryApplication();
                    nodeRegistry.push(nodeToPush);
                    if (papp!=null && !isApplicationRegistered(papp)) applications.push(papp);
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
                    this.addNode(JSONmapDesc.nodes[i]);

                for (i = 0, ii = containerRegistry.length; i < ii; i++)
                    containerRegistry[i].defineHeapContainers();

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
                    var mbusRegistry = transportRegistry[i].getMulticastBusRegistry();
                    for (var j = 0, jj = mbusRegistry.length; j < jj; j++) {
                        mapObjects.push(mbusRegistry[j])
                    }
                }
            };

            this.buildMap = function() {
                var i, ii, j, jj;

                // first : place nodes and containers in container (first placement)
                for (j = 0, jj = nodeRegistry.length; j < jj; j++)
                    nodeRegistry[j].placeIn();
                for (j = 0, jj = containerRegistry.length; j < jj; j++)
                    containerRegistry[j].placeIn();

                // second : define container max size
                for (j = 0, jj = containerRegistry.length; j < jj; j++)
                    containerRegistry[j].defineMaxSize();



                // third : layout policy
                var layout = options.getLayout();
                switch (layout) {
                    case dic.mapLayout.NETL3P:
                        containerRegistry.sort(minMaxLinkedObjectsComparator);
                        // third 0 : populate DC, Area and Lan registries and enrich the objects
                        for (j = 0, jj = rootContainerRegistry.length; j < jj; j++)
                            mapmatrix.populateLayoutRegistries(rootContainerRegistry[j]);
                        // third 1 : place container and the linked bus into the map matrix
                        for (j = 0, jj = rootContainerRegistry.length; j < jj; j++)
                            mapmatrix.addContainerZone(rootContainerRegistry[j]);
                        // third 2 : define map objects max size and first position
                        mapmatrix.defineMtxZoneMaxSize();
                        mapmatrix.defineMapContentMaxSize();
                        mapmatrix.defineMtxZoneFirstPoz(mbrdSpan, zoneSpan);
                        break;

                    case dic.mapLayout.MANUAL:
                    //    mapWidth  = 1800;
                    //    mapHeight = 800;
                    //    for (j = 0, jj = containerRegistry.length; j < jj; j++)
                    //        containerRegistry[j].defineChildsPoz();
                        break;

                    case dic.mapLayout.OBTREE:
                    case dic.mapLayout.BBTREE:

                        // third 0 : sort all tree lists
                        sortOrdering = options.getRootTreeSorting();
                        for (i = 0, ii = mapObjects.length; i<ii; i++) {
                            mapObjects[i].setSortOrdering(options.getSubTreesSorting());
                            mapObjects[i].sortLinkedTreeObjects();
                        }
                        containerRegistry.sort(minMaxLinkedObjectsComparator);

                        treegrps.computeTreeGroups(mapObjects, layout);
                        treegrps.sort(minMaxLinkedObjectsComparator);

                        // third 1 : define the tree with objects
                        treegrps.loadTrees();

                        // third 2 : define map objects position
                        treegrps.definePoz();
                        break;
                }

                this.updateObjectFinalPozAndSize();
                this.updateMapSize();

                // fourth : define average links
                for (i = 0, ii = linkRegistry.length; i < ii; i++)
                    linkRegistry[i].linkAvgEp();
            };

            this.updateObjectFinalPozAndSize = function () {
                var j, jj;
                var layout = options.getLayout();

                if (layout === dic.mapLayout.NETL3P)
                    mapmatrix.optimizeMtxCoord();

                // Set final container size
                for (j = 0, jj = containerRegistry.length; j < jj; j++) {
                    containerRegistry[j].defineMtxIntermediatePoz();
                    containerRegistry[j].defineSize();
                }

                if (layout === dic.mapLayout.NETL3P) {
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
                } else if (layout == dic.mapLayout.BBTREE || layout == dic.mapLayout.OBTREE) {
                    for (j = 0, jj=containerRegistry.length; j < jj; j++) {
                        containerRegistry[j].clean();
                        containerRegistry[j].defineSize();
                    }
                    treegrps.definePoz();
                }
            };

            this.updateMapSize = function () {
                var layout = options.getLayout();
                //noinspection FallthroughInSwitchStatementJS
                switch (layout) {
                    case dic.mapLayout.NETL3P:
                        mapmatrix.defineMapContentSize();

                        mapWidth = mbrdSpan*2 + mapmatrix.getMapContentSize().width;
                        mapHeight = mbrdSpan*2 + mapmatrix.getMapContentSize().height;

                        mapTopLeftX = mapmatrix.getTopLeftCoords().topLeftX;
                        mapTopLeftY = mapmatrix.getTopLeftCoords().topLeftY;

                        mapBottomRightX = mapmatrix.getBottomRightCoords().bottomRightX;
                        mapBottomRightY = mapmatrix.getBottomRightCoords().bottomRightY;

                        break;

                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.OBTREE:
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

                        mapWidth = Math.abs(mapBottomRightX - mapTopLeftX) + 2*mbrdSpan;
                        mapHeight = Math.abs(mapBottomRightY - mapTopLeftY) + 2*mbrdSpan;
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
                return options.edition
            };

            this.print = function (r) {
                var i, ii, j, jj;
                if (options.getLayout()===dic.mapLayout.NETL3P)
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
                var editionMode = options_.edition;
                for (i = 0, ii = containerRegistry.length; i < ii; i++) {
                    containerRegistry[i].propagateEditionMode(editionMode);
                    var linkedBus = containerRegistry[i].getLinkedBus();
                    for (var j = 0, jj = linkedBus.length; j<jj; j++)
                        linkedBus[j].mbus.setEditionMode(editionMode);
                }
                return mapmatrix.setEditionMode(editionMode);
            };

            this.endpointReset = function(options_) {
                var i, ii;
                var epreset = options_.epreset;
                for (i = 0, ii = containerRegistry.length; i < ii; i++)
                    containerRegistry[i].propagateEndpointReset(epreset);
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
                treegrps.reloadTrees();
                treegrps.definePoz();
                for (i = 0, ii = endpointRegistry.length; i < ii; i++) {
                    endpointRegistry[i].resetPoz();
                }
                for (i = 0, ii = linkRegistry.length; i < ii; i++) {
                    linkRegistry[i].linkAvgEp();
                }
                for (i = 0, ii = nodeRegistry.length; i < ii; i++) {
                    nodeRegistry[i].defineEndpointsAvgPoz();
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
                    case dic.mapLayout.NETL3P:
                        mapmatrix.translate(dx, dy);
                        break;
                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.OBTREE:
                    case dic.mapLayout.BBTREE:
                        moveTreeMap(dx, dy);
                        break;
                }
            };

            this.reInitToInitalPoz = function() {
                var layout = options.getLayout();
                var dx = (mapTopLeftX - mbrdSpan == 0 ) ? 0 : mapTopLeftX - mbrdSpan;
                var dy = (mapTopLeftY - mbrdSpan == 0 ) ? 0 : mapTopLeftY - mbrdSpan;
                switch (layout) {
                    case dic.mapLayout.NETL3P:
                        mapmatrix.translate(dx, dy);
                        break;
                    case dic.mapLayout.MANUAL:
                    case dic.mapLayout.OBTREE:
                    case dic.mapLayout.BBTREE:
                        moveTreeMap(dx, dy);
                        break;
                }
            };

            this.displayLegend = function() {
                var html_applications, html_teams, i, ii;
                if (teams.length > 0 && teams[0] !=null) {
                    html_teams = "<b style='color: #333'>Teams : </b><ul>";
                    for (i=0, ii=teams.length; i<ii; i++)
                        html_teams += "<li style='color: #"+ teams[i].color +"'><p style='color: #333'>" + teams[i].name + "</p></li>"
                    html_teams += "</ul>";
                }

                if (applications.length > 0 && applications[0] != null) {
                    html_applications = "<b style='color: #333'>Applications : </b><ul>";
                    for (i=0, ii=applications.length; i<ii; i++)
                        html_applications += "<li style='color: #"+ applications[i].color +"'><p style='color: #333'>" + applications[i].name + "</p></li>"
                    html_applications += "</ul>";
                }


                var details = ((html_teams!=null) ? html_teams : "") + ((html_applications!=null) ? html_applications : "");
                helper_.legendOpen(details);
            };

            this.hideLegend = function() {
                helper_.legendClose()
            };
        }

        return map;
    });