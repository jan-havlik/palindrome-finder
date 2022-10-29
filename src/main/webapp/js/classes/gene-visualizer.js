define("classes/gene-visualizer", function() {

    var GeneVisualizer = function() {

    };

    GeneVisualizer.prototype._arrayToStr = function(array) {
        var ret = "";
        for(var i = 0; i< array.length; i++) {
            for(var j = 0; j < array[i].length; j++) {
                if(array[i][j]) {
                    ret += array[i][j];
                } else {
                    ret += " ";
                }

            }
            ret += "\r\n";
        }
        return ret;
    };

    GeneVisualizer.prototype._isMakePear = function(a, b) {
        if (a == 'A' && b == 'T') return true;
        if (a == 'T' && b == 'A') return true;
        if (a == 'A' && b == 'U') return true;
        if (a == 'U' && b == 'A') return true;
        if (a == 'C' && b == 'G') return true;
        if (a == 'G' && b == 'C') return true;
        return false;
    };

    GeneVisualizer.prototype._printSpacer = function(row, col, charArr, str) {
        //console.log(str);
        if(str.length == 1) {
            charArr[row][col] = str[0];
        } else if(str.length == 2) {
            charArr[row - 1][col] = str[0];
            charArr[row + 1][col] = str[1];
        } else if(str.length == 3) {
            charArr[row - 1][col] = str[0];
            charArr[row][col] = str[1];
            charArr[row + 1][col] = str[2];
            return;
        } else if(str.length > 3) {
            charArr[row - 2][col] = str[0];
            charArr[row + 2][col] = str[str.length - 1];
            this._printSpacer(row, col + 1, charArr, str.substr(1, str.length - 2));
        }
    };

    GeneVisualizer.prototype._printString = function(charArr, row, col, str) {
        str = String(str);
        for(var i = 0; i < str.length; i++) {
            charArr[row][col + i] = str[i];
        }
    };

    GeneVisualizer.prototype.renderGene = function(geneInfo) {
        var maxPos = geneInfo.position + geneInfo.before.length + geneInfo.after.length + geneInfo.sequence.length + geneInfo.spacer.length;
        var positionsLength = String(maxPos).length;
        var neededCols = 1 + positionsLength + geneInfo.sequence.length + Math.ceil(geneInfo.spacer.length / 2);
        var neededRows = Math.max(geneInfo.before.length, 1) + Math.max(geneInfo.after.length, 1) + 3;

        //console.log("Needed rows x cols:", neededRows, "x", neededCols)

        //alokace pole znaku
        var charArr = new Array(neededRows);
        for(var i = 0; i < neededRows; i++) {
            charArr[i] = new Array(neededCols);
        }

        var startRowPos = positionsLength + 3;  //odsazeni zleve

        //vytisknout before
        for(var i = 0; i < geneInfo.before.length; i++) {
            this._printString(charArr, i, 0, (geneInfo.position - geneInfo.before.length + i + 1) + ":");
            charArr[i][startRowPos] = geneInfo.before[i];
        }

        //vytisknout sequence a opposite
        var r = Math.max(1, geneInfo.before.length);
        this._printString(charArr, r, 0, (geneInfo.position + 1) + ":");
        this._printString(charArr, r + 1, 0, "...");
        this._printString(charArr, r + 2, 0, (geneInfo.position + (geneInfo.sequence.length * 2) + geneInfo.spacer.length) + ":");
        for(var i = 0; i < geneInfo.sequence.length; i++) {
            var gene = geneInfo.sequence[i];
            var opposite = geneInfo.opposite[geneInfo.opposite.length - i - 1];
            charArr[r][startRowPos + i + 1] = gene;
            charArr[r + 1][startRowPos + i + 1] = this._isMakePear(gene, opposite) ? "|" : "-";
            charArr[r + 2][startRowPos + i + 1] = opposite;
        }

        //vytisknout after
        for(var i = 0; i < geneInfo.after.length; i++) {
            this._printString(charArr, r + 3 + i, 0, (geneInfo.position + (geneInfo.sequence.length * 2) + geneInfo.spacer.length + i + 1) + ":");
            charArr[r + 3 + i][startRowPos] = geneInfo.after[i];
        }

        this._printSpacer(r + 1, geneInfo.sequence.length + 2 + startRowPos, charArr, geneInfo.spacer);

        var result = this._arrayToStr(charArr);

        return result;
    };

    GeneVisualizer.prototype.formatGenes = function(genesInfo) {
        var self = this;
        return genesInfo.map(function(v) {
            v.visual = self.renderGene(v);
            return v;
        });
    };

    return GeneVisualizer;

});

