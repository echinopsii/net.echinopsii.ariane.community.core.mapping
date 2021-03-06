
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

        function mintLogo(x,y) {
            var gray_border = rsr.path("M 331.79042,510.93347 C 310.5003,510.93347 292.02462,494.79355 292.02462,473.52251 " +
                "L 292.0055,451.53024 L 292.0055,444.9442 L 278.50774,444.9442 L 278.50774,408.2929 L 335.4281,408.56096" +
                " L 353.23367,408.59927 C 374.54293,408.59927 392.99946,424.71998 392.99946,446.01013 L 392.99946,510.93" +
                "347 L 331.79042,510.93347 L 331.79042,510.93347 L 331.79042,510.93347 z").
                attr({opacity: '0.26000001',fill: '#000000',"fill-opacity": '1',
                    "fill-rule": 'evenodd',stroke: 'none','stroke-width':'1','stroke-opacity':'1',"stroke-width": '1',
                    "stroke-linecap": 'butt',"stroke-linejoin": 'miter',marker: 'none',"marker-start": 'none',
                    "marker-mid": 'none',"marker-end": 'none',"stroke-miterlimit": '4',"stroke-dasharray": 'none',
                    "stroke-dashoffset": '0',"stroke-opacity": '1',visibility: 'visible',display: 'inline',
                    overflow: 'visible',filter: 'url(#filter3573)',"enable-background": 'accumulate'}).
                transform("t"+x+","+y+"t-475,-360m6.5972926,0,0,6.5972926,-735.85177,-2072.0441 m1.0429295,0,0,1.0429295,-163.96565,-107.54293s0.06");
            var lm = rsr.path("M 193.35561,347.16391 C 189.33282,347.16391 185.75845,348.65744 182.88651,351.55859" +
                " C 180.016,354.45819 178.50881,358.07812 178.50882,362.16333 L 178.50882,382.81318 L 187.87503,382.8131" +
                "8 L 187.87503,362.16333 C 187.87503,360.59753 188.38594,359.37319 189.50392,358.24385 C 190.62558,357.1" +
                "109 191.81335,356.59801 193.35561,356.59801 C 194.93019,356.59801 196.10014,357.10832 197.22425,358.243" +
                "85 C 198.34224,359.37319 198.85316,360.59753 198.85316,362.16333 L 198.85316,382.81318 L 208.21936,382." +
                "81318 L 208.21936,362.16333 C 208.21936,360.59753 208.73028,359.37319 209.84828,358.24385 C 210.97238,3" +
                "57.10832 212.14233,356.59801 213.71694,356.59801 C 215.25921,356.59801 216.44695,357.1109 217.56859,358" +
                ".24385 C 218.68658,359.37319 219.1975,360.59753 219.1975,362.16333 L 219.2484,386.29164 C 219.2484,391." +
                "43866 214.34681,395.77655 208.04968,395.77655 L 177.84708,395.6578 C 172.98759,395.6578 168.83719,390.9" +
                "2784 168.83719,384.84932 L 168.83719,339.4123 L 159.98003,339.4123 L 159.98003,386.59697 C 159.98003,39" +
                "1.60325 161.81195,395.99339 165.37578,399.54343 C 168.42187,402.5501 172.08521,404.49909 176.2012,404.9" +
                "3927 L 209.8313,404.93919 C 214.99906,404.93919 219.53918,403.01766 223.20189,399.28879 L 223.21888,399" +
                ".28879 C 226.33398,396.08829 228.15508,392.25338 228.59765,387.93748 L 228.56371,362.16333 C 228.56371," +
                "358.07812 227.05657,354.45819 224.18602,351.55859 C 221.31411,348.65744 217.73971,347.16391 213.71694,3" +
                "47.16391 C 209.76825,347.16391 206.31314,348.63225 203.5193,351.32099 C 200.73363,348.63668 197.30267,3" +
                "47.16391 193.35561,347.16391 z").
                attr({fill: '#ffffff',"fill-opacity": '1',"fill-rule": 'evenodd',
                    stroke: 'none','stroke-width':'1','stroke-opacity':'1',"stroke-width": '1',"stroke-linecap": 'butt',
                    "stroke-linejoin": 'miter',marker: 'none',"marker-start": 'none',"marker-mid": 'none',"marker-end": 'none',
                    "stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1',visibility: 'visible',
                    display: 'inline',overflow: 'visible',"enable-background": 'accumulate'}).
                transform("t"+x+","+y+"t-525,-363m6.5972926,0,0,6.5972926,-735.85177,-2072.0441s0.06");
            var white_border = rsr.path("M 182.74092,423.66488 C 161.4508,423.66488 142.97513,407.52496 142.97513,386.25392 " +
                "L 142.956,360.42629 L 142.956,353.84026 L 129.45825,353.84026 L 129.45825,321.02431 L 186.3786,321.2923" +
                "7 L 204.18418,321.33068 C 225.49343,321.33068 243.94996,337.45139 243.94996,358.74153 L 243.94996,423.6" +
                "6488 L 182.74092,423.66488 L 182.74092,423.66488 L 182.74092,423.66488 z M 235.27688,406.24921 C 235.27" +
                "688,392.10579 235.27688,358.74153 235.27688,358.74153 C 235.27688,342.86978 221.35795,330.00376 204.184" +
                "18,330.00376 L 186.35947,330.00376 L 186.35947,329.96546 L 138.15044,329.75481 L 138.15044,345.16718 C " +
                "138.15044,345.16718 140.08675,345.16718 143.55081,345.16718 C 147.21721,345.16718 151.62901,348.61265 1" +
                "51.62901,353.66876 L 151.66732,386.23492 C 151.66732,402.10668 165.58632,414.97268 182.74092,414.97268 " +
                "L 225.09557,414.97268 C 230.52546,414.97268 235.27688,411.53397 235.27688,406.24921 z").
                attr({fill: '#ffffff',"fill-opacity": '1',"fill-rule": 'evenodd',
                    stroke: 'none','stroke-width':'1','stroke-opacity':'1',"stroke-width": '1',"stroke-linecap": 'butt',
                    "stroke-linejoin": 'miter',marker: 'none',"marker-start": 'none',"marker-mid": 'none',
                    "marker-end": 'none',"stroke-miterlimit": '4',"stroke-dashoffset": '0',"stroke-opacity": '1',
                    visibility: 'visible',display: 'inline',overflow: 'visible',"enable-background": 'accumulate'}).
                transform("t"+x+","+y+"t-478,-364m6.5972926,0,0,6.5972926,-735.85177,-2072.0441s0.06");
//            rsr.rect(x,y,41,41);
        }

        mintLogo(100,100)
    });