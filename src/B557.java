import java.io.*;
import java.util.*;

public class B557 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int w=sc.nextInt();
        double[] arr=new double[2*n];
        for(int i=0;i<2*n;i++)
        {
            arr[i]=sc.nextFloat();
        }
        Arrays.sort(arr);
        double min1=arr[0],min2=arr[n];
        for(int i=0,j=n;j<2*n;i++,j++)
        {
            min1=Math.min(arr[i], min1);
            min2=Math.min(arr[j], min2);
                    
        }
        double result =0;
        //System.out.println(min1+" "+min2);
        if(min1*2>min2)
        {
          double var=(double)(min2/2);
          //System.out.println(var);
          result = n*var +n*min2;
        }
        else
        {
            result= min1*n+2*n*min1;
        }
        if(result > w)
        {
            result = w;
        }
        if(result==(int)result)
        {System.out.println((int)result);}
        else
        {System.out.println(result);}
        
                
    }
    
}
