package edu.asu.irs13.snippet;

import org.apache.lucene.index.IndexReader;

public class Snippet {
    public static void DisplaySnippet(IndexReader indexReader, int docid)
    {
        try
        {
            String d_url = indexReader.document(docid).getFieldable("path").stringValue();

        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }

    }
}
