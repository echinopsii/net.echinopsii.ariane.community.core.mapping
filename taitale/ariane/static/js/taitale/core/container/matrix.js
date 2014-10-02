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

        var count            = 0,
            nbLines          = 0,
            nbColumns        = 0,
            rows             = [],
            contentWidth     = 0,
            contentHeight    = 0,
            contentMaxWidth  = 0,
            contentMaxHeight = 0;

        this.getMtxSize = function() {
            return {
                x: nbColumns,
                y: nbLines-1
            };
        };

        this.getMtxCount = function() {
            return count;
        };

        this.getNodeFromMtx = function (x,y) {
            return rows[y+1][x];
        };

        this.defineContainerContentMaxSize = function() {
            var block;
            var i, ii, j, jj;
            for (i = 1, ii = nbLines; i < ii ; i++) {
                for (j = 0, jj = nbColumns; j < jj; j++) {
                    block = rows[i][j];
                    block.defineMaxSize();
                    contentMaxWidth += block.getMaxRectSize().width;
                    contentMaxHeight += block.getMaxRectSize().height;
                }
            }
        };

        this.getContainerContentMaxSize = function() {
            return {
                width  : contentMaxWidth,
                height : contentMaxHeight
            }
        };

        this.defineContainerContentSize = function() {
            var tmpHeight, tmpWidth, block;
            var i, ii, j, jj;
            for (i = 1, ii = nbLines; i < ii ; i++) {
                tmpWidth = 0;
                for (j = 0, jj = nbColumns; j < jj; j++) {
                    block = rows[i][j];
                    block.defineSize();
                    tmpWidth = tmpWidth + block.getRectSize().width;
                }
                if (tmpWidth > contentWidth)
                    contentWidth = tmpWidth;
            }
            for (i = 0, ii = nbColumns; i < ii ; i++) {
                tmpHeight = 0;
                for (j = 1, jj = nbLines; j < jj; j++) {
                    block = rows[j][i];
                    tmpHeight = tmpHeight + block.getRectSize().height;
                }
                if (tmpHeight > contentHeight)
                    contentHeight=tmpHeight;
            }
        };

        this.getContainerContentSize = function() {
            return {
                width : contentWidth,
                height: contentHeight
            }
        };

        this.defineMtxNodePoz = function(topLeftX, topLeftY, interSpan) {
            var curContHeight = topLeftY;
            for (var j = 1, jj = nbLines; j < jj; j++) {
                var curContWidth  = topLeftX, maxContHeight=0;
                for (var i = 0, ii = nbColumns; i < ii; i++) {
                    var node = rows[j][i];
                        node.setPoz(interSpan*(i+1) + curContWidth, interSpan*j + curContHeight);
                        //node.definedNodesPoz();
                        curContWidth = curContWidth + node.getMaxRectSize().width;
                        if (node.getMaxRectSize().height>maxContHeight)
                            maxContHeight = node.getMaxRectSize().height;
                }
                curContHeight = curContHeight + maxContHeight;
            }
        };

        this.addNode = function(node) {
            if (nbLines!=0) {
                var linkedNX = 0,
                    linkedNY = 0;
                for (var j = 1, jj = nbLines; j < jj; j++) {
                    for (var i = 0, ii = nbColumns; i < ii; i++) {
                        if (node.isLinkedToNode(rows[j][i])) {
                            linkedNX = i;
                            linkedNY = j;
                        }
                    }
                }
                if (linkedNX == 0 && linkedNY == 0) {
                    rows[nbLines]    = [];
                    rows[nbLines][0] = node;
                    rows[0][nbLines] = 1;
                    nbLines++;
                } else {
                    rows[linkedNY][rows[0][linkedNY-1]] = node;
                    rows[0][linkedNY-1]++;
                    if (nbColumns<rows[0][linkedNY-1])
                        nbColumns++;
                }
            } else {
                // init columns count per lines
                rows[0]    = [];
                rows[0][nbLines] = 1;
                nbLines ++ ;
                // push node in matrix
                rows[nbLines]    = [];
                rows[nbLines][nbColumns] = node;
                nbLines ++ ;
                nbColumns ++ ;
            }
            count++;
        };

        this.toFront = function() {
            for (var i = 1, ii = nbLines; i < ii; i++) {
                for (var j = 0, jj = nbColumns; j < jj; j++) {
                    rows[i][j].toFront();
                }
            }
        }
    }

    return containerMatrix;
});