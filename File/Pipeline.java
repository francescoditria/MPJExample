
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import mpi.MPI;
import mpi.Status;

public class Pipeline {

	public static void main(String[] args) throws IOException {

		MPI.Init(args);
		int myRank=MPI.COMM_WORLD.Rank();
		int size=MPI.COMM_WORLD.Size();
		
		String fileName="dati.txt";
		
		if(myRank==1)
		{
			System.out.println("starting "+myRank);
			FileWriter fw = new FileWriter(fileName, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter writer = new PrintWriter(bw);
		    
			int i;
			writer.append("myRank is "+myRank+"\n");
			writer.close();
		}
		MPI.COMM_WORLD.Barrier();	

		if(myRank==2)
		{
			System.out.println("starting "+myRank);
			FileWriter fw = new FileWriter(fileName, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter writer = new PrintWriter(bw);

			int i;
			writer.append("myRank is "+myRank+"\n");
			writer.close();
		}
		MPI.COMM_WORLD.Barrier();	

		if(myRank==0)
		{
			File file = new File(fileName);
		    Scanner scanner = new Scanner(file);
	        while (scanner.hasNextLine()) 
	        {
	            String line=scanner.nextLine();
	            System.out.println(line);
	        }
            System.out.println("end");

		}

		MPI.Finalize();
	}


}


