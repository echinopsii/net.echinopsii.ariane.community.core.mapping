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
        'taitale-area-hat',
        'taitale-helper'
    ],
    function (params,dictionary,areaMatrix, areaHat, helper) {
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
            this.rect     = null;

            this.minJailX    = 0;
            this.minJailY    = 0;
            this.maxJailX    = 0;
            this.maxJailY    = 0;
            this.isJailed    = false;
            this.isMoving    = false;
            this.isEditing   = false;
            this.rightClick  = false;

            this.oUnselected = params.area_opacUnselec;
            this.oSelected   = params.area_opacSelec;
            this.sDasharray  = params.area_strokeDasharray;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.area_menuFillColor;
            this.menuOpacity       = params.area_menuOpacity;
            this.menuStrokeWidth   = params.area_menuStrokeWidth;
            this.menuHided         = true;

            this.menuMainTitleTXT  = params.area_menuMainTitle;
            this.menuFieldTXT      = params.area_menuFields;
            this.menuFieldTXTOver  = params.area_menuFieldsOver;

            this.menuEditionMode     = null;
            this.menuEditionModeRect = null;
            this.menuFieldStartEditTitle  = "Edition mode ON";
            this.menuFieldStopEditTitle   = "Edition mode OFF";

            this.areaHat  = new areaHat(this.areaDef.type + " area | " +
                ((this.areaDef.marea != null) ? this.areaDef.marea : "no multicast area"),
                params.area_txtTitle, params.area_color);

            this.mvx = 0;
            this.mvy = 0;

            var areaRef = this ;

            var mouseDown = function(e) {
                    if (e.which == 3) {
                        if (areaRef.menuHided) {
                            areaRef.rect.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                            areaRef.areaR.show();
                            areaRef.dispArea = true;
                            areaRef.rectTopMiddleX = areaRef.topLeftX + areaRef.areawidth/2;
                            areaRef.rectTopMiddleY = areaRef.topLeftY;
                            areaRef.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = areaRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    areaRef.menuSet[i].attr({"x": areaRef.rectTopMiddleX, "y": areaRef.rectTopMiddleY +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = areaRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": areaRef.rectTopMiddleX - fieldRectWidth/2, "y": areaRef.rectTopMiddleY+30 - fieldRectHeight/2});
                                    areaRef.menuSet[i+1].attr({"x": areaRef.rectTopMiddleX, "y": areaRef.rectTopMiddleY+30});
                                    if (areaRef.isEditing) areaRef.menuSet[i+1].attr({text: areaRef.menuFieldStopEditTitle});
                                    else areaRef.menuSet[i+1].attr({text: areaRef.menuFieldStartEditTitle});
                                    i++;
                                } else {
                                    fieldRect = areaRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": areaRef.rectTopMiddleX - fieldRectWidth/2, "y": areaRef.rectTopMiddleY+30+(i-2)*15 - fieldRectHeight/2});
                                    areaRef.menuSet[i+1].attr({"x": areaRef.rectTopMiddleX, "y": areaRef.rectTopMiddleY+30+(i-2)*15});
                                    i++;
                                }
                            }
                            if (areaRef.menu != null)
                                areaRef.menu.remove();
                            areaRef.menu = areaRef.r.menu(areaRef.rectTopMiddleX,areaRef.rectTopMiddleY+10,areaRef.menuSet).
                                attr({fill: areaRef.menuFillColor, stroke: areaRef.color, "stroke-width": areaRef.menuStrokeWidth,
                                    "fill-opacity": areaRef.menuOpacity});
                            areaRef.menu.mousedown(menuMouseDown);
                            areaRef.menu.toFront();
                            areaRef.menuSet.toFront();
                            areaRef.menuSet.show();
                            areaRef.menuHided=false;
                        } else {
                            if (!areaRef.isEditing) {
                                areaRef.rect.animate({"stroke-width": 0}, 0);
                                areaRef.areaR.hide();
                                areaRef.dispArea = false;
                            }
                            areaRef.menu.toBack();
                            areaRef.menuSet.toBack();
                            areaRef.menu.hide();
                            areaRef.menuSet.hide();
                            areaRef.menuHided=true;
                        }
                        areaRef.rightClick=true;
                        if (areaRef.r.getDisplayMainMenu())
                            areaRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        areaRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        if (!areaRef.isEditing) {
                            areaRef.rect.animate({"stroke-width": 0}, 0);
                            areaRef.areaR.hide();
                            areaRef.dispArea = false;
                        }
                        areaRef.menu.toBack();
                        areaRef.menuSet.toBack();
                        areaRef.menu.hide();
                        areaRef.menuSet.hide();
                        areaRef.menuHided=true;
                        areaRef.rightClick=true;
                        if (areaRef.r.getDisplayMainMenu())
                            areaRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        areaRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(areaRef.menuFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(areaRef.menuFieldTXT);
                };

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
                    if (!areaRef.dispArea && !areaRef.isMoving && !areaRef.isEditing) {
                        this.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                        areaRef.areaR.show();
                        areaRef.areaHat.show();
                    }
                },
                areaOut = function () {
                    if (!areaRef.dispArea && !areaRef.isMoving && !areaRef.isEditing) {
                        this.animate({"stroke-width": 0}, 1);
                        areaRef.areaR.hide();
                        areaRef.areaHat.hide();
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

            this.defineMaxSize = function() {
                this.armatrix.defineMtxObjMaxSize();
                this.armatrix.defineAreaContentMaxSize();

                var contentAreaSize = this.armatrix.getAreaContentMaxSize();
                this.areawidth  = this.abrdSpan*2 + (this.armatrix.getMtxSize().x-1)*this.lanSpan + contentAreaSize.width;
                this.areaheight = this.abrdSpan*2 + (this.armatrix.getMtxSize().y-1)*this.lanSpan + contentAreaSize.height;
            };

            this.defineSize = function() {
                this.armatrix.defineMtxObjSize();
                this.armatrix.defineAreaContentSize();

                var contentAreaSize = this.armatrix.getAreaContentSize();
                this.areawidth  = this.abrdSpan*2 + (this.armatrix.getMtxSize().x-1)*this.lanSpan + contentAreaSize.width;
                this.areaheight = this.abrdSpan*2 + (this.armatrix.getMtxSize().y-1)*this.lanSpan + contentAreaSize.height;
            };

            this.defineFirstPoz = function() {
                this.armatrix.defineMtxObjFirstPoz(this.topLeftX, this.topLeftY, this.abrdSpan, this.lanSpan, this.areawidth, this.areaheight);
            };

            this.optimizeMtxCoord = function() {
                this.armatrix.optimizeLanAndBusMtxCoord();
            };

            this.defineFinalPoz = function() {
                this.armatrix.defineMtxObjFinalPoz(this.topLeftX, this.topLeftY, this.abrdSpan, this.lanSpan, this.areawidth, this.areaheight);
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

            this.getAreaMaxSize = function() {
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

            this.isElemMoving = function() {
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
                this.areaHat.print(this.r, this.topLeftX + (this.areawidth/2), this.topLeftY + this.abrdSpan/5);

                this.rect     = this.r.rect(this.topLeftX, this.topLeftY, this.areawidth, this.areaheight, 0);

                this.areaHat.mousedown(mouseDown);
                this.areaHat.drag(areaMove, areaDragg, areaUP);

                this.rect.attr({fill: params.area_color, stroke: params.area_color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                this.rect.mousedown(mouseDown);
                this.rect.drag(areaMove, areaDragg, areaUP);
                this.rect.mouseover(areaOver);
                this.rect.mouseout(areaOut);

                this.areaR.hide();
                this.areaHat.hide();
                this.armatrix.printMtx(this.r);

                this.menuTitle = this.r.text(0,10,"Area menu").attr(this.menuMainTitleTXT);

                this.menuEditionModeRect = this.r.rect(0,10,this.menuFieldStartEditTitle.width(this.menuFieldTXT),this.menuFieldStartEditTitle.height(this.menuFieldTXT));
                this.menuEditionModeRect.attr({fill: this.color, stroke: this.color, "fill-opacity": 0, "stroke-width": 0});
                this.menuEditionModeRect.mouseover(menuFieldOver);
                this.menuEditionModeRect.mouseout(menuFieldOut);
                this.menuEditionModeRect.mousedown(this.menuFieldEditClick);
                this.menuEditionMode = this.r.text(0,10,this.menuFieldStartEditTitle).attr(this.menuFieldTXT);
                this.menuEditionMode.mouseover(menuFieldOver);
                this.menuEditionMode.mouseout(menuFieldOut);
                this.menuEditionMode.mousedown(this.menuFieldEditClick);

                this.menuSet = this.r.set();
                this.menuSet.push(this.menuTitle);
                this.menuSet.push(this.menuEditionModeRect);
                this.menuSet.push(this.menuEditionMode);

                this.menuSet.toBack();
                this.menuSet.hide();
            };

            this.displayArea = function(display) {
                this.dispArea=display;
                if (display) {
                    this.rect.animate({"stroke-width": params.area_strokeWidthShow}, 1);
                    this.areaR.show();
                    this.areaHat.show();
                } else {
                    this.rect.animate({"stroke-width": 0}, 1);
                    this.areaR.hide();
                    this.areaHat.hide();
                }
            };

            this.displayLan = function(display) {
                this.armatrix.displayLan(display);
            };

            this.changeInit = function() {
                this.extrx  = this.rect.attr("x");
                this.extry  = this.rect.attr("y");
                this.extrw  = this.rect.attr("width");
                this.extrh  = this.rect.attr("height");
                this.minTopLeftX = this.minJailX;
                this.minTopLeftY = this.minJailY;
                this.maxTopLeftX = this.maxJailX - this.areawidth;
                this.maxTopLeftY = this.maxJailY - this.areaheight;
            };

            this.changeUp = function() {
                var j, jj, k, kk, mtxX, mtxY;

                this.setTopLeftCoord(this.rect.attr("x"),this.rect.attr("y"));

                mtxX = this.armatrix.getMtxSize().x;
                mtxY = this.armatrix.getMtxSize().y;

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

            };

            //MOVEABLE

            this.moveInit = function() {
                if (this.isEditing)
                    this.r.scaleDone(this);

                this.r.areasOnMovePush(this);
                this.r.moveSetPush(this.rect);

                var mtxX, mtxY, i, ii, j, jj;
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

                this.changeInit();
                this.isMoving = true;
            };

            this.moveAction = function(dx, dy) {
                this.mvx = dx; this.mvy = dy;
                this.areaHat.move(this.r, this.extrx + dx + (this.areawidth/2), this.extry + dy + this.abrdSpan/5);
            };

            this.moveUp = function() {
                var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy},
                    attrtxt0 = {x: this.extt0x + this.mvx, y: this.extt0y + this.mvy};

                this.mvx=0; this.mvy=0;
                this.rect.attr(attrect);

                this.changeUp();
                this.isMoving = false;
                if (this.isEditing)
                    this.r.scaleInit(this)
            };

            //EDITABLE

            this.setEditionMode = function(editionMode) {
                if (editionMode) {
                    if (this.isEditing)
                        this.r.scaleDone(this);
                    this.r.scaleInit(this);
                    this.isEditing = true;
                } else if (!editionMode) {
                    if (this.isEditing)
                        this.r.scaleDone(this);
                    this.isEditing = false;
                }

                var mtxX, mtxY, i, ii, j, jj;
                mtxX = this.armatrix.getMtxSize().x;
                mtxY = this.armatrix.getMtxSize().y;
                for (i = 0, ii =  mtxX; i < ii; i++)
                    for (j = 0, jj =  mtxY; j < jj; j++) {
                        var areaObj = this.armatrix.getObjFromMtx(i,j);
                        var areaObjType = this.armatrix.getObjTypeFromMtx(i,j);
                        if (areaObjType==="LAN")
                            areaObj.setEditionMode(editionMode);
                        else if (areaObjType==="BUS")
                            areaObj.mbus.setEditionMode(editionMode);
                    }
            };

            this.menuFieldEditClick = function() {
                areaRef.menu.toBack();
                areaRef.menuSet.toBack();
                areaRef.menu.hide();
                areaRef.menuSet.hide();
                areaRef.menuHided=true;

                if (!areaRef.isEditing) {
                    areaRef.r.scaleInit(areaRef);
                    areaRef.isEditing = true;
                } else {
                    areaRef.r.scaleDone(areaRef);
                    areaRef.isEditing = false;
                    areaRef.rect.animate({"fill-opacity": areaRef.oUnselected, "stroke-width": 0}, 0);
                    areaRef.areaHat.hide();
                    areaRef.areaR.hide();
                    areaRef.dispArea = false;
                }
            };

            this.getBBox = function() {
                return this.rect.getBBox();
            };

            var areaObjSet;
            this.getMinBBox = function() {
                var mtxX, mtxY, i, ii, j, jj;

                areaObjSet = this.r.set();

                mtxX = this.armatrix.getMtxSize().x;
                mtxY = this.armatrix.getMtxSize().y;
                for (i = 0, ii =  mtxX; i < ii; i++)
                    for (j = 0, jj =  mtxY; j < jj; j++) {
                        var areaObj = this.armatrix.getObjFromMtx(i,j);
                        var areaObjType = this.armatrix.getObjTypeFromMtx(i,j);
                        if (areaObjType==="LAN")
                            areaObjSet.push(areaObj.rect);
                    }

                var areaMinBBox = areaObjSet.getBBox();

                return {
                    x: areaMinBBox.x - this.abrdSpan,
                    y: areaMinBBox.y - this.abrdSpan,
                    x2: areaMinBBox.x2 + this.abrdSpan,
                    y2: areaMinBBox.y2 + this.abrdSpan,
                    width: areaMinBBox.width + 2*this.abrdSpan,
                    height: areaMinBBox.height + 2*this.abrdSpan
                };
            };

            this.getMaxBBox = function() {
                if (this.isJailed) {
                    return {
                        x: this.minJailX,
                        y: this.minJailY,
                        x2: this.maxJailX,
                        y2: this.maxJailY,
                        width: this.maxJailX - this.minJailX,
                        height: this.maxJailY - this.minJailY
                    }
                } else {
                    return null;
                }
            };

            this.editInit = function() {
                this.extwidth  = this.areawidth;
                this.extheight = this.areaheight;
                this.changeInit();
                this.isMoving = true;
            };

            this.editAction = function(elem, dx, dy) {
                switch(elem.idx) {
                    case 0:
                        this.extrx = this.topLeftX + dx;
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.areawidth - dx;
                        this.extheight = this.areaheight - dy;
                        break;

                    case 1:
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.areawidth + dx;
                        this.extheight = this.areaheight - dy;
                        break;

                    case 2:
                        this.extwidth = this.areawidth + dx;
                        this.extheight = this.areaheight + dy;
                        break;

                    case 3:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.areawidth - dx;
                        this.extheight = this.areaheight + dy;
                        break;

                    case 4:
                        this.extry = this.topLeftY + dy;
                        this.extheight = this.areaheight - dy;
                        break;

                    case 5:
                        this.extwidth = this.areawidth + dx;
                        break;

                    case 6:
                        this.extheight = this.areaheight + dy;
                        break;

                    case 7:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.areawidth - dx;
                        break;

                    default:
                        break;
                }

                this.areaR.pop(this.areaName);
                this.rect.remove();

                var title    = this.areaDef.type + " area | " + ((this.areaDef.marea != null) ? this.areaDef.marea : "no multicast area");
                this.rect     = this.r.rect(this.extrx, this.extry, this.extwidth, this.extheight, 0);

                this.areaR.push(this.areaName);

                this.rect.attr({fill: params.area_color, stroke: params.area_color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 1});
                this.rect.drag(areaMove, areaDragg, areaUP);
                this.rect.mouseover(areaOver);
                this.rect.mouseout(areaOut);
                this.rect.mousedown(mouseDown);

                this.areaHat.move(this.r, this.extrx + (this.extwidth/2), this.extry + this.abrdSpan/5);

                var mtxX, mtxY, i, ii, j, jj;
                mtxX = this.armatrix.getMtxSize().x;
                mtxY = this.armatrix.getMtxSize().y;
                for (i = 0, ii =  mtxX; i < ii; i++)
                    for (j = 0, jj =  mtxY; j < jj; j++) {
                        var areaObj = this.armatrix.getObjFromMtx(i,j);
                        var areaObjType = this.armatrix.getObjTypeFromMtx(i,j);
                        if (areaObjType==="LAN")
                            areaObj.toFront();
                        else if (areaObjType==="BUS")
                            areaObj.mbus.toFront();
                    }
            };

            this.editUp = function() {
                this.areawidth = this.extwidth;
                this.areaheight = this.extheight;
                this.isMoving = false;
                this.changeUp();
            };

        }

        return area;
    });