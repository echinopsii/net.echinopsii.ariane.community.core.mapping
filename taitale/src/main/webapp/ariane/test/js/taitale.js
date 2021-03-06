// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale 0.0.1   - JavaScript Taitale Library                                         │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │ 																					  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of						  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the						  │ \\
// │ GNU Affero General Public License for more details.								  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.				  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\

require.config({
    baseUrl: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split('/')[1],
    paths: {
        'jquery': 'js/jquery/jquery-1.9.1',
        'jquery-ui': 'js/jquery-ui/jquery-ui-1.10.3.custom',
        'prime-ui': 'js/primeui/primeui-0.9.6',
        'rgbcolor': 'js/canvag/rgbcolor',
        'StackBlur': 'js/canvag/StackBlur',
        'canvg': 'js/canvag/canvg',
        'eve': 'js/raphael/eve',
        'raphael-core': 'js/raphael/raphael.core',
        'raphael-svg': 'js/raphael/raphael.svg',
        'raphael-vml': 'js/raphael/raphael.vml',
        'raphael': 'js/raphael/raphael.amd',
        'raphael-zpd': 'js/raphael/raphael.zpd',
        'raphael-svg-export': 'js/raphael/raphael.svg.export',
        /*taitale tools*/
        'taitale-cylinder': 'js/taitale/tools/cylinder',
        'taitale-dictionaries': 'js/taitale/tools/dictionaries',
        'taitale-ext-string': 'js/taitale/tools/ext.string',
        'taitale-ext-raphael': 'js/taitale/tools/ext.raphael',
        'taitale-helper': 'js/taitale/tools/helper',
        'taitale-matrix': 'js/taitale/tools/matrix',
        'taitale-loader': 'js/taitale/tools/loader',
        'taitale-params': 'js/taitale/tools/params',
        'taitale-prototypes': 'js/taitale/tools/prototypes',
        'taitale-tree-groups': 'js/taitale/tools/treeGroups',
        /*taitale core*/
        'taitale-map-options': 'js/taitale/core/map/options',
        'taitale-map-matrix': 'js/taitale/core/map/matrix',
        'taitale-map': 'js/taitale/core/map/map',
        'taitale-container-matrix': 'js/taitale/core/container/matrix',
        'taitale-container-hat': 'js/taitale/core/container/hat',
        'taitale-container': 'js/taitale/core/container/container',
        'taitale-node-matrix': 'js/taitale/core/node/matrix',
        'taitale-node': 'js/taitale/core/node/node',
        'taitale-endpoint': 'js/taitale/core/endpoint',
        'taitale-transport': 'js/taitale/core/transport/transport',
        'taitale-transport-multicastbus': 'js/taitale/core/transport/multicastBus',
        'taitale-link': 'js/taitale/core/link',
        /*taitale bubble tree layout*/
        'taitale-btree': 'js/taitale/layout/bubbletree/btree',
        'taitale-bvertex': 'js/taitale/layout/bubbletree/bvertex',
        /*taitale orbital tree layout*/
        'taitale-otree': 'js/taitale/layout/orbitaltree/otree',
        'taitale-overtex': 'js/taitale/layout/orbitaltree/overtex',
        /*taitale netL3+ layout*/
        'taitale-map-splitter': 'js/taitale/layout/netL3+/mapSplitter',
        'taitale-layoutntw-registries' : 'js/taitale/layout/netL3+/registries',
        'taitale-datacenter': 'js/taitale/layout/netL3+/datacenter/datacenter',
        'taitale-datacenter-splitter': 'js/taitale/layout/netL3+/datacenter/dcSplitter',
        'taitale-datacenter-hat': 'js/taitale/layout/netL3+/datacenter/hat',
        'taitale-datacenter-matrix': 'js/taitale/layout/netL3+/datacenter/matrix',
        'taitale-area': 'js/taitale/layout/netL3+/area/area',
        'taitale-area-hat': 'js/taitale/layout/netL3+/area/hat',
        'taitale-area-matrix': 'js/taitale/layout/netL3+/area/matrix',
        'taitale-lan': 'js/taitale/layout/netL3+/lan/lan',
        'taitale-lan-matrix': 'js/taitale/layout/netL3+/lan/matrix',
        'taitale-lan-hat': 'js/taitale/layout/netL3+/lan/hat'
    },
    shim: {
        "jquery-ui": {
            exports: "$",
            deps: ['jquery']
        },
        "prime-ui": {
            exports: "$",
            deps: ['jquery','jquery-ui']
        },
        "canvg": {
            exports: "canvg",
            deps: ['rgbcolor', 'StackBlur']
        }
    }
});

