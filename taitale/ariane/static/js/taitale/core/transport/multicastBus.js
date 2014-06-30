// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Transport Multicast Bus         │ \\
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
        'taitale-cylinder',
        'taitale-helper'
    ],
    function (cylinder, helper) {
        function multicastBus(tid, ridx, localisation, multicastAddr_, properties_) {
            this.ID            = tid*1000+ridx;

            this.dcName        = localisation.getDatacenter().dc;
            this.areaName      = localisation.getArea().marea;
            this.multicastAddr = multicastAddr_;
            this.properties    = properties_;
            this.isInserted    = false;
            this.isMoving      = false;

            this.diameter      = 30;
            this.longg         = 400;

            this.mbus = null;

            this.linkedTreeObjects         = [];
            this.sortOrdering              = 1;

            this.r = null;

            //var helper_       = new helper();

            this.maxLinkedTreeObjectsCount = function(linkedObject1, linkedObject2) {
                return (linkedObject2.getLinkedTreeObjectsCount() - linkedObject1.getLinkedTreeObjectsCount())*this.sortOrdering;
            };

            this.getMaxBoxSize = function() {
                return {
                    width  : this.longg,
                    height : this.diameter
                };
            };

            this.getName = function() {
                return "Multicast Bus " + this.multicastAddr + " ({" + this.dcName + "," + this.areaName + "})"
            };

            this.getLinkedTreeObjectsCount = function() {
                return this.linkedTreeObjects.length;
            };

            this.sortLinkedTreeObjects = function() {
                this.linkedTreeObjects.sort(this.maxLinkedTreeObjectsCount);
            };

            this.pushLinkedTreeObject = function(object) {
                this.linkedTreeObjects.push(object);
            };

            this.pushBindedLink = function(link){
                this.mbus.pushBindedLink(link);
            };

            this.equal = function(multicastBus) {
                return (this.dcName        === multicastBus.dcName &&
                        this.areaName      === multicastBus.areaName &&
                        this.multicastAddr === multicastBus.multicastAddr)
            };

            this.setTopLeftCoord = function(x,y) {
                var centerX = x + this.longg/ 2, centerY = y + this.diameter/2,
                    title = (this.properties != null && this.properties.busDescription != null) ? this.properties.busDescription + " " + this.multicastAddr : this.multicastAddr,
                    color = (this.properties != null && this.properties.primaryApplication != null) ? this.properties.primaryApplication.color : "000000";
                this.mbus = new cylinder(this,centerX,centerY,this.diameter,this.longg,title,color);
            };

            this.definedNodesPoz = function() {
            };

            this.setCylinder = function(centerX,centerY) {
                var title = (this.properties != null && this.properties.busDescription     != null) ? this.properties.busDescription + " " + this.multicastAddr : this.multicastAddr,
                    color = (this.properties != null && this.properties.primaryApplication != null) ? this.properties.primaryApplication.color : "000000";
                this.mbus = new cylinder(this,centerX,centerY,this.diameter,this.longg,title,color);
            };

            this.setMoveJail = function(minX, minY, maxX, maxY){
                this.mbus.setMoveJail(minX,minY,maxX,maxY);
            };

            this.getBusSize = function() {
                return {
                    width:this.longg,
                    height:this.diameter
                }
            };

            this.getBusCoords = function() {
                return this.mbus.getTopLeftCoords();
            };

            this.print = function(r) {
                this.r = r;
                this.mbus.print(r);
            };
        }
        return multicastBus;
    });