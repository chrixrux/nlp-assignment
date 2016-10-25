import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.chunk.BioTagChunkCodec;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.TagChunkCodec;
import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.crf.ChainCrfChunker;
import com.aliasi.crf.ChainCrfFeatureExtractor;
import com.aliasi.io.LogLevel;
import com.aliasi.io.Reporter;
import com.aliasi.io.Reporters;
import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.RegressionPrior;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;

public class APIAnnotationCrossValidationTrain {
	// annotationList stores all annotations that were annotated in the text
	public List<Annotation> fullAnnotationList;
	private String text;

	public APIAnnotationCrossValidationTrain() {
		try {
			text = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
			ANNParser annParser = new ANNParser("resources/data/annotated_dataset.ann");
			fullAnnotationList = annParser.getAnnotationList();
		} catch (IOException ex) {
			System.out.println("Error reading dataset and parsing annotations");
		}
	}

	/**
	 * This function will train on the full list of annotations  minus the quarter specified with the parameter indexOfTestList
	 * @param indexOfTestList
	 * @throws IOException
	 */
	public void train(int indexOfTestList) throws IOException {
		
		List<Annotation> trainingList = getTrainingList(indexOfTestList, fullAnnotationList);

		Corpus<ObjectHandler<Chunking>> corpus = new APIAnnotationCorpus(text, trainingList);
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		boolean enforceConsistency = true;
		//Use BIO tagging
		TagChunkCodec tagChunkCodec = new BioTagChunkCodec(tokenizerFactory, enforceConsistency);

		ChainCrfFeatureExtractor<String> featureExtractor = new SimpleChainCrfFeatureExtractor();
		//Set learning variables
		int minFeatureCount = 1;

		boolean cacheFeatures = true;

		boolean addIntercept = true;

		double priorVariance = 4.0;
		boolean uninformativeIntercept = true;
		RegressionPrior prior = RegressionPrior.gaussian(priorVariance, uninformativeIntercept);
		int priorBlockSize = 3;

		double initialLearningRate = 0.05;
		double learningRateDecay = 0.995;
		AnnealingSchedule annealingSchedule = AnnealingSchedule.exponential(initialLearningRate, learningRateDecay);

		double minImprovement = 0.00001;
		int minEpochs = 10;
		int maxEpochs = 5000;

		Reporter reporter = Reporters.stdOut().setLevel(LogLevel.NONE);
		System.out.println("Training the model. This might take a while.");
		//Train the model
		ChainCrfChunker crfChunker = ChainCrfChunker.estimate(corpus, tagChunkCodec, tokenizerFactory, featureExtractor,
				addIntercept, minFeatureCount, cacheFeatures, prior, priorBlockSize, annealingSchedule, minImprovement,
				minEpochs, maxEpochs, reporter);
		//Save the trained model
		File modelFile = new File("resources/models/crossValidationModel.ser");
		AbstractExternalizable.serializeTo(crfChunker, modelFile);
	}

	/***
	 * This function returns the specified quarter of a list
	 * 
	 * @param quarter
	 *            values: 0 - 3
	 * @param list
	 *            from which one quarter will be extracted
	 * @return
	 */
	public static <E> List<E> getQuarter(int quarter, List<E> list) {
		int quarterIndex = list.size() / 4;
		List<E> quarterList = new ArrayList<>();
		if (quarter == 4) {
			quarterList = list.subList(quarterIndex * quarter, list.size() - 1);
		} else {
			quarterList = list.subList(quarterIndex * quarter, quarterIndex * (quarter + 1) - 1);
		}
		return quarterList;
	}

	/**
	 * 
	 * @param indexTestList
	 *            specifies which quarter of the list should be used for testing
	 *            (0-3).
	 * @param list
	 *            fullList from which the trainingList will be extracted
	 * @return the list used for training in this iteration of the cross
	 *         validation, i.e. the full list without the specified list used
	 *         for testing
	 */
	private <E> List<E> getTrainingList(int indexTestList, List<E> list) {
		List<E> trainingList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			// If the current quarter is not the test quarter add it to the
			// trainingList
			if (!(i == indexTestList)) {
				trainingList.addAll(getQuarter(i, list));
			}
		}
		return trainingList;
	}
}
