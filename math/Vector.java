import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import mpi.MPI;
import mpi.Status;

public class Vector {

	public static void main(String[] args) {

		MPI.Init(args);
        int myRank=MPI.COMM_WORLD.Rank();
        int size=MPI.COMM_WORLD.Size();
        
        int buffer [] = new int[10];
        int sommaParziale=0;
        int tag=100;
        
        String fileName="integers.txt";
		File file = new File(fileName);
		int i=0;
		try 
		{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line=scanner.nextLine();
		        if(i%size==myRank)
		        {
			        int num=Integer.parseInt(line);
		        	sommaParziale=sommaParziale+num;
		        }
		        i++;
		     
		    }
		}
		catch (FileNotFoundException e) {
		        e.printStackTrace();
		}
			    	
        
        if(myRank!=0)
        {
              buffer[0] = sommaParziale;
              MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.INT, 0, tag);
        }
        else
        {
          Status status;
          for(i=1;i<size;i++) {
              status = MPI.COMM_WORLD.Recv(buffer, 0, buffer.length,MPI.INT, i, tag);
              sommaParziale=sommaParziale+buffer[0];
          } 
          System.out.println(sommaParziale);
        }

        MPI.Finalize();
  }
		
}

