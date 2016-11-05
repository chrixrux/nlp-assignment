package main;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;

import crf.APIAnnotationsCrossValidation;
import crf.CrfApiChunker;
import posTagger.POSTagger;
import posTagger.SentenceDetector;
import regExChunker.APIRegexChunker;
import stemmer.NormalizationResult;
import stemmer.Stemmer;
import stemmer.StemmingResult;
import utils.Utils;

/**
 * The StackoverflowAnalyzer offers functionality to analyze the dataset after
 * it was parsed by the StackoverflowXMLParser. It offers the following
 * functions: 
 * - Stemming: Given the dataset this function will remove common
 * English stopwords and characters like parentheses and brackets. The method
 * then will count all remaining words before and after performing the stemming.
 * To specify the number of top words printed in the table e.g. Top 20, the
 * numberOfTopWords parameter has to be passed when using this function.
 * 
 * - posTagging: Given the dataset this function will first perform sentence
 * detection and then randomly extract a specified number of sentences. The
 * sentences then will be annotated with the most likely POS-tag sequence and
 * printed. For repeatable results the seed for the RNG can be specified, so
 * that the same sentences will be extracted each time.
 * 
 * - nBestPOSSequence: This function does the same as "posTagging" but instead
 * of printing just the most likely POS-tag sequence this prints the n-likeliest
 * including their score. A higher score means this POS-tag sequence is more
 * likely.
 *
 * - nBestPOSTag: This function does the same as "posTagging" but for each token
 * it prints the n-likeliest tags and their probabilities.
 * 
 * - regexAPIMentions: This function will find API mentions in the provided
 * dataset by using a regular expression. The regular expression we implemented
 * can be found in the report. The number of API annotations found, their
 * position in the whole text, and their content will be printed.
 * 
 * - crfAPIMentions: This function will find API mentions in the provided
 * dataset by using a trained conditional random field. The CRF was trained
 * using a human annotated gold standard with more than 100 API annotations.
 * 
 * - 4CrossValidation: This will perform 4 Cross Validation using our manually
 * annotated dataset. You are not able to specify your own dataset for this
 * function as this would mean you also have to provide the correct annotations.
 * 
 * Usage: StackoverflowAnalyzer pathToDataset stemming numberOfTopWords
 * posTagging numberOfSentences seed nBestPOSSequence numberOfSentences seed n
 * nBestPOSTag numberOfSentences seed n regexAPIMentions crfAPIMentions
 * 4CrossValidation
 * 
 * 
 * @author Christian Widmer
 *
 */
public class StackoverflowAnalyzer {
	private static String text = "Error: Default text should be overwritten";

	public static void main(String[] args) {
		// Validate and parse input parameters
		
		//If the first argument is the command to perform cross validation just do it 
		if(args.length == 1) {
			if(args[0].equals("4CrossValidation")) {
				perform4CrossValidation();
			} else {
				printUsage();
			}
			System.exit(0);
		}
		
		//For all other commands the validation starts here
		if (args.length < 2) {
			System.out.println("Not enough input parameters specified!");
			printUsage();
			System.exit(0);
		}

		String pathToDataset = args[0];

		try {
			text = new String(Files.readAllBytes(Paths.get(pathToDataset)));
		} catch (IOException e) {
			System.out.println("Error while loading dataset. Is the path correct?");
			printUsage();
			System.exit(0);
		}

		String function = args[1];
		switch (function) {
		case "stemming":
			if (args.length == 3) {
				String numberOfTopWords = args[2];
				int numTopWords = Integer.parseInt(numberOfTopWords);
				performStemming(numTopWords);
			} else {
				System.out.println("Please provide the number of top words to print");
				printUsage();
			}
			break;
		case "posTagging":
		case "nBestPOSSequence":
		case "nBestPOSTag":
			if (args.length == 4 || args.length == 5) {
				String numberOfSentences = args[2];
				int numSentences = Integer.parseInt(numberOfSentences);

				String seed = args[3];
				int intSeed = Integer.parseInt(seed);

				if (args.length == 5) {
					String n = args[4];
					int intN = Integer.parseInt(n);
					performPOSTagging(function, numSentences, intSeed, intN);
					break;
				}
				performPOSTagging(function, numSentences, intSeed);
			} else {
				System.out.println("Missing parameters for " + function);
				printUsage();
			}
			break;
		case "regexAPIMentions":
			performRegExAPImentions();
			break;
		case "crfAPIMentions":
			performCrfApiMentions();
			break;
		case "4CrossValidation":
			perform4CrossValidation();

		default:
			System.out.println("Function " + function + " is not recognized.");
			printUsage();
		}
	}

