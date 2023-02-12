import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import mpi.MPI;
import mpi.Status;

public class Equation {

	public static void main(String[] args) {
		
		MPI.Init(args);
		int myRank=MPI.COMM_WORLD.Rank();
		int size=MPI.COMM_WORLD.Size();
		
		int buffer [] = new int[4];
		int tag=100;
		int a,b,c,delta;
		
		if(myRank==0)
		{
		String fileName="data.txt";
		File file = new File(fileName);
	    try {
	        Scanner scanner = new Scanner(file);
	        while (scanner.hasNextLine()) {
	            String line=scanner.nextLine();
	            String[] params=line.split(",");
	            a=Integer.parseInt(params[0]);
	            b=Integer.parseInt(params[1]);
	            c=Integer.parseInt(params[2]);
	            delta=(b*b)-(4*a*c);
	            if(delta>0)
	            {
	            	buffer[0] = a;
	            	buffer[1] = b;
	            	buffer[2] = c;
	            	buffer[3] = delta;
	            
	    			MPI.COMM_WORLD.Send(buffer, 0, buffer.length, MPI.INT, 1, tag);
	    			MPI.COMM_WORLD.Send(buffer, 0, buffer.length, MPI.INT, 2, tag);
	            }
	        }
	        
        	buffer[0] = 0;
        	buffer[1] = 0;
        	buffer[2] = 0;
			MPI.COMM_WORLD.Send(buffer, 0, buffer.length, MPI.INT, 1, tag);
			MPI.COMM_WORLD.Send(buffer, 0, buffer.length, MPI.INT, 2, tag);
			scanner.close();
			
	    }	    	   
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		}
		
		if(myRank!=0)
		{
			float x=0;
			boolean cont=true;
			do
			{
			MPI.COMM_WORLD.Recv(buffer, 0, buffer.length,MPI.INT, 0, tag);
			a=buffer[0];
			b=buffer[1];
			c=buffer[2];
			delta=buffer[3];
			
			if(a==0 && b==0 && c==0) cont=false;
			else
			{
			
				if(myRank==1)
					x = (float) ((-b + Math.sqrt(delta))/(2*a));
				
				if(myRank==2)
					x = (float) ((-b - Math.sqrt(delta))/(2*a));
			
	        	System.out.println(a+"\t"+b+"\t"+c+"\tx"+myRank+": "+x);
			}
    		
			}while(cont);	
			
		}

		MPI.Finalize();
					
	}

}
