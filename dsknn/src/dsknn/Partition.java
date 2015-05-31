package dsknn;


import java.util.ArrayList;

import static dsknn.Tools.*;

public class Partition implements Comparable<Partition>{
	
	public ArrayList<Point> points;
	//index of x and y (0~N), for identification
	public int xi,yi;
	//retangle of partition
	public double xmin,ymin,xmax,ymax;
	
	//cal after create
	public double minDist = -1;
	public double maxDist = -1;
	
	//will cal after
	public double intsecArea = -1;
	
	//cal after create, but not for NaiveMain, will be used in an advanced way
	public int minRank = -1;
	public int maxRank = -1;
	
	
	@Override
	public int compareTo(Partition o) {
		if( this.minDist-o.minDist != 0){
			return this.minDist-o.minDist < 0 ? -1 : 1;
		}else if( this.xi-o.xi != 0){
			return o.xi-this.xi; //less is more, so -1 would be larger than all normal xi
		}else{
			return o.yi-this.yi;
		}
	}
	
	//create ONLY for comparing
	public Partition(int xi, int yi, double minDist) {
		super();
		this.xi = xi;
		this.yi = yi;
		this.minDist = minDist;
	}

	public Partition(int xi, int yi, double xmin,double ymin, double xmax, double ymax) {
		points = new ArrayList<Point>();
		this.xi = xi;
		this.yi = yi;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	@Override
	public String toString(){
//		return "("+xi+","+yi+")*"+points.size()+"&"+this.minDist+"&"+this.maxDist;
		return "("+xi+","+yi+")*"+points.size()+"&("+xmin+","+ymin+") & ("+xmax+ "," + ymax+") ";
	}

	public void calMinDist(Point focusPt) {
		if(focusPt.x >= this.xmin &&focusPt.x <= this.xmax){
			
			if(focusPt.y >= this.ymin &&focusPt.y <= this.ymax){//in it
				this.minDist = 0;
			}else if( focusPt.y <= this.ymin ){//down
				this.minDist = this.ymin - focusPt.y;
			}else{//up focusPt.y >= this.ymax
				this.minDist = focusPt.y - this.ymax;
			}
		}else if(focusPt.y >= this.ymin &&focusPt.y <= this.ymax){
			if( focusPt.x <= this.xmin ){//left
				this.minDist = this.xmin - focusPt.x;
			}else{//right focusPt.x >= this.xmax
				this.minDist = focusPt.x - this.xmax;
			}
		}else{ //from one of 4 extreme points
			double dist1 = dist(focusPt.x,focusPt.y, xmin,ymin);
			double dist2 = dist(focusPt.x,focusPt.y, xmin,ymax);
			double dist3 = dist(focusPt.x,focusPt.y, xmax,ymin);
			double dist4 = dist(focusPt.x,focusPt.y, xmax,ymax);
			this.minDist = Math.min(dist1, Math.min(dist2, Math.min(dist3, dist4)));
		}
	}
	
	public void calMaxDist(Point focusPt) {
		//from one of 4 extreme points
		double dist1 = dist(focusPt.x,focusPt.y, xmin,ymin);
		double dist2 = dist(focusPt.x,focusPt.y, xmin,ymax);
		double dist3 = dist(focusPt.x,focusPt.y, xmax,ymin);
		double dist4 = dist(focusPt.x,focusPt.y, xmax,ymax);
		this.maxDist = Math.max(dist1, Math.max(dist2, Math.max(dist3, dist4)));
	}
	
	public static void main(String[] args){
		Partition pn = new Partition(0,0,0,0,2,2);
		Point pt1 = new Point(1.5,1.5);
		pn.calMinDist(pt1);
		pn.calMaxDist(pt1);
		System.out.println(pn);
		
		pt1 = new Point(2.5,2.5);
		pn.calMinDist(pt1);
		pn.calMaxDist(pt1);
		System.out.println(pn);
	}
}
