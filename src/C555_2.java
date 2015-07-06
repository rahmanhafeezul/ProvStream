import java.io.*;
import java.util.*;
public class C555_2 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int q=sc.nextInt();
        int[] row=new int[q+1];
        int[] col=new int[q+1];
        char[] dir=new char[q+1];
        row[0]=0;
        col[0]=0;
        dir[0]='o';
        for(int i=0;i<q;i++)
        {
            
            col[i+1]=sc.nextInt();
            row[i+1]=sc.nextInt();
            dir[i+1]=sc.next().charAt(0);
            int min=Integer.MAX_VALUE;
            if(dir[i+1]=='U')
            {
               int c=row[i+1];
               int b=col[i+1];
               for(int j=0;j<=i;j++)
               {
                 if(dir[j]!='L'){
                     
                 
                 if(c-row[j]>=0 && b>=col[j])
                 {
                     min=Math.min(min, c-row[j]);
                 }
               }
               else
               {
                  if(c-row[j]>=0)
                 {
                     min=Math.min(min, c-row[j]);
                 }     
                       }
               }}
            else
            {
                int c=col[i+1];
                int b=row[i+1];
                for(int j=0;j<=i;j++)
               {
                   if(dir[j]!='U'){
                 if(c-col[j]>=0 && b>=row[j])
                 {
                     min=Math.min(min, c-col[j]);
                 }
               }
                   else
                   {if(c-col[j]>=0)
                 {
                     min=Math.min(min, c-col[j]);
                 }
                       
                   }
            }
            
        }
        System.out.println(min);        
    }
    
}}
