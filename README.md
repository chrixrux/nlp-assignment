Please make sure to download our full package with the following link: https://www.dropbox.com/sh/ldlpo2k2xc17b86/AABVC7k-9TIHVpzbDz3UcDmNa?dl=0 . The downloaded zip contains all models and datasets necessary to run our programs. The datasets we used for our analysis can be found in the data folder. 
The file dataset.txt contains roughly 900 total posts and the annotated_dataset.txt contains ca. 100 posts with at least one API mention and ca. 300 API mentions in total.

We developed two programs, one called  StackoverflowXMLParser which can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange and the other program called StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser. If you use the provided dataset.txt in the resources/data folder you can directly use it with the StackoverflowAnalyzer.

The programs can be started from the command line. Please make sure  to navigate to the extracted folder first.

-------------------------------------------------------------------------------------------------------------------------------------------
The StackoverflowXMLParser can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange at the following link: https://archive.org/details/stackexchange
The parser will create a new "dataset.txt" file containing the extractet posts and a "stats.txt" file containing statistics about the dataset like the answer distribution. 
	 
 Required parameters:
	  	- pathToPostsFile
	 	- pathToOutputDirectory
 Optional parameters (If one optional parameter is provided, all optional parameters are required)
	 	- numberOfDiscussionThreads
	 	- totalNumberOfPosts (this includes questions and answers)
	 	- minimumAnswers
	 	- maximumAnswers
	 	
To run the parser please type:
java -jar StackoverflowXMLParser.jar pathToPostsFile pathToOutputDirectory numberOfDiscussionThreads totalNumberOfPosts minimumAnswers maximiumAnswers 
	
For the assignment we extracted 900 total posts from 200 discussion threads, with each thread having at least 4 and at most 7 answers.
The command would look like this:
StackoverflowXMLParser pathToPostsFile pathToOutputDirectory 200 900 4 7
These are also the default values if no optional parameters are provided.
	
-------------------------------------------------------------------------------------------------------------------------------------------	
 
The StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser.
It offers the following functions:

- Stemming: Given the dataset this function will remove common English stopwords and characters like parentheses and brackets. The method then will count all remaining words before and after performing the stemming. To specify the number of top words printed in the table e.g. Top 20, the numberOfTopWords parameter has to be passed when using this function.

- posTagging: Given the dataset this function will first perform sentence detection and then randomly extract a specified number of 				  sentences. The sentences then will be annotated with the most likely POS-tag sequence and printed. For repeatable results the seed for the RNG can be specified, so that the same sentences will be extracted each time.

- nBestPOSSequence: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence this prints the n-likeliest ones including their score. A higher score means this POS-tag sequence is more likely. 

- nBestPOSTag: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence it prints the n-likeliest tags and their probabilities for each token.
	 
- regexAPIMentions: This function will find API mentions in the provided dataset by using a regular expression. The regular expression we implemented can be found in the report. The number of API annotations found, their position in the whole text, and their content will be printed.
 
- crfAPIMentions: This function will find API mentions in the provided dataset by using a trained conditional random field. The CRF was trained using a human annotated gold standard with more than 100 API annotations.

- 4CrossValidation: This will perform 4 Cross Validation using our manually annotated dataset. You are not able to specify your own dataset for this function as this would mean you also have to provide the correct annotations.
					  
Usage:
java -jar StackoverflowAnalyzer.jar pathToDataset
												stemming 		 numberOfTopWords 
												posTagging 		 numberOfSentences seed
												nBestPOSSequence numberOfSentences seed n
												nBestPOSTag 	 numberOfSentences seed n 
  												regexAPIMentions
 												crfAPIMentions		
 									4CrossValidation
 										
For the following examples one of the posts from our full 500 posts database will be used. It containes multiple natural language sentences and five API mentions in the source code.
------------------------------------------------------------------------------------------------------------------------------------------------------
I'm trying to read binary data using C#. I have all information about the layout of the data in the files I want to read. I'm able to read the data "chunk by chunk", i.e. getting the first 40 bytes of data converting it to a string, get the next 40 bytes, ...
Since there are at least three slighlty different version of the data, I would like to read the data directly into a struct. It just feels so much more right than by reading it "line by line".
I have tried the following approach but to no avail:

StructType aStruct;
int count = Marshal.SizeOf(typeof(StructType));
byte[] readBuffer = new byte[count];
BinaryReader reader = new BinaryReader(stream);
readBuffer = reader.ReadBytes(count);
GCHandle handle = GCHandle.Alloc(readBuffer, GCHandleType.Pinned);
aStruct = (StructType) Marshal.PtrToStructure(handle.AddrOfPinnedObject(), typeof(StructType));
handle.Free();

The stream is an opened FileStream from which I have began to read from. I get an AccessViolationException when using Marshal.PtrToStructure.
The stream contains more information than I'm trying to read since I'm not interested in data at the end of the file.

The examples code is changed from original to make this question shorter.
How would I read binary data from a file into a struct?
-------------------------------------------------------------------------------------------------------------------------------------------------------
Goal: Print Top 5 most common words before and after stemming.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset	stemming 5
Output:
 
Goal: Print the most likely POS Sequence for three sentences of the dataset. Specify the seed to get repeatable results.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset	posTagging 3 1
Output: 
 
Goal: Print the three most likeliest POS Sequences for three sentences of the dataset. Specify the seed to get repeatable results.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset	nBestPOSSequence 3 1 3
Output: 
 		
Goal: Print the three most likeliest POS Tags and their probabilities for each word in three sentences of the dataset. Specify the seed to get repeatable results.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset	nBestPOSTag 3 1
Output: 
 
Goal: Find all API mentions in the dataset using a regular expression.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset regexAPIMentions
Output:
 
Goal: Find all API mentions in the dataset using a CRF model.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset crfAPIMentions
Output: 
As we can see all APIs were correctly recognized.
 		
Goal: Perform 4-fold cross validation using our manually annotated dataset. 
Command: java -jar StackoverflowAnalyzer.jar 4CrossValidation
Output: 
 														