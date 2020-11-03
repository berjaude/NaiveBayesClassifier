package edu;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Classifier {
	private List<Attribute> attributes;
	private List<String[]> matrix;
	private List<int[][]> phases;
	private int classifications[];
	private int attrSize;
	private int cases;

	public Classifier(String meta, String train) throws FileNotFoundException {
		Scanner m = new Scanner(new File(meta));
		Scanner t = new Scanner(new File(train));
		attributes = new ArrayList<>();
		matrix = new ArrayList<>();
		phases = new ArrayList<>();
		cases = 0;

		System.out.println("Meta fFile : " + meta);
		while (m.hasNextLine()) {
			String temp = m.nextLine();
			int find = temp.indexOf(":");
			String key = temp.substring(0, find);
			String[] value = temp.substring(find + 1).split(",");
			Attribute att = new Attribute(key, value);
			attributes.add(att);
		}
		// Once we have the meta data, we can initialize classification array
		// having the same size as the classification array of string
		attrSize = attributes.size();
		int size = attributes.get(attrSize - 1).getValue().length; // # of classifications
		classifications = new int[size];
		// System.out.println(size);

		System.out.println("Training file : " + train);
		while (t.hasNextLine()) {
			String temp = t.nextLine();
			String[] line = temp.split(",");
			int len = line.length;
			if (len == attributes.size()) {
				matrix.add(line);
				for (int i = 0; i < size; i++) {
					if (attributes.get(attrSize - 1).getValue()[i].equals(line[len - 1])) {
						classifications[i] += 1;
					}
				}
				// increment cases' number
				cases++;
			}
		}

		//close scanners
		m.close();
		t.close();
	}

	public void printAttributes() {
		System.out.println("There are " + (attributes.size() - 1) + " attribute(s)");
		for (int i = 0; i < attributes.size() - 1; i++) {
			System.out.print("\t" + attributes.get(i).getKey() + " : ");
			for (String s : attributes.get(i).getValue())
				System.out.print(s + " ");
			System.out.println();
		}
		int temp = attributes.size() - 1; // case for classification
		System.out.print("Classified under " + attributes.get(temp).getKey() + " as: ");
		for (String s : attributes.get(temp).getValue())
			System.out.print(s + " ");
		System.out.println();

		System.out.println("\nClassifications's repartition:");
		for (int i = 0; i < classifications.length; i++) {
			System.out.println(attributes.get(attrSize - 1).getValue()[i] + " = " + classifications[i] + " / " + cases);
		}
	}

	public void printMatrix() {
		System.out.println("Training data");
		for (int i = 0; i < matrix.size(); i++) {
			for (String l : matrix.get(i))
				System.out.print(l + " ");
			System.out.println();
		}
	}

	public void createLearningPhase() {
		int cl = attributes.size() - 1; // index to access classification slot
		for (int i = 0; i < attributes.size() - 1; i++) {
			int row = attributes.get(i).getValue().length;
			int col = classifications.length;
			int temp[][] = new int[row][col];
			for (int j = 0; j < matrix.size(); j++) {
				for (int k = 0; k < classifications.length; k++) {
					// System.out.println("searching " + matrix.get(j)[i] + " under " +
					// attributes.get(cl).getValue()[k]);
					for (int r = 0; r < row; r++) {
						if (matrix.get(j)[i].equals(attributes.get(i).getValue()[r])
								&& matrix.get(j)[cl].equals(attributes.get(cl).getValue()[k]))
							temp[r][k] += 1;
					}
				}
			}
			phases.add(temp);
		}
	}

	public void getTestingPhase(String file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(file));
		int accurateProb = 0, testCases = 0;
		
		while (sc.hasNextLine()) {
			String temp = sc.nextLine();
			String[] line = temp.split(",");
			List<Prob> P = new ArrayList<>();
			for (int i = 0; i < classifications.length; i++) {
				float rep = 1.0f;
				for (int j = 0; j < line.length - 1; j++) {
					int ind = attributes.get(j).indexOf(line[j]);
					rep = rep * phases.get(j)[ind][i] / classifications[i];
				}
				rep = rep * classifications[i] / cases;
				Prob t = new Prob(rep, attributes.get(attrSize - 1).getValue()[i]);
				P.add(t);;
			}
			// sort list in descending order
			P.sort(Collections.reverseOrder(new ProbComparator()));
			System.out.format("Max Probability is %.4f %s", P.get(0).getProb(), P.get(0).getCate());
			System.out.println(); System.out.println();
			
			if(P.get(0).getCate().equals(line[line.length - 1])) {
				accurateProb += 1; //increment accurate probability case
			}
			
			testCases++; //increment test cases
		}
		
		NumberFormat form = NumberFormat.getPercentInstance();
		float acc = (float) accurateProb / testCases;
		System.out.format("Accurate Probability(ies) = %d, Test Cases = %d", accurateProb, testCases);
		System.out.println();
		System.out.println("Accuracy : "+ form.format(acc));
		System.out.println();
		sc.close();
	}

	public void printPhases() {
		for (int i = 0; i < phases.size(); i++) {
			System.out.println("Phase " + attributes.get(i).getKey());
			System.out.print("\t");
			for (String s : attributes.get(attributes.size() - 1).getValue())
				System.out.print(s + "\t");
			System.out.println();
			for (int[] j : phases.get(i)) {
				System.out.print("\t");
				for (int k : j)
					System.out.print(k + "\t");
				System.out.println();
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Enter meta file (as meta.txt):");
		Scanner sc1 = new Scanner(System.in);
		String str1 = sc1.nextLine();
		boolean train = true;
		Scanner sc2 = null;
		do {
			System.out.println("Enter the training file (as training.txt):");
			sc2 = new Scanner(System.in);
			String str2 = sc2.nextLine();
			
			//String files[] = str.split(" ");
			Classifier classify = new Classifier(str1, str2);
			System.out.println();
			classify.printAttributes();
			System.out.println();
			//classify.printMatrix();
			classify.createLearningPhase();
			System.out.println();
			classify.printPhases();
			System.out.println();
			System.out.print("Enter testing file: ");
			Scanner sc3 = new Scanner(System.in);
			String test = sc3.next();
			classify.getTestingPhase(test);
			
			System.out.println();
			System.out.println("You wish to train another file? (Yes or No) ");
			Scanner te = new Scanner(System.in);
			String temp = te.next().toLowerCase();
			if(temp.equals("yes"))
				train = true;
			else if(temp.equals("no")) {
				train = false;
				System.out.println("Bye!");
			}
			
			//close scanners
			sc1.close();
			sc2.close();
			sc3.close();
			te.close();
		}while(train);
		
		
		Classifier classify = new Classifier("tennis.meta.txt", "tennis.train.txt");
		System.out.println();
		classify.printAttributes();
		System.out.println();
		//classify.printMatrix();
		classify.createLearningPhase();
		System.out.println();
		classify.printPhases();
		System.out.println();
		classify.getTestingPhase("tennis.test.txt");

	}

}
