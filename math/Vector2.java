
import mpi.MPI;
import mpi.Status;

public class Vector2 {

	public static void main(String[] args) {

		MPI.Init(args);
		int myRank=MPI.COMM_WORLD.Rank();
		int size=MPI.COMM_WORLD.Size();
		
		int buffer [] = new int[10];
		int[] number={1,1,1,1,1,1};
		int tag=100;
		int i;
		int sommaParziale=number[myRank*2]+(number[myRank*2+1]);
		//System.out.println(myRank+","+sommaParziale);
		
		if(myRank!=0)
		{
			buffer[0] = sommaParziale;
			MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.INT, 0, tag);
		}
		else
		{
			for(i=1;i<size;i++) {
				MPI.COMM_WORLD.Recv(buffer, 0, buffer.length,MPI.INT, i, tag);
				sommaParziale+=buffer[0];
			}
			System.out.println(sommaParziale);
		}

		MPI.Finalize();
	}


}


