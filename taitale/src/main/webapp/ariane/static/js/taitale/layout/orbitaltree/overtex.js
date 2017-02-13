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

            this.relX           = 0;     // relative X (center of the rect)
            this.relY           = 0;     // relative Y (center of the rect)
            this.orientV        = 0;     // relative orientation from rootV coords
            this.relFirstChT    = 0;     // relative orientation of first child
            this.orientStep     = 0;     // orientation step from this vertex to next floor
            this.isPlaced       = false;
            this.isSimpleProxy  = false;
            this.isLeaf         = false;
            this.leafOrbitFloor = [];
            this.leafFloorCount = 0;
            this.orbitToNextFloor  = null;
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

            this.computeBubbleCenterFromOrbitalInputs = function(floor, orbitInputs, orientV, rootVRelX, rootVRelY, objSize) {
                var cos = parseFloat(Math.cos(orientV).toFixed(10)),
                    sin = parseFloat(Math.sin(orientV).toFixed(10)),
                    excentricity = Math.sqrt(Math.pow(orbitInputs.smajor,2)-Math.pow(orbitInputs.sminor,2))/orbitInputs.smajor,
                    ro = orbitInputs.sminor/Math.sqrt(1 - Math.pow(excentricity,2)*Math.pow(cos,2)),
                    relX, relY;

                // helper_.debug("[vertex.computeBubbleCenterFromOrbitalInputs] " + this.object.name + " : " +
                //     "{orbitInputs: " + JSON.stringify(orbitInputs) + ", ro: " + ro + ", objSize: " + JSON.stringify(objSize) + "}");
                relX = (!orbitInputs.isVerticalOrbit) ? rootVRelX + objSize.width/2 + (ro+objSize.rad)*Math.cos(orientV) : rootVRelX + objSize.width/2 + ro * Math.sin(orientV);
                relY = (!orbitInputs.isVerticalOrbit) ? rootVRelY + objSize.height/2 + (ro+objSize.rad) * Math.sin(orientV) : rootVRelY + objSize.height/2 + ro * Math.cos(orientV);

                if (cos < 0) {
                    if (sin < 0) {
                        if (orbitInputs.isVerticalOrbit) {
                            relX -= objSize.width;
                            relY -= objSize.height;
                        } else {
                            relX -= objSize.width;
                            relY -= objSize.height;
                        }
                    } else if (sin > 0) {
                        if (orbitInputs.isVerticalOrbit)  relY -= objSize.height;
                        else relX -= objSize.width;
                    } else {
                        if (orbitInputs.isVerticalOrbit) {
                            relX -= objSize.width/2;
                            relY -= objSize.height;
                        } else {
                            relX -= objSize.width;
                            relY -= objSize.height/2;
                        }
                    }
                } else if (cos > 0) {
                    if (sin < 0) {
                        if (orbitInputs.isVerticalOrbit) relX -= objSize.width;
                        else relY -= objSize.height;
                    } else if (sin == 0) {
                        if (orbitInputs.isVerticalOrbit) relX -= objSize.width/2;
                        else relY -= objSize.height/2;
                    }
                } else {
                    if (sin > 0) {
                        if (orbitInputs.isVerticalOrbit) relY -= objSize.height/2;
                        else relX -= objSize.width/2;
                    } else {
                        if (orbitInputs.isVerticalOrbit) {
                            relX -= objSize.width;
                            relY -= objSize.height/2;
                        } else {
                            relX -= objSize.width/2;
                            relY -= objSize.height;
                        }
                    }
                }

                return {
                    'ro': ro,
                    'relX': relX,
                    'relY': relY
                }
            };

            this.computeOrbitMaxInputsFromArray = function (floor, floorArray) {

                var lastFloorInputs = this.leafOrbitFloor[floor - 1], currentInputs, maxHRad=0, maxVRad=0, i, ii, result;
                // helper_.debug("[vertex.computeOrbitMaxInputsFromArray] " + this.object.name +
                //     " : {" + floor + ", " + JSON.stringify(lastFloorInputs) + "}");
                for (i=0, ii=floorArray.length; i < ii; i++) {
                    currentInputs = floorArray[i].object.getOrbitalInputs();
                    if (currentInputs.isVerticalOrbit) {
                        if (maxVRad < currentInputs.smajor) maxVRad = currentInputs.smajor;
                        if (maxHRad < currentInputs.sminor) maxHRad = currentInputs.sminor;
                    } else {
                        if (maxVRad < currentInputs.sminor) maxVRad = currentInputs.sminor;
                        if (maxHRad < currentInputs.smajor) maxHRad = currentInputs.smajor;
                    }
                }
                if (lastFloorInputs.isVerticalOrbit) {
                    maxVRad += lastFloorInputs.smajor;
                    maxHRad += lastFloorInputs.sminor;
                } else {
                    maxVRad += lastFloorInputs.sminor;
                    maxHRad += lastFloorInputs.smajor;
                }
                result =  {
                    'isVerticalOrbit': (maxVRad>maxHRad),
                    'smajor': (maxHRad>maxVRad) ? maxHRad : maxVRad,
                    'sminor': (maxHRad>maxVRad) ? maxVRad : maxHRad
                };
                // helper_.debug("[vertex.computeOrbitMaxInputsFromArray] " + this.object.name + " : " + JSON.stringify(result));
                return result;
            };

            this.refineOrbitMaxInputs = function(floor, orbitMaxInputs) {
                var i, ii, objRad1, objRad2, objSize1, objSize2, orientV1, orientV2, rad1, rad2,
                    listToExplore, orientStep, cos, orbitMaxInputsIsOK, disto1o2;

                if (floor < this.linkedLeafsByFloor.length) {
                    listToExplore = this.linkedLeafsByFloor[floor];
                    orientStep = 2*Math.PI / this.linkedLeafsByFloor[floor].length;
                } else {
                    listToExplore = this.linkedProxies;
                    orientStep = this.orientStep;
                }

                cos = parseFloat(Math.cos(orientStep).toFixed(10));
                for (i = 0, ii = listToExplore.length; i+1 < ii; i++) {
                    objRad1 = listToExplore[i].object.getBubbleInputs().diameter / 2;
                    objRad2 = listToExplore[i+1].object.getBubbleInputs().diameter / 2;
                    objSize1 = listToExplore[i].object.getRectSize();
                    if (floor < this.linkedLeafsByFloor.length) objSize1.rad = 0;
                    else objSize1.rad = objRad1;
                    objSize2 = listToExplore[i].object.getRectSize();
                    if (floor < this.linkedLeafsByFloor.length) objSize2.rad = 0;
                    else objSize2.rad = objRad2;
                    orientV1 = i*orientStep;
                    orientV2 = (i+1)*orientStep;
                    orbitMaxInputsIsOK=false;
                    while(!orbitMaxInputsIsOK) {
                        rad1 = this.computeBubbleCenterFromOrbitalInputs(floor, orbitMaxInputs, orientV1, 0, 0, objSize1).ro;
                        rad2 = this.computeBubbleCenterFromOrbitalInputs(floor, orbitMaxInputs, orientV2, 0, 0, objSize2).ro;
                        disto1o2 = Math.sqrt(Math.pow(rad1,2) + Math.pow(rad2,2) - 2*rad1*rad2*cos);
                        if (disto1o2 >= (objRad1+objRad2)/2) orbitMaxInputsIsOK = true;
                        else {
                            orbitMaxInputs.sminor += 10;
                            orbitMaxInputs.smajor += 10;
                        }
                    }
                }
            };

            this.computeOrbitMaxInputs = function(floor) {
                var inputs;
                if (floor == 0) inputs = this.object.getOrbitalInputs();
                else if (floor < this.linkedLeafsByFloor.length) inputs = this.computeOrbitMaxInputsFromArray(floor, this.linkedLeafsByFloor[floor]);
                else inputs = this.computeOrbitMaxInputsFromArray(floor, this.linkedProxies);

                this.refineOrbitMaxInputs(floor, inputs);
                // helper_.debug("[vertex.computeOrbitMaxInputs] " + this.object.name + " : {floor: " + floor + ", array: " +
                //     ((floor == this.linkedLeafsByFloor.length) ? "linkedProxies array" : ((floor != 0) ? "linkedLeafsByFloor[" + floor + "]" : "root object orbital inputs")) +
                //     ", inputs: " + JSON.stringify(inputs) + "}");
                return inputs;
            };

            this.getLeafOrientStep = function(floor) {
                // helper_.debug("[vertex.getLeafOrientStep] " + this.object.name + " : " +
                //     "\n{rootV: " + ((this.rootV!=null) ? this.rootV.object.name : "None") + ", leafFloorCount: " + this.leafFloorCount +
                //     ", floor: " + floor + ", linkedLeafsByFloor[leafFloorCount-1].length: " +
                //     ((this.leafFloorCount>0) ? this.linkedLeafsByFloor[this.leafFloorCount-1].length : 0) +"}");
                var leafOrientStep;
                if (floor < this.leafFloorCount-1) leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                else {
                    if (this.rootV!=null) {
                        if (this.linkedLeafsByFloor[floor].length > (this.leafFloorCount+1)*2) leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                        else leafOrientStep = Math.PI/this.linkedLeafsByFloor[floor].length;
                    } else leafOrientStep = 2 * Math.PI/this.linkedLeafsByFloor[floor].length;
                }
                return leafOrientStep;
            };

            this.computeVertexRelativePozOnOrbit = function(floor) {
                var orbitInputs  = (floor < this.rootV.leafOrbitFloor.length) ? this.rootV.leafOrbitFloor[floor] : this.rootV.orbitToNextFloor,
                    objSize = this.object.getRectSize(),computedCenter;

                if ((this.leafOrbitFloor.length==0 && this.rootV.leafOrbitFloor.length==0) || floor < this.rootV.leafOrbitFloor.length) objSize.rad = 0;
                else {
                    if (this.linkedLeafsByFloor.length > 0 && this.linkedLeafsByFloor[this.leafFloorCount-1].length < (this.leafFloorCount+1)*2) objSize.rad = 0;
                    else {
                        if (Math.abs(Math.cos(this.orientV)) < Math.abs(Math.sin(this.orientV))) objSize.rad = this.orbitToNextFloor.sminor;
                        else objSize.rad = this.orbitToNextFloor.smajor;
                    }
                    //objSize.rad = this.orbitToNextFloor.smajor;
                }
                computedCenter = this.computeBubbleCenterFromOrbitalInputs(floor, orbitInputs, this.orientV, this.rootV.relX, this.rootV.relY, objSize);
                this.relX = computedCenter.relX;
                this.relY = computedCenter.relY;
                this.isPlaced = true;

                // helper_.debug("[vertex.computeVertexRelativePozOnOrbit] " + this.object.name + " : " +
                //     "{relX: " + this.relX + ", relY: " + this.relY + "}");
            };

            this.defineLeafsRelativePozFromRoot = function() {
                var i, ii, j, jj, leafOrientStep, aVertex, nextRelFirstCht = this.relFirstChT;
                // if (this.linkedLeafsByFloor.length > 0)
                //     helper_.debug("[vertex.defineLeafsRelativePozFromRoot] " + this.object.name + " : " +
                //         "{ relX: " + this.relX + ", relY: " + this.relY + ", orientV: " + this.orientV +
                //         ", orientStep: " + this.orientStep + ", radiusToNextFloor: " + this.radiusToNextFloor +
                //         ", relFirstChT: " + this.relFirstChT + " }");
                for (i = 0, ii = this.linkedLeafsByFloor.length; i < ii; i++) {
                    for (j = 0, jj = this.linkedLeafsByFloor[i].length; j < jj; j++) {
                        aVertex = this.linkedLeafsByFloor[i][j];
                        if (!aVertex.isPlaced) {
                            leafOrientStep = this.getLeafOrientStep(i);
                            // helper_.debug("[vertex.defineLeafsRelativePozFromRoot] (" + this.object.name + ") " + aVertex.object.name +
                            //      " : " + leafOrientStep + "; " + this.relFirstChT + "; " + j);
                            if (Math.round(this.relFirstChT*100)/100 != Math.round((leafOrientStep/(this.linkedLeafsByFloor[i].length-1))*100)/100)
                                aVertex.orientV = this.relFirstChT + ((i % 2 == 0) ? 0 : Math.PI / 16) + j * leafOrientStep;
                            else
                                aVertex.orientV = this.relFirstChT + ((i % 2 == 0) ? 0 : Math.PI / 16) + ((j%2 == 0) ? 0 : Math.PI) + j * leafOrientStep;
                            // helper_.debug("[vertex.defineLeafsRelativePozFromRoot] (" + this.object.name + ") " + aVertex.object.name +
                            //     " : " + aVertex.orientV);

                            if (i==this.leafFloorCount-1 && this.simplePrxOnLastLeafsFloorCount>0 && j<=this.simplePrxOnLastLeafsFloorCount)
                                nextRelFirstCht = aVertex.orientV;

                            aVertex.computeVertexRelativePozOnOrbit(i);
                            if (!aVertex.isLeaf) {
                                if (this.linkedProxies.length <= 2) aVertex.orientStep = (aVertex.linkedProxies.length>1) ? Math.PI/aVertex.linkedProxies.length : 0;
                                else aVertex.orientStep = (aVertex.linkedProxies.length>1) ? this.orientStep*2/aVertex.linkedProxies.length : this.orientStep*2 ;
                                aVertex.orbitToNextFloor = aVertex.computeOrbitMaxInputs(aVertex.leafFloorCount);
                                aVertex.relFirstChT = aVertex.orientV - ((aVertex.linkedProxies.length>1) ? aVertex.orientStep/aVertex.linkedProxies.length : 0);
                                aVertex.defineLeafsRelativePozFromRoot();
                            }
                            // helper_.debug("[vertex.defineLeafsRelativePozFromRoot - " + this.object.name + "] " + aVertex.object.name + " : " +
                            //     "{relX: " + aVertex.relX + ", relY: " + aVertex.relY + ", orientV: " + aVertex.orientV + "}");
                        }
                    }
                }
                this.relFirstChT = nextRelFirstCht;
            };

            this.defineRelativeLeafsData = function() {
                var i, ii, j, jj, aVertex, freePlaceOnLastLeafsStep;

                if (this.linkedLeafs.length > 0) {
                    this.linkedLeafs.sort(function(linkedObject1, linkedObject2) {
                        return (linkedObject2.object.getLinksCount() - linkedObject1.object.getLinksCount());
                    });
                    this.linkedLeafsByFloor = [];
                    this.leafFloorCount = 1;

                    var vStepCounter = 0, currentLeafsFloorLen = (this.leafFloorCount + 1) * 4;
                    for (i = 0, ii = this.linkedLeafs.length; i < ii; i++) {
                        aVertex = this.linkedLeafs[i];
                        // helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                        //     " current leafFloorCount: " + this.leafFloorCount);
                        // helper_.debug("[vertex.defineRelativeLeafsData] " + this.object.name +
                        //     " current currentLeafsFloorLen: " + currentLeafsFloorLen);
                        if (vStepCounter < currentLeafsFloorLen) {
                            if (this.linkedLeafsByFloor[this.leafFloorCount - 1] == null)
                                this.linkedLeafsByFloor[this.leafFloorCount - 1] = [];
                            this.linkedLeafsByFloor[this.leafFloorCount - 1].push(aVertex);
                            vStepCounter += 1;
                        } else {
                            this.leafFloorCount += 1;
                            this.linkedLeafsByFloor[this.leafFloorCount - 1] = [];
                            this.linkedLeafsByFloor[this.leafFloorCount - 1].push(aVertex);
                            vStepCounter = 1;
                            currentLeafsFloorLen = (this.leafFloorCount + 1) * 4;
                        }
                    }

                    freePlaceOnLastLeafsStep = (this.leafFloorCount + 1) * 4 - this.linkedLeafsByFloor[this.leafFloorCount - 1].length;

                    for (i = 0, ii = this.linkedLeafsByFloor.length; i < ii; i++) {
                        if (i == this.leafFloorCount - 1) {
                            var spIdx2RM = [];
                            for (j = 0, jj = this.linkedSimpleProxies.length; j < jj; j++) {
                                if (j > (freePlaceOnLastLeafsStep - 1)) break;
                                this.linkedLeafsByFloor[i].splice(0, 0, this.linkedSimpleProxies[j]);
                                spIdx2RM.push(j);
                                this.simplePrxOnLastLeafsFloorCount++;
                            }
                            for (j = 0, jj = spIdx2RM.length; j < jj; j++) this.linkedSimpleProxies.splice(spIdx2RM[j], 1);
                        }
                        this.leafOrbitFloor[i] = this.computeOrbitMaxInputs(i);
                    }
                }
                for (i = 0, ii = this.linkedSimpleProxies.length; i < ii; i++) {
                    this.linkedSimpleProxies[i].idFromRoot = this.linkedProxies.length;
                    this.linkedProxies.push(this.linkedSimpleProxies[i]);
                }
                this.linkedSimpleProxies = [];
            };

            this.defineRelativePoz = function() {
                // if (!this.isPlaced)
                //     helper_.debug("[vertex.defineRelativePoz] " + this.object.name + " : " +
                //         "{isPlaced: " + this.isPlaced + ", isLeaf: " + this.isLeaf +
                //         ", rootV: " + ((this.rootV!=null) ? this.rootV.object.name : "NONE") +
                //         ", floor: " + this.floor + ", objectOrbitalInputs: " + JSON.stringify(this.object.getOrbitalInputs())+ "}");
                if (!this.isPlaced && !this.isLeaf) {
                    if (this.rootV!=null && this.floor!=0) {
                        var rootLinkedProxies = this.rootV.linkedProxies;
                        if (rootLinkedProxies.length <= 2)
                            this.orientStep = (this.linkedProxies.length>1) ? Math.PI/this.linkedProxies.length : 0;
                        else
                            this.orientStep  = (this.linkedProxies.length>1) ? this.rootV.orientStep*2/this.linkedProxies.length : this.rootV.orientStep*2 ;
                        this.orbitToNextFloor = this.computeOrbitMaxInputs(this.leafFloorCount);
                        this.orientV = this.rootV.relFirstChT + this.idFromRoot*this.rootV.orientStep;
                        this.relFirstChT = this.orientV - ((this.linkedProxies.length>1) ? this.orientStep/this.linkedProxies.length : 0);

                        this.computeVertexRelativePozOnOrbit(this.rootV.leafFloorCount);
                        this.defineLeafsRelativePozFromRoot();
                    } else {
                        this.isPlaced = true;
                        this.defineLeafsRelativePozFromRoot();
                        this.orientStep  = (this.linkedProxies.length>1) ? 2*Math.PI/this.linkedProxies.length : 0;
                        this.orbitToNextFloor = this.computeOrbitMaxInputs(this.leafFloorCount);
                    }
                    // helper_.debug("[vertex.defineRelativePoz] " + this.object.name + " : " +
                    //     "{ relX: " + this.relX + ", relY: " + this.relY + ", orientV: " + this.orientV + ", idFromRoot: " + this.idFromRoot +
                    //     ", orientStep: " + this.orientStep + ", orbitToNextFloor: " + JSON.stringify(this.orbitToNextFloor) +
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