package dsknn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Intersect {

	public static void main(String[] args) {
		System.out.println(Tools.dist(0.5, 0.5, 2, 0));
		
		Partition pn = new Partition(0,0,0,0,2,2);
		pn.minDist = 0;
		
		//test minDist == 0;
		try {
			Point pt = new Point(1,1);
			System.out.println(calIntersect(pn,pt,1));
			System.out.println(calIntersect(pn,pt,1.2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Point pt = new Point(1.5,1.5);
			System.out.println(calIntersect(pn,pt,0.5));
			System.out.println(calIntersect(pn,pt,1));
			System.out.println(calIntersect(pn,pt,1.2));
			System.out.println(calIntersect(pn,pt,1.4));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Point pt = new Point(0.5,0.5);
			System.out.println(calIntersect(pn,pt,1.6));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//test minDist>0
		System.out.println("test minDist>0!!!");
		try {
			Point pt = new Point(3,1);
			pn.calMinDist(pt);
			System.out.println("minDist:"+pn.minDist);
			System.out.println(calIntersect(pn,pt,1));
			System.out.println(calIntersect(pn,pt,1.1));
			System.out.println(calIntersect(pn,pt,1.2));
			System.out.println(calIntersect(pn,pt,1.6));
			System.out.println(calIntersect(pn,pt,1.9));
			System.out.println(calIntersect(pn,pt,2.6));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Point pt = new Point( 3 , 0);
			System.out.println("focus point is "+pt);
			pn.calMinDist(pt);
			System.out.println("minDist:"+pn.minDist);
			System.out.println(calIntersect(pn,pt,1));
			System.out.println(calIntersect(pn,pt,1.1));
			System.out.println(calIntersect(pn,pt,1.2));
			System.out.println(calIntersect(pn,pt,1.6));
			System.out.println(calIntersect(pn,pt,1.9));
			System.out.println(calIntersect(pn,pt,2.6));
			System.out.println(calIntersect(pn,pt,3.1));
			System.out.println(calIntersect(pn,pt,3.2));
			System.out.println(calIntersect(pn,pt,3.2));
			System.out.println(calIntersect(pn,pt,3.5));
			System.out.println(calIntersect(pn,pt,3.6));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*************************************************************************************
	 *  for partitions in yellowlist, calc the intersect area:
	 * 	1  according to the # of extreme points that outter of the circle( dist > radius), we name it as #exptOut
	 * 				and the # of intersection points between circle and rectangle, we name it as intsecNum
	 * 
	 * ----------------------------------------------------------------------------
	 * 	2  if mindist==0( focus partition)
	 * 		2.1 #exptOut == 1: rectangle - triangle
	 * 		2.2 #exptOut == 2: 
	 * 			2.2.1 if intsecNum == 4 rectangle - 2 triangle
	 * 			2.2.2 if intsecNum == 2 trapezoid
	 * 		2.3 #exptOut == 3: (notice that we always has a trapezoid because the focus point is in the rectangle)
	 * 							1/4circle + 2 big rectangle - 1 small rectangle
	 * 		2.4 #exptOut == 4: if intsecNum = 0	 circle area
	 * 						   if intsecNum = 2  circle - 1 triangle
	 *  					   if intsecNum = 4  circle - 2 triangle
	 *  					   if intsecNum = 6  circle - 3 triangle
	 * 						   //if intsecNum = 8	 rectangle - 4 triangles(deprecated)
	 * 						   if intsecNum = 8	 circle - 4 triangles
	 *						   in summary,  =  circle - (intsecNum / 2) triangles 							
	 * ----------------------------------------------------------------------------
	 *	3 if mindist>0 ( not focus partition)
	 *		3.1 #exptOut == 1: rectangle - triangle
	 *		3.2 #exptOut == 2: 
	 *				if intsecNum == 4 rectangle - 2 triangle
	 *				if intsecNum == 2 trapezoid
	 *		3.3 #exptOut == 3: triangle(notice that it cannot be a trapezoid)
	 *		3.4 #exptOut == 4: triangle
	 * @param p
	 * @param focusPt
	 * @param radius
	 * @return the size of intersecting area
	 * @throws Exception 
	 */
	public static double calIntersect(Partition p, Point focusPt, double radius) throws Exception {
		
		//outer extreme points(> NOT >= radius)
		//onner extreme points(<= radius)
		ArrayList<Point> outerPts = new ArrayList<Point>();
		ArrayList<Point> innerPts = new ArrayList<Point>();
		int exptOut = getExtPtOuter(p, focusPt, radius, outerPts, innerPts);
		
		//intersect points
		ArrayList<Point> intsecPts = new ArrayList<Point>();
		int intsecNum = getIntsecPts(p,focusPt, radius, intsecPts);
		
		double rgtArea,trgArea,trpzdArea;
		double trgHgt,trgBtm;
		
		rgtArea = (p.xmax-p.xmin)*(p.ymax-p.ymin);
		
		if(p.minDist == -1){
			throw new Exception("mindist not initilized");
		}
		
		System.out.println("exptOut:"+exptOut + " radius:"+radius);
		
		if(p.minDist == 0){
			
			switch(exptOut){
			case 1:
				if(intsecNum != 2){
					throw new Exception("why not intsecNum:"+intsecNum);
				}
				trgBtm = Tools.dist(intsecPts.get(0), outerPts.get(0));
				trgHgt = Tools.dist(intsecPts.get(1), outerPts.get(0));
				
				trgArea = calTriangleArea(trgBtm,trgHgt);
				
				return rgtArea - trgArea;
				
			case 2:
				if(intsecNum==4){
					//according to distance get 2 small triangles
					trgArea = cal2SmallTriangleArea(outerPts,intsecPts);
					return rgtArea - trgArea;
				}else if(intsecNum == 2){
					//according to 4 points
					trpzdArea = calTrapezoidArea(innerPts,intsecPts);
					return trpzdArea;
				}else{
					throw new Exception("why not intsecNum:"+intsecNum);
				}
			case 3:
				if(intsecNum!=2){
					throw new Exception("why not intsecNum:"+intsecNum);
				}
				//according to 3(1 inner and 2 intersect pts) points and the circle
				trpzdArea = calPolygonArea(innerPts,intsecPts,focusPt,radius);
				return trpzdArea;
			case 4:
				if(intsecNum == 0){
					return Math.PI * radius * radius;
				}else if(intsecNum == 2 ||intsecNum == 4 ||intsecNum == 6 ||intsecNum == 8 ){
					//the intsec points should be pair order
					trgArea = calIntSecOuterTrgArea(intsecPts,focusPt,radius);
					return Math.PI * radius * radius - trgArea;
				}else{
					throw new Exception("why not intsecNum:"+intsecNum+"--"+intsecPts+"**"+outerPts);
				}
			default:
				throw new Exception("what?"+intsecNum);
			}
			 
		}else{
			//now is minDist>0!!!!
			switch(exptOut){
			case 1://here the SAME as the mindist == 0
				if(intsecNum != 2){
					if( intsecNum == 3 ){
						//this is special case
						if((intsecPts.get(0).equals(intsecPts.get(1))  ) ){
							intsecPts.remove(0);
						}else if(  intsecPts.get(0).equals(intsecPts.get(2))  ){
							intsecPts.remove(0);
						}else if (intsecPts.get(1).equals(intsecPts.get(2)) ){
							intsecPts.remove(1);
						}else{
							throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
						}
						
					}else if( intsecNum == 4){
						//special, take as expOut = 2 with 2 triangles TODO
						
					}
					else{
						throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
					}
				}
				trgBtm = Tools.dist(intsecPts.get(0), outerPts.get(0));
				trgHgt = Tools.dist(intsecPts.get(1), outerPts.get(0));
				
				trgArea = calTriangleArea(trgBtm,trgHgt);
				
				return rgtArea - trgArea;
			case 2:// here the same as mindist == 0
				if(intsecNum==4){
					//according to distance get 2 small triangles
					trgArea = cal2SmallTriangleArea(outerPts,intsecPts);
					return rgtArea - trgArea;
				}else if(intsecNum == 2){
					//according to 4 points
					trpzdArea = calTrapezoidArea(innerPts,intsecPts);
					return trpzdArea;
				}else{
					if( intsecNum == 3 ){
						//this is special case
						if((intsecPts.get(0).equals(intsecPts.get(1))  ) ){
							intsecPts.add ( intsecPts.get(0) );
						}else if(  intsecPts.get(0).equals(intsecPts.get(2))  ){
							intsecPts.add ( intsecPts.get(0) );
						}else if (intsecPts.get(1).equals(intsecPts.get(2)) ){
							intsecPts.add ( intsecPts.get(1) );
						}else{
							throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
						}
						//according to 4 points
						trpzdArea = calTrapezoidArea(innerPts,intsecPts);
						return trpzdArea;
					}else{
						throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
					}
				}
			case 3: 
				if(intsecNum != 2){
					if(intsecNum == 3 && intsecPts.get(0).equals(intsecPts.get(1)) &&intsecPts.get(2).equals(intsecPts.get(1)) ){
						return 0;
					}else if( intsecNum == 3 ){
						//this is special case
						if((intsecPts.get(0).equals(intsecPts.get(1))  ) ){
							intsecPts.remove(0);
						}else if(  intsecPts.get(0).equals(intsecPts.get(2))  ){
							intsecPts.remove(0);
						}else if (intsecPts.get(1).equals(intsecPts.get(2)) ){
							intsecPts.remove(1);
						}else{
							throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
						}
						trgBtm = Tools.dist(intsecPts.get(0), intsecPts.get(1));
						trgHgt = radius - p.minDist;
						
						trgArea = calTriangleArea(trgBtm,trgHgt);
						
						return trgArea;
						
					}
					else{
						throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
					}
				}
				trgBtm = Tools.dist(intsecPts.get(0), innerPts.get(0));
				trgHgt = Tools.dist(intsecPts.get(1), innerPts.get(0));
				
				trgArea = calTriangleArea(trgBtm,trgHgt);
				
				return trgArea;
			case 4:
				if(intsecNum != 2){
					if( intsecNum == 3 ){
						//this is special case
						if((intsecPts.get(0).equals(intsecPts.get(1))  ) ){
							intsecPts.remove(0);
						}else if(  intsecPts.get(0).equals(intsecPts.get(2))  ){
							intsecPts.remove(0);
						}else if (intsecPts.get(1).equals(intsecPts.get(2)) ){
							intsecPts.remove(1);
						}else{
							throw new Exception("why not intsecNum2 :"+intsecNum+"--:"+intsecPts+" pn:"+p);
						}
					}else{
						throw new Exception("why not intsecNum:"+intsecNum+"--intsecPts:"+intsecPts);
					}
				}
				trgBtm = Tools.dist(intsecPts.get(0), intsecPts.get(1));
				trgHgt = radius - p.minDist;
				
				trgArea = calTriangleArea(trgBtm,trgHgt);
				
				return trgArea;
			default:
				throw new Exception("what?"+intsecNum);
			}
		}
	}

	/**
	 * calculate the area of outer triangles. 1~4 triangles
	 * @param intsecPts
	 * @param focusPt
	 * @param radius
	 * @return
	 * @throws Exception
	 */
	private static double calIntSecOuterTrgArea(
			ArrayList<Point> intsecPts,
			Point focusPt, double radius) throws Exception 
	{
		double areaSum = 0;
		double wth, height;
		for(int i=0; i<intsecPts.size() / 2; i++){
			Point p1 = intsecPts.get(i*2);
			Point p2 = intsecPts.get(i*2+1);
			if(p1.x == p2.x){//vertical triangle
				wth = Math.abs( p1.y - p2.y );
				height = radius - Math.abs(p1.x - focusPt.x);
				areaSum += calTriangleArea(wth,height);
			}else if(p1.y == p2.y){//horizontal triangle
				wth = Math.abs( p1.x - p2.x );
				height = radius - Math.abs(p1.y - focusPt.y);
				areaSum += calTriangleArea(wth,height);
			}else{
				throw new Exception("what?");
			}
		}
		return areaSum;
	}


	/**
	 * 
	 * @param pts4 4 points
	 * @param pts8 8 points, it will be changed in the function
	 * @return
	 */
//	private static double cal4SmallTriangleArea(
//			ArrayList<Point> pts4,
//			ArrayList<Point> pts8) 
//	{
//		final Point p1 = pts4.get(0);
//		final Point p2 = pts4.get(1);
//		final Point p3 = pts4.get(2);
//		final Point p4 = pts4.get(3);
//		
//		Point p11,p12,p21,p22,p31,p32,p41,p42;
//		
//		//sort according to p1
//		Collections.sort(pts8, new Comparator<Point>() {
//			@Override
//			public int compare(Point o1, Point o2) {
//				double dist1 = Tools.dist(o1,p1);
//				double dist2 = Tools.dist(o2,p1);
//				if (dist1 == dist2) {
//					return 0;
//				} else {
//					return dist1 < dist2 ? -1 : 1;
//				}
//			}
//		});
//		
//		
//		p11 = pts8.get(0);
//		p12 = pts8.get(1);
//		pts8.remove(0);//remove top
//		pts8.remove(0);//remove top
//		
//		//sort according to p2
//		Collections.sort(pts8, new Comparator<Point>() {
//			@Override
//			public int compare(Point o1, Point o2) {
//				double dist1 = Tools.dist(o1,p2);
//				double dist2 = Tools.dist(o2,p2);
//				if (dist1 == dist2) {
//					return 0;
//				} else {
//					return dist1 < dist2 ? -1 : 1;
//				}
//			}
//		});
//		
//		p21 = pts8.get(0);
//		p22 = pts8.get(1);
//		pts8.remove(0);//remove top
//		pts8.remove(0);//remove top
//		
//		//sort according to p3
//		Collections.sort(pts8, new Comparator<Point>() {
//			@Override
//			public int compare(Point o1, Point o2) {
//				double dist1 = Tools.dist(o1,p3);
//				double dist2 = Tools.dist(o2,p3);
//				if (dist1 == dist2) {
//					return 0;
//				} else {
//					return dist1 < dist2 ? -1 : 1;
//				}
//			}
//		});
//		
//		p31 = pts8.get(0);
//		p32 = pts8.get(1);
//		p41 = pts8.get(2);
//		p42 = pts8.get(3);
//		
//		double trgArea,trgHgt,trgBtm; 
//		
//		trgHgt = Tools.dist(p1, p11);
//		trgBtm = Tools.dist(p1, p12);
//		trgArea = calTriangleArea(trgHgt,trgBtm);
//		
//		trgHgt = Tools.dist(p2, p21);
//		trgBtm = Tools.dist(p2, p22);
//		trgArea += calTriangleArea(trgHgt,trgBtm);
//		
//		trgHgt = Tools.dist(p3, p31);
//		trgBtm = Tools.dist(p3, p32);
//		trgArea += calTriangleArea(trgHgt,trgBtm);
//		
//		trgHgt = Tools.dist(p4, p41);
//		trgBtm = Tools.dist(p4, p42);
//		trgArea += calTriangleArea(trgHgt,trgBtm);
//		
//		return trgArea;
//	}
	
	/**
	 * according to 3(1 inner and 2 intersect pts) points and the circle
	 * have 1/4circle + 2 big rectangle - 1 small rectangle
	 * @param innerPts
	 * @param intsecPts
	 * @param focusPt
	 * @param radius
	 * @return
	 * @throws Exception 
	 */
	private static double calPolygonArea(
			ArrayList<Point> innerPts,
			ArrayList<Point> intsecPts, 
			Point focusPt, double radius) throws Exception 
	{
		Point innerPt = innerPts.get(0);
		double small_rec_x = Tools.dist(focusPt.x, 0, innerPt.x, 0);
		double small_rec_y = Tools.dist(focusPt.y, 0, innerPt.y, 0);
		
		Point xp;
		Point yp;
		if(intsecPts.get(0).x == innerPt.x){
			xp = intsecPts.get(0);
			yp = intsecPts.get(1);
		}else if(intsecPts.get(1).x == innerPt.x){
			xp = intsecPts.get(1);
			yp = intsecPts.get(0);
		}else{
			throw new Exception("why?");
		}

		double x_recg_width = Tools.dist(xp, innerPt);
		double y_recg_height = Tools.dist(yp, innerPt);
		
		//1/4circle + 2 big rectangle - 1 small rectangle
		double area = Math.PI * radius * radius * 0.25  + x_recg_width * small_rec_y + y_recg_height * small_rec_x - small_rec_x * small_rec_y;
		
		return area;
	}

	/**
	 * according to 4 points
	 * 
	 * @param innerPts as points for height
	 * @param intsecPts as points for upper and bottom
	 * @return
	 * @throws Exception 
	 */
	private static double calTrapezoidArea(
			ArrayList<Point> heightPts,
			ArrayList<Point> intsecPts) throws Exception 
	{
		Point hp1 = heightPts.get(0);
		Point hp2 = heightPts.get(1);
		Point wp1,wp2;
		double w1,w2,height;
		height = Tools.dist(hp1, hp2);
		
		if(hp1.x == hp2.x){
			//height is vertical
			//2 intsecPts will have 2 same y's
			if(intsecPts.get(0).y == hp1.y){
				wp1 = intsecPts.get(0);
				wp2 = intsecPts.get(1);
			}else if(intsecPts.get(1).y == hp1.y){
				wp1 = intsecPts.get(1);
				wp2 = intsecPts.get(2);
			}else{
				throw new Exception("whyat?!");
			}
			w1 = Tools.dist(wp1, hp1);
			w2 = Tools.dist(wp2, hp2);
			
			return (w1+w2)*height*0.5;
			
		}else if(hp1.y == hp2.y){
			//height is horizontal
			//2 intsecPts will have 2 same x's
			if(intsecPts.get(0).x == hp1.x){
				wp1 = intsecPts.get(0);
				wp2 = intsecPts.get(1);
			}else if(intsecPts.get(1).x == hp1.x){
				wp1 = intsecPts.get(1);
				wp2 = intsecPts.get(2);
			}else{
				throw new Exception("whyat?!");
			}
			w1 = Tools.dist(wp1, hp1);
			w2 = Tools.dist(wp2, hp2);
			
			return (w1+w2)*height*0.5;
		}else{
			throw new Exception("whyat?!");
		}
	}

	/**
	 * 
	 * @param pts2 2 points
	 * @param pts4 4 points
	 * @return
	 */
	private static double cal2SmallTriangleArea(
			ArrayList<Point> pts2,
			ArrayList<Point> pts4) 
	{
		final Point p1 = pts2.get(0);
		final Point p2 = pts2.get(1);
		
		//sort according to p1
		Collections.sort(pts4, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double dist1 = Tools.dist(o1,p1);
				double dist2 = Tools.dist(o2,p1);
				if (dist1 == dist2) {
					return 0;
				} else {
					return dist1 < dist2 ? -1 : 1;
				}
			}
		});
		
		Point p11,p12,p21,p22;
		p11 = pts4.get(0);
		p12 = pts4.get(1);
		p21 = pts4.get(2);
		p22 = pts4.get(3);
		
		double trgArea,trgHgt,trgBtm; 
		trgHgt = Tools.dist(p1, p11);
		trgBtm = Tools.dist(p1, p12);
		trgArea = calTriangleArea(trgHgt,trgBtm);
		
		trgHgt = Tools.dist(p2, p21);
		trgBtm = Tools.dist(p2, p22);
		trgArea += calTriangleArea(trgHgt,trgBtm);
		
		return trgArea;
	}

	//area of a triangle 
	private static double calTriangleArea(double trgBtm, double trgHgt) {
		return trgHgt * trgBtm / 2.0;
	}

	/***
	 * four lines(x=p.xmin,x=p.xmax (when y>=ymin y<=ymax), y=p.ymin, y=p.ymax(when x>=xmin x<=xmax) )
	 * circle formula: (x-x0)^2+(y-y0)^2 = radius^2, of which (x0,y0) is focus point
	 * The Formula::
	 * so, for x=x1, we have y= (+/-)sqrt(radius^2 - (x1-x0)^2)+y0
	 *	   for y=y1, we have x= (+/-)sqrt(radius^2 - (y1-y0)^2)+x0	
	 * @param p
	 * @param focusPt
	 * @param radius
	 * @param intsecPts
	 * @return
	 */
	private static int getIntsecPts(Partition p, Point focusPt, double radius,
			ArrayList<Point> intsecPts) {
		int num = 0;
		ArrayList<Double> y_pair1 = intsecPtsFormula(radius,p.xmin,focusPt.x,focusPt.y);
		num += checkYRange(p.ymin,p.ymax,y_pair1,p.xmin,intsecPts);
		ArrayList<Double> y_pair2 = intsecPtsFormula(radius,p.xmax,focusPt.x,focusPt.y);
		num += checkYRange(p.ymin,p.ymax,y_pair2,p.xmax,intsecPts);
		
		ArrayList<Double> x_pair1 = intsecPtsFormula(radius,p.ymin,focusPt.y,focusPt.x);
		num += checkXRange(p.xmin,p.xmax,x_pair1,p.ymin,intsecPts);
		ArrayList<Double> x_pair2 = intsecPtsFormula(radius,p.ymax,focusPt.y,focusPt.x);
		num += checkXRange(p.xmin,p.xmax,x_pair2,p.ymax,intsecPts);
		
		return num;
	}
	
	//checking range and add to list
	private static int checkXRange(double xmin, double xmax,
			ArrayList<Double> x_list, double y, ArrayList<Point> intsecPts) {
		int num = 0;
		for(int i=0; i<x_list.size(); i++){
			if(x_list.get(i) >= xmin && x_list.get(i) <= xmax){
				intsecPts.add(new Point(x_list.get(i),y));
				num++;
			}
		}
		return num;
	}

	//checking range and add to list
	private static int  checkYRange(double ymin, double ymax,
			ArrayList<Double> y_list, double x, ArrayList<Point> intsecPts) {
		int num = 0;
		for(int i=0; i<y_list.size(); i++){
			if(y_list.get(i) >= ymin && y_list.get(i) <= ymax){
				intsecPts.add(new Point(x,y_list.get(i)));
				num++;
			}
		}
		return num;
	}

	/**
	 * y= (+/-)sqrt(radius^2 - (x1-x0)^2)+y0
	 * x= (+/-)sqrt(radius^2 - (y1-y0)^2)+x0
	 * @param radius
	 * @param x1
	 * @param x0
	 * @param y0
	 * @return the 2 values, even they are the same we just return 2 values!!! TODO
	 */
	private static ArrayList<Double> intsecPtsFormula(double radius,
			double x1, double x0, double y0) {
		ArrayList<Double> y_pair = new ArrayList<Double> ();
		radius = Math.pow(radius, 2);
		double tmp = Math.pow( x1-x0, 2);
		double sqrt = Math.sqrt(radius - tmp);
		y_pair.add( new Double(sqrt+y0) );
		y_pair.add( new Double(-sqrt+y0) );
		return y_pair;
	}

	/**
	 * ONLY strictly greater > !!! can be outer points
	 * @param p
	 * @param focusPt
	 * @param radius
	 * @param outerPts
	 * @param innerPts
	 * @return
	 */
	private static int getExtPtOuter(
			Partition p, Point focusPt,double radius, ArrayList<Point> outerPts, ArrayList<Point> innerPts) 
	{
		Point p1,p2,p3,p4;
		p1 = new Point(p.xmin,p.ymin);
		p2 = new Point(p.xmin,p.ymax);
		p3 = new Point(p.xmax,p.ymin);
		p4 = new Point(p.xmax,p.ymax);
		
		double d1,d2,d3,d4;
		d1 = Tools.dist(p1, focusPt);
		d2 = Tools.dist(p2, focusPt);
		d3 = Tools.dist(p3, focusPt);
		d4 = Tools.dist(p4, focusPt);
		
		int num = 0;
		if(d1>radius){
			num++;
			outerPts.add(p1);
		}else{
			innerPts.add(p1);
		}
		if(d2>radius){
			num++;
			outerPts.add(p2);
		}else{
			innerPts.add(p2);
		}
		if(d3>radius){
			num++;
			outerPts.add(p3);
		}else{
			innerPts.add(p3);
		}
		if(d4>radius){
			num++;
			outerPts.add(p4);
		}else{
			innerPts.add(p4);
		}
		
		return num;
	}

}
