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
 * @author Christian
 *
 */
public class StackoverflowAnalyzer {
	public static void main(String[] args) throws IOException {
		String sampleText = new String(Files.readAllBytes(Paths.get("resources/data/dataset.txt")));
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
		posTagger.printNBestPosTags(randomSentencesList, 3); 
	}
}
