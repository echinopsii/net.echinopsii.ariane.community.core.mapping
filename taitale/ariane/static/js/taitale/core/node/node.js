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
        'taitale-params',
        'taitale-node-matrix'
    ],
    function(Raphael, helper, params, nodeMatrix){
        function node(JSONNodeDesc, container_) {
            var helper_       = new helper();

            //noinspection JSUnresolvedVariable
            this.ID            = JSONNodeDesc.nodeID;
            this.name          = JSONNodeDesc.nodeName;
            //noinspection JSUnresolvedVariable
            this.cID           = JSONNodeDesc.nodeContainerID;
            //noinspection JSUnresolvedVariable
            this.npID          = (JSONNodeDesc.nodeParentNodeID!=null)?JSONNodeDesc.nodeParentNodeID:0;
            //noinspection JSUnresolvedVariable
            this.properties    = JSONNodeDesc.nodeProperties;

            this.r              = null;
            this.nodeContainer  = container_;
            //noinspection JSUnresolvedVariable
            this.color          = ((this.properties != null && this.properties.primaryApplication != null && this.properties.primaryApplication.color != null) ?
                                    "#"+this.properties.primaryApplication.color :
                                    (this.nodeContainer!=null) ? this.nodeContainer.color : Raphael.getColor());
            this.nodeName      = null;
            //this.nodeDesc      = null;
            //this.nodeR         = null;
            this.rect          = null;

            this.isMoving       = false;
            this.isEditing      = false;
            this.rightClick     = false;


            this.nodeParentNode    = null;
            this.nodeHeapNodes     = []; // the current nodes heap from this to the last parent node of the chain as a list [this,this.nodeParentNode,this.nodeParentNode.nodeParentNode ...]
            this.nodeChildNodes    = new nodeMatrix();

            this.nodeEndpoints   = [];
            // ordered list of epAvgLinksTeta (Teta is the angle as : T = Y/sqrt(X*X+Y*Y))
            this.nodeEpAvgLinksT = [];

            this.linkedBus         = [];
            this.linkedNodes       = [];

            this.layoutData = {
                isConnectedInsideMtx:  false,
                isConnectedOutsideMtx: false,
                isConnectedOutsideToUpMtx: false,
                isConnectedOutsideToDownMtx: false,
                isConnectedOutsideToLeftMtx: false,
                isConnectedOutsideToRightMtx: false,
                mtxCoord: null,
                tag: null
            };

            this.type = "node";

            this.titleHeight   = params.node_titleHeight;
            this.txtTitleFont  = params.node_txtTitle;
            //this.txtDescFont   = params.node_txtDesc;

            this.rectWidth  = params.node_minWidth;
            this.rectHeight = params.node_minHeight;

            this.maxRectWidth  = 0;
            this.maxRectHeight = 0;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.node_menuFillColor;
            //this.menuStrokeColor   = params.node_menuStrokeColor;
            this.menuOpacity       = params.node_menuOpacity;
            this.menuStrokeWidth   = params.node_menuStrokeWidth;
            //this.menuMainTitleTXT  = params.node_menuMainTitle;
            //this.menuFieldTXT      = params.node_menuFields;
            this.menuHided         = true;

            this.menuEditionMode     = null;
            this.menuEditionModeRect = null;
            this.menuFieldStartEditTitle  = "Edition mode ON";
            this.menuFieldStopEditTitle   = "Edition mode OFF";

            this.oUnselected = params.node_opacUnselec;
            this.oSelected   = params.node_opacSelec;
            this.cornerRad   = params.node_cornerRad;
            this.strokeWidth = params.node_strokeWidth;
            this.interSpan   = params.node_interSpan;

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
            //this.rectMiddleLeftX = 0;
            this.rectMiddleLeftY = 0;
            // coord rect middle point
            this.rectMiddleX = 0;
            this.rectMiddleY = 0;
            // coord middle right point
            //this.rectMiddleRightX = 0;
            //this.rectMiddleRightY = 0;
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
            //this.rectBottomMiddleX = 0;
            //this.rectBottomMiddleY = 0;
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

            this.nodeMenuSet = null;
            this.nodeMenuTitle = null;
            this.nodeMenuProperties = null;
            this.nodeMenuPropertiesRect = null;

            this.nodeMainTitleTXT  = params.node_menuMainTitle;
            this.nodeFieldTXT      = params.node_menuFields;
            this.nodeFieldTXTOver  = params.node_menuFieldsOver;

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
            },
            defineEndpointsPoz = function(endpoint) {
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

            var mouseDown = function(e){
                    if (e.which == 3) {
                        if (nodeRef.menuHided) {
                            nodeRef.menuSet = nodeRef.nodeMenuSet;
                            nodeRef.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = nodeRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    nodeRef.menuSet[i].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = nodeRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": nodeRef.rectTopMiddleX - fieldRectWidth/2, "y": nodeRef.rectTopMiddleY+30 - fieldRectHeight/2});
                                    nodeRef.menuSet[i+1].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY+30});
                                    if (nodeRef.isEditing) nodeRef.menuSet[i+1].attr({text: nodeRef.menuFieldStopEditTitle});
                                    else nodeRef.menuSet[i+1].attr({text: nodeRef.menuFieldStartEditTitle});
                                    i++;
                                } else {
                                    fieldRect = nodeRef.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": nodeRef.rectTopMiddleX - fieldRectWidth/2, "y": nodeRef.rectTopMiddleY+30+(i-2)*15 - fieldRectHeight/2});
                                    nodeRef.menuSet[i+1].attr({"x": nodeRef.rectTopMiddleX, "y": nodeRef.rectTopMiddleY+30+(i-2)*15});
                                    i++;
                                }
                            }
                            nodeRef.menu = nodeRef.r.menu(nodeRef.rectTopMiddleX,nodeRef.rectTopMiddleY+10,nodeRef.menuSet).
                                attr({fill: nodeRef.menuFillColor, stroke: nodeRef.color, "stroke-width": nodeRef.menuStrokeWidth, "fill-opacity": nodeRef.menuOpacity});
                            nodeRef.menu.mousedown(menuMouseDown);
                            nodeRef.menu.toFront();
                            nodeRef.menuSet.toFront();
                            nodeRef.menuSet.show();
                            nodeRef.menuHided=false;
                        } else {
                            nodeRef.menu.toBack();
                            nodeRef.menuSet.toBack();
                            nodeRef.menu.hide();
                            nodeRef.menuSet.hide();
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
                        nodeRef.menu.toBack();
                        nodeRef.menuSet.toBack();
                        nodeRef.menu.hide();
                        nodeRef.menuSet.hide();
                        nodeRef.menuHided=true;
                        nodeRef.rightClick=true;
                        if (nodeRef.r.getDisplayMainMenu())
                            nodeRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        nodeRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(nodeRef.nodeFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(nodeRef.nodeFieldTXT);
                },
                menuFieldPropertyClick = function(e) {
                    if (e.which != 3) {
                        //noinspection JSUnresolvedVariable
                        var details = "<br/> <b>Name</b> : " + nodeRef.name +
                            "<br/>" +
                            ((nodeRef.properties.primaryApplication!=null) ? "<br/> <b>Primary application</b> : " + nodeRef.properties.primaryApplication.name + "<br/>": "");

                        var sortedKeys = [];

                        for (var key in nodeRef.properties)
                            if (key !== "primaryApplication")
                                if (nodeRef.properties.hasOwnProperty(key))
                                    sortedKeys.push(key);
                        sortedKeys.sort();

                        for (var i = 0, ii = sortedKeys.length; i < ii; i++)
                            details = helper_.propertiesDisplay(details, sortedKeys[i], nodeRef.properties[sortedKeys[i]])

                        details += "<br/>";

                        helper_.dialogOpen("nodeDetail"+ nodeRef.cID + "_" + nodeRef.ID, "Details of " + nodeRef.name, details);
                    }
                };

            var nodeDragger = function () {
                    if (!nodeRef.rightClick)
                        nodeRef.moveInit();
                },
                nodeMove = function (dx, dy) {
                    if (!nodeRef.rightClick) {
                        var rx = nodeRef.extrx,
                            ry = nodeRef.extry;

                        if (nodeRef.nodeParentNode==null) {
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
                        } else {
                            if (!nodeRef.nodeParentNode.isMoving) {
                                var minX = nodeRef.nodeParentNode.getRectCornerPoints().topLeftX,
                                    minY = nodeRef.nodeParentNode.getRectCornerPoints().topLeftY +
                                           nodeRef.nodeParentNode.name.height(params.node_txtTitle["font-size"]) +
                                           params.node_interSpan,
                                    maxX = nodeRef.nodeParentNode.getRectCornerPoints().bottomRightX - nodeRef.rectWidth,
                                    maxY = nodeRef.nodeParentNode.getRectCornerPoints().bottomRightY - nodeRef.rectHeight;

                                if (minX > rx + dx)
                                    dx = minX - rx;
                                if (minY > ry + dy)
                                    dy = minY - ry;
                                if (maxX < rx + dx)
                                    dx = maxX - rx;
                                if (maxY < ry + dy)
                                    dy = maxY - ry;
                            }
                        }

                        nodeRef.mvx=dx; nodeRef.mvy=dy;
                        nodeRef.r.move(nodeRef.mvx, nodeRef.mvy);
                        nodeRef.r.safari();
                    }
                },
                nodeUP = function () {
                    if (!nodeRef.rightClick)
                        nodeRef.r.up()
                };

            this.toString = function() {
                return "{\n Node " + this.name + " : ("+nodeRef.rectTopLeftX+","+nodeRef.rectTopLeftY+")\n}";
            };

            this.pushChildNode = function (node) {
                this.nodeChildNodes.addObject(node);
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

            this.getMaxRectSize = function() {
                return {
                    width  : this.maxRectWidth,
                    height : this.maxRectHeight
                };
            };

            this.defineMaxSize = function () {
                this.nodeChildNodes.defineMtxContentMaxSize();
                var mtxMaxSize = this.nodeChildNodes.getMtxContentSize();
                var mtxMaxInterspan = (this.nodeChildNodes.getMtxObjCount()+1)*this.interSpan;

                if (mtxMaxSize.width == 0)
                    this.maxRectWidth = this.rectWidth;
                else
                    this.maxRectWidth = mtxMaxInterspan + mtxMaxSize.width;

                if (mtxMaxSize.height == 0)
                    this.maxRectHeight = this.rectHeight;
                else
                    this.maxRectHeight = mtxMaxInterspan + mtxMaxSize.height;
            };

            this.defineSize = function() {
                this.nodeChildNodes.defineMtxContentSize();
                var mtxSize = this.nodeChildNodes.getMtxContentSize();
                if (mtxSize.width != 0)
                    this.rectWidth = (this.nodeChildNodes.getMtxSize().y+1)*this.interSpan + mtxSize.width;
                if (mtxSize.height != 0)
                    this.rectHeight = (this.nodeChildNodes.getMtxSize().x+1)*this.interSpan + this.titleHeight + mtxSize.height;
            };

            this.getRectSize = function() {
                return {
                    width : this.rectWidth,
                    height : this.rectHeight
                }
            };

            this.setPoz = function(x,y) {
                defineRectPoints(x,y);
            };

            this.definedNodesPoz = function() {
                this.nodeChildNodes.defineMtxObjectLastPoz(this.rectTopLeftX, this.rectTopLeftY + this.titleHeight,
                    this.interSpan, this.interSpan, function(node, mtxSpan, objSpan, columnIdx, lineIdx, widthPointer, heightPointer) {
                        node.setPoz(mtxSpan + objSpan * columnIdx + widthPointer, objSpan * (lineIdx+1) + heightPointer);
                        node.definedNodesPoz();
                    });
                    //defineMtxObjectPoz(this.rectTopLeftX, this.rectTopLeftY + this.titleHeight, this.interSpan);
            };

            this.defineIntermediateNodesPoz = function() {
                this.nodeChildNodes.defineMtxObjectIntermediatePoz(this.rectTopLeftX, this.rectTopLeftY + this.titleHeight,
                    this.interSpan, this.interSpan, function(node, mtxSpan, objSpan, columnIdx, lineIdx, widthPointer, heightPointer) {
                        node.setPoz(mtxSpan + objSpan * columnIdx + widthPointer, objSpan * (lineIdx+1) + heightPointer);
                        node.defineIntermediateNodesPoz();
                    });
                //defineMtxObjectPoz(this.rectTopLeftX, this.rectTopLeftY + this.titleHeight, this.interSpan);
            };

            this.clean = function() {
                this.nodeChildNodes.cleanMtx();
            };

            this.defineHeapNodes = function() {
                var parentNode = this.nodeParentNode;
                this.nodeHeapNodes.push(this);
                while (parentNode != null) {
                    this.nodeHeapNodes.push(parentNode);
                    parentNode = parentNode.nodeParentNode;
                }
            };

            this.isInHeapNode = function(node) {
                var i, ii;
                for (i=0, ii=this.nodeHeapNodes.length; i < ii; i++)
                    if (this.nodeHeapNodes[i].ID==node.ID)
                        return true;
                return false;
            };

            this.placeIn = function() {
                if (this.nodeParentNode!=null)
                    this.nodeParentNode.pushChildNode(this);
                else
                    this.nodeContainer.pushNode(this);
            };

            this.pushLinkedNode = function(node) {
                var i, ii, j, jj, isAlreadyPushed = this.isLinkedToNode(node);
                var isInHeap = [];
                if (!isAlreadyPushed) {
                    for (i = 0, ii = this.nodeHeapNodes.length; i < ii; i++)
                        for (j = 0, jj=node.nodeHeapNodes.length; j <jj ; j++) {
                            var linkedNodeHeapNode = node.nodeHeapNodes[j],
                                thisNodeHeapNode = this.nodeHeapNodes[i];
                            if (isInHeap.indexOf[linkedNodeHeapNode]==-1)
                                if (linkedNodeHeapNode.ID!=thisNodeHeapNode.ID)
                                    if (!thisNodeHeapNode.isInHeapNode(linkedNodeHeapNode))
                                        if (thisNodeHeapNode.linkedNodes.indexOf(linkedNodeHeapNode)==-1)
                                            thisNodeHeapNode.linkedNodes.push(linkedNodeHeapNode);
                                    else
                                        isInHeap.push(linkedNodeHeapNode)
                        }

                    if (this.linkedNodes.indexOf(node)==-1)
                        this.linkedNodes.push(node);
                    this.nodeContainer.pushLinkedContainer(node.nodeContainer);
                }
            };

            this.isLinkedToNode = function(node) {
                for (var i = 0, ii = this.linkedNodes.length; i < ii; i++)
                    if (this.linkedNodes[i].ID==node.ID)
                        return true;
                return false;
            };

            this.pushLinkedBus = function(bus) {
                var i, ii, isAlreadyPushed = this.isLinkedToBus(bus);
                if (!isAlreadyPushed) {
                    for (i = 0, ii = this.nodeHeapNodes.length; i < ii; i++) {
                        this.nodeHeapNodes[i].linkedBus.push(bus);
                    }
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

            this.updateLayoutData = function() {
                var i, ii, linkedNode, linkedContainer, linkedBus;
                for (i = 0, ii = this.linkedNodes.length; i < ii; i++) {
                    linkedNode = this.linkedNodes[i];
                    linkedContainer = this.linkedNodes[i].nodeContainer;
                    if (this.nodeContainer.ID!=linkedContainer.ID) {
                        this.layoutData.isConnectedOutsideMtx = true;
                        if (this.nodeContainer.rectTopLeftX > linkedContainer.rectTopLeftX) {
                            this.layoutData.isConnectedOutsideToLeftMtx = true;
                            this.layoutData.isConnectedOutsideToRightMtx = false;
                        } else if (this.nodeContainer.rectTopLeftX < linkedContainer.rectTopLeftX) {
                            this.layoutData.isConnectedOutsideToRightMtx = true;
                            this.layoutData.isConnectedOutsideToLeftMtx = false;
                        } else {
                            this.layoutData.isConnectedOutsideToRightMtx = false;
                            this.layoutData.isConnectedOutsideToLeftMtx = false;
                        }
                        if (this.nodeContainer.rectTopLeftY > linkedContainer.rectTopLeftY) {
                            this.layoutData.isConnectedOutsideToUpMtx = true;
                            this.layoutData.isConnectedOutsideToDownMtx = false;
                        }
                        else if (this.nodeContainer.rectTopLeftY < linkedContainer.rectTopLeftY) {
                            this.layoutData.isConnectedOutsideToDownMtx = true;
                            this.layoutData.isConnectedOutsideToUpMtx = false;
                        } else {
                            this.layoutData.isConnectedOutsideToDownMtx = false;
                            this.layoutData.isConnectedOutsideToUpMtx = false;
                        }
                    } else {
                        if (this.nodeParentNode!=null) {
                            if (this.nodeParentNode.ID!=linkedNode.nodeParentNode.ID) {
                                this.layoutData.isConnectedOutsideMtx = true;
                                if (this.nodeParentNode.rectTopLeftX > linkedNode.nodeParentNode.rectTopLeftX) {
                                    this.layoutData.isConnectedOutsideToLeftMtx = true;
                                    this.layoutData.isConnectedOutsideToRightMtx = false;
                                } else if (this.nodeParentNode.rectTopLeftX < linkedNode.nodeParentNode.rectTopLeftX) {
                                    this.layoutData.isConnectedOutsideToRightMtx = true;
                                    this.layoutData.isConnectedOutsideToLeftMtx = false;
                                } else {
                                    this.layoutData.isConnectedOutsideToRightMtx = false;
                                    this.layoutData.isConnectedOutsideToLeftMtx = false;
                                }
                                if (this.nodeParentNode.rectTopLeftY > linkedNode.nodeParentNode.rectTopLeftY) {
                                    this.layoutData.isConnectedOutsideToUpMtx = true;
                                    this.layoutData.isConnectedOutsideToDownMtx = false;
                                } else if (this.nodeParentNode.rectTopLeftY < linkedNode.nodeParentNode.rectTopLeftY) {
                                    this.layoutData.isConnectedOutsideToDownMtx = true;
                                    this.layoutData.isConnectedOutsideToUpMtx = false;
                                } else {
                                    this.layoutData.isConnectedOutsideToDownMtx = false;
                                    this.layoutData.isConnectedOutsideToUpMtx = false;
                                }
                            } else this.layoutData.isConnectedInsideMtx = true;
                        } else this.layoutData.isConnectedInsideMtx = true;
                    }
                }

                if (this.linkedBus.length > 0)
                    this.layoutData.isConnectedOutsideMtx = true;

                for (i=0 , ii = this.linkedBus.length; i < ii; i++) {
                    linkedBus = this.linkedBus[i];
                    if (this.nodeContainer.rectTopLeftX > linkedBus.getBusCoords().x) {
                        this.layoutData.isConnectedOutsideToLeftMtx = true;
                        this.layoutData.isConnectedOutsideToRightMtx = false;
                    } else if (this.nodeContainer.rectTopLeftX < linkedBus.getBusCoords().x) {
                        this.layoutData.isConnectedOutsideToRightMtx = true;
                        this.layoutData.isConnectedOutsideToLeftMtx = false;
                    } else {
                        this.layoutData.isConnectedOutsideToRightMtx = false;
                        this.layoutData.isConnectedOutsideToLeftMtx = false;
                    }
                    if (this.nodeContainer.rectTopLeftY > linkedBus.getBusCoords().y) {
                        this.layoutData.isConnectedOutsideToUpMtx = true;
                        this.layoutData.isConnectedOutsideToDownMtx = false;
                    } else if (this.nodeContainer.rectTopLeftY < linkedBus.getBusCoords().y) {
                        this.layoutData.isConnectedOutsideToDownMtx = true;
                        this.layoutData.isConnectedOutsideToUpMtx = false;
                    } else {
                        this.layoutData.isConnectedOutsideToDownMtx = false;
                        this.layoutData.isConnectedOutsideToUpMtx = false;
                    }
                }
            };

            this.updatePosition = function() {
                this.nodeChildNodes.updateLayoutData(function(node1, node2){
                    return ((node2.linkedNodes.length+node2.linkedBus.length)-(node1.linkedNodes.length+node1.linkedBus.length));
                });
                this.nodeChildNodes.updatePosition();
            };

            this.print = function(r_) {
                this.r        = r_;

                this.nodeName = this.r.text(0, 0, this.name).attr(this.txtTitleFont);
                this.r.FitText(this.nodeName, this.rectWidth-1, 1.5);
                this.nodeName.attr({x: this.rectTopLeftX + (this.rectWidth/2), y: this.rectTopLeftY + (this.titleHeight/2)});
                this.nodeName.mousedown(mouseDown);
                this.nodeName.drag(nodeMove, nodeDragger, nodeUP);

                this.rect = this.r.rect(this.rectTopLeftX, this.rectTopLeftY, this.rectWidth, this.rectHeight, this.cornerRad);
                this.rect.attr({fill: this.color, stroke: this.color, "fill-opacity": this.oUnselected, "stroke-width": this.strokeWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(nodeMove, nodeDragger, nodeUP);
                defineRectPoints(this.rectTopLeftX, this.rectTopLeftY);

                this.nodeMenuTitle = this.r.text(0,10,"Node menu").attr(this.nodeMainTitleTXT);
                this.menuEditionModeRect = this.r.rect(0,10,this.menuFieldStartEditTitle.width(this.nodeFieldTXT),this.menuFieldStartEditTitle.height(this.nodeFieldTXT));
                this.menuEditionModeRect.attr({fill: this.color, stroke: this.color, "fill-opacity": 0, "stroke-width": 0});
                this.menuEditionModeRect.mouseover(menuFieldOver);
                this.menuEditionModeRect.mouseout(menuFieldOut);
                this.menuEditionModeRect.mousedown(this.menuFieldEditClick);
                this.menuEditionMode = this.r.text(0,10,this.menuFieldStartEditTitle).attr(this.nodeFieldTXT);
                this.menuEditionMode.mouseover(menuFieldOver);
                this.menuEditionMode.mouseout(menuFieldOut);
                this.menuEditionMode.mousedown(this.menuFieldEditClick);

                var fieldTitle = "Display all properties";
                this.nodeMenuPropertiesRect = this.r.rect(0,10,fieldTitle.width(this.nodeFieldTXT),fieldTitle.height(this.nodeFieldTXT));
                this.nodeMenuPropertiesRect.attr({fill: this.color, stroke: this.color, "fill-opacity": 0, "stroke-width": 0});
                this.nodeMenuPropertiesRect.mouseover(menuFieldOver);
                this.nodeMenuPropertiesRect.mouseout(menuFieldOut);
                this.nodeMenuPropertiesRect.mousedown(menuFieldPropertyClick);
                this.nodeMenuProperties = this.r.text(0,10,fieldTitle).attr(this.nodeFieldTXT);
                this.nodeMenuProperties.mouseover(menuFieldOver);
                this.nodeMenuProperties.mouseout(menuFieldOut);
                this.nodeMenuProperties.mousedown(menuFieldPropertyClick);

                this.nodeMenuSet = this.r.set();
                this.nodeMenuSet.push(this.nodeMenuTitle);
                this.nodeMenuSet.push(this.menuEditionModeRect);
                this.nodeMenuSet.push(this.menuEditionMode);
                this.nodeMenuSet.push(this.nodeMenuPropertiesRect);
                this.nodeMenuSet.push(this.nodeMenuProperties);
                this.nodeMenuSet.toBack();
                this.nodeMenuSet.hide();
            };

            this.toFront = function() {
                this.rect.toFront();
                this.nodeName.toFront();
                this.nodeChildNodes.toFront();
                for (var i = 0, ii = this.nodeEndpoints.length; i < ii; i++)
                    this.nodeEndpoints[i].toFront();
            };

            this.changeInit = function() {
                this.extrx = this.rect.attr("x");
                this.extry = this.rect.attr("y");
                this.extt0x = this.nodeName.attr("x");
                this.extt0y = this.nodeName.attr("y");

                if (!this.menuHided) {
                    this.menu.toBack();
                    this.menuSet.toBack();
                    this.menu.hide();
                    this.menuSet.hide();
                    this.menuHided=true;
                    if (this.r.getDisplayMainMenu())
                        this.r.setDisplayMainMenu(false);
                }

                this.isMoving = true;
            };

            this.changeUp = function() {
                this.toFront();
                this.setPoz(this.nodeName.attr("x")-(this.rectWidth/2), this.nodeName.attr("y")-(this.titleHeight/2));
                this.isMoving = false;
            };

            // MOVEABLE

            this.moveInit = function() {
                if (this.isEditing)
                    this.r.scaleDone(this);

                var i, ii, j, jj;
                var mtxX        = this.nodeChildNodes.getMtxSize().x,
                    mtxY        = this.nodeChildNodes.getMtxSize().y;

                this.r.nodesOnMovePush(this);
                this.r.moveSetPush(this.rect);
                this.r.moveSetPush(this.nodeName);

                for (i = 0, ii = mtxX; i < ii; i++)
                    for (j = 0, jj = mtxY; j < jj; j++)
                        if (this.nodeChildNodes.getObjectFromMtx(i, j)!=null)
                            this.nodeChildNodes.getObjectFromMtx(i, j).moveInit();

                for (i = 0, ii = this.nodeEndpoints.length; i < ii; i++)
                    this.nodeEndpoints[i].moveInit();

                this.changeInit();

                this.rect.animate({"fill-opacity": this.oSelected}, 500);
            };

            this.moveAction = function(dx,dy) {
                this.mvx = dx; this.mvy = dy;
            };

            this.moveUp = function() {
                var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy},
                    attrtxt0 = {x: this.extt0x + this.mvx, y: this.extt0y + this.mvy};

                this.mvx=0; this.mvy=0;
                this.rect.attr(attrect);
                this.nodeName.attr(attrtxt0);

                this.rect.animate({"fill-opacity": this.oUnselected}, 500);
                this.changeUp();

                if (this.isEditing)
                    this.r.scaleInit(this);
            };

            // EDITABLE

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
                nodeRef.menu.toBack();
                nodeRef.menuSet.toBack();
                nodeRef.menu.hide();
                nodeRef.menuSet.hide();
                nodeRef.menuHided=true;

                if (!nodeRef.isEditing) {
                    nodeRef.r.scaleInit(nodeRef);
                    nodeRef.isEditing = true;
                } else {
                    nodeRef.r.scaleDone(nodeRef);
                    nodeRef.isEditing = false;
                }
            };

            this.getBBox = function() {
                return this.rect.getBBox();
            };

            var nodeSet;
            this.getMinBBox = function() {
                var i, ii, j, jj;
                var mtxX        = this.nodeChildNodes.getMtxSize().x,
                    mtxY        = this.nodeChildNodes.getMtxSize().y;

                nodeSet = this.r.set();
                for (i = 0, ii = mtxX; i < ii; i++)
                    for (j = 0, jj = mtxY; j < jj; j++)
                        if (this.nodeChildNodes.getObjectFromMtx(i, j)!=null)
                            nodeSet.push(this.nodeChildNodes.getObjectFromMtx(i, j).rect);

                var nodeBBox = nodeSet.getBBox();

                return {
                    x: nodeBBox.x - this.interSpan,
                    y: nodeBBox.y - (this.titleHeight + this.interSpan),
                    x2: nodeBBox.x2 + this.interSpan,
                    y2: nodeBBox.y2 + this.interSpan,
                    width: nodeBBox.width + 2*this.interSpan,
                    height: nodeBBox.height + (this.titleHeight + this.interSpan)
                };
            };

            this.getMaxBBox = function() {
                if (this.nodeParentNode==null) {
                    if (this.nodeContainer!=null) {
                        this.minTopLeftX = this.nodeContainer.getRectCornerPoints().topLeftX;
                        this.minTopLeftY = this.nodeContainer.getRectCornerPoints().topLeftY +
                                           this.nodeContainer.name.height(params.container_txtTitle["font-size"]) +
                                           this.nodeContainer.containerHat_.height + params.container_interSpan;
                        this.maxTopLeftX = this.nodeContainer.getRectCornerPoints().bottomRightX - this.rectWidth;
                        this.maxTopLeftY = this.nodeContainer.getRectCornerPoints().bottomRightY - nodeRef.rectHeight;
                    }
                } else {
                    this.minTopLeftX = this.nodeParentNode.getRectCornerPoints().topLeftX;
                    this.minTopLeftY = this.nodeParentNode.getRectCornerPoints().topLeftY +
                                       this.nodeParentNode.name.height(params.node_txtTitle["font-size"]) +
                                       params.node_interSpan;
                    this.maxTopLeftX = this.nodeParentNode.getRectCornerPoints().bottomRightX - this.rectWidth;
                    this.maxTopLeftY = this.nodeParentNode.getRectCornerPoints().bottomRightY - this.rectHeight;
                }

                return {
                    x: this.minTopLeftX,
                    y: this.minTopLeftY,
                    x2: this.maxTopLeftX + this.rectWidth,
                    y2: this.maxTopLeftY + this.rectHeight,
                    width: this.maxTopLeftX + this.rectWidth - this.minTopLeftX,
                    height: this.maxTopLeftY + this.rectHeight - this.minTopLeftY
                }
            };

            this.editInit = function() {
                this.extwidth  = this.rectWidth;
                this.extheight = this.rectHeight;
                this.changeInit();
            };

            this.editAction = function(elem, dx, dy) {
                switch(elem.idx) {
                    case 0:
                        this.extrx = this.rectTopLeftX + dx;
                        this.extry = this.rectTopLeftY + dy;
                        this.extwidth = this.rectWidth - dx;
                        this.extheight = this.rectHeight - dy;
                        break;

                    case 1:
                        this.extry = this.rectTopLeftY + dy;
                        this.extwidth = this.rectWidth + dx;
                        this.extheight = this.rectHeight - dy;
                        break;

                    case 2:
                        this.extwidth = this.rectWidth + dx;
                        this.extheight = this.rectHeight + dy;
                        break;

                    case 3:
                        this.extrx = this.rectTopLeftX + dx;
                        this.extwidth = this.rectWidth - dx;
                        this.extheight = this.rectHeight + dy;
                        break;

                    case 4:
                        this.extry = this.rectTopLeftY + dy;
                        this.extheight = this.rectHeight - dy;
                        break;

                    case 5:
                        this.extwidth = this.rectWidth + dx;
                        break;

                    case 6:
                        this.extheight = this.rectHeight + dy;
                        break;

                    case 7:
                        this.extrx = this.rectTopLeftX + dx;
                        this.extwidth = this.rectWidth - dx;
                        break;

                    default:
                        break;
                }

                this.nodeName.remove();
                this.rect.remove();

                this.nodeName = this.r.text(0, 0, this.name).attr(this.txtTitleFont);
                this.nodeName.attr({x: this.extrx + (this.extwidth/2), y: this.extry + (this.titleHeight/2)});
                this.nodeName.mousedown(mouseDown);
                this.nodeName.drag(nodeMove, nodeDragger, nodeUP);

                this.rect = this.r.rect(this.extrx, this.extry, this.extwidth, this.extheight, this.cornerRad);
                this.rect.attr({fill: this.color, stroke: this.color, "fill-opacity": nodeRef.oUnselected, "stroke-width": this.strokeWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(nodeMove, nodeDragger, nodeUP);
                this.toFront();
            };

            this.editUp = function() {
                this.maxTopLeftX = this.maxTopLeftX + this.rectWidth - this.extwidth;
                this.maxTopLeftY = this.maxTopLeftY + this.rectHeight - this.extheight;
                this.rectWidth = this.extwidth;
                this.rectHeight = this.extheight;

                this.changeUp();
            };
        }

        return node;
    });