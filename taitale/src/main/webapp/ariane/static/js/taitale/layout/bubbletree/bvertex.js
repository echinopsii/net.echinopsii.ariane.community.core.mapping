// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TREE module - Vertex                          │ \\
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
        'taitale-helper'
    ],
    function(helper) {
        function aVertex(object_) {
            var helper_ = new helper();

            this.object         = object_;
            this.vertexid       = this.object.ID;
            this.rootV          = null;
            this.floor          = 0;
            this.idFromRoot     = 0;

            this.radStep        = 0;
            this.relX           = 0;     // relative X (center of the rect)
            this.relY           = 0;     // relative Y (center of the rect)
            this.relFirstChT    = 0;     // relative orientation of first child
            this.orientStep     = 0;     // orientation step from this vertex to next floor
            this.isPlaced       = false;
            this.isSimpleProxy  = false;
            this.isLeaf         = false;
            this.leafRadFloor   = [];
            this.leafRadFloorCount = 0;
            this.simplePrxOnLastLeafsFloorCount = 0;

            this.linkedVertexx  = [];
            this.linkedLeafs    = [];
            this.linkedLeafsByStep = [];
            this.linkedSimpleProxies = [];
            this.linkedProxies = [];

            // TODO: prototype
            // currently object is a container.
            // any object should have method getRectSize available
            this.pushLinkedVertex = function(vertex) {
                this.linkedVertexx.push(vertex);
                var linkedTreeObjectsCount = vertex.object.getLinkedTreeObjectsCount();
                helper_.debug("[vertex.pushLinkedVertex] (" + this.object.name + ") " + vertex.object.name +
                    " has " + linkedTreeObjectsCount + " linked tree object(s)...");
                if (linkedTreeObjectsCount==1 && !this.isLeaf) {
                    this.linkedLeafs.push(vertex);
                    vertex.isLeaf = true;
                } else {
                    if (!this.isSimpleProxy && linkedTreeObjectsCount==2) {
                        vertex.isSimpleProxy = true;
                        this.linkedSimpleProxies.push(vertex);
                    } else this.linkedProxies.push(vertex);
                }
            };

            this.getMaxChildRad = function(leafStep) {
                var maxRad = 0, currentRad, i, ii;
                helper_.debug("[vertex.getMaxChildRad] " + leafStep + " ; " + this.linkedLeafsByStep.length + " ; " + this.linkedProxies.length);
                if (leafStep < this.linkedLeafsByStep.length) {
                    for (i = 0, ii = this.linkedLeafsByStep[leafStep].length; i < ii; i++) {
                        currentRad = this.linkedLeafsByStep[leafStep][i].object.getBubbleDiameter()/2;
                        if (currentRad>maxRad)
                            maxRad = currentRad
                    }
                } else {
                    for (i = 0, ii = this.linkedProxies.length; i < ii; i++) {
                        currentRad = this.linkedProxies[i].object.getBubbleDiameter()/2;
                        if (currentRad>maxRad)
                            maxRad = currentRad
                    }
                }
                helper_.debug("[vertex.getMaxChildRad] " + maxRad);
                return maxRad
            };

            /*
            this.getMaxChildRad = function() {
                var maxRad = 0, currentRad, i, ii;
                for (i = 0, ii = this.linkedVertexx.length; i < ii; i++) {
                    currentRad = this.linkedVertexx[i].object.getBubbleDiameter()/2;
                    if (currentRad>maxRad)
                        maxRad = currentRad
                }
                return maxRad
            };

            this.defineRadStepValue = function(orientStep) {
                var currentRad1, currentRad2, teta1, teta2, i, ii;
                var radStepIsOK, currentRadStep = this.getMaxChildRad3() + this.object.getBubbleDiameter()/2 + 10;
                for (i = 0, ii = this.linkedVertexx.length; i+1 < ii; i++) {
                    currentRad1 = this.linkedVertexx[i].object.getBubbleDiameter()/2;
                    currentRad2 = this.linkedVertexx[i+1].object.getBubbleDiameter()/2;
                    radStepIsOK = false;
                    while (!radStepIsOK) {
                        teta1 = Math.atan(currentRad1/(currentRadStep*2))*2;
                        teta2 = Math.atan(currentRad2/(currentRadStep*2))*2;
                        if (orientStep > teta1+teta2)
                            radStepIsOK = true;
                        else
                            currentRadStep += 10;
                    }
                }
                return currentRadStep
            };
            */


            this.defineLeafRadStepValue = function(leafStep, leafOrientStep) {
                var currentRad1, currentRad2, teta1, teta2, i, ii, currentRadStep, radStepIsOK, listToExplore;
                currentRadStep = this.getMaxChildRad(leafStep) + 10 +
                    ((leafStep==0) ? this.object.getBubbleDiameter()/2 : this.leafRadFloor[leafStep-1]);
                listToExplore = this.linkedLeafsByStep[leafStep];

                for (i = 0, ii = listToExplore.length; i+1 < ii; i++) {
                    currentRad1 = listToExplore[i].object.getBubbleDiameter()/2;
                    currentRad2 = listToExplore[i+1].object.getBubbleDiameter()/2;
                    radStepIsOK = false;
                    while (!radStepIsOK) {
                        teta1 = Math.atan(currentRad1/(currentRadStep*2))*2;
                        teta2 = Math.atan(currentRad2/(currentRadStep*2))*2;
                        if (leafOrientStep > teta1+teta2) {
                            radStepIsOK = true;
                        } else {
                            currentRadStep += 10;
                        }
                    }
                }

                return currentRadStep
            };

            this.defineProxyRadStepValue = function(orientStep) {
                var currentRad1, currentRad2, teta1, teta2, i, ii;
                var radStepIsOK, currentRadStep;

                currentRadStep = this.getMaxChildRad(this.leafRadFloorCount);
                if (this.leafRadFloorCount > 0) {
                    for (i = 0, ii = this.leafRadFloorCount-2; i < ii; i++)
                        currentRadStep += this.getMaxChildRad(i)*2;
                    currentRadStep += this.getMaxChildRad(this.leafRadFloorCount-1);
                }
                currentRadStep += this.object.getBubbleDiameter()/2 + 10;

                for (i = 0, ii = this.linkedProxies.length; i+1 < ii; i++) {
                    currentRad1 = this.linkedProxies[i].object.getBubbleDiameter()/2;
                    currentRad2 = this.linkedProxies[i+1].object.getBubbleDiameter()/2;
                    radStepIsOK = false;
                    while (!radStepIsOK) {
                        teta1 = Math.atan(currentRad1/(currentRadStep*2))*2;
                        teta2 = Math.atan(currentRad2/(currentRadStep*2))*2;
                        if (orientStep > teta1+teta2)
                            radStepIsOK = true;
                        else
                            currentRadStep += 10;
                    }
                }
                return currentRadStep
            };

            var maxLinksComparator = function(linkedObject1, linkedObject2) {
                return (linkedObject2.object.getLinksCount() - linkedObject1.object.getLinksCount());
            };

            this.defineRelativeLeafsFromRootPoz = function(rootRelX, rootRelY) {
                var i, ii, j, jj, leafOrientStep, leafOrientV, aVertex, nextRelFirstCht = this.relFirstChT;
                for (i = 0, ii = this.linkedLeafsByStep.length; i < ii; i++) {
                    for (j = 0, jj = this.linkedLeafsByStep[i].length; j < jj; j++) {
                        aVertex = this.linkedLeafsByStep[i][j];
                        helper_.debug("[vertex.defineRelativeLeafsFromRootPoz] (" + this.object.name + ") " + aVertex.object.name +
                            " : " + this.linkedLeafsByStep[i].length);
                        if (!aVertex.isPlaced) {
                            leafOrientStep = 2 * Math.PI/this.linkedLeafsByStep[i].length;
                            leafOrientV = this.relFirstChT + ((i % 2 == 0) ? 0 : Math.PI / 16) + j * leafOrientStep;
                            if (i==this.leafRadFloorCount-1 && this.simplePrxOnLastLeafsFloorCount>0 && j<=this.simplePrxOnLastLeafsFloorCount)
                                nextRelFirstCht = leafOrientV;
                            aVertex.relX = rootRelX + this.leafRadFloor[i] * Math.cos(leafOrientV);
                            aVertex.relY = rootRelY + this.leafRadFloor[i] * Math.sin(leafOrientV);
                            aVertex.isPlaced = true;
                            helper_.debug("[vertex.defineRelativeLeafsFromRootPoz] (" + this.object.name + ") " + aVertex.object.name +
                                " : {relX: " + aVertex.relX + ", relY: " + aVertex.relY + "}");
                            if (!aVertex.isLeaf)
                                aVertex.defineRelativeLeafsFromRootPoz(aVertex.relX, aVertex.relY);
                        }
                    }
                }
                this.relFirstChT = nextRelFirstCht;
            };

            this.defineRelativeLeafsData = function() {
                var i, ii, j, jj, leafOrientStep, aVertex, freePlaceOnLastLeafsStep;

                if (this.linkedLeafs.length > 0) {
                    this.linkedLeafs.sort(maxLinksComparator);
                    this.linkedLeafsByStep = [];
                    this.leafRadFloorCount = 1;

                    var vStepCounter = 0, currentLeafsFloorLen = this.leafRadFloorCount * 8;
                    for (i = 0, ii = this.linkedLeafs.length; i < ii; i++) {
                        aVertex = this.linkedLeafs[i];
                        helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                            " current leafRadFloorCount: " + this.leafRadFloorCount);
                        helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                            " current currentLeafsFloorLen: " + currentLeafsFloorLen);
                        if (vStepCounter < currentLeafsFloorLen) {
                            if (this.linkedLeafsByStep[this.leafRadFloorCount - 1] == null)
                                this.linkedLeafsByStep[this.leafRadFloorCount - 1] = [];
                            this.linkedLeafsByStep[this.leafRadFloorCount - 1].push(aVertex);
                            vStepCounter += 1;
                        } else {
                            this.leafRadFloorCount += 1;
                            this.linkedLeafsByStep[this.leafRadFloorCount - 1] = [];
                            this.linkedLeafsByStep[this.leafRadFloorCount - 1].push(aVertex);
                            vStepCounter = 1;
                            currentLeafsFloorLen = this.leafRadFloorCount * 8;
                        }
                    }

                    freePlaceOnLastLeafsStep = this.leafRadFloorCount * 8 - this.linkedLeafsByStep[this.leafRadFloorCount - 1].length;

                    for (i = 0, ii = this.linkedLeafsByStep.length; i < ii; i++) {
                        if (i == this.leafRadFloorCount - 1) {
                            var spIdx2RM = [];
                            for (j = 0, jj = this.linkedSimpleProxies.length; j < jj; j++) {
                                if (j > (freePlaceOnLastLeafsStep - 1)) break;
                                this.linkedLeafsByStep[i].splice(0, 0, this.linkedSimpleProxies[j]);
                                spIdx2RM.push(j);
                                this.simplePrxOnLastLeafsFloorCount++;
                            }
                            for (j = 0, jj = spIdx2RM.length; j < jj; j++) this.linkedSimpleProxies.splice(spIdx2RM[j], 1);
                        }

                        leafOrientStep = 2 * Math.PI / this.linkedLeafsByStep[i].length;
                        this.leafRadFloor[i] = this.defineLeafRadStepValue(i, leafOrientStep);
                    }
                }
                for (i = 0, ii = this.linkedSimpleProxies.length; i < ii; i++)
                    this.linkedProxies.push(this.linkedSimpleProxies[i]);
                this.linkedSimpleProxies = [];
            };

            this.defineRelativePoz = function() {
                if (!this.isPlaced && !this.isLeaf) {
                    if (this.rootV!=null && this.floor!=0) {
                        var rootLinkedVertexx = this.rootV.linkedProxies;
                        if (rootLinkedVertexx.length <= 2)
                            this.orientStep = (this.linkedProxies.length>1) ? Math.PI/this.linkedProxies.length : 0;
                        else
                            this.orientStep  = (this.linkedProxies.length>1) ? this.rootV.orientStep*2/this.linkedProxies.length : this.rootV.orientStep*2 ;
                        this.radStep = this.defineProxyRadStepValue(this.orientStep);

                        var orientV = this.rootV.relFirstChT + this.idFromRoot*this.rootV.orientStep;
                        this.relX   = this.rootV.relX + this.rootV.radStep*Math.cos(orientV);
                        this.relY   = this.rootV.relY + this.rootV.radStep*Math.sin(orientV);
                        this.relFirstChT = orientV - ((this.linkedProxies.length>1) ? this.orientStep/this.linkedProxies.length : 0);
                        this.isPlaced    = true;
                        helper_.debug("[vertex.defineRelativePoz] " + this.object.name +
                            " : {relX: " + this.relX + ", relY: " + this.relY + "}");
                        this.defineRelativeLeafsFromRootPoz(this.relX, this.relY);
                    } else {
                        // root relX = 0, relY = 0
                        // place leafs from root coord
                        this.isPlaced = true;
                        helper_.debug("[vertex.defineRelativePoz] " + this.object.name +
                            " : {relX: " + this.relX + ", relY: " + this.relY + "}");
                        this.defineRelativeLeafsFromRootPoz(0,0);
                        this.orientStep  = (this.linkedProxies.length>1) ? 2*Math.PI/this.linkedProxies.length : 0;
                        this.radStep = this.defineProxyRadStepValue(this.orientStep);
                    }
                    // helper_.debug("[vertex.defineRelativePoz] " + this.object.name + " : {orientStep: " + this.orientStep +
                    //     ", radStep: " + this.radStep + ", orientV: " + orientV + ", relX: " + this.relX + ", relY: " + this.relY +
                    //     ", relFirstChT: " + this.relFirstChT + "}");
                }
            };

            this.defineAbsolutePoz = function(treeCenterX, treeCenterY) {
                this.object.setBubbleCoord(this.relX+treeCenterX,this.relY+treeCenterY);
                this.object.defineChildsPoz();
            };
        }

        return aVertex;
    });