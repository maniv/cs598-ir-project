package edu.asu.irs13.kmeans;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;

import java.util.ArrayList;
import java.util.HashMap;


public class ForwardIndex {
    final static String CONTENTS = "contents";
    public static DocVectorStructure[] CreateReverseInvertedIndex(IndexReader indexReader,HashMap<Integer,Double> hmp_kdocstoclusters)
    {
        DocVectorStructure[] docVectorStructures = null;
        try{
            //Initialize trems and numdocs
            TermEnum termenum = indexReader.terms();
            int totalNumDocs = indexReader.numDocs();

            //Get the TF-IDF scores for documents
            Double[] tfidfscores = hmp_kdocstoclusters.values().toArray(new Double[hmp_kdocstoclusters.size()]) ;


            /*Initialize items to store:
              Includes:
              1. List of terms in documents
              2. Docnumber->TermID
              3.Docnumber->Termweight
            */
            ArrayList<String> termNameList = new ArrayList<String>();

            ArrayList<Integer>[] index_DoctermIndex = new ArrayList[totalNumDocs];

            ArrayList<Double>[] index_DocTermWeight = new ArrayList[totalNumDocs];

            //Initialze collection with document id's
            for ( int iterator = 0; iterator < totalNumDocs; iterator++ ) {
                index_DoctermIndex[iterator] = new ArrayList<Integer>();
                index_DocTermWeight[iterator] = new ArrayList<Double>();
            }

            int termId= 0;
            //Iterate through all terms in document
            while(termenum.next())
            {
                Term term = termenum.term();
                if (term.field().equals(CONTENTS))
                {
                    //Add terms to list
                    termNameList.add(term.text());
                    //Compute Raw IDF Scores
                    double rawIdValue = Math.log((double) totalNumDocs / (double)termenum.docFreq());

                    TermDocs termdocs = indexReader.termDocs(term);

                    while ( termdocs.next() ) {

                        index_DoctermIndex[termdocs.doc()].add(termId);
                        //Normalize term weights
                        double termweight = ((double) termdocs.freq() * rawIdValue) / (tfidfscores[termdocs.doc()]);
                        index_DocTermWeight[termdocs.doc()].add(termweight);
                    }
                    termId++;
                }
            }


            //Initialzie doc index
            String[] terms = termNameList.toArray(new String[0]);
            docVectorStructures = new DocVectorStructure[totalNumDocs];


            for(int iterator1=0;iterator1 < totalNumDocs;iterator1++)
            {
                  int totalNumterms = index_DoctermIndex[iterator1].size();
                  HashMap<Integer,Double> hmp_termweightindex = new HashMap<Integer, Double>();
                  ArrayList<Integer> doc_term = index_DoctermIndex[iterator1];
                  ArrayList<Double> doc_termWeight = index_DocTermWeight[iterator1];
                  //Iterate all the terms
                  for(int iterator2=0;iterator2 < totalNumterms;iterator2++)
                  {
                      hmp_termweightindex.put(doc_term.get(iterator2),doc_termWeight.get(iterator2));
                  }
                  docVectorStructures[iterator1] = new DocVectorStructure(iterator1,hmp_termweightindex);
            }
            //FileOps.SaveObject(docVectorStructures,"FORWARDINDEX_DOCTERMWEIGHT.ser");
            //FileOps.SaveObject(terms,"FORWARDINDEX_TERMS.dat");


        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        return docVectorStructures;
    }

    public static String[] GetTermsNamesandIndex(IndexReader indexReader)
    {
        ArrayList<String> termNameList = new ArrayList<String>();
        try{
        TermEnum termenum = indexReader.terms();

        int termId= 0;
        //Iterate through all terms in document
        while(termenum.next())
        {
            Term term = termenum.term();
            //Add terms to list
            if (term.field().equals(CONTENTS))
            {
            termNameList.add(term.text());
            termId++;
            }
        }
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        return termNameList.toArray(new String[0]);
    }


    public static DocVectorStructure[] CreateForwardIndex(IndexReader indexReader)
    {
        try{
        TermEnum t = indexReader.terms();
        while(t.next())
        {

        }
        }
        catch (Exception ex)
        {

        }
        return null;
    }
}
