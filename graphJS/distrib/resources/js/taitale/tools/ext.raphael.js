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

        Raphael.fn.popup = function (X, Y, set, pos, ret) {
            pos = String(pos || "top-middle").split("-");
            pos[1] = pos[1] || "middle";
            var r = 5,
                bb = set.getBBox(),
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
                offset = {
                    hx0: X - (x + r + w - gap * 2),
                    hx1: X - (x + r + w / 2 - gap),
                    hx2: X - (x + r + gap),
                    vhy: Y - (y + r + h + r + gap),
                    "^hy": Y - (y - gap)
                },
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
                out = this.path(fill(shapes[pos[0]], mask)).insertBefore(set);
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
            set.translate(dx, dy);
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
                offset = {
                    hx0: X - (x + r + w - gap * 2),
                    hx1: X - (x + r + w / 2 - gap),
                    hx2: X - (x + r + gap),
                    vhy: Y - (y + r + h + r + gap),
                    "^hy": Y - (y - gap)
                },
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
            var dx  = 0,
                dy  = 0,
                out = this.path(fill(shape, mask)).insertBefore(set);
            dx = X - (x + r + mask.left + gap);
            dy = Y - (y + r + h + r + gap);
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
            mainMenuSet.push(this.text(0,0+10,"Taitale menu").attr(menuMainTitleTXT));
            mainMenuSet.push(this.text(0,0+30,"submenu1").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,0+45,"submenu2").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,0+60,"submenu3").attr(menuFieldTXT));
            mainMenuSet.push(this.text(0,0+75,"submenu4").attr(menuFieldTXT));
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
            containerMenuSet.push(this.text(0,0+10,"Container menu").attr(containerMainTitleTXT));
            containerMenuSet.push(this.text(0,0+10,"Display all properties").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,0+30,"Highlight cluster").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,0+45,"Show gates").attr(containerFieldTXT));
            containerMenuSet.push(this.text(0,0+60,"Hide gates").attr(containerFieldTXT));
            containerMenuSet.toBack();
            containerMenuSet.hide();
        };
        Raphael.fn.getContainerMenuSet = function() {
            var clonedMenuSet = containerMenuSet.clone();
            clonedMenuSet.toBack();
            clonedMenuSet.hide();
            return clonedMenuSet ;
        };

        var nodeMenuSet,
            nodeMainTitleTXT  = params.node_menuMainTitle,
            nodeFieldTXT      = params.node_menuFields;
        Raphael.fn.setNodeMenuSet = function() {
            nodeMenuSet = this.set();
            nodeMenuSet.push(this.text(0,0+10,"Node menu").attr(containerMainTitleTXT));
            nodeMenuSet.push(this.text(0,0+10,"Display all properties").attr(containerFieldTXT));
            nodeMenuSet.toBack();
            nodeMenuSet.hide();
        };
        Raphael.fn.getNodeMenuSet = function() {
            var clonedMenuSet = nodeMenuSet.clone();
            clonedMenuSet.toBack();
            clonedMenuSet.hide();
            return clonedMenuSet ;
        };

        var endpointMenuSet,
            endpointMainTitleTXT  = params.node_menuMainTitle,
            endpointFieldTXT      = params.node_menuFields;
        Raphael.fn.setEndpointMenuSet = function() {
            endpointMenuSet = this.set();
            endpointMenuSet.push(this.text(0,0+10,"Endpoint menu").attr(containerMainTitleTXT));
            endpointMenuSet.push(this.text(0,0+10,"Display all properties").attr(containerFieldTXT));
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
                circle = this.circle(x,y);
                circle.attr({fill: color, stroke: color, "fill-opacity": 0, "r": 2,"stroke-width": 2});
            }
        };

        Raphael.fn.rectPath = function (rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad) {
            return Raphael._rectPath(rectTopLeftX, rectTopLeftY, rectWidth, rectHeight, cornerRad);
        };

        Raphael.fn.getHTMLOffsets = function () {
            /*
             * mainCenter div contains the mappyLayout div
             * mappyLayout div contains the mappyCanvas div
             * => referentials for mouse event positioning offset
             */
            var mappyLayoutDiv       = document.getElementById("mainCenter"),
                mainCenterDiv        = document.getElementById("mappyLayout");

            var divOffsetTop  = ((mappyLayoutDiv!=null) ? mappyLayoutDiv.offsetTop : 0) + ((mainCenterDiv!=null) ? mainCenterDiv.offsetTop : 0),
                divOffsetLeft = ((mappyLayoutDiv!=null) ? mappyLayoutDiv.offsetLeft : 0) + ((mainCenterDiv!=null) ? mainCenterDiv.offsetLeft : 0);

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
            helper_.debug("relative mouse positioning : {".concat(relativeX).concat(',').concat(relativeY).concat("}"));

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

            helper_.debug("[Raphael.fn.link] dx:" + dx + ", dy:" + dy);

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

            helper_.debug("[Raphael.fn.link] dx:" + dx + ", dy:" + dy);

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
                    to  = obj2,
                    line =  this.path(path).attr({stroke: color, fill: "none", }),
                    bg = bg && bg.split && this.path(path).attr({stroke: bg.split("|")[0], fill: "none", "stroke-width": bg.split("|")[1] || 3});
                return {
                    from: from,
                    to: to,
                    line: line,
                    bg: bg
                };
            }
        };
    });