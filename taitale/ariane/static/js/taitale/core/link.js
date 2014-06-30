// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Link                            │ \\
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
        'taitale-helper'
    ],
    function(helper){

        function link(id_, epS_, epT_, transport_, line_, bg_) {
            this.helper       = new helper();
            this.id           = id_;
            this.epSource     = epS_;
            this.epTarget     = epT_;
            this.line         = line_;
            this.lineColor    = line_;
            this.bg           = bg_;
            this.bgColor      = bg_;
            this.transport    = transport_;
            this.multicastBus = null;
            this.bpMulticast  = null; // circle or {x:value,y:value}

            this.getEpSource = function() {
                return this.epSource;
            };

            this.getEpTarget = function() {
                return this.epTarget;
            };

            this.getTransport = function(){
                return this.transport;
            };

            this.getMulticastBus = function() {
                return this.multicastBus;
            };

            this.getBPMulticast = function() {
                return this.getBPMulticast();
            };

            this.setBPMulticast = function(bp) {
                this.bpMulticast = bp;
            };

            this.getPeerEp = function(endpoint) {
                if (endpoint!=this.epSource)
                    return this.epSource;
                else
                    return this.epTarget;
            };

            this.getLine = function() {
                return this.line;
            };

            this.setLine = function(line_) {
                this.line = line_;
            };

            this.getBg = function() {
                return this.bg;
            };

            this.setBg = function(bg_) {
                this.bg = bg_;
            };

            this.toCompute = function() {
                if (this.transport!=null && !this.transport.isMulticast() && this.epSource!=null && this.epTarget!=null) {
                    return {
                        from: this.epSource.circle,
                        to: this.epTarget.circle,
                        line: this.line,
                        bg: this.bg
                    };
                } else if (this.transport!=null && this.transport.isMulticast() && this.epSource!=null) {
                    if (this.bpMulticast!=null && this.bpMulticast.x!=null && this.bpMulticast.y!=null)
                        this.bpMulticast = this.multicastBus.mbus.getBindedCircle(this.bpMulticast);
                    if (this.bpMulticast!=null) {
                        //this.helper.debug("[link.toCompute] New bpMulticast - coord: {" +
                        //    this.bpMulticast.attr("cx") + "," +
                        //    this.bpMulticast.attr("cy") + "} - transform:" + this.bpMulticast.attr("transform").toString());
                        return {
                            from: this.epSource.circle,
                            to: this.bpMulticast,
                            line: this.line,
                            bg: this.bg
                        };
                    } else {
                        this.helper.addMsgToGrowl(
                            {
                                severity: 'warn',
                                summary: 'Link warning',
                                detail: 'Link multicast binded point is invalid. Multicast link will be ignored.',
                                sticky: true
                            });
                        return null;
                    }
                } else {
                    throw {
                        severity: 'error',
                        summary: 'Link error',
                        detail: 'Link description is invalid',
                        sticky: true
                    };
                }
            };

            this.toUpdate = function(up) {
                if (this.transport!=null && !this.transport.isMulticast() && this.epSource!=null && this.epTarget!=null) {
                    this.epSource.circle = up.from;
                    this.epTarget.circle = up.to;
                    this.setLine(up.line);
                    this.setBg(up.bg);
                } else if (this.transport!=null && this.transport.isMulticast() && this.epSource!=null && this.bpMulticast!=null) {
                    this.epSource.circle = up.from;
                    this.bpMulticast = up.to;
                    this.setLine(up.line);
                    this.setBg(up.bg);
                    //this.helper.debug("[link.toUpdate] Updated bpMulticast - coord: {".
                    //    concat(this.bpMulticast.attr("cx")).concat(",").
                    //    concat(this.bpMulticast.attr("cy")).concat("} - transform:").
                    //    concat(this.bpMulticast.attr("transform").toString()));
                } else {
                    throw {
                        severity: 'error',
                        summary: 'Link error',
                        detail: 'Link description is invalid',
                        sticky: true
                    };
                }
            };

            this.print = function(r) {
                /*
                 * reinit line for map refresh
                 */
                this.line = this.lineColor;
                this.bg = this.bgColor;

                /*
                 * print
                 */
                var up = r.link(this.toCompute());
                if (typeof up != 'undefined') {
                    //this.helper.debug(up);
                    this.toUpdate(up);
                }
            };

            this.toString = function () {
                return "Link {\n r: " + this.r
                    + "\n epSource: " + this.epSource
                    + "\n epTarget: " + this.epTarget
                    + "\n transport:" + this.transport
                    + "\n line: " + this.line
                    + "\n bg: " + this.bg
                    + "\n}";
            };

            this.linkEp = function() {
                if (this.transport!=null && !this.transport.isMulticast() && this.epSource!=null && this.epTarget!=null) {
                    this.epSource.calcLinkAvgPoz(this);
                    this.epTarget.calcLinkAvgPoz(this);
                } else if (this.transport!=null && this.transport.isMulticast() && this.epSource!=null) {
                    this.epSource.chooseMulticastTargetBindingPointAndCalcPoz(this);
                    this.multicastBus.pushBindedLink(this);
                } else {
                    throw {
                        stack: new Error("Link error - description invalid").stack,
                        severity: 'error',
                        summary: 'Link error',
                        detail: 'Link description is invalid',
                        sticky: true
                    };
                }
            };

            //this.helper.debug("[link]NEW : " + this.toString());
            if (this.transport!=null && !this.transport.isMulticast() && this.epSource!=null && this.epTarget!=null) {
                this.epSource.pushLink(this);
                this.epTarget.pushLink(this);
                this.epSource.epNode.pushLinkedNode(this.epTarget.epNode);
                this.epTarget.epNode.pushLinkedNode(this.epSource.epNode);
            } else if (this.transport!=null && this.transport.isMulticast() && this.epSource!= null) {
                this.epSource.pushLink(this);
                this.multicastBus = this.transport.defineMulticastBus(this.epSource.epNode.nodeContainer.localisation);
                this.multicastBus.pushLinkedTreeObject(this.epSource.epNode.nodeContainer);
                this.epSource.epNode.pushLinkedBus(this.multicastBus);
            } else {
                throw {
                    stack: new Error("Link error - description invalid").stack,
                    severity: 'error',
                    summary: 'Link error',
                    detail: 'Link description is invalid',
                    sticky: true
                };
            }
        }

        return link;
    });