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
                contentHeight = 0,
                topLeftX      = 0,
                topLeftY      = 0,
                bottomRightX  = 0,
                bottomRightY  = 0;

            var GLI_LINE = 0;
            var LDC_LINE = 1;
            
            var ldatacenterSplitter = null, //for NETL3P map type
                glintSplitter       = null, //for NETL3P map type
                layoutNtwRegistries = null;

            var dic  = new dictionaries();

            this.printMtx = function(r) {
                var i, ii, j, jj;
                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    for (j = 0, jj = nbLines; j < jj ; j++) {
                        if (rows[j].length != 0 && rows[j][i] != null)
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
            //noinspection JSUnusedGlobalSymbols
            this.getZoneFromMtx = function (x,y) {
                return rows[y][x];
            };

            this.isMoving = function() {
                var i, ii, j, jj;
                for (i = 0, ii = nbColumns; i < ii ; i++)
                    for (j = 0, jj = nbLines; j < jj ; j++ )
                        if (rows[j][i] != null && rows[j][i].isElemMoving())
                            return true;
                return false;
            };

            this.setEditionMode = function(editionMode) {
                var i, ii, j, jj;
                for (i = 0, ii = nbColumns; i < ii ; i++)
                    for (j = 0, jj = nbLines; j < jj ; j++ )
                        if (rows[j][i] != null)
                            rows[j][i].setEditionMode(editionMode);
            };

            this.defineMtxZoneMaxSize = function() {
                var i, ii, j, jj;
                for (j = 0, jj = nbLines; j < jj ; j++ )
                    if (rows[j].length != 0)
                        for (i = 0, ii = nbColumns; i < ii ; i++)
                            if (rows[j][i] != null) rows[j][i].defineZoneObjectsMaxSize();
                for (j = 0, jj = nbLines; j < jj ; j++ )
                    if (rows[j].length != 0)
                        for (i = 0, ii = nbColumns; i < ii ; i++)
                            if (rows[j][i] != null) rows[j][i].defineZoneMaxSize();
            };

            this.defineMtxZoneSize = function() {
                var i, ii, j, jj;
                ldatacenterSplitter.init();
                glintSplitter.init();
                for (j = 0, jj = nbLines; j < jj ; j++ )
                    if (rows[j].length != 0)
                        for (i = 0, ii = nbColumns; i < ii ; i++)
                            if (rows[j][i] != null) rows[j][i].defineZoneObjectsSize();
                for (j = 0, jj = nbLines; j < jj ; j++ )
                    if (rows[j].length != 0)
                        for (i = 0, ii = nbColumns; i < ii ; i++)
                            if (rows[j][i] != null) rows[j][i].defineZoneSize();
            };

            this.defineMapContentMaxSize = function() {
                var i, ii, j, jj;

                var tmpHeightL0 = 0, tmpHeightL1 = 0;
                if (rows[LDC_LINE].length != 0)
                    for (i = 0, ii = nbColumns; i < ii ; i++) {
                        if (rows[LDC_LINE][i] != null)
                            if (tmpHeightL1 < rows[LDC_LINE][i].getZoneMaxSize().height)
                                tmpHeightL1 = rows[LDC_LINE][i].getZoneMaxSize().height;
                    }
                if (rows[GLI_LINE].length != 0)
                    for (i = 0, ii = nbColumns; i < ii; i++)
                        if (rows[GLI_LINE][i] != null) tmpHeightL0 += rows[GLI_LINE][i].getZoneMaxSize().height;
                contentHeight = tmpHeightL0 + tmpHeightL1;

                contentWidth = 0;
                for (i = 0, ii = nbLines; i < ii ; i++) {
                    if (rows[i].length != 0) {
                        var tmpWidth = 0;
                        for (j = 0, jj = nbColumns; j < jj; j++)
                            if (rows[i][j] != null) tmpWidth = tmpWidth + rows[i][j].getZoneMaxSize().width;
                        if (tmpWidth > contentWidth)
                            contentWidth = tmpWidth;
                    }
                }
            };

            this.defineMapContentSize = function() {
                var i, ii, j, jj;

                var tmpHeightL0 = 0, tmpHeightL1 = 0;
                if (rows[LDC_LINE].length != 0)
                    for (i = 0, ii = nbColumns; i < ii ; i++) {
                        if (rows[LDC_LINE][i] != null)
                            if (tmpHeightL1 < rows[LDC_LINE][i].getZoneSize().height)
                                tmpHeightL1 = rows[LDC_LINE][i].getZoneSize().height;
                    }
                if (rows[GLI_LINE].length != 0)
                    for (i = 0, ii = nbColumns; i < ii; i++)
                        if (rows[GLI_LINE][i] != null) tmpHeightL0 += rows[GLI_LINE][i].getZoneSize().height;
                contentHeight = tmpHeightL0 + tmpHeightL1;

                contentWidth = 0;
                for (i = 0, ii = nbLines; i < ii ; i++) {
                    if (rows[i].length != 0) {
                        var tmpWidth = 0;
                        for (j = 0, jj = nbColumns; j < jj; j++)
                            if (rows[i][j] != null)
                                tmpWidth = tmpWidth + rows[i][j].getZoneSize().width;
                        if (tmpWidth > contentWidth)
                            contentWidth = tmpWidth;
                    }
                }
            };

            this.getMapContentSize = function() {
                return {
                    width  : contentWidth,
                    height : contentHeight
                };
            };

            this.getTopLeftCoords = function() {
                return {
                    topLeftX: topLeftX,
                    topLeftY: topLeftY
                }
            };

            this.getBottomRightCoords = function() {
                return {
                    bottomRightX: bottomRightX,
                    bottomRightY: bottomRightY
                }
            };

            this.defineMtxZoneFirstPoz = function(borderSpan, zoneSpan) {
                var i, ii, toplx;
                var cursorWidth  = 0;

                if (rows[GLI_LINE].length != 0) {
                    if (rows[LDC_LINE].length != 0) {
                        var maxL0Height = 0;
                        for (i = 0, ii = nbColumns; i < ii; i++) if (rows[GLI_LINE][i] != null && rows[GLI_LINE][i].getZoneMaxSize().height > maxL0Height) maxL0Height = rows[GLI_LINE][i].getZoneMaxSize().height;
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            if (rows[LDC_LINE][i] != null) {
                                rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan + maxL0Height + zoneSpan);
                                rows[LDC_LINE][i].defineIntermediatePoz();
                                cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneMaxSize().width;
                            }
                        }
                    }
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        if (rows[GLI_LINE][i] != null) {
                            toplx = (cursorWidth == 0) ? 0 : (borderSpan + zoneSpan * nbColumns + cursorWidth - rows[GLI_LINE][i].getZoneMaxSize().width)/2;
                            rows[GLI_LINE][i].setTopLeftCoord(toplx, borderSpan);
                            rows[GLI_LINE][i].defineIntermediatePoz();
                        }
                    }
                } else {
                    if (rows[LDC_LINE].length != 0) {
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            if (rows[LDC_LINE][i] != null) {
                                rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan);
                                rows[LDC_LINE][i].defineIntermediatePoz();
                                cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneMaxSize().width;
                            }
                        }
                    }
                }
            };

            this.optimizeMtxCoord = function() {
                var i, ii, j, jj;
                for (i = 0, ii = nbColumns; i < ii; i++)
                    for (j = 0, jj = nbLines; j < jj; j++)
                        if (rows[j].length != 0 && rows[j][i] != null)
                            rows[j][i].optimizeMtxCoord();
            };

            this.defineMtxZoneIntermediatePoz = function(borderSpan, zoneSpan) {
                var i, ii, toplx;
                var cursorWidth  = 0;

                if (rows[GLI_LINE].length != 0) {
                    if (rows[LDC_LINE].length != 0) {
                        var maxL0Height = 0;
                        for (i = 0, ii = nbColumns; i < ii; i++) if (rows[GLI_LINE][i] != null && rows[GLI_LINE][i].getZoneSize().height > maxL0Height) maxL0Height = rows[GLI_LINE][i].getZoneMaxSize().height;
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            if (rows[LDC_LINE][i] != null) {
                                rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan + maxL0Height + zoneSpan);
                                rows[LDC_LINE][i].defineIntermediatePoz();
                                cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                            }
                        }
                    }
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        if (rows[GLI_LINE][i] != null) {
                            toplx = (cursorWidth == 0) ? 0 : (borderSpan + zoneSpan * nbColumns + cursorWidth - rows[GLI_LINE][i].getZoneSize().width)/2;
                            rows[GLI_LINE][i].setTopLeftCoord(toplx, borderSpan);
                            rows[GLI_LINE][i].defineIntermediatePoz();
                        }
                    }
                } else {
                    if (rows[LDC_LINE].length != 0) {
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan);
                            rows[LDC_LINE][i].defineIntermediatePoz();
                            if (rows[LDC_LINE][i] != null) cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                        }
                    }
                }
            };

            this.defineMtxZoneFinalPoz = function(borderSpan, zoneSpan) {
                var i, ii, toplx;
                var cursorWidth  = 0;
                if (rows[GLI_LINE].length != 0) {
                    if (rows[LDC_LINE].length != 0) {
                        var maxL0Height = 0;
                        for (i = 0, ii = nbColumns; i < ii; i++) if (rows[GLI_LINE][i] != null && rows[GLI_LINE][i].getZoneSize().height > maxL0Height) maxL0Height = rows[GLI_LINE][i].getZoneMaxSize().height;
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            if (rows[LDC_LINE][i] != null) {
                                rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan + maxL0Height + zoneSpan);
                                rows[LDC_LINE][i].defineFinalPoz();
                                if (bottomRightX < borderSpan + zoneSpan * i + cursorWidth + rows[LDC_LINE][i].getZoneSize().width)
                                    bottomRightX = borderSpan + zoneSpan * i + cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                                if (bottomRightY < borderSpan + rows[LDC_LINE][i].getZoneSize().height)
                                    bottomRightY = borderSpan + rows[LDC_LINE][i].getZoneSize().height;
                                cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                            }
                        }
                    }
                    topLeftX = borderSpan;
                    topLeftY = borderSpan;
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        if (rows[GLI_LINE][i] != null) {
                            toplx = (cursorWidth == 0) ? 0 : (borderSpan + zoneSpan * nbColumns + cursorWidth - rows[GLI_LINE][i].getZoneSize().width)/2;
                            if (topLeftX > toplx) topLeftX = toplx;
                            rows[GLI_LINE][i].setTopLeftCoord(toplx, borderSpan);
                            rows[GLI_LINE][i].defineFinalPoz();
                            if (bottomRightX < borderSpan + rows[GLI_LINE][i].getZoneSize().width)
                                bottomRightX = borderSpan + rows[GLI_LINE][i].getZoneSize().width;
                            if (bottomRightY < borderSpan + rows[GLI_LINE][i].getZoneSize().height)
                                bottomRightY = borderSpan + rows[GLI_LINE][i].getZoneSize().height;
                        }
                    }
                } else {
                    if (rows[LDC_LINE].length != 0) {
                        topLeftX = borderSpan;
                        topLeftY = borderSpan;
                        for (i = 0, ii = nbColumns; i < ii; i++) {
                            rows[LDC_LINE][i].setTopLeftCoord(borderSpan + zoneSpan * i + cursorWidth, borderSpan);
                            rows[LDC_LINE][i].defineFinalPoz();
                            if (bottomRightX < borderSpan + zoneSpan * i + cursorWidth + rows[LDC_LINE][i].getZoneSize().width)
                                bottomRightX = borderSpan + zoneSpan * i + cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                            if (bottomRightY < borderSpan + rows[LDC_LINE][i].getZoneSize().height)
                                bottomRightY = borderSpan + rows[LDC_LINE][i].getZoneSize().height;
                            if (rows[LDC_LINE][i] != null) cursorWidth = cursorWidth + rows[LDC_LINE][i].getZoneSize().width;
                        }
                    }
                }
            };

            this.populateLayoutRegistries = function(container) {
                var layout = options.getLayout();
                //noinspection FallthroughInSwitchStatementJS
                switch(layout) {
                    case dic.mapLayout.NETL3P:
                    default:
                        if (layoutNtwRegistries==null)
                            layoutNtwRegistries = new ntwRegistries();

                        if (ldatacenterSplitter==null)
                            ldatacenterSplitter = new mapSplitter();
                        if (glintSplitter==null)
                            glintSplitter = new mapSplitter();

                        var location = container.localisation;
                        if (!location) {
                            throw   {
                                severity: 'error',
                                summary: 'MAP Network parse error',
                                detail: 'Missing localisation data on container '.concat(container.getName()),
                                sticky: true
                            }
                        }

                        var datacenterDef = location.getPLocation(),
                            areaDef       = location.getArea(),
                            lanDef        = location.getLan();
                        areaDef.dcname = datacenterDef.pname;
                        lanDef.dcname = datacenterDef.pname;

                        var isConnectedToUpDC      = false,
                            isConnectedToUpArea    = false,
                            isConnectedToUpLan     = false,
                            isConnectedToDownDC    = false,
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

                            var linkedPLocationDef = linkedLocation.getPLocation(),
                                linkedLanDef       = linkedLocation.getLan();

                            /*
                             * define the current known linking data
                             */
                            if (location.equal(linkedLocation))
                                isConnectedInsideLan = true;
                            else if (location.equalArea(linkedLocation) && lanDef.sname!=linkedLanDef.sname) {
                                isConnectedInsideArea = true;
                            } else {
                                if (lanDef.dc==linkedLanDef.dc && lanDef.ratype!=linkedLanDef.ratype) {
                                    switch(lanDef.ratype) {
                                        case dic.networkType.WAN:
                                            if (linkedLanDef.ratype===dic.networkType.MAN || linkedLanDef.ratype===dic.networkType.LAN) {
                                                isConnectedToDownArea = true;
                                                isConnectedToDownLan  = true;
                                            }
                                            break;
                                        case dic.networkType.MAN:
                                            if (linkedLanDef.ratype===dic.networkType.LAN) {
                                                isConnectedToDownArea = true;
                                                isConnectedToDownLan  = true;
                                            } else if (linkedLanDef.ratype===dic.networkType.WAN) {
                                                isConnectedToUpArea = true;
                                                isConnectedToUpLan  = true;
                                            }
                                            break;
                                        case dic.networkType.LAN:
                                            if (linkedLanDef.ratype===dic.networkType.WAN || linkedLanDef.ratype===dic.networkType.MAN) {
                                                isConnectedToUpArea = true;
                                                isConnectedToUpLan  = true;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                } else if (lanDef.dc!=linkedLanDef.dc) {
                                    if (lanDef.dc.pName.indexOf(dic.networkType.GLI) !== -1 || linkedLanDef.dc.pName.indexOf(dic.networkType.GLI) !== -1) {
                                        if (lanDef.dc.pName.indexOf(dic.networkType.GLI) !== -1) isConnectedToUpDC = true;
                                        else isConnectedToDownDC = true;
                                    } else {
                                        var linkedLng = parseFloat(linkedPLocationDef.gpsLng),
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
                        }

                        var dc ;
                        var area = layoutNtwRegistries.pushAreaIntoRegistry(areaDef, options);
                        var lan  = layoutNtwRegistries.pushLanIntoRegistry(lanDef, options);

                        if (datacenterDef.pname != null && datacenterDef.pname.indexOf(dic.networkType.GLI) !== -1) dc = layoutNtwRegistries.pushDatacenterIntoRegistry(datacenterDef, glintSplitter, options);
                        else dc = layoutNtwRegistries.pushDatacenterIntoRegistry(datacenterDef, ldatacenterSplitter, options);

                        container.layoutData =
                            {
                                dc: dc, area: area, lan: lan,
                                isConnectedToLeftDC: isConnectedToLeftDC, isConnectedToRightDC: isConnectedToRightDC,
                                isConnectedToUpDC: isConnectedToUpDC, isConnectedToDownDC: isConnectedToDownDC,
                                isConnectedToLeftArea: isConnectedToRightArea, isConnectedToRightArea: isConnectedToRightArea,
                                isConnectedToLeftLan: isConnectedToLeftLan, isConnectedToRightLan: isConnectedToRightLan,
                                isConnectedToUpArea: isConnectedToUpArea, isConnectedToDownArea:isConnectedToDownArea,
                                isConnectedToUpLan: isConnectedToUpLan, isConnectedToDownLan:isConnectedToDownLan,
                                isConnectedInsideArea: isConnectedInsideArea, isConnectedInsideLan:isConnectedInsideLan,
                                lanMtxCoord: null, lanInternalLinksWeight: 0, lanConnectedContainer: [], lanUpDownIdx: 0
                            };

                        lan.setLayoutData(
                            {
                                isConnectedToLeftDC: isConnectedToLeftDC, isConnectedToRightDC: isConnectedToRightDC,
                                isConnectedToUpDC: isConnectedToUpDC, isConnectedToDownDC: isConnectedToDownDC,
                                isConnectedToLeftArea: isConnectedToRightArea, isConnectedToRightArea: isConnectedToRightArea,
                                isConnectedToLeftLan: isConnectedToLeftLan, isConnectedToRightLan: isConnectedToRightLan,
                                isConnectedToUpArea: isConnectedToUpArea, isConnectedToDownArea: isConnectedToDownArea,
                                isConnectedToUpLan: isConnectedToUpLan, isConnectedToDownLan: isConnectedToDownLan,
                                isConnectedInsideArea: isConnectedInsideArea, isConnectedInsideLan: isConnectedInsideLan,
                                areaMtxCoord: null, areaInternalLinksWeight: 0, areaConnectedObject: [], averageLine: -1, areaPozFinal: false
                            }
                        );

                        break;
                }
            };

            this.addContainerZone = function(container) {
                var layout = options.getLayout();
                //noinspection FallthroughInSwitchStatementJS
                switch(layout) {
                    case dic.mapLayout.NETL3P:
                    default:
                        if (nbLines==0) {
                            rows[nbLines] = []; // FIRST LINE FOR GLOBAL INTERNET - MAYBE EMPTY
                            nbLines++;
                            rows[nbLines] = [];
                            nbLines++;
                        }

                        // in that case zone is a dc which contains areas that contains lans which finally contains the containers
                        var pivotDC         = container.layoutData.dc,
                            alreadyInserted = pivotDC.isInserted;

                        // if DC not inserted insert in the mtx
                        var i, ii;
                        if (pivotDC.pName.indexOf(dic.networkType.GLI) !== -1) {
                            // Global internet zone is placed on bottom of NTW view
                            if (alreadyInserted) {
                                for (i = 0, ii = nbColumns; i < ii; i++) {
                                    rows[GLI_LINE][i] = null
                                }
                            } else {
                                if (nbColumns == 0) {
                                    nbColumns++;
                                }
                            }
                            var middleColumn = Math.floor(nbColumns/2);
                            rows[GLI_LINE][middleColumn] = pivotDC;
                            pivotDC.isInserted = true;
                        } else {
                            if (!alreadyInserted) {
                                //pivotDC = layoutNtwRegistries.getDatacenterFromRegistry(geoDCLoc);

                                // in that NTW view DC shares the same line (user story RVRD 01).
                                // they are placed depending on their GPS longitude
                                nbColumns++;
                                for (i = 0, ii = nbColumns; i < ii; i++) {
                                    var tmpDC = rows[LDC_LINE][i];
                                    if (tmpDC != null && tmpDC.pName !== container.layoutData.dc.pName) {
                                        var tmpLng = parseFloat(tmpDC.getGeoDCLoc().gpsLng),
                                            pvtLng = parseFloat(pivotDC.getGeoDCLoc().gpsLng);
                                        if (tmpLng > pvtLng) {
                                            rows[LDC_LINE][i] = pivotDC;
                                            pivotDC.isInserted = true;
                                            pivotDC = tmpDC;
                                            pivotDC.isInserted = false;
                                        }
                                        //DONT'T ASK : SOLVE A STRANGE BEHAVIOR ON FIREFOX
                                        if (tmpLng > pvtLng) {
                                            ;
                                        }
                                    } else if (tmpDC == null && !pivotDC.isInserted) {
                                        rows[LDC_LINE][i] = pivotDC;
                                        pivotDC.isInserted = true;
                                    }
                                }
                            }
                        }
                        // finally push container area
                        container.layoutData.dc.pushContainerArea(container);
                        break;
                }
            };

            this.translate = function(mvx, mvy) {
                var i, ii;
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[GLI_LINE][i]!=null) rows[GLI_LINE][i].moveInit();
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[GLI_LINE][i]!=null) rows[GLI_LINE][i].move(mvx, mvy);
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[GLI_LINE][i]!=null) rows[GLI_LINE][i].up();
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[LDC_LINE][i]!=null) rows[LDC_LINE][i].moveInit();
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[LDC_LINE][i]!=null) rows[LDC_LINE][i].move(mvx, mvy);
                for (i=0, ii=nbColumns; i<ii; i++) if (rows[LDC_LINE][i]!=null) rows[LDC_LINE][i].up();
            };

            this.displayDC = function(display) {
                var i, ii;
                if (options.getLayout() === dic.mapLayout.NETL3P) {
                    for (i= 0, ii=nbColumns; i < ii; i++)
                        if (rows[LDC_LINE].length != 0 && rows[LDC_LINE][i] != null)
                            rows[LDC_LINE][i].displayDC(display);
                    if (rows[GLI_LINE].length != 0)
                        for (i= 0, ii=nbColumns; i < ii; i++)
                            if (rows[GLI_LINE][i]!=null)
                                rows[GLI_LINE][i].displayDC(display);
                }
            };

            this.displayArea = function(display) {
                var i, ii;
                if (options.getLayout() === dic.mapLayout.NETL3P) {
                    for (i= 0, ii=nbColumns; i < ii; i++)
                        if (rows[LDC_LINE].length != 0 && rows[LDC_LINE][i] != null)
                            rows[LDC_LINE][i].displayArea(display);
                }
            };

            this.displayLan = function(display) {
                var i, ii;
                if (options.getLayout() === dic.mapLayout.NETL3P) {
                    for (i= 0, ii=nbColumns; i < ii; i++)
                        if (rows[LDC_LINE].length != 0 && rows[LDC_LINE][i] != null)
                            rows[LDC_LINE][i].displayLan(display);
                }
            };
        }

        return mapMatrix;
    });
