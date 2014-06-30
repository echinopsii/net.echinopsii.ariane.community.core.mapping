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
        'jquery-private': 'js/jquery-private',
        'prime-ui': 'js/primeui/primeui-0.9.6',
        'eve': 'js/raphael/eve',
        'raphael-core': 'js/raphael/raphael.core',
        'raphael-svg': 'js/raphael/raphael.svg',
        'raphael-vml': 'js/raphael/raphael.vml',
        'raphael': 'js/raphael/raphael.amd',
        'raphael-zpd': 'js/raphael/raphael.zpd',
        'raphael.free_transform': 'js/raphael/raphael.free_transform',

        /*taitale tools*/
        'taitale-cylinder': 'js/taitale/tools/cylinder',
        'taitale-dictionaries': 'js/taitale/tools/dictionaries',
        'taitale-ext-string': 'js/taitale/tools/ext.string',
        'taitale-ext-raphael': 'js/taitale/tools/ext.raphael',
        'taitale-helper': 'js/taitale/tools/helper',
        'taitale-loader': 'js/taitale/tools/loader',
        'taitale-params': 'js/taitale/tools/params',
        'taitale-prototypes': 'js/taitale/tools/prototypes',

        /*taitale core*/
        'taitale-map-options': 'js/taitale/core/map/options',
        'taitale-map-matrix': 'js/taitale/core/map/matrix',
        'taitale-map': 'js/taitale/core/map/map',
        'taitale-container-matrix': 'js/taitale/core/container/matrix',
        'taitale-container-hat': 'js/taitale/core/container/hat',
        'taitale-container': 'js/taitale/core/container/container',
        'taitale-node': 'js/taitale/core/node/node',
        'taitale-endpoint': 'js/taitale/core/endpoint',
        'taitale-transport': 'js/taitale/core/transport/transport',
        'taitale-transport-multicastbus': 'js/taitale/core/transport/multicastBus',
        'taitale-link': 'js/taitale/core/link',

        /*taitale tree layout*/
        'taitale-tree': 'js/taitale/layout/tree/tree',
        'taitale-vertex': 'js/taitale/layout/tree/vertex',

        /*taitale network layout*/
        'taitale-map-splitter': 'js/taitale/layout/network/mapSplitter',
        'taitale-layoutntw-registries' : 'js/taitale/layout/network/registries',
        'taitale-datacenter': 'js/taitale/layout/network/datacenter/datacenter',
        'taitale-datacenter-splitter': 'js/taitale/layout/network/datacenter/dcSplitter',
        'taitale-datacenter-matrix': 'js/taitale/layout/network/datacenter/matrix',
        'taitale-area': 'js/taitale/layout/network/area/area',
        'taitale-area-matrix': 'js/taitale/layout/network/area/matrix',
        'taitale-lan': 'js/taitale/layout/network/lan/lan',
        'taitale-lan-matrix': 'js/taitale/layout/network/lan/matrix'
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
        }
    }
});

