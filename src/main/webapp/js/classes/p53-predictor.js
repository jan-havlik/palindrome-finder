define("classes/p53-predictor", function() {

    var P53Predictor = function() {
        this._ref = -7.61;
        this._maxLoss = 7.22;   //maximanlni ztrata z tabulky
        this._seq = ['A', 'T', 'G', 'C'];
        this._lastLength = 0;
        this._lastSequence = "";
        this._deltaLogKd = [
                     /*  G       G       A/G     C       A       T       G       C       C       C       G       G       G       C       A       T       G       T/A     C       C  */
            /*A*/    [   0.05,   0.03,   0.00,   0.59,   0.00,   0.16,   0.55,   0.25,   0.15,   0.12,   0.03,   0.10,   0.05,   0.62,   0.00,   0.21,   0.31,   0.15,   0.10,   0.07],
            /*T*/    [   0.07,   0.10,   0.15,   0.31,   0.21,   0.00,   0.62,   0.05,   0.10,   0.03,   0.12,   0.15,   0.25,   0.55,   0.16,   0.00,   0.59,   0.00,   0.03,   0.05],
            /*G*/    [   0.00,   0.00,   0.00,   0.55,   0.28,   0.35,   0.00,   0.47,   0.29,   0.13,   0.00,   0.00,   0.00,   0.61,   0.32,   0.47,   0.00,   0.40,   0.18,   0.11],
            /*C*/    [   0.11,   0.18,   0.40,   0.00,   0.47,   0.32,   0.61,   0.00,   0.00,   0.00,   0.13,   0.29,   0.47,   0.00,   0.35,   0.28,   0.55,   0.00,   0.00,   0.00]
        ];
    };

    P53Predictor.prototype._prepare = function(sequence) {
        sequence = sequence.trim().toUpperCase();
        sequence = sequence.replace(/[^atgc]/gi, "");
        return sequence;
    };

    P53Predictor.prototype._calcDifference = function(sequence) {
        var difference = 0;
        for(var i = 0; i < sequence.length; i++) {
            var char = sequence[i];
            var row = this._seq.indexOf(char);
            if(row != -1) {
                difference += this._deltaLogKd[row][i];
            } else {
                break;
            }
        }
        return difference;
    };

    P53Predictor.prototype.getLength = function() {
        return this._lastLength;
    };

    P53Predictor.prototype.getSequence = function() {
        return this._lastSequence;
    };

    P53Predictor.prototype.calc = function(sequence) {
        if(sequence) {
            this._lastSequence = this._prepare(sequence);
            this._lastLength = this._lastSequence.length;
            if(this._lastLength == 20) {
                var diff = this._calcDifference(this._lastSequence);
                return {
                    difference: diff,
                    predictor: this._ref + diff,
                    relativeAffinity: -diff / this._maxLoss + 1
                };
            } else {
                throw "The sequence must have 20 valid characters.";
            }
        }
        return null;
    };

    P53Predictor.prototype.calcSequence = function(sequence) {
        var sequence = this._prepare(sequence);
        var results = [];
        if(sequence.length >= 20) {
            for(var i = 19; i < sequence.length; i++) {
                var subsequence = sequence.substr(i - 19, 20);
                var diff = this._calcDifference(subsequence);
                results.push({
                    sequence: subsequence,
                    position: i - 19,
                    difference: diff,
                    predictor: this._ref + diff,
                    relativeAffinity: -diff / this._maxLoss + 1
                });
            }
        } else {
            throw "Sequence must have at least 20 valid characters.";
        }

        console.log(results);

        return results;
    };

    return P53Predictor;

});