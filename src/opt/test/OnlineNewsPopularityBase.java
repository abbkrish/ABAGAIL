/**
 * 
 */
package opt.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import shared.DataSet;
import shared.Instance;
import shared.writer.CSVWriter;

/**
 * @author Abishek
 *
 */
public abstract class OnlineNewsPopularityBase {

	private static CSVWriter csvWriter = null;

	protected static String trainingFileName = "src/opt/test/onn-assignment2-train-stdscaler.txt";

	protected static String testingFileName = "src/opt/test/onn-assignmet2-test-stdscaler.txt";

	protected final static int NO_OF_TRAINING_INSTANCES = 8874;

	protected final static int NO_OF_TESTING_INSTANCES = 2959;

	protected final static int NO_OF_ATTRIBUTES = 36;

	protected static Instance[] initializeInstances(int no_of_instances, String fileName) {

		double[][][] attributes = new double[no_of_instances][][];

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

			for (int i = 0; i < attributes.length; i++) {
				String line = br.readLine();
				if (line == null)
					break;
				Scanner scan = new Scanner(line);
				scan.useDelimiter(",");

				attributes[i] = new double[2][];
				attributes[i][0] = new double[NO_OF_ATTRIBUTES]; // 58 attributes
				attributes[i][1] = new double[1];

				for (int j = 0; j < NO_OF_ATTRIBUTES; j++)
					attributes[i][0][j] = Double.parseDouble(scan.next());

				attributes[i][1][0] = Double.parseDouble(scan.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Instance[] instances = new Instance[attributes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = new Instance(attributes[i][0]);
			instances[i].setLabel(new Instance(attributes[i][1][0] == 1 ? 0 : 1)); // values are either 1 or 2
		}

		return instances;
	}

	protected static DataSet getTestingDataSet() {
		Instance[] instances = initializeInstances(NO_OF_TESTING_INSTANCES, testingFileName);
		DataSet set = new DataSet(instances); // initialize a new dataset from the instances
		return set;
	}

	protected static DataSet getTrainingDataSet() {
		Instance[] instances = initializeInstances(NO_OF_TRAINING_INSTANCES, trainingFileName);
		DataSet set = new DataSet(instances); // initialize a new dataset from the instances
		return set;
	}

	protected static void write(String filename, double[] x) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < x.length; i++) {
			// Maybe:
			outputWriter.write(x[i] + "");
			// Or:
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}

	protected static void writeFields(String fileName, List<String> outputFields) throws IOException {
		String[] arr = new String[outputFields.size()];
		csvWriter = new CSVWriter(fileName, outputFields.toArray(arr));
		csvWriter.open();
	}

	protected static void writeRow(List<String> outputFields) throws IOException {
		for (String s : outputFields) {
			csvWriter.write(s);
		}
		csvWriter.nextRecord();
	}

	protected static void writeClose() throws IOException {
		csvWriter.close();
	}

}