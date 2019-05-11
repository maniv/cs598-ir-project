package edu.asu.irs13.docir;

import edu.asu.irs13.BoundedPriorityQueue;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 1/31/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryProcessor {
    public static final int TOP_K_DOC=10;
    public static BoundedPriorityQueue TFCompute(IndexReader indexReaders,String[] terms,HashMap<Integer,Double> lhmp_tf_docnormvalues)
    {

        long startTime = System.currentTimeMillis();
        HashMap<String,Double> QueryVector = new HashMap<String,Double>();
        HashMap<Integer,HashMap<String,Integer>> DocumentTFVector = new HashMap<Integer, HashMap<String, Integer>>();
        TermDocs tdocs = null;

       try
       {
            /*Iterate through each terms*/
            for(String word : terms)
            {

                /*If the hasmap contains the word, then increment the word count*/
                if (QueryVector.containsKey(word))
                {
                    Double termcount = QueryVector.get(word);
                    termcount += 1.0;
                    QueryVector.put(word, termcount);
                }
                /*Else add the new word to the dictionary*/
                else if(!QueryVector.containsKey(word))
                {
                    QueryVector.put(word,1.0);
                }
                
                /*
                 * Construct the document vector corresponding to query using Inverted Index
                */
                Term term = new Term("contents", word);

                tdocs = indexReaders.termDocs(term);
                while(tdocs.next())
                {

                   /*System.out.println(tdocs.doc());*/
                    if(tdocs.doc() > 10000)
                    {
                        HashMap<String,Integer> doctfvector = new HashMap<String, Integer>();

                        if(DocumentTFVector.containsKey(tdocs.doc()))
                        {
                            doctfvector = DocumentTFVector.get(tdocs.doc());
                            doctfvector.put(term.text(), tdocs.freq());
                        }
                        else if(!DocumentTFVector.containsKey(tdocs.doc())){
                            doctfvector.put(term.text(), tdocs.freq());
                        }
                        DocumentTFVector.put(tdocs.doc(),doctfvector);
                    }
                }
            }
       }

       catch (IOException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("TF Query Processing time & Document fetched:" + elapsedTime + "milliseconds");
         return ComputeTFSimilarity(QueryVector,DocumentTFVector,lhmp_tf_docnormvalues);
    }


    public static BoundedPriorityQueue<SimilarityScore> IDFCompute(IndexReader indexReaders,String[] terms,HashMap<Integer,Double> lhmp_idf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        HashMap<String,Double> QueryVector = new HashMap<String, Double>();
        HashMap<Integer,HashMap<String,Double>> DocumentIDFVector = new HashMap<Integer, HashMap<String, Double>>();
        TermDocs tdocs = null;
        try
        {
            /*Iterate through each terms*/
            for(String word : terms)
            {

                /*If the hasmap contains the word, then increment the word count*/
                if (QueryVector.containsKey(word))
                {
                    Double termcount = QueryVector.get(word);
                    termcount += 1.0;
                    QueryVector.put(word, termcount);
                }
                /*Else add the new word to the dictionary*/
                else if(!QueryVector.containsKey(word))
                {
                    QueryVector.put(word,1.0);
                }

                /*
                 * Construct the document vector corresponding to query using Inverted Index
                */
                Term term = new Term("contents", word);

                tdocs = indexReaders.termDocs(term);
                while(tdocs.next())
                {
                    /*System.out.println(tdocs.doc());
                    if(tdocs.doc() != 0)
                    {*/
                        HashMap<String,Double> docidfvector = new HashMap<String, Double>();
                        if(DocumentIDFVector.containsKey(tdocs.doc()))
                        {
                            double maxdoc = indexReaders.maxDoc();
                            double docfreq = indexReaders.docFreq(term);
                            double termfreq = tdocs.freq();
                            double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                            docidfvector = DocumentIDFVector.get(tdocs.doc());
                            docidfvector.put(term.text(), tfidfvalue);
                        }
                        else if(!DocumentIDFVector.containsKey(tdocs.doc()))
                        {
                            double maxdoc = indexReaders.maxDoc();
                            double docfreq = indexReaders.docFreq(term);
                            double termfreq = tdocs.freq();
                            double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                            docidfvector.put(term.text(), tfidfvalue);
                        }
                        DocumentIDFVector.put(tdocs.doc(),docidfvector);
                    /*}*/
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("IDF Query Processing time & Document fetched:" + elapsedTime + "milliseconds");
        return ComputeIDFSimilarity(QueryVector,DocumentIDFVector,lhmp_idf_docnormvalues);
    }

    /*Compute Dot product between Document and Query TF scores
    * */
    public static BoundedPriorityQueue ComputeTFSimilarity(HashMap<String,Double> QueryVector,HashMap<Integer,HashMap<String,Integer>> DocumentTFVector,HashMap<Integer,Double> lhmp_tf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        BoundedPriorityQueue<SimilarityScore> doctfscores = new BoundedPriorityQueue<SimilarityScore>(TOP_K_DOC,new SimlarityScoreComparator());

         for (Map.Entry<Integer,HashMap<String,Integer>> doctfvectors : DocumentTFVector.entrySet())
         {
              HashMap<String,Integer> doctfvalues = doctfvectors.getValue();
              Double sumofdotproducts = 0.0;

              for (Map.Entry<String,Integer> tfvalue : doctfvalues.entrySet())
              {
                  double doctf = tfvalue.getValue();
                  double querytf = QueryVector.get(tfvalue.getKey());
                  sumofdotproducts += ( doctf * querytf);
              }
             double docmagnitude = lhmp_tf_docnormvalues.get(doctfvectors.getKey());
             double similarity = (sumofdotproducts / docmagnitude );
             doctfscores.add(new SimilarityScore(doctfvectors.getKey(),similarity));
         }
        System.out.println("Number of document results fetched:" + DocumentTFVector.size() );
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Sorting Time:" + elapsedTime + "milliseconds");
        return doctfscores;
    }

    /*Compute Similarity based on IDF values*/
    public  static BoundedPriorityQueue<SimilarityScore> ComputeIDFSimilarity(HashMap<String,Double> QueryVector,HashMap<Integer,HashMap<String,Double>> DocumentIDFVector,HashMap<Integer,Double> lhmp_idf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        BoundedPriorityQueue<SimilarityScore> docidfscores = new BoundedPriorityQueue<SimilarityScore>(TOP_K_DOC,new SimlarityScoreComparator());

        //Iterate through all the documents that contains the query term
        for (Map.Entry<Integer,HashMap<String,Double>> doctfidfvectors : DocumentIDFVector.entrySet())
        {
            HashMap<String,Double> doctfidfvalues = doctfidfvectors.getValue();
            double sumofdotproducts = 0.0;

            for (Map.Entry<String,Double> idfvalue : doctfidfvalues.entrySet())
            {
                double docidf = idfvalue.getValue();
                double querytf = QueryVector.get(idfvalue.getKey());
                sumofdotproducts += (docidf * querytf);
            }

            double similarity = sumofdotproducts / lhmp_idf_docnormvalues.get(doctfidfvectors.getKey());
            docidfscores.add(new SimilarityScore(doctfidfvectors.getKey(),similarity));
        }
        System.out.println("Number of document results fetched:" + DocumentIDFVector.size() );
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Sorting Time:" + elapsedTime + "milliseconds");
        return docidfscores;
    }

    public static HashMap<Integer,Double> AllIDFCompute(IndexReader indexReaders,String[] terms,HashMap<Integer,Double> lhmp_idf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        HashMap<String,Double> QueryVector = new HashMap<String, Double>();
        HashMap<Integer,HashMap<String,Double>> DocumentIDFVector = new HashMap<Integer, HashMap<String, Double>>();
        TermDocs tdocs = null;
        try
        {
            /*Iterate through each terms*/
            for(String word : terms)
            {

                /*If the hasmap contains the word, then increment the word count*/
                if (QueryVector.containsKey(word))
                {
                    Double termcount = QueryVector.get(word);
                    termcount += 1.0;
                    QueryVector.put(word, termcount);
                }
                /*Else add the new word to the dictionary*/
                else if(!QueryVector.containsKey(word))
                {
                    QueryVector.put(word,1.0);
                }

                /*
                 * Construct the document vector corresponding to query using Inverted Index
                */
                Term term = new Term("contents", word);

                tdocs = indexReaders.termDocs(term);
                while(tdocs.next())
                {
                    /*System.out.println(tdocs.doc());
                    if(tdocs.doc() != 0)
                    {*/
                    HashMap<String,Double> docidfvector = new HashMap<String, Double>();
                    if(DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector = DocumentIDFVector.get(tdocs.doc());
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    else if(!DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    DocumentIDFVector.put(tdocs.doc(),docidfvector);
                    /*}*/
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("IDF Query Processing time & Document fetched:" + elapsedTime + "milliseconds");
        return ComputeAllIDFSimilarity(QueryVector,DocumentIDFVector,lhmp_idf_docnormvalues);
    }

    public  static HashMap<Integer,Double> ComputeAllIDFSimilarity(HashMap<String,Double> QueryVector,HashMap<Integer,HashMap<String,Double>> DocumentIDFVector,HashMap<Integer,Double> lhmp_idf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        //BoundedPriorityQueue<SimilarityScore> docidfscores = new BoundedPriorityQueue<SimilarityScore>(10,new SimlarityScoreComparator());
        HashMap<Integer,Double>  docidfscores = new HashMap<Integer, Double>();

        //Iterate through all the documents that contains the query term
        for (Map.Entry<Integer,HashMap<String,Double>> doctfidfvectors : DocumentIDFVector.entrySet())
        {
            HashMap<String,Double> doctfidfvalues = doctfidfvectors.getValue();
            double sumofdotproducts = 0.0;

            for (Map.Entry<String,Double> idfvalue : doctfidfvalues.entrySet())
            {
                double docidf = idfvalue.getValue();
                double querytf = QueryVector.get(idfvalue.getKey());
                sumofdotproducts += (docidf * querytf);
            }

            double similarity = sumofdotproducts / lhmp_idf_docnormvalues.get(doctfidfvectors.getKey());
            docidfscores.put(doctfidfvectors.getKey(), similarity);
        }
        System.out.println("Number of document results fetched:" + docidfscores.size() );
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Sorting Time:" + elapsedTime + "milliseconds");
        return docidfscores;
    }

    public static  HashMap<Integer,Double> ComputeTopKIDFSimilarity(HashMap<String,Double> QueryVector,HashMap<Integer,HashMap<String,Double>> DocumentIDFVector,HashMap<Integer,Double> lhmp_idf_docnormvalues,int topkdoc)
    {

        BoundedPriorityQueue<SimilarityScore> bpqrankeddoc = new BoundedPriorityQueue<SimilarityScore>(topkdoc,new SimlarityScoreComparator());
        HashMap<Integer,Double> hmp_topkidfdocscores = new HashMap<Integer, Double>();
        //Iterate through all the documents that contains the query term
        for (Map.Entry<Integer,HashMap<String,Double>> doctfidfvectors : DocumentIDFVector.entrySet())
        {
            HashMap<String,Double> doctfidfvalues = doctfidfvectors.getValue();
            double sumofdotproducts = 0.0;

            for (Map.Entry<String,Double> idfvalue : doctfidfvalues.entrySet())
            {
                double docidf = idfvalue.getValue();
                double querytf = QueryVector.get(idfvalue.getKey());
                sumofdotproducts += (docidf * querytf);
            }

            double similarity = sumofdotproducts / lhmp_idf_docnormvalues.get(doctfidfvectors.getKey());
            bpqrankeddoc.add(new SimilarityScore(doctfidfvectors.getKey(),similarity));
        }
        System.out.println("Number of document results fetched:" + DocumentIDFVector.size() );

        ArrayList<Double> sortedscore = new ArrayList<Double>();
        HashMap<Integer,Double> ktmprankeddoc = new HashMap<Integer, Double>();

        while(!bpqrankeddoc.isEmpty())
        {
            SimilarityScore obj = (SimilarityScore) bpqrankeddoc.poll();
            ktmprankeddoc.put(obj.docid, obj.simscore);
            sortedscore.add(obj.simscore);
        }
        Collections.sort(sortedscore);

        for(int i=(sortedscore.size() -1); i>=0 ; i--)
        {
            for(Map.Entry<Integer,Double> docid : ktmprankeddoc.entrySet())
            {
                if (docid.getValue() == sortedscore.get(i))
                {
                      hmp_topkidfdocscores.put(docid.getKey(),docid.getValue());
                }
            }
        }
        return hmp_topkidfdocscores;
    }

    public static HashMap<Integer,Double> GetTopKIDFSimilarity(IndexReader indexReaders,String[] terms,HashMap<Integer,Double> lhmp_idf_docnormvalues,int topkdoc)
    {
        long startTime = System.currentTimeMillis();
        HashMap<String,Double> QueryVector = new HashMap<String, Double>();
        HashMap<Integer,HashMap<String,Double>> DocumentIDFVector = new HashMap<Integer, HashMap<String, Double>>();
        TermDocs tdocs = null;
        try
        {
            /*Iterate through each terms*/
            for(String word : terms)
            {

                /*If the hasmap contains the word, then increment the word count*/
                if (QueryVector.containsKey(word))
                {
                    Double termcount = QueryVector.get(word);
                    termcount += 1.0;
                    QueryVector.put(word, termcount);
                }
                /*Else add the new word to the dictionary*/
                else if(!QueryVector.containsKey(word))
                {
                    QueryVector.put(word,1.0);
                }

                /*
                 * Construct the document vector corresponding to query using Inverted Index
                */
                Term term = new Term("contents", word);

                tdocs = indexReaders.termDocs(term);
                while(tdocs.next())
                {
                    /*System.out.println(tdocs.doc());
                    if(tdocs.doc() != 0)
                    {*/
                    HashMap<String,Double> docidfvector = new HashMap<String, Double>();
                    if(DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector = DocumentIDFVector.get(tdocs.doc());
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    else if(!DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    DocumentIDFVector.put(tdocs.doc(),docidfvector);
                    /*}*/
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("IDF Query Processing time & Document fetched:" + elapsedTime + "milliseconds");
        return ComputeTopKIDFSimilarity(QueryVector,DocumentIDFVector,lhmp_idf_docnormvalues,topkdoc);
    }

    public static BoundedPriorityQueue<SimilarityScore> PhaseSearchCompute(IndexReader indexReaders,String[] terms,HashMap<Integer,Double> lhmp_idf_docnormvalues)
    {
        long startTime = System.currentTimeMillis();
        HashMap<String,Double> QueryVector = new HashMap<String, Double>();
        HashMap<Integer,HashMap<String,Double>> DocumentIDFVector = new HashMap<Integer, HashMap<String, Double>>();
        TermDocs tdocs = null;
        try
        {
            /*Iterate through each terms*/
            for(String word : terms)
            {

                /*If the hasmap contains the word, then increment the word count*/
                if (QueryVector.containsKey(word))
                {
                    Double termcount = QueryVector.get(word);
                    termcount += 1.0;
                    QueryVector.put(word, termcount);
                }
                /*Else add the new word to the dictionary*/
                else if(!QueryVector.containsKey(word))
                {
                    QueryVector.put(word,1.0);
                }

                /*
                 * Construct the document vector corresponding to query using Inverted Index
                */
                Term term = new Term("contents", word);

                tdocs = indexReaders.termDocs(term);
                while(tdocs.next())
                {
                    /*System.out.println(tdocs.doc());
                    if(tdocs.doc() != 0)
                    {*/
                    HashMap<String,Double> docidfvector = new HashMap<String, Double>();
                    if(DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector = DocumentIDFVector.get(tdocs.doc());
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    else if(!DocumentIDFVector.containsKey(tdocs.doc()))
                    {
                        double maxdoc = indexReaders.maxDoc();
                        double docfreq = indexReaders.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        docidfvector.put(term.text(), tfidfvalue);
                    }
                    DocumentIDFVector.put(tdocs.doc(),docidfvector);
                    /*}*/
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //Filter based on phase search
        HashMap<Integer,HashMap<String,Double>> shallowCopyDocIDF = new HashMap<Integer,HashMap<String,Double>>(DocumentIDFVector);
        for(Map.Entry<Integer,HashMap<String,Double>> docidfphasesearchonly : DocumentIDFVector.entrySet())
        {
             if(docidfphasesearchonly.getValue().size() < terms.length)
             {
                 shallowCopyDocIDF.remove(docidfphasesearchonly.getKey());
             }
        }


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("IDF Query Processing time & Document fetched:" + elapsedTime + "milliseconds");



        return ComputeIDFSimilarity(QueryVector,shallowCopyDocIDF,lhmp_idf_docnormvalues);
    }
}
