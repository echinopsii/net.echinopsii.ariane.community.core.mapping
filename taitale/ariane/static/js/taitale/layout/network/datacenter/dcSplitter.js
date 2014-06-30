// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - DC Splitter                    │ \\
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
        'taitale-helper'
    ],
    function (params, helper) {
        function dcSplitter(dc) {

            this.datacenter = dc;
            //var helper_    = new helper();
            this.r          = null;

            this.wanLineTopY   = 0;
            this.manLineTopY   = 0;
            this.lanLineTopY   = 0;
            this.lanLineBdrY   = 0;

            this.wanLineHeight = 0;
            this.manLineHeight = 0;
            this.lanLineHeight = 0;

            this.dc_wan_border   = null;
            this.wan_title       = null;
            this.wan_man_border  = null;
            this.wan_man_mover   = null;
            this.wan_man_moverl1 = null;
            this.wan_man_moverl2 = null;
            this.wan_man_moverl3 = null;
            this.man_title       = null;
            this.man_lan_border  = null;
            this.man_lan_mover   = null;
            this.man_lan_moverl1 = null;
            this.man_lan_moverl2 = null;
            this.man_lan_moverl3 = null;
            this.lan_title       = null;
            this.lan_dc_border   = null;

            this.splitterLineColor = params.dc_split_lineColor;
            this.splitterTitleAttr = params.dc_split_title;

            this.moverSpan        = params.dc_split_moverSpan;
            this.moverLineSpan    = params.dc_split_moverLSpan;
            this.moverStrokeColor = params.dc_split_lineColor;
            this.moverHeight      = params.dc_split_moverHeight;
            this.moverYPoz        = -this.moverHeight/2;
            this.moverCornerRad   = params.dc_split_moverCRad;
            this.moverOSelected   = params.dc_split_moverOSel;
            this.moverOUnselected = params.dc_split_moverOUnsel;

            var splitterRef = this;

            var wmbDragg = function () {
                    this.cx    = splitterRef.datacenter.getZoneCoord().x;
                    this.cy    = splitterRef.manLineTopY;
                    this.xlong = splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.getZoneSize().width;
                    this.animate({"fill-opacity": splitterRef.moverOSelected}, 1);
                    splitterRef.datacenter.setIsMoving(true);

                    this.minY = null;
                    this.maxY = null;

                    this.dcmatrix = splitterRef.datacenter.dcmatrix;

                    var mtxS  = this.dcmatrix.getWanMtxSize(), i, ii;
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.minY == null) {
                            this.minY = this.dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;
                        } else {
                            var areaSY = this.dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;
                            if (areaSY > this.minY)
                                this.minY = areaSY;
                        }
                    }

                    mtxS = this.dcmatrix.getManMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.maxY == null) {
                            this.maxY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y;
                        } else {
                            var areaNY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y;
                            if (areaNY < this.maxY)
                                this.maxY = areaNY;
                        }
                    }
                },
                wmbMove = function(dx,dy) {
                    //block line move on x
                    //dx = 0;
                    //helper_.debug("wan man line move - Y borders: {".concat(this.minY).concat(",").concat(this.maxY).concat("}"));
                    if (this.cy+dy>this.minY && this.cy+dy<this.maxY) {
                        var wanManPath =
                                [
                                    ["M",this.cx, this.cy+dy],
                                    ["L",this.xlong, this.cy+dy]
                                ],
                            wanManMoverL1Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy]
                                ],
                            wanManMoverL2Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy-splitterRef.moverLineSpan],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy-splitterRef.moverLineSpan]
                                ],
                            wanManMoverL3Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy+splitterRef.moverLineSpan],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy+splitterRef.moverLineSpan]
                                ];
                        splitterRef.wan_man_border.remove();
                        splitterRef.wan_man_border = splitterRef.r.path(wanManPath).attr({stroke: splitterRef.moverStrokeColor, "stroke-dasharray": splitterRef.datacenter.sDasharray, "stroke-width": splitterRef.datacenter.sWidth});
                        splitterRef.wan_man_moverl1.remove();
                        splitterRef.wan_man_moverl1 = splitterRef.r.path(wanManMoverL1Path);
                        splitterRef.wan_man_moverl1.attr({stroke: splitterRef.moverStrokeColor, "stroke-width": splitterRef.datacenter.sWidth});
                        splitterRef.wan_man_moverl2.remove();
                        splitterRef.wan_man_moverl2 = splitterRef.r.path(wanManMoverL2Path);
                        splitterRef.wan_man_moverl2.attr({stroke: splitterRef.moverStrokeColor, "stroke-width": splitterRef.datacenter.sWidth});
                        splitterRef.wan_man_moverl3.remove();
                        splitterRef.wan_man_moverl3 = splitterRef.r.path(wanManMoverL3Path);
                        splitterRef.wan_man_moverl3.attr({stroke: splitterRef.moverStrokeColor, "stroke-width": splitterRef.datacenter.sWidth});
                        splitterRef.wan_man_mover.attr({x:this.cx+splitterRef.moverSpan,y:this.cy+dy+splitterRef.moverYPoz});

                        splitterRef.manLineTopY = this.cy+dy;
                        splitterRef.wan_title.rotate(90).attr(
                            {
                                x:splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.dbrdSpan/2,
                                y:splitterRef.wanLineTopY+(splitterRef.manLineTopY-splitterRef.wanLineTopY)/2
                            }
                        ).rotate(-90);
                        splitterRef.man_title.rotate(90).attr(
                            {
                                x:splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.dbrdSpan/2,
                                y:splitterRef.manLineTopY+(splitterRef.lanLineTopY-splitterRef.manLineTopY)/2
                            }
                        ).rotate(-90);
                        //helper_.debug("wan man line Y: ".concat(getManLineTopY()));
                    }
                },
                wmbUP = function() {
                    var i, ii;
                    this.animate({"fill-opacity": splitterRef.moverOUnselected}, 1);
                    splitterRef.datacenter.setIsMoving(false);

                    var mtxS = this.dcmatrix.getWanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromWanMtx(i).setMoveJail(null,null,null,splitterRef.manLineTopY);
                    }

                    mtxS = this.dcmatrix.getManMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromManMtx(i).setMoveJail(null,splitterRef.manLineTopY,null,null);
                    }
                    splitterRef.wanLineHeight = splitterRef.manLineTopY - splitterRef.wanLineTopY;
                    splitterRef.manLineHeight = splitterRef.lanLineTopY - splitterRef.manLineTopY;
                },
                wmbMouseMove = function(e) {
                    this.attr('cursor','move');
                    splitterRef.datacenter.show();
                    //helper_.debug("wan man line cursor: ".concat(this.attr("cursor")));
                };

            var mlbDragg = function () {
                    this.cx    = splitterRef.datacenter.getZoneCoord().x;
                    this.cy    = splitterRef.lanLineTopY;
                    this.xlong = splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.getZoneSize().width;
                    this.animate({"fill-opacity": splitterRef.moverOSelected}, 1);
                    splitterRef.datacenter.setIsMoving(true);

                    this.minY = null;
                    this.maxY = null;

                    this.dcmatrix = splitterRef.datacenter.dcmatrix;

                    var mtxS  = this.dcmatrix.getManMtxSize();
                    var i, ii;
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.minY == null) {
                            this.minY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromManMtx(i).getAreaSize().height;
                        } else {
                            var areaSY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromManMtx(i).getAreaSize().height;
                            if (areaSY > this.minY)
                                this.minY = areaSY;
                        }
                    }

                    mtxS = this.dcmatrix.getLanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.maxY == null) {
                            this.maxY = this.dcmatrix.getAreaFromLanMtx(i).getAreaCoords().y;
                        } else {
                            var areaNY = this.dcmatrix.getAreaFromLanMtx(i).getAreaCoords().y;
                            if (areaNY < this.maxY)
                                this.maxY = areaNY;
                        }
                    }
                },
                mlbMove = function(dx,dy) {
                    //block line move on x
                    //dx = 0;
                    //helper_.debug("man lan line move - Y borders: {".concat(this.minY).concat(",").concat(this.maxY).concat("}"));
                    if (this.cy+dy>this.minY && this.cy+dy<this.maxY) {
                        var manLanPath =
                                [
                                    ["M",this.cx, this.cy+dy],
                                    ["L",this.xlong, this.cy+dy]
                                ],
                            manLanMoverL1Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy]
                                ],
                            manLanMoverL2Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy-splitterRef.moverLineSpan],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy-splitterRef.moverLineSpan]
                                ],
                            manLanMoverL3Path =
                                [
                                    ["M",this.cx+splitterRef.moverSpan, this.cy+dy+splitterRef.moverLineSpan],
                                    ["L",this.cx+splitterRef.datacenter.dbrdSpan, this.cy+dy+splitterRef.moverLineSpan]
                                ];
                        splitterRef.man_lan_border.remove();
                        splitterRef.man_lan_border = splitterRef.r.path(manLanPath).attr({stroke:splitterRef.moverStrokeColor, "stroke-dasharray":splitterRef.datacenter.sDasharray, "stroke-width":splitterRef.datacenter.sWidth});
                        splitterRef.man_lan_moverl1.remove();
                        splitterRef.man_lan_moverl1 = splitterRef.r.path(manLanMoverL1Path);
                        splitterRef.man_lan_moverl1.attr({stroke:splitterRef.moverStrokeColor,"stroke-width":splitterRef.datacenter.sWidth});
                        splitterRef.man_lan_moverl2.remove();
                        splitterRef.man_lan_moverl2 = splitterRef.r.path(manLanMoverL2Path);
                        splitterRef.man_lan_moverl2.attr({stroke:splitterRef.moverStrokeColor,"stroke-width":splitterRef.datacenter.sWidth});
                        splitterRef.man_lan_moverl3.remove();
                        splitterRef.man_lan_moverl3 = splitterRef.r.path(manLanMoverL3Path);
                        splitterRef.man_lan_moverl3.attr({stroke:splitterRef.moverStrokeColor,"stroke-width":splitterRef.datacenter.sWidth});
                        splitterRef.man_lan_mover.attr({x:this.cx+splitterRef.moverSpan,y:this.cy+dy+splitterRef.moverYPoz});

                        splitterRef.lanLineTopY = this.cy+dy;
                        splitterRef.man_title.rotate(90).attr(
                            {
                                x:splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.dbrdSpan/2,
                                y:splitterRef.manLineTopY+(splitterRef.lanLineTopY-splitterRef.manLineTopY)/2
                            }
                        ).rotate(-90);
                        splitterRef.lan_title.rotate(90).attr(
                            {
                                x:splitterRef.datacenter.getZoneCoord().x+splitterRef.datacenter.dbrdSpan/2,
                                y:splitterRef.lanLineTopY+(splitterRef.lanLineBdrY - splitterRef.lanLineTopY)/2
                            }
                        ).rotate(-90);
                        //helper_.debug("man lan line Y: ".concat(getLanLineTopY()));
                    }
                },
                mlbUP = function() {
                    this.animate({"fill-opacity": splitterRef.moverOUnselected}, 1);
                    splitterRef.datacenter.setIsMoving(false);

                    var mtxS = this.dcmatrix.getManMtxSize();
                    var i, ii;
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromManMtx(i).setMoveJail(null,null,null,splitterRef.lanLineTopY);
                    }

                    mtxS = this.dcmatrix.getLanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromLanMtx(i).setMoveJail(null,splitterRef.lanLineTopY,null,null);
                    }

                    splitterRef.manLineHeight = splitterRef.lanLineTopY-splitterRef.manLineTopY;
                    splitterRef.lanLineHeight = splitterRef.lanLineBdrY-splitterRef.lanLineTopY;
                },
                mlbMouseMove = function(e) {
                    this.attr('cursor','move');
                    splitterRef.datacenter.show();
                    //helper_.debug("man lan line cursor: ".concat(this.attr("cursor")));
                };

            this.definePoz = function() {
                this.wanLineHeight = this.datacenter.msplitter.getWanLineHeight();
                this.manLineHeight = this.datacenter.msplitter.getManLineHeight();
                this.lanLineHeight = this.datacenter.msplitter.getLanLineHeight();

                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    this.wanLineTopY = this.datacenter.getZoneCoord().y + this.datacenter.dbrdSpan;
                    this.manLineTopY = this.wanLineTopY + this.wanLineHeight;
                    this.lanLineTopY = this.manLineTopY + this.manLineHeight;
                    this.lanLineBdrY = this.lanLineTopY + this.lanLineHeight;
                } else {
                    if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                        this.manLineTopY = this.datacenter.getZoneCoord().y + this.datacenter.dbrdSpan;
                        this.lanLineTopY = this.manLineTopY + this.manLineHeight;
                        this.lanLineBdrY = this.lanLineTopY + this.lanLineHeight;
                    } else {
                        this.lanLineTopY = this.datacenter.getZoneCoord().y + this.datacenter.dbrdSpan;
                        this.lanLineBdrY = this.lanLineTopY + this.lanLineHeight;
                    }
                }
            };

            this.print = function(r_) {
                this.r = r_;
                var dcWanPath = null;
                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.wanLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.wanLineTopY]
                    ];
                } else if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.manLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.manLineTopY]
                    ];
                } else if (this.datacenter.dcmatrix.getLanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.lanLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineTopY]
                    ];
                }
                var wanManPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.manLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.manLineTopY]
                        ],
                    wanManMoverL1Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY]
                        ],
                    wanManMoverL2Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY-splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY-splitterRef.moverLineSpan]
                        ],
                    wanManMoverL3Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY+splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY+splitterRef.moverLineSpan]
                        ],
                    manLanPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.lanLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineTopY]
                        ],
                    lanManMoverL1Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY]
                        ],
                    lanManMoverL2Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY-splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY-splitterRef.moverLineSpan]
                        ],
                    lanManMoverL3Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY+splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY+splitterRef.moverLineSpan]
                        ],
                    lanDcPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.lanLineBdrY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineBdrY]
                        ];

                this.splitterR = this.r.set();

                this.dc_wan_border = this.r.path(dcWanPath);
                this.dc_wan_border.attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                this.dc_wan_border.hide();

                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    this.wan_man_border = splitterRef.r.path(wanManPath);
                    this.wan_man_border.attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                    this.wan_man_border.hide();
                    this.wan_man_moverl1 = splitterRef.r.path(wanManMoverL1Path);
                    this.wan_man_moverl1.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.wan_man_moverl1.mousemove(wmbMouseMove).hide();
                    this.wan_man_moverl2 = splitterRef.r.path(wanManMoverL2Path);
                    this.wan_man_moverl2.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.wan_man_moverl2.mousemove(wmbMouseMove).hide();
                    this.wan_man_moverl3 = splitterRef.r.path(wanManMoverL3Path);
                    this.wan_man_moverl3.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.wan_man_moverl3.mousemove(wmbMouseMove).hide();
                    this.wan_man_mover = splitterRef.r.rect(this.datacenter.getZoneCoord().x+splitterRef.moverSpan,this.manLineTopY+splitterRef.moverYPoz,this.datacenter.dbrdSpan-splitterRef.moverSpan,splitterRef.moverHeight,splitterRef.moverCornerRad);
                    this.wan_man_mover.attr({fill: splitterRef.moverStrokeColor,"fill-opacity":splitterRef.moverOUnselected,stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.wan_man_mover.hide();
                    this.wan_man_mover.drag(wmbMove, wmbDragg, wmbUP);
                    this.wan_man_mover.mousemove(wmbMouseMove);
                    this.wan_title = splitterRef.r.text(this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,this.wanLineTopY+this.wanLineHeight/2,"WAN").rotate(-90);
                    this.wan_title.attr(splitterRef.splitterTitleAttr);
                    this.wan_title.hide();
                }

                if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    this.man_lan_border = splitterRef.r.path(manLanPath);
                    this.man_lan_border.attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                    this.man_lan_border.hide();
                    this.man_lan_moverl1 = splitterRef.r.path(lanManMoverL1Path);
                    this.man_lan_moverl1.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.man_lan_moverl1.hide();
                    this.man_lan_moverl2 = splitterRef.r.path(lanManMoverL2Path);
                    this.man_lan_moverl2.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.man_lan_moverl2.hide();
                    this.man_lan_moverl3 = splitterRef.r.path(lanManMoverL3Path);
                    this.man_lan_moverl3.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.man_lan_moverl3.hide();
                    this.man_lan_mover = splitterRef.r.rect(this.datacenter.getZoneCoord().x+splitterRef.moverSpan,this.lanLineTopY+splitterRef.moverYPoz,this.datacenter.dbrdSpan-splitterRef.moverSpan,splitterRef.moverHeight,splitterRef.moverCornerRad);
                    this.man_lan_mover.attr({fill: splitterRef.moverStrokeColor,"fill-opacity":splitterRef.moverOUnselected,stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.man_lan_mover.hide();
                    this.man_lan_mover.drag(mlbMove, mlbDragg, mlbUP);
                    this.man_lan_mover.mousemove(mlbMouseMove);
                    this.man_title = splitterRef.r.text(this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,this.manLineTopY+this.manLineHeight/2,"MAN").rotate(-90);
                    this.man_title.attr(splitterRef.splitterTitleAttr);
                    this.man_title.hide();
                }

                this.lan_title = splitterRef.r.text(this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,this.lanLineTopY+this.lanLineHeight/2,"LAN").rotate(-90);
                this.lan_title.attr(splitterRef.splitterTitleAttr);
                this.lan_title.hide();

                this.lan_dc_border = splitterRef.r.path(lanDcPath);
                this.lan_dc_border.attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                this.lan_dc_border.hide();
            };

            this.hide = function() {
                if (this.dc_wan_border) this.dc_wan_border.hide();
                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    if (this.wan_man_border) this.wan_man_border.hide().toBack();
                    if (this.wan_man_mover) this.wan_man_mover.hide().toBack();
                    if (this.wan_man_moverl1) this.wan_man_moverl1.hide().toBack();
                    if (this.wan_man_moverl2) this.wan_man_moverl2.hide().toBack();
                    if (this.wan_man_moverl3) this.wan_man_moverl3.hide().toBack();
                    if (this.wan_title) this.wan_title.hide().toBack();
                }
                if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    if (this.man_lan_border) this.man_lan_border.hide().toBack();
                    if (this.man_lan_mover) this.man_lan_mover.hide().toBack();
                    if (this.man_lan_moverl1) this.man_lan_moverl1.hide().toBack();
                    if (this.man_lan_moverl2) this.man_lan_moverl2.hide().toBack();
                    if (this.man_lan_moverl3) this.man_lan_moverl3.hide().toBack();
                    if (this.man_title) this.man_title.hide().toBack();
                }
                if (this.lan_title) this.lan_title.hide().toBack();
                if (this.lan_dc_border) this.lan_dc_border.hide();
            };

            this.show = function() {
                if (this.dc_wan_border) this.dc_wan_border.show();
                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    if (this.wan_man_border) this.wan_man_border.show().toFront();
                    if (this.datacenter.isEditionMode()){
                        if (this.wan_man_mover) this.wan_man_mover.show().toFront();
                        if (this.wan_man_moverl1) this.wan_man_moverl1.show().toFront();
                        if (this.wan_man_moverl2) this.wan_man_moverl2.show().toFront();
                        if (this.wan_man_moverl3) this.wan_man_moverl3.show().toFront();
                    }
                    if (this.wan_title) this.wan_title.show().toFront();
                }
                if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    if (this.man_lan_border) this.man_lan_border.show().toFront();
                    if (this.datacenter.isEditionMode()) {
                        if (this.man_lan_mover) this.man_lan_mover.show().toFront();
                        if (this.man_lan_moverl1) this.man_lan_moverl1.show().toFront();
                        if (this.man_lan_moverl2) this.man_lan_moverl2.show().toFront();
                        if (this.man_lan_moverl3) this.man_lan_moverl3.show().toFront();
                    }
                    if (this.man_title) this.man_title.show().toFront();
                }
                if (this.lan_title) this.lan_title.show().toFront();
                if (this.lan_dc_border) this.lan_dc_border.show();
            };

            this.move = function(r) {
                var dcWanPath =  null;
                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.wanLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.wanLineTopY]
                    ];
                } else if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.manLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.manLineTopY]
                    ];
                } else if (this.datacenter.dcmatrix.getLanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",this.datacenter.getZoneCoord().x, this.lanLineTopY],
                        ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineTopY]
                    ];
                }

                var wanManPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.manLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.manLineTopY]
                        ],
                    wanManMoverL1Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY]
                        ],
                    wanManMoverL2Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY-splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY-splitterRef.moverLineSpan]
                        ],
                    wanManMoverL3Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.manLineTopY+splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.manLineTopY+splitterRef.moverLineSpan]
                        ],
                    manLanPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.lanLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineTopY]
                        ],
                    lanManMoverL1Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY]
                        ],
                    lanManMoverL2Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY-splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY-splitterRef.moverLineSpan]
                        ],
                    lanManMoverL3Path =
                        [
                            ["M",this.datacenter.getZoneCoord().x+splitterRef.moverSpan, this.lanLineTopY+splitterRef.moverLineSpan],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan, this.lanLineTopY+splitterRef.moverLineSpan]
                        ],
                    lanDcPath =
                        [
                            ["M",this.datacenter.getZoneCoord().x, this.lanLineBdrY],
                            ["L",this.datacenter.getZoneCoord().x+this.datacenter.getZoneSize().width, this.lanLineBdrY]
                        ];

                this.dc_wan_border.remove();
                this.dc_wan_border = r.path(dcWanPath).attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});

                if (this.datacenter.dcmatrix.getWanMtxSize()!=0) {
                    this.wan_man_border.remove();
                    this.wan_man_border = r.path(wanManPath).attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                    this.wan_title.rotate(90).attr({x:this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,y:this.wanLineTopY+this.wanLineHeight/2}).rotate(-90);
                    this.wan_man_mover.attr({x:this.datacenter.getZoneCoord().x+splitterRef.moverSpan,y:this.manLineTopY+splitterRef.moverYPoz});
                    this.wan_man_moverl1.remove();
                    this.wan_man_moverl1 = this.r.path(wanManMoverL1Path);
                    if (!this.datacenter.isEditionMode()) this.wan_man_moverl1.hide();
                    this.wan_man_moverl1.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.wan_man_moverl2.remove();
                    this.wan_man_moverl2 = r.path(wanManMoverL2Path);
                    this.wan_man_moverl2.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    if (!this.datacenter.isEditionMode()) this.wan_man_moverl2.hide();
                    this.wan_man_moverl3.remove();
                    this.wan_man_moverl3 = r.path(wanManMoverL3Path);
                    this.wan_man_moverl3.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    if (!this.datacenter.isEditionMode()) this.wan_man_moverl3.hide();
                }

                if (this.datacenter.dcmatrix.getManMtxSize()!=0) {
                    this.man_lan_border.remove();
                    this.man_lan_border = r.path(manLanPath).attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
                    this.man_title.rotate(90).attr({x:this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,y:this.manLineTopY+this.manLineHeight/2}).rotate(-90);
                    this.man_lan_mover.attr({x:this.datacenter.getZoneCoord().x+splitterRef.moverSpan,y:this.lanLineTopY+splitterRef.moverYPoz});
                    this.man_lan_mover.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    this.man_lan_moverl1.remove();
                    this.man_lan_moverl1 = r.path(lanManMoverL1Path);
                    this.man_lan_moverl1.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    if (!this.datacenter.isEditionMode()) this.man_lan_moverl1.hide();
                    this.man_lan_moverl2.remove();
                    this.man_lan_moverl2 = r.path(lanManMoverL2Path);
                    this.man_lan_moverl2.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    if (!this.datacenter.isEditionMode()) this.man_lan_moverl2.hide();
                    this.man_lan_moverl3.remove();
                    this.man_lan_moverl3 = r.path(lanManMoverL3Path);
                    this.man_lan_moverl3.attr({stroke: splitterRef.moverStrokeColor,"stroke-width": this.datacenter.sWidth});
                    if (!this.datacenter.isEditionMode()) this.man_lan_moverl3.hide();
                }
                this.lan_title.rotate(90).attr({x:this.datacenter.getZoneCoord().x+this.datacenter.dbrdSpan/2,y:this.lanLineTopY+this.lanLineHeight/2}).rotate(-90);
                this.lan_dc_border.remove();
                this.lan_dc_border = this.r.path(lanDcPath).attr({stroke: splitterRef.splitterLineColor, "stroke-dasharray": this.datacenter.sDasharray, "stroke-width": this.datacenter.sWidth});
            }
        }

        return dcSplitter;
    });