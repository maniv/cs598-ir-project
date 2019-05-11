package edu.asu.irs13.docir;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 2/7/13
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClustureResults {
    public Integer docid;
    public Double simscore;
    public Double distance;

    public ClustureResults(Integer docid,Double simscore,Double distance)
    {
        this.docid = docid;
        this.simscore = simscore;
        this.distance = distance;
    }
}

