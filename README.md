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