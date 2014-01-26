// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - Lan                            │ \\
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
        'taitale-helper',
        'taitale-dictionaries',
        'taitale-lan-matrix'
    ],
    function (params,helper,dictionary,lanMatrix) {
        function lan(lanDef_,options_) {
            var r          = null,
                topLeftX   = 0,
                topLeftY   = 0,
                lanwidth   = 0,
                lanheight  = 0,
                contSpan   = params.lan_contSpan, /*space between 2 container*/
                lbrdSpan   = params.lan_lbrdSpan, /*space between 1 container and lan border*/
                lbrdResz   = params.lan_lbrdResz,
                lanmatrix  = new lanMatrix(),
                lanDef     = lanDef_,
                dic        = new dictionary(),
                options    = options_,
                helper_    = new helper(),
                isInserted = false,
                displayLan = false,
                layoutData = null;

            var lanR    = null,
                lanName = null,
                rect    = null;

            var minJailX       = 0,
                minJailY       = 0,
                maxJailX       = 0,
                maxJailY       = 0,
                isJailed       = false,
                isMoving       = false,
                isResizing     = false;

            var mindcW  = 0,
                mindcH  = 0,
                mindcWX = 0,
                mindcTY = 0,
                mindcEX = 0,
                mindcSY = 0;

            var oUnselected = params.lan_opacUnselec,
                oSelected   = params.lan_opacSelec,
                sDasharray  = params.lan_strokeDasharray,
                color       = params.lan_color;

            var reDefineRectPoints = function(x, y) {
                    topLeftX = x;
                    topLeftY = y;
                    helper_.debug("[lan.reDefineRectPoints] { topLeftX: ".concat(topLeftX).concat(", topLeftY: ").concat(topLeftY).concat(" }"));
                },
                reDefineSize = function(width, height) {
                    lanwidth = width;
                    lanheight = height;
                    helper_.debug("[lan.reDefineSize] { lanwidth: ".concat(lanwidth).concat(", lanheight: ").concat(lanheight).concat(" }"));
                },
                reDefineResizingJailRectPoints = function(xe, xw, yn, ys) {
                    if (xw!=null) mindcWX = xw-lbrdSpan;
                    if (xe!=null) mindcEX = xe+lbrdSpan;
                    if (yn!=null) mindcTY = yn-lbrdSpan;
                    if (ys!=null) mindcSY = yn+lbrdSpan;
                    helper_.debug("[lan.reDefineResizingJailRectPoints] { mindcWX: ".concat(mindcWX).concat(", mindcEX: ").concat(mindcEX).concat(", mindcTY: ").concat(mindcTY).concat(", mindcSY: ").concat(mindcSY).concat(" }"));
                };

            var lDragg = function() {
                    helper_.debug("[lan.lDragg] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving: ").concat(isMoving).concat(", isResizing: ").concat(isResizing).concat(" }"));
                    var mtxX        = lanmatrix.getMtxSize().x,
                        mtxY        = lanmatrix.getMtxSize().y;
                    if (rect.attr('cursor')==='default') {
                        for (var i = 0, ii =  mtxX; i < ii; i++) {
                            for (var j = 0, jj =  mtxY; j < jj; j++) {
                                lanmatrix.getContainerFromMtx(i, j).dragger();
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
                                if (maxWX == null) {
                                    maxWX = lanmatrix.getContainerFromMtx(i,j).getContainerCoords().x;
                                    minEX = maxWX + lanmatrix.getContainerFromMtx(i,j).getRectSize().width;
                                    maxNY = lanmatrix.getContainerFromMtx(i,j).getContainerCoords().y;
                                    minSY = maxNY + lanmatrix.getContainerFromMtx(i,j).getRectSize().height;
                                } else {
                                    var lanWX = lanmatrix.getContainerFromMtx(i,j).getContainerCoords().x,
                                        lanEX = lanWX + lanmatrix.getContainerFromMtx(i,j).getRectSize().width,
                                        lanNY = lanmatrix.getContainerFromMtx(i,j).getContainerCoords().y,
                                        lanSY = lanNY + lanmatrix.getContainerFromMtx(i,j).getRectSize().height;
                                    if (lanWX < maxWX)
                                        maxWX = lanWX;
                                    if (lanEX > minEX)
                                        minEX = lanEX;
                                    if (lanNY < maxNY)
                                        maxNY = lanNY;
                                    if (lanSY > minSY)
                                        minSY = lanSY;
                                }
                            }
                        }

                        reDefineResizingJailRectPoints(minEX, maxWX, maxNY, minSY);
                    }
                    isMoving = true;
                },
                lMove = function(rx, ry, rw, rh, t0x, t0y, minTopLeftX, minTopLeftY, maxTopLeftX, maxTopLeftY, dx, dy) {
                    helper_.debug("[lan.lMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    helper_.debug("[lan.lMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    var mtxX        = lanmatrix.getMtxSize().x,
                        mtxY        = lanmatrix.getMtxSize().y;


                    helper_.debug("[lan.lMove] { minTopLeftX: ".concat(minTopLeftX).concat(", minTopLeftY: ").concat(minTopLeftY).concat(", maxTopLeftX: ").concat(maxTopLeftX).concat(", maxTopLeftY: ").concat(maxTopLeftY).concat(" }"));

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
                    helper_.debug("[lan.lMove] { dx: ".concat(dx).concat(", dy: ").concat(dy).concat(" }"));

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
                                lanR[0].attr({
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
                                lanR[0].attr({
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
                                lanR[0].attr({
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
                                lanR[0].attr({
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
                                lanR[0].attr({
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
                                lanR[0].attr({
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
                                lanR[0].attr({
                                    x:TX
                                });
                                reDefineRectPoints(rect.attr("x"),rect.attr("y"));
                                reDefineSize(rect.attr("width"),rect.attr("height"));
                            }
                            break;
                        default:
                            rect.attr({x: rx + dx, y: ry + dy});
                            lanR[0].attr({x: t0x + dx, y: t0y + dy});
                            reDefineRectPoints(rect.attr("x"),rect.attr("y"));

                            for (var i = 0, ii = mtxX; i < ii; i++) {
                                for (var j = 0, jj = mtxY; j < jj; j++) {
                                    lanmatrix.getContainerFromMtx(i, j).mover(dx,dy);
                                }
                            }
                            break;
                    }
                    for (var i = 0, ii = mtxX; i < ii; i++) {
                        for (var j = 0, jj = mtxY; j < jj; j++) {
                            lanmatrix.getContainerFromMtx(i, j).setMoveJail(topLeftX, topLeftY+lbrdSpan, topLeftX+lanwidth, topLeftY+lanheight);
                        }
                    }
                    r.safari();
                },
                lUP = function() {
                    helper_.debug("[lan.lUP] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    if (rect.attr('cursor')==='default') {
                        var mtxX        = lanmatrix.getMtxSize().x,
                            mtxY        = lanmatrix.getMtxSize().y;

                        for (var i = 0, ii = mtxX; i < ii; i++) {
                            for (var j = 0, jj = mtxY; j < jj; j++) {
                                lanmatrix.getContainerFromMtx(i, j).uper();
                            }
                        }
                    } else {
                        isResizing = false;
                        rect.attr('cursor','default');
                    }

                    isMoving = false;
                }

            var lanDragg = function () {
                    this.rx = this.attr("x");
                    this.ry = this.attr("y");
                    this.rw = this.attr("width");
                    this.rh = this.attr("height");
                    this.t0x = lanR[0].attr("x");
                    this.t0y = lanR[0].attr("y");
                    this.minTopLeftX = minJailX;
                    this.minTopLeftY = minJailY;
                    this.maxTopLeftX = maxJailX - lanwidth;
                    this.maxTopLeftY = maxJailY - lanheight;
                    this.animate({"fill-opacity": oSelected}, 500);
                    lDragg();
                },
                lanMove = function (dx, dy) {
                    lMove(this.rx,this.ry,this.rw,this.rh,this.t0x,this.t0y,this.minTopLeftX,this.minTopLeftY,this.maxTopLeftX,this.maxTopLeftY,dx,dy);
                },
                lanUP = function () {
                    this.animate({"fill-opacity": oUnselected}, 500);
                    lUP();
                },
                lanOver = function () {
                    if (!displayLan) {
                        rect.animate({"fill-opacity": oUnselected, "stroke-width": params.lan_strokeWidthShow}, 1);
                        lanR.show();
                    }
                },
                lanOut  = function () {
                    if (!displayLan) {
                        rect.animate({"fill-opacity": oUnselected, "stroke-width": 0}, 1);
                        lanR.hide();
                    }
                },
                lanMouseMove = function(e) {
                    // Don't change cursor  if edition mode is not activated or
                    // during a drag operation
                    var isEditionMode = (options.getMode()==dic.mapMode.EDITION);
                    if ((isMoving && !isResizing) || !isEditionMode) {
                        this.attr('cursor','default');
                    } else {
                        r.rectMouseMove(this, e, lbrdResz, isResizing);
                    }
                    helper_.debug("[lan.lanMouseMove] cursor: ".concat(this.attr("cursor")));
                };

            this.dragger = function() {
                this.extrx  = rect.attr("x");
                this.extry  = rect.attr("y");
                this.extrw  = rect.attr("width");
                this.extrh  = rect.attr("height");
                this.extt0x = lanR[0].attr("x");
                this.extt0y = lanR[0].attr("y");
                rect.attr('cursor','default');
                lDragg();
            }

            this.mover = function(dx,dy) {
                this.minTopLeftX = minJailX;
                this.minTopLeftY = minJailY;
                this.maxTopLeftX = maxJailX - lanwidth;
                this.maxTopLeftY = maxJailY - lanheight;
                lMove(this.extrx,this.extry,this.extrw,this.extrh,this.extt0x,this.extt0y,this.minTopLeftX,this.minTopLeftY,this.maxTopLeftX,this.maxTopLeftY,dx,dy);
            }

            this.uper = function() {
                lUP();
            }

            this.pushContainer = function(container) {
                lanmatrix.addContainer(container);
            };

            this.defineSize = function() {
                lanmatrix.defineLanContentSize();

                var contentLanSize = lanmatrix.getLanContentSize();
                lanwidth  = lbrdSpan*2 + (lanmatrix.getMtxSize().x-1)*contSpan + contentLanSize.width;
                lanheight = lbrdSpan*2 + (lanmatrix.getMtxSize().y-1)*contSpan + contentLanSize.height;
            };

            this.definePoz = function() {
                lanmatrix.defineMtxContainerPoz(topLeftX, topLeftY, lbrdSpan, contSpan, lanwidth, lanheight);
            };

            this.getLanDef = function() {
                return lanDef;
            };

            this.getLanSize = function() {
                return {
                    width  : lanwidth,
                    height : lanheight
                };
            };

            this.setTopLeftCoord = function(x,y) {
                topLeftX = x;
                topLeftY = y;
            };

            this.getLanCoords = function() {
                return {
                    x: topLeftX,
                    y: topLeftY
                }
            };

            this.defEqual = function(lanDef_) {
                var ret =
                    (
                        lanDef.dc===lanDef_.dc &&
                        lanDef.type===lanDef_.type &&
                        lanDef.area===lanDef_.area &&
                        lanDef.lan===lanDef_.lan &&
                        lanDef.subnetip===lanDef_.subnetip &&
                        lanDef.subnetmask===lanDef_.subnetmask
                    );
                return ret;
            };

            this.setMoveJail = function(minJailX_, minJailY_, maxJailX_, maxJailY_) {
                if (minJailX_!=null) minJailX = minJailX_;
                if (minJailY_!=null) minJailY = minJailY_;
                if (maxJailX_!=null) maxJailX = maxJailX_;
                if (maxJailY_!=null) maxJailY = maxJailY_;
                isJailed = true;
            };

            this.isMoving = function() {
                return isMoving;
            };

            this.isInserted = function() {
                return isInserted;
            };

            this.setInserted = function() {
                isInserted = true;
            };

            this.setLayoutData = function(data) {
                if (layoutData!=null) {
                    for (var i = 0, ii = data.length; i < ii; i++) {
                        if (layoutData[i]!=null)
                            layoutData[i] = layoutData[i] || data[i];
                        else
                            layoutData[i] = data[i];
                    }
                } else {
                    layoutData = data;
                }
            };

            this.getLayoutData = function() {
                return layoutData;
            }

            this.print = function(r_) {
                r = r_;
                var lanTitle = "Lan " + lanDef.lan + " - " + lanDef.subnetip + "/" + lanDef.subnetmask;

                lanR    = r.set(),
                lanName = r.text(topLeftX + (lanwidth/2), topLeftY + lbrdSpan/2, lanTitle);
                rect    = r.rect(topLeftX, topLeftY, lanwidth, lanheight, 0);

                lanName.attr(params.lan_txtTitle);
                lanR.push(lanName);
                lanR.hide();

                rect.attr({fill: color, stroke: color, "stroke-dasharray": sDasharray, "fill-opacity": oUnselected, "stroke-width": 0});
                rect.drag(lanMove, lanDragg, lanUP);
                rect.mouseover(lanOver);
                rect.mouseout(lanOut);
                rect.mousemove(lanMouseMove);
            };

            this.displayLan = function(display) {
                displayLan=display;
                if (displayLan) {
                    rect.animate({"fill-opacity": oUnselected, "stroke-width": params.lan_strokeWidthShow}, 1);
                    lanR.show();
                } else {
                    rect.animate({"fill-opacity": oUnselected, "stroke-width": 0}, 1);
                    lanR.hide();
                }
            };
        };
        return lan;
    });