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

            function routerLogo() {
                this.width  = 28;
                this.height = 28;

                this.print = function(r,x,y,color,hset) {

                    var path_d = r.path(" M 47.96 32.06 C 52.01 25.65 57.35 20.06 63.18 15.24 C 63.59 15.24 64.41 15.24 64.83 " +
                        "15.24 C 70.50 19.88 75.58 25.33 79.68 31.42 C 80.56 32.46 79.54 34.41 78.13 34.28 C 75.68 34.50 73.23 " +
                        "34.37 70.78 34.37 C 70.18 38.59 69.96 42.89 68.92 47.03 C 66.39 49.94 61.64 49.27 58.67 47.31 C 58.14 " +
                        "43.00 57.62 38.69 57.19 34.37 C 54.25 33.80 48.73 36.10 47.96 32.06 Z");
                    path_d.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'}).transform("t-50,-25s0.2t"+x*5+","+y*5);
                    hset.push(path_d);

                    var path_e = r.path(" M 29.63 57.22 C 30.21 54.28 27.89 48.75 31.93 47.96 C 38.34 51.99 43.91 57.34 48.74 " +
                        "63.14 C 48.75 63.57 48.75 64.42 48.75 64.85 C 44.29 70.31 39.05 75.16 33.29 79.23 C 32.37 80.27 30.51 " +
                        "80.25 29.88 78.92 C 29.39 76.25 29.67 73.51 29.63 70.81 C 25.31 70.38 21.00 69.86 16.69 69.33 C 14.72 " +
                        "66.37 14.04 61.51 17.04 59.07 C 21.15 58.02 25.44 57.83 29.63 57.22 Z");
                    path_e.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'}).transform("t-25,-50s0.2t"+x*5+","+y*5);
                    hset.push(path_e);

                    var path_f = r.path(" M 95.34 48.36 C 96.38 47.40 98.48 48.47 98.28 49.93 C 98.49 52.36 98.37 54.80 98.38 " +
                        "57.23 C 102.59 57.82 106.89 58.03 111.02 59.08 C 113.93 61.60 113.25 66.37 111.31 69.33 C 107.00 69.86 " +
                        "102.69 70.38 98.37 70.82 C 97.80 73.76 100.09 79.22 96.08 80.05 C 89.66 76.00 84.06 70.66 79.24 64.82 C " +
                        "79.24 64.41 79.24 63.59 79.24 63.18 C 83.86 57.52 89.29 52.47 95.34 48.36 Z");
                    path_f.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'}).transform("t-75,-50s0.2t"+x*5+","+y*5);
                    hset.push(path_f);

                    var path_i = r.path(" M 58.67 80.69 C 61.64 78.72 66.32 78.09 68.92 80.94 C 69.96 85.09 70.18 89.40 70.78 " +
                        "93.63 C 73.67 93.85 76.92 92.96 79.57 94.32 C 79.69 94.72 79.93 95.52 80.05 95.92 C 76.00 102.34 70.66 " +
                        "107.92 64.84 112.76 C 64.42 112.75 63.57 112.75 63.15 112.75 C 57.51 108.12 52.47 102.71 48.37 96.67 C " +
                        "47.41 95.63 48.43 93.56 49.89 93.72 C 52.31 93.50 54.75 93.63 57.17 93.64 C 57.63 89.32 58.14 85.01 58.67 " +
                        "80.69 Z");
                    path_i.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'}).transform("t-50,-75s0.2t"+x*5+","+y*5);
                    hset.push(path_i);

                    var path_b = r.path(" M 62.89 0.00 L 65.34 0.00 C 82.11 0.41 98.62 7.54 110.16 19.75 C 121.30 31.22 127.67 " +
                        "46.98 128.00 62.94 L 128.00 65.33 C 127.59 81.19 121.24 96.83 110.17 108.24 C 98.58 120.50 81.98 127.65 " +
                        "65.13 128.00 L 62.67 128.00 C 46.82 127.59 31.18 121.25 19.78 110.19 C 7.48 98.57 0.32 81.92 0.00 65.03 " +
                        "L 0.00 62.66 C 0.41 46.12 7.34 29.82 19.27 18.30 C 30.76 6.90 46.72 0.35 62.89 0.00 Z");
                    path_b.attr({fill: '#333333','stroke-width': '0','stroke-opacity': '1'}).transform("t-50,-50s0.2t"+x*5+","+y*5);
                    hset.push(path_b);
                }
            }

            var txtFont = params.containerHat_txtFont,
                routerLogo  = new routerLogo();

            this.width   = routerLogo.width;
            this.height  = routerLogo.height;
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
                routerLogo.print(r,x-this.width/3,y,this.color,this.hatSet);

                if (this.name != null) {
                    var nameTxt = r.text(x,
                            y+routerLogo.height+5, this.name).attr(this.txtFont);
                    this.textSet.push(nameTxt);
                }

                this.hatSet.push(this.textSet);
                this.hatSet.attr({stroke:'none','stroke-width':'1','stroke-opacity':'1'});
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