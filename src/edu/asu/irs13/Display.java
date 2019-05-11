package edu.asu.irs13;


import edu.asu.irs13.authhubs.AHScores;
import edu.asu.irs13.docir.SimilarityScore;
import edu.asu.irs13.pagerank.PRSimScores;
import org.apache.lucene.index.IndexReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 2/5/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Display {

    public static void Result(BoundedPriorityQueue bpqrankeddoc,IndexReader indexReader)
    {
        try{

            ArrayList<Double> sortedscore = new ArrayList<Double>();
            HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();

            while(!bpqrankeddoc.isEmpty())
            {
                SimilarityScore obj = (SimilarityScore) bpqrankeddoc.poll();
                rankeddoc.put(obj.docid,obj.simscore);
                sortedscore.add(obj.simscore);
            }
            Collections.sort(sortedscore);

            for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
                    for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
                    {
                        if (docid.getValue() == sortedscore.get(i))
                        {
                            //String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue().replace("%%", "/");
                            String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                            System.out.println("[" + docid.getKey()+ "] " + docid.getValue() + "Url" + d_url);
                        }
                    }
            }

           /* Arrays.sort(sortedscore, Collections.reverseOrder());
            for(int i=0; i< sortedscore.length; i++)
            {
                String d_url = indexReader.document(rankeddoc.get(sortedscore[i])).getFieldable("path").stringValue().replace("%%", "/");
                System.out.println("[" + rankeddoc.get(sortedscore[i]) + "] " + d_url);
            }*/
            /*for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
               //String d_url = indexReader.document(bimaprankeddocclone.inverse().get(sortedscore.get(i))).getFieldable("path").stringValue().replace("%%", "/");
                String d_url = indexReader.document(1).getFieldable("path").stringValue().replace("%%", "/");
                System.out.println("[" + rankeddoc.get(sortedscore.get(i)) + "] ");
            }*/
       }
       catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void PrintDocNormMagnitudeVector()
    {
        try{
            LinkedHashMap<Integer,Double> lhmp_docnormvalues = (LinkedHashMap<Integer,Double>) FileOps.ReadObjectFromFile("docnormvalues.dat").readObject();
            BufferedWriter out = FileOps.GetFileWriterObject("Docmagnitude.txt");
            for (Map.Entry<Integer, Double> docnormentry : lhmp_docnormvalues.entrySet())
            {
                System.out.println("Doc #:" +  docnormentry.getKey() + "    " + "Magnitude Value:" + docnormentry.getValue());
                out.write("Doc #:" +  docnormentry.getKey() + "    " + "Magnitude Value:" + docnormentry.getValue());
                out.newLine();
            }
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void PrintDocumentVsm(LinkedHashMap<Integer,LinkedHashMap<String,Integer>> lhmap_docvsm) {

        BufferedWriter out = FileOps.GetFileWriterObject("tf.txt");
        try{
            for (Map.Entry<Integer, LinkedHashMap<String,Integer>> entry : lhmap_docvsm.entrySet())
            {
                System.out.println("Document Id:" +  entry.getKey());
                LinkedHashMap<String,Integer> subvector = entry.getValue();
                for (Map.Entry<String,Integer> subentry : subvector.entrySet())
                {
                    System.out.println("Term:" +  subentry.getKey() + "    " + "Frequency:" + subentry.getValue());
                    out.write("Term:" +  subentry.getKey() + "    " + "Frequency:" + subentry.getValue());
                    out.newLine();
                }
                System.out.println("-----------------------------------------------------------------------------");
                out.write("-----------------------------------------------------------------------------");
                out.newLine();
            }
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static Integer[] GetTFIDFResultDocIds(BoundedPriorityQueue bpqrankeddoc,IndexReader indexReader)
    {
        ArrayList<Integer> top10rankeddocs = new ArrayList<Integer>();
        ArrayList<Double> sortedscore = new ArrayList<Double>();
        HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();


            while(!bpqrankeddoc.isEmpty())
            {
                SimilarityScore obj = (SimilarityScore) bpqrankeddoc.poll();
                rankeddoc.put(obj.docid,obj.simscore);
                sortedscore.add(obj.simscore);
            }
            Collections.sort(sortedscore);

            for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
                for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
                {
                    if (docid.getValue() == sortedscore.get(i))
                    {
                        //String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue().replace("%%", "/");
                       // String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                        //System.out.println("[" + docid.getKey()+ "] " + d_url);
                        top10rankeddocs.add(docid.getKey());
                    }
                }
            }
        return top10rankeddocs.toArray(new Integer[top10rankeddocs.size()]);
    }

    public static void DisplayAuthHUbResult(BoundedPriorityQueue bpqrankeddoc,IndexReader indexReader)
    {
        try{
            ArrayList<Double> sortedscore = new ArrayList<Double>();
            HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();

            while(!bpqrankeddoc.isEmpty())
            {
                AHScores obj = (AHScores) bpqrankeddoc.poll();
                rankeddoc.put(obj.docid,obj.score);
                sortedscore.add(obj.score);
            }
            Collections.sort(sortedscore);

            for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
              for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
              {
                    if (docid.getValue() == sortedscore.get(i))
                    {
                        String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                        System.out.println("[" + docid.getKey()+ "] "+ "    Score:" + docid.getValue() + "  Url:" + d_url);
                    }
               }
            }
            System.out.println("-----------------------Hub---------------------");
            /*String d_url = indexReader.document(25053).getFieldable("path").stringValue();
            System.out.println("url" + d_url);*/
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }
    }

    public static HashMap<Integer,Double> GetTopKNormalisedTFIDFScoresAndDocId(BoundedPriorityQueue bpqrankeddoc,IndexReader indexReader)
    {
        HashMap<Integer,Double> top10docidandscores = new HashMap<Integer, Double>();
        ArrayList<Double> sortedscore = new ArrayList<Double>();
        HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();

        while(!bpqrankeddoc.isEmpty())
        {
            SimilarityScore obj = (SimilarityScore) bpqrankeddoc.poll();
            rankeddoc.put(obj.docid,obj.simscore);
            sortedscore.add(obj.simscore);
        }
        double Maxvalue = FileOps.GetMaxValue(sortedscore.toArray(new Double[sortedscore.size()]));

        //Normalize Scores
        for(int i=0;i<sortedscore.size();i++)
        {
            for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
            {
                if (docid.getValue() == sortedscore.get(i))
                {
                    //String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue().replace("%%", "/");
                    // String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                    //System.out.println("[" + docid.getKey()+ "] " + d_url);
                    double normalisedscore = docid.getValue() /Maxvalue;
                    top10docidandscores.put(docid.getKey(), normalisedscore);
                }
            }
        }
       return top10docidandscores;
    }

    public static void DisplayPagerankTFIDFResults(ArrayList<Double> sortedscore, HashMap<Integer,Double> pageandfinalscores,IndexReader indexReader)
    {
        try{
            for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
                for(Map.Entry<Integer,Double> docid : pageandfinalscores.entrySet())
                {
                    if (docid.getValue().equals(sortedscore.get(i)))
                    {
                        //String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue().replace("%%", "/");
                        String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                        System.out.println("[" + docid.getKey()+ "] " + d_url);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }

    }



    public static ArrayList<Double> NormalizeTFIDFScore(ArrayList<Double> sortedscore,double Max_Value)
    {
        ArrayList<Double> newsortedarray = new ArrayList<Double>();
        for(int iterator =0; iterator < sortedscore.size();iterator++)
           {
               double normalized = sortedscore.get(iterator) / Max_Value;
               newsortedarray.add(iterator,normalized);
           }
        return newsortedarray;
    }


    public static void DisplayPRSimScoreResults(BoundedPriorityQueue bpqrankeddoc,IndexReader indexReader)
    {
        try{
            ArrayList<Double> sortedscore = new ArrayList<Double>();
            HashMap<Integer,Double> rankeddoc = new HashMap<Integer, Double>();

            while(!bpqrankeddoc.isEmpty())
            {
                PRSimScores obj = (PRSimScores) bpqrankeddoc.poll();
                rankeddoc.put(obj.docid,obj.score);
                sortedscore.add(obj.score);
            }
            Collections.sort(sortedscore);

            for(int i=(sortedscore.size() -1); i>=0 ; i--)
            {
                for(Map.Entry<Integer,Double> docid : rankeddoc.entrySet())
                {
                    if (docid.getValue() == sortedscore.get(i))
                    {
                        String d_url = indexReader.document(docid.getKey()).getFieldable("path").stringValue();
                        System.out.println("[" + docid.getKey()+ "] "+ "    Score:" + docid.getValue() + "  Url:" + d_url);
                    }
                }
            }
            /*String d_url = indexReader.document(25053).getFieldable("path").stringValue();
            System.out.println("url" + d_url);*/
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }
    }
    
    public static void DisplayDocumentIds(Integer[] docids)
    {
       System.out.println("Total number of documents to be clustered:" + docids.length);
       for(int iterator =0;iterator <docids.length; iterator++)
       {
           System.out.print(docids[iterator] + " ");
       }
       System.out.println();
    }


}
