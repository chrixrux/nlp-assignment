
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class parseQuestions extends DefaultHandler{
	List<Integer> postsIds = new ArrayList<Integer>();
	HashMap AnswersToQuestions = new HashMap<>();
	HashMap postsLength = new HashMap();
	PrintWriter output_file;
	PrintWriter stats;
	static String filename = "C:/Users/DELL/Downloads/posts.xml";
	
	public static void main(String[] args){
		// start parsing the XML data
		try {
		 SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
		    XMLReader xmlReader = saxParser.getXMLReader();
		    xmlReader.setContentHandler(new parseQuestions());
		    xmlReader.parse(convertToFileURL(filename));
		} catch (Exception e){
			System.out.println("error in parsing the XML document please try again");
		}
	}
	

	// first parsing of data file - find 100 questions with 4 to 8 answers
    public void startDocument() throws SAXException {
    	// initialize postsLength hash table
    	postsLength.put("short",0);
    	postsLength.put("average",0);
    	postsLength.put("long",0);
    	
    	// open up output files : dataset and statistics
    	try {
    		output_file = new PrintWriter("c:/dataset.txt", "UTF-8");
    		stats = new PrintWriter("c:/stats.txt", "UTF-8");
    	} catch (Exception e){
    		System.out.println("error in openning output files");
    	}
    }
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts)
            		throws SAXException {
    	
    	// make sure this post is a question
    	int exists =  atts.getIndex("PostTypeId");
    	if (exists > 0) {
    		int typeId = Integer.parseInt(atts.getValue("PostTypeId"));
    		if (typeId == 1) {
    			exists =  atts.getIndex("AnswerCount");
    			if (exists > 0) {
    				int answers = Integer.parseInt(atts.getValue("AnswerCount"));
    				
    				// if this post is a question, make sure it has between 4 to 7 answers
    				if ((answers > 3) && (answers < 8)) {
    					
    					// update answers stats
    					int count = AnswersToQuestions.containsKey(answers) ? (int) AnswersToQuestions.get(answers) : 0;
    					AnswersToQuestions.put(answers, count + 1);
    					String postId = atts.getValue("Id");
    					postsIds.add(Integer.parseInt(postId));
    					
    					// update post len stats
    					String content = atts.getValue("Body");
    					int content_len = wordcount(content); 
    					if (content_len  < 50) {
    						postsLength.put("short", (int)postsLength.get("short") + 1);
    					}else if (content_len < 100) {
    						postsLength.put("average", (int)postsLength.get("average") + 1);
    					} else {
    						postsLength.put("long", (int)postsLength.get("long") + 1);
    					}
    					
    					// add the content to our dataset
    					String noHtmlContent = content.replaceAll("\\<.*?>", "");
    					output_file.println(noHtmlContent);
    					output_file.println("");
    					// only get 100 questions
    					if (postsIds.size() == 100){
    						try {
    							// find answers for each question
    						 SAXParserFactory spf = SAXParserFactory.newInstance();
    						    spf.setNamespaceAware(true);
    						    SAXParser saxParser = spf.newSAXParser();
    						    XMLReader xmlReader = saxParser.getXMLReader();
    						    xmlReader.setContentHandler(new parseAnswers());
    						    xmlReader.parse(convertToFileURL(filename));
    						} catch (Exception e){
    							
    						}
    					}
    				}
    			}
    		}
    	}

}

    public void endDocument() throws SAXException {  }
    

	// helper function to convert file name from user to correct format
    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }
    
    // helper function to count number of words in a post
    public int wordcount(String word) {
    	if (word == null || word.isEmpty()) {
    		return 0; 
    		} 
    	int count = 0; 
    	char ch[] = new char[word.length()];
    	for (int i = 0; i < word.length(); i++){
    		ch[i] = word.charAt(i); 
    		if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0))) {
    			count++; 
    			} 
    		} 
    	return count; 
    }

    
    // helper class for second part of parsing 
    // find answers to selected answers
    public class parseAnswers extends DefaultHandler{
    	List<Integer> answersIds = new ArrayList<Integer>();
    	
        public void startDocument() throws SAXException {}
        public void startElement(String namespaceURI,
                String localName,
                String qName, 
                Attributes atts)
                		throws SAXException {
        	// make sure this post is an answer
        	int exists =  atts.getIndex("ParentId");
        	if (exists > 0){
        		int parentId = Integer.parseInt(atts.getValue("ParentId"));
        		// check this is an answer to one of our selected questions
        		if (postsIds.contains(parentId)){
        			answersIds.add(Integer.parseInt(atts.getValue("Id")));
        			
        			// update len stats
					String content = atts.getValue("Body");
					int content_len = wordcount(content); 
					if (content_len  < 50) {
						postsLength.put("short", (int)postsLength.get("short") + 1);
					}else if (content_len < 100) {
						postsLength.put("average", (int)postsLength.get("average") + 1);
					} else {
						postsLength.put("long", (int)postsLength.get("long") + 1);
					}
					
					// add the content to our dataset
					String noHtmlContent = content.replaceAll("\\<.*?>", "");
					output_file.println(noHtmlContent);
					output_file.println("");
        		}
        	}
        	// stop parsing when we got enough data
        	if (answersIds.size() > 400){
        		// output stats
        		stats.println(AnswersToQuestions.toString());
        		stats.println(postsLength.toString());
        		
        		// close output files
        		stats.close();
        		output_file.close();
        		System.exit(0);
        	}
    }
        public void endDocument() throws SAXException {}
    }   
}
