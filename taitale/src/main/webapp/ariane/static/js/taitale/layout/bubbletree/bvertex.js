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
        function vertex(/*vertexid_*/object_) {
            var object         = object_,
                vertexid       = object.ID,
                rootV          = null,
                floor          = 0;

            var helper_ = new helper();

            var idFromRoot     = 0;

            var	radStep         = 0,
                relX            = 0,     // relative X (center of the rect)
                relY            = 0,     // relative Y (center of the rect)
                relFirstChT     = 0,     // relative orientation of first child
                orientStep      = 0,     // orientation step from this vertex to next floor
                isPlaced        = false;

            var	linkedVertexx  = [];

            // TODO: prototype
            // currently object is a container.
            // any object should have method getRectSize available

            this.getVertexID = function() {
                return vertexid;
            };

            this.setRootV = function(rootv_) {
                rootV=rootv_;
            };

            this.getRootV = function() {
                return rootV;
            };

            this.setFloor = function(floor_) {
                floor=floor_;
            };

            this.getFloor = function() {
                return floor;
            };

            this.setIdFromRoot = function(id) {
                idFromRoot = id;
            };

            this.getIdFromRoot = function() {
                return idFromRoot;
            };

            this.getRadStep = function() {
                return radStep;
            };

            this.getRelX = function() {
                return relX;
            };

            this.getRelY = function() {
                return relY;
            };

            this.getRelFirstChT = function() {
                return relFirstChT ;
            };

            this.getOrientStep = function() {
                return orientStep;
            };

            this.isPlaced = function() {
                return isPlaced;
            };

            this.pushLinkedVertex = function(vertex) {
                linkedVertexx.push(vertex);
            };

            this.getLinkedVertex = function() {
                return linkedVertexx;
            };

            this.setObject = function(object_) {
                object = object_;
            };

            this.getObject = function() {
                return object;
            };

            this.getMaxChildRad = function() {
                var maxRad = 0, currentRad, i, ii;
                for (i = 0, ii = linkedVertexx.length; i < ii; i++) {
                    currentRad=linkedVertexx[i].getObject().getBubbleDiameter()/2;
                    if (currentRad>maxRad)
                        maxRad = currentRad
                }
                return maxRad
            };

            this.defineRadStepValue = function(orientStep) {
                var currentRad1, currentRad2, teta1, teta2, i, ii;
                var radStepIsOK, currentRadStep = this.getMaxChildRad() + this.getObject().getBubbleDiameter()/2 + 10;
                for (i = 0, ii = linkedVertexx.length; i+1 < ii; i++) {
                    currentRad1 = linkedVertexx[i].getObject().getBubbleDiameter()/2;
                    currentRad2 = linkedVertexx[i+1].getObject().getBubbleDiameter()/2;
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

            this.defineRelativePoz = function() {
                var name = object.name;
                if (!isPlaced) {
                    if (rootV!=null && floor!=0) {
                        var rootLinkedVertexx = rootV.getLinkedVertex();
                        if (rootLinkedVertexx.length <= 2)
                            orientStep = Math.PI/linkedVertexx.length;
                        else
                            orientStep  = rootV.getOrientStep()*2/linkedVertexx.length;
                        radStep = this.defineRadStepValue(orientStep);

                        var orientV = rootV.getRelFirstChT()+idFromRoot*rootV.getOrientStep();
                        relX        = rootV.getRelX() + rootV.getRadStep()*Math.cos(orientV);
                        relY        = rootV.getRelY() + rootV.getRadStep()*Math.sin(orientV);

                        relFirstChT = orientV - ((linkedVertexx.length>2) ? (orientStep/(linkedVertexx.length-1)) : 0);
                    } else {
                        // root relX = 0, relY = 0
                        orientStep  = 2*Math.PI/(linkedVertexx.length);
                        radStep = this.defineRadStepValue(orientStep);
                        relFirstChT = 0;
                    }
                    //helper_.debug("[defineRelativePoz] " + object.name + " : {orientStep: " + orientStep +
                    //    ", radStep: " + radStep + ", orientV: " + orientV +
                    //    ", relX: " + relX + ", relY: " + relY + ", relFirstChT: " + relFirstChT);
                    isPlaced=true;
                }
            };

            this.defineAbsolutePoz = function(treeCenterX, treeCenterY) {
                object.setBubbleCoord(relX+treeCenterX,relY+treeCenterY);
                object.defineChildsPoz();
            };
        }

        return vertex;
    });