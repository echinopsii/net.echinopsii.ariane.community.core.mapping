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
        var r = Raphael('rsr', '1200', '1200');
        function freebsdLogo(x,y) {
            r.path("m878.6 605.07c8.07 8.06-14.297 43.508-18.08 47.29-3.78 " +
                "3.774-13.383.302-21.448-7.764-8.07-8.06-11.541-17.669-7.761-21.448 3.781-3.783 39.22-26.14 47.29-18.08").
                attr({fill: '#fff',"fill-opacity": '.85','stroke-width': '0','stroke-opacity': '1'}).
                transform("t"+x+","+y+"t-821,-621s0.27");
            r.path("m765.47 613.6c-12.314-6.987-29.839-14.761-35.414-9.187-5.648 5.647 2.41 23.566 9.47 35.904 " +
                "6.283-10.924 15.225-20.12 25.944-26.717").
                attr({fill: '#fff',"fill-opacity": '.85','stroke-width': '0','stroke-opacity': '1'}).
                transform("t"+x+","+y+"t-742,-616s0.27");
            r.path("m866.29 649.58c1.135 3.848.928 7.02-.908 8.86-4.299 " +
                "4.298-15.901-.276-26.362-10.233-.729-.653-1.454-1.333-2.167-2.046-3.78-3.784-6.725-7.813-8.607-11.522-" +
                "3.663-6.572-4.58-12.377-1.81-15.15 1.508-1.508 3.923-1.919 6.868-1.389 1.92-1.214 4.186-2.568 6.674-3.953-" +
                "10.111-5.273-21.603-8.252-33.799-8.252-40.441 0-73.23 32.781-73.23 73.23 0 40.44 32.786 73.23 73.23 " +
                "73.23 40.442 0 73.23-32.784 73.23-73.23 0-13.06-3.427-25.308-9.416-35.926-1.295 2.362-2.553 4.526-3.702 6.381").
                attr({fill: '#fff',"fill-opacity": '.85','stroke-width': '0','stroke-opacity': '1'}).
                transform("t"+x+","+y+"t-785,-658s0.27");
            r.rect(x,y,41,41);
        }

        freebsdLogo(100,100)
    });