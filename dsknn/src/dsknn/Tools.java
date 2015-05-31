package dsknn;

public class Tools {

	public static void main(String[] args) {
		System.out.println( dist(1,1,2,2));
	}
	
	public static double dist(double x1,double y1, double x2, double y2){
		return Math.sqrt( Math.pow( (x1-x2), 2) + Math.pow( (y1-y2), 2) );
	}

	public static double dist(Point p1, Point focusPt) {
		return dist(p1.x,p1.y,focusPt.x,focusPt.y);
	}
}
