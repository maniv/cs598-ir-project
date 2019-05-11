package edu.asu.irs13.kmeans;

import edu.asu.irs13.Display;
import edu.asu.irs13.similarpages.Similarpages;
import org.apache.lucene.index.IndexReader;

import java.util.*;

public class Clustering {

    //Initialize default variables
    public static final int NO_DOCS_CLUSTERED=50;

    public static int NO_OF_CLUSTERS=3;

    public static final int TOP_K_RESULTCLUSTERS=5;

    public static final int CLUSTER_SUMMARY_LENGTH=10;

    public static final int INDIVI_DOC_SUMMARY_LENGTH=5;

    ClusterStructure[] clusterCollections;


   public void Kmeans(HashMap<Integer,Double> hmp_kDocsSimToClusters,DocVectorStructure[] docVectorStructures,String[] index_termNames)
   {



       //Get the collection of k document id's that rae to be clustered
       Integer[] kdocidstocluster = hmp_kDocsSimToClusters.keySet().toArray(new Integer[hmp_kDocsSimToClusters.size()]);

       //Get Docvectors for top k documents to cluster
       DocVectorStructure[] kDocVectorStructures = ClusterUtil.GetKDocVectors(docVectorStructures, kdocidstocluster);
       //Print list of k document id's:
       Display.DisplayDocumentIds(kdocidstocluster);


       //Initialize Cluster Structure
        clusterCollections = new ClusterStructure[NO_OF_CLUSTERS];

        for(int iterator=0;iterator < NO_OF_CLUSTERS;iterator++)
        {
            clusterCollections[iterator] = new ClusterStructure();
        }

       //Pick Initial centroid for the N clusters
       DocVectorStructure[] initialCentroidDocArray = ClusterUtil.GetRandomInitialCentroidsDocSet(kDocVectorStructures, NO_OF_CLUSTERS, NO_DOCS_CLUSTERED);

       //Update Initial Cluster - Assign initial centroids to clusters
       for(int iterator=0;iterator <NO_OF_CLUSTERS; iterator++)
       {
           initialCentroidDocArray[iterator].distanceToCentroid = 1.0;
           clusterCollections[iterator].set_doccluster.add(initialCentroidDocArray[iterator]);
       }


       //Update centroids for clusters that are picked
        RecomputeClusterCentroids();

        //Assign other documents to the newly formed cluster for iteration
       Set<DocVectorStructure> initialCentroidDocSet = new HashSet<DocVectorStructure>(Arrays.asList(initialCentroidDocArray));
      // int debugcount=0;
       for (DocVectorStructure docVectorStructure: kDocVectorStructures)
       {
            //Check documents other than the centroid
            if(!initialCentroidDocSet.contains(docVectorStructure))
            {
               // System.out.println(debugcount++);
                  ClusterDocumentsToCentroids(docVectorStructure);
            }
       }

       //Iterate untill clusters remain same

       int iterationCount = 1;
       do {
           System.out.println("Iteration:" + iterationCount);
          // ClusterUtil.DisplayClusters(clusterCollections,hmp_kDocsSimToClusters,TOP_K_RESULTCLUSTERS,index_termNames);
           RecomputeClusterCentroids();
           iterationCount++;
       }
       while (IfClustersConverge(ComputeAndMoveClusters())) ;

       System.out.println("Total Iterations to cluster: " + iterationCount);

       ClusterUtil.DisplayClusters(clusterCollections,hmp_kDocsSimToClusters,TOP_K_RESULTCLUSTERS,index_termNames,CLUSTER_SUMMARY_LENGTH,INDIVI_DOC_SUMMARY_LENGTH);


   }
    
   public ClusterStructure ClusterDocumentsToCentroids(DocVectorStructure docVectorStructure,ClusterStructure existingCluster)
   {
       ClusterStructure bestCluster = existingCluster;

       double maxDistance = docVectorStructure.ComputeDistance(bestCluster.clustercentroid);

       for(ClusterStructure cluster : clusterCollections)
       {
                if(!cluster.equals(bestCluster))
                {
                    double distanceFromCentroid = docVectorStructure.ComputeDistance(cluster.clustercentroid);
                    if(distanceFromCentroid > maxDistance)
                    {
                        maxDistance = distanceFromCentroid;
                        bestCluster = cluster;
                    }
                }
       }

       //update new centroid
       docVectorStructure.distanceToCentroid = maxDistance;
       bestCluster.AddToCluster(docVectorStructure);
       return bestCluster;

   }

    public ClusterStructure ClusterDocumentsToCentroids(DocVectorStructure docVectorStructure)
    {
        ClusterStructure bestCluster = clusterCollections[0];

        double maxDistance = docVectorStructure.ComputeDistance(bestCluster.clustercentroid);

        for(ClusterStructure cluster : clusterCollections)
        {
            //Check if not a empty cluster
            if(cluster!=null)
            {
                double distanceFromCentroid = docVectorStructure.ComputeDistance(cluster.clustercentroid);
                if(distanceFromCentroid > maxDistance)
                {
                    maxDistance = distanceFromCentroid;
                    bestCluster = cluster;
                }
            }
        }

        //update new centroid
        docVectorStructure.distanceToCentroid = maxDistance;
        bestCluster.AddToCluster(docVectorStructure);
        return bestCluster;

    }


