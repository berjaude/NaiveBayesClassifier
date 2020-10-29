package edu;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ProbComparator implements Comparator<Prob>{

	@Override
	public int compare(Prob o1, Prob o2) {
		return Float.compare(o1.getProb(), o2.getProb());
	}
	
}

public class Prob {
	private float prob;
	private String cate;
	
	public Prob(float f, String s) {
		prob = f;
		cate = s;
	}

	public float getProb() {
		return prob;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public static void main(String[] args) throws FileNotFoundException {
		List<Prob> list = new ArrayList<>();
		list.add(new Prob(0.05f, "acc"));
		list.add(new Prob(0.11f, "unacc"));
		list.add(new Prob(0.31f, "good"));
		list.add(new Prob(0.01f, "vgood"));
		
		System.out.println("Before sorting: ");
		for(Prob i : list) {
			System.out.println(i.getProb() + " " + i.getCate());
		}
		 
		//Collections.sort(list, new ProbComparator());
		list.sort(Collections.reverseOrder(new ProbComparator()));
		
		System.out.println("After sorting: ");
		for(Prob i : list) {
			System.out.println(i.getProb() + " " + i.getCate());
		}
	}
}

