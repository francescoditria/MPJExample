import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.Connection;
import mpi.MPI;


public class Database {

	public static void main(String[] args) {

		MPI.Init(args);
        int myRank=MPI.COMM_WORLD.Rank();
        int size=MPI.COMM_WORLD.Size();
        double buffer[] = new double[1];

        
        String host="localhost";
        String port="3306";
        String user="root";
        String pwd="******";
        String database="northwind";
        String tableName="order_details";
        String function="sum";
        String column="quantity";
        String jdbc="jdbc:mysql://"+host+":"+port+"/"+database;
        
        int i;
        int tag=100;
        if(myRank==0)
        {
        	try 
        	{
        		Class.forName("com.mysql.jdbc.Driver");
                Connection connection = (Connection) DriverManager.getConnection(jdbc, user, pwd);
    			Statement statement=connection.createStatement();
    			//estrazione del numero di righe dai metadati
    		    String metaq="select TABLE_NAME, TABLE_ROWS from information_schema.TABLES where TABLE_SCHEMA='"+database+"' and TABLE_NAME='"+tableName+"'";
    		    //System.out.println(metaq);
    	        ResultSet rs = statement.executeQuery(metaq);
    	        rs.next();
    	        String tr=rs.getString("TABLE_ROWS");
    	        int rows=Integer.parseInt(tr);
                buffer[0] = Double.valueOf(rows);//rows;
            
                //invio numero di righe della tabella
                for(i=1;i<size;i++)
                {
                	MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.DOUBLE, i, tag);	
                }
                //lettura risultati parziale e stima risposta finale
                double parziale=0;
                for(i=1;i<size;i++) {
                    MPI.COMM_WORLD.Recv(buffer, 0, buffer.length,MPI.DOUBLE, i, tag);
    				double fattore=(double) (size-1) /(i+1);
                    parziale=parziale+buffer[0];
                    double answ=parziale*fattore;
                    System.out.println("Estimation ["+(i*100/(size-1))+"%]:\t"+parziale);
                } 
                System.out.println("Final:\t"+parziale);
                
                
    		}catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
    		}
        }
        else
        {
            MPI.COMM_WORLD.Recv(buffer, 0, buffer.length,MPI.DOUBLE, 0, tag);
            int rows=(int) buffer[0];
            //System.out.println(myRank+","+rows);
            int n=rows / (size-1);	//righe da estrarre
    		int m=rows % (size-1); //resto di righe da estrarre		
			
    		int start = n*(myRank-1);
			int end = n;
			if(myRank==size-1) end=end+m;

            try 
        	{
            	
        		Class.forName("com.mysql.jdbc.Driver");
                Connection connection = (Connection) DriverManager.getConnection(jdbc, user, pwd);
    			Statement statement=connection.createStatement();
    			String query="select "+function+"("+column+") from (select "+column+" from "+tableName+" LIMIT "+start+","+end+") as t";
                //System.out.println(myRank+","+query);
    			ResultSet rs = statement.executeQuery(query);
    	        rs.next();
    	    	double parziale=rs.getDouble(1);
    	    	buffer[0]=parziale;
                MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.DOUBLE, 0, tag);			    	        
                
    		}catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
    		}
            
        }
                       
        MPI.Finalize();		
	}

}
