Please make sure to download our full package with the following link: https://www.dropbox.com/s/mmsm0b6z59i9sou/CZ4045full.zip?dl=0 . The downloaded zip contains all models and datasets necessary to run our programs. The datasets we used for our analysis can be found in the data folder.
The file dataset.txt contains roughly 900 total posts and the annotated_dataset.txt contains ca. 100 posts with at least one API mention and ca. 300 API mentions in total.

We developed two programs, one called  StackoverflowXMLParser which can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange and the other program called StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser. If you use the provided dataset.txt in the resources/data folder you can directly use it with the StackoverflowAnalyzer.

The programs can be started from the command line. Please make sure  to navigate to the extracted folder first.

The StackoverflowXMLParser can be used to extract posts from the unzipped "Posts.xml" file provided by Stackexchange at the following link: https://archive.org/details/stackexchange
The parser will create a new "dataset.txt" file containing the extractet posts and a "stats.txt" file containing statistics about the dataset like the answer distribution.

 + Required parameters:
	  - pathToPostsFile
	 	- pathToOutputDirectory
 + Optional parameters (If one optional parameter is provided, all optional parameters are required)
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

### StackoverflowAnalyzer

The StackoverflowAnalyzer offers functionality to analyze the dataset after it was parsed by the StackoverflowXMLParser.
It offers the following functions:

- Stemming: Given the dataset this function will remove common English stopwords and characters like parentheses and brackets. The method then will count all remaining words before and after performing the stemming. To specify the number of top words printed in the table e.g. Top 20, the numberOfTopWords parameter has to be passed when using this function.

- posTagging: Given the dataset this function will first perform sentence detection and then randomly extract a specified number of 				  sentences. The sentences then will be annotated with the most likely POS-tag sequence and printed. For repeatable results the seed for the RNG can be specified, so that the same sentences will be extracted each time.

- nBestPOSSequence: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence this prints the n-likeliest ones including their score. A higher score means this POS-tag sequence is more likely.

- nBestPOSTag: This function does the same as "posTagging" but instead of printing just the most likely POS-tag sequence it prints the n-likeliest tags and their probabilities for each token.

- regexAPIMentions: This function will find API mentions in the provided dataset by using a regular expression. The regular expression we implemented can be found in the report. The number of API annotations found, their position in the whole text, and their content will be printed.

- crfAPIMentions: This function will find API mentions in the provided dataset by using a trained conditional random field. The CRF was trained using a human annotated gold standard with more than 100 API annotations.

- 4CrossValidation: This will perform 4 Cross Validation using our manually annotated dataset. You are not able to specify your own dataset for this function as this would mean you also have to provide the correct annotations.

**Usage:**
```bash
java -jar StackoverflowAnalyzer.jar pathToDataset
												stemming 		 numberOfTopWords
												posTagging 		 numberOfSentences seed
												nBestPOSSequence numberOfSentences seed n
												nBestPOSTag 	 numberOfSentences seed n
  												regexAPIMentions
 												crfAPIMentions		
 									4CrossValidation
 ```
### Examples
For the following examples one of the posts from our full 500 posts database will be used. It containes multiple natural language sentences and five API mentions in the source code. It can be found in recources/data/exampleDataset.

```
I'm trying to read binary data using C#. I have all information about the layout of the data in the files I want to read. I'm able to read the data "chunk by chunk", i.e. getting the first 40 bytes of data converting it to a string, get the next 40 bytes, ....
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
```
___
##### Stemming
**Goal:**  Print Top 5 most common words before and after stemming.

**Command:**
```
java -jar StackoverflowAnalyzer.jar pathToDataset	stemming 5
```
**Output:**

Top 5 Words after stop words were removed and before the stemming:  
Position: 1   Token: i          Count: 12  
Position: 2   Token: data       Count: 8  
Position: 3   Token: read       Count: 7  
Position: 4   Token: structtype Count: 4  
Position: 5   Token: readbuffer Count: 3  

Top 5 Words after the stemming:

Position: 1   Token: i          Count: 12  Origins: [i]  
Position: 2   Token: data       Count: 8   Origins: [data]  
Position: 3   Token: read       Count: 8   Origins: [read, reading]  
Position: 4   Token: structtyp  Count: 4   Origins: [structtype]  
Position: 5   Token: byte       Count: 4   Origins: [bytes, byte]
___
##### Finding the most likely POS sequence

**Goal:** Print the most likely POS sequence for two sentences of the dataset. Specify the seed to get repeatable results.

**Command:**
```
java -jar StackoverflowAnalyzer.jar pathToDataset	posTagging 2 1
```
**Output:**

