// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Container Hat                   │ \\
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
        'taitale-params',
        'taitale-ext-string'
    ],
    function (params) {
        function hat(name_, nameFont, color_) {

            function datacenterLogo() {
                this.width  = 50;
                this.height = 50;

                this.print = function(r,x,y,color,hset) {
                    var datacenterPath = r.path("m 16.286384,958.74213 0,7.58486 30.916164,16.23674 0,7.58485 -30.887324,-16.20793 " +
                        "0,67.97525 -15.45808192,0 0,4.0664 98.28571592,0 0,-4.0664 -15.400404,0 0,-62.66873 -36.539906,-19.29377 0,7.64252 " +
                        "31.810195,16.61167 0,7.58485 z m 8.594232,38.01947 11.79544,0 0,33.0791 -11.79544,0 0,-1.4997 10.295776,0 0,-30.0509 " +
                        "-10.295776,0 z m 38.443327,3.9899 11.79544,0 0,33.0793 -11.79544,0 0,-1.5286 10.295774,0 0,-30.0222 -10.295774,0 z " +
                        "m -38.443327,1.057 7.325286,0 0,3.3454 -7.325286,0 z m 19.207244,3.1826 11.824279,0 0,33.0791 -11.824279,0 0,-1.4997 " +
                        "10.295774,0 0,-30.051 -10.295774,0 z m 19.236083,0.8075 7.296445,0 0,3.3165 -7.296445,0 z m -19.236083,4.2394 7.325285," +
                        "0 0,3.3165 -7.325285,0 z m -19.207244,-1.5963 7.325286,0 0,3.3166 -7.325286,0 z m 38.443327,3.961 7.296445,0 0,3.3167 " +
                        "-7.296445,0 z m -19.236083,4.2395 7.325285,0 0,3.3166 -7.325285,0 z m -19.207244,-1.5385 7.325286,0 0,3.3454 -7.325286," +
                        "0 z m 38.443327,3.9899 7.296445,0 0,3.3454 -7.296445,0 z m -19.236083,4.2395 7.325285,0 0,3.3454 -7.325285,0 z m -19.207244," +
                        "-1.4232 7.325286,0 0,3.3165 -7.325286,0 z m 38.443327,3.9898 7.296445,0 0,3.3167 -7.296445,0 z m -19.236083,4.2395 7.325285," +
                        "0 0,3.3165 -7.325285,0 z").
                        attr({fill: color,"fill-opacity": '1',
                            "fill-rule": 'evenodd','stroke-width': '0','stroke-opacity': '1'}).
                        transform("t0,-952.36218t-25,-30s0.5t"+x*2+","+y*2);
                    hset.push(datacenterPath);
                }
            }

            var txtFont = params.containerHat_txtFont,
                dcLogo  = new datacenterLogo();

            this.width   = dcLogo.width;
            this.height  = dcLogo.height;
            this.textSet = null;
            this.hatSet  = null;
            this.color   = color_;
            this.txtFont = nameFont;
            this.name    = name_;

            this.move = function(r,newX,newY) {
                this.hatSet.remove();
                this.print(r,newX,newY);
            };

            this.print = function (r,x,y) {
                this.textSet = r.set();
                this.hatSet = r.set();
                dcLogo.print(r,x-this.width/3,y,this.color,this.hatSet);

                this.hatSet.push(this.textSet);
                this.hatSet.attr({stroke:'none','stroke-width':'1','stroke-opacity':'1'});

                if (this.name != null) {
                    var nameTxt = r.text(x,
                            y+dcLogo.height+5, this.name).attr(this.txtFont);
                    this.textSet.push(nameTxt);
                }
            };

            this.hide = function() {
                this.hatSet.hide();
                this.textSet.hide();
            };

            this.show = function() {
                this.hatSet.show();
                this.textSet.show();
            };

            this.mousedown = function(callback) {
                this.textSet.mousedown(callback);
                this.hatSet.mousedown(callback);
            };

            this.drag = function(mvcb, drgcb, upcb) {
                this.textSet.drag(mvcb, drgcb, upcb);
                this.hatSet.drag(mvcb, drgcb, upcb);
            };

            this.toFront = function() {
                this.textSet.toFront();
                this.hatSet.toFront();
            };

            this.toBack = function() {
                this.textSet.toBack();
                this.hatSet.toBack();
            };
        }

        return hat;
    }
);