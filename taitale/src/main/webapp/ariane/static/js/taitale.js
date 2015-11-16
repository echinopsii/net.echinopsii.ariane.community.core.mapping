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
        'jquery': 'javax.faces.resource/jquery/jquery.js.jsf?ln=primefaces',
        'jquery-ui': 'javax.faces.resource/jquery/jquery-plugins.js.jsf?ln=primefaces',
        'jquery-private': 'ajs/jquery-private',
        'prime-ui': 'ajs/primeui/primeui-0.9.6-min',
        'rgbcolor': 'ajs/canvag/rgbcolor-min',
        'StackBlur': 'ajs/canvag/StackBlur-min',
        'canvg': 'ajs/canvag/canvg-min',
        'eve': 'ajs/raphael/eve-min',
        'raphael-core': 'ajs/raphael/raphael.core-min',
        'raphael-svg': 'ajs/raphael/raphael.svg-min',
        'raphael-vml': 'ajs/raphael/raphael.vml-min',
        'raphael': 'ajs/raphael/raphael.amd-min',
        'raphael-zpd': 'ajs/raphael/raphael.zpd-min',
        'raphael-svg-export': 'ajs/raphael/raphael.svg.export-min',

        /*taitale tools*/
        'taitale-cylinder': 'ajs/taitale/tools/cylinder-min',
        'taitale-dictionaries': 'ajs/taitale/tools/dictionaries-min',
        'taitale-ext-string': 'ajs/taitale/tools/ext.string-min',
        'taitale-ext-raphael': 'ajs/taitale/tools/ext.raphael-min',
        'taitale-helper': 'ajs/taitale/tools/helper-min',
        'taitale-loader': 'ajs/taitale/tools/loader-min',
        'taitale-matrix': 'ajs/taitale/tools/matrix-min',
        'taitale-params': 'ajs/taitale/tools/params-min',
        'taitale-prototypes': 'ajs/taitale/tools/prototypes-min',

        /*taitale core*/
        'taitale-map-options': 'ajs/taitale/core/map/options-min',
        'taitale-map-matrix': 'ajs/taitale/core/map/matrix-min',
        'taitale-map': 'ajs/taitale/core/map/map-min',
        'taitale-container-matrix': 'ajs/taitale/core/container/matrix-min',
        'taitale-container-hat': 'ajs/taitale/core/container/hat-min',
        'taitale-container': 'ajs/taitale/core/container/container-min',
        'taitale-node-matrix': 'ajs/taitale/core/node/matrix-min',
        'taitale-node': 'ajs/taitale/core/node/node-min',
        'taitale-endpoint': 'ajs/taitale/core/endpoint-min',
        'taitale-transport': 'ajs/taitale/core/transport/transport-min',
        'taitale-transport-multicastbus': 'ajs/taitale/core/transport/multicastBus-min',
        'taitale-link': 'ajs/taitale/core/link-min',

        /*taitale tree layout*/
        'taitale-tree': 'ajs/taitale/layout/tree/tree-min',
        'taitale-vertex': 'ajs/taitale/layout/tree/vertex-min',

        /*taitale bubble tree layout*/
        'taitale-btree': 'ajs/taitale/layout/bubbletree/btree-min',
        'taitale-bvertex': 'ajs/taitale/layout/bubbletree/bvertex-min',

        /*taitale network layout*/
        'taitale-map-splitter': 'ajs/taitale/layout/network/mapSplitter-min',
        'taitale-layoutntw-registries' : 'ajs/taitale/layout/network/registries-min',
        'taitale-datacenter': 'ajs/taitale/layout/network/datacenter/datacenter-min',
        'taitale-datacenter-splitter': 'ajs/taitale/layout/network/datacenter/dcSplitter-min',
        'taitale-datacenter-hat': 'ajs/taitale/layout/network/datacenter/hat-min',
        'taitale-datacenter-matrix': 'ajs/taitale/layout/network/datacenter/matrix-min',
        'taitale-area': 'ajs/taitale/layout/network/area/area-min',
        'taitale-area-matrix': 'ajs/taitale/layout/network/area/matrix-min',
        'taitale-area-hat': 'ajs/taitale/layout/network/area/hat-min',
        'taitale-lan': 'ajs/taitale/layout/network/lan/lan-min',
        'taitale-lan-matrix': 'ajs/taitale/layout/network/lan/matrix-min',
        'taitale-lan-hat': 'ajs/taitale/layout/network/lan/hat-min'
    },
    map: {
        '*': { 'jquery': 'jquery-private' },
        'jquery-private': { 'jquery': 'jquery' }
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
    function ($, canvag, helper, loader, dictionaries, mapOptions) {

        var loader_   = new loader(),
            helper_   = new helper(),
            dic       = new dictionaries(),
            options   = new mapOptions(),
            homeURI   = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split('/')[1],
            i, ii, input;

        var readyStateCheckInterval = setInterval(function() {
            if (document.readyState === "complete") {
                /*
                 * wait document state is complete and
                 * load here any PrimeFaces JQuery (ie : global $)
                 * object events related to map
                 */

                $(execQuery.jqId).click([loader_, dic], function(){
                    var request = $(mdslQuery.jqId)[0].value
                    var requestURI = homeURI + "/rest/mapping/service/map/query?mdsl="+encodeURI(request)
                    helper_.debug(requestURI.toString());
                    options.setURI(requestURI);
                    try {
                        loader_.reloadMap(options);
                        loader_.editionMode(options);
                        if (options.getLayout()===dic.mapLayout.NTWWW) {
                            document.getElementById('treeOptions').style.display = "none";
                            document.getElementById('networkOptions').style.display = "";
                            for (i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                input = networkLayoutDisplayOptions.inputs[i];
                                if (input.value==="displayDC") options.displayDC = input.checked;
                                else if (input.value==="displayArea") options.displayAREA = input.checked;
                                else if (input.value==="displayLan") options.displayLAN = input.checked;
                            }
                        } else if (options.getLayout()===dic.mapLayout.TREE || options.getLayout()===dic.mapLayout.BBTREE) {
                            document.getElementById('treeOptions').style.display = "";
                            document.getElementById('networkOptions').style.display = "none";
                        }
                        $("#mappyCanvas").css({"background-color":"whitesmoke"});
                    } catch (e) {
                        helper_.addMsgToGrowl(e);
                        helper_.growlMsgs(
                            {
                                severity: 'error',
                                summary: 'Failed to load map',
                                detail: 'Layout: '+options.getLayout() +"<br>Mode: "+options.getMode(),
                                sticky: true
                            }
                        );
                        console.log(e.stack);
                    }
                });
                $(layoutSelector.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = layoutSelector.inputs.length; i < ii; i++) {
                        input = layoutSelector.inputs[i];
                        if (input.checked) {
                            var num = input.value;
                            options.setLayout(num);
                            try {
                                loader_.rebuildMap(options);
                                loader_.editionMode(options);
                                if (options.getLayout()===dic.mapLayout.NTWWW) {
                                    document.getElementById('treeOptions').style.display = "none";
                                    document.getElementById('networkOptions').style.display = "";
                                    for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                        var input = networkLayoutDisplayOptions.inputs[i];
                                        if (input.value==="displayDC") options.displayDC = input.checked;
                                        else if (input.value==="displayArea") options.displayAREA = input.checked;
                                        else if (input.value==="displayLan") options.displayLAN = input.checked;
                                    }
                                } else if (options.getLayout()===dic.mapLayout.TREE || options.getLayout()===dic.mapLayout.BBTREE) {
                                    document.getElementById('treeOptions').style.display = "";
                                    document.getElementById('networkOptions').style.display = "none";
                                }
                            } catch (e) {
                                helper_.addMsgToGrowl(e);
                                helper_.growlMsgs(
                                    {
                                        severity: 'error',
                                        summary: 'Failed to load map',
                                        detail: 'Layout: '+options.getLayout() + "<br>Mode: "+options.getMode(),
                                        sticky: true
                                    }
                                );
                                console.log(e.stack);
                            }
                            break;
                        }
                    }
                });
                $(modeSelector.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = modeSelector.inputs.length; i < ii; i++) {
                        input = modeSelector.inputs[i];
                        if (input.checked) {
                            options.setMode(input.value);
                            try {
                                //loader_.refreshMap(options);
                                loader_.editionMode(options);
                                if (options.getLayout()===dic.mapLayout.NTWWW) {
                                    for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                        var input = networkLayoutDisplayOptions.inputs[i];
                                        if (input.value==="displayDC") options.displayDC = input.checked;
                                        else if (input.value==="displayArea") options.displayAREA = input.checked;
                                        else if (input.value==="displayLan") options.displayLAN = input.checked;
                                    }
                                }
                            } catch (e) {
                                helper_.addMsgToGrowl(e);
                                helper_.growlMsgs(
                                    {
                                        severity: 'error',
                                        summary: 'Failed to refresh map',
                                        detail: 'Layout: '+options.getLayout()+"<br>Mode: "+options.getMode(),
                                        sticky: true
                                    });
                                console.log(e.stack);
                            }
                        }
                    }
                });
                $(notificationsOptions.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = notificationsOptions.inputs.length; i < ii; i++) {
                        input = notificationsOptions.inputs[i];
                        if (input.value==="notifyInfos") {
                            helper_.setNotifyInfo(input.checked);
                        } else if (input.value==="notifyWarns") {
                            helper_.setNotifyWarn(input.checked);
                        } else if (input.value==="notifyErrs") {
                            helper_.setNotifyErrs(input.checked);
                        }
                    }
                });
                $(networkLayoutDisplayOptions.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                        input = networkLayoutDisplayOptions.inputs[i];
                        if (input.value==="displayDC") {
                            options.displayDC = input.checked;
                            loader_.displayDC(options.displayDC);
                        } else if (input.value==="displayArea") {
                            options.displayAREA = input.checked;
                            loader_.displayArea(options.displayAREA);
                        } else if (input.value==="displayLan") {
                            options.displayLAN = input.checked;
                            loader_.displayLan(options.displayLAN);
                        }
                    }
                });
                $(rootTreeSorting.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = rootTreeSorting.inputs.length; i < ii; i++) {
                        input = rootTreeSorting.inputs[i];
                        if (input.checked) {
                            var value = input.value;
                            try {
                                options.setRootTreeSorting(value);
                                loader_.sortRootTree(value);
                                loader_.rebuildMapTreeLayout();
                                loader_.refreshMap(options);
                            } catch (e) {
                                helper_.addMsgToGrowl(e);
                                helper_.growlMsgs(
                                    {
                                        severity: 'error',
                                        summary: 'Failed refresh tree map with selected root tree sorting',
                                        detail: 'Check the console log to know more...',
                                        sticky: true
                                    }
                                );
                                console.log(e.stack);
                            }
                            break;
                        }
                    }
                });
                $(subTreesSorting.jqId).change([loader_, dic], function() {
                    for (i = 0, ii = subTreesSorting.inputs.length; i < ii; i++) {
                        input = subTreesSorting.inputs[i];
                        if (input.checked) {
                            var value = input.value;
                            try {
                                options.setSubTreesSorting(value);
                                loader_.sortSubTrees(options.getSubTreesSorting());
                                loader_.rebuildMapTreeLayout();
                                loader_.refreshMap(options);
                                helper_.growlMsgs(
                                    {
                                        severity: 'info',
                                        summary: 'Map successfully refreshed ',
                                        detail: 'Name: '+$('#test').val()+'<br>Layout: '+options.getLayout() +"<br>Mode: "+options.getMode()
                                    }
                                );
                            } catch (e) {
                                helper_.addMsgToGrowl(e);
                                helper_.growlMsgs(
                                    {
                                        severity: 'error',
                                        summary: 'Failed refresh tree map with selected sub trees sorting',
                                        detail: 'Check the console log to know more...',
                                        sticky: true
                                    }
                                );
                                console.log(e.stack);
                            }
                            break;
                        }
                    }
                });
                $(JPG.jqId).click([loader_, dic], function() {
                    try {
                        var inProgress = document.getElementById('exportInProgress');
                        $("#imgExport").empty();
                        mapExport.show();
                        inProgress.style.display = "";
                        var svg          = loader_.exportToSVG(),
                            exportCanvas = '<canvas id="exportCanvas" title="jpgMap"></canvas>';
                        inProgress.style.display = "none";
                        $("#imgExport").append(exportCanvas);
                        canvg('exportCanvas', svg);
                        document.getElementById("exportCanvas").toDataURL("image/jpeg");
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
                    }
                });
                $(PNG.jqId).click([loader_, dic], function() {
                    try {
                        var inProgress = document.getElementById('exportInProgress');
                        $("#imgExport").empty();
                        mapExport.show();
                        inProgress.style.display = "";
                        var svg          = loader_.exportToSVG(),
                            exportCanvas = '<canvas id="exportCanvas" title="pngMap"></canvas>';
                        inProgress.style.display = "none";
                        $("#imgExport").append(exportCanvas);
                        canvg('exportCanvas', svg);
                        document.getElementById("exportCanvas").toDataURL("image/png");
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
                    }
                });
                $(SVG.jqId).click([loader_, dic], function(){
                    try {
                        var inProgress = document.getElementById('exportInProgress');
                        $("#imgExport").empty();
                        mapExport.show();
                        inProgress.style.display = "";
                        var svg    = loader_.exportToSVG(),
                            imgsrc = "data:image/svg+xml," + encodeURIComponent(svg),
                            img    = '<img src="'+imgsrc+'" title="svgMap">';
                        inProgress.style.display = "none";
                        $("#imgExport").append(img);
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
                    }
                });

                for (i = 0, ii = notificationsOptions.inputs.length; i < ii; i++) {
                    input = notificationsOptions.inputs[i];
                    if (input.value==="notifyInfos") {
                        input.checked=helper_.getNotifyInfo();
                    } else if (input.value==="notifyWarns") {
                        input.checked=helper_.getNotifyWarn();
                    } else if (input.value==="notifyErrs") {
                        input.checked=helper_.getNotifyErrs();
                    }
                }
                clearInterval(readyStateCheckInterval);
            }
        }, 10);

        helper_.initGrowlMsgs(widget_growl.jqId);
        helper_.initErrorBox('#mapError', '#mapErrorMsg');

        for (i = 0, ii = layoutSelector.inputs.length; i < ii; i++) {
            input = layoutSelector.inputs[i];
            if (input.checked) {
                var num = input.value;
                options.setLayout(num);
                //options.setURI(homeURI + "/rest/service/map/all");
                break;
            }
        }

        for (i = 0, ii = modeSelector.inputs.length; i < ii; i++) {
            input = modeSelector.inputs[i];
            if (input.checked) {
                options.setMode(input.value);
            }
        }

        if (options.getLayout()===dic.mapLayout.NTWWW) {
            document.getElementById('treeOptions').style.display = "none";
            document.getElementById('networkOptions').style.display = "";
            for (i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                input = networkLayoutDisplayOptions.inputs[i];
                if (input.value==="displayDC") {
                    options.displayDC = input.checked;
                    //loader_.displayDC(options.displayDC);
                } else if (input.value==="displayArea") {
                    options.displayAREA = input.checked;
                    //loader_.displayArea(options.displayAREA);
                } else if (input.value==="displayLan") {
                    options.displayLAN = input.checked;
                    //loader_.displayLan(options.displayLAN);
                }
            }
        } else if (options.getLayout()===dic.mapLayout.TREE  || options.getLayout()===dic.mapLayout.BBTREE) {
            document.getElementById('treeOptions').style.display = "";
            document.getElementById('networkOptions').style.display = "none";
        }
    });