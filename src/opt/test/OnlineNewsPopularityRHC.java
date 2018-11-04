package opt.test;

import java.io.IOException;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import shared.tester.AccuracyTestMetric;
import shared.writer.CSVWriter;

public class OnlineNewsPopularityRHC extends OnlineNewsPopularityBase {
	private static Instance[] trainingInstances = null;

	private static Instance[] testingInstances = null;

	private static int inputLayer = 36, hiddenLayer1 = 4, hiddenLayer2 = 1, outputLayer = 1, trainingIterations = 5000;
	private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

	private static ErrorMeasure measure = new SumOfSquaresError();

	private static DataSet trainingSet = null;

	private static DataSet testSet = null;

	private static BackPropagationNetwork network = new BackPropagationNetwork();

	private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

	private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[1];
	private static String oaNames = "RHC";
	private static StringBuffer results = new StringBuffer("");

	private static DecimalFormat df = new DecimalFormat("0.000");

	private static AccuracyTestMetric aMetric = new AccuracyTestMetric();

	private Double trainError = null;

	private Double testError = null;

	public static void main(String[] args) throws IOException {

		trainingSet = getTrainingDataSet();
		testSet = getTestingDataSet();
		trainingInstances = trainingSet.getInstances();
		testingInstances = testSet.getInstances();

		initializeOptimizers();// initialize the optimizers
		List<String> fields = Arrays.asList("iteration", "trainError", "testError");

		writeFields("Error " + oaNames + ".txt", fields);

		double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
		train(oa[0], network, oaNames); // trainer.train(); //train each of the neural networks with the
										// respective randomized algos
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);

		Instance optimalInstance = oa[0].getOptimal();
		network.setWeights(optimalInstance.getData());

		double predicted, actual;
		start = System.nanoTime();
		for (int j = 0; j < trainingInstances.length; j++) {
			network.setInputValues(trainingInstances[j].getData());
			network.run();

			actual = Double.parseDouble(trainingInstances[j].getLabel().toString());
			predicted = Double.parseDouble(network.getOutputValues().toString());

			double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

		}
		end = System.nanoTime();
		testingTime = end - start;
		testingTime /= Math.pow(10, 9);

		results.append("\nResults for " + oaNames + ": \nCorrectly classified " + correct + " instances."
				+ "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
				+ df.format(correct / (correct + incorrect) * 100) + "%\nTraining time: " + df.format(trainingTime)
				+ " seconds\nTesting time: " + df.format(testingTime) + " seconds\n");

		System.out.println(results);

		// Testing Results
		results = new StringBuffer("");
		correct = 0;
		incorrect = 0;
		for (int j = 0; j < testingInstances.length; j++) {
			network.setInputValues(testingInstances[j].getData());
			network.run();

			predicted = Double.parseDouble(testingInstances[j].getLabel().toString());
			actual = Double.parseDouble(network.getOutputValues().toString());

			double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

		}
		end = System.nanoTime();
		testingTime = end - start;
		testingTime /= Math.pow(10, 9);

		results.append("\nResults for " + oaNames + ": \nCorrectly classified " + correct + " instances."
				+ "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
				+ df.format(correct / (correct + incorrect) * 100) + "%\nTraining time: " + df.format(trainingTime)
				+ " seconds\nTesting time: " + df.format(testingTime) + " seconds\n");

		System.out.println(results);

		write(oaNames + "_weights.txt", network.getWeights());

	}

	private static void initializeOptimizers() {
		for (int i = 0; i < oa.length; i++) {
			network = factory
					.createClassificationNetwork(new int[] { inputLayer, hiddenLayer1, hiddenLayer2, outputLayer });
			nnop[i] = new NeuralNetworkOptimizationProblem(trainingSet, network, measure);
		}

		oa[0] = new RandomizedHillClimbing(nnop[0]);
		// oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
		// oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);
	}

	private static void crossValidate() {
		trainingInstances = initializeInstances(NO_OF_TRAINING_INSTANCES, trainingFileName);
		trainingSet = new DataSet(trainingInstances); // initialize a new dataset from the instances
	}

	private static Double test(BackPropagationNetwork network) {

		Instance[] instances = testSet.getInstances();
		double error = 0;
		for (int j = 0; j < instances.length; j++) {
			network.setInputValues(instances[j].getData());
			network.run();

			Instance output = instances[j].getLabel();
			Instance example = new Instance(network.getOutputValues());
			example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));

			error += measure.value(output, example);
		}

		return error / instances.length;
	}

	private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName)
			throws IOException {
		System.out.println("\nError results for " + oaName + "\n---------------------------");

		for (int i = 0; i < trainingIterations; i++) {
			oa.train();

			double error = 0;
			for (int j = 0; j < trainingInstances.length; j++) {
				network.setInputValues(trainingInstances[j].getData());
				network.run();

				Instance output = trainingInstances[j].getLabel();
				Instance example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));

				error += measure.value(output, example);
			}

			Double testError = test(network);
			List<String> row = Arrays.asList(Integer.toString(i), Double.toString(error / trainingInstances.length),
					Double.toString(testError));
			writeRow(row);
			System.out.println("iteration " + i + " " + df.format(error));
		}

		writeClose();
	}

}