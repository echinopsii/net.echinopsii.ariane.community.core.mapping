
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
        'raphael',
        'taitale-cylinder'
    ],
    function (Raphael, cylinder) {

        var rsr = Raphael('rsr', '1000', '800');

        var mbus = new cylinder(710,371,30,200, "Multicast IP : 239.69.69.69");
        mbus.print(rsr);

        //var mbusFT = mbus.getFreeTransform();
        //mbusFT.attrs.rotate = -45;
        //mbusFT.apply();
    });