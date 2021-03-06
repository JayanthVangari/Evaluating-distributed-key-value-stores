 import java.io.*;
import java.util.*;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import org.apache.commons.lang3.RandomStringUtils;

public class Oracle
{
	static String[] majorcomponents;
	final KVStore kv;
	static String STORENAME;
	static String[] hosts;
	static int host;
	static String[] data;
	public int ops=100000;		
	long start_time,tot_time;
	float latency;
	
	Oracle(int nodes)
	{
		hosts=new String[nodes];
		String k;
		String currentDir=System.getProperty("user.dir");
		//read config file
		File f=new File(currentDir+"/config");
		try{
			BufferedReader br=new BufferedReader(new FileReader(f));
			int j=0;
			while((k=br.readLine())!=null)
			{
					hosts[j]=k.toString(); //host addresses in the cluster that are read through config file
					j++;
			}
		}catch(Exception e)
		{
				e.printStackTrace();
		}
		kv=KVStoreFactory.getStore(new KVStoreConfig(STORENAME,hosts));	
	}
	public static void main(String[] args)
	{
	
		majorcomponents=new String[100000];
		data=new String[100000];
		for(int i=0;i<=99999;i++)
		{	
			majorcomponents[i]=RandomStringUtils.randomAlphanumeric(10); //creates random keys
			data[i]=RandomStringUtils.randomAlphanumeric(90); //creates random values
		
		}
	
		STORENAME=args[0]; //store name through command-line
		host=Integer.parseInt(args[1]); //number of nodes in the cluster , enter it through command-line
		Oracle client = new Oracle(host);

 		client.put();
		client.get();
		client.delete();
		System.exit(0);
	}
 	
	//performs insert of key value pair 1Million operations
	public void put()
	{
		float kilo_throughput;		
		long throughput;
		Key key;
		Value value;
		start_time=System.currentTimeMillis();
		for(int i=0;i<=99999;i++)			
		{
			key=Key.createKey(majorcomponents[i]);
			value=Value.createValue(data[i].getBytes());
			kv.put(key,value);
		}
		tot_time=System.currentTimeMillis()-start_time;
		latency=(float)tot_time/100000;
		System.out.println("Latency for PUT: "+latency+" millisecs");
		throughput=ops*1000/tot_time;
		kilo_throughput=(float)throughput/1000;
		System.out.println("Throughput for PUT: "+kilo_throughput+" K_Ops/sec");
		
	}
	//retrieves the value for 1 Million keys inserted
	public void get()
	{
		float kilo_throughput;		
		long throughput;
		Key key;
                Value value;
		ValueVersion v;
                String data_get;
                start_time=System.currentTimeMillis();
               for(int i=0;i<=99999;i++)			
                {
                        key=Key.createKey(majorcomponents[i]);
                        v=kv.get(key);
			value=v.getValue();
			data_get=new String(value.getValue());
                }
                tot_time=System.currentTimeMillis()-start_time;
                latency=(float)tot_time/100000;
        		System.out.println("Latency for RETRIEVE: "+latency+" millisecs");
        		throughput=ops*1000/tot_time;
        		kilo_throughput=(float)throughput/1000;
                System.out.println("Throughput for RETRIEVE: "+kilo_throughput+" K_Ops/sec");
	}
	//deletes the key-value pairs in the kvstore.
	public void delete()
	{
		float kilo_throughput;		
		long throughput;
		Key key;
                Value value;
                String data;
                start_time=System.currentTimeMillis();
                for(int i=0;i<=99999;i++)			
                {
                      	key=Key.createKey(majorcomponents[i]);
                        kv.delete(key);
                }
                tot_time=System.currentTimeMillis()-start_time;
                latency=(float)tot_time/100000;
        		System.out.println("Latency for DELETE: "+latency+" millisecs");
		        throughput=ops*1000/tot_time;
		kilo_throughput=(float)throughput/1000;
                System.out.println("Throughput for DELETE: "+kilo_throughput+" K_Ops/sec");
	}
}


