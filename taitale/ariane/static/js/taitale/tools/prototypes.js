// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TOOLS - prototypes                            │ \\
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
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			 			  │ \\
// │ GNU Affero General Public License for more details.								  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.		  		  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

define( function() {

    function prototypes () {

        this.dcprototype = {
            equal: function(that) {
                return(this.dc===that.dc);
            },
            toString: function() {
                return 'Datacenter:'   +
                    '\n\t Name : '     + this.dc +
                    '\n\t Address : '  + this.address +
                    '\n\t Town : '     + this.town +
                    '\n\t Country : '  + this.country +
                    '\n\t GPS lat : '  + this.gpsLat +
                    '\n\t GPS lng : '  + this.gpsLng;
            }
        };

        this.ntprototype = {
            equal : function(that) {
                var ret =
                    (
                        this.dcproto.equal(that.dcproto) &&
                        this.type===that.type &&
                        this.marea===that.marea &&
                        this.lan===that.lan &&
                        this.subnetip===that.subnetip &&
                        this.subnetmask===that.subnetmask
                    );
                return ret;
            },
            toString: function() {
                return 'Network location:'   +
                    '\n\t Datacenter : \n\t' + this.dcproto.toString() +
                    '\n\t Type : '           + this.type +
                    '\n\t Multicast Area: '  + this.marea +
                    '\n\t LAN : '            + this.lan +
                    '\n\t Subnet IP: '       + this.subnetip +
                    '\n\t Subnet Mask: '     + this.subnetmask;
            },
            getDatacenter: function () {
                return this.dcproto;
            },
            getArea: function() {
                return {
                    dc   : this.dcproto.dc,
                    type : this.type,
                    marea: this.marea
                };
            },
            equalArea: function(that) {
                var ret =
                    (
                        this.dcproto.equal(that.dcproto) &&
                        this.type === that.type &&
                        this.marea === that.marea
                    )
                return ret;
            },
            getLan: function() {
                return {
                    dc         : this.dcproto.dc,
                    type       : this.type,
                    area       : this.marea,
                    lan        : this.lan,
                    subnetip   : this.subnetip,
                    subnetmask : this.subnetmask
                };
            }
        };

        this.create = function(prototype, object) {
            var newObject = Object.create(prototype);
            for (var prop in object) {
                if (object.hasOwnProperty(prop)) {
                    newObject[prop] = object[prop];
                }
            }
            return newObject;
        };
    }

    return prototypes;
});