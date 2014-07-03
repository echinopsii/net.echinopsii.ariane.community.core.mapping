/**
 *  Raphaël-ZPD: A zoom/pan/drag plugin for Raphaël.
 * ==================================================
 *
 * This code is licensed under the following BSD license:
 *
 * Copyright 2010 Gabriel Zabusek <gabriel.zabusek@gmail.com> (Interface and feature extensions and modifications). All rights reserved.
 * Copyright 2010 Daniel Assange <somnidea@lemma.org> (Raphaël integration and extensions). All rights reserved.
 * Copyright 2009-2010 Andrea Leofreddi <a.leofreddi@itcharm.com> (original author). All rights reserved.
 * Copyright 2013 Mathilde Ffrench <ffrench.mathilde@gmail.com> (Integration with taitale mapping lib & RequireJS)
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Andrea Leofreddi ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Andrea Leofreddi OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Andrea Leofreddi.
 */


define(
    [
        'raphael'
    ],
    function (Raphael) {

        var raphaelZPDId = 0,
            zpdOffsetX   = 0,
            zpdOffsetY   = 0,
            currentZoom  = 0,
            mwdelta      = 0.05,
            me = null;

        //function logOnFirbugConsole(stringToLog) {
        //    if (typeof console != "undefined") {
        //        console.log(stringToLog);
        //    }
        //}

        this.RaphaelZPD = function(raphaelPaper, o, map) {
            function supportsSVG() {
                return document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#BasicStructure", "1.1");
            }

            if (!supportsSVG()) {
                //noinspection JSConstructorReturnsPrimitive
                return null;
            }

            me = this;

            me.initialized = false;
            me.opts = {
                zoom: true, pan: true, drag: true, // Enable/disable core functionalities.
                zoomThreshold: null // Zoom [out, in] boundaries. E.g [-100, 10].
            };

            me.id   = ++raphaelZPDId;
            me.root = raphaelPaper.canvas;
            me.map = map;
            me.moveX = 0;
            me.moveY = 0;

            me.gelem = document.createElementNS('http://www.w3.org/2000/svg', 'g');
            me.gelem.id = 'viewport'+me.id;
            me.root.appendChild(me.gelem);

            function overrideElements(paper) {
                var elementTypes = ['circle', 'rect', 'ellipse', 'image', 'text', 'path'];
                for(var i = 0; i < elementTypes.length; i++) {
                    overrideElementFunc(paper, elementTypes[i]);
                }
            }

            function overrideElementFunc(paper, elementType) {
                paper[elementType] = function(oldFunc) {
                    return function() {
                        var element = oldFunc.apply(paper, arguments);
                        element.gelem = me.gelem;
                        me.gelem.appendChild(element.node);
                        return element;
                    };
                }(paper[elementType]);
            }

            overrideElements(raphaelPaper);

            function transformEvent(evt) {
                if (typeof evt.clientX != "number") return evt;

                var svgDoc = evt.target.ownerDocument;

                var g = svgDoc.getElementById("viewport"+me.id);

                var p = me.getEventPoint(evt);

                //noinspection JSUnresolvedFunction
                p = p.matrixTransform(g.getCTM().inverse());

                evt.zoomedX = p.x;
                evt.zoomedY = p.y;

                return evt;
            }

            var events = ['click', 'dblclick', 'mousedown', 'mousemove', 'mouseout', 'mouseover', 'mouseup', 'touchstart', 'touchmove', 'touchend', 'orientationchange', 'touchcancel', 'gesturestart', 'gesturechange', 'gestureend'];

            events.forEach(function(eventName) {
                var oldFunc = Raphael.el[eventName];
                Raphael.el[eventName] = function(fn, scope) {
                    if (fn === undefined) return null;
                    var wrap = function(evt) {
                        return fn.apply(this, [transformEvent(evt)]);
                    };
                    return oldFunc.apply(this, [wrap, scope]);
                }
            });

            this.ZPDClearEvents = function () {
                events.forEach(function(eventName) {
                    me.root.removeEventListener(eventName, me.handleMouseWheel, false);
                });
                raphaelZPDId--;
                zpdOffsetX=0;
                zpdOffsetY=0;
                currentZoom=0;
            };

            this.ZPDRefreshLastOffset = function(x, y) {
                var svgDoc = document;
                var g = svgDoc.getElementById("viewport"+me.id);
                zpdOffsetX = x;
                zpdOffsetY = y;
                //noinspection JSUnresolvedFunction
                me.stateTf = g.getCTM().inverse();
                me.setCTM(g, me.stateTf.inverse().translate(x, y));
            };

            var scale = function(z, pt, svgDoc) {
                var g = svgDoc.getElementById("viewport"+me.id);
                //noinspection JSUnresolvedFunction
                var p = pt.matrixTransform(g.getCTM().inverse());

                if (!me.isEditionMode()){
                    //noinspection JSUnresolvedFunction
                    var k = me.root.createSVGMatrix().translate(p.x, p.y).scale(z).translate(-p.x, -p.y);
                }
                /*
                 else {
                 var k = me.root.createSVGMatrix().scale(z);
                 }
                 */
                //noinspection JSUnresolvedFunction
                me.setCTM(g, g.getCTM().multiply(k));

                if (!me.stateTf)
                { //noinspection JSUnresolvedFunction
                    me.stateTf = g.getCTM().inverse();
                }

                //noinspection JSUnresolvedFunction
                me.stateTf = me.stateTf.multiply(k.inverse());
            };

            this.ZPDScaleTo = function(delta, svgDoc, pt) {
                var dfactor = 1;

                //logOnFirbugConsole("delta : " + delta);
                if (delta > 0) {
                    if (me.opts.zoomThreshold)
                        if (me.opts.zoomThreshold[1] <= me.zoomCurrent) return;
                    if (delta != mwdelta) {
                        dfactor = Math.round(Math.log(1+delta)/Math.log(1+mwdelta));
                        me.zoomCurrent += dfactor;
                        delta = mwdelta;
                    } else {
                        me.zoomCurrent++;
                    }
                } else {
                    if (me.opts.zoomThreshold)
                        if (me.opts.zoomThreshold[0] >= me.zoomCurrent) return;
                    if (delta != -mwdelta) {
                        dfactor = Math.round(Math.log(1+delta)/Math.log(1-mwdelta));
                        me.zoomCurrent -= dfactor;
                        delta = -mwdelta;
                    } else {
                        me.zoomCurrent--;
                    }
                }

                //logOnFirbugConsole("dfactor: " + dfactor);
                //logOnFirbugConsole("delta:" + delta + " ; zoomCurrent:" + me.zoomCurrent);

                //logOnFirbugConsole("[RaphaelZPD.zoomer] zoom:"+me.zoomCurrent);
                var z = 1 + delta; // delta on mousewheel : +/- 0.05
                currentZoom += delta;
                currentZoom = Math.round(currentZoom*100)/100;
                //logOnFirbugConsole("[RaphaelZPD.zoomer]zoom factor:"+currentZoom+","+z);

                if (dfactor > 1)
                    for (var i = 0, ii = dfactor; i < ii; i++)
                        scale(z,pt,svgDoc);
                else
                    scale(z,pt,svgDoc);

            };

            me.state = 'none';
            me.stateTarget = null;
            me.stateOrigin = null;
            me.stateTf = null;
            me.zoomCurrent = 0;

            if (o) {
                for (var key in o) {
                    //noinspection JSUnfilteredForInLoop
                    if (me.opts[key] !== undefined) {
                        //noinspection JSUnfilteredForInLoop
                        me.opts[key] = o[key];
                    }
                }
            }

            /**
             * Handler registration
             */
            me.setupHandlers = function() {
                me.root.onmousedown = me.handleMouseDown;
                me.root.onmousemove = me.handleMouseMove;
                me.root.onmouseup   = me.handleMouseUp;

                me.root.onmouseout = me.handleMouseUp; // Decomment me to stop the pan functionality when dragging out of the SVG element

                if (navigator.userAgent.toLowerCase().indexOf('webkit') >= 0)
                    me.root.addEventListener('mousewheel', me.handleMouseWheel, false); // Chrome/Safari
                else
                    me.root.addEventListener('DOMMouseScroll', me.handleMouseWheel, false); // Others
            };

            /**
             * Instance an SVGPoint object with given event coordinates.
             */
            me.getEventPoint = function(evt) {
                //noinspection JSUnresolvedFunction
                var p = me.root.createSVGPoint();

                p.x = evt.clientX;
                p.y = evt.clientY;

                return p;
            };

            me.isMapObjectMoving = function() {
                return me.map.isMapElementMoving();
            };

            me.isEditionMode = function() {
                return me.map.isEditionMode();
            };

            /**
             * Sets the current transform matrix of an element.
             */
            me.setCTM = function(element, matrix) {
                var s = "matrix(" + matrix.a + "," + matrix.b + "," + matrix.c + "," + matrix.d + "," + matrix.e + "," + matrix.f + ")";

                element.setAttribute("transform", s);
            };

            /**
             * Dumps a matrix to a string (useful for debug).
             */
            me.dumpMatrix = function(matrix) {
                return "[ " + matrix.a + ", " + matrix.c + ", " + matrix.e + "\n  " + matrix.b + ", " + matrix.d + ", " + matrix.f + "\n  0, 0, 1 ]";
            };

            /**
             * Sets attributes of an element.
             */
            me.setAttributes = function(element, attributes) {
                for (var i in attributes)
                    { //noinspection JSUnfilteredForInLoop
                        element.setAttributeNS(null, i, attributes[i]);
                    }
            };

            var zoomer = function(evt,delta) {
                if (evt.preventDefault)
                    evt.preventDefault();

                evt.returnValue = false;

                var svgDoc = evt.target.ownerDocument;

                var pt = me.getEventPoint(evt);
                //noinspection JSUnresolvedFunction

                me.ZPDScaleTo(delta, svgDoc, pt);
            };

            /**
             * Handle mouse wheel event.
             */
            me.handleMouseWheel = function(evt) {
                if (!me.opts.zoom) return;

                var delta;

                if (evt.wheelDelta) {
                    if (evt.wheelDelta<0)
                        delta = -mwdelta;
                    else
                        delta = mwdelta;
                }
                else {
                    if (evt.detail<0)
                        delta = mwdelta;
                    else
                        delta = -mwdelta;
                }
                //logOnFirbugConsole("[RaphaelZPD.me.handleMouseWheel]delta:"+delta);

                // Compute new scale matrix in current mouse position
                if (!me.isEditionMode()) {
                    zoomer(evt,delta);
                }/* else {
                 if (Math.abs(me.zoomCurrent)<5) {
                 zoomer(evt,delta);
                 } else if ((me.zoomCurrent==-5 && delta > 0) || (me.zoomCurrent==5 && delta < 0)) {
                 zoomer(evt,delta);
                 }

                 }*/
            };

            /**
             * Handle mouse move event.
             */
            me.handleMouseMove = function(evt) {
                if (evt.preventDefault)
                    evt.preventDefault();

                evt.returnValue = false;

                var svgDoc = evt.target.ownerDocument;

                var g = svgDoc.getElementById("viewport"+me.id);

                var p;

                if (me.state == 'pan') {
                    // Pan mode
                    if (!me.opts.pan) return;
                    //logOnFirbugConsole("[RaphaelZPD.me.handleMouseMove]MouseMove => PAN");
                    if (!me.isMapObjectMoving()) {
                        var pt = me.getEventPoint(evt);
                        //noinspection JSUnresolvedFunction
                        p = pt.matrixTransform(me.stateTf);
                        me.setCTM(g, me.stateTf.inverse().translate(p.x - me.stateOrigin.x, p.y - me.stateOrigin.y));
                        me.moveX = p.x - me.stateOrigin.x;
                        me.moveY = p.y - me.stateOrigin.y;
                        //logOnFirbugConsole("[RaphaelZPD.me.handleMouseMove]Panning : {"+me.moveX+","+me.moveY+"}");
                    }
                } else if (me.state == 'move') {
                    // Move mode
                    if (!me.opts.drag) return;

                    //logOnFirbugConsole("[RaphaelZPD.me.handleMouseMove]MouseMove => MOVE");

                    //noinspection JSUnresolvedFunction
                    p = me.getEventPoint(evt).matrixTransform(g.getCTM().inverse());

                    //noinspection JSUnresolvedFunction
                    me.setCTM(me.stateTarget, me.root.createSVGMatrix().translate(p.x - me.stateOrigin.x, p.y - me.stateOrigin.y).multiply(g.getCTM().inverse()).multiply(me.stateTarget.getCTM()));

                    me.stateOrigin = p;
                }
            };

            /**
             * Handle click event.
             */
            me.handleMouseDown = function(evt) {
                if (evt.preventDefault)
                    evt.preventDefault();

                evt.returnValue = false;

                var svgDoc = evt.target.ownerDocument;

                var g = svgDoc.getElementById("viewport"+me.id);

                if (evt.target.tagName == "svg" || !me.opts.drag) {
                    // Pan mode
                    if (!me.opts.pan) return;

                    //logOnFirbugConsole("[RaphaelZPD.me.handleMouseMove]MouseDown => PAN");

                    me.state = 'pan';

                    //noinspection JSUnresolvedFunction
                    me.stateTf = g.getCTM().inverse();

                    //noinspection JSUnresolvedFunction
                    me.stateOrigin = me.getEventPoint(evt).matrixTransform(me.stateTf);
                } else {
                    // Move mode
                    if (!me.opts.drag || evt.target.draggable == false) return;

                    //logOnFirbugConsole("[RaphaelZPD.me.handleMouseMove]MouseDown => MOVE");

                    me.state = 'move';

                    me.stateTarget = evt.target;

                    //noinspection JSUnresolvedFunction
                    me.stateTf = g.getCTM().inverse();

                    //noinspection JSUnresolvedFunction
                    me.stateOrigin = me.getEventPoint(evt).matrixTransform(me.stateTf);
                }
            };

            /**
             * Handle mouse button release event.
             */
            me.handleMouseUp = function(evt) {
                if (evt.preventDefault)
                    evt.preventDefault();

                evt.returnValue = false;

                //var svgDoc = evt.target.ownerDocument;

                if ((me.state == 'pan' && me.opts.pan) || (me.state == 'move' && me.opts.drag)) {
                    // Quit pan mode
                    if (!me.isMapObjectMoving()) {
                        zpdOffsetX += me.moveX;
                        zpdOffsetY += me.moveY;
                        //logOnFirbugConsole("[RaphaelZPD.me.handleMouseUp]zpdOffset : {"+zpdOffsetX+","+zpdOffsetY+"}");
                    }
                    me.state = '';
                }
            };

            // end of constructor
            me.setupHandlers(me.root);
            me.initialized = true;
            return this;
        };

        Raphael.fn.ZPDPanTo = function(x, y) {
            //noinspection JSUnresolvedFunction
            if (me.gelem == null || me.gelem.getCTM() == null) {
                alert('failed');
            }

            //noinspection JSUnresolvedFunction
            var stateTf = me.gelem.getCTM().inverse();

            var svg = document.getElementsByTagName("svg")[0];

            //noinspection JSUnresolvedVariable
            if (!svg.createSVGPoint) alert("no svg");

            //noinspection JSUnresolvedFunction
            var p = svg.createSVGPoint();

            p.x = x;
            p.y = y;

            //noinspection JSUnresolvedFunction
            p = p.matrixTransform(stateTf);

            var element = me.gelem;
            var matrix = stateTf.inverse().translate(p.x, p.y);

            var s = "matrix(" + matrix.a + "," + matrix.b + "," + matrix.c + "," + matrix.d + "," + matrix.e + "," + matrix.f + ")";

            element.setAttribute("transform", s);
        };

        Raphael.fn.ZPDScaleTo = function(delta,x,y) {
            //noinspection JSUnresolvedFunction
            if (me.gelem == null || me.gelem.getCTM() == null) {
                alert('failed');
            }

            var svg = document.getElementsByTagName("svg")[0];

            //noinspection JSUnresolvedVariable
            if (!svg.createSVGPoint) alert("no svg");

            //noinspection JSUnresolvedFunction
            var pt = svg.createSVGPoint();

            pt.x = x;
            pt.y = y;

            me.ZPDScaleTo(delta, svg, pt);
        };

        Raphael.fn.ZPDNormalSize = function(x, y) {
            var delta;
            //logOnFirbugConsole("zoomCurrent : " + me.zoomCurrent);
            if (me.zoomCurrent > 0)
                delta = Math.exp(me.zoomCurrent*Math.log(1+mwdelta))  - 1;
            else if (me.zoomCurrent < 0)
                delta = 1 - Math.exp(me.zoomCurrent*Math.log(1-mwdelta));
            else
                delta = 0;
            //logOnFirbugConsole("delta : " + delta);
            if (delta!=0)
                this.ZPDScaleTo(-delta, x, y);
        };

        Raphael.fn.getZPDoffsets = function() {
            return {
                x: zpdOffsetX,
                y: zpdOffsetY
            }
        };

        Raphael.fn.getZPDCurrentZoom = function() {
            return currentZoom;
        };

        return this.RaphaelZPD;
});