package edu.asu.irs13;
import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * Created with IntelliJ IDEA.
 * User: mvijaya2
 * Date: 2/7/13
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
        private int bpqMaxSize;
        Comparator<T> bpqComparator;
        public BoundedPriorityQueue() {
            super();
        }

        public BoundedPriorityQueue(int maxSize) {
            super(maxSize);
            bpqMaxSize = maxSize;
        }

        public BoundedPriorityQueue(int maxSize, Comparator<T> comparator) {
            super(maxSize, comparator);
            bpqComparator = comparator;
            bpqMaxSize = maxSize;
        }

        public boolean add(T newElement) {
            if(size() < bpqMaxSize)
            {
                return super.add(newElement);
            }
            else
            {
                if(bpqComparator.compare(peek(), newElement) < 0) {
                    remove();
                    return super.add(newElement);
                }
            }
            return true;
        }
}
