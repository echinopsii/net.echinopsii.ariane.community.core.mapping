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
            this.r          = null;
            //var helper_    = new helper();
            this.topLeftX   = 0;
            this.topLeftY   = 0;
            this.dcwidth    = 0;
            this.dcheight   = 0;
            this.areaSpan   = params.dc_areaSpan;
            this.dbrdSpan   = params.dc_dbrdSpan;
            this.dbrdResz   = params.dc_dbrdResz;
            this.geoDCLoc   = geoDCLoc_;
            this.dic        = new dictionary();
            this.msplitter  = mapSplitter;
            this.isInserted = false;
            this.dispDC  = false;

            this.dcsplitter = null;
            this.dcmatrix   = new datacenterMatrix(this.msplitter, registries, options);

            this.dcR    = null;
            this.dcName = null;
            this.dcTown = null;
            this.rect   = null;

            this.isMoving   = false;

            this.oUnselected = params.dc_opacUnselec;
            this.oSelected   = params.dc_opacSelec;
            this.sDasharray  = params.dc_strokeDasharray;
            this.sWidth      = params.dc_strokeWidthShow;
            this.color       = params.dc_color;

            this.mvx = 0;
            this.mvy = 0;

            var dcRef = this;

            var dMove = function (dx, dy) {
                    //helper_.debug("[datacenter.dMove] { cursor: ".concat(rect.attr('cursor')).concat(", isMoving:").concat(isMoving).concat(", isResizing:").concat(isResizing).concat(" }"));
                    //helper_.debug("[datacenter.dMove] { rx: ".concat(rx).concat(", ry: ").concat(ry).concat(", rw: ").concat(rw).concat(", rh: ").concat(rh).concat(", dx: ").concat(dx).concat(", dy: ").concat(dy).concat(" }"));
                    dcRef.r.move(dx,dy);
                    dcRef.r.safari();
                };

            var dcDragg = function () {
                    dcRef.r.drag(dcRef, "dc");
                },
                dcMove = function (dx, dy) {
                    dMove(dx,dy);
                },
                dcUP = function () {
                    dcRef.r.up();
                },
                dcOver = function () {
                    if (!dcRef.dispDC) {
                        this.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": dcRef.sWidth}, 1);
                        dcRef.dcR.show();
                        dcRef.dcsplitter.show();
                    }
                },
                dcOut  = function () {
                    if (!dcRef.dispDC) {
                        this.animate({"fill-opacity": dcRef.oUnselected, "stroke-width": 0}, 1);
                        dcRef.dcR.hide();
                        dcRef.dcsplitter.hide();
                    }
                };

            this.isEditionMode = function() {
                return (options.getMode()==dcRef.dic.mapMode.EDITION);
            };

            this.show = function() {
                dcRef.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": this.sWidth}, 1);
                dcRef.dcR.show();
                dcRef.dcsplitter.show();
            };

            this.pushContainerArea = function(container) {
                this.dcmatrix.addContainerArea(container);
            };

            this.defineZoneSize = function() {
                this.dcmatrix.defineDCContentSize();

                var contentDCSize = this.dcmatrix.getDCContentSize();
                this.dcwidth  = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().x-1)*this.areaSpan + contentDCSize.width;
                this.dcheight = this.dbrdSpan*2 + (this.dcmatrix.getMtxSize().y-1)*this.areaSpan + contentDCSize.height;
            };

            this.defineZoneObjectsSize = function() {
                this.dcmatrix.defineMtxAreaSize();
            };

            this.definePoz = function() {
                this.dcmatrix.defineMtxAreaPoz(this.topLeftX,this.topLeftY,this.dcwidth,this.dbrdSpan,this.areaSpan);
                this.dcsplitter = new datacenterSplitter(this);
                this.dcsplitter.definePoz();
            };

            this.getGeoDCLoc = function() {
                return this.geoDCLoc;
            };

            this.setTopLeftCoord = function (x, y) {
                this.topLeftX = x;
                this.topLeftY = y + this.dcmatrix.getYOffset(); //align on network type
            };

            this.geoDCLocEqual = function (geoDCLoc_) {
                return (this.geoDCLoc.dc==geoDCLoc_.dc);
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
                this.dcName = this.r.text(this.topLeftX + (this.dcwidth/2), this.topLeftY + this.dbrdSpan/2, this.geoDCLoc.dc);
                this.dcTown = this.r.text(this.topLeftX + (this.dcwidth/2), this.topLeftY + this.dcheight - this.dbrdSpan/2, this.geoDCLoc.town);
                this.rect   = this.r.rect(this.topLeftX, this.topLeftY, this.dcwidth, this.dcheight, 0);

                this.dcName.attr(params.dc_txtTitle).attr({'fill':this.color});
                this.dcTown.attr(params.dc_txtTitle).attr({'fill':this.color});
                this.dcR.push(this.dcName);
                this.dcR.push(this.dcTown);
                this.dcR.hide();

                this.rect.attr({fill: this.color, stroke: this.color, "stroke-dasharray": this.sDasharray, "fill-opacity": this.oUnselected, "stroke-width": 0});
                this.rect.drag(dcMove, dcDragg, dcUP);
                this.rect.mouseover(dcOver);
                this.rect.mouseout(dcOut);

                this.dcmatrix.printMtx(this.r);
                this.dcsplitter.print(this.r);
            };

            this.displayDC = function(display) {
                this.dispDC = display;
                if (this.dispDC) {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": this.sWidth}, 1);
                    this.dcR.show();
                    this.dcsplitter.show();
                } else {
                    this.rect.animate({"fill-opacity": this.oUnselected, "stroke-width": 0}, 1);
                    this.dcR.hide();
                    this.dcsplitter.hide();
                }
            };

            this.displayArea = function(display) {
                this.dcmatrix.displayArea(display);
            };

            this.displayLan = function(display) {
                this.dcmatrix.displayLan(display);
            };
        }

        return datacenter;
    });