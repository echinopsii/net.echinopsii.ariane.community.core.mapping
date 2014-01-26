// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - Lan Matrix                     │ \\
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
        function lanMatrix() {
            var nbLines       = 0,
                nbColumns     = 0,
                rows          = [],
                contentWidth  = 0,
                contentHeight = 0;

            this.getMtxSize = function() {
                return {
                    x: nbColumns,
                    y: nbLines
                };
            };

            this.defineMtxContainerPoz = function(topLeftX, topLeftY, lbrdSpan, contSpan, lanwidth, lanheight) {
                var curContWidth  = topLeftX;
                for (var i = 0, ii = nbColumns; i < ii; i++) {
                    var curContHeight = topLeftY, maxContWidth=0;
                    for (var j = 0, jj = nbLines; j < jj; j++) {
                        rows[j][i].setTopLeftCoord(lbrdSpan + contSpan*i + curContWidth , lbrdSpan + contSpan*j + curContHeight);
                        rows[j][i].setMoveJail(topLeftX, topLeftY+lbrdSpan, topLeftX+lanwidth, topLeftY+lanheight);
                        rows[j][i].definedNodesPoz();
                        curContHeight = curContHeight + rows[j][i].getMaxRectSize().height;
                        if (rows[j][i].getMaxRectSize().width>maxContWidth)
                            maxContWidth = rows[j][i].getMaxRectSize().width;
                    }
                    curContWidth = curContWidth + maxContWidth;
                };
            };

            this.defineLanContentSize = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    var tmpHeight = 0;
                    for (var j = 0, jj = nbLines; j < jj; j++) {
                        tmpHeight = tmpHeight + rows[j][i].getMaxRectSize().height;
                    }
                    if (tmpHeight > contentHeight)
                        contentHeight=tmpHeight;
                }
                for (var i = 0, ii = nbLines; i < ii ; i++) {
                    var tmpWidth = 0;
                    for (var j = 0, jj = nbColumns; j < jj; j++) {
                        tmpWidth = tmpWidth + rows[i][j].getMaxRectSize().width;
                    }
                    if (tmpWidth > contentWidth)
                        contentWidth = tmpWidth;
                }
            };

            this.getLanContentSize = function () {
                return {
                    width  : contentWidth,
                    height : contentHeight
                };
            };

            this.getContainerFromMtx = function (x,y) {
                return rows[y][x];
            };

            this.addContainer = function(container) {
                if (nbLines==0) {
                    rows[nbLines]            = [];
                    nbLines++;
                }

                rows[0][nbColumns] = container;
                nbColumns ++ ;
            };
        };

        return lanMatrix;
    });