import java.util.List;

public class QuarterList {
	private List<Annotation> quarterList;
	private int firstIndex;
	private int lastIndex;
	
	public QuarterList(List<Annotation> quarterList) {
		super();
		this.quarterList = quarterList;
		this.firstIndex = quarterList.get(0).start;
		this.lastIndex = quarterList.get(quarterList.size()-1).end;
	}
	
	
}
