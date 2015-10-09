require.config({
    baseUrl: '../../../js',
    paths: {
        'jquery': 'jquery/jquery-1.9.1',
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
        var rsr = Raphael('rsr', '640', '480');
        var group_a = rsr.set();

        group_a.attr({'name': 'group_a'});

        var layer1 = rsr.set();

        var rect1944 = rsr.rect(162.858066, 202.039343, 49.309592, 11.728805);
        rect1944.attr(
            {
                id: 'rect1944',x: '162.858066',y: '202.039343',"stroke-dashoffset": '0',"stroke-miterlimit": '4',"stroke-linejoin": 'round',
                "stroke-width": '3.54',"fill-rule": 'nonzero',fill: '315-:0-:100','fill-opacity': '',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'rect1944');

        var rect1946 = rsr.rect(1143.562247, 1332.706561, 117.41212, 51.303547);
        rect1946.attr(
            {
                id: 'rect1946',x: '1143.562247',y: '1332.706561',"stroke-dashoffset": '0',"stroke-miterlimit": '4',"stroke-linejoin": 'round',
                "stroke-width": '3.54',"fill-rule": 'nonzero',fill: '315.17004086475-:0-:100','fill-opacity': '',parent: 'group_a','stroke-opacity': '1'
            });
        rect1946.transform("m0.0669183366880715,-0.20930216821087896,0,0.228615865111351,135.64326317103905,136.71220683401407").data('id', 'rect1946');

        var rect10242 = rsr.path("m162.86087,201.848801l-0.150757,0.200043l0,11.73085l0.150757,0.200043l49.291626,0l0.145554,-0.200043l0,-11.73085l-0.145554,-0.200043l-49.291626,0zm0.145554,0.407227l48.995316,0l0,11.316483l-48.995316,0l0,-11.316483zm7.615723,-25.026291l-0.041595,0.121445l-7.818451,24.490479l0.270309,0.164322l7.782074,-24.369019l49.073303,0.007141l-0.015594,11.516525l-7.849655,24.46904l0.270325,0.164322l7.865234,-24.519058l0.010406,-0.042862l0,-0.042862l0.010391,-11.952332l-49.463181,-0.007141l-0.093567,0zm49.276031,0.078583l-7.917221,24.776245l0.270309,0.164322l7.917236,-24.776245l-0.270325,-0.164322z");
        rect10242.attr(
            {
                id: 'rect10242',"stroke-dashoffset": '0',"stroke-miterlimit": '4',"stroke-linejoin": 'bevel',"stroke-linecap": 'square',"stroke-width": '1.77',
                fill: 'black',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'rect10242');

        layer1.attr(
            {
                'id': 'layer1','parent': 'group_a','name': 'layer1'
            });

        var g9312 = rsr.set();
        var path4749 = rsr.path("m170.720917,177.43692l-7.875641,24.604782l49.322815,0l7.875641,-24.604782l-49.322815,0z");
        path4749.attr(
            {
                id: 'path4749',"stroke-dashoffset": '0',"stroke-miterlimit": '4',"stroke-linejoin": 'round',"stroke-width": '3.54',"fill-rule": 'nonzero',fill: '45-:0-:100','fill-opacity': '',
                parent: 'group_a','stroke-opacity': '1'}).data('id', 'path4749');

        var rect1948 = rsr.path("m170.720917,177.43692l-7.875641,24.604782l49.322815,0l7.875641,-24.604782l-49.322815,0zm40.423096,1.185944l4.028778,4.129379l-6.65921,4.093643l0.743378,-2.329025l-15.335388,0l1.122864,-3.52211l15.340607,0l0.758972,-2.371887zm-33.779495,4.665192l-0.753769,2.371887l15.335403,0l-1.128067,3.52211l-15.335403,0l-0.748566,2.329025l-4.0392,-4.093643l6.669601,-4.129379zm30.790375,4.672333l4.028793,4.129379l-6.659195,4.093658l0.743362,-2.336166l-15.335388,0l1.128052,-3.522125l15.335403,0l0.758972,-2.364746zm-33.774277,4.665192l-0.758972,2.364746l15.335403,0l-1.122864,3.529266l-15.340607,0l-0.743378,2.329025l-4.044388,-4.093658l6.674805,-4.129379z");
        rect1948.attr(
            {
                id: 'rect1948',"stroke-dashoffset": '0',"stroke-miterlimit": '4',"stroke-linejoin": 'round',"stroke-width": '3.54',"fill-rule": 'nonzero',
                fill: '51.716760051751-:0-:100','fill-opacity': '',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'rect1948');

        g9312.attr(
            {
                'id': 'g9312','parent': 'group_a','name': 'g9312'
            });

        var g9298 = rsr.set();
        g9298.attr(
            {
                'id': 'g9298','parent': 'group_a','name': 'g9298'
            });

        var g9282 = rsr.set();
        var path7440 = rsr.path("m170.795074,187.78981l6.68631,-4.144974");
        path7440.attr(
            {
                id: 'path7440',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9228)',"fill-rule": 'evenodd',
                "fill-opacity": '0.75',fill: 'none',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path7440');

        var path7442 = rsr.path("m176.623978,186.072449l15.388245,-0.020203");
        path7442.attr(
            {
                id: 'path7442',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9230)',"fill-rule": 'evenodd',"fill-opacity": '0.75',
                fill: 'none',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path7442');

        g9282.attr({'id': 'g9282','parent': 'group_a','name': 'g9282'});

        var g9294 = rsr.set();

        var path7444 = rsr.path("m194.181259,184.711456l1.059219,-3.311768l15.331741,-0.003769l0.682419,-2.194626l3.755264,3.873199");
        path7444.attr(
            {
                id: 'path7444',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9226)',"fill-rule": 'evenodd',
                "fill-opacity": '0.75',fill: 'none',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path7444');

        var path7446 = rsr.path("m209.255859,184.516296l-0.747467,2.343887l0.747162,-0.470139l0.000305,-1.873749z");
        path7446.attr(
            {
                id: 'path7446',"stroke-miterlimit": '4',"stroke-width": '3.54',"fill-rule": 'evenodd',fill: '326.44982026694-:0-:100',
                'fill-opacity': '',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path7446');

        g9294.attr({'id': 'g9294','parent': 'group_a','name': 'g9294'});

        var g9286 = rsr.set();
        var path9238 = rsr.path("m167.799149,197.125443l6.717499,-4.152115");
        path9238.attr(
            {
                id: 'path9238',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9246)',"fill-rule": 'evenodd',
                "fill-opacity": '0.75',fill: 'none',parent: 'group_a','stroke-opacity': '1'}).data('id', 'path9238');

        var path9240 = rsr.path("m173.628036,195.386642l15.38826,0.001221");
        path9240.attr(
            {
                id: 'path9240',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9248)',"fill-rule": 'evenodd',
                "fill-opacity": '0.75',fill: 'none',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path9240');

        g9286.attr(
            {
                'id': 'g9286','parent': 'group_a','name': 'g9286'
            });

        var g9290 = rsr.set();

        var path9242 = rsr.path("m191.185318,194.047073l1.059235,-3.311752l15.331741,-0.003769l0.682419,-2.194626l3.755264,3.873184");
        path9242.attr(
            {
                id: 'path9242',"stroke-miterlimit": '4',"stroke-width": '3.54',stroke: 'url(#linearGradient9250)',"fill-rule": 'evenodd',
                "fill-opacity": '0.75',fill: 'none',parent: 'group_a','stroke-opacity': '1'
            }).data('id', 'path9242');

        var path9244 = rsr.path("m206.259933,193.851929l-0.747482,2.343872l0.747177,-0.42012l0.000305,-1.923752z");
        path9244.attr(
            {
                id: 'path9244',"stroke-miterlimit": '4',"stroke-width": '3.54',"fill-rule": 'evenodd',fill: '326.44983511697-:0-:100','fill-opacity': '',
                parent: 'group_a','stroke-opacity': '1'}).data('id', 'path9244');

        g9290.attr({'id': 'g9290','parent': 'group_a','name': 'g9290'}); var rsrGroups = [group_a,layer1,g9312,g9298,g9282,g9294,g9286,g9290];
    });