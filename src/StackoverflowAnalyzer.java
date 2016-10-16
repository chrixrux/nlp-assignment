import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * The StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser.
 * It offers the following functions:
 * 	- Stemming: Given the dataset this function will remove common English stopwords and characters like parentheses and brackets.
 * 				The method then will count all remaining words before and after performing the stemming. To specify the number of 
 * 				top words printed in the table e.g. Top 20, the numberOfTopWords parameter has to be passed when using this function.
 * 
 * - posTagging: Given the dataset this function will first perform sentence detection and then randomly extract a specified number of sentences.
 * 				 The sentences then will be annotated with the most likely POS-tag sequence and printed. For repeatable results the seed
 * 				 for the RNG can be specified, so that the same sentences will be extracted each time.
 * 
 * - nBestPOSSequence: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence this prints the n-likeliest
 * 					  including their score. A higher score means this POS-tag sequence is more likely. 
 *
 * - nBestPOSTag: This function does the same as "posTagging" but for each token it prints the n-likeliest tags and their probabilities.
 * 
 * - regexAPIMentions: This function will find API mentions in the provided dataset by using a regular expression. The regular expression
 * 					   we implemented can be found in the report. The number of API annotations found, their position in the whole text, 
 * 					   and their content will be printed.
 * 
 * - crfAPIMentions: This function will find API mentions in the provided dataset by using a trained conditional random field. The CRF was trained using a
 * 					 human annotated gold standard with more than 100 API annotations.  //ToDo: Cross Validation
 * 
 * Usage:
 * StackoverflowAnalyzer pathToDataset
 * 										stemming 		 numberOfTopWords 
 * 										posTagging 		 numberOfSentences seed
 * 										nBestPOSSequence numberOfSentences seed n
 * 										nBestPOSTag 	 numberOfSentences seed n 
 * 										regexAPIMentions
 * 										crfAPIMentions
 * 							
 * 							
 * @author Christian Widmer
 *
 */
public class StackoverflowAnalyzer {
	private static String text = "Error: Default text should be overwritten";
	public static void main(String[] args) {
		//Validate and parse input parameters 
		if(args.length < 2) {
			System.out.println("Not enough input parameters specified");
		}
		
		String pathToDataset = args[0];
		
		try {
			text = new String(Files.readAllBytes(Paths.get(pathToDataset)));
		} catch (IOException e) {
			System.out.println("Error while loading dataset. Is the path correct?");
			System.exit(0);
		}
		
		String function = args[1];
		switch (function) {
		case "stemming":
			if(args.length == 3) {
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
			if(args.length == 4 || args.length == 5) {
				String numberOfSentences = args[2];
				int numSentences = Integer.parseInt(numberOfSentences);
				
				String seed = args[3];
				int intSeed = Integer.parseInt(seed);
				
				if(args.length == 5) {
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
			break;
		case "crfAPIMentions":
			break;
			
		default:
				System.out.println("Function " + function + "is not recognized.");
				printUsage();
		}
		
		/*String sampleText = new String(Files.readAllBytes(Paths.get("resources/data/dataset.txt")));
		Stemmer stemmer = new Stemmer();
		NormalizationResult normResult = stemmer.normalize(sampleText);
		normResult.printTopNTokens(20);
		
		StemmingResult stemResult = stemmer.stem(normResult.tokenList);
		stemResult.printTopNStems(20);
		
		SentenceDetector sentenceDetector = new SentenceDetector();
		List<String> sentenceList = sentenceDetector.detectSentences(sampleText);
		List<String> randomSentencesList = Utils.getNRandomListElements(sentenceList, 10);
		
		POSTagger posTagger = new POSTagger();
		posTagger.printBestPosTag(randomSentencesList);
		posTagger.printPosTagLattice(randomSentencesList);
		posTagger.printNBestPosTags(randomSentencesList, 3);  */
	}
	
	private static void printUsage() {
		System.out.println("Usage: \n"
				+ "StackoverflowAnalyzer pathToDataset \n"
				+ "\t\t\t\t stemming numberOfTopWords \n"
				+ "\t\t\t\t posTagging numberOfSentences seed \n"
				+ "\t\t\t\t nBestPOSSequence numberOfSentences seed n \n"
				+ "\t\t\t\t nBestPOSTag numberOfSentences seed n  \n"
				+ "\t\t\t\t regexAPIMentions \n"
				+ "\t\t\t\t crfAPIMentions");
	}
	
	private static void performStemming(int numberOfTopWords) {
		Stemmer stemmer = new Stemmer();
		NormalizationResult normResult = stemmer.normalize(text);
		normResult.printTopNTokens(numberOfTopWords);
		
		StemmingResult stemResult = stemmer.stem(normResult.tokenList);
		stemResult.printTopNStems(numberOfTopWords);
	}
	
	private static void performPOSTagging(String function, int... numbers) {
		
	}
}
