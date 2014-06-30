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
            this.r          = null;
            this.topLeftX   = 0;
            this.topLeftY   = 0;
            this.lanwidth   = 0;
            this.lanheight  = 0;
            this.contSpan   = params.lan_contSpan; /*space between 2 container*/
            this.lbrdSpan   = params.lan_lbrdSpan; /*space between 1 container and lan border*/
            this.lbrdResz   = params.lan_lbrdResz;
            this.lanmatrix  = new lanMatrix();
            this.lanDef     = lanDef_;
            this.dic        = new dictionary();
            this.options    = options_;
            //this.helper_    = new helper();
            this.isInserted = false;
            this.dispLan    = false;
            this.layoutData = null;

            this.lanR    = null;
            this.lanName = null;
            this.rect    = null;

            this.minJailX       = 0;
            this.minJailY       = 0;
            this.maxJailX       = 0;
            this.maxJailY       = 0;
            this.isJailed       = false;
            this.isMoving       = false;

            this.oUnselected = params.lan_opacUnselec;
            this.oSelected   = params.lan_opacSelec;
            this.sDasharray  = params.lan_strokeDasharray;
            this.color       = params.lan_color;

            this.mvx = 0;
            this.mvy = 0;

            var lanRef = this;

            var reDefineRectPoints = function(x, y) {
                    lanRef.topLeftX = x;
                    lanRef.topLeftY = y;
                    //helper_.debug("[lan.reDefineRectPoints] { topLeftX: ".concat(topLeftX).concat(", topLeftY: ").concat(topLeftY).concat(" }"));
                };

            var lMove = function(dx, dy) {
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
                };

            var lanDragg = function () {
                    lanRef.r.drag(lanRef,"lan");
                },
                lanMove = function (dx, dy) {
                    lMove(dx,dy);
                },
                lanUP = function () {
                    lanRef.r.up()
                },
                lanOver = function () {
                    if (!lanRef.dispLan) {
                        lanRef.rect.animate({"fill-opacity": lanRef.oUnselected, "stroke-width": params.lan_strokeWidthShow}, 1);
                        lanRef.lanR.show();
                    }
                },
                lanOut  = function () {
                    if (!lanRef.dispLan) {
                        lanRef.rect.animate({"fill-opacity": lanRef.oUnselected, "stroke-width": 0}, 1);
                        lanRef.lanR.hide();
                    }
                };

            this.pushContainer = function(container) {
                this.lanmatrix.addContainer(container);
            };

            this.defineSize = function() {
                this.lanmatrix.defineLanContentSize();

                var contentLanSize = this.lanmatrix.getLanContentSize();
                this.lanwidth  = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().x-1)*this.contSpan + contentLanSize.width;
                this.lanheight = this.lbrdSpan*2 + (this.lanmatrix.getMtxSize().y-1)*this.contSpan + contentLanSize.height;
            };

            this.definePoz = function() {
                this.lanmatrix.defineMtxContainerPoz(this.topLeftX, this.topLeftY, this.lbrdSpan, this.contSpan, this.lanwidth, this.lanheight);
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
                        this.lanDef.dc===lanDef_.dc &&
                        this.lanDef.type===lanDef_.type &&
                        this.lanDef.area===lanDef_.area &&
                        this.lanDef.lan===lanDef_.lan &&
                        this.lanDef.subnetip===lanDef_.subnetip &&
                        this.lanDef.subnetmask===lanDef_.subnetmask
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
                    this.layoutData.isConnectedToLeftArea = this.layoutData.isConnectedToLeftArea || data.isConnectedToLeftArea;
                    this.layoutData.isConnectedToRightArea = this.layoutData.isConnectedToRightArea || data.isConnectedToRightArea;
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
                var lanTitle = "Lan " + this.lanDef.lan + " - " + this.lanDef.subnetip + "/" + this.lanDef.subnetmask;

                this.lanR    = this.r.set();
                this.lanName = this.r.text(this.topLeftX + (this.lanwidth/2), this.topLeftY + this.lbrdSpan/2, lanTitle);
                this.rect    = this.r.rect(this.topLeftX, this.topLeftY, this.lanwidth, this.lanheight, 0);

                this.lanName.attr(params.lan_txtTitle);
                this.lanR.push(this.lanName);
                this.lanR.hide();

                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                this.rect.drag(lanMove, lanDragg, lanUP);
                this.rect.mouseover(lanOver);
                this.rect.mouseout(lanOut);
            };

            this.displayLan = function(display) {
                this.disLan=display;
                if (this.disLan) {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": params.lan_strokeWidthShow}, 1);
                    this.lanR.show();
                } else {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": 0}, 1);
                    this.lanR.hide();
                }
            };
        }
        return lan;
    });