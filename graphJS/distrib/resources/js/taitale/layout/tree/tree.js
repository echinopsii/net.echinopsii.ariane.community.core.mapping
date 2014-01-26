// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TREE module - Tree                            │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │ 																					  │ \\
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
        'jquery',
        'taitale-helper',
        'taitale-vertex',
        'taitale-container'
    ],
    function($,helper,vertex,container) {
        function tree() {
            var vertexRegistry    = [] ,
                maxTreeFloor      = 0  ,
                treeWidth         = 0  ,
                treeHeight        = 0  ,
                treeCenterX       = 0  ,
                treeCenterY       = 0  ,
                helper_           = new helper();

            this.findVertexByID = function(vertexID) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    if (vertexRegistry[i].getVertexID()==vertexID)
                        return vertexRegistry[i];
                }
            };

            this.addVertex = function(treeObject) {
                var rvertex;
                if (!treeObject.isInserted()) {
                    rvertex = new vertex(treeObject);
                    rvertex.setFloor(0);
                    rvertex.incrementRepulsionFactor(treeObject.getLinkedTreeObjects().length);
                    vertexRegistry.push(rvertex);
                    treeObject.setInserted();
                    helper_.debug("[tree.addVertex] New root vertex " + rvertex.getVertexID() + " added (" + treeObject.getName() + "). Floor =  " + 0 );
                } else {
                    rvertex = this.findVertexByID(treeObject.getID());
                    helper_.debug("[tree.addVertex] Parent vertex " + rvertex.getVertexID() + " retrieved (" + treeObject.getName() + "). Floor =  " + rvertex.getFloor() );
                }

                var linkedContainers  = ((treeObject instanceof container) ? treeObject.getLinkedContainers():treeObject.getLinkedTreeObjects()),
                    idFromRoot        = 0,
                    childMulticastBus = [];
                for (var i = 0, ii = linkedContainers.length; i<ii; i++) {
                    var linkedContainer = linkedContainers[i];
                    if (!linkedContainer.isInserted()) {
                        var linkedVertex = new vertex(linkedContainer);
                        linkedVertex.setRootV(rvertex);
                        linkedVertex.setIdFromRoot(idFromRoot++);
                        linkedVertex.setFloor(rvertex.getFloor()+1);
                        linkedVertex.incrementRepulsionFactor(linkedContainer.getLinkedTreeObjectsCount())
                        vertexRegistry.push(linkedVertex);
                        rvertex.pushLinkedVertex(linkedVertex);
                        linkedContainer.setInserted();
                        helper_.debug(
                            "[tree.addVertex] New vertex " + linkedVertex.getVertexID() +
                            " added (" + linkedContainer.getName() + "). Floor =  " + linkedVertex.getFloor() +
                            ", ID from root = " + linkedVertex.getIdFromRoot());
                        if (linkedContainer.getLinkedTreeObjects().length!=0)
                            childMulticastBus = childMulticastBus.concat(this.addVertex(linkedContainer));
                    }
                }

                for (var i = 0, ii = childMulticastBus.length; i<ii; i++) {
                    var childBus = childMulticastBus[i];
                    if (!childBus.isInserted()) {
                        var busVertex     = new vertex(childBus),
                            linkedObjects = childBus.getLinkedTreeObjects();
                        for (var j= 0, jj = linkedObjects.length; j<jj; j++) {
                            var linkedVertex = this.findVertexByID(childBus.getLinkedTreeObjects()[j].getID())
                            if (linkedVertex!=null) {
                                if (!childBus.isInserted()) {
                                    busVertex.setRootV(linkedVertex);
                                    busVertex.setIdFromRoot(linkedVertex.getLinkedVertex().length);
                                    busVertex.setFloor(linkedVertex.getFloor()+1);
                                    busVertex.incrementRepulsionFactor(childBus.getLinkedTreeObjectsCount());
                                    vertexRegistry.push(busVertex);
                                    childBus.setInserted();
                                    helper_.debug(
                                        "[tree.addVertex] New vertex " + linkedVertex.getVertexID() +
                                        " added (" + linkedContainer.getName() + "). Floor =  " + linkedVertex.getFloor() +
                                        ", ID from root = " + linkedVertex.getIdFromRoot());
                                }
                                linkedVertex.pushLinkedVertex(busVertex);
                            }
                        }
                    }
                }

                for (var i = 0, ii = childMulticastBus.length; i<ii ;i++)
                    this.addVertex(childMulticastBus[i])

                return ((treeObject instanceof container) ? treeObject.getLinkedBus() : []);
            };

            this.definePoz = function() {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].defineRelativePoz();
                }

                var minX=0,	maxX=0,
                    minY=0, maxY=0;

                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    var relX = vertexRegistry[i].getRelX(),
                        relY = vertexRegistry[i].getRelY();
                    if (relX > maxX)
                        maxX = relX;
                    if (relX < minX)
                        minX = relX;
                    if (relY > maxY)
                        maxY = relY;
                    if (relY < minY)
                        minY = relY;
                }

                treeWidth   = maxX-minX + 50;
                treeHeight  = maxY-minY + 50;
                treeCenterX = treeWidth/2;
                treeCenterY = treeHeight/2;

                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].defineAbsolutePoz(treeCenterX,treeCenterY);
                }
            };

            this.loadTree = function(treeRoot) {
                helper_.debug("[tree] treeRoot = " + treeRoot.getID());
                this.addVertex(treeRoot);

                for (var i = 0, ii = vertexRegistry.length; i<ii; i++) {
                    if (vertexRegistry[i].getFloor()!=0) {
                        vertexRegistry[i].pushLinkedVertex(vertexRegistry[i].getRootV());
                    }
                };
            };

            this.reloadTree = function(treeRoot) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].getObject().unsetInserted();
                }
                vertexRegistry = [];
                this.loadTree(treeRoot);
            };
        };
        return tree;
    });