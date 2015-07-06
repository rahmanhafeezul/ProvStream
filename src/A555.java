import java.io.*;
import java.util.*;

public class A555 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int k=sc.nextInt();
        int seconds =0;
        Vector<elements> arr=new Vector<elements>(n);
        for(int i=0;i<n;i++)
        {
            arr.add(new elements());
        }
        //System.out.println(arr.size());
        for(int i=0;i<k;i++)
        {
            int t=sc.nextInt();
            //System.out.println(i+" "+t);
            for(int j=0;j<t;j++)
            {
               arr.elementAt(i).add(sc.nextInt());
            }
        }
        seconds = n-1;
        for(int i=0;i<k;i++)
        {
            seconds+=arr.elementAt(i).size()-1;
        }
        for(int i=0;i<k;i++)
        {
            for(int j=0;j<arr.elementAt(i).size()-1;j++)
            {int f=(Integer)(arr.elementAt(i).elementAt(j+1))-(Integer)(arr.elementAt(i).elementAt(j));
                if(f==1)
                {
                    seconds-=2;
                    
                }
            }
        }
        System.out.println(seconds);
        
    }
    
}
class elements{
    Vector<Integer> v;
    public elements()
    {
        v=new Vector();
    }
    public void add(int n)
    {
        v.add(n);
    }
    public int size(){
        return v.size();
    }
    public int elementAt(int j)
    {
        return v.elementAt(j);
    }
}
