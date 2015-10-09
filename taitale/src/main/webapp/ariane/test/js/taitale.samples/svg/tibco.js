

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
        var rsr = Raphael('rsr', '800', '800');

        function tibcoLogo(x,y,rset) {
            this.width=90;
            this.height=30;

            var rightPath = rsr.path(
                "M 97,524 C 97,524 110,511 95,487 C 95,487 81,466 69,455 L 2,455 " +
                    "L 2,609 L 28,609 C 35,601 74,555 79,548 C 79,548 94,531 97,524");
            rightPath.transform("t-2,-453s0.2t-200,-317t"+x*5+","+y*5)
            rset.push(rightPath);

            var middlePath = rsr.path(
                "M 147,532 C 147,532 152,516 135,494 C 135,494 126,483 94,455 L 75,455 " +
                    "C 78,459 91,471 99,482 C 99,482 118,504 105,527 C 105,527 98,540 41,609 " +
                    "L 101,609 C 106,602 129,569 132,562 C 132,562 143,545 147,532");
            middlePath.transform("t-2,-453s0.2t-200,-317t-160,0t"+x*5+","+y*5)
            rset.push(middlePath);

            var leftPath = rsr.path(
                "M 137,487 C 137,487 161,510 154,536 C 154,536 153,549 114,609 L 161,609" +
                    "L 161,455 L 100,455 C 105,460 128,478 137,487");
            leftPath.transform("t-2,-453s0.2t-200,-317t-295,0t"+x*5+","+y*5)
            rset.push(leftPath);
        }

        function companyLogo(x,y,company) {
            this.logoSet = rsr.set();
            if (company==="Tibco") {
                var tibLogo = new tibcoLogo(x,y,this.logoSet);
                this.logoWidth=tibLogo.width;
                this.logoHeight=tibLogo.height;
            } else {
                this.logoWidth=0;
                this.logoHeight=0;
            }
        }

        function componentType(x,y,color,company,product,component) {
            var logo = new companyLogo(x,y,company);
            var componentLogo = rsr.set();
            componentLogo.push(logo.logoSet);

            var productTxt = rsr.text(x+logo.logoWidth, y+logo.logoHeight/3, product).attr({'font-size': '11px', 'font-family': 'Arial', 'font-weight': 'bold'});
            var componentTxt = rsr.text(x+logo.logoWidth, y+2*logo.logoHeight/3, component).attr({'font-size': '11px', 'font-family': 'Arial', 'font-weight': 'bold'});

            componentLogo.push(productTxt);
            componentLogo.push(componentTxt);
            componentLogo.attr({fill: color,'fill-rule':'nonzero',stroke:'none','stroke-width':'1','stroke-opacity':'1'});
        }

        componentType(0,0,'blue','Tibco','Tibco Rendez Vous','RVRD');
    });