+ **Sentence 1:**
The/at stream/nn contains/vbz more/ap information/nn than/cs I/ppss '/' m/bem trying/vbg to/to read/vb since/cs I/ppss '/' m/bem not/* interested/vbn in/in data/nn at/in the/at end/nn of/in the/at file/nn ./.  

+ **Sentence 2:**
I/ppss '/' m/bem trying/vbg to/to read/vb binary/jj data/nns using/vbg C/nil #/nil ./.

**Comment:** As we can see Sentence 3 are actually two sentences but the sentence detection failed to distinguish them because they are not separated by a dot but by an ellipses.
___

#### Find the n most likeliest POS sequences

**Goal:** Print the three most likeliest POS Sequences for two sentences of the dataset. Specify the seed to get repeatable results.

**Command:**
```  
java -jar StackoverflowAnalyzer.jar pathToDataset nBestPOSSequence 2 1 3
```
**Output:**

* Sentence 1:
 * Score: -238.790  The/at stream/nn contains/vbz more/ap information/nn than/cs I/ppss '/' m/bem trying/vbg to/to read/vb since/cs I/ppss '/' m/bem not/* interested/vbn in/in data/nn at/in the/at end/nn of/in the/at file/nn ./.
 * Score: -238.872  The/at stream/nn contains/vbz more/ap information/nn than/cs I/ppss '/' m/bem trying/vbg to/to read/vb since/cs I/ppss '/' m/bem not/* interested/vbn in/in data/nns at/in the/at end/nn of/in the/at file/nn ./.
 * Score: -243.149  The/at stream/nn contains/vbz more/ap information/nn than/cs I/ppss '/nps$ m/bem trying/vbg to/to read/vb since/cs I/ppss '/' m/bem not/* interested/vbn in/in data/nn at/in the/at end/nn of/in the/at file/nn ./.

* Sentence 2:
 * Score: -149.250  I/ppss '/' m/bem trying/vbg to/to read/vb binary/jj data/nns using/vbg C/nil #/nil ./.
 * Score: -150.560  I/ppss '/' m/bem trying/vbg to/to read/vb binary/jj data/nn using/vbg C/nil #/nil ./.
 * Score: -152.674  I/ppss '/' m/bem trying/vbg to/to read/vb  binary/jj data/nns using/vbg C/uh #/uh ./.


**Comment:** For example we can see in the second sentence for the first and second POS sequence the POS tag for the token 'data' is different. In the first sequence it's 'nns' and in the second 'nn'. As we can also see the POSSequence with the highest score is the sequence printed by the earlier posTagging command.

#### Print the n most likeliest POS tags for each word

**Goal:** Print the three most likeliest POS Tags and their probabilities for each word in two sentences of the dataset. Specify the seed to get repeatable results.
**Command:**
```
 java -jar StackoverflowAnalyzer.jar pathToDataset	nBestPOSTag 2 1 3
 ```
**Output:**
Token       Prob/Tag
+ Sentence 1:
The          0.999/at   0.001/np   0.000/jj  
stream       0.992/nn   0.006/rb   0.002/jj
contains     0.989/vbz   0.011/nns   0.000/nn
more         0.930/ap   0.042/ql   0.023/rbr
information    0.997/nn   0.003/jj   0.000/vb
than         0.969/cs   0.031/in   0.000/vbd
I            0.917/ppss   0.017/np$   0.015/np
'            0.715/'   0.088/nps$   0.042/nns$
m            0.710/bem   0.036/nns$   0.024/nn
trying       0.977/vbg   0.022/jj   0.002/nil
to           0.982/to   0.017/in   0.000/rb
read         0.975/vb   0.011/vbn   0.008/nn
since        0.957/cs   0.029/rb   0.014/in
I            0.936/ppss   0.012/np$   0.011/np
'            0.724/'   0.091/nps$   0.042/nns$
m            0.790/bem   0.050/wpo   0.021/nil
not          0.998/*   0.001/ql   0.001/rb
interested    0.972/vbn   0.026/jj   0.002/vbd
in           0.957/in   0.034/rp   0.003/jj
data         0.508/nn   0.492/nns   0.000/jj
at           0.987/in   0.006/cs   0.002/wpo
the          1.000/at   0.000/jj   0.000/nn
end          0.986/nn   0.005/vbn   0.004/jj
of           1.000/in   0.000/rb   0.000/vbz
the          1.000/at   0.000/jj   0.000/nn
file         0.971/nn   0.014/jj   0.010/nns
.            1.000/.   0.000/np   0.000/nn

+ Sentence 2:
I            0.925/ppss   0.017/np   0.014/np$
'            0.715/'   0.088/nps$   0.042/nns$
m            0.710/bem   0.036/nns$   0.024/nn
trying       0.977/vbg   0.022/jj   0.002/nil
to           0.983/to   0.016/in   0.000/rb
read         0.976/vb   0.008/vbn   0.008/jj
binary       0.965/jj   0.034/nn   0.000/np
data         0.652/nns   0.348/nn   0.000/jj
using        0.977/vbg   0.013/jj   0.005/nn
C            0.198/nil   0.142/nn   0.089/np
*#*           0.192/nil   0.069/pp$$   0.058/uh
.            1.000/.   0.000/nil   0.000/np


Goal: Find all API mentions in the dataset using a regular expression.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset regexAPIMentions
Output:

5 API mentions found with a regular expression
From index 537 to 571 API mentioned: Marshal.SizeOf(typeof(StructType))
From index 671 to 694 API mentioned: reader.ReadBytes(count)
From index 714 to 761 API mentioned: GCHandle.Alloc(readBuffer, GCHandleType.Pinned)
From index 786 to 857 API mentioned: Marshal.PtrToStructure(handle.AddrOfPinnedObject(), typeof(StructType))
From index 859 to 872 API mentioned: handle.Free()

-------------------------------------------------------------------------------------------------------------------------------------------------------

Goal: Find all API mentions in the dataset using a CRF model.
Command: java -jar StackoverflowAnalyzer.jar pathToDataset crfAPIMentions
Output:

4 API mentions found with the trained CRF
From index 537 to 571 API mentioned: Marshal.SizeOf(typeof(StructType))
From index 714 to 761 API mentioned: GCHandle.Alloc(readBuffer, GCHandleType.Pinned)
From index 773 to 857 API mentioned: (StructType) Marshal.PtrToStructure(handle.AddrOfPinnedObject(), typeof(StructType))
From index 859 to 872 API mentioned: handle.Free()

-------------------------------------------------------------------------------------------------------------------------------------------------------

Goal: Perform 4-fold cross validation using our manually annotated dataset.
Command: java -jar StackoverflowAnalyzer.jar 4CrossValidation
Output:

Average Precision: 0.50125
Average Recall: 0.27142857142857146
Average F1: 0.3488779269170512
