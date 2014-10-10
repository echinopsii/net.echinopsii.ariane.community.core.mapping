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
                minInternalLeftUDC   = 2, // CONTAINERS WITH UP & DOWN & INTERNAL LINKS ON LEFT
                maxInternalLeftUDC   = 3,
                minInternalLefTudC   = 4, // CONTAINERS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON LEFT
                maxInternalLefTudC   = 5,
                minInternalLeftC     = 6, // CONTAINERS WITH INTERNAL LINKS ON LEFT
                maxInternalLeftC     = 7,
                minInternalC         = 8, // CONTAINERS WITH INTERNAL LINKS (LEFT, RIGHT, BOTTOM, UP)
                maxInternalC         = 9,
                minInternalRightC    = 10,// CONTAINERS WITH INTERNAL LINKS ON RIGHT
                maxInternalRightC    = 11,
                minInternalRighTudC  = 12,// CONTAINERS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON RIGHT
                maxInternalRighTudC  = 13,
                minInternalRightUDC  = 14,// CONTAINERS WITH UP & DOWN & INTERNAL LINKS ON RIGHT
                maxInternalRightUDC  = 15,
                minRightUDC          = 16,// CONTAINERS WITH UP & DOWN LINKS ON RIGHT
                maxRightUDC          = 17;

            var addLineToMtx = function(index) {
                var i,ii;
                if (index < nbLines) {
                    for (i = 0, ii = nbColumns; i < ii; i++) {
                        for (var j = index, jj = nbLines; j <= jj; jj--) {
                            rows[i][jj] = rows[i][jj-1];
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

            var addColumnToMtx = function(index,flag) {
                var i, ii;
                if (index < nbColumns){
                    rows[nbColumns] = [];
                    for (i = index, ii=nbColumns; i < ii; ii--)  {
                        rows[ii] = rows[ii-1];
                    }
                }
                rows[index] = [];
                for(i = 0, ii = nbLines; i < ii ; i++) {
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

            var getInternalCoord = function() {
                var column = -1,
                    line   = -1;

                //FIRST : TRY TO GET FREE COORDS IN THE DOWN INTERNAL LINE
                initDownInternalLineWithZone(minInternalC,maxInternalC);
                line   = downInternalLine;
                column = getFreeBlockColumn(downInternalLine,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);

                //SECOND : TRY TO GET FREE COORDS IN THE INTERNAL LEFT COLUMNS AND THEN TRY IN INTERNAL RIGHT COLUMNS
                if (mtxColumnsSplitter[minInternalLeftC]!=-1 && mtxColumnsSplitter[maxInternalLeftC]!=-1 && column==-1) {
                    for (var i = maxInternalLine, ii = minInternalLine; i>=ii; i--) {
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
                    for (i = maxInternalLine, ii = minInternalLine; i>=ii; i--) {
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
                    initDownInternalLineWithZone(minInternalC,maxInternalC);
                    line   = upInternalLine;
                    column = getFreeBlockColumn(upInternalLine,mtxColumnsSplitter[minInternalC],mtxColumnsSplitter[maxInternalC]);
                }

                if (column==-1) {
                    //FOURTH : ADD A NEW INTERNAL COLUMN AND RETURN COORDS(downInternalLine,maxInternalC)
                    for (i = maxInternalC, ii = mtxColumnsSplitter.length; i<ii; i++){
                        if (mtxColumnsSplitter[i]!=-1) mtxColumnsSplitter[i]++
                    }
                    column=mtxColumnsSplitter[maxInternalC];
                    addColumnToMtx(column,FREE);
                    line=downInternalLine;
                }

                return {
                    column: column,
                    line  : line
                }
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
                        }
                    }
                }
                return column;
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
                            block.setTopLeftCoord(lbrdSpan + contSpan*j + curContWidth , lbrdSpan + contSpan*i + curContHeight);
                            block.definedNodesPoz();
                            curContWidth = curContWidth + block.getMaxRectSize().width;
                            if (block.getMaxRectSize().height>maxContHeight)
                                maxContHeight = block.getMaxRectSize().height;
                        }
                    }
                    curContHeight = curContHeight + maxContHeight;
                }
            };

            this.defineMtxContainerFinalPoz = function(topLeftX, topLeftY, lbrdSpan, contSpan, lanwidth, lanheight) {
                var curContHeight = topLeftY;
                for (var i = 0, ii = nbLines; i < ii; i++) {
                    var curContWidth  = topLeftX, maxContHeight=0;
                    for (var j = 0, jj = nbColumns; j < jj; j++) {
                        var block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED) {
                            block.setTopLeftCoord(lbrdSpan + contSpan*j + curContWidth , lbrdSpan + contSpan*i + curContHeight);
                            block.setMoveJail(topLeftX, topLeftY+lbrdSpan, topLeftX+lanwidth, topLeftY+lanheight);
                            block.definedNodesPoz();
                            curContWidth = curContWidth + block.getRectSize().width;
                            if (block.getRectSize().height>maxContHeight)
                                maxContHeight = block.getRectSize().height;
                        }
                    }
                    curContHeight = curContHeight + maxContHeight;
                }
            };

            this.defineLanContentSize = function() {
                var tmpHeight, tmpWidth, block;
                var i, ii, j, jj;
                contentHeight = 0 ;
                contentWidth  = 0 ;
                for (i = 0, ii = nbColumns; i < ii ; i++) {
                    tmpHeight = 0;
                    for (j = 0, jj = nbLines; j < jj; j++) {
                        block = rows[i][j];
                        if (block!=null && block!==FREE && block!==LOCKED)
                            tmpHeight = tmpHeight + block.getRectSize().height;
                    }
                    if (tmpHeight > contentHeight)
                        contentHeight=tmpHeight;
                }
                for (i = 0, ii = nbLines; i < ii ; i++) {
                    tmpWidth = 0;
                    for (j = 0, jj = nbColumns; j < jj; j++) {
                        block = rows[j][i];
                        if (block!=null && block!==FREE && block!==LOCKED)
                            tmpWidth = tmpWidth + block.getRectSize().width;
                    }
                    if (tmpWidth > contentWidth)
                        contentWidth = tmpWidth;
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

            this.addContainer = function(container) {
                var upColumn, downColumn, newInternalUDC, newInternalCoord, newUDC;

                if (container.layoutData.isConnectedInsideLan) {
                    if (container.layoutData.isConnectedToUpArea && container.layoutData.isConnectedToDownArea) {
                        newInternalUDC = getNewInternalUpDownColumn();
                        rows[newInternalUDC][0] = container;
                    } else if (container.layoutData.isConnectedToUpArea) {
                        upColumn = getInternalUpColumn();
                        rows[upColumn][upInternalLine] = container;
                        rows[upColumn][upLine]=LOCKED;
                    } else if (container.layoutData.isConnectedToDownArea) {
                        downColumn = getInternalDownColumn();
                        rows[downColumn][downInternalLine] = container;
                        rows[downColumn][downLine]=LOCKED;
                    } else  {
                        newInternalCoord = getInternalCoord();
                        rows[newInternalCoord.column][newInternalCoord.line] = container;
                    }
                } else {
                    if (container.layoutData.isConnectedToUpArea && container.layoutData.isConnectedToDownArea) {
                        newUDC = getNewUpDownColumn();
                        rows[newUDC][0] = container;
                    } else if (container.layoutData.isConnectedToUpArea) {
                        upColumn = getUpColumn();
                        rows[upColumn][upLine] = container;
                    } else if (container.layoutData.isConnectedToDownArea) {
                        downColumn = getDownColumn();
                        rows[downColumn][downLine] = container;
                    } else  {
                        newInternalCoord = getInternalCoord();
                        rows[newInternalCoord.column][newInternalCoord.line] = container;
                    }
                }

                /*
                if (nbLines==0) {
                    rows[nbLines] = [];
                    nbLines++;
                }

                rows[0][nbColumns] = container;
                nbColumns ++ ;
                */
            };
        }

        return lanMatrix;
    });