package edu.asu.irs13.kmeans;

import java.util.HashMap;
import java.util.Map;


public class DocVectorStructure implements java.io.Serializable {

    //Default for a centroid
    //Document Vector structure holding doc id and score
    public HashMap<Integer,Double> hmp_termweightindex;
    public int docidindex = -1024;
    //Document distance from cluster
    public Double distanceToCentroid= 0.0;

    //Initialize document vector structure
    public DocVectorStructure()
    {
        hmp_termweightindex = new HashMap<Integer, Double>();
    }

    public DocVectorStructure(int docid,HashMap<Integer,Double> hmp_termweight)
    {
        docidindex = docid;
        hmp_termweightindex = hmp_termweight;
    }


    public void UpdateNewTermWeights(DocVectorStructure docVectorStructure)
    {
        //Iterate through the terms in the documents and update weights of centroids
        for(Map.Entry<Integer,Double> termWeight : docVectorStructure.hmp_termweightindex.entrySet())
        {
            double newWt=0.0;
            if(hmp_termweightindex.containsKey(termWeight.getKey()))
            {
                newWt =  hmp_termweightindex.get(termWeight.getKey()) + termWeight.getValue();
            }
            else if(!hmp_termweightindex.containsKey(termWeight.getKey()))
            {
                 newWt = termWeight.getValue();
            }
            hmp_termweightindex.put(termWeight.getKey(),newWt);
        }
    }

    public void NormalizeWeights()
    {
        double SumSquare = 0.0;

        for(Map.Entry<Integer,Double> termWtObj : hmp_termweightindex.entrySet())
        {
            SumSquare +=Math.pow(termWtObj.getValue(),2);
        }

        if(SumSquare !=0)
        {
            double normalizeValue = Math.sqrt(SumSquare);
            for(Map.Entry<Integer,Double> termWtObj : hmp_termweightindex.entrySet())
            {
                double normalizedtermWeight = termWtObj.getValue() / normalizeValue;
                hmp_termweightindex.put(termWtObj.getKey(),normalizedtermWeight);
            }
        }
    }


    //Using hashmap instead a matrix to avoid sparse matrix for documents
    //Compute dot product based on the documents with max terms else go with default
    public double ComputeDistance(DocVectorStructure docVectorStruct)
    {
        double distance = 0.0;
        try{
        HashMap<Integer,Double> minTermWts = null;
        HashMap<Integer,Double> maxTermWts = null;


        if(docVectorStruct.hmp_termweightindex.size() > hmp_termweightindex.size())
        {
            minTermWts = hmp_termweightindex;
            maxTermWts = docVectorStruct.hmp_termweightindex;
        }
        else
        {
            minTermWts = docVectorStruct.hmp_termweightindex;
            maxTermWts =  hmp_termweightindex;
        }
        //Compute Euclidean Distance
        /*double eucl_numerator = 0.0;
        double eucl_denominatorX = 0.0;
        double eucl_denominatorY = 0.0;*/
        if(minTermWts !=null && maxTermWts !=null)
        {
            for(Map.Entry<Integer,Double> minTermWeight : minTermWts.entrySet())
            {
                //Check with if both the documents have the same terms  then compute euclidean distance
                if(maxTermWts.containsKey(minTermWeight.getKey()))
                {
                    //Compute distance based on similairty
                    distance += maxTermWts.get(minTermWeight.getKey()) * minTermWeight.getValue();
                    //eucl_numerator += maxTermWts.get(minTermWeight.getKey()) * minTermWeight.getValue();
                    //eucl_denominatorX += Math.pow(minTermWeight.getValue(),2);
                    //eucl_denominatorY += Math.pow(maxTermWts.get(minTermWeight.getKey()),2);
                }
            }
        }
        //Euclidean Distance Formula
        //distance = eucl_numerator / Math.sqrt(eucl_denominatorX) * Math.sqrt(eucl_denominatorY);
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        return distance;
    }



}
