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
            //noinspection JSUnresolvedVariable
            this.epURL      = JSONEndpointDesc.endpointURL;
            //noinspection JSUnresolvedVariable
            this.epID       = JSONEndpointDesc.endpointID;
            //noinspection JSUnresolvedVariable
            this.properties = JSONEndpointDesc.endpointProperties;
            this.epLinks    = [];
            this.epNode     = Node_;
            this.epIsPushed = false;
            this.linkAvgX   = 0;
            this.linkAvgY   = 0;
            this.linkAvgT   = 0;
            this.circle     = null;
            //noinspection JSUnresolvedVariable
            this.color      = ((this.properties!=null && this.properties.primaryApplication!=null && this.properties.primaryApplication.color!=null) ?
                                        "#" + this.properties.primaryApplication.color :(this.epNode!=null) ? this.epNode.color : Raphael.getColor());
            this.txt12      = params.endpoint_txtBxURLTitle;
            this.txt10      = params.endpoint_txtBxURLDef;
            this.label      = null;
            this.frame      = null;
            this.x          = 0;
            this.y          = 0;
            this.epIsPosed  = false;

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
            this.sWidth    = params.endpoint_strokeWidth;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.endpoint_menuFillColor;
            //this.menuStrokeColor   = params.endpoint_menuStrokeColor;
            this.menuOpacity       = params.endpoint_menuOpacity;
            this.menuStrokeWidth   = params.endpoint_menuStrokeWidth;
            //this.menuMainTitleTXT  = params.endpoint_menuMainTitle;
            //this.menuFieldTXT      = params.endpoint_menuFields;
            this.menuHided         = true;

            this.mvx        = 0;
            this.mvy        = 0;
            this.lmvx       = 0;
            this.lmvy       = 0;

            this.endpointMenuSet = null;
            this.endpointMenuTitle = null;
            this.endpointMenuProperties = null;
            this.endpointMenuPropertiesRect = null;

            this.endpointMainTitleTXT  = params.endpoint_menuMainTitle;
            this.endpointFieldTXT      = params.endpoint_menuFields;
            this.endpointFieldTXTOver  = params.endpoint_menuFieldsOver;

            var epRef = this;

            var calcLinkT = function(linkX, linkY) {
                var asin = Math.asin(linkY / (Math.sqrt(linkX*linkX+linkY*linkY))), linkT;
                if (linkY > 0 && linkX > 0) {
                    linkT = asin; // 0 =< as < PI/2  & 0 =< at < PI/2
                } else if (linkY > 0 && linkX < 0) {
                    linkT = Math.PI - asin; // 0 < as < PI/2 & PI/2 < at < PI
                } else if (linkY < 0 && linkX < 0) {
                    linkT = Math.PI - asin ; // -PI/2 < as < 0 & PI < at < 3PI/2
                } else if (linkY < 0 && linkX > 0) {
                    linkT = 2*Math.PI + asin ; // -PI/2 < as < 0 & 3PI/2 < at < 2PI
                } else if (linkY == 0) {
                    if (linkX==0) {
                        linkT=0;
                    } else if (linkX<0) {
                        linkT=Math.PI;
                    } else {
                        linkT=Math.PI*2;
                    }
                } else if (linkX == 0) {
                    if (linkY==0) {
                        linkT=0;
                    } else if (linkY>0){
                        linkT=Math.PI/2;
                    } else {
                        linkT=3*Math.PI/2;
                    }
                }
                return linkT
            };

            var mouseDown = function(e){
                    if (e.which == 3) {
                        if (epRef.properties != null && epRef.menuHided) {
                            epRef.menuSet = epRef.endpointMenuSet;
                            epRef.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = epRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    epRef.menuSet[i].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy") +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = epRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": epRef.circle.attr("cx") - fieldRectWidth/2, "y": epRef.circle.attr("cy")+30 - fieldRectHeight/2});
                                    epRef.menuSet[i+1].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy")+30});
                                    i++;
                                } else {
                                    fieldRect = epRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy")+30+(i-1)*15});
                                    epRef.menuSet[i+1].attr({"x": epRef.circle.attr("cx"), "y": epRef.circle.attr("cy")+30+(i-1)*15});
                                    i++;
                                }
                            }
                            epRef.menu = epRef.r.menu(epRef.circle.attr("cx"),epRef.circle.attr("cy")+10,epRef.menuSet).
                                attr({fill:epRef.menuFillColor, stroke: epRef.color, "stroke-width": epRef.menuStrokeWidth, "fill-opacity": epRef.menuOpacity});
                            epRef.menu.mousedown(menuMouseDown);
                            epRef.menu.toFront();
                            epRef.menuSet.toFront();
                            epRef.menuSet.show();
                            epRef.menuHided=false;
                        } else if (epRef.properties != null) {
                            epRef.menu.toBack();
                            epRef.menuSet.toBack();
                            epRef.menu.hide();
                            epRef.menuSet.hide();
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
                    if (this.properties != null && e.which == 3) {
                        epRef.menu.toBack();
                        epRef.menuSet.toBack();
                        epRef.menu.hide();
                        epRef.menuSet.hide();
                        epRef.menuHided=true;
                        epRef.rightClick=true;
                        if (epRef.r.getDisplayMainMenu())
                            epRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        epRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(epRef.endpointFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(epRef.endpointFieldTXT);
                },
                menuFieldPropertyClick = function(e) {
                    if (e.which != 3) {
                        //noinspection JSUnresolvedVariable
                        var details = "<br/> <b>URL</b> : " + epRef.epURL +
                            "<br/>" +
                            ((epRef.properties!=null && epRef.properties.primaryApplication!=null) ? "<br/> <b>Primary application</b> : " + epRef.properties.primaryApplication.name + "<br/>": "");

                        var sortedKeys = [];

                        for (var key in epRef.properties)
                            if (key !== "primaryApplication")
                                if (epRef.properties.hasOwnProperty(key))
                                    sortedKeys.push(key);
                        sortedKeys.sort();

                        for (var i = 0, ii = sortedKeys.length; i < ii; i++)
                            details = helper_.propertiesDisplay(details, sortedKeys[i], epRef.properties[sortedKeys[i]])

                        details += "<br/>";

                        helper_.dialogOpen("epDetail"+ epRef.epNode.cID + "_" + epRef.epNode.ID + "_" + epRef.epID, "Details of " + epRef.epURL, details);
                    }
                };

            var epDragger = function () {
                    if(!epRef.rightClick)
                        epRef.moveInit();
                },
                epMove = function (dx, dy) {
                    var zoomedMoveCoord = epRef.r.getZPDZoomedMoveCoord(dx, dy);
                    dx = zoomedMoveCoord.dx; dy = zoomedMoveCoord.dy;
                    if (!epRef.rightClick) {
                        var att = {cx: epRef.cx + dx, cy: epRef.cy + dy};//,

                        if (epRef.epNode!=null && !epRef.epNode.isMoving) {
                            var topLeftX     = epRef.epNode.getRectCornerPoints().topLeftX,
                                topLeftY     = epRef.epNode.getRectCornerPoints().topLeftY,
                                bottomRightX = epRef.epNode.getRectCornerPoints().bottomRightX,
                                bottomRightY = epRef.epNode.getRectCornerPoints().bottomRightY,
                                topLeftRadX  = epRef.epNode.getRectCornerPoints().TopLeftRadX,
                                topLeftRadY  = epRef.epNode.getRectCornerPoints().TopLeftRadY,
                                bottomRightRadX = epRef.epNode.getRectCornerPoints().BottomRightRadX,
                                bottomRightRadY = epRef.epNode.getRectCornerPoints().BottomRightRadY,
                                middleY = epRef.epNode.getRectMiddlePoint().y,
                                cornerRad = epRef.epNode.cornerRad;

                            /*
                             * is in the node
                             */
                            if ((att.cx > topLeftX) && (att.cx < bottomRightX ) && (att.cy > topLeftY) && (att.cy < bottomRightY)) {
                                var teta=null;
                                if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > topLeftY && att.cy <= middleY) {
                                    att.cy = topLeftY;
                                } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > middleY && att.cy < bottomRightY) {
                                    att.cy = bottomRightY;
                                } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                    teta = Math.atan((topLeftRadX-att.cx)/(topLeftRadY-att.cy));
                                    att.cx = topLeftRadX - cornerRad*Math.sin(teta);
                                    att.cy = topLeftRadY - cornerRad*Math.cos(teta);
                                } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                    teta = Math.atan((att.cy-bottomRightRadY)/(topLeftRadX-att.cx));
                                    att.cx = topLeftRadX - cornerRad*Math.cos(teta);
                                    att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                } else if (att.cx > bottomRightRadX && att.cy < topLeftRadY) {
                                    teta = Math.atan((topLeftRadY-att.cy)/(att.cx - bottomRightRadX));
                                    att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                    att.cy = topLeftRadY - cornerRad*Math.sin(teta);
                                } else if (att.cx > bottomRightRadX && att.cy > bottomRightRadY) {
                                    teta = Math.atan((att.cy-bottomRightRadY)/(att.cx - bottomRightRadX));
                                    att.cx = bottomRightRadX + cornerRad*Math.cos(teta);
                                    att.cy = bottomRightRadY + cornerRad*Math.sin(teta);
                                } else if (att.cx < topLeftRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                    att.cx = topLeftX;
                                    att.cy = middleY;
                                } else if (att.cx > bottomRightRadX && att.cy > topLeftRadY && att.cy < bottomRightRadY) {
                                    att.cx = bottomRightX;
                                    att.cy = middleY;
                                }
                            } else {
                                /*
                                 * is outside the node
                                 */
                                var dist = null;
                                if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy < topLeftY) {
                                    att.cy = topLeftY;
                                } else if (att.cx > topLeftRadX && att.cx < bottomRightRadX && att.cy > bottomRightY) {
                                    att.cy = bottomRightY;
                                } else if (att.cx < topLeftRadX && att.cy < topLeftRadY) {
                                    dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                    att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                    att.cy=(att.cy-topLeftRadY)*cornerRad/dist + topLeftRadY;
                                } else if (att.cx < topLeftRadX && att.cy > bottomRightRadY) {
                                    dist = Math.sqrt((att.cx-topLeftRadX)*(att.cx-topLeftRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                    att.cx=(att.cx-topLeftRadX)*cornerRad/dist + topLeftRadX;
                                    att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                } else if (att.cx >= bottomRightRadX && att.cy <= topLeftRadY) {
                                    dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX)+(att.cy-topLeftRadY)*(att.cy-topLeftRadY));
                                    att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                    att.cy=topLeftRadY - (-att.cy+topLeftRadY)*cornerRad/dist;
                                } else if (att.cx >= bottomRightRadX && att.cy >= bottomRightRadY){
                                    dist = Math.sqrt((att.cx-bottomRightRadX)*(att.cx-bottomRightRadX) + (att.cy-bottomRightRadY)*(att.cy-bottomRightRadY));
                                    att.cx=(att.cx-bottomRightRadX)*cornerRad/dist + bottomRightRadX;
                                    att.cy=(att.cy-bottomRightRadY)*cornerRad/dist + bottomRightRadY;
                                } else if (att.cy < topLeftY) {
                                    att.cy = topLeftY;
                                } else if (att.cy > bottomRightY) {
                                    att.cy = bottomRightY;
                                } else if (att.cx < topLeftX) {
                                    att.cx = topLeftX;
                                } else if (att.cx > bottomRightX) {
                                    att.cx = bottomRightX;
                                }
                            }
                        }

                        epRef.r.move(att.cx-epRef.cx, att.cy-epRef.cy);
                        epRef.r.safari();
                    }
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
                    epRef.label[0].attr({"x": labelX, "y": labelY, fill: "#fff"});
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
                    epRef.frame = epRef.r.popup(epRef.circle.x, epRef.circle.y, epRef.label, popupOrientation).attr({fill: "#000", stroke: epRef.color, "stroke-width": 2, "fill-opacity": .7});
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

                    epRef.linkAvgT = calcLinkT(epRef.linkAvgX, epRef.linkAvgY);

                    link_.setBPMulticast({x:bp.x,y:bp.y})

                    if (this.epNode!=null && !this.epIsPushed) {
                        this.epNode.pushEndpoint(this);
                        this.epIsPushed = true;
                    }
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
                epRef.linkAvgT = calcLinkT(epRef.linkAvgX, epRef.linkAvgY);

                if (this.epNode!=null && !this.epIsPushed) {
                    this.epNode.pushEndpoint(this);
                    this.epIsPushed = true;
                }
                //helper_.debug("linkAvgPoint: (" + this.linkAvgX + "," + this.linkAvgY + "," + this.linkAvgT + ")");
            };

            this.calcLinkAbsPoz = function() {
                var peerEpsPosed = true, peerEpAvgX = 0, peerEpAvgY = 0, ret = null, i, ii;
                var linkAbsX, linkAbsY, linkAbsT;
                for (i=0, ii=this.epLinks.length; i<ii; i++)
                    if (this.epLinks[i].getPeerEp(this)!=null && this.epLinks[i].getPeerEp(this).epIsPosed) {
                        peerEpAvgX += this.epLinks[i].getPeerEp(this).x;
                        peerEpAvgY += this.epLinks[i].getPeerEp(this).y;
                    } else {
                        peerEpsPosed = false;
                        break;
                    }

                if (peerEpsPosed) {
                    peerEpAvgX = peerEpAvgX / this.epLinks.length;
                    peerEpAvgY = peerEpAvgY / this.epLinks.length;

                    linkAbsX = peerEpAvgX - this.epNode.getRectMiddlePoint().x;
                    linkAbsY = this.epNode.getRectMiddlePoint().y - peerEpAvgY;
                    linkAbsT = calcLinkT(linkAbsX, linkAbsY);

                    ret = {
                        x: linkAbsX,
                        y: linkAbsY,
                        t: linkAbsT
                    }
                }

                return ret;
            };

            this.getLinkPoz = function() {
                var ret = this.calcLinkAbsPoz();
                if (ret == null) {
                    ret = {
                        x: this.linkAvgX,
                        y: this.linkAvgY,
                        t: this.linkAvgT
                    };
                }
                return ret;
            };

            this.setPoz = function(x_,y_) {
                this.x = x_;
                this.y = y_;
                this.epIsPosed = true;
            };

            this.resetPoz = function() {
                this.epIsPushed = false;
                this.epIsPosed = false;
                this.epNode.popEndpoint(this);
                this.linkAvgX   = 0;
                this.linkAvgY   = 0;
                this.linkAvgT   = 0;
            };

            this.clear = function() {
                this.circle.remove();
                this.label.remove();
                this.frame.remove();
                if (this.properties != null) {
                    this.endpointMenuTitle.remove();
                    this.endpointMenuPropertiesRect.remove();
                    this.endpointMenuProperties.remove();
                    this.endpointMenuSet.clear();
                }
            };

            this.print = function(r_) {
                this.r=r_;

                if (this.color == 0) this.color = this.epNode.color;

                this.circle = this.r.circle(this.x,this.y);
                this.circle.attr({fill: this.color, stroke: this.color, "fill-opacity": this.oUnselected, "r": this.rUnselected,"stroke-width": this.sWidth, cursor: "crosshair"});
                this.circle.attr({guide: this.epNode.rectPath});
                this.circle.mousedown(mouseDown);
                this.circle.drag(epMove, epDragger, epUP);
                this.circle.dblclick(epdClick);

                this.label = this.r.set();
                this.label.push(this.r.text(this.circle.attr("cx"), this.circle.attr("cy"), "Endpoint URL : ").attr(this.txt12));
                this.label.push(this.r.text(this.circle.attr("cx"), this.circle.attr("cy"), this.epURL).attr(this.txt10));
                this.label.toBack();
                this.label.hide();

                this.frame = this.r.popup(this.circle.x, this.circle.y, this.label, "right").
                    attr({fill: this.frmFillColor, stroke: this.frmStrokeColor, "stroke-width": this.sWidth, "fill-opacity": this.frmOpacity});
                this.frame.toBack();
                this.frame.hide();

                if (this.properties != null) {
                    this.endpointMenuTitle = this.r.text(0, 10, "Endpoint menu").attr(this.endpointMainTitleTXT);
                    var fieldTitle = "Display all properties";
                    this.endpointMenuPropertiesRect = this.r.rect(0, 10, fieldTitle.width(this.endpointFieldTXT), fieldTitle.height(this.endpointFieldTXT));
                    this.endpointMenuPropertiesRect.attr({fill: this.color, stroke: this.color, "fill-opacity": 0, "stroke-width": 0});
                    this.endpointMenuPropertiesRect.mouseover(menuFieldOver);
                    this.endpointMenuPropertiesRect.mouseout(menuFieldOut);
                    this.endpointMenuPropertiesRect.mousedown(menuFieldPropertyClick);
                    this.endpointMenuProperties = this.r.text(0, 10, fieldTitle).attr(this.endpointFieldTXT);
                    this.endpointMenuProperties.mouseover(menuFieldOver);
                    this.endpointMenuProperties.mouseout(menuFieldOut);
                    this.endpointMenuProperties.mousedown(menuFieldPropertyClick);

                    this.endpointMenuSet = this.r.set();
                    this.endpointMenuSet.push(this.endpointMenuTitle);
                    this.endpointMenuSet.push(this.endpointMenuPropertiesRect);
                    this.endpointMenuSet.push(this.endpointMenuProperties);
                    this.endpointMenuSet.toBack();
                    this.endpointMenuSet.hide();
                }
            };

            this.toFront = function() {
                this.circle.toFront();
                //this.avgT.toFront();
            };

            this.moveInit = function() {
                var i, ii;
                this.r.endpointsOnMovePush(this);
                this.r.moveSetPush(this.circle);
                for (i = 0, ii = this.epLinks.length; i < ii; i++)
                    this.epLinks[i].moveInit();

                this.cx = this.circle.attr("cx");
                this.cy = this.circle.attr("cy");

                if (this.properties != null && !this.menuHided) {
                    this.menu.toBack();
                    this.menuSet.toBack();
                    this.menu.hide();
                    this.menuSet.hide();
                    this.menuHided=true;
                    if (this.r.getDisplayMainMenu())
                        this.r.setDisplayMainMenu(false);
                }
                if (this.labelHided==false) {
                    this.label.hide();
                    this.circle.attr("r",this.rUnselected);
                    this.labelHided=true;
                }
                if (this.frameHided==false) {
                    this.frame.hide();
                    this.frameHided = true;
                }
                this.isMoving = true;

                this.circle.animate({"fill-opacity": this.oSelected}, 500);
            };

            this.moveAction = function(dx,dy) {
                var i, link, up;
                this.lmvx = this.mvx; this.lmvy = this.mvy;
                this.mvx = dx; this.mvy = dy;
                if ((this.mvx!=this.lmvx) || (this.mvy!=this.lmvy)) {
                    for (i = this.epLinks.length; i--;) {
                        link = this.epLinks[i];

                        if (this.r.isLinkToUp(link)) {
                            if (link.getMulticastBus()!=null) {
                                this.chooseMulticastTargetBindingPointAndCalcPoz(link);
                            }
                            up = this.r.link(link.toCompute());
                            if (typeof up != 'undefined') {
                                link.toUpdate(up);
                            }
                        }
                    }
                }
            };

            this.moveUp = function() {
                var att = {cx: this.circle.attr("cx") + this.mvx, cy: this.circle.attr("cy") + this.mvy}, up, i, link;
                this.mvx = 0 ; this.mvy = 0;
                this.circle.attr(att);
                this.circle.animate({"fill-opacity": this.oUnselected}, 500);
                this.x = this.circle.attr("cx");
                this.y = this.circle.attr("cy");
                for (i = this.epLinks.length; i--;) {
                    link = this.epLinks[i];

                    if (!this.r.isLinkToUp(link)) {
                        if (link.getMulticastBus()!=null) {
                            this.chooseMulticastTargetBindingPointAndCalcPoz(link);
                        }
                        up = this.r.link(link.toCompute());
                        if (typeof up != 'undefined') {
                            link.toUpdate(up);
                        }
                    }
                }
                this.isMoving = false;
            };

            if (this.epNode!=null && !this.epIsPushed) {
                this.epNode.pushEndpoint(this);
                this.epIsPushed = true;
            }

        }

        return endpoint;
    });