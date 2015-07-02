/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;
/**
 *
 * @author HafeezulRahman
 */
public class A557 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int min1=sc.nextInt();
        int max1=sc.nextInt();
        int min2=sc.nextInt();
        int max2=sc.nextInt();
        int min3=sc.nextInt();
        int max3=sc.nextInt();
        int first,second,third=0;
        first=min1;
        second=min2;
        third=min3;
        n=n-first;
        n=n-second;
        n=n-third;
        max1=max1-min1;
        max2=max2-min2;
        max3=max3-min3;
        min1=0;
        min2=0;
        min3=0;
        if(n>0)
        {
            if(n-min1 < max1-min1)
            {
                first=first+n-min1;
                n=n-(n-min1);
            }
            else
            {
                first=first+max1-min1;
                n-=max1-min1;
            }
            //System.out.println(n+"first");
            if(n>0)
            {
                if(n-min2 < max2-min2)
                {
                second=second+n-min2;
                n=n-(n-min2);
                }
            else
                {
                second=second+max2-min2;
                n-=max2-min2;
                }
              //  System.out.println(n + "second");
                if(n>0)
                {
                  if(n-min3 < max3-min3)
                {
                third=third+n-min3;
                n=n-(n-min3);
                }
            else
                {
                third=third+max3-min3;
                n-=max3-min3;
                }  
                }
                //System.out.println(n+" third");
            }
        }
        System.out.println(first+" "+second+" "+third);
    }
    
}
