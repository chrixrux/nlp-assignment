import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.Tokenization;
import com.aliasi.tokenizer.TokenizerFactory;

public class Stemmer {
	
	/**
	 * This function will tokenize the input string and remove stop words
	 * @param text
	 * @return
	 */
	 public NormalizationResult normalize(String text) {
         // Create base tokenizer
		 /*
		  * It might be a bad idea to use IndoEuropeanTokenizer because it also separates . and ( )
		  * So it will split String.format() into 6 different tokens. Other possibility would be a whitespace tokenizer
		  * but it would keep word, together.
		  */
         TokenizerFactory baseTokenizer = IndoEuropeanTokenizerFactory.INSTANCE;
         // LowerCaseTokenizer filters the tokenizers produced by the base tokenizer factory to produce lower case output.
         TokenizerFactory lowerCaseTokenizer = new LowerCaseTokenizerFactory(baseTokenizer);
         // EnglishStopTokenizer applies an English stop list
         TokenizerFactory stopTokenizer = new StopTokenizerFactory(lowerCaseTokenizer, STOP_SET);
         //perform tokenization
         Tokenization tk = new Tokenization(text, stopTokenizer);
         List<String> tokenList = tk.tokenList();
         
         //After we perform the normalization we iterate over the resulting list to count the words. 
         //We store the count for each token in a map.
         Map<String, Integer> tokenCountMap = new HashMap<>();
 		
         for(String token: tokenList) {
 			if (tokenCountMap.containsKey(token)) {
 				tokenCountMap.put(token, tokenCountMap.get(token) + 1);
 			} else {
 				tokenCountMap.put(token, 1);
 			}
 		}
          return new NormalizationResult(tokenList, tokenCountMap); 
    }

   public StemmingResult stem(List<String> tokens) {
         TokenizerFactory baseTokenizer = IndoEuropeanTokenizerFactory.INSTANCE;
         PorterStemmerTokenizerFactory porterTokenizer = new PorterStemmerTokenizerFactory(baseTokenizer);
         
         List<String> stemList = new ArrayList<String>();
         Map<String, Integer> stemCountMap = new HashMap<>();
         Map<String, Set<String>> stemOriginsMap = new HashMap<>();
         
         for (String token: tokens) {
        	String stem = PorterStemmerTokenizerFactory.stem(token);
        	stemList.add(stem);
        	
        	if (stemCountMap.containsKey(stem)) {
 				stemCountMap.put(stem, stemCountMap.get(stem) + 1);
 			} else {
 				stemCountMap.put(stem, 1);
 			}
        	
        	if (stemOriginsMap.containsKey(stem)) {
        		Set<String> origin = stemOriginsMap.get(stem);
        		origin.add(token);
        		stemOriginsMap.put(stem, origin);
        	} else {
        		Set<String> stemOriginsSet = new HashSet<>();
        		stemOriginsSet.add(token);
				stemOriginsMap.put(stem, stemOriginsSet);
			} 
         }   
         return new StemmingResult(stemList, stemCountMap, stemOriginsMap);
    } 
   
   /**
    * The set of stop words, all lowercased.
    */
   static final Set<String> STOP_SET = new HashSet<String>();
   static {
	   //This is taken from the EnglishStopTokenizerFactory provided by Lingpipe
       STOP_SET.add("a");
       STOP_SET.add("be");
       STOP_SET.add("had");
       STOP_SET.add("it");
       STOP_SET.add("only");
       STOP_SET.add("she");
       STOP_SET.add("was");
       STOP_SET.add("about");
       STOP_SET.add("because");
       STOP_SET.add("has");
       STOP_SET.add("its");
       STOP_SET.add("of");
       STOP_SET.add("some");
       STOP_SET.add("we");
       STOP_SET.add("after");
       STOP_SET.add("been");
       STOP_SET.add("have");
       STOP_SET.add("last");
       STOP_SET.add("on");
       STOP_SET.add("such");
       STOP_SET.add("were");
       STOP_SET.add("all");
       STOP_SET.add("but");
       STOP_SET.add("he");
       STOP_SET.add("more");
       STOP_SET.add("one");
       STOP_SET.add("than");
       STOP_SET.add("when");
       STOP_SET.add("also");
       STOP_SET.add("by");
       STOP_SET.add("her");
       STOP_SET.add("most");
       STOP_SET.add("or");
       STOP_SET.add("that");
       STOP_SET.add("which");
       STOP_SET.add("an");
       STOP_SET.add("can");
       STOP_SET.add("his");
       STOP_SET.add("mr");
       STOP_SET.add("other");
       STOP_SET.add("the");
       STOP_SET.add("who");
       STOP_SET.add("any");
       STOP_SET.add("co");
       STOP_SET.add("if");
       STOP_SET.add("mrs");
       STOP_SET.add("out");
       STOP_SET.add("their");
       STOP_SET.add("will");
       STOP_SET.add("and");
       STOP_SET.add("corp");
       STOP_SET.add("in");
       STOP_SET.add("ms");
       STOP_SET.add("over");
       STOP_SET.add("there");
       STOP_SET.add("with");
       STOP_SET.add("are");
       STOP_SET.add("could");
       STOP_SET.add("inc");
       STOP_SET.add("mz");
       STOP_SET.add("s");
       STOP_SET.add("they");
       STOP_SET.add("would");
       STOP_SET.add("as");
       STOP_SET.add("for");
       STOP_SET.add("into");
       STOP_SET.add("no");
       STOP_SET.add("so");
       STOP_SET.add("this");
       STOP_SET.add("up");
       STOP_SET.add("at");
       STOP_SET.add("from");
       STOP_SET.add("is");
       STOP_SET.add("not");
       STOP_SET.add("says");
       STOP_SET.add("to");
       // We added the following manually so that we only get words 
       STOP_SET.add(".");
       STOP_SET.add(",");
       STOP_SET.add("?");
       STOP_SET.add("!");
       STOP_SET.add("/");
       STOP_SET.add("\\");
       STOP_SET.add("(");
       STOP_SET.add(")");
       STOP_SET.add(":");
       STOP_SET.add("\"");
       STOP_SET.add("'");
       STOP_SET.add("t"); //From words like can't or don't, t would be counted as a separate word. We don't want that. 
       STOP_SET.add(";");
       STOP_SET.add("-");
       STOP_SET.add("gt");
       STOP_SET.add("lt");
       STOP_SET.add("=");
       STOP_SET.add("&");
       STOP_SET.add("{");
       STOP_SET.add("}");
       STOP_SET.add("_");
       STOP_SET.add("+");
       STOP_SET.add("[");
       STOP_SET.add("]");
       STOP_SET.add("$");
       //Numbers are not words
       STOP_SET.add("0");
       STOP_SET.add("1");
       STOP_SET.add("2");
       STOP_SET.add("3");
       STOP_SET.add("4");
       STOP_SET.add("5");
       STOP_SET.add("6");
       STOP_SET.add("7");
       STOP_SET.add("8");
       STOP_SET.add("9");
       //Random letters
       STOP_SET.add("c");
       STOP_SET.add("m");
       
   }
}
