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

            var ID       	 = JSONContainerDesc.containerID,
                company      = JSONContainerDesc.containerCompany,
                product      = JSONContainerDesc.containerProduct,
                type         = JSONContainerDesc.containerType,
                gateURI      = JSONContainerDesc.containerGateURI,
                name         = JSONContainerDesc.containerGateURI,
                properties   = JSONContainerDesc.containerProperties,
                localisation = null;

            var tmpDatacenter = properties.Datacenter,
                tmpNetwork    = properties.Network;
            if (tmpDatacenter != null && tmpNetwork != null) {
                localisation = prototypes_.create(prototypes_.ntprototype, {
                    dcproto:    prototypes_.create(prototypes_.dcprototype, tmpDatacenter),
                    type:       tmpNetwork.type,
                    marea:      tmpNetwork.marea,
                    lan:        tmpNetwork.lan,
                    subnetip:   tmpNetwork.subnetip,
                    subnetmask: tmpNetwork.subnetmask
                });
            }

            var layoutData        = null;

            var r                 = null,
                color             = (properties.supportTeam!=null && properties.supportTeam.color!=null) ? "#"+properties.supportTeam.color : Raphael.getColor(),
                txtFont           = params.container_txtTitle,
                X                 = x_,
                Y                 = y_,
                containerName     = null,
                containerR        = null,
                rect              = null,
                rightClick        = false,
                isMoving          = false,
                isInserted        = false;

            var menu              = null,
                menuSet           = null,
                menuFillColor     = params.container_menuFillColor,
                menuStrokeColor   = params.container_menuStrokeColor,
                menuOpacity       = params.container_menuOpacity,
                menuStrokeWidth   = params.container_menuStrokeWidth,
                menuMainTitleTXT  = params.container_menuMainTitle,
                menuFieldTXT      = params.container_menuFields,
                menuHided         = true;

            var containerNodes    = new containerMatrix(),
                containerHat_     = new containerHat(company,product,type);

            var linkedTreeObjects                  = [],
                sortOrdering                       = 1,
                minMaxLinkedTreedObjectsComparator = function(linkedObject1, linkedObject2) {
                    return (linkedObject2.getLinkedTreeObjectsCount() - linkedObject1.getLinkedTreeObjectsCount())*sortOrdering;
                };

            var linkedBus         = [],
                linkedContainers  = [];

            var interSpan         = params.container_interSpan,
                titleWidth        = null,
                titleHeight       = null,
                titleFont         = null,
                fitTitleMinFont   = params.container_fitTxtTitleMinFont,
                fitTextPadding    = params.container_fitTextPadding,
                cornerRad         = params.container_cornerRad,
                strokeWidth       = params.container_strokeWidth;

            var rectWidth         = 0,
                rectHeight        = 0,
                maxRectWidth      = 0,
                maxRectHeight     = 0;

            var nodeRectWidth  = params.node_minWidth,
                nodeRectHeight = params.node_minHeight ;

            var minTopLeftX       = 0,
                minTopLeftY       = 0,
                maxTopLeftX       = 0,
                maxTopLeftY       = 0,
                isJailed          = false;

            // coord top left point
            var	rectTopLeftX      = 0,
                rectTopLeftY      = 0,
            // coord top middle point
                rectTopMiddleX    = 0,
                rectTopMiddleY    = 0,
            // coord top right point
                rectTopRightX     = 0,
                rectTopRightY     = 0,
            // coord middle left point
                rectMiddleLeftX   = 0,
                rectMiddleLeftY   = 0,
            // coord rect middle point
                rectMiddleX       = 0,
                rectMiddleY       = 0,
            // coord middle right point
                rectMiddleRightX  = 0,
                rectMiddleRightY  = 0,
            //coord bottom left point
                rectBottomLeftX   = 0,
                rectBottomLeftY   = 0,
            //coord bottom middle point
                rectBottomMiddleX = 0,
                rectBottomMiddleY = 0,
            //coord bottom right point
                rectBottomRightX  = 0,
                rectBottomRightY  = 0;

            var oUnselected = params.container_opacUnselec,
                oSelected   = params.container_opacSelec;

            /**
             * x = abs of containerR[0], y = ord of containerR[0]
             */
            var defineRectPoints = function(x,y) {
                rectTopLeftX     = x,
                rectTopLeftY     = y;

                rectTopMiddleX   = rectTopLeftX + rectWidth/2;
                rectTopMiddleY   = rectTopLeftY;

                rectTopRightX    = rectTopLeftX + rectWidth;
                rectTopRightY    = rectTopLeftY;

                rectMiddleLeftX  = rectTopLeftX;
                rectMiddleLeftY  = rectTopLeftY + rectHeight/2;

                rectMiddleRightX = rectTopRightX;
                rectMiddleRightY = rectMiddleLeftY;

                rectBottomLeftX  = rectTopLeftX;
                rectBottomLeftY  = rectTopLeftY + rectHeight;

                rectBottomMiddleX = rectTopMiddleX;
                rectBottomMiddleY = rectBottomLeftY;

                rectBottomRightX = rectTopRightX;
                rectBottomRightY = rectBottomLeftY;

                rectMiddleX = rectTopMiddleX;
                rectMiddleY = rectMiddleLeftY;
            };

            var cDragg = function () {
                    var mtxX        = containerNodes.getMtxSize().x,
                        mtxY        = containerNodes.getMtxSize().y;

                    if (!menuHided) {
                        menu.remove();
                        menuSet.remove();
                        menuHided=true;
                        if (r.getDisplayMainMenu())
                            r.setDisplayMainMenu(false);
                    }

                    for (var i = 0, ii =  mtxX; i < ii; i++) {
                        for (var j = 0, jj =  mtxY; j < jj; j++) {
                            containerNodes.getNodeFromMtx(i, j).dragger();
                        }
                    }

                    rect.animate({"fill-opacity": oSelected}, 500);

                    isMoving = true;
                },
                cMove = function (rx,ry,t0x,t0y,dx, dy) {
                    if (!rightClick) {
                        var mtxX        = containerNodes.getMtxSize().x,
                            mtxY        = containerNodes.getMtxSize().y;

                        if (isJailed) {
                            if (minTopLeftX > rx + dx)
                                dx = minTopLeftX - rx;
                            if (minTopLeftY > ry + dy)
                                dy = minTopLeftY - ry;
                            if (maxTopLeftX < rx + dx)
                                dx = maxTopLeftX - rx;
                            if (maxTopLeftY < ry + dy)
                                dy = maxTopLeftY - ry;
                        };

                        var attrect = {x: rx + dx, y: ry + dy},
                            attrtxt1 = {x: t0x + dx, y: t0y + dy};

                        rect.attr(attrect);
                        containerR[0].attr(attrtxt1);

                        for (var i = 0, ii = mtxX; i < ii; i++) {
                            for (var j = 0, jj = mtxY; j < jj; j++) {
                                containerNodes.getNodeFromMtx(i, j).mover(dx,dy);
                            }
                        }

                        containerHat_.move(r, rx + (rectWidth/2) + dx, ry + dy);
                        defineRectPoints(rect.attr("x"),rect.attr("y"));
                        r.safari();
                    }
                },
                cUP = function () {
                    if (!rightClick) {
                        var mtxX        = containerNodes.getMtxSize().x,
                            mtxY        = containerNodes.getMtxSize().y;

                        rect.animate({"fill-opacity": oUnselected}, 500);

                        for (var i = 0, ii = mtxX; i < ii; i++) {
                            for (var j = 0, jj = mtxY; j < jj; j++) {
                                containerNodes.getNodeFromMtx(i, j).uper();
                            }
                        }

                        isMoving = false;
                    }
                };

            var containerDragger = function() {
                    if (!rightClick) {
                        this.rx = this.attr("x");
                        this.ry = this.attr("y");
                        this.t0x = containerR[0].attr("x");
                        this.t0y = containerR[0].attr("y");
                        cDragg();
                    }
                },
                containerMove = function(dx,dy) {
                    if (!rightClick)
                        cMove(this.rx,this.ry,this.t0x,this.t0y,dx,dy);
                },
                containerUP =  function() {
                    if (!rightClick)
                        cUP();
                },
                mouseDown = function(e){
                    if (e.which == 3) {
                        if (menuHided) {
                            menuSet = r.getContainerMenuSet();
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

            this.dragger = function() {
                this.extrx = rect.attr("x");
                this.extry = rect.attr("y");
                this.extt0x = containerR[0].attr("x");
                this.extt0y = containerR[0].attr("y");
                cDragg();
            };
            this.mover = function(dx,dy) {
                cMove(this.extrx,this.extry,this.extt0x,this.extt0y,dx,dy);
            };
            this.uper =  function() {
                cUP();
            };

            this.toString = function() {
                return "{\n Container " + containerName + " : ("+rectMiddleX+","+rectMiddleY+")\n}";
            };

            this.pushNode = function (node) {
                containerNodes.addNode(node);
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

            this.getHat = function() {
                return containerHat_;
            }

            this.getID = function() {
                return ID;
            }

            this.getRectMiddlePoint = function() {
                return {
                    x: rectMiddleX,
                    y: rectMiddleY
                };
            };

            this.getRectSize = function() {
                return {
                    width  : rectWidth,
                    height : rectHeight
                };
            };

            this.getMaxRectSize = function() {
                return {
                    width  : maxRectWidth,
                    height : maxRectHeight
                };
            };

            this.getMaxBoxSize = function() {
                return {
                    width  : maxRectWidth,
                    height : maxRectHeight
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
                    bottomRightY: rectBottomRightY
                };
            };

            this.getContainerCoords = function() {
                return {
                    x: rectTopLeftX,
                    y: rectTopLeftY
                }
            }

            this.getLocalisation = function() {
                return localisation;
            };

            var getMaxWidth = function(firstWidth) {
                var fontSize = txtFont["font-size"];
                fontSize = helper_.fitText(fontSize,rectWidth-fitTextPadding,1.5,fitTitleMinFont);
                txtFont["font-size"]=fontSize;
                titleWidth  = name.width(txtFont);
                titleHeight = name.height(txtFont);

                return Math.max(firstWidth,titleWidth+fitTextPadding);
            };

            this.setSize = function () {
                var mtxX        = containerNodes.getMtxSize().x,
                    mtxY        = containerNodes.getMtxSize().y;

                rectWidth    = getMaxWidth(interSpan*(mtxX+1) + nodeRectWidth*mtxX);
                rectHeight = containerHat_.height + titleHeight + interSpan*(mtxY+1) + nodeRectHeight*mtxY;

                defineRectPoints(X,Y);
            };

            this.setMaxSize = function() {
                var nodesCount = containerNodes.getMtxCount();
                maxRectWidth = getMaxWidth(nodesCount*nodeRectWidth + (nodesCount+1)*interSpan);
                maxRectHeight = containerHat_.height + titleHeight + interSpan*(nodesCount+1) + nodeRectHeight*nodesCount;
            };

            this.setTopLeftCoord = function(x,y) {
                defineRectPoints(x,y);
            };

            this.setMoveJail = function(minJailX, minJailY, maxJailX, maxJailY) {
                minTopLeftX = minJailX;
                minTopLeftY = minJailY;
                maxTopLeftX = maxJailX - rectWidth;
                maxTopLeftY = maxJailY - rectHeight;
                isJailed    = true;
            };

            this.definedNodesPoz = function() {
                var mtxX        = containerNodes.getMtxSize().x,
                    mtxY        = containerNodes.getMtxSize().y;

                for (var i = 0, ii = mtxX; i < ii; i++) {
                    for (var j = 0, jj = mtxY; j < jj; j++) {
                        containerNodes.getNodeFromMtx(i, j).setPoz(
                            rectTopLeftX + (interSpan+nodeRectWidth)*i + interSpan,
                            rectTopLeftY + containerHat_.height + titleHeight + (interSpan+nodeRectHeight)*j + interSpan
                        );
                    }
                }
            };

            this.getLayoutData = function() {
                return layoutData;
            };

            this.setLayoutData = function(data) {
                layoutData = data;
            };

            this.isInserted = function() {
                return isInserted;
            };

            this.setInserted = function() {
                isInserted = true;
            };

            this.unsetInserted = function() {
                isInserted = false;
            };

            this.getLinkedTreeObjectsCount = function() {
                return linkedTreeObjects.length;
            };

            this.sortLinkedTreeObjects = function() {
                linkedTreeObjects.sort(minMaxLinkedTreedObjectsComparator);
                linkedContainers.sort(minMaxLinkedTreedObjectsComparator);
                linkedBus.sort(minMaxLinkedTreedObjectsComparator);
            };

            this.setSortOrdering = function(sort) {
                sortOrdering = sort;
            };

            this.getLinkedTreeObjects = function() {
                return linkedTreeObjects
            };

            this.pushLinkedContainer = function(container) {
                var isAlreadyPushed = this.isLinkedToContainer(container);
                if (!isAlreadyPushed) {
                    linkedContainers.push(container);
                    linkedTreeObjects.push(container);
                }
            };

            this.isLinkedToContainer = function(container) {
                for (var i = 0, ii = linkedContainers.length; i < ii; i++) {
                    if (linkedContainers[i].getID()==container.getID())
                        return true;
                }
                return false;
            };

            this.getLinkedContainers = function () {
                return linkedContainers;
            };

            this.pushLinkedBus = function(bus) {
                var isAlreadyPushed = this.isLinkedToBus(bus);
                if (!isAlreadyPushed) {
                    linkedBus.push(bus);
                    linkedTreeObjects.push(bus);
                }
            };

            this.isLinkedToBus = function(bus) {
                for (var i = 0, ii = linkedBus.length; i < ii; i++) {
                    if (linkedBus[i].equal(bus))
                        return true;
                }
                return false;
            };

            this.getLinkedBus = function() {
                return linkedBus;
            };

            this.print = function(r_) {
                r                 = r_;
                containerR        = r.set();
                containerHat_.print(r,rectTopLeftX + (rectWidth/2),rectTopLeftY,color)
                containerName     = r.text(0, 0, name).attr(txtFont).attr({'fill':color});
                containerName.attr({x: rectTopLeftX + (rectWidth/2), y: rectTopLeftY + containerHat_.height + titleHeight});
                containerR.push(containerName);
                containerR[0].attr({cursor: "move"});

                rect = r.rect(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad);
                rect.attr({fill: color, stroke: color, "fill-opacity": oUnselected, "stroke-width": strokeWidth});
                rect.mousedown(mouseDown);
                rect.drag(containerMove, containerDragger, containerUP);
                containerR.push(rect);
            };

            this.toFront = function() {
                containerR[0].toFront();
                containerR[1].toFront();
            };

            name  = name.split("://")[1].split(":")[0];
            var tmp1 = name.split(".")[0], tmp2 = name.split(".")[1].split(".")[0];
            name = tmp1 + "." +tmp2;
        };

        return container;
    });