package dsknn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KNN {

	public static void main(String[] args) {
		
		test1();
		test2();
		
	}

	private static void test2() {
		Partition pn1 = new Partition(0,0,0,0,2,2);
		Partition pn2 = new Partition(0,0,0,0,2,2);
		for(int i=0; i<9; i++){
			pn1.points.add( new Point(i,i));
			pn1.points.add( new Point(i,i));
		}
		
		Point p = new Point(10,10);
		ArrayList<Partition> plist = new ArrayList<Partition>();
		plist.add(pn1);
		plist.add(pn2);
		
		System.out.println(getKPoints(plist,p,10));
	}

	private static void test1() {
		Partition pn1 = new Partition(0,0,0,0,2,2);
		Partition pn2 = new Partition(0,0,0,0,2,2);
		for(int i=0; i<9; i++){
			pn1.points.add( new Point(i,i));
			pn1.points.add( new Point(i,0));
		}
		
		Point p = new Point(10,10);
		ArrayList<Partition> plist = new ArrayList<Partition>();
		plist.add(pn1);
		plist.add(pn2);
		
		System.out.println(getKPoints(plist,p,16));
	}

	/**
	 * get the kth point among the points in the partititon list just using
	 * sort!!
	 * 
	 * @param guessList
	 * @param k
	 * @return
	 */
	public static List<Point> getKPoints(ArrayList<Partition> plist, final Point focusPt,
			int k) {
		if(plist == null){
//			NaiveMain.errprintln("null list??!!");
			System.err.println("null list??!!");
			return null;
		}
		ArrayList<Point> allpt = new ArrayList<Point>();
		for (int i = 0; i < plist.size(); i++) {
			Partition p = plist.get(i);
			allpt.addAll(p.points);
		}

		// sort
		Collections.sort(allpt, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double dist1 = Tools.dist(focusPt.x, focusPt.y, o1.x, o1.y);
				double dist2 = Tools.dist(focusPt.x, focusPt.y, o2.x, o2.y);
				if (dist1 == dist2) {
					return 0;
				} else {
					return dist1 < dist2 ? -1 : 1;
				}
			}
		});

//		return allpt.get(k-1);
		return allpt.subList(0, k);
	}
}
