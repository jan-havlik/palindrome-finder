package cz.mendelu.genetika.binding.predictor;

import cz.mendelu.genetika.genoms.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class P53BindingPredictor implements BindingPredictor {
	
	private double threshold = 0.2;
	private double reference = -7.61;
	private Indexes indexes;
	
	private double[][] deltaLogKd = new double[][] {
	                            /*  G       G       A/G     C       A       T       G       C       C       C       G       G       G       C       A       T       G       T/A     C       C  */
        /*A*/    new double[] {   0.05,   0.03,   0.00,   0.59,   0.00,   0.16,   0.55,   0.25,   0.15,   0.12,   0.03,   0.10,   0.05,   0.62,   0.00,   0.21,   0.31,   0.15,   0.10,   0.07},
        /*T*/    new double[] {   0.07,   0.10,   0.15,   0.31,   0.21,   0.00,   0.62,   0.05,   0.10,   0.03,   0.12,   0.15,   0.25,   0.55,   0.16,   0.00,   0.59,   0.00,   0.03,   0.05},
        /*G*/    new double[] {   0.00,   0.00,   0.00,   0.55,   0.28,   0.35,   0.00,   0.47,   0.29,   0.13,   0.00,   0.00,   0.00,   0.61,   0.32,   0.47,   0.00,   0.40,   0.18,   0.11},
        /*C*/    new double[] {   0.11,   0.18,   0.40,   0.00,   0.47,   0.32,   0.61,   0.00,   0.00,   0.00,   0.13,   0.29,   0.47,   0.00,   0.35,   0.28,   0.55,   0.00,   0.00,   0.00}
	};
	
	public P53BindingPredictor() {
	}

	/**
	 * @param threshold prahova hodnota delta energie
	 */
	public P53BindingPredictor(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * vypocte jen rozdil proti idealni sekvenci
	 * @param sequence
	 * @return
     */
	private double calcDifference(String sequence) {
		double difference = 0;
		for(int i = 0; i < sequence.length(); i++) {
			String gene = sequence.substring(i, i + 1);
			if(indexes.isSequenceChar(gene)) {
				int row = indexes.valueOf(gene).getIndex();
				if (row != -1) {
					difference += this.deltaLogKd[row][i];
				} else {
					break;
				}
			} else {
				//je tam neznamy, takze radsi vynulovat
				return -reference;
			}
        }
		return difference;
	}

	/**
	 * vypocte hodnotu energie
	 *
	 * @param sequence
	 * @return
	 * @throws Exception
     */
	public double calcForSequence(String sequence) throws Exception {
		if(sequence.length() == 20) {
			return this.reference + calcDifference(sequence);
		} else {
			throw new Exception("Sequence length must be 20.");
		}
	}

	public double calcForSequence(Sequence sequence) throws Exception {
		return calcForSequence(sequence.toString());
	}

	/**
	 * vrati stream objektu popisujicich zajmova mista, jen pokud je ubytek energie do urcite hodnoty
	 *
	 * @param genom
	 * @return
	 */
	@Override
	public Stream<BindingPrediction> find(Sequence genom) {
		List<BindingPrediction> result = new ArrayList<>();
		for(int i = 0; i < genom.getLength() - 19; i++) {
			Sequence tmp = genom.getSequence(i, 20);
			double diff = calcDifference(tmp.toString());
			if(diff <= threshold) {
				BindingPrediction pred = new BindingPrediction(genom, i, 20, this.reference + diff);
				result.add(pred);
			}
		}
		return result.stream();
	}

	/**
	 * vrati jen energie v poli pro kazdou moznou pozici v sekvenci
	 *
	 * @param genom
	 * @return
	 */
	public double[] scan(Sequence genom) {
		double[] result = new double[genom.getLength() - 19];
		for(int i = 0; i < genom.getLength() - 19; i++) {
			Sequence tmp = genom.getSequence(i, 20);
			result[i] = this.reference + calcDifference(tmp.toString());
		}
		return result;
	}

	public static Stream<BindingPrediction> findSites(Sequence genom) {
		P53BindingPredictor pred = new P53BindingPredictor();
		return pred.find(genom);
	}

	public static Stream<BindingPrediction> findSites(Sequence genom, double threshold) {
		P53BindingPredictor pred = new P53BindingPredictor(threshold);
		return pred.find(genom);
	}

	public static double calc(String sequence) throws Exception {
		P53BindingPredictor pred = new P53BindingPredictor();
		return pred.calcForSequence(sequence);
	}

	public static double calc(Sequence sequence) throws Exception {
		P53BindingPredictor pred = new P53BindingPredictor();
		return pred.calcForSequence(sequence);
	}

}

enum Indexes {
	A(0),
	T(1),
	G(2),
	C(3);

	private int i;
	Indexes(int i) {
		this.i = i;
	}
	int getIndex() {
		return i;
	}

	static boolean isSequenceChar(String b) {
		return b.equals("A")
			|| b.equals("T")
			|| b.equals("C")
			|| b.equals("G");
	}
};