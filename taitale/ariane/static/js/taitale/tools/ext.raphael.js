// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TOOLS - ext.raphael                           │ \\
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
    function (Raphael, helper, params) {

        var helper_ = new helper();
            tokenRegex = /\{([^\}]+)\}/g,
            objNotationRegex = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|")(.+?)\2\])(\(\))?/g, // matches .xxxxx or ["xxxxx"] to run over object properties
            replacer = function (all, key, obj) {
                var res = obj;
                key.replace(objNotationRegex, function (all, name, quote, quotedName, isFunc) {
                    name = name || quotedName;
                    if (res) {
                        if (name in res) {
                            res = res[name];
                        }
                        typeof res == "function" && isFunc && (res = res());
                    }
                });
                res = (res == null || res == obj ? all : res) + "";
                return res;
            },
            fill = function (str, obj) {
                return String(str).replace(tokenRegex, function (all, key) {
                    return replacer(all, key, obj);
                });
            };

        Raphael.fn.popup = function (X, Y, sett, pos, ret) {
            pos = String(pos || "top-middle").split("-");
            pos[1] = pos[1] || "middle";
            var r = 5,
                bb = sett.getBBox(),
                w = bb.width,
                h = bb.height,
                x = bb.x - r,
                y = bb.y - r,
                gap = Math.min(h / 2, w / 2, 10),
                shapes = {
                    top: "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}l-{right},0-{gap},{gap}-{gap}-{gap}-{left},0a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z",
                    bottom: "M{x},{y}l{left},0,{gap}-{gap},{gap},{gap},{right},0a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z",
                    right: "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}l0-{bottom}-{gap}-{gap},{gap}-{gap},0-{top}a{r},{r},0,0,1,{r}-{r}z",
                    left: "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}l0,{top},{gap},{gap}-{gap},{gap},0,{bottom}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z"
                },
                //offset = {
                //    hx0: X - (x + r + w - gap * 2),
                //    hx1: X - (x + r + w / 2 - gap),
                //    hx2: X - (x + r + gap),
                //    vhy: Y - (y + r + h + r + gap),
                //    "^hy": Y - (y - gap)
                //},
                mask = [{
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    right: 0,
                    left: w - gap * 2,
                    bottom: 0,
                    top: h - gap * 2,
                    r: r,
                    h: h,
                    gap: gap
                }, {
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    left: w / 2 - gap,
                    right: w / 2 - gap,
                    top: h / 2 - gap,
                    bottom: h / 2 - gap,
                    r: r,
                    h: h,
                    gap: gap
                }, {
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    left: 0,
                    right: w - gap * 2,
                    top: 0,
                    bottom: h - gap * 2,
                    r: r,
                    h: h,
                    gap: gap
                }][pos[1] == "middle" ? 1 : (pos[1] == "top" || pos[1] == "left") * 2];
            var dx = 0,
                dy = 0,
                out = this.path(fill(shapes[pos[0]], mask)).insertBefore(sett);
            switch (pos[0]) {
                case "top":
                    dx = X - (x + r + mask.left + gap);
                    dy = Y - (y + r + h + r + gap);
                    break;
                case "bottom":
                    dx = X - (x + r + mask.left + gap);
                    dy = Y - (y - gap);
                    break;
                case "left":
                    dx = X - (x + r + w + r + gap);
                    dy = Y - (y + r + mask.top + gap);
                    break;
                case "right":
                    dx = X - (x - gap);
                    dy = Y - (y + r + mask.top + gap);
                    break;
            }
            out.translate(dx, dy);
            if (ret) {
                ret = out.attr("path");
                out.remove();
                return {
                    path: ret,
                    dx: dx,
                    dy: dy
                };
            }
            sett.translate(dx, dy);
            return out;
        };

        Raphael.fn.menu = function (X, Y, set) {
            var r = 5,
                bb = set.getBBox(),
                w = bb.width,
                h = bb.height,
                x = bb.x - r,
                y = bb.y - r,
                gap = Math.min(h / 2, w / 2, 10),
                shape = "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4},-{w4},-{w4},-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z",
                //offset = {
                //    hx0: X - (x + r + w - gap * 2),
                //    hx1: X - (x + r + w / 2 - gap),
                //    hx2: X - (x + r + gap),
                //    vhy: Y - (y + r + h + r + gap),
                //    "^hy": Y - (y - gap)
                //},
                mask = [{
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    right: 0,
                    left: w - gap * 2,
                    bottom: 0,
                    top: h - gap * 2,
                    r: r,
                    h: h,
                    gap: gap
                }, {
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    left: w / 2 - gap,
                    right: w / 2 - gap,
                    top: h / 2 - gap,
                    bottom: h / 2 - gap,
                    r: r,
                    h: h,
                    gap: gap
                }, {
                    x: x + r,
                    y: y,
                    w: w,
                    w4: w / 4,
                    h4: h / 4,
                    left: 0,
                    right: w - gap * 2,
                    top: 0,
                    bottom: h - gap * 2,
                    r: r,
                    h: h,
                    gap: gap
                }][1];
            var //dx  = 0,
                //dy  = 0,
                out = this.path(fill(shape, mask)).insertBefore(set);
            //dx = X - (x + r + mask.left + gap);
            //dy = Y - (y + r + h + r + gap);
            //out.translate(dx, dy);
            return out;
        };

        var displayMainMenu = true;
        Raphael.fn.setDisplayMainMenu = function (display) {
            displayMainMenu=display;
        };
        Raphael.fn.getDisplayMainMenu = function () {
            return displayMainMenu;
        };

        var mainMenuSet,
            menuMainTitleTXT  = params.map_menuMainTitle,
            menuFieldTXT      = params.map_menuFields;
        Raphael.fn.setMainMenuSet = function() {
            mainMenuSet = this.set();
            mainMenuSet.push(this.text(0,10,"Taitale menu").attr(menuMainTitleTXT));
            mainMenuSet.push(this.text(0,30,"submenu1").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,45,"submenu2").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,60,"submenu3").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,75,"submenu4").attr(menuFieldTXT));
            mainMenuSet.toBack();
            mainMenuSet.hide();
        };
        Raphael.fn.getMainMenuSet = function() {
            return mainMenuSet;
        };

        var containerMenuSet,
            containerMainTitleTXT  = params.container_menuMainTitle,
            containerFieldTXT      = params.container_menuFields;
        Raphael.fn.setContainerMenuSet = function() {
            containerMenuSet = this.set();
            containerMenuSet.push(this.text(0,10,"Container menu").attr(containerMainTitleTXT));
            containerMenuSet.push(this.text(0,10,"Display all properties").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,30,"Highlight cluster").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,45,"Show gates").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,60,"Hide gates").attr(containerFieldTXT));
            containerMenuSet.toBack();
            containerMenuSet.hide();
        };
        Raphael.fn.getContainerMenuSet = function() {
            var clonedMenuSet = containerMenuSet.clone();
            clonedMenuSet.toBack();
            clonedMenuSet.hide();
            return clonedMenuSet ;
        };

        var nodeMenuSet;
            //nodeMainTitleTXT  = params.node_menuMainTitle,
            //nodeFieldTXT      = params.node_menuFields;
        Raphael.fn.setNodeMenuSet = function() {
            nodeMenuSet = this.set();
            nodeMenuSet.push(this.text(0,10,"Node menu").attr(containerMainTitleTXT));
            nodeMenuSet.push(this.text(0,10,"Display all properties").attr(containerFieldTXT));
            nodeMenuSet.toBack();
            nodeMenuSet.hide();
        };
        Raphael.fn.getNodeMenuSet = function() {
            var clonedMenuSet = nodeMenuSet.clone();
            clonedMenuSet.toBack();
            clonedMenuSet.hide();
            return clonedMenuSet ;
        };

        var endpointMenuSet;
            //endpointMainTitleTXT  = params.node_menuMainTitle,
            //endpointFieldTXT      = params.node_menuFields;

        Raphael.fn.setEndpointMenuSet = function() {
            endpointMenuSet = this.set();
            endpointMenuSet.push(this.text(0,10,"Endpoint menu").attr(containerMainTitleTXT));
            endpointMenuSet.push(this.text(0,10,"Display all properties").attr(containerFieldTXT));
            endpointMenuSet.toBack();
            endpointMenuSet.hide();
        };
        Raphael.fn.getEndpointMenuSet = function() {
            var clonedMenuSet = endpointMenuSet.clone();
            clonedMenuSet.toBack();
            clonedMenuSet.hide();
            return clonedMenuSet ;
        };

        Raphael.fn.FitText = function (rText, containerWidth, compressor, min) {
            var maxFontSize = rText.attr('font-size').split("px")[0],
                minFontSize = parseFloat(-1/0),
                compress    = compressor || 1,
                newFontSize = Math.max(Math.min(containerWidth / (compress*10), maxFontSize), minFontSize) ;
            if (newFontSize<min)
                newFontSize = min + ' px';
            else
                newFontSize = newFontSize + ' px';
            rText.attr({'font-size': newFontSize});
        };

        Raphael.fn.debugPoint = function (x, y, color) {
            if (typeof console != "undefined") {
                var circle = this.circle(x,y);
                circle.attr({fill: color, stroke: color, "fill-opacity": 0, "r": 2,"stroke-width": 2});
            }
        };

        Raphael.fn.rectPath = function (rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad) {
            return Raphael._rectPath(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad);
        };

        Raphael.fn.getHTMLOffsets = function () {
            /*
             * mainCenter div contains the mappyLayout div
             * mappingCanvas div contains the mappyCanvas div
             * => referentials for mouse event positioning offset
             */
            var mainCenterDiv    = document.getElementById("mainCenter"),
                mappingCanvasDiv = document.getElementById("mappingCanvas");

            var divOffsetTop  = ((mappingCanvasDiv!=null) ? mappingCanvasDiv.offsetTop : 0) + ((mainCenterDiv!=null) ? mainCenterDiv.offsetTop : 0),
                divOffsetLeft = ((mappingCanvasDiv!=null) ? mappingCanvasDiv.offsetLeft : 0) + ((mainCenterDiv!=null) ? mainCenterDiv.offsetLeft : 0);

            return {
                top: divOffsetTop,
                left: divOffsetLeft
            }
        };

        Raphael.fn.rectMouseMove = function(rect, e, dbrdResz, isResizing) {
            // X,Y Coordinates relative to shape's orgin
            var htmlOffsets = this.getHTMLOffsets();
            var zpdOffsets = this.getZPDoffsets();
            var relativeX = e.clientX - zpdOffsets.x - htmlOffsets.left - rect.attr('x');
            var relativeY = e.clientY - zpdOffsets.y - htmlOffsets.top - rect.attr('y');
            //helper_.debug("relative mouse positioning : {".concat(relativeX).concat(',').concat(relativeY).concat("}"));

            var shapeWidth = rect.attr('width');
            var shapeHeight = rect.attr('height');

            if (relativeY < dbrdResz) {
                if (relativeX < dbrdResz) {
                    rect.attr('cursor','nw-resize');
                } else if (relativeX > shapeWidth - dbrdResz) {
                    rect.attr('cursor','ne-resize');
                } else {
                    rect.attr('cursor','n-resize');
                }
            } else if (relativeY > shapeHeight - dbrdResz) {
                if (relativeX < dbrdResz) {
                    rect.attr('cursor','sw-resize');
                } else if (relativeX > shapeWidth - dbrdResz) {
                    rect.attr('cursor','se-resize');
                } else {
                    rect.attr('cursor','s-resize');
                }
            } else {
                if (relativeX < dbrdResz) {
                    rect.attr('cursor','w-resize');
                } else if (relativeX > shapeWidth - dbrdResz) {
                    rect.attr('cursor','e-resize');
                } else {
                    if (!isResizing)
                        rect.attr('cursor','default');
                }
            }
        }

        Raphael.fn.link = function (obj1, obj2, line, bg) {

            if (obj1==null)
                return
            else if (obj1.line && obj1.from && obj1.to && typeof obj1.line == "string" ) {
                line = obj1;
                obj1 = line.from;
                obj2 = line.to;
                bg = line.bg;
                line = line.line;
            } else if (obj1.line && obj1.from && obj1.to) {
                line = obj1;
                obj1 = line.from;
                obj2 = line.to;
            }

            var bb1 = obj1.getBBox(),
                bb2 = obj2.getBBox(),
                p = [{x: bb1.x + bb1.width / 2, y: bb1.y - 1},
                    {x: bb1.x + bb1.width / 2, y: bb1.y + bb1.height + 1},
                    {x: bb1.x - 1, y: bb1.y + bb1.height / 2},
                    {x: bb1.x + bb1.width + 1, y: bb1.y + bb1.height / 2},
                    {x: bb2.x + bb2.width / 2, y: bb2.y - 1},
                    {x: bb2.x + bb2.width / 2, y: bb2.y + bb2.height + 1},
                    {x: bb2.x - 1, y: bb2.y + bb2.height / 2},
                    {x: bb2.x + bb2.width + 1, y: bb2.y + bb2.height / 2}],
                d = {}, dis = [];

            for (var i = 0; i < 4; i++) {
                for (var j = 4; j < 8; j++) {
                    var dx = Math.abs(p[i].x - p[j].x),
                        dy = Math.abs(p[i].y - p[j].y);
                    if ((i == j - 4) || (((i != 3 && j != 6) || p[i].x < p[j].x) && ((i != 2 && j != 7) || p[i].x > p[j].x) && ((i != 0 && j != 5) || p[i].y > p[j].y) && ((i != 1 && j != 4) || p[i].y < p[j].y))) {
                        dis.push(dx + dy);
                        d[dis[dis.length - 1]] = [i, j];
                    }
                }
            }

            //helper_.debug("[Raphael.fn.link] dx:" + dx + ", dy:" + dy);

            if (dis.length == 0) {
                var res = [0, 4];
            } else {
                res = d[Math.min.apply(Math, dis)];
            }

            var x1 = p[res[0]].x,
                y1 = p[res[0]].y,
                x4 = p[res[1]].x,
                y4 = p[res[1]].y;

            dx = Math.max(Math.abs(x1 - x4) / 2, 10);
            dy = Math.max(Math.abs(y1 - y4) / 2, 10);

            //helper_.debug("[Raphael.fn.link] dx:" + dx + ", dy:" + dy);

            var x2 = [x1, x1, x1 - dx, x1 + dx][res[0]].toFixed(3),
                y2 = [y1 - dy, y1 + dy, y1, y1][res[0]].toFixed(3),
                x3 = [0, 0, 0, 0, x4, x4, x4 - dx, x4 + dx][res[1]].toFixed(3),
                y3 = [0, 0, 0, 0, y1 + dy, y1 - dy, y4, y4][res[1]].toFixed(3);

            var path = ["M", x1.toFixed(3), y1.toFixed(3), "C", x2, y2, x3, y3, x4.toFixed(3), y4.toFixed(3)].join(",");

            if (line && line.line) {
                line.bg && line.bg.attr({path: path});
                line.line.attr({path: path});
            } else {
                var color = typeof line == "string" ? line : "#000",
                    from = obj1,
                    to  = obj2;
                    line =  this.path(path).attr({stroke: color, fill: "none"});
                    bg = bg && bg.split && this.path(path).attr({stroke: bg.split("|")[0], fill: "none", "stroke-width": bg.split("|")[1] || 3});
                return {
                    from: from,
                    to: to,
                    line: line,
                    bg: bg
                };
            }
        };

        // MOVE WITH SET
        var bussOnMove       = null,
            linksOnMove      = null,
            linksToUp        = null,
            endpointsOnMove  = null,
            nodesOnMove      = null,
            containersOnMove = null,
            lansOnMove       = null,
            areasOnMove      = null,
            dcsOnMove        = null;

        var dragOnLan  = false,
            dragOnArea = false,
            dragOnDC   = false;

        var moveSet = null;

        Raphael.fn.drag = function(object, type) {
            var i, ii, j, jj, area;
            var mtxX, mtxY, mtxS;
            switch (type) {
                case "link":
                    if (linksToUp == null)
                        linksToUp = [];
                    if (linksOnMove == null)
                        linksOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    if (dragOnLan || dragOnArea || dragOnDC) {

                        var sourceLan  = object.getEpSource().epNode.nodeContainer.localisation.lan,
                            sourceArea = object.getEpSource().epNode.nodeContainer.localisation.marea,
                            sourceDC   = object.getEpSource().epNode.nodeContainer.localisation.dcproto.dc;

                        var targetLan, targetArea, targetDC;
                        if (object.getEpTarget()!=null) {
                            targetLan = object.getEpTarget().epNode.nodeContainer.localisation.lan;
                            targetArea = object.getEpTarget().epNode.nodeContainer.localisation.marea;
                            targetDC = object.getEpTarget().epNode.nodeContainer.localisation.dcproto.dc;
                        } else {
                            targetLan = null;
                            targetArea = object.getMulticastBus().areaName;
                            targetDC = object.getMulticastBus().dcName;
                        }

                        var isOnMove = false;

                        if (dragOnDC) {
                            for (i = 0, ii = dcsOnMove.length; i < ii; i++) {
                                var dc = dcsOnMove[i];
                                if (sourceDC === dc.dcName && targetDC === dc.dcName) {
                                    linksOnMove.push(object);
                                    moveSet.push(object.line);
                                    isOnMove=true;
                                }
                            }
                            if (!isOnMove)
                                linksToUp.push(object);
                        } else if (dragOnArea) {
                            for (i = 0, ii = areasOnMove.length; i < ii; i++) {
                                area = areasOnMove[i];
                                if (sourceArea === area.areaName && targetArea === area.areaName) {
                                    linksOnMove.push(object);
                                    moveSet.push(object.line);
                                    isOnMove=true;
                                }
                            }
                            if (!isOnMove)
                                linksToUp.push(object);
                        } else if (dragOnLan) {
                            for (i = 0, ii = lansOnMove.length; i < ii; i++) {
                                var lan = lansOnMove[i];
                                if (sourceLan === lan.lanName && targetLan === lan.lanName) {
                                    moveSet.push(object.line);
                                    linksOnMove.push(object);
                                    isOnMove=true;
                                }
                            }
                            if (!isOnMove)
                                linksToUp.push(object);
                        }
                    } else {
                        linksToUp.push(object);
                    }

                    break;

                case "endpoint":
                    if (endpointsOnMove == null)
                        endpointsOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    endpointsOnMove.push(object);
                    for (i = 0, ii = object.epLinks.length; i < ii; i++)
                        object.r.drag(object.epLinks[i], "link");
                    moveSet.push(object.circle);

                    object.cx = object.circle.attr("cx");
                    object.cy = object.circle.attr("cy");

                    if (!object.menuHided) {
                        object.menu.remove();
                        object.menuSet.remove();
                        object.menuHided=true;
                        if (object.r.getDisplayMainMenu())
                            object.r.setDisplayMainMenu(false);
                    }
                    if (object.labelHided==false) {
                        object.label.hide();
                        object.circle.attr("r",object.rUnselected);
                        object.labelHided=true;
                    }
                    if (object.frameHided==false) {
                        object.frame.hide();
                        object.frameHided = true;
                    }
                    object.isMoving = true;

                    object.circle.animate({"fill-opacity": object.oSelected}, 500);

                    break;


                case "node":
                    if (nodesOnMove == null)
                        nodesOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    nodesOnMove.push(object);
                    for (i = 0, ii = object.nodeEndpoints.length; i < ii; i++)
                        object.nodeEndpoints[i].r.drag(object.nodeEndpoints[i],"endpoint");
                    moveSet.push(object.nodeName);
                    moveSet.push(object.rect);

                    object.extrx  = object.rect.attr("x");
                    object.extry  = object.rect.attr("y");
                    object.extt0x = object.nodeName.attr("x");
                    object.extt0y = object.nodeName.attr("y");

                    if (!object.menuHided) {
                        object.menu.remove();
                        object.menuSet.remove();
                        object.menuHided=true;
                        if (object.r.getDisplayMainMenu())
                            object.r.setDisplayMainMenu(false);
                    }

                    object.isMoving = true;

                    object.rect.animate({"fill-opacity": object.oSelected}, 500);

                    break;


                case "container":
                    if (containersOnMove == null)
                        containersOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    containersOnMove.push(object);
                    mtxX        = object.containerNodes.getMtxSize().x;
                    mtxY        = object.containerNodes.getMtxSize().y;
                    for (i = 0, ii = mtxX; i < ii; i++)
                        for (j = 0, jj = mtxY; j < jj; j++) {
                            var node = object.containerNodes.getNodeFromMtx(i, j);
                            node.r.drag(node,"node");
                        }
                    moveSet.push(object.containerName);
                    moveSet.push(object.rect);

                    object.extrx = object.rect.attr("x");
                    object.extry = object.rect.attr("y");
                    object.extt0x = object.containerName.attr("x");
                    object.extt0y = object.containerName.attr("y");

                    if (!object.menuHided) {
                        object.menu.remove();
                        object.menuSet.remove();
                        object.menuHided=true;
                        if (object.r.getDisplayMainMenu())
                            object.r.setDisplayMainMenu(false);
                    }

                    object.isMoving = true;
                    object.rect.animate({"fill-opacity": object.oSelected}, 500);

                    break;

                case "bus":
                    if (bussOnMove == null)
                        bussOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    bussOnMove.push(object);
                    for (i = object.bindedLinks.length; i--;)
                        object.r.drag(object.bindedLinks[i], "link");
                    moveSet.push(object.bindingPt1);
                    moveSet.push(object.bindingPt2);
                    moveSet.push(object.bindingPt3);
                    moveSet.push(object.bindingPt4);
                    moveSet.push(object.bindingPt5);
                    moveSet.push(object.bindingPt6);

                    object.exttX  = object.cylinder.attr("transform").toString();
                    object.extox1 = object.bindingPt1.attr("cx");
                    object.extoy1 = object.bindingPt1.attr("cy");
                    object.extox2 = object.bindingPt2.attr("cx");
                    object.extoy2 = object.bindingPt2.attr("cy");
                    object.extox3 = object.bindingPt3.attr("cx");
                    object.extoy3 = object.bindingPt3.attr("cy");
                    object.extox4 = object.bindingPt4.attr("cx");
                    object.extoy4 = object.bindingPt4.attr("cy");
                    object.extox5 = object.bindingPt5.attr("cx");
                    object.extoy5 = object.bindingPt5.attr("cy");
                    object.extox6 = object.bindingPt6.attr("cx");
                    object.extoy6 = object.bindingPt6.attr("cy");

                    object.isMoving=true;
                    object.root.isMoving=true;

                    break;

                case "lan":
                    if (!dragOnDC && !dragOnArea)
                        dragOnLan = true;
                    if (lansOnMove == null)
                        lansOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    lansOnMove.push(object);
                    mtxX = object.lanmatrix.getMtxSize().x;
                    mtxY = object.lanmatrix.getMtxSize().y;
                    for (i = 0, ii =  mtxX; i < ii; i++)
                        for (j = 0, jj =  mtxY; j < jj; j++) {
                            var container = object.lanmatrix.getContainerFromMtx(i, j);
                            container.r.drag(container, "container");
                        }
                    moveSet.push(object.lanName);
                    moveSet.push(object.rect);

                    object.extrx  = object.rect.attr("x");
                    object.extry  = object.rect.attr("y");
                    object.extrw  = object.rect.attr("width");
                    object.extrh  = object.rect.attr("height");
                    object.extt0x = object.lanR[0].attr("x");
                    object.extt0y = object.lanR[0].attr("y");
                    object.minTopLeftX = object.minJailX;
                    object.minTopLeftY = object.minJailY;
                    object.maxTopLeftX = object.maxJailX - object.lanwidth;
                    object.maxTopLeftY = object.maxJailY - object.lanheight;

                    object.isMoving = true;

                    if (dragOnLan)
                        object.rect.animate({"fill-opacity": object.oSelected}, 500);

                    break;

                case "area":
                    if (!dragOnDC)
                        dragOnArea = true;

                    if (areasOnMove == null)
                        areasOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    areasOnMove.push(object);
                    mtxX = object.armatrix.getMtxSize().x;
                    mtxY = object.armatrix.getMtxSize().y;
                    for (i = 0, ii =  mtxX; i < ii; i++)
                        for (j = 0, jj =  mtxY; j < jj; j++) {
                            var areaObj = object.armatrix.getObjFromMtx(i,j);
                            var areaObjType = object.armatrix.getObjTypeFromMtx(i,j);
                            if (areaObjType==="LAN") {
                                areaObj.r.drag(areaObj,"lan");
                            } else if (areaObjType==="BUS") {
                                areaObj.r.drag(areaObj.mbus,"bus");
                            }
                        }
                    moveSet.push(object.areaName);
                    moveSet.push(object.rect);

                    object.extrx  = object.rect.attr("x");
                    object.extry  = object.rect.attr("y");
                    object.extrw  = object.rect.attr("width");
                    object.extrh  = object.rect.attr("height");
                    object.extt0x = object.areaName.attr("x");
                    object.extt0y = object.areaName.attr("y");
                    object.minTopLeftX = object.minJailX;
                    object.minTopLeftY = object.minJailY;
                    object.maxTopLeftX = object.maxJailX - object.areawidth;
                    object.maxTopLeftY = object.maxJailY - object.areaheight;

                    object.isMoving = true;

                    if (dragOnArea)
                        object.rect.animate({"fill-opacity": object.oSelected}, 500);

                    break;

                case "dc":
                    dragOnDC = true;

                    if (dcsOnMove == null)
                        dcsOnMove = [];
                    if (moveSet == null)
                        moveSet = this.set();

                    dcsOnMove.push(object);

                    mtxS = object.dcmatrix.getWanMtxSize();
                    var area;
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        area = object.dcmatrix.getAreaFromWanMtx(i);
                        area.r.drag(area, "area");
                    }
                    mtxS = object.dcmatrix.getManMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        area = object.dcmatrix.getAreaFromManMtx(i);
                        area.r.drag(area, "area");
                    }
                    mtxS = object.dcmatrix.getLanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        area = object.dcmatrix.getAreaFromLanMtx(i);
                        area.r.drag(area, "area");
                    }
                    moveSet.push(object.dcName);
                    moveSet.push(object.dcTown);
                    moveSet.push(object.rect);

                    object.extrx = object.rect.attr("x");
                    object.extry = object.rect.attr("y");
                    object.extrw = object.rect.attr("width");
                    object.extrh = object.rect.attr("height");
                    object.extt0x = object.dcName.attr("x");
                    object.extt0y = object.dcName.attr("y");
                    object.extt1x = object.dcTown.attr("x");
                    object.extt1y = object.dcTown.attr("y");

                    object.isMoving = true;
                    object.rect.animate({"fill-opacity": object.oSelected}, 500);
                    object.dcsplitter.hide();

                    break;

                default:
                    break;
            }
        };

        Raphael.fn.move = function(dx, dy) {
            var transform = "t" + dx + "," + dy;
            //helper_.debug(transform);

            var j, jj, i, k, kk, link, toUp;

            moveSet.transform(transform);

            if (dcsOnMove!=null) {
                for (j = 0, jj = dcsOnMove.length; j < jj; j++) {
                    var dc = dcsOnMove[j];
                    dc.mvx = dx; dc.mvy = dy;
                }
            }
            if (areasOnMove!=null) {
                for (j = 0, jj = areasOnMove.length; j < jj; j++) {
                    var area = areasOnMove[j];
                    area.mvx = dx; area.mvy = dy;
                }
            }
            if (lansOnMove!=null) {
                for (j = 0, jj = lansOnMove.length; j < jj; j++) {
                    var lan = lansOnMove[j];
                    lan.mvx = dx; lan.mvy = dy;
                }
            }
            if (bussOnMove!=null) {
                for (j = 0, jj = bussOnMove.length; j < jj; j++) {
                    var bus = bussOnMove[j];
                    bus.mvx=dx; bus.mvy=dy;

                    bus.cylinder.transform(transform+bus.translateForm);
                    bus.titleTxt.transform(transform+bus.translateForm);

                    for (i = bus.bindedLinks.length; i--;) {
                        link = bus.bindedLinks[i];
                        toUp = false;
                        if (linksToUp.length <= linksOnMove.length) {
                            toUp = false;
                            for (k = 0, kk = linksToUp.length; k < kk; k++)
                                if (linksToUp[k].id === link.id) {
                                    toUp = true;
                                    break;
                                }
                        } else {
                            toUp = true;
                            for (k = 0, kk = linksOnMove.length; k < kk; k++)
                                if (linksOnMove[k].id === link.id) {
                                    toUp = false;
                                    break;
                                }
                        }
                        if (toUp) {
                            link.getEpSource().chooseMulticastTargetBindingPointAndCalcPoz(bus.bindedLinks[i]);
                            var up = bus.r.link(link.toCompute());
                            if (typeof up != 'undefined')
                                link.toUpdate(up);
                        }
                    }
                }
            }
            if (containersOnMove!=null) {
                for (j = 0, jj = containersOnMove.length; j < jj; j++) {
                    var container = containersOnMove[j];
                    container.mvx = dx; container.mvy = dy;
                    container.containerHat_.move(this, container.extrx + (container.rectWidth/2) + dx, container.extry + dy);
                }
            }
            if (nodesOnMove!=null) {
                for (j = 0, jj = nodesOnMove.length; j < jj; j++) {
                    var node = nodesOnMove[j];
                    node.mvx = dx; node.mvy = dy;
                }
            }
            if (endpointsOnMove!=null) {
                for (j = 0, jj = endpointsOnMove.length; j < jj; j++) {
                    var endpoint = endpointsOnMove[j];
                    endpoint.lmvx = endpoint.mvx; endpoint.lmvy = endpoint.mvy;
                    endpoint.mvx = dx; endpoint.mvy = dy;
                    if ((endpoint.mvx!=endpoint.lmvx) || (endpoint.mvy!=endpoint.lmvy)) {
                        for (i = endpoint.epLinks.length; i--;) {
                            link = endpoint.epLinks[i];
                            toUp = false;

                            if (linksToUp.length <= linksOnMove.length) {
                                toUp = false;
                                for (k = 0, kk = linksToUp.length; k < kk; k++)
                                    if (linksToUp[k].id === link.id) {
                                        toUp = true;
                                        break;
                                    }
                            } else {
                                toUp = true;
                                for (k = 0, kk = linksOnMove.length; k < kk; k++)
                                    if (linksOnMove[k].id === link.id) {
                                        toUp = false;
                                        break;
                                    }
                            }

                            if (toUp) {
                                if (link.getMulticastBus()!=null) {
                                    endpoint.chooseMulticastTargetBindingPointAndCalcPoz(link);
                                }
                                var up = endpoint.r.link(link.toCompute());
                                if (typeof up != 'undefined') {
                                    //helper_.debug(up);
                                    link.toUpdate(up);
                                }
                            }
                        }
                    }
                }
            }
        };

        Raphael.fn.up = function() {
            var i, ii, j, jj, k, kk;
            var attrect, attrtxt0, attrtxt1;
            if (moveSet!=null) {
                moveSet.transform("");
                moveSet = null;
            }
            if (dcsOnMove!=null) {
                for (i = 0, ii = dcsOnMove.length; i < ii; i++) {
                    var dc = dcsOnMove[i];
                    attrect  = {x: dc.extrx + dc.mvx, y: dc.extry + dc.mvy};
                    attrtxt0 = {x: dc.extt0x + dc.mvx, y: dc.extt0y + dc.mvy};
                    attrtxt1 = {x: dc.extt1x + dc.mvx, y: dc.extt1y + dc.mvy};

                    dc.mvx = 0; dc.mvy = 0;

                    dc.rect.attr(attrect);
                    dc.dcName.attr(attrtxt0);
                    dc.dcTown.attr(attrtxt1);

                    dc.setTopLeftCoord(dc.rect.attr("x"),dc.rect.attr("y"));
                    if (dc.dcmatrix.getWanMtxSize()!=0) {
                        dc.dcsplitter.wanLineTopY = dc.topLeftY+dc.dbrdSpan;
                        dc.dcsplitter.manLineTopY = dc.dcsplitter.wanLineTopY+dc.dcsplitter.wanLineHeight;
                        dc.dcsplitter.lanLineTopY = dc.dcsplitter.manLineTopY+dc.dcsplitter.manLineHeight;
                    } else if (dc.dcmatrix.getManMtxSize()!=0) {
                        dc.dcsplitter.manLineTopY = dc.topLeftY+dc.dbrdSpan;
                        dc.dcsplitter.lanLineTopY = dc.dcsplitter.manLineTopY+dc.dcsplitter.manLineHeight;
                    } else
                        dc.dcsplitter.lanLineTopY = dc.topLeftY+dc.dbrdSpan;
                    dc.dcsplitter.lanLineBdrY = dc.topLeftY+dc.dcheight-dc.dbrdSpan;

                    dc.dcsplitter.move(dc.r);
                    dc.dcsplitter.show();

                    var mtxS = dc.dcmatrix.getWanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        dc.dcmatrix.getAreaFromWanMtx(i).setMoveJail(dc.topLeftX+dc.dbrdSpan,dc.dcsplitter.wanLineTopY,
                            dc.topLeftX+dc.dcwidth-dc.dbrdSpan,dc.dcsplitter.manLineTopY);
                    }

                    mtxS = dc.dcmatrix.getManMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        dc.dcmatrix.getAreaFromManMtx(i).setMoveJail(dc.topLeftX+dc.dbrdSpan,dc.dcsplitter.manLineTopY,
                            dc.topLeftX+dc.dcwidth-dc.dbrdSpan,dc.dcsplitter.lanLineTopY);
                    }

                    mtxS = dc.dcmatrix.getLanMtxSize();
                    for (i = 0, ii =  mtxS; i < ii; i++) {
                        dc.dcmatrix.getAreaFromLanMtx(i).setMoveJail(dc.topLeftX+dc.dbrdSpan,dc.dcsplitter.lanLineTopY,
                            dc.topLeftX+dc.dcwidth-dc.dbrdSpan,dc.dcsplitter.lanLineBdrY);
                    }

                    dc.rect.animate({"fill-opacity": dc.oUnselected}, 500);
                    dc.isMoving = false;
                }
                dcsOnMove = null;
            }
            if (areasOnMove!=null) {
                for (i = 0, ii = areasOnMove.length; i < ii; i++) {
                    var area = areasOnMove[i];
                    attrect = {x: area.extrx + area.mvx, y: area.extry + area.mvy};
                    attrtxt0 = {x: area.extt0x + area.mvx, y: area.extt0y + area.mvy};

                    area.mvx=0; area.mvy=0;
                    area.rect.attr(attrect);
                    area.areaName.attr(attrtxt0);

                    mtxX        = area.armatrix.getMtxSize().x;
                    mtxY        = area.armatrix.getMtxSize().y;
                    area.setTopLeftCoord(area.rect.attr("x"),area.rect.attr("y"));
                    for (j = 0, jj = mtxX; j < jj; j++) {
                        for (k = 0, kk = mtxY; k < kk; k++) {
                            var obj = area.armatrix.getObjFromMtx(j,k);
                            if (obj!=null)
                                obj.setMoveJail(
                                    area.topLeftX+area.abrdSpan,
                                    area.topLeftY + area.abrdSpan,
                                    area.topLeftX+area.areawidth - area.abrdSpan,
                                    area.topLeftY+area.areaheight-area.abrdSpan
                                );
                        }
                    }

                    if (dragOnArea)
                        area.rect.animate({"fill-opacity": area.oUnselected}, 500);
                    area.isMoving = false;
                }
                dragOnArea = false;
                areasOnMove = null;
            }
            if (lansOnMove!=null) {
                for (i = 0, ii = lansOnMove.length; i < ii; i++) {
                    var lan = lansOnMove[i];
                    attrect  = {x: lan.extrx + lan.mvx, y: lan.extry + lan.mvy};
                    attrtxt0 = {x: lan.extt0x + lan.mvx, y: lan.extt0y + lan.mvy};

                    lan.mvx=0; lan.mvy=0;
                    lan.rect.attr(attrect);
                    lan.lanName.attr(attrtxt0);

                    var mtxX = lan.lanmatrix.getMtxSize().x,
                        mtxY = lan.lanmatrix.getMtxSize().y;
                    lan.setTopLeftCoord(lan.rect.attr("x"),lan.rect.attr("y"));
                    for (j = 0, jj = mtxX; j < jj; j++)
                        for (k = 0, kk = mtxY; k < kk; k++) {
                            lan.lanmatrix.getContainerFromMtx(j, k).setMoveJail(
                                lan.topLeftX,
                                lan.topLeftY+lan.lbrdSpan,
                                lan.topLeftX+lan.lanwidth,
                                lan.topLeftY+lan.lanheight
                            );
                        }

                    if (dragOnLan)
                        lan.rect.animate({"fill-opacity": lan.oUnselected}, 500);
                    lan.isMoving = false;
                }
                dragOnLan = false;
                lansOnMove = null;
            }
            if (bussOnMove!=null) {
                for (i = 0, ii = bussOnMove.length; i < ii; i++) {
                    var bus = bussOnMove[i];

                    bus.ctrX += bus.mvx; bus.ctrY+=bus.mvy; bus.x=bus.ctrX - bus.height/ 2; bus.y=bus.ctrY - bus.diameter/ 2;
                    bus.translateForm = bus.exttX+"T"+bus.mvx+","+bus.mvy;
                    bus.bindingPt1X = bus.extox1+bus.mvx; bus.bindingPt1Y = bus.extoy1+bus.mvy;
                    bus.bindingPt2X = bus.extox2+bus.mvx; bus.bindingPt2Y = bus.extoy2+bus.mvy;
                    bus.bindingPt3X = bus.extox3+bus.mvx; bus.bindingPt3Y = bus.extoy3+bus.mvy;
                    bus.bindingPt4X = bus.extox4+bus.mvx; bus.bindingPt4Y = bus.extoy4+bus.mvy;
                    bus.bindingPt5X = bus.extox5+bus.mvx; bus.bindingPt5Y = bus.extoy5+bus.mvy;
                    bus.bindingPt6X = bus.extox6+bus.mvx; bus.bindingPt6Y = bus.extoy6+bus.mvy;

                    bus.mvx=0; bus.mvy=0;
                    bus.cylinder.transform(bus.translateForm);
                    bus.titleTxt.transform(bus.translateForm);
                    bus.bindingPt1.attr({cx:bus.bindingPt1X,cy:bus.bindingPt1Y});
                    bus.bindingPt2.attr({cx:bus.bindingPt2X,cy:bus.bindingPt2Y});
                    bus.bindingPt3.attr({cx:bus.bindingPt3X,cy:bus.bindingPt3Y});
                    bus.bindingPt4.attr({cx:bus.bindingPt4X,cy:bus.bindingPt4Y});
                    bus.bindingPt5.attr({cx:bus.bindingPt5X,cy:bus.bindingPt5Y});
                    bus.bindingPt6.attr({cx:bus.bindingPt6X,cy:bus.bindingPt6Y});

                    bus.root.isMoving=false;
                    bus.isMoving=false;
                }
                bussOnMove = null;
            }
            if (containersOnMove!=null) {
                for (i = 0, ii = containersOnMove.length; i < ii; i++) {
                    var container = containersOnMove[i];
                    attrect  = {x: container.extrx + container.mvx, y: container.extry + container.mvy};
                    attrtxt0 = {x: container.extt0x + container.mvx, y: container.extt0y + container.mvy};

                    container.mvx=0; container.mvy=0;
                    container.rect.attr(attrect);
                    container.containerName.attr(attrtxt0);

                    container.setTopLeftCoord(container.rect.attr("x"),container.rect.attr("y"));
                    container.rect.animate({"fill-opacity": container.oUnselected}, 500);
                    container.isMoving = false;
                }
                containersOnMove = null;
            }
            if (nodesOnMove!=null) {
                for (i = 0, ii = nodesOnMove.length; i < ii; i++) {
                    var node = nodesOnMove[i];
                    attrect  = {x: node.extrx + node.mvx, y: node.extry + node.mvy};
                    attrtxt0 = {x: node.extt0x + node.mvx, y: node.extt0y + node.mvy};

                    node.mvx=0; node.mvy=0;
                    node.rect.attr(attrect);
                    node.nodeName.attr(attrtxt0);

                    node.setPoz(node.nodeName.attr("x")-(node.rectWidth/2), node.nodeName.attr("y")-(node.titleHeight/2));
                    node.rect.animate({"fill-opacity": node.oUnselected}, 500);
                    node.isMoving = false;
                }
                nodesOnMove = null;
            }
            if (endpointsOnMove!=null) {
                for (i = 0, ii = endpointsOnMove.length; i < ii; i++) {
                    var endpoint = endpointsOnMove[i];
                    var att = {cx: endpoint.circle.attr("cx") + endpoint.mvx, cy: endpoint.circle.attr("cy") + endpoint.mvy};
                    endpoint.mvx = 0 ; endpoint.mvy = 0;
                    endpoint.circle.attr(att);
                    endpoint.circle.animate({"fill-opacity": endpoint.oUnselected}, 500);
                    endpoint.x = endpoint.circle.attr("cx");
                    endpoint.y = endpoint.circle.attr("cy");
                    endpoint.isMoving = false;
                }
                endpointsOnMove = null;
            }
            if (linksOnMove!=null) linksOnMove = null;
            if (linksToUp!=null) linksToUp = null;

            dragOnLan  = false, dragOnArea = false, dragOnDC   = false;
        }
    });