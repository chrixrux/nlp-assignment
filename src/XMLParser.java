import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler{
	
	private Hashtable tags;
	List<Integer> postsIds = new ArrayList<Integer>();


    
    public void startDocument() throws SAXException {
        tags = new Hashtable();
    }
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts)
            		throws SAXException {
    	
    	int exists =  atts.getIndex("PostTypeId");
    	if (exists > 0) {
    		int typeId = Integer.parseInt(atts.getValue("PostTypeId"));
    		if (typeId == 1) {
    			exists =  atts.getIndex("AnswerCount");
    			if (exists > 0) {
    				int answers = Integer.parseInt(atts.getValue("AnswerCount"));
    				if (answers > 4) {
    					String postId = atts.getValue("Id");
    					postsIds.add(Integer.parseInt(postId));
    					// get post answers
    					
    					if (postsIds.size() == 100){
    						System.out.println(postsIds.toString());
    						System.exit(0);
    					}
    					
    				}
    				
    			}
    		}
    	}

    	String key = localName;
    	Object value = tags.get(key);

    	if (value == null) {
    		tags.put(key, new Integer(1));
    	} 
    	else {
    		int count = ((Integer)value).intValue();
    		count++;
    		tags.put(key, new Integer(count));
    	}
}

    public void endDocument() throws SAXException {
        
    }
    
	public static void main(String[] args){

		String filename = "C:/Users/DELL/Downloads/posts.xml";
		try {
		 SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
		    XMLReader xmlReader = saxParser.getXMLReader();
		    xmlReader.setContentHandler(new XMLParser());
		    xmlReader.parse(convertToFileURL(filename));
		} catch (Exception e){
			
		}
	}
	
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
	
}
