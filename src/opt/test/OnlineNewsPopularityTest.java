package opt.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;

public class OnlineNewsPopularityTest {
	private static Instance[] instances = null;

	private static int inputLayer = 36, hiddenLayer1 = 4, hiddenLayer2 = 1, outputLayer = 1, trainingIterations = 4000;
	private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

	private static ErrorMeasure measure = new SumOfSquaresError();

	private static DataSet set = null;

	private static String trainingFileName = "src/opt/test/onn-assignment2-train-stdscaler.txt";

	private static String testingFileName = "";

	private final static int NO_OF_TRAINING_INSTANCES = 8874;

	private final static int NO_OF_ATTRIBUTES = 36;

	private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
	private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

	private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
	private static String[] oaNames = { "RHC", "SA", "GA" };
	private static StringBuffer results = new StringBuffer("");

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void main(String[] args) throws IOException {

		instances = initializeInstances(NO_OF_TRAINING_INSTANCES, trainingFileName);
		set = new DataSet(instances); // initialize a new dataset from the instances

		initializeOptimizers();// initialize the optimizers

		for (int i = 0; i < oa.length; i++) {
			double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
			train(oa[i], networks[i], oaNames[i]); // trainer.train();
			end = System.nanoTime();
			trainingTime = end - start;
			trainingTime /= Math.pow(10, 9);

			Instance optimalInstance = oa[i].getOptimal();
			networks[i].setWeights(optimalInstance.getData());

			double predicted, actual;
			start = System.nanoTime();
			for (int j = 0; j < instances.length; j++) {
				networks[i].setInputValues(instances[j].getData());
				networks[i].run();

				predicted = Double.parseDouble(instances[j].getLabel().toString());
				actual = Double.parseDouble(networks[i].getOutputValues().toString());

				double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

			}
			end = System.nanoTime();
			testingTime = end - start;
			testingTime /= Math.pow(10, 9);

			results.append("\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances."
					+ "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
					+ df.format(correct / (correct + incorrect) * 100) + "%\nTraining time: " + df.format(trainingTime)
					+ " seconds\nTesting time: " + df.format(testingTime) + " seconds\n");
			
			System.out.println(results);
			write(oaNames[i] + "_weights.txt", networks[i].getWeights());

		}

	}

	private static void initializeOptimizers() {
		for (int i = 0; i < oa.length; i++) {
			networks[i] = factory.createClassificationNetwork(new int[] { inputLayer, hiddenLayer1, hiddenLayer2, outputLayer });
			nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
		}

		oa[0] = new RandomizedHillClimbing(nnop[0]);
		oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
		oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);
	}

	private static void crossValidate() {

	}

	private static void test() {

	}

	private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
		System.out.println("\nError results for " + oaName + "\n---------------------------");

		for (int i = 0; i < trainingIterations; i++) {
			oa.train();

			double error = 0;
			for (int j = 0; j < instances.length; j++) {
				network.setInputValues(instances[j].getData());
				network.run();

				Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
				error += measure.value(output, example);
			}

			System.out.println(df.format(error));
		}
	}

	private static Instance[] initializeInstances(int no_of_instances, String fileName) {

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

	public static void write(String filename, double[] x) throws IOException {
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
}
