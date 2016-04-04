// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TOOLS - helper                                │ \\
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

define(
    [
        'prime-ui'
    ],
    function ($) {

        var sharedMsgToGrowl = [],
            errorBoxDiv      = null,
            errorMsgDiv      = null,
            growlDiv         = null,
            notifyInfo       = true,
            notifyWarn       = false,
            notifyErrs       = true;

        function helper() {

            var msgsToGrowl = sharedMsgToGrowl;

            this.equalSortedArray = function(a, b) {
                if (a === b) return true;
                if (a == null || b == null) return false;
                if (a.length != b.length) return false;

                a.sort();
                b.sort();

                for (var i = 0; i < a.length; ++i) {
                    if (a[i] !== b[i]) return false;
                }
                return true;
            };

            this.fitText = function (fontSize, containerWidth, compressor, min) {
                var maxFontSize = fontSize.split("px")[0],
                    minFontSize = min,
                    compress    = compressor || 1,
                    newFontSize = Math.max(Math.min(containerWidth / (compress*10), maxFontSize), minFontSize) + ' px' ;
                return newFontSize;
            };

            this.debug = function(stringToLog) {
                if (typeof console != "undefined") {
                    console.log(stringToLog);
                }
            };

            this.isValidIPAddress = function (ipaddress)
            {
                if (/^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/.test(ipaddress)) {
                    return true
                }
                return false
            };

            this.propertiesDisplay = function(details, propsKey, propsValue) {
                if (Array.isArray(propsValue) || Object.prototype.toString.call(propsValue)==="[object Object]") {
                    var jsonString = JSON.stringify(propsValue);
                    jsonString=jsonString.split("[").join("[<br/>");
                    jsonString=jsonString.split("{").join("{<br/>");
                    jsonString=jsonString.split("},").join("},<br/>");
                    jsonString=jsonString.split("],").join("],<br/>");
                    jsonString=jsonString.split("\",").join("\",<br/>");
                    jsonString=jsonString.split("\"]").join("\"<br/>]");
                    jsonString=jsonString.split("\"}").join("\"<br/>}");
                    jsonString=jsonString.split("]}").join("]<br/>}");
                    jsonString=jsonString.split("]]").join("]<br/>]");
                    jsonString=jsonString.split("}]").join("}<br/>]");
                    jsonString=jsonString.split("}}").join("}<br/>}");
                    details += "<br/> <b>"+ propsKey + "</b> : " + jsonString;
                } else {
                    details += "<br/> <b>"+ propsKey + "</b> : " + JSON.stringify(propsValue);
                }
                return details;
            };

            this.dialogOpen = function(id, title, contents) {
                $('#mappingCanvas').append("<div id=\"content"+ id +"\" style=\"width: auto; display:inline-block\" class=\"mappingPropsDialog\">"+title+"<br/>"+contents+"</div>");
                var dialogWidth = $("#content"+id).width() + 100,
                    dialogHeight = $("#content"+id).height();
                $("#content"+id).remove();
                $('#mappingCanvas').append("<div id=\"dialog"+ id + "\" title=\"" + title + "\" class=\"mappingPropsDialog\">"+contents+"</div>");
                $("#dialog"+id).puidialog({
                    showEffect: 'fade',
                    hideEffect: 'fade',
                    minimizable: true,
                    maximizable: false,
                    modal: false,
                    width: (dialogWidth > 800) ? 800 : (dialogWidth < 200) ? 200 : dialogWidth,
                    height: (dialogHeight > 600) ? 600 : (dialogHeight < 100) ? 100 : dialogHeight,
                    afterHide: function(event) {
                        $("#dialog"+id).remove();
                    }
                });
                $("#dialog"+id).puidialog('show');
                $('#dialog'+id).children().css({"color":"#ffffff"});
                $('#dialog'+id).children(".pui-dialog-titlebar").children(".pui-dialog-titlebar-maximize").remove();
            };

            this.dialogClose = function(id) {
                $("#dialog"+id).remove();
            };

            this.legendOpen = function(contents) {
                $('#mappingCanvas').append("<div id=\"legendContent\" style=\"width: auto; display:inline-block\" class=\"mappingLegendPropsDialog\">Map Legend<br/>"+contents+"</div>");
                var dialogWidth = $("#legendContent").width() + 100,
                    dialogHeight = $("#legendContent").height();
                $("#legendContent").remove();
                $('#mappingCanvas').append("<div id=\"legendContent\" title=\"Map Legend\" class=\"mappingLegendPropsDialog\">"+contents+"</div>");
                $("#legendContent").puidialog({
                    showEffect: 'fade',
                    hideEffect: 'fade',
                    minimizable: true,
                    maximizable: false,
                    modal: false,
                    location: "left",
                    width: (dialogWidth > 800) ? 800 : (dialogWidth < 200) ? 200 : dialogWidth,
                    height: (dialogHeight > 600) ? 600 : (dialogHeight < 100) ? 100 : dialogHeight,
                    afterHide: function(event) {
                        $("#legendContent").remove();
                    }
                });
                $("#legendContent").puidialog('show');
                $("#legendContent").children().css({"color":"#ffffff"});
                $("#legendContent").children(".pui-dialog-titlebar").children(".pui-dialog-titlebar-maximize").remove();
                $("#legendContent").children(".pui-dialog-titlebar").children(".pui-dialog-titlebar-close").remove();
                $("#legendContent").children(".pui-dialog-titlebar").children(".pui-dialog-titlebar-minimize").remove();
            };

            this.legendClose = function(id) {
                $("#legendContent").remove();
            };

            this.getMappyLayoutDivSize = function() {
                var mappyLayoutDiv = document.getElementById("mappyLayout");
                if (mappyLayoutDiv!=null)
                    return {
                        width:mappyLayoutDiv.clientWidth,
                        height:mappyLayoutDiv.clientHeight
                    };
                else
                    return {
                        width:0,
                        height:0
                    };
            };

            this.getMappyCanvasDivSize = function () {
                var mappyCanvasDiv = document.getElementById("mappyCanvas");
                if (mappyCanvasDiv!=null) {
                    return {
                        width: mappyCanvasDiv.clientWidth,
                        height: mappyCanvasDiv.clientHeight
                    }
                } else {
                    throw   {
                        severity: 'error',
                        summary: 'HTML rendering error',
                        detail: 'Unable to find the mappyCanvas div on this HTML page !',
                        sticky: true
                    };
                }
            };

            this.initGrowlMsgs = function(div) {
                growlDiv = div;
                $(growlDiv).puigrowl();
            };

            var pushMsgToGrowl = function(msg) {
                if (msg!=null) {
                    if (notifyErrs && msg.severity==="error")
                        msgsToGrowl.push(msg);
                    else if (notifyWarn && msg.severity==="warn")
                        msgsToGrowl.push(msg);
                    else if (notifyInfo && msg.severity==="info")
                        msgsToGrowl.push(msg);
                }
            };

            this.growlMsgs = function(msg) {
                pushMsgToGrowl(msg);
                $(growlDiv).puigrowl('show',msgsToGrowl);
                msgsToGrowl.length=0;
            };

            this.initErrorBox = function(divErrorBox, divErrorMsg) {
                errorBoxDiv = divErrorBox;
                errorMsgDiv = divErrorMsg;
            };

            this.showErrorBox = function(msg) {
                $(errorMsgDiv).empty();
                $(errorMsgDiv).append(msg);
                $(errorBoxDiv).show()
            };

            this.hideErrorBox = function() {
                $(errorBoxDiv).hide()
            };

            this.addMsgToGrowl = function(msg) {
                pushMsgToGrowl(msg);
            };

            this.setNotifyInfo = function(notify) {
                notifyInfo=notify;
            };

            this.getNotifyInfo = function() {
                return notifyInfo;
            };

            this.setNotifyWarn = function(notify) {
                notifyWarn=notify;
            };

            this.getNotifyWarn = function() {
                return notifyWarn;
            };

            this.setNotifyErrs = function(notify) {
                notifyErrs=notify;
            };

            this.getNotifyErrs = function() {
                return notifyErrs;
            };
        }
        return helper;
    }
);
