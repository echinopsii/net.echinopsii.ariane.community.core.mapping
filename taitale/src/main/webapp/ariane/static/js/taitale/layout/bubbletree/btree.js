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
        'taitale-bvertex'
    ],
    function($,helper,vertex) {
        function tree() {
            var vertexRegistry    = [] ,
                //maxTreeFloor      = 0  ,
                treeWidth         = 0  ,
                treeHeight        = 0  ,
                treeCenterX       = 0  ,
                treeCenterY       = 0  ;
             // var helper_           = new helper();

            this.findVertexByID = function(vertexID) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    if (vertexRegistry[i].vertexid==vertexID)
                        return vertexRegistry[i];
                }
                return null;
            };

            this.addVertex = function(treeObject, parentTreeVertex) {
                var i, ii, currentVertex, linkedObjects  = treeObject.linkedTreeObjects;

                if (parentTreeVertex==null || (treeObject.primaryTreeObjID === parentTreeVertex.object.ID))
                {
                    currentVertex = new vertex(treeObject);
                    if (parentTreeVertex==null) currentVertex.floor = 0;
                    else parentTreeVertex.pushLinkedVertex(currentVertex);
                    vertexRegistry.push(currentVertex);
                    treeObject.isInserted=true;

                    for (i = 0, ii = linkedObjects.length; i<ii; i++)
                        if (!linkedObjects[i].isInserted && linkedObjects[i].primaryTreeObjID==null) {
                            // helper_.debug("[tree.addVertex] Define soon : " + linkedObjects[i].name);
                            linkedObjects[i].primaryTreeObjID = treeObject.ID;
                        }

                    for (i = 0, ii = linkedObjects.length; i<ii; i++)
                        if (!linkedObjects[i].isInserted)
                            this.addVertex(linkedObjects[i], currentVertex);
                        // else
                        //     helper_.debug("[tree.addVertex] ALREADY INSERTED " + linkedObjects[i].name + " !!!");

                    // currentVertex.defineRelativeLeafsData();
                    // var parentVertexID = (parentTreeVertex==null) ? "NONE" : parentTreeVertex.object.ID;
                    // helper_.debug(
                    //     "[tree.addVertex] New vertex " + currentVertex.vertexid + " added (" + treeObject.name + "). " +
                    //     "{ Parent Vertex ID = " + parentVertexID +
                    //     ", Floor =  " + currentVertex.floor +
                    //     ", ID from root = " + currentVertex.idFromRoot + " }");
                }
            };

            this.definePoz = function() {
                var i, ii;
                for (i = 0, ii = vertexRegistry.length; i < ii ; i++)
                   vertexRegistry[i].defineRelativeLeafsData();

                for (i = 0, ii = vertexRegistry.length; i < ii ; i++)
                    vertexRegistry[i].defineRelativePoz();

                var minX=0,	maxX=0,
                    minY=0, maxY=0;

                for (i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    var relX = vertexRegistry[i].relX,
                        relY = vertexRegistry[i].relY;
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
                this.addVertex(treeRoot, null);
                // for (var i = 0, ii = vertexRegistry.length; i<ii; i++)
                //     if (vertexRegistry[i].floor!=0) vertexRegistry[i].pushLinkedVertex(vertexRegistry[i].rootV);
            };

            this.reloadTree = function(treeRoot) {
                for (var i = 0, ii = vertexRegistry.length; i < ii ; i++) {
                    vertexRegistry[i].object.isInserted = false;
                }
                vertexRegistry = [];
                this.loadTree(treeRoot);
            };
        }
        return tree;
    });