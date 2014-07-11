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

        //noinspection JSUnusedLocalSymbols
        var helper_ = new helper();
        var tokenRegex = /\{([^\}]+)\}/g;
        var objNotationRegex = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|")(.+?)\2\])(\(\))?/g; // matches .xxxxx or ["xxxxx"] to run over object properties

        var replacer = function (all, key, obj) {
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
            };
        var fill = function (str, obj) {
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

        Raphael.fn.menu = function (X, Y, sett) {
            var r = 5,
                bb = sett.getBBox(),
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
            //var dx  = 0,
            //    dy  = 0,
            //    out = this.path(fill(shape, mask)).insertBefore(sett);
            //dx = X - (x + r + mask.left + gap);
            //dy = Y - (y + r + h + r + gap);
            //out.translate(dx, dy);
            //return out;
            return this.path(fill(shape, mask)).insertBefore(sett)
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

        Raphael.fn.FitText = function (rText, containerWidth, compressor, min) {
            var maxFontSize = rText.attr('font-size').split("px")[0],
                minFontSize = parseFloat(-1/0),
                compress    = compressor || 1,
                newFontSize = Math.max(Math.min(containerWidth / (compress*10), maxFontSize), minFontSize) ;
            if (newFontSize < min)
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
        };

        //noinspection FunctionWithInconsistentReturnsJS
        Raphael.fn.link = function(obj1, obj2, line, bg) {

            if (obj1==null)
                return null;
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

        var moveSet = null;

        Raphael.fn.moveSetPush = function(object) {
            if (moveSet == null)
                moveSet = this.set();
            moveSet.push(object);
        };

        Raphael.fn.dcsOnMovePush = function(object) {
            if (dcsOnMove == null)
                dcsOnMove = [];
            dcsOnMove.push(object);
        };

        Raphael.fn.areasOnMovePush = function(object) {
            if (areasOnMove == null)
                areasOnMove = [];
            areasOnMove.push(object);
        };

        Raphael.fn.lansOnMovePush = function(object) {
            if (lansOnMove == null)
                lansOnMove = [];
            lansOnMove.push(object);
        };

        Raphael.fn.busOnMovePush = function(object) {
            if (bussOnMove == null)
                bussOnMove = [];
            bussOnMove.push(object);
        };

        Raphael.fn.containersOnMovePush = function(object) {
            if (containersOnMove == null)
                containersOnMove = [];
            containersOnMove.push(object);
        };

        Raphael.fn.nodesOnMovePush = function(object) {
            if (nodesOnMove == null)
                nodesOnMove = [];
            nodesOnMove.push(object);
        };

        Raphael.fn.endpointsOnMovePush = function(object) {
            if (endpointsOnMove == null)
                endpointsOnMove = [];
            endpointsOnMove.push(object);
        };

        Raphael.fn.isLinkToUp = function(link) {
            var k, kk,  toUp;
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
            return toUp;
        };

        Raphael.fn.setLinkToUpOrMove = function(link) {
            var isOnMove = false, i, ii, dc, area, lan;

            if (linksToUp == null)
                linksToUp = [];
            if (linksOnMove == null)
                linksOnMove = [];
            if (moveSet == null)
                moveSet = this.set();

            var sourceLan  = link.getEpSource().epNode.nodeContainer.localisation.lan,
                sourceArea = link.getEpSource().epNode.nodeContainer.localisation.marea,
                sourceDC   = link.getEpSource().epNode.nodeContainer.localisation.dcproto.dc;

            var targetLan, targetArea, targetDC;
            if (link.getEpTarget()!=null) {
                targetLan  = link.getEpTarget().epNode.nodeContainer.localisation.lan;
                targetArea = link.getEpTarget().epNode.nodeContainer.localisation.marea;
                targetDC   = link.getEpTarget().epNode.nodeContainer.localisation.dcproto.dc;
            } else {
                targetLan  = null;
                targetArea = link.getMulticastBus().areaName;
                targetDC   = link.getMulticastBus().dcName;
            }

            if (!isOnMove && dcsOnMove!=null) {
                for (i = 0, ii = dcsOnMove.length; i < ii; i++) {
                    dc = dcsOnMove[i];
                    if (sourceDC === dc.dcName && targetDC === dc.dcName) {
                        linksOnMove.push(link);
                        moveSet.push(link.line);
                        isOnMove=true;
                        break;
                    }
                }
            }
            if (!isOnMove && areasOnMove!=null) {
                for (i = 0, ii = areasOnMove.length; i < ii; i++) {
                    area = areasOnMove[i];
                    if (sourceArea === area.areaName && targetArea === area.areaName) {
                        linksOnMove.push(link);
                        moveSet.push(link.line);
                        isOnMove=true;
                        break;
                    }
                }
            }
            if (!isOnMove && lansOnMove!=null) {
                for (i = 0, ii = lansOnMove.length; i < ii; i++) {
                    lan = lansOnMove[i];
                    if (sourceLan === lan.lanName && targetLan === lan.lanName) {
                        moveSet.push(link.line);
                        linksOnMove.push(link);
                        isOnMove=true;
                        break;
                    }
                }
            }
            if (!isOnMove)
                linksToUp.push(link);

        };

        Raphael.fn.move = function(dx, dy) {
            var transform = "t" + dx + "," + dy;
            //helper_.debug(transform);
            var j, jj;

            moveSet.transform(transform);

            if (dcsOnMove!=null)
                for (j = 0, jj = dcsOnMove.length; j < jj; j++)
                    dcsOnMove[j].moveAction(dx, dy);
            if (areasOnMove!=null)
                for (j = 0, jj = areasOnMove.length; j < jj; j++)
                    areasOnMove[j].moveAction(dx,dy);
            if (lansOnMove!=null)
                for (j = 0, jj = lansOnMove.length; j < jj; j++)
                    lansOnMove[j].moveAction(dx,dy);
            if (bussOnMove!=null)
                for (j = 0, jj = bussOnMove.length; j < jj; j++)
                    bussOnMove[j].moveAction(dx,dy);
            if (containersOnMove!=null)
                for (j = 0, jj = containersOnMove.length; j < jj; j++)
                    containersOnMove[j].moveAction(dx,dy);
            if (nodesOnMove!=null)
                for (j = 0, jj = nodesOnMove.length; j < jj; j++)
                    nodesOnMove[j].moveAction(dx,dy);
            if (endpointsOnMove!=null)
                for (j = 0, jj = endpointsOnMove.length; j < jj; j++)
                    endpointsOnMove[j].moveAction(dx,dy);
        };

        Raphael.fn.up = function() {
            var i, ii;
            if (moveSet!=null) {
                moveSet.transform("");
                moveSet = null;
            }
            if (dcsOnMove!=null) {
                for (i = 0, ii = dcsOnMove.length; i < ii; i++)
                    dcsOnMove[i].moveUp();
                dcsOnMove = null;
            }
            if (areasOnMove!=null) {
                for (i = 0, ii = areasOnMove.length; i < ii; i++)
                    areasOnMove[i].moveUp();
                areasOnMove = null;
            }
            if (lansOnMove!=null) {
                for (i = 0, ii = lansOnMove.length; i < ii; i++)
                    lansOnMove[i].moveUp();
                lansOnMove = null;
            }
            if (bussOnMove!=null) {
                for (i = 0, ii = bussOnMove.length; i < ii; i++)
                    bussOnMove[i].moveUp();
                bussOnMove = null;
            }
            if (containersOnMove!=null) {
                for (i = 0, ii = containersOnMove.length; i < ii; i++)
                    containersOnMove[i].moveUp();
                containersOnMove = null;
            }
            if (nodesOnMove!=null) {
                for (i = 0, ii = nodesOnMove.length; i < ii; i++)
                    nodesOnMove[i].moveUp();
                nodesOnMove = null;
            }
            if (endpointsOnMove!=null) {
                for (i = 0, ii = endpointsOnMove.length; i < ii; i++)
                    endpointsOnMove[i].moveUp();
                endpointsOnMove = null;
            }
            if (linksOnMove!=null) linksOnMove = null;
            if (linksToUp!=null) linksToUp = null;
        };


        // OBJECTS RESIZING

        var onmove = function(dx,dy) {
                if (this.scaleDir[0] == 0) dx = 0;
                if (this.scaleDir[1] == 0) dy = 0;

                if (dx != 0 || dy != 0) {
                    var i, ii;

                    //helper_.debug("dx : " + dx + "; dy : " + dy);
                    this.object.bboxLine.remove();
                    for (i = 0, ii = this.object.scaleHandles.length; i < ii; i++)
                        if (i != this.idx)
                            this.object.scaleHandles[i].element.remove();

                    switch(this.idx) {
                        case 0:
                            if (this.object.bbxCorners[0].x + dx >= this.object.bbxCorners[1].x)
                                dx = this.object.bbxCorners[1].x - this.object.bbxCorners[0].x;
                            if (this.object.bbxCorners[0].y + dy >= this.object.bbxCorners[3].y)
                                dy = this.object.bbxCorners[3].y - this.object.bbxCorners[0].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.bbxCorners[0].x + dx >= this.minBBox.x)
                                    dx = this.minBBox.x - this.object.bbxCorners[0].x;
                                if (this.object.bbxCorners[0].y + dy >= this.minBBox.y)
                                    dy = this.minBBox.y - this.object.bbxCorners[0].y;
                            }

                            this.ibbxCorners[0].x = this.object.bbxCorners[0].x+dx;
                            this.ibbxCorners[0].y = this.object.bbxCorners[0].y+dy;
                            this.ibbxCorners[1].y = this.object.bbxCorners[1].y+dy;
                            this.ibbxCorners[3].x = this.object.bbxCorners[3].x+dx;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[0].y = this.object.mdlPoints[0].y+dy;
                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;
                            this.imdlPoints[3].x = this.object.mdlPoints[3].x+dx;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 1:
                            if (this.object.bbxCorners[1].x + dx <= this.object.bbxCorners[0].x)
                                dx = this.object.bbxCorners[0].x - this.object.bbxCorners[1].x;
                            if (this.object.bbxCorners[1].y + dy >= this.object.bbxCorners[3].y)
                                dy = this.object.bbxCorners[3].y - this.object.bbxCorners[1].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.bbxCorners[1].x + dx <= this.minBBox.x2)
                                    dx = this.minBBox.x2 - this.object.bbxCorners[1].x;
                                if (this.object.bbxCorners[1].y + dy >= this.minBBox.y)
                                    dy = this.minBBox.y - this.object.bbxCorners[1].y;
                            }

                            this.ibbxCorners[0].y = this.object.bbxCorners[0].y+dy;
                            this.ibbxCorners[1].x = this.object.bbxCorners[1].x+dx;
                            this.ibbxCorners[1].y = this.object.bbxCorners[1].y+dy;
                            this.ibbxCorners[2].x = this.object.bbxCorners[2].x+dx;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[0].y = this.object.mdlPoints[0].y+dy;
                            this.imdlPoints[1].x = this.object.mdlPoints[1].x+dx;
                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 2:
                            if (this.object.bbxCorners[2].x + dx <= this.object.bbxCorners[0].x)
                                dx = this.object.bbxCorners[0].x -this.object.bbxCorners[2].x;
                            if (this.object.bbxCorners[2].y + dy <= this.object.bbxCorners[0].y)
                                dy = this.object.bbxCorners[0].y - this.object.bbxCorners[2].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.bbxCorners[2].x + dx <= this.minBBox.x2)
                                    dx = this.minBBox.x2 - this.object.bbxCorners[2].x;
                                if (this.object.bbxCorners[2].y + dy <= this.minBBox.y2)
                                    dy = this.minBBox.y2 - this.object.bbxCorners[2].y;
                            }

                            this.ibbxCorners[1].x = this.object.bbxCorners[1].x+dx;
                            this.ibbxCorners[2].x = this.object.bbxCorners[2].x+dx;
                            this.ibbxCorners[2].y = this.object.bbxCorners[2].y+dy;
                            this.ibbxCorners[3].y = this.object.bbxCorners[3].y+dy;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[1].x = this.object.mdlPoints[1].x+dx;
                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;
                            this.imdlPoints[2].y = this.object.mdlPoints[2].y+dy;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 3:
                            if (this.object.bbxCorners[3].x + dx >= this.object.bbxCorners[1].x)
                                dx = this.object.bbxCorners[1].x - this.object.bbxCorners[3].x;
                            if (this.object.bbxCorners[3].y + dy <= this.object.bbxCorners[0].y)
                                dy = this.object.bbxCorners[0].y - this.object.bbxCorners[3].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.bbxCorners[3].x + dx >= this.minBBox.x)
                                    dx = this.minBBox.x - this.object.bbxCorners[3].x;
                                if (this.object.bbxCorners[3].y + dy <= this.minBBox.y2)
                                    dy = this.minBBox.y2 - this.object.bbxCorners[3].y;
                            }

                            this.ibbxCorners[0].x = this.object.bbxCorners[0].x+dx;
                            this.ibbxCorners[2].y = this.object.bbxCorners[2].y+dy;
                            this.ibbxCorners[3].x = this.object.bbxCorners[3].x+dx;
                            this.ibbxCorners[3].y = this.object.bbxCorners[3].y+dy;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;
                            this.imdlPoints[2].y = this.object.mdlPoints[2].y+dy;
                            this.imdlPoints[3].x = this.object.mdlPoints[3].x+dx;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 4:
                            if (this.object.mdlPoints[0].y + dy >= this.object.mdlPoints[2].y)
                                dy = this.object.mdlPoints[2].y - this.object.mdlPoints[0].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.mdlPoints[0].y + dy >= this.minBBox.y)
                                    dy = this.minBBox.y - this.object.mdlPoints[0].y
                            }

                            this.ibbxCorners[0].y = this.object.bbxCorners[0].y+dy;
                            this.ibbxCorners[1].y = this.object.bbxCorners[1].y+dy;

                            this.imdlPoints[0].y = this.object.mdlPoints[0].y+dy;
                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 5:
                            if (this.object.mdlPoints[1].x + dx <= this.object.mdlPoints[3].x)
                                dx = this.object.mdlPoints[3].x - this.object.mdlPoints[1].x;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.mdlPoints[1].x + dx <= this.minBBox.x2)
                                    dx = this.minBBox.x2 - this.object.mdlPoints[1].x
                            }

                            this.ibbxCorners[1].x = this.object.bbxCorners[1].x+dx;
                            this.ibbxCorners[2].x = this.object.bbxCorners[2].x+dx;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[1].x = this.object.mdlPoints[1].x+dx;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;

                            break;

                        case 6:
                            if (this.object.mdlPoints[2].y + dy <= this.object.mdlPoints[0].y)
                                dy = this.object.mdlPoints[0].y - this.object.mdlPoints[2].y;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.mdlPoints[2].y + dy <= this.minBBox.y2)
                                    dy = this.minBBox.y2 - this.object.mdlPoints[2].y
                            }

                            this.ibbxCorners[2].y = this.object.bbxCorners[2].y+dy;
                            this.ibbxCorners[3].y = this.object.bbxCorners[3].y+dy;

                            this.imdlPoints[1].y = this.object.mdlPoints[1].y+dy/2;
                            this.imdlPoints[2].y = this.object.mdlPoints[2].y+dy;
                            this.imdlPoints[3].y = this.object.mdlPoints[3].y+dy/2;

                            break;

                        case 7:
                            if (this.object.mdlPoints[3].x + dx >= this.object.mdlPoints[1].x)
                                dx = this.object.mdlPoints[1].x - this.object.mdlPoints[3].x;
                            if (this.maxBBox!=null) {

                            }
                            if (this.minBBox!=null) {
                                if (this.object.mdlPoints[3].x + dx >= this.minBBox.x)
                                    dx = this.minBBox.x - this.object.mdlPoints[3].x
                            }

                            this.ibbxCorners[0].x = this.object.bbxCorners[0].x+dx;
                            this.ibbxCorners[3].x = this.object.bbxCorners[3].x+dx;

                            this.imdlPoints[0].x = this.object.mdlPoints[0].x+dx/2;
                            this.imdlPoints[2].x = this.object.mdlPoints[2].x+dx/2;
                            this.imdlPoints[3].x = this.object.mdlPoints[3].x+dx;
                            break;

                        default:
                            break;
                    }

                    var attrect = {x: this.ix + dx, y: this.iy + dy};

                    this.attr(attrect);
                    this.object.bboxLine = this.object.r.path()
                        .attr({path: [
                                        [ 'M', this.ibbxCorners[0].x, this.ibbxCorners[0].y ],
                                        [ 'L', this.ibbxCorners[1].x, this.ibbxCorners[1].y ],
                                        [ 'L', this.ibbxCorners[2].x, this.ibbxCorners[2].y ],
                                        [ 'L', this.ibbxCorners[3].x, this.ibbxCorners[3].y ],
                                        [ 'L', this.ibbxCorners[0].x, this.ibbxCorners[0].y ]
                                     ],
                                     'stroke-width': 3,
                                     'stroke-dasharray': '- ',
                                     opacity: .5
                        });

                    var handle;
                    for (i = 0, ii = this.ibbxCorners.length; i < ii; i++) {
                        if (this.idx != i) {
                            handle = this.object.scaleHandles[i];
                            handle.element = this.object.r.rect(this.ibbxCorners[i].x-5, this.ibbxCorners[i].y-5, 10, 10).attr({fill: "#000"});
                            handle.element.drag(
                                onmove,
                                onmovestart,
                                onmoveend
                            );
                            handle.element.object = this.object;
                            handle.element.scaleDir = handle.scaleDir;
                            handle.element.idx = i;
                            handle.element.minBBox = this.minBBox;
                            handle.element.maxBBox = this.maxBBox;
                        }
                    }

                    for (i = 0, ii = this.imdlPoints.length; i < ii; i++) {
                        if (this.idx != i+4) {
                            handle = this.object.scaleHandles[i+4];
                            handle.element = this.object.r.rect(this.imdlPoints[i].x-5, this.imdlPoints[i].y-5, 10, 10).attr({fill: "#000"});
                            handle.element.drag(
                                onmove,
                                onmovestart,
                                onmoveend
                            );
                            handle.element.object = this.object;
                            handle.element.scaleDir = handle.scaleDir;
                            handle.element.idx = i+4;
                            handle.element.minBBox = this.minBBox;
                            handle.element.maxBBox = this.maxBBox;
                        }
                    }
                }

                this.object.editAction(this, dx, dy);

                this.object.bboxLine.toFront();
                for (i = 0, ii = this.object.scaleHandles.length; i < ii; i++)
                    this.object.scaleHandles[i].element.toFront();
            },
            onmovestart = function() {
                if (this.scaleDir[0] > 0 && this.scaleDir[1] > 0)
                    this.attr('cursor','se-resize');
                if (this.scaleDir[0] == 0 && this.scaleDir[1] > 0)
                    this.attr('cursor','s-resize');
                if (this.scaleDir[0] < 0 && this.scaleDir[1] > 0)
                    this.attr('cursor','sw-resize');
                if (this.scaleDir[0] > 0 && this.scaleDir[1] < 0)
                    this.attr('cursor','ne-resize');
                if (this.scaleDir[0] == 0 && this.scaleDir[1] < 0)
                    this.attr('cursor','n-resize');
                if (this.scaleDir[0] < 0 && this.scaleDir[1] < 0)
                    this.attr('cursor','nw-resize');
                if (this.scaleDir[0] > 0 && this.scaleDir[1] == 0)
                    this.attr('cursor','e-resize');
                if (this.scaleDir[0] < 0 && this.scaleDir[1] == 0)
                    this.attr('cursor','w-resize');

                this.ix = this.attr("x"); this.iy = this.attr("y");
                this.ibbxCorners = [
                                            {x:this.object.bbxCorners[0].x, y:this.object.bbxCorners[0].y},
                                            {x:this.object.bbxCorners[1].x, y:this.object.bbxCorners[1].y},
                                            {x:this.object.bbxCorners[2].x, y:this.object.bbxCorners[2].y},
                                            {x:this.object.bbxCorners[3].x, y:this.object.bbxCorners[3].y}
                                   ];
                this.imdlPoints  = [
                                            {x:this.object.mdlPoints[0].x, y:this.object.mdlPoints[0].y},
                                            {x:this.object.mdlPoints[1].x, y:this.object.mdlPoints[1].y},
                                            {x:this.object.mdlPoints[2].x, y:this.object.mdlPoints[2].y},
                                            {x:this.object.mdlPoints[3].x, y:this.object.mdlPoints[3].y}
                                   ];
                this.minBBox = this.object.getMinBBox();
                this.maxBBox = this.object.getMaxBBox();
                this.object.editInit();
            },
            onmoveend = function() {
                this.attr('cursor','default');
                this.object.bbxCorners = [
                    {x:this.ibbxCorners[0].x, y:this.ibbxCorners[0].y},
                    {x:this.ibbxCorners[1].x, y:this.ibbxCorners[1].y},
                    {x:this.ibbxCorners[2].x, y:this.ibbxCorners[2].y},
                    {x:this.ibbxCorners[3].x, y:this.ibbxCorners[3].y}
                ];
                this.object.mdlPoints = [
                    {x:this.imdlPoints[0].x, y:this.imdlPoints[0].y},
                    {x:this.imdlPoints[1].x, y:this.imdlPoints[1].y},
                    {x:this.imdlPoints[2].x, y:this.imdlPoints[2].y},
                    {x:this.imdlPoints[3].x, y:this.imdlPoints[3].y}
                ];
                this.object.editUp();
                this.object.bboxLine.toFront();
                for (var i = 0, ii = this.object.scaleHandles.length; i < ii; i++)
                    this.object.scaleHandles[i].element.toFront();
            };

        Raphael.fn.scaleInit = function(object) {
            var obbox      = object.getBBox(),
                bbxCorners = [
                                {x:obbox.x, y:obbox.y},
                                {x:obbox.x + obbox.width, y:obbox.y},
                                {x:obbox.x2, y:obbox.y2},
                                {x:obbox.x2 - obbox.width, y:obbox.y2}
                ],
                mdlPoints  = [
                                {x:obbox.x + obbox.width/2,y:obbox.y},
                                {x:obbox.x2, y:obbox.y+obbox.height/2},
                                {x:obbox.x + obbox.width/2,y:obbox.y2},
                                {x:obbox.x, y:obbox.y + obbox.height/2}
                ],
                scaleDir   = [
                    [-1,-1],[1,-1],[1,1],[-1,1],
                    [0,-1],[1,0],[0,1],[-1,0]
                ],
                i, ii;

            object.bbxCorners = bbxCorners;
            object.mdlPoints  = mdlPoints;
            object.bboxLine = this.path()
                .attr({path: [
                        [ 'M', bbxCorners[0].x, bbxCorners[0].y ],
                        [ 'L', bbxCorners[1].x, bbxCorners[1].y ],
                        [ 'L', bbxCorners[2].x, bbxCorners[2].y ],
                        [ 'L', bbxCorners[3].x, bbxCorners[3].y ],
                        [ 'L', bbxCorners[0].x, bbxCorners[0].y ]
                    ],
                    'stroke-width': 3,
                    'stroke-dasharray': '- ',
                    opacity: .5
                });

            object.scaleHandles = [];
            var handle;
            for (i = 0, ii = bbxCorners.length; i < ii; i++) {
                handle = {};
                handle.scaleDir = scaleDir[i];
                handle.element = this.rect(bbxCorners[i].x-5, bbxCorners[i].y-5, 10, 10).attr({fill: "#000"});
                handle.element.drag(
                    onmove,
                    onmovestart,
                    onmoveend
                );
                handle.element.object = object;
                handle.element.scaleDir = scaleDir[i];
                handle.element.idx = i;
                handle.element.minBBox = object.getMinBBox();
                handle.element.maxBBox = object.getMaxBBox();
                object.scaleHandles[i] = handle;
            }

            for (i = 0, ii = mdlPoints.length; i < ii; i++) {
                handle = {};
                handle.scaleDir = scaleDir[i+4];
                handle.element = this.rect(mdlPoints[i].x-5, mdlPoints[i].y-5, 10, 10).attr({fill: "#000"});
                handle.element.drag(
                    onmove,
                    onmovestart,
                    onmoveend
                );
                handle.element.object = object;
                handle.element.scaleDir = scaleDir[i+4];
                handle.element.idx = i+4;
                handle.element.minBBox = object.getMinBBox();
                handle.element.maxBBox = object.getMaxBBox();
                object.scaleHandles[i+4] = handle;
            }

        };

        Raphael.fn.scaleDone = function(object) {
            var i, ii;
            object.bboxLine.remove();
            for (i = 0, ii = object.scaleHandles.length; i < ii; i++)
                object.scaleHandles[i].element.remove();
        };
    });