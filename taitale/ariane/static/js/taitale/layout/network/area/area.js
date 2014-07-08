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
            //noinspection JSUnusedLocalSymbols
            var helper_     = new helper();

            this.r           = null;
            this.topLeftX    = 0;
            this.topLeftY    = 0;
            this.areawidth   = 0;
            this.areaheight  = 0;
            this.lanSpan     = params.area_lanSpan;  /*space between 2 lan*/
            this.abrdSpan    = params.area_abrdSpan; /*space between 1 lan and area border*/
            //this.abrdResz    = params.area_abrdResz;
            this.armatrix    = new areaMatrix(registries, options_);
            this.areaDef     = areaDef_;
            this.dic         = new dictionary();
            this.options     = options_;
            this.isInserted  = false;
            this.dispArea    = false;

            this.areaR    = null;
            this.areaName = null;
            this.rect     = null;

            this.minJailX    = 0;
            this.minJailY    = 0;
            this.maxJailX    = 0;
            this.maxJailY    = 0;
            this.isJailed    = false;
            this.isMoving    = false;

            this.oUnselected = params.area_opacUnselec;
            this.oSelected   = params.area_opacSelec;
            this.sDasharray  = params.area_strokeDasharray;

            this.mvx = 0;
            this.mvy = 0;

            var areaRef = this ;

            var areaDragg = function () {
                    areaRef.moveInit();
                    areaRef.rect.animate({"fill-opacity": areaRef.oSelected}, 500);
                },
                areaMove = function (dx, dy) {
                    areaRef.minTopLeftX = areaRef.minJailX;
                    areaRef.minTopLeftY = areaRef.minJailY;
                    areaRef.maxTopLeftX = areaRef.maxJailX - areaRef.areawidth;
                    areaRef.maxTopLeftY = areaRef.maxJailY - areaRef.areaheight;

                    var rx = areaRef.extrx, ry = areaRef.extry,
                        minTopLeftX = areaRef.minTopLeftX,
                        minTopLeftY = areaRef.minTopLeftY,
                        maxTopLeftX = areaRef.maxTopLeftX,
                        maxTopLeftY = areaRef.maxTopLeftY;

                    //helper_.debug("[area.arMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    //helper_.debug("[area.arMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    //helper_.debug("[area.arMove] { minTopLeftX: ".concat(minTopLeftX).concat(", minTopLeftY: ").concat(minTopLeftY).concat(", maxTopLeftX: ").concat(maxTopLeftX).concat(", maxTopLeftY: ").concat(maxTopLeftY).concat(" }"));

                    if (areaRef.isJailed) {
                        if (minTopLeftX > rx + dx)
                            dx = minTopLeftX - rx;
                        if (minTopLeftY > ry + dy)
                            dy = minTopLeftY - ry;
                        if (maxTopLeftX < rx + dx)
                            dx = maxTopLeftX - rx;
                        if (maxTopLeftY < ry + dy)
                            dy = maxTopLeftY - ry;
                    }
                    //helper_.debug("[area.arMove] { dx: ".concat(dx).concat(", dy: ").concat(dy).concat(" }"));

                    areaRef.r.move(dx,dy);
                    areaRef.r.safari();
                },
                areaUP = function () {
                    areaRef.r.up();
                    areaRef.rect.animate({"fill-opacity": areaRef.oUnselected}, 500);
                },
                areaOver = function () {
                    if (!areaRef.dispArea) {
                        this.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                        areaRef.areaR.show();
                    }
                },
                areaOut = function () {
                    if (!areaRef.dispArea) {
                        this.animate({"stroke-width": 0}, 1);
                        areaRef.areaR.hide();
                    }
                };

            /*
            this.dragger = function() {
                this.r.drag(this,"area")
            };
            */

            /*
            this.mover = function(dx,dy) {
                arMove(dx,dy);
            };
            */

            /*
            this.uper = function() {
                this.r.up();
            };
            */

            this.pushContainerLan = function(container) {
                this.armatrix.addContainerLanAndBus(container);
            };

            this.defineSize = function() {
                this.armatrix.defineMtxObjSize();
                this.armatrix.defineAreaContentSize();

                var contentAreaSize = this.armatrix.getAreaContentSize();
                this.areawidth  = this.abrdSpan*2 + (this.armatrix.getMtxSize().x-1)*this.lanSpan + contentAreaSize.width;
                this.areaheight = this.abrdSpan*2 + (this.armatrix.getMtxSize().y-1)*this.lanSpan + contentAreaSize.height;
            };

            this.definePoz = function() {
                this.armatrix.defineMtxObjPoz(this.topLeftX, this.topLeftY, this.abrdSpan, this.lanSpan, this.areawidth, this.areaheight);
            };

            this.getAreaDef = function() {
                return this.areaDef;
            };

            this.getAreaSize = function() {
                return {
                    width  : this.areawidth,
                    height : this.areaheight
                };
            };

            this.getAreaCoords = function() {
                return {
                    x : this.topLeftX,
                    y : this.topLeftY
                }
            };

            this.setTopLeftCoord = function(x,y){
                this.topLeftX = x;
                this.topLeftY = y;
            };

            this.defEqual = function(areaDef_) {
                return (this.areaDef.dc===areaDef_.dc && this.areaDef.type===areaDef_.type && this.areaDef.marea===areaDef_.marea);
            };

            this.setMoveJail = function(minJailX_, minJailY_, maxJailX_, maxJailY_) {
                if (minJailX_!=null) this.minJailX = minJailX_;
                if (minJailY_!=null) this.minJailY = minJailY_;
                if (maxJailX_!=null) this.maxJailX = maxJailX_;
                if (maxJailY_!=null) this.maxJailY = maxJailY_;
                this.isJailed = true;
            };

            this.isMoving = function() {
                var mtxX        = this.armatrix.getMtxSize().x,
                    mtxY        = this.armatrix.getMtxSize().y;
                var i, ii, j, jj;
                for (i = 0, ii = mtxX; i < ii; i++) {
                    for (j = 0, jj = mtxY; j < jj; j++) {
                        var obj = this.armatrix.getObjFromMtx(i,j);
                        if (obj!=null && obj.isMoving)
                            return true;
                    }
                }
                return this.isMoving;
            };

            this.print = function(r_) {
                this.r = r_;
                var title    = this.areaDef.type + " area | " + ((this.areaDef.marea != null) ? this.areaDef.marea : "no multicast area");

                this.areaR    = this.r.set();
                this.areaName = this.r.text(this.topLeftX + (this.areawidth/2), this.topLeftY + this.abrdSpan/2, title);
                this.rect     = this.r.rect(this.topLeftX, this.topLeftY, this.areawidth, this.areaheight, 0);

                this.areaName.attr(params.area_txtTitle).attr({'fill':params.area_color});
                this.areaR.push(this.areaName);

                this.rect.attr({fill: params.area_color, stroke: params.area_color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                this.rect.drag(areaMove, areaDragg, areaUP);
                this.rect.mouseover(areaOver);
                this.rect.mouseout(areaOut);

                this.areaR.hide();
                this.armatrix.printMtx(this.r);
            };

            this.displayArea = function(display) {
                this.dispArea=display;
                if (display) {
                    this.rect.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                    this.areaR.show();
                } else {
                    this.rect.animate({"stroke-width": 0}, 1);
                    this.areaR.hide();
                }
            };

            this.displayLan = function(display) {
                this.armatrix.displayLan(display);
            };

            this.moveInit = function() {
                var mtxX, mtxY, i, ii, j, jj;

                this.r.areasOnMovePush(this);
                this.r.moveSetPush(this.areaName);
                this.r.moveSetPush(this.rect);

                mtxX = this.armatrix.getMtxSize().x;
                mtxY = this.armatrix.getMtxSize().y;
                for (i = 0, ii =  mtxX; i < ii; i++)
                    for (j = 0, jj =  mtxY; j < jj; j++) {
                        var areaObj = this.armatrix.getObjFromMtx(i,j);
                        var areaObjType = this.armatrix.getObjTypeFromMtx(i,j);
                        if (areaObjType==="LAN")
                            areaObj.moveInit();
                        else if (areaObjType==="BUS")
                            areaObj.mbus.moveInit();
                    }

                this.extrx  = this.rect.attr("x");
                this.extry  = this.rect.attr("y");
                this.extrw  = this.rect.attr("width");
                this.extrh  = this.rect.attr("height");
                this.extt0x = this.areaName.attr("x");
                this.extt0y = this.areaName.attr("y");
                this.minTopLeftX = this.minJailX;
                this.minTopLeftY = this.minJailY;
                this.maxTopLeftX = this.maxJailX - this.areawidth;
                this.maxTopLeftY = this.maxJailY - this.areaheight;

                this.isMoving = true;
            };

            this.moveAction = function(dx, dy) {
                this.mvx = dx; this.mvy = dy;
            };

            this.moveUp = function() {
                var j, jj, k, kk;
                var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy},
                    attrtxt0 = {x: this.extt0x + this.mvx, y: this.extt0y + this.mvy};
                var mtxX     = this.armatrix.getMtxSize().x,
                    mtxY     = this.armatrix.getMtxSize().y;

                this.mvx=0; this.mvy=0;
                this.rect.attr(attrect);
                this.areaName.attr(attrtxt0);

                this.setTopLeftCoord(this.rect.attr("x"),this.rect.attr("y"));
                for (j = 0, jj = mtxX; j < jj; j++) {
                    for (k = 0, kk = mtxY; k < kk; k++) {
                        var obj = this.armatrix.getObjFromMtx(j,k);
                        if (obj!=null)
                            obj.setMoveJail(
                                this.topLeftX + this.abrdSpan,
                                this.topLeftY + this.abrdSpan,
                                this.topLeftX + this.areawidth  - this.abrdSpan,
                                this.topLeftY + this.areaheight - this.abrdSpan
                            );
                    }
                }
                this.isMoving = false;
            };
        }

        return area;
    });