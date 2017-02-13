
require.config({
    baseUrl: '../../../js',
    paths: {
        'jquery': 'jquery/jquery-1.9.1',
        'prime-ui': 'primeui/primeui-0.9.6',
        'eve': 'raphael/eve',
        'raphael-core': 'raphael/raphael.core',
        'raphael-svg': 'raphael/raphael.svg',
        'raphael-vml': 'raphael/raphael.vml',
        'raphael': 'raphael/raphael.amd',
        'raphael.free_transform': 'raphael/raphael.free_transform',
        'taitale-helper': 'taitale/tools/helper',
        'taitale-cylinder': 'taitale/tools/cylinder'
    }
});

requirejs (
    [
        'raphael'
    ],
    function (Raphael) {

        function ellipseBubbleForRect(x, y, width, height) {
            this.x = x + width/2;
            this.y = y + height/2;
            this.p = (height < width) ? height/2 : width/2; // semi latus rectum
            this.c = (width > height) ? width/2 : height/2; // distance from center to focal point
            this.hrad = 3*( -p + Math.sqrt( Math.pow(p,2) + ( 4*(Math.pow(c,2) ) ) ) )/4; // horizontal radius
            this.vrad = Math.sqrt(this.p*this.hrad);                                      // vertical radius
            this.rotate = (height > width);

            this.sminor = (hrad < vrad) ? hrad : vrad; // semi minor axis
            this.smajor = (hrad < vrad) ? vrad : hrad; // semi major axis
            this.excent = Math.sqrt(Math.pow(this.smajor,2)-Math.pow(this.sminor,2))/this.smajor; // excentricity

            this.bubble = null;
            this.container = null;

            this.print = function(r) {
                this.container = r.rect(x, y, width, height);
                this.bubble = r.ellipse(this.x, this.y, this.hrad, this.vrad);
                if (rotate) this.bubble.transform("r90");
            };

            this.placeRect = function(r, rad, width, height) {
                var cos = parseFloat(Math.cos(rad).toFixed(10)), sin = parseFloat(Math.sin(rad).toFixed(10)),
                    ro = this.sminor/Math.sqrt(1 - Math.pow(this.excent,2)*Math.pow(cos,2)),
                    x = (!this.rotate) ? this.x + width/2 +ro * Math.cos(rad) : this.x + width/2 + ro * Math.sin(rad),
                    y = (!this.rotate) ? this.y + height/2 + ro * Math.sin(rad) : this.y + height/2 + ro * Math.cos(rad);

                if (cos < 0) {
                    if (sin < 0) {
                        if (this.rotate) {
                            x -= width;
                            y -= height;
                        } else {
                            x -= width;
                            y -= height;
                        }
                    } else if (sin > 0) {
                        if (this.rotate)  y -= height;
                        else x -= width;
                    } else {
                        if (this.rotate) {
                            x -= width/2;
                            y -= height;
                        } else {
                            x -= width;
                            y -= height/2;
                        }
                    }
                } else if (cos > 0) {
                    if (sin < 0) {
                        if (this.rotate) x -= width;
                        else y -= height;
                    } else if (sin == 0) {
                        if (this.rotate) x -= width/2;
                        else y -= height/2;
                    }
                } else {
                    if (sin > 0) {
                        if (this.rotate) y -= height/2;
                        else x -= width/2;
                    } else {
                        if (this.rotate) {
                            x -= width;
                            y -= height/2;
                        } else {
                            x -= width/2;
                            y -= height;
                        }
                    }
                }

                r.rect(x-width/2, y-height/2, width, height);
            };

            return this;
        }

        var rsr = Raphael('rsr', '5000', '800');
        var ell1 = ellipseBubbleForRect(300, 300, 400, 100);
        ell1.print(rsr);
        ell1.placeRect(rsr, 0, 100, 20);
        ell1.placeRect(rsr, Math.PI/4, 100, 20);
        ell1.placeRect(rsr, Math.PI/2, 100, 20);
        ell1.placeRect(rsr, 3*Math.PI/4, 100, 20);
        ell1.placeRect(rsr, Math.PI, 100, 20);
        ell1.placeRect(rsr, 5*Math.PI/4, 100, 20);
        ell1.placeRect(rsr, 3*Math.PI/2, 100, 20);
        ell1.placeRect(rsr, 7*Math.PI/4, 100, 20);

        var ell2 = ellipseBubbleForRect(1200, 300, 100, 300);
        ell2.print(rsr);
        ell2.placeRect(rsr, 0, 100, 20);
        ell2.placeRect(rsr, Math.PI/4, 100, 20);
        ell2.placeRect(rsr, Math.PI/2, 100, 20);
        ell2.placeRect(rsr, 3*Math.PI/4, 100, 20);
        ell2.placeRect(rsr, Math.PI, 100, 20);
        ell2.placeRect(rsr, 5*Math.PI/4, 100, 20);
        ell2.placeRect(rsr, 3*Math.PI/2, 100, 20);
        ell2.placeRect(rsr, 7*Math.PI/4, 100, 20);
    });