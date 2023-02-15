
import mpi.MPI;
import mpi.Status;

public class Vector3 {

	public static void main(String[] args) {

		MPI.Init(args);
		int myRank=MPI.COMM_WORLD.Rank();
		int size=MPI.COMM_WORLD.Size();
		
		int n=8;
		int buffer [] = new int[n];
		int sendbuf [] = new int[1];
		int recvbuf [] = new int[1];
		
		int tag=100;
		int i;
		
		if(myRank==0)
		{
			int j;
			for(j=0;j<n;j++)
				buffer[j]=1; //riempio il vettore
		
		}
		//invio in broadcast
		MPI.COMM_WORLD.Bcast(buffer, 0, n, MPI.INT, 0);
		
		//calcolo somma parziale
		sendbuf[0]=buffer[myRank*2]+(buffer[myRank*2+1]);
		
		//operazione di riduzione dei dati
		MPI.COMM_WORLD.Reduce(sendbuf, 0, recvbuf, 0, 1, MPI.INT, MPI.SUM, 0);
		
		//stampa del risultato finale
		if(myRank==0)
			System.out.println(recvbuf[0]);
		
		MPI.Finalize();
	}


}


