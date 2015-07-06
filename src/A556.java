import java.io.*;
import java.util.*;
public class A556 {
    public static void main(String[] args)throws IOException
    {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        int n=Integer.parseInt(br.readLine());
        String s = br.readLine();
        int ones=0,zeros=0;
        for(int i=0;i<s.length();i++ )
        {
            if(s.charAt(i)=='0')
            {
                zeros++;
            }
            else{
                ones++;
            }
        }
        System.out.println((int)(Math.abs(ones-zeros)));
        
    }
    
}
