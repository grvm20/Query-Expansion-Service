
Part 2 –Sampling Documentation:
This process is carried out in several modules, each part handling a separate task.

The functions used are : 

1.	processCat(String ):
Takes the classification string in the form of : “Root/Health/Fitness” and return a string of arrays with each category as an element. The leaf node (the lowermost classification) comes first in the indexing of the array . (for ex - {Fitness,Health,Root})

2.	create_summary(String [], String name of database):
For each category that is not the leaf category, extract the queries for that category.  For each query, extract the top four URLs from Bing API. For each of those top four URLs, use the run lynx function to extract the words from the documents. This also ensures that the duplicate entries are not used. For the extracted words , the function uses a hashmap to update the count of words occurring in a document. Hence at the end of the loop, we have the count of no of docs containing that word in the hashmap value set, with key set being the words occurring in the documents. We then publish this to a file for the category and database.
Now since we process the sub categories first and do not reset the hashmap, the parent nodes or the parent classes also contain the words and their counts from the documents processed by the children classes. Hence this satisfies the requirement of the sample of the parent class containing all the children classes ‘ document sample as well.

3.	create_query_set(String category) :
This function fetches the set of query words from text files under resources folder and initializes the global map qset which holds all the queries for each class.

4.	get_query_list():
This function retrieves the query word list from the global map for a particular category and returns it

5.	searchBingResult():
This takes a url , queries the BING API and returns the result

6.	getTop4URL():
This function takes in a database and a query word, forms the Bing query URL required and gets the top 4 results and returns them

7.	create_output_file():
The map created by create_summary function is passed along with the database and class name and an output textfile is generated.

8.	Driver ():
Takes in the string class as input and database to be searched upon as inputs and starts the process


