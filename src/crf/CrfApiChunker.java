package crf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.crf.ChainCrfChunker;
import com.aliasi.util.AbstractExternalizable;

public class CrfApiChunker implements Chunker {
	/**
	 * 
	 * @param text that will be searched for APIs
	 */
	
	public static void main(String[] args) throws IOException {
		String text = new String(Files.readAllBytes(Paths.get("resources/data/dataset.txt")));
		Chunker apiFinder = new CrfApiChunker();
		System.out.println(apiFinder.chunk(text).toString());
	}
	

	@Override
	public Chunking chunk(CharSequence arg0) {
		ChainCrfChunker crfChunker = null;
		try {
			crfChunker = (ChainCrfChunker) AbstractExternalizable.readObject(new File("resources/models/fullAPIModel.ser"));
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Error reading model");
			e.printStackTrace();
		}
		return crfChunker.chunk(arg0);
	}

	@Override
	public Chunking chunk(char[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
