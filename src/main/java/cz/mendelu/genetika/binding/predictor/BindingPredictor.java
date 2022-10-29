package cz.mendelu.genetika.binding.predictor;

import java.util.stream.Stream;

import cz.mendelu.genetika.genoms.Sequence;

public interface BindingPredictor {
	
	public Stream<BindingPrediction> find(Sequence genom);

}
