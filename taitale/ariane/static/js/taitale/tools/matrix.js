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

        function Matrix() {
            this.helper = new helper_();

            this.nbLines   = 0;
            this.nbColumns = 0;
            this.zemtx     = [];

            this.contentWidth   = 0;
            this.contentHeight  = 0;

            // LINES SPLITTER TABLE
            this.mtxLinesSplitter    = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
            this.upLineMin           = 0; // UP LINK ONLY
            this.upLineMax           = 1;
            this.upInternalLineMin   = 2; // UP & INTERNAL
            this.upInternalLineMax   = 3;
            this.minInternalLine     = 4;
            this.maxInternalLine     = 5;
            this.downInternalLineMin = 6; // DOWN & INTERNAL
            this.downInternalLineMax = 7;
            this.downLineMin         = 8; // DOWN LINK ONLY
            this.downLineMax         = 9;

            // PUSH MIN/MAX BALANCER
            this.pushInternalLineMin      = false;

            // COLUMNS SPLITTER TABLE
            this.mtxColumnsSplitter  = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1];
            // COLUMNS SPLITTER TABLE IDX
            this.minLeftUDC          = 0;  // OBJS WITH UP &| DOWN &| LEFT LINKS
            this.maxLeftUDC          = 1;
            this.minInternalLeftUDC  = 2;  // OBJS WITH UP &| DOWN &| LEFT & INTERNAL LINKS
            this.maxInternalLeftUDC  = 3;
            this.minInternalLefTudC  = 4;  // OBJS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN &| LEFT & INTERNAL LINKS
            this.maxInternalLefTudC  = 5;
            this.minInternalLeftC    = 6;  // OBJS WITH INTERNAL LINKS ON LEFT
            this.maxInternalLeftC    = 7;
            this.minInternalC        = 8;  // INTERNAL OBJECT WITH INTERNAL LINKS ONLY
            this.maxInternalC        = 9;
            this.minInternalRightC   = 10; // OBJS WITH INTERNAL LINKS ON RIGHT
            this.maxInternalRightC   = 11;
            this.minInternalRighTudC = 12; // OBJS WITH UP OR DOWN LINKS OR LANS WITH UP OR DOWN & INTERNAL LINKS ON RIGHT
            this.maxInternalRighTudC = 13;
            this.minInternalRightUDC = 14; // OBJS WITH UP & DOWN & INTERNAL LINKS ON RIGHT
            this.maxInternalRightUDC = 15;
            this.minRightUDC         = 16; // OBJS WITH UP & DOWN LINKS ON RIGHT
            this.maxRightUDC         = 17;

            // PUSH LEFT/RIGHT BALANCER
            //noinspection JSUnusedLocalSymbols
            this.pushUDonLeft         = false;
            this.pushInternalUDonLeft = false;
            this.pushInternaLudOnLeft = false;
            this.pushInternalOnLeft   = false;

            this.objectsList   = [];
            this.objectLinkedToOutsideOnly      = [];
            this.objectLinkedToInsideOnly       = [];
            this.objectLinkedToInsideAndOutside = [];

            this.initMatrix();
        }

        Matrix.prototype.FREE   = "FREE";
        Matrix.prototype.LOCKED = "LOCKED";

        Matrix.prototype.initMatrix = function() {
            var i, ii;

            //INIT COLUMNS
            for (i=this.minLeftUDC, ii=this.minRightUDC; i<=ii; i+=2) {
                this.mtxColumnsSplitter[i]   = this.nbColumns;
                this.mtxColumnsSplitter[i+1] = this.nbColumns;
                this.addColumnToMtx(this.nbColumns,this.FREE);
            }

            //INIT LINES
            for(i=this.upLineMin, ii=this.downLineMin; i<=ii; i+=2) {
                this.mtxLinesSplitter[i]   = this.nbLines;
                this.mtxLinesSplitter[i+1] = this.nbLines;
                this.addLineToMtx(this.nbLines)
            }
        };

        // Low level matrix functions
        // --------------------------

        Matrix.prototype.addLineToMtx = function(index) {
            var i,ii;
            if (index < this.nbLines) {
                for (i = 0, ii = this.nbColumns; i < ii; i++) {
                    for (var j = index, jj = this.nbLines; j < jj; jj--) {
                        this.zemtx[i][jj] = this.zemtx[i][jj-1];
                        if (this.zemtx[i][jj]!==this.FREE && this.zemtx[i][jj]!==this.LOCKED && this.zemtx[i][jj]!=null)
                            this.zemtx[i][jj].obj.layoutData.mtxCoord= {x: jj, y: i};
                    }
                }
            }
            for (i = 0, ii = this.nbColumns; i < ii ; i++) {
                if ((i>=this.mtxColumnsSplitter[this.minLeftUDC] && i<=this.mtxColumnsSplitter[this.maxLeftUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minInternalLeftUDC] && i<=this.mtxColumnsSplitter[this.maxInternalLeftUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minInternalRightUDC] && i<=this.mtxColumnsSplitter[this.maxInternalRightUDC]) ||
                    (i>=this.mtxColumnsSplitter[this.minRightUDC] && i<=this.mtxColumnsSplitter[this.maxRightUDC]))
                    this.zemtx[i][index] = this.LOCKED;
                else
                    this.zemtx[i][index] = this.FREE;
            }

            this.nbLines++;
        };

        Matrix.prototype.removeLineFromMtx = function(index) {
            var i, ii, j, jj;
            for (i=0, ii=this.nbColumns; i < ii; i++) {
                for (j=index, jj = this.nbLines; j < jj; j++) {
                    this.zemtx[i][j] = this.zemtx[i][j+1];
                    if (this.zemtx[i][j]!==this.FREE && this.zemtx[i][j]!==this.LOCKED && this.zemtx[i][j]!=null)
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
                        if (this.zemtx[ii][j]!==this.FREE && this.zemtx[ii][j]!==this.LOCKED && this.zemtx[ii][j]!=null)
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
            for (i=index, ii=this.nbColumns-1; i < ii; i++) {
                this.zemtx[i] = this.zemtx[i+1];
                for (j=0, jj=this.nbLines; j<jj; j++) {
                    if (this.zemtx[i][j]!==this.FREE && this.zemtx[i][j]!==this.LOCKED && this.zemtx[i][j]!=null)
                        this.zemtx[i][j].obj.layoutData.mtxCoord= {x: j, y: i};
                }
            }
            this.zemtx.pop();
            this.nbColumns--;
        };

        Matrix.prototype.isBlockFree = function(lineIdx, columnIdx) {
            return (this.zemtx[columnIdx][lineIdx]===this.FREE);
        };

        Matrix.prototype.isColumnFreeFromMinToMax = function(columnIdx, minLine, maxLine) {
            var ret = true;
            for (var j = minLine, jj = maxLine; j <= jj; j++) {
                if (this.zemtx[columnIdx][j]!=this.FREE) {
                    ret = false;
                    break;
                }
            }
            return ret;
        };

        Matrix.prototype.isLineFreeFromMinToMax = function(lineIdx, minColumn, maxColumn) {
            var ret = true;
            for (var j = minColumn, jj = maxColumn; j <= jj; j++) {
                if (this.zemtx[j][lineIdx]!=this.FREE) {
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
                if (this.zemtx[i][lineIdx]===this.FREE) {
                    column=i;
                    break;
                }
            }
            return column;
        };

        Matrix.prototype.getFreeBlockLine = function(columnIdx,minL,maxL) {
            var line = -1;
            for (var i = minL, ii = maxL; i <= ii; i++) {
                if (this.zemtx[columnIdx][i]===this.FREE) {
                    line=i;
                    break;
                }
            }
            return line;
        };


        // Area level matrix basic functions
        // ----------------------------------

        Matrix.prototype.addUpMinLine = function() {
            var i, ii;
            this.addLineToMtx(this.mtxLinesSplitter[this.upLineMin]);
            for (i=this.upLineMax, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.upLineMin];
        };

        Matrix.prototype.addUpMaxLine = function() {
            var i, ii;
            this.addLineToMtx(++this.mtxLinesSplitter[this.upLineMax]);
            for (i=this.upInternalLineMin, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.upLineMax];
        };

        Matrix.prototype.addUpInternalLine = function() {
            var i, ii;
            this.addLineToMtx(this.mtxLinesSplitter[this.upInternalLineMin]);
            for (i=this.upInternalLineMax, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.upInternalLineMin];
        };

        Matrix.prototype.addInternalMinLine = function() {
            var i, ii;
            this.addLineToMtx(this.mtxLinesSplitter[this.minInternalLine]);
            for (i=this.maxInternalLine, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.minInternalLine];
        };

        Matrix.prototype.addInternalLine = function() {
            var line = -1;
            if (this.pushInternalLineMin) line = this.addInternalMinLine();
            else line = this.addInternalMaxLine();
            this.pushInternalLineMin=!this.pushInternalLineMin;
            return line;
        };

        Matrix.prototype.addInternalMaxLine = function() {
            var i, ii;
            this.addLineToMtx(++this.mtxLinesSplitter[this.maxInternalLine]);
            for (i=this.downInternalLineMin, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.maxInternalLine];
        };

        Matrix.prototype.addDownInternalLine = function() {
            var i, ii;
            this.addLineToMtx(++this.mtxLinesSplitter[this.downInternalLineMax]);
            for (i=this.downLineMin, ii=this.downLineMax; i<=ii; i++)
                this.mtxLinesSplitter[i]++;
            return this.mtxLinesSplitter[this.downInternalLineMax];
        };

        Matrix.prototype.addDownMinLine = function() {
            this.addLineToMtx(this.mtxLinesSplitter[this.downLineMin]);
            this.mtxLinesSplitter[this.downLineMax]++;
            return this.mtxLinesSplitter[this.downLineMin];
        };

        Matrix.prototype.addDownMaxLine = function() {
            this.addLineToMtx(++this.mtxLinesSplitter[this.downLineMax]);
            return this.mtxLinesSplitter[this.downLineMax];
        };


        Matrix.prototype.addLeftMinUDColumn = function() {
            var column = this.mtxColumnsSplitter[this.minLeftUDC], i, ii;
            this.addColumnToMtx(column,this.LOCKED);
            for(i=this.maxLeftUDC, ii=this.maxRightUDC; i <= ii; i++)
                this.mtxColumnsSplitter[i]++
            return column;
        };

        Matrix.prototype.addLeftMaxUDColumn = function() {
            var column = ++this.mtxColumnsSplitter[this.maxLeftUDC], i, ii;
            this.addColumnToMtx(column,this.LOCKED);
            for(i=this.minInternalLeftUDC, ii=this.maxRightUDC; i <= ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addMinLeftInternalUDColumn = function() {
            var column = this.mtxColumnsSplitter[this.minInternalLeftUDC], i, ii;
            this.addColumnToMtx(column, this.LOCKED);
            for (i=this.maxInternalLeftUDC, ii=this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addLeftInternalUDColumn = function() {
        };

        Matrix.prototype.addMaxLeftInternalUDColumn = function() {
            var column = ++this.mtxColumnsSplitter[this.maxInternalLeftUDC], i, ii;
            this.addColumnToMtx(column, this.LOCKED);
            for (i = this.minInternalLefTudC, ii = this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addMinLeftInternaLudColumn = function() {
            var column = this.mtxColumnsSplitter[this.minInternalLefTudC], i, ii;
            this.addColumnToMtx(column, this.LOCKED);
            for (i = this.maxInternalLefTudC, ii = this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addLeftInternaLudColumn = function() {

        };

        Matrix.prototype.addMaxLeftInternaLudColumn = function() {
            var column = ++this.mtxColumnsSplitter[this.maxInternalLefTudC], i, ii;
            this.addColumnToMtx(column, this.LOCKED);
            for (i = this.minInternalLeftC, ii = this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addLeftInternalColumn = function() {

        };

        Matrix.prototype.addMinInternalColumn = function() {
            var column = -1, i, ii;
            column = this.mtxColumnsSplitter[this.minInternalC];
            this.addColumnToMtx(column,this.FREE);
            for (i=this.maxInternalC, ii=this.maxRightUDC; i <= ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addInternalColumn = function() {
            var column = -1;
            if (this.pushInternalOnLeft) column = this.addMinInternalColumn();
            else column = this.addMaxInternalColumn();
            this.pushInternalOnLeft=!this.pushInternalOnLeft;
            return column;
        };

        Matrix.prototype.addMaxInternalColumn = function() {
            var column = -1, i, ii;
            column = ++this.mtxColumnsSplitter[this.maxInternalC];
            this.addColumnToMtx(column,this.FREE);
            for (i=this.minInternalRighTudC, ii=this.maxRightUDC; i <= ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addRightInternalColumn = function() {

        };

        Matrix.prototype.addMinRightInternaLudColumn = function() {
            var column = this.mtxColumnsSplitter[this.minInternalRighTudC], i, ii;
            this.addColumnToMtx(column, this.FREE);
            for (i=this.maxInternalRighTudC, ii=this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addRightInternaLudColumn = function() {

        };

        Matrix.prototype.addMaxRightInternaLudColumn = function() {
            var column = ++this.mtxColumnsSplitter[this.maxInternalRighTudC], i, ii;
            this.addColumnToMtx(column, this.FREE);
            for (i=this.minInternalRightUDC, ii=this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++
            return column;
        };

        Matrix.prototype.addMinRightInternalUDColumn = function() {
            var column = this.mtxColumnsSplitter[this.minInternalRightUDC], i, ii;
            this.addColumnToMtx(column,this.LOCKED);
            for (i=this.maxInternalRightUDC, ii=this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++;
            return column;
        };

        Matrix.prototype.addRightInternalUDColumn = function() {

        };

        Matrix.prototype.addMaxRightInternalUDColumn = function() {
            var column = ++this.mtxColumnsSplitter[this.maxInternalC], i, ii;
            this.addColumnToMtx(column, this.LOCKED);
            for (i=this.minRightUDC, ii=this.maxRightUDC; i < ii; i++)
                this.mtxColumnsSplitter[i]++
            return column;
        };

        Matrix.prototype.addRightMinUDColumn = function() {
            var column = this.mtxColumnsSplitter[this.minRightUDC];
            this.addColumnToMtx(column,this.LOCKED);
            this.mtxColumnsSplitter[this.maxRightUDC]++;
            return column;
        };

        Matrix.prototype.addRightMaxUDColumn = function() {
            var column = this.mtxColumnsSplitter[this.minRightUDC];
            this.addColumnToMtx(column,this.LOCKED);
            return column;
        };



        // Helper for advanced matrix coord algorithm choice - internal only
        // ------------------------------------------------------------------

        Matrix.prototype.getInternalBasicCoord = function() {
            var column2ret,
                line2ret  ;

            line2ret   = this.mtxLinesSplitter[this.minInternalLine];
            column2ret = this.getFreeBlockColumn(this.mtxLinesSplitter[this.minInternalLine],this.mtxColumnsSplitter[this.minInternalC],this.mtxColumnsSplitter[this.maxInternalC]);

            if (column2ret==-1)
                column2ret = this.addInternalColumn();

            return {
                column: column2ret,
                line  : line2ret
            }
        };




        // Helper for advanced matrix coord algorithm choice - external / internal
        // ------------------------------------------------------------------------

        Matrix.prototype.getExternalInternalUpDownCoord = function() {
            var column2ret = (this.pushInternalUDonLeft) ? this.addMaxLeftInternalUDColumn() : this.addMinRightInternalUDColumn();
            this.pushInternalUDonLeft=!this.pushInternalUDonLeft;
            return {
                column: column2ret,
                line: this.addInternalLine()
            };
        };

        Matrix.prototype.getExternalInternalUpCoord = function() {
            var column2ret = (this.pushInternaLudOnLeft) ? this.addMaxLeftInternaLudColumn() : this.addMinRightInternaLudColumn();
            this.pushInternaLudOnLeft=!this.pushInternaLudOnLeft;
            return {
                column: column2ret,
                line: this.addUpInternalLine()
            };
        };

        Matrix.prototype.getExternalInternalDownCoord = function() {
            var column2ret = (this.pushInternaLudOnLeft) ? this.addMaxLeftInternaLudColumn() : this.addMinRightInternaLudColumn();
            this.pushInternaLudOnLeft=!this.pushInternaLudOnLeft;
            return {
                column: column2ret,
                line: this.addDownInternalLine()
            };
        };

        Matrix.prototype.getExternalInternalRightUpDownCoord = function() {
            return {
                column: this.addMinRightInternalUDColumn(),
                line: this.addInternalLine()
            };
        };

        Matrix.prototype.getExternalInternalRightUpCoord = function() {
            return {
                column: this.addMinRightInternaLudColumn(),
                line: this.addUpInternalLine()
            };
        };

        Matrix.prototype.getExternalInternalRightDownCoord = function() {
            return {
                column: this.addMinRightInternaLudColumn(),
                line: this.addDownInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalRightCoord = function() {
            return {
                column: this.addMaxRightInternaLudColumn(),
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftUpDownCoord = function() {
            return {
                column: this.addMaxLeftInternalUDColumn(),
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftUpCoord = function() {
            return {
                column: this.addMaxLeftInternaLudColumn(),
                line: this.addUpInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftDownCoord = function() {
            return {
                column: this.addMaxLeftInternaLudColumn(),
                line: this.addDownInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftCoord = function() {
            return {
                column: this.addMinLeftInternaLudColumn(),
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftRightUpDownCoord = function() {
            var column2ret = (this.pushInternalUDonLeft) ? this.addMaxLeftInternalUDColumn() : this.addMinRightInternalUDColumn();
            this.pushInternalUDonLeft=!this.pushInternalUDonLeft;
            return {
                column: column2ret,
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftRightUpCoord = function() {
            var column2ret = (this.pushInternalUDonLeft) ? this.addMaxLeftInternaLudColumn() : this.addMinRightInternaLudColumn();
            this.pushInternalUDonLeft=!this.pushInternalUDonLeft;
            return {
                column: column2ret,
                line: this.addUpInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftRightDownCoord = function() {
            var column2ret = (this.pushInternalUDonLeft) ? this.addMaxLeftInternaLudColumn() : this.addMinRightInternaLudColumn();
            this.pushInternalUDonLeft=!this.pushInternalUDonLeft;
            return {
                column: column2ret,
                line: this.addDownInternalLine()
            }
        };

        Matrix.prototype.getExternalInternalLeftRightCoord = function() {
            return this.getExternalInternalLeftRightUpDownCoord()
        };



        // Helper for advanced matrix coord algorithm choice - external only
        // ------------------------------------------------------------------

        Matrix.prototype.getExternalRightUpDownCoord = function() {
            return {
                column: this.addRightMaxUDColumn(),
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalRightDownCoord = function() {
            return {
                column: this.addRightMinUDColumn(),
                line: this.addDownMinLine()
            };
        };

        Matrix.prototype.getExternalRightUpCoord = function() {
            return {
                column: this.addRightMinUDColumn(),
                line: this.addUpMaxLine()
            };
        };

        Matrix.prototype.getExternalRightCoord = function() {
            return {
                column: this.mtxColumnsSplitter[this.maxRightUDC],
                line: this.addInternalLine()
            };
        };

        Matrix.prototype.getExternalLeftUpDownCoord = function() {
            return {
                column: this.addLeftMinUDColumn(),
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalLeftDownCoord = function() {
            return {
                column: this.addLeftMaxUDColumn(),
                line: this.addDownMinLine()
            }
        };

        Matrix.prototype.getExternalLeftUpCoord = function() {
            return {
                column: this.addLeftMaxUDColumn(),
                line: this.addUpMaxLine()
            };
        };

        Matrix.prototype.getExternalLeftCoord = function() {
            return {
                column: this.mtxColumnsSplitter[this.maxLeftUDC],
                line: this.addInternalLine()
            };
        };

        Matrix.prototype.getExternalUpDownCoord = function() {
            var column2ret = (this.pushUDonLeft) ? this.addLeftMaxUDColumn() : this.addRightMinUDColumn();
            this.pushUDonLeft=!this.pushUDonLeft;
            return {
                column: column2ret,
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalUpCoord = function() {
            return {
                column: this.addInternalColumn(),
                line: this.mtxLinesSplitter[this.upLineMin]
            }
        };

        Matrix.prototype.getExternalDownCoord = function() {
            return {
                column: this.addInternalColumn(),
                line: this.mtxLinesSplitter[this.downLineMax]
            }
        };

        Matrix.prototype.getExternalLeftRightCoord = function() {
            var column2ret = (this.pushUDonLeft) ? this.addLeftMaxUDColumn() : this.addRightMinUDColumn();
            this.pushUDonLeft=!this.pushUDonLeft;
            return {
                column: column2ret,
                line: this.addInternalLine()
            }
        };

        Matrix.prototype.getExternalLeftRightUpCoord = function() {
            var column2ret = (this.pushUDonLeft) ? this.addLeftMaxUDColumn() : this.addRightMinUDColumn();
            this.pushUDonLeft=!this.pushUDonLeft;
            return {
                column: column2ret,
                line: this.addUpMaxLine()
            }
        };

        Matrix.prototype.getExternalLeftRightDownCoord = function() {
            var column2ret = (this.pushUDonLeft) ? this.addLeftMaxUDColumn() : this.addRightMinUDColumn();
            this.pushUDonLeft=!this.pushUDonLeft;
            return {
                column: column2ret,
                line: this.addDownMinLine()
            }
        };

        Matrix.prototype.getExternalLeftRightUpDownCoord = function() {
            return this.getExternalLeftRightCoord();
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
            if (block!=null && block!==this.FREE && block!==this.LOCKED)
                return block.obj;
            else
                return null;
        };

        Matrix.prototype.toFront = function() {
            var i, ii, j, jj, block;
            for (i = 0, ii = this.nbLines; i < ii; i++)
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) block.obj.toFront();
                }
        };

        Matrix.prototype.getMtxObjCount = function() {
            return this.objectsList.length;
        };

        Matrix.prototype.addObject = function(object) {
            var newInternalCoord = this.getInternalBasicCoord(object);
            this.zemtx[newInternalCoord.column][newInternalCoord.line] = {obj: object, type: object.type};
            object.layoutData.mtxCoord = {x:newInternalCoord.line, y:newInternalCoord.column};

            this.objectsList.push(object);
        };

        // To be used on intermediate placements pass
        Matrix.prototype.defineMtxObjectIntermediatePoz = function(topLeftX, topLeftY, mtxSpan, objSpan, objDefinePozCallback) {
            var heightPointer = topLeftY, widthPointer, maxLineHeight;
            var i, ii, j, jj, block;
            for (i = 0, ii = this.nbLines; i < ii; i++) {
                widthPointer  = topLeftX; maxLineHeight=0;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
                        objDefinePozCallback(block.obj, mtxSpan, objSpan, j, i, widthPointer, heightPointer);
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
                for (j = 0, jj = this.nbLines; j < jj; j++) {
                    block = this.zemtx[i][j];
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
                        if (block.obj.getMaxRectSize().width==0)
                            block.obj.defineMaxSize();
                        this.contentHeight += block.obj.getMaxRectSize().height;
                        this.contentWidth += block.obj.getMaxRectSize().width;

                    }
                }
            }
            //this.helper.debug("[matrix.defineMtxContentMaxSize] {contentHeight:"+this.contentHeight+", contentWidth:"+this.contentWidth+"}");
        };

        // Optimisation

        Matrix.prototype.updateLayoutData = function(arraySortCallback) {
            var i, ii, j, jj, block;

            for (i = 0, ii = this.nbLines; i < ii; i++)
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block != null && block !== this.FREE && block !== this.LOCKED)
                        block.obj.updateLayoutData();
                }

            this.objectLinkedToOutsideOnly = [];
            this.objectLinkedToInsideAndOutside = [];
            this.objectLinkedToInsideOnly = [];

            if (this.objectsList.length > 1) {
                for (i = 0, ii = this.nbLines; i < ii; i++)
                    for (j = 0, jj = this.nbColumns; j < jj; j++) {
                        block = this.zemtx[j][i];
                        if (block!=null && block!==this.FREE && block!==this.LOCKED) {
                            if (block.obj.layoutData.isConnectedOutsideMtx && !block.obj.layoutData.isConnectedInsideMtx)
                                this.objectLinkedToOutsideOnly.push(block.obj);
                            else if (block.obj.layoutData.isConnectedInsideMtx && !block.obj.layoutData.isConnectedOutsideMtx)
                                this.objectLinkedToInsideOnly.push(block.obj);
                            else if (block.obj.layoutData.isConnectedOutsideMtx && block.obj.layoutData.isConnectedInsideMtx)
                                this.objectLinkedToInsideAndOutside.push(block.obj);
                        }
                    }

                if (arraySortCallback!=null) {
                    this.objectLinkedToOutsideOnly.sort(arraySortCallback);
                    this.objectLinkedToInsideAndOutside.sort(arraySortCallback);
                    this.objectLinkedToInsideOnly.sort(arraySortCallback);
                }
            }
        };

        Matrix.prototype.TAG_EXT_UP_DOWN = "TAG_EXT_UP_DOWN";
        Matrix.prototype.TAG_EXT_UP      = "TAG_EXT_UP";
        Matrix.prototype.TAG_EXT_DOWN    = "TAG_EXT_DOWN";

        Matrix.prototype.TAG_EXT_RIGHT_UP_DOWN = "TAG_EXT_RIGHT_UP_DOWN";
        Matrix.prototype.TAG_EXT_RIGHT_UP      = "TAG_EXT_RIGHT_UP";
        Matrix.prototype.TAG_EXT_RIGHT_DOWN    = "TAG_EXT_RIGHT_DOWN";
        Matrix.prototype.TAG_EXT_RIGHT         = "TAG_EXT_RIGHT";

        Matrix.prototype.TAG_EXT_LEFT_UP_DOWN = "TAG_EXT_LEFT_UP_DOWN";
        Matrix.prototype.TAG_EXT_LEFT_UP      = "TAG_EXT_LEFT_UP";
        Matrix.prototype.TAG_EXT_LEFT_DOWN    = "TAG_EXT_LEFT_DOWN";
        Matrix.prototype.TAG_EXT_LEFT         = "TAG_EXT_LEFT";

        Matrix.prototype.TAG_EXT_LEFT_RIGHT_UP_DOWN = "TAG_EXT_LEFT_RIGHT_UP_DOWN";
        Matrix.prototype.TAG_EXT_LEFT_RIGHT_UP      = "TAG_EXT_LEFT_RIGHT_UP";
        Matrix.prototype.TAG_EXT_LEFT_RIGHT_DOWN    = "TAG_EXT_LEFT_RIGHT_DOWN";
        Matrix.prototype.TAG_EXT_LEFT_RIGHT         = "TAG_EXT_LEFT_RIGHT";

        Matrix.prototype.position4ObjectLinkedToOutsideOnly = function() {
            var i, ii, object;

            for (i=0, ii=this.objectLinkedToOutsideOnly.length; i < ii; i++) {
                var newCoord, newColumn=-1, newLine=-1, newTag = null;

                object = this.objectLinkedToOutsideOnly[i];
                if (!object.layoutData.isConnectedOutsideToRightMtx && !object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_UP_DOWN) {
                        newCoord = this.getExternalUpDownCoord(); newTag   = this.TAG_EXT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_UP) {
                        newCoord = this.getExternalUpCoord(); newTag = this.TAG_EXT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_DOWN) {
                        newCoord = this.getExternalDownCoord(); newTag = this.TAG_EXT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (object.layoutData.isConnectedOutsideToRightMtx && !object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (!object.layoutData.isConnectedOutsideToUpMtx && !object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_RIGHT) {
                        newCoord = this.getExternalRightCoord(); newTag = this.TAG_EXT_RIGHT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_RIGHT_UP_DOWN) {
                        newCoord = this.getExternalRightUpDownCoord(); newTag = this.TAG_EXT_RIGHT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_RIGHT_UP) {
                        newCoord = this.getExternalRightUpCoord(); newTag = this.TAG_EXT_RIGHT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_RIGHT_DOWN) {
                        newCoord = this.getExternalRightDownCoord(); newTag = this.TAG_EXT_RIGHT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (!object.layoutData.isConnectedOutsideToRightMtx && object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (!object.layoutData.isConnectedOutsideToUpMtx && !object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT) {
                        newCoord = this.getExternalLeftCoord(); newTag = this.TAG_EXT_LEFT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_UP_DOWN) {
                        newCoord = this.getExternalLeftUpDownCoord(); newTag = this.TAG_EXT_LEFT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_UP) {
                        newCoord = this.getExternalLeftUpCoord(); newTag = this.TAG_EXT_LEFT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_DOWN) {
                        newCoord = this.getExternalLeftDownCoord(); newTag= this.TAG_EXT_LEFT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (object.layoutData.isConnectedOutsideToRightMtx && object.layoutData.isConnectedOutsideToRightMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_UP_DOWN) {
                        newCoord = this.getExternalLeftRightUpDownCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_UP) {
                        newCoord = this.getExternalLeftRightUpCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_DOWN) {
                        newCoord = this.getExternalLeftRightDownCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT) {
                        newCoord = this.getExternalLeftRightCoord(); newTag = this.TAG_EXT_LEFT_RIGHT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                }

                if (newColumn!=-1 && newLine!=-1) {
                    this.zemtx[object.layoutData.mtxCoord.y][object.layoutData.mtxCoord.x] = this.FREE;
                    this.zemtx[newColumn][newLine] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:newLine, y:newColumn};
                    object.layoutData.tag = newTag;
                }
            }
        };

        Matrix.prototype.TAG_EXT_INT_UP_DOWN = "TAG_EXT_INT_UP_DOWN";
        Matrix.prototype.TAG_EXT_INT_UP      = "TAG_EXT_INT_UP";
        Matrix.prototype.TAG_EXT_INT_DOWN    = "TAG_EXT_INT_DOWN";

        Matrix.prototype.TAG_EXT_INT_RIGHT_UP_DOWN = "TAG_EXT_INT_RIGHT_UP_DOWN";
        Matrix.prototype.TAG_EXT_INT_RIGHT_UP      = "TAG_EXT_INT_RIGHT_UP";
        Matrix.prototype.TAG_EXT_INT_RIGHT_DOWN    = "TAG_EXT_INT_RIGHT_DOWN";
        Matrix.prototype.TAG_EXT_INT_RIGHT         = "TAG_EXT_INT_RIGHT";

        Matrix.prototype.TAG_EXT_INT_LEFT_UP_DOWN = "TAG_EXT_INT_LEFT_UP_DOWN";
        Matrix.prototype.TAG_EXT_INT_LEFT_UP      = "TAG_EXT_INT_LEFT_UP";
        Matrix.prototype.TAG_EXT_INT_LEFT_DOWN    = "TAG_EXT_INT_LEFT_DOWN";
        Matrix.prototype.TAG_EXT_INT_LEFT         = "TAG_EXT_INT_LEFT";

        Matrix.prototype.TAG_EXT_INT_LEFT_RIGHT_UP_DOWN = "TAG_EXT_INT_LEFT_RIGHT_UP_DOWN";
        Matrix.prototype.TAG_EXT_INT_LEFT_RIGHT_UP      = "TAG_EXT_INT_LEFT_RIGHT_UP";
        Matrix.prototype.TAG_EXT_INT_LEFT_RIGHT_DOWN    = "TAG_EXT_INT_LEFT_RIGHT_DOWN";
        Matrix.prototype.TAG_EXT_INT_LEFT_RIGHT         = "TAG_EXT_INT_LEFT_RIGHT";

        Matrix.prototype.position4ObjectLinkedToInsideAndOutside = function() {
            var i, ii, object;
            for (i=0, ii=this.objectLinkedToInsideAndOutside.length; i < ii; i++) {
                var newCoord, newColumn = -1, newLine = -1, newTag = null;
                object = this.objectLinkedToInsideAndOutside[i];

                if (!object.layoutData.isConnectedOutsideToRightMtx && !object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_UP_DOWN) {
                        newCoord = this.getExternalInternalUpDownCoord(); newTag = this.TAG_EXT_INT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_UP) {
                        newCoord = this.getExternalInternalUpCoord(); newTag = this.TAG_EXT_INT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_DOWN) {
                        newCoord = this.getExternalInternalDownCoord(); newTag = this.TAG_EXT_INT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (object.layoutData.isConnectedOutsideToRightMtx && !object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_RIGHT_UP_DOWN) {
                        newCoord = this.getExternalInternalRightUpDownCoord(); newTag = this.TAG_EXT_INT_RIGHT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_RIGHT_UP) {
                        newCoord = this.getExternalInternalRightUpCoord(); newTag = this.TAG_EXT_INT_RIGHT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_RIGHT_UP_DOWN) {
                        newCoord = this.getExternalInternalRightDownCoord(); newTag = this.TAG_EXT_INT_RIGHT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.tag!=this.TAG_EXT_INT_RIGHT) {
                        newCoord = this.getExternalInternalRightCoord(); newTag = this.TAG_EXT_INT_RIGHT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (!object.layoutData.isConnectedOutsideToRightMtx && object.layoutData.isConnectedOutsideToLeftMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_LEFT_UP_DOWN) {
                        newCoord = this.getExternalInternalLeftUpDownCoord(); newTag = this.TAG_EXT_INT_LEFT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_LEFT_UP) {
                        newCoord = this.getExternalInternalLeftUpCoord(); newTag = this.TAG_EXT_INT_LEFT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_INT_LEFT_DOWN) {
                        newCoord = this.getExternalInternalLeftDownCoord(); newTag = this.TAG_EXT_INT_LEFT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.tag!=this.TAG_EXT_INT_LEFT) {
                        newCoord = this.getExternalInternalLeftCoord(); newTag = this.TAG_EXT_INT_LEFT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                } else if (object.layoutData.isConnectedOutsideToRightMtx && object.layoutData.isConnectedOutsideToRightMtx) {
                    if (object.layoutData.isConnectedOutsideToUpMtx && object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_UP_DOWN) {
                        newCoord = this.getExternalInternalLeftRightUpDownCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_UP_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column
                    } else if (object.layoutData.isConnectedOutsideToUpMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_UP) {
                        newCoord = this.getExternalInternalLeftRightUpCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_UP;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    } else if (object.layoutData.isConnectedOutsideToDownMtx &&
                        object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT_DOWN) {
                        newCoord = this.getExternalInternalLeftRightDownCoord(); newTag = this.TAG_EXT_LEFT_RIGHT_DOWN;
                        newLine = newCoord.line; newColumn = newCoord.column
                    } else if (object.layoutData.tag!=this.TAG_EXT_LEFT_RIGHT) {
                        newCoord = this.getExternalInternalLeftRightCoord(); newTag = this.TAG_EXT_INT_LEFT_RIGHT;
                        newLine = newCoord.line; newColumn = newCoord.column;
                    }
                }

                if (newColumn!=-1 && newLine!=-1) {
                    this.zemtx[object.layoutData.mtxCoord.y][object.layoutData.mtxCoord.x] = this.FREE;
                    this.zemtx[newColumn][newLine] = {obj: object, type: object.type};
                    object.layoutData.mtxCoord = {x:newLine, y:newColumn}
                    object.layoutData.tag = newTag;
                }
            }
        };

        Matrix.prototype.position4ObjectLinkedToInsideOnly = function() {
        };

        Matrix.prototype.updatePosition = function() {
            var i, ii, j, jj, block;
            for (i = 0, ii = this.nbLines; i < ii; i++)
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    block = this.zemtx[j][i];
                    if (block != null && block !== this.FREE && block !== this.LOCKED)
                        block.obj.updatePosition();
                }
            this.position4ObjectLinkedToOutsideOnly();
            this.position4ObjectLinkedToInsideAndOutside();
            this.position4ObjectLinkedToInsideOnly();
        };

        Matrix.prototype.cleanMtx = function() {
            var i, ii, j, jj;
            var linesIdxToBeRemoved = [], columnsIdxToBeRemoved = [];

            for (i=0, ii=this.nbLines; i < ii; i++)
                for (j = 0, jj = this.nbColumns; j < jj; j++)
                    if (this.zemtx[j][i] != null && this.zemtx[j][i] !== this.FREE && this.zemtx[j][i] !== this.LOCKED)
                        this.zemtx[j][i].obj.clean();

            for (i=0, ii=this.nbLines; i < ii; i++) {
                var lineToBeRemoved = true;
                for (j = 0, jj = this.nbColumns; j < jj; j++) {
                    if (this.zemtx[j][i] != null && this.zemtx[j][i] !== this.FREE && this.zemtx[j][i] !== this.LOCKED) {
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
                    if (this.zemtx[i][j]!=null && this.zemtx[i][j]!==this.FREE && this.zemtx[i][j]!==this.LOCKED) {
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

        // To be used on final placement pass
        // topLeftX, topLeftY, mtxSpan, objSpan, objDefinePozCallback
        Matrix.prototype.defineMtxObjectLastPoz = function(topLeftX, topLeftY, mtxSpan, objSpan, objDefinePozCallback) {
            var i, ii, j, jj, block;
            var maxColumnWidth = [];

            for (i=0, ii=this.nbColumns; i < ii; i++) {
                for (j=0, jj=this.nbLines; j < jj; j++) {
                    block = this.zemtx[i][j];
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
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
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
                        objDefinePozCallback(block.obj, mtxSpan, objSpan, j, i, widthPointer, heightPointer);
                        if (block.obj.getRectSize().height > maxLineHeight)
                            maxLineHeight = block.obj.getRectSize().height;
                    }
                    widthPointer += maxColumnWidth[j];
                }
                heightPointer += maxLineHeight;
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
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
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
                    if (block!=null && block!==this.FREE && block!==this.LOCKED) {
                        if (maxLineHeight[i]==null || (maxLineHeight[i]!=null && maxLineHeight[i] < block.obj.getRectSize().height))
                            maxLineHeight[i] = block.obj.getRectSize().height;
                    } else if (maxLineHeight[i]==null)
                        maxLineHeight[i]=0;
                }
                this.contentHeight+=maxLineHeight[i];
            }
            //this.helper.debug("[matrix.defineMtxContentSize] {contentHeight:"+this.contentHeight+", contentWidth:"+this.contentWidth+"}");
        };

        return Matrix;
    });