package edu.asu.irs13.pagerank;

import edu.asu.irs13.authhubs.AHScores;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Manikandan
 * Date: 3/8/13
 * Time: 2:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class PRSimScoresComparator implements Comparator<PRSimScores> {


    @Override
    public int compare(PRSimScores o1, PRSimScores o2) {
        return o1.score.compareTo(o2.score);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
