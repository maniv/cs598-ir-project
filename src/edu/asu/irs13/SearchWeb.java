package edu.asu.irs13;

import edu.asu.irs13.authhubs.HITSAlgorithm;
import edu.asu.irs13.docir.QueryProcessor;
import edu.asu.irs13.pagerank.PRSimScores;
import edu.asu.irs13.pagerank.PageRankTFIDFSearch;
import edu.asu.irs13.kmeans.*;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.File;
import java.util.*;

public class SearchWeb {
    public static void main(String[] args) throws Exception
    {
        // Initilaize Inverted Index Reader
        IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
        LinkAnalysis linkAnalysis = new LinkAnalysis();

        HashMap<Integer, Double> lhmp_idf_docnormvalues = (HashMap<Integer, Double>) FileOps.ReadObjectFromFile("tfidf-docmagnitude-2013.dat").readObject();


        double[] pagerankscores = (double[]) FileOps.ReadObjectFromFile("PAGERANK_SCORES_ALLPAGES.dat").readObject();

        DocVectorStructure[] docVectorStructures = ForwardIndex.CreateReverseInvertedIndex(indexReader,lhmp_idf_docnormvalues);
        String[] index_termNames = ForwardIndex.GetTermsNamesandIndex(indexReader);

        //DocVectorStructure[] docVectorStructures = (DocVectorStructure[]) FileOps.ReadObjectFromFile("FORWARDINDEX_DOCTERMWEIGHT.ser").readObject();
        /*double val =FileOps.GetMaxValue(pagerankscores);
        System.out.println("Max Page Rank Score: " + val);
        int count =0;*/


     /*   for(double score : pagerankscores)
        {
            if(score == val)
            {
                System.out.println(count);
                break;
            }
            count++;
        }
        String d_url = indexReader.document(count).getFieldable("path").stringValue();
        System.out.println(d_url);*/

     /*   LinkAnalysis obj = new LinkAnalysis();
        int[] doc1 = obj.getLinks(9048);
        int[] doc2 = obj.getCitations(9048);
        System.out.println(doc1.length + "  " + doc2.length);*/

        Scanner sc = new Scanner(System.in);
        String str = "";
        System.out.print("" +
                "Choose \n" +
                "'1' IDF Search \n" +
                "'2' AuthHubsSerach \n" +
                "'3' Page Rank \n" +
                "'4' kmeans Clustering \n" +
                "'5' Similairty Pages \n" +
                "'6' Phase Search \n" +
                "'7' Quit \n" + ">");

        while(!(str = sc.nextLine()).equals("7"))
        {
            //TF-IDF Scores
            if(str.equals("1"))
            {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");

                long startTime = System.currentTimeMillis();
                Display.Result(QueryProcessor.IDFCompute(indexReader, terms, lhmp_idf_docnormvalues), indexReader);

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Result fetched in:" + elapsedTime + "milliseconds");
            }

            //Auth Hubs
            else if (str.equals("2")) {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");

                Integer[] rootset = Display.GetTFIDFResultDocIds(QueryProcessor.IDFCompute(indexReader, terms, lhmp_idf_docnormvalues), indexReader);
                long startTime = System.currentTimeMillis();
                HITSAlgorithm authorityHubs = new HITSAlgorithm();
                //Display.DisplayAuthHUbResult(authorityHubs.AuthHubCalculation(rootset, linkAnalysis),indexReader);
                authorityHubs.AuthHubCalculation(rootset, linkAnalysis);
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Auth Hub Computation:" + elapsedTime + "milliseconds");
            }
            //Page Rank
            else  if (str.equals("3")) {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");
                HashMap<Integer,Double> simscoresofalldocs = QueryProcessor.AllIDFCompute(indexReader,terms,lhmp_idf_docnormvalues);

                //Compute Global page rank and save the object
              /* PageRank pageRank = new PageRank();


                double[] pagerankscores = pageRank.ComputePageRank(linkAnalysis);*/
                double weight = 0.8;
                PageRankTFIDFSearch pageRankTFIDFSearchobj = new PageRankTFIDFSearch();
                BoundedPriorityQueue<PRSimScores> topkresults = pageRankTFIDFSearchobj.PageRankwithTFIDFResults(simscoresofalldocs, pagerankscores, weight, indexReader);
                Display.DisplayPRSimScoreResults(topkresults, indexReader);
            }

            //Clustering
            else if (str.equals("4")) {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");
                System.out.print("No of clusters> ");
                String clusterno = scn.nextLine();
                Clustering.NO_OF_CLUSTERS = Integer.parseInt(clusterno);
                long startTime = System.currentTimeMillis();
                HashMap<Integer,Double> hmp_kDocsSimToClusters = QueryProcessor.GetTopKIDFSimilarity(indexReader,terms,lhmp_idf_docnormvalues,Clustering.NO_DOCS_CLUSTERED);

                Clustering clustering = new Clustering();
                clustering.Kmeans(hmp_kDocsSimToClusters,docVectorStructures,index_termNames);

                //BEGIN CLUSTERING

                //ENDS CLUSTERING

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Clustering Computation:" + elapsedTime + "milliseconds");
            }

            //Clustering with Similarity pages
            else if(str.equals("5"))
            {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");
                HashMap<Integer,Double> hmp_kDocsSimToClusters = QueryProcessor.GetTopKIDFSimilarity(indexReader,terms,lhmp_idf_docnormvalues,Clustering.NO_DOCS_CLUSTERED);

                long startTime = System.currentTimeMillis();

                //BEGIN CLUSTERING
                Clustering clustering = new Clustering();
                clustering.KmeansWithSimilarPages(hmp_kDocsSimToClusters,docVectorStructures,index_termNames,indexReader,lhmp_idf_docnormvalues);
                //ENDS CLUSTERING

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Result fetched in:" + elapsedTime + "milliseconds");
            }

            //Phase Search
            else if(str.equals("6"))
            {
                Scanner scn = new Scanner(System.in);
                System.out.print("query> ");
                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");

                long startTime = System.currentTimeMillis();
                Display.Result(QueryProcessor.PhaseSearchCompute(indexReader, terms, lhmp_idf_docnormvalues), indexReader);

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Result fetched in:" + elapsedTime + "milliseconds");
            }
            System.out.print("" +
                    "Choose \n" +
                    "'1' IDF Search \n" +
                    "'2' AuthHubsSerach \n" +
                    "'3' Page Rank \n" +
                    "'4' kmeans Clustering \n" +
                    "'5' Similairty Pages \n" +
                    "'6' Phase Search \n" +
                    "'7' Quit \n" + ">");
        }
        System.out.println("Run Successfully");
    }


}
