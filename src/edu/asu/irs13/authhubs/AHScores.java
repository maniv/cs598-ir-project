package edu.asu.irs13.authhubs;

/**
 * Created with IntelliJ IDEA.
 * User: Manikandan
 * Date: 3/8/13
 * Time: 2:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class AHScores {
    public Integer docid;
    public Double score;

    AHScores(Integer docid,Double score)
    {
        this.docid = docid;
        this.score = score;
    }
}
