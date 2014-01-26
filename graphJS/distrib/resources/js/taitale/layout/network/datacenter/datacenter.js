// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - DC                             │ \\
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
        'taitale-params',
        'taitale-dictionaries',
        'taitale-datacenter-matrix',
        'taitale-datacenter-splitter',
        'taitale-helper'
    ],
    function (params,dictionary,datacenterMatrix,datacenterSplitter,helper) {
        function datacenter(geoDCLoc_, mapSplitter, registries, options) {
            var r          = null,
                helper_    = new helper(),
                topLeftX   = 0,
                topLeftY   = 0,
                dcwidth    = 0,
                dcheight   = 0,
                areaSpan   = params.dc_areaSpan,
                dbrdSpan   = params.dc_dbrdSpan,
                dbrdResz   = params.dc_dbrdResz,
                geoDCLoc   = geoDCLoc_,
                dic        = new dictionary(),
                msplitter  = mapSplitter,
                isInserted = false,
                displayDC  = false;

            var dcsplitter = null,
                dcmatrix   = new datacenterMatrix(msplitter, registries, options);

            var mindcW  = 0,
                mindcH  = 0,
                mindcWX = 0,
                mindcTY = 0,
                mindcEX = 0,
                mindcSY = 0;

            var dcR    = null,
                dcName = null,
                dcTown = null,
                rect   = null;

            var isMoving   = false,
                isResizing = false;

            var oUnselected = params.dc_opacUnselec,
                oSelected   = params.dc_opacSelec,
                sDasharray  = params.dc_strokeDasharray,
                sWidth      = params.dc_strokeWidthShow,
                color       = params.dc_color;

            var reDefineRectPoints = function(x, y) {
                    topLeftX = x;
                    topLeftY = y;
                    helper_.debug("[datacenter.reDefineRectPoints] { topLeftX: ".concat(topLeftX).concat(", topLeftY: ").concat(topLeftY).concat(" }"));
                },
                reDefineSize = function(width, height) {
                    dcwidth  = width;
                    dcheight = height;
                    helper_.debug("[datacenter.reDefineSize] { dcwidth: ".concat(dcwidth).concat(", dcheight: ").concat(dcheight).concat(" }"));
                },
                reDefineResizingJailRectPoints = function(xe, xw, yn, ys) {
                    if (xw!=null) mindcWX = xw-dbrdSpan;
                    if (xe!=null) mindcEX = xe+dbrdSpan;
                    if (yn!=null) mindcTY = yn-dbrdSpan;
                    if (ys!=null) mindcSY = yn+dbrdSpan;
                    helper_.debug("[datacenter.reDefineResizingJailRectPoints] { mindcWX: ".concat(mindcWX).concat(", mindcEX: ").concat(mindcEX).concat(", mindcTY: ").concat(mindcTY).concat(", mindcSY: ").concat(mindcSY).concat(" }"));
                };

            var dDragg = function () {
                    helper_.debug("[datacenter.dDragg] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving: ").concat(isMoving).concat(", isResizing: ").concat(isResizing).concat(" }"));
                    if (rect.attr('cursor')==='default') {
                        var mtxS        = dcmatrix.getWanMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromWanMtx(i).dragger();
                        }
                        mtxS = dcmatrix.getManMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromManMtx(i).dragger();
                        }
                        mtxS = dcmatrix.getLanMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromLanMtx(i).dragger();
                        }
                    } else {
                        isResizing = true;

                        var mtxS  = dcmatrix.getWanMtxSize();
                        var maxWX = null,
                            minEX = null,
                            maxNY = null,
                            minSY = null;

                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            if (maxWX == null) {
                                maxWX = dcmatrix.getAreaFromWanMtx(i).getAreaCoords().x;
                                minEX = maxWX + dcmatrix.getAreaFromWanMtx(i).getAreaSize().width;
                                maxNY = dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y;
                                minSY = maxNY+ dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;
                            } else {
                                var areaWX = dcmatrix.getAreaFromWanMtx(i).getAreaCoords().x,
                                    areaEX = areaWX + dcmatrix.getAreaFromWanMtx(i).getAreaSize().width,
                                    areaNY = dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y,
                                    areaSY = areaNY + dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;

                                if (areaWX < maxWX)
                                    maxWX = areaWX;
                                if (areaEX > minEX)
                                    minEX = areaEX;
                                if (areaNY < maxNY)
                                    maxNY = areaNY;
                                if (areaSY > minSY)
                                    minSY = areaSY;
                            }
                        }

                        mtxS = dcmatrix.getManMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            if (maxWX == null) {
                                maxWX = dcmatrix.getAreaFromManMtx(i).getAreaCoords().x;
                                minEX = maxWX + dcmatrix.getAreaFromManMtx(i).getAreaSize().width;
                                maxNY = dcmatrix.getAreaFromManMtx(i).getAreaCoords().y;
                                minSY = maxNY+ dcmatrix.getAreaFromManMtx(i).getAreaSize().height;
                            } else {
                                var areaWX = dcmatrix.getAreaFromManMtx(i).getAreaCoords().x,
                                    areaEX = areaWX + dcmatrix.getAreaFromManMtx(i).getAreaSize().width
                                    areaNY = dcmatrix.getAreaFromManMtx(i).getAreaCoords().y,
                                    areaSY = areaNY + dcmatrix.getAreaFromManMtx(i).getAreaSize().height;

                                if (areaWX < maxWX)
                                    maxWX = areaWX;
                                if (areaEX > minEX)
                                    minEX = areaEX;
                                if (areaNY < maxNY)
                                    maxNY = areaNY;
                                if (areaSY > minSY)
                                    minSY = areaSY;
                            }
                        }

                        mtxS = dcmatrix.getLanMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            if (maxWX == null) {
                                maxWX = dcmatrix.getAreaFromLanMtx(i).getAreaCoords().x;
                                minEX = maxWX + dcmatrix.getAreaFromLanMtx(i).getAreaSize().width;
                                maxNY = dcmatrix.getAreaFromLanMtx(i).getAreaCoords().y;
                                minSY = maxNY + dcmatrix.getAreaFromLanMtx(i).getAreaSize().height;
                            } else {
                                var areaWX = dcmatrix.getAreaFromLanMtx(i).getAreaCoords().x,
                                    areaEX = areaWX + dcmatrix.getAreaFromLanMtx(i).getAreaSize().width,
                                    areaNY = dcmatrix.getAreaFromLanMtx(i).getAreaCoords().y,
                                    areaSY = areaNY + dcmatrix.getAreaFromLanMtx(i).getAreaSize().height;

                                if (areaWX < maxWX)
                                    maxWX = areaWX;
                                if (areaEX > minEX)
                                    minEX = areaEX;
                                if (areaNY < maxNY)
                                    maxNY = areaNY;
                                if (areaSY > minSY)
                                    minSY = areaSY;
                            }
                        }

                        reDefineResizingJailRectPoints(minEX, maxWX, maxNY, minSY);
                    }
                    rect.animate({"fill-opacity": oSelected}, 500);
                    isMoving = true;
                },
                dMove = function (rx, ry, rw, rh, t0x, t0y, t1x, t1y, dx, dy) {
                    helper_.debug("[datacenter.dMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    helper_.debug("[datacenter.dMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    switch (rect.attr('cursor')) {
                        case 'n-resize' :
                            var RY = ry + dy,
                                TY = t0y + dy,
                                RH = rh - dy;
                            if (RH>mindcH && RY<mindcTY) {
                                rect.attr({
                                    y: RY,
                                    height: RH
                                });
                                dcR[0].attr({
                                    y:TY
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                if (dcmatrix.getWanMtxSize()!=0) {
                                    dcsplitter.setWanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setWanLineHeight(dcsplitter.getManLineTopY()-dcsplitter.getWanLineTopY());
                                } else if (dcmatrix.getManMtxSize()!=0) {
                                    dcsplitter.setManLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setManLineHeight(dcsplitter.getLanLineTopY()-dcsplitter.getManLineTopY());
                                } else {
                                    dcsplitter.setLanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                                }
                            }
                            break;
                        case 'nw-resize' :
                            var RX = rx + dx,
                                RY = ry + dy,
                                RW = rw - dx,
                                RH = rh - dy,
                                TY = t0y + dy,
                                TX = RX + RW/2,
                                T1Y = t1y + dy;
                            if (RH>mindcH && RY<mindcTY && RX<mindcWX) {
                                rect.attr({
                                    x: RX,
                                    y: RY,
                                    width: RW,
                                    height: RH
                                });
                                dcR[0].attr({
                                    x:TX,
                                    y:TY
                                });
                                dcR[1].attr({
                                    x:TX,
                                    y:T1Y
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                if (dcmatrix.getWanMtxSize()!=0) {
                                    dcsplitter.setWanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setWanLineHeight(dcsplitter.getManLineTopY()-dcsplitter.getWanLineTopY());
                                } else if (dcmatrix.getManMtxSize()!=0) {
                                    dcsplitter.setManLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setManLineHeight(dcsplitter.getLanLineTopY()-dcsplitter.getManLineTopY());
                                } else {
                                    dcsplitter.setLanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                                }
                            }
                            break;
                        case 'ne-resize' :
                            var RY  = ry + dy,
                                RW  = rw + dx,
                                RH  = rh - dy,
                                RXW = topLeftX + RW,
                                TY  = t0y + dy,
                                TX  = rx + RW/2,
                                T1Y = t1y + dy;
                            if (RH>mindcH && RY<mindcTY && RXW>/*(mindcTWX+mindcW)*/mindcEX) {
                                rect.attr({
                                    y: RY,
                                    width: RW,
                                    height: RH
                                });
                                dcR[0].attr({
                                    x:TX,
                                    y:TY
                                });
                                dcR[1].attr({
                                    x:TX,
                                    y:T1Y
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                if (dcmatrix.getWanMtxSize()!=0) {
                                    dcsplitter.setWanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setWanLineHeight(dcsplitter.getManLineTopY()-dcsplitter.getWanLineTopY());
                                } else if (dcmatrix.getManMtxSize()!=0) {
                                    dcsplitter.setManLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setManLineHeight(dcsplitter.getLanLineTopY()-dcsplitter.getManLineTopY());
                                } else {
                                    dcsplitter.setLanLineTopY(topLeftY+dbrdSpan);
                                    dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                                }
                            }
                            break;
                        case 'w-resize' :
                            var RX = rx + dx,
                                RW = rw - dx,
                                TX = RX + RW/2;
                            if (RX<mindcWX) {
                                rect.attr({
                                    x: RX,
                                    width: RW
                                });
                                dcR[0].attr({
                                    x:TX
                                });
                                dcR[1].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 'e-resize' :
                            var RW   = rw + dx,
                                RXW  = topLeftX + RW,
                                TX   = rx + RW/2;
                            if (RXW>mindcEX) {
                                rect.attr({
                                    width: RW
                                });
                                dcR[0].attr({
                                    x:TX
                                });
                                dcR[1].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 's-resize' :
                            var RH  = rh + dy,
                                RYH = topLeftY + RH,
                                T1Y = t1y + dy;
                            if (RH>mindcH && RYH>(mindcTY+mindcH)) {
                                rect.attr({
                                    height: RH
                                });
                                dcR[1].attr({
                                    y:T1Y
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                dcsplitter.setLanLineBdrY(topLeftY+dcheight-dbrdSpan);
                                dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                            }
                            break;
                        case 'se-resize' :
                            var RW  = rw + dx,
                                RH  = rh + dy,
                                RXW = topLeftX + RW,
                                RYW = topLeftY + RH,
                                TX  = rx + RW/2,
                                T1Y = t1y + dy;
                            if (RH>mindcH && RXW>mindcEX && RYW>(mindcTY+mindcH)) {
                                rect.attr({
                                    width: RW,
                                    height: RH
                                });
                                dcR[0].attr({
                                    x:TX
                                });
                                dcR[1].attr({
                                    x:TX,
                                    y:T1Y
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                dcsplitter.setLanLineBdrY(topLeftY+dcheight-dbrdSpan);
                                dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                            }
                            break;
                        case 'sw-resize':
                            var RX = rx + dx,
                                RW = rw - dx,
                                RH = rh + dy,
                                RYW = topLeftY + RH,
                                TX = RX + RW/2,
                                T1Y = t1y + dy;
                            if (RH>mindcH && RX<mindcWX && RYW>(mindcTY+mindcH)) {
                                rect.attr({
                                    x: RX,
                                    width: RW,
                                    height: RH
                                });
                                dcR[0].attr({
                                    x:TX
                                });
                                dcR[1].attr({
                                    x:TX,
                                    y:T1Y
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                                dcsplitter.setLanLineBdrY(topLeftY+dcheight-dbrdSpan);
                                dcsplitter.setLanLineHeight(dcsplitter.getLanLineBdrY()-dcsplitter.getLanLineTopY());
                            }
                            break;
                        case 'default' :
                        default :
                            var attrect  = {x: rx + dx, y: ry + dy},
                                attrtxt0 = {x: t0x + dx, y: t0y + dy},
                                attrtxt1 = {x: t1x + dx, y: t1y + dy};

                            rect.attr(attrect);
                            dcR[0].attr(attrtxt0);
                            dcR[1].attr(attrtxt1);
                            reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                            if (dcmatrix.getWanMtxSize()!=0) {
                                dcsplitter.setWanLineTopY(topLeftY+dbrdSpan);
                                dcsplitter.setManLineTopY(dcsplitter.getWanLineTopY()+dcsplitter.getWanLineHeight());
                                dcsplitter.setLanLineTopY(dcsplitter.getManLineTopY()+dcsplitter.getManLineHeight());
                            } else if (dcmatrix.getManMtxSize()!=0) {
                                dcsplitter.setManLineTopY(topLeftY+dbrdSpan);
                                dcsplitter.setLanLineTopY(dcsplitter.getManLineTopY()+dcsplitter.getManLineHeight());
                            } else
                                dcsplitter.setLanLineTopY(topLeftY+dbrdSpan);
                            dcsplitter.setLanLineBdrY(topLeftY+dcheight-dbrdSpan);

                            var mtxS = dcmatrix.getWanMtxSize();
                            for (var i = 0, ii =  mtxS; i < ii; i++) {
                                dcmatrix.getAreaFromWanMtx(i).mover(dx,dy);
                            };

                            mtxS = dcmatrix.getManMtxSize();
                            for (var i = 0, ii =  mtxS; i < ii; i++) {
                                dcmatrix.getAreaFromManMtx(i).mover(dx,dy);
                            };

                            mtxS = dcmatrix.getLanMtxSize();
                            for (var i = 0, ii =  mtxS; i < ii; i++) {
                                dcmatrix.getAreaFromLanMtx(i).mover(dx,dy);
                            };
                    }
                    dcsplitter.move(r);

                    var mtxS = dcmatrix.getWanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        dcmatrix.getAreaFromWanMtx(i).setMoveJail(topLeftX+dbrdSpan,dcsplitter.getWanLineTopY(),topLeftX+dcwidth-dbrdSpan,dcsplitter.getManLineTopY());
                    }

                    mtxS = dcmatrix.getManMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        dcmatrix.getAreaFromManMtx(i).setMoveJail(topLeftX+dbrdSpan,dcsplitter.getManLineTopY(),topLeftX+dcwidth-dbrdSpan,dcsplitter.getLanLineTopY());
                    }

                    mtxS = dcmatrix.getLanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        dcmatrix.getAreaFromLanMtx(i).setMoveJail(topLeftX+dbrdSpan,dcsplitter.getLanLineTopY(),topLeftX+dcwidth-dbrdSpan,dcsplitter.getLanLineBdrY());
                    }
                    r.safari();
                },
                dUP = function () {
                    helper_.debug("[datacenter.dUP] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    if (rect.attr('cursor')==='default') {
                        var mtxS        = dcmatrix.getWanMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromWanMtx(i).uper();
                        }

                        mtxS = dcmatrix.getManMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromManMtx(i).uper();
                        }

                        mtxS = dcmatrix.getLanMtxSize();
                        for (var i = 0, ii =  mtxS; i < ii; i++) {
                            dcmatrix.getAreaFromLanMtx(i).uper();
                        }
                    } else {
                        isResizing = false;
                        rect.attr('cursor','default');
                    }
                    rect.animate({"fill-opacity": oUnselected}, 500);
                    isMoving = false;
                };

            var dcDragg = function () {
                    this.rx = this.attr("x");
                    this.ry = this.attr("y");
                    this.rw = this.attr("width");
                    this.rh = this.attr("height");
                    this.t0x = dcR[0].attr("x");
                    this.t0y = dcR[0].attr("y");
                    this.t1x = dcR[1].attr("x");
                    this.t1y = dcR[1].attr("y");
                    dDragg();
                },
                dcMove = function (dx, dy) {
                    //if (isMoving && !isResizing)
                    //    dy=0; //DC on NTWWW layout are not free to move on Y (Map WAN,MAN,LAN line must be respected)
                    dMove(this.rx,this.ry,this.rw,this.rh,this.t0x,this.t0y,this.t1x,this.t1y,dx,dy);
                },
                dcUP = function () {
                    dUP();
                },
                dcOver = function () {
                    if (!displayDC) {
                        this.animate({"fill-opacity": oUnselected, "stroke-width": sWidth}, 1);
                        dcR.show();
                        dcsplitter.show();
                    }
                },
                dcOut  = function () {
                    if (!displayDC) {
                        this.animate({"fill-opacity": oUnselected, "stroke-width": 0}, 1);
                        dcR.hide();
                        dcsplitter.hide();
                    }
                },
                dcMouseMove = function(e) {
                    // Don't change cursor  if edition mode is not activated or
                    // during a drag operation
                    var isEditionMode = (options.getMode()==dic.mapMode.EDITION);
                    if ((isMoving && !isResizing) || !isEditionMode) {
                        this.attr('cursor','default');
                    } else {
                        r.rectMouseMove(this, e, dbrdResz, isResizing);
                    }
                    helper_.debug("[datacenter.dcMouseMove] cursor: ".concat(this.attr("cursor")));
                };

            this.isEditionMode = function() {
                return (options.getMode()==dic.mapMode.EDITION);
            }

            this.show = function() {
                rect.animate({"fill-opacity": oUnselected, "stroke-width": sWidth}, 1);
                dcR.show();
                dcsplitter.show();
            };

            this.pushContainerArea = function(container) {
                dcmatrix.addContainerArea(container);
            };

            this.defineZoneSize = function() {
                dcmatrix.defineDCContentSize();

                var contentDCSize = dcmatrix.getDCContentSize();
                dcwidth  = dbrdSpan*2 + (dcmatrix.getMtxSize().x-1)*areaSpan + contentDCSize.width;
                dcheight = dbrdSpan*2 + (dcmatrix.getMtxSize().y-1)*areaSpan + contentDCSize.height;

                mindcW = dcwidth;
                mindcH = dcheight;
            };

            this.defineZoneObjectsSize = function() {
                dcmatrix.defineMtxAreaSize();
            };

            this.definePoz = function() {
                dcmatrix.defineMtxAreaPoz(topLeftX,topLeftY,dcwidth,dbrdSpan,areaSpan);
                dcsplitter = new datacenterSplitter(this);
                dcsplitter.definePoz();
            };

            this.getGeoDCLoc = function() {
                return geoDCLoc;
            };

            this.setTopLeftCoord = function (x, y) {
                topLeftX = x;
                topLeftY = y + dcmatrix.getYOffset(); //align on network type
                mindcWX = topLeftX;
                mindcTY = topLeftY;
            };

            this.geoDCLocEqual = function (geoDCLoc_) {
                return (geoDCLoc.dc==geoDCLoc_.dc);
            };

            this.getZoneSize = function() {
                return {
                    width  : dcwidth,
                    height : dcheight
                };
            };

            this.getZoneCoord = function() {
                return {
                    x : topLeftX,
                    y : topLeftY
                }
            }

            this.getZoneSpan = function() {
                return dbrdSpan;
            }

            this.getMapSplitter = function() {
                return msplitter;
            };

            this.getStrokeDasharray = function() {
                return sDasharray;
            };

            this.getStrokeWidth = function() {
                return sWidth;
            };

            this.getDCMatrix = function() {
                return dcmatrix;
            };

            this.isMoving = function() {
                var mtxS = dcmatrix.getWanMtxSize();
                for (var i = 0, ii =  mtxS; i < ii; i++) {
                    if (dcmatrix.getAreaFromWanMtx(i).isMoving())
                        return true;
                }
                mtxS = dcmatrix.getManMtxSize();
                for (var i = 0, ii =  mtxS; i < ii; i++) {
                    if (dcmatrix.getAreaFromManMtx(i).isMoving())
                        return true;
                }
                mtxS = dcmatrix.getLanMtxSize();
                for (var i = 0, ii =  mtxS; i < ii; i++) {
                    if (dcmatrix.getAreaFromLanMtx(i).isMoving())
                        return true;
                }
                return isMoving;
            };

            this.setIsMoving = function(isMov) {
                isMoving = isMov;
            };

            this.isInserted = function() {
                return isInserted;
            }

            this.setInserted = function() {
                isInserted=true;
            }

            this.print = function(r_) {
                r      = r_;
                dcR    = r.set();
                dcName = r.text(topLeftX + (dcwidth/2), topLeftY + dbrdSpan/2, geoDCLoc.dc);
                dcTown = r.text(topLeftX + (dcwidth/2), topLeftY + dcheight - dbrdSpan/2, geoDCLoc.town)
                rect   = r.rect(topLeftX, topLeftY, dcwidth, dcheight, 0);

                dcName.attr(params.dc_txtTitle).attr({'fill':color});
                dcTown.attr(params.dc_txtTitle).attr({'fill':color});
                dcR.push(dcName);
                dcR.push(dcTown);
                dcR.hide();

                rect.attr({fill: color, stroke: color, "stroke-dasharray": sDasharray, "fill-opacity": oUnselected, "stroke-width": 0});
                rect.drag(dcMove, dcDragg, dcUP);
                rect.mouseover(dcOver);
                rect.mouseout(dcOut);
                rect.mousemove(dcMouseMove);

                dcmatrix.printMtx(r);
                dcsplitter.print(r);
            };

            this.displayDC = function(display) {
                displayDC = display;
                if (displayDC) {
                    rect.animate({"fill-opacity": oUnselected, "stroke-width": sWidth}, 1);
                    dcR.show();
                    dcsplitter.show();
                } else {
                    rect.animate({"fill-opacity": oUnselected, "stroke-width": 0}, 1);
                    dcR.hide();
                    dcsplitter.hide();
                }
            };

            this.displayArea = function(display) {
                dcmatrix.displayArea(display);
            };

            this.displayLan = function(display) {
                dcmatrix.displayLan(display);
            };
        };

        return datacenter;
    });