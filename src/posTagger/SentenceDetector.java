package posTagger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

public class SentenceDetector {
	public List<String> detectSentences(String text) {
		TokenizerFactory baseTokenizer = IndoEuropeanTokenizerFactory.INSTANCE;
		SentenceModel sentenceModel = new IndoEuropeanSentenceModel();
		SentenceChunker sentenceChunker = new SentenceChunker(baseTokenizer, sentenceModel);
		Chunking chunking = sentenceChunker.chunk(text.toCharArray(), 0, text.length());
		Set<Chunk> sentences = chunking.chunkSet();
		String slice = chunking.charSequence().toString();
		
		List<String> sentenceList = new ArrayList<>();
		for (Chunk sentence : sentences) {
			int start = sentence.start();
			int end = sentence.end();
			sentenceList.add(slice.substring(start, end));
		}
		return sentenceList;
	}
}
