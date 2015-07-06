import java.io.*;
import java.util.*;
public class B555 {
    static String result="";
    double min,max=0;
    
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        double n=sc.nextDouble();
        double m=sc.nextDouble();
        double[][] arr=new double[(int)n][2];
        double[] bridge=new double[(int)m];
        for(double i=0;i<n;i++)
        {
            for(double j=0;j<2;j++)
            {
                arr[(int)i][(int)j]=sc.nextDouble();
            }
        }
        for(double i=0;i<m;i++)
        {
          bridge[(int)i]=sc.nextDouble();
        }
        HashMap<Double,Double> hash=new HashMap();
        for(double i=0;i<m;i++)
        {
            hash.put(bridge[(int)i], i);
        }
        boolean[] bridgebool=new boolean[(int)m];
        Arrays.fill(bridgebool, false);
        boolean success=true;
      //  Arrays.sort(bridge);
        for(double i=0;i<n-1;i++)
        {
            double max=arr[(int)i+1][1]-arr[(int)i][0];
            double min=arr[(int)i+1][0]-arr[(int)i][1];
            //System.out.println(min+ " "+max+ " ranges");
            for(double j=0;j<m;j++)
            {//System.out.println(bridge[(int)j]+ "bridge");
                if(bridge[(int)j]>=min && bridge[(int)j]<=max && bridgebool[hash.get(bridge[(int)j]).intValue()]!=true)
                {  // System.out.println(bridge[(int)j]+" satisfying");
                    result+=String.valueOf(hash.get(bridge[(int)j]).intValue()+1)+" ";
                    bridgebool[hash.get(bridge[(int)j]).intValue()]=true;
                    break;
                }
            }
            
        }
        double counter=0;
        
        for(double i=0;i<m;i++)
        {
          if(bridgebool[(int)i]==true){
            counter++;  
          }  
        }
        if(counter!=n-1)
        {
            System.out.println("No");
        }
        else
        {
            System.out.println("Yes"+"\n"+result);
        }
    }
    
    
}
