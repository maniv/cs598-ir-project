# CSE 494 Projects

### Project Part A: Text Ranking (Project Part1 preview)
### Project Part B: Ranking using Text and Link structure 
### Project Part C: Clustering + Integrated search interface

## Summary
===============
I have shared the source files, all depenant files such as website files indexed in solr, pre-computed tf-idf vectors and pagerank scores are not included.

### Folder Structure as Follows:

### Main Class:
SearchWeb.java

### Packages:
authhubs - Authority Hubs Algorithm Implementation (For ranking pages)
docir - Includes all cosine similarity implementations (To find similarity of pages)
kmeans- Kmeans Algorithm Implementation(To cluster similar pages)
pagerank - Page Rank Algorithm Implementation (Rank pages based on page rank score combining with cosine similairty score)
snippet - Snippet generation for documents based on the query

