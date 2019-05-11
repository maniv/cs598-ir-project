package edu.asu.irs13.kmeans;

import edu.asu.irs13.BoundedPriorityQueue;
import edu.asu.irs13.docir.SimilarityScore;
import edu.asu.irs13.docir.SimlarityScoreComparator;

import java.util.*;


public class ClusterUtil {

    public static DocVectorStructure[] GetKDocVectors(DocVectorStructure[] docVectorStructures,Integer[] kdocidstocluster)
    {
        DocVectorStructure[] kDocVectorStructures = new DocVectorStructure[kdocidstocluster.length];
        int count=0;
        for(int docid: kdocidstocluster)
        {
            kDocVectorStructures[count] = docVectorStructures[docid];
            count++;
        }
        return kDocVectorStructures;
    }


    public static DocVectorStructure[] GetRandomInitialCentroidsDocSet(DocVectorStructure[] kDocVectorStructures, int noOfClusters, int noDocsClustered )
    {
        HashSet<DocVectorStructure> initialCCentroidCluster = new HashSet<DocVectorStructure>(noOfClusters);
         //Initialize Random Function
        Random random = new Random();
        // Pick N centroid for the cluster
        System.out.println("Initial Centroids picked:");
       for(int iterator=0;iterator < noOfClusters;iterator++)
        {
            int randomnumber;
            do {
                randomnumber = random.nextInt(noDocsClustered);
            }while (initialCCentroidCluster.contains(kDocVectorStructures[randomnumber]));
            initialCCentroidCluster.add(kDocVectorStructures[randomnumber]);
            System.out.println(kDocVectorStructures[randomnumber].docidindex);
        }
        System.out.println("-----------------------------------------------");

        /*for(DocVectorStructure ds :kDocVectorStructures)
        {
            if(ds.docidindex == 23365)
            {
                initialCCentroidCluster.add(ds);
                System.out.println(ds.docidindex);
            }
            else if(ds.docidindex ==23564)
            {
                initialCCentroidCluster.add(ds);
                System.out.println(ds.docidindex);
            }
            else if(ds.docidindex == 726)
            {
                initialCCentroidCluster.add(ds);
                System.out.println(ds.docidindex);
            }
        } */


        return initialCCentroidCluster.toArray(new DocVectorStructure[initialCCentroidCluster.size()]);
    }

    public static void DisplayClusters(ClusterStructure[] clustercollection,HashMap<Integer,Double> hmp_kDocsSimToClusters,int topKResultclusters,String[] termNames,int clusterSummaryLength, int docSummarylenth)
    {
         //Iterate through all clusters
        HashMap<Integer,Double> hmp_alldocdistance = new HashMap<Integer, Double>();
         for (int iterator=0; iterator < clustercollection.length;iterator++)
         {
            System.out.println("List of clusters:");
             BoundedPriorityQueue<SimilarityScore> bqrankedclusters = new BoundedPriorityQueue<SimilarityScore>(topKResultclusters,new SimlarityScoreComparator());
              HashMap<Integer,HashMap<Integer,Double>> hmp_doctermsummary = new HashMap<Integer, HashMap<Integer, Double>>();
              HashMap<Integer,Double> hmp_docdistance = new HashMap<Integer, Double>();
             for(DocVectorStructure docVectorStructure:clustercollection[iterator].set_doccluster)
             {
                 bqrankedclusters.add(new SimilarityScore(docVectorStructure.docidindex,hmp_kDocsSimToClusters.get(docVectorStructure.docidindex)));
                 hmp_doctermsummary.put(docVectorStructure.docidindex,docVectorStructure.hmp_termweightindex);
                 hmp_docdistance.put(docVectorStructure.docidindex,docVectorStructure.distanceToCentroid);
                 hmp_alldocdistance.put(docVectorStructure.docidindex,docVectorStructure.distanceToCentroid);
             }

             ArrayList<Double> sortedscore = new ArrayList<Double>();
             HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();

             while(!bqrankedclusters.isEmpty())
             {
                 SimilarityScore obj = (SimilarityScore) bqrankedclusters.poll();
                 rankeddoc.put(obj.docid,obj.simscore);
                 sortedscore.add(obj.simscore);
             }
             Collections.sort(sortedscore);
             System.out.println("Cluster :" + (iterator + 1));
             DisplaySummary(GetSummaryForClusters(clustercollection[iterator].clustercentroid.hmp_termweightindex, termNames, clusterSummaryLength));

             System.out.println();
             double sumdistance =0.0;
             double denominator =0.0;
             for(int i=(sortedscore.size() -1); i>=0 ; i--)
             {
                 for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
                 {
                     if (docid.getValue() == sortedscore.get(i))
                     {
                        // String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                         //System.out.println("[" + docid.getKey()+ "] "+ "    Score:" + docid.getValue() + "  Url:" + d_url);
                         System.out.println("[" + docid.getKey()+ "] "+ "    Score:" + docid.getValue() + " " + "Distance:" + hmp_docdistance.get(docid.getKey()));
                         DisplaySummary(GetSummaryForClusters(hmp_doctermsummary.get(docid.getKey()),termNames,docSummarylenth));
                         sumdistance +=hmp_docdistance.get(docid.getKey());
                         denominator++;
                     }
                 }

             }
             //System.out.println(denominator);
             System.out.println("Avg Distance:" + sumdistance/denominator);
             System.out.println("==================================================");
         }
        DisplayClusterAverage(hmp_alldocdistance);
    }

    public static void DisplayClusterAverage( HashMap<Integer,Double> hmp_docdistance )
    {
        double numerator = 0.0;
        double denominator = (double) hmp_docdistance.size();
        System.out.println(denominator);
        for(Map.Entry<Integer,Double> termWt : hmp_docdistance.entrySet())
        {
            numerator =+termWt.getValue();
        }
        System.out.println("Avg distance for the complete cluster:" + numerator/denominator);
    }

    public static String[] GetSummaryForClusters( HashMap<Integer,Double> hmp_termweight,String[] index_termNames,int summarylength)
    {
        ArrayList<String> querynames = new ArrayList<String>();
        Double[] termwights=  hmp_termweight.values().toArray(new Double[hmp_termweight.size()]);
        Arrays.sort(termwights, Collections.reverseOrder());

        for(int iterator=0; iterator < summarylength ; iterator++)
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

    public static void DisplaySummary(String[] summarykeywords)
    {
        System.out.println("Summary:");
        for(String str : summarykeywords)
        {
            System.out.print(" " + '#' + str + ',' + " ");
        }
        System.out.println();
    }




}
