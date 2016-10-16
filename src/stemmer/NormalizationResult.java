package stemmer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.Utils;

public class NormalizationResult {
	public List<String> tokenList;
	public Map<String, Integer> tokenCountMap;
	public int uniqueTokensCount;
	
	NormalizationResult(List<String> tokenList, Map<String, Integer> tokenCountMap) {
		this.tokenList = tokenList;
		this.tokenCountMap = tokenCountMap;
		this.uniqueTokensCount = tokenCountMap.size();	
	}
	
	/**
	 * This will print a table of the n most frequent words
	 * @param n number of tokens to print
	 */
	public void printTopNTokens(int n) {
		Map<String, Integer>  sortedTokenCountMap = Utils.sortMapByValueDescending(tokenCountMap);
		System.out.println("Top " + n + " Words after stop words were removed and before the stemming");
		int counter = 0;
		for (Entry<String, Integer> entry : sortedTokenCountMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    System.out.printf("Position: %-3s Token: %-10s Count: %-3s%n",
		    		(counter + 1), key, value);
		    counter++;
		    if (counter >= n) {
		    	break;
		    }
		}
	}
}
