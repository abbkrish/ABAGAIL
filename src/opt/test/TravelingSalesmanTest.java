package opt.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;
import shared.writer.CSVWriter;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {

	private static CSVWriter csvWriter = null;

	private static List<String> fields = Arrays.asList("error", "fitness", "time", "cities");

	private static List<Double> error = new ArrayList<>();

	private static List<Double> fitness = new ArrayList<>();

	/** The n value */
	private static int N = 50;

	private static int NO_OF_ITERATIONS = 1000;

	/**
	 * The test main
	 * 
	 * @param args
	 *            ignored
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Random random = new Random();
		// create the random points
		if (args.length > 0) {
			N = Integer.valueOf(args[0]);
			NO_OF_ITERATIONS = Integer.valueOf(args[1]);
		}

		double[][] points = new double[N][2];
		for (int i = 0; i < points.length; i++) {
			points[i][0] = random.nextDouble();
			points[i][1] = random.nextDouble();
		}
		// for rhc, sa, and ga we use a permutation based encoding
		TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
		Distribution odd = new DiscretePermutationDistribution(N);
		NeighborFunction nf = new SwapNeighbor();
		MutationFunction mf = new SwapMutation();
		CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
		HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
		GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

		
		//HILL CLIMBING
		RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
		FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 1);
		double start = System.nanoTime();
		error = new ArrayList<>();
		fitness = new ArrayList<>();
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			error.add(fit.train());
			fitness.add(ef.value(rhc.getOptimal()));

		}
		fit.train();
		double end = System.nanoTime();
		double trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);
		System.out.println("Randomized Hill Climbing Results");
		System.out.println(ef.value(rhc.getOptimal()));
		writeFields("TravelingSalesmanRHC_" + N + "_.csv", fields);
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			List<String> row = Arrays.asList(Double.toString(error.get(i)), Double.toString(fitness.get(i)),
					Double.toString(trainingTime), Double.toString(N));
			writeRow(row);
		}
		writeClose();

		//SA
		SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
		fit = new FixedIterationTrainer(sa, 1);
		start = System.nanoTime();
		error = new ArrayList<>();
		fitness = new ArrayList<>();
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			error.add(fit.train());
			fitness.add(ef.value(sa.getOptimal()));

		}
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);
		System.out.println("Simulated Annealing Results");
		writeFields("TravelingSalesmanSA_" + N + "_.csv", fields);
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			List<String> row = Arrays.asList(Double.toString(error.get(i)), Double.toString(fitness.get(i)),
					Double.toString(trainingTime), Double.toString(N));
			writeRow(row);
		}
		writeClose();
		System.out.println(ef.value(sa.getOptimal()));
		
		
		
		//GA
		StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
		fit = new FixedIterationTrainer(ga, 1);
		start = System.nanoTime();
		error = new ArrayList<>();
		fitness = new ArrayList<>();
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			error.add(fit.train());
			fitness.add(ef.value(ga.getOptimal()));

		}
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);
		writeFields("TravelingSalesmanGA_" + N + "_.csv", fields);
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			List<String> row = Arrays.asList(Double.toString(error.get(i)), Double.toString(fitness.get(i)),
					Double.toString(trainingTime), Double.toString(N));
			writeRow(row);
		}
		writeClose();
		System.out.println("Genetic Algorithm Results");
		System.out.println(ef.value(ga.getOptimal()));

		// for mimic we use a sort encoding
		ef = new TravelingSalesmanSortEvaluationFunction(points);
		int[] ranges = new int[N];
		Arrays.fill(ranges, N);
		odd = new DiscreteUniformDistribution(ranges);
		Distribution df = new DiscreteDependencyTree(.1, ranges);
		ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

		MIMIC mimic = new MIMIC(200, 100, pop);
		fit = new FixedIterationTrainer(mimic, 1000);
		fit.train();
		System.out.println(ef.value(mimic.getOptimal()));

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
