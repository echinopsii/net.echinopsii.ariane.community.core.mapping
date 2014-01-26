// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - DC Matrix                      │ \\
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
    [
        'taitale-dictionaries',
        'taitale-area'
    ],
    function (dictionaries, area) {
        function datacenterMatrix(splitter_, registries_, options_) {
            var nbLines       = 0,
                nbColumns     = 0,
                rows          = [],
                contentWidth  = 0,
                contentHeight = 0,
                dic           = new dictionaries(),
                splitter      = splitter_,
                registries    = registries_,
                options       = options_;

            this.printMtx = function(r) {
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    rows[0][i].print(r);
                }
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    rows[1][i].print(r);
                }
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    rows[2][i].print(r);
                }
            };

            this.displayArea = function(display) {
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    rows[0][i].displayArea(display);
                }
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    rows[1][i].displayArea(display);
                }
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    rows[2][i].displayArea(display);
                }
            };

            this.displayLan = function(display) {
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    rows[0][i].displayLan(display);
                }
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    rows[1][i].displayLan(display);
                }
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    rows[2][i].displayLan(display);
                }
            };

            this.getMtxSize = function() {
                return {
                    x: nbColumns,
                    y: nbLines
                };
            };

            this.getAreaFromMtx = function (x,y) {
                return rows[y][x];
            };

            this.getWanMtxSize = function() {
                return rows[3][0];
            }

            this.getManMtxSize = function() {
                return rows[3][1];
            }

            this.getLanMtxSize = function() {
                return rows[3][2];
            }

            this.getAreaFromWanMtx = function(x) {
                return rows[0][x];
            }

            this.getAreaFromManMtx = function(x) {
                return rows[1][x];
            }

            this.getAreaFromLanMtx = function(x) {
                return rows[2][x];
            }

            this.getYOffset = function() {
                var yoffset = 0;

                if (rows[0].length == 0) {
                    yoffset += splitter.getWanLineHeight();
                    if (rows[1].length == 0)
                        yoffset += splitter.getManLineHeight();
                }

                return yoffset;
            };

            this.defineMtxAreaSize = function() {
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    rows[0][i].defineSize();
                    splitter.setWanLineHeight(rows[0][i].getAreaSize().height);
                }
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    rows[1][i].defineSize();
                    splitter.setManLineHeight(rows[1][i].getAreaSize().height);
                }
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    rows[2][i].defineSize();
                    splitter.setLanLineHeight(rows[2][i].getAreaSize().height);
                }
            };

            this.defineDCContentSize = function() {
                var tmpWidth = 0;
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    tmpWidth = tmpWidth + rows[0][i].getAreaSize().width;
                }
                if (tmpWidth > contentWidth)
                    contentWidth = tmpWidth;

                tmpWidth=0;
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    tmpWidth = tmpWidth + rows[1][i].getAreaSize().width;
                }
                if (tmpWidth > contentWidth)
                    contentWidth = tmpWidth;

                tmpWidth=0;
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    tmpWidth = tmpWidth + rows[2][i].getAreaSize().width;
                }
                if (tmpWidth > contentWidth)
                    contentWidth = tmpWidth;

                var tmpHeight = 0;
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    var rowHeight = rows[0][i].getAreaSize().height;
                    if (rowHeight > tmpHeight)
                        tmpHeight = rowHeight;
                }
                contentHeight += tmpHeight;

                var tmpHeight = 0;
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    var rowHeight = rows[1][i].getAreaSize().height;
                    if (rowHeight > tmpHeight)
                        tmpHeight = rowHeight;
                }
                contentHeight += ((tmpHeight!=0)? tmpHeight : splitter.getManLineHeight());

                var tmpHeight = 0;
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    var rowHeight = rows[2][i].getAreaSize().height;
                    if (rowHeight > tmpHeight)
                        tmpHeight = rowHeight;
                }
                contentHeight = contentHeight + tmpHeight;

            };

            this.getDCContentSize = function() {
                return {
                    width : contentWidth,
                    height: contentHeight
                };
            };

            this.defineMtxAreaPoz = function(dcTopLeftX,dcTopLeftY,dcWidth,dbrdSpan,areaSpan) {
                var cursorHeight = dcTopLeftY,
                    cursorWidth  = dcTopLeftX,
                    tmpAHeight    = 0;

                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    var aHeight = rows[0][i].getAreaSize().height,
                        aWidth  = rows[0][i].getAreaSize().width;
                    if (ii == 1)
                        rows[0][i].setTopLeftCoord(cursorWidth + (dcWidth/2 - aWidth/2), dbrdSpan + cursorHeight);
                    else {
                        rows[0][i].setTopLeftCoord(dbrdSpan + areaSpan*i + cursorWidth , dbrdSpan + cursorHeight);
                        cursorWidth = cursorWidth + aWidth;
                    }
                    rows[0][i].definePoz();
                    if (tmpAHeight < aHeight)
                        tmpAHeight = aHeight;
                }
                cursorHeight += tmpAHeight + areaSpan;
                var areaJailXMin = dcTopLeftX+dbrdSpan;
                var areaJailYMin = dcTopLeftY+dbrdSpan;
                var areaJailXMax = dcTopLeftX+dcWidth-dbrdSpan;
                var areaJailYMax = dcTopLeftY+dbrdSpan+splitter.getWanLineHeight();
                for (var i = 0, ii = rows[3][0]; i < ii ; i++ ) {
                    rows[0][i].setMoveJail(areaJailXMin,areaJailYMin,areaJailXMax,areaJailYMax);
                }

                cursorWidth  = dcTopLeftX;
                tmpAHeight    = 0;
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    var aHeight = rows[1][i].getAreaSize().height,
                        aWidth  = rows[1][i].getAreaSize().width;
                    if (ii == 1)
                        rows[1][i].setTopLeftCoord(cursorWidth + (dcWidth/2 - aWidth/2), dbrdSpan + cursorHeight);
                    else {
                        rows[1][i].setTopLeftCoord(dbrdSpan + areaSpan*i + cursorWidth , dbrdSpan + cursorHeight);
                        cursorWidth = cursorWidth + aWidth;
                    }
                    rows[1][i].definePoz();
                    if (tmpAHeight < aHeight)
                        tmpAHeight = aHeight;
                }
                areaJailYMin = cursorHeight+dbrdSpan;
                areaJailYMax = cursorHeight+dbrdSpan+splitter.getManLineHeight();
                cursorHeight += ((tmpAHeight!=0)? tmpAHeight : splitter.getManLineHeight())+ areaSpan;
                for (var i = 0, ii = rows[3][1]; i < ii ; i++ ) {
                    rows[1][i].setMoveJail(areaJailXMin,areaJailYMin,areaJailXMax,areaJailYMax);
                }

                cursorWidth  = dcTopLeftX;
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    var aWidth  = rows[2][i].getAreaSize().width;
                    if (ii == 1)
                        rows[2][i].setTopLeftCoord(cursorWidth + (dcWidth/2 - aWidth/2), dbrdSpan + cursorHeight);
                    else {
                        rows[2][i].setTopLeftCoord(dbrdSpan + areaSpan*i + cursorWidth , dbrdSpan + cursorHeight);
                        cursorWidth = cursorWidth + aWidth;
                    }
                    rows[2][i].definePoz();
                }
                areaJailYMin = cursorHeight+dbrdSpan;
                areaJailYMax = cursorHeight+dbrdSpan+splitter.getLanLineHeight();
                for (var i = 0, ii = rows[3][2]; i < ii ; i++ ) {
                    rows[2][i].setMoveJail(areaJailXMin,areaJailYMin,areaJailXMax,areaJailYMax);
                }
            };

            this.addContainerArea = function(container) {
                if (nbLines==0) {
                    rows[0] = [];      // WAN Area
                    rows[1] = [];      // MAN Area
                    rows[2] = [];      // LAN or Multicast Area
                    rows[3] = [0,0,0]; // columns counters
                    nbLines = 3;
                }

                var curarea         = container.getLayoutData().area,
                    alreadyInserted = curarea.isInserted(),
                    networkType     = curarea.getAreaDef().type;

                var wanColCount     = rows[3][0];
                var manColCount     = rows[3][1];
                var lanColCount     = rows[3][2];

                // if not create area and insert in the mtx
                if (!alreadyInserted) {
                    switch(networkType) {
                        case dic.networkType.WAN:
                            rows[0][wanColCount] = curarea;
                            curarea.setInserted();
                            rows[3][0]           = ++wanColCount;
                            if (nbColumns < rows[3][0])
                                nbColumns = rows[3][0];
                            break;
                        case dic.networkType.MAN:
                            rows[1][manColCount] = curarea;
                            curarea.setInserted();
                            rows[3][1]           = ++manColCount;
                            if (nbColumns < rows[3][1])
                                nbColumns = rows[3][1];
                            break;
                        case dic.networkType.LAN:
                            rows[2][lanColCount] = curarea;
                            curarea.setInserted();
                            rows[3][2]           = ++lanColCount;
                            if (nbColumns < rows[3][2])
                                nbColumns = rows[3][2];
                            break;
                        default:
                            // SHOULD NEVER APPEND...
                            // TODO MNG ERROR
                            break;
                    }
                }

                // finally push the lan
                curarea.pushContainerLan(container);
            };
        };

        return datacenterMatrix;
    });