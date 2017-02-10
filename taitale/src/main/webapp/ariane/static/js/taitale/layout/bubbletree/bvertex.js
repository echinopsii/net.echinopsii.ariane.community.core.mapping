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
            this.orientV        = 0;     // relative orientation from rootV coords
            this.relFirstChT    = 0;     // relative orientation of first child
            this.orientStep     = 0;     // orientation step from this vertex to next floor
            this.isPlaced       = false;
            this.isSimpleProxy  = false;
            this.isLeaf         = false;
            this.leafRadFloor   = [];
            this.leafRadFloorCount = 0;
            this.simplePrxOnLastLeafsFloorCount = 0;

            this.linkedLeafs         = [];
            this.linkedLeafsByFloor  = [];
            this.linkedSimpleProxies = [];
            this.linkedProxies       = [];

            // TODO: prototype
            // currently object is a container.
            // any object should have method getRectSize available
            this.pushLinkedVertex = function(vertex) {
                var linkedTreeObjectsCount = vertex.object.getLinkedTreeObjectsCount();
                // helper_.debug("[vertex.pushLinkedVertex] (" + this.object.name + ") " + vertex.object.name +
                //     " has " + linkedTreeObjectsCount + " linked tree object(s)...");
                vertex.rootV = this;
                vertex.floor = this.floor + 1;
                if (linkedTreeObjectsCount==1 && !this.isLeaf) {
                    this.linkedLeafs.push(vertex);
                    vertex.isLeaf = true;
                } else {
                    if (!this.isSimpleProxy && linkedTreeObjectsCount==2) {
                        vertex.isSimpleProxy = true;
                        this.linkedSimpleProxies.push(vertex);
                    } else {
                        vertex.idFromRoot = this.linkedProxies.length;
                        this.linkedProxies.push(vertex);
                    }
                }
            };

            this.getMaxChildRad = function(leafFloor) {
                var maxRad = 0, currentRad, i, ii;
                // helper_.debug("[vertex.getMaxChildRad] " + this.object.name + " : " +
                //     "{leafFloor: " + + leafFloor + ", linkedLeafsByFloor.length: " + this.linkedLeafsByFloor.length +
                //     ", linkedProxies.length: " + this.linkedProxies.length + "}");
                if (leafFloor < this.linkedLeafsByFloor.length) {
                    for (i = 0, ii = this.linkedLeafsByFloor[leafFloor].length; i < ii; i++) {
                        currentRad = this.linkedLeafsByFloor[leafFloor][i].object.getBubbleInputs().diameter/2;
                        if (currentRad>maxRad)
                            maxRad = currentRad
                    }
                } else {
                    for (i = 0, ii = this.linkedProxies.length; i < ii; i++) {
                        currentRad = this.linkedProxies[i].object.getBubbleInputs().diameter/2;
                        if (currentRad>maxRad)
                            maxRad = currentRad
                    }
                }
                // helper_.debug("[vertex.getMaxChildRad] " + this.object.name + " : " + + maxRad);
                return maxRad
            };

            this.definedRadStepValueFromList = function(listToExplore, currentRadStep, orientStep) {
                var currentRad1, currentRad2, teta1, teta2, i, ii, radStepIsOK, lambda = 0;
                for (i = 0, ii = listToExplore.length; i+1 < ii; i++) {
                    currentRad1 = listToExplore[i].object.getBubbleInputs().diameter / 2;
                    currentRad2 = listToExplore[i + 1].object.getBubbleInputs().diameter / 2;
                    radStepIsOK = false;
                    while (!radStepIsOK) {
                        teta1 = Math.atan(currentRad1 / (currentRadStep * 2)) * 2;
                        teta2 = Math.atan(currentRad2 / (currentRadStep * 2)) * 2;
                        // avoid infinite currentRadStep computing. limit equality with two digit
                        if (Math.abs(orientStep) < 1) lambda = 0.3;
                        if (Math.abs(orientStep) > (teta1 + teta2 - lambda)) radStepIsOK = true;
                        else if (Math.abs(orientStep) == (teta1 + teta2 - lambda)) {
                            if (lambda>0) currentRadStep += 100;
                            else currentRadStep += 10;
                            radStepIsOK = true;
                        } else {
                            if (lambda > 0) currentRadStep += 100;
                            else currentRadStep += 10;
                        }
                        // if (lambda!=0)
                        //     helper_.debug("[vertex.definedRadStepValueFromList] " + this.object.name + " : " +
                        //          "\n{currentRadStep: " + currentRadStep + ", lambda: " + lambda +
                        //          "\n, name1: " + listToExplore[i].object.name + ", currentRad1: " + currentRad1+ ", teta1: " + teta1 +
                        //          "\n, name2: " + listToExplore[i+1].object.name + ", currentRad2: " + currentRad2+ ", teta2: " + teta2 +
                        //          "\n, orientStep: " + leafOrientStep + ", radStepIsOK: " + radStepIsOK + "}");
                    }
                }
                return currentRadStep;
            };

            this.defineLeafRadStepValue = function(leafFloor, leafOrientStep) {
                var currentRadStep, listToExplore;
                // helper_.debug("[vertex.defineLeafRadStepValue] " + this.object.name + " : " +
                //     "{leafFloor: " + leafFloor + ", leafOrientStep"  + leafOrientStep + "}");
                currentRadStep = this.getMaxChildRad(leafFloor) + 10 +
                    ((leafFloor==0) ? this.object.getBubbleInputs().diameter/2 : this.leafRadFloor[leafFloor-1]);
                listToExplore = this.linkedLeafsByFloor[leafFloor];

                // helper_.debug("[vertex.defineLeafRadStepValue] " + this.object.name + " : " +
                //     "{currentRadStep: " + currentRadStep + "}");
                return this.definedRadStepValueFromList(listToExplore, currentRadStep, leafOrientStep);
            };

            this.defineProxyRadStepValue = function() {
                var i, ii, currentRadStep;
                // helper_.debug("[vertex.defineProxyRadStepValue] " + this.object.name + " : " +
                //     "{orientStep: " + this.orientStep + "}");

                currentRadStep = this.getMaxChildRad(this.leafRadFloorCount);
                if (this.leafRadFloorCount > 0) {
                    for (i = 0, ii = this.leafRadFloorCount; i < ii; i++)
                        currentRadStep += this.getMaxChildRad(i)*2;
                    // currentRadStep += this.getMaxChildRad(this.leafRadFloorCount-1);
                }
                currentRadStep += this.object.getBubbleInputs().diameter/2 + 10;

                // helper_.debug("[vertex.defineProxyRadStepValue] " + this.object.name + " : " +
                //     "{currentRadStep: " + currentRadStep + "}");
                return this.definedRadStepValueFromList(this.linkedProxies, currentRadStep, this.orientStep);
            };

            this.getLeafOrientStep = function(floor) {
                // helper_.debug("[vertex.getLeafOrientStep] " + this.object.name + " : " +
                //     "\n{rootV: " + ((this.rootV!=null) ? this.rootV.object.name : "None") + ", leafRadFloorCount: " + this.leafRadFloorCount +
                //     ", floor: " + floor + ", linkedLeafsByFloor[leafRadFloorCount-1].length: " +
                //     ((this.leafRadFloorCount>0) ? this.linkedLeafsByFloor[this.leafRadFloorCount-1].length : 0) +"}");
                var leafOrientStep;
                if (floor < this.leafRadFloorCount-1) leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                else {
                    if (this.rootV!=null) {
                        if (this.linkedLeafsByFloor[floor].length > this.leafRadFloorCount*4) leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                        else leafOrientStep = Math.PI/this.linkedLeafsByFloor[floor].length;
                    } else leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                }
                return leafOrientStep;
            };

            this.defineRelativeLeafsFromRootPoz = function() {
                var i, ii, j, jj, leafOrientStep, aVertex, nextRelFirstCht = this.relFirstChT;
                // if (this.linkedLeafsByFloor.length > 0)
                //     helper_.debug("[vertex.defineRelativeLeafsFromRootPoz] " + this.object.name + " : " +
                //         "{ relX: " + this.relX + ", relY: " + this.relY + ", orientV: " + this.orientV +
                //         ", orientStep: " + this.orientStep + ", radStep: " + this.radStep +
                //         ", relFirstChT: " + this.relFirstChT + " }");
                for (i = 0, ii = this.linkedLeafsByFloor.length; i < ii; i++) {
                    for (j = 0, jj = this.linkedLeafsByFloor[i].length; j < jj; j++) {
                        aVertex = this.linkedLeafsByFloor[i][j];
                        // helper_.debug("[vertex.defineRelativeLeafsFromRootPoz] (" + this.object.name + ") " + aVertex.object.name +
                        //     " : " + this.linkedLeafsByFloor[i].length);
                        if (!aVertex.isPlaced) {
                            leafOrientStep = this.getLeafOrientStep(i);
                            aVertex.orientV = this.relFirstChT + ((i % 2 == 0) ? 0 : Math.PI / 16) + j * leafOrientStep;
                            if (i==this.leafRadFloorCount-1 && this.simplePrxOnLastLeafsFloorCount>0 && j<=this.simplePrxOnLastLeafsFloorCount)
                                nextRelFirstCht = aVertex.orientV;
                            aVertex.relX = this.relX + this.leafRadFloor[i] * Math.cos(aVertex.orientV);
                            aVertex.relY = this.relY + this.leafRadFloor[i] * Math.sin(aVertex.orientV);
                            aVertex.isPlaced = true;
                            if (!aVertex.isLeaf) {
                                if (this.linkedProxies.length <= 2)
                                    aVertex.orientStep = (aVertex.linkedProxies.length>1) ? Math.PI/aVertex.linkedProxies.length : 0;
                                else
                                    aVertex.orientStep = (aVertex.linkedProxies.length>1) ? this.orientStep*2/aVertex.linkedProxies.length : this.orientStep*2 ;
                                aVertex.radStep = this.defineProxyRadStepValue();
                                aVertex.relFirstChT = aVertex.orientV - ((aVertex.linkedProxies.length>1) ? aVertex.orientStep/aVertex.linkedProxies.length : 0);
                                aVertex.defineRelativeLeafsFromRootPoz();
                            }
                            // helper_.debug("[vertex.defineRelativeLeafsFromRootPoz - " + this.object.name + "] " + aVertex.object.name + " : " +
                            //     "{relX: " + aVertex.relX + ", relY: " + aVertex.relY + ", orientV: " + aVertex.orientV + "}");
                        }
                    }
                }
                this.relFirstChT = nextRelFirstCht;
            };

            this.defineRelativeLeafsData = function() {
                var i, ii, j, jj, leafOrientStep, aVertex, freePlaceOnLastLeafsStep;

                if (this.linkedLeafs.length > 0) {
                    this.linkedLeafs.sort(function(linkedObject1, linkedObject2) {
                        return (linkedObject2.object.getLinksCount() - linkedObject1.object.getLinksCount());
                    });
                    this.linkedLeafsByFloor = [];
                    this.leafRadFloorCount = 1;

                    var vStepCounter = 0, currentLeafsFloorLen = this.leafRadFloorCount * 8;
                    for (i = 0, ii = this.linkedLeafs.length; i < ii; i++) {
                        aVertex = this.linkedLeafs[i];
                        // helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                        //     " current leafRadFloorCount: " + this.leafRadFloorCount);
                        // helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                        //     " current currentLeafsFloorLen: " + currentLeafsFloorLen);
                        if (vStepCounter < currentLeafsFloorLen) {
                            if (this.linkedLeafsByFloor[this.leafRadFloorCount - 1] == null)
                                this.linkedLeafsByFloor[this.leafRadFloorCount - 1] = [];
                            this.linkedLeafsByFloor[this.leafRadFloorCount - 1].push(aVertex);
                            vStepCounter += 1;
                        } else {
                            this.leafRadFloorCount += 1;
                            this.linkedLeafsByFloor[this.leafRadFloorCount - 1] = [];
                            this.linkedLeafsByFloor[this.leafRadFloorCount - 1].push(aVertex);
                            vStepCounter = 1;
                            currentLeafsFloorLen = this.leafRadFloorCount * 8;
                        }
                    }

                    freePlaceOnLastLeafsStep = this.leafRadFloorCount * 8 - this.linkedLeafsByFloor[this.leafRadFloorCount - 1].length;

                    for (i = 0, ii = this.linkedLeafsByFloor.length; i < ii; i++) {
                        if (i == this.leafRadFloorCount - 1) {
                            var spIdx2RM = [];
                            for (j = 0, jj = this.linkedSimpleProxies.length; j < jj; j++) {
                                if (j > (freePlaceOnLastLeafsStep - 1)) break;
                                this.linkedLeafsByFloor[i].splice(0, 0, this.linkedSimpleProxies[j]);
                                spIdx2RM.push(j);
                                this.simplePrxOnLastLeafsFloorCount++;
                            }
                            for (j = 0, jj = spIdx2RM.length; j < jj; j++) this.linkedSimpleProxies.splice(spIdx2RM[j], 1);
                        }

                        leafOrientStep = 2 * Math.PI / this.linkedLeafsByFloor[i].length;
                        this.leafRadFloor[i] = this.defineLeafRadStepValue(i, leafOrientStep);
                    }
                }
                for (i = 0, ii = this.linkedSimpleProxies.length; i < ii; i++) {
                    this.linkedSimpleProxies[i].idFromRoot = this.linkedProxies.length;
                    this.linkedProxies.push(this.linkedSimpleProxies[i]);
                }
                this.linkedSimpleProxies = [];
            };

            this.getRadSetFromRoot = function() {
                // helper_.debug("[vertex.getRadSetFromRoot] " + this.object.name + " : " +
                //     "\n{leafRadFloorCount: " + this.leafRadFloorCount +
                //     ", linkedLeafsByFloor[0].length: " + ((this.leafRadFloorCount>0) ? this.linkedLeafsByFloor[0].length : 0) +"}");
                if (this.leafRadFloorCount>1) return this.rootV.radStep+this.radStep;
                else if (this.leafRadFloorCount==1 && this.linkedLeafsByFloor[0].length > 4) return this.rootV.radStep+this.radStep;
                else return this.rootV.radStep;
            };

            this.defineRelativePoz = function() {
                // helper_.debug("[vertex.defineRelativePoz] " + this.object.name + " : " +
                //     "{isPlaced: " + this.isPlaced + ", isLeaf: " + this.isLeaf +
                //     ", rootV: " + ((this.rootV!=null) ? this.rootV.object.name : "NONE") +
                //     ", floor: " + this.floor + "}");
                if (!this.isPlaced && !this.isLeaf) {
                    if (this.rootV!=null && this.floor!=0) {
                        var rootLinkedProxies = this.rootV.linkedProxies;
                        if (rootLinkedProxies.length <= 2)
                            this.orientStep = (this.linkedProxies.length>1) ? Math.PI/this.linkedProxies.length : 0;
                        else
                            this.orientStep  = (this.linkedProxies.length>1) ? this.rootV.orientStep*2/this.linkedProxies.length : this.rootV.orientStep*2 ;
                        this.radStep = this.defineProxyRadStepValue();

                        this.orientV = this.rootV.relFirstChT + this.idFromRoot*this.rootV.orientStep;
                        this.relX    = this.rootV.relX + this.getRadSetFromRoot()*Math.cos(this.orientV);
                        this.relY    = this.rootV.relY + this.getRadSetFromRoot()*Math.sin(this.orientV);
                        this.relFirstChT = this.orientV - ((this.linkedProxies.length>1) ? this.orientStep/this.linkedProxies.length : 0);
                        this.isPlaced    = true;
                        this.defineRelativeLeafsFromRootPoz();
                    } else {
                        this.isPlaced = true;
                        this.defineRelativeLeafsFromRootPoz();
                        this.orientStep  = (this.linkedProxies.length>1) ? 2*Math.PI/this.linkedProxies.length : 0;
                        this.radStep = this.defineProxyRadStepValue();
                    }
                    // helper_.debug("[vertex.defineRelativePoz] " + this.object.name + " : " +
                    //     "{ relX: " + this.relX + ", relY: " + this.relY + ", orientV: " + this.orientV + ", idFromRoot: " + this.idFromRoot +
                    //     ", orientStep: " + this.orientStep + ", radStep: " + this.radStep +
                    //     ", relFirstChT: " + this.relFirstChT + " }");
                }
            };

            this.defineAbsolutePoz = function(treeCenterX, treeCenterY) {
                this.object.setBubbleCoord(this.relX+treeCenterX,this.relY+treeCenterY);
                this.object.defineChildsPoz();
            };
        }

        return aVertex;
    });