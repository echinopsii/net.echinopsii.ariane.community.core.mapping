// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Map matrix                      │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │										 											  │ \\
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
        'taitale-dictionaries',
        'taitale-map-splitter',
        'taitale-datacenter',
        'taitale-layoutntw-registries'
    ],
    function(dictionaries, mapSplitter, datacenter, ntwRegistries) {
        function mapMatrix(options) {
            var nbLines       = 0,
                nbColumns     = 0,
                rows          = [],
                contentWidth  = 0,
                contentHeight = 0;

            var ldatacenterSplitter = null, //for NTWWW map type
                layoutNtwRegistries = null;

            var dic  = new dictionaries();

            this.printMtx = function(r) {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++) {
                        rows[j][i].print(r);
                    }
                }
            };

            this.getMtxSize = function() {
                return {
                    x: nbColumns,
                    y: nbLines
                };
            };

            // A zone could be a DC or something else (to be defined)
            this.getZoneFromMtx = function (x,y) {
                return rows[y][x];
            };

            this.isMoving = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        if (rows[j][i].isMoving)
                            return true;
                    }
                }
                return false;
            };

            this.defineMtxZoneSize = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        rows[j][i].defineZoneObjectsSize();
                    }
                }
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        rows[j][i].defineZoneSize();
                    }
                }
            };

            this.defineMapContentSize = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    var tmpHeight = 0;
                    for (var j = 0, jj = nbLines; j < jj; j++) {
                        tmpHeight = tmpHeight + rows[j][i].getZoneSize().height;
                    }
                    if (tmpHeight > contentHeight)
                        contentHeight=tmpHeight;
                }
                for (var i = 0, ii = nbLines; i < ii ; i++) {
                    var tmpWidth = 0;
                    for (var j = 0, jj = nbColumns; j < jj; j++) {
                        tmpWidth = tmpWidth + rows[i][j].getZoneSize().width;
                    }
                    if (tmpWidth > contentWidth)
                        contentWidth = tmpWidth;
                }
            };

            this.getMapContentSize = function() {
                return {
                    width  : contentWidth,
                    height : contentHeight
                };
            };

            this.defineMtxZonePoz = function(borderSpan, zoneSpan) {
                var cursorWidth  = 0;
                for (var i = 0, ii = nbColumns; i < ii; i++) {
                    var cursorHeight = 0;
                    for (var j = 0, jj = nbLines; j < jj; j++) {
                        rows[j][i].setTopLeftCoord(borderSpan+zoneSpan*i+cursorWidth,borderSpan+zoneSpan*j+cursorHeight);
                        rows[j][i].definePoz();
                        cursorHeigth = cursorHeight + rows[j][i].getZoneSize().height;
                    }
                    cursorWidth = cursorWidth + rows[0][i].getZoneSize().width;
                }
            };

            this.populateLayoutRegistries = function(container) {
                var layout = options.getLayout();
                switch(layout) {
                    case dic.mapLayout.NTWWW:
                    default:
                        if (layoutNtwRegistries==null)
                            layoutNtwRegistries = new ntwRegistries();

                        if (ldatacenterSplitter==null)
                            ldatacenterSplitter = new mapSplitter();

                        var location = container.localisation;
                        if (!location) {
                            throw   {
                                severity: 'error',
                                summary: 'MAP Network parse error',
                                detail: 'Missing localisation data on container '.concat(container.getName()),
                                sticky: true
                            }
                        }

                        var datacenterDef = location.getDatacenter(),
                            areaDef       = location.getArea(),
                            lanDef        = location.getLan();

                        var isConnectedToUpArea    = false,
                            isConnectedToUpLan     = false,
                            isConnectedToDownArea  = false,
                            isConnectedToDownLan   = false,
                            isConnectedToLeftDC    = false,
                            isConnectedToLeftArea  = false,
                            isConnectedToLeftLan   = false,
                            isConnectedToRightDC   = false,
                            isConnectedToRightArea = false,
                            isConnectedToRightLan  = false,
                            isConnectedInsideArea  = false,
                            isConnectedInsideLan   = false;

                        if (container.getLinkedBus().length!=0)
                            isConnectedInsideArea=true;

                        var linkedContainers = container.getLinkedContainers();
                        for (var i = 0, ii = linkedContainers.length; i < ii; i++) {
                            var linkedLocation      = linkedContainers[i].localisation;
                            if (!location) {
                                throw   {
                                    severity: 'error',
                                    summary: 'MAP Network parse error',
                                    detail: 'Missing localisation data on container '.concat(linkedContainers[i].getName()),
                                    sticky: true
                                }
                            }

                            var linkedDatacenterDef = linkedLocation.getDatacenter(),
                                linkedLanDef        = linkedLocation.getLan();

                            /*
                             * define the current known linking data
                             */
                            if (location.equal(linkedLocation))
                                isConnectedInsideLan = true;
                            else if (location.equalArea(linkedLocation) && lanDef.lan!=linkedLanDef.lan) {
                                isConnectedInsideArea = true;
                            } else {
                                if (lanDef.type!=linkedLanDef.type) {
                                    switch(lanDef.type) {
                                        case dic.networkType.WAN:
                                            if (linkedLanDef.type===dic.networkType.MAN || linkedLanDef.type===dic.networkType.LAN) {
                                                isConnectedToDownArea = true;
                                                isConnectedToDownLan  = true;
                                            }
                                            break;
                                        case dic.networkType.MAN:
                                            if (linkedLanDef.type===dic.networkType.LAN) {
                                                isConnectedToDownArea = true;
                                                isConnectedToDownLan  = true;
                                            } else if (linkedLanDef.type===dic.networkType.WAN) {
                                                isConnectedToUpArea = true;
                                                isConnectedToUpLan  = true;
                                            }
                                            break;
                                        case dic.networkType.LAN:
                                            if (linkedLanDef.type===dic.networkType.WAN || linkedLanDef.type===dic.networkType.MAN) {
                                                isConnectedToUpArea = true;
                                                isConnectedToUpLan  = true;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (lanDef.dc!=linkedLanDef.dc) {
                                    var linkedLng = parseFloat(linkedDatacenterDef.gpsLng),
                                        localLng  = parseFloat(datacenterDef.gpsLng);
                                    if (linkedLng > localLng) {
                                        isConnectedToLeftDC   = true;
                                        isConnectedToLeftArea = true;
                                        isConnectedToLeftLan  = true;
                                    } else {
                                        isConnectedToRightDC   = true;
                                        isConnectedToRightArea = true;
                                        isConnectedToRightLan  = true;
                                    }
                                }
                            }
                        }

                        var dc   = layoutNtwRegistries.pushDatacenterIntoRegistry(datacenterDef, ldatacenterSplitter, options);
                        var area = layoutNtwRegistries.pushAreaIntoRegistry(areaDef, options);
                        var lan  = layoutNtwRegistries.pushLanIntoRegistry(lanDef, options);

                        container.layoutData =
                            {
                                dc: dc, area: area, lan: lan,
                                isConnectedToLeftDC: isConnectedToLeftDC, isConnectedToRightDC: isConnectedToRightDC,
                                isConnectedToLeftArea: isConnectedToRightArea, isConnectedToRightArea: isConnectedToRightArea,
                                isConnectedToLeftLan: isConnectedToLeftLan, isConnectedToRightLan: isConnectedToRightLan,
                                isConnectedToUpArea: isConnectedToUpArea, isConnectedToDownArea:isConnectedToDownArea,
                                isConnectedToUpLan: isConnectedToUpLan, isConnectedToDownLan:isConnectedToDownLan,
                                isConnectedInsideArea: isConnectedInsideArea, isConnectedInsideLan:isConnectedInsideLan
                            };

                        lan.setLayoutData(
                            {
                                isConnectedToLeftDC: isConnectedToLeftDC, isConnectedToRightDC: isConnectedToRightDC,
                                isConnectedToLeftArea: isConnectedToRightArea, isConnectedToRightArea: isConnectedToRightArea,
                                isConnectedToLeftLan: isConnectedToLeftLan, isConnectedToRightLan: isConnectedToRightLan,
                                isConnectedToUpArea: isConnectedToUpArea, isConnectedToDownArea:isConnectedToDownArea,
                                isConnectedToUpLan: isConnectedToUpLan, isConnectedToDownLan:isConnectedToDownLan,
                                isConnectedInsideArea: isConnectedInsideArea, isConnectedInsideLan:isConnectedInsideLan
                            }
                        );

                        break;
                }
            };

            this.addContainerZone = function(container) {
                var layout = options.getLayout();
                switch(layout) {
                    case dic.mapLayout.NTWWW:
                    default:
                        if (nbLines==0) {
                            rows[nbLines] = [];
                            nbLines++;
                        }

                        // in that case zone is a dc which contains areas that contains lans which finally contains the containers
                        var pivotDC         = container.layoutData.dc,
                            alreadyInserted = pivotDC.isInserted;

                        // if DC not inserted insert in the mtx
                        if (!alreadyInserted){
                            //pivotDC = layoutNtwRegistries.getDatacenterFromRegistry(geoDCLoc);

                            // in that NTW view DC shares the same line (user story RVRD 01).
                            // they are placed depending on their GPS longitude
                            nbColumns++;
                            for (var i=0, ii=nbColumns; i < ii; i++) {
                                var tmpDC = rows[0][i];
                                if (tmpDC != null) {
                                    var tmpLng = parseFloat(tmpDC.getGeoDCLoc().gpsLng),
                                        pvtLng = parseFloat(pivotDC.getGeoDCLoc().gpsLng);
                                    if (tmpLng > pvtLng) {
                                        rows[0][i]=pivotDC;
                                        pivotDC.isInserted=true;
                                        pivotDC=tmpDC;
                                    }
                                    //DONT'T ASK : SOLVE A STRANGE BEHAVIOR ON FIREFOX
                                    if (tmpLng > pvtLng) {
                                        ;
                                    }
                                } else {
                                    rows[0][i]=pivotDC;
                                    pivotDC.isInserted=true;
                                }
                            }
                        }
                        // finally push container area
                        container.layoutData.dc.pushContainerArea(container);
                        break;
                }
            };

            this.displayDC = function(display) {
                if (options.getLayout() === dic.mapLayout.NTWWW) {
                    for (var i= 0, ii=nbColumns; i < ii; i++) {
                        rows[0][i].displayDC(display);
                    }
                }
            };

            this.displayArea = function(display) {
                if (options.getLayout() === dic.mapLayout.NTWWW) {
                    for (var i= 0, ii=nbColumns; i < ii; i++) {
                        rows[0][i].displayArea(display);
                    }
                }
            };

            this.displayLan = function(display) {
                if (options.getLayout() === dic.mapLayout.NTWWW) {
                    for (var i= 0, ii=nbColumns; i < ii; i++) {
                        rows[0][i].displayLan(display);
                    }
                }
            };
        };

        return mapMatrix;
    });