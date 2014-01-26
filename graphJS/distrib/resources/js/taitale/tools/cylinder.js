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
        function cylinder(centerX,centerY,d,h,title,color_) {
            var r       = null,
                ctrX    = centerX,
                ctrY    = centerY,
                x       = ctrX - h/ 2,
                y       = ctrY - d/ 2,
                title_  = title,
                color   = color_,
                vcpath  =
                    [
                        ["M", x, y],
                        ["C", x+d/8, y, x+d/5, y-d/4, x+d/5, y-d/2],
                        ["C", x+d/5, y-3*d/4, x+d/8, y-d, x, y-d],
                        ["C", x-d/8, y-d, x-d/5, y-3*d/4, x-d/5, y-d/2],
                        ["C", x-d/5, y-d/5, x-d/8, y, x, y],
                        ["Z"],
                        ["M", x, y],
                        ["L", x+h, y],
                        ["C", x+h+d/8, y, x+h+d/5, y-d/4, x+h+d/5, y-d/2],
                        ["C", x+h+d/5, y-3*d/4, x+h+d/8, y-d, x+h, y-d],
                        ["L", x,y-d]
                    ],
                translateForm="",
                helper_ = new helper();

            var cylinder   = null,
                titleTxt   = null,
                cylinderR  = null,
                ft         = null,
                boundary   = null;

            var bindedLinks = [];

            var bindingPt1 = null,
                bindingPt1X = x,
                bindingPt1Y = y,
                bindingPt2 = null,
                bindingPt2X = x+h,
                bindingPt2Y = y,
                bindingPt3 = null,
                bindingPt3X = x+h,
                bindingPt3Y = y-d,
                bindingPt4 = null,
                bindingPt4X = x,
                bindingPt4Y = y-d,
                bindingPt5 = null,
                bindingPt5X = x+h/2,
                bindingPt5Y = y,
                bindingPt6 = null,
                bindingPt6X = x+h/2,
                bindingPt6Y = y-d;

            var isMoving = false,
                isJailed = false;

            var dragger = function() {
                    isMoving=true;
                },
                mover = function(tx,ox1,oy1,ox2,oy2,ox3,oy3,ox4,oy4,ox5,oy5,ox6,oy6,dx,dy) {
                    if (isJailed) {
                        if (ox1+dx<boundary.minX)
                            dx=boundary.minX-ox1;
                        else if (ox3+dx>boundary.maxX)
                            dx=boundary.maxX-ox3;
                        if (oy1+dy>boundary.maxY)
                            dy=boundary.maxY-oy1;
                        else if (oy3+dy<boundary.minY)
                            dy=boundary.minY-oy3;
                    }

                    translateForm = tx+"T"+dx+","+dy;
                    ctrX +=dx; ctrY+=dy; x=ctrX - h/ 2; y=ctrY - d/ 2;
                    bindingPt1X = ox1+dx; bindingPt1Y = oy1+dy;
                    bindingPt2X = ox2+dx; bindingPt2Y = oy2+dy;
                    bindingPt3X = ox3+dx; bindingPt3Y = oy3+dy;
                    bindingPt4X = ox4+dx; bindingPt4Y = oy4+dy;
                    bindingPt5X = ox5+dx; bindingPt5Y = oy5+dy;
                    bindingPt6X = ox6+dx; bindingPt6Y = oy6+dy;

                    cylinder.transform(translateForm);
                    titleTxt.transform(translateForm);
                    bindingPt1.attr({cx:bindingPt1X,cy:bindingPt1Y});
                    bindingPt2.attr({cx:bindingPt2X,cy:bindingPt2Y});
                    bindingPt3.attr({cx:bindingPt3X,cy:bindingPt3Y});
                    bindingPt4.attr({cx:bindingPt4X,cy:bindingPt4Y});
                    bindingPt5.attr({cx:bindingPt5X,cy:bindingPt5Y});
                    bindingPt6.attr({cx:bindingPt6X,cy:bindingPt6Y});

                    for (var i = bindedLinks.length; i--;) {
                        bindedLinks[i].getEpSource().chooseMulticastTargetBindingPointAndCalcPoz(bindedLinks[i]);
                        var up = r.link(bindedLinks[i].toCompute());
                        if (typeof up != 'undefined')
                            bindedLinks[i].toUpdate(up);
                    };
                },
                upper = function() {
                    isMoving=false;
                };

            var cyDragger = function() {
                    this.tX  = cylinder.attr("transform").toString();
                    this.ox1 = bindingPt1.attr("cx");
                    this.oy1 = bindingPt1.attr("cy");
                    this.ox2 = bindingPt2.attr("cx");
                    this.oy2 = bindingPt2.attr("cy");
                    this.ox3 = bindingPt3.attr("cx");
                    this.oy3 = bindingPt3.attr("cy");
                    this.ox4 = bindingPt4.attr("cx");
                    this.oy4 = bindingPt4.attr("cy");
                    this.ox5 = bindingPt5.attr("cx");
                    this.oy5 = bindingPt5.attr("cy");
                    this.ox6 = bindingPt6.attr("cx");
                    this.oy6 = bindingPt6.attr("cy");
                    dragger();
                },
                cyMove = function(dx,dy) {
                    mover(this.tX,this.ox1,this.oy1,this.ox2,this.oy2,this.ox3,this.oy3,
                        this.ox4,this.oy4,this.ox5,this.oy5,this.ox6,this.oy6,dx,dy);
                },
                cyUP   = function() {
                    upper();
                };

            this.dragger = function() {
                this.exttX  = cylinder.attr("transform").toString();
                this.extox1 = bindingPt1.attr("cx");
                this.extoy1 = bindingPt1.attr("cy");
                this.extox2 = bindingPt2.attr("cx");
                this.extoy2 = bindingPt2.attr("cy");
                this.extox3 = bindingPt3.attr("cx");
                this.extoy3 = bindingPt3.attr("cy");
                this.extox4 = bindingPt4.attr("cx");
                this.extoy4 = bindingPt4.attr("cy");
                this.extox5 = bindingPt5.attr("cx");
                this.extoy5 = bindingPt5.attr("cy");
                this.extox6 = bindingPt6.attr("cx");
                this.extoy6 = bindingPt6.attr("cy");
                dragger();
            };

            this.mover = function(dx,dy) {
                mover(this.exttX,this.extox1,this.extoy1,this.extox2,this.extoy2,this.extox3,this.extoy3,
                    this.extox4,this.extoy4,this.extox5,this.extoy5,this.extox6,this.extoy6,dx,dy);
            };

            this.uper = function() {
                upper();
            };

            this.pushBindedLink = function(link) {
                bindedLinks.push(link);
            };

            this.setMoveJail = function(minX, minY, maxX, maxY) {
                boundary = {minX:minX,minY:minY,maxX:maxX,maxY:maxY}
                isJailed=true;
            };

            this.isMoving = function() {
                return isMoving;
            };

            this.getBindingPoints = function() {
                var ret =
                    [
                        {circle:bindingPt1,x:bindingPt1X,y:bindingPt1Y},
                        {circle:bindingPt2,x:bindingPt2X,y:bindingPt2Y},
                        {circle:bindingPt3,x:bindingPt3X,y:bindingPt3Y},
                        {circle:bindingPt4,x:bindingPt4X,y:bindingPt4Y},
                        {circle:bindingPt5,x:bindingPt5X,y:bindingPt5Y},
                        {circle:bindingPt6,x:bindingPt6X,y:bindingPt6Y}
                    ];
                return ret;
            };

            this.getBindedCircle = function (coord) {
                if (bindingPt1X==coord.x && bindingPt1Y==coord.y)
                    return bindingPt1;
                else if (bindingPt2X==coord.x && bindingPt2Y==coord.y)
                    return bindingPt2;
                else if (bindingPt3X==coord.x && bindingPt3Y==coord.y)
                    return bindingPt3;
                else if (bindingPt4X==coord.x && bindingPt4Y==coord.y)
                    return bindingPt4;
                else if (bindingPt5X==coord.x && bindingPt5Y==coord.y)
                    return bindingPt5;
                else if (bindingPt6X==coord.x && bindingPt6Y==coord.y)
                    return bindingPt6;
                else
                    return null;
            };

            /*
             this.getCylinderPath = function() {
             return cylinder;
             };

             this.getFreeTransform = function() {
             return ft;
             };

             this.plugFreeTransform = function() {
             ft = r.freeTransform(cylinderR,{},ftCallback);
             ft.setOpts({boundary: boundary});
             ft.hideHandles({undrag: false})
             };

             this.unplugFreeTransform = function() {
             if (ft!=null)
             ft.unplug();
             };
             */

            this.getTopLeftCoords = function() {
                return {x:bindingPt4X,y:bindingPt4Y};
            };

            this.isPrinted  = function() {
                return (cylinder!=null);
            }

            function delHexColor(c1, c2) {
                var hexStr = (parseInt(c1, 16) - parseInt(c2, 16)).toString(16);
                while (hexStr.length < 6) { hexStr = '0' + hexStr; } // Zero pad.
                return hexStr;
            }

            this.print = function(r_) {
                if (r == null || (r != null && r_!=r)) {
                    r = r_;
                    var fillColor   = "#"+color,
                        strokeColor = "#" + delHexColor("fff000", color);
                    cylinder  = r.path(vcpath).attr(
                        {
                            fill: fillColor,"fill-opacity": '0.7',"fill-rule": 'evenodd',stroke:strokeColor,"stroke-width": '2',"stroke-linecap": 'butt',
                            "stroke-linejoin": 'round',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1'
                        });
                    cylinder.transform(translateForm);
                    titleTxt   = r.text(centerX, centerY-d, title_).attr({'font-size': '14px', 'font-weight': 'bold', 'font-family': 'Arial', fill: strokeColor});
                    titleTxt.transform(translateForm);
                    bindingPt1 = r.circle(bindingPt1X,bindingPt1Y,0);
                    bindingPt2 = r.circle(bindingPt2X,bindingPt2Y,0);
                    bindingPt3 = r.circle(bindingPt3X,bindingPt3Y,0);
                    bindingPt4 = r.circle(bindingPt4X,bindingPt4Y,0);
                    bindingPt5 = r.circle(bindingPt5X,bindingPt5Y,0);
                    bindingPt6 = r.circle(bindingPt6X,bindingPt6Y,0);
                    cylinderR  = r.set().push(titleTxt).push(cylinder).push(bindingPt1).push(bindingPt2).
                        push(bindingPt3).push(bindingPt4).push(bindingPt5).push(bindingPt6);
                    cylinderR.drag(cyMove, cyDragger, cyUP);
                    //this.plugFreeTransform();
                }
            };
        }


        return cylinder;
    });