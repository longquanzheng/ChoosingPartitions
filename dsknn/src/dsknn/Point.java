package dsknn;

public class Point {
	public Point(double x2, double y2) {
		x = x2;
		y = y2;
	}
	
	//generate random point,skip edge point
	//x:(-90~90)	y:(-180~180)
	public Point(){
		while(true){
			x =  ((Math.random() * ( 90 - (-90)))+(-90));
			y =  ((Math.random() * ( 180 - (-180)))+(-180));
			if(World.isInEdge(x, World.xstep) || World.isInEdge(y, World.ystep) ){
				continue;
			}
			break;
		}
	}
	
	public boolean equals(Point p){
		//the precision problem of Java TODO
//		if( Math.abs( this.x - p.x) < 0.00000000000001 || Math.abs( this.y - p.y) < 0.00000000000001 ){
		if( this.x == p.x && this.y == p.y){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	public double x,y;
}
