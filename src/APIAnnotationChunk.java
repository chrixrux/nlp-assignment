import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ChunkerEvaluator;
import com.aliasi.chunk.Chunking;
import com.aliasi.crf.ChainCrfChunker;
import com.aliasi.util.AbstractExternalizable;

public class APIAnnotationChunk {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ChainCrfChunker crfChunker = (ChainCrfChunker) AbstractExternalizable.readObject(new File("resources/data/output.ser"));
		//ChunkerEvaluator evaluator = new ChunkerEvaluator(crfChunker);

		String text = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.txt")));
		//char[] textChars = text.toCharArray();
		Chunking chunking = crfChunker.chunk(text);
		Set<Chunk> chunkSet = chunking.chunkSet();
  		System.out.println(chunkSet.size());
		
		Iterator<Chunk> it = chunkSet.iterator();
       
		while (it.hasNext()) {
            Chunk chunk = it.next();
            int start = chunk.start();
            int end = chunk.end();
            String ann = text.substring(start,end);
            System.out.println("chunk: " + chunk + " text: " + ann);
        }
    }
}