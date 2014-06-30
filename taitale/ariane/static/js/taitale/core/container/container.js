// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Container                       │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench						  						  │ \\
// │ 																					  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of			  			  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			 			  │ \\
// │ GNU Affero General Public License for more details.				  				  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.		  		  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

define(
    [
        'raphael',
        'taitale-helper',
        'taitale-params',
        'taitale-prototypes',
        'taitale-container-matrix',
        'taitale-container-hat',
        'taitale-ext-string'
    ],
    function(Raphael,helper,params,prototypes,containerMatrix,containerHat) {
        function container(JSONContainerDesc, x_, y_) {
            var helper_      = new helper(),
                prototypes_  = new prototypes();

            this.ID       	  = JSONContainerDesc.containerID;
            this.company      = JSONContainerDesc.containerCompany;
            this.product      = JSONContainerDesc.containerProduct;
            this.type         = JSONContainerDesc.containerType;
            this.gateURI      = JSONContainerDesc.containerGateURI;
            this.name         = JSONContainerDesc.containerGateURI;
            this.properties   = JSONContainerDesc.containerProperties;
            this.localisation = null;

            var tmpDatacenter = this.properties.Datacenter,
                tmpNetwork    = this.properties.Network;
            if (tmpDatacenter != null && tmpNetwork != null) {
                this.localisation = prototypes_.create(prototypes_.ntprototype, {
                        dcproto:    prototypes_.create(prototypes_.dcprototype, tmpDatacenter),
                        type:       tmpNetwork.type,
                        marea:      tmpNetwork.marea,
                        lan:        tmpNetwork.lan,
                        subnetip:   tmpNetwork.subnetip,
                        subnetmask: tmpNetwork.subnetmask
                });
            }

            this.layoutData        = null;

            this.r                 = null;
            this.color             = (this.properties.supportTeam!=null && this.properties.supportTeam.color!=null) ? "#"+this.properties.supportTeam.color : Raphael.getColor();
            this.txtFont           = params.container_txtTitle;
            this.X                 = x_;
            this.Y                 = y_;
            this.containerName     = null;
            this.rect              = null;
            this.rightClick        = false;
            this.isMoving          = false;
            this.isInserted        = false;

            this.menu              = null;
            this.menuSet           = null;
            this.menuFillColor     = params.container_menuFillColor;
            this.menuStrokeColor   = params.container_menuStrokeColor;
            this.menuOpacity       = params.container_menuOpacity;
            this.menuStrokeWidth   = params.container_menuStrokeWidth;
            this.menuMainTitleTXT  = params.container_menuMainTitle;
            this.menuFieldTXT      = params.container_menuFields;
            this.menuHided         = true;

            this.containerNodes    = new containerMatrix();
            this.containerHat_     = new containerHat(this.company,this.product,this.type);

            this.linkedTreeObjects                  = [];
            this.sortOrdering                       = 1;

            var minMaxLinkedTreedObjectsComparator = function(linkedObject1, linkedObject2) {
                    return (linkedObject2.getLinkedTreeObjectsCount() - linkedObject1.getLinkedTreeObjectsCount())*this.sortOrdering;
                };

            this.linkedBus         = [];
            this.linkedContainers  = [];

            this.interSpan         = params.container_interSpan;
            this.titleWidth        = null;
            this.titleHeight       = null;
            this.titleFont         = null;
            this.fitTitleMinFont   = params.container_fitTxtTitleMinFont;
            this.fitTextPadding    = params.container_fitTextPadding;
            this.cornerRad         = params.container_cornerRad;
            this.strokeWidth       = params.container_strokeWidth;

            this.rectWidth         = 0;
            this.rectHeight        = 0;
            this.maxRectWidth      = 0;
            this.maxRectHeight     = 0;

            this.nodeRectWidth  = params.node_minWidth;
            this.nodeRectHeight = params.node_minHeight;

            this.minTopLeftX       = 0;
            this.minTopLeftY       = 0;
            this.maxTopLeftX       = 0;
            this.maxTopLeftY       = 0;
            this.isJailed          = false;

            // coord top left point
            this.rectTopLeftX      = 0;
            this.rectTopLeftY      = 0;
            // coord top middle point
            this.rectTopMiddleX    = 0;
            this.rectTopMiddleY    = 0;
            // coord top right point
            this.rectTopRightX     = 0;
            this.rectTopRightY     = 0;
            // coord middle left point
            this.rectMiddleLeftX   = 0;
            this.rectMiddleLeftY   = 0;
            // coord rect middle point
            this.rectMiddleX       = 0;
            this.rectMiddleY       = 0;
            // coord middle right point
            this.rectMiddleRightX  = 0;
            this.rectMiddleRightY  = 0;
            //coord bottom left point
            this.rectBottomLeftX   = 0;
            this.rectBottomLeftY   = 0;
            //coord bottom middle point
            this.rectBottomMiddleX = 0;
            this.rectBottomMiddleY = 0
            //coord bottom right point
            this.rectBottomRightX  = 0;
            this.rectBottomRightY  = 0;

            this.oUnselected = params.container_opacUnselec;
            this.oSelected   = params.container_opacSelec;

            this.mvx = 0;
            this.mvy = 0;

            var containerRef = this;

            /**
             * x = abs of containerR[0], y = ord of containerR[0]
             */
            var defineRectPoints = function(x,y) {
                containerRef.rectTopLeftX     = x;
                containerRef.rectTopLeftY     = y;

                containerRef.rectTopMiddleX   = containerRef.rectTopLeftX + containerRef.rectWidth/2;
                containerRef.rectTopMiddleY   = containerRef.rectTopLeftY;

                containerRef.rectTopRightX    = containerRef.rectTopLeftX + containerRef.rectWidth;
                containerRef.rectTopRightY    = containerRef.rectTopLeftY;

                containerRef.rectMiddleLeftX  = containerRef.rectTopLeftX;
                containerRef.rectMiddleLeftY  = containerRef.rectTopLeftY + containerRef.rectHeight/2;

                containerRef.rectMiddleRightX = containerRef.rectTopRightX;
                containerRef.rectMiddleRightY = containerRef.rectMiddleLeftY;

                containerRef.rectBottomLeftX  = containerRef.rectTopLeftX;
                containerRef.rectBottomLeftY  = containerRef.rectTopLeftY + containerRef.rectHeight;

                containerRef.rectBottomMiddleX = containerRef.rectTopMiddleX;
                containerRef.rectBottomMiddleY = containerRef.rectBottomLeftY;

                containerRef.rectBottomRightX = containerRef.rectTopRightX;
                containerRef.rectBottomRightY = containerRef.rectBottomLeftY;

                containerRef.rectMiddleX = containerRef.rectTopMiddleX;
                containerRef.rectMiddleY = containerRef.rectMiddleLeftY;
            };

            var cMove = function (dx, dy) {
                    var rx = containerRef.extrx,
                        ry = containerRef.extry;

                    if (!containerRef.rightClick) {
                        if (containerRef.isJailed) {
                            if (containerRef.minTopLeftX > rx + dx)
                                dx = containerRef.minTopLeftX - rx;
                            if (containerRef.minTopLeftY > ry + dy)
                                dy = containerRef.minTopLeftY - ry;
                            if (containerRef.maxTopLeftX < rx + dx)
                                dx = containerRef.maxTopLeftX - rx;
                            if (containerRef.maxTopLeftY < ry + dy)
                                dy = containerRef.maxTopLeftY - ry;
                        }

                        containerRef.r.move(dx, dy);
                        containerRef.r.safari();
                    }
                };

            var containerDragger = function() {
                    if (!containerRef.rightClick) {
                        containerRef.r.drag(containerRef,"container");
                    }
                },
                containerMove = function(dx,dy) {
                    if (!containerRef.rightClick)
                        cMove(dx,dy);
                },
                containerUP =  function() {
                    if (!containerRef.rightClick)
                        containerRef.r.up();
                },
                mouseDown = function(e) {
                    if (e.which == 3) {
                        if (containerRef.menuHided) {
                            containerRef.menuSet = containerRef.r.getContainerMenuSet();
                            containerRef.menuSet.mousedown(menuMouseDown);
                            for (var i = 0, ii = containerRef.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    containerRef.menuSet[i].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY +10, fill: containerRef.color});
                                else if (i==1)
                                    containerRef.menuSet[i].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY+30});
                                else
                                    containerRef.menuSet[i].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY+30+(i-1)*15});
                            }
                            containerRef.menu = containerRef.r.menu(containerRef.rectTopMiddleX,containerRef.rectTopMiddleY+10,containerRef.menuSet).
                                attr({fill: containerRef.menuFillColor, stroke: containerRef.menuStrokeColor, "stroke-width": containerRef.menuStrokeWidth, "fill-opacity": containerRef.menuOpacity});
                            containerRef.menu.mousedown(menuMouseDown);
                            containerRef.menu.toFront();
                            containerRef.menuSet.toFront();
                            containerRef.menuSet.show();
                            containerRef.menuHided=false;
                        } else {
                            containerRef.menu.remove();
                            containerRef.menuSet.remove();
                            containerRef.menuHided=true;
                        }
                        containerRef.rightClick=true;
                        if (containerRef.r.getDisplayMainMenu())
                            containerRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        containerRef.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        containerRef.menu.remove();
                        containerRef.menuSet.remove();
                        containerRef.menuHided=true;
                        containerRef.rightClick=true;
                        if (containerRef.r.getDisplayMainMenu())
                            containerRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        containerRef.rightClick=false;
                    }
                };

            this.toString = function() {
                return "{\n Container " + this.containerName + " : ("+this.rectMiddleX+","+this.rectMiddleY+")\n}";
            };

            this.pushNode = function (node) {
                this.containerNodes.addNode(node);
            };

            this.getRectMiddlePoint = function() {
                return {
                    x: this.rectMiddleX,
                    y: this.rectMiddleY
                };
            };

            this.getRectSize = function() {
                return {
                    width  : this.rectWidth,
                    height : this.rectHeight
                };
            };

            this.getMaxRectSize = function() {
                return {
                    width  : this.maxRectWidth,
                    height : this.maxRectHeight
                };
            };

            this.getMaxBoxSize = function() {
                return {
                    width  : this.maxRectWidth,
                    height : this.maxRectHeight
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
                    bottomRightY: this.rectBottomRightY
                };
            };

            this.getContainerCoords = function() {
                return {
                    x: this.rectTopLeftX,
                    y: this.rectTopLeftY
                }
            };

            var getMaxWidth = function(firstWidth) {
                var fontSize = containerRef.txtFont["font-size"];
                fontSize = helper_.fitText(fontSize,containerRef.rectWidth-containerRef.fitTextPadding,1.5,containerRef.fitTitleMinFont);
                containerRef.txtFont["font-size"]=fontSize;
                containerRef.titleWidth  = containerRef.name.width(containerRef.txtFont);
                containerRef.titleHeight = containerRef.name.height(containerRef.txtFont);

                return Math.max(firstWidth,containerRef.titleWidth+containerRef.fitTextPadding);
            };

            this.setSize = function () {
                var mtxX        = this.containerNodes.getMtxSize().x,
                    mtxY        = this.containerNodes.getMtxSize().y;

                this.rectWidth  = getMaxWidth(this.interSpan*(mtxX+1) + this.nodeRectWidth*mtxX);
                this.rectHeight = containerRef.containerHat_.height + this.titleHeight + this.interSpan*(mtxY+1) + this.nodeRectHeight*mtxY;

                defineRectPoints(this.X,this.Y);
            };

            this.setMaxSize = function() {
                var nodesCount = this.containerNodes.getMtxCount();
                this.maxRectWidth = getMaxWidth(nodesCount*this.nodeRectWidth + (nodesCount+1)*this.interSpan);
                this.maxRectHeight = this.containerHat_.height + this.titleHeight + this.interSpan*(nodesCount+1) + this.nodeRectHeight*nodesCount;
            };

            this.setTopLeftCoord = function(x,y) {
                defineRectPoints(x,y);
            };

            this.setMoveJail = function(minJailX, minJailY, maxJailX, maxJailY) {
                this.minTopLeftX = minJailX;
                this.minTopLeftY = minJailY;
                this.maxTopLeftX = maxJailX - this.rectWidth;
                this.maxTopLeftY = maxJailY - this.rectHeight;
                this.isJailed    = true;
            };

            this.definedNodesPoz = function() {
                var mtxX        = this.containerNodes.getMtxSize().x,
                    mtxY        = this.containerNodes.getMtxSize().y;
                var i = 0, ii = 0, j = 0, jj = 0;

                for (i = 0, ii = mtxX; i < ii; i++) {
                    for (j = 0, jj = mtxY; j < jj; j++) {
                        this.containerNodes.getNodeFromMtx(i, j).setPoz(
                            this.rectTopLeftX + (this.interSpan+this.nodeRectWidth)*i + this.interSpan,
                            this.rectTopLeftY + this.containerHat_.height + this.titleHeight + (this.interSpan+this.nodeRectHeight)*j + this.interSpan
                        );
                    }
                }
            };

            this.getLinkedTreeObjectsCount = function() {
                return this.linkedTreeObjects.length;
            };

            this.sortLinkedTreeObjects = function() {
                this.linkedTreeObjects.sort(minMaxLinkedTreedObjectsComparator);
                this.linkedContainers.sort(minMaxLinkedTreedObjectsComparator);
                this.linkedBus.sort(minMaxLinkedTreedObjectsComparator);
            };

            this.setSortOrdering = function(sort) {
                this.sortOrdering = sort;
            };

            this.getLinkedTreeObjects = function() {
                return this.linkedTreeObjects
            };

            this.pushLinkedContainer = function(container) {
                var isAlreadyPushed = this.isLinkedToContainer(container);
                if (!isAlreadyPushed) {
                    this.linkedContainers.push(container);
                    this.linkedTreeObjects.push(container);
                }
            };

            this.isLinkedToContainer = function(container) {
                for (var i = 0, ii = this.linkedContainers.length; i < ii; i++) {
                    if (this.linkedContainers[i].ID==container.ID)
                        return true;
                }
                return false;
            };

            this.getLinkedContainers = function () {
                return this.linkedContainers;
            };

            this.pushLinkedBus = function(bus) {
                var isAlreadyPushed = this.isLinkedToBus(bus);
                if (!isAlreadyPushed) {
                    this.linkedBus.push(bus);
                    this.linkedTreeObjects.push(bus);
                }
            };

            this.isLinkedToBus = function(bus) {
                for (var i = 0, ii = this.linkedBus.length; i < ii; i++) {
                    if (this.linkedBus[i].equal(bus))
                        return true;
                }
                return false;
            };

            this.getLinkedBus = function() {
                return this.linkedBus;
            };

            this.print = function(r_) {
                this.r = r_;
                this.containerHat_.print(this.r,this.rectTopLeftX + (this.rectWidth/2),this.rectTopLeftY,this.color);
                this.containerName = this.r.text(0, 0, this.name).attr(this.txtFont).attr({'fill':this.color});
                this.containerName.attr({x: this.rectTopLeftX + (this.rectWidth/2), y: this.rectTopLeftY + this.containerHat_.height + this.titleHeight});

                this.rect = this.r.rect(this.rectTopLeftX, this.rectTopLeftY, this.rectWidth, this.rectHeight, this.cornerRad);
                this.rect.attr({fill: this.color, stroke: this.color, "fill-opacity": containerRef.oUnselected, "stroke-width": this.strokeWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(containerMove, containerDragger, containerUP);
            };

            this.toFront = function() {
                this.containerName.toFront();
                this.rect.toFront();
            };

            this.name  = this.name.split("://")[1].split(":")[0];
            var tmp1 = this.name.split(".")[0], tmp2 = this.name.split(".")[1].split(".")[0];
            this.name = tmp1 + "." +tmp2;
        }

        return container;
    });