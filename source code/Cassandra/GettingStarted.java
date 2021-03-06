import com.datastax.driver.core.*;
import java.io.*;
import java.util.*;
import org.apache.commons.lang3.RandomStringUtils;
public class GettingStarted {
    Cluster cluster;
	Session session;
	long start_time,tot_time;
	static String[] key,value;
	public int ops=100000;		
	float latency;
	public void connect(String node) {
      cluster = Cluster.builder()
            .addContactPoint(node) //connects to the seed 
            .build();
	session = cluster.connect();
      Metadata metadata = cluster.getMetadata();
      System.out.printf("Connected to cluster: %s\n", 
            metadata.getClusterName());
      for ( Host host : metadata.getAllHosts() ) {
         System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
               host.getDatacenter(), host.getAddress(), host.getRack());
      }
   }
	//creates keyspace "simplex" and table "new" if it doesnt exist
	public void createSchema()
	{
		try
		{
		session.execute("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication " + 
      "= {'class':'SimpleStrategy', 'replication_factor':1};");
		session.execute("CREATE TABLE simplex.new ("+ "key varchar PRIMARY KEY," + "value varchar"+");" );

		}
		catch(Exception e)
		{
		}
	}
	//inserts key-value to pair into cassandra column family table
	public void loadData()
	{
		long throughput;
		float kilo_throughput;
		
		start_time=System.currentTimeMillis();
		for(int i=0;i<=99999;i++)			
		{
			session.execute("INSERT into simplex.new (key,value) VALUES ('"+key[i]+"',"+"'"+value[i]+"');");
		}
		tot_time=System.currentTimeMillis()-start_time;
		latency=(float)tot_time/100000;
		System.out.println("Latency for PUT: "+latency+" millisecs");
		
		//System.out.println("response time for PUT: "+tot_time+" millisecs");
		throughput=ops*1000/tot_time;
		kilo_throughput=throughput/1000;

		System.out.println("Throughput for PUT: "+kilo_throughput+" K_Ops/sec");
		
	}
	//retrieves the values for all the keys inserted.
	public void getData()
	{
		float kilo_throughput;
		
		long throughput;		
		start_time=System.currentTimeMillis();
		for(int i=0;i<=99999;i++)			
		{
			ResultSet results = session.execute("SELECT * FROM simplex.new WHERE key='"+key[i]+"'");

                //for (Row row : results) {
                //System.out.format("%s\n", row.getString("value"));
                //}
                }
		tot_time=System.currentTimeMillis()-start_time;
		latency=(float)tot_time/100000;
        		System.out.println("Latency for RETRIEVE: "+latency+" millisecs");
        	
		//System.out.println("response time for RETRIEVE: "+tot_time+" millisecs");
		throughput=ops*1000/tot_time;
		kilo_throughput=throughput/1000;

                System.out.println("Throughput for RETRIEVE: "+kilo_throughput+" K_Ops/sec");
	}
	// performs delete operations on 1M keys 
	public void deleteData()
	{
		float kilo_throughput;
		long throughput;
		start_time=System.currentTimeMillis();
                for(int i=0;i<=99999;i++)	
                {
                        ResultSet results = session.execute("DELETE FROM simplex.new WHERE key='"+key[i]+"'");
                        
               // for (Row row : results) {
                //System.out.format("%s\n", row.getString("value"));
                //}
                }
                tot_time=System.currentTimeMillis()-start_time;
		latency=(float)tot_time/100000;
        		System.out.println("Latency for DELETE: "+latency+" millisecs");
		
		//System.out.println("response time for DELETE: "+tot_time+"millisecs");
		throughput=ops*1000/tot_time;
		kilo_throughput=throughput/1000;

                System.out.println("Throughput for DELETE: "+throughput+" K_Ops/sec");
	}	
   public void close() {
      cluster.close();
   }

   public static void main(String[] args) {
      GettingStarted client = new GettingStarted();
	key=new String[100000];
	value=new String[100000];
	String seed; 	
	for(int i=0;i<=99999;i++)
	{	
		key[i]=RandomStringUtils.randomAlphanumeric(10); // random key created
		value[i]=RandomStringUtils.randomAlphanumeric(90); //random value is created
		
	}
	BufferedReader br;
		
	String currentDir=System.getProperty("user.dir");
	File f=new File(currentDir+"/config"); // read the seed address from config file.
	try{
		br=new BufferedReader(new FileReader(f));
		seed=br.readLine();	
		client.connect(seed); // the seed is the contact point to which the client driver connects
		client.createSchema();
		client.loadData();
		client.getData();
		client.deleteData();

 	 }
	catch(Exception e)
	{
	}
}
}
