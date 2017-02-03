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
        'taitale-bvertex',
        'taitale-container'
    ],
    function($,helper,vertex,container) {
        function tree() {
            var vertexRegistry    = [] ,
                //maxTreeFloor      = 0  ,
                treeWidth         = 0  ,
                treeHeight        = 0  ,
                treeCenterX       = 0  ,
                treeCenterY       = 0  ;
             var helper_           = new helper();

            this.findVertexByID = function(vertexID) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    if (vertexRegistry[i].getVertexID()==vertexID)
                        return vertexRegistry[i];
                }
                return null;
            };

            this.addVertex = function(treeObject, parentTreeVertex, idFromRoot) {
                var i, ii, currentVertex;
                var linkedObjects  = treeObject.linkedTreeObjects,
                    idFromRootLoop = 0;

                if (parentTreeVertex==null ||
                    (treeObject.primaryTreeObjID === parentTreeVertex.getObject().ID)) {
                    currentVertex = new vertex(treeObject);
                    if (parentTreeVertex==null) {
                        currentVertex.setFloor(0);
                    } else {
                        currentVertex.setRootV(parentTreeVertex);
                        currentVertex.setIdFromRoot(idFromRoot);
                        currentVertex.setFloor(parentTreeVertex.getFloor() + 1);
                        parentTreeVertex.pushLinkedVertex(currentVertex);
                    }
                    vertexRegistry.push(currentVertex);
                    treeObject.isInserted=true;

                    //var parentVertexID = (parentTreeVertex==null) ? "NONE" : parentTreeVertex.getObject().ID;
                    //helper_.debug(
                    //    "[tree.addVertex] New vertex " + currentVertex.getVertexID() +
                    //    " added (" + treeObject.name + "). Parent Vertex ID = " + parentVertexID +
                    //    ",Floor =  " + currentVertex.getFloor() + ", ID from root = " + currentVertex.getIdFromRoot());

                    for (i = 0, ii = linkedObjects.length; i<ii; i++)
                        if (!linkedObjects[i].isInserted && linkedObjects[i].primaryTreeObjID==null) {
                            //helper_.debug("[tree.addVertex] Define soon : " + linkedObjects[i].name);
                            linkedObjects[i].primaryTreeObjID = treeObject.ID;
                        }

                    for (i = 0, ii = linkedObjects.length; i<ii; i++)
                        if (!linkedObjects[i].isInserted)
                            this.addVertex(linkedObjects[i], currentVertex, idFromRootLoop++);
                    //    else
                    //        helper_.debug("[tree.addVertex] ALREADY INSERTED " + linkedObjects[i].name + " !!!")
                }
            };

            this.definePoz = function() {
                var i, ii;
                for (i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].defineRelativePoz();
                }

                var minX=0,	maxX=0,
                    minY=0, maxY=0;

                for (i = 0, ii = vertexRegistry.length; i < ii ; i++) {
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

                treeWidth   = maxX-minX;
                treeHeight  = maxY-minY;
                treeCenterX = treeWidth/2;
                treeCenterY = treeHeight/2;

                for (i = 0, ii = vertexRegistry.length; i < ii ; i++)
                    vertexRegistry[i].defineAbsolutePoz(treeCenterX,treeCenterY);
            };

            this.loadTree = function(treeRoot) {
                //helper_.debug("[tree] treeRoot = " + treeRoot.getID());
                this.addVertex(treeRoot, null, 0);

                for (var i = 0, ii = vertexRegistry.length; i<ii; i++) {
                    if (vertexRegistry[i].getFloor()!=0) {
                        vertexRegistry[i].pushLinkedVertex(vertexRegistry[i].getRootV());
                    }
                }
            };

            this.reloadTree = function(treeRoot) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].getObject().isInserted = false;
                }
                vertexRegistry = [];
                this.loadTree(treeRoot);
            };
        }
        return tree;
    });