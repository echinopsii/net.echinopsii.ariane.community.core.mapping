// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Endpoint                        │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure topology graph engine                            │ \\
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

            var r          = null,
                epURL      = JSONEndpointDesc.endpointURL,
                epID       = JSONEndpointDesc.endpointID,
                epLinks    = [],
                epNode     = Node_,
                epIsPushed = false,
                linkAvgX   = 0,
                linkAvgY   = 0,
                linkAvgT   = 0,
                circle     = null,
                color      = (epNode!=null) ? epNode.getColor() : Raphael.getColor(),
                txt12      = params.endpoint_txtBxURLTitle,
                txt10      = params.endpoint_txtBxURLDef,
                label      = null,
                frame      = null,
                x          = 0,
                y          = 0,
                labelHided = true,
                frameHided = true,
                isMoving   = false;

            var frmFillColor   = params.endpoint_frmFillColor,
                frmStrokeColor = params.endpoint_frmStrokeColor,
                frmOpacity     = params.endpoint_frmOpacity,
                rUnselected    = params.endpoint_radUnselec,
                rSelected      = params.endpoint_radSelec,
                oUnselected    = params.endpoint_opacUnselec,
                oSelected      = params.endpoint_opacSelec,
                strokeWidth    = params.endpoint_strokeWidth;

            var menu              = null,
                menuSet           = null,
                menuFillColor     = params.endpoint_menuFillColor,
                menuStrokeColor   = params.endpoint_menuStrokeColor,
                menuOpacity       = params.endpoint_menuOpacity,
                menuStrokeWidth   = params.endpoint_menuStrokeWidth,
                menuMainTitleTXT  = params.endpoint_menuMainTitle,
                menuFieldTXT      = params.endpoint_menuFields,
                menuHided         = true;

            var calcLinkAvgT = function() {
                var as = Math.asin(linkAvgY / (Math.sqrt(linkAvgX*linkAvgX+linkAvgY*linkAvgY)));
                if (linkAvgY > 0 && linkAvgX > 0) {
                    linkAvgT = as; // 0 =< as < PI/2  & 0 =< at < PI/2
                } else if (linkAvgY > 0 && linkAvgX < 0) {
                    linkAvgT = Math.PI - as; // 0 < as < PI/2 & PI/2 < at < PI
                } else if (linkAvgY < 0 && linkAvgX < 0) {
                    linkAvgT = Math.PI - as ; // -PI/2 < as < 0 & PI < at < 3PI/2
                } else if (linkAvgY < 0 && linkAvgX > 0) {
                    linkAvgT = 2*Math.PI + as ; // -PI/2 < as < 0 & 3PI/2 < at < 2PI
                } else if (linkAvgY == 0) {
                    if (linkAvgX==0) {
                        linkAvgT=0;
                    } else if (linkAvgX<0) {
                        linkAvgT=Math.PI;
                    } else {
                        linkAvgT=Math.PI*2;
                    }
                } else if (linkAvgX == 0) {
                    if (linkAvgY==0) {
                        linkAvgT=0;
                    } else if (linkAvgY>0){
                        linkAvgT=Math.PI/2;
                    } else {
                        linkAvgT=3*Math.PI/2;
                    }
                }
            };

            var chooseMulticastTargetBindingPointAndCalcPoz = function(link_) {
                if (link_.getMulticastBus().isInserted()) {
                    var bpList = link_.getMulticastBus().getMBus().getBindingPoints(),
                        bp     = null;

                    var minDist   = -1;
                    for (var i = 0, ii=bpList.length; i<ii; i++) {
                        var tmpLinkAvgX = bpList[i].x - epNode.getRectMiddlePoint().x, //left -> right => x>0
                            tmpLinkAvgY = epNode.getRectMiddlePoint().y-bpList[i].y,   //bottom -> top => y>0
                            tmpDist = Math.sqrt(tmpLinkAvgX*tmpLinkAvgX + tmpLinkAvgY*tmpLinkAvgY);
                        if (minDist==-1) {
                            minDist=tmpDist;
                            linkAvgX=tmpLinkAvgX;
                            linkAvgY=tmpLinkAvgY;
                            bp = bpList[i];
                        } else if (minDist>tmpDist) {
                            minDist = tmpDist;
                            linkAvgX=tmpLinkAvgX;
                            linkAvgY=tmpLinkAvgY;
                            bp = bpList[i];
                        }
                    }

                    calcLinkAvgT();

                    link_.setBPMulticast({x:bp.x,y:bp.y})
                }
            };

            var dragg = function() {
                    if (!menuHided) {
                        menu.remove();
                        menuSet.remove();
                        menuHided=true;
                        if (r.getDisplayMainMenu())
                            r.setDisplayMainMenu(false);
                    }
                    circle.animate({"fill-opacity": oSelected}, 500);
                    if (labelHided==false) {
                        label.hide();
                        circle.attr("r",rUnselected);
                        labelHided=true;
                    }
                    if (frameHided==false) {
                        frame.hide();
                        frameHided = true;
                    }
                    isMoving = true;
                },
                move = function(ox, oy, dx, dy) {
                    var att = {cx: ox + dx, cy: oy + dy},
                        ray = circle.attr("r");
                    helper_.debug("cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);

                    if (epNode!=null && !epNode.isMoving()) {
                        var topLeftX     = epNode.getRectCornerPoints().topLeftX,
                            topLeftY     = epNode.getRectCornerPoints().topLeftY,
                            bottomRightX = epNode.getRectCornerPoints().bottomRightX,
                            bottomRightY = epNode.getRectCornerPoints().bottomRightY,
                            topLeftRadX  = epNode.getRectCornerPoints().TopLeftRadX,
                            topLeftRadY  = epNode.getRectCornerPoints().TopLeftRadY,
                            bottomRightRadX = epNode.getRectCornerPoints().BottomRightRadX,
                            bottomRightRadY = epNode.getRectCornerPoints().BottomRightRadY,
                            middleX = epNode.getRectMiddlePoint().x,
                            middleY = epNode.getRectMiddlePoint().y,
                            cornerRad = epNode.getCornerRad();

                        if (!Raphael.isPointInsidePath(epNode.getRectPath,att.cx,att.cy)) {
                            helper_.debug("Not more on the path ! cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                            /*
                             * is in the node
                             */
                            if ((att.cx > topLeftX) && (att.cx < bottomRightX ) && (att.cy > topLeftY) && (att.cy < bottomRightY)) {
                                if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > topLeftY && att.cy <= middleY) {
                                    att.cy = topLeftY;
                                    helper_.debug("01 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > middleY && att.cy < bottomRightY) {
                                    att.cy = bottomRightY;
                                    helper_.debug("02 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                    var teta = Math.atan((topLeftRadX-att.cx)/(topLeftRadY-att.cy));
                                    att.cx = topLeftRadX - cornerRad*Math.sin(teta);
                                    att.cy = topLeftRadY - cornerRad*Math.cos(teta);
                                    helper_.debug("03 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                    var teta = Math.atan((att.cy-bottomRightRadY)/(topLeftRadX-att.cx));
                                    att.cx = topLeftRadX - cornerRad*Math.cos(teta);
                                    att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                    helper_.debug("04 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx > bottomRightRadX && att.cy < topLeftRadY) {
                                    var teta = Math.atan((topLeftRadY-att.cy)/(att.cx - bottomRightRadX));
                                    att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                    att.cy = topLeftRadY - cornerRad*Math.sin(teta);
                                    helper_.debug("05 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx > bottomRightRadX && att.cy > bottomRightRadY) {
                                    var teta = Math.atan((att.cy-bottomRightRadY)/(att.cx - bottomRightRadX));
                                    att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                    att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                    helper_.debug("06 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx < topLeftRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                    att.cx = topLeftX;
                                    att.cy = middleY;
                                    helper_.debug("07 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx > bottomRightRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                    att.cx = bottomRightX;
                                    att.cy = middleY;
                                    helper_.debug("08 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                }  else {
                                    helper_.debug("09 I'm lost !");
                                }
                            } else {
                                /*
                                 * is outside the node
                                 */
                                if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy < topLeftY) {
                                    att.cy = topLeftY;
                                    helper_.debug("10 bis cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > bottomRightY) {
                                    att.cy = bottomRightY;
                                    helper_.debug("10 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                    var dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                    att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                    att.cy=(att.cy-topLeftRadY)*cornerRad/dist + topLeftRadY;
                                    helper_.debug("11 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                    var dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                    att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                    att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                    helper_.debug("12 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx >= bottomRightRadX && att.cy <= topLeftRadY) {
                                    var dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX)+(att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                    helper_.debug("dist:" + dist + "; " + " rad:" + cornerRad);
                                    att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                    att.cy=topLeftRadY - (-att.cy+topLeftRadY)*cornerRad/dist;
                                    helper_.debug("13 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else if (att.cx >= bottomRightRadX && att.cy >= bottomRightRadY){
                                    helper_.debug("14 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                    var dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                    att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                    att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                    helper_.debug("11 cx:"+ att.cx + "; cy:" + att.cy + "; r:" + ray);
                                } else {
                                    helper_.debug("12 I'm lost !");
                                }
                            }
                        }
                    }

                    circle.attr(att);
                    for (var i = epLinks.length; i--;) {
                        if (epLinks[i].getMulticastBus()!=null) {
                            chooseMulticastTargetBindingPointAndCalcPoz(epLinks[i]);
                        }
                        var up = r.link(epLinks[i].toCompute());
                        if (typeof up != 'undefined') {
                            helper_.debug(up);
                            epLinks[i].toUpdate(up);
                        }
                    }
                    r.safari();
                },
                up = function() {
                    circle.animate({"fill-opacity": oUnselected}, 500);
                    x = circle.attr("cx");
                    y = circle.attr("cy");
                    isMoving=false;
                },
                mouseDown = function(e){
                    if (e.which == 3) {
                        if (menuHided) {
                            menuSet = r.getEndpointMenuSet();
                            menuSet.mousedown(menuMouseDown);
                            for (var i = 0, ii = menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    menuSet[i].attr({"x": circle.attr("cx"), "y": circle.attr("cy") +10, fill: color});
                                else if (i==1)
                                    menuSet[i].attr({"x": circle.attr("cx"), "y": circle.attr("cy") + 30});
                                else
                                    menuSet[i].attr({"x": circle.attr("cx"), "y": circle.attr("cy") + 30 + (i-1)*15});
                            }
                            menu = r.menu(circle.attr("cx"),circle.attr("cy")+10,menuSet).attr({fill: menuFillColor, stroke: menuStrokeColor, "stroke-width": menuStrokeWidth, "fill-opacity": menuOpacity});
                            menu.mousedown(menuMouseDown);
                            menu.toFront();
                            menuSet.toFront();
                            menuSet.show();
                            menuHided=false;
                        } else {
                            menu.remove();
                            menuSet.remove();
                            menuHided=true;
                        }
                        rightClick=true;
                        if (r.getDisplayMainMenu())
                            r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        menu.remove();
                        menuSet.remove();
                        menuHided=true;
                        rightClick=true;
                        if (r.getDisplayMainMenu())
                            r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        rightClick=false;
                    }
                };

            var epDragger = function () {
                    if(!rightClick) {
                        this.ox = circle.attr("cx");
                        this.oy = circle.attr("cy");
                        dragg();
                    }
                },
                epMove = function (dx, dy) {
                    if (!rightClick)
                        move(this.ox,this.oy,dx,dy);
                },
                epUP = function () {
                    if (!rightClick)
                        up();
                };

            var epdClick = function () {
                var maxX = Math.max(label[0].getBBox().width,label[1].getBBox().width),
                    maxY = Math.max(label[0].getBBox().height,label[1].getBBox().height),
                    labelX = null, labelY = null, popupOrientation = null;

                if (epNode!=null && !epNode.isMoving()) {
                    var topLeftX = epNode.getRectCornerPoints().topLeftX,
                        topLeftY = epNode.getRectCornerPoints().topLeftY,
                        bottomRightX = epNode.getRectCornerPoints().bottomRightX,
                        bottomRightY = epNode.getRectCornerPoints().bottomRightY;

                    if (circle.attr("cx") == topLeftX) {
                        labelX = circle.attr("cx")-(maxX/2+14);
                        labelY = circle.attr("cy");
                        popupOrientation = "left";
                    } else if (circle.attr("cx") == bottomRightX) {
                        labelX = circle.attr("cx")+maxX/2+14;
                        labelY = circle.attr("cy");
                        popupOrientation = "right";
                    } else if (circle.attr("cy") == topLeftY) {
                        labelX = circle.attr("cx");
                        labelY = circle.attr("cy")-(maxY/2+30);
                        popupOrientation = "top";
                    } else if (circle.attr("cy") == bottomRightY) {
                        labelX = circle.attr("cx");
                        labelY = circle.attr("cy")+(maxY/2+30);
                        popupOrientation = "bottom";
                    } else if (Math.abs(circle.attr("cx")-topLeftX)<20) {
                        labelX = circle.attr("cx")-(maxX/2+14);
                        labelY = circle.attr("cy");
                        popupOrientation = "left";
                    } else if (Math.abs(circle.attr("cx")-bottomRightX)<20) {
                        labelX = circle.attr("cx")+maxX/2+14;
                        labelY = circle.attr("cy");
                        popupOrientation = "right";
                    } else if (Math.abs(circle.attr("cy")-topLeftY)<20) {
                        labelX = circle.attr("cx");
                        labelY = circle.attr("cy")-(maxY/2+30);
                        popupOrientation = "top";
                    } else if (Math.abs(circle.attr("cy")-bottomRightY)<20) {
                        labelX = circle.attr("cx");
                        labelY = circle.attr("cy")+(maxY/2+30);
                        popupOrientation = "bottom";
                    } else {
                        labelX = circle.attr("cx")+maxX/2+14;
                        labelY = circle.attr("cy");
                        popupOrientation = "right";
                    }
                }

                if (labelHided==true) {
                    circle.attr("r",rSelected);
                    label[0].attr({"x": labelX, "y": labelY, fill: color});
                    label[1].attr({"x": labelX, "y": labelY+15});
                    label.toFront();
                    label.show();
                    labelHided = false;
                } else {
                    label.toBack();
                    label.hide();
                    circle.attr("r",rUnselected);
                    labelHided = true;
                }

                if (frameHided==true) {
                    frame = r.popup(circle.x, circle.y, label, popupOrientation).attr({fill: "#000", stroke: "#666", "stroke-width": 2, "fill-opacity": .7});
                    frame.show();
                    frameHided = false;
                } else {
                    frame.toBack();
                    frame.hide();
                    frameHided = true;
                }
            };

            this.dragger = function() {
                this.cx = circle.attr("cx");
                this.cy = circle.attr("cy");
                dragg();
            };

            this.mover = function(dx,dy) {
                move(this.cx,this.cy,dx,dy);
            };

            this.uper = function() {
                up();
            };

            this.toString = function() {
                return "{\n Endpoint " + epURL + " : (" + linkAvgX + "," + linkAvgY + "," + linkAvgT +")\n}";
            };

            this.getNode = function() {
                return epNode;
            };

            this.getLinks = function() {
                return epLinks;
            };

            this.pushLink = function(link_) {
                epLinks.push(link_);
            };

            this.getURL = function() {
                return epURL;
            };

            this.getID = function() {
                return epID;
            };

            this.isMoving = function() {
                return isMoving;
            };

            this.chooseMulticastTargetBindingPointAndCalcPoz = function(link_) {
                chooseMulticastTargetBindingPointAndCalcPoz(link_);
            }

            this.calcLinkAvgPoz = function(link_) {
                var peerEp   = link_.getPeerEp(this);
                helper_.debug("thisEP : " + this.toString());
                helper_.debug("peerEP : " + peerEp.toString());

                var peerNode = peerEp.getNode();
                helper_.debug("epNode   : " + epNode.toString());
                helper_.debug("peerNode : " + peerNode.toString());

                linkAvgX = linkAvgX + (peerNode.getRectMiddlePoint().x-epNode.getRectMiddlePoint().x); //left -> right => x>0
                linkAvgY = linkAvgY + (epNode.getRectMiddlePoint().y-peerNode.getRectMiddlePoint().y); //bottom -> top => y>0

                calcLinkAvgT();

                helper_.debug("linkAvgPoint: (" + linkAvgX + "," + linkAvgY + "," + linkAvgT + ")");
            };

            this.getCircle = function() {
                return circle;
            };

            this.setCircle = function(circle_) {
                circle=circle_;
            };

            this.getLinkAvgPoz = function() {
                return {
                    x: linkAvgX,
                    y: linkAvgY,
                    t: linkAvgT
                };
            };

            this.setPoz = function(x_,y_) {
                var ray = rUnselected,
                    topLeftX = epNode.getRectCornerPoints().topLeftX,
                    topLeftY     = epNode.getRectCornerPoints().topLeftY,
                    bottomRightX = epNode.getRectCornerPoints().bottomRightX,
                    bottomRightY = epNode.getRectCornerPoints().bottomRightY,
                    topLeftRadX  = epNode.getRectCornerPoints().TopLeftRadX,
                    topLeftRadY  = epNode.getRectCornerPoints().TopLeftRadY,
                    bottomRightRadX = epNode.getRectCornerPoints().BottomRightRadX,
                    bottomRightRadY = epNode.getRectCornerPoints().BottomRightRadY,
                    middleX = epNode.getRectMiddlePoint().x,
                    middleY = epNode.getRectMiddlePoint().y,
                    cornerRad = epNode.getCornerRad();

                if (x_ < topLeftRadX && y_ < topLeftRadY) {
                    var dist = Math.sqrt((x_-topLeftRadX)*(x_-topLeftRadX) + (y_-topLeftRadY)*(y_-topLeftRadY));
                    x_=(x_-topLeftRadX)*cornerRad/dist + topLeftRadX;
                    y_=(y_-topLeftRadY)*cornerRad/dist + topLeftRadY;
                    helper_.debug("11 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ < topLeftRadX && y_ > bottomRightRadY) {
                    var dist = Math.sqrt((x_-topLeftRadX)*(x_-topLeftRadX) + (y_-bottomRightRadY)*(y_-bottomRightRadY));
                    x_=(x_-topLeftRadX)*cornerRad/dist + topLeftRadX;
                    y_=(y_-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                    helper_.debug("12 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ >= bottomRightRadX && y_ <= topLeftRadY) {
                    var dist = Math.sqrt((x_-bottomRightRadX)*(x_-bottomRightRadX)+(y_-topLeftRadY)*(y_-topLeftRadY));
                    helper_.debug("dist:" + dist + "; " + " rad:" + cornerRad);
                    x_=(x_-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                    y_=topLeftRadY - (-y_+topLeftRadY)*cornerRad/dist;
                    helper_.debug("13 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else if (x_ >= bottomRightRadX && y_ >= bottomRightRadY){
                    helper_.debug("14 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                    var dist = Math.sqrt((x_-bottomRightRadX)*(x_-bottomRightRadX) + (y_-bottomRightRadY)*(y_-bottomRightRadY));
                    x_=(x_-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                    y_=(y_-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                    helper_.debug("11 cx:"+ x_ + "; cy:" + y_ + "; r:" + ray);
                } else {
                    helper_.debug("12 I'm lost !");
                }

                x = x_;
                y = y_;
            };

            this.resetPoz = function() {
                epIsPushed = false;
                epNode.popEndpoint(this);
                linkAvgX   = 0;
                linkAvgY   = 0;
                linkAvgT   = 0;
            };

            this.print = function(r_) {
                r=r_;

                if (epNode!=null && !epIsPushed) {
                    epNode.pushEndpoint(this);
                    epIsPushed = true;
                }

                circle = r.circle(x,y);
                circle.attr({fill: color, stroke: color, "fill-opacity": oUnselected, "r": rUnselected,"stroke-width": strokeWidth, cursor: "crosshair"});
                circle.attr({guide: epNode.getRectPath()});
                circle.mousedown(mouseDown);
                circle.drag(epMove, epDragger, epUP);
                circle.dblclick(epdClick);

                label = r.set();
                label.push(r.text(circle.attr("cx"), circle.attr("cy"), "Endpoint URL : ").attr(txt12));
                label.push(r.text(circle.attr("cx"), circle.attr("cy"), epURL).attr(txt10));
                label.toBack();
                label.hide();

                frame = r.popup(circle.x, circle.y, label, "right").attr({fill: frmFillColor, stroke: frmStrokeColor, "stroke-width": strokeWidth, "fill-opacity": frmOpacity});
                frame.toBack();
                frame.hide();
            };

            this.toFront = function() {
                circle.toFront();
            };
        };

        return endpoint;
    });
