import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tag.TagLattice;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenization;
import com.aliasi.tokenizer.TokenizerFactory;

public class POSTagger {

	private HmmDecoder decoder;

	POSTagger() {
		try {
			FileInputStream fileInputStream = new FileInputStream(
					"resources/models/pos-en-general-brown.HiddenMarkovModel");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			HiddenMarkovModel hmm = (HiddenMarkovModel) objectInputStream.readObject();
			objectInputStream.close();
			decoder = new HmmDecoder(hmm);
		} catch (FileNotFoundException ex) {
			System.out.println("Path is wrong");
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printBestPosTag(List<String> sentences) {
		for (String sentence : sentences) {
//			List<String> tokens = whitespaceTokenization(sentence);
			List<String> tokens = indoEuropeanTokenization(sentence);
			Tagging<String> taggingResults = decoder.tag(tokens);
			System.out.println(taggingResults.toString());
		}
	}

	public void printNBestPosTags(List<String> sentences, int maxResults) {
		System.out.printf("Score %13s", "Sentences");
		for (String sentence : sentences) {
//			List<String> tokens = whitespaceTokenization(sentence);
			List<String> tokens = indoEuropeanTokenization(sentence);
			Iterator<ScoredTagging<String>> iterator = decoder.tagNBest(tokens, maxResults);
			System.out.println();
			while (iterator.hasNext()) {
				ScoredTagging<String> scoredTagging = iterator.next();
				System.out.printf("%-8.3f ", scoredTagging.score());
				
				for (int i = 0; i < scoredTagging.size(); i++) {
				System.out.print(" " + scoredTagging.token(i) + "/" + scoredTagging.tag(i));
				}
				System.out.println();
			}
		}
	}

	public void printPosTagLattice(List<String> sentences) {
		System.out.printf("Token %14s", "Prob/Tag");
		for (String sentence : sentences) {
//			List<String> tokens = whitespaceTokenization(sentence);
			List<String> tokens = indoEuropeanTokenization(sentence);
			
			// Perform POS Tagging
			TagLattice<String> tagLattice = decoder.tagMarginal(tokens);
			
			System.out.println();
			for (int i = 0; i < tagLattice.numTokens(); i++) {
				System.out.printf("%-10s", tokens.get(i) + " ");
				ConditionalClassification cc = tagLattice.tokenClassification(i);
				for (int j = 0; j < 3; j++) {
					System.out.printf("%8.3f", cc.score(j));
					System.out.print("/" + cc.category(j));
				}
				System.out.println();
			}
		}
	}
	
	private List<String> whitespaceTokenization(String text) {
		String[] tokensArray = text.split(" ");
		return Arrays.asList(tokensArray);

	}
	
	private List<String> indoEuropeanTokenization(String text) {
		TokenizerFactory baseTokenizer = IndoEuropeanTokenizerFactory.INSTANCE;
		Tokenization tokenizer = new Tokenization(text, baseTokenizer);
		List<String> tokens = tokenizer.tokenList();
		return tokens;
	}
}
