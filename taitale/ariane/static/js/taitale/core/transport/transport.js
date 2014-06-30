// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Transport                       │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench                       						  │ \\
// │                                        											  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.	                				  │ \\
// │		                				                        					  │ \\
// │ This program is distributed in the hope that it will be useful,        			  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of	            		  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the	            		  │ \\
// │ GNU Affero General Public License for more details.	                			  │ \\
// │	                                        										  │ \\
// │ You should have received a copy of the GNU Affero General Public License   		  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.	        	  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

define(
    [
        'taitale-transport-multicastbus',
        'taitale-helper'
    ],
    function(multicastbus, helper){

        function transport(JSONTransportDesc) {
            var id            = JSONTransportDesc.transportID,
                name          = JSONTransportDesc.transportName,
                properties    = JSONTransportDesc.transportProperties,
                isMoulticast  = name.contains("multicast"),
                multicastAddr = (isMoulticast ? name.split("://")[1] : "");//,
                //helper_       = new helper();

            var multicastBusRegistry = [];

            this.getID = function() {
                return id ;
            };

            this.getName = function() {
                return name;
            };

            this.isMulticast = function() {
                return isMoulticast;
            };

            this.defineMulticastBus = function(localisation) {
                if (localisation != null && isMoulticast ) {
                    var multicastBus=null;
                    for (var i = 0, ii = multicastBusRegistry.length; i < ii; i++) {
                        if (multicastBusRegistry[i].dcName === localisation.dcproto.dc &&
                            multicastBusRegistry[i].areaName === localisation.marea) {
                            multicastBus =  multicastBusRegistry[i];
                            break;
                        }
                    }
                    if (multicastBus==null) {
                        multicastBus = new multicastbus(id, multicastBusRegistry.length, localisation, multicastAddr, properties);
                        multicastBusRegistry.push(multicastBus);
                    }
                    return multicastBus;
                }
            };

            this.getMulticastBus = function(localisation) {
                var multicastBus = null;
                if (localisation != null && isMoulticast ) {
                    for (var i = 0, ii = multicastBusRegistry.length; i < ii; i++) {
                        if (multicastBusRegistry[i].dcName === localisation.dcproto.dc &&
                            multicastBusRegistry[i].areaName === localisation.marea) {
                            multicastBus =  multicastBusRegistry[i];
                            break;
                        }
                    }
                }
                return multicastBus;
            };

            this.getMulticastBusRegistry = function() {
                return multicastBusRegistry;
            };

            this.sortLinkedTreeObjects = function() {
                if (isMoulticast) {
                    for (var i = 0, ii = multicastBusRegistry.length; i < ii; i++) {
                        multicastBusRegistry[i].sortLinkedTreeObjects();
                    }
                }
            };
        }

        return transport;
    }
);