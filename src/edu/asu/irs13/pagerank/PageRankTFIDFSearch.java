package edu.asu.irs13.pagerank;

import edu.asu.irs13.BoundedPriorityQueue;
import edu.asu.irs13.Display;
import edu.asu.irs13.FileOps;
import edu.asu.irs13.docir.SimilarityScore;
import edu.asu.irs13.docir.SimlarityScoreComparator;
import org.apache.lucene.index.IndexReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Manikandan
 * Date: 3/11/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageRankTFIDFSearch {
   final int K_VALUE = 10;
   public BoundedPriorityQueue<PRSimScores> PageRankwithTFIDFResults(HashMap<Integer,Double> hmp_top10tfidfscorsanddocid,double[] allpagerankscores,double weight,IndexReader indexReader)
   {

       //double[] indivpagerankscore = new double[hmp_top10tfidfscorsanddocid.size()];
       HashMap<Integer,Double> hmp_normalisedpagerankscore = new HashMap<Integer,Double>();
       BoundedPriorityQueue<PRSimScores> topkcombinedscore = new BoundedPriorityQueue<PRSimScores>(K_VALUE,new PRSimScoresComparator());

       //Collect all pagerank scores relevant ot the query
       for(Map.Entry<Integer,Double> docidpagerankscore : hmp_top10tfidfscorsanddocid.entrySet())
       {
           hmp_normalisedpagerankscore.put(docidpagerankscore.getKey(),allpagerankscores[docidpagerankscore.getKey()]);
       }

       //Get the maximum of Page Rank Score

       Double PR_MAX_VALUE = FileOps.GetMaxValue(hmp_normalisedpagerankscore.values().toArray(new Double[hmp_normalisedpagerankscore.size()]));

       //Normalize page rank scores
       for(Map.Entry<Integer,Double> pageranksocre : hmp_normalisedpagerankscore.entrySet())
       {
           Double normalisedpagerankscore = pageranksocre.getValue() / PR_MAX_VALUE;
           hmp_normalisedpagerankscore.put(pageranksocre.getKey(),normalisedpagerankscore);
       }

       //Calculate the combined sim and page rank score



       for(Map.Entry<Integer,Double> docidpagerankscore : hmp_top10tfidfscorsanddocid.entrySet())
       {
           double normalisedtfidfscore = docidpagerankscore.getValue();
           double normalisedpagerankscore = hmp_normalisedpagerankscore.get(docidpagerankscore.getKey());

           double combinedscore = (weight * normalisedpagerankscore) + ( (1-weight) * normalisedtfidfscore);
           topkcombinedscore.add(new PRSimScores(docidpagerankscore.getKey(),combinedscore));
       }

       return topkcombinedscore;
   }

}
