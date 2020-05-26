package matrix;

import java.util.Random;
import java.util.Vector;
import java.text.DecimalFormat;
/**
 *
 * @author Sajid @CT
 */
public class Matrix {

    /**
     * @param args the command line arguments
     */Random r=new Random();
     double[][] randomIntA(int m, int n, int max, int min) {
       double[][] A = new double[m][n];
        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A[i].length; j++)
            {  A[i][j] = r.nextInt(max - min + 1) + min;
            }
            }
        return A;
    
    }
     
     double[][] randomIntB(int m, int n, int max, int min) {
        double[][] B = new  double[m][n];
        for (int i = 0; i < B.length; i++)
        {
            for (int j = 0; j < B[i].length; j++)
            {  B[i][j] = r.nextInt(max - min + 1) + min;
            }
            }
        return B;
    
    }
     public double[][] inverz_matrike(double[][]in){
		int st_vrs=in.length, st_stolp=in[0].length;
		double[][]out=new double[st_vrs][st_stolp];
		double[][]old=new double[st_vrs][st_stolp*2];
		double[][]new1=new double[st_vrs][st_stolp*2];

		
		for (int v=0;v<st_vrs;v++){//ones vector
			for (int s=0;s<st_stolp*2;s++){
				if (s-v==st_vrs) 
					old[v][s]=1;
				if(s<st_stolp)
					old[v][s]=in[v][s];
			}
		}
		//zeros below the diagonal
		for (int v=0;v<st_vrs;v++){
			for (int v1=0;v1<st_vrs;v1++){
				for (int s=0;s<st_stolp*2;s++){
					if (v==v1)
						new1[v][s]=old[v][s]/old[v][v];
					else
						new1[v1][s]=old[v1][s];
				}
			}
			old=prepisi(new1);		
			for (int v1=v+1;v1<st_vrs;v1++){
				for (int s=0;s<st_stolp*2;s++){
					new1[v1][s]=old[v1][s]-old[v][s]*old[v1][v];
				}
			}
			old=prepisi(new1);
		}
		//zeros above the diagonal
		for (int s=st_stolp-1;s>0;s--){
			for (int v=s-1;v>=0;v--){
				for (int s1=0;s1<st_stolp*2;s1++){
					new1[v][s1]=old[v][s1]-old[s][s1]*old[v][s];
				}
			}
			old=prepisi(new1);
		}
		for (int v=0;v<st_vrs;v++){//rigt part of matrix is invers
			for (int s=st_stolp;s<st_stolp*2;s++){
				out[v][s-st_stolp]=new1[v][s];
			}
		}
		return out;
	}

	public double[][] prepisi(double[][]in){
		double[][]out=new double[in.length][in[0].length];
		for(int v=0;v<in.length;v++){
			for (int s=0;s<in[0].length;s++){
				out[v][s]=in[v][s];
			}
		}
		return out;
	}
    
public static Vector main(int t) {
        // TODO code application logic here
		String key="";
Vector v=new Vector();
       double c[][]=new  double[t][t];
        Matrix m=new Matrix();
        double a[][]=m.randomIntA(t,t,5,1);
          double b[][]=m.randomIntA(t,t,5,1);
                 
         for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < b.length; j++)
            {
      
      c[i][j]=a[i][j]*b[i][j] ;
               
       }
            
            }
     for (int i = 0; i < c.length; i++)
        {
            for (int j = 0; j < c.length; j++)
            {
              
                System.out.print(c[i][j]+" ") ;
         
              
            }
         System.out.println("\n") ;
 
    }
     
     
     
     
    double inv[][]= m.inverz_matrike(c);
    
    for (int i = 0; i < inv.length; i++)
        {
            for (int j = 0; j < inv.length; j++)
            {
               DecimalFormat df2 = new DecimalFormat("###.##");
               System.out.print(df2.format(inv[i][j])+"  ") ;
			   key=key+df2.format(inv[i][j])+" ";
								
              
            }
         System.out.println("\n") ;
		v.add(key);
		key="";

    }
    
		return v;    
    
    
    }
	public static void main(String[] args)
	{
		Vector vv=null;
					vv=main(5);

         System.out.println("sss++"+vv.size()) ;
for(Object o:vv)
{
	System.out.println(o);
}

	}
}
