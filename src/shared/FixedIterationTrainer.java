package shared;

import java.util.ArrayList;
import java.util.List;

/**
 * A fixed iteration trainer
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FixedIterationTrainer implements Trainer {

	/**
	 * The inner trainer
	 */
	private Trainer trainer;

	/**
	 * The number of iterations to train
	 */
	private int iterations;

	/*
	 * Store the curval over iterations while training
	 */
	List<Double> valList = new ArrayList<>();

	/**
	 * Make a new fixed iterations trainer
	 * 
	 * @param t
	 *            the trainer
	 * @param iter
	 *            the number of iterations
	 */
	public FixedIterationTrainer(Trainer t, int iter) {
		trainer = t;
		iterations = iter;
	}

	/**
	 * @see shared.Trainer#train()
	 */
	public double train() {
		double sum = 0;
		for (int i = 0; i < iterations; i++) {
			double error = trainer.train();
			// System.out.println("Training fitness: " + error);
			sum += trainer.train();
			valList.add(error);
		}
		return sum / iterations;
	}

	public List<Double> getValList() {

		return this.valList;
	}

}
