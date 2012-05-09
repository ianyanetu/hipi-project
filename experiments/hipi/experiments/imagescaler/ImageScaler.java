/*
Ike Anyanetu
ImageScaler class
This class implements the scaling function
*/

package hipi.experiments.imagescaler;

import hipi.experiments.mapreduce.*;
import hipi.experiments.mapreduce.JPEGFileInputFormat;
import hipi.experiments.mapreduce.JPEGRecordReader;
import hipi.image.FloatImage;
import hipi.image.ImageHeader;
import hipi.image.io.ImageEncoder;
import hipi.image.io.JPEGImageUtil;
import hipi.image.io.CodecManager;
import hipi.image.io.ImageDecoder;
import hipi.imagebundle.mapreduce.ImageBundleInputFormat;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.apache.hadoop.io.BooleanWritable;

public class ImageScaler extends Configured implements Tool{
	static int scale_input=0;
	static float scale_value=0;

	public static class MyMapper extends Mapper<ImageHeader, FloatImage, BooleanWritable, LongWritable>
	{	
		private Path path;
		private FileSystem fileSystem;
		public void setup(Context jc) throws IOException
		{
			Configuration conf = jc.getConfiguration();
			fileSystem = FileSystem.get(conf);
			path = new Path( conf.get("imagescaler.outdir"));
			fileSystem.mkdirs(path);
		}
		public void map(ImageHeader key, FloatImage value, Context context) throws IOException, InterruptedException{
			if (value != null) {
				value.scale(scale_value);
				context.write(new BooleanWritable(true), new LongWritable(value.hashCode()));
				//If we later decide we want to output the image
				JPEGImageUtil encoder = JPEGImageUtil.getInstance();
				
				//a neccessary step to avoid files with duplicate hash values
				Path outpath = new Path(path + "/" + value.hashCode() + ".jpg");
				while(fileSystem.exists(outpath)){
					String temp = outpath.toString();
					outpath = new Path(temp.substring(0,temp.lastIndexOf('.')) + "1.jpg"); 
				}
				
				FSDataOutputStream os = fileSystem.create(outpath);
				encoder.encodeImage(value, key, os);
				os.flush();
				os.close();
				
			}
			else
				context.write(new BooleanWritable(false), new LongWritable(0));
		}
	}
	public static class MyReducer extends Reducer<BooleanWritable, LongWritable, BooleanWritable, LongWritable> {
		// Just the basic indentity reducer... no extra functionality needed at this time
		public void reduce(BooleanWritable key, Iterable<LongWritable> values, Context context) 
		throws IOException, InterruptedException
		{
			System.out.println("REDUCING...");
			for (LongWritable temp_hash : values) {
				{	    
					context.write(key, temp_hash);
				}

			}
		}
	}

	public int run(String[] args) throws Exception
	{	

		// Read in the configurations
		if (args.length < 4)
		{
			System.out.println("Usage: imagescaler <inputdir> <outputdir> <scale_percent> "+
				"<input type: hib, har, sequence, small_files>");
			System.exit(0);
		}


		// Setup configuration
		Configuration conf = new Configuration();

		// set the dir to output the jpegs to
		String outputPath = args[1];
		String input_file_type = args[3];
		conf.setStrings("imagescaler.outdir", outputPath);
		conf.setStrings("imagescaler.filetype", input_file_type);
		

		scale_input = Integer.parseInt(args[2]);
		//convert to scaling factor (e.g. input of '50' means a factor of .5)
		scale_value = (float)((float)scale_input * .01);

		Job job = new Job(conf, "imagescaler");
		job.setJarByClass(ImageScaler.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);

		// Set formats
		job.setOutputKeyClass(BooleanWritable.class);
		job.setOutputValueClass(LongWritable.class);     
		
		//job.setOutputFormatClass(BinaryOutputFormat.class);
		//job.setMapOutputKeyClass(NullWritable.class);
		//job.setMapOutputValueClass(FloatImage.class);

		// Set out/in paths
		removeDir(outputPath, conf);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		JPEGFileInputFormat.addInputPath(job, new Path(args[0]));

		if(input_file_type.equals("hib"))
			job.setInputFormatClass(ImageBundleInputFormat.class);
		else if(input_file_type.equals("har"))
			job.setInputFormatClass(JPEGFileInputFormat.class);
		else if(input_file_type.equals("small_files"))
			job.setInputFormatClass(JPEGFileInputFormat.class);
		else if (input_file_type.equals("sequence"))
			job.setInputFormatClass(JPEGSequenceFileInputFormat.class);
		else{
			System.out.println("Usage: imagescaler <inputdir> <outputdir> <scale_percent> "+
				"<input type: hib, har, sequence, small_files>");
			System.exit(0);			
		}

		//conf.set("mapred.job.tracker", "local");
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		return 0;
	}
	public static void removeDir(String path, Configuration conf) throws IOException {
		Path output_path = new Path(path);

		FileSystem fs = FileSystem.get(conf);

		if (fs.exists(output_path)) {
			fs.delete(output_path, true);
		}
	}
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new ImageScaler(), args);
		System.exit(res);
	}
}
