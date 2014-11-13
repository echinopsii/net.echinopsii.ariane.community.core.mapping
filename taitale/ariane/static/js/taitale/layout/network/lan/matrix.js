// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - NTWWW module - Lan Matrix                     │ \\
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
    function () {
        function lanMatrix() {

            var FREE   = "FREE",
                LOCKED = "LOCKED";

            var nbLines         = 0,
                nbColumns       = 0,
                rows            = [],
                lineMaxHeight   = [],
                columnMaxWidth  = [],
                contentWidth    = 0,
                contentHeight   = 0;

            var upLine              = -1, // UP LINK ONLY
                upInternalLine      = -1, // UP & INTERNAL
                minInternalLine     = -1, // INTERNAL ONLY
                maxInternalLine     = -1,
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
            var minLeftUDC           = 0, // CONTAINERS WITH UP & DOWN LINKS ON LEFT
                maxLeftUDC           = 1,
                minInternalLeftUDC   = 2, // CONTAINERS WITH UP & DOWN & EXTERNAL LINKS ON LEFT
                maxInternalLeftUDC   = 3,
                minInternalLefTudC   = 4, // CONTAINERS WITH UP OR DOWN LINKS & INTERNAL LINKS ON LEFT
                maxInternalLefTudC   = 5,
                minInternalLeftC     = 6, // CONTAINERS WITH INTERNAL LINKS ON LEFT
                maxInternalLeftC     = 7,
                minInternalC         = 8, // CONTAINERS WITH INTERNAL LINKS (LEFT, RIGHT, BOTTOM, UP)
                maxInternalC         = 9,
                minInternalRightC    = 10,// CONTAINERS WITH INTERNAL LINKS ON RIGHT
                maxInternalRightC    = 11,
                minInternalRighTudC  = 12,// CONTAINERS WITH UP OR DOWN LINKS & INTERNAL LINKS ON RIGHT
                maxInternalRighTudC  = 13,
                minInternalRightUDC  = 14,// CONTAINERS WITH UP & DOWN & EXTERNAL LINKS ON RIGHT
                maxInternalRightUDC  = 15,
                minRightUDC          = 16,// CONTAINERS WITH UP & DOWN LINKS ON RIGHT
                maxRightUDC          = 17;

            var containersList = [];

            var addLineToMtx = function(index) {
                var i,ii, j, jj;
                if (index < nbLines) {
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        for (j = index, jj = nbLines; j <= jj; jj--) {
                            rows[i][jj] = rows[i][jj-1];
                            if (rows[i][jj]!==FREE && rows[i][jj]!==LOCKED && rows[i][jj]!=null)
                                rows[i][jj].layoutData.lanMtxCoord= {x: jj, y: i};
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
                            rows[i][j].layoutData.lanMtxCoord= {x: j, y: i};
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
                    for (i = index, ii=nbColumns; i < ii; ii--)  {
                        rows[ii] = rows[ii-1];
                        for (j=0, jj=nbLines; j<jj; j++) {
                            if (rows[ii][j]!==FREE && rows[ii][j]!==LOCKED && rows[ii][j]!=null)
                                rows[ii][j].layoutData.lanMtxCoord= {x: j, y: ii};
                        }
                    }
                }
                rows[index] = [];
                for(i = 0, ii = nbLines; i < ii ; i++) {
                    rows[index][i] = flag;
                }
                nbColumns++;
            };

            var removeColumnFromMtx = function(index) {
                var i, ii, j, jj;
                for (i=index, ii=nbColumns; i < ii; i++)
                    rows[i] = rows[i+1]
                rows.pop();
                nbColumns--;
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

            var getUpOrDownFreeBlockColumn = function(minL,maxL) {
                var column=-1;

                //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL AREA
                if (mtxColumnsSplitter[minInternalC]!=-1 && mtxColumnsSplitter[maxInternalC]!=-1 && column==-1) {
                    if (maxL!=null)
                        column=getFreeColumnFromTo(minL,maxL,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);
                    else
                        column=getFreeBlockColumn(minL,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);
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
                        column=getColumnFromInitializedArea(minInternalLefTudC,maxInternalLefTudC);
                }

                // IF NO BLOCK FOUNDED THEN CREATE A NEW COLUMN IN LEFT|RIGHT UP or DOWN &/or INTERNAL AREA
                if (column==-1) {
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
                        if (minInternalLine!=-1) minInternalLine+=2;
                        if (maxInternalLine!=-1) maxInternalLine+=2;
                        if (downInternalLine!=-1) downInternalLine+=2;
                        if (downLine!=-1) downLine+=2;
                    }
                }
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
                        } else downLine++;
                    }
                }
                return column;
            };

            var initInternalLineWithZone = function(minZoneC, maxZoneC) {
                var column = -1;
                if (minInternalLine==-1){
                    if (nbLines == 0 && nbColumns ==0) {
                        minInternalLine=0;
                        maxInternalLine=0;
                        downInternalLine=1;
                        downLine=2;
                        column=++mtxColumnsSplitter[minZoneC];
                        mtxColumnsSplitter[maxZoneC]++
                        addColumnToMtx(column,FREE);
                        addLineToMtx(minInternalLine);
                        addLineToMtx(downInternalLine);
                        addLineToMtx(downLine);
                    } else {
                        if (upLine!=-1 && upInternalLine!=-1)
                            minInternalLine=upInternalLine+1;
                        else if (downInternalLine!=-1 && downLine!=-1)
                            minInternalLine=downInternalLine;
                        else
                            minInternalLine=0;
                        maxInternalLine=minInternalLine;
                        addLineToMtx(minInternalLine);
                        if (downInternalLine==-1) {
                            downInternalLine = nbLines;
                            addLineToMtx(downInternalLine);
                        } else downInternalLine++;
                        if (downLine==-1) {
                            downLine = nbLines;
                            addLineToMtx(downLine);
                        } else downLine++;
                    }
                }
                return column;
            };

            var addInternalMinLine = function() {
                if (minInternalLine!=-1 && maxInternalLine!=-1) {
                    addLineToMtx(minInternalLine);
                    maxInternalLine++;

                    if (downInternalLine==-1) {
                        downInternalLine = nbLines;
                        addLineToMtx(downInternalLine);
                    } else downInternalLine++;

                    if (downLine==-1) {
                        downLine = nbLines;
                        addLineToMtx(downLine);
                    } else downLine++;
                } else {
                    //TODO RAISE ERROR
                }
            };

            var addInternalMaxLine = function() {
                if (minInternalLine!=-1 && maxInternalLine!=-1) {
                    addLineToMtx(maxInternalLine);

                    if (downInternalLine==-1) {
                        downInternalLine = nbLines;
                        addLineToMtx(downInternalLine);
                    } else downInternalLine++;

                    if (downLine==-1) {
                        downLine = nbLines;
                        addLineToMtx(downLine);
                    } else downLine++;
                } else {
                    //TODO RAISE ERROR
                }
            };

            var getInternalUpColumn = function() {
                var column = -1;
                initUpInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column==-1)
                    column = getUpOrDownFreeBlockColumn(upLine,null);
                return column;
            };

            var getInternalDownColumn = function() {
                var column = -1;
                initDownInternalLineWithZone(minInternalLefTudC,maxInternalLefTudC);
                if (column == -1)
                    column = getUpOrDownFreeBlockColumn(downLine,null);
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
                        if (minInternalLine!=-1) minInternalLine++;
                        if (maxInternalLine!=-1) maxInternalLine++;
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

            var addNewLeftFUpDownColumn = function(minLeft, maxLeft) {
                var column = -1, i, ii;
                column=mtxColumnsSplitter[minLeft];
                for (i = minLeft+1, ii = mtxColumnsSplitter.length; i<ii; i++)
                    if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++;
                addColumnToMtx(column,LOCKED);
                return column;
            };

            var addNewRightFUpDownColumn = function(minRight, maxRight) {
                var column = -1, i, ii;
                column=++mtxColumnsSplitter[maxRight];
                for (i = maxRight+1, ii = mtxColumnsSplitter.length; i<ii; i++)
                    if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++;
                addColumnToMtx(column,LOCKED);
                return column;
            };

            var getNewFUpDownColumn = function(minLeft,maxLeft,minRight,maxRight,boolLeftRight) {
                var column = -1, i, ii;
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
                    if (!boolLeftRight)
                        column=addNewRightFUpDownColumn(minRight, maxRight);
                    else
                        column=addNewLeftFUpDownColumn(minLeft, maxLeft);
                }
                return column;
            };



            var getNewUpDownColumn = function() {
                var column = getNewFUpDownColumn(minLeftUDC,maxLeftUDC,minRightUDC,maxRightUDC,pushUDonLeft); pushUDonLeft=!pushUDonLeft;
                return column;
            };

            var getNewInternalUpDownColumn = function() {
                var column = getNewFUpDownColumn(minInternalLeftUDC,maxInternalLeftUDC,minInternalRightUDC,maxInternalRightUDC,pushInternalUDonLeft); pushInternalUDonLeft=!pushInternalUDonLeft;
                return column;
            };

            var getInternalCoord = function() {
                var column2ret,
                    line2ret  ;

                //FIRST : TRY TO GET FREE COORDS IN THE DOWN INTERNAL LINE
                initDownInternalLineWithZone(minInternalC,maxInternalC);
                line2ret   = downInternalLine;
                column2ret = getFreeBlockColumn(downInternalLine,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);

                //SECOND : TRY TO GET FREE COORDS IN THE INTERNAL LEFT COLUMNS AND THEN TRY IN INTERNAL RIGHT COLUMNS
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column2ret==-1) {
                    for (var i = maxInternalLine, ii = minInternalLine; i>=ii; i--) {
                        column2ret=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalLeftC],mtxColumnsSplitter[maxInternalLeftC]);
                        if (column2ret!=-1) {
                            line2ret = i;
                            break;
                        }
                    }
                } else {
                    if (column2ret==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column2ret=getColumnFromInitializedArea(minInternalLeftC,maxInternalLeftC);
                        line2ret=downInternalLine;
                    }
                }

                if (mtxColumnsSplitter[minInternalRightC]!=-1 && mtxColumnsSplitter[maxInternalRightC]!=-1 && column2ret==-1) {
                    for (i = maxInternalLine, ii = minInternalLine; i>=ii; i--) {
                        column2ret=getFreeBlockColumn(i,mtxColumnsSplitter[minInternalRightC],mtxColumnsSplitter[maxInternalRightC]);
                        if (column2ret!=-1) {
                            line2ret = i;
                            break;
                        }
                    }
                } else {
                    if (column2ret==-1) {
                        //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                        column2ret=getColumnFromInitializedArea(minInternalRightC,maxInternalRightC);
                        line2ret=downInternalLine;
                    }
                }

                //THIRD : TRY TO GET FREE COORDS IN THE UP INTERNAL LINE
                if (column2ret==-1) {
                    initDownInternalLineWithZone(minInternalC,maxInternalC);
                    line2ret   = upInternalLine;
                    column2ret = getFreeBlockColumn(upInternalLine,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);
                }

                //FOURTH : TRY TO GET FREE COORDS IN THE INTERNAL LINE
                if (column2ret==-1) {
                    initInternalLineWithZone(minInternalC,maxInternalC);
                    line2ret   = minInternalLine;
                    column2ret = getFreeBlockColumn(minInternalLine,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);
                }

                //FIFTH : ADD A NEW INTERNAL COLUMN AND RETURN COORDS(downInternalLine,maxInternalC)
                if (column2ret==-1) {
                    for (i = maxInternalC, ii = mtxColumnsSplitter.length; i<ii; i++){
                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                    }
                    column2ret=mtxColumnsSplitter[maxInternalC];
                    addColumnToMtx(column2ret,FREE);
                    line2ret=downInternalLine;
                }

                return {
                    column: column2ret,
                    line  : line2ret
                }
            };

            this.getMtxSize = function() {
                return {
                    x: nbLines,
                    y: nbColumns
                };
            };

            this.defineMtxContainerFirstPoz = function(topLeftX, topLeftY, lbrdSpan, contSpan, lanwidth, lanheight) {
                var curContHeight = topLeftY;
                for (var i = 0, ii = nbLines; i < ii; i++) {
                    var curContWidth  = topLeftX, maxContHeight=0;
                    for (var j = 0, jj = nbColumns; j < jj; j++) {
                        var block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            curContWidth = curContWidth + block.getMaxRectSize().width;
                            if (block.getMaxRectSize().height>maxContHeight)
                                maxContHeight = block.getMaxRectSize().height;
                        }
                    }
                    curContHeight = curContHeight + maxContHeight;
                }
            };

            this.defineMtxContainerFinalPoz = function(topLeftX, topLeftY, lbrdSpan, contSpan, lanwidth, lanheight) {
                var i, ii, j, jj, block;
                var maxColumnWidth = [];
                var curContHeight = topLeftY;

                for (i=0, ii=nbColumns; i < ii; i++) {
                    for (j=0, jj=nbLines; j < jj; j++) {
                        block = rows[i][j];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            if (maxColumnWidth[i]==null || maxColumnWidth[i] < block.getRectSize().width)
                                maxColumnWidth[i] = block.getRectSize().width;
                        } else if (maxColumnWidth[i]==null)
                            maxColumnWidth[i] = 0;
                    }
                }

                for (i = 0, ii = nbLines; i < ii; i++) {
                    var curContWidth  = topLeftX, maxContHeight=0;
                    for (j = 0, jj = nbColumns; j < jj; j++) {
                        block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            block.setTopLeftCoord(lbrdSpan + contSpan*j + curContWidth , lbrdSpan + contSpan*i + curContHeight);
                            block.setMoveJail(topLeftX, topLeftY+lbrdSpan, topLeftX+lanwidth, topLeftY+lanheight);
                            block.definedNodesPoz();
                            curContWidth = curContWidth + block.getRectSize().width;
                            if (block.getRectSize().height>maxContHeight)
                                maxContHeight = block.getRectSize().height;
                        } else {
                            curContWidth = curContWidth + maxColumnWidth[j];
                        }
                    }
                    curContHeight = curContHeight + maxContHeight;
                }
            };

            this.defineLanContentSize = function() {
                var block;
                var maxLineHeight = [], maxColumnWidth = [];
                var i, ii, j, jj;
                contentHeight = 0 ;
                contentWidth  = 0 ;

                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        block = rows[i][j];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            if (maxColumnWidth[i]==null || maxColumnWidth[i] < block.getRectSize().width)
                                maxColumnWidth[i] = block.getRectSize().width;
                        } else if (maxColumnWidth[i]==null)
                            maxColumnWidth[i] = 0;
                    }
                    contentWidth+=maxColumnWidth[i];
                }

                for (i = 0, ii = nbLines; i < ii; i++) {
                    for (j=0, jj = nbColumns; j < jj; j++) {
                        block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            if (maxLineHeight[i]==null || (maxLineHeight[i]!=null && maxLineHeight[i] < block.getRectSize().height))
                                maxLineHeight[i] = block.getRectSize().height;
                        } else if (maxLineHeight[i]==null)
                            maxLineHeight[i]=0;
                    }
                    contentHeight+=maxLineHeight[i];
                }
            };

            this.defineLanContentMaxSize = function() {
                var tmpHeight, tmpWidth, block;
                var i, ii, j, jj;
                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    tmpHeight = 0;
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        block = rows[i][j];
                        if (block!=null && block!==FREE && block!==LOCKED)
                            tmpHeight = tmpHeight + block.getMaxRectSize().height;
                    }
                    if (tmpHeight > contentHeight)
                        contentHeight=tmpHeight;
                }
                for (i = 0, ii = nbLines; i < ii ; i++) {
                    tmpWidth = 0;
                    for (j = 0, jj = nbColumns; j < jj; j++) {
                        block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED)
                            tmpWidth = tmpWidth + block.getMaxRectSize().width;
                    }
                    if (tmpWidth > contentWidth)
                        contentWidth = tmpWidth;
                }
            };

            this.getLanContentMaxSize = function () {
                return {
                    width  : contentWidth,
                    height : contentHeight
                };
            };

            this.getLanContentSize = function () {
                return {
                    width  : contentWidth,
                    height : contentHeight
                };
            };

            this.getContainerFromMtx = function (x,y) {
                var block = rows[y][x];
                if (block!=null && block!==FREE && block!==LOCKED)
                    return block;
                else
                    return null;
            };

            var addConnectedLanContainerToContainer = function(targetLanContainer, sourceContainer) {
                var i, ii, isRedefined = false;
                for (i = 0, ii = targetLanContainer.layoutData.lanConnectedContainer.length; i < ii; i++) {
                    if (targetLanContainer.layoutData.lanConnectedContainer[i].ID === sourceContainer.ID) {
                        sourceContainer.weight++;
                        isRedefined = true;
                        break;
                    }
                }

                if (!isRedefined)
                    targetLanContainer.layoutData.lanConnectedContainer.push(sourceContainer);

                targetLanContainer.layoutData.lanInternalLinksWeight++;
            };

            var optimizeContainerLinkedToLeftOrRightLan = function(container, minLeft, maxLeft, minRight, maxRight) {
                var i, ii, j, jj;
                var targetSwapColumn = -1, targetSwapLine = container.layoutData.lanMtxCoord.x;
                if (container.layoutData.isConnectedToLeftLan && !container.layoutData.isConnectedToRightLan) {
                    if (mtxColumnsSplitter[minLeft]!=-1 && mtxColumnsSplitter[maxLeft]!=-1 && targetSwapColumn==-1) {
                        //EXTENDS minLeft,maxLeft area
                        if (targetSwapLine==upLine || targetSwapLine==upInternalLine) {
                            if (minInternalLine==-1 || maxInternalLine==-1)
                                initInternalLineWithZone(minLeft, maxLeft);
                            else
                                addInternalMinLine();
                            targetSwapLine=minInternalLine;
                        } else if (targetSwapLine==downLine || targetSwapLine==downInternalLine) {
                            if (minInternalLine==-1 || maxInternalLine==-1)
                                initInternalLineWithZone(minLeft, maxLeft);
                            else
                                addInternalMaxLine();
                            targetSwapLine=maxInternalLine;
                        } else {
                            addInternalMaxLine();
                            targetSwapLine=maxInternalLine;
                        }
                        addNewLeftFUpDownColumn(minLeft, maxLeft);
                        targetSwapColumn=mtxColumnsSplitter[minLeft];
                    } else {
                        if (targetSwapColumn==-1)
                            //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                            targetSwapColumn=getColumnFromInitializedArea(minLeft,maxLeft);
                    }
                } else if (container.layoutData.isConnectedToRightLan && !container.layoutData.isConnectedToLeftLan) {
                    if (mtxColumnsSplitter[minRight]!=-1 && mtxColumnsSplitter[maxRight]!=-1 && targetSwapColumn==-1) {
                        //EXTENDS minRight,maxRight area
                        if (targetSwapLine==upLine || targetSwapLine==upInternalLine) {
                            if (minInternalLine==-1 || maxInternalLine==-1)
                                initInternalLineWithZone(minRight, maxRight);
                            else
                                addInternalMinLine();
                            targetSwapLine=minInternalLine;
                        } else if (targetSwapLine==downLine || targetSwapLine==downInternalLine) {
                            if (minInternalLine==-1 || maxInternalLine==-1)
                                initInternalLineWithZone(minRight, maxRight);
                            else
                                addInternalMaxLine();
                            targetSwapLine=maxInternalLine;
                        } else {
                            addInternalMaxLine();
                            targetSwapLine=maxInternalLine;
                        }
                        addNewRightFUpDownColumn(minRight, maxRight);
                        targetSwapColumn=mtxColumnsSplitter[maxRight];
                    } else {
                        if (targetSwapColumn==-1)
                            //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                            targetSwapColumn=getColumnFromInitializedArea(minRight,maxRight);
                    }
                }
                if (targetSwapColumn!=-1) {
                    var swapObj = rows[targetSwapColumn][targetSwapLine];
                    rows[targetSwapColumn][targetSwapLine] = container;
                    rows[container.layoutData.lanMtxCoord.y][container.layoutData.lanMtxCoord.x] = swapObj;
                    container.layoutData.lanMtxCoord = {x:targetSwapLine, y: targetSwapColumn}
                }
            };

            this.optimizeContainerMtxCoord = function() {
                var i, ii, j, jj;
                var container, linkedContainers, linkedContainer, linkedBuss, linkedBus;
                var containersToExternalLeft = [], containersToExternalRight = [];
                var lan1, lan2;

                //FIRST : SORT ACCORDING INTERNAL LINKS WIEGHT
                containersList.sort(function(container1, container2){
                    return (container2.layoutData.lanInternalLinksWeight-container1.layoutData.lanInternalLinksWeight);
                });

                //SECOND : ADD CONTAINER LAYOUT DATA TO FEET LAN POSITIONING OPTIMIZATION
                for (i=0, ii=containersList.length; i<ii; i++) {
                    container = containersList[i];
                    if (container.layoutData.isConnectedInsideArea) {
                        linkedContainers = container.getLinkedContainers();
                        for(j=0, jj=linkedContainers.length; j < jj; j++) {
                            linkedContainer = linkedContainers[j];
                            if (container.localisation.equalArea(linkedContainer.localisation)) {
                                lan1 = container.layoutData.lan;
                                lan2 = linkedContainer.layoutData.lan;

                                if (lan1.layoutData.areaMtxCoord.x > lan2.layoutData.areaMtxCoord.x)
                                    container.layoutData.isConnectedToUpLan = true;
                                else if (lan1.layoutData.areaMtxCoord.x < lan2.layoutData.areaMtxCoord.x)
                                    container.layoutData.isConnectedToDowntLan = true;

                                if (lan1.layoutData.areaMtxCoord.y > lan2.layoutData.areaMtxCoord.y && !container.layoutData.isConnectedToLeftLan){
                                    container.layoutData.isConnectedToLeftLan = true;
                                    containersToExternalLeft.push(container);
                                } else if (lan1.layoutData.areaMtxCoord.y < lan2.layoutData.areaMtxCoord.y && !container.layoutData.isConnectedToRightLan ) {
                                    container.layoutData.isConnectedToRightLan = true;
                                    containersToExternalRight.push(container);
                                }

                                container.layoutData.lanUpDownIdx+=(lan1.layoutData.areaMtxCoord.x - lan2.layoutData.areaMtxCoord.x)
                            }
                        }

                        linkedBuss = container.getLinkedBus();
                        for (j=0, jj=linkedBuss.length; j < jj; j++) {
                            linkedBus = linkedBuss[j];
                            lan1 = container.layoutData.lan;

                            if (lan1.layoutData.areaMtxCoord.x > linkedBus.layoutData.areaMtxCoord.x)
                                container.layoutData.isConnectedToUpLan = true;
                            else if (lan1.layoutData.areaMtxCoord.x < linkedBus.layoutData.areaMtxCoord.x)
                                container.layoutData.isConnectedToDownLan = true;

                            if (lan1.layoutData.areaMtxCoord.y > linkedBus.layoutData.areaMtxCoord.y && !container.layoutData.isConnectedToLeftLan) {
                                container.layoutData.isConnectedToLeftLan = true;
                                containersToExternalLeft.push(container);
                            }
                            else if (lan1.layoutData.areaMtxCoord.y < linkedBus.layoutData.areaMtxCoord.y && !container.layoutData.isConnectedToRightLan ) {
                                container.layoutData.isConnectedToRightLan = true;
                                containersToExternalRight.push(container);
                            }

                            container.layoutData.lanUpDownIdx+=(lan1.layoutData.areaMtxCoord.x - linkedBus.layoutData.areaMtxCoord.x)
                        }
                    }
                }

                //THIRD : OPTIMIZE COLUMN POZ
                for (i=0, ii=containersList.length; i < ii; i++) {
                    container = containersList[i];
                    if (container.layoutData.isConnectedInsideLan) {
                        optimizeContainerLinkedToLeftOrRightLan(container, minInternalLeftUDC, maxInternalLeftUDC, minInternalRightUDC, maxInternalRightUDC);
                    } else {
                        optimizeContainerLinkedToLeftOrRightLan(container, minLeftUDC, maxLeftUDC, minRightUDC, maxRightUDC);
                    }
                }

                //FOURTH : OPTIMIZE LINE POZ
                containersToExternalLeft.sort(function(container1, container2){
                    return (container2.layoutData.lanUpDownIdx-container1.layoutData.lanUpDownIdx);
                });
                for (i=0, ii=containersToExternalLeft.length; i < ii; i++) {
                    var container = containersToExternalLeft[i];
                    if (i < nbLines) {
                        var swapObj = rows[container.layoutData.lanMtxCoord.y][i];
                        rows[container.layoutData.lanMtxCoord.y][i] = container;
                        rows[container.layoutData.lanMtxCoord.y][container.layoutData.lanMtxCoord.x] = swapObj;
                        container.layoutData.lanMtxCoord = {x:i, y: container.layoutData.y}
                    } else {
                        if (downInternalLine!=-1 && downLine !=1) {
                            downLine = nbLines;
                            addLineToMtx(downLine);
                        } else if (minInternalLine==-1 && maxInternalLine==-1) {
                            maxInternalLine = nbLines;
                            addLineToMtx(maxInternalLine);
                        } else if (upInternalLine!=-1 && upLine!=-1) {
                            upInternalLine = nbLines;
                            addLineToMtx(upInternalLine);
                        }
                    }
                }

                containersToExternalRight.sort(function(container1, container2){
                    return (container2.layoutData.lanUpDownIdx-container1.layoutData.lanUpDownIdx);
                });
                for (i=0, ii=containersToExternalRight.length; i < ii; i++) {
                    var container = containersToExternalRight[i];
                    if (i < nbLines) {
                        var swapObj = rows[container.layoutData.lanMtxCoord.y][i];
                        rows[container.layoutData.lanMtxCoord.y][i] = container;
                        rows[container.layoutData.lanMtxCoord.y][container.layoutData.lanMtxCoord.x] = swapObj;
                        container.layoutData.lanMtxCoord = {x:i, y: container.layoutData.y}
                    } else {
                        if (downInternalLine!=-1 && downLine !=1) {
                            downLine = nbLines;
                            addLineToMtx(downLine);
                        } else if (minInternalLine==-1 && maxInternalLine==-1) {
                            maxInternalLine = nbLines;
                            addLineToMtx(maxInternalLine);
                        } else if (upInternalLine!=-1 && upLine!=-1) {
                            upInternalLine = nbLines;
                            addLineToMtx(upInternalLine);
                        }
                    }
                }

                //FIFTH : CLEAN MATRIX (IE REMOVE UNUSED COLUMN OR UNUSED LINE)
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

            this.addContainer = function(container) {
                var upColumn, downColumn, newInternalUDC, newInternalCoord, newUDC;
                var i, ii;

                if (container.layoutData.isConnectedInsideLan) {
                    if (container.layoutData.isConnectedToUpArea && container.layoutData.isConnectedToDownArea) {
                        newInternalUDC = getNewInternalUpDownColumn();
                        rows[newInternalUDC][0] = container;
                        container.layoutData.lanMtxCoord = {x:0, y:newInternalUDC}
                    } else if (container.layoutData.isConnectedToUpArea) {
                        upColumn = getInternalUpColumn();
                        rows[upColumn][upInternalLine] = container;
                        rows[upColumn][upLine]=LOCKED;
                        container.layoutData.lanMtxCoord = {x:upLine, y:upColumn}
                    } else if (container.layoutData.isConnectedToDownArea) {
                        downColumn = getInternalDownColumn();
                        rows[downColumn][downInternalLine] = container;
                        rows[downColumn][downLine]=LOCKED;
                        container.layoutData.lanMtxCoord = {x:downLine, y:downColumn}
                    } else  {
                        newInternalCoord = getInternalCoord(container);
                        rows[newInternalCoord.column][newInternalCoord.line] = container;
                        container.layoutData.lanMtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column}
                    }
                } else {
                    if (container.layoutData.isConnectedToUpArea && container.layoutData.isConnectedToDownArea) {
                        newUDC = getNewUpDownColumn();
                        rows[newUDC][0] = container;
                        container.layoutData.lanMtxCoord = {x:0, y:newUDC}
                    } else if (container.layoutData.isConnectedToUpArea) {
                        upColumn = getUpColumn();
                        rows[upColumn][upLine] = container;
                        container.layoutData.lanMtxCoord = {x: upLine, y:upColumn}
                    } else if (container.layoutData.isConnectedToDownArea) {
                        downColumn = getDownColumn();
                        rows[downColumn][downLine] = container;
                        container.layoutData.lanMtxCoord = {x: downLine, y:downColumn}
                    } else  {
                        newInternalCoord = getInternalCoord(container);
                        rows[newInternalCoord.column][newInternalCoord.line] = container;
                        container.layoutData.lanMtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column}
                    }
                }

                containersList.push(container);

                //ADD CURRENT CONTAINER TO LAN CONTAINER CONNECTED LIST
                var linkedContainers = container.getLinkedContainers();
                for (i=0, ii=linkedContainers.length; i << ii; i++)
                    if (container.localisation.equal(linkedContainers[i]))
                        addConnectedLanContainerToContainer(linkedContainers[i], container)
            };
        }

        return lanMatrix;
    });