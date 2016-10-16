# nlp-assignment
The StackoverflowXMLParser can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange.
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
	StackoverflowXMLParser pathToPostsFile pathToOutputDirectory numberOfDiscussionThreads totalNumberOfPosts minimumAnswers maximiumAnswers 
	
	For the assignment we extracted 900 total posts from 200 discussion threads, with each thread having at least 4 and at most 7 answers.
	The command would look like this:
	StackoverflowXMLParser pathToPostsFile pathToOutputDirectory 200 900 4 7
	These are also the default values if no optional parameters are provided.
	
 The StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser.
 It offers the following functions:
 	- Stemming: Given the dataset this function will remove common English stopwords and characters like parentheses and brackets.
 				The method then will count all remaining words before and after performing the stemming. To specify the number of 
  				top words printed in the table e.g. Top 20, the numberOfTopWords parameter has to be passed when using this function.
  
	- posTagging: Given the dataset this function will first perform sentence detection and then randomly extract a specified number of sentences.
 				 The sentences then will be annotated with the most likely POS-tag sequence and printed. For repeatable results the seed
 				 for the RNG can be specified, so that the same sentences will be extracted each time.
 
	- nBestPOSSequence: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence this prints the n-likeliest
					  including their score. A higher score means this POS-tag sequence is more likely. 

	- nBestPOSTag: This function does the same as "posTagging" but for each token it prints the n-likeliest tags and their probabilities.
	 
	- regexAPIMentions: This function will find API mentions in the provided dataset by using a regular expression. The regular expression
					   we implemented can be found in the report. The number of API annotations found, their position in the whole text, 
					   and their content will be printed.
 
	- crfAPIMentions: This function will find API mentions in the provided dataset by using a trained conditional random field. The CRF was trained using a
					 human annotated gold standard with more than 100 API annotations.  //ToDo: Cross Validation
					  
	Usage:
	StackoverflowAnalyzer pathToDataset
										stemming 		 numberOfTopWords 
										posTagging 		 numberOfSentences seed
										nBestPOSSequence numberOfSentences seed n
										nBestPOSTag 	 numberOfSentences seed n 
  										regexAPIMentions
 										crfAPIMentions							