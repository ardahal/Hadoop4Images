package gaussian_blur;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class GaussianMapper extends Mapper<LongWritable, BufferedImage, LongWritable, BufferedImage>{

	public void map(LongWritable key, BufferedImage value, Context context) 
			throws IOException, InterruptedException {
		
		System.out.println("map started");
		
		//create the detector 
		GaussianBlur filter = new GaussianBlur();
		
		//adjust its parameters as desired 
		
		
		//apply it to an image 
		filter.setSourceImage(value); 
		filter.process();
		
		System.out.println("Edge Detected chunk " + key.get());
		
		BufferedImage edges = filter.getEdgesImage();
		
		if(edges == null) {
			System.out.println("edge detect made a null");
		}

		//context.write(key, edges);
		
		FileSystem dfs = FileSystem.get(context.getConfiguration());
		Path newimgpath = new Path(context.getWorkingDirectory(), context.getJobID().toString()+"/"+key.get());
		dfs.createNewFile(newimgpath);
		FSDataOutputStream ofs = dfs.create(newimgpath);
		ImageIO.write(edges, "jpg", ofs);
	}
}

