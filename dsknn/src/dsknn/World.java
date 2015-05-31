package dsknn;

import java.io.*;

public class World {
	//!we skip the edge points for simplicity, so that we don't need [N+1][N+1] partitions
	public static int N  = 20; //100 * 100 partitions
	public int edgePtCnt = 0;
	public int allcnt = 0;
	
	public Partition[][] ptn;
	public static double xstep = 0,ystep = 0;
	private final String textPath = "./points.txt";
//	private final String textPath = "./morepoints.txt";
	
	static{
		xstep = (90-(-90))*1.0 / N;
		ystep = (180-(-180))*1.0 / N;
	}
	
	public World(){
		
		ptn = new Partition[N][N];
		
		//init partition
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				ptn[i][j] = new Partition(i,j, (-90)+xstep*i,(-180)+ystep*j, (-90)+xstep*(i+1),(-180)+ystep*(j+1));
			}
		}
		
		//reading points from file
		InputStream is = null;
		try {
		    is = new FileInputStream(textPath );
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
		    // read a line
		    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
		        line = line.trim();
		        // do something here
		        String[] sts = line.split(",");
		        double x = (Double.parseDouble(sts[0]) / 10000000.0) ;
				double y = (Double.parseDouble(sts[1]) / 10000000.0) ;
				
				//!!!!we skip the edge points for simplicity, so that we don't need [N+1][N+1] partitions
				if(isInEdge(x,xstep)||isInEdge(y,ystep)){
					edgePtCnt++;
					continue;
				}
				allcnt ++;
				
				int ix = getIx(x);
				int iy = getIy(y);
				
				ptn[ix][iy].points.add(new Point(x,y));
		    }
		    
		    System.out.println(" succ to load ["+this.allcnt+"] points, skip ["+this.edgePtCnt+"] points" )	;
		    reader.close();
		    is.close();
		}catch (Exception ioe){
		    ioe.printStackTrace();
		} 
	}
	
	public static int getIx(double x){
		return (int) ( (x-(-90)) / xstep );
	}
	
	public static int getIy(double y){
		return (int) ( (y-(-180)) / ystep );
	}
	
	public static boolean isInEdge(double v, double step){
		return v / step == ((int)(v / step)) ;
	}
	
	public static void main(String[] args){
		World wd = new World();
		for(int i=0; i<World.N; i++){
			for(int j=0; j<World.N; j++){
				System.out.print("	"+wd.ptn[i][j].points.size());
			}
			System.out.println();
		}
		System.out.println(wd.edgePtCnt);
		System.out.println(wd.allcnt);
	}
	
	
}
