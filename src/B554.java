import java.io.*;
import java.util.*;
public class B554 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        String[] arr=new String[n];
        for(int i=0;i<n;i++)
        {
            arr[i]=sc.next();
        }
        int res=0;
        for(int i=0;i<n;i++)
        {
            int count=0;
            for(int j=0;j<n;j++)
            {
                if(arr[i].equals(arr[j]))
                    count++;
            }
            res=Math.max(res, count);
        }
        System.out.println(res);
        
    }
    
}
