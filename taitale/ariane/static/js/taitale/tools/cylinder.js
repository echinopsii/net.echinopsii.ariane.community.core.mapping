// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library                                                 │ \\
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
        'raphael.free_transform'
    ],
    function (Raphael,helper) {
        function cylinder(parent,centerX,centerY,d,h,title,color_) {
            this.r         = null;
            this.ctrX      = centerX;
            this.ctrY      = centerY;
            this.diameter  = d;
            this.height    = h;
            this.x         = this.ctrX - h/ 2;
            this.y         = this.ctrY - d/ 2;
            this.title_    = title;
            this.color     = color_;
            this.vcpath    =
                [
                    ["M", this.x, this.y],
                    ["C", this.x+this.diameter/8, this.y, this.x+this.diameter/5, this.y-this.diameter/4, this.x+this.diameter/5, this.y-this.diameter/2],
                    ["C", this.x+this.diameter/5, this.y-3*this.diameter/4, this.x+this.diameter/8, this.y-this.diameter, this.x, this.y-this.diameter],
                    ["C", this.x-this.diameter/8, this.y-this.diameter, this.x-this.diameter/5, this.y-3*this.diameter/4, this.x-this.diameter/5, this.y-this.diameter/2],
                    ["C", this.x-this.diameter/5, this.y-this.diameter/5, this.x-this.diameter/8, this.y, this.x, this.y],
                    ["Z"],
                    ["M", this.x, this.y],
                    ["L", this.x+this.height, this.y],
                    ["C", this.x+this.height+this.diameter/8, this.y, this.x+this.height+this.diameter/5, this.y-this.diameter/4, this.x+this.height+this.diameter/5, this.y-this.diameter/2],
                    ["C", this.x+this.height+this.diameter/5, this.y-3*this.diameter/4, this.x+this.height+this.diameter/8, this.y-this.diameter, this.x+this.height, this.y-this.diameter],
                    ["L", this.x, this.y-this.diameter]
                ];
            this.translateForm="";
            this.helper_ = new helper();

            this.cylinder   = null;
            this.titleTxt   = null;
            this.cylinderR  = null;
            this.boundary   = null;

            this.bindedLinks = [];

            this.bindingPt1 = null;
            this.bindingPt2 = null;
            this.bindingPt3 = null;
            this.bindingPt4 = null;
            this.bindingPt5 = null;
            this.bindingPt6 = null;

            this.bindingPt1X = this.x;
            this.bindingPt1Y = this.y;
            this.bindingPt2X = this.x+this.height;
            this.bindingPt2Y = this.y;
            this.bindingPt3X = this.x+this.height;
            this.bindingPt3Y = this.y-d;
            this.bindingPt4X = this.x;
            this.bindingPt4Y = this.y-d;
            this.bindingPt5X = this.x+this.height/2;
            this.bindingPt5Y = this.y;
            this.bindingPt6X = this.x+this.height/2;
            this.bindingPt6Y = this.y-d;

            this.root          = parent;
            this.root.isMoving = false;
            this.isMoving      = false;

            this.root.isEditing = false;

            this.mvx = 0;
            this.mvy = 0;

            this.lmvx = 0;
            this.lmvy = 0;

            this.isJailed = false;

            var cylinderRef = this;

            var cyDragger = function() {
                    if (!cylinderRef.root.rightClick)
                        cylinderRef.moveInit();
                },
                cyMove = function(dx,dy) {
                    if (!cylinderRef.root.rightClick) {
                        if (cylinderRef.isJailed) {
                            if (cylinderRef.extox1+dx<cylinderRef.boundary.minX)
                                dx=cylinderRef.boundary.minX-cylinderRef.extox1;
                            else if (cylinderRef.extox3+dx>cylinderRef.boundary.maxX)
                                dx=cylinderRef.boundary.maxX-cylinderRef.extox3;
                            if (cylinderRef.extoy1+dy>cylinderRef.boundary.maxY)
                                dy=cylinderRef.boundary.maxY-cylinderRef.extoy1;
                            else if (cylinderRef.extoy3+dy<cylinderRef.boundary.minY)
                                dy=cylinderRef.boundary.minY-cylinderRef.extoy3;
                        }
                        cylinderRef.r.move(dx,dy);
                    }
                },
                cyUP = function() {
                    if (!cylinderRef.root.rightClick)
                        cylinderRef.r.up();
                };

            var mouseDown = function(e) {
                    if (e.which == 3) {
                        if (cylinderRef.root.menuHided) {
                            cylinderRef.root.menuSet.mousedown(menuMouseDown);
                            var fieldRect, fieldRectWidth, fieldRectHeight;
                            for (var i = 0, ii = cylinderRef.root.menuSet.length ; i < ii ; i++) {
                                if (i==0)
                                    cylinderRef.root.menuSet[i].attr({"x": cylinderRef.bindingPt6X, "y": cylinderRef.bindingPt6Y +10, fill: "#fff"});
                                else if (i==1) {
                                    fieldRect = cylinderRef.root.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": cylinderRef.bindingPt6X - fieldRectWidth/2, "y": cylinderRef.bindingPt6Y+30 - fieldRectHeight/2});
                                    cylinderRef.root.menuSet[i+1].attr({"x": cylinderRef.bindingPt6X, "y": cylinderRef.bindingPt6Y+30});
                                    if (cylinderRef.root.isEditing) {
                                        cylinderRef.root.menuSet[i+1].attr({text: cylinderRef.root.menuFieldStopEditTitle});
                                    } else {
                                        cylinderRef.root.menuSet[i+1].attr({text: cylinderRef.root.menuFieldStartEditTitle});
                                    }
                                    i++;
                                }
                                else {
                                    fieldRect = cylinderRef.root.menuSet[i];
                                    fieldRectWidth = fieldRect.attr("width");
                                    fieldRectHeight = fieldRect.attr("height");
                                    fieldRect.attr({"x": cylinderRef.bindingPt6X, "y": cylinderRef.bindingPt6Y+30+(i-1)*15});
                                    cylinderRef.root.menuSet[i+1].attr({"x": cylinderRef.bindingPt6X, "y": cylinderRef.bindingPt6Y+30+(i-1)*15});
                                    i++;
                                }
                            }
                            if (cylinderRef.root.menu != null)
                                cylinderRef.root.menu.remove();
                            cylinderRef.root.menu = cylinderRef.root.r.menu(cylinderRef.bindingPt6X,cylinderRef.bindingPt6Y+10,cylinderRef.root.menuSet).
                                attr(
                                    {
                                        fill: cylinderRef.root.menuFillColor,
                                        stroke: cylinderRef.root.color,
                                        "stroke-width": cylinderRef.root.menuStrokeWidth,
                                        "fill-opacity": cylinderRef.root.menuOpacity
                                    });
                            cylinderRef.root.menu.mousedown(menuMouseDown);
                            cylinderRef.root.menu.toFront();
                            cylinderRef.root.menuSet.toFront();
                            cylinderRef.root.menuSet.show();
                            cylinderRef.root.menuHided=false;
                        } else {
                            cylinderRef.root.menu.toBack();
                            cylinderRef.root.menuSet.toBack();
                            cylinderRef.root.menu.hide();
                            cylinderRef.root.menuSet.hide();
                            cylinderRef.root.menuHided=true;
                        }
                        cylinderRef.root.rightClick=true;
                        if (cylinderRef.root.r.getDisplayMainMenu())
                            cylinderRef.root.r.setDisplayMainMenu(false);
                    } else if (e.which == 1) {
                        cylinderRef.root.rightClick=false;
                    }
                },
                menuMouseDown = function(e) {
                    if (e.which == 3) {
                        cylinderRef.root.menu.toBack();
                        cylinderRef.root.menuSet.toBack();
                        cylinderRef.root.menu.hide();
                        cylinderRef.root.menuSet.hide();
                        cylinderRef.root.menuHided=true;
                        cylinderRef.root.rightClick=true;
                    } else if (e.which == 1) {
                        cylinderRef.root.rightClick=false;
                    }
                };

            this.menuFieldOver = function() {
                this.attr(cylinderRef.root.menuFieldTXTOver);
            };
            this.menuFieldOut = function() {
                this.attr(cylinderRef.root.menuFieldTXT);
            };

            this.pushBindedLink = function(link) {
                cylinderRef.bindedLinks.push(link);
            };

            this.setMoveJail = function(minX, minY, maxX, maxY) {
                this.boundary = {minX:minX,minY:minY,maxX:maxX,maxY:maxY};
                this.isJailed=true;
            };

            this.getBindingPoints = function() {
                return (
                    [
                        {circle:this.bindingPt1,x:this.bindingPt1X,y:this.bindingPt1Y},
                        {circle:this.bindingPt2,x:this.bindingPt2X,y:this.bindingPt2Y},
                        {circle:this.bindingPt3,x:this.bindingPt3X,y:this.bindingPt3Y},
                        {circle:this.bindingPt4,x:this.bindingPt4X,y:this.bindingPt4Y},
                        {circle:this.bindingPt5,x:this.bindingPt5X,y:this.bindingPt5Y},
                        {circle:this.bindingPt6,x:this.bindingPt6X,y:this.bindingPt6Y}
                    ]);
            };

            this.getBindedCircle = function (coord) {
                if (this.bindingPt1X==coord.x && this.bindingPt1Y==coord.y)
                    return this.bindingPt1;
                else if (this.bindingPt2X==coord.x && this.bindingPt2Y==coord.y)
                    return this.bindingPt2;
                else if (this.bindingPt3X==coord.x && this.bindingPt3Y==coord.y)
                    return this.bindingPt3;
                else if (this.bindingPt4X==coord.x && this.bindingPt4Y==coord.y)
                    return this.bindingPt4;
                else if (this.bindingPt5X==coord.x && this.bindingPt5Y==coord.y)
                    return this.bindingPt5;
                else if (this.bindingPt6X==coord.x && this.bindingPt6Y==coord.y)
                    return this.bindingPt6;
                else
                    return null;
            };

            this.getTopLeftCoords = function() {
                return {x:this.bindingPt4X,y:this.bindingPt4Y};
            };

            function delHexColor(c1, c2) {
                var hexStr = (parseInt(c1, 16) - parseInt(c2, 16)).toString(16);
                while (hexStr.length < 6) { hexStr = '0' + hexStr; } // Zero pad.
                return hexStr;
            }

            this.print = function(r_) {
                if (this.r == null || (this.r != null && r_!=this.r)) {
                    this.r = r_;
                    var fillColor   = "#" + this.color,
                        strokeColor = "#" + delHexColor("fff000", this.color);
                    this.cylinder  = this.r.path(this.vcpath).attr(
                        {
                            fill: fillColor,"fill-opacity": '0.7',"fill-rule": 'evenodd',stroke:strokeColor,"stroke-width": '2',"stroke-linecap": 'butt',
                            "stroke-linejoin": 'round',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1'
                        });
                    this.cylinder.transform(this.translateForm);
                    this.titleTxt   = this.r.text(this.ctrX, this.ctrY-this.diameter, this.title_).
                        attr({'font-size': '14px', 'font-weight': 'bold', 'font-family': 'Arial', fill: strokeColor, 'cursor': 'default'});
                    this.titleTxt.transform(this.translateForm);
                    this.bindingPt1 = this.r.circle(this.bindingPt1X, this.bindingPt1Y, 0);
                    this.bindingPt2 = this.r.circle(this.bindingPt2X, this.bindingPt2Y, 0);
                    this.bindingPt3 = this.r.circle(this.bindingPt3X, this.bindingPt3Y, 0);
                    this.bindingPt4 = this.r.circle(this.bindingPt4X, this.bindingPt4Y, 0);
                    this.bindingPt5 = this.r.circle(this.bindingPt5X, this.bindingPt5Y, 0);
                    this.bindingPt6 = this.r.circle(this.bindingPt6X, this.bindingPt6Y, 0);
                    this.cylinderR  = this.r.set().
                        push(this.titleTxt).push(this.cylinder).push(this.bindingPt1).push(this.bindingPt2).
                        push(this.bindingPt3).push(this.bindingPt4).push(this.bindingPt5).push(this.bindingPt6);
                    this.cylinderR.mousedown(mouseDown);
                    this.cylinderR.drag(cyMove, cyDragger, cyUP);
                }
            };

            this.changeInit = function() {
                this.exttX  = this.cylinder.attr("transform").toString();
                this.extox1 = this.bindingPt1.attr("cx");
                this.extoy1 = this.bindingPt1.attr("cy");
                this.extox2 = this.bindingPt2.attr("cx");
                this.extoy2 = this.bindingPt2.attr("cy");
                this.extox3 = this.bindingPt3.attr("cx");
                this.extoy3 = this.bindingPt3.attr("cy");
                this.extox4 = this.bindingPt4.attr("cx");
                this.extoy4 = this.bindingPt4.attr("cy");
                this.extox5 = this.bindingPt5.attr("cx");
                this.extoy5 = this.bindingPt5.attr("cy");
                this.extox6 = this.bindingPt6.attr("cx");
                this.extoy6 = this.bindingPt6.attr("cy");

                if (!this.root.menuHided) {
                    this.root.menu.toBack();
                    this.root.menuSet.toBack();
                    this.root.menu.hide();
                    this.root.menuSet.hide();
                    this.root.  menuHided=true;
                    if (this.r.getDisplayMainMenu())
                        this.r.setDisplayMainMenu(false);
                }

                this.isMoving=true;
                this.root.isMoving=true;
            };

            this.changeUp = function() {
                this.root.isMoving=false;
                this.isMoving=false;
            };

            // MOVEABLE

            this.moveInit = function() {
                if (this.root.isEditing)
                    this.r.scaleDone();

                this.r.busOnMovePush(this);
                this.r.moveSetPush(this.bindingPt1);
                this.r.moveSetPush(this.bindingPt2);
                this.r.moveSetPush(this.bindingPt3);
                this.r.moveSetPush(this.bindingPt4);
                this.r.moveSetPush(this.bindingPt5);
                this.r.moveSetPush(this.bindingPt6);

                for (var i = this.bindedLinks.length; i--;)
                    this.bindedLinks[i].moveInit();

                this.changeInit();
                this.cylinderR.animate({"fill-opacity": 1}, 500);
            };
            this.moveAction = function(dx,dy) {
                var transform = "t" + dx + "," + dy, i, link, up;
                this.lmvx = this.mvx; this.lmvy = this.mvy;
                this.mvx = dx; this.mvy = dy;
                if ((this.mvx!=this.lmvx) || (this.mvy!=this.lmvy)) {
                    this.cylinder.transform(transform+this.translateForm);
                    this.titleTxt.transform(transform+this.translateForm);

                    for (i = this.bindedLinks.length; i--;) {
                        link = this.bindedLinks[i];
                        if (this.r.isLinkToUp(i)) {
                            link.getEpSource().chooseMulticastTargetBindingPointAndCalcPoz(this.bindedLinks[i]);
                            up = this.r.link(link.toCompute());
                            if (typeof up != 'undefined')
                                link.toUpdate(up);
                        }
                    }
                }
            };
            this.moveUp = function() {
                this.ctrX += this.mvx; this.ctrY+=this.mvy; this.x=this.ctrX - this.height/ 2; this.y=this.ctrY - this.diameter/ 2;
                this.translateForm = this.exttX+"T"+this.mvx+","+this.mvy;
                this.bindingPt1X = this.extox1+this.mvx; this.bindingPt1Y = this.extoy1+this.mvy;
                this.bindingPt2X = this.extox2+this.mvx; this.bindingPt2Y = this.extoy2+this.mvy;
                this.bindingPt3X = this.extox3+this.mvx; this.bindingPt3Y = this.extoy3+this.mvy;
                this.bindingPt4X = this.extox4+this.mvx; this.bindingPt4Y = this.extoy4+this.mvy;
                this.bindingPt5X = this.extox5+this.mvx; this.bindingPt5Y = this.extoy5+this.mvy;
                this.bindingPt6X = this.extox6+this.mvx; this.bindingPt6Y = this.extoy6+this.mvy;

                this.mvx=0; this.mvy=0;
                this.cylinder.transform(this.translateForm);
                this.titleTxt.transform(this.translateForm);
                this.bindingPt1.attr({cx:this.bindingPt1X,cy:this.bindingPt1Y});
                this.bindingPt2.attr({cx:this.bindingPt2X,cy:this.bindingPt2Y});
                this.bindingPt3.attr({cx:this.bindingPt3X,cy:this.bindingPt3Y});
                this.bindingPt4.attr({cx:this.bindingPt4X,cy:this.bindingPt4Y});
                this.bindingPt5.attr({cx:this.bindingPt5X,cy:this.bindingPt5Y});
                this.bindingPt6.attr({cx:this.bindingPt6X,cy:this.bindingPt6Y});

                this.changeUp();

                if (this.root.isEditing)
                    this.r.scaleInit(this);
                this.cylinderR.animate({"fill-opacity": 0.8}, 500);
            };


            // EDITABLE

            this.menuFieldEditClick = function() {
                cylinderRef.root.menu.toBack();
                cylinderRef.root.menuSet.toBack();
                cylinderRef.root.menu.hide();
                cylinderRef.root.menuSet.hide();
                cylinderRef.root.menuHided=true;

                if (!cylinderRef.root.isEditing) {
                    cylinderRef.r.scaleInit(cylinderRef);
                    cylinderRef.root.isEditing = true;
                } else {
                    cylinderRef.r.scaleDone();
                    cylinderRef.root.isEditing = false;
                }
            };

            this.getBBox = function() {
                //noinspection JSSuspiciousNameCombination
                return {
                    x: this.bindingPt4X,
                    y: this.bindingPt4Y,
                    x2: this.bindingPt2X,
                    y2: this.bindingPt2Y,
                    width: this.height,
                    height: this.diameter
                }
            };

            this.getMinBBox = function() {
                return this.titleTxt.getBBox();
            };

            this.getMaxBBox = function() {
                if (this.isJailed) {
                    return {
                        x:      this.boundary.minX,
                        y:      this.boundary.minY,
                        x2:     this.boundary.maxX,
                        y2:     this.boundary.maxY,
                        width:  this.boundary.maxX - this.boundary.minX,
                        height: this.boundary.maxY - this.boundary.maxY
                    }
                } else {
                    return null;
                }
            };

            this.editInit = function() {
                this.extctrX     = this.ctrX;
                this.extctrY     = this.ctrY;
                this.extX        = this.x;
                this.extY        = this.y;
                this.extheight   = this.height;
                this.extdiameter = this.diameter;
                this.extvcpath   = this.vcpath;

                this.changeInit();
            };

            this.editAction = function(elem, dx, dy) {

                switch(elem.idx) {
                    case 0:
                        this.extX = this.x + dx;
                        this.extheight   = this.height - dx;
                        this.extdiameter = this.diameter - dy;
                        break;
                    case 1:
                        this.extheight = this.height + dx;
                        this.extdiameter = this.diameter - dy;
                        break;
                    case 2:
                        this.extY = this.y + dy;
                        this.extheight = this.height + dx;
                        this.extdiameter = this.diameter + dy;
                        break;
                    case 3:
                        this.extX = this.x + dx;
                        this.extY = this.y + dy;
                        this.extheight = this.height - dx;
                        this.extdiameter = this.diameter + dy;
                        break;
                    case 4:
                        this.extdiameter = this.diameter - dy;
                        break;
                    case 5:
                        this.extheight = this.height + dx;
                        break;
                    case 6:
                        this.extY = this.y + dy;
                        this.extdiameter = this.diameter + dy;
                        break;
                    case 7:
                        this.extX = this.x + dx;
                        this.extheight = this.height - dx;
                        break;
                    default:
                        break;
                }

                this.extctrX = this.extX + this.extheight/2;
                this.extctrY = this.extY + this.extdiameter/2;

                this.extox1 = this.extX;
                this.extoy1 = this.extY;
                this.extox2 = this.extX+this.extheight;
                this.extoy2 = this.extY;
                this.extox3 = this.extX+this.extheight;
                this.extoy3 = this.extY-this.extdiameter;
                this.extox4 = this.extX;
                this.extoy4 = this.extY-this.extdiameter;
                this.extox5 = this.extX+this.extheight/2;
                this.extoy5 = this.extY;
                this.extox6 = this.extX+this.extheight/2;
                this.extoy6 = this.extY-this.extdiameter;

                this.extvcpath    =
                    [
                        ["M", this.extX, this.extY],
                        ["C", this.extX+this.extdiameter/8, this.extY, this.extX+this.extdiameter/5, this.extY-this.extdiameter/4, this.extX+this.extdiameter/5, this.extY-this.extdiameter/2],
                        ["C", this.extX+this.extdiameter/5, this.extY-3*this.extdiameter/4, this.extX+this.extdiameter/8, this.extY-this.extdiameter, this.extX, this.extY-this.extdiameter],
                        ["C", this.extX-this.extdiameter/8, this.extY-this.extdiameter, this.extX-this.extdiameter/5, this.extY-3*this.extdiameter/4, this.extX-this.extdiameter/5, this.extY-this.extdiameter/2],
                        ["C", this.extX-this.extdiameter/5, this.extY-this.extdiameter/5, this.extX-this.extdiameter/8, this.extY, this.extX, this.extY],
                        ["Z"],
                        ["M", this.extX, this.extY],
                        ["L", this.extX+this.extheight, this.extY],
                        ["C", this.extX+this.extheight+this.extdiameter/8, this.extY, this.extX+this.extheight+this.extdiameter/5, this.extY-this.extdiameter/4, this.extX+this.extheight+this.extdiameter/5, this.extY-this.extdiameter/2],
                        ["C", this.extX+this.extheight+this.extdiameter/5, this.extY-3*this.extdiameter/4, this.extX+this.extheight+this.extdiameter/8, this.extY-this.extdiameter, this.extX+this.extheight, this.extY-this.extdiameter],
                        ["L", this.extX, this.extY-this.extdiameter]
                    ];


                this.cylinderR.pop(this.cylinder);
                this.cylinderR.pop(this.titleTxt);
                this.cylinder.remove();
                this.titleTxt.remove();

                var fillColor   = "#" + this.color,
                    strokeColor = "#" + delHexColor("fff000", this.color);
                this.cylinder  = this.r.path(this.extvcpath).attr(
                    {
                        fill: fillColor,"fill-opacity": '0.7',"fill-rule": 'evenodd',stroke:strokeColor,"stroke-width": '2',"stroke-linecap": 'butt',
                        "stroke-linejoin": 'round',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1'
                    });
                this.titleTxt   = this.r.text(this.extctrX, this.extctrY-this.extdiameter, this.title_).
                    attr({'font-size': '14px', 'font-weight': 'bold', 'font-family': 'Arial', fill: strokeColor, 'cursor': 'default'});

                this.bindingPt1.attr({cx:this.extox1,cy:this.extoy1});
                this.bindingPt2.attr({cx:this.extox2,cy:this.extoy2});
                this.bindingPt3.attr({cx:this.extox3,cy:this.extoy3});
                this.bindingPt4.attr({cx:this.extox4,cy:this.extoy4});
                this.bindingPt5.attr({cx:this.extox5,cy:this.extoy5});
                this.bindingPt6.attr({cx:this.extox6,cy:this.extoy6});

                for (var i = this.bindedLinks.length; i--;) {
                    var link = this.bindedLinks[i];
                    link.getEpSource().chooseMulticastTargetBindingPointAndCalcPoz(this.bindedLinks[i]);
                    var up = this.r.link(link.toCompute());
                    if (typeof up != 'undefined')
                        link.toUpdate(up);
                }

                this.cylinderR.push(this.cylinder);
                this.cylinderR.push(this.titleTxt);
                this.cylinderR.mousedown(mouseDown);
                this.cylinderR.drag(cyMove, cyDragger, cyUP);
            };

            this.editUp = function() {
                this.x = this.extX;
                this.y = this.extY;
                this.diameter = this.extdiameter;
                this.height = this.extheight;
                this.ctrX = this.x + this.height/2;
                this.ctrY = this.y + this.diameter/2;
                this.vcpath = this.extvcpath;
                this.translateForm=""

                this.bindingPt1X = this.x;
                this.bindingPt1Y = this.y;
                this.bindingPt2X = this.x+this.height;
                this.bindingPt2Y = this.y;
                this.bindingPt3X = this.x+this.height;
                this.bindingPt3Y = this.y-this.diameter;
                this.bindingPt4X = this.x;
                this.bindingPt4Y = this.y-this.diameter;
                this.bindingPt5X = this.x+this.height/2;
                this.bindingPt5Y = this.y;
                this.bindingPt6X = this.x+this.height/2;
                this.bindingPt6Y = this.y-this.diameter;
                this.changeUp();
            }

        }

        return cylinder;
    });