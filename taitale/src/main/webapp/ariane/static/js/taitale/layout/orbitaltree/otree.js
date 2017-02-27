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
        'taitale-overtex'
    ],
    function($,helper,vertex) {
        function tree() {
            this.vertexRegistry = [];
            this.treeWidth      = 0;
            this.treeHeight     = 0;
            this.treeCenterX    = 0;
            this.treeCenterY    = 0;
            this.treeRoot       = null;
            this.type           = "ltree";

            var helper_           = new helper();

            this.layoutData = {
                isConnectedInsideMtx:  false,
                isConnectedOutsideMtx: false,
                isConnectedOutsideToUpMtx: false,
                isConnectedOutsideToDownMtx: false,
                isConnectedOutsideToLeftMtx: false,
                isConnectedOutsideToRightMtx: false,
                mtxCoord: null,
                tag: null
            };

            this.toString = function() {
                return "{\n tree : (" + this.vertexRegistry +")\n}";
            };

            this.findVertexByID = function(vertexID) {
                for (var i = 0, ii = this.vertexRegistry.length; i < ii ; i++) {
                    if (this.vertexRegistry[i].vertexid==vertexID)
                        return this.vertexRegistry[i];
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
                    this.vertexRegistry.push(currentVertex);
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

            this.getMaxRectSize = function() {
                return this.getRectSize();
            };

            this.defineMaxSize = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.defineMaxSize();
                this.defineTreeSize();
            };

            this.getRectSize = function() {
                return {
                    width : this.treeWidth,
                    height : this.treeHeight
                }
            };

            this.defineTreeSize = function() {
                var i, ii;
                var minX=0,	maxX=0,
                    minY=0, maxY=0;

                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++) {
                    var relX = this.vertexRegistry[i].relX,
                        relY = this.vertexRegistry[i].relY;
                    if ((relX + this.vertexRegistry[i].object.getRectSize().width/2)> maxX)
                        maxX = relX + this.vertexRegistry[i].object.getRectSize().width/2;
                    if ((relX - this.vertexRegistry[i].object.getRectSize().width/2) < minX)
                        minX = relX - this.vertexRegistry[i].object.getRectSize().width/2;
                    if (relY + this.vertexRegistry[i].object.getRectSize().height/2> maxY)
                        maxY = relY + this.vertexRegistry[i].object.getRectSize().height/2;
                    if (relY - this.vertexRegistry[i].object.getRectSize().height/2< minY)
                        minY = relY - this.vertexRegistry[i].object.getRectSize().height/2;
                }

                this.treeWidth   = maxX-minX;
                this.treeHeight  = maxY-minY;
                this.treeCenterX = this.treeWidth/2;
                this.treeCenterY = this.treeHeight/2;
            };

            this.defineSize = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.defineSize();
                this.defineTreeSize();
            };

            this.updateTreeLayoutData = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++) {
                    if (this.vertexRegistry[i].object.layoutData.isConnectedOutsideMtx)
                        this.layoutData.isConnectedOutsideMtx = true;
                    if (this.vertexRegistry[i].object.layoutData.isConnectedOutsideToDownMtx)
                        this.layoutData.isConnectedOutsideToDownMtx = true;
                    if (this.vertexRegistry[i].object.layoutData.isConnectedOutsideToLeftMtx)
                        this.layoutData.isConnectedOutsideToLeftMtx = true;
                    if (this.vertexRegistry[i].object.layoutData.isConnectedOutsideToRightMtx)
                        this.layoutData.isConnectedOutsideToRightMtx = true;
                    if (this.vertexRegistry[i].object.layoutData.isConnectedOutsideToUpMtx)
                        this.layoutData.isConnectedOutsideToUpMtx = true;
                }
            };

            this.updateLayoutData = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.updateLayoutData();
                this.updateTreeLayoutData();
            };

            this.updatePosition = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.updatePosition();
                this.updateTreeLayoutData();
            };

            this.clean = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.clean();
            };

            this.toFront = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.toFront();
            };

            this.moveInit = function() {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.moveInit();
            };

            this.propagateEditionMode = function(editionMode) {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.propagateEditionMode(editionMode);
            };

            this.propagateEndpointReset = function(epreset) {
                var i, ii;
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.propagateEndpointReset(epreset);
            };

            this.shiftX = null;
            this.shiftY = null;
            this.setPoz = function(x,y) {
                this.shiftX = x;
                this.shiftY = y;
            };

            this.defineIntermediateChildsPoz = function() {
                this.reloadTree(this.treeRoot);
                this.definePoz(this.shiftX, this.shiftY)
            };

            this.defineChildsPoz = function() {
                this.reloadTree(this.treeRoot);
                this.definePoz(this.shiftX, this.shiftY)
            };

            this.definePoz = function(shiftX, shiftY) {
                var i, ii;
                this.defineSize();
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].defineAbsolutePoz(shiftX+this.treeCenterX,shiftY+this.treeCenterY);
            };

            this.loadTree = function(treeRoot) {
                // helper_.debug("[tree] treeRoot = " + treeRoot.getID());
                var i, ii;
                this.treeRoot = treeRoot;
                this.addVertex(treeRoot, null);
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].defineRelativeLeafsData();
                for (i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].defineRelativePoz();
            };

            this.reloadTree = function(treeRoot) {
                for (var i = 0, ii = this.vertexRegistry.length; i < ii ; i++)
                    this.vertexRegistry[i].object.isInserted = false;
                this.vertexRegistry = [];
                this.loadTree(treeRoot);
            };
        }
        return tree;
    });