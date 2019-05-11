package edu.asu.irs13.pagerank;

import com.sun.javafx.collections.transformation.SortedList;
import edu.asu.irs13.FileOps;
import edu.asu.irs13.LinkAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Manikandan
 * Date: 3/4/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageRank {

      final int N_PAGES = LinkAnalysis.numDocs;
      final double C_FACTOR = 0.85;
      final double CONV_THRESHOLD = 0.00000001;

      public double[] ComputePageRank(LinkAnalysis linkAnalysis)
      {
           //Initialize totoal number of pages crawled

           //
           int  [][] adjacencymatrix = new int[N_PAGES][];
           double [] pagerankscore = new double[N_PAGES];
           //LinkedHashMap<Integer,Double> hmp_pagerankscore = new LinkedHashMap<Integer, Double>();

           //Construct Adjacency Matrix with pages and its outdegree nodes and Initialize default page rank values which is a stochastic matrix
           for(int initializer = 0; initializer<N_PAGES; initializer++ )
           {
               adjacencymatrix[initializer] = linkAnalysis.getLinks(initializer);
               //Ri matrix value 1/N
               pagerankscore[initializer] = (double)1.0/(double)N_PAGES;
              // hmp_pagerankscore.put(initializer,(1.0/N_PAGES));
           }

           boolean isConverged = false;
           int countiterations =0;
           // Calculate (M+Z)
           // Construct the transition matrix M (column normalised adjacency matrix) and add it to Z matrix if sink node exist (1/N)
          double [] temppagerankscore;
          do{
               temppagerankscore = new double[N_PAGES];
               double sumsinknodevalue = 0.0;
               //(1 -C)K Calculations
               double resetmatrixvector = (1 - C_FACTOR) / N_PAGES;


               //C(M+Z) Calculations
               for(int page_x=0; page_x < N_PAGES;page_x++)
               {
                   //If there is no sink node
                   int totaloutdegreenodes = adjacencymatrix[page_x].length;
                   if(totaloutdegreenodes !=0)
                   {
                       //For each page Y pointing to page X
                       for(int page_y : adjacencymatrix[page_x])
                       {
                           //Multiplying the damping fcator C with the Column Normalized vector of page that has outdegrees
                           temppagerankscore[page_y] += C_FACTOR *  pagerankscore[page_x] / totaloutdegreenodes ;
                       }
                   }
                   //If there is a sink node
                   else if(adjacencymatrix[page_x].length == 0)
                   {
                       // Multiply the damping factor with the vector of matrix M summed up with Z matrix which has 1/N values
                       //Summation with Z is not shown as we have initialized the default pagerankscore with 1/N values
                       sumsinknodevalue += (C_FACTOR /N_PAGES) * pagerankscore[page_x] ;
                   }
               }
               //Compute Page-Rank Formula M* = C(M+Z) + (1-C)K
               for(int page=0; page < N_PAGES; page++)
               {
                   temppagerankscore[page] += sumsinknodevalue + resetmatrixvector;
               }
               //Check if the values converged  for value iteration for MDP
               isConverged = CheckConvergence(pagerankscore,temppagerankscore);
               //Replace the old score with new one
               pagerankscore = temppagerankscore.clone();
               countiterations++;
           }
          while (!isConverged   || countiterations == 100);

           System.out.println("PR_Converges @" +countiterations);
          FileOps.SaveObject(pagerankscore,"PAGERANK_SCORES_ALLPAGES.dat");
          return pagerankscore;
          // double[] pagerankscoresforpages = NormalizepageRankScore(pagerankscore);

      }





    public boolean CheckConvergence(double [] oldpagerankscore, double [] pagerankscore)
    {
        double valueiteration = 0.0;
        for(int initializer=0; initializer < oldpagerankscore.length; initializer++)
        {
            //valueiteration += oldpagerankscore[initializer] - pagerankscore[initializer];
            if(Math.abs(pagerankscore[initializer] - oldpagerankscore[initializer])  > CONV_THRESHOLD)
            {
                return false;
            }
        }
        return true;

        /*if(Math.abs(valueiteration) <= CONV_THRESHOLD)
        {
            return true;
        }
        else{
            return false;
        }*/
    }

    public static double[] NormalizepageRankScore(double[] pagerankscores)
    {
        double PAGERANK_MAX_VALUE = FileOps.GetMaxValue(pagerankscores);
        for(int initializer=0; initializer < pagerankscores.length; initializer++)
        {
            pagerankscores[initializer]  = pagerankscores[initializer] / PAGERANK_MAX_VALUE;
        }
        return pagerankscores;
    }

    public static Double[] NormalizepageRankScore(Double[] pagerankscores)
    {
        double PAGERANK_MAX_VALUE = FileOps.GetMaxValue(pagerankscores);
        for(int initializer=0; initializer < pagerankscores.length; initializer++)
        {
            pagerankscores[initializer]  = pagerankscores[initializer] / PAGERANK_MAX_VALUE;
        }
        return pagerankscores;
    }

  /*  public HashMap<Integer,Double> ConstructPagerankHashTable(double[] pagerankscores)
    {
        HashMap<Integer,Double> hmp_pageranktable = new HashMap<Integer, Double>();
        for(int initializer=0; initializer < pagerankscores.length; initializer++)
        {
            hmp_pageranktable.put(initializer,pagerankscores[initializer]);
        }
        return hmp_pageranktable;
    }*/







}
