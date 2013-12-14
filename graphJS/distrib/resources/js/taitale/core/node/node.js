// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Node                            │ \\
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
        function node(JSONNodeDesc, container_) {
            var helper_       = new helper();

            var	ID            = JSONNodeDesc.nodeID,
                name          = JSONNodeDesc.nodeName,
                cID           = JSONNodeDesc.nodeContainerID,
                properties    = JSONNodeDesc.nodeProperties;

            var r             = null,
                nodeContainer = container_,
                color         = (nodeContainer!=null) ? nodeContainer.getColor() : Raphael.getColor(),
                nodeName      = null,
                nodeDesc      = null,
                nodeR         = null,
                rect          = null,
                rectPath      = null,
                rectPathLen   = null,
                rectPathOrg   = null,
                isMoving      = false;

            var	nodeEndpoints   = [],
            // ordered list of epAvgLinksTeta (Teta is the angle as : T = Y/sqrt(X*X+Y*Y))
                nodeEpAvgLinksT = [];

            var linkedBus         = [],
                linkedNodes       = [];

            var titleHeight   = params.node_titleHeight,
                txtTitleFont  = params.node_txtTitle,
                txtDescFont   = params.node_txtDesc;

            var rectWidth  = params.node_minWidth,
                rectHeight = params.node_minHeight;

            var menu              = null,
                menuSet           = null,
                menuFillColor     = params.node_menuFillColor,
                menuStrokeColor   = params.node_menuStrokeColor,
                menuOpacity       = params.node_menuOpacity,
                menuStrokeWidth   = params.node_menuStrokeWidth,
                menuMainTitleTXT  = params.node_menuMainTitle,
                menuFieldTXT      = params.node_menuFields,
                menuHided         = true;

            var oUnselected = params.node_opacUnselec,
                oSelected   = params.node_opacSelec,
                cornerRad   = params.node_cornerRad,
                strokeWidth = params.node_strokeWidth;

            // coord top left point
            var	rectTopLeftX  = 0,
                rectTopLeftY  = 0,
            // coord top top left rad point
                rectTopTopLeftRadX = 0,
                rectTopTopLeftRadY = 0,
            // coord top bottom left rad point
                rectTopBottomLeftRadX = 0,
                rectTopBottomLeftRadY = 0,
            // coord top left circle center point
                rectTopLeftRadX = 0,
                rectTopLeftRadY = 0,
            // coord top middle point
                rectTopMiddleX = 0,
                rectTopMiddleY = 0,
            // coord top right point
                rectTopRightX = 0,
                rectTopRightY = 0,
            // coord top top right rad point
                rectTopTopRightRadX = 0,
                rectTopTopRightRadY = 0,
            // coord top bottom right rad point
                rectTopBottomRightRadX = 0,
                rectTopBottomRightRadY = 0,
            // coord top right circle center point
                rectTopRightRadX = 0,
                rectTopRightRadY = 0,
            // coord middle left point
                rectMiddleLeftX = 0,
                rectMiddleLeftY = 0,
            // coord rect middle point
                rectMiddleX = 0,
                rectMiddleY = 0,
            // coord middle right point
                rectMiddleRightX = 0,
                rectMiddleRightY = 0,
            //coord bottom left point
                rectBottomLeftX = 0,
                rectBottomLeftY = 0,
            //coord bottom bottom left rad point
                rectBottomBottomLeftRadX = 0,
                rectBottomBottomLeftRadY = 0,
            //coord bottom top left rad point
                rectBottomTopLeftRadX = 0,
                rectBottomTopLeftRadY = 0,
            // coord bottome left circle center point
                rectBottomLeftRadX = 0,
                rectBottomLeftRadY = 0,
            //coord bottom middle point,
                rectBottomMiddleX = 0,
                rectBottomMiddleY = 0,
            //coord bottom right point,
                rectBottomRightX = 0,
                rectBottomRightY = 0,
            //coord bottom bottom right rad point,
                rectBottomBottomRightRadX = 0,
                rectBottomBottomRightRadY = 0,
            //coord bottom top right rad point
                rectBottomTopRightRadX = 0,
                rectBottomTopRightRadY = 0,
            // coord bottome right circle center point
                rectBottomRightRadX = 0,
                rectBottomRightRadY = 0;

            /**
             * x = abs of nodeR[0], y = ord of nodeR[0]
             */
            var defineRectPoints = function(x,y) {

                rectTopLeftX     = x;
                rectTopLeftY     = y;

                rectTopTopLeftRadX = rectTopLeftX + cornerRad;
                rectTopTopLeftRadY = rectTopLeftY;

                rectTopBottomLeftRadX = rectTopLeftX;
                rectTopBottomLeftRadY = rectTopLeftY + cornerRad;

                rectTopLeftRadX = rectTopTopLeftRadX;
                rectTopLeftRadY = rectTopBottomLeftRadY

                rectTopMiddleX   = rectTopLeftX + rectWidth/2;
                rectTopMiddleY   = rectTopLeftY;

                rectTopRightX    = rectTopLeftX + rectWidth;
                rectTopRightY    = rectTopLeftY;

                rectTopTopRightRadX = rectTopRightX - cornerRad;
                rectTopTopRightRadY = rectTopRightY;

                rectTopBottomRightRadX = rectTopRightX;
                rectTopBottomRightRadY = rectTopRightY + cornerRad;

                rectTopRightRadX = rectTopTopRightRadX;
                rectTopRightRadY = rectTopBottomRightRadY;

                rectMiddleLeftX  = rectTopLeftX;
                rectMiddleLeftY  = rectTopLeftY + rectHeight/2;

                rectMiddleRightX = rectTopRightX;
                rectMiddleRightY = rectMiddleLeftY;

                rectBottomLeftX  = rectTopLeftX;
                rectBottomLeftY  = rectTopLeftY + rectHeight;

                rectBottomTopLeftRadX = rectBottomLeftX;
                rectBottomTopLeftRadY = rectBottomLeftY - cornerRad;

                rectBottomBottomLeftRadX = rectBottomLeftX + cornerRad;
                rectBottomBottomLeftRadY = rectBottomLeftY;

                rectBottomLeftRadX = rectBottomBottomLeftRadX;
                rectBottomLeftRadY = rectBottomTopLeftRadY;

                rectBottomMiddleX = rectTopMiddleX;
                rectBottomMiddleY = rectBottomLeftY;

                rectBottomRightX = rectTopRightX;
                rectBottomRightY = rectBottomLeftY;

                rectBottomTopRightRadX = rectBottomRightX;
                rectBottomTopRightRadY = rectBottomRightY - cornerRad;

                rectBottomBottomRightRadX = rectBottomRightX - cornerRad;
                rectBottomBottomRightRadY = rectBottomRightY;

                rectBottomRightRadX = rectBottomBottomRightRadX;
                rectBottomRightRadY = rectBottomTopRightRadY;

                rectMiddleX = rectTopMiddleX;
                rectMiddleY = rectMiddleLeftY;
            };

            var nDragg = function () {
                    if (!menuHided) {
                        menu.remove();
                        menuSet.remove();
                        menuHided=true;
                        if (r.getDisplayMainMenu())
                            r.setDisplayMainMenu(false);
                    }

                    for (var i = 0, ii = nodeEndpoints.length; i < ii; i++) {
                        nodeEndpoints[i].dragger();
                    }

                    rect.animate({"fill-opacity": oSelected}, 500);

                    isMoving = true;
                },
                nMove = function (rx, ry, t0x, t0y, t1x, t1y, dx, dy) {

                    if (nodeContainer!=null && !nodeContainer.isMoving()) {
                        var minX = nodeContainer.getRectCornerPoints().topLeftX;
                        minY = nodeContainer.getRectCornerPoints().topLeftY +
                            nodeContainer.getName().height(params.container_txtTitle["font-size"]) +
                            nodeContainer.getHat().height +
                            params.container_interSpan;
                        maxX = nodeContainer.getRectCornerPoints().bottomRightX - rectWidth,
                            maxY = nodeContainer.getRectCornerPoints().bottomRightY - rectHeight;

                        if (minX > rx + dx)
                            dx = minX - rx;
                        if (minY > ry + dy)
                            dy = minY - ry;
                        if (maxX < rx + dx)
                            dx = maxX - rx;
                        if (maxY < ry + dy)
                            dy = maxY - ry;
                    }

                    var attrect = {x: rx + dx, y: ry + dy},
                        attrtxt1 = {x: t0x + dx, y: t0y + dy},
                        attrtxt2 = {x: t1x + dx, y: t1y + dy};

                    rect.attr(attrect);
                    nodeR[0].attr(attrtxt1);
                    nodeR[1].attr(attrtxt2);

                    for (var i = 0, ii = nodeEndpoints.length; i < ii; i++) {
                        nodeEndpoints[i].mover(dx,dy);
                    }

                    defineRectPoints(nodeR[0].attr("x")-(rectWidth/2),nodeR[0].attr("y")-(titleHeight/2));
                    rectPath = r.rectPath(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad);
                    r.safari();
                },
                nUP = function () {
                    rect.animate({"fill-opacity": oUnselected}, 500);

                    for (var i = 0, ii = nodeEndpoints.length; i < ii; i++) {
                        nodeEndpoints[i].uper();
                    }

                    isMoving = false;
                },
                mouseDown = function(e){
                    if (e.which == 3) {
                        if (menuHided) {
                            menuSet = r.getNodeMenuSet();
                            menuSet.mousedown(menuMouseDown);
                            for (var i = 0, ii = menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    menuSet[i].attr({"x": rectTopMiddleX, "y": rectTopMiddleY +10, fill: color});
                                else if (i==1)
                                    menuSet[i].attr({"x": rectTopMiddleX, "y": rectTopMiddleY+30});
                                else
                                    menuSet[i].attr({"x": rectTopMiddleX, "y": rectTopMiddleY+30+(i-1)*15});
                            }
                            menu = r.menu(rectTopMiddleX,rectTopMiddleY+10,menuSet).attr({fill: menuFillColor, stroke: menuStrokeColor, "stroke-width": menuStrokeWidth, "fill-opacity": menuOpacity});
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

            var nodeDragger = function () {
                    if (!rightClick) {
                        this.rx = rect.attr("x");
                        this.ry = rect.attr("y");
                        this.t0x = nodeR[0].attr("x");
                        this.t0y = nodeR[0].attr("y");
                        this.t1x = nodeR[1].attr("x");
                        this.t1y = nodeR[1].attr("y");
                        nDragg();
                    }
                },
                nodeMove = function (dx, dy) {
                    if (!rightClick)
                        nMove(this.rx,this.ry,this.t0x,this.t0y,this.t1x,this.t1y,dx,dy);
                },
                nodeUP = function () {
                    if (!rightClick)
                        nUP();
                };

            this.dragger = function() {
                this.extrx = rect.attr("x");
                this.extry = rect.attr("y");
                this.extt0x = nodeR[0].attr("x");
                this.extt0y = nodeR[0].attr("y");
                this.extt1x = nodeR[1].attr("x");
                this.extt1y = nodeR[1].attr("y");
                nDragg();
            };

            this.mover = function(dx,dy) {
                nMove(this.extrx,this.extry,this.extt0x,this.extt0y,this.extt1x,this.extt1y,dx,dy);
            };

            this.uper = function() {
                nUP();
            };

            var defineEndpointsPoz = function(endpoint) {
                nodeEpAvgLinksT.push(endpoint);
                nodeEpAvgLinksT.sort(function(a,b){
                    at = a.getLinkAvgPoz().t;
                    bt = b.getLinkAvgPoz().t;
                    return at-bt;
                });

                for (var i = 0, ii = nodeEpAvgLinksT.length; i < ii; i++) {
                    var epX=0,epY=0;
                    helper_.debug("EP : " + nodeEpAvgLinksT[i].toString());
                    var avgTeta = nodeEpAvgLinksT[i].getLinkAvgPoz().t;

                    if (avgTeta >= 0 && avgTeta <(Math.PI/4)) {
                        epX = rectTopRightX;
                        epY = rectMiddleY - ((epX-rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI/4) && avgTeta < (Math.PI/2)) {
                        epY = rectTopRightY;
                        epX = rectMiddleX + ((epY-rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI/2) && avgTeta < (3*Math.PI/4)) {
                        epY = rectTopRightY;
                        epX = rectMiddleX - ((epY-rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (3*Math.PI/4) && avgTeta < (Math.PI)) {
                        epX = rectBottomLeftX;
                        epY = rectMiddleY - ((epX-rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI) && avgTeta < (5*Math.PI/4)) {
                        epX = rectBottomLeftX;
                        epY = rectMiddleY + ((epX-rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (5*Math.PI/4) && avgTeta < (3*Math.PI/2)) {
                        epY = rectBottomLeftY;
                        epX = rectMiddleX - ((epY-rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (3*Math.PI/2) && avgTeta < (7*Math.PI/4)) {
                        epY = rectBottomLeftY;
                        epX = rectMiddleX + ((epY-rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (7*Math.PI/4) && avgTeta <= (2*Math.PI)) {
                        epX = rectBottomRightX;
                        epY = rectMiddleY + ((epX-rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        helper_.debug(epX+","+epY+","+avgTeta);

                    }
                    nodeEpAvgLinksT[i].setPoz(epX,epY);
                }
            };

            this.toString = function() {
                return "{\n Node " + name + " : ("+rectTopLeftX+","+rectTopLeftY+")\n}";
            };

            this.popEndpoint = function(endpoint) {
                var index = nodeEndpoints.indexOf(endpoint);
                nodeEndpoints.splice(index,1)
            }

            this.pushEndpoint = function(endpoint) {
                nodeEndpoints.push(endpoint);
                defineEndpointsPoz(endpoint);
            };

            this.isMoving = function() {
                return isMoving;
            };

            this.getColor = function() {
                return color;
            };

            this.getName = function() {
                return name;
            };

            this.getID = function() {
                return ID;
            };

            this.getContainer = function() {
                return nodeContainer;
            };

            this.getCornerRad = function() {
                return cornerRad;
            };

            this.getRectMiddlePoint = function() {
                return {
                    x: rectMiddleX,
                    y: rectMiddleY
                };
            };

            this.getRectCornerPoints = function() {
                return {
                    topLeftX: rectTopLeftX,
                    topLeftY: rectTopLeftY,
                    bottomLeftX: rectBottomLeftX,
                    bottomLeftY: rectBottomLeftY,
                    topRightX: rectTopRightX,
                    topRightY: rectTopRightY,
                    bottomRightX: rectBottomRightX,
                    bottomRightY: rectBottomRightY,
                    TopTopLeftRadX: rectTopTopLeftRadX,
                    TopTopLeftRadY: rectTopTopLeftRadY,
                    TopBottomLeftRadX: rectTopBottomLeftRadX,
                    TopBottomLeftRadY: rectTopBottomLeftRadY,
                    TopLeftRadX: rectTopLeftRadX,
                    TopLeftRadY: rectTopLeftRadY,
                    TopTopRightRadX: rectTopTopRightRadX,
                    TopTopRightRadY: rectTopTopRightRadY,
                    TopBottomRightRadX: rectTopBottomRightRadX,
                    TopBottomRightRadY: rectTopBottomRightRadY,
                    TopRightRadX: rectTopRightRadX,
                    TopRightRadY: rectTopRightRadY,
                    BottomTopLeftRadX: rectBottomTopLeftRadX,
                    BottomTopLeftRadY: rectBottomTopLeftRadY,
                    BottomBottomLeftRadX: rectBottomBottomLeftRadX,
                    BottomBottomLeftRadY: rectBottomBottomLeftRadY,
                    BottomLeftRadX: rectBottomLeftRadX,
                    BottomLeftRadY: rectBottomLeftRadY,
                    BottomTopRightRadX: rectBottomTopRightRadX,
                    BottomTopRightRadY: rectBottomTopRightRadY,
                    BottomBottomRightRadX: rectBottomBottomRightRadX,
                    BottomBottomRightRadY: rectBottomBottomRightRadY,
                    BottomRightRadX: rectBottomRightRadX,
                    BottomRightRadY: rectBottomRightRadY
                };
            };

            this.getRectPath = function() {
                return rectPath;
            };

            this.setPoz = function(x,y) {
                defineRectPoints(x,y);
            };

            this.placeInContainer = function() {
                nodeContainer.pushNode(this);
            };

            this.pushLinkedNode = function(node) {
                var isAlreadyPushed = this.isLinkedToNode(node);
                if (!isAlreadyPushed) {
                    linkedNodes.push(node);
                    this.getContainer().pushLinkedContainer(node.getContainer());
                }
            };

            this.isLinkedToNode = function(node) {
                for (var i = 0, ii = linkedNodes.length; i < ii; i++) {
                    if (linkedNodes[i].getID()==node.getID())
                        return true;
                }
                return false;
            };

            this.pushLinkedBus = function(bus) {
                var isAlreadyPushed = this.isLinkedToBus(bus);
                if (!isAlreadyPushed) {
                    linkedBus.push(bus);
                    this.getContainer().pushLinkedBus(bus);
                }
            }

            this.isLinkedToBus = function(bus) {
                for (var i = 0, ii = linkedBus.length; i < ii; i++) {
                    if (linkedBus[i].equal(bus))
                        return true;
                }
                return false;
            }

            this.print = function(r_) {
                r             = r_;

                nodeR         = r.set();

                nodeName      = r.text(0, 0, name).attr(txtTitleFont);
                r.FitText(nodeName, rectWidth-1, 1);
                nodeName.attr({x: rectTopLeftX + (rectWidth/2), y: rectTopLeftY + (titleHeight/2)});
                nodeR.push(nodeName);

                /*
                 *
                 * rectPath = r.path(r.rectPath(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad));
                 * rectPathLen = rectPath.getTotalLength();
                 * rectPathOrg = rectPath.getPointAtLength(10);
                 *
                 *
                 */

                rect = r.rect(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad);
                rect.attr({fill: color, stroke: color, "fill-opacity": oUnselected, "stroke-width": strokeWidth});
                rect.mousedown(mouseDown);
                rect.drag(nodeMove, nodeDragger, nodeUP);

                //nodeR.dblclick(nrClick);
                nodeR.push(rect);
            };

            this.toFront = function() {
                nodeR[0].toFront();
                nodeR[1].toFront();
            };
        };

        return node;
    });