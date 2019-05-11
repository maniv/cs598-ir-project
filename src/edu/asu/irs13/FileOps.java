package edu.asu.irs13;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 1/31/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileOps {

    public static ObjectInput ReadObjectFromFile(String filepath)
    {
        //InputStream file = new FileInputStream( "docnorm.dat" );
        ObjectInput input = null;
        try {
            InputStream file = new FileInputStream(filepath);
            InputStream buffer = new BufferedInputStream( file );
            input = new ObjectInputStream( buffer );
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return input;
    }

    public static BufferedWriter GetFileWriterObject(String filepath)
    {
        BufferedWriter out = null;
        try {
            FileWriter fstream = new FileWriter(filepath);
             out = new BufferedWriter(fstream);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return out;
    }

    public static void SaveObject(Object obj,String filepath)
    {
        OutputStream file = null;
        ObjectOutput output = null;
        try {
            file = new FileOutputStream(filepath);
            OutputStream buffer = new BufferedOutputStream( file );
            output = new ObjectOutputStream( buffer );
            output.writeObject(obj);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


     public void PrintConsole(String s)
     {
         System.out.println(s);
     }

    public static double GetMaxValue(Double [] values) {
        double max = values[0];
        for (int iterator = 0; iterator < values.length; iterator++) {
            if (values[iterator] > max) {
                max = values[iterator];
            }
        }
        return max;
    }

    public static double GetMinValue(Double [] values) {
        double min = values[0];
        for (int iterator = 0; iterator < values.length; iterator++) {
            if (values[iterator] < min) {
                min = values[iterator];
            }
        }
        return min;
    }

    public static double GetMaxValue(double  [] values) {
        double max = values[0];
        for (int iterator = 0; iterator < values.length; iterator++) {
            if (values[iterator] > max) {
                max = values[iterator];
            }
        }
        return max;
    }

    public static double GetMinValue(double [] values) {
        double min = values[0];
        for (int iterator = 0; iterator < values.length; iterator++) {
            if (values[iterator] < min) {
                min = values[iterator];
            }
        }
        return min;
    }

    public static void PrintPageRankScores(double[] pagerankscores)
    {
        try{

            BufferedWriter out= FileOps.GetFileWriterObject("pagerankscore.txt");
            for(double item : pagerankscores)
            {
                out.write(String.valueOf(item));
                out.newLine();
            }
            out.flush();
            out.close();

        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }
    }

}
