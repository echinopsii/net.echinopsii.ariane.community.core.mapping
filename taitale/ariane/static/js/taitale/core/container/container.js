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

            //noinspection JSUnresolvedVariable
            this.ID       	  = JSONContainerDesc.containerID;
            //noinspection JSUnresolvedVariable
            this.company      = JSONContainerDesc.containerCompany;
            //noinspection JSUnresolvedVariable
            this.product      = JSONContainerDesc.containerProduct;
            this.type         = JSONContainerDesc.containerType;
            //noinspection JSUnresolvedVariable
            this.gateURI      = JSONContainerDesc.containerGateURI;
            //noinspection JSUnresolvedVariable
            this.name         = JSONContainerDesc.containerGateURI;
            //noinspection JSUnresolvedVariable
            this.properties   = JSONContainerDesc.containerProperties;
            this.localisation = null;

            //noinspection JSUnresolvedVariable
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
            //noinspection JSUnresolvedVariable
            this.color             = (this.properties.supportTeam!=null && this.properties.supportTeam.color!=null) ? "#"+this.properties.supportTeam.color : Raphael.getColor();
            this.txtFont           = params.container_txtTitle;
            this.X                 = x_;
            this.Y                 = y_;
            this.containerName     = null;
            this.rect              = null;
            this.rightClick        = false;
            this.isMoving          = false;
            this.isInserted        = false;

            this.containerNodes    = new containerMatrix();
            this.containerHat_     = new containerHat(this.company,this.product,this.type);

            this.linkedTreeObjects                  = [];
            this.sortOrdering                       = 1;

            this.linkedBus         = [];
            this.linkedContainers  = [];

            this.interSpan         = params.container_interSpan;
            this.titleWidth        = null;
            this.titleHeight       = null;
            //this.titleFont         = null;
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
            //this.rectMiddleLeftX   = 0;
            this.rectMiddleLeftY   = 0;
            // coord rect middle point
            this.rectMiddleX       = 0;
            this.rectMiddleY       = 0;
            // coord middle right point
            //this.rectMiddleRightX  = 0;
            //this.rectMiddleRightY  = 0;
            //coord bottom left point
            this.rectBottomLeftX   = 0;
            this.rectBottomLeftY   = 0;
            //coord bottom middle point
            //this.rectBottomMiddleX = 0;
            //this.rectBottomMiddleY = 0
            //coord bottom right point
            this.rectBottomRightX  = 0;
            this.rectBottomRightY  = 0;

            this.oUnselected = params.container_opacUnselec;
            this.oSelected   = params.container_opacSelec;

            this.mvx = 0;
            this.mvy = 0;


            this.menu              = null;
            this.menuFillColor     = params.container_menuFillColor;
            this.menuOpacity       = params.container_menuOpacity;
            this.menuStrokeWidth   = params.container_menuStrokeWidth;
            this.menuHided         = true;

            this.containerMenuSet = null;
            this.containerMenuProperties = null;
            this.containerMenuPropertiesRect = null;

            this.containerMainTitleTXT  = params.container_menuMainTitle;
            this.containerFieldTXT      = params.container_menuFields;
            this.containerFieldTXTOver  = params.container_menuFieldsOver;

            this.menuFieldStartEditTitle  = "Edition mode ON";
            this.menuFieldStopEditTitle   = "Edition mode OFF";

            var containerRef = this;

            var minMaxLinkedTreedObjectsComparator = function(linkedObject1, linkedObject2) {
                return (linkedObject2.getLinkedTreeObjectsCount() - linkedObject1.getLinkedTreeObjectsCount())*containerRef.sortOrdering;
            };

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

            var getMaxWidth = function(firstWidth) {
                var fontSize = containerRef.txtFont["font-size"];
                fontSize = helper_.fitText(fontSize,containerRef.rectWidth-containerRef.fitTextPadding,1.5,containerRef.fitTitleMinFont);
                containerRef.txtFont["font-size"]=fontSize;
                containerRef.titleWidth  = containerRef.name.width(containerRef.txtFont);
                containerRef.titleHeight = containerRef.name.height(containerRef.txtFont);

                return Math.max(firstWidth,containerRef.titleWidth+containerRef.fitTextPadding);
            };

            var mouseDown = function(e) {
                    if (e.which == 3) {
                        if (containerRef.menuHided) {
                            containerRef.containerMenuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = containerRef.containerMenuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    containerRef.containerMenuSet[i].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = containerRef.containerMenuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": containerRef.rectTopMiddleX - fieldRectWidth/2, "y": containerRef.rectTopMiddleY+30 - fieldRectHeight/2});
                                    containerRef.containerMenuSet[i+1].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY+30});
                                    i++;
                                } else {
                                    fieldRect = containerRef.containerMenuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY+30+(i-1)*15});
                                    containerRef.containerMenuSet[i+1].attr({"x": containerRef.rectTopMiddleX, "y": containerRef.rectTopMiddleY+30+(i-1)*15});
                                    i++;
                                }
                            }
                            if (containerRef.menu != null)
                                containerRef.menu.remove();
                            containerRef.menu = containerRef.r.menu(containerRef.rectTopMiddleX,containerRef.rectTopMiddleY+10,containerRef.containerMenuSet).
                                attr({fill: containerRef.menuFillColor, stroke: containerRef.color, "stroke-width": containerRef.menuStrokeWidth,
                                    "fill-opacity": containerRef.menuOpacity});
                            containerRef.menu.mousedown(menuMouseDown);
                            containerRef.menu.toFront();
                            containerRef.containerMenuSet.toFront();
                            containerRef.containerMenuSet.show();
                            containerRef.menuHided=false;
                        } else {
                            containerRef.menu.toBack();
                            containerRef.containerMenuSet.toBack();
                            containerRef.menu.hide();
                            containerRef.containerMenuSet.hide();
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
                        containerRef.menu.toBack();
                        containerRef.containerMenuSet.toBack();
                        containerRef.menu.hide();
                        containerRef.containerMenuSet.hide();
                        containerRef.menuHided=true;
                        containerRef.rightClick=true;
                        if (containerRef.r.getDisplayMainMenu())
                            containerRef.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        containerRef.rightClick=false;
                    }
                },
                menuFieldOver = function() {
                    this.attr(containerRef.containerFieldTXTOver);
                },
                menuFieldOut = function() {
                    this.attr(containerRef.containerFieldTXT);
                },
                menuFieldPropertyClick = function(e) {
                    if (e.which != 3) {
                        //noinspection JSUnresolvedVariable
                        var details = "<br/> <b>Name</b> : " + containerRef.name +
                            "<br/> <b>Primary Gate</b> : <a href=\"" + containerRef.gateURI + "\" target=\"_blank\">" + containerRef.gateURI + "</a>" +
                            "<br/>" +
                            "<br/> <b>Company</b> : " + containerRef.company +
                            "<br/> <b>Product</b> : " + containerRef.product +
                            "<br/> <b>Type</b> : " + containerRef.type +
                            "<br/>" +
                            ((containerRef.properties.supportTeam!=null) ? "<br/> <b>Support team</b> : " + containerRef.properties.supportTeam.name + "<br/>": "") +
                            ((containerRef.properties.Server!=null) ? "<br/> <b>OS instance hostname</b> : " + containerRef.properties.Server.hostname +
                                "<br/> <b>OS instance type</b> : " + containerRef.properties.Server.os + "<br/>": "") +
                            ((containerRef.properties.Network!=null) ? "<br/> <b>Network ID</b> : " + containerRef.properties.Network.lan +
                                "<br/> <b>Network type</b> : " + containerRef.properties.Network.type +
                                "<br/> <b>Network subnet IP</b> : " + containerRef.properties.Network.subnetip +
                                "<br/> <b>Network subnet mask</b> : " + containerRef.properties.Network.subnetmask +
                                "<br/> <b>Network multicast area</b> : " + ((containerRef.properties.Network.marea!=null) ? containerRef.properties.Network.marea + "<br/>": "no multicast<br/>") :  "") +
                            ((containerRef.properties.Datacenter!=null) ? "<br> <b>Datacenter ID </b> : " + containerRef.properties.Datacenter.dc +
                                "<br/> <b>Datacenter address</b> : " + containerRef.properties.Datacenter.address +
                                "<br/> <b>Datacenter town</b> : " + containerRef.properties.Datacenter.town +
                                "<br/> <b>Datacenter country</b> : " + containerRef.properties.Datacenter.country + "<br/>": "");

                        var sortedKeys = [];

                        for (var key in containerRef.properties)
                            if (key !== "supportTeam" && key !== "Server" && key != "Network" && key != "Datacenter" && key != "manualCoord")
                                if (containerRef.properties.hasOwnProperty(key))
                                    sortedKeys.push(key);
                        sortedKeys.sort();

                        for (var i = 0, ii = sortedKeys.length; i < ii; i++)
                            details += "<br/> <b>"+ sortedKeys[i] + "</b> : " + containerRef.properties[sortedKeys[i]];

                        details += "<br/>";

                        helper_.dialogOpen("containerDetail"+containerRef.ID, "Details of " + containerRef.name, details);
                    }
                };

            var containerDragger = function() {
                    //helper_.debug("drag: " + containerRef.rightClick);
                    if (!containerRef.rightClick)
                        containerRef.moveInit();
                },
                containerMove = function(dx,dy) {
                    //helper_.debug("move: " + containerRef.rightClick);
                    if (!containerRef.rightClick) {
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
                    }
                },
                containerUP =  function() {
                    //helper_.debug("up: " + containerRef.rightClick);
                    if (!containerRef.rightClick)
                        containerRef.r.up();
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

            /*
            this.getRectSize = function() {
                return {
                    width  : this.rectWidth,
                    height : this.rectHeight
                };
            };
            */

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

            /*
            this.getContainerCoords = function() {
                return {
                    x: this.rectTopLeftX,
                    y: this.rectTopLeftY
                }
            };
            */

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
                var i, ii, j, jj;

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
                this.containerHat_.mousedown(mouseDown);
                this.containerHat_.drag(containerMove, containerDragger, containerUP);

                this.containerName = this.r.text(0, 0, this.name).attr(this.txtFont).attr({'fill':this.color});
                this.containerName.attr({x: this.rectTopLeftX + (this.rectWidth/2), y: this.rectTopLeftY + this.containerHat_.height + this.titleHeight});
                this.containerName.mousedown(mouseDown);
                this.containerName.drag(containerMove, containerDragger, containerUP);

                this.rect = this.r.rect(this.rectTopLeftX, this.rectTopLeftY, this.rectWidth, this.rectHeight, this.cornerRad);
                this.rect.attr({fill: this.color, stroke: this.color, "fill-opacity": containerRef.oUnselected, "stroke-width": this.strokeWidth});
                this.rect.mousedown(mouseDown);
                this.rect.drag(containerMove, containerDragger, containerUP);


                this.containerMenuTitle = this.r.text(0,10,"Container menu").attr(this.containerMainTitleTXT);
                var fieldTitle = "Display all properties";
                this.containerMenuPropertiesRect = this.r.rect(0,10,fieldTitle.width(this.containerFieldTXT),fieldTitle.height(this.containerFieldTXT));
                this.containerMenuPropertiesRect.attr({fill: this.color, stroke: this.color, "fill-opacity": 0, "stroke-width": 0});
                this.containerMenuPropertiesRect.mouseover(menuFieldOver);
                this.containerMenuPropertiesRect.mouseout(menuFieldOut);
                this.containerMenuPropertiesRect.mousedown(menuFieldPropertyClick);
                this.containerMenuProperties = this.r.text(0,10,fieldTitle).attr(this.containerFieldTXT);
                this.containerMenuProperties.mouseover(menuFieldOver);
                this.containerMenuProperties.mouseout(menuFieldOut);
                this.containerMenuProperties.mousedown(menuFieldPropertyClick);

                this.containerMenuSet = this.r.set();
                this.containerMenuSet.push(this.containerMenuTitle);
                this.containerMenuSet.push(this.containerMenuPropertiesRect);
                this.containerMenuSet.push(this.containerMenuProperties);
                //containerMenuSet.push(this.text(0,30,"Highlight cluster").attr(containerFieldTXT));
                //containerMenuSet.push(this.text(0,45,"Show gates").attr(containerFieldTXT));
                //containerMenuSet.push(this.text(0,60,"Hide gates").attr(containerFieldTXT));
                this.containerMenuSet.toBack();
                this.containerMenuSet.hide();
            };

            this.toFront = function() {
                this.containerHat_.toFront();
                this.containerName.toFront();
                this.rect.toFront();
                this.containerNodes.toFront();
            };

            this.changeInit = function() {
                this.extrx = this.rect.attr("x");
                this.extry = this.rect.attr("y");
                this.extt0x = this.containerName.attr("x");
                this.extt0y = this.containerName.attr("y");

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
                this.isMoving = false;
            };

            // MOVEABLE

            this.moveInit = function() {
                var i, ii, j, jj;
                var mtxX        = this.containerNodes.getMtxSize().x,
                    mtxY        = this.containerNodes.getMtxSize().y;

                this.r.containersOnMovePush(this);
                this.r.moveSetPush(this.containerName);
                this.r.moveSetPush(this.rect);

                for (i = 0, ii = mtxX; i < ii; i++)
                    for (j = 0, jj = mtxY; j < jj; j++)
                        this.containerNodes.getNodeFromMtx(i, j).moveInit();

                this.changeInit();

                this.rect.animate({"fill-opacity": this.oSelected}, 500);
            };

            this.moveAction = function(dx, dy) {
                this.mvx = dx; this.mvy = dy;
                this.containerHat_.move(this.r, this.extrx + (this.rectWidth/2) + dx, this.extry + dy);
            };

            this.moveUp = function() {
                var attrect  = {x: this.extrx + this.mvx, y: this.extry + this.mvy},
                    attrtxt0 = {x: this.extt0x + this.mvx, y: this.extt0y + this.mvy};

                this.mvx=0; this.mvy=0;
                this.rect.attr(attrect);
                this.containerName.attr(attrtxt0);

                this.setTopLeftCoord(this.rect.attr("x"),this.rect.attr("y"));
                this.rect.animate({"fill-opacity": this.oUnselected}, 500);
                this.toFront();

                this.changeUp();
            };

            // EDITABLE




            this.name  = this.name.split("://")[1].split(":")[0];
            var tmp1 = this.name.split(".")[0], tmp2 = this.name.split(".")[1].split(".")[0];
            this.name = tmp1 + "." +tmp2;
        }

        return container;
    });