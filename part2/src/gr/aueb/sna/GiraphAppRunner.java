package gr.aueb.sna;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.GiraphConstants;
import org.apache.giraph.examples.SimpleBoggleComputation;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.io.formats.IdWithValueTextOutputFormat;
import org.apache.giraph.io.formats.BoggleInputFormat;

import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GiraphAppRunner implements Tool {

	Configuration conf;

	private String inputPath;
	private String outputPath;

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// Commend out if you want to run with the simple 3x3 Boggle Board.
		// setInputPath("boggle.txt");
		// setOutputPath("boggleOutput");

		// Commend out if you want to run with the 4x4 Boggle Board.
		setInputPath("boggle_4x4.txt");
		setOutputPath("boggleOutput_4x4");

		GiraphConfiguration giraphConf = new GiraphConfiguration(getConf());

		giraphConf.setComputationClass(SimpleBoggleComputation.class);

		giraphConf.setVertexInputFormatClass(BoggleInputFormat.class);

		GiraphFileInputFormat.addVertexInputPath(giraphConf, new Path(getInputPath()));

		giraphConf.setVertexOutputFormatClass(IdWithValueTextOutputFormat.class);

		giraphConf.setWorkerConfiguration(0, 1, 100);

		giraphConf.setLocalTestMode(true);

		giraphConf.setMaxNumberOfSupersteps(100);

		GiraphConstants.SPLIT_MASTER_WORKER.set(giraphConf, false);
		GiraphConstants.USE_OUT_OF_CORE_GRAPH.set(giraphConf, true);

		GiraphJob job = new GiraphJob(giraphConf, getClass().getName());
		FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(getOutputPath()));

		job.run(true);

		return 1;
	}

	private String getOutputPath() {
		return this.outputPath;
	}

	private String getInputPath() {
		return this.inputPath;
	}

	private void setInputPath(String string) {
		this.inputPath = string;
	}

	private void setOutputPath(String string) {
		this.outputPath = string;

	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new GiraphAppRunner(), args);
	}
}
