package edu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Classifier {
	private List<Attribute> attributes;
	private List<String[]> matrix;
	
	public Classifier(String meta, String train) throws FileNotFoundException {
		Scanner m = new Scanner(new File(meta));
		Scanner t = new Scanner(new File(train));
		attributes = new ArrayList<>();
		matrix = new ArrayList<>();
		
		System.out.println("File " + meta);
		while(m.hasNextLine()) {
			String temp = m.nextLine();
			int find = temp.indexOf(":");
			String key = temp.substring(0, find);
			String[] value = temp.substring(find+1).split(",");
			Attribute att = new Attribute(key, value);
			attributes.add(att);
		}
		
		System.out.println("File " + train);
		while(t.hasNextLine()) {
			String temp = t.nextLine();
			String[] line = temp.split(",");
			if(line.length == attributes.size())
				matrix.add(line);
		}
		
		m.close();
		t.close();
	}
	
	public void printAttributes() {
		System.out.println("There are " + attributes.size() + " attribute(s)");
		for(int i=0; i<attributes.size(); i++) {
			System.out.print(attributes.get(i).getKey() + " : ");
			for(String s : attributes.get(i).getValue())
				System.out.print(s + " ");
			System.out.println();
		}
	}
	
	public void printMatrix() {
		System.out.println("Training data");
		for(int i=0; i<matrix.size(); i++) {
			for(String l : matrix.get(i))
				System.out.print(l + " ");
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		System.out.print("Enter meta file: ");
		Scanner m = new Scanner(System.in);
		String meta = m.next();
		System.out.print("Enter training file: ");
		Scanner t = new Scanner(System.in);
		String train = t.next();
		
		Classifier classify = new Classifier(meta, train);
		System.out.println();
		classify.printAttributes();
		System.out.println();
		classify.printMatrix();
		
		m.close();
		t.close();
	}

}
