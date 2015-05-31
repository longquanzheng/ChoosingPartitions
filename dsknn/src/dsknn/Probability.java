package dsknn;

import java.math.BigInteger;
import java.util.ArrayList;

public class Probability {
	
	public static void main(String[] args) {
		Partition pa = new Partition(0,0,0,0,1,1);
		Partition pb = new Partition(0,1,0,0,1,1);

		pa.intsecArea = 0.5;
		pb.intsecArea = 0.3;
		for(int i=0; i<100; i++){
			pa.points.add(new Point());
			pb.points.add(new Point());
		}
		for(int i=0; i<100; i++){
			pb.points.add(new Point());
		}
		
		ArrayList<Partition> list = new ArrayList<Partition>();
		list.add(pa);
		list.add(pb);
		
		try {
			System.out.println(predictProb(list,1));
			System.out.println(predictProb(list,2));
			System.out.println(predictProb(list,3));
			System.out.println(predictProb(list,4));
			System.out.println(predictProb(list,5));
			System.out.println(predictProb(list,60));
			System.out.println(predictProb(list,7));
			System.out.println(predictProb(list,8));
			System.out.println(predictProb(list,9));
			System.out.println(predictProb(list,10));
			System.out.println(predictProb(list,11));
			System.out.println(predictProb(list,12));
			System.out.println(predictProb(list,103));
			System.out.println(predictProb(list,1003));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**!!!!!!!!!!!key function
	 * calculate the proability, using the approximate way
	 *    For example, we have Na,Nb, Pa=a/A,Pb=b/B
	 *    
	 *    of which
	 *    a is the intersect area of A which is the rectangle area
	 *    b is the intersect area of B which is the rectangle area
	 *    ...
	 *    
	 *    then
	 *    Pab = (Na*Pa+Nb*Pb)/(Na+Nb);
	 *    Nab = (Na+Nb);
	 *    1st = 1-(none);
	 *    2nd = 1-(none)-(only 1);
	 *    3rd = 1-(none)-(only 1) - (only 2);
	 *    ...
	 *    
	 *    of which:
	 *    none = (1-Pab)^Nab
	 *    only 1 = Com(Nab,1) * (1-Pab)^(Nab-1) * (Pab)^1
	 *    ...
	 *    only X = Com(Nab,X) * (1-Pab)^(Nab-X) * (Pab)^X
	 *    
	 *    of which
	 *    Com is the combination number
	 *    
	 *    !!because the yellowlist did't count the focus partition so we need to judge whether to use it or not here!!
	 * @param yellowList 
	 * @param kRem the remaining k
	 * @return precent number, like 80 represent 0.8
	 * @throws Exception 
	 */
	public static double predictProb(ArrayList<Partition> yellowList, int kRem) throws Exception {
		double px = 0;
		int nx = 0;
		for(int i=0; i<yellowList.size(); i++){
			Partition pn = yellowList.get(i);
			if(pn.intsecArea == -1){
				throw new Exception("not cal pn.intsecArea yet!");
			}
			double p = pn.intsecArea / ( (pn.xmax-pn.xmin)*(pn.ymax-pn.ymin) );
			px += pn.points.size() * p;
			nx += pn.points.size();
		}
		px = px / nx;//Pab in the comments

		if(nx < kRem){
			return 0;
		}
//		System.out.println(px+" "+nx);
		
		//loop from 0 to kRem
		double sub = 0;
		for(int i=0; i<=kRem; i++){
			sub += calOne(i,nx,px);
		}
		
		return (1-sub)*100;
	}

	/**
	 *   none = (1-Pab)^Nab which is 0 for the i
	 *   	  = Com(Nab,0) * (1-Pab)^(Nab) * (Pab)^0
	 *    only 1 = Com(Nab,1) * (1-Pab)^(Nab-1) * (Pab)^1
	 *    ...
	 *    only X = Com(Nab,X) * (1-Pab)^(Nab-X) * (Pab)^X
	 * @param i
	 * @param nx
	 * @param px
	 * @return
	 */
	private static double calOne(int i, int nx, double px) {
		BigInteger com = calCombination(nx,i);
		//TODO is it OK to do the casting here???
		double com_double = com.doubleValue();
		return com_double * Math.pow( (1-px), nx-i) * Math.pow( (px), i);
	}

	/**
	 * get m from n, cal the combination
	 * com(n,m) = n! / ( m! * (n-m)! )
	 * @param n
	 * @param m
	 * @return
	 */
	private static BigInteger calCombination(int n, int m) {
		if(n-m<0){
			return BigInteger.valueOf(0);
		}
		if(m==0){
			return BigInteger.valueOf(1);
		}
		
//		BigInteger bi1 = calFactorial(n);
//		BigInteger bi3 = calFactorial(n-m);
		
		//get bi1/bi3 directly
		BigInteger bi31 = calFactorial2(n,n-m);
		
		BigInteger bi2 = calFactorial(m);
		
		BigInteger bi4 = bi31.divide( bi2 );
		
		return bi4;
	}
	
	private static BigInteger calFactorial2(int n, int m) {
		BigInteger result = new BigInteger("1");
		if(n-m<0){
			return BigInteger.valueOf(0);
		}
		if (n < 0) {
			System.err.println("n must be great than 0");
			return new BigInteger("-1");
		} else if (n == 0) {
			return new BigInteger("1");
		} else {
			for (int i = n; i > m; i--) {
				BigInteger num = new BigInteger(String.valueOf(i));
				result = result.multiply(num);
			}
			return result;
		}
	}


	/**
	 * get n!
	 * @param n
	 * @return
	 */
	private static BigInteger calFactorial(int n) {
		BigInteger result = new BigInteger("1");
		if (n < 0) {
			System.err.println("n must be great than 0");
			return new BigInteger("-1");
		} else if (n == 0) {
			return new BigInteger("1");
		} else {
			for (int i = 1; i <= n; i++) {
				BigInteger num = new BigInteger(String.valueOf(i));
				result = result.multiply(num);
			}
			return result;
		}
	}

}