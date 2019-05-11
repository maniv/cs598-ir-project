
package edu.asu.irs13.docir;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;


import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: manikandan
 * Date: 1/27/13
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class TermUtility
{

    /* Get count of total number of terms in the document.
    * */
    public static int GetTermCount(IndexReader indexReader)
    {
        TermEnum terms = null;
        int termcount = 0;
        try {
            terms = indexReader.terms();
            while(terms.next())
            {
                ++termcount;
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return termcount;
    }

     /*Dictioanry of TermId and List of all terms appearing in the documents
     * */
    public static LinkedHashMap<Integer,String> GetTermCache(IndexReader indexReader)
    {

        TermEnum terms = null;
        int termcount = 0;
        LinkedHashMap<Integer,String> lhm_termindexcache = new LinkedHashMap<Integer, String>();
        try {
            terms = indexReader.terms();
            while(terms.next())
            {
                lhm_termindexcache.put(++termcount,terms.term().text());
            }
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return lhm_termindexcache;
    }

    /*Get the details of TD matrix. Term and the occurrence in n-docs
    * */
    public static void LowestIDFOccurance(IndexReader indexReader)
    {
        TermEnum terms = null;
        LinkedHashMap<Double,String> lhm_tdoccurance = new LinkedHashMap<Double, String>();
        try {
            terms = indexReader.terms();
            FileWriter fstream = new FileWriter("lowestidf.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            ArrayList<Double> idfscores = new ArrayList<Double>();
            while(terms.next())
            {
                Term term = new Term("contents", terms.term().text());
                //if(indexReader.docFreq(term) != 0)
                 //{
                     //System.out.println("Term:" + terms.term().text() + "   " + "Term_DocFreq" + "   " + "[" +  indexReader.docFreq(term) + "]");
                     double maxdoc = indexReader.maxDoc();
                     double termdocfreq = indexReader.docFreq(term);
                     double termidf = Math.log(maxdoc /termdocfreq);
                     idfscores.add(termidf);
                     lhm_tdoccurance.put(termidf,terms.term().text());
                    // out.write("Term:" + terms.term().text() + "   " + "Term_DocFreq" + "   " + "[" +  indexReader.docFreq(term) + "]");
                     //out.newLine();
                 //}

            }
            Collections.sort(idfscores);
            for(int i = 0;i< idfscores.size();i++)
            {
                //System.out.println("Tern:" + lhm_tdoccurance.get(idfscores.get(i)) + " " + "IDFvalue:" + i);
                out.write("Tern:" + lhm_tdoccurance.get(idfscores.get(i)) + " " + "IDFvalue:" + idfscores.get(i));
                out.newLine();
            }
            out.close();
            System.out.println("Test");
            //out.close();
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public static void Listallterms(IndexReader indexReader)
    {
        TermEnum terms = null;
        LinkedHashMap<String,Integer> lhm_tdoccurance = new LinkedHashMap<String, Integer>();
        try {
            terms = indexReader.terms();
            FileWriter fstream = new FileWriter("termlist.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            while(terms.next())
            {
                Term term = new Term("contents", terms.term().text());
                //if(indexReader.docFreq(term) != 0)
                //{
                System.out.println("Term:" + terms.term().text() + "   " + "Term_DocFreq" + "   " + "[" +  indexReader.docFreq(term) + "]");
                out.write("Term:" + terms.term().text() + "   " + "Term_DocFreq" + "   " + "[" +  indexReader.docFreq(term) + "]");
                out.newLine();
                //}

            }
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
