# COMS-E6111-Query-Expansion
Contributors of this project in no particular order
- Plaban Mohanty ([pm2878@columbia.edu](mailto:pm2878@columbia.edu))
- Gaurav Mishra ([gm2715@columbia.edu](mailto:gm2715@columbia.edu))

### Objective
To design an efficient information retrieval system that exploits user-provided relevance
feedback to improve the search results returned by Bing. This aims to achieve the target precision
entered by the user by disambiguating queries and improve the relevance of the query results that are
produced with the least possible no of feedback loops

Input: Bing API key, Target Precision, Query

Mechanism:

Base Algorithms: Stop Word Elimination and Rocchio’s algorithm
The query modification method relies on the Rocchio’s algorithm to compute a new query vector after
each feedback cycle. The overall process is mentioned below:
In the first feedback cycle, the Query string entered by user is used as the input query for the Bing API.
The system then gets the top 10 results in JSON format, parses them and prints them for the user to
provide feedback. The user marks each displayed result as relevant or not and this decision is mapped to
each result. The precision is calculated and if the precision is less than the target, then the system
generates a new query by adding 2 words to the original query and rearranging the words in the
relevant order .In the subsequent feedback cycles , this modified query is used as input for the Bing call
and the process is continued till the desired target precision is achieved.


### Class Diagram

![alt tag](https://cloud.githubusercontent.com/assets/5005160/19406498/07e9a828-9255-11e6-95c1-b51b57b7ce2a.jpg "Class Diagram")

Listing functionalities of each class

* QueryExpansionRunner – Responsible for initializing all dependencies. Also triggers QueryInteractor
* QueryInteractor – Queries Web and fetches top 10 result for the query. Takes user feedback about the results that have been returned and then passes this information to QueryExpander
* QueryWeb – Queries Web to figure out top 10 results of a query
* QueryExpander – Receives user feedback from QueryInteractor and uses this information to
expand query
* StopWordsCache – Maintains a cache of all stopwords. Is used by query expander to filter out
stop words.

### QueryResultInfo Data Model
* String URL
* String Title
* String Summary
* Boolean isRelevant
