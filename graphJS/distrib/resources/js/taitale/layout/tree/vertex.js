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
    function() {
        function vertex(/*vertexid_*/object_) {
            var object         = object_,
                vertexid       = object.getID(),
                rootV          = null,
                floor          = 0;

            var idFromRoot     = 0;

            var	radX            = 0,     // radius from this vertex to next floor on X
                radY            = 0,     // radius from this vertex to next floor on Y
                relX            = 0,     // relative X (center of the rect)
                relY            = 0,     // relative Y (center of the rect)
                relFirstChT     = 0,     // relative orientation of first child
                orientStep      = 0,     // orientation step from this vertex to next floor
                isPlaced        = false,
                repulsionFactor = 0;

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

            this.getRadX = function() {
                return radX;
            };

            this.getRadY = function() {
                return radY;
            };

            this.getRelX = function() {
                return relX;
            };

            this.getRelY = function() {
                return relY;
            };

            this.getRelFirstChT = function() {
                var name = object.getName();
                return relFirstChT ;
            };

            this.getOrientStep = function() {
                var name = object.getName();
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
            }

            this.setObject = function(object_) {
                object = object_;
            };

            this.getObject = function() {
                return object;
            };

            this.incrementRepulsionFactor = function(repulsion) {
                repulsionFactor += repulsion;
                if (rootV!=null && repulsion > 1)
                    rootV.incrementRepulsionFactor(repulsion-1);
            };

            this.getMaxChildSize = function() {
                var maxChildWidth  = 0,
                    maxChildHeight = 0;

                for (var i = 0, ii=linkedVertexx.length ; i < ii ; i++) {
                    var width=linkedVertexx[i].getObject().getMaxBoxSize().width,
                        height=linkedVertexx[i].getObject().getMaxBoxSize().height;
                    if (width>maxChildWidth)
                        maxChildWidth=width;
                    if (height>maxChildHeight)
                        maxChildHeight=height;
                }

                return {x: maxChildWidth, y: maxChildHeight};
            };

            this.defineRadXValue = function(maxChildSize) {
                var tan  ;
                if (orientStep<Math.PI/4) {
                    tan = Math.log(Math.abs(Math.tan(orientStep)))/Math.LN10;
                } else {
                    tan = 1;
                }
                var calR1 = (maxChildSize.y*1.3)/tan;
                var rad1  = Math.max(maxChildSize.x*1.3,calR1);//*((repulsionFactor>2)?Math.log(repulsionFactor):repulsionFactor);

                // reduce as much a possible the radX value
                if ((maxChildSize.y/maxChildSize.x) > 1) {
                    rad1 = rad1*(maxChildSize.x/maxChildSize.y);
                } else {
                    rad1 = rad1*(maxChildSize.y/maxChildSize.x);
                }

                // be sure there is no overstep with current object
                if (rad1 < this.getObject().getMaxBoxSize().width) {
                    rad1 = rad1+ this.getObject().getMaxBoxSize().width/1.3;
                }
                return rad1;
            };

            this.defineRadYValue = function(maxChildSize) {
                var tan  ;
                if (orientStep<Math.PI/4) {
                    tan = Math.log(Math.abs(Math.tan(orientStep)))/Math.LN10;
                } else {
                    tan = 1;
                }
                var calR2 = (maxChildSize.x*1.3)/tan;
                var rad2 = Math.max(maxChildSize.y*1.3,calR2);//*((repulsionFactor>2)?Math.log(repulsionFactor):repulsionFactor);

                // reduce as much a possible the radX value
                if ((maxChildSize.x/maxChildSize.y) > 1) {
                    rad2 = rad2*(maxChildSize.y/maxChildSize.x);
                } else {
                    rad2 = rad2*(maxChildSize.x/maxChildSize.y);
                }

                // be sure there is no overstep with current object
                if (rad2 < this.getObject().getMaxBoxSize().height) {
                    rad2 = rad2+ this.getObject().getMaxBoxSize().height/1.3;
                }
                return rad2;
            };

            this.defineRelativePoz = function() {
                var name = object.getName();
                if (!isPlaced) {
                    if (rootV!=null && floor!=0) {
                        orientStep  = rootV.getOrientStep()*2/linkedVertexx.length;
                        radX        = this.defineRadXValue(this.getMaxChildSize());
                        radY        = this.defineRadYValue(this.getMaxChildSize());

                        var orientV = rootV.getRelFirstChT()+idFromRoot*rootV.getOrientStep();
                        relX        = rootV.getRelX() + rootV.getRadX()*((Math.cos(orientV)>0)?1:-1)*Math.abs(Math.cos(orientV))*((repulsionFactor>2)?Math.log(repulsionFactor):repulsionFactor);
                        relY        = rootV.getRelY() + rootV.getRadY()*((Math.sin(orientV)>0)?-1:1)*Math.abs(Math.sin(orientV))*((repulsionFactor>2)?Math.log(repulsionFactor):repulsionFactor);

                        relFirstChT = orientV - ((linkedVertexx.length>2) ? (orientStep/(linkedVertexx.length-1)) : 0);
                    } else {
                        // root relX = 0, relY = 0
                        orientStep  = 2*Math.PI/(linkedVertexx.length);
                        radX        = this.defineRadXValue(this.getMaxChildSize());
                        radY        = this.defineRadYValue(this.getMaxChildSize());
                        relFirstChT = 0;
                    }
                    isPlaced=true;
                }
            };

            this.defineAbsolutePoz = function(treeCenterX, treeCenterY) {
                object.setTopLeftCoord(relX+treeCenterX,relY+treeCenterY);
                object.definedNodesPoz();
            };
        };

        return vertex;
    });