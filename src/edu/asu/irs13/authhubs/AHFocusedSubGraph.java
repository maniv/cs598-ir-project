package edu.asu.irs13.authhubs;

/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 3/6/13
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class AHFocusedSubGraph {

    Integer[][] basesetauths, basesethubs;
    Integer[] docidset;

    //Initialize with Constructor
    public AHFocusedSubGraph(Integer[][] baseauths,Integer[][] basehubs,Integer[] docset)
    {
         basesetauths =  baseauths;
         basesethubs = basehubs;
         docidset = docset;
    }
}
