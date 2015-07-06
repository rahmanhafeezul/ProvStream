import java.io.*;
import java.util.*;
public class A554 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        String s=sc.next();
        ArrayList<String> list=new ArrayList();
        for(int i=97;i<=122;i++)
        {
            char ch=(char)i;
            for(int j=0;j<=s.length();j++)
            {//System.out.println(s.substring(0, j)+" string "+s.substring(j,s.length()));
                String snew=s.substring(0, j)+ch+s.substring(j,s.length());
                if(list.contains(snew))
                {
                    
                }
                else
                {
                    list.add(snew);
                }
            }
        }
        System.out.println(list.size());
    }
    
}
