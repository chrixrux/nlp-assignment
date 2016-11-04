package crf;
import java.util.List;

public class APIAnnotationEvaluator {
	double truePositives = 0.0;
	double falsePositives = 0.0;
	
	public Evaluation performEvaluation(List<String> recognizedAPIs, List<String> manuallyAnnotatedAPIs) {
		countPositives(recognizedAPIs, manuallyAnnotatedAPIs);
		double precision = truePositives / recognizedAPIs.size();
		double recall = truePositives / manuallyAnnotatedAPIs.size();
		double f1 = 2 * ((precision * recall) / (precision + recall));
		return new Evaluation(precision, recall, f1);
	}
	
	private void countPositives(List<String> recognizedAPIMentions, List<String> manuallyAnnotatedAPIs) {
		
		for (String apiMention : recognizedAPIMentions) {
			if (manuallyAnnotatedAPIs.contains(apiMention)) {
				truePositives++;
			} else {
				falsePositives++;
			}
		}
	}
}
