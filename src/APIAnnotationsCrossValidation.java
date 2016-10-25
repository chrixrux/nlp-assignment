import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ChunkerEvaluator;
import com.aliasi.chunk.Chunking;
import com.aliasi.crf.ChainCrfChunker;
import com.aliasi.util.AbstractExternalizable;

public class APIAnnotationsCrossValidation {
	static PrintWriter output_file;

	public void perform4CrossValidation() throws IOException, ClassNotFoundException {
		// Initalize trainer that will be used to train on the dataset
		APIAnnotationCrossValidationTrain trainer = new APIAnnotationCrossValidationTrain();

		// Write the falsely predicted api mentions to a file, so that we can
		// discuss them in the report
		try {
			output_file = new PrintWriter("resources/data/falsePositivesAnnotations.txt", "UTF-8");
		} catch (Exception e) {
			System.out.println("Error in opening output files");
		}

		double totalf1 = 0;
		// Iterate over each quarter of the dataset to use it for testing
		for (int i = 0; i < 4; i++) {
			// i specifies wich quarter is used for testing the others are for
			// training. The trained model is stored in
			// resources/models/crossValidationModel.ser
			trainer.train(i);
			//testListAnnotations contains all manually annotated APIs
			List<Annotation> testListAnnotations = trainer.getQuarter(i, trainer.fullAnnotationList);
			List<String> testAnnotationTexts = new ArrayList<>();
			for (Annotation ann : testListAnnotations) {
				testAnnotationTexts.add(ann.text);
			}
			// Read the model that was created during training.
			ChainCrfChunker crfChunker = (ChainCrfChunker) AbstractExternalizable
					.readObject(new File("resources/models/crossValidationModel.ser"));
			
			
			String testDataset;
			//Get the full text which was used in brat for annotations
			String fullDatasetUsedForAnnotation = new String(
					Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
			
			//From the full text we extract the quarter which in this iteration is used for testing
			if (i == 0) {
				testDataset = fullDatasetUsedForAnnotation.substring(0,
						testListAnnotations.get(testListAnnotations.size() - 1).end);
			} else if (i == 3) {
				List<Annotation> previousAnnotations = trainer.getQuarter(i - 1, trainer.fullAnnotationList);
				testDataset = fullDatasetUsedForAnnotation
						.substring(previousAnnotations.get(previousAnnotations.size() - 1).end);
			} else {
				List<Annotation> previousAnnotations = trainer.getQuarter(i - 1, trainer.fullAnnotationList);
				testDataset = fullDatasetUsedForAnnotation.substring(
						previousAnnotations.get(previousAnnotations.size() - 1).end,
						testListAnnotations.get(testListAnnotations.size() - 1).end);
			}
			//Perform the actual named entity recognization on this iterartions testDataset
			Chunking chunking = crfChunker.chunk(testDataset);
			Set<Chunk> chunkSet = chunking.chunkSet();
			
			//Iterate over the recognized API mentions 
			Iterator<Chunk> it = chunkSet.iterator();
			List<String> recognizedAPIMentions = new ArrayList<>();
			while (it.hasNext()) {
				Chunk chunk = it.next();
				int start = chunk.start();
				int end = chunk.end();
				String ann = testDataset.substring(start, end);
				recognizedAPIMentions.add(ann);
			}
			//recognizedAPIMentions contain all the recognized APImentions while testAnnotationText contains all manually annotated apis 
			//totalf1 += calculateF1(recognizedAPIMentions, testAnnotationTexts);
			APIAnnotationEvaluator evaluator = new APIAnnotationEvaluator();
			evaluator.performEvaluation(recognizedAPIMentions, testAnnotationTexts);
			
		}
		//totalf1 = totalf1 / 4;
		//System.out.println("totalF1: " + totalf1);
		output_file.close();
	}

	public double calculateF1(List<String> recognizedAPIMentions, List<String> annotationTexts) {
		double correctMatches = correctMatches(recognizedAPIMentions, annotationTexts);
		double precision = correctMatches / recognizedAPIMentions.size();
		System.out.println("precision: " + precision);
		double recall = correctMatches / annotationTexts.size();
		System.out.println("recall: " + recall);
		double f1 = 2 * ((precision * recall) / (precision + recall));
		return f1;
	}

	public int correctMatches(List<String> recognizedAPIMentions, List<String> annotationTexts) {
		int correctMatches = 0;
		int counter = 0;
		for (String apiMention : recognizedAPIMentions) {
			if (annotationTexts.contains(apiMention)) {
				correctMatches++;
			} else {
				output_file.println("-----------------------------------");
				output_file.println(counter + " " + apiMention + "\n");
				counter++;
			}
		}
		return correctMatches;
	}

}