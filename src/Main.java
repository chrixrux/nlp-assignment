import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
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
