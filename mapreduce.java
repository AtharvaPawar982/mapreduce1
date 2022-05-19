
WDriver.java

package wrd_count;

import java.io.IOException;
import org.apache.hadoop.fs.Path;    
import org.apache.hadoop.io.IntWritable;    
import org.apache.hadoop.io.Text;    
import org.apache.hadoop.mapred.FileInputFormat;    
import org.apache.hadoop.mapred.FileOutputFormat;    
import org.apache.hadoop.mapred.JobClient;    
import org.apache.hadoop.mapred.JobConf;    
import org.apache.hadoop.mapred.TextInputFormat;    
import org.apache.hadoop.mapred.TextOutputFormat;    




public class WDriver {
 
        public static void main(String[] args) throws IOException{    
            JobConf conf = new JobConf(WDriver.class);    
            conf.setJobName("WordCount");    
            conf.setOutputKeyClass(Text.class);    
            conf.setOutputValueClass(IntWritable.class);  
            conf.setJarByClass(WDriver.class);
            conf.setMapperClass(WMapper.class);    
            conf.setReducerClass(WReducer.class);
            conf.setInputFormat(TextInputFormat.class);    
            conf.setOutputFormat(TextOutputFormat.class);          
            FileInputFormat.setInputPaths(conf,new Path(args[0]));    
            FileOutputFormat.setOutputPath(conf,new Path(args[1]));    
            JobClient.runJob(conf);    
        }    
    }    



WMapper.java


package wrd_count;

import java.io.IOException;    
import java.util.StringTokenizer;    
import org.apache.hadoop.io.IntWritable;    
import org.apache.hadoop.io.LongWritable;    
import org.apache.hadoop.io.Text;    
import org.apache.hadoop.mapred.MapReduceBase;    
import org.apache.hadoop.mapred.Mapper;    
import org.apache.hadoop.mapred.OutputCollector;    
import org.apache.hadoop.mapred.Reporter;    
public class WMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>{    
       
       
    public void map(LongWritable key, Text input_text,OutputCollector<Text,IntWritable> output,    
           Reporter reporter) throws IOException{    
    Text word = new Text();
        String line = input_text.toString();    
        StringTokenizer  tokenizer = new StringTokenizer(line);    
        while (tokenizer.hasMoreTokens()){    
            word.set(tokenizer.nextToken());    
            output.collect(word, new IntWritable(1));    
        }    
    }    
   
}


WReducer.java

package wrd_count;

import java.io.IOException;    
import java.util.Iterator;    
import org.apache.hadoop.io.IntWritable;    
import org.apache.hadoop.io.Text;    
import org.apache.hadoop.mapred.MapReduceBase;    
import org.apache.hadoop.mapred.OutputCollector;    
import org.apache.hadoop.mapred.Reducer;    
import org.apache.hadoop.mapred.Reporter;    
   
public class WReducer  extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable> {    
public void reduce(Text key, Iterator<IntWritable> values,OutputCollector<Text,IntWritable> output,    
 Reporter reporter) throws IOException {    
int sum=0;    
while (values.hasNext()) {    
sum+=values.next().get();    
}    
output.collect(key,new IntWritable(sum));    
}    
}   



-----------------------------------------------------------
package ipaddress;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
//import java.util.StringTokenizer;

public class WMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> 
{

	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException 
	{
   
		    	Text word = new Text();
		        String line = value.toString();   
		        
		       String[] lineSplit = line.split(",");
		       String ip = lineSplit[0];	        
		         
		       if(ip.startsWith("10"))
		        
		       output.collect(new Text(ip), new IntWritable(1));    	        
		        	        
		    }    
		    
	}

-------------------------------********************--------------------------------



package ipaddress;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;


public class WReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> 
{

	public void reduce(Text t_key, Iterator<IntWritable> values, OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException 
	{
		Text key = t_key;
		int fcount = 0;
				
		while (values.hasNext()) 
		{
			IntWritable value = (IntWritable) values.next();
			fcount += value.get();			
		}
				
		output.collect(key, new IntWritable(fcount));
		
	}
}


-----------------------*************************---------------------

package ipaddress;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

public class WDriver {
	public static void main(String[] args) {
		JobClient my_client = new JobClient();
		JobConf job_conf = new JobConf(WDriver.class);
		job_conf.setJobName("SalePerCountry");
		job_conf.setOutputKeyClass(Text.class);
		job_conf.setOutputValueClass(IntWritable.class);
		job_conf.setMapperClass(ipaddress.WMapper.class);
		job_conf.setReducerClass(ipaddress.WReducer.class);
		job_conf.setInputFormat(TextInputFormat.class);
		job_conf.setOutputFormat(TextOutputFormat.class);

		// Set input and output directories using command line arguments, 
		//arg[0] = name of input directory on HDFS, and arg[1] =  name of output directory to be created to store the output file.
		
		FileInputFormat.setInputPaths(job_conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(job_conf, new Path(args[1]));

		my_client.setConf(job_conf);
		try {
			// Run the job 
			JobClient.runJob(job_conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Configuration conf = new Configuration();
		try
		{
			FileSystem fs = FileSystem.get(conf);
			Path p = new Path("hdfs://localhost:9000"+args[1]+"part-00000");
			FSDataInputStream in = fs.open(new Path("hdfs://localhost:9000"+args[1]+"/part-00000"));
			int max=0,i=1;
			String ip,l;
			String fip = "";
			
			if(!fs.exists(p))
			{
				System.out.println("File exits");
				
				while((l = in.readLine()) != null)
				{
					String[] arr = l.split("\t");
						if(Integer.parseInt(arr[1]) > max)
						{
							max = Integer.parseInt(arr[1]);
							fip = arr[0];
						}
					}
					
			}
				System.out.println("===========Maximum Ocuurace of IP===========");
				System.out.println("IP : " +fip);
				System.out.println("Count : " +max);
				

				
		
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
	}
}
