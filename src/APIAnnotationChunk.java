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

public class APIAnnotationChunk {
	static PrintWriter output_file;
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		APIAnnotationTrain at = new APIAnnotationTrain();
		
    	try {
    		output_file = new PrintWriter("resources/data/falsePositivesAnnotations.txt", "UTF-8");
    	} catch (Exception e){
    		System.out.println("Error in opening output files");
    	}
    	
		double totalf1 = 0;
		for (int i = 0; i < 4; i++) {
			at.train(i);
			List<Annotation> list = at.getQuarter(i, at.annotationList);
			List<String> annotationTexts = new ArrayList<>();
			for (Annotation ann : list) {
				annotationTexts.add(ann.text);
			}
			ChainCrfChunker crfChunker = (ChainCrfChunker) AbstractExternalizable
					.readObject(new File("resources/data/output.ser"));
			
			String substring;
			String text = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
			if(i == 0) {
				 substring = text.substring(0, list.get(list.size() - 1).end);
			} else if (i == 3) {
				List<Annotation> previousAnnotations = at.getQuarter(i-1, at.annotationList);
				 substring = text.substring(previousAnnotations.get(previousAnnotations.size()-1).end);
			}
			else {
				List<Annotation> previousAnnotations = at.getQuarter(i-1, at.annotationList);
				 substring = text.substring(previousAnnotations.get(previousAnnotations.size()-1).end, list.get(list.size() - 1).end);
			}
			Chunking chunking = crfChunker.chunk(substring);
			Set<Chunk> chunkSet = chunking.chunkSet();

			Iterator<Chunk> it = chunkSet.iterator();
			List<String> recognizedAPIMentions = new ArrayList<>();
			while (it.hasNext()) {
				Chunk chunk = it.next();
				int start = chunk.start();
				int end = chunk.end();
				String ann = substring.substring(start, end);
				recognizedAPIMentions.add(ann);
			}
		
			totalf1 += calculateF1(recognizedAPIMentions, annotationTexts);
		}
		totalf1 = totalf1/4;
		System.out.println(totalf1);
		output_file.close();
	}
	public static double calculateF1(List<String> recognizedAPIMentions, List<String> annotationTexts){
		double correctMatches = correctMatches(recognizedAPIMentions, annotationTexts);
		double precision = correctMatches / recognizedAPIMentions.size();
		double recall = correctMatches / annotationTexts.size();
		double f1 = 2 * ((precision * recall) / (precision + recall));
		return f1;
	}
	public static int correctMatches(List<String> recognizedAPIMentions, List<String> annotationTexts) {
		int countFalsePositives = 0;
		for (String apiMention : recognizedAPIMentions) {
			if (annotationTexts.contains(apiMention)) {
				countFalsePositives++;
			} else {
				System.out.println("wrong api mention");
				output_file.println(apiMention + "\n");
			}
		}
		return countFalsePositives;
	}

}