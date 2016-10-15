
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

public class StackoverflowXMLParser extends DefaultHandler{
	private List<Integer> postsIds = new ArrayList<Integer>();
	private HashMap<Integer, Integer> AnswersToQuestions = new HashMap<>();
	private HashMap<String, Integer> postsLength = new HashMap<>();
	private PrintWriter output_file;
	private PrintWriter stats;
	private static String pathToPostsFile;
	private static String pathToOutputDirectory;
	private static int numberOfDiscussionThreads = 200;
	private static int totalNumberOfPosts = 900;
	private static int minimumAnswers = 4;
	private static int maximumAnswers = 7;
	
	
	/**
	 * The StackoverflowXMLParser can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange.
	 *  The parser will create a new "dataset.txt" file containing the extractet posts and a "stats.txt" file containing statistics about
	 *  the dataset like answer distribution. 
	 * Required parameters:
	 * 	- pathToPostsFile
	 * 	- pathToOutputDirectory
	 * Optional parameters (If one optional parameter is provided, all optional parameters are required)
	 * 	- numberOfDiscussionThreads
	 * 	- totalNumberOfPosts (this includes questions and answers)
	 *  - minimumAnswers
	 *  - maximumAnswers
	 *  
	 *  To run the parser please type:
	 *  StackoverflowXMLParser pathToPostsFile pathToOutputDirectory numberOfDiscussionThreads totalNumberOfPosts minimumAnswers maximiumAnswers
	 *  
	 *  For the assignment we extracted 900 total posts from 200 discussion threads, with each thread having at least 4 and at most 7 answers.
	 *  The command would look like this:
	 *  StackoverflowXMLParser pathToPostsFile pathToOutputDirectory 200 900 4 7
	 *  These are also the default values if no optional parameters are provided.
	 * @param args
	 */
	public static void main(String[] args){
		//Validate inputs
		if(args.length <= 1) {
			System.out.println("The path to the Posts.xml file and the path to the output directory are required.");
			printUsage();
			System.exit(0);
		} else {
			pathToPostsFile = args[0];
			pathToOutputDirectory = args[1];
			
			if(args.length >= 2 && args.length <=5) {
				System.out.println("If one optional parameter is provided, all optional parameters are required.");
				printUsage();
				System.exit(0);
			} else {
				try{
				numberOfDiscussionThreads = Integer.parseInt(args[2]);
				totalNumberOfPosts = Integer.parseInt(args[3]);
				minimumAnswers = Integer.parseInt(args[4]);
				maximumAnswers = Integer.parseInt(args[5]);
				} catch (NumberFormatException ex) {
					System.out.println("All optinal parameters must be integers.");
					printUsage();
					System.exit(0);
				}
			}
		}
		
		
		// start parsing the XML data
		try {
		 SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
		    XMLReader xmlReader = saxParser.getXMLReader();
		    xmlReader.setContentHandler(new StackoverflowXMLParser());
		    xmlReader.parse(convertToFileURL(pathToPostsFile));
		} catch (Exception e){
			System.out.println("error in parsing the XML document please try again");
		}
	}
	

	// first parsing of data file - find 200 questions with 4 to 7 answers
    public void startDocument() throws SAXException {
    	// initialize postsLength hash table
    	postsLength.put("short",0);
    	postsLength.put("average",0);
    	postsLength.put("long",0);
    	
    	// Open up output files : dataset and statistics
    	try {
    		output_file = new PrintWriter(pathToOutputDirectory + "/dataset.txt", "UTF-8");
    		stats = new PrintWriter(pathToOutputDirectory + "/stats.txt", "UTF-8");
    	} catch (Exception e){
    		System.out.println("Error in opening output files");
    	}
    }
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts)
            		throws SAXException {
    	
    	// Make sure this post is a question
    	int exists =  atts.getIndex("PostTypeId");
    	if (exists > 0) {
    		int typeId = Integer.parseInt(atts.getValue("PostTypeId"));
    		if (typeId == 1) {
    			exists =  atts.getIndex("AnswerCount");
    			if (exists > 0) {
    				int answers = Integer.parseInt(atts.getValue("AnswerCount"));
    				
    				// if this post is a question, make sure it has between 4 to 7 answers
    				if ((answers >= minimumAnswers) && (answers <= maximumAnswers)) {
    					
    					// update answers stats
    					int count = AnswersToQuestions.containsKey(answers) ? AnswersToQuestions.get(answers) : 0;
    					AnswersToQuestions.put(answers, count + 1);
    					String postId = atts.getValue("Id");
    					postsIds.add(Integer.parseInt(postId));
    					
    					// update post len stats
    					String content = atts.getValue("Body");
    					int content_len = wordcount(content); 
    					if (content_len  < 50) {
    						postsLength.put("short", postsLength.get("short") + 1);
    					}else if (content_len < 100) {
    						postsLength.put("average", postsLength.get("average") + 1);
    					} else {
    						postsLength.put("long", postsLength.get("long") + 1);
    					}
    					
    					// add the content to our dataset
    					String noHtmlContent = content.replaceAll("\\<.*?>", "");
    					output_file.println(noHtmlContent);
    					output_file.println("");
    					// only get 200 questions
    					if (postsIds.size() == numberOfDiscussionThreads) {
    						try {
    							// find answers for each question
    						 SAXParserFactory spf = SAXParserFactory.newInstance();
    						    spf.setNamespaceAware(true);
    						    SAXParser saxParser = spf.newSAXParser();
    						    XMLReader xmlReader = saxParser.getXMLReader();
    						    xmlReader.setContentHandler(new parseAnswers());
    						    xmlReader.parse(convertToFileURL(pathToPostsFile));
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
    
    private static void printUsage() {
    	System.out.println("To run the parser please type: \n"
    			+ "StackoverflowXMLParser pathToPostsFile pathToOutputDirectory numberOfDiscussionThreads totalNumberOfPosts minimumAnswers maximiumAnswers \n"
    			+ "Required parameters: \n"
    			+ "- pathToPostsFile \n"
    			+ "- pathToOutputDirectory \n"
    			+ "Optional parameters (If one optional parameter is provided, all optional parameters are required): \n"
    			+ "- numberOfDiscussionThreads \n"
    			+ "- totalNumberOfPosts (This includes questions and answers) \n"
    			+ "- minimumAnswers \n"
    			+ "- maximumAnswers \n");
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
						postsLength.put("short", postsLength.get("short") + 1);
					}else if (content_len < 100) {
						postsLength.put("average", postsLength.get("average") + 1);
					} else {
						postsLength.put("long", postsLength.get("long") + 1);
					}
					
					// add the content to our dataset
					String noHtmlContent = content.replaceAll("\\<.*?>", "");
					output_file.println(noHtmlContent);
					output_file.println("");
        		}
        	}
        	// stop parsing when we got enough data
        	if (answersIds.size() > totalNumberOfPosts){
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
