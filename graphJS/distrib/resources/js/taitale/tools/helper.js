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
            growlDiv         = null,
            notifyInfo       = true,
            notifyWarn       = false,
            notifyErrs       = true;

        function helper() {

            var msgsToGrowl = sharedMsgToGrowl;

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

            this.getMappyLayoutDivSize = function() {
                var mappyLayoutDiv = document.getElementById("mappyLayout");
                if (mappyLayoutDiv!=null)
                    return {
                        width:mappyLayoutDiv.clientHeight,
                        height:mappyLayoutDiv.clientHeight
                    }
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
            }

            this.growlMsgs = function(msg) {
                pushMsgToGrowl(msg);
                $(growlDiv).puigrowl('show',msgsToGrowl);
                msgsToGrowl.length=0;
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
        };
        return helper;
    }
);