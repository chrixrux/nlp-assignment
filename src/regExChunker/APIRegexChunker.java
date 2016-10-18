package regExChunker;
import com.aliasi.chunk.RegExChunker;

public class APIRegexChunker extends RegExChunker {
	
	private final static String API_RE = "[A-z][A-z0-9]*\\.([A-z0-9]+(\\.|\\[.*\\]\\.|\\.|\\(.*\\)\\.))*[A-z0-9]+(\\[([^\\[\\]\\n]*|(\\[[^\\[\\]]*\\]))*\\]|(\\(([^\\(\\)\\n]*|(\\([^\\(\\)]*\\)))*\\)))";
	private final static String CHUNK_TYPE = "API";
	private final static double CHUNK_SCORE = 0.0;
	
	public APIRegexChunker() {
		super(API_RE,CHUNK_TYPE,CHUNK_SCORE);
	}
}
