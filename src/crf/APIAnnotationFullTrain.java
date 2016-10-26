package crf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

public class APIAnnotationFullTrain {
	private static List<Annotation> fullAnnotationList;
	private static String text;
	
	public static void main(String[] args) throws IOException {
		try {
			text = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
			ANNParser annParser = new ANNParser("resources/data/annotated_dataset.ann");
			fullAnnotationList = annParser.getAnnotationList();
		} catch (IOException ex) {
			System.out.println("Error reading dataset and parsing annotations");
		}

		Corpus<ObjectHandler<Chunking>> corpus = new APIAnnotationCorpus(text, fullAnnotationList);
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		boolean enforceConsistency = true;
		TagChunkCodec tagChunkCodec = new BioTagChunkCodec(tokenizerFactory, enforceConsistency);

		ChainCrfFeatureExtractor<String> featureExtractor = new SimpleChainCrfFeatureExtractor();

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

		Reporter reporter = Reporters.stdOut().setLevel(LogLevel.INFO);

		ChainCrfChunker crfChunker = ChainCrfChunker.estimate(corpus, tagChunkCodec, tokenizerFactory, featureExtractor,
				addIntercept, minFeatureCount, cacheFeatures, prior, priorBlockSize, annealingSchedule, minImprovement,
				minEpochs, maxEpochs, reporter);

		File modelFile = new File("resources/models/fullAPIModel.ser");
		AbstractExternalizable.serializeTo(crfChunker, modelFile);
	}
}
