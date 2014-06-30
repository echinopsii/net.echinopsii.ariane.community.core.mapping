// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - registries                     │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013 Mathilde Ffrench												  │ \\
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
        'taitale-datacenter',
        'taitale-area',
        'taitale-lan'
    ],
    function (datacenter,area, lan) {
        function registries() {
            var dcRegistry   = [],
                areaRegistry = [],
                lanRegistry  = [];

            var getDatacenterFromRegistry = function(id) {
                for (i = 0, ii = dcRegistry.length ; i < ii ; i++) {
                    if (dcRegistry[i].geoDCLocEqual(id))
                        return dcRegistry[i];
                }
            }

            this.getDatacenterFromRegistry = function(id) {
                 return getDatacenterFromRegistry(id);
            };

            var getAreaFromRegistry = function(id) {
                for (i = 0, ii = areaRegistry.length; i < ii; i++) {
                    if (areaRegistry[i].defEqual(id))
                        return areaRegistry[i];
                }
            }

            this.getAreaFromRegistry = function(id) {
                return getAreaFromRegistry(id);
            };


            var getLanFromRegistry = function(id) {
                for (i = 0, ii = lanRegistry.length; i < ii; i++) {
                    if (lanRegistry[i].defEqual(id))
                        return lanRegistry[i];
                }
            }

            this.getLanFromRegistry = function(id) {
                return getLanFromRegistry(id);
            };

            this.pushDatacenterIntoRegistry = function(datacenterDef, ldatacenterSplitter,options) {
                var ret = getDatacenterFromRegistry(datacenterDef);
                if (ret==null) {
                    ret = new datacenter(datacenterDef, ldatacenterSplitter, this, options);
                    dcRegistry.push(ret);
                }
                return ret;
            };

            this.pushAreaIntoRegistry = function(areaDef, options) {
                var ret = getAreaFromRegistry(areaDef);
                if (ret==null) {
                    ret = new area(areaDef, this, options);
                    areaRegistry.push(ret);
                }
                return ret;
            };

            this.pushLanIntoRegistry = function(lanDef, options) {
                var ret = getLanFromRegistry(lanDef);
                if (ret==null) {
                    ret = new lan(lanDef,options);
                    lanRegistry.push(ret);
                }
                return ret;
            };
        };

        return registries;
    });