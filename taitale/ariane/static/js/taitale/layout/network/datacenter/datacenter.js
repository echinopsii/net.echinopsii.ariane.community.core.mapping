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
        'taitale-datacenter-hat',
        'taitale-helper'
    ],
    function (params,dictionary,datacenterMatrix,datacenterSplitter,datacenterHat,helper) {
        function datacenter(geoDCLoc_, mapSplitter, registries, options) {
            //noinspection JSUnusedLocalSymbols
            var helper_     = new helper();
            this.r          = null;
            this.topLeftX   = 0;
            this.topLeftY   = 0;
            this.dcwidth    = 0;
            this.dcheight   = 0;
            this.areaSpan   = params.dc_areaSpan;
            this.dbrdSpan   = params.dc_dbrdSpan;
            //this.dbrdResz   = params.dc_dbrdResz;
            this.geoDCLoc   = geoDCLoc_;
            this.dic        = new dictionary();
            this.msplitter  = mapSplitter;
            this.isInserted = false;
            this.dispDC     = false;

            this.dcsplitter = null;
            this.dcmatrix   = new datacenterMatrix(this.msplitter, registries, options);

            this.dcR    = null;
            this.dcTown = null;
            this.rect   = null;

            this.isMoving   = false;
            this.isEditing  = false;
            this.rightClick = false;

            this.oUnselected = params.dc_opacUnselec;
            this.oSelected   = params.dc_opacSelec;
            this.sDasharray  = params.dc_strokeDasharray;
            this.sWidth      = params.dc_strokeWidthShow;
            this.color       = params.dc_color;


            this.dcName = this.geoDCLoc.dc;
            this.dcHat  = new datacenterHat(this.geoDCLoc.dc, params.dc_txtTitle, this.color);

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.dc_menuFillColor;
            this.menuOpacity       = params.dc_menuOpacity;
            this.menuStrokeWidth   = params.dc_menuStrokeWidth;
            this.menuHided         = true;

            this.menuMainTitleTXT  = params.dc_menuMainTitle;
            this.menuFieldTXT      = params.dc_menuFields;
            this.menuFieldTXTOver  = params.dc_menuFieldsOver;

            this.menuEditionMode         = null;
            this.menuEditionModeRect     = null;
            this.menuFieldStartEditTitle = "Edition mode ON";
            this.menuFieldStopEditTitle  = "Edition mode OFF";

            this.mvx = 0;
            this.mvy = 0;

            var dcRef = this;

            var mouseDown = function(e) {
                    if (e.which == 3) {
                        if (dcRef.menuHided) {
                            dcRef.rect.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": dcRef.sWidth}, 1);
                            dcRef.dcR.show();
                            dcRef.dcsplitter.show();
                            dcRef.dispDC = true;

                            dcRef.rectTopMiddleX = dcRef.topLeftX + dcRef.dcwidth/2;
                            dcRef.rectTopMiddleY = dcRef.topLeftY;
                            dcRef.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = dcRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    dcRef.menuSet[i].attr({"x": dcRef.rectTopMiddleX, "y": dcRef.rectTopMiddleY +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = dcRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": dcRef.rectTopMiddleX - fieldRectWidth/2, "y": dcRef.rectTopMiddleY+30 - fieldRectHeight/2});
                                    dcRef.menuSet[i+1].attr({"x": dcRef.rectTopMiddleX, "y": dcRef.rectTopMiddleY+30});
                                    if (dcRef.isEditing) dcRef.menuSet[i+1].attr({text: dcRef.menuFieldStopEditTitle});
                                    else dcRef.menuSet[i+1].attr({text: dcRef.menuFieldStartEditTitle});
                                    i++;
                                } else {
                                    fieldRect = dcRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": dcRef.rectTopMiddleX - fieldRectWidth/2, "y": dcRef.rectTopMiddleY+30+(i-2)*15 - fieldRectHeight/2});
                                    dcRef.menuSet[i+1].attr({"x": dcRef.rectTopMiddleX, "y": dcRef.rectTopMiddleY+30+(i-2)*15});
                                    i++;
                                }
                            }
                            if (dcRef.menu != null)
                                dcRef.menu.remove();
                            dcRef.menu = dcRef.r.menu(dcRef.rectTopMiddleX,dcRef.rectTopMiddleY+10,dcRef.menuSet).
                                attr({fill: dcRef.menuFillColor, stroke: dcRef.color, "stroke-width": dcRef.menuStrokeWidth,
                                    "fill-opacity": dcRef.menuOpacity});
                            dcRef.menu.mousedown(menuMouseDown);
                            dcRef.menu.toFront();
                            dcRef.menuSet.toFront();
                            dcRef.menuSet.show();
                            dcRef.menuHided=false;
                        } else {
                            if (!dcRef.isEditing) {
                                dcRef.rect.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": 0}, 0);
                                dcRef.dcR.hide();
                                dcRef.dcsplitter.hide();
                                dcRef.dcHat.hide();
                                dcRef.dispDC = false;
                            }
                            dcRef.menu.toBack();
                            dcRef.menuSet.toBack();
                            dcRef.menu.hide();
                            dcRef.menuSet.hide();
                            dcRef.menuHided=true;
                        }
                        dcRef.rightClick=true;
                        if (dcRef.r.getDisplayMainMenu())
                            dcRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        dcRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        if (!dcRef.isEditing) {
                            dcRef.rect.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": 0}, 0);
                            dcRef.dcR.hide();
                            dcRef.dcsplitter.hide();
                            dcRef.dcHat.hide();
                            dcRef.dispDC = false;
                        }
                        dcRef.menu.toBack();
                        dcRef.menuSet.toBack();
                        dcRef.menu.hide();
                        dcRef.menuSet.hide();
                        dcRef.menuHided=true;
                        dcRef.rightClick=true;
                        if (dcRef.r.getDisplayMainMenu())
                            dcRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        dcRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(dcRef.menuFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(dcRef.menuFieldTXT);
                };

            var dcDragg = function () {
                    dcRef.moveInit();
                },
                dcMove = function (dx, dy) {
                    dcRef.r.move(dx,dy);
                    dcRef.r.safari();
                },
                dcUP = function () {
                    dcRef.r.up();
                },
                dcOver = function () {
                    if (!dcRef.dispDC && !dcRef.isMoving && !dcRef.isEditing) {
                        this.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": dcRef.sWidth}, 1);
                        dcRef.dcR.show();
                        dcRef.dcsplitter.show();
                        dcRef.dcHat.show();
                    }
                },
                dcOut  = function () {
                    if (!dcRef.dispDC && !dcRef.isMoving && !dcRef.isEditing) {
                        this.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": 0}, 1);
                        dcRef.dcR.hide();
                        dcRef.dcsplitter.hide();
                        dcRef.dcHat.hide();
                    }
                };

            this.isEditionMode = function() {
                return this.isEditing;
            };

            this.show = function() {
                this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": this.sWidth}, 1);
                this.dcR.show();
                this.dcsplitter.show();
                this.dcHat.show();
            };

            this.pushContainerArea = function(container) {
                this.dcmatrix.addContainerArea(container);
            };

            this.defineZoneMaxSize = function() {
                this.dcmatrix.defineDCContentMaxSize();

                var contentDCSize = this.dcmatrix.getDCContentMaxSize();
                this.dcwidth  = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().x-1)*this.areaSpan + contentDCSize.width;
                this.dcheight = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().y-1)*this.areaSpan + contentDCSize.height;
            };

            this.defineZoneSize = function() {
                this.dcmatrix.defineDCContentSize();

                var contentDCSize = this.dcmatrix.getDCContentSize();
                this.dcwidth  = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().x-1)*this.areaSpan + contentDCSize.width;
                this.dcheight = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().y-1)*this.areaSpan + contentDCSize.height;
            };

            this.defineZoneObjectsMaxSize = function() {
                this.dcmatrix.defineMtxAreaMaxSize();
            };

            this.defineZoneObjectsSize = function() {
                this.dcmatrix.defineMtxAreaSize();
            };

            this.defineFirstPoz = function() {
                this.dcmatrix.defineMtxAreaFirstPoz(this.topLeftX,this.topLeftY,this.dcwidth,this.dbrdSpan,this.areaSpan);
            };

            this.optimizeMtxCoord = function() {
                this.dcmatrix.optimizeAreaMtxCoord();
            };

            this.defineIntermediatePoz = function() {
                this.dcmatrix.defineMtxAreaIntermediatePoz(this.topLeftX,this.topLeftY,this.dcwidth,this.dbrdSpan,this.areaSpan);
                this.dcsplitter = new datacenterSplitter(this);
                this.dcsplitter.definePoz();
            };

            this.defineFinalPoz = function() {
                this.dcmatrix.defineMtxAreaFinalPoz(this.topLeftX,this.topLeftY,this.dcwidth,this.dbrdSpan,this.areaSpan);
                this.dcsplitter = new datacenterSplitter(this);
                this.dcsplitter.definePoz();
            };

            this.getGeoDCLoc = function() {
                return this.geoDCLoc;
            };

            this.setTopLeftCoord = function (x, y) {
                this.topLeftX = x;
                this.topLeftY = y;
            };

            this.geoDCLocEqual = function (geoDCLoc_) {
                return (this.geoDCLoc.dc==geoDCLoc_.dc);
            };

            this.getZoneMaxSize = function() {
                return {
                    width  : this.dcwidth,
                    height : this.dcheight
                };
            };

            this.getZoneSize = function() {
                return {
                    width  : this.dcwidth,
                    height : this.dcheight
                };
            };

            this.getZoneCoord = function() {
                return {
                    x : this.topLeftX,
                    y : this.topLeftY
                }
            };

            this.print = function(r_) {
                this.r      = r_;
                this.dcR    = this.r.set();
                this.dcHat.print(this.r, this.topLeftX + (this.dcwidth/2), this.topLeftY + this.dbrdSpan/5);
                //noinspection JSUnresolvedVariable
                this.dcTown = this.r.text(this.topLeftX + (this.dcwidth/2), this.topLeftY + this.dcheight - this.dbrdSpan/2, this.geoDCLoc.town);
                this.rect   = this.r.rect(this.topLeftX, this.topLeftY, this.dcwidth, this.dcheight, 0);

                this.dcHat.mousedown(mouseDown);
                this.dcHat.drag(dcMove, dcDragg, dcUP);
                this.dcTown.attr(params.dc_txtTitle).attr({'fill':this.color});
                this.dcTown.mousedown(mouseDown);
                this.dcR.push(this.dcTown);
                this.dcR.hide();
                this.dcHat.hide();

                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                this.rect.mousedown(mouseDown);
                this.rect.drag(dcMove, dcDragg, dcUP);
                this.rect.mouseover(dcOver);
                this.rect.mouseout(dcOut);

                this.dcmatrix.printMtx(this.r);
                this.dcsplitter.print(this.r);

                this.menuTitle = this.r.text(0,10,"Datacenter menu").attr(this.menuMainTitleTXT);

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

            this.displayDC = function(display) {
                this.dispDC = display;
                if (this.dispDC) {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": this.sWidth}, 1);
                    this.dcR.show();
                    this.dcsplitter.show();
                    this.dcHat.show();
                } else {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": 0}, 1);
                    this.dcR.hide();
                    this.dcsplitter.hide();
                    this.dcHat.hide();
                }
            };

            this.displayArea = function(display) {
                this.dcmatrix.displayArea(display);
            };

            this.displayLan = function(display) {
                this.dcmatrix.displayLan(display);
            };

            this.isElemMoving = function() {
                if (this.dcmatrix.isElemMoving()) return true;
                else return this.isMoving;
            };

            this.changeInit = function() {
                if (!this.menuHided && !this.isMoving) {
                    this.menu.toBack();
                    this.menuSet.toBack();
                    this.menu.hide();
                    this.menuSet.hide();
                    this.menuHided=true;
                    if (this.r.getDisplayMainMenu())
                        this.r.setDisplayMainMenu(false);
                }

                this.extrx = this.rect.attr("x");
                this.extry = this.rect.attr("y");
                //noinspection JSUnusedGlobalSymbols
                this.extrw = this.rect.attr("width");
                //noinspection JSUnusedGlobalSymbols
                this.extrh = this.rect.attr("height");
                this.extt1x = this.dcTown.attr("x");
                this.extt1y = this.dcTown.attr("y");
            };

            this.changeUp = function() {
                var i, ii;
                this.mvx = 0; this.mvy = 0;

                this.setTopLeftCoord(this.rect.attr("x"),this.rect.attr("y"));
                if (this.dcmatrix.getWanMtxSize()!=0) {
                    this.dcsplitter.wanLineTopY = this.topLeftY + this.dbrdSpan;
                    this.dcsplitter.manLineTopY = this.dcsplitter.wanLineTopY+this.dcsplitter.wanLineHeight;
                    this.dcsplitter.lanLineTopY = this.dcsplitter.manLineTopY+this.dcsplitter.manLineHeight;
                } else if (this.dcmatrix.getManMtxSize()!=0) {
                    this.dcsplitter.manLineTopY = this.topLeftY + this.dbrdSpan;
                    this.dcsplitter.lanLineTopY = this.dcsplitter.manLineTopY+this.dcsplitter.manLineHeight;
                } else
                    this.dcsplitter.lanLineTopY = this.topLeftY + this.dbrdSpan;
                this.dcsplitter.lanLineBdrY = this.topLeftY+this.dcheight-this.dbrdSpan;


                this.dcsplitter.move(this.r);

                var mtxS = this.dcmatrix.getWanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++) {
                    this.dcmatrix.getAreaFromWanMtx(i).setMoveJail(this.topLeftX+this.dbrdSpan,this.dcsplitter.wanLineTopY,
                        this.topLeftX+this.dcwidth-this.dbrdSpan,this.dcsplitter.manLineTopY);
                }

                mtxS = this.dcmatrix.getManMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++) {
                    this.dcmatrix.getAreaFromManMtx(i).setMoveJail(this.topLeftX+this.dbrdSpan,this.dcsplitter.manLineTopY,
                        this.topLeftX+this.dcwidth-this.dbrdSpan,this.dcsplitter.lanLineTopY);
                }

                mtxS = this.dcmatrix.getLanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++) {
                    this.dcmatrix.getAreaFromLanMtx(i).setMoveJail(this.topLeftX+this.dbrdSpan,this.dcsplitter.lanLineTopY,
                        this.topLeftX+this.dcwidth-this.dbrdSpan,this.dcsplitter.lanLineBdrY);
                }

            };

            //MOVEABLE

            this.moveInit = function() {
                if (!this.rightClick) {
                    if (this.isEditing)
                        this.r.scaleDone(this);

                    var mtxS, i, ii;

                    this.r.dcsOnMovePush(this);
                    this.r.moveSetPush(this.dcTown);
                    this.r.moveSetPush(this.rect);

                    mtxS = this.dcmatrix.getWanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++)
                        this.dcmatrix.getAreaFromWanMtx(i).moveInit();
                    mtxS = this.dcmatrix.getManMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++)
                        this.dcmatrix.getAreaFromManMtx(i).moveInit();
                    mtxS = this.dcmatrix.getLanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++)
                        this.dcmatrix.getAreaFromLanMtx(i).moveInit();

                    this.changeInit();

                    this.isMoving = true;
                    dcRef.rect.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": dcRef.sWidth}, 1);
                    dcRef.dcR.show();
                    this.dcsplitter.hide();
                    this.rect.animate({"fill-opacity": this.oSelected}, 500);
                }
            };

            this.moveAction = function(dx, dy) {
                this.mvx = dx; this.mvy = dy;
                this.dcHat.move(this.r, this.extrx + dx + (this.dcwidth/2), this.extry + dy + this.dbrdSpan/5);
                this.dcHat.mousedown(mouseDown);
                this.dcHat.drag(dcMove, dcDragg, dcUP);
                this.dcHat.toBack();
            };

            this.moveUp = function() {
                if (!this.rightClick) {
                    var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy},
                        attrtxt0 = {x: this.extt0x + this.mvx, y: this.extt0y + this.mvy},
                        attrtxt1 = {x: this.extt1x + this.mvx, y: this.extt1y + this.mvy};

                    this.rect.attr(attrect);
                    this.dcTown.attr(attrtxt1);

                    this.changeUp();

                    this.dcsplitter.show();
                    this.rect.animate({"fill-opacity": this.oUnselected}, 500);
                    this.isMoving = false;

                    if (this.isEditing)
                        this.r.scaleInit(this);
                }
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

                var mtxS, i, ii;

                mtxS = this.dcmatrix.getWanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    this.dcmatrix.getAreaFromWanMtx(i).setEditionMode(editionMode);
                mtxS = this.dcmatrix.getManMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    this.dcmatrix.getAreaFromManMtx(i).setEditionMode(editionMode);
                mtxS = this.dcmatrix.getLanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    this.dcmatrix.getAreaFromLanMtx(i).setEditionMode(editionMode);
            };

            this.menuFieldEditClick = function() {
                dcRef.menu.toBack();
                dcRef.menuSet.toBack();
                dcRef.menu.hide();
                dcRef.menuSet.hide();
                dcRef.menuHided=true;

                if (!dcRef.isEditing) {
                    dcRef.r.scaleInit(dcRef);
                    dcRef.isEditing = true;
                    dcRef.dcsplitter.show();
                    dcRef.dcHat.show();
                } else {
                    dcRef.r.scaleDone(dcRef);
                    dcRef.isEditing = false;
                    dcRef.rect.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": 0}, 0);
                    dcRef.dcR.hide();
                    dcRef.dcsplitter.hide();
                    dcRef.dcHat.hide();
                    dcRef.dispDC = false;
                }
            };

            this.getBBox = function() {
                return this.rect.getBBox();
            };

            var areaSet;
            this.getMinBBox = function() {
                var i, ii;

                //noinspection JSUnresolvedVariable
                var nameHeight = this.geoDCLoc.dc.height(params.dc_txtTitle),
                    townHeight = this.geoDCLoc.town.height(params.dc_txtTitle);

                areaSet = this.r.set();

                var mtxS = this.dcmatrix.getWanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    areaSet.push(this.dcmatrix.getAreaFromWanMtx(i).rect);
                mtxS = this.dcmatrix.getManMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    areaSet.push(this.dcmatrix.getAreaFromManMtx(i).rect);
                mtxS = this.dcmatrix.getLanMtxSize();
                for (i = 0, ii =  mtxS; i < ii; i++)
                    areaSet.push(this.dcmatrix.getAreaFromLanMtx(i).rect);

                var areaBBox = areaSet.getBBox();

                return {
                    x: areaBBox.x - this.dbrdSpan ,
                    y: areaBBox.y - (this.dbrdSpan + this.areaSpan),
                    x2: areaBBox.x2 + this.dbrdSpan,
                    y2: areaBBox.y2  + this.dbrdSpan + this.areaSpan,
                    width: areaBBox.width + 2*this.dbrdSpan,
                    height: areaBBox.height + (this.dbrdSpan + this.areaSpan)
                };
            };

            this.getMaxBBox = function() {
                return null
            };

            this.editInit = function() {
                this.extwidth  = this.dcwidth;
                this.extheight = this.dcheight;
                this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight;
                this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight;
                this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight;
                this.dcsplitter.hide();
                this.dcHat.hide();
                this.changeInit();
                this.isMoving = true;
            };

            this.editAction = function(elem, dx, dy) {
                switch(elem.idx) {
                    case 0:
                        this.extrx = this.topLeftX + dx;
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.dcwidth - dx;
                        this.extheight = this.dcheight - dy;
                        if (this.dcmatrix.getWanMtxSize()!=0)
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight - dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight - dy;
                        else
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight - dy;
                        break;

                    case 1:
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.dcwidth + dx;
                        this.extheight = this.dcheight - dy;
                        if (this.dcmatrix.getWanMtxSize()!=0)
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight - dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight - dy;
                        else
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight - dy;
                        break;

                    case 2:
                        this.extwidth = this.dcwidth + dx;
                        this.extheight = this.dcheight + dy;
                        if (this.dcmatrix.getLanMtxSize()!=0)
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight + dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight + dy;
                        else
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight + dy;
                        break;

                    case 3:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.dcwidth - dx;
                        this.extheight = this.dcheight + dy;
                        if (this.dcmatrix.getLanMtxSize()!=0)
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight + dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight + dy;
                        else
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight + dy;
                        break;

                    case 4:
                        this.extry = this.topLeftY + dy;
                        this.extheight = this.dcheight - dy;
                        if (this.dcmatrix.getWanMtxSize()!=0)
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight - dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight - dy;
                        else
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight - dy;
                        break;

                    case 5:
                        this.extwidth = this.dcwidth + dx;
                        break;

                    case 6:
                        this.extheight = this.dcheight + dy;
                        if (this.dcmatrix.getLanMtxSize()!=0)
                            this.dcsplitter.extlanLineHeight = this.dcsplitter.lanLineHeight + dy;
                        else if (this.dcmatrix.getManMtxSize()!=0)
                            this.dcsplitter.extmanLineHeight = this.dcsplitter.manLineHeight + dy;
                        else
                            this.dcsplitter.extwanLineHeight = this.dcsplitter.wanLineHeight + dy;
                        break;

                    case 7:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.dcwidth - dx;
                        break;

                    default:
                        break;
                }

                this.dcR.pop(this.dcTown);

                this.dcTown.remove();
                this.rect.remove();

                //noinspection JSUnresolvedVariable
                this.dcTown = this.r.text(this.extrx + (this.extwidth/2), this.extry + this.extheight - this.dbrdSpan/2, this.geoDCLoc.town);
                this.rect   = this.r.rect(this.extrx, this.extry, this.extwidth, this.extheight, 0);

                this.dcTown.attr(params.dc_txtTitle).attr({'fill':this.color});
                this.dcTown.mousedown(mouseDown);
                this.dcR.push(this.dcTown);
                this.dcR.toBack();

                this.dcHat.move(this.r, this.extrx + (this.extwidth/2), this.extry + this.dbrdSpan/5);
                this.dcHat.mousedown(mouseDown);
                this.dcHat.drag(dcMove, dcDragg, dcUP);

                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": this.sWidth});
                this.rect.drag(dcMove, dcDragg, dcUP);
                this.rect.mouseover(dcOver);
                this.rect.mouseout(dcOut);
                this.rect.mousedown(mouseDown);
                this.rect.toBack();
            };

            this.editUp = function() {
                this.dcwidth = this.extwidth;
                this.dcheight = this.extheight;
                this.dcsplitter.wanLineHeight = this.dcsplitter.extwanLineHeight;
                this.dcsplitter.manLineHeight = this.dcsplitter.extmanLineHeight;
                this.dcsplitter.lanLineHeight = this.dcsplitter.extlanLineHeight;
                this.dcsplitter.show();
                this.dcHat.show();
                this.isMoving = false;
                this.changeUp();
            };
        }

        return datacenter;
    });