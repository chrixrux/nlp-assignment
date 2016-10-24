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

public class APIAnnotationTrain {
	public static List<Annotation> annotationList;
	public List<String> annotationTexts; 
	public APIAnnotationTrain() {
		// TODO Auto-generated constructor stub
	}
	
	public static void train(int indexTestList) throws IOException {
		//Do not parse the dataset each time training is called. But once when the APIChunker is initialized
		String sampleText = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
		ANNParser annParser = new ANNParser();
		 annotationList = annParser.annotationList;
		
		
		List<Annotation> list = getTrainingList(indexTestList, annotationList);

		Corpus<ObjectHandler<Chunking>> corpus = new APIAnnotationCorpus(sampleText, list);
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		boolean enforceConsistency = true;
		TagChunkCodec tagChunkCodec = new BioTagChunkCodec(tokenizerFactory, enforceConsistency);
		
		ChainCrfFeatureExtractor<String> featureExtractor
        = new SimpleChainCrfFeatureExtractor();

    int minFeatureCount = 1;

    boolean cacheFeatures = true;

    boolean addIntercept = true;

    double priorVariance = 4.0;
    boolean uninformativeIntercept = true;
    RegressionPrior prior
        = RegressionPrior.gaussian(priorVariance,
                                   uninformativeIntercept);
    int priorBlockSize = 3;

    double initialLearningRate = 0.05;
    double learningRateDecay = 0.995;
    AnnealingSchedule annealingSchedule
        = AnnealingSchedule.exponential(initialLearningRate,
                                        learningRateDecay);

    double minImprovement = 0.00001;
    int minEpochs = 10;
    int maxEpochs = 5000;

    Reporter reporter
        = Reporters.stdOut().setLevel(LogLevel.NONE);

    //System.out.println("\nEstimating");
  ChainCrfChunker crfChunker
        = ChainCrfChunker.estimate(corpus,
                                   tagChunkCodec,
                                   tokenizerFactory,
                                   featureExtractor,
                                   addIntercept,
                                   minFeatureCount,
                                   cacheFeatures,
                                   prior,
                                   priorBlockSize,
                                   annealingSchedule,
                                   minImprovement,
                                   minEpochs,
                                   maxEpochs,
                                   reporter); 
 
    File modelFile = new File("resources/data/output.ser");
  //  System.out.println("\nCompiling to file=" + modelFile);
    AbstractExternalizable.serializeTo(crfChunker,modelFile);
	} 

	public static <E> List<E> getQuarter(int quarter, List<E> list) {
	int quarterIndex = list.size()/4;
	List<E> quarterList = new ArrayList<>();
	if (quarter == 4){
		quarterList = list.subList(quarterIndex*quarter, list.size()-1);
	} else {
		 quarterList = list.subList(quarterIndex*quarter, quarterIndex*(quarter+1)-1);
	}
	return quarterList;	
}
	
	private static <E> List<E> getTrainingList(int indexTestList, List<E> list) {
		List<E> trainingList = new ArrayList<>();
		for(int i = 0; i<4; i++) {
			if (!(i == indexTestList)) {
				trainingList.addAll(getQuarter(i, list));
			}
		}
		return trainingList;
	}
	
	}