    public HashSet<DocVectorStructure> ComputeAndMoveClusters()
    {
        HashSet<DocVectorStructure> reassignedDocList = new HashSet<DocVectorStructure>();

        for(ClusterStructure clusterStructure : clusterCollections)
        {
            for(DocVectorStructure docVectorStructure : clusterStructure.set_doccluster.toArray(new DocVectorStructure[0]))
            {
                if(!reassignedDocList.contains(docVectorStructure))
                {
                    ClusterStructure nearestCluster = ClusterDocumentsToCentroids(docVectorStructure,clusterStructure);
                    if(nearestCluster != clusterStructure)
                    {
                        clusterStructure.set_doccluster.remove(docVectorStructure);
                        reassignedDocList.add(docVectorStructure);
                    }
                }
            }
        }
        return reassignedDocList;
    }

    public boolean IfClustersConverge(HashSet<DocVectorStructure> reassignedDocList)
    {
         if(reassignedDocList.size() >0)
         {
             return true ;
         }
        else {
             return false;
         }
    }

    public void RecomputeClusterCentroids()
    {
        for(ClusterStructure clusterStructure : clusterCollections)
        {
            clusterStructure.UpdateCentroids();
        }
    }


    public void AddDocVectorsToCluster()
    {

    }

    public void KmeansWithSimilarPages(HashMap<Integer,Double> hmp_kDocsSimToClusters,DocVectorStructure[] docVectorStructures,String[] index_termNames,IndexReader indexReader,HashMap<Integer, Double> lhmp_idf_docnormvalues)
    {
        //Get the collection of k document id's that rae to be clustered
        Integer[] kdocidstocluster = hmp_kDocsSimToClusters.keySet().toArray(new Integer[hmp_kDocsSimToClusters.size()]);

        //Get Docvectors for top k documents to cluster
        DocVectorStructure[] kDocVectorStructures = ClusterUtil.GetKDocVectors(docVectorStructures, kdocidstocluster);
        //Print list of k document id's:
        Display.DisplayDocumentIds(kdocidstocluster);

        //Initialize Cluster Structure
        clusterCollections = new ClusterStructure[NO_OF_CLUSTERS];

        for(int iterator=0;iterator < NO_OF_CLUSTERS;iterator++)
        {
            clusterCollections[iterator] = new ClusterStructure();
        }

        //Pick Initial centroid for the N clusters
        DocVectorStructure[] initialCentroidDocArray = ClusterUtil.GetRandomInitialCentroidsDocSet(kDocVectorStructures, NO_OF_CLUSTERS, NO_DOCS_CLUSTERED);

        //Update Initial Cluster - Assign initial centroids to clusters
        for(int iterator=0;iterator <NO_OF_CLUSTERS; iterator++)
        {
            initialCentroidDocArray[iterator].distanceToCentroid = 1.0;
            clusterCollections[iterator].set_doccluster.add(initialCentroidDocArray[iterator]);
        }


        //Update centroids for clusters that are picked
        RecomputeClusterCentroids();

        //Assign other documents to the newly formed cluster for iteration
        Set<DocVectorStructure> initialCentroidDocSet = new HashSet<DocVectorStructure>(Arrays.asList(initialCentroidDocArray));
        for (DocVectorStructure docVectorStructure: kDocVectorStructures)
        {
            //Check documents other than the centroid
            if(!initialCentroidDocSet.contains(docVectorStructure))
            {
                ClusterDocumentsToCentroids(docVectorStructure);
            }
        }

        //Iterate untill clusters remain same

        int iterationCount = 0;
        do {
            RecomputeClusterCentroids();
            iterationCount++;
        }
        while (IfClustersConverge(ComputeAndMoveClusters())) ;

        System.out.println("Total Iterations to cluster: " + iterationCount);

        ClusterUtil.DisplayClusters(clusterCollections,hmp_kDocsSimToClusters,TOP_K_RESULTCLUSTERS,index_termNames,CLUSTER_SUMMARY_LENGTH,INDIVI_DOC_SUMMARY_LENGTH);


        Scanner scn = new Scanner(System.in);
        System.out.print("Enter the document number to get its similar pages> ");
        String inputquery = scn.nextLine();
        System.out.print("Enter K terms to be choosen:> ");
        String ksimilarterms = scn.nextLine();
        int docidtofindsimdocs = Integer.parseInt(inputquery);
        Similarpages.N0_SIMILAR_PAGES_KEYWORD = Integer.parseInt(ksimilarterms);
        Similarpages.DisplaySimilarPages(docidtofindsimdocs,docVectorStructures,index_termNames,indexReader,lhmp_idf_docnormvalues);


    }
    
}
