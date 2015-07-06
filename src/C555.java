import java.io.*;
import java.util.*;
public class C555 {
    public static void main(String[] args)throws IOException
    {
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        int q=sc.nextInt();
        char[] dir=new char[q];
        int[] row=new int[q];
        int[] col=new int[q];
        int[][] surface=new int[n+2][n+2];
        for(int i=0;i<=n+1;i++)
        {
            for(int j=0;j<n+1;j++)
            {
                  surface[i][j]=0;
               
            }
        }
        for(int i=0;i<=n+1;i++)
        {
            for(int j=0;j<n+1;j++)
            {
                if(i+j<=n+1)
                {
                    surface[i][j]=1;
                }
            }
        }
        for(int i=0;i<=n+1;i++)
        {
           surface[0][i]=0;
           surface[i][0]=0;
        }
        for(int i=0;i<q;i++)
        {
            col[i]=sc.nextInt();
            row[i]=sc.nextInt();
            dir[i]=sc.next().charAt(0);
            
        }
        for(int i=0;i<q;i++)
        {
            int counter=0;
            int r=row[i];
            int c=col[i];
            char d=dir[i];
            if(d=='U')
            {
              while(surface[r][c]!=0)
              {
                  counter++;
                  surface[r][c]=0;
                  r--;
              }
            }
            else
            {
                while(surface[r][c]!=0)
              {
                  counter++;
                  surface[r][c]=0;

                  c--;
              }
            }
            System.out.println(counter);
        }
        
    }
    
}
