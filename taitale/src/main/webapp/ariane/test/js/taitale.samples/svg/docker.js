
require.config({
    baseUrl: '../../../js',
    paths: {
        'jquery': 'jquery/jquery-1.9.1',
        'prime-ui': 'primeui/primeui-0.9.6',
        'eve': 'raphael/eve',
        'raphael-core': 'raphael/raphael.core',
        'raphael-svg': 'raphael/raphael.svg',
        'raphael-vml': 'raphael/raphael.vml',
        'raphael': 'raphael/raphael.amd'
    }
});

requirejs (
    [
        'raphael'
    ],
    function (Raphael) {

        var rsr = Raphael('rsr', '1200', '1200');

        function dockerLogo(x,y) {
            var outline_7_ = rsr.path("M242.133,168.481h47.146v48.194h23.837 c11.008,0,22.33-1.962,32.755-5.494c5.123-" +
                "1.736,10.872-4.154,15.926-7.193c-6.656-8.689-10.053-19.661-11.054-30.476 c-1.358-14.71,1.609-33.855," +
                "11.564-45.368l4.956-5.732l5.905,4.747c14.867,11.946,27.372,28.638,29.577,47.665 c17.901-5.266,38.921-" +
                "4.02,54.701,5.088l6.475,3.734l-3.408,6.652c-13.345,26.046-41.246,34.113-68.524,32.687 c-40.817,101.663-" +
                "129.68,149.794-237.428,149.794c-55.666,0-106.738-20.81-135.821-70.197l-0.477-0.807l-4.238-8.621 C4.195," +
                "271.415,0.93,247.6,3.145,223.803l0.664-7.127h40.315v-48.194h47.143v-47.145h94.292V74.191h56.574V168.481z").
                attr({id: 'outline_7_',"fill-rule": 'evenodd',"clip-rule": 'evenodd','stroke-width': '0',
                    'stroke-opacity': '1','fill': '#000000'}).
                transform("t"+x+","+y+"t-210,-203s0.10");
            rsr.rect(x,y,41,41);
        }

        dockerLogo(100,100)
    });