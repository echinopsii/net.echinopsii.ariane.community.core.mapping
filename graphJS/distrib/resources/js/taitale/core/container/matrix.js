// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Container Matrix                │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench						  						  │ \\
// │ 																					  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of			  			  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			 			  │ \\
// │ GNU Affero General Public License for more details.				  				  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.		  		  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

define(function() {
    function containerMatrix() {

        var count     = 0,
            nbLines   = 0,
            nbColumns = 0,
            rows      = [];

        this.getMtxSize = function() {
            return {
                x: nbColumns,
                y: nbLines
            };
        };

        this.getMtxCount = function() {
            return count;
        }

        this.getNodeFromMtx = function (x,y) {
            return rows[y][x];
        };

        this.addNode = function(node) {
            if (nbLines!=0) {
                var linkedNX = -1,
                    linkedNY = -1;
                for (var j = 0, jj = nbLines; j < jj; j++) {
                    for (var i = 0, ii = nbColumns; i < ii; i++) {
                        if (node.isLinkedToNode(rows[j][i])) {
                            linkedNX = i;
                            linkedNY = j;
                        }
                    }
                }
                if (linkedNX == -1 && linkedNY == -1) {
                    //logOnFirbugConsole("Push Node " + node.getName() + " on "+container.getName()+"(0,"+nbLines+")");
                    rows[nbLines]    = [];
                    rows[nbLines][0] = node;
                    nbLines++;
                } else {
                    //TODO
                }
            } else {
                //logOnFirbugConsole("Push Node " + node.getName() + " on "+container.getName()+"(0,0)");
                rows[0]    = [];
                rows[0][0] = node;
                nbLines ++ ;
                nbColumns ++ ;
            }
            count++;
        };
    };

    return containerMatrix;
});