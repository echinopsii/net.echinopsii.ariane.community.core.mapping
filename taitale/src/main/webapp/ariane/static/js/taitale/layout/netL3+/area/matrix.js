// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NETL3P module - Area                           │ \\
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
        'taitale-dictionaries',
        'taitale-helper'
    ],
    function (dictionary, helper) {

        var dic    = new dictionary();
        var FREE   = "FREE",
            LOCKED = "LOCKED";

        var LAN    = dic.areaObjType.LAN,
            BUS    = dic.areaObjType.BUS;

        function areaMatrix() {
            var helper_ = new helper();
            var nbLines             = 0,
                nbColumns           = 0,
                rows                = [],
                lineMaxHeight       = [],
                columnMaxWidth      = [],
                contentWidth        = 0,
                contentHeight       = 0;

            var upLine              = -1, // UP LINK ONLY
                upInternalLine      = -1, // UP & INTERNAL
                upMulticastL        = false, // MULTICAST BUS
                minMulticastL       = -1,
                maxMulticastL       = -1,
                downMulticastL      = false,
                downInternalLine    = -1, // DOWN & INTERNAL
                downLine            = -1; // DOWN LINK ONLY

            // PUSH LEFT/RIGHT BALANCER
            //noinspection JSUnusedLocalSymbols
            var pushUDonLeft         = false,
                pushInternalUDonLeft = false,
                pushInternaLudOnLeft = false,
                pushInternalOnLeft   = false;

            // COLUMNS SPLITTER TABLE
            var mtxColumnsSplitter  = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1];
            // COLUMNS SPLITTER TABLE IDX
            var minLeftUDC           = 0, // LANS WITH UP & DOWN LINKS ON LEFT
                maxLeftUDC           = 1,
                minInternalLeftUDC   = 2, // LANS WITH UP & DOWN & INTERNAL LINKS ON LEFT
                maxInternalLeftUDC   = 3,
                minInternalLefTudC   = 4, // LANS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON LEFT
                maxInternalLefTudC   = 5,
                minInternalLeftC     = 6, // LANS WITH INTERNAL LINKS ON LEFT
                maxInternalLeftC     = 7,
                minMulticastC        = 8, // MULTICAST BUS
                maxMulticastC        = 9,
                minInternalRightC    = 10,// LANS WITH INTERNAL LINKS ON RIGHT
                maxInternalRightC    = 11,
                minInternalRighTudC  = 12,// LANS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON RIGHT
                maxInternalRighTudC  = 13,
                minInternalRightUDC  = 14,// LANS WITH UP & DOWN & INTERNAL LINKS ON RIGHT
                maxInternalRightUDC  = 15,
                minRightUDC          = 16,// LANS WITH UP & DOWN LINKS ON RIGHT
                maxRightUDC          = 17;

            var lansList = [];

            var addLineToMtx = function(index) {
                var i,ii;
                if (index < nbLines) {
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        for (var j = index, jj = nbLines; j < jj; jj--) {
                            rows[i][jj] = rows[i][jj-1];
                            if (rows[i][jj]!==FREE && rows[i][jj]!==LOCKED && rows[i][jj]!=null)
                                rows[i][jj].obj.layoutData.areaMtxCoord= {x: jj, y: i};
                        }
                    }
                }
                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    if ((i>=mtxColumnsSplitter[minLeftUDC] && i<=mtxColumnsSplitter[maxLeftUDC]) ||
                        (i>=mtxColumnsSplitter[minInternalLeftUDC] && i<=mtxColumnsSplitter[maxInternalLeftUDC]) ||
                        (i>=mtxColumnsSplitter[minInternalRightUDC] && i<=mtxColumnsSplitter[maxInternalRightUDC]) ||
                        (i>=mtxColumnsSplitter[minRightUDC] && i<=mtxColumnsSplitter[maxRightUDC]))
                        rows[i][index] = LOCKED;
                    else
                        rows[i][index] = FREE;
                }
                nbLines++;
            };

            var removeLineFromMtx = function(index) {
                var i, ii, j, jj;
                for (i=0, ii=nbColumns; i < ii; i++) {
                    for (j=index, jj = nbLines; j < jj; j++) {
                        rows[i][j] = rows[i][j+1];
                        if (rows[i][j]!==FREE && rows[i][j]!==LOCKED && rows[i][j]!=null)
                            rows[i][j].obj.layoutData.areaMtxCoord= {x: j, y: i};
                    }
                }
                for (i=0, ii=nbColumns; i < ii; i++)
                    //    rows[i][nbLines] = null
                    rows[i].pop();
                nbLines--;
            };

            var addColumnToMtx = function(index,flag) {
                var i, ii, j, jj;
                if (index < nbColumns){
                    rows[nbColumns] = [];
                    for (i = index, ii=nbColumns; i < ii; ii--) {
                        rows[ii] = rows[ii-1];
                        for (j=0, jj=nbLines; j<jj; j++) {
                            if (rows[ii][j]!==FREE && rows[ii][j]!==LOCKED && rows[ii][j]!=null)
                                rows[ii][j].obj.layoutData.areaMtxCoord= {x: j, y: ii};
                        }
                    }
                }
                rows[index] = [];
                for(i = 0, ii = nbLines; i < ii ; i++) {
                    rows[index][i] = flag;
                }
                nbColumns++;
            };

            var removeColumnFromMtx = function(index) {
                var i, ii;
                for (i=index, ii=nbColumns; i < ii; i++)
                    rows[i] = rows[i+1]
                rows.pop();
                nbColumns--;
            };

            var cleanFinalMatrix = function() {
                var i, ii, j, jj;
                var linesIdxToBeRemoved = [], columnsIdxToBeRemoved = [];
                for (i=0, ii=nbLines; i < ii; i++) {
                    var lineToBeRemoved = true;
                    for (j = 0, jj = nbColumns; j < jj; j++) {
                        if (rows[j][i] != null && rows[j][i] !== FREE && rows[j][i] !== LOCKED) {
                            lineToBeRemoved = false;
                            break;
                        }
                    }
                    if (lineToBeRemoved)
                        linesIdxToBeRemoved.push(i)
                }
                for (i=0, ii=linesIdxToBeRemoved.length; i<ii; i++)
                    removeLineFromMtx(linesIdxToBeRemoved[i]-i);

                for (i=0, ii=nbColumns; i < ii; i++) {
                    var columnToBeRemoved = true;
                    for (j=0, jj=nbLines; j < jj; j++) {
                        if (rows[i][j]!=null && rows[i][j]!==FREE && rows[i][j]!==LOCKED) {
                            columnToBeRemoved = false;
                            break;
                        }
                    }
                    if (columnToBeRemoved)
                        columnsIdxToBeRemoved.push(i)
                }
                for (i=0, ii=columnsIdxToBeRemoved.length; i<ii; i++)
                    removeColumnFromMtx(columnsIdxToBeRemoved[i]-i);
            };

            var isColumnFreeFromMinToMax = function(columnIdx, minLine, maxLine) {
                var ret = true;
                for (var j = minLine, jj = maxLine; j <= jj; j++) {
                    if (rows[columnIdx][j]!=FREE) {
                        ret = false;
                        break;
                    }
                }
                return ret;
            };

            /*
            var isLineFreeFromMinToMax = function(lineIdx, minColumn, maxColumn) {
                var ret = true;
                for (var j = minColumn, jj = maxColumn; j <= jj; j++) {
                    if (rows[j][lineIdx]!=FREE) {
                        ret = false;
                        break;
                    }
                }
                return ret;
            };
            */

            var getFreeColumnFromTo = function(minL,maxL,minC,maxC) {
                var column = -1;
                for (var i = minC, ii=maxC; i<=ii; i++) {
                    if (isColumnFreeFromMinToMax(i,minL,maxL)) {
                        column=i;
                        break;
                    }
                }
                return column;
            };

            var getFreeBlockColumn = function(lineIdx,minC,maxC) {
                var column = -1;
                for (var i=minC, ii=maxC; i<=ii;i++) {
                    if (rows[i][lineIdx]===FREE) {
                        column=i;
                        break;
                    }
                }
                return column;
            };

            var getFreeOrNonFinalBlockColumn = function(lineIdx,minC,maxC) {
                var column = -1;
                for (var i=minC, ii=maxC; i<=ii;i++) {
                    if (rows[i][lineIdx]===FREE || (rows[i][lineIdx]!==FREE && rows[i][lineIdx]!==LOCKED && rows[i][lineIdx]!=null && !rows[i][lineIdx].obj.layoutData.areaPozFinal)) {
                        column=i;
                        break;
                    }
                }
                return column;
            };

            var getColumnFromInitializedArea = function(minSplitterIdx, maxSplitterIdx) {
                var column = -1;
                var i,ii;
                for (i = minSplitterIdx+2, ii = mtxColumnsSplitter.length-1; i<ii; i+=2){
                    if (mtxColumnsSplitter[i]!=-1 && mtxColumnsSplitter[i+1]!=-1) {
                        column = mtxColumnsSplitter[i];
                        mtxColumnsSplitter[minSplitterIdx]=column;
                        mtxColumnsSplitter[maxSplitterIdx]=column;
                        addColumnToMtx(column,FREE);
                        break;
                    }
                }
                if (column==-1) {
                    for (i = maxSplitterIdx-2, ii=1; i>ii; i-=2) {
                        if (mtxColumnsSplitter[i]!=-1 && mtxColumnsSplitter[i-1]!=-1) {
                            column = mtxColumnsSplitter[i]+1;
                            mtxColumnsSplitter[minSplitterIdx]=column;
                            mtxColumnsSplitter[maxSplitterIdx]=column;
                            addColumnToMtx(column,FREE);
                            break;
                        }
                    }
                } else {
                    for (i = maxSplitterIdx+1, ii = mtxColumnsSplitter.length; i<ii; i++){
                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                    }
                }
                return column;
            };

            var getNewFUpDownColumn = function(minLeft,maxLeft,minRight,maxRight,boolLeftRight) {
                var column = -1;
                if (mtxColumnsSplitter[minLeft]==-1 && mtxColumnsSplitter[maxLeft]==-1) {
                    if (nbColumns==0 && nbLines==0) {
                        column = ++mtxColumnsSplitter[minLeft];
                        mtxColumnsSplitter[maxLeft]++;
                        addColumnToMtx(column,LOCKED);
                        addLineToMtx(column);
                    } else {
                        column=getColumnFromInitializedArea(minLeft,maxLeft);
                    }
                } else if (mtxColumnsSplitter[minRight]==-1 && mtxColumnsSplitter[maxRight]==-1) {
                    if (nbColumns==0 && nbLines==0) {
                        column=++mtxColumnsSplitter[minRight];
                        mtxColumnsSplitter[maxRight]++;
                        addColumnToMtx(column,LOCKED);
                    } else {
                        column=getColumnFromInitializedArea(minRight,maxRight);
                    }
                } else {
                    if (!boolLeftRight) {
                        //noinspection JSUnusedAssignment
                        boolLeftRight=true;
                        column=++mtxColumnsSplitter[maxRight];
                        addColumnToMtx(column,LOCKED);
                    } else {
                        //noinspection JSUnusedAssignment
                        boolLeftRight=false;
                        column=mtxColumnsSplitter[minLeft];
                        for (var i = minLeft, ii = mtxColumnsSplitter.length; i<ii; i++){
                            if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                        }
                        addColumnToMtx(column,LOCKED);
                    }
                }
                return column;
            };


            var getNewUpDownColumn = function() {
                return getNewFUpDownColumn(minLeftUDC,maxLeftUDC,minRightUDC,maxRightUDC,pushUDonLeft);
            };

            var getNewInternalUpDownColumn = function() {
                return getNewFUpDownColumn(minInternalLeftUDC,maxInternalLeftUDC,minInternalRightUDC,maxInternalRightUDC,pushInternalUDonLeft)
            };

            var getUpOrDownFreeBlockColumn = function(minL,maxL) {
                var column=-1;
                //CHECK IF THERE IS FREE UP BLOCK IN MULTICAST AREA
                if (mtxColumnsSplitter[minMulticastC]!=-1 && mtxColumnsSplitter[maxMulticastC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT AREA
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC]);
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT AREA
                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL, maxL, mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT UP or DOWN &/or INTERNAL AREA
                if (mtxColumnsSplitter[minInternalLefTudC]!=-1 && mtxColumnsSplitter[maxInternalLefTudC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalLefTudC],mtxColumnsSplitter[maxInternalLefTudC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalLefTudC],mtxColumnsSplitter[maxInternalLefTudC]);
                } else {
                    if (column==-1)
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalLefTudC,maxInternalLefTudC);
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT UP or DOWN &/or INTERNAL AREA
                if (mtxColumnsSplitter[minInternalRighTudC]!=-1 && mtxColumnsSplitter[maxInternalRighTudC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalRighTudC],mtxColumnsSplitter[maxInternalRighTudC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalRighTudC],mtxColumnsSplitter[maxInternalRighTudC]);
                } else {
                    if (column==-1)
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalRighTudC,maxInternalRighTudC);
                }

                // IF NO BLOCK FOUNDED THEN CREATE A NEW COLUMN IN LEFT|RIGHT UP or DOWN &/or INTERNAL AREA
                if (column==-1) {
                    if (!pushInternaLudOnLeft) {
                        pushInternaLudOnLeft=true;
                        column=++mtxColumnsSplitter[maxInternalRighTudC];
                        addColumnToMtx(column,FREE);
                    } else {
                        pushInternaLudOnLeft=false;
                        column=mtxColumnsSplitter[minInternalLefTudC];
                        for (var i = minInternalLefTudC, ii = mtxColumnsSplitter.length; i<ii; i++){
                            if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                        }
                        addColumnToMtx(column,FREE);
                    }
                }
                return column;
            };

            var getUpColumn = function() {
                var column = -1;
                if (upLine==-1) {
                    if (nbLines == 0 && nbColumns == 0) {
                        upLine=0;
                        column=++mtxColumnsSplitter[minInternalLefTudC];
                        mtxColumnsSplitter[maxInternalLefTudC]++;
                        addColumnToMtx(column,FREE);
                        addLineToMtx(upLine);
                    } else {
                        upLine = 0;
                        addLineToMtx(upLine);
                        if (minMulticastL!=-1) minMulticastL++;
                        if (maxMulticastL!=-1) maxMulticastL++;
                        if (downInternalLine!=-1) downInternalLine++;
                        if (downLine!=-1) downLine++;
                    }
                }

                if (column==-1)
                    column = getUpOrDownFreeBlockColumn(upLine,null);

                return column;
            };

            var getDownColumn = function() {
                var column = -1;
                if (downLine==-1) {
                    if (nbLines == 0 && nbColumns == 0) {
                        downLine=0;
                        column=++mtxColumnsSplitter[minInternalLefTudC];
                        mtxColumnsSplitter[maxInternalLefTudC]++;
                        addColumnToMtx(column,FREE);
                        addLineToMtx(downLine);
                    } else {
                        downLine = nbLines;
                        addLineToMtx(downLine);
                    }
                }

                if (column == -1)
                    column = getUpOrDownFreeBlockColumn(downLine,null);

                return column;
            };

            var initUpInternalLineWithZone = function(minZoneC,maxZoneC) {
                var column = -1;
                if (upInternalLine==-1) {
                    if (nbLines == 0 && nbColumns == 0) {
                        upLine=0;
                        upInternalLine=1;
                        column=++mtxColumnsSplitter[minZoneC];
                        mtxColumnsSplitter[maxZoneC]++;
                        addColumnToMtx(column,FREE);
                        addLineToMtx(upLine);
                        addLineToMtx(upInternalLine);
                    } else {
                        if (upLine==-1) {
                            upLine = 0;
                            addLineToMtx(upLine);
                        }
                        upInternalLine=1;
                        addLineToMtx(upInternalLine);
                        if (minMulticastL!=-1) minMulticastL+=2;
                        if (maxMulticastL!=-1) maxMulticastL+=2;
                        if (downInternalLine!=-1) downInternalLine+=2;
                        if (downLine!=-1) downLine+=2;
                    }
                }
                return column;
            };

            var getInternalUpColumn = function() {
                var column = initUpInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column==-1)
                    column = getUpOrDownFreeBlockColumn(upLine,upInternalLine);
                return column;
            };

            var initDownInternalLineWithZone = function(minZoneC,maxZoneC) {
                var column = -1;
                if (downInternalLine==-1) {
                    if (nbLines == 0 && nbColumns == 0) {
                        downInternalLine=0;
                        downLine=1;
                        column=++mtxColumnsSplitter[minZoneC];
                        mtxColumnsSplitter[maxZoneC]++;
                        addColumnToMtx(column,FREE);
                        addLineToMtx(downInternalLine);
                        addLineToMtx(downLine);
                    } else {
                        downInternalLine=nbLines;
                        addLineToMtx(downInternalLine);
                        if (downLine==-1) {
                            downLine = nbLines;
                            addLineToMtx(downLine);
                        }
                    }
                }
                return column;
            };

            var getInternalDownColumn = function() {
                var column = initDownInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column == -1)
                    column = getUpOrDownFreeBlockColumn(downLine,downInternalLine);
                return column;
            };

            var getInternalCoord = function() {
                var column2ret, line2ret;

                //FIRST : TRY TO GET FREE COORDS IN THE DOWN INTERNAL LINE
                initDownInternalLineWithZone(minMulticastC,maxMulticastC);
                line2ret   = downInternalLine;
                column2ret = getFreeBlockColumn(downInternalLine,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);

                //SECOND : TRY TO GET FREE COORDS IN THE INTERNAL LEFT COLUMNS AND THEN TRY IN INTERNAL RIGHT COLUMNS
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column2ret==-1) {
                    for (var i = maxMulticastL, ii = minMulticastL; i>=ii; i--) {
                        column2ret=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC]);
                        if (column2ret!=-1) line2ret = i;
                    }
                } else {
                    if (column2ret==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column2ret=getColumnFromInitializedArea(minInternalLeftC,maxInternalLeftC);
                        line2ret=downInternalLine;
                    }
                }

                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column2ret==-1) {
                    for (i = maxMulticastL, ii = minMulticastL; i>=ii; i--) {
                        column2ret=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);
                        if (column2ret!=-1) line2ret = i;
                    }
                } else {
                    if (column2ret==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column2ret=getColumnFromInitializedArea(minInternalRightC,maxInternalRightC);
                        line2ret=downInternalLine;
                    }
                }

                if (column2ret==-1) {
                    //THIRD : TRY TO GET FREE COORDS IN THE UP INTERNAL LINE
                    initUpInternalLineWithZone(minMulticastC,maxMulticastC);
                    line2ret   = upInternalLine;
                    column2ret = getFreeBlockColumn(upInternalLine,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);
                }

                if (column2ret==-1) {
                    //FOURTH : ADD A NEW MULTICAST COLUMN AND RETURN COORDS(downInternalLine,maxMulticastC)
                    for (i = maxMulticastC, ii = mtxColumnsSplitter.length; i<ii; i++)
                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++;
                    column2ret=mtxColumnsSplitter[maxMulticastC];
                    addColumnToMtx(column2ret,FREE);
                    line2ret=downInternalLine;
                }

                return {
                    column: column2ret,
                    line  : line2ret
                }
            };

            var swapInternalCoordToAverageLine = function(objToSwapFinal, line) {
                var column2swap = -1;

                column2swap = getFreeBlockColumn(line,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);

                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column2swap==-1)
                    column2swap = getFreeBlockColumn(line, mtxColumnsSplitter[minInternalLeftC], mtxColumnsSplitter[maxInternalLeftC]);

                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column2swap==-1)
                    column2swap = getFreeBlockColumn(line,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);

                if (column2swap==-1)
                    column2swap = getFreeBlockColumn(line,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);

                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column2swap==-1)
                    column2swap = getFreeOrNonFinalBlockColumn(line, mtxColumnsSplitter[minInternalLeftC], mtxColumnsSplitter[maxInternalLeftC]);

                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column2swap==-1)
                    column2swap = getFreeOrNonFinalBlockColumn(line,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);

                if (column2swap!=-1) {
                    var blockToSwap = rows[column2swap][line];
                    rows[objToSwapFinal.obj.layoutData.areaMtxCoord.y][objToSwapFinal.obj.layoutData.areaMtxCoord.x] = blockToSwap;
                    if (blockToSwap!=null && blockToSwap!==FREE && blockToSwap !==LOCKED)
                        blockToSwap.obj.layoutData.areaMtxCoord = {x: objToSwapFinal.obj.layoutData.areaMtxCoord.x, y:objToSwapFinal.obj.layoutData.areaMtxCoord.y};
                    rows[column2swap][line] = objToSwapFinal;
                    objToSwapFinal.obj.layoutData.areaMtxCoord = {x: line, y: column2swap}
                }
            };

            var sortAgainLineOnColumn = function(minC, maxC) {
                var i, ii, j, jj, k, kk;
                if (mtxColumnsSplitter[minC]!=-1 && mtxColumnsSplitter[maxC]!=-1) {
                    for (i=mtxColumnsSplitter[minC], ii=mtxColumnsSplitter[maxC]; i<=ii; i++) {
                        for(j=0, jj=nbLines; j<jj; j++) {
                            if (rows[i][j]!==FREE && rows[i][j]!== LOCKED && rows[i][j].type===LAN) {
                                for(k=j+1, kk=nbLines; k<kk; k++) {
                                    if (rows[i][k]!==FREE && rows[i][k]!== LOCKED && rows[i][k].type===LAN) {
                                        if (rows[i][j].obj.layoutData.averageLine > rows[i][k].obj.layoutData.averageLine) {
                                            var objToSwitch = rows[i][j], objSwitchWith = rows[i][k];
                                            rows[i][j] = objSwitchWith; objSwitchWith.obj.layoutData.areaMtxCoord={x:j,y:i};
                                            rows[i][k] = objToSwitch; objToSwitch.obj.layoutData.areaMtxCoord={x:k,y:i};
                                        } else if (rows[i][j].obj.layoutData.averageLine == rows[i][k].obj.layoutData.averageLine) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            };

            var getMulticastBusCoord = function() {
                var column = -1,
                    line   = -1;
                if (mtxColumnsSplitter[minMulticastC]==-1 && mtxColumnsSplitter[maxMulticastC]==-1) {
                    if (nbLines == 0 && nbColumns == 0) {
                        line = ++minMulticastL;
                        maxMulticastL++;
                        column = ++mtxColumnsSplitter[minMulticastC];
                        mtxColumnsSplitter[maxMulticastC]++;
                        addColumnToMtx(column,FREE);
                        addLineToMtx(line);
                    } else {
                        if (upInternalLine!=-1) {
                            line = upInternalLine+1;
                        } else if (upLine!=-1) {
                            line = upLine+1;
                        } else if (downInternalLine!=-1) {
                            line = downInternalLine;
                        } else if (downLine!=-1) {
                            line = downLine;
                        } else {
                            line = nbLines;
                        }
                        minMulticastL=line;
                        maxMulticastL=line;
                        column=getColumnFromInitializedArea(minMulticastC,maxMulticastC);
                        addLineToMtx(line);
                        if (downInternalLine!=-1) downInternalLine++;
                        if (downLine!=-1) downLine++;
                    }
                } else {
                    for (var i = minMulticastL, ii=maxMulticastL; i<=ii; i++) {
                        column = getFreeBlockColumn(i,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);
                        if (column!=-1) {
                            line = i;
                            break;
                        }
                    }
                    if (line==-1) {
                        column = mtxColumnsSplitter[minMulticastC];
                        line   = ++maxMulticastL;
                        if (minMulticastL==-1) minMulticastL=line;
                        addLineToMtx(line);
                        if (downInternalLine!=-1) downInternalLine++;
                        if (downLine!=-1) downLine++;
                    }
                }
                return {
                    column: column,
                    line  : line
                }
            };

            var addConnectedAreaObjectToAreaObject = function(targetAreaObj, sourceObj) {
                var i, ii, isRedefined = false;
                for (i = 0, ii = targetAreaObj.layoutData.areaConnectedObject.length; i < ii; i++) {
                    var alreadyConnectedObject = targetAreaObj.layoutData.areaConnectedObject[i];
                    if (sourceObj.type === alreadyConnectedObject.type) {
                        if (sourceObj.type === LAN) {
                            if (sourceObj.obj.lanDef.lan === alreadyConnectedObject.obj.lanDef.lan) {
                                alreadyConnectedObject.weight++;
                                isRedefined = true;
                                break;
                            }
                        } else if (sourceObj.type === BUS) {
                            if (sourceObj.obj.multicastAddr === alreadyConnectedObject.obj.multicastAddr) {
                                alreadyConnectedObject.weight++;
                                isRedefined = true;
                                break;
                            }
                        }
                    }
                }

                if (!isRedefined)
                    targetAreaObj.layoutData.areaConnectedObject.push(sourceObj);

                targetAreaObj.layoutData.areaInternalLinksWeight++;
            };

            var optimizeMulticastBusCoord = function() {
                var i, ii, j, jj, k, kk, isSwapped, lBus, llBus;

                if (minMulticastL!=-1 && maxMulticastL!=-1) {
                    for (j = mtxColumnsSplitter[minMulticastC], jj = mtxColumnsSplitter[maxMulticastC]; j<=jj; j++) {
                        lBus = rows[j][minMulticastL];
                        if (lBus!=FREE && lBus!=LOCKED && upMulticastL && !lBus.obj.layoutData.toUp) {
                            if (downMulticastL && lBus.obj.layoutData.toDown) {
                                for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k<=kk; k++) {
                                    llBus = rows[k][maxMulticastL];
                                    if ((llBus!=FREE && llBus!=LOCKED && llBus.obj.layoutData.toUp) || (llBus==FREE)) {
                                        rows[k][maxMulticastL] = lBus;
                                        lBus.obj.layoutData.areaMtxCoord= {x: maxMulticastL, y: k};
                                        lBus.obj.layoutData.areaPozFinal = true;
                                        rows[j][minMulticastL] = llBus;
                                        llBus.obj.layoutData.areaMtxCoord= {x: minMulticastL, y: j};
                                        llBus.obj.layoutData.areaPozFinal = true;
                                        isSwapped = true;
                                        break;
                                    }
                                }
                            } else {
                                for (i = minMulticastL+1, ii = maxMulticastL; i<=ii; i++) {
                                    for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k<=kk; k++) {
                                        llBus = rows[k][i];
                                        if ((llBus!=FREE && llBus!=LOCKED && llBus.obj.layoutData.toUp) || (llBus==FREE)) {
                                            rows[k][i] = lBus;
                                            lBus.obj.layoutData.areaMtxCoord= {x: i, y: k};
                                            lBus.obj.layoutData.areaPozFinal = true;
                                            rows[j][minMulticastL] = llBus;
                                            llBus.obj.layoutData.areaMtxCoord= {x: minMulticastL, y: j};
                                            llBus.obj.layoutData.areaPozFinal = true;
                                            isSwapped = true;
                                            break;
                                        }
                                    }
                                    if (isSwapped)
                                        break;
                                }
                            }
                        } else if (lBus!=FREE && lBus!=LOCKED && upMulticastL && !lBus.obj.layoutData.toUp)
                            lBus.obj.layoutData.areaPozFinal = true;
                        else if (lBus!=FREE && lBus!=LOCKED && upMulticastL)
                            lBus.obj.layoutData.areaPozFinal = true;

                        isSwapped = false;
                        lBus = rows[j][maxMulticastL];
                        if (lBus!=FREE && lBus!=LOCKED && downMulticastL && !lBus.obj.layoutData.toDown) {
                            if (upMulticastL && lBus.obj.layoutData.toUp) {
                                for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k<=kk; k++) {
                                    llBus = rows[k][minMulticastL];
                                    if ((llBus!=FREE && llBus!=LOCKED && llBus.obj.layoutData.toDown) || (llBus==FREE)) {
                                        rows[k][minMulticastL] = lBus;
                                        lBus.obj.layoutData.areaMtxCoord= {x: minMulticastL, y: k};
                                        lBus.obj.layoutData.areaPozFinal = true;
                                        rows[j][maxMulticastL] = llBus;
                                        llBus.obj.layoutData.areaMtxCoord= {x: maxMulticastL, y: j};
                                        llBus.obj.layoutData.areaPozFinal = true;
                                        isSwapped = true;
                                        break;
                                    }
                                }
                            } else {
                                for (i = minMulticastL, ii = maxMulticastL-1; i<=ii; i++) {
                                    for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k<=kk; k++) {
                                        llBus = rows[k][i];
                                        if ((llBus!=FREE && llBus!=LOCKED && llBus.obj.layoutData.toDown) || llBus==FREE) {
                                            rows[k][i] = lBus;
                                            lBus.obj.layoutData.areaMtxCoord={x: i, y: k};
                                            lBus.obj.layoutData.areaPozFinal = true;
                                            rows[j][maxMulticastL] = llBus;
                                            llBus.obj.layoutData.areaMtxCoord={x: maxMulticastL, y: j};
                                            llBus.obj.layoutData.areaPozFinal = true;
                                            isSwapped = true;
                                            break;
                                        }
                                    }
                                    if (isSwapped)
                                        break;
                                }
                            }
                        } else if (lBus!=FREE && lBus!=LOCKED && downMulticastL && lBus.obj.layoutData.toDown)
                            lBus.obj.layoutData.areaPozFinal = true;
                        else if (lBus!=FREE && lBus!=LOCKED && downMulticastL)
                            lBus.obj.layoutData.areaPozFinal = true;

                        var column;
                        for (i = minMulticastL+1, ii = maxMulticastL-1; i<=ii; i++) {
                            isSwapped = false;
                            lBus = rows[j][i];
                            if (lBus!=FREE && lBus!=LOCKED && upMulticastL && lBus.obj.layoutData.toUp && !lBus.obj.layoutData.toDown) {
                                for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k <= kk; k++) {
                                    llBus = rows[k][minMulticastL];
                                    if ((llBus!=FREE && llBus!=LOCKED && !llBus.obj.layoutData.toUp) || llBus == FREE) {
                                        rows[k][minMulticastL] = lBus;
                                        lBus.obj.layoutData.areaMtxCoord={x: minMulticastL, y: k};
                                        lBus.obj.layoutData.areaPozFinal = true;
                                        rows[j][i] = llBus;
                                        llBus.obj.layoutData.areaMtxCoord={x: i, y: j};
                                        llBus.obj.layoutData.areaPozFinal = true;
                                        isSwapped = true;
                                        break;
                                    }
                                }

                                if (!isSwapped) {
                                    for (i = maxMulticastC, ii = mtxColumnsSplitter.length; i<ii; i++)
                                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++;
                                    column = mtxColumnsSplitter[maxMulticastC];
                                    addColumnToMtx(column,FREE);
                                    rows[j][i] = FREE;
                                    rows[column][minMulticastL] = lBus;
                                    lBus.obj.layoutData.areaMtxCoord={x: minMulticastL, y: column};
                                    lBus.obj.layoutData.areaPozFinal = true;
                                    isSwapped = true;
                                }
                            }

                            if (isSwapped)
                                break;

                            if (lBus!=FREE && lBus!=LOCKED && downMulticastL && !lBus.obj.layoutData.toUp && lBus.obj.layoutData.toDown) {
                                for (k = mtxColumnsSplitter[minMulticastC], kk = mtxColumnsSplitter[maxMulticastC]; k <= kk; k++) {
                                    llBus = rows[k][maxMulticastL];
                                    if ((llBus!=FREE && llBus!=LOCKED && !llBus.obj.layoutData.toDown) || llBus == FREE) {
                                        rows[k][maxMulticastL] = lBus;
                                        lBus.obj.layoutData.areaMtxCoord={x: maxMulticastL, y: k};
                                        lBus.obj.layoutData.areaPozFinal = true;
                                        rows[j][i] = llBus;
                                        llBus.obj.layoutData.areaMtxCoord={x: i, y: j};
                                        llBus.obj.layoutData.areaPozFinal = true;
                                        isSwapped = true;
                                        break;
                                    }
                                }

                                if (!isSwapped) {
                                    for (i = maxMulticastC, ii = mtxColumnsSplitter.length; i<ii; i++)
                                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++;
                                    column = mtxColumnsSplitter[maxMulticastC];
                                    addColumnToMtx(column,FREE);
                                    rows[j][i] = FREE;
                                    rows[column][maxMulticastL] = lBus;
                                    lBus.obj.layoutData.areaMtxCoord={x: maxMulticastL, y: column};
                                    lBus.obj.layoutData.areaPozFinal = true;
                                    isSwapped = true;
                                }
                            }
                        }
                    }

                    for (j = mtxColumnsSplitter[minMulticastC], jj = mtxColumnsSplitter[maxMulticastC]; j<=jj; j++)
                        for (i = minMulticastL, ii = maxMulticastL; i<=ii; i++)
                            rows[j][i].obj.layoutData.areaPozFinal = true;
                }
            };

            var optimizeLanCoord = function() {
                var i, ii, j, jj;
                var lan, connectedObjects, averageLine = 0, mtxAverageLine = Math.round(nbLines/2), connectedWeight = 0;
                //var stillNeedFinalPoz = [];

                lansList.sort(function(lan1, lan2){
                    return (lan2.layoutData.areaInternalLinksWeight-lan1.layoutData.areaInternalLinksWeight);
                });

                for (i=0, ii=lansList.length; i<ii; i++) {
                    lan = lansList[i];
                    if (lan.layoutData.isConnectedInsideArea && !lan.layoutData.isConnectedToUpArea && !lan.layoutData.isConnectedToDownArea &&
                        !lan.layoutData.isConnectedToLeftArea && !lan.layoutData.isConnectedToRightArea) {
                        connectedObjects = lan.layoutData.areaConnectedObject.sort(function(coord1, coord2) {
                            return (coord2.weight - coord1.weight);
                        });
                        connectedWeight = 0;
                        averageLine = 0;
                        for (j = 0, jj = connectedObjects.length; j<jj; j++) {
                            if (connectedObjects[j].obj.layoutData.areaPozFinal) {
                                averageLine += (connectedObjects[j].obj.layoutData.areaMtxCoord.x-mtxAverageLine)*connectedObjects[j].weight;
                                connectedWeight += connectedObjects[j].weight;
                                //helper_.debug('['+lan.lanDef.sip+'] : connectectObj['+i+'], averageLine:'+averageLine+', connectedWeight:'+connectedWeight);
                            }
                        }


                        if (connectedWeight!=0) {
                            averageLine = Math.round(averageLine/connectedWeight) + mtxAverageLine ;
                            lan.layoutData.averageLine = averageLine;
                            //helper_.debug('['+lan.lanDef.sip+'] : connectectObj['+i+'], averageLine:'+averageLine+', connectedWeight:'+connectedWeight);
                            if (averageLine!=lan.layoutData.areaMtxCoord.x)
                                swapInternalCoordToAverageLine(rows[lan.layoutData.areaMtxCoord.y][lan.layoutData.areaMtxCoord.x], averageLine);
                            lan.layoutData.areaPozFinal = true;
                        }
                    }
                }

                sortAgainLineOnColumn(minInternalLeftC,maxInternalLeftC);
                sortAgainLineOnColumn(minInternalRightC,maxInternalRightC);
            };

            this.printMtx = function(r) {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        var block = rows[i][j];
                        if (block!=FREE &&  block!=LOCKED)
                            block.obj.print(r);
                    }
                }
            };

            this.displayLan = function(display) {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        var block = rows[i][j];
                        if (block!=FREE &&  block!=LOCKED) {
                            if (block.type===LAN)
                                block.obj.displayLan(display);
                        }
                    }
                }
            };

            this.getMtxSize = function() {
                return {
                    x: nbColumns,
                    y: nbLines
                };
            };

            this.getObjFromMtx = function (x,y) {
                return rows[x][y].obj;
            };

            this.getObjTypeFromMtx = function(x,y) {
                return rows[x][y].type;
            };

            this.defineMtxObjMaxSize = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        var block = rows[i][j];
                        if (block!=FREE && block!=LOCKED) {
                            var size;
                            if (block.type===LAN) {
                                block.obj.defineMaxSize();
                                size = block.obj.getLanMaxSize();
                            } else if (block.type===BUS){
                                size = block.obj.getBusSize();
                            }
                            if ((lineMaxHeight[j]!=null && lineMaxHeight[j]<size.height)||lineMaxHeight[j]==null)
                                lineMaxHeight[j]=size.height;
                            if ((columnMaxWidth[i]!=null && columnMaxWidth[i]<size.width)||columnMaxWidth[i]==null)
                                columnMaxWidth[i]=size.width;
                        } else {
                            if (lineMaxHeight[j]==null)
                                lineMaxHeight[j]=0;
                            if (columnMaxWidth[i]==null)
                                columnMaxWidth[i]=0;
                        }
                    }
                }
            };

            this.defineMtxObjSize = function() {
                var i, ii, j, jj;

                for (i = 0, ii = lineMaxHeight.length; i < ii; i++)
                    lineMaxHeight[i] = 0;

                for (i = 0, ii = columnMaxWidth.length; i < ii; i++)
                    columnMaxWidth[i] = 0;

                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    for (j = 0, jj = nbLines; j < jj ; j++ ) {
                        var block = rows[i][j];
                        if (block!=FREE && block!=LOCKED) {
                            var size;
                            if (block.type===LAN) {
                                block.obj.defineSize();
                                size = block.obj.getLanSize();
                            } else if (block.type===BUS){
                                size = block.obj.getBusSize();
                            }
                            if ((lineMaxHeight[j]!=null && lineMaxHeight[j]<size.height)||lineMaxHeight[j]==null)
                                lineMaxHeight[j]=size.height;
                            if ((columnMaxWidth[i]!=null && columnMaxWidth[i]<size.width)||columnMaxWidth[i]==null)
                                columnMaxWidth[i]=size.width;
                        } else {
                            if (lineMaxHeight[j]==null)
                                lineMaxHeight[j]=0;
                            if (columnMaxWidth[i]==null)
                                columnMaxWidth[i]=0;
                        }
                    }
                }
            };

            this.defineAreaContentMaxSize = function() {
                for (var j = 0, jj = nbLines; j < jj; j++)
                    contentHeight = contentHeight + lineMaxHeight[j];
                for (j = 0, jj = nbColumns; j < jj; j++)
                    contentWidth = contentWidth + columnMaxWidth[j];
            };

            this.defineAreaContentSize = function() {
                contentHeight = 0 ;
                contentWidth  = 0 ;
                for (var j = 0, jj = nbLines; j < jj; j++)
                    contentHeight = contentHeight + lineMaxHeight[j];
                for (j = 0, jj = nbColumns; j < jj; j++)
                    contentWidth = contentWidth + columnMaxWidth[j];
            };

            this.getAreaContentSize = function() {
                return {
                    width : contentWidth,
                    height: contentHeight
                };
            };

            this.getAreaContentMaxSize = function() {
                return {
                    width : contentWidth,
                    height: contentHeight
                };
            };

            this.defineMtxObjFirstPoz = function(topLeftX, topLeftY, abrdSpan, lanSpan, areawidth, areaheight) {
                var i, ii, j, jj;
                var curContWidth  = topLeftX;
                for (i = 0, ii = nbColumns; i < ii; i++) {
                    var curContHeight = topLeftY;
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        var block = rows[i][j];
                        if (block!=FREE && block!=LOCKED) {
                            if (block.type===BUS) {
                                block.obj.setCylinder(
                                    abrdSpan + lanSpan*i + curContWidth + columnMaxWidth[i]/2,
                                    abrdSpan + lanSpan*j + curContHeight + block.obj.getBusSize().height + lineMaxHeight[j]/2
                                );
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                            } else if (block.type===LAN){
                                if (block.obj.getLanMaxSize().width < columnMaxWidth[i]) {
                                    block.obj.setTopLeftCoord(
                                        abrdSpan + lanSpan*i + curContWidth + (columnMaxWidth[i]-block.obj.getLanMaxSize().width)/2,
                                        abrdSpan + lanSpan*j + curContHeight
                                    );
                                } else block.obj.setTopLeftCoord(abrdSpan + lanSpan*i + curContWidth , abrdSpan + lanSpan*j + curContHeight);
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                                block.obj.defineFirstPoz();
                            }
                        }
                        curContHeight += lineMaxHeight[j];
                    }
                    curContWidth += columnMaxWidth[i];
                }
            };

            this.defineMtxObjIntermediatePoz = function(topLeftX, topLeftY, abrdSpan, lanSpan, areawidth, areaheight) {
                var i, ii, j, jj;
                var curContWidth  = topLeftX;
                for (i = 0, ii = nbColumns; i < ii; i++) {
                    var curContHeight = topLeftY;
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        var block = rows[i][j];
                        if (block!=FREE && block!=LOCKED) {
                            if (block.type===BUS) {
                                block.obj.setCylinder(
                                        abrdSpan + lanSpan*i + curContWidth + columnMaxWidth[i]/2,
                                        abrdSpan + lanSpan*j + curContHeight + block.obj.getBusSize().height + lineMaxHeight[j]/2
                                );
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                            } else if (block.type===LAN){
                                if (block.obj.getLanSize().width < columnMaxWidth[i]) {
                                    block.obj.setTopLeftCoord(
                                            abrdSpan + lanSpan*i + curContWidth + (columnMaxWidth[i]-block.obj.getLanSize().width)/2,
                                            abrdSpan + lanSpan*j + curContHeight
                                    );
                                } else block.obj.setTopLeftCoord(abrdSpan + lanSpan*i + curContWidth , abrdSpan + lanSpan*j + curContHeight);
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                                block.obj.defineIntermediatePoz();
                            }
                        }
                        curContHeight += lineMaxHeight[j];
                    }
                    curContWidth += columnMaxWidth[i];
                }
            };

            this.defineMtxObjFinalPoz = function(topLeftX, topLeftY, abrdSpan, lanSpan, areawidth, areaheight) {
                var i, ii, j, jj;
                var curContWidth  = topLeftX;
                for (i = 0, ii = nbColumns; i < ii; i++) {
                    var curContHeight = topLeftY;
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        var block = rows[i][j];
                        if (block!=FREE && block!=LOCKED) {
                            if (block.type===BUS) {
                                block.obj.setCylinder(
                                        abrdSpan + lanSpan*i + curContWidth + columnMaxWidth[i]/2,
                                        abrdSpan + lanSpan*j + curContHeight + block.obj.getBusSize().height + lineMaxHeight[j]/2
                                );
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                            } else if (block.type===LAN){
                                if (block.obj.getLanSize().width < columnMaxWidth[i]) {
                                    block.obj.setTopLeftCoord(
                                            abrdSpan + lanSpan*i + curContWidth + (columnMaxWidth[i]-block.obj.getLanSize().width)/2,
                                            abrdSpan + lanSpan*j + curContHeight
                                    );
                                } else block.obj.setTopLeftCoord(abrdSpan + lanSpan*i + curContWidth , abrdSpan + lanSpan*j + curContHeight);
                                block.obj.setMoveJail(topLeftX+abrdSpan, topLeftY+abrdSpan, topLeftX+areawidth-abrdSpan, topLeftY+areaheight-abrdSpan);
                                block.obj.defineFinalPoz();
                            }
                        }
                        curContHeight += lineMaxHeight[j];
                    }
                    curContWidth += columnMaxWidth[i];
                }
            };

            this.optimizeLanAndBusMtxCoord = function() {
                var i, ii;

                optimizeMulticastBusCoord();
                optimizeLanCoord();
                cleanFinalMatrix();

                for (i=0, ii=lansList.length; i<ii; i++)
                    lansList[i].optimizeMtxCoord();
            };

            this.addContainerLanAndBus = function(container) {
                var curlan          = container.layoutData.lan,
                    alreadyInserted = curlan.isInserted;

                var i, ii;
                var linkedContainers;
                var linkedBuss, lBus, newBusCoord;
                var upColumn, downColumn, newInternalUDC, newInternalCoord, newUDC;

                linkedBuss = container.getLinkedBus();
                for (i = 0, ii = linkedBuss.length; i < ii; i++) {
                    lBus = linkedBuss[i];
                    if (!lBus.isInserted) {
                        lBus.layoutData = {
                            busWeight: 1, toUp: curlan.layoutData.isConnectedToUpArea,
                            toDown : curlan.layoutData.isConnectedToDownArea, areaMtxCoord: null,
                            areaConnectedObject: []
                        };
                        newBusCoord = getMulticastBusCoord();
                        rows[newBusCoord.column][newBusCoord.line] = {obj:lBus,type:BUS};
                        lBus.layoutData.areaMtxCoord= {x: newBusCoord.line, y: newBusCoord.column};
                        lBus.isInserted=true;
                    } else {
                        lBus.layoutData.busWeight++;
                        lBus.layoutData.toUp = lBus.layoutData.toUp || curlan.layoutData.isConnectedToUpArea || curlan.layoutData.isConnectedToUpDC;
                        lBus.layoutData.toDown = lBus.layoutData.toDown || curlan.layoutData.isConnectedToDownArea || curlan.layoutData.isConnectedToDownDC;
                    }
                    if (lBus.layoutData.toUp)
                        upMulticastL = true;
                    if (lBus.layoutData.toDown)
                        downMulticastL = true;

                    addConnectedAreaObjectToAreaObject(curlan, {obj:lBus, type: BUS, weight: 1});
                }

                if (!alreadyInserted){
                    // if not inserted create lan and insert it in the area mtx
                    if (curlan.layoutData.isConnectedInsideArea) {
                        if ((curlan.layoutData.isConnectedToUpArea || curlan.layoutData.isConnectedToUpDC) &&
                            (curlan.layoutData.isConnectedToDownArea || curlan.layoutData.isConnectedToDownDC)) {
                            newInternalUDC = getNewInternalUpDownColumn();
                            rows[newInternalUDC][0] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:0, y:newInternalUDC};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToUpArea || curlan.layoutData.isConnectedToUpDC) {
                            upColumn = getInternalUpColumn();
                            rows[upColumn][upInternalLine] = {obj:curlan,type:LAN};
                            rows[upColumn][upLine]=LOCKED;
                            curlan.layoutData.areaMtxCoord = {x:upInternalLine, y:upColumn};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToDownArea || curlan.layoutData.isConnectedToDownDC) {
                            downColumn = getInternalDownColumn();
                            rows[downColumn][downInternalLine] = {obj:curlan,type:LAN};
                            rows[downColumn][downLine]=LOCKED;
                            curlan.layoutData.areaMtxCoord = {x:downInternalLine, y:downColumn};
                            curlan.isInserted = true;
                        } else  {
                            newInternalCoord = getInternalCoord();
                            rows[newInternalCoord.column][newInternalCoord.line] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column};
                            curlan.isInserted = true;
                        }
                    } else {
                        if ((curlan.layoutData.isConnectedToUpArea || curlan.layoutData.isConnectedToUpDC) &&
                            (curlan.layoutData.isConnectedToDownArea || curlan.layoutData.isConnectedToDownDC)) {
                            newUDC = getNewUpDownColumn();
                            rows[newUDC][0] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:0, y:newUDC};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToUpArea || curlan.layoutData.isConnectedToUpDC) {
                            upColumn = getUpColumn();
                            rows[upColumn][upLine] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:upLine, y:upColumn};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToDownArea || curlan.layoutData.isConnectedToDownDC) {
                            downColumn = getDownColumn();
                            rows[downColumn][downLine] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:downLine, y:downColumn};
                            curlan.isInserted = true;
                        } else  {
                            newInternalCoord = getInternalCoord();
                            rows[newInternalCoord.column][newInternalCoord.line] = {obj:curlan,type:LAN};
                            curlan.layoutData.areaMtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column};
                            curlan.isInserted = true;
                        }
                    }
                    lansList.push(curlan);
                }

                //ADD CURRENT LAN TO AREA LAN/MBUS CONNECTED LIST
                linkedBuss = container.getLinkedBus();
                for (i = 0, ii = linkedBuss.length; i < ii; i++)
                    addConnectedAreaObjectToAreaObject(linkedBuss[i], {obj:curlan, type: LAN, weight: 1});

                linkedContainers = container.getLinkedContainers();
                for (i = 0, ii = linkedContainers.length; i < ii; i++)
                    if (container.localisation.equalArea(linkedContainers[i].localisation) &&
                        container.localisation.getLan().lan!==linkedContainers[i].localisation.getLan().lan)
                            addConnectedAreaObjectToAreaObject(linkedContainers[i].layoutData.lan,
                                {obj:curlan, type: LAN, weight: 1});

                // finally push the container
                curlan.pushContainer(container);
            };
        }

        return areaMatrix;
    });
