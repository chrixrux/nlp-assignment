
public class Annotation {
	public int start;
	public int end;
	public String text;
	
	public Annotation(int start, int end, String text) {
		super();
		this.start = start;
		this.end = end;
		this.text = text;
	}
	
	public String toString() {
		return "StartIndex: " + start + " EndIndex: " + end + " Text: " + text + "\n";
		
	}
}
