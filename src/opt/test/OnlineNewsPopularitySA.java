package opt.test;

import java.io.IOException;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import opt.OptimizationAlgorithm;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;

public class OnlineNewsPopularitySA extends OnlineNewsPopularityBase {
	private static Instance[] trainingInstances = null;

	private static Instance[] testingInstances = null;

	private static int inputLayer = 36, hiddenLayer1 = 4, hiddenLayer2 = 1, outputLayer = 1, trainingIterations = 5000;
	private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

	private static ErrorMeasure measure = new SumOfSquaresError();

	private static DataSet trainingSet = null;

	private static DataSet testSet = null;

	private static BackPropagationNetwork network = new BackPropagationNetwork();

	private static NeuralNetworkOptimizationProblem nnop = null;

	private static OptimizationAlgorithm oa = null;
	private static String oaNames = "SA";
	private static StringBuffer results = new StringBuffer("");

	private static DecimalFormat df = new DecimalFormat("0.000");

	
	private static Double temperature = 1E8;

	private static Double coolingExponent = 0.6;

	public static void main(String[] args) throws IOException {

		// check cmd line for hyperparams
		if (args.length != 0) {
			temperature = Double.valueOf(args[0]);
			coolingExponent = Double.valueOf(args[1]);
		}

		trainingSet = getTrainingDataSet();
		testSet = getTestingDataSet();
		trainingInstances = trainingSet.getInstances();
		testingInstances = testSet.getInstances();

		initializeOptimizers();// initialize the optimizers
		List<String> fields = Arrays.asList("iteration", "trainError", "testError");

		writeFields("Error_" + temperature + "_" + coolingExponent + "_" + oaNames + ".csv", fields);

		double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
		train(oa, network, oaNames); // trainer.train(); //train each of the neural networks with the
										// respective randomized algos
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);

		Instance optimalInstance = oa.getOptimal();
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

		write(oaNames + "_" + temperature + "_" + coolingExponent + "_weights.txt", network.getWeights());

	}

	private static void initializeOptimizers() {
		network = factory
				.createClassificationNetwork(new int[] { inputLayer, hiddenLayer1, hiddenLayer2, outputLayer });
		nnop = new NeuralNetworkOptimizationProblem(trainingSet, network, measure);

		oa = new SimulatedAnnealing(temperature, coolingExponent, nnop);
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