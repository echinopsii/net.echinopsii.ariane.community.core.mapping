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

define(
    [
        'taitale-helper'
    ],
    function(helper) {

        function prototypes () {
            var helper_ = new helper();

            this.physicalLocation = {
                equal: function(that) {
                    return(this.pname===that.pname);
                },
                toString: function() {
                    return 'Datacenter:'   +
                        '\n\t Name : '     + this.pname +
                        '\n\t Address : '  + this.address +
                        '\n\t Town : '     + this.town +
                        '\n\t Country : '  + this.country +
                        '\n\t GPS lat : '  + this.gpsLat +
                        '\n\t GPS lng : '  + this.gpsLng;
                }
            };

            this.simpleRoutingArea = {
                equal: function(that) {
                    return (this.raname===that.raname)
                },
                toString: function() {
                    return 'RoutingArea:' +
                            '\n\t Name: ' + this.raname +
                            '\n\t Type: ' + this.ratype +
                            '\n\t Multicast filter: ' + this.ramulticast
                }
            };

            this.simpleSubnet = {
                equal: function(that) {
                    return (this.sname===that.sname)
                },
                toString: function() {
                    return 'Subnet:' +
                            '\n\t Name: ' + this.sname +
                            '\n\t SubnetIP: ' + this.sip +
                            '\n\t SubnetMask: ' + this.smask
                }
            };

            this.subnet = {
                equal: function(that) {
                    return (this.sname===that.sname)
                },
                toString: function() {
                    return 'Subnet:' +
                        '\n\t Name: ' + this.sname +
                        '\n\t SubnetIP: ' + this.sip +
                        '\n\t SubnetMask: ' + this.smask +
                        '\n\t IsDefault: ' + this.isdefault
                }
            };

            this.routingArea = {
                equal: function(that) {
                    ret = this.raname===that.raname;
                    if (ret)
                        ret = helper_.equalSortedArray(this.subnets, that.subnets);

                    return ret
                },
                toString: function() {
                    ret = 'RoutingArea:' +
                        '\n\t Name: ' + this.raname +
                        '\n\t Type: ' + this.ratype +
                        '\n\t Multicast filter: ' + this.ramulticast +
                        '\n\t Subnets :\n' + this.subnets.toString();
                    return ret
                }
            };

            var ret = null;
            this.multipleNetwork = {
                equal : function(that) {
                    ret = this.plocation.equal(that.plocation);
                    if (ret)
                        ret = helper_.equalSortedArray(this.rareas, that.rareas);
                    return ret
                },
                toString: function() {
                    return 'Multiple Network:'   +
                        '\n\t Physical location : \n\t' + this.plocation.toString() +
                        '\n\t Routing areas : \n\t' + this.rareas.toString()
                },
                getPLocation: function () {
                    return this.plocation;
                },
                getArea: function() {
                    var i, ii, j, jj;
                    var ret_rarea = null, ret_subnet = null;
                    for (i = 0, ii = this.rareas.length ; i < ii; i++) {
                        var rarea = this.rareas[i];
                        for (j = 0, jj = rarea.subnets.length; j < jj; j++) {
                            var subnet = rarea.subnets[j];
                            if (subnet.isdefault) {
                                ret_rarea = rarea;
                                ret_subnet = subnet;
                                break;
                            }
                        }
                    }
                    if (ret_rarea!=null) {
                        return {
                            pname: this.plocation.pname,
                            raname: ret_rarea.raname,
                            ratype : ret_rarea.ratype,
                            ramulticast: ret_rarea.ramulticast
                        };
                    } else return null;
                },
                equalArea: function(that) {
                    var i, ii, j, jj;
                    var ret_rarea = null, ret_subnet = null;
                    for (i = 0, ii = this.rareas.length ; i < ii; i++) {
                        var rarea = this.rareas[i];
                        for (j = 0, jj = rarea.subnets.length; j < jj; j++) {
                            var subnet = rarea.subnets[j];
                            if (subnet.isdefault) {
                                ret_rarea = rarea;
                                ret_subnet = subnet;
                                break;
                            }
                        }
                    }
                    if (ret_rarea!=null) {
                        ret =
                            (
                                this.plocation.equal(that.plocation) &&
                                ret_rarea.equal(that.getArea())
                            );
                    }
                    return ret;
                },
                getLan: function() {
                    var i, ii, j, jj;
                    var ret_rarea = null, ret_subnet = null;
                    for (i = 0, ii = this.rareas.length ; i < ii; i++) {
                        var rarea = this.rareas[i];
                        for (j = 0, jj = rarea.subnets.length; j < jj; j++) {
                            var subnet = rarea.subnets[j];
                            if (subnet.isdefault) {
                                ret_rarea = rarea;
                                ret_subnet = subnet;
                                break;
                            }
                        }
                    }
                    if (ret_rarea!=null && ret_subnet!=null) {
                        return {
                            pname       : this.plocation.pname,
                            ratype      : ret_rarea.ratype,
                            raname      : ret_rarea.raname,
                            ramulticast : ret_rarea.ramulticast,
                            sname       : ret_subnet.sname,
                            sip         : ret_subnet.sip,
                            smask       : ret_subnet.smask
                        };
                    } else return null;
                }
            };

            this.standaloneNetwork = {
                equal : function(that) {
                    ret =
                        (
                            this.plocation.equal(that.plocation) &&
                            this.rarea.equal(that.rarea) &&
                            this.subnet.equal(that.subnet)
                        );
                    return ret;
                },
                toString: function() {
                    return 'Standalone Network:'   +
                        '\n\t Physical location : \n\t' + this.plocation.toString() +
                        '\n\t Routing area : \n\t' + this.rarea.toString() +
                        '\n\t Subnet : \n\t' + this.subnet.toString()
                },
                getPLocation: function () {
                    return this.plocation;
                },
                getArea: function() {
                    return {
                        pname: this.plocation.pname,
                        raname: this.rarea.raname,
                        ratype : this.rarea.ratype,
                        ramulticast: this.rarea.ramulticast
                    };
                },
                equalArea: function(that) {
                    ret =
                        (
                            this.plocation.equal(that.plocation) &&
                            this.rarea.equal(that.rarea)
                        );
                    return ret;
                },
                getLan: function() {
                    return {
                        pname       : this.plocation.pname,
                        ratype      : this.rarea.ratype,
                        raname      : this.rarea.raname,
                        ramulticast : this.rarea.ramulticast,
                        sname       : this.subnet.sname,
                        sip         : this.subnet.sip,
                        smask       : this.subnet.smask
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