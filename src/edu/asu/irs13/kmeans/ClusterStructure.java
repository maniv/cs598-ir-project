package edu.asu.irs13.kmeans;

import java.util.HashSet;


public class ClusterStructure {

    //Initialize the cluster structure holding list of its document id's
    public HashSet<DocVectorStructure> set_doccluster = new HashSet<DocVectorStructure>();

    //Initialize centroid for the cluster - Single document vector structure
    public DocVectorStructure clustercentroid = new DocVectorStructure();

    //Structure Functions
    public void AddToCluster(DocVectorStructure docVectorStructure)
    {
        set_doccluster.add(docVectorStructure);
    }

    //Update Cluster Structure
    public void UpdateCentroids()
    {
            //Empty cluster if in case if its used
            //if(set_doccluster.size() > 0)
            //{
                clustercentroid.hmp_termweightindex.clear();
            //}

            //Iterate all documents in the clusters to get all their term weights
            for(DocVectorStructure docVectorStructure: set_doccluster)
            {
               clustercentroid.UpdateNewTermWeights(docVectorStructure);

            }
            clustercentroid.NormalizeWeights();
    }

    public void DeleteVector()
    {

    }




}
