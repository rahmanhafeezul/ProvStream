import java.io.*;
import java.util.*;

public class B556 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        String s="";
        for(int i=0;i<n;i++)
        {
            s+=String.valueOf(i);
        }
        int arr[]=new int[n];
        for(int i=0;i<n;i++)
        {
            arr[i]=sc.nextInt();
        }
        boolean same=true;
        for(int i=0;i<n;i++)
        {//System.out.println(IntArrayToString(arr));
          if(IntArrayToString(arr).equals(s))
          {
              same=false;
              break;
          }
          else
          {
              for(int j=0;j<n;j++)
              {if(j%2==0)
                  arr[j]=(arr[j]+1)%n;
              else
                  arr[j]=(arr[j]+n-1)%n;
              }
          }
        }
        if(same==false)
        {
            System.out.println("Yes");
        }
        else
        {
            System.out.println("No");
        }
    }
    private static String IntArrayToString(int[] array) {
        String strRet="";
        for(int i : array) {
            strRet+=Integer.toString(i);
        }
        return strRet;
    }
    
}