requirejs (
    [
        'prime-ui',
        'taitale-helper',
        'taitale-loader',
        'taitale-dictionaries',
        'taitale-map-options'
    ],
    function ($, helper, loader, dictionaries, mapOptions) {

        var loader_   = new loader(),
            helper_   = new helper(),
            dic       = new dictionaries(),
            options   = new mapOptions(),
            homeURI   = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split('/')[1];

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
                        if (options.getLayout()===dic.mapLayout.NTWWW) {
                            document.getElementById('treeOptions').style.display = "none";
                            document.getElementById('networkOptions').style.display = "";
                            for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                var input = networkLayoutDisplayOptions.inputs[i];
                                if (input.value==="displayDC") {
                                    loader_.displayDC(input.checked);
                                } else if (input.value==="displayArea") {
                                    loader_.displayArea(input.checked);
                                } else if (input.value==="displayLan") {
                                    loader_.displayLan(input.checked);
                                }
                            }
                        } else if (options.getLayout()===dic.mapLayout.TREE) {
                            document.getElementById('treeOptions').style.display = "";
                            document.getElementById('networkOptions').style.display = "none";
                        }
                    } catch (e) {
                        helper_.addMsgToGrowl(e);
                        helper_.growlMsgs(
                            {
                                severity: 'error',
                                summary: 'Failed to load map',
                                detail: 'Layout: '+options.getLayout(), // +"<br>Mode: "+options.getMode(),
                                sticky: true
                            }
                        );
                        console.log(e.stack);
                    }
                });
                $(layoutSelector.jqId).change([loader_, dic], function() {
                    for (var i = 0, ii = layoutSelector.inputs.length; i < ii; i++) {
                        var input = layoutSelector.inputs[i];
                        if (input.checked) {
                            var num = input.value;
                            options.setLayout(num);
                            //options.setURI(homeURI + "/rest/service/map/all");
                            try {
                                loader_.rebuildMap(options);
                                if (options.getLayout()===dic.mapLayout.NTWWW) {
                                    document.getElementById('treeOptions').style.display = "none";
                                    document.getElementById('networkOptions').style.display = "";
                                    for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                        var input = networkLayoutDisplayOptions.inputs[i];
                                        if (input.value==="displayDC") {
                                            loader_.displayDC(input.checked);
                                        } else if (input.value==="displayArea") {
                                            loader_.displayArea(input.checked);
                                        } else if (input.value==="displayLan") {
                                            loader_.displayLan(input.checked);
                                        }
                                    }
                                } else if (options.getLayout()===dic.mapLayout.TREE) {
                                    document.getElementById('treeOptions').style.display = "";
                                    document.getElementById('networkOptions').style.display = "none";
                                }
                            } catch (e) {
                                helper_.addMsgToGrowl(e);
                                helper_.growlMsgs(
                                    {
                                        severity: 'error',
                                        summary: 'Failed to load map',
                                        detail: 'Layout: '+options.getLayout(), // +"<br>Mode: "+options.getMode(),
                                        sticky: true
                                    }
                                );
                                console.log(e.stack);
                            }
                            break;
                        }
                    }
                });
                /*
                $(modeSelector.jqId).change([loader_, dic], function() {
                    for (var i = 0, ii = modeSelector.inputs.length; i < ii; i++) {
                        var input = modeSelector.inputs[i];
                        if (input.checked) {
                            options.setMode(input.value);
                            try {
                                loader_.refreshMap(options);
                                if (options.getLayout()===dic.mapLayout.NTWWW) {
                                    for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                                        var input = networkLayoutDisplayOptions.inputs[i];
                                        if (input.value==="displayDC") {
                                            loader_.displayDC(input.checked);
                                        } else if (input.value==="displayArea") {
                                            loader_.displayArea(input.checked);
                                        } else if (input.value==="displayLan") {
                                            loader_.displayLan(input.checked);
                                        }
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
                */
                $(notificationsOptions.jqId).change([loader_, dic], function() {
                    for (var i = 0, ii = notificationsOptions.inputs.length; i < ii; i++) {
                        var input = notificationsOptions.inputs[i];
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
                    for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                        var input = networkLayoutDisplayOptions.inputs[i];
                        if (input.value==="displayDC") {
                            loader_.displayDC(input.checked);
                        } else if (input.value==="displayArea") {
                            loader_.displayArea(input.checked);
                        } else if (input.value==="displayLan") {
                            loader_.displayLan(input.checked);
                        }
                    }
                });
                $(rootTreeSorting.jqId).change([loader_, dic], function() {
                    for (var i = 0, ii = rootTreeSorting.inputs.length; i < ii; i++) {
                        var input = rootTreeSorting.inputs[i];
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
                    for (var i = 0, ii = subTreesSorting.inputs.length; i < ii; i++) {
                        var input = subTreesSorting.inputs[i];
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
                                        detail: 'Name: '+$('#test').val()+'<br>Layout: '+options.getLayout() //+"<br>Mode: "+options.getMode()
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
                for (var i = 0, ii = notificationsOptions.inputs.length; i < ii; i++) {
                    var input = notificationsOptions.inputs[i];
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

        for (var i = 0, ii = layoutSelector.inputs.length; i < ii; i++) {
            var input = layoutSelector.inputs[i];
            if (input.checked) {
                var num = input.value;
                options.setLayout(num);
                //options.setURI(homeURI + "/rest/service/map/all");
                break;
            }
        }

        /*
        for (var i = 0, ii = modeSelector.inputs.length; i < ii; i++) {
            var input = modeSelector.inputs[i];
            if (input.checked) {
                options.setMode(input.value);
            }
        }
        */

        if (options.getLayout()===dic.mapLayout.NTWWW) {
            document.getElementById('treeOptions').style.display = "none";
            document.getElementById('networkOptions').style.display = "";
        } else if (options.getLayout()===dic.mapLayout.TREE) {
            document.getElementById('treeOptions').style.display = "";
            document.getElementById('networkOptions').style.display = "none";
        }

        /*
        try {
            if (options.getLayout()!==dic.mapLayout.NTWWW) {
                document.getElementById('networkOptions').style.display = "none";
            } else {
                document.getElementById('networkOptions').style.display = "";
            }
            loader_.loadMap(options);
            for (var i = 0, ii = networkLayoutDisplayOptions.inputs.length; i < ii; i++) {
                var input = networkLayoutDisplayOptions.inputs[i];
                if (input.value==="displayDC") {
                    loader_.displayDC(input.checked);
                } else if (input.value==="displayArea") {
                    loader_.displayArea(input.checked);
                } else if (input.value==="displayLan") {
                    loader_.displayLan(input.checked);
                }
            }
            helper_.growlMsgs(
                {
                    severity: 'info',
                    summary: 'Map successfully loaded',
                    detail: 'Layout: '+options.getLayout()+"<br>Mode: "+options.getMode()
                }
            );
        } catch (e) {
            helper_.addMsgToGrowl(e);
            helper_.growlMsgs(
                {
                    severity: 'error',
                    summary: 'Failed to load map',
                    detail: 'Layout: '+options.getLayout()+"<br>Mode: "+options.getMode(),
                    sticky: true
                });
            console.log(e.stack);
        }
        */
    });