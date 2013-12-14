// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Transport Multicast Bus         │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure topology graph engine                            │ \\
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
        function multicastBus(tid, ridx, localisation, multicastAddr) {
            var id            = tid*1000+ridx,
                dcName        = localisation.getDatacenter().dc,
                areaName      = localisation.getArea().marea,
                multicastAddr = multicastAddr,
                helper_       = new helper(),
                isInserted    = false;

            var diameter      = 30,
                long          = 400;

            var mbus = null;

            var linkedTreeObjects         = [],
                sortOrdering              = 1;
                maxLinkedTreeObjectsCount = function(linkedObject1, linkedObject2) {
                    return (linkedObject2.getLinkedTreeObjectsCount() - linkedObject1.getLinkedTreeObjectsCount())*sortOrdering;
                };

            this.getID = function() {
                return id;
            };

            this.getMaxBoxSize = function() {
                return {
                    width  : long,
                    height : diameter
                };
            };

            this.getName = function() {
                return "Multicast Bus " + multicastAddr + " ({" + dcName + "," + areaName + "})"
            }

            this.getLinkedTreeObjectsCount = function() {
                return linkedTreeObjects.length;
            };

            this.getLinkedTreeObjects = function() {
                return linkedTreeObjects
            };

            this.sortLinkedTreeObjects = function() {
                linkedTreeObjects.sort(maxLinkedTreeObjectsCount);
            };

            this.setSortOrdering = function(sort) {
                sortOrdering = sort;
            };

            this.pushLinkedTreeObject = function(object) {
                linkedTreeObjects.push(object);
            };

            this.pushBindedLink = function(link){
                mbus.pushBindedLink(link);
            };

            this.getDCName = function() {
                return dcName;
            };

            this.getAreaName = function() {
                return areaName;
            };

            this.getMulticastAddr = function() {
                return multicastAddr;
            };

            this.equal = function(multicastBus) {
                return (dcName        === multicastBus.getDCName() &&
                        areaName      === multicastBus.getAreaName() &&
                        multicastAddr === multicastBus.getMulticastAddr())
            };

            this.isInserted = function() {
                return isInserted;
            };

            this.setInserted = function() {
                isInserted = true;
            };

            this.unsetInserted = function() {
                isInserted = false;
            };

            this.setTopLeftCoord = function(x,y) {
                var centerX = x + long/ 2, centerY = y + diameter/2;
                mbus = new cylinder(centerX,centerY,diameter,long,multicastAddr);
            };

            this.definedNodesPoz = function() {
                ;
            };

            this.setCylinder = function(centerX,centerY) {
                mbus = new cylinder(centerX,centerY,diameter,long,multicastAddr);
            };

            this.setMoveJail = function(minX, minY, maxX, maxY){
                mbus.setMoveJail(minX,minY,maxX,maxY);
            }

            this.getBusSize = function() {
                return {
                    width:long,
                    height:diameter
                }
            };

            this.getBusCoords = function() {
                return mbus.getTopLeftCoords();
            }

            this.getMBus = function() {
                return mbus;
            };

            this.isMoving = function() {
                return mbus.isMoving();
            };

            this.dragger = function() {
                mbus.dragger();
            };

            this.uper = function() {
                mbus.uper();
            };

            this.mover = function(dx,dy) {
                mbus.mover(dx,dy);
            };

            this.print = function(r) {
                mbus.print(r);
            };
        }
        return multicastBus;
    });