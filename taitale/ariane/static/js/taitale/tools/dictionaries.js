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
            this.mapLayout = {
                MANUAL: "Random",
                TREE  : "Tree",
                NTWWW : "Network"
            };

            this.mapMode = {
                NAVIGATION: "Navigation",
                EDITION: "Edition"
            }

            this.networkType = {
                WAN: "WAN",
                MAN: "MAN",
                LAN: "LAN"
            };

            this.company = {
                //TODO: this values should be retrieved from server
                //      (then we synchronize server and client runtimes)
                TIBCO : 'Tibco',
                IBM : 'IBM'
                //...
            }

            this.product = {
                //TODO: this values should be retrieved from server
                //      (then we synchronize server and client runtimes)
                TIBRV : 'Tibco Rendez Vous',
                TIBEMS : 'Tibco EMS'
            }

            this.containerType = {
                //TODO: this values should be retrieved from server
                //      (then we synchronize server and client runtimes)
                TIBRVRD  : 'RV Router Daemon',
                TIBRVD : 'RV Daemon',
                TIBEMS : 'EMS Server',
                TIBBW  : 'BUSINESS WORK Server',
                IBMWSMQ : 'WEBSPHERE MQ Server',
                ACTIVEMQ : 'ACTIVE MQ Server'
                //...
            };
        }
        return dictionaries;
    });