// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Node                            │ \\
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
        function node(JSONNodeDesc, container_) {
            var helper_       = new helper();

            this.ID            = JSONNodeDesc.nodeID;
            this.name          = JSONNodeDesc.nodeName;
            this.cID           = JSONNodeDesc.nodeContainerID;
            this.properties    = JSONNodeDesc.nodeProperties;

            this.r             = null;
            this.nodeContainer = container_;
            this.color         = ((this.properties != null && this.properties.primaryApplication != null && this.properties.primaryApplication.color != null) ?
                                            "#"+this.properties.primaryApplication.color :
                                            (this.nodeContainer!=null) ? this.nodeContainer.color : Raphael.getColor());
            this.nodeName      = null;
            this.nodeDesc      = null;
            this.nodeR         = null;
            this.rect          = null;

            this.isMoving       = false;
            this.rightClick     = false;

            this.nodeEndpoints   = [];
            // ordered list of epAvgLinksTeta (Teta is the angle as : T = Y/sqrt(X*X+Y*Y))
            this.nodeEpAvgLinksT = [];

            this.linkedBus         = [];
            this.linkedNodes       = [];

            this.titleHeight   = params.node_titleHeight;
            this.txtTitleFont  = params.node_txtTitle;
            this.txtDescFont   = params.node_txtDesc;

            this.rectWidth  = params.node_minWidth;
            this.rectHeight = params.node_minHeight;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.node_menuFillColor;
            this.menuStrokeColor   = params.node_menuStrokeColor;
            this.menuOpacity       = params.node_menuOpacity;
            this.menuStrokeWidth   = params.node_menuStrokeWidth;
            this.menuMainTitleTXT  = params.node_menuMainTitle;
            this.menuFieldTXT      = params.node_menuFields;
            this.menuHided         = true;

            this.oUnselected = params.node_opacUnselec,
            this.oSelected   = params.node_opacSelec,
            this.cornerRad   = params.node_cornerRad,
            this.strokeWidth = params.node_strokeWidth;

            // coord top left point
            this.rectTopLeftX  = 0;
            this.rectTopLeftY  = 0;
            // coord top top left rad point
            this.rectTopTopLeftRadX = 0;
            this.rectTopTopLeftRadY = 0;
            // coord top bottom left rad point
            this.rectTopBottomLeftRadX = 0;
            this.rectTopBottomLeftRadY = 0;
            // coord top left circle center point
            this.rectTopLeftRadX = 0;
            this.rectTopLeftRadY = 0;
            // coord top middle point
            this.rectTopMiddleX = 0;
            this.rectTopMiddleY = 0;
            // coord top right point
            this.rectTopRightX = 0;
            this.rectTopRightY = 0;
            // coord top top right rad point
            this.rectTopTopRightRadX = 0;
            this.rectTopTopRightRadY = 0;
            // coord top bottom right rad point
            this.rectTopBottomRightRadX = 0;
            this.rectTopBottomRightRadY = 0;
            // coord top right circle center point
            this.rectTopRightRadX = 0;
            this.rectTopRightRadY = 0;
            // coord middle left point
            this.rectMiddleLeftX = 0;
            this.rectMiddleLeftY = 0;
            // coord rect middle point
            this.rectMiddleX = 0;
            this.rectMiddleY = 0;
            // coord middle right point
            this.rectMiddleRightX = 0;
            this.rectMiddleRightY = 0;
            //coord bottom left point
            this.rectBottomLeftX = 0;
            this.rectBottomLeftY = 0;
            //coord bottom bottom left rad point
            this.rectBottomBottomLeftRadX = 0;
            this.rectBottomBottomLeftRadY = 0;
            //coord bottom top left rad point
            this.rectBottomTopLeftRadX = 0;
            this.rectBottomTopLeftRadY = 0;
            // coord bottome left circle center point
            this.rectBottomLeftRadX = 0;
            this.rectBottomLeftRadY = 0;
            //coord bottom middle point,
            this.rectBottomMiddleX = 0;
            this.rectBottomMiddleY = 0;
            //coord bottom right point,
            this.rectBottomRightX = 0;
            this.rectBottomRightY = 0;
            //coord bottom bottom right rad point,
            this.rectBottomBottomRightRadX = 0;
            this.rectBottomBottomRightRadY = 0;
            //coord bottom top right rad point
            this.rectBottomTopRightRadX = 0;
            this.rectBottomTopRightRadY = 0;
            // coord bottome right circle center point
            this.rectBottomRightRadX = 0;
            this.rectBottomRightRadY = 0;

            this.mvx = 0;
            this.mvy = 0;

            var nodeRef = this;

            /**
             * x = abs of nodeR[0], y = ord of nodeR[0]
             */
            var defineRectPoints = function(x,y) {

                nodeRef.rectTopLeftX     = x;
                nodeRef.rectTopLeftY     = y;

                nodeRef.rectTopTopLeftRadX = nodeRef.rectTopLeftX + nodeRef.cornerRad;
                nodeRef.rectTopTopLeftRadY = nodeRef.rectTopLeftY;

                nodeRef.rectTopBottomLeftRadX = nodeRef.rectTopLeftX;
                nodeRef.rectTopBottomLeftRadY = nodeRef.rectTopLeftY + nodeRef.cornerRad;

                nodeRef.rectTopLeftRadX = nodeRef.rectTopTopLeftRadX;
                nodeRef.rectTopLeftRadY = nodeRef.rectTopBottomLeftRadY;

                nodeRef.rectTopMiddleX   = nodeRef.rectTopLeftX + nodeRef.rectWidth/2;
                nodeRef.rectTopMiddleY   = nodeRef.rectTopLeftY;

                nodeRef.rectTopRightX    = nodeRef.rectTopLeftX + nodeRef.rectWidth;
                nodeRef.rectTopRightY    = nodeRef.rectTopLeftY;

                nodeRef.rectTopTopRightRadX = nodeRef.rectTopRightX - nodeRef.cornerRad;
                nodeRef.rectTopTopRightRadY = nodeRef.rectTopRightY;

                nodeRef.rectTopBottomRightRadX = nodeRef.rectTopRightX;
                nodeRef.rectTopBottomRightRadY = nodeRef.rectTopRightY + nodeRef.cornerRad;

                nodeRef.rectTopRightRadX = nodeRef.rectTopTopRightRadX;
                nodeRef.rectTopRightRadY = nodeRef.rectTopBottomRightRadY;

                nodeRef.rectMiddleLeftX  = nodeRef.rectTopLeftX;
                nodeRef.rectMiddleLeftY  = nodeRef.rectTopLeftY + nodeRef.rectHeight/2;

                nodeRef.rectMiddleRightX = nodeRef.rectTopRightX;
                nodeRef.rectMiddleRightY = nodeRef.rectMiddleLeftY;

                nodeRef.rectBottomLeftX  = nodeRef.rectTopLeftX;
                nodeRef.rectBottomLeftY  = nodeRef.rectTopLeftY + nodeRef.rectHeight;

                nodeRef.rectBottomTopLeftRadX = nodeRef.rectBottomLeftX;
                nodeRef.rectBottomTopLeftRadY = nodeRef.rectBottomLeftY - nodeRef.cornerRad;

                nodeRef.rectBottomBottomLeftRadX = nodeRef.rectBottomLeftX + nodeRef.cornerRad;
                nodeRef.rectBottomBottomLeftRadY = nodeRef.rectBottomLeftY;

                nodeRef.rectBottomLeftRadX = nodeRef.rectBottomBottomLeftRadX;
                nodeRef.rectBottomLeftRadY = nodeRef.rectBottomTopLeftRadY;

                nodeRef.rectBottomMiddleX = nodeRef.rectTopMiddleX;
                nodeRef.rectBottomMiddleY = nodeRef.rectBottomLeftY;

                nodeRef.rectBottomRightX = nodeRef.rectTopRightX;
                nodeRef.rectBottomRightY = nodeRef.rectBottomLeftY;

                nodeRef.rectBottomTopRightRadX = nodeRef.rectBottomRightX;
                nodeRef.rectBottomTopRightRadY = nodeRef.rectBottomRightY - nodeRef.cornerRad;

                nodeRef.rectBottomBottomRightRadX = nodeRef.rectBottomRightX - nodeRef.cornerRad;
                nodeRef.rectBottomBottomRightRadY = nodeRef.rectBottomRightY;

                nodeRef.rectBottomRightRadX = nodeRef.rectBottomBottomRightRadX;
                nodeRef.rectBottomRightRadY = nodeRef.rectBottomTopRightRadY;

                nodeRef.rectMiddleX = nodeRef.rectTopMiddleX;
                nodeRef.rectMiddleY = nodeRef.rectMiddleLeftY;
            };

            var nMove = function (dx, dy) {
                    var rx = nodeRef.extrx,
                        ry = nodeRef.extry;

                    if (nodeRef.nodeContainer!=null && !nodeRef.nodeContainer.isMoving) {
                        var minX = nodeRef.nodeContainer.getRectCornerPoints().topLeftX,
                            minY = nodeRef.nodeContainer.getRectCornerPoints().topLeftY +
                                   nodeRef.nodeContainer.name.height(params.container_txtTitle["font-size"]) +
                                   nodeRef.nodeContainer.containerHat_.height + params.container_interSpan,
                            maxX = nodeRef.nodeContainer.getRectCornerPoints().bottomRightX - nodeRef.rectWidth,
                            maxY = nodeRef.nodeContainer.getRectCornerPoints().bottomRightY - nodeRef.rectHeight;

                        if (minX > rx + dx)
                            dx = minX - rx;
                        if (minY > ry + dy)
                            dy = minY - ry;
                        if (maxX < rx + dx)
                            dx = maxX - rx;
                        if (maxY < ry + dy)
                            dy = maxY - ry;
                    }

                    nodeRef.mvx=dx; nodeRef.mvy=dy;
                    nodeRef.r.move(nodeRef.mvx, nodeRef.mvy);
                    nodeRef.r.safari();
                },
                mouseDown = function(e){
                    if (e.which == 3) {
                        if (nodeRef.menuHided) {
                            nodeRef.menuSet = nodeRef.r.getNodeMenuSet();
                            nodeRef.menuSet.mousedown(menuMouseDown);
                            for (var i = 0, ii = nodeRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    nodeRef.menuSet[i].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY +10, fill: nodeRef.color});
                                else if (i==1)
                                    nodeRef.menuSet[i].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY+30});
                                else
                                    nodeRef.menuSet[i].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY+30+(i-1)*15});
                            }
                            nodeRef.menu = nodeRef.r.menu(nodeRef.rectTopMiddleX,nodeRef.rectTopMiddleY+10,nodeRef.menuSet).
                                attr({fill: nodeRef.menuFillColor, stroke: nodeRef.menuStrokeColor, "stroke-width": nodeRef.menuStrokeWidth, "fill-opacity": nodeRef.menuOpacity});
                            nodeRef.menu.mousedown(menuMouseDown);
                            nodeRef.menu.toFront();
                            nodeRef.menuSet.toFront();
                            nodeRef.menuSet.show();
                            nodeRef.menuHided=false;
                        } else {
                            nodeRef.menu.remove();
                            nodeRef.menuSet.remove();
                            nodeRef.menuHided=true;
                        }
                        nodeRef.rightClick=true;
                        if (nodeRef.r.getDisplayMainMenu())
                            nodeRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        nodeRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        nodeRef.menu.remove();
                        nodeRef.menuSet.remove();
                        nodeRef.menuHided=true;
                        nodeRef.rightClick=true;
                        if (nodeRef.r.getDisplayMainMenu())
                            nodeRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        nodeRef.rightClick=false;
                    }
                };

            var nodeDragger = function () {
                    if (!nodeRef.rightClick)
                        nodeRef.r.drag(nodeRef,"node");
                },
                nodeMove = function (dx, dy) {
                    if (!nodeRef.rightClick)
                        nMove(dx,dy);
                },
                nodeUP = function () {
                    if (!nodeRef.rightClick)
                        nodeRef.r.up()
                };

            var defineEndpointsPoz = function(endpoint) {
                nodeRef.nodeEpAvgLinksT.push(endpoint);
                nodeRef.nodeEpAvgLinksT.sort(function(a,b){
                    var at = a.getLinkAvgPoz().t,
                        bt = b.getLinkAvgPoz().t;
                    return at-bt;
                });

                for (var i = 0, ii = nodeRef.nodeEpAvgLinksT.length; i < ii; i++) {
                    var epX=0,epY=0;
                    //helper_.debug("EP : " + nodeRef.nodeEpAvgLinksT[i].toString());
                    var avgTeta = nodeRef.nodeEpAvgLinksT[i].getLinkAvgPoz().t;

                    if (avgTeta >= 0 && avgTeta <(Math.PI/4)) {
                        epX = nodeRef.rectTopRightX;
                        epY = nodeRef.rectMiddleY - ((epX-nodeRef.rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI/4) && avgTeta < (Math.PI/2)) {
                        epY = nodeRef.rectTopRightY;
                        epX = nodeRef.rectMiddleX + ((epY-nodeRef.rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI/2) && avgTeta < (3*Math.PI/4)) {
                        epY = nodeRef.rectTopRightY;
                        epX = nodeRef.rectMiddleX - ((epY-nodeRef.rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (3*Math.PI/4) && avgTeta < (Math.PI)) {
                        epX = nodeRef.rectBottomLeftX;
                        epY = nodeRef.rectMiddleY - ((epX-nodeRef.rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (Math.PI) && avgTeta < (5*Math.PI/4)) {
                        epX = nodeRef.rectBottomLeftX;
                        epY = nodeRef.rectMiddleY + ((epX-nodeRef.rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (5*Math.PI/4) && avgTeta < (3*Math.PI/2)) {
                        epY = nodeRef.rectBottomLeftY;
                        epX = nodeRef.rectMiddleX - ((epY-nodeRef.rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (3*Math.PI/2) && avgTeta < (7*Math.PI/4)) {
                        epY = nodeRef.rectBottomLeftY;
                        epX = nodeRef.rectMiddleX + ((epY-nodeRef.rectMiddleY)/Math.sin(avgTeta))*Math.sqrt(1-Math.sin(avgTeta)*Math.sin(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    } else if (avgTeta >= (7*Math.PI/4) && avgTeta <= (2*Math.PI)) {
                        epX = nodeRef.rectBottomRightX;
                        epY = nodeRef.rectMiddleY + ((epX-nodeRef.rectMiddleX)/Math.cos(avgTeta))*Math.sqrt(1-Math.cos(avgTeta)*Math.cos(avgTeta));
                        //helper_.debug(epX+","+epY+","+avgTeta);

                    }
                    nodeRef.nodeEpAvgLinksT[i].setPoz(epX,epY);
                }
            };

            this.toString = function() {
                return "{\n Node " + this.name + " : ("+nodeRef.rectTopLeftX+","+nodeRef.rectTopLeftY+")\n}";
            };

            this.popEndpoint = function(endpoint) {
                var index = nodeRef.nodeEndpoints.indexOf(endpoint);
                nodeRef.nodeEndpoints.splice(index,1)
            };

            this.pushEndpoint = function(endpoint) {
                this.nodeEndpoints.push(endpoint);
                defineEndpointsPoz(endpoint);
            };

            this.getRectMiddlePoint = function() {
                return {
                    x: nodeRef.rectMiddleX,
                    y: nodeRef.rectMiddleY
                };
            };

            this.getRectCornerPoints = function() {
                return {
                    topLeftX: this.rectTopLeftX,
                    topLeftY: this.rectTopLeftY,
                    bottomLeftX: this.rectBottomLeftX,
                    bottomLeftY: this.rectBottomLeftY,
                    topRightX: this.rectTopRightX,
                    topRightY: this.rectTopRightY,
                    bottomRightX: this.rectBottomRightX,
                    bottomRightY: this.rectBottomRightY,
                    TopTopLeftRadX: this.rectTopTopLeftRadX,
                    TopTopLeftRadY: this.rectTopTopLeftRadY,
                    TopBottomLeftRadX: this.rectTopBottomLeftRadX,
                    TopBottomLeftRadY: this.rectTopBottomLeftRadY,
                    TopLeftRadX: this.rectTopLeftRadX,
                    TopLeftRadY: this.rectTopLeftRadY,
                    TopTopRightRadX: this.rectTopTopRightRadX,
                    TopTopRightRadY: this.rectTopTopRightRadY,
                    TopBottomRightRadX: this.rectTopBottomRightRadX,
                    TopBottomRightRadY: this.rectTopBottomRightRadY,
                    TopRightRadX: this.rectTopRightRadX,
                    TopRightRadY: this.rectTopRightRadY,
                    BottomTopLeftRadX: this.rectBottomTopLeftRadX,
                    BottomTopLeftRadY: this.rectBottomTopLeftRadY,
                    BottomBottomLeftRadX: this.rectBottomBottomLeftRadX,
                    BottomBottomLeftRadY: this.rectBottomBottomLeftRadY,
                    BottomLeftRadX: this.rectBottomLeftRadX,
                    BottomLeftRadY: this.rectBottomLeftRadY,
                    BottomTopRightRadX: this.rectBottomTopRightRadX,
                    BottomTopRightRadY: this.rectBottomTopRightRadY,
                    BottomBottomRightRadX: this.rectBottomBottomRightRadX,
                    BottomBottomRightRadY: this.rectBottomBottomRightRadY,
                    BottomRightRadX: this.rectBottomRightRadX,
                    BottomRightRadY: this.rectBottomRightRadY
                };
            };

            this.setPoz = function(x,y) {
                defineRectPoints(x,y);
            };

            this.placeInContainer = function() {
                this.nodeContainer.pushNode(this);
            };

            this.pushLinkedNode = function(node) {
                var isAlreadyPushed = this.isLinkedToNode(node);
                if (!isAlreadyPushed) {
                    this.linkedNodes.push(node);
                    this.nodeContainer.pushLinkedContainer(node.nodeContainer);
                }
            };

            this.isLinkedToNode = function(node) {
                for (var i = 0, ii = this.linkedNodes.length; i < ii; i++) {
                    if (this.linkedNodes[i].ID==node.ID)
                        return true;
                }
                return false;
            };

            this.pushLinkedBus = function(bus) {
                var isAlreadyPushed = this.isLinkedToBus(bus);
                if (!isAlreadyPushed) {
                    this.linkedBus.push(bus);
                    this.nodeContainer.pushLinkedBus(bus);
                }
            };

            this.isLinkedToBus = function(bus) {
                for (var i = 0, ii = this.linkedBus.length; i < ii; i++) {
                    if (this.linkedBus[i].equal(bus))
                        return true;
                }
                return false;
            };

            this.print = function(r_) {
                this.r        = r_;

                this.nodeName = this.r.text(0, 0, this.name).attr(this.txtTitleFont);
                this.r.FitText(this.nodeName, this.rectWidth-1, 1);
                this.nodeName.attr({x: this.rectTopLeftX + (this.rectWidth/2), y: this.rectTopLeftY + (this.titleHeight/2)});

                this.rect = this.r.rect(this.rectTopLeftX, this.rectTopLeftY, this.rectWidth, this.rectHeight, this.cornerRad);
                this.rect.attr({fill: this.color, stroke: this.color, "fill-opacity": this.oUnselected, "stroke-width": this.strokeWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(nodeMove, nodeDragger, nodeUP);
            };

            this.toFront = function() {
                this.rect.toFront();
                this.nodeName.toFront();
            };
        }

        return node;
    });