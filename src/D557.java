import java.io.*;
import java.util.*;
public class D557 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int m=sc.nextInt();
        ArrayList<Integer> g[]=new ArrayList[n];
        for(int i=0;i<n;i++)
        {
            g[i]=new ArrayList();
        }
        for(int i=0;i<m;i++)
        {
            int a =sc.nextInt()-1;
            int b=sc.nextInt()-1;
            g[a].add(b);
            g[b].add(a);
        }
        int color[]=new int[n];
        long ways=0;
        Arrays.fill(color, -1);
        boolean twocolor=true;
        for(int i=0;i<n;i++)
        {
            if(color[i]!=-1)
                continue;
            color[i]=0;
            Queue<Integer> q=new LinkedList();
            q.add(i);
            long zeroes = 0, ones = 0;
            while(!q.isEmpty())
            {
                int at=q.poll();
                if(color[at]==0)zeroes++;
                else
                    ones++;
                for(int e: g[at])
                {
                    if(color[e]==-1)
                    {
                        color[e]=1-color[at];
                        q.add(e);
                    }
                    else if(color[e]==color[at])
                    {
                        twocolor=false;
                        break;
                    }
                }
            }
            ways+=ones*(ones-1)/2;
            ways+=zeroes*(zeroes-1)/2;
            
        }
        if(!twocolor)
        {
            System.out.println("0 1");
        }
        else if(m==0)
        {
            System.out.println("3 "+(long)n * (n-1) * (n-2) / 6);
        }
        else
        {
            if(ways>0)
            {
               System.out.println("1 "+ways); 
            }
            else
            {
                System.out.println("2 "+(long)(n-2)*m);
            }
        }
        
    }
    
}