	private static void printUsage() {
		System.out.println("Usage: \n" + "java -jar StackoverflowAnalyzer.jar pathToDataset \n"
				+ "\t\t\t\t stemming numberOfTopWords \n" + "\t\t\t\t posTagging numberOfSentences seed \n"
				+ "\t\t\t\t nBestPOSSequence numberOfSentences seed n \n"
				+ "\t\t\t\t nBestPOSTag numberOfSentences seed n  \n" + "\t\t\t\t regexAPIMentions \n"
				+ "\t\t\t\t crfAPIMentions \n"
				+ "\t 4CrossValidation");
	}

	private static void performStemming(int numberOfTopWords) {
		Stemmer stemmer = new Stemmer();
		NormalizationResult normResult = stemmer.normalize(text);
		normResult.printTopNTokens(numberOfTopWords);

		StemmingResult stemResult = stemmer.stem(normResult.tokenList);
		stemResult.printTopNStems(numberOfTopWords);
	}

	private static void performPOSTagging(String function, int... parameters) {
		int numberOfSentences = parameters[0];
		int seed = parameters[1];
		int n = -1;
		if (parameters.length == 3) {
			n = parameters[2];
		}

		SentenceDetector sentenceDetector = new SentenceDetector();
		List<String> sentenceList = sentenceDetector.detectSentences(text);
		List<String> randomSentencesList = Utils.getNRandomListElements(sentenceList, numberOfSentences, seed);

		POSTagger posTagger = new POSTagger();

		switch (function) {
		case "posTagging":
			posTagger.printBestPosTag(randomSentencesList);
			break;
		case "nBestPOSSequence":
			if(n > 0) {
			posTagger.printNBestPosTags(randomSentencesList, n);
			} else {
				printUsage();
			}
			break;
		case "nBestPOSTag":
			if(n > 0) {
			posTagger.printPosTagLattice(randomSentencesList, n);
			} else {
				printUsage();
			}
			break;
		}
	}

	private static void performRegExAPImentions() {
		Chunker chunker = new APIRegexChunker();
		Chunking chunking = chunker.chunk(text);
		Set<Chunk> chunkSet = chunking.chunkSet();
		
		//Just used to evaluate RegEx Api finder not necessary for actual API recognition 
		/*ANNParser annParser = new ANNParser("resources/data/annotated_dataset.ann");
		List<Annotation> manuallyAnnotatedAPIs = annParser.getAnnotationList();
		List<String> manuallyAnnotatedAPITexts = new ArrayList<>();
		for (Annotation ann : manuallyAnnotatedAPIs) {
			manuallyAnnotatedAPITexts.add(ann.text);
		}
		List<String> recognizedAPIMentions = new ArrayList<>(); */
		
		System.out.println(chunkSet.size() + " API mentions found with a regular expression");
		for (Chunk chunk : chunkSet) {
			int startIndex = chunk.start();
			int endIndex = chunk.end();
			String apiMention = text.substring(startIndex, endIndex);
		//	recognizedAPIMentions.add(apiMention);
			System.out.println("From index " + startIndex + " to " + endIndex + " API mentioned: " + apiMention);
		}
	/*	APIAnnotationEvaluator evaluator = new APIAnnotationEvaluator();
		Evaluation eval = evaluator.performEvaluation(recognizedAPIMentions, manuallyAnnotatedAPITexts);
		
		System.out.println("Precision: " + eval.precision);
		System.out.println("Recall: " + eval.recall);
		System.out.println("F1: " + eval.f1); */
	}

	private static void performCrfApiMentions() {
		Chunker chunker = new CrfApiChunker();
		Chunking chunking = chunker.chunk(text);
		Set<Chunk> chunkSet = chunking.chunkSet();

		System.out.println(chunkSet.size() + " API mentions found with the trained CRF");
		for (Chunk chunk : chunkSet) {
			int startIndex = chunk.start();
			int endIndex = chunk.end();
			String apiMention = text.substring(startIndex, endIndex);
			System.out.println("From index " + startIndex + " to " + endIndex + " API mentioned: " + apiMention);
		}
	}

	private static void perform4CrossValidation() {
		APIAnnotationsCrossValidation cv = new APIAnnotationsCrossValidation();
		try {
			cv.perform4CrossValidation();
		} catch (Exception ex) {
			System.out.println("There was an issue while performing 4 - cross validation");
			ex.printStackTrace();
		}
	}
}
