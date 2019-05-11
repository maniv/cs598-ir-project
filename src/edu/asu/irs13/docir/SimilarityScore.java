package edu.asu.irs13.docir;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 2/7/13
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimilarityScore {
    public Integer docid;
    public Double simscore;

    public SimilarityScore(Integer docid,Double simscore)
    {
        this.docid = docid;
        this.simscore = simscore;
    }
}

