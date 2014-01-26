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

            var datacenter = dc,
                helper_    = new helper(),
                r          = null;

            var wanLineTopY   = 0,
                manLineTopY   = 0,
                lanLineTopY   = 0,
                lanLineBdrY   = 0;

            this.setWanLineTopY = function(topY) {
                wanLineTopY = topY;
            }

            var getWanLineTopY = function() {
                return wanLineTopY;
            }

            this.getWanLineTopY = function() {
                return getWanLineTopY();
            }

            var setManLineTopY = function(topY) {
                manLineTopY = topY;
            };

            this.setManLineTopY = function(topY) {
                setManLineTopY(topY);
            };

            var getManLineTopY = function() {
                return manLineTopY;
            }

            this.getManLineTopY = function() {
                return getManLineTopY();
            }

            var setLanLineTopY = function(topY) {
                lanLineTopY = topY;
            }

            this.setLanLineTopY = function(topY) {
                setLanLineTopY(topY);
            }

            var getLanLineTopY = function() {
                return lanLineTopY;
            }

            this.getLanLineTopY = function() {
                return getLanLineTopY();
            }

            this.setLanLineBdrY = function(bdrY) {
                lanLineBdrY = bdrY;
            }

            var getLanLineBrdY = function() {
                return lanLineBdrY;
            }

            this.getLanLineBdrY = function() {
                return getLanLineBrdY();
            }

            var wanLineHeight = 0,
                manLineHeight = 0,
                lanLineHeight = 0;

            var setWanLineHeight = function(areaHeight) {
                helper_.debug("last wanLineHeight: ".concat(wanLineHeight).concat(", next wanLineHeight: ").concat(areaHeight));
                wanLineHeight = areaHeight;
            }

            this.setWanLineHeight = function(areaHeight) {
                setWanLineHeight(areaHeight);
            };

            this.getWanLineHeight = function() {
                return wanLineHeight;
            };

            var setManLineHeight = function(areaHeight) {
                helper_.debug("last manLineHeight: ".concat(manLineHeight).concat(", next manLineHeight: ").concat(areaHeight));
                manLineHeight = areaHeight;
            }

            this.setManLineHeight = function(areaHeight) {
                setManLineHeight(areaHeight);
            };

            this.getManLineHeight = function() {
                return manLineHeight;
            };

            var setLanLineHeight = function(areaHeight) {
                helper_.debug("last lanLineHeight: ".concat(lanLineHeight).concat(", next lanLineHeight: ").concat(areaHeight));
                lanLineHeight = areaHeight;
            }

            this.setLanLineHeight = function(areaHeight) {
                setLanLineHeight(areaHeight);
            };

            this.getLanLineHeight = function() {
                return lanLineHeight;
            };

            this.definePoz = function() {
                wanLineHeight = datacenter.getMapSplitter().getWanLineHeight();
                manLineHeight = datacenter.getMapSplitter().getManLineHeight();
                lanLineHeight = datacenter.getMapSplitter().getLanLineHeight();

                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    wanLineTopY = datacenter.getZoneCoord().y + datacenter.getZoneSpan();
                    manLineTopY = wanLineTopY + wanLineHeight;
                    lanLineTopY = manLineTopY + manLineHeight;
                    lanLineBdrY = lanLineTopY + lanLineHeight;
                } else {
                    if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                        manLineTopY = datacenter.getZoneCoord().y + datacenter.getZoneSpan();
                        lanLineTopY = manLineTopY + manLineHeight;
                        lanLineBdrY = lanLineTopY + lanLineHeight;
                    } else {
                        lanLineTopY = datacenter.getZoneCoord().y + datacenter.getZoneSpan();
                        lanLineBdrY = lanLineTopY + lanLineHeight;
                    }
                }
            };

            var dc_wan_border   = null,
                wan_title       = null,
                wan_man_border  = null,
                wan_man_mover   = null,
                wan_man_moverl1 = null,
                wan_man_moverl2 = null,
                wan_man_moverl3 = null,
                man_title       = null,
                man_lan_border  = null,
                man_lan_mover   = null,
                man_lan_moverl1 = null,
                man_lan_moverl2 = null,
                man_lan_moverl3 = null,
                lan_title       = null,
                lan_dc_border   = null;

            var splitterLineColor = params.dc_split_lineColor,
                splitterTitleAttr = params.dc_split_title;

            var moverSpan        = params.dc_split_moverSpan,
                moverLineSpan    = params.dc_split_moverLSpan,
                moverStrokeColor = params.dc_split_lineColor,
                moverHeight      = params.dc_split_moverHeight,
                moverYPoz        = -moverHeight/2,
                moverCornerRad   = params.dc_split_moverCRad,
                moverOSelected   = params.dc_split_moverOSel,
                moverOUnselected = params.dc_split_moverOUnsel;

            var wmbDragg = function () {
                    this.cx    = datacenter.getZoneCoord().x;
                    this.cy    = manLineTopY;
                    this.xlong = datacenter.getZoneCoord().x+datacenter.getZoneSize().width;
                    this.animate({"fill-opacity": moverOSelected}, 1);
                    datacenter.setIsMoving(true);

                    this.minY = null;
                    this.maxY = null;

                    this.dcmatrix = datacenter.getDCMatrix();

                    var mtxS  = this.dcmatrix.getWanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.minY == null) {
                            this.minY = this.dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;
                        } else {
                            var areaSY = this.dcmatrix.getAreaFromWanMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromWanMtx(i).getAreaSize().height;
                            if (areaSY > this.minY)
                                this.minY = areaSY;
                        }
                    }

                    mtxS = this.dcmatrix.getManMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
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
                    dx = 0;
                    helper_.debug("wan man line move - Y borders: {".concat(this.minY).concat(",").concat(this.maxY).concat("}"));
                    if (this.cy+dy>this.minY && this.cy+dy<this.maxY) {
                        var wanManPath =
                                [
                                    ["M",this.cx, this.cy+dy],
                                    ["L",this.xlong, this.cy+dy]
                                ],
                            wanManMoverL1Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy]
                                ],
                            wanManMoverL2Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy-moverLineSpan],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy-moverLineSpan]
                                ],
                            wanManMoverL3Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy+moverLineSpan],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy+moverLineSpan]
                                ];
                        wan_man_border.remove();
                        wan_man_border = r.path(wanManPath).attr({stroke: moverStrokeColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                        wan_man_moverl1.remove();
                        wan_man_moverl1 = r.path(wanManMoverL1Path);
                        wan_man_moverl1.attr({stroke: moverStrokeColor, "stroke-width": datacenter.getStrokeWidth()});
                        wan_man_moverl2.remove();
                        wan_man_moverl2 = r.path(wanManMoverL2Path);
                        wan_man_moverl2.attr({stroke: moverStrokeColor, "stroke-width": datacenter.getStrokeWidth()});
                        wan_man_moverl3.remove();
                        wan_man_moverl3 = r.path(wanManMoverL3Path);
                        wan_man_moverl3.attr({stroke: moverStrokeColor, "stroke-width": datacenter.getStrokeWidth()});
                        wan_man_mover.attr({x:this.cx+moverSpan,y:this.cy+dy+moverYPoz});

                        setManLineTopY(this.cy+dy);
                        wan_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:wanLineTopY+(getManLineTopY()-getWanLineTopY())/2}).rotate(-90);
                        man_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:manLineTopY+(getLanLineTopY()-getManLineTopY())/2}).rotate(-90);
                        helper_.debug("wan man line Y: ".concat(getManLineTopY()));
                    }
                },
                wmbUP = function() {
                    this.animate({"fill-opacity": moverOUnselected}, 1);
                    datacenter.setIsMoving(false);

                    var mtxS = this.dcmatrix.getWanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromWanMtx(i).setMoveJail(null,null,null,getManLineTopY());
                    }

                    mtxS = this.dcmatrix.getManMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromManMtx(i).setMoveJail(null,getManLineTopY(),null,null);
                    }
                    setWanLineHeight(getManLineTopY()-getWanLineTopY());
                    setManLineHeight(getLanLineTopY()-getManLineTopY());
                },
                wmbMouseMove = function(e) {
                    this.attr('cursor','move');
                    datacenter.show();
                    helper_.debug("wan man line cursor: ".concat(this.attr("cursor")));
                };

            var mlbDragg = function () {
                    this.cx    = datacenter.getZoneCoord().x;
                    this.cy    = lanLineTopY;
                    this.xlong = datacenter.getZoneCoord().x+datacenter.getZoneSize().width;
                    this.animate({"fill-opacity": moverOSelected}, 1);
                    datacenter.setIsMoving(true);

                    this.minY = null;
                    this.maxY = null;

                    this.dcmatrix = datacenter.getDCMatrix();

                    var mtxS  = this.dcmatrix.getManMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        if (this.minY == null) {
                            this.minY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromManMtx(i).getAreaSize().height;
                        } else {
                            var areaSY = this.dcmatrix.getAreaFromManMtx(i).getAreaCoords().y + this.dcmatrix.getAreaFromManMtx(i).getAreaSize().height;
                            if (areaSY > this.minY)
                                this.minY = areaSY;
                        }
                    }

                    mtxS = this.dcmatrix.getLanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
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
                    dx = 0;
                    helper_.debug("man lan line move - Y borders: {".concat(this.minY).concat(",").concat(this.maxY).concat("}"));
                    if (this.cy+dy>this.minY && this.cy+dy<this.maxY) {
                        var manLanPath =
                                [
                                    ["M",this.cx, this.cy+dy],
                                    ["L",this.xlong, this.cy+dy]
                                ],
                            manLanMoverL1Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy]
                                ],
                            manLanMoverL2Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy-moverLineSpan],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy-moverLineSpan]
                                ],
                            manLanMoverL3Path =
                                [
                                    ["M",this.cx+moverSpan, this.cy+dy+moverLineSpan],
                                    ["L",this.cx+datacenter.getZoneSpan(), this.cy+dy+moverLineSpan]
                                ];
                        man_lan_border.remove();
                        man_lan_border = r.path(manLanPath).attr({stroke:moverStrokeColor, "stroke-dasharray":datacenter.getStrokeDasharray(), "stroke-width":datacenter.getStrokeWidth()});
                        man_lan_moverl1.remove();
                        man_lan_moverl1 = r.path(manLanMoverL1Path);
                        man_lan_moverl1.attr({stroke:moverStrokeColor,"stroke-width":datacenter.getStrokeWidth()});
                        man_lan_moverl2.remove();
                        man_lan_moverl2 = r.path(manLanMoverL2Path);
                        man_lan_moverl2.attr({stroke:moverStrokeColor,"stroke-width":datacenter.getStrokeWidth()});
                        man_lan_moverl3.remove();
                        man_lan_moverl3 = r.path(manLanMoverL3Path);
                        man_lan_moverl3.attr({stroke:moverStrokeColor,"stroke-width":datacenter.getStrokeWidth()});
                        man_lan_mover.attr({x:this.cx+moverSpan,y:this.cy+dy+moverYPoz});

                        setLanLineTopY(this.cy+dy);
                        man_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:manLineTopY+(getLanLineTopY()-getManLineTopY())/2}).rotate(-90);
                        lan_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:lanLineTopY+(getLanLineBrdY()-getLanLineTopY())/2}).rotate(-90);
                        helper_.debug("man lan line Y: ".concat(getLanLineTopY()));
                    }
                },
                mlbUP = function() {
                    this.animate({"fill-opacity": moverOUnselected}, 1);
                    datacenter.setIsMoving(false);

                    var mtxS = this.dcmatrix.getManMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromManMtx(i).setMoveJail(null,null,null,getLanLineTopY());
                    }

                    mtxS = this.dcmatrix.getLanMtxSize();
                    for (var i = 0, ii =  mtxS; i < ii; i++) {
                        this.dcmatrix.getAreaFromLanMtx(i).setMoveJail(null,getLanLineTopY(),null,null);
                    }

                    setManLineHeight(getLanLineTopY()-getManLineTopY());
                    setLanLineHeight(getLanLineBrdY()-getLanLineTopY());
                },
                mlbMouseMove = function(e) {
                    this.attr('cursor','move');
                    datacenter.show();
                    helper_.debug("man lan line cursor: ".concat(this.attr("cursor")));
                };


            this.print = function(r_) {
                r = r_;
                var dcWanPath = null;
                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, wanLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, wanLineTopY]
                    ];
                } else if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, manLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, manLineTopY]
                    ];
                } else if (datacenter.getDCMatrix().getLanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, lanLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineTopY]
                    ];
                }
                var wanManPath =
                        [
                            ["M",datacenter.getZoneCoord().x, manLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, manLineTopY]
                        ],
                    wanManMoverL1Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY]
                        ],
                    wanManMoverL2Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY-moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY-moverLineSpan]
                        ],
                    wanManMoverL3Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY+moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY+moverLineSpan]
                        ],
                    manLanPath =
                        [
                            ["M",datacenter.getZoneCoord().x, lanLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineTopY]
                        ],
                    lanManMoverL1Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY]
                        ],
                    lanManMoverL2Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY-moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY-moverLineSpan]
                        ],
                    lanManMoverL3Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY+moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY+moverLineSpan]
                        ],
                    lanDcPath =
                        [
                            ["M",datacenter.getZoneCoord().x, lanLineBdrY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineBdrY]
                        ];

                dc_wan_border = r.path(dcWanPath);
                dc_wan_border.attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                dc_wan_border.hide();

                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    wan_man_border = r.path(wanManPath);
                    wan_man_border.attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                    wan_man_border.hide();
                    wan_man_moverl1 = r.path(wanManMoverL1Path);
                    wan_man_moverl1.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    wan_man_moverl1.mousemove(wmbMouseMove).hide();
                    wan_man_moverl2 = r.path(wanManMoverL2Path);
                    wan_man_moverl2.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    wan_man_moverl2.mousemove(wmbMouseMove).hide();
                    wan_man_moverl3 = r.path(wanManMoverL3Path);
                    wan_man_moverl3.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    wan_man_moverl3.mousemove(wmbMouseMove).hide();
                    wan_man_mover = r.rect(datacenter.getZoneCoord().x+moverSpan,manLineTopY+moverYPoz,datacenter.getZoneSpan()-moverSpan,moverHeight,moverCornerRad);
                    wan_man_mover.attr({fill: moverStrokeColor,"fill-opacity":moverOUnselected,stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    wan_man_mover.hide();
                    wan_man_mover.drag(wmbMove, wmbDragg, wmbUP);
                    wan_man_mover.mousemove(wmbMouseMove);
                    wan_title = r.text(datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,wanLineTopY+wanLineHeight/2,"WAN").rotate(-90);
                    wan_title.attr(splitterTitleAttr);
                    wan_title.hide();
                }

                if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    man_lan_border = r.path(manLanPath);
                    man_lan_border.attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                    man_lan_border.hide();
                    man_lan_moverl1 = r.path(lanManMoverL1Path);
                    man_lan_moverl1.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    man_lan_moverl1.hide();
                    man_lan_moverl2 = r.path(lanManMoverL2Path);
                    man_lan_moverl2.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    man_lan_moverl2.hide();
                    man_lan_moverl3 = r.path(lanManMoverL3Path);
                    man_lan_moverl3.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    man_lan_moverl3.hide();
                    man_lan_mover = r.rect(datacenter.getZoneCoord().x+moverSpan,lanLineTopY+moverYPoz,datacenter.getZoneSpan()-moverSpan,moverHeight,moverCornerRad);
                    man_lan_mover.attr({fill: moverStrokeColor,"fill-opacity":moverOUnselected,stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    man_lan_mover.hide();
                    man_lan_mover.drag(mlbMove, mlbDragg, mlbUP);
                    man_lan_mover.mousemove(mlbMouseMove);
                    man_title = r.text(datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,manLineTopY+manLineHeight/2,"MAN").rotate(-90);
                    man_title.attr(splitterTitleAttr);
                    man_title.hide();
                }

                lan_title = r.text(datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,lanLineTopY+lanLineHeight/2,"LAN").rotate(-90);
                lan_title.attr(splitterTitleAttr);
                lan_title.hide();

                lan_dc_border = r.path(lanDcPath);
                lan_dc_border.attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                lan_dc_border.hide();
            };

            this.hide = function() {
                if (dc_wan_border) dc_wan_border.hide();
                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    if (wan_man_border) wan_man_border.hide().toBack();
                    if (wan_man_mover) wan_man_mover.hide().toBack();
                    if (wan_man_moverl1) wan_man_moverl1.hide().toBack();
                    if (wan_man_moverl2) wan_man_moverl2.hide().toBack();
                    if (wan_man_moverl3) wan_man_moverl3.hide().toBack();
                    if (wan_title) wan_title.hide().toBack();
                }
                if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    if (man_lan_border) man_lan_border.hide().toBack();
                    if (man_lan_mover) man_lan_mover.hide().toBack();
                    if (man_lan_moverl1) man_lan_moverl1.hide().toBack();
                    if (man_lan_moverl2) man_lan_moverl2.hide().toBack();
                    if (man_lan_moverl3) man_lan_moverl3.hide().toBack();
                    if (man_title) man_title.hide().toBack();
                }
                if (lan_title) lan_title.hide().toBack();
                if (lan_dc_border) lan_dc_border.hide();
            };

            this.show = function() {
                if (dc_wan_border) dc_wan_border.show();
                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    if (wan_man_border) wan_man_border.show().toFront();
                    if (datacenter.isEditionMode()){
                        if (wan_man_mover) wan_man_mover.show().toFront();
                        if (wan_man_moverl1) wan_man_moverl1.show().toFront();
                        if (wan_man_moverl2) wan_man_moverl2.show().toFront();
                        if (wan_man_moverl3) wan_man_moverl3.show().toFront();
                    }
                    if (wan_title) wan_title.show().toFront();
                }
                if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    if (man_lan_border) man_lan_border.show().toFront();
                    if (datacenter.isEditionMode()) {
                        if (man_lan_mover) man_lan_mover.show().toFront();
                        if (man_lan_moverl1) man_lan_moverl1.show().toFront();
                        if (man_lan_moverl2) man_lan_moverl2.show().toFront();
                        if (man_lan_moverl3) man_lan_moverl3.show().toFront();
                    }
                    if (man_title) man_title.show().toFront();
                }
                if (lan_title) lan_title.show().toFront();
                if (lan_dc_border) lan_dc_border.show();
            };

            this.move = function(r) {
                var dcWanPath =  null;
                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, wanLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, wanLineTopY]
                    ];
                } else if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, manLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, manLineTopY]
                    ];
                } else if (datacenter.getDCMatrix().getLanMtxSize()!=0) {
                    dcWanPath =                         [
                        ["M",datacenter.getZoneCoord().x, lanLineTopY],
                        ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineTopY]
                    ];
                }

                var wanManPath =
                        [
                            ["M",datacenter.getZoneCoord().x, manLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, manLineTopY]
                        ],
                    wanManMoverL1Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY]
                        ],
                    wanManMoverL2Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY-moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY-moverLineSpan]
                        ],
                    wanManMoverL3Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, manLineTopY+moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), manLineTopY+moverLineSpan]
                        ],
                    manLanPath =
                        [
                            ["M",datacenter.getZoneCoord().x, lanLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineTopY]
                        ],
                    lanManMoverL1Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY]
                        ],
                    lanManMoverL2Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY-moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY-moverLineSpan]
                        ],
                    lanManMoverL3Path =
                        [
                            ["M",datacenter.getZoneCoord().x+moverSpan, lanLineTopY+moverLineSpan],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSpan(), lanLineTopY+moverLineSpan]
                        ],
                    lanDcPath =
                        [
                            ["M",datacenter.getZoneCoord().x, lanLineBdrY],
                            ["L",datacenter.getZoneCoord().x+datacenter.getZoneSize().width, lanLineBdrY]
                        ];

                dc_wan_border.remove();
                dc_wan_border = r.path(dcWanPath).attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});

                if (datacenter.getDCMatrix().getWanMtxSize()!=0) {
                    wan_man_border.remove();
                    wan_man_border = r.path(wanManPath).attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                    wan_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:wanLineTopY+wanLineHeight/2}).rotate(-90);
                    wan_man_mover.attr({x:datacenter.getZoneCoord().x+moverSpan,y:manLineTopY+moverYPoz});
                    wan_man_moverl1.remove();
                    wan_man_moverl1 = r.path(wanManMoverL1Path);
                    if (!datacenter.isEditionMode()) wan_man_moverl1.hide();
                    wan_man_moverl1.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    wan_man_moverl2.remove();
                    wan_man_moverl2 = r.path(wanManMoverL2Path);
                    wan_man_moverl2.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    if (!datacenter.isEditionMode()) wan_man_moverl2.hide();
                    wan_man_moverl3.remove();
                    wan_man_moverl3 = r.path(wanManMoverL3Path);
                    wan_man_moverl3.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    if (!datacenter.isEditionMode()) wan_man_moverl3.hide();
                }

                if (datacenter.getDCMatrix().getManMtxSize()!=0) {
                    man_lan_border.remove();
                    man_lan_border = r.path(manLanPath).attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
                    man_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:manLineTopY+manLineHeight/2}).rotate(-90);
                    man_lan_mover.attr({x:datacenter.getZoneCoord().x+moverSpan,y:lanLineTopY+moverYPoz});
                    man_lan_mover.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    man_lan_moverl1.remove();
                    man_lan_moverl1 = r.path(lanManMoverL1Path);
                    man_lan_moverl1.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    if (!datacenter.isEditionMode()) man_lan_moverl1.hide();
                    man_lan_moverl2.remove();
                    man_lan_moverl2 = r.path(lanManMoverL2Path);
                    man_lan_moverl2.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    if (!datacenter.isEditionMode()) man_lan_moverl2.hide();
                    man_lan_moverl3.remove();
                    man_lan_moverl3 = r.path(lanManMoverL3Path);
                    man_lan_moverl3.attr({stroke: moverStrokeColor,"stroke-width": datacenter.getStrokeWidth()});
                    if (!datacenter.isEditionMode()) man_lan_moverl3.hide();
                }
                lan_title.rotate(90).attr({x:datacenter.getZoneCoord().x+datacenter.getZoneSpan()/2,y:lanLineTopY+lanLineHeight/2}).rotate(-90);
                lan_dc_border.remove();
                lan_dc_border = r.path(lanDcPath).attr({stroke: splitterLineColor, "stroke-dasharray": datacenter.getStrokeDasharray(), "stroke-width": datacenter.getStrokeWidth()});
            }
        };

        return dcSplitter;
    });