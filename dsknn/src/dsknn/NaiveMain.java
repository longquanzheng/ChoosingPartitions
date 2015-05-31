package dsknn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static dsknn.Tools.*;

public class NaiveMain {

	public static boolean isDebug = false;
	public static boolean isErr = false;
	
	public static double THRESHOLD = 10;//80%
	
	public static int statis_succ = 0, statis_fail = 0, statis_maybefail = 0, statis_saving_opt = 0, statis_saving_ovh = 0;
	public static int statis_real_saving_opt = 0, statis_real_saving_ovh = 0;
	public static long statis_guess = 0, statis_opt = 0, statis_ovh = 0;
	
	public static World wd;
	
	private static void resetStatis(){
		statis_succ = 0; statis_fail = 0; statis_maybefail = 0; statis_saving_opt = 0; statis_saving_ovh = 0;
		statis_real_saving_opt = 0; statis_real_saving_ovh = 0;
		statis_guess = 0; statis_opt = 0; statis_ovh = 0;
	}
	public static void main(String[] args) {
		wd = new World(); 
		
		try {
			for(int k=1; k<20; k+=2){
				for(double t = 9.9999; t<100; t+=10){
					NaiveMain.THRESHOLD = t;
					resetStatis();
					for(int i=0; i<100; i++){
						testkNN(k);
						println("finish["+(i+1)+"]...");
					}
					
//					System.out.println("***["+k+"]["+Math.cbrt(t)+"]int statis_succ "+statis_succ+", statis_fail "+statis_fail
//							+", statis_maybefail "+statis_maybefail+", statis_saving_opt "+statis_saving_opt+", statis_saving_ovh "
//							+statis_saving_ovh+"\n statis_real_saving_opt "+statis_real_saving_opt + " statis_real_saving_ovh "+statis_real_saving_ovh
//							+"\n statis_guess "+statis_guess+", statis_opt "+statis_opt+", statis_ovh "+statis_ovh+"");
					System.out.println("***["+k+"]["+t+"]int statis_succ "+statis_succ+", statis_fail "+statis_fail
							+", statis_maybefail "+statis_maybefail+" statis_guess "+statis_guess+", statis_opt "+statis_opt);
//					Thread.sleep(10000);

				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void println(String str){
		if(isDebug){
			System.out.println(str);
		}
	}
	public static void errprintln(String str){
		if(isErr){
			System.err.println(str);
		}
	}
	
	private static void testkNN(int k) throws Exception {
		
//		Point focusPt = new Point(59.492029548199326,75.47355763123949);//saving but some calculating error in probability
//		Point focusPt = new Point(-81.06359485588497,24.177614124813715);//with some errors
//		Point focusPt = new Point(-42.12565932403546,89.50471514058677);//good saving
//		Point focusPt = new Point(21.71232270905817,157.16844746018523);//predict fail in 20 threshold
//		Point focusPt = new Point(34.46536475637487,-109.91450311156294);
//		Point focusPt = new Point(60.67030451881257,96.09404296095107);//good saving with 50 prob
//		Point focusPt = new Point(79.50853737784513,-9.270436243968959);// not guarantee but still succ
//		Point focusPt = new Point(-37.78586543839447,93.64918288360064);//predict fail in k=12 threshold = 20,succ in 99.999
		Point focusPt = new Point();
//		Point focusPt = new Point();
//		Point focusPt = new Point();
//		Point focusPt = new Point();
//		Point focusPt = new Point();
//		Point focusPt = new Point(-18.413281377903047,73.80952779501027);
//		Point focusPt = new Point(52.04651181511619,-11.462995650837883);//good example!saving!!!
//		Point focusPt = new Point(-36.331294315520275,-176.12264400820553);
//		Point focusPt = new Point(-33.27737815442204,85.04308648246376);
		
		println(" >>the focus point is"+focusPt);
		
		//0. now we begin with a simple way to sort all the partitions
		// TODO in an advanced way we may interate only part of the whole world (as a small world), 
		// and put only needed partitions in the sorted list, like using the overhead list
		ArrayList<Partition> sortedPtn = new ArrayList<Partition>(); 
		for(int i=0;i<World.N;i++){
			for(int j=0;j<World.N;j++){
				wd.ptn[i][j].calMinDist(focusPt);
				wd.ptn[i][j].calMaxDist(focusPt);
				//skip the focus partition(0 mindist), for the simplicity of the following codes
				if(wd.ptn[i][j].minDist == 0){
					continue;
				}
				//skip the empty partition
				if(wd.ptn[i][j].points.size() == 0 ){
					continue;
				}
				sortedPtn.add(wd.ptn[i][j]);
			}
		}
		Collections.sort(sortedPtn, new Comparator<Partition>() {
			@Override
			public int compare(Partition o1, Partition o2) {
				return o1.compareTo(o2);
			}
		});
	
		//try from the nearest to the farest
		double guessProb = 0; // the probability
		ArrayList<Partition> guessList = null; // the partitions list to guess, having threshold( like 80%)
		ArrayList<Partition> optimalList = null;  // the partitions list to guarantee 100%
		double radius = -1;
		
		for(int i=0; i<sortedPtn.size(); i++){
			//curr predict partition boundary
			Partition boundaryP = sortedPtn.get(i);
			
			//1.get the partition intersect with the circle
			//generate yellow and green list of partitions
			ArrayList<Partition> yellowList = new ArrayList<Partition>();
			ArrayList<Partition> greenList = new ArrayList<Partition>();
			getAffectedLists(yellowList,greenList,wd,focusPt,boundaryP);
			
			//3. cal the probability to get kNN here
			//based on the yellow and green lists
			//3.1 parse greenlist, generally, the points in the green list should < k
			int ptNum = calPtNum(greenList);
			if(ptNum>=k){
				if(guessProb<NaiveMain.THRESHOLD){//go to 100% directly,mostly it won't happen
					guessProb = 100;
					guessList = yellowList;
					guessList.addAll(greenList);
					optimalList = guessList;
					radius = boundaryP.minDist;
				}else{
					optimalList = yellowList;
					optimalList.addAll(greenList);
				}
				break;
			}else{
				//3.2 parse yellowlist to predict probability if not meet yet
				if(guessProb<NaiveMain.THRESHOLD){
					guessProb = Probability.predictProb(yellowList,k-ptNum);
					println("prob:"+guessProb);
					if(guessProb >= NaiveMain.THRESHOLD){
						guessList = yellowList;
						guessList.addAll(greenList);
						radius = boundaryP.minDist;
					}
				}
			}
		}
		
		if(optimalList == null){//I think it won't happen unless too big K or bugs
			errprintln("never has 100% conditions?");
		}
		
		if(guessProb<NaiveMain.THRESHOLD){
			println("not found radius meet the threshold,will process all partitions");
		}else{
			//4.get the true result of KNN
			//4.1 predict of guesslist
			List<Point> pl1 = KNN.getKPoints(guessList, focusPt, k);
//			System.out.println("nearest k points of guesslist:"+pl1);
			Point p1 = pl1.get(k-1);
//			System.out.println("the k point from guesslist is ("+p1.x+","+p1.y+
//					") in ["+World.getIx(p1.x)+"]["+World.getIy(p1.y)+"],dist to focus point is ("
//					+dist(p1.x, p1.y,focusPt.x , focusPt.y)+")  r:" + radius );
			boolean maybefail = false;
			if( dist(p1.x, p1.y,focusPt.x , focusPt.y) > radius ){
//				System.err.println("NOT guarantee success, it maybe will fail!");
				statis_maybefail ++;
				maybefail = true;
			}
			
			//4.2 the true result of optimallist
			List<Point> pl2 = KNN.getKPoints(optimalList, focusPt , k);
//			System.out.println("nearest k points of optimalList:"+pl2);
			Point p2 = pl2.get(k-1);
//			System.out.println("the k point from optimallist is ("+p2.x+","+p2.y+
//					") in ["+World.getIx(p2.x)+"]["+World.getIy(p2.y)+"],dist to focus point is ("
//					+dist(p2.x, p2.y,focusPt.x , focusPt.y)+")");
			
			//4.3 the true result from overhead list, which should be the same as the optimal list
			ArrayList<Partition> overheadList = getOverheadList(wd,focusPt,k);
			List<Point> pl3 = KNN.getKPoints(overheadList, focusPt , k);
//			System.out.println("nearest k points of overheadList:"+pl3);
			Point p3 = pl3.get(k-1);
//			System.out.println("the k point from overheadlist is ("+p3.x+","+p3.y+
//					") in ["+World.getIx(p3.x)+"]["+World.getIy(p3.y)+"],dist to focus point is ("
//					+dist(p3.x, p3.y,focusPt.x , focusPt.y)+")");
			
			if(p3.x != p2.x || p3.y != p2.y){
				errprintln("Something wrong!!overhead!=optimal");
			}
			statis_guess += guessList.size();
			statis_opt += optimalList.size();
			statis_ovh += overheadList.size();
			
			//4.4 final result
			if(p1.x == p2.x && p1.y == p2.y){
				println("predict success!!");
				statis_succ++;
				
				statis_saving_opt += optimalList.size()-guessList.size();
				statis_saving_ovh += overheadList.size()-guessList.size();
				if(!maybefail){
					statis_real_saving_opt += optimalList.size()-guessList.size();
					statis_real_saving_ovh += overheadList.size()-guessList.size();
				}
			}else{
				statis_fail++;
				errprintln("!!!predict fail!!");
			}
			println("radius meet the threshold, used only ["+ 
					guessList.size() + "] with probability ("+ guessProb+") ,instead of optimallist ["+optimalList.size()+"]"+
					"or overheadlist ["+overheadList.size()+"]");
			
			println("it saves ["+ (optimalList.size()-guessList.size()) +"] or ["+
					(overheadList.size()-guessList.size())+"] partitions");
			println("<<");
		}
	}

	/**
	 * walking left or right to generate the small world then get a overhead list
	 * @param wd
	 * @param focusPt
	 * @param k
	 * @return
	 */
	private static ArrayList<Partition> getOverheadList(World wd, Point focusPt, int k) {
		int ptnum = 0;
		int ix = World.getIx(focusPt.x);
		int iix = ix;
		int iy = World.getIy(focusPt.y);
		boolean left;
		if(iix > World.N/2 ){//go left
			left = true;
		}else{//go right
			left = false;
		}
		ArrayList<Partition> pl = new ArrayList<Partition>();
		while(ptnum<k){
			Partition p = wd.ptn[iix][iy];
			ptnum += p.points.size();
			if(left){
				if(iix == 0){
					break;
				}else{
					iix--;
				}
			}else{
				if(iix == World.N-1){
					break;
				}else{
					iix++;
				}
			}
		}
		
		int leftx,rightx,upy,downy;
		if(left){
			leftx = iix;
			rightx = ix + (ix-iix);
			upy = (int) Math.ceil( iy+ (ix-iix)*World.xstep / World.ystep );
			downy = iy - (upy - iy);
		}else{
			rightx = iix;
			leftx = ix + (ix-iix);
			upy = (int) Math.ceil( iy+ (iix - ix )*World.xstep / World.ystep );
			downy = iy - (upy - iy);
		}
		if(leftx <0){
			leftx = 0;
		}
		if(rightx >= World.N){
			rightx = World.N -1;
		}
		if(downy<0){
			downy = 0;
		}
		if(upy >= World.N){
			upy = World.N -1;
		}
		
		//return small world
		for(int i=leftx; i<= rightx; i++){
			for(int j=downy; j<= upy; j++){
				pl.add(wd.ptn[i][j]);
			}
		}
		
		return pl;
	}

	//calculate the points in the list
	private static int calPtNum(ArrayList<Partition> plist) {
		int num = 0;
		for(int i=0; i<plist.size(); i++){
			num += plist.get(i).points.size();
		}
		return num;
	}

	/**!!!!!!!!!!!key function
	 * naive way, iterate all the partitions in the world
	 * TODO actually we have better way as walking but hard to implement
	 * 1. iterate all the partition, if mindist < radius and maxdist> radius, then get it into yellow list
	 * 2. iterate all the partition, if mindist < radius and maxdist<= radius, then get it into green list
	 * 3. iterate all the partition, if mindist >= radius , then skip (grey list)
	 * 
	 * 4. for partitions in yellowlist, calc the intersect area BTW(Call Intersect class):
	 * 	
	 * @param wd world
	 * @param focusPt focus point
	 * @param boundaryP boundary partition
	 * @return
	 * @throws Exception 
	 */
	private static boolean getAffectedLists(
			ArrayList<Partition> yellowList, ArrayList<Partition> greenList, 
			World wd, Point focusPt, Partition boundaryP) 
	{
		double radius = boundaryP.minDist;
		for(int i=0;i<World.N;i++){
			for(int j=0;j<World.N;j++){
				Partition p = wd.ptn[i][j];
				
				//TODO we can skip the empty partition here,
				
				if (p.minDist < radius && p.maxDist > radius){//yellow list(intesect list)
					try {
						p.intsecArea = Intersect.calIntersect(p,focusPt,radius);
					} catch (Exception e) {
						errprintln("cannot cal area, make zero intsec Area for "+p);
						p.intsecArea = 0;
					}

					yellowList.add(p);
				}else if(p.minDist < radius && p.maxDist <= radius){//green list
					greenList.add(p);
				}else{//skip, grey list
					continue;
				}
			}
		}
		return true;
	}

}
