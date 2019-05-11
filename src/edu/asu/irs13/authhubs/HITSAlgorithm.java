package edu.asu.irs13.authhubs;

import edu.asu.irs13.Display;
import edu.asu.irs13.LinkAnalysis;
import edu.asu.irs13.BoundedPriorityQueue;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Manikandan
 * Date: 3/4/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class HITSAlgorithm {

    public static final int TOP_K_DOC=10;
    public static final double CONV_THRESHOLD = 0.00000001;

    //Compute Authority-Hub Score
    public BoundedPriorityQueue<AHScores> AuthHubCalculation(Integer[] rootset,LinkAnalysis linkAnalysis)
    {
          return  GetAuthHubScores(GetBaseSetforAH(rootset, linkAnalysis));
    }

    //Construct the Focused Sub-graph based on TF-IDF scores
    //Input: Array containing rootset values
    //Output: A&HResult Object containg the Authority matrix and Hub matrix and corresponding documentid
    public AHFocusedSubGraph GetBaseSetforAH(Integer[] rootset, LinkAnalysis linkAnalysis)
    {
        //Initialize pointer for each docid's
        Integer docidindex = 0;
        //Table stores docid as key and docid position for chekcsum
        HashMap<Integer,Integer> hmp_docandindexes = new HashMap<Integer, Integer>();
        //Final List of Documents
        Integer[] docidset;
        //Matrix to store authorities and hubs
        int[][] rootsetauths = new int[rootset.length][], rootsethubs = new int[rootset.length][];



        //Iterate through documents in rootset and get its outdegree and indegree nodes
        for(int docidnode : rootset)
        {
            hmp_docandindexes.put(docidnode, docidindex);
            //Collect the out-degree links
            rootsetauths[docidindex] = linkAnalysis.getLinks(docidnode);
            //Collect the in-degree links
            rootsethubs[docidindex] = linkAnalysis.getCitations(docidnode);
            docidindex++;
        }

        //Store list of all document id's in a hashmap and their indexes

        for( int[] rootsetauth : rootsetauths)
        {
            for( int docidnode : rootsetauth)
            {
                if(!hmp_docandindexes.containsKey(docidnode))
                {
                    hmp_docandindexes.put(docidnode,docidindex++);
                }
            }
        }

        for( int[] rootsethub : rootsethubs)
        {
            for( int docidnode : rootsethub)
            {
                if(!hmp_docandindexes.containsKey(docidnode))
                {
                    hmp_docandindexes.put(docidnode,docidindex++);
                }
            }
        }
       //Store all unique Baseset nodes of docid's
       docidset =  hmp_docandindexes.keySet().toArray( new Integer[hmp_docandindexes.size()]);

       //Get all indexes corresponding to baseset for authorities and hubs  seperately
        Integer[][] basesetauths = new Integer[docidindex][], basesethubs = new Integer[docidindex][];
        //Iterate to get all initial documents in rootset
        for(int initializer = 0; initializer < rootset.length; initializer++ )
        {
             basesetauths[initializer] = ParseRootDocids(rootsetauths[initializer], hmp_docandindexes);
             basesethubs[initializer] = ParseRootDocids(rootsethubs[initializer], hmp_docandindexes);
        }

        //Iterate to get the remaining documents in baseset that are not in rootset
        for(int initializer = rootset.length; initializer < docidindex; initializer++ )
        {
            basesetauths[initializer] = ParseRefDocids(linkAnalysis.getLinks(docidset[initializer]), hmp_docandindexes);
            basesethubs[initializer] = ParseRefDocids(linkAnalysis.getLinks(docidset[initializer]), hmp_docandindexes);
        }
        //Store the hashmap containing the documentid as key and index as value, set fo authority documenst indexes, set of hub document index in a Class and return
        return new AHFocusedSubGraph(basesetauths,basesethubs,docidset);
    }

    public Integer[] ParseRootDocids(int[] doclinklist,HashMap<Integer,Integer> hmp_docandindexes)
    {
        ArrayList<Integer> docindexes = new ArrayList<Integer>();

        for(int doc : doclinklist)
        {
            docindexes.add(hmp_docandindexes.get(doc));
        }

        return docindexes.toArray(new Integer[docindexes.size()]);
    }

    public Integer[] ParseRefDocids(int[] doclinklist, HashMap<Integer, Integer> hmp_docandindexes)
    {
        ArrayList<Integer> docindexes = new ArrayList<Integer>();

        for(int doc : doclinklist)
        {
            if(hmp_docandindexes.containsKey(doc))
            {
                docindexes.add(hmp_docandindexes.get(doc));
            }
        }

        return docindexes.toArray(new Integer[docindexes.size()]);
    }

    public BoundedPriorityQueue<AHScores> GetAuthHubScores(AHFocusedSubGraph ahFocusedSubGraph)
    {
        //Parse and get the required variables from the result class object
        Integer[][] basesetauths = ahFocusedSubGraph.basesetauths, basesethubs = ahFocusedSubGraph.basesethubs;
        Integer[] docidset =  ahFocusedSubGraph.docidset;

        //Initialize authority vector,hub vector and temperory vector to store old values
        Double[] vector_auth = new Double[basesetauths.length];
        Double[] vector_hub = new Double[basesethubs.length];
        Double[] vector_previous = new Double[basesetauths.length];
        InitializeZero(vector_previous);


        //Let Z denote the vector (1,1,1,1, ...1) belongs to R^n
        //Initialize A_Zero & H_Zero to one

        for(int initializer=0; initializer < basesetauths.length; initializer++)
        {
            vector_auth[initializer] = vector_hub[initializer] =1.0;
        }

        //Authorities = A^T . H0 && Hubs = A . A1

        vector_auth = ComputeDotProductofAdjMatrixandVec(basesetauths, vector_auth);
        vector_hub = ComputeDotProductofAdjMatrixandVec(basesethubs, vector_hub);

        //K-Iterations
        boolean is_auth_converge = false , is_hub_converge = false;
        Integer count_iterations=0;

        //Perform Value Iterations to determine when the score convergess for teh Markov Model.
        while (!is_auth_converge || !is_hub_converge )
        {
            //Authorities Iterations
            vector_previous = vector_auth;
            vector_auth = ComputeDotProductofAdjMatrixandVec(basesetauths,vector_auth);
            if(!is_auth_converge)
            {
                is_auth_converge = CheckConvergence(vector_previous,vector_auth);
            }

            //Hub Iterations
            vector_previous = vector_hub;
            vector_hub = ComputeDotProductofAdjMatrixandVec(basesethubs,vector_hub);
            if(!is_hub_converge)
            {
                is_hub_converge = CheckConvergence(vector_previous,vector_hub);
            }
            count_iterations++;
        }

        System.out.println("Converges @" + count_iterations);

        return FilterTopKDocuments(vector_auth,vector_hub,docidset);

    }

    public Double[] ComputeDotProductofAdjMatrixandVec(Integer[][] baseset,Double[] vector)
    {
        Double[] computedvector = new Double[baseset.length];
        computedvector = InitializeZero(computedvector);

        for(int initializer=0;initializer < baseset.length; initializer++)
        {
            for(int doc : baseset[initializer])
            {
                computedvector[initializer] += vector[doc];
            }
        }
        return NormalizeValues(computedvector);
    }

    public Double[] NormalizeValues(Double[] computedvector)
    {
        Double normalisedvector = 0.0;
        for(Double vec : computedvector)
        {
           normalisedvector += Math.pow(vec,2);
        }
        normalisedvector = Math.sqrt(normalisedvector);
        //Test-Case checksum
        if(normalisedvector.equals(0)){
            return computedvector;
        }
        else{
             for (int initializer =0; initializer < computedvector.length ; initializer++)
             {
                 computedvector[initializer] = computedvector[initializer] / normalisedvector;
             }
        }
        return computedvector;
    }

    public boolean CheckConvergence(Double[] vector_previous, Double[] vector_computed)
    {
        Double valueiteration = 0.0;
        for(int initializer=0; initializer < vector_previous.length; initializer++)
        {
            valueiteration += vector_previous[initializer] - vector_computed[initializer];
        }

        if(Math.abs(valueiteration) <= CONV_THRESHOLD)
        {
            return true;
        }
        else{
            return false;
        }
        //return Arrays.deepEquals(vector_previous,vector_computed);
    }

    public BoundedPriorityQueue<AHScores> FilterTopKDocuments(Double[] vector_auth, Double[] vector_hub,Integer[] docidset)
    {
        BoundedPriorityQueue<AHScores> doc_authhscores = new BoundedPriorityQueue<AHScores>(TOP_K_DOC,new AHScoreComparator());
        BoundedPriorityQueue<AHScores> doc_hubhscores = new BoundedPriorityQueue<AHScores>(TOP_K_DOC,new AHScoreComparator());
        try{
        IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("index")));

        for(int initializer =0; initializer < vector_auth.length; initializer++)
        {
            doc_authhscores.add(new AHScores(docidset[initializer],vector_auth[initializer]));
            doc_hubhscores.add(new AHScores(docidset[initializer],vector_hub[initializer]));
        }
        Display.DisplayAuthHUbResult(doc_authhscores,indexReader);
        Display.DisplayAuthHUbResult(doc_hubhscores,indexReader);

        }
        catch(Exception ex)
        {

        }
        return doc_authhscores;
    }

    public Double[] InitializeZero(Double[] doublearray)
    {
        for(int initializer=0; initializer < doublearray.length; initializer++)
        {
            doublearray[initializer] = 0.0;
        }
        return doublearray;
    }


}
