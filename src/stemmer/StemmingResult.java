package stemmer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import utils.Utils;

public class StemmingResult {
	List<String> stemList;
	Map<String, Integer> stemCountMap;
	Map<String, Set<String>> stemOriginsMap;
	int uniqueStemsCount;
	
	public StemmingResult(List<String> stemList, Map<String, Integer> stemCountMap,
			Map<String, Set<String>> stemOriginsMap) {
		super();
		this.stemList = stemList;
		this.stemCountMap = stemCountMap;
		this.stemOriginsMap = stemOriginsMap;
	}
	
	public void printTopNStems(int n) {
		Map<String, Integer>  sortedStemCountMap = Utils.sortMapByValueDescending(stemCountMap);
		System.out.println("Top " + n + " Words after the stemming");
		int counter = 0;
		for (Entry<String, Integer> entry : sortedStemCountMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    System.out.printf("Position: %-3s Token: %-10s Count: %-3s Origins: %s%n",
		    		(counter + 1), key, value,stemOriginsMap.get(key));
		    counter++;
		    if (counter >= n) {
		    	break;
		    }
		} 
	}
	}
