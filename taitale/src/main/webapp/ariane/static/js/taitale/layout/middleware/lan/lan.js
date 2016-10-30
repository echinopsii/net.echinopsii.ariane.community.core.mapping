// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - MDW module - Lan                            │ \\
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
        'taitale-lan-matrix',
        'taitale-lan-hat'
    ],
    function (params,helper,dictionary,lanMatrix, lanHat) {
        function lan(lanDef_,options_) {
            this.r          = null;
            this.topLeftX   = 0;
            this.topLeftY   = 0;
            this.lanwidth   = 0;
            this.lanheight  = 0;
            this.contSpan   = params.lan_contSpan; /*space between 2 container*/
            this.lbrdSpan   = params.lan_lbrdSpan; /*space between 1 container and lan border*/
            //this.lbrdResz   = params.lan_lbrdResz;
            this.lanmatrix  = new lanMatrix();
            this.lanDef     = lanDef_;
            this.dic        = new dictionary();
            this.options    = options_;
            //this.helper_    = new helper();
            this.isInserted = false;
            this.dispLan    = false;
            this.dispLanOD  = false;
            this.layoutData = null;

            this.lanR    = null;
            this.rect    = null;

            this.minJailX       = 0;
            this.minJailY       = 0;
            this.maxJailX       = 0;
            this.maxJailY       = 0;
            this.hasMoveHdl     = false;
            this.isJailed       = false;
            this.isMoving       = false;
            this.isEditing      = false;
            this.rightClick     = false;

            this.oUnselected = params.lan_opacUnselec;
            this.oSelected   = params.lan_opacSelec;
            this.sDasharray  = params.lan_strokeDasharray;
            this.color       = params.lan_color;
            this.sWidth      = params.lan_strokeWidthShow;

            this.lanName        = this.lanDef.sname;
            this.lanNameHat     = "Lan " + this.lanDef.sname + " - " + this.lanDef.sip + "/" + this.lanDef.smask;

            this.lanHat  = new lanHat(this.lanNameHat, params.lan_txtTitle, this.color);

            this.mvx = 0;
            this.mvy = 0;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.lan_menuFillColor;
            this.menuOpacity       = params.lan_menuOpacity;
            this.menuStrokeWidth   = params.lan_menuStrokeWidth;
            this.menuHided         = true;

            this.menuMainTitleTXT  = params.lan_menuMainTitle;
            this.menuFieldTXT      = params.lan_menuFields;
            this.menuFieldTXTOver  = params.lan_menuFieldsOver;

            this.menuEditionMode     = null;
            this.menuEditionModeRect = null;
            this.menuFieldStartEditTitle  = "Edition mode ON";
            this.menuFieldStopEditTitle   = "Edition mode OFF";

            var lanRef = this;

            var reDefineRectPoints = function(x, y) {
                    lanRef.topLeftX = x;
                    lanRef.topLeftY = y;
                    //helper_.debug("[lan.reDefineRectPoints] { topLeftX: ".concat(topLeftX).concat(", topLeftY: ").concat(topLeftY).concat(" }"));
                };

            var mouseDown = function(e) {
                    if (e.which == 3) {
                        if (lanRef.menuHided) {
                            lanRef.rect.animate({"fill-opacity": lanRef.oUnselected, "stroke-width": lanRef.sWidth}, 1);
                            lanRef.lanR.show();
                            lanRef.lanHat.show();
                            lanRef.dispLan = true;

                            lanRef.rectTopMiddleX = lanRef.topLeftX + lanRef.lanwidth/2;
                            lanRef.rectTopMiddleY = lanRef.topLeftY;
                            lanRef.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = lanRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    lanRef.menuSet[i].attr({"x": lanRef.rectTopMiddleX, "y": lanRef.rectTopMiddleY +16, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = lanRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": lanRef.rectTopMiddleX - fieldRectWidth/2, "y": lanRef.rectTopMiddleY+41 - fieldRectHeight/2});
                                    lanRef.menuSet[i+1].attr({"x": lanRef.rectTopMiddleX, "y": lanRef.rectTopMiddleY+41});
                                    if (lanRef.isEditing) lanRef.menuSet[i+1].attr({text: lanRef.menuFieldStopEditTitle});
                                    else lanRef.menuSet[i+1].attr({text: lanRef.menuFieldStartEditTitle});
                                    i++;
                                } else {
                                    fieldRect = lanRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": lanRef.rectTopMiddleX - fieldRectWidth/2, "y": lanRef.rectTopMiddleY+41+(i-2)*15 - fieldRectHeight/2});
                                    lanRef.menuSet[i+1].attr({"x": lanRef.rectTopMiddleX, "y": lanRef.rectTopMiddleY+41+(i-2)*15});
                                    i++;
                                }
                            }
                            if (lanRef.menu != null)
                                lanRef.menu.remove();
                            lanRef.menu = lanRef.r.menu(lanRef.menuSet).
                                attr({fill: lanRef.menuFillColor, stroke: lanRef.color, "stroke-width": lanRef.menuStrokeWidth,
                                    "fill-opacity": lanRef.menuOpacity});
                            lanRef.menu.mousedown(menuMouseDown);
                            lanRef.menu.toFront();
                            lanRef.menuSet.toFront();
                            lanRef.menuSet.show();
                            lanRef.menuHided=false;
                        } else {
                            if (!lanRef.isEditing && !lanRef.dispLanOD)
                                lanRef.dispLan = false;
                            lanRef.menu.toBack();
                            lanRef.menuSet.toBack();
                            lanRef.menu.hide();
                            lanRef.menuSet.hide();
                            lanRef.menuHided=true;
                        }
                        lanRef.rightClick=true;
                        if (lanRef.r.getDisplayMainMenu())
                            lanRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        lanRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        if (!lanRef.isEditing && !lanRef.dispLanOD)
                            lanRef.dispLan = false;
                        lanRef.menu.toBack();
                        lanRef.menuSet.toBack();
                        lanRef.menu.hide();
                        lanRef.menuSet.hide();
                        lanRef.menuHided=true;
                        lanRef.rightClick=true;
                        if (lanRef.r.getDisplayMainMenu())
                            lanRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        lanRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(lanRef.menuFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(lanRef.menuFieldTXT);
                };

            var lanDragg = function () {
                    lanRef.hasMoveHdl = true;
                    lanRef.moveInit();
                    lanRef.rect.animate({"fill-opacity": lanRef.oSelected}, 500);
                },
                lanMove = function (dx, dy) {
                    var zoomedMoveCoord = lanRef.r.getZPDZoomedMoveCoord(dx, dy);
                    dx = zoomedMoveCoord.dx; dy = zoomedMoveCoord.dy;

                    var rx  = lanRef.extrx,
                        ry  = lanRef.extry;
                    var minTopLeftX = lanRef.minTopLeftX,
                        minTopLeftY = lanRef.minTopLeftY,
                        maxTopLeftX = lanRef.maxJailX - lanRef.lanwidth,
                        maxTopLeftY = lanRef.maxJailY - lanRef.lanheight;

                    //helper_.debug("[lan.lMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    //helper_.debug("[lan.lMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    //helper_.debug("[lan.lMove] { minTopLeftX: ".concat(minTopLeftX).concat(", minTopLeftY: ").concat(minTopLeftY).concat(", maxTopLeftX: ").concat(maxTopLeftX).concat(", maxTopLeftY: ").concat(maxTopLeftY).concat(" }"));

                    if (lanRef.isJailed) {
                        if (minTopLeftX > rx + dx)
                            dx = minTopLeftX - rx;
                        if (minTopLeftY > ry + dy)
                            dy = minTopLeftY - ry;
                        if (maxTopLeftX < rx + dx)
                            dx = maxTopLeftX - rx;
                        if (maxTopLeftY < ry + dy)
                            dy = maxTopLeftY - ry;
                    }
                    lanRef.r.move(dx,dy);
                    lanRef.r.safari();
                },
                lanUP = function () {
                    lanRef.r.up();
                    lanRef.rect.animate({"fill-opacity": lanRef.oUnselected}, 500);
                    lanRef.hasMoveHdl = false;
                },
                lanOver = function () {
                    if (!lanRef.dispLan  && !lanRef.isMoving && !lanRef.isEditing) {
                        lanRef.rect.animate({"fill-opacity": lanRef.oUnselected, "stroke-width": lanRef.sWidth}, 1);
                        lanRef.lanR.show();
                        lanRef.lanHat.show();
                    }
                },
                lanOut  = function () {
                    if (!lanRef.dispLan && !lanRef.isMoving && !lanRef.isEditing) {
                        lanRef.rect.animate({"fill-opacity": lanRef.oUnselected, "stroke-width": 0}, 1);
                        lanRef.lanR.hide();
                        lanRef.lanHat.hide();
                    }
                };

            this.pushContainer = function(container) {
                this.lanmatrix.addContainer(container);
            };

            this.defineMaxSize = function() {
                this.lanmatrix.defineLanContentMaxSize();

                var contentLanSize = this.lanmatrix.getLanContentMaxSize();
                this.lanwidth  = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().y)*this.contSpan + contentLanSize.width;
                this.lanheight = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().x)*this.contSpan + contentLanSize.height;
            };

            this.defineSize = function() {
                this.lanmatrix.defineLanContentSize();

                var contentLanSize = this.lanmatrix.getLanContentSize();
                this.lanwidth  = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().y)*this.contSpan + contentLanSize.width;
                this.lanheight = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().x)*this.contSpan + contentLanSize.height;
            };

            this.defineFirstPoz = function() {
                this.lanmatrix.defineMtxContainerFirstPoz(this.topLeftX, this.topLeftY, this.lbrdSpan, this.contSpan, this.lanwidth, this.lanheight);
            };

            this.optimizeMtxCoord = function() {
                this.lanmatrix.optimizeContainerMtxCoord();
            };

            this.defineIntermediatePoz = function() {
                this.lanmatrix.defineMtxContainerIntermediatePoz(this.topLeftX, this.topLeftY, this.lbrdSpan, this.contSpan, this.lanwidth, this.lanheight);
            };

            this.defineFinalPoz = function() {
                this.lanmatrix.defineMtxContainerFinalPoz(this.topLeftX, this.topLeftY, this.lbrdSpan, this.contSpan, this.lanwidth, this.lanheight);
            };

            this.getLanMaxSize = function() {
                return {
                    width  : this.lanwidth,
                    height : this.lanheight
                };
            };

            this.getLanSize = function() {
                return {
                    width  : this.lanwidth,
                    height : this.lanheight
                };
            };

            this.setTopLeftCoord = function(x,y) {
                reDefineRectPoints(x,y);
            };

            this.getLanCoords = function() {
                return {
                    x: this.topLeftX,
                    y: this.topLeftY
                }
            };

            this.defEqual = function(lanDef_) {
                return (
                        this.lanDef.pname===lanDef_.pname &&
                        this.lanDef.ratype===lanDef_.ratype &&
                        this.lanDef.raname===lanDef_.raname &&
                        this.lanDef.sname===lanDef_.sname &&
                        this.lanDef.sip===lanDef_.sip &&
                        this.lanDef.smask===lanDef_.smask
                    );
            };

            this.setMoveJail = function(minJailX_, minJailY_, maxJailX_, maxJailY_) {
                if (minJailX_!=null) this.minJailX = minJailX_;
                if (minJailY_!=null) this.minJailY = minJailY_;
                if (maxJailX_!=null) this.maxJailX = maxJailX_;
                if (maxJailY_!=null) this.maxJailY = maxJailY_;
                this.isJailed = true;
            };

            this.setLayoutData = function(data) {
                if (this.layoutData!=null) {
                    this.layoutData.isConnectedToLeftDC = this.layoutData.isConnectedToLeftDC || data.isConnectedToLeftDC;
                    this.layoutData.isConnectedToRightDC = this.layoutData.isConnectedToRightDC || data.isConnectedToRightDC;
                    this.layoutData.isConnectedToLeftArea = this.layoutData.isConnectedToLeftArea || data.isConnectedToLeftArea;
                    this.layoutData.isConnectedToRightArea = this.layoutData.isConnectedToRightArea || data.isConnectedToRightArea;
                    this.layoutData.isConnectedToLeftLan = this.layoutData.isConnectedToLeftLan || data.isConnectedToLeftLan;
                    this.layoutData.isConnectedToRightLan = this.layoutData.isConnectedToRightLan || data.isConnectedToRightLan;
                    this.layoutData.isConnectedToUpArea = this.layoutData.isConnectedToUpArea || data.isConnectedToUpArea;
                    this.layoutData.isConnectedToDownArea = this.layoutData.isConnectedToDownArea || data.isConnectedToDownArea;
                    this.layoutData.isConnectedToUpLan = this.layoutData.isConnectedToUpLan || data.isConnectedToUpLan;
                    this.layoutData.isConnectedToDownLan = this.layoutData.isConnectedToDownLan || data.isConnectedToDownLan;
                    this.layoutData.isConnectedInsideArea = this.layoutData.isConnectedInsideArea || data.isConnectedInsideArea;
                    this.layoutData.isConnectedInsideLan = this.layoutData.isConnectedInsideLan || data.isConnectedInsideLan;
                } else {
                    this.layoutData = data;
                }
            };

            this.print = function(r_) {
                this.r = r_;
                var lanTitle = "Lan " + this.lanDef.sname + " - " + this.lanDef.sip + "/" + this.lanDef.smask;

                this.lanR    = this.r.set();
                this.lanHat.print(this.r, this.topLeftX + (this.lanwidth/2), this.topLeftY + this.lbrdSpan/5);
                this.rect    = this.r.rect(this.topLeftX, this.topLeftY, this.lanwidth, this.lanheight, 0);

                this.lanHat.mousedown(mouseDown);
                this.lanHat.drag(lanMove, lanDragg, lanUP);
                this.lanR.push(this.lanName);
                this.lanR.hide();
                this.lanHat.hide();

                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                if (this.lanDef.dcname!=null && this.lanDef.dcname.indexOf(this.dic.networkType.GLI) === -1) {
                    this.rect.mousedown(mouseDown);
                    this.rect.drag(lanMove, lanDragg, lanUP);
                    this.rect.mouseover(lanOver);
                    this.rect.mouseout(lanOut);
                }

                this.menuTitle = this.r.text(0,10,"Lan menu").attr(this.menuMainTitleTXT);

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

            this.toFront = function() {
                this.rect.toFront();
                this.lanR.toFront();

                var i, ii, j, jj;
                var mtxX = this.lanmatrix.getMtxSize().x,
                    mtxY = this.lanmatrix.getMtxSize().y;

                for (i = 0, ii =  mtxX; i < ii; i++)
                    for (j = 0, jj =  mtxY; j < jj; j++) {
                        var container = this.lanmatrix.getContainerFromMtx(i, j);
                        if (container != null)
                            container.toFront();
                    }

            };

            this.displayLan = function(display) {
                this.dispLan=display;
                this.dispLanOD=display;
                if (this.dispLan) {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": this.sWidth}, 1);
                    this.lanR.show();
                    this.lanHat.show();
                } else {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": 0}, 1);
                    this.lanR.hide();
                    this.lanHat.hide();
                }
            };

            this.changeInit = function() {
                this.extrx  = this.rect.attr("x");
                this.extry  = this.rect.attr("y");
                //noinspection JSUnusedGlobalSymbols
                this.extrw  = this.rect.attr("width");
                //noinspection JSUnusedGlobalSymbols
                this.extrh  = this.rect.attr("height");
                this.minTopLeftX = this.minJailX;
                this.minTopLeftY = this.minJailY;
                this.maxTopLeftX = this.maxJailX - this.lanwidth;
                this.maxTopLeftY = this.maxJailY - this.lanheight;

                this.isMoving = true;
            };

            this.changeUp = function() {
                var j, jj, k, kk;

                var mtxX = this.lanmatrix.getMtxSize().x,
                    mtxY = this.lanmatrix.getMtxSize().y;

                this.setTopLeftCoord(this.rect.attr("x"),this.rect.attr("y"));

                for (j = 0, jj = mtxX; j < jj; j++)
                    for (k = 0, kk = mtxY; k < kk; k++) {
                        var container = this.lanmatrix.getContainerFromMtx(j, k);
                        if (container != null) {
                            container.setMoveJail(
                                this.topLeftX,
                                this.topLeftY+this.lbrdSpan,
                                this.topLeftX+this.lanwidth,
                                this.topLeftY+this.lanheight
                            );
                        }
                    }
            };

            //MOVEABLE

            this.moveInit = function() {
                if (!this.rightClick) {
                    if (this.isEditing)
                        this.r.scaleDone(this);

                    var i, ii, j, jj;
                    var mtxX = this.lanmatrix.getMtxSize().x,
                        mtxY = this.lanmatrix.getMtxSize().y;

                    this.r.lansOnMovePush(this);
                    this.r.moveSetPush(this.rect);

                    for (i = 0, ii =  mtxX; i < ii; i++)
                        for (j = 0, jj =  mtxY; j < jj; j++) {
                            var container = this.lanmatrix.getContainerFromMtx(i, j);
                            if (container!=null)
                                container.moveInit();
                        }

                    if (!this.hasMoveHdl)
                        this.lanHat.hide();

                    this.changeInit();
                }
            };

            this.moveAction = function(dx,dy) {
                this.mvx = dx; this.mvy = dy;
                if (this.hasMoveHdl) {
                    this.lanHat.move(this.r, this.extrx + dx + (this.lanwidth/2), this.extry + dy + this.lbrdSpan/5);
                    this.lanHat.mousedown(mouseDown);
                    this.lanHat.drag(lanMove, lanDragg, lanUP);
                    this.lanHat.toBack();
                }
            };

            this.moveUp = function() {
                if (!this.rightClick) {
                    var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy};

                    this.rect.attr(attrect);
                    if (!this.hasMoveHdl) {
                        this.lanHat.move(this.r, this.extrx + this.mvx + (this.lanwidth/2), this.extry + this.mvy + this.lbrdSpan/5);
                        this.lanHat.mousedown(mouseDown);
                        this.lanHat.drag(lanMove, lanDragg, lanUP);
                        this.lanHat.toBack();
                        if (!this.dispLan)
                            this.lanHat.hide();
                    }
                    this.mvx=0; this.mvy=0;

                    this.changeUp();
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
            };

            this.menuFieldEditClick = function() {
                lanRef.menu.toBack();
                lanRef.menuSet.toBack();
                lanRef.menu.hide();
                lanRef.menuSet.hide();
                lanRef.menuHided=true;

                if (!lanRef.isEditing) {
                    lanRef.r.scaleInit(lanRef);
                    lanRef.isEditing = true;
                } else {
                    lanRef.r.scaleDone(lanRef);
                    lanRef.isEditing = false;
                    if (!lanRef.dispLanOD) {
                        lanRef.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": 0}, 1);
                        lanRef.lanR.hide();
                        lanRef.lanHat.hide();
                        lanRef.dispLan = false;
                    }
                }
            };

            this.getBBox = function() {
                return this.rect.getBBox();
            };

            var lanObjSet;
            this.getMinBBox = function() {
                var j, jj, k, kk;

                var mtxX = this.lanmatrix.getMtxSize().x,
                    mtxY = this.lanmatrix.getMtxSize().y;

                lanObjSet = this.r.set();

                for (j = 0, jj = mtxX; j < jj; j++)
                    for (k = 0, kk = mtxY; k < kk; k++) {
                        var container = this.lanmatrix.getContainerFromMtx(j, k);
                        if (container != null)
                            lanObjSet.push(container.rect);
                    }

                var lanMinBBox = lanObjSet.getBBox();

                return {
                    x: lanMinBBox.x - this.lbrdSpan,
                    y: lanMinBBox.y - this.lbrdSpan,
                    x2: lanMinBBox.x2 + this.lbrdSpan,
                    y2: lanMinBBox.y2 + this.lbrdSpan,
                    width: lanMinBBox.width + 2*this.lbrdSpan,
                    height: lanMinBBox.height + 2*this.lbrdSpan
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
                this.extwidth  = this.lanwidth;
                this.extheight = this.lanheight;
                this.changeInit();
                this.isMoving = true;
            };

            this.editAction = function(elem, dx, dy) {
                switch(elem.idx) {
                    case 0:
                        this.extrx = this.topLeftX + dx;
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.lanwidth - dx;
                        this.extheight = this.lanheight - dy;
                        break;

                    case 1:
                        this.extry = this.topLeftY + dy;
                        this.extwidth = this.lanwidth + dx;
                        this.extheight = this.lanheight - dy;
                        break;

                    case 2:
                        this.extwidth = this.lanwidth + dx;
                        this.extheight = this.lanheight + dy;
                        break;

                    case 3:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.lanwidth - dx;
                        this.extheight = this.lanheight + dy;
                        break;

                    case 4:
                        this.extry = this.topLeftY + dy;
                        this.extheight = this.lanheight - dy;
                        break;

                    case 5:
                        this.extwidth = this.lanwidth + dx;
                        break;

                    case 6:
                        this.extheight = this.lanheight + dy;
                        break;

                    case 7:
                        this.extrx = this.topLeftX + dx;
                        this.extwidth = this.lanwidth - dx;
                        break;

                    default:
                        break;
                }

                this.rect.remove();

                this.rect    = this.r.rect(this.extrx, this.extry, this.extwidth, this.extheight, 0);
                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray,
                    "fill-opacity": this.oUnselected, "stroke-width": this.sWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(lanMove, lanDragg, lanUP);
                this.rect.mouseover(lanOver);
                this.rect.mouseout(lanOut);
                if (this.lanDef.dcname!=null && this.lanDef.dcname.indexOf(this.dic.networkType.GLI) !== -1) this.rect.hide();
                else {
                    this.rect.mousedown(mouseDown);
                    this.rect.drag(lanMove, lanDragg, lanUP);
                    this.rect.mouseover(lanOver);
                    this.rect.mouseout(lanOut);
                }

                this.lanHat.move(this.r, this.extrx + (this.extwidth/2), this.extry + this.lbrdSpan/5);

                this.toFront();
            };

            this.editUp = function() {
                this.lanwidth = this.extwidth;
                this.lanheight = this.extheight;
                this.isMoving = false;
                this.changeUp();
            };
        }
        return lan;
    });
