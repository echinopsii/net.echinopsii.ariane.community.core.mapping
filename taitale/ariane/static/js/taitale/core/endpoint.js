// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Endpoint                        │ \\
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
        'raphael',
        'taitale-helper',
        'taitale-params'
    ],
    function(Raphael, helper, params){
        function endpoint(JSONEndpointDesc, Node_) {
            var helper_    = new helper();

            this.r          = null;
            this.epURL      = JSONEndpointDesc.endpointURL;
            this.epID       = JSONEndpointDesc.endpointID;
            this.properties = JSONEndpointDesc.endpointProperties;
            this.epLinks    = [];
            this.epNode     = Node_;
            this.epIsPushed = false;
            this.linkAvgX   = 0;
            this.linkAvgY   = 0;
            this.linkAvgT   = 0;
            this.circle     = null;
            this.color      = ((this.properties!=null && this.properties.primaryApplication!=null && this.properties.primaryApplication.color!=null) ?
                                        "#" + this.properties.primaryApplication.color :(this.epNode!=null) ? this.epNode.color : Raphael.getColor());
            this.txt12      = params.endpoint_txtBxURLTitle;
            this.txt10      = params.endpoint_txtBxURLDef;
            this.label      = null;
            this.frame      = null;
            this.x          = 0;
            this.y          = 0;

            this.labelHided   = true;
            this.frameHided   = true;
            this.isMoving     = false;

            this.frmFillColor   = params.endpoint_frmFillColor;
            this.frmStrokeColor = params.endpoint_frmStrokeColor;
            this.frmOpacity     = params.endpoint_frmOpacity;
            this.rUnselected    = params.endpoint_radUnselec;
            this.rSelected      = params.endpoint_radSelec;
            this.oUnselected    = params.endpoint_opacUnselec;
            this.oSelected      = params.endpoint_opacSelec;
            this.strokeWidth    = params.endpoint_strokeWidth;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.endpoint_menuFillColor
            this.menuStrokeColor   = params.endpoint_menuStrokeColor;
            this.menuOpacity       = params.endpoint_menuOpacity;
            this.menuStrokeWidth   = params.endpoint_menuStrokeWidth;
            this.menuMainTitleTXT  = params.endpoint_menuMainTitle;
            this.menuFieldTXT      = params.endpoint_menuFields;
            this.menuHided         = true;

            this.mvx        = 0;
            this.mvy        = 0;
            this.lmvx       = 0;
            this.lmvy       = 0;

            var epRef = this;

            var calcLinkAvgT = function() {
                var as = Math.asin(epRef.linkAvgY / (Math.sqrt(epRef.linkAvgX*epRef.linkAvgX+epRef.linkAvgY*epRef.linkAvgY)));
                if (epRef.linkAvgY > 0 && epRef.linkAvgX > 0) {
                    epRef.linkAvgT = as; // 0 =< as < PI/2  & 0 =< at < PI/2
                } else if (epRef.linkAvgY > 0 && epRef.linkAvgX < 0) {
                    epRef.linkAvgT = Math.PI - as; // 0 < as < PI/2 & PI/2 < at < PI
                } else if (epRef.linkAvgY < 0 && epRef.linkAvgX < 0) {
                    epRef.linkAvgT = Math.PI - as ; // -PI/2 < as < 0 & PI < at < 3PI/2
                } else if (epRef.linkAvgY < 0 && epRef.linkAvgX > 0) {
                    epRef.linkAvgT = 2*Math.PI + as ; // -PI/2 < as < 0 & 3PI/2 < at < 2PI
                } else if (epRef.linkAvgY == 0) {
                    if (epRef.linkAvgX==0) {
                        epRef.linkAvgT=0;
                    } else if (epRef.linkAvgX<0) {
                        epRef.linkAvgT=Math.PI;
                    } else {
                        epRef.linkAvgT=Math.PI*2;
                    }
                } else if (epRef.linkAvgX == 0) {
                    if (epRef.linkAvgY==0) {
                        epRef.linkAvgT=0;
                    } else if (epRef.linkAvgY>0){
                        epRef.linkAvgT=Math.PI/2;
                    } else {
                        epRef.linkAvgT=3*Math.PI/2;
                    }
                }
            };

            var move = function(dx, dy) {
                    var att = {cx: epRef.cx + dx, cy: epRef.cy + dy};//,
                    //ray = epRef.circle.attr("r");
                    //helper_.debug("cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);

                    if (epRef.epNode!=null && !epRef.epNode.isMoving) {
                        var topLeftX     = epRef.epNode.getRectCornerPoints().topLeftX,
                            topLeftY     = epRef.epNode.getRectCornerPoints().topLeftY,
                            bottomRightX = epRef.epNode.getRectCornerPoints().bottomRightX,
                            bottomRightY = epRef.epNode.getRectCornerPoints().bottomRightY,
                            topLeftRadX  = epRef.epNode.getRectCornerPoints().TopLeftRadX,
                            topLeftRadY  = epRef.epNode.getRectCornerPoints().TopLeftRadY,
                            bottomRightRadX = epRef.epNode.getRectCornerPoints().BottomRightRadX,
                            bottomRightRadY = epRef.epNode.getRectCornerPoints().BottomRightRadY,
                            //middleX = epRef.epNode.getRectMiddlePoint().x,
                            middleY = epRef.epNode.getRectMiddlePoint().y,
                            cornerRad = epRef.epNode.cornerRad;

                        /*
                         * is in the node
                         */
                        if ((att.cx > topLeftX) && (att.cx < bottomRightX ) && (att.cy > topLeftY) && (att.cy < bottomRightY)) {
                            var teta=null;
                            if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > topLeftY && att.cy <= middleY) {
                                att.cy = topLeftY;
                                //helper_.debug("01 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > middleY && att.cy < bottomRightY) {
                                att.cy = bottomRightY;
                                //helper_.debug("02 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                teta = Math.atan((topLeftRadX-att.cx)/(topLeftRadY-att.cy));
                                att.cx = topLeftRadX - cornerRad*Math.sin(teta);
                                att.cy = topLeftRadY - cornerRad*Math.cos(teta);
                                //helper_.debug("03 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                teta = Math.atan((att.cy-bottomRightRadY)/(topLeftRadX-att.cx));
                                att.cx = topLeftRadX - cornerRad*Math.cos(teta);
                                att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                //helper_.debug("04 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx > bottomRightRadX && att.cy < topLeftRadY) {
                                teta = Math.atan((topLeftRadY-att.cy)/(att.cx - bottomRightRadX));
                                att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                att.cy = topLeftRadY - cornerRad*Math.sin(teta);
                                //helper_.debug("05 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx > bottomRightRadX && att.cy > bottomRightRadY) {
                                teta = Math.atan((att.cy-bottomRightRadY)/(att.cx - bottomRightRadX));
                                att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                //helper_.debug("06 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx < topLeftRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                att.cx = topLeftX;
                                att.cy = middleY;
                                //helper_.debug("07 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx > bottomRightRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                att.cx = bottomRightX;
                                att.cy = middleY;
                                //helper_.debug("08 cx:"+ att.cx + "; cy:" + att.cy);
                            }  else {
                                //helper_.debug("09 I'm lost !");
                            }
                        } else {
                            /*
                             * is outside the node
                             */
                            var dist = null;
                            if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy < topLeftY) {
                                att.cy = topLeftY;
                                //helper_.debug("10 bis cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > bottomRightY) {
                                att.cy = bottomRightY;
                                //helper_.debug("10 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                att.cy=(att.cy-topLeftRadY)*cornerRad/dist + topLeftRadY;
                                //helper_.debug("11 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                //helper_.debug("12 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx >= bottomRightRadX && att.cy <= topLeftRadY) {
                                dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX)+(att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                //helper_.debug("dist:" + dist + "; " + " rad:" + cornerRad);
                                att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                att.cy=topLeftRadY - (-att.cy+topLeftRadY)*cornerRad/dist;
                                //helper_.debug("13 cx:"+ att.cx + "; cy:" + att.cy);
                            } else if (att.cx >= bottomRightRadX && att.cy >= bottomRightRadY){
                                //helper_.debug("14 cx:"+ att.cx + "; cy:" + att.cy);
                                dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                //helper_.debug("11 cx:"+ att.cx + "; cy:" + att.cy);
                            } else {
                                //helper_.debug("12 I'm lost !");
                            }
                        }
                    }

                    epRef.r.move(att.cx-epRef.cx, att.cy-epRef.cy);
                    epRef.r.safari();
                },
                mouseDown = function(e){
                    if (e.which == 3) {
                        if (epRef.menuHided) {
                            epRef.menuSet = epRef.r.getEndpointMenuSet();
                            epRef.menuSet.mousedown(menuMouseDown);
                            for (var i = 0, ii = epRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    epRef.menuSet[i].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy") +10, fill: epRef.color});
                                else if (i==1)
                                    epRef.menuSet[i].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy") + 30});
                                else
                                    epRef.menuSet[i].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy") + 30 + (i-1)*15});
                            }
                            epRef.menu = epRef.r.menu(epRef.circle.attr("cx"),epRef.circle.attr("cy")+10,epRef.menuSet).attr({fill: epRef.menuFillColor, stroke: epRef.menuStrokeColor, "stroke-width": epRef.menuStrokeWidth, "fill-opacity": epRef.menuOpacity});
                            epRef.menu.mousedown(menuMouseDown);
                            epRef.menu.toFront();
                            epRef.menuSet.toFront();
                            epRef.menuSet.show();
                            epRef.menuHided=false;
                        } else {
                            epRef.menu.remove();
                            epRef.menuSet.remove();
                            epRef.menuHided=true;
                        }
                        epRef.rightClick=true;
                        if (epRef.r.getDisplayMainMenu())
                            epRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        epRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        epRef.menu.remove();
                        epRef.menuSet.remove();
                        epRef.menuHided=true;
                        epRef.rightClick=true;
                        if (epRef.r.getDisplayMainMenu())
                            epRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        epRef.rightClick=false;
                    }
                };

            var epDragger = function () {
                    if(!epRef.rightClick) {
                        epRef.r.drag(epRef,"endpoint");
                    }
                },
                epMove = function (dx, dy) {
                    if (!epRef.rightClick)
                        move(dx,dy);
                },
                epUP = function () {
                    if (!epRef.rightClick) {
                        epRef.r.up()
                    }
                };

            var epdClick = function () {
                var maxX = Math.max(epRef.label[0].getBBox().width,epRef.label[1].getBBox().width),
                    maxY = Math.max(epRef.label[0].getBBox().height,epRef.label[1].getBBox().height),
                    labelX = null, labelY = null, popupOrientation = null;

                if (epRef.epNode!=null && !epRef.epNode.isMoving) {
                    var topLeftX = epRef.epNode.getRectCornerPoints().topLeftX,
                        topLeftY = epRef.epNode.getRectCornerPoints().topLeftY,
                        bottomRightX = epRef.epNode.getRectCornerPoints().bottomRightX,
                        bottomRightY = epRef.epNode.getRectCornerPoints().bottomRightY;

                    if (epRef.circle.attr("cx") == topLeftX) {
                        labelX = epRef.circle.attr("cx")-(maxX/2+14);
                        labelY = epRef.circle.attr("cy");
                        popupOrientation = "left";
                    } else if (epRef.circle.attr("cx") == bottomRightX) {
                        labelX = epRef.circle.attr("cx")+maxX/2+14;
                        labelY = epRef.circle.attr("cy");
                        popupOrientation = "right";
                    } else if (epRef.circle.attr("cy") == topLeftY) {
                        labelX = epRef.circle.attr("cx");
                        labelY = epRef.circle.attr("cy")-(maxY/2+30);
                        popupOrientation = "top";
                    } else if (epRef.circle.attr("cy") == bottomRightY) {
                        labelX = epRef.circle.attr("cx");
                        labelY = epRef.circle.attr("cy")+(maxY/2+30);
                        popupOrientation = "bottom";
                    } else if (Math.abs(epRef.circle.attr("cx")-topLeftX)<20) {
                        labelX = epRef.circle.attr("cx")-(maxX/2+14);
                        labelY = epRef.circle.attr("cy");
                        popupOrientation = "left";
                    } else if (Math.abs(epRef.circle.attr("cx")-bottomRightX)<20) {
                        labelX = epRef.circle.attr("cx")+maxX/2+14;
                        labelY = epRef.circle.attr("cy");
                        popupOrientation = "right";
                    } else if (Math.abs(epRef.circle.attr("cy")-topLeftY)<20) {
                        labelX = epRef.circle.attr("cx");
                        labelY = epRef.circle.attr("cy")-(maxY/2+30);
                        popupOrientation = "top";
                    } else if (Math.abs(epRef.circle.attr("cy")-bottomRightY)<20) {
                        labelX = epRef.circle.attr("cx");
                        labelY = epRef.circle.attr("cy")+(maxY/2+30);
                        popupOrientation = "bottom";
                    } else {
                        labelX = epRef.circle.attr("cx")+maxX/2+14;
                        labelY = epRef.circle.attr("cy");
                        popupOrientation = "right";
                    }
                }

                if (epRef.labelHided==true) {
                    epRef.circle.attr("r",epRef.rSelected);
                    epRef.label[0].attr({"x": labelX, "y": labelY, fill: epRef.color});
                    epRef.label[1].attr({"x": labelX, "y": labelY+15});
                    epRef.label.toFront();
                    epRef.label.show();
                    epRef.labelHided = false;
                } else {
                    epRef.label.toBack();
                    epRef.label.hide();
                    epRef.circle.attr("r",epRef.rUnselected);
                    epRef.labelHided = true;
                }

                if (epRef.frameHided==true) {
                    epRef.frame = epRef.r.popup(epRef.circle.x, epRef.circle.y, epRef.label, popupOrientation).attr({fill: "#000", stroke: "#666", "stroke-width": 2, "fill-opacity": .7});
                    epRef.frame.show();
                    epRef.frameHided = false;
                } else {
                    epRef.frame.toBack();
                    epRef.frame.hide();
                    epRef.frameHided = true;
                }
            };

            this.toString = function() {
                return "{\n Endpoint " + this.epURL + " : (" + this.linkAvgX + "," + this.linkAvgY + "," + this.linkAvgT +")\n}";
            };

            this.pushLink = function(link_) {
                this.epLinks.push(link_);
            };

            this.chooseMulticastTargetBindingPointAndCalcPoz = function(link_) {
                if (link_.getMulticastBus().isInserted) {
                    var bpList = link_.getMulticastBus().mbus.getBindingPoints(),
                        bp     = null,
                        busmvx = link_.getMulticastBus().mbus.mvx,
                        busmvy = link_.getMulticastBus().mbus.mvy;

                    var minDist   = -1;
                    for (var i = 0, ii=bpList.length; i<ii; i++) {
                        var tmpLinkAvgX = (bpList[i].x+busmvx) - (epRef.epNode.getRectMiddlePoint().x+epRef.epNode.mvx),//left -> right => x>0
                            tmpLinkAvgY = (epRef.epNode.getRectMiddlePoint().y+epRef.epNode.mvy)-(bpList[i].y+busmvy),  //bottom -> top => y>0
                            tmpDist = Math.sqrt(tmpLinkAvgX*tmpLinkAvgX + tmpLinkAvgY*tmpLinkAvgY);
                        if (minDist==-1) {
                            minDist=tmpDist;
                            epRef.linkAvgX=tmpLinkAvgX;
                            epRef.linkAvgY=tmpLinkAvgY;
                            bp = bpList[i];
                        } else if (minDist>tmpDist) {
                            minDist = tmpDist;
                            epRef.linkAvgX=tmpLinkAvgX;
                            epRef.linkAvgY=tmpLinkAvgY;
                            bp = bpList[i];
                        }
                    }

                    calcLinkAvgT();

                    link_.setBPMulticast({x:bp.x,y:bp.y})
                }
            };

            this.calcLinkAvgPoz = function(link_) {
                var peerEp   = link_.getPeerEp(this);
                //helper_.debug("thisEP : " + this.toString());
                //helper_.debug("peerEP : " + peerEp.toString());

                var peerNode = peerEp.epNode;
                //helper_.debug("this.epNode   : " + this.epNode.toString());
                //helper_.debug("peerNode : " + peerNode.toString());

                this.linkAvgX = this.linkAvgX + (peerNode.getRectMiddlePoint().x-this.epNode.getRectMiddlePoint().x); //left -> right => x>0
                this.linkAvgY = this.linkAvgY + (this.epNode.getRectMiddlePoint().y-peerNode.getRectMiddlePoint().y); //bottom -> top => y>0

                calcLinkAvgT();

                //helper_.debug("linkAvgPoint: (" + this.linkAvgX + "," + this.linkAvgY + "," + this.linkAvgT + ")");
            };

            this.getLinkAvgPoz = function() {
                return {
                    x: this.linkAvgX,
                    y: this.linkAvgY,
                    t: this.linkAvgT
                };
            };

            this.setPoz = function(x_,y_) {
                var ray = this.rUnselected,
                    topLeftX = this.epNode.getRectCornerPoints().topLeftX,
                    topLeftY     = this.epNode.getRectCornerPoints().topLeftY,
                    bottomRightX = this.epNode.getRectCornerPoints().bottomRightX,
                    bottomRightY = this.epNode.getRectCornerPoints().bottomRightY,
                    topLeftRadX  = this.epNode.getRectCornerPoints().TopLeftRadX,
                    topLeftRadY  = this.epNode.getRectCornerPoints().TopLeftRadY,
                    bottomRightRadX = this.epNode.getRectCornerPoints().BottomRightRadX,
                    bottomRightRadY = this.epNode.getRectCornerPoints().BottomRightRadY,
                    middleX = this.epNode.getRectMiddlePoint().x,
                    middleY = this.epNode.getRectMiddlePoint().y,
                    cornerRad = this.epNode.cornerRad;

                var dist = null;

                if (x_ < topLeftRadX && y_ < topLeftRadY) {
                    dist = Math.sqrt((x_-topLeftRadX)*(x_-topLeftRadX) + (y_-topLeftRadY)*(y_-topLeftRadY));
                    x_=(x_-topLeftRadX)*cornerRad/dist + topLeftRadX;
                    y_=(y_-topLeftRadY)*cornerRad/dist + topLeftRadY;
                    //helper_.debug("11 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ < topLeftRadX && y_ > bottomRightRadY) {
                    dist = Math.sqrt((x_-topLeftRadX)*(x_-topLeftRadX) + (y_-bottomRightRadY)*(y_-bottomRightRadY));
                    x_=(x_-topLeftRadX)*cornerRad/dist + topLeftRadX;
                    y_=(y_-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                    //helper_.debug("12 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ >= bottomRightRadX && y_ <= topLeftRadY) {
                    dist = Math.sqrt((x_-bottomRightRadX)*(x_-bottomRightRadX)+(y_-topLeftRadY)*(y_-topLeftRadY));
                    //helper_.debug("dist:" + dist + "; " + " rad:" + cornerRad);
                    x_=(x_-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                    y_=topLeftRadY - (-y_+topLeftRadY)*cornerRad/dist;
                    //helper_.debug("13 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ >= bottomRightRadX && y_ >= bottomRightRadY){
                    //helper_.debug("14 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                    dist = Math.sqrt((x_-bottomRightRadX)*(x_-bottomRightRadX) + (y_-bottomRightRadY)*(y_-bottomRightRadY));
                    x_=(x_-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                    y_=(y_-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                    //helper_.debug("11 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else {
                    //helper_.debug("12 I'm lost !");
                }

                this.x = x_;
                this.y = y_;
            };

            this.resetPoz = function() {
                this.epIsPushed = false;
                this.epNode.popEndpoint(this);
                this.linkAvgX   = 0;
                this.linkAvgY   = 0;
                this.linkAvgT   = 0;
            };

            this.print = function(r_) {
                this.r=r_;

                if (this.epNode!=null && !this.epIsPushed) {
                    this.epNode.pushEndpoint(this);
                    this.epIsPushed = true;
                }

                this.circle = this.r.circle(this.x,this.y);
                this.circle.attr({fill: this.color, stroke: this.color, "fill-opacity": this.oUnselected, "r": this.rUnselected,"stroke-width": this.strokeWidth, cursor: "crosshair"});
                this.circle.attr({guide: this.epNode.rectPath});
                this.circle.mousedown(mouseDown);
                this.circle.drag(epMove, epDragger, epUP);
                this.circle.dblclick(epdClick);

                this.label = this.r.set();
                this.label.push(this.r.text(this.circle.attr("cx"), this.circle.attr("cy"), "Endpoint URL : ").attr(this.txt12));
                this.label.push(this.r.text(this.circle.attr("cx"), this.circle.attr("cy"), this.epURL).attr(this.txt10));
                this.label.toBack();
                this.label.hide();

                this.frame = this.r.popup(this.circle.x, this.circle.y, this.label, "right").attr({fill: this.frmFillColor, stroke: this.frmStrokeColor, "stroke-width": this.strokeWidth, "fill-opacity": this.frmOpacity});
                this.frame.toBack();
                this.frame.hide();
            };

            this.toFront = function() {
                this.circle.toFront();
            };
        }

        return endpoint;
    });
