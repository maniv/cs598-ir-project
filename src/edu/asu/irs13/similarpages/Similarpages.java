package edu.asu.irs13.similarpages;


import edu.asu.irs13.Display;
import edu.asu.irs13.docir.QueryProcessor;
import edu.asu.irs13.kmeans.Clustering;
import edu.asu.irs13.kmeans.DocVectorStructure;
import org.apache.lucene.index.IndexReader;

import java.util.*;

public class Similarpages {
    public static int N0_SIMILAR_PAGES_KEYWORD = 50;

      public static void DisplaySimilarPages(int docidtofindsimdocs,DocVectorStructure[] docVectorStructures,String[] index_termNames,IndexReader indexReader,HashMap<Integer,Double> lhmp_idf_docnormvalues)
      {
          //Get all possible terms of the document
          HashMap<Integer,Double> hmp_termweight = docVectorStructures[docidtofindsimdocs].hmp_termweightindex;
          String[] queryterms = GetQueryterms(hmp_termweight,index_termNames);
          HashMap<Integer,Double> hmp_kDocsSimToClusters = QueryProcessor.GetTopKIDFSimilarity(indexReader,queryterms,lhmp_idf_docnormvalues, Clustering.NO_DOCS_CLUSTERED);

          Clustering clustering = new Clustering();
          clustering.Kmeans(hmp_kDocsSimToClusters,docVectorStructures,index_termNames);
        // Display.Result(QueryProcessor.IDFCompute(indexReader, queryterms, lhmp_idf_docnormvalues), indexReader);
      }

      public static String[] GetQueryterms( HashMap<Integer,Double> hmp_termweight,String[] index_termNames)
      {
          ArrayList<String> querynames = new ArrayList<String>();
          Double[] termwights=  hmp_termweight.values().toArray(new Double[hmp_termweight.size()]);
          Arrays.sort(termwights, Collections.reverseOrder());

          for(int iterator=0; iterator < 50 ; iterator++)
          {
              for(Map.Entry<Integer,Double> termWt : hmp_termweight.entrySet())
              {
                  if(termwights[iterator]== termWt.getValue())
                  {
                      querynames.add(index_termNames[termWt.getKey()]);
                  }
              }
          }
          return querynames.toArray(new String[querynames.size()]);
      }




}
