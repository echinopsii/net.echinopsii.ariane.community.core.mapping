// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library                                                 │ \\
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
    function (helper_) {

        var FREE   = "FREE",
            LOCKED = "LOCKED";

        function Matrix() {
            this.helper = new helper_();

            this.nbLines   = 0;
            this.nbColumns = 0;
            this.zemtx     = [];

            this.lineMaxHeight  = [];
            this.columnMaxWidth = [];
            this.contentWidth   = 0;
            this.contentHeight  = 0;

            this.upLine            = -1; // UP LINK ONLY
            this.upInternalLine    = -1; // UP & INTERNAL
            this.minInternalLine   = -1;
            this.maxInternalLine   = -1;
            this.downInternalLine  = -1; // DOWN & INTERNAL
            this.downLine          = -1; // DOWN LINK ONLY

            // PUSH LEFT/RIGHT BALANCER
            //noinspection JSUnusedLocalSymbols
            this.pushUDonLeft         = false;
            this.pushInternalUDonLeft = false;
            this.pushInternaLudOnLeft = false;
            this.pushInternalOnLeft   = false;

            // COLUMNS SPLITTER TABLE
            this.mtxColumnsSplitter  = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1];
            // COLUMNS SPLITTER TABLE IDX
            this.minLeftUDC          = 0;  // LANS WITH UP & DOWN LINKS ON LEFT
            this.maxLeftUDC          = 1;
            this.minInternalLeftUDC  = 2;  // LANS WITH UP & DOWN & INTERNAL LINKS ON LEFT
            this.maxInternalLeftUDC  = 3;
            this.minInternalLefTudC  = 4;  // LANS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON LEFT
            this.maxInternalLefTudC  = 5;
            this.minInternalLeftC    = 6;  // LANS WITH INTERNAL LINKS ON LEFT
            this.maxInternalLeftC    = 7;
            this.minInternalC        = 8;  // INTERNAL OBJECT WITH INTERNAL LINKS ONLY
            this.maxInternalC        = 9;
            this.minInternalRightC   = 10; // LANS WITH INTERNAL LINKS ON RIGHT
            this.maxInternalRightC   = 11;
            this.minInternalRighTudC = 12; // LANS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON RIGHT
            this.maxInternalRighTudC = 13;
            this.minInternalRightUDC = 14; // LANS WITH UP & DOWN & INTERNAL LINKS ON RIGHT
            this.maxInternalRightUDC = 15;
            this.minRightUDC         = 16; // LANS WITH UP & DOWN LINKS ON RIGHT
            this.maxRightUDC         = 17;

            this.objectsList   = [];
        }

        /*
         * Internal methods use only
         */


        Matrix.prototype.addLineToMtx = function(index) {
            var i,ii;
            if (index < this.nbLines) {
                for (i = 0, ii = this.nbColumns; i < ii; i++) {
                    for (var j = index, jj = this.nbLines; j <= jj; jj--) {
                        this.zemtx[i][jj] = this.zemtx[i][jj-1];
                        if (this.zemtx[i][jj]!==FREE && this.zemtx[i][jj]!==LOCKED && this.zemtx[i][jj]!=null)
                            this.zemtx[i][jj].obj.layoutData.mtxCoord= {x: jj, y: i};
                    }
                }
            }
            for (i = 0, ii = this.nbColumns; i < ii ; i++) {
                if ((i>=this.mtxColumnsSplitter[this.minLeftUDC] && i<=this.mtxColumnsSplitter[this.maxLeftUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minInternalLeftUDC] && i<=this.mtxColumnsSplitter[this.maxInternalLeftUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minInternalRightUDC] && i<=this.mtxColumnsSplitter[this.maxInternalRightUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minRightUDC] && i<=this.mtxColumnsSplitter[this.maxRightUDC]))
                    this.zemtx[i][index] = LOCKED;
                else
                    this.zemtx[i][index] = FREE;
            }
            this.nbLines++;
        };

        Matrix.prototype.removeLineFromMtx = function(index) {
            var i, ii, j, jj;
            for (i=0, ii=this.nbColumns; i < ii; i++) {
                for (j=index, jj = this.nbLines; j < jj; j++) {
                    this.zemtx[i][j] = this.zemtx[i][j+1];
                    if (this.zemtx[i][j]!==FREE && this.zemtx[i][j]!==LOCKED && this.zemtx[i][j]!=null)
                        this.zemtx[i][j].obj.layoutData.mtxCoord= {x: j, y: i};
                }
            }
            for (i=0, ii=this.nbColumns; i < ii; i++)
                this.zemtx[i].pop();

            this.nbLines--;
        };

        Matrix.prototype.addColumnToMtx = function(index,flag) {
            var i, ii, j, jj;
            if (index < this.nbColumns){
                this.zemtx[this.nbColumns] = [];
                for (i = index, ii=this.nbColumns; i < ii; ii--)  {
                    this.zemtx[ii] = this.zemtx[ii-1];
                    for (j=0, jj=this.nbLines; j<jj; j++) {
                        if (this.zemtx[ii][j]!==FREE && this.zemtx[ii][j]!==LOCKED && this.zemtx[ii][j]!=null)
                            this.zemtx[ii][j].obj.layoutData.mtxCoord= {x: j, y: ii};
                    }
                }
            }
            this.zemtx[index] = [];
            for(i = 0, ii = this.nbLines; i < ii ; i++)
                this.zemtx[index][i] = flag;

            this.nbColumns++;
        };

        Matrix.prototype.removeColumnFromMtx = function(index) {
            var i, ii, j, jj;
            for (i=index, ii=this.nbColumns; i < ii; i++) {
                this.zemtx[i] = this.zemtx[i+1];
                for (j=0, jj=this.nbLines; j<jj; j++) {
                    if (this.zemtx[i][j]!==FREE && this.zemtx[i][j]!==LOCKED && this.zemtx[i][j]!=null)
                        this.zemtx[i][j].obj.layoutData.mtxCoord= {x: j, y: i};
                }
            }
            this.zemtx.pop();
            this.nbColumns--;
        };

        Matrix.prototype.isColumnFreeFromMinToMax = function(columnIdx, minLine, maxLine) {
            var ret = true;
            for (var j = minLine, jj = maxLine; j <= jj; j++) {
                if (this.zemtx[columnIdx][j]!=FREE) {
                    ret = false;
                    break;
                }
            }
            return ret;
        };

        Matrix.prototype.isLineFreeFromMinToMax = function(lineIdx, minColumn, maxColumn) {
            var ret = true;
            for (var j = minColumn, jj = maxColumn; j <= jj; j++) {
                if (this.zemtx[j][lineIdx]!=FREE) {
                    ret = false;
                    break;
                }
            }
            return ret;
        };

        Matrix.prototype.getFreeColumnFromTo = function(minL,maxL,minC,maxC) {
            var column = -1;
            for (var i = minC, ii=maxC; i<=ii; i++) {
                if (this.isColumnFreeFromMinToMax(i,minL,maxL)) {
                    column=i;
                    break;
                }
            }
            return column;
        };

        Matrix.prototype.getFreeBlockColumn = function(lineIdx,minC,maxC) {
            var column = -1;
            for (var i=minC, ii=maxC; i<=ii;i++) {
                if (this.zemtx[i][lineIdx]===FREE) {
                    column=i;
                    break;
                }
            }
            return column;
        };

        Matrix.prototype.initUpInternalLineWithZone = function(minZoneC,maxZoneC) {
            var column = -1;
            if (this.upInternalLine==-1) {
                if (this.nbLines == 0 && this.nbColumns == 0) {
                    this.upLine=0;
                    this.upInternalLine=1;
                    column=++this.mtxColumnsSplitter[minZoneC];
                    this.mtxColumnsSplitter[maxZoneC]++;
                    this.addColumnToMtx(column,FREE);
                    this.addLineToMtx(this.upLine);
                    this.addLineToMtx(this.upInternalLine);
                } else {
                    if (this.upLine==-1) {
                        this.upLine = 0;
                        this.addLineToMtx(this.upLine);
                    }
                    this.upInternalLine=1;
                    this.addLineToMtx(this.upInternalLine);
                    if (this.minInternalLine!=-1) this.minInternalLine+=2;
                    if (this.maxInternalLine!=-1) this.maxInternalLine+=2;
                    if (this.downInternalLine!=-1) this.downInternalLine+=2;
                    if (this.downLine!=-1) this.downLine+=2;
                }
            }
            return column;
        };

        Matrix.prototype.initDownInternalLineWithZone = function(minZoneC,maxZoneC) {
            var column = -1;
            if (this.downInternalLine == -1) {
                if (this.nbLines == 0 && this.nbColumns == 0) {
                    this.downInternalLine=0;
                    this.downLine=1;
                    column=++this.mtxColumnsSplitter[minZoneC];
                    this.mtxColumnsSplitter[maxZoneC]++;
                    this.addColumnToMtx(column,FREE);
                    this.addLineToMtx(this.downInternalLine);
                    this.addLineToMtx(this.downLine);
                } else {
                    this.downInternalLine=this.nbLines;
                    this.addLineToMtx(this.downInternalLine);
                    if (this.downLine == -1) {
                        this.downLine = this.nbLines;
                        this.addLineToMtx(this.downLine);
                    } else this.downLine++;
                }
            }
            return column;
        };

        Matrix.prototype.initInternalLineWithZone = function(minZoneC, maxZoneC) {
            var column = -1;
            if (this.minInternalLine==-1){
                if (this.nbLines == 0 && this.nbColumns ==0) {
                    this.minInternalLine=0;
                    this.maxInternalLine=0;
                    this.downInternalLine=1;
                    this.downLine=2;
                    column=++this.mtxColumnsSplitter[minZoneC];
                    this.mtxColumnsSplitter[maxZoneC]++;
                    this.addColumnToMtx(column,FREE);
                    this.addLineToMtx(this.minInternalLine);
                    this.addLineToMtx(this.downInternalLine);
                    this.addLineToMtx(this.downLine);
                } else {
                    if (this.upLine!=-1 && this.upInternalLine!=-1)
                        this.minInternalLine=this.upInternalLine+1;
                    else if (this.downInternalLine!=-1 && this.downLine!=-1)
                        this.minInternalLine=this.downInternalLine;
                    else
                        this.minInternalLine=0;
                    this.maxInternalLine=this.minInternalLine;
                    this.addLineToMtx(this.minInternalLine);
                    if (this.downInternalLine==-1) {
                        this.downInternalLine = nbLines;
                        this.addLineToMtx(this.downInternalLine);
                    } else this.downInternalLine++;
                    if (this.downLine==-1) {
                        this.downLine = this.nbLines;
                        this.addLineToMtx(this.downLine);
                    } else this.downLine++;
                }
            }
            return column;
        };

        Matrix.prototype.addInternalMinLine = function() {
            if (this.minInternalLine!=-1 && this.maxInternalLine!=-1) {
                this.addLineToMtx(this.minInternalLine);
                this.maxInternalLine++;

                if (this.downInternalLine==-1) {
                    this.downInternalLine = this.nbLines;
                    this.addLineToMtx(this.downInternalLine);
                } else this.downInternalLine++;

                if (this.downLine==-1) {
                    this.downLine = this.nbLines;
                    this.addLineToMtx(this.downLine);
                } else this.downLine++;
            } else {
                //TODO RAISE ERROR
            }
        };

        Matrix.prototype.addInternalMaxLine = function() {
            if (this.minInternalLine!=-1 && this.maxInternalLine!=-1) {
                this.addLineToMtx(this.maxInternalLine);

                if (this.downInternalLine==-1) {
                    this.downInternalLine = this.nbLines;
                    this.addLineToMtx(this.downInternalLine);
                } else this.downInternalLine++;

                if (this.downLine==-1) {
                    this.downLine = this.nbLines;
                    this.addLineToMtx(this.downLine);
                } else this.downLine++;
            } else {
                //TODO RAISE ERROR
            }
        };

        Matrix.prototype.getColumnFromInitializedArea = function(minSplitterIdx, maxSplitterIdx) {
            var column = -1;
            var i,ii;
            for (i = minSplitterIdx+2, ii = this.mtxColumnsSplitter.length-1; i<ii; i+=2){
                if (this.mtxColumnsSplitter[i]!=-1 && this.mtxColumnsSplitter[i+1]!=-1) {
                    column = this.mtxColumnsSplitter[i];
                    this.mtxColumnsSplitter[minSplitterIdx]=column;
                    this.mtxColumnsSplitter[maxSplitterIdx]=column;
                    this.addColumnToMtx(column,FREE);
                    break;
                }
            }
            if (column==-1) {
                for (i = maxSplitterIdx-2, ii=1; i>ii; i-=2) {
                    if (this.mtxColumnsSplitter[i]!=-1 && this.mtxColumnsSplitter[i-1]!=-1) {
                        column = this.mtxColumnsSplitter[i]+1;
                        this.mtxColumnsSplitter[minSplitterIdx]=column;
                        this.mtxColumnsSplitter[maxSplitterIdx]=column;
                        this.addColumnToMtx(column,FREE);
                        break;
                    }
                }
            } else {
                for (i = maxSplitterIdx+1, ii = this.mtxColumnsSplitter.length; i<ii; i++){
                    if (this.mtxColumnsSplitter[i]!=-1) this.mtxColumnsSplitter[i]++
                }
            }
            return column;
        };

        Matrix.prototype.getUpOrDownFreeBlockColumn = function(minL,maxL) {
            var column=-1;

            //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL AREA
            if (this.mtxColumnsSplitter[this.minInternalC]!=-1 && this.mtxColumnsSplitter[this.maxInternalC]!=-1 && column==-1) {
                if (maxL!=null)
                    column=this.getFreeColumnFromTo(minL,maxL,this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);
                else
                    column=this.getFreeBlockColumn(minL,this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);
            }

            //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT AREA
            if (this.mtxColumnsSplitter[this.minInternalLeftC]!=-1 && this.mtxColumnsSplitter[this.maxInternalLeftC]!=-1 && column==-1) {
                if (maxL!=null)
                    column=this.getFreeColumnFromTo(minL,maxL,this.mtxColumnsSplitter[this.minInternalLeftC],this.mtxColumnsSplitter[this.maxInternalLeftC]);
                else
                    column=this.getFreeBlockColumn(minL,this.mtxColumnsSplitter[this.minInternalLeftC],this.mtxColumnsSplitter[this.maxInternalLeftC]);
            }

            //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT AREA
            if (this.mtxColumnsSplitter[this.minInternalRightC]!=-1 && this.mtxColumnsSplitter[this.maxInternalRightC]!=-1 && column==-1) {
                if (maxL!=null)
                    column=this.getFreeColumnFromTo(minL, maxL, this.mtxColumnsSplitter[this.minInternalRightC],this.mtxColumnsSplitter[this.maxInternalRightC]);
                else
                    column=this.getFreeBlockColumn(minL,this.mtxColumnsSplitter[this.minInternalRightC],this.mtxColumnsSplitter[this.maxInternalRightC]);
            }

            //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL LEFT UP or DOWN &/or INTERNAL AREA
            if (this.mtxColumnsSplitter[this.minInternalLefTudC]!=-1 && this.mtxColumnsSplitter[this.maxInternalLefTudC]!=-1 && column==-1) {
                if (maxL!=null)
                    column=this.getFreeColumnFromTo(minL,maxL,this.mtxColumnsSplitter[this.minInternalLefTudC],this.mtxColumnsSplitter[this.maxInternalLefTudC]);
                else
                    column=this.getFreeBlockColumn(minL,this.mtxColumnsSplitter[this.minInternalLefTudC],this.mtxColumnsSplitter[this.maxInternalLefTudC]);
            } else {
                if (column==-1)
                //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                    column=this.getColumnFromInitializedArea(this.minInternalLefTudC,this.maxInternalLefTudC);
            }

            //CHECK IF THERE IS FREE UP BLOCK IN INTERNAL RIGHT UP or DOWN &/or INTERNAL AREA
            if (this.mtxColumnsSplitter[this.minInternalRighTudC]!=-1 && this.mtxColumnsSplitter[this.maxInternalRighTudC]!=-1 && column==-1) {
                if (maxL!=null)
                    column=this.getFreeColumnFromTo(minL,maxL,this.mtxColumnsSplitter[this.minInternalRighTudC],this.mtxColumnsSplitter[this.maxInternalRighTudC]);
                else
                    column=this.getFreeBlockColumn(minL,mtxColumnsSplitter[this.minInternalRighTudC],mtxColumnsSplitter[this.maxInternalRighTudC]);
            } else {
                if (column==-1)
                //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                    column=this.getColumnFromInitializedArea(this.minInternalLefTudC,this.maxInternalLefTudC);
            }

            // IF NO BLOCK FOUNDED THEN CREATE A NEW COLUMN IN LEFT|RIGHT UP or DOWN &/or INTERNAL AREA
            if (column==-1) {
                if (!this.pushInternaLudOnLeft) {
                    this.pushInternaLudOnLeft=true;
                    column=++this.mtxColumnsSplitter[this.maxInternalRighTudC];
                    this.addColumnToMtx(column,FREE);
                } else {
                    this.pushInternaLudOnLeft=false;
                    column=this.mtxColumnsSplitter[this.minInternalLefTudC];
                    for (var i = this.minInternalLefTudC, ii = this.mtxColumnsSplitter.length; i<ii; i++){
                        if (this.mtxColumnsSplitter[i]!=-1) this.mtxColumnsSplitter[i]++
                    }
                    this.addColumnToMtx(column,FREE);
                }
            }
            return column;
        };

        Matrix.prototype.getInternalUpColumnIdx = function() {
            var column = -1;
            this.initUpInternalLineWithZone(this.minInternalLefTudC,this.maxInternalLefTudC);
            if (column==-1)
                column = this.getUpOrDownFreeBlockColumn(this.upLine,null);
            return column;
        };

        Matrix.prototype.getInternalDownColumnIdx = function() {
            var column = -1;
            this.initDownInternalLineWithZone(this.minInternalLefTudC,this.maxInternalLefTudC);
            if (column == -1)
                column = this.getUpOrDownFreeBlockColumn(this.downLine,null);
            return column;
        };

        Matrix.prototype.getUpColumn = function() {
            var column = -1;
            if (this.upLine==-1) {
                if (this.nbLines == 0 && this.nbColumns == 0) {
                    this.upLine=0;
                    column=++this.mtxColumnsSplitter[this.minInternalLefTudC];
                    this.mtxColumnsSplitter[this.maxInternalLefTudC]++;
                    this.addColumnToMtx(column,FREE);
                    this.addLineToMtx(this.upLine);
                } else {
                    this.upLine = 0;
                    this.addLineToMtx(upLine);
                    if (this.minInternalLine!=-1) this.minInternalLine++;
                    if (this.maxInternalLine!=-1) this.maxInternalLine++;
                    if (this.downInternalLine!=-1) this.downInternalLine++;
                    if (this.downLine!=-1) this.downLine++;
                }
            }

            if (column==-1)
                column = this.getUpOrDownFreeBlockColumn(this.upLine,null);

            return column;
        };

        Matrix.prototype.getDownColumn = function() {
            var column = -1;
            if (this.downLine==-1) {
                if (this.nbLines == 0 && this.nbColumns == 0) {
                    this.downLine=0;
                    column=++this.mtxColumnsSplitter[this.minInternalLefTudC];
                    this.mtxColumnsSplitter[this.maxInternalLefTudC]++;
                    this.addColumnToMtx(column,FREE);
                    this.addLineToMtx(this.downLine);
                } else {
                    this.downLine = this.nbLines;
                    this.addLineToMtx(this.downLine);
                }
            }

            if (column == -1)
                column = this.getUpOrDownFreeBlockColumn(this.downLine,null);

            return column;
        };

        Matrix.prototype.addNewLeftFUpDownColumn = function(minLeft, maxLeft) {
            var column = -1, i, ii;
            column=this.mtxColumnsSplitter[minLeft];
            for (i = minLeft+1, ii = this.mtxColumnsSplitter.length; i<ii; i++)
                if (this.mtxColumnsSplitter[i]!=-1) this.mtxColumnsSplitter[i]++;
            this.addColumnToMtx(column,LOCKED);
            return column;
        };

        Matrix.prototype.addNewRightFUpDownColumn = function(minRight, maxRight) {
            var column = -1, i, ii;
            column=++this.mtxColumnsSplitter[maxRight];
            for (i = maxRight+1, ii = this.mtxColumnsSplitter.length; i<ii; i++)
                if (this.mtxColumnsSplitter[i]!=-1) this.mtxColumnsSplitter[i]++;
            this.addColumnToMtx(column,LOCKED);
            return column;
        };

        Matrix.prototype.getNewFUpDownColumn = function(minLeft,maxLeft,minRight,maxRight,boolLeftRight) {
            var column = -1;
            if (this.mtxColumnsSplitter[minLeft]==-1 && this.mtxColumnsSplitter[maxLeft]==-1) {
                if (this.nbColumns==0 && this.nbLines==0) {
                    column = ++this.mtxColumnsSplitter[minLeft];
                    this.mtxColumnsSplitter[maxLeft]++;
                    this.addColumnToMtx(column,LOCKED);
                    this.addLineToMtx(column);
                } else {
                    column=this.getColumnFromInitializedArea(minLeft,maxLeft);
                }
            } else if (this.mtxColumnsSplitter[minRight]==-1 && this.mtxColumnsSplitter[maxRight]==-1) {
                if (this.nbColumns==0 && this.nbLines==0) {
                    column=++this.mtxColumnsSplitter[minRight];
                    this.mtxColumnsSplitter[maxRight]++;
                    this.addColumnToMtx(column,LOCKED);
                } else
                    column=this.getColumnFromInitializedArea(minRight,maxRight);
            } else {
                if (!boolLeftRight)
                    column=this.addNewRightFUpDownColumn(minRight, maxRight);
                else
                    column=this.addNewLeftFUpDownColumn(minLeft, maxLeft);
            }
            return column;
        };

        Matrix.prototype.getNewUpDownColumn = function() {
            var column = this.getNewFUpDownColumn(this.minLeftUDC,this.maxLeftUDC,this.minRightUDC,
                this.maxRightUDC,this.pushUDonLeft);
            this.pushUDonLeft=!this.pushUDonLeft;
            return column;
        };

        Matrix.prototype.getNewInternalUpDownColumnIdx = function() {
            var column = this.getNewFUpDownColumn(this.minInternalLeftUDC,this.maxInternalLeftUDC,
                this.minInternalRightUDC,this.maxInternalRightUDC,this.pushInternalUDonLeft);
            this.pushInternalUDonLeft=!this.pushInternalUDonLeft;
            return column;
        };

        Matrix.prototype.getInternalCoord = function() {
            var column2ret,
                line2ret  ;

            //FIRST : TRY TO GET FREE COORDS IN THE DOWN INTERNAL LINE
            this.initDownInternalLineWithZone(this.minInternalC,this.maxInternalC);
            line2ret   = this.downInternalLine;
            column2ret = this.getFreeBlockColumn(this.downInternalLine,this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);

            //SECOND : TRY TO GET FREE COORDS IN THE INTERNAL LEFT COLUMNS AND THEN TRY IN INTERNAL RIGHT COLUMNS
            if (this.mtxColumnsSplitter[this.minInternalLeftC]!=-1 && this.mtxColumnsSplitter[this.maxInternalLeftC]!=-1 && column2ret==-1) {
                for (var i = this.maxInternalLine, ii = this.minInternalLine; i>=ii; i--) {
                    column2ret=this.getFreeBlockColumn(i,this.mtxColumnsSplitter[this.minInternalLeftC],this.mtxColumnsSplitter[this.maxInternalLeftC]);
                    if (column2ret!=-1) {
                        line2ret = i;
                        break;
                    }
                }
            } else {
                if (column2ret==-1) {
                    //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                    column2ret=this.getColumnFromInitializedArea(this.minInternalLeftC,this.maxInternalLeftC);
                    line2ret=this.downInternalLine;
                }
            }

            if (this.mtxColumnsSplitter[this.minInternalRightC]!=-1 && this.mtxColumnsSplitter[this.maxInternalRightC]!=-1 && column2ret==-1) {
                for (i = this.maxInternalLine, ii = this.minInternalLine; i>=ii; i--) {
                    column2ret=this.getFreeBlockColumn(i,this.mtxColumnsSplitter[this.minInternalRightC],this.mtxColumnsSplitter[this.maxInternalRightC]);
                    if (column2ret!=-1) {
                        line2ret = i;
                        break;
                    }
                }
            } else {
                if (column2ret==-1) {
                    //ELSE IF THIS AREA IS NOT INITIALIZED INITIALIZE IT
                    column2ret=this.getColumnFromInitializedArea(this.minInternalRightC,this.maxInternalRightC);
                    line2ret=this.downInternalLine;
                }
            }

            //THIRD : TRY TO GET FREE COORDS IN THE UP INTERNAL LINE
            if (column2ret==-1) {
                this.initDownInternalLineWithZone(this.minInternalC,this.maxInternalC);
                line2ret   = this.upInternalLine;
                column2ret = this.getFreeBlockColumn(this.upInternalLine,this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);
            }

            //FOURTH : TRY TO GET FREE COORDS IN THE INTERNAL LINE
            if (column2ret==-1) {
                this.initInternalLineWithZone(this.minInternalC,this.maxInternalC);
                line2ret   = this.minInternalLine;
                column2ret = this.getFreeBlockColumn(this.minInternalLine,this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);
            }

            //FIFTH : ADD A NEW INTERNAL COLUMN AND RETURN COORDS(downInternalLine,maxInternalC)
            if (column2ret==-1) {
                for (i = this.maxInternalC, ii = this.mtxColumnsSplitter.length; i<ii; i++)
                    if (this.mtxColumnsSplitter[i]!=-1) this.mtxColumnsSplitter[i]++;
                column2ret = this.mtxColumnsSplitter[this.maxInternalC];
                this.addColumnToMtx(column2ret,FREE);
                line2ret=this.downInternalLine;
            }

            return {
                column: column2ret,
                line  : line2ret
            }
        };




        /*
         * "Public" methods
         */

        Matrix.prototype.getMtxSize = function() {
            return {
                x: this.nbLines,
                y: this.nbColumns
            };
        };

        Matrix.prototype.getMtxContentSize = function () {
            return {
                width  : this.contentWidth,
                height : this.contentHeight
            };
        };

        Matrix.prototype.getObjectFromMtx = function (x,y) {
            var block = this.zemtx[y][x];
            if (block!=null && block!==FREE && block!==LOCKED)
                return block.obj;
            else
                return null;
        };

        Matrix.prototype.toFront = function() {
            var i, ii, j, jj, block;
            for (i = 0, ii = this.nbLines; i < ii; i++)
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==FREE && block!==LOCKED) block.obj.toFront();
                }
        };

        Matrix.prototype.getMtxObjCount = function() {
            return this.objectsList.length;
        };

        Matrix.prototype.addObject = function(object) {
            var upColumn, downColumn, newInternalUDC, newInternalCoord, newUDC;

            newInternalCoord = this.getInternalCoord(object);
            this.zemtx[newInternalCoord.column][newInternalCoord.line] = {obj: object, type: object.type};
            object.layoutData.mtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column};

            this.objectsList.push(object);

            /*
            if (object.layoutData.isConnectedInsideMtx) {
                if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx) {
                    newInternalUDC = this.getNewInternalUpDownColumnIdx();
                    this.zemtx[newInternalUDC][0] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:0, y:newInternalUDC}
                } else if (object.layoutData.isConnectedOutsideToUpMtx) {
                    upColumn = this.getInternalUpColumnIdx();
                    this.zemtx[upColumn][this.upInternalLine] = {obj: object, type: object.type};
                    this.zemtx[upColumn][this.upLine]=LOCKED;
                    object.layoutData.mtxCoord = {x:this.upLine, y:upColumn}
                } else if (object.layoutData.isConnectedOutsideToDownMtx) {
                    downColumn = this.getInternalDownColumnIdx();
                    this.zemtx[downColumn][this.downInternalLine] = {obj: object, type: object.type};
                    this.zemtx[downColumn][this.downLine]=LOCKED;
                    object.layoutData.mtxCoord = {x:this.downLine, y:downColumn}
                } else {
                    newInternalCoord = this.getInternalCoord(object);
                    this.zemtx[newInternalCoord.column][newInternalCoord.line] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column}
                }
            } else {
                if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx) {
                    newUDC = this.getNewUpDownColumn();
                    this.zemtx[newUDC][0] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:0, y:newUDC}
                } else if (object.layoutData.isConnectedOutsideToUpMtx) {
                    upColumn = this.getUpColumn();
                    this.zemtx[upColumn][this.upLine] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x: this.upLine, y:upColumn}
                } else if (object.layoutData.isConnectedOutsideToDownMtx) {
                    downColumn = this.getDownColumn();
                    this.zemtx[downColumn][this.downLine] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x: this.downLine, y:downColumn}
                } else  {
                    newInternalCoord = this.getInternalCoord(object);
                    this.zemtx[newInternalCoord.column][newInternalCoord.line] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column}
                }
            }

            //TODO callback
            //ADD CURRENT CONTAINER TO LAN CONTAINER CONNECTED LIST
            /*
             var i, ii;
             var linkedContainers = object.getLinkedContainers();
            for (i=0, ii=linkedContainers.length; i << ii; i++)
                if (object.localisation.equal(linkedContainers[i]))
                    this.addConnectedLanContainerToContainer(linkedContainers[i], object)
            */
        };

        // To be used on first placement pass

        Matrix.prototype.defineMtxObjectFirstPoz = function(topLeftX, topLeftY, mtxSpan, objSpan, objDefinePozCallback) {
            var heightPointer = topLeftY, widthPointer, maxLineHeight;
            var i, ii, j, jj, block;
            for (i = 0, ii = this.nbLines; i < ii; i++) {
                widthPointer  = topLeftX; maxLineHeight=0;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        objDefinePozCallback(block.obj, mtxSpan, objSpan, j, i, widthPointer, heightPointer);
                        //TODO : to be replaced by callback
                        //block.obj.setTopLeftCoord(mtxSpan + objSpan*j + widthPointer, mtxSpan + objSpan*i + heightPointer);
                        //block.obj.definedNodesPoz();
                        widthPointer += block.obj.getMaxRectSize().width;
                        if (block.obj.getMaxRectSize().height>maxLineHeight)
                            maxLineHeight = block.obj.getMaxRectSize().height;
                    }
                }
                heightPointer += maxLineHeight;
            }
        };

        Matrix.prototype.defineMtxContentMaxSize = function() {
            var tmpHeight, tmpWidth, block;
            var i, ii, j, jj;
            for (i = 0, ii = this.nbColumns; i < ii ; i++) {
                tmpHeight = 0;
                for (j = 0, jj = this.nbLines; j < jj; j++) {
                    block = this.zemtx[i][j];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        if (block.obj.getMaxRectSize().height==0)
                            block.obj.defineMaxSize();
                        tmpHeight += block.obj.getMaxRectSize().height;

                    }

                }
                if (tmpHeight > this.contentHeight)
                    this.contentHeight=tmpHeight;
            }
            for (i = 0, ii = this.nbLines; i < ii ; i++) {
                tmpWidth = 0;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==FREE && block!==LOCKED)
                        tmpWidth += block.obj.getMaxRectSize().width;
                }
                if (tmpWidth > this.contentWidth)
                    this.contentWidth = tmpWidth;
            }
        };

        Matrix.prototype.defineMtxContentSize = function() {
            var block;
            var maxLineHeight = [], maxColumnWidth = [];
            var i, ii, j, jj;
            this.contentHeight = 0 ;
            this.contentWidth  = 0 ;

            for (i = 0, ii = this.nbColumns; i < ii ; i++) {
                for (j = 0, jj = this.nbLines; j < jj; j++) {
                    block = this.zemtx[i][j];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        block.obj.defineSize();
                        if (maxColumnWidth[i]==null || maxColumnWidth[i] < block.obj.getRectSize().width)
                            maxColumnWidth[i] = block.obj.getRectSize().width;
                    } else if (maxColumnWidth[i]==null)
                        maxColumnWidth[i] = 0;
                }
                this.contentWidth+=maxColumnWidth[i];
            }

            for (i = 0, ii = this.nbLines; i < ii; i++) {
                for (j=0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        if (maxLineHeight[i]==null || (maxLineHeight[i]!=null && maxLineHeight[i] < block.obj.getRectSize().height))
                            maxLineHeight[i] = block.obj.getRectSize().height;
                    } else if (maxLineHeight[i]==null)
                        maxLineHeight[i]=0;
                }
                this.contentHeight+=maxLineHeight[i];
            }
        };

        Matrix.prototype.cleanMtx = function() {
            var i, ii, j, jj;
            var linesIdxToBeRemoved = [], columnsIdxToBeRemoved = [];
            for (i=0, ii=this.nbLines; i < ii; i++) {
                var lineToBeRemoved = true;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    if (this.zemtx[j][i] != null && this.zemtx[j][i] !== FREE && this.zemtx[j][i] !== LOCKED) {
                        lineToBeRemoved = false;
                        break;
                    }
                }
                if (lineToBeRemoved)
                    linesIdxToBeRemoved.push(i)
            }
            for (i=0, ii=linesIdxToBeRemoved.length; i<ii; i++)
                this.removeLineFromMtx(linesIdxToBeRemoved[i]-i);

            for (i=0, ii=this.nbColumns; i < ii; i++) {
                var columnToBeRemoved = true;
                for (j=0, jj=this.nbLines; j < jj; j++) {
                    if (this.zemtx[i][j]!=null && this.zemtx[i][j]!==FREE && this.zemtx[i][j]!==LOCKED) {
                        columnToBeRemoved = false;
                        break;
                    }
                }
                if (columnToBeRemoved)
                    columnsIdxToBeRemoved.push(i)
            }
            for (i=0, ii=columnsIdxToBeRemoved.length; i<ii; i++)
                this.removeColumnFromMtx(columnsIdxToBeRemoved[i]-i);
        };

        // To be used on second placement pass

        Matrix.prototype.defineMtxObjectLastPoz = function(topLeftX, topLeftY, mtxSpan, objSpan, mtxWidth, mtxHeight) {
            var i, ii, j, jj, block;
            var maxColumnWidth = [];

            for (i=0, ii=this.nbColumns; i < ii; i++) {
                for (j=0, jj=this.nbLines; j < jj; j++) {
                    block = this.zemtx[i][j];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        //TODO : to be replaced by callback
                        var blockWidth =  block.obj.getRectSize().width;
                        if (maxColumnWidth[i]==null || maxColumnWidth[i] < blockWidth)
                            maxColumnWidth[i] = blockWidth;
                    } else if (maxColumnWidth[i]==null)
                        maxColumnWidth[i] = 0;
                }
            }

            var heightPointer = topLeftY, widthPointer, maxLineHeight;

            for (i = 0, ii = this.nbLines; i < ii; i++) {
                widthPointer  = topLeftX; maxLineHeight=0;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==FREE && block!==LOCKED) {
                        //TODO : to be replaced by callback
                        block.obj.setTopLeftCoord(mtxSpan + objSpan*j + widthPointer , mtxSpan + objSpan*i + heightPointer);
                        block.obj.setMoveJail(topLeftX, topLeftY+mtxSpan, topLeftX+mtxWidth, topLeftY+mtxHeight);
                        block.obj.definedNodesPoz();
                        widthPointer += block.obj.getRectSize().width;
                        if (block.obj.getRectSize().height > maxLineHeight)
                            maxLineHeight = block.obj.getRectSize().height;
                    } else
                        widthPointer += maxColumnWidth[j];
                }
                heightPointer += maxLineHeight;
            }
        };

        return Matrix;
    });