package nnet;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of genetic algorithm to classify breast cancer dataset.
 * 
 * Modified by Xiaolu, original version is Hannah Lau's AbaloneTest
 */

public class GA4BC {
	private static Instance[] trainSet = initializeInstances("data/btrain.csv", 455);
	private static Instance[] testSet = initializeInstances("data/btest.csv", 114);

	private static int inputLayer = 30, hiddenLayer = 22, outputLayer = 1, trainingIterations = 100;
	private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

	private static ErrorMeasure measure = new SumOfSquaresError();

	private static DataSet set = new DataSet(trainSet);

	private static BackPropagationNetwork network = new BackPropagationNetwork();
	private static NeuralNetworkOptimizationProblem nnop;

	private static OptimizationAlgorithm oa;
	private static String oaName = "GA";
	private static String results = "";

	private static DecimalFormat df = new DecimalFormat("0.0000");

	public static void main(String[] args) {
		int repeat = 10;
		double trainingAcc = 0, testingAcc = 0, trainingTime = 0, testingTime = 0;
		for (int i = 0; i < repeat; i++) {
			List<Double> result = oneRun();
			trainingAcc += result.get(0);
			testingAcc += result.get(1);
			trainingTime += result.get(2);
			testingTime += result.get(3);
		}
		trainingAcc /= repeat;
		testingAcc /= repeat;
		trainingTime /= repeat;
		testingTime /= repeat;

		System.out.println();
		System.out.println("trainingIterations,trainingAcc,testingAcc,trainingTime,testingTime");
		System.out.println(trainingIterations + "," + df.format(trainingAcc) + "," + df.format(testingAcc) + ","
				+ df.format(trainingTime) + "," + df.format(testingTime));
	}

	private static List<Double> oneRun() {
		List<Double> result = new ArrayList<>();
		network = factory.createClassificationNetwork(new int[] { inputLayer, hiddenLayer, outputLayer });
		nnop = new NeuralNetworkOptimizationProblem(set, network, measure);
		
		// change parameters here
        oa = new StandardGeneticAlgorithm(200, 100, 30, nnop);

		double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
		train(oa, network, oaName); // trainer.train();
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);

		Instance optimalInstance = oa.getOptimal();
		network.setWeights(optimalInstance.getData());

		// Calculate Training Set statistics
		double predicted, actual;
		for (int j = 0; j < trainSet.length; j++) {
			network.setInputValues(trainSet[j].getData());
			network.run();

			predicted = Double.parseDouble(trainSet[j].getLabel().toString());
			actual = Double.parseDouble(network.getOutputValues().toString());

			double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

		}
		double trainingAcc = correct / (correct + incorrect);

		// Calculate Test Set Statistics //
		start = System.nanoTime();
		correct = 0;
		incorrect = 0;
		for (int j = 0; j < testSet.length; j++) {
			network.setInputValues(testSet[j].getData());
			network.run();

			predicted = Double.parseDouble(testSet[j].getLabel().toString());
			actual = Double.parseDouble(network.getOutputValues().toString());

			double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
		}

		double testingAcc = correct / (correct + incorrect);

		end = System.nanoTime();
		testingTime = end - start;
		testingTime /= Math.pow(10, 9);

		// add result to the list
		result.add(trainingAcc);
		result.add(testingAcc);
		result.add(trainingTime);
		result.add(testingTime);

		return result;

	}

	private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
		//System.out.println("\nError results for " + oaName + "\n---------------------------");
		//System.out.println("train mse, test mse");

		for (int i = 0; i < trainingIterations; i++) {
			oa.train();

			double train_error = 0;
			for (int j = 0; j < trainSet.length; j++) {
				network.setInputValues(trainSet[j].getData());
				network.run();

				Instance output = trainSet[j].getLabel(), example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
				train_error += measure.value(output, example);
			}

			double test_error = 0;
			for (int j = 0; j < testSet.length; j++) {
				network.setInputValues(testSet[j].getData());
				network.run();

				Instance output = testSet[j].getLabel(), example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
				test_error += measure.value(output, example);
			}

			//System.out.println(df.format(train_error / trainSet.length) + "," + df.format(test_error / testSet.length));
		}
	}

	/**
	 * 
	 * @param filename
	 *            is the sample file location
	 * @param size
	 *            is the sample size
	 * @return
	 */
	private static Instance[] initializeInstances(String filename, int size) {

		double[][][] attributes = new double[size][][];

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));

			for (int i = 0; i < attributes.length; i++) {
				Scanner scan = new Scanner(br.readLine());
				scan.useDelimiter(",");

				attributes[i] = new double[2][];
				attributes[i][0] = new double[30]; // 30 attributes
				attributes[i][1] = new double[1];

				for (int j = 0; j < 30; j++)
					attributes[i][0][j] = Double.parseDouble(scan.next());

				attributes[i][1][0] = Double.parseDouble(scan.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Instance[] instances = new Instance[attributes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = new Instance(attributes[i][0]);
			instances[i].setLabel(new Instance(attributes[i][1][0]));
		}

		return instances;
	}
}
