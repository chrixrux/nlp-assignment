package crf;
import java.io.File;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.crf.ChainCrfChunker;
import com.aliasi.util.AbstractExternalizable;

public class FullCrfApiFinder {
	/**
	 * 
	 * @param text that will be searched for APIs
	 */
	private Set<Chunk> chunkSet;
	
	public FullCrfApiFinder(String text) {
		//Load model. We created the model during development and it is included in the package
		try {
			ChainCrfChunker crfChunker = (ChainCrfChunker) AbstractExternalizable
					.readObject(new File("resources/models/fullAPIModel.ser"));
			Chunking chunking = crfChunker.chunk(text);
			chunkSet = chunking.chunkSet();
		} catch (Exception e) {
			System.out.println("Error finding API mentions");
		}
	}
	
	public Set<Chunk> chunkSet() {
		return chunkSet;
	}
}
