
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

        function fedoraLogo(x,y) {
            var voice = rsr.path("M 266.62575,133.50613 C 266.62575,59.98128 207.02222,0.37583 133.49792,0.37583 C " +
                "60.00668,0.37583 0.42639,59.93123 0.37425,133.41225 L 0.37425,236.4333 C 0.4138,253.11763 13.94545," +
                "266.62417 30.64027,266.62417 L 133.55192,266.62417 C 207.05167,266.59532 266.62575,207.01142 " +
                "266.62575,133.50613");
            voice.attr({fill: '#000000','stroke-width': '0','stroke-opacity': '1'});
            voice.transform("t"+x+","+y+"t-113,-113s0.14");
            var free1 = rsr.path("M 139.6074,127.52923 L 139.6074,189.87541 C 139.6074,224.37943 111.63203,252.35541 " +
                "77.12679,252.35541 C 71.89185,252.35541 68.1703,251.7644 63.32444,250.49771 C 56.25849,248.64859 " +
                "50.48398,242.85518 50.48158,236.1166 C 50.48158,227.97147 56.39394,222.0467 65.23187,222.0467 C " +
                "69.43824,222.0467 70.96454,222.85435 77.12679,222.85435 C 95.3184,222.85435 110.07443,208.11916 " +
                "110.10634,189.92756 L 110.10634,161.27099 C 110.10634,158.70324 108.01971,156.62274 105.44767,156.62274 " +
                "L 83.78246,156.61846 C 75.71034,156.61846 69.18845,150.18003 69.18845,142.0858 C 69.18414,133.94124 " +
                "75.77725,127.52923 83.93653,127.52923");
            free1.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'});
            free1.transform("t"+x+","+y+"t-82,-162s0.14");
            var free2 = rsr.path("M 139.6074,127.52923 L 139.6074,189.87541 C 139.6074,224.37943 111.63203,252.35541 " +
                "77.12679,252.35541 C 71.89185,252.35541 68.1703,251.7644 63.32444,250.49771 C 56.25849,248.64859 " +
                "50.48398,242.85518 50.48158,236.1166 C 50.48158,227.97147 56.39394,222.0467 65.23187,222.0467 C " +
                "69.43824,222.0467 70.96454,222.85435 77.12679,222.85435 C 95.3184,222.85435 110.07443,208.11916 " +
                "110.10634,189.92756 L 110.10634,161.27099 C 110.10634,158.70324 108.01971,156.62274 105.44767,156.62274 " +
                "L 83.78246,156.61846 C 75.71034,156.61846 69.18845,150.18003 69.18845,142.0858 C 69.18414,133.94124 " +
                "75.77725,127.52923 83.93653,127.52923");
            free2.attr({fill: '#ffffff','stroke-width': '0','stroke-opacity': '1'});
            free2.transform("t"+x+","+y+"t-73,-176r180s0.14");
            rsr.rect(x,y,41,41);
        }

        fedoraLogo(100,100)
    });