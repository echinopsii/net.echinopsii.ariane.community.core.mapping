
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

        function ellipse(x, y, hrad, vrad) {
            this.x = x;
            this.y = y;
            this.hrad = hrad;
            this.vrad = vrad;

            this.sminor = (hrad < vrad) ? hrad : vrad;
            this.smajor = (hrad < vrad) ? vrad : hrad;
            this.excent = Math.sqrt(Math.pow(this.smajor,2)-Math.pow(this.sminor,2))/this.smajor;

            this.print = function(r) {
                r.ellipse(this.x, this.y, this.hrad, this.vrad);
            };

            this.placePoint = function(r, rad) {
                var ro = this.sminor/Math.sqrt(1 - Math.pow(this.excent,2)*Math.pow(Math.cos(rad),2));
                var x = this.x + ro * Math.cos(rad); y = this.y + ro * Math.sin(rad);
                r.circle(x, y, 5);
            };

            return this;
        }

        var rsr = Raphael('rsr', '1000', '800');
        var ell = ellipse(100, 100, 50, 30);
        ell.print(rsr);
        ell.placePoint(rsr, 0);
        ell.placePoint(rsr, Math.PI/4);
        ell.placePoint(rsr, Math.PI/2);
        ell.placePoint(rsr, 3*Math.PI/4);
        ell.placePoint(rsr, Math.PI);
        ell.placePoint(rsr, 5*Math.PI/4);
        ell.placePoint(rsr, 3*Math.PI/2);
        ell.placePoint(rsr, 7*Math.PI/4);
    });