// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - mapSplitter                    │ \\
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
        function mapSplitter() {
            var wanLineHeight = 0,
                manLineHeight = 0,
                lanLineHeight = 0;

            this.setWanLineHeight = function(areaHeight) {
                if (areaHeight > wanLineHeight)
                    wanLineHeight = areaHeight;
            };

            this.getWanLineHeight = function() {
                return wanLineHeight;
            };

            this.setManLineHeight = function(areaHeight) {
                if (areaHeight > manLineHeight)
                    manLineHeight = areaHeight;
            };

            this.getManLineHeight = function() {
                return manLineHeight;
            };

            this.setLanLineHeight = function(areaHeight) {
                if (areaHeight > lanLineHeight)
                    lanLineHeight = areaHeight;
            };

            this.getLanLineHeight = function() {
                return lanLineHeight;
            };
        };

        return mapSplitter;
    });