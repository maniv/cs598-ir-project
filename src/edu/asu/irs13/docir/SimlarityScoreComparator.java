package edu.asu.irs13.docir;

import java.util.Comparator;

public class  SimlarityScoreComparator implements Comparator<SimilarityScore>
{
    @Override
    public int compare(SimilarityScore simobjone,SimilarityScore simobjtwo) {
        return simobjone.simscore.compareTo(simobjtwo.simscore);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