requirejs (
    [
        'prime-ui',
        'canvg',
        'taitale-helper',
        'taitale-loader',
        'taitale-dictionaries',
        'taitale-map-options'
    ],
    function ($,canvg,helper,loader,dictionaries,mapOptions) {

        var loader_   = new loader(),
            dic       = new dictionaries(),
            options   = new mapOptions(),
            helper_   = new helper(),
            homeURI   = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split('/')[1];

        helper_.initGrowlMsgs('#mapGrowl');
        helper_.initErrorBox('#mapError', '#mapErrorMsg');

        var test = $('#test'),
            layout = $('#layout'),
            edition = $('#edition'),
            epreset = $('#epreset'),
            legend = $('#legend'),
            notifyI = $('#notifyInfo'),
            notifyW = $('#notifyWarn'),
            notifyE = $('#notifyErrs'),
            reload = $('#reload'),
            nsize = $('#nsize'),
            center = $('#center'),
            details = $('#details'),
            JPG = $('#JPG'),
            PNG = $('#PNG'),
            SVG = $('#SVG'),
            mapExport = $('#mapExport'),
            imgExport = $('#imgExport'),
            displayDC = $("#displayDC"),
            displayArea = $("#displayArea"),
            displayLan = $("#displayLan"),
            rootTreeSorting = $('#rootTreeSorting'),
            subTreesSorting = $('#subTreesSorting');

        test.puidropdown({
            change: function() {
                helper_.hideErrorBox();
                options.setURI(homeURI + "/js/taitale.samples/json/sample.taitale.input."+test.val()+".json");
                try {
                    loader_.reloadMap(options);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to load map',
                            detail: 'Name: '+test.val()+'<br>Layout: '+options.getLayout(),
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    var msg = "<h3>oO ! We have some problem to load the map  here ! <br/> Let's find a way to correct it ... </h3>" +
                        '<p>1) open a new JIRA ticket <a href="http://jira.echinopsii.net" target="_blank">here</a></p>' +
                        '<p>2) complete the ticket : <ul>' +
                        '<li>setup ticket title as "map loading error"</li>' +
                        '<li>attach <a href="'+ options.getURI() +'" target="_blank">the source of the problem</a></li>'+
                        '<li>specify the layout (' + options.getLayout() +')</li></ul></p>' +
                        "<p>3) wait the ticket to be resolved ... </p>";
                    helper_.showErrorBox(msg);
                }
            }
        });
        layout.puidropdown({
            change: function() {
                helper_.hideErrorBox();
                options.setLayout(layout.val());
                try {
                    if (options.getLayout()===dic.mapLayout.NETL3P) {
                        document.getElementById('netl3pOptions').style.display = "";
                        document.getElementById('treeOptions').style.display    = "none";
                    } else if (options.getLayout()===dic.mapLayout.BBTREE) {
                        document.getElementById('netl3pOptions').style.display = "none";
                        document.getElementById('treeOptions').style.display    = "";
                    } else if (options.getLayout()===dic.mapLayout.OBTREE) {
                        document.getElementById('netl3pOptions').style.display = "none";
                        document.getElementById('treeOptions').style.display    = "";
                    } else {
                        document.getElementById('netl3pOptions').style.display = "none";
                        document.getElementById('treeOptions').style.display    = "none";
                    }
                    loader_.rebuildMap(options);
                    loader_.displayDC(displayDC[0].checked);
                    loader_.displayArea(displayArea[0].checked);
                    loader_.displayLan(displayLan[0].checked);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to load map',
                            detail: 'Name: '+test.val()+'<br>Layout: '+options.getLayout(),
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    var msg = "<h3>oO ! We have some problem to load the map here ! <br/> Let's find a way to correct it ... </h3>" +
                        '<p>1) open a new JIRA ticket <a href="http://jira.echinopsii.net" target="_blank">here</a></p>' +
                        '<p>2) complete the ticket : <ul>' +
                        '<li>setup ticket title as "map loading error"</li>' +
                        '<li>attach <a href="'+ options.getURI() +'" target="_blank">the source of the problem</a></li>'+
                        '<li>specify the layout (' + options.getLayout() +')</li></ul></p>' +
                        "<p>3) wait the ticket to be resolved ... </p>";
                    helper_.showErrorBox(msg);
                }
            }
        });
        edition.puicheckbox({
            change: function() {
                options.edition = edition[0].checked;
                try {
                    loader_.editionMode(options);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to activate edition helper',
                            detail: 'Name: ' + test.val() + '<br>Layout: ' + options.getLayout(),
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    var msg = "<h3>oO ! We have some problem to activate edition helper here ! <br/> Let's find a way to correct it ... </h3>" +
                        '<p>1) open a new JIRA ticket <a href="http://jira.echinopsii.net" target="_blank">here</a></p>' +
                        '<p>2) complete the ticket : <ul>' +
                        '<li>setup ticket title as "edition helper activation error"</li>' +
                        '<li>attach <a href="'+ options.getURI() +'" target="_blank">the source of the problem</a></li>'+
                        '<li>specify the layout (' + options.getLayout() +')</li></ul></p>' +
                        "<p>3) wait the ticket to be resolved ... </p>";
                    helper_.showErrorBox(msg);
                }
            }
        });
        epreset.puicheckbox({
            change: function() {
                options.epreset = epreset[0].checked;
                try {
                    loader_.endpointReset(options);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to activate endpoint helper',
                            detail: 'Name: ' + test.val() + '<br>Layout: ' + options.getLayout(),
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    var msg = "<h3>oO ! We have some problem to activate endpoint helper here ! <br/> Let's find a way to correct it ... </h3>" +
                        '<p>1) open a new JIRA ticket <a href="http://jira.echinopsii.net" target="_blank">here</a></p>' +
                        '<p>2) complete the ticket : <ul>' +
                        '<li>setup ticket title as "endpoint helper activation error"</li>' +
                        '<li>attach <a href="'+ options.getURI() +'" target="_blank">the source of the problem</a></li>'+
                        '<li>specify the layout (' + options.getLayout() +')</li></ul></p>' +
                        "<p>3) wait the ticket to be resolved ... </p>";
                    helper_.showErrorBox(msg);
                }
            }
        });
        legend.puicheckbox({
            change: function () {
                options.displayLegend = legend[0].checked;
                try {
                    loader_.legend(options)
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to display legend',
                            detail: 'Name: ' + test.val() + '<br>Layout: ' + options.getLayout(),
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                }
            }
        });
        notifyI.puicheckbox({
            change: function() {
                helper_.setNotifyInfo(notifyI.puicheckbox('isChecked'));
            }
        });
        notifyW.puicheckbox({
            change: function() {
                helper_.setNotifyWarn(notifyW.puicheckbox('isChecked'));
            }
        });
        notifyE.puicheckbox({
            change: function() {
                helper_.setNotifyErrs(notifyE.puicheckbox('isChecked'));
            }
        });
        nsize.puibutton({
            click: function() {
                try {
                    loader_.normalSize();
                    nsize.removeClass('ui-state-focus');
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to center map',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        center.puibutton({
            click: function() {
                try {
                    loader_.centerMappy();
                    center.removeClass('ui-state-focus');
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to center map',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        reload.puibutton({
            click: function() {
                try {
                    loader_.reloadMap(options);
                    reload.removeClass('ui-state-focus');
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to reload map',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });

        mapExport.puidialog({
            showEffect: 'fade',
            hideEffect: 'fade',
            width: 1000,
            height:800
        });

        JPG.puibutton({
            click: function() {
                try {
                    var svg          = loader_.exportToSVG(),
                        exportCanvas = '<canvas id="exportCanvas" title="jpgMap"></canvas>';
                    imgExport.empty();
                    imgExport.append(exportCanvas);
                    canvg('exportCanvas', svg);
                    document.getElementById("exportCanvas").toDataURL("image/jpeg", 1.0);
                    mapExport.puidialog('show');
                    JPG.removeClass('ui-state-focus');
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to export map to JPG',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        PNG.puibutton({
            click: function() {
                try {
                    var svg    = loader_.exportToSVG(),
                        imgsrc = "data:image/svg+xml," + encodeURIComponent(svg),
                        img    = '<canvas id="exportPngCanvas" title="pngMap"></canvas>';
                    imgExport.empty();
                    imgExport.append(img);
                    canvg('exportPngCanvas', svg);
                    document.getElementById("exportPngCanvas").toDataURL("image/png");
                    mapExport.puidialog('show');
                    PNG.removeClass('ui-state-focus');
                    //open(imgsrc);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to export map to PNG',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        SVG.puibutton({
            click: function() {
                try {
                    var svg    = loader_.exportToSVG(),
                        imgsrc = "data:image/svg+xml," + encodeURIComponent(svg),
                        img    = '<img src="'+imgsrc+'" title="svgMap">';
                    imgExport.empty();
                    imgExport.append(img);
                    mapExport.puidialog('show');
                    SVG.removeClass('ui-state-focus');
                    //open(imgsrc);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to export map to SVG',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });

        displayDC.puicheckbox({
            change: function() {
                try {
                    options.displayDC = displayDC[0].checked;
                    loader_.displayDC(options.displayDC);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to display/hide DCs',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        displayArea.puicheckbox({
            change:function() {
                try {
                    options.displayAREA = displayArea[0].checked;
                    loader_.displayArea(options.displayAREA);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to display/hide Areas',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        displayLan.puicheckbox({
            change: function() {
                try {
                    options.displayLAN = displayLan[0].checked;
                    loader_.displayLan(options.displayLAN);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed to display/hide Lans',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });

        rootTreeSorting.puidropdown({
            change: function() {
                try {
                    options.setRootTreeSorting(rootTreeSorting.val());
                    loader_.sortRootTree(rootTreeSorting.val());
                    loader_.rebuildMapTreeLayout();
                    loader_.refreshMap(options);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed refresh tree map with selected sub tree sorting',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });
        subTreesSorting.puidropdown({
            change: function() {
                try {
                    options.setSubTreesSorting(subTreesSorting.val());
                    loader_.sortSubTrees(options.getSubTreesSorting());
                    loader_.rebuildMapTreeLayout();
                    loader_.refreshMap(options);
                } catch (e) {
                    helper_.addMsgToGrowl(e);
                    helper_.growlMsgs(
                        {
                            severity: 'error',
                            summary: 'Failed refresh tree map with selected sub tree sorting',
                            detail: 'Check the console log to know more...',
                            sticky: true
                        }
                    );
                    console.log(e.stack);
                    helper_.showErrorBox();
                }
            }
        });

        try {
            helper_.hideErrorBox();
            if (helper_.getNotifyInfo())
                notifyI.puicheckbox('check');
            if (helper_.getNotifyWarn())
                notifyW.puicheckbox('check');
            if (helper_.getNotifyErrs())
                notifyE.puicheckbox('check');

            options.setLayout(layout.val());
            options.edition = edition[0].checked;
            options.epreset = epreset[0].checked;
            options.displayLegend = legend[0].checked;
            options.displayDC = displayDC[0].checked;
            options.displayAREA = displayArea[0].checked;
            options.displayLAN = displayLan[0].checked;
            options.setURI(homeURI + "/js/taitale.samples/json/sample.taitale.input."+test.val()+".json");
            if (options.getLayout()===dic.mapLayout.NETL3P) {
                document.getElementById('netl3pOptions').style.display = "";
                document.getElementById('treeOptions').style.display    = "none";
            } else if (options.getLayout()===dic.mapLayout.BBTREE) {
                document.getElementById('netl3pOptions').style.display = "none";
                document.getElementById('treeOptions').style.display    = "";
            } else if (options.getLayout()===dic.mapLayout.OBTREE) {
                document.getElementById('netl3pOptions').style.display = "none";
                document.getElementById('treeOptions').style.display    = "";
            } else {
                document.getElementById('netl3pOptions').style.display = "none";
                document.getElementById('treeOptions').style.display    = "none";
            }
            loader_.loadMap(options);
        } catch (e) {
            helper_.addMsgToGrowl(e);
            helper_.growlMsgs(
                {
                    severity: 'error',
                    summary: 'Failed to load map',
                    detail: 'Name: '+test.val()+'<br>Layout: '+options.getLayout(),
                    sticky: true
                });
            console.log(e.stack);
            helper_.showErrorBox();
        }
    });