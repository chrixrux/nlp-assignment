import java.util.List;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ChunkFactory;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ChunkingImpl;
import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;

public class APIAnnotationCorpus extends Corpus<ObjectHandler<Chunking>> {
	
	Chunking chunking;
	
	APIAnnotationCorpus(String text, List<Annotation> annotations) {
		chunking = chunking(text, annotations);
	}
	
	public void visitTrain(ObjectHandler<Chunking> handler) {
		handler.handle(chunking);
	}
	
	public void visitTest(ObjectHandler<Chunking> handler) {
		/*handler.handle(chunking); */
		}
	
	private Chunking chunking(String text, List<Annotation> annotations) {
		ChunkingImpl chunkingImpl = new ChunkingImpl(text);
		for(Annotation ann: annotations) {
			chunkingImpl.add(chunk(ann.start, ann.end, "API"));
		}
		return chunkingImpl;
	}
	
	private Chunk chunk(int start, int end, String type) {
        return ChunkFactory.createChunk(start,end,type);
    }
}
