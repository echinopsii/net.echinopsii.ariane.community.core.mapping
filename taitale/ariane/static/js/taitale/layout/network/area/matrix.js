// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - Area                           │ \\
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
        'taitale-lan'
    ],
    function (lan) {

        var FREE   = "FREE",
            LOCKED = "LOCKED";

        var LAN    = "LAN",
            BUS    = "BUS";

        function areaMatrix(registries, options_) {
            var nbLines             = 0,
                nbColumns           = 0,
                rows                = [],
                lineMaxHeight       = [],
                columnMaxWidth      = [],
                contentWidth        = 0,
                contentHeight       = 0;

            var upLine              = -1, // UP LINK ONLY
                upInternalLine      = -1, // UP & INTERNAL
                minMulticastL       = -1, // MULTICAST BUS
                maxMulticastL       = -1,
                downInternalLine    = -1, // DOWN & INTERNAL
                downLine            = -1; // DOWN LINK ONLY

            // PUSH LEFT/RIGHT BALANCER
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

            var addLineToMtx = function(index) {
                if (index < nbLines) {
                    for (var i = 0, ii = nbColumns; i < ii; i++) {
                        for (var j = index, jj = nbLines; j <= jj; jj--) {
                            rows[i][jj] = rows[i][jj-1];
                        }
                    }
                }
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
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

            var addColumnToMtx = function(index,flag) {
                if (index < nbColumns){
                    rows[nbColumns] = [];
                    for (var i = index, ii=nbColumns; i < ii; ii--)  {
                        rows[ii] = rows[ii-1];
                    }
                }
                rows[index] = [];
                for(var i = 0, ii = nbLines; i < ii ; i++) {
                    rows[index][i] = flag;
                }
                nbColumns++;
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

            var getColumnFromInitializedArea = function(minSplitterIdx, maxSplitterIdx) {
                var column = -1;
                for (var i = minSplitterIdx+2, ii = mtxColumnsSplitter.length-1; i<ii; i+=2){
                    if (mtxColumnsSplitter[i]!=-1 && mtxColumnsSplitter[i+1]!=-1) {
                        column = mtxColumnsSplitter[i];
                        mtxColumnsSplitter[minSplitterIdx]=column;
                        mtxColumnsSplitter[maxSplitterIdx]=column;
                        addColumnToMtx(column,FREE);
                        break;
                    }
                }
                if (column==-1) {
                    for (var i = maxSplitterIdx-2, ii=1; i>ii; i-=2) {
                        if (mtxColumnsSplitter[i]!=-1 && mtxColumnsSplitter[i-1]!=-1) {
                            column = mtxColumnsSplitter[i]+1;
                            mtxColumnsSplitter[minSplitterIdx]=column;
                            mtxColumnsSplitter[maxSplitterIdx]=column;
                            addColumnToMtx(column,FREE);
                            break;
                        }
                    }
                } else {
                    for (var i = maxSplitterIdx+1, ii = mtxColumnsSplitter.length; i<ii; i++){
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
                        boolLeftRight=true;
                        column=++mtxColumnsSplitter[maxRight];
                        addColumnToMtx(column,LOCKED);
                    } else {
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
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC])
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC])
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT AREA
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC])
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC])
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT AREA
                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL, maxL, mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC])
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC])
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT UP or DOWN &/or INTERNAL AREA
                if (mtxColumnsSplitter[minInternalLefTudC]!=-1 && mtxColumnsSplitter[maxInternalLefTudC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalLefTudC],mtxColumnsSplitter[maxInternalLefTudC])
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalLefTudC],mtxColumnsSplitter[maxInternalLefTudC])
                } else {
                    if (column==-1)
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalLefTudC,maxInternalLefTudC);
                }

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT UP or DOWN &/or INTERNAL AREA
                if (mtxColumnsSplitter[minInternalRighTudC]!=-1 && mtxColumnsSplitter[maxInternalRighTudC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalRighTudC],mtxColumnsSplitter[maxInternalRighTudC])
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalRighTudC],mtxColumnsSplitter[maxInternalRighTudC])
                } else {
                    if (column==-1)
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalLefTudC,maxInternalLefTudC);
                }

                // IF NO BLOCK FOUNDED THEN CREATE A NEW COLUMN IN LEFT|RIGHT UP or DOWN &/or INTERNAL AREA
                if (column==-1) {
                    if (!pushInternaLudOnLeft) {
                        pushInternaLudOnLeft=true;
                        column=mtxColumnsSplitter[maxInternalRighTudC]++;
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
                        upInternalLine=1
                        addLineToMtx(upInternalLine);
                        if (minMulticastL!=-1) minMulticastL+=2;
                        if (maxMulticastL!=-1) maxMulticastL+=2;
                        if (downInternalLine!=-1) downInternalLine+=2;
                        if (downLine!=-1) downLine+=2;
                    }
                }
                return column;
            }

            var getInternalUpColumn = function() {
                var column = -1;
                initUpInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column==-1)
                    column = getUpOrDownFreeBlockColumn(upLine,null);
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
                var column = -1;
                initDownInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column == -1)
                    column = getUpOrDownFreeBlockColumn(downLine,null);
                return column;
            };

            var getInternalCoord = function() {
                var column = -1,
                    line   = -1;

                //FIRST : TRY TO GET FREE COORDS IN THE DOWN INTERNAL LINE
                initDownInternalLineWithZone(minMulticastC,maxMulticastC);
                line   = downInternalLine;
                column = getFreeBlockColumn(downInternalLine,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);

                //SECOND : TRY TO GET FREE COORDS IN THE INTERNAL LEFT COLUMNS AND THEN TRY IN INTERNAL RIGHT COLUMNS
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column==-1) {
                    for (var i = maxMulticastL, ii = minMulticastL; i>=ii; i--) {
                        column=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC]);
                        if (column!=-1) line = i;
                    }
                } else {
                    if (column==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalLeftC,maxInternalLeftC);
                        line=downInternalLine;
                    }
                }

                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column==-1) {
                    for (var i = maxMulticastL, ii = minMulticastL; i>=ii; i--) {
                        column=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);
                        if (column!=-1) line = i;
                    }
                } else {
                    if (column==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column=getColumnFromInitializedArea(minInternalRightC,maxInternalRightC);
                        line=downInternalLine;
                    }
                }

                if (column==-1) {
                    //THIRD : TRY TO GET FREE COORDS IN THE UP INTERNAL LINE
                    initDownInternalLineWithZone(minMulticastC,maxMulticastC);
                    line   = upInternalLine;
                    column = getFreeBlockColumn(upInternalLine,mtxColumnsSplitter[minMulticastC],mtxColumnsSplitter[maxMulticastC]);
                }

                if (column==-1) {
                    //FOURTH : ADD A NEW MULTICAST COLUMN AND RETURN COORDS(downInternalLine,maxMulticastC)
                    for (var i = maxMulticastC, ii = mtxColumnsSplitter.length; i<ii; i++){
                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                    }
                    column=mtxColumnsSplitter[maxMulticastC];
                    addColumnToMtx(column,FREE);
                    line=downInternalLine;
                }

                return {
                    column: column,
                    line  : line
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
                        addLineToMtx(line)
                        if (downInternalLine!=-1) downInternalLine++;
                        if (downLine!=-1) downLine++;
                    }
                }
                return {
                    column: column,
                    line  : line
                }
            };

            this.printMtx = function(r) {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
                        var block = rows[i][j];
                        if (block!=FREE &&  block!=LOCKED) {
                            block.obj.print(r);
                        }
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
            }

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
            }

            this.getObjSizeFromMtx = function(x,y) {
                var block=rows[x][y];
                if (block!=null && block!==FREE && block!==LOCKED) {
                    if (block.type===LAN) {
                        return block.obj.getLanSize();
                    } else if (block.type===BUS) {
                        return block.obj.getBusSize();
                    }
                } else {
                    return null;
                }
            };

            this.getObjCoordsFromMtx = function(x,y) {
                var block=rows[x][y];
                if (block!=null && block!==FREE && block!==LOCKED) {
                    if (block.type===LAN) {
                        return block.obj.getLanCoords();
                    } else if (block.type===BUS) {
                        return block.obj.getBusCoords();
                    }
                } else {
                    return null;
                }
            };

            this.defineMtxObjSize = function() {
                for (var i = 0, ii = nbColumns; i < ii ; i++) {
                    for (var j = 0, jj = nbLines; j < jj ; j++ ) {
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

            this.defineAreaContentSize = function() {
                for (var j = 0, jj = nbLines; j < jj; j++) {
                    contentHeight = contentHeight + lineMaxHeight[j];
                }
                for (var j = 0, jj = nbColumns; j < jj; j++) {
                    contentWidth = contentWidth + columnMaxWidth[j];
                }
            };

            this.getAreaContentSize = function() {
                return {
                    width : contentWidth,
                    height: contentHeight
                };
            };

            this.defineMtxObjPoz = function(topLeftX, topLeftY, abrdSpan, lanSpan, areawidth, areaheight) {
                var curContWidth  = topLeftX;
                for (var i = 0, ii = nbColumns; i < ii; i++) {
                    var curContHeight = topLeftY;
                    for (var j = 0, jj = nbLines; j < jj; j++) {
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
                                block.obj.definePoz();
                            }
                        }
                        curContHeight += lineMaxHeight[j];
                    }
                    curContWidth += columnMaxWidth[i];
                }
            };

            this.addContainerLanAndBus = function(container) {
                var curlan          = container.layoutData.lan,
                    alreadyInserted = curlan.isInserted;


                if (!alreadyInserted){
                    // if not inserted create lan and insert it in the area mtx
                    if (curlan.layoutData.isConnectedInsideArea) {

                        var linkedBus = container.getLinkedBus();
                        for (var i = 0, ii = linkedBus.length; i < ii; i++) {
                            var lBus = linkedBus[i];
                            if (!lBus.isInserted) {
                                var newBusCoord = getMulticastBusCoord();
                                rows[newBusCoord.column][newBusCoord.line] = {obj:lBus,type:BUS};
                                lBus.isInserted=true;
                            }
                        }

                        if (curlan.layoutData.isConnectedToUpArea && curlan.layoutData.isConnectedToDownArea) {
                            var newInternalUDC = getNewInternalUpDownColumn();
                            rows[newInternalUDC][0] = {obj:curlan,type:LAN};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToUpArea) {
                            var upColumn = getInternalUpColumn();
                            rows[upColumn][upInternalLine] = {obj:curlan,type:LAN};
                            rows[upColumn][upLine]=LOCKED;
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToDownArea) {
                            var downColumn = getInternalDownColumn();
                            rows[downColumn][downInternalLine] = {obj:curlan,type:LAN};
                            rows[downColumn][downLine]=LOCKED;
                            curlan.isInserted = true;
                        } else  {
                            var newInternalCoord = getInternalCoord();
                            rows[newInternalCoord.column][newInternalCoord.line] = {obj:curlan,type:LAN};
                            curlan.isInserted = true;
                        }
                    } else {
                        if (curlan.layoutData.isConnectedToUpArea && curlan.layoutData.isConnectedToDownArea) {
                            var newUDC = getNewUpDownColumn();
                            rows[newUDC][0] = {obj:curlan,type:LAN};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToUpArea) {
                            var upColumn = getUpColumn();
                            rows[upColumn][upLine] = {obj:curlan,type:LAN};
                            curlan.isInserted = true;
                        } else if (curlan.layoutData.isConnectedToDownArea) {
                            var downColumn = getDownColumn();
                            rows[downColumn][downLine] = {obj:curlan,type:LAN};
                            curlan.isInserted = true;
                        }
                    }
                } else {
                    var linkedBus = container.getLinkedBus();
                    for (var i = 0, ii = linkedBus.length; i < ii; i++) {
                        var lBus = linkedBus[i];
                        if (!lBus.isInserted) {
                            var newBusCoord = getMulticastBusCoord();
                            rows[newBusCoord.column][newBusCoord.line] = {obj:lBus,type:BUS};
                            lBus.isInserted=true;
                        }
                    }
                }

                // finally push the container
                curlan.pushContainer(container);
            };
        };

        return areaMatrix;
    });