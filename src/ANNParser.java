import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ANNParser {

	private String annotationText;
	private String[] lines;
	public  List<Annotation> annotationList;

	public ANNParser() {
		annotationList = new ArrayList<>();
		
		try {
			annotationText = new String(Files.readAllBytes(Paths.get("resources/data/annotated_dataset.ann")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lines = annotationText.split("\n"); 

		for (int i = 0; i < lines.length; i++) {
			Annotation ann = extractAnnotation(i);
			annotationList.add(ann);		
		}
	}
	
	private  Annotation extractAnnotation (int lineIndex) {
		int startIndex = -1;
		int endIndex =-1;
		String text = "Error";
		if (lineIndex < 2) {
			startIndex = Integer.parseInt(lines[lineIndex].substring(7,10));
			endIndex = Integer.parseInt(lines[lineIndex].substring(11,14));
			text = lines[lineIndex].substring(15);
		} else if (lineIndex < 9) {
			startIndex = Integer.parseInt(lines[lineIndex].substring(7,11));
			endIndex = Integer.parseInt(lines[lineIndex].substring(12,16));
			text = lines[lineIndex].substring(17);
		} else if (lineIndex < 24) {
			startIndex = Integer.parseInt(lines[lineIndex].substring(8,12));
			endIndex = Integer.parseInt(lines[lineIndex].substring(13,17));
			text = lines[lineIndex].substring(18);
		} else if (lineIndex < 98) {
			startIndex = Integer.parseInt(lines[lineIndex].substring(8,13));
			endIndex = Integer.parseInt(lines[lineIndex].substring(14,19));
			text = lines[lineIndex].substring(20);
		} else if (lineIndex < 300) {
			startIndex = Integer.parseInt(lines[lineIndex].substring(9,14));
			endIndex = Integer.parseInt(lines[lineIndex].substring(15,20));	
			text = lines[lineIndex].substring(21);
		}
		return new Annotation(startIndex, endIndex, text);
	}
	
	
	}

