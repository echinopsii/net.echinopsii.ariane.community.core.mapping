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

                this.print = function(r,x,y,rset,color) {
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
                    rset.attr({fill: color,'fill-rule':'nonzero'})
                }
            }

            function rabbitmqLogo() {
                this.width = 41;
                this.height = 41;

                this.print = function(r, x, y, rset, color) {
                    var bodyLayer = r.set();

                    var whitePath = r.path(" M 75.86 58.81 C 80.32 56.90 85.25 57.17 89.99 57.21 C 108.01 57.26 126.03 57.19 144.05 57.23 C 152.31 57.40 159.51 62.47 164.61 68.65 C 167.98 " +
                        "73.38 169.80 79.14 169.80 84.95 C 169.77 134.65 169.80 184.36 169.78 234.07 C 169.41 237.56 171.74 240.34 174.15 242.52 C 176.73 244.24 180.00 244.11 182.97 244.23 " +
                        "C 200.00 244.13 217.03 244.30 234.05 244.16 C 239.02 244.37 244.07 240.97 244.95 235.93 C 244.92 186.95 244.98 137.95 244.93 88.96 C 244.89 83.20 245.19 77.20 247.97 " +
                        "72.02 C 252.45 63.23 262.07 57.03 272.02 57.20 C 291.35 57.27 310.68 57.23 330.02 57.22 C 336.29 57.21 342.36 59.95 347.03 64.05 C 352.32 68.37 354.92 74.89 356.39 " +
                        "81.38 C 356.73 132.59 356.40 183.81 356.56 235.02 C 356.51 236.81 357.47 238.37 358.22 239.93 C 360.49 243.56 365.05 244.28 369.00 244.22 C 417.99 244.19 466.99 244.21 " +
                        "515.99 244.20 C 523.09 243.96 530.04 247.01 535.04 251.98 C 540.02 256.49 542.11 263.02 543.60 269.37 C 543.63 350.90 543.58 432.43 543.62 513.97 C 544.52 526.65 534.94 " +
                        "538.73 522.83 541.92 C 518.67 543.14 514.30 542.72 510.03 542.79 C 370.36 542.78 230.69 542.79 91.01 542.79 C 86.69 542.71 82.25 543.19 78.07 541.82 C 67.56 538.90 59.10 " +
                        "529.37 57.87 518.48 C 57.84 382.33 57.88 246.19 57.86 110.05 C 58.39 100.70 57.42 91.34 57.96 81.99 C 58.38 76.24 61.02 70.87 64.57 66.41 C 68.00 63.42 71.52 60.37 75.86 " +
                        "58.81 Z");
                    whitePath.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'}).data('id', 'path_d');
                    bodyLayer.push(whitePath);
                    rset.push(whitePath);

                    var body = r.path(" M 66.72 84.88 C 66.90 75.59 74.50 66.71 84.01 66.39 C 103.36 66.29 122.71 66.36 142.06 66.35 C 151.99 65.87 161.01 75.08 160.62 84.96 C 160.62 134.65 " +
                        "160.63 184.34 160.61 234.03 C 160.05 244.11 169.00 253.15 179.01 253.20 C 197.99 253.20 216.99 253.25 235.97 253.18 C 245.77 252.83 254.21 243.96 253.84 234.13 C 253.85 " +
                        "184.05 253.84 133.97 253.85 83.89 C 254.07 74.54 262.51 66.05 271.96 66.36 C 291.32 66.34 310.67 66.32 330.03 66.37 C 339.69 66.48 347.72 75.64 347.52 85.14 C 347.58 135.42 " +
                        "347.51 185.71 347.55 235.99 C 348.03 245.39 356.66 253.19 366.00 253.20 C 416.01 253.22 466.02 253.20 516.03 253.21 C 525.60 253.01 534.13 261.47 534.37 270.95 C 534.28 " +
                        "353.30 534.45 435.65 534.28 518.00 C 532.71 526.50 524.95 533.95 516.00 533.64 C 372.66 533.68 229.32 533.65 85.98 533.65 C 75.75 534.35 66.92 525.12 66.73 515.12 C 66.73 " +
                        "371.71 66.74 228.29 66.72 84.88 Z");
                    body.attr({fill: '#f57b20','stroke-width': '0','stroke-opacity': '1'}).data('id', 'path_f');
                    bodyLayer.push(body);
                    rset.push(body);

                    var eye = r.path(" M 370.26 347.19 C 372.80 346.59 375.42 346.81 378.01 346.79 C 390.01 346.78 402.01 346.81 414.00 346.78 C 423.68 347.07 432.85 352.99 437.37 361.51 C 439.94 366.23 " +
                        "440.74 371.67 440.66 376.99 C 440.60 388.86 440.76 400.75 440.58 412.63 C 440.58 423.07 434.01 433.10 424.49 437.37 C 420.36 439.55 415.67 439.61 411.16 440.24 C 399.73 440.22 388.31 " +
                        "440.23 376.89 440.24 C 373.21 439.51 369.34 439.80 365.85 438.26 C 355.35 434.67 347.73 424.11 347.53 413.03 C 347.55 399.69 347.52 386.35 347.55 373.01 C 348.03 360.54 358.03 349.35 " +
                        "370.26 347.19 Z");
                    eye.attr({fill: '#fefefe','stroke-width': '0','stroke-opacity': '1'}).data('id', 'path_k');
                    rset.push(eye);

                    bodyLayer.transform("t"+x+","+y+"t-280,-280s0.08");
                    eye.transform("t"+x+","+y+"t-280,-280t-85,-85s0.08");
                }
            }

            function mariadbLogo() {
                this.width=59;
                this.height=39;

                this.print = function(r, x, y, rset, color) {
                    var transform1 = "t-252.94671,-292.65607s0.3t-190,-130t"+x*1/0.3+","+y*1/0.3;
                    var transform2 = "t-252.94671,-292.65607s0.3t-308,-205t"+x*1/0.3+","+y*1/0.3;
                    var transform3 = "t-252.94671,-292.65607s0.3t-335,-25t"+x*1/0.3+","+y*1/0.3;
                    var transform4 = "t-252.94671,-292.65607s0.3t-380,-15t"+x*1/0.3+","+y*1/0.3;

                    var backPath = r.path("M 428.82171,292.65922 C 426.04264,292.74803 426.92153,293.54877 420.91654,295.02655 C 414.8526,296.51884 407.44539,296.06125 400.91546,298.79984 C " +
                        "381.42246,306.97484 377.51145,334.91583 359.79046,344.92484 C 346.54446,352.40684 333.18047,353.00359 321.16546,356.76859 C 313.26946,359.24459 304.63196,364.32235 " +
                        "297.47796,370.48734 C 291.92496,375.27434 291.77997,379.48335 285.97796,385.48734 C 279.77196,391.90934 261.31271,385.59586 252.94671,395.42484 C 255.64171,398.14984 " +
                        "256.82322,398.9131 262.13421,398.20609 C 261.03464,400.29018 254.5529,402.0465 255.82171,405.11234 C 257.15677,408.33826 272.8269,410.52508 287.07171,401.92484 C " +
                        "293.70571,397.91959 298.98972,392.1466 309.32171,390.76859 C 322.69171,388.98659 338.09372,391.9116 353.57171,394.14359 C 351.27671,400.98659 346.66897,405.53734 " +
                        "342.97796,410.98734 C 341.83496,412.21834 345.2737,412.35633 349.19671,411.61234 C 356.25371,409.86734 361.33947,408.46234 366.66546,405.36234 C 373.20846,401.55334 " +
                        "374.19997,391.78785 382.22796,389.67484 C 386.70096,396.54984 398.86649,398.17385 406.41546,392.67484 C 399.79146,390.79984 397.96073,376.70034 400.19671,370.48734 C " +
                        "402.31471,364.60634 404.40749,355.19884 406.54046,347.42484 C 408.83046,339.07584 409.67529,328.55269 412.44671,324.29984 C 416.61636,317.90135 421.22366,315.70409 " +
                        "425.22363,312.09604 C 429.22359,308.48799 432.88484,304.97584 432.76469,296.71988 C 432.72599,294.06068 431.35105,292.57839 428.82171,292.65922 z").
                        attr({id: 'backPath',parent: 'layer1',fill: color,"fill-opacity": '1',
                            "fill-rule": 'evenodd',stroke: 'none','stroke-width':'1','stroke-opacity':'1',"stroke-width": '0.2',
                            "stroke-miterlimit": '4',"stroke-dasharray": 'none',"stroke-opacity": '1'}).
                        transform(transform1).data('id', 'backPath');

                    var frontPath1 = r.path("M 258.70071,404.52409 C 268.84471,405.97709 275.01371,404.52409 283.15971,400.99109 C 290.09071,397.98509 296.78471,391.78809 304.96971,389.16109 C " +
                        "316.99071,385.30409 330.17271,389.16609 343.02271,389.93609 C 346.15171,390.12409 349.26071,390.12609 352.32971,389.79209 C 357.11671,386.85009 357.01771,375.84509 " +
                        "361.67671,374.83709 C 361.53971,390.27709 355.20971,399.52809 348.58971,408.48609 C 362.54171,406.02209 370.89071,397.95209 376.52971,387.17409 C 378.24071,383.90609 " +
                        "379.70171,380.39009 380.99371,376.70509 C 382.99071,378.23809 381.85871,382.90209 382.86271,385.42909 C 392.47271,380.07609 397.97571,367.85909 401.61971,355.50309 C " +
                        "405.83571,341.20309 407.56171,326.72309 410.28071,322.49209 C 412.93471,318.36209 417.06471,315.81609 420.83471,313.17209 C 425.11871,310.16609 428.93971,307.03309 429.59971," +
                        "301.30509 C 425.08171,300.88709 424.03671,299.84209 423.36871,297.56509 C 421.10671,298.84009 419.02671,299.11309 416.67671,299.18309 C 414.63771,299.24509 412.39771," +
                        "299.15409 409.66171,299.43509 C 387.03471,301.75909 384.15971,326.69809 369.65671,340.83609 C 368.60171,341.86409 367.46071,342.82309 366.25471,343.72309 C 361.17571," +
                        "347.50809 354.94471,350.21309 349.21471,352.40309 C 339.93971,355.94709 331.12171,356.19909 322.41971,359.25809 C 316.03171,361.50309 309.54071,364.76009 304.29371," +
                        "368.35109 C 302.98171,369.24809 301.74571,370.16709 300.60871,371.09709 C 297.52971,373.61609 295.50871,376.41109 293.55371,379.28609 C 291.53771,382.24909 289.59071," +
                        "385.29709 286.62271,388.20909 C 281.81471,392.93009 263.84871,389.58609 257.52371,393.96409 C 256.81871,394.45109 256.25871,395.03609 255.87771,395.73809 C 259.32871," +
                        "397.30609 261.63371,396.34409 265.60371,396.78309 C 266.12471,400.55009 257.41271,402.78909 258.70071,404.52409 z").
                        attr({id: 'frontPath1',parent: 'layer1',fill: '#ffffff',stroke: 'none','stroke-width':'1','stroke-opacity':'1',"stroke-width": '0.25'}).
                        transform(transform1).
                        data('id', 'frontPath1');

                    var frontPath2 = r.path("M 395.78457,377.38405 C 396.05457,381.70605 398.56357,390.28105 400.77857,392.36505 C 396.44057,393.42005 388.96757,391.67705 387.05057,388.61705 C " +
                        "388.03557,384.19905 393.16157,380.16005 395.78457,377.38405 z").
                        attr({id: 'frontPath2',"clip-rule": 'evenodd',parent: 'layer1',fill: '#ffffff',"fill-rule": 'evenodd',"fill-opacity": '1','stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform2).
                        data('id', 'frontPath2');

                    var frontPath3 = r.path("M 258.70071,404.52409 C 268.84471,405.97709 275.01371,404.52409 283.15971,400.99109 C 290.09071,397.98509 296.78471,391.78809 304.96971,389.16109 C " +
                        "316.99071,385.30409 330.17271,389.16609 343.02271,389.93609 C 346.15171,390.12409 349.26071,390.12609 352.32971,389.79209 C 357.11671,386.85009 357.01771,375.84509 " +
                        "361.67671,374.83709 C 361.53971,390.27709 355.20971,399.52809 348.58971,408.48609 C 362.54171,406.02209 370.89071,397.95209 376.52971,387.17409 C 378.24071,383.90609 " +
                        "379.70171,380.39009 380.99371,376.70509 C 382.99071,378.23809 381.85871,382.90209 382.86271,385.42909 C 392.47271,380.07609 397.97571,367.85909 401.61971,355.50309 C " +
                        "405.83571,341.20309 407.56171,326.72309 410.28071,322.49209 C 412.93471,318.36209 417.06471,315.81609 420.83471,313.17209 C 425.11871,310.16609 428.93971,307.03309 429.59971," +
                        "301.30509 C 425.08171,300.88709 424.03671,299.84209 423.36871,297.56509 C 421.10671,298.84009 419.02671,299.11309 416.67671,299.18309 C 414.63771,299.24509 412.39771," +
                        "299.15409 409.66171,299.43509 C 387.03471,301.75909 384.15971,326.69809 369.65671,340.83609 C 368.60171,341.86409 367.46071,342.82309 366.25471,343.72309 C 361.17571," +
                        "347.50809 354.94471,350.21309 349.21471,352.40309 C 339.93971,355.94709 331.12171,356.19909 322.41971,359.25809 C 316.03171,361.50309 309.54071,364.76009 304.29371," +
                        "368.35109 C 302.98171,369.24809 301.74571,370.16709 300.60871,371.09709 C 297.52971,373.61609 295.50871,376.41109 293.55371,379.28609 C 291.53771,382.24909 289.59071," +
                        "385.29709 286.62271,388.20909 C 281.81471,392.93009 263.84871,389.58609 257.52371,393.96409 C 256.81871,394.45109 256.25871,395.03609 255.87771,395.73809 C 259.32871," +
                        "397.30609 261.63371,396.34409 265.60371,396.78309 C 266.12471,400.55009 257.41271,402.78909 258.70071,404.52409 z").
                        attr({id: 'frontPath1',parent: 'layer1',fill: color, "fill-opacity": 0.1, stroke: 'none','stroke-width':0.1,'stroke-opacity':'1',"stroke-width": '0.25'}).
                        transform(transform1).
                        data('id', 'frontPath1');

                    var frontPath4 = r.path("M 395.78457,377.38405 C 396.05457,381.70605 398.56357,390.28105 400.77857,392.36505 C 396.44057,393.42005 388.96757,391.67705 387.05057,388.61705 C " +
                        "388.03557,384.19905 393.16157,380.16005 395.78457,377.38405 z").
                        attr({id: 'frontPath2',"clip-rule": 'evenodd',parent: 'layer1',fill: color,"fill-rule": 'evenodd',"fill-opacity": 0.1,'stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform2).
                        data('id', 'frontPath2');

                    var eye = r.path("M 402.13957,308.79227 C 405.34257,311.57327 412.06357,309.34127 410.86257,303.80827 C 405.88557,303.39627 403.00257,305.08527 402.13957,308.79227 z").
                        attr({id: 'eye',"clip-rule": 'evenodd',parent: 'layer1',fill: color,"fill-rule": 'evenodd','stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform3).
                        data('id', 'eye');

                    var poil1 = r.path("M 424.47057,302.32627 C 423.61857,304.11327 421.98757,306.41727 421.98757,310.96627 C 421.98057,311.74727 421.39457,312.28227 421.38457,311.07827 C " +
                        "421.42857,306.63227 422.60557,304.71027 423.85557,302.18427 C 424.43657,301.14927 424.78657,301.57627 424.47057,302.32627 z").
                        attr({id: 'poil1',parent: 'layer1',fill: color,'stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform4).
                        data('id', 'poil1');

                    var poil2 = r.path("M 423.61257,301.65327 C 422.60757,303.35827 420.18757,306.46827 419.78757,311.00027 C 419.71357,311.77727 419.08157,312.25827 419.17757,311.05727 C " +
                        "419.61357,306.63327 421.54757,303.86427 423.01557,301.45727 C 423.68157,300.47827 423.99457,300.93427 423.61257,301.65327 z").
                        attr({id: 'poil2',parent: 'layer1',fill: color,'stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform4).
                        data('id', 'poil2');

                    var poil3 = r.path("M 422.83057,300.76127 C 421.68557,302.37527 417.96057,306.11027 417.18157,310.59327 C 417.04157,311.36027 416.37157,311.78827 416.56857,310.59927 C " +
                        "417.37457,306.22627 420.58557,302.78927 422.25057,300.51627 C 422.99657,299.59527 423.26957,300.07627 422.83057,300.76127 z").
                        attr({id: 'poil3',parent: 'layer1',fill: color,'stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform4).
                        data('id', 'poil3');

                    var poil4 = r.path("M 422.13257,299.76627 C 420.77257,301.20327 416.33157,305.96627 414.93157,310.29427 C 414.68357,311.03427 413.96057,311.36427 414.32357,310.21427 C " +
                        "415.73557,305.99827 419.62457,301.45727 421.59257,299.44127 C 422.46157,298.63427 422.66357,299.14927 422.13257,299.76627 z").
                        attr({id: 'poil4',parent: 'layer1',fill: color,'stroke-width': '0','stroke-opacity': '1'}).
                        transform(transform4).
                        data('id', 'poil4');

                    rset.attr({'transformG': 'translate(-252.94671,-292.65607)','fill-rule':'nonzero'});

                    rset.push(
                        backPath ,
                        frontPath1 ,
                        frontPath2 ,
                        frontPath3 ,
                        frontPath4 ,
                        eye ,
                        poil1 ,
                        poil2 ,
                        poil3 ,
                        poil4
                    );
                }
            }

            function gearLogo() {
                this.width=34;
                this.height=34;

                this.print = function(r, x, y, rset, color) {
                    var externalCircle = r.path("M26,48h-4c-1.654,0-3-1.346-3-3v-3.724c-1.28-0.37-2.512-0.881-3.681-1.527l-2.634,2.635     " +
                        "c-1.134,1.134-3.109,1.132-4.243,0l-2.829-2.828c-0.567-0.566-0.879-1.32-0.879-2.121s0.312-1.555,0.879-2.121l2.635-2.636     " +
                        "c-0.645-1.166-1.156-2.398-1.525-3.679H3c-1.654,0-3-1.346-3-3v-4c0-0.802,0.312-1.555,0.878-2.121     " +
                        "c0.567-0.566,1.32-0.879,2.122-0.879h3.724c0.37-1.278,0.88-2.511,1.526-3.679l-2.634-2.635c-1.17-1.17-1.17-3.072,0-4.242     " +
                        "l2.828-2.829c1.133-1.132,3.109-1.134,4.243,0l2.635,2.635C16.49,7.604,17.722,7.093,19,6.724V3c0-1.654,1.346-3,3-3h4     " +
                        "c1.654,0,3,1.346,3,3v3.724c1.28,0.37,2.512,0.881,3.678,1.525l2.635-2.635c1.134-1.132,3.109-1.134,4.243,0l2.829,2.828     " +
                        "c0.567,0.566,0.879,1.32,0.879,2.121s-0.312,1.555-0.879,2.121l-2.634,2.635c0.646,1.168,1.157,2.4,1.526,3.68H45     " +
                        "c1.654,0,3,1.346,3,3v4c0,0.802-0.312,1.555-0.878,2.121s-1.32,0.879-2.122,0.879h-3.724c-0.37,1.28-0.881,2.513-1.526,3.68     " +
                        "l2.634,2.635c1.17,1.17,1.17,3.072,0,4.242l-2.828,2.829c-1.134,1.133-3.109,1.133-4.243,0L32.68,39.75     " +
                        "c-1.168,0.646-2.401,1.156-3.679,1.526V45C29,46.654,27.655,48,26,48z M15.157,37.498c0.179,0,0.36,0.048,0.521,0.146     " +
                        "c1.416,0.866,2.949,1.502,4.557,1.891C20.684,39.644,21,40.045,21,40.507V45c0,0.552,0.449,1,1,1h4c0.551,0,1-0.448,1-1v-4.493     " +
                        "c0-0.462,0.316-0.863,0.765-0.972c1.606-0.389,3.139-1.023,4.556-1.89c0.396-0.241,0.902-0.18,1.229,0.146l3.178,3.179     " +
                        "c0.375,0.374,1.039,0.376,1.415,0l2.828-2.829c0.39-0.39,0.39-1.024,0-1.414l-3.179-3.179c-0.327-0.326-0.387-0.835-0.146-1.229     " +
                        "c0.865-1.414,1.5-2.947,1.889-4.556c0.108-0.449,0.51-0.766,0.972-0.766H45c0.267,0,0.519-0.104,0.708-0.293     " +
                        "C45.896,26.518,46,26.267,46,25.999v-4c0-0.552-0.449-1-1-1h-4.493c-0.462,0-0.864-0.316-0.972-0.766     " +
                        "c-0.388-1.607-1.023-3.14-1.889-4.556c-0.241-0.394-0.181-0.901,0.146-1.229l3.179-3.179c0.186-0.187,0.293-0.444,0.293-0.707     " +
                        "s-0.107-0.521-0.293-0.707l-2.829-2.828c-0.378-0.377-1.037-0.377-1.415,0l-3.179,3.179c-0.326,0.328-0.833,0.389-1.229,0.146     " +
                        "c-1.413-0.864-2.945-1.5-4.554-1.889C27.317,8.356,27,7.955,27,7.493V3c0-0.552-0.449-1-1-1h-4c-0.551,0-1,0.448-1,1v4.493     " +
                        "c0,0.462-0.316,0.863-0.765,0.972c-1.606,0.388-3.139,1.023-4.556,1.889c-0.395,0.241-0.902,0.181-1.228-0.146l-3.179-3.179     " +
                        "c-0.378-0.377-1.037-0.377-1.415,0L7.03,9.857c-0.39,0.39-0.39,1.024,0,1.414l3.179,3.179c0.327,0.326,0.387,0.835,0.146,1.229     " +
                        "c-0.866,1.416-1.501,2.949-1.889,4.555c-0.108,0.449-0.51,0.766-0.972,0.766H3c-0.267,0-0.519,0.104-0.708,0.293     " +
                        "C2.104,21.48,2,21.731,2,21.999v4c0,0.552,0.449,1,1,1h4.493c0.462,0,0.864,0.316,0.972,0.766     " +
                        "c0.389,1.608,1.024,3.141,1.889,4.555c0.241,0.394,0.181,0.901-0.146,1.229l-3.179,3.18c-0.186,0.187-0.293,0.444-0.293,0.707     " +
                        "s0.107,0.521,0.293,0.707l2.829,2.828c0.377,0.377,1.037,0.377,1.415,0l3.178-3.179C14.643,37.598,14.898,37.498,15.157,37.498z").
                        attr({parent: 'Expanded','stroke-width': '0','stroke-opacity': '1','fill': color}).
                        transform("s0.6t-10,-10t"+x*5/3+","+y*5/3).
                        data('id', 'path_d');

                    var internalCircle = r.path("M24,34c-5.514,0-10-4.486-10-10s4.486-10,10-10s10,4.486,10,10S29.515,34,24,34z M24,16c-4.411,0-8,3.589-8,8     " +
                        "s3.589,8,8,8s8-3.589,8-8S28.412,16,24,16z").
                        attr({parent: 'Expanded','stroke-width': '0','stroke-opacity': '1','fill': color}).
                        transform("s0.6t-10,-10t"+x*5/3+","+y*5/3).
                        data('id', 'path_e');

                    rset.push(externalCircle,internalCircle);
                }
            }

            function companyLogo(company, product) {
                this.logoSet = null;
                this.cpyLogo = null;
                if (company==="Tibco") {
                    this.cpyLogo = new tibcoLogo();
                    this.logoWidth = this.cpyLogo.width;
                    this.logoHeight = this.cpyLogo.height;
                } else if (company==="MariaDB Foundation") {
                    this.cpyLogo = new mariadbLogo();
                    this.logoWidth = this.cpyLogo.width;
                    this.logoHeight = this.cpyLogo.height;
                } else if (company==="Pivotal" && product==="RabbitMQ") {
                    this.cpyLogo = new rabbitmqLogo();
                    this.logoWidth = this.cpyLogo.width;
                    this.logoHeight = this.cpyLogo.height;
                } else {
                    this.cpyLogo = new gearLogo();
                    this.logoWidth = this.cpyLogo.width;
                    this.logoHeight = this.cpyLogo.height;
                }

                this.print= function(r,x,y,color) {
                    if (this.cpyLogo) {
                        this.logoSet = r.set();
                        this.cpyLogo.print(r,x,y,this.logoSet,color);
                    }
                }
            }

            var txtFont = params.containerHat_txtFont,
                logo = new companyLogo(company_, product_),
                product = product_,
                component = component_;

            this.width  = logo.logoWidth + Math.max((product ? product.width(txtFont):0),(component ? component.width(txtFont):0));
            this.height = Math.max(logo.logoHeight, (product ? product.height(txtFont) + ((logo.logoHeight<10)?10:0): 0) + (component ? component.height(txtFont):0));
            this.textSet = null;
            this.hatSet = null;
            this.color  = null;

            this.move = function(r,newX,newY) {
                this.hatSet.remove();
                this.print(r,newX,newY,this.color);
            };

            this.print = function (r,x,y,color_) {
                this.color = color_;
                this.textSet = r.set();
                this.hatSet = r.set();
                logo.print(r,x-this.width/3,y,this.color);
                if (logo.logoSet)
                    this.hatSet.push(logo.logoSet);

                if (product != null) {
                    var productTxt = r.text(x-this.width/3+logo.logoWidth+product.width(txtFont)/2,
                                            y+((logo.logoHeight/3<10)?10:logo.logoHeight/3), product).attr(txtFont);
                    this.textSet.push(productTxt);
                }
                if (component != null) {
                    var componentTxt = r.text(x-this.width/3+logo.logoWidth+component.width(txtFont)/2,
                                              y+((logo.logoHeight/3<10)?10:logo.logoHeight/3)+(product ? product.height(txtFont):0), component).attr(txtFont);
                    this.textSet.push(componentTxt);
                }

                this.textSet.attr({fill:this.color})
                this.hatSet.push(this.textSet)
                this.hatSet.attr({stroke:'none','stroke-width':'1','stroke-opacity':'1'});
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
            }
        }

        return hat;
    }
);