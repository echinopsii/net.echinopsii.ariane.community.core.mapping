// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - Area                           │ \\
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
        'taitale-area-matrix',
        'taitale-helper'
    ],
    function (params,dictionary,areaMatrix, helper) {
        function area(areaDef_, registries, options_) {
            var r           = null,
                helper_     = new helper(),
                topLeftX    = 0,
                topLeftY    = 0,
                areawidth   = 0,
                areaheight  = 0,
                lanSpan     = params.area_lanSpan,  /*space between 2 lan*/
                abrdSpan    = params.area_abrdSpan, /*space between 1 lan and area border*/
                abrdResz    = params.area_abrdResz,
                armatrix    = new areaMatrix(registries, options_),
                areaDef     = areaDef_,
                dic         = new dictionary(),
                options     = options_,
                isInserted  = false,
                displayArea = false;

            var areaR    = null,
                areaName = null,
                rect     = null,
                color    = null;

            var minJailX    = 0,
                minJailY    = 0,
                maxJailX    = 0,
                maxJailY    = 0,
                isJailed    = false,
                isMoving    = false,
                isResizing  = false;

            var mindcW  = 0,
                mindcH  = 0,
                mindcWX = 0,
                mindcTY = 0,
                mindcEX = 0,
                mindcSY = 0;

            var oUnselected = params.area_opacUnselec,
                oSelected   = params.area_opacSelec,
                sDasharray  = params.area_strokeDasharray;

            var reDefineRectPoints = function(x, y) {
                    topLeftX = x;
                    topLeftY = y;
                    helper_.debug("[area.reDefineRectPoints] { topLeftX: ".concat(topLeftX).concat(", topLeftY: ").concat(topLeftY).concat(" }"));
                },
                reDefineSize = function(width, height) {
                    areawidth = width;
                    areaheight = height;
                    helper_.debug("[area.reDefineSize] { areawidth: ".concat(areawidth).concat(", areaheight: ").concat(areaheight).concat(" }"));
                },
                reDefineResizingJailRectPoints = function(xe, xw, yn, ys) {
                    if (xw!=null) mindcWX = xw-abrdSpan;
                    if (xe!=null) mindcEX = xe+abrdSpan;
                    if (yn!=null) mindcTY = yn-abrdSpan;
                    if (ys!=null) mindcSY = yn+abrdSpan;
                    helper_.debug("[area.reDefineResizingJailRectPoints] { mindcWX: ".concat(mindcWX).concat(", mindcEX: ").concat(mindcEX).concat(", mindcTY: ").concat(mindcTY).concat(", mindcSY: ").concat(mindcSY).concat(" }"));
                };

            var arDragg = function () {
                    helper_.debug("[area.arDragg] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving: ").concat(isMoving).concat(", isResizing: ").concat(isResizing).concat(" }"));
                    var mtxX        = armatrix.getMtxSize().x,
                        mtxY        = armatrix.getMtxSize().y;
                    if (rect.attr('cursor')==='default') {
                        for (var i = 0, ii =  mtxX; i < ii; i++) {
                            for (var j = 0, jj =  mtxY; j < jj; j++) {
                                var obj = armatrix.getObjFromMtx(i,j);
                                if (obj!=null)
                                    obj.dragger();
                            }
                        }
                    } else {
                        isResizing = true;

                        var maxWX = null,
                            minEX = null,
                            maxNY = null,
                            minSY = null;

                        for (var i = 0, ii =  mtxX; i < ii; i++) {
                            for (var j = 0, jj =  mtxY; j < jj; j++) {
                                var objSize   = armatrix.getObjSizeFromMtx(i,j),
                                    objCoords = armatrix.getObjCoordsFromMtx(i,j);
                                if (objSize!=null && objCoords!=null) {
                                    if (maxWX == null) {
                                        maxWX = objCoords.x;
                                        minEX = maxWX + objSize.width;
                                        maxNY = objCoords.y;
                                        minSY = maxNY+ objSize.height;
                                    } else {
                                        var areaWX = objCoords.x,
                                            areaEX = areaWX + objSize.width,
                                            areaNY = objCoords.y,
                                            areaSY = areaNY+ objSize.height;
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
                            }
                        }

                        reDefineResizingJailRectPoints(minEX, maxWX, maxNY, minSY);
                    }
                    isMoving = true;
                },
                arMove = function (rx, ry, rw, rh, t0x, t0y, minTopLeftX, minTopLeftY, maxTopLeftX, maxTopLeftY, dx, dy) {
                    helper_.debug("[area.arMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    helper_.debug("[area.arMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    helper_.debug("[area.arMove] { minTopLeftX: ".concat(minTopLeftX).concat(", minTopLeftY: ").concat(minTopLeftY).concat(", maxTopLeftX: ").concat(maxTopLeftX).concat(", maxTopLeftY: ").concat(maxTopLeftY).concat(" }"));
                    var mtxX        = armatrix.getMtxSize().x,
                        mtxY        = armatrix.getMtxSize().y;

                    if (isJailed) {
                        if (minTopLeftX > rx + dx)
                            dx = minTopLeftX - rx;
                        if (minTopLeftY > ry + dy)
                            dy = minTopLeftY - ry;
                        if (maxTopLeftX < rx + dx)
                            dx = maxTopLeftX - rx;
                        if (maxTopLeftY < ry + dy)
                            dy = maxTopLeftY - ry;
                    };
                    helper_.debug("[area.arMove] { dx: ".concat(dx).concat(", dy: ").concat(dy).concat(" }"));

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
                                areaR[0].attr({
                                    y:TY
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 'ne-resize' :
                            var RY  = ry + dy,
                                RW  = rw + dx,
                                RH  = rh - dy,
                                RXW = topLeftX + RW,
                                TY  = t0y + dy,
                                TX  = rx + RW/2;
                            if (RH>mindcH && RY<mindcTY && RXW>mindcEX) {
                                rect.attr({
                                    y: RY,
                                    width: RW,
                                    height: RH
                                });
                                areaR[0].attr({
                                    x:TX,
                                    y:TY
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 'nw-resize' :
                            var RX = rx + dx,
                                RY = ry + dy,
                                RW = rw - dx,
                                RH = rh - dy,
                                TY = t0y + dy,
                                TX = RX + RW/2;
                            if (RH>mindcH && RY<mindcTY && RX<mindcWX) {
                                rect.attr({
                                    x: RX,
                                    y: RY,
                                    width: RW,
                                    height: RH
                                });
                                areaR[0].attr({
                                    x:TX,
                                    y:TY
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
                                areaR[0].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
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
                                areaR[0].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 's-resize' :
                            var RH  = rh + dy,
                                RYH = topLeftY + RH;
                            if (RH>mindcH && RYH>(mindcTY+mindcH)) {
                                rect.attr({
                                    height: RH
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 'se-resize' :
                            var RW  = rw + dx,
                                RH  = rh + dy,
                                RXW = topLeftX + RW,
                                RYW = topLeftY + RH,
                                TX  = rx + RW/2;
                            if (RH>mindcH && RXW>mindcEX && RYW>(mindcTY+mindcH)) {
                                rect.attr({
                                    width: RW,
                                    height: RH
                                });
                                areaR[0].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        case 'sw-resize' :
                            var RX = rx + dx,
                                RW = rw - dx,
                                RH = rh + dy,
                                RYW = topLeftY + RH,
                                TX = RX + RW/2;
                            if (RH>mindcH && RX<mindcWX && RYW>(mindcTY+mindcH)) {
                                rect.attr({
                                    x: RX,
                                    width: RW,
                                    height: RH
                                });
                                areaR[0].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        default:
                            var attrect = {x: rx + dx, y: ry + dy},
                                attrtxt1 = {x: t0x + dx, y: t0y + dy};

                            rect.attr(attrect);
                            areaR[0].attr(attrtxt1);
                            reDefineRectPoints(rect.attr("x"),rect.attr("y"));

                            for (var i = 0, ii = mtxX; i < ii; i++) {
                                for (var j = 0, jj = mtxY; j < jj; j++) {
                                    var obj = armatrix.getObjFromMtx(i,j)
                                    if (obj!=null)
                                        obj.mover(dx,dy);
                                }
                            }
                            break;
                    }
                    for (var i = 0, ii = mtxX; i < ii; i++) {
                        for (var j = 0, jj = mtxY; j < jj; j++) {
                            var obj = armatrix.getObjFromMtx(i,j)
                            if (obj!=null)
                                obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                        }
                    }
                    r.safari();
                },
                arUP = function () {
                    helper_.debug("[area.arUP] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    if (rect.attr('cursor')==='default') {
                        var mtxX        = armatrix.getMtxSize().x,
                            mtxY        = armatrix.getMtxSize().y;
                        for (var i = 0, ii = mtxX; i < ii; i++) {
                            for (var j = 0, jj = mtxY; j < jj; j++) {
                                var obj = armatrix.getObjFromMtx(i,j)
                                if (obj!=null)
                                    obj.uper();
                            }
                        }
                    } else {
                        isResizing = false;
                        rect.attr('cursor','default');
                    }
                    isMoving = false;
                },
                areaMouseMove = function(e) {
                    // Don't change cursor  if edition mode is not activated or
                    // during a drag operation
                    var isEditionMode = (options.getMode()==dic.mapMode.EDITION);
                    if ((isMoving && !isResizing) || !isEditionMode) {
                        this.attr('cursor','default');
                    } else {
                        r.rectMouseMove(this, e, abrdResz, isResizing);
                    }
                    helper_.debug("[area.areaMouseMove] cursor: ".concat(this.attr("cursor")));

                };

            var areaDragg = function () {
                    this.rx = this.attr("x");
                    this.ry = this.attr("y");
                    this.rw = this.attr("width");
                    this.rh = this.attr("height");
                    this.t0x = areaR[0].attr("x");
                    this.t0y = areaR[0].attr("y");
                    this.minTopLeftX = minJailX;
                    this.minTopLeftY = minJailY;
                    this.maxTopLeftX = maxJailX - areawidth;
                    this.maxTopLeftY = maxJailY - areaheight;
                    this.animate({"fill-opacity": oSelected}, 500);
                    arDragg();
                },
                areaMove = function (dx, dy) {
                    arMove(this.rx,this.ry,this.rw,this.rh,this.t0x,this.t0y,this.minTopLeftX,this.minTopLeftY,this.maxTopLeftX,this.maxTopLeftY,dx,dy);
                },
                areaUP = function () {
                    rect.animate({"fill-opacity": oUnselected}, 500);
                    arUP();
                },
                areaOver = function () {
                    if (!displayArea) {
                        this.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                        areaR.show();
                    }
                },
                areaOut  = function () {
                    if (!displayArea) {
                        this.animate({"stroke-width": 0}, 1);
                        areaR.hide();
                    }
                };

            this.dragger = function() {
                this.extrx  = rect.attr("x");
                this.extry  = rect.attr("y");
                this.extrw  = rect.attr("width");
                this.extrh  = rect.attr("height");
                this.extt0x = areaR[0].attr("x");
                this.extt0y = areaR[0].attr("y");
                rect.attr('cursor','default');
                arDragg();
            }

            this.mover = function(dx,dy) {
                this.minTopLeftX = minJailX;
                this.minTopLeftY = minJailY;
                this.maxTopLeftX = maxJailX - areawidth;
                this.maxTopLeftY = maxJailY - areaheight;
                arMove(this.extrx,this.extry,this.extrw,this.extrh,this.extt0x,this.extt0y,this.minTopLeftX,this.minTopLeftY,this.maxTopLeftX,this.maxTopLeftY,dx,dy);
            }

            this.uper = function() {
                arUP();
            }

            this.pushContainerLan = function(container) {
                armatrix.addContainerLanAndBus(container);
            };

            this.defineSize = function() {
                armatrix.defineMtxObjSize();
                armatrix.defineAreaContentSize();

                var contentAreaSize = armatrix.getAreaContentSize();
                areawidth  = abrdSpan*2 + (armatrix.getMtxSize().x-1)*lanSpan + contentAreaSize.width;
                areaheight = abrdSpan*2 + (armatrix.getMtxSize().y-1)*lanSpan + contentAreaSize.height;

                mindcW = areawidth;
                mindcH = areaheight;
            };

            this.definePoz = function() {
                armatrix.defineMtxObjPoz(topLeftX, topLeftY, abrdSpan, lanSpan, areawidth, areaheight);
            };

            this.getAreaDef = function() {
                return areaDef;
            };

            this.getAreaSize = function() {
                return {
                    width  : areawidth,
                    height : areaheight
                };
            };

            this.getAreaCoords = function() {
                return {
                    x : topLeftX,
                    y : topLeftY
                }
            }

            this.setTopLeftCoord = function(x,y){
                topLeftX = x;
                topLeftY = y;
            };

            this.defEqual = function(areaDef_) {
                return (areaDef.dc===areaDef_.dc && areaDef.type===areaDef_.type && areaDef.marea===areaDef_.marea);
            };

            this.setMoveJail = function(minJailX_, minJailY_, maxJailX_, maxJailY_) {
                if (minJailX_!=null) minJailX = minJailX_;
                if (minJailY_!=null) minJailY = minJailY_;
                if (maxJailX_!=null) maxJailX = maxJailX_;
                if (maxJailY_!=null) maxJailY = maxJailY_;
                isJailed = true;
            };

            this.isMoving = function() {
                var mtxX        = armatrix.getMtxSize().x,
                    mtxY        = armatrix.getMtxSize().y;
                for (var i = 0, ii = mtxX; i < ii; i++) {
                    for (var j = 0, jj = mtxY; j < jj; j++) {
                        var obj = armatrix.getObjFromMtx(i,j);
                        if (obj!=null && obj.isMoving())
                            return true;
                    }
                }
                return isMoving;
            };

            this.isInserted = function() {
                return isInserted;
            }

            this.setInserted = function() {
                isInserted=true;
            }

            this.print = function(r_) {
                r = r_;
                var title    = areaDef.type + " area | " + ((areaDef.marea != null) ? areaDef.marea : "no multicast area");

                areaR    = r.set();
                areaName = r.text(topLeftX + (areawidth/2), topLeftY + abrdSpan/2, title);
                rect     = r.rect(topLeftX, topLeftY, areawidth, areaheight, 0);

                areaName.attr(params.area_txtTitle).attr({'fill':params.area_color});
                areaR.push(areaName);

                rect.attr({fill: params.area_color, stroke: params.area_color, "stroke-dasharray": sDasharray, "fill-opacity": oUnselected, "stroke-width": 0});
                rect.drag(areaMove, areaDragg, areaUP);
                rect.mouseover(areaOver);
                rect.mouseout(areaOut);
                rect.mousemove(areaMouseMove);

                areaR.hide();
                armatrix.printMtx(r);
            };

            this.displayArea = function(display) {
                displayArea=display;
                if (display) {
                    rect.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                    areaR.show();
                } else {
                    rect.animate({"stroke-width": 0}, 1);
                    areaR.hide();
                };
            };

            this.displayLan = function(display) {
                armatrix.displayLan(display);
            };
        };

        return area;
    });