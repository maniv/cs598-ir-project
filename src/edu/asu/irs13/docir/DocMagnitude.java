package edu.asu.irs13.docir;


import edu.asu.irs13.FileOps;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;

import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: manikandan
 * Date: 1/28/13
 * Time: 2:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocMagnitude {

    /*Constructs a vector space model of TF and IDF model of given document list
      Hashmap 1 contains -> Key : docid and Values: hashmap: Key: term and values:  termfrequency(TF weights) corresponding to that document
      Save HashMap objects
   * */
    public static long ConstructTFVSM(IndexReader indexReader)
    {
        long elapsedTime = 0;
        try {
            // Stopwatch
            long startTime = System.currentTimeMillis();
            System.out.println("Starttime:" + startTime);
            //Initialize Terms
            TermEnum termEnum = indexReader.terms();

            //Initialize dictionary to store TF weight
            HashMap<Integer,HashMap<String,Integer>> hm_tf_docvsm = new HashMap<Integer, HashMap<String, Integer>>(indexReader.maxDoc());

            //Construct document key for Hashmaps
            for(int docid = 0;docid < indexReader.maxDoc(); docid++)
            {
                HashMap<String,Integer> lhmap_tfvector = new HashMap<String, Integer>();
                hm_tf_docvsm.put(docid, lhmap_tfvector);
            }

                while (termEnum.next())
                {
                    Term term = new Term("contents", termEnum.term().text());
                    TermDocs tdocs = indexReader.termDocs(term);
                    while (tdocs.next())
                    {
                        if(hm_tf_docvsm.containsKey(tdocs.doc()))
                        {
                            HashMap<String,Integer> lhmap_tf = hm_tf_docvsm.get(tdocs.doc());
                            lhmap_tf.put(termEnum.term().text(), tdocs.freq());
                            hm_tf_docvsm.put(tdocs.doc(),lhmap_tf);
                        }
                    }
                }
            long stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("TimeTaken ClassFormatError Doc norm:" + elapsedTime);

            FileOps.SaveObject(hm_tf_docvsm, "TFVSM2013.dat");
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return  elapsedTime;
    }

    /*Constructs a vector space model of TF and IDF model of given document list
     Hashmap contains -> Key : docid and Values: hashmap: Key: term and values:  idf (weights) corresponding to the term in that document
     Save all HashMap objects
  * */
    public static long ConstructIDFVSM(IndexReader indexReader)
    {
        long elapsedTime = 0;
        try {
            // Stopwatch
            long startTime = System.currentTimeMillis();
            System.out.println("Starttime:" + startTime);
            //Initialize Terms
            TermEnum termEnum = indexReader.terms();
            //Initialize dictionary to store TF, IDF and TF*IDF weight
            HashMap<Integer,HashMap<String,Double>> hm_idf_docvsm = new HashMap<Integer, HashMap<String, Double>>();

            //Construct key for all hashmaps
            for(int docid = 0;docid < indexReader.maxDoc(); docid++)
            {
                HashMap<String,Double> lhmap_idfvector = new HashMap<String, Double>();
                hm_idf_docvsm.put(docid,lhmap_idfvector);
            }

            while (termEnum.next())
            {
                Term term = new Term("contents", termEnum.term().text());
                TermDocs tdocs = indexReader.termDocs(term);
                while (tdocs.next())
                {
                    if(hm_idf_docvsm.containsKey(tdocs.doc()))
                    {
                        HashMap<String,Double> lhmap_idf = hm_idf_docvsm.get(tdocs.doc());
                        //IDF Storage
                        double idfvalue = Math.log( (double) (indexReader.maxDoc()) / (double) (indexReader.docFreq(term)));
                        lhmap_idf.put(termEnum.term().text(), idfvalue);
                        hm_idf_docvsm.put(tdocs.doc(),lhmap_idf);
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("TimeTaken ClassFormatError Doc norm:" + elapsedTime);

            FileOps.SaveObject(hm_idf_docvsm,"IDFVSM2013.dat");

        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return  elapsedTime;
    }

    /*Constructs a vector space model of TF and IDF model of given document list
     Hashmap contains -> Key : docid and Values: hashmap: Key: term and values:  tf*idf (weights) corresponding to the term in that document
     Save HashMap objects
  * */
    public static void ConstructTFIDFVSM(IndexReader indexReader)
    {
        long elapsedTime = 0;
        try {
            // Stopwatch
            long startTime = System.currentTimeMillis();
            System.out.println("Starttime:" + startTime);
            //Initialize Terms
            TermEnum termEnum = indexReader.terms();

            //Initialize dictionary to store TF, IDF and TF*IDF weight
            HashMap<Integer,HashMap<String,Double>> hm_tfidf_docvsm = new HashMap<Integer, HashMap<String, Double>>();

            //Construct key for all Hashmaps
            for(int docid = 0;docid < indexReader.maxDoc(); docid++)
            {
                HashMap<String,Double> lhmap_idfvector = new HashMap<String, Double>();
                hm_tfidf_docvsm.put(docid,lhmap_idfvector);
            }

            while (termEnum.next())
            {
                Term term = new Term("contents", termEnum.term().text());
                TermDocs tdocs = indexReader.termDocs(term);
                while (tdocs.next())
                {
                    if(hm_tfidf_docvsm.containsKey(tdocs.doc()))
                    {
                        HashMap<String,Double> lhmap_tfidf = hm_tfidf_docvsm.get(tdocs.doc());

                        //TF-IDF Storage
                        double maxdoc = indexReader.maxDoc();
                        double docfreq = indexReader.docFreq(term);
                        double termfreq = tdocs.freq();
                        double tfidfvalue = ( (termfreq) *  ( Math.log( maxdoc / docfreq) ) );
                        lhmap_tfidf.put(termEnum.term().text(), tfidfvalue);
                        hm_tfidf_docvsm.put(tdocs.doc(),lhmap_tfidf);
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("TimeTaken ConstructTFIDFVSM:" + elapsedTime);
            FileOps.SaveObject(hm_tfidf_docvsm,"TFIDFVSM2013.dat");
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /*Calculate the Document magnitude for TF vector |Di| */
    public static  void CalculateTFDocNorm(HashMap<Integer,HashMap<String,Integer>> lhmap_docvsm)
    {
        long elapsedTime = 0;
        HashMap<Integer,Double> lhmp_tf_docnormvalues = new HashMap<Integer, Double>();
        try{
            // Stopwatch
            long startTime = System.currentTimeMillis();
            System.out.println("Starttime:" + startTime);
            //Iterate through all documents
            for (Map.Entry<Integer, HashMap<String,Integer>> docvsmentry : lhmap_docvsm.entrySet())
            {
                //Get the TF list of the specific document
                HashMap<String,Integer> subvector = docvsmentry.getValue();
                double tf_sumofsqures =0;

                //Iterate through all TF values in the list
                for (Map.Entry<String,Integer> tfvsm : subvector.entrySet())
                {
                    //TF- Calculation on computing square of TF
                    tf_sumofsqures += Math.pow(tfvsm.getValue(),2);
                }
                //TF-Calculation for taking magnitude of the vector
                double tf_docmagnitude = Math.sqrt(tf_sumofsqures);

                //Store the values in hashmap
                lhmp_tf_docnormvalues.put(docvsmentry.getKey(), tf_docmagnitude);
            }

            long stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("TimeTaken CalculateTFDocNorm:" + elapsedTime);
            FileOps.SaveObject(lhmp_tf_docnormvalues,"tf-docmagnitude-2013.dat");
        }
        catch (Exception e)
        {

        }

    }

    /*Calculate the Document magnitude for IDF vector |Di|*/
    public static void CalculateIDFDocNorm(HashMap<Integer,HashMap<String,Double>> lhmap_docvsm)
    {
        long elapsedTime = 0;
        HashMap<Integer,Double> lhmp_idf_docnormvalues = new HashMap<Integer, Double>();
        try{
            // Stopwatch
            long startTime = System.currentTimeMillis();
            System.out.println("Starttime:" + startTime);
            //Iterate through all documents
            for (Map.Entry<Integer, HashMap<String,Double>> docvsmentry : lhmap_docvsm.entrySet())
            {
                //Get the IDF list of the specific document
                HashMap<String,Double> subvector = docvsmentry.getValue();
                double tf_sumofsqures =0.0;

                //Iterate through all IDF values in the list
                for (Map.Entry<String,Double> tfvsm : subvector.entrySet())
                {
                    //TF- Calculation on computing square of TF
                    tf_sumofsqures += Math.pow(tfvsm.getValue(),2);
                }
                //TF-Calculation for taking magnitude of the vector
                double tf_docmagnitude = Math.sqrt(tf_sumofsqures);

                //Store the values in hashmap
                lhmp_idf_docnormvalues.put(docvsmentry.getKey(), tf_docmagnitude);
            }
            long stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("TimeTaken CalculateIDFDocNorm:" + elapsedTime);
            FileOps.SaveObject(lhmp_idf_docnormvalues,"tfidf-docmagnitude-2013.dat");
        }
        catch (Exception e)
        {

        }
    }



}
