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
        function hat(company_,product_,component_) {
            function tibcoLogo() {
                this.width=40;
                this.height=30;

                this.print = function(r,x,y,rset) {
                    var rightPath = r.path(
                        "M 97,524 C 97,524 110,511 95,487 C 95,487 81,466 69,455 L 2,455 " +
                            "L 2,609 L 28,609 C 35,601 74,555 79,548 C 79,548 94,531 97,524");
                    rightPath.transform("t-2,-453s0.2t-200,-317t"+x*5+","+y*5)
                    rset.push(rightPath);

                    var middlePath = r.path(
                        "M 147,532 C 147,532 152,516 135,494 C 135,494 126,483 94,455 L 75,455 " +
                            "C 78,459 91,471 99,482 C 99,482 118,504 105,527 C 105,527 98,540 41,609 " +
                            "L 101,609 C 106,602 129,569 132,562 C 132,562 143,545 147,532");
                    middlePath.transform("t-2,-453s0.2t-200,-317t-160,0t"+x*5+","+y*5)
                    rset.push(middlePath);

                    var leftPath = r.path(
                        "M 137,487 C 137,487 161,510 154,536 C 154,536 153,549 114,609 L 161,609" +
                            "L 161,455 L 100,455 C 105,460 128,478 137,487");
                    leftPath.transform("t-2,-453s0.2t-200,-317t-295,0t"+x*5+","+y*5)
                    rset.push(leftPath);
                }
            }

            function companyLogo(company) {
                this.logoSet = null;
                this.cpyLogo = null;
                if (company==="Tibco") {
                    this.cpyLogo = new tibcoLogo();
                    this.logoWidth = this.cpyLogo.width;
                    this.logoHeight = this.cpyLogo.height;
                } else {
                    this.logoWidth=0;
                    this.logoHeight=0;
                }

                this.print= function(r,x,y) {
                    if (this.cpyLogo) {
                        this.logoSet = r.set();
                        this.cpyLogo.print(r,x,y,this.logoSet);
                    }
                }
            }

            var txtFont = params.containerHat_txtFont,
                logo = new companyLogo(company_),
                product = product_,
                component = component_;

            this.width  = logo.logoWidth + Math.max((product ? product.width(txtFont):0),(component ? component.width(txtFont):0));
            this.height = Math.max(logo.logoHeight, (product ? product.height(txtFont) + ((logo.logoHeight<10)?10:0): 0) + (component ? component.height(txtFont):0));
            this.hatSet = null;
            this.color  = null;

            this.move = function(r,newX,newY) {
                this.hatSet.remove();
                this.print(r,newX,newY,this.color);
            }

            this.print = function (r,x,y,color_) {
                this.color = color_;
                this.hatSet = r.set();
                logo.print(r,x-this.width/3,y);
                if (logo.logoSet)
                    this.hatSet.push(logo.logoSet);

                if (product != null) {
                    var productTxt = r.text(x-this.width/3+logo.logoWidth+product.width(txtFont)/2, y+((logo.logoHeight/3<10)?10:logo.logoHeight/3), product).attr(txtFont);
                    this.hatSet.push(productTxt);
                }
                if (component != null) {
                    var componentTxt = r.text(x-this.width/3+logo.logoWidth+component.width(txtFont)/2, y+((logo.logoHeight/3<10)?10:logo.logoHeight/3)+(product ? product.height(txtFont):0), component).attr(txtFont);
                    this.hatSet.push(componentTxt);
                }

                this.hatSet.attr({fill: this.color,'fill-rule':'nonzero',stroke:'none','stroke-width':'1','stroke-opacity':'1'});
            }
        };

        return hat;
    }
);