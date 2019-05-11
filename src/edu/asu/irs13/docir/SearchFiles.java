package edu.asu.irs13.docir;

import edu.asu.irs13.Display;
import edu.asu.irs13.FileOps;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.File;
import java.util.*;

public class SearchFiles {
	public static void main(String[] args) throws Exception
	{
		// Initilaize Inverted Index Reader
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));
        //DocMagnitude.ConstructTFIDFVSM(indexReader);
        //DocMagnitude.ConstructTFIDFVSM(indexReader);
        //HashMap<Integer,HashMap<String,Double>> hm_tfidf_docvsm = (HashMap<Integer, HashMap<String, Double>>) FileOps.ReadObjectFromFile("TFIDFVSM2013.dat").readObject();
        //DocMagnitude.CalculateIDFDocNorm(hm_tfidf_docvsm);
        //TermUtility.LowestIDFOccurance(indexReader);

        Scanner sc = new Scanner(System.in);
        String str = "";
        System.out.print("Enter '1' to do TF search and '2' for IDF search and '3' to quit:");
        while(!(str = sc.nextLine()).equals("3"))
        {
            if (str.equals("1")) {
                Scanner scn = new Scanner(System.in);
                HashMap<Integer, Double> lhmp_tf_docnormvalues = (HashMap<Integer, Double>) FileOps.ReadObjectFromFile("tf-docmagnitude-2013.dat").readObject();
                System.out.print("query> ");

                long startTime = System.currentTimeMillis();

                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");
                Display.Result(QueryProcessor.TFCompute(indexReader, terms, lhmp_tf_docnormvalues), indexReader);

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Result fetched in:" + elapsedTime + "milliseconds");

            } else if (str.equals("2")) {
                Scanner scn = new Scanner(System.in);
                HashMap<Integer, Double> lhmp_idf_docnormvalues = (HashMap<Integer, Double>) FileOps.ReadObjectFromFile("tfidf-docmagnitude-2013.dat").readObject();
                System.out.print("query> ");

                long startTime = System.currentTimeMillis();

                String inputquery = scn.nextLine();
                String[] terms = inputquery.split("\\s+");
                Display.Result(QueryProcessor.IDFCompute(indexReader, terms, lhmp_idf_docnormvalues), indexReader);

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Result fetched in:" + elapsedTime + "milliseconds");
            }
            System.out.print("Enter '1' to do TF search and '2' for IDF search and '3' to quit:");
        }

        System.out.println("Run Successfully");
    }

    /*public static Integer[] TFIDFSearchResults(IndexReader indexReader)
    {
        Integer[] Baseset = new Integer[10];
     try{
        Scanner scn = new Scanner(System.in);
        HashMap<Integer, Double> lhmp_idf_docnormvalues = (HashMap<Integer, Double>) FileOps.ReadObjectFromFile("tfidf-docmagnitude-2013.dat").readObject();
        System.out.print("query> ");

        String inputquery = scn.nextLine();
        String[] terms = inputquery.split("\\s+");
        Baseset = Display.GetTFIDFResultDocIds(QueryProcessor.IDFCompute(indexReader, terms, lhmp_idf_docnormvalues), indexReader);
        }
     catch(Exception ex)
        {

        }
        return Baseset;
    }*/
}
