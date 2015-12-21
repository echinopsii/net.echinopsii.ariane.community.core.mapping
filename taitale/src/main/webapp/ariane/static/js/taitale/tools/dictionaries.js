// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library                                                 │ \\
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
    function () {
        function dictionaries() {
            this.mapToolActivation = {
                ON: "ON",
                OFF: "OFF"
            };
            this.mapLayout = {
                MANUAL: "Random",
                BBTREE: "BBTree",
                MDW : "Middleware"
            };
            this.mapMode = {
                NAVIGATION: "Navigation",
                EDITION: "Edition"
            };
            this.networkType = {
                WAN: "WAN",
                MAN: "MAN",
                LAN: "LAN",
                DMZ: "DMZ",
                VPN: "VPN",
                VIRT: "VIRT",
                GLI: "GLOBAL INTERNET"
            };
        }
        return dictionaries;
    });