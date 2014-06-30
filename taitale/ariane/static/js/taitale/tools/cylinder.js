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
            this.ft         = null;
            this.boundary   = null;

            this.bindedLinks = [];

            this.bindingPt1 = null;
            this.bindingPt1X = this.x;
            this.bindingPt1Y = this.y;
            this.bindingPt2 = null;
            this.bindingPt2X = this.x+this.height;
            this.bindingPt2Y = this.y;
            this.bindingPt3 = null;
            this.bindingPt3X = this.x+this.height;
            this.bindingPt3Y = this.y-d;
            this.bindingPt4 = null;
            this.bindingPt4X = this.x;
            this.bindingPt4Y = this.y-d;
            this.bindingPt5 = null;
            this.bindingPt5X = this.x+this.height/2;
            this.bindingPt5Y = this.y;
            this.bindingPt6 = null;
            this.bindingPt6X = this.x+this.height/2;
            this.bindingPt6Y = this.y-d;

            this.root          = parent;
            this.root.isMoving = false;
            this.isMoving = false;

            this.mvx = 0;
            this.mvy = 0;

            var isJailed  = false;

            var cylinderRef = this;

            var cyDragger = function() {
                    cylinderRef.r.drag(cylinderRef, "bus");
                },
                cyMove = function(dx,dy) {
                    cylinderRef.r.move(dx,dy);
                },
                cyUP   = function() {
                    cylinderRef.r.up();
                };

            this.pushBindedLink = function(link) {
                cylinderRef.bindedLinks.push(link);
            };

            this.setMoveJail = function(minX, minY, maxX, maxY) {
                this.boundary = {minX:minX,minY:minY,maxX:maxX,maxY:maxY};
                isJailed=true;
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
                return {x:this.bindingPt4X,y:this.bindingPt4Y};
            };

            function delHexColor(c1, c2) {
                var hexStr = (parseInt(c1, 16) - parseInt(c2, 16)).toString(16);
                while (hexStr.length < 6) { hexStr = '0' + hexStr; } // Zero pad.
                return hexStr;
            }

            this.updateCylinder = function() {
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

                var fillColor   = "#"+this.color,
                    strokeColor = "#" + delHexColor("fff000", this.color);

                this.cylinder = this.r.path(this.vcpath).attr(
                    {
                        fill: fillColor,"fill-opacity": '0.7',"fill-rule": 'evenodd',stroke:strokeColor,"stroke-width": '2',"stroke-linecap": 'butt',
                        "stroke-linejoin": 'round',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1'
                    });
                this.titleTxt   = this.r.text(this.ctrX, this.ctrY-this.diameter, this.title_).attr({'font-size': '14px', 'font-weight': 'bold', 'font-family': 'Arial', fill: strokeColor});

                this.cylinderR.remove();
                this.cylinderR  = this.r.set().push(this.titleTxt).push(this.cylinder).push(this.bindingPt1).push(this.bindingPt2).
                    push(this.bindingPt3).push(this.bindingPt4).push(this.bindingPt5).push(this.bindingPt6);
                this.cylinderR.drag(cyMove, cyDragger, cyUP);
            };

            this.print = function(r_) {
                if (this.r == null || (this.r != null && r_!=this.r)) {
                    this.r = r_;
                    var fillColor   = "#"+this.color,
                        strokeColor = "#" + delHexColor("fff000", this.color);
                    this.cylinder  = this.r.path(this.vcpath).attr(
                        {
                            fill: fillColor,"fill-opacity": '0.7',"fill-rule": 'evenodd',stroke:strokeColor,"stroke-width": '2',"stroke-linecap": 'butt',
                            "stroke-linejoin": 'round',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1'
                        });
                    this.cylinder.transform(this.translateForm);
                    this.titleTxt   = this.r.text(this.ctrX, this.ctrY-this.diameter, this.title_).attr({'font-size': '14px', 'font-weight': 'bold', 'font-family': 'Arial', fill: strokeColor});
                    this.titleTxt.transform(this.translateForm);
                    this.bindingPt1 = this.r.circle(this.bindingPt1X,this.bindingPt1Y,0);
                    this.bindingPt2 = this.r.circle(this.bindingPt2X,this.bindingPt2Y,0);
                    this.bindingPt3 = this.r.circle(this.bindingPt3X,this.bindingPt3Y,0);
                    this.bindingPt4 = this.r.circle(this.bindingPt4X,this.bindingPt4Y,0);
                    this.bindingPt5 = this.r.circle(this.bindingPt5X,this.bindingPt5Y,0);
                    this.bindingPt6 = this.r.circle(this.bindingPt6X,this.bindingPt6Y,0);
                    this.cylinderR  = this.r.set().push(this.titleTxt).push(this.cylinder).push(this.bindingPt1).push(this.bindingPt2).
                        push(this.bindingPt3).push(this.bindingPt4).push(this.bindingPt5).push(this.bindingPt6);
                    this.cylinderR.drag(cyMove, cyDragger, cyUP);
                    //this.plugFreeTransform();
                }
            };
        }


        return cylinder;
    });