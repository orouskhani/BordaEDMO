package edu.sharif.ce.nipl.dmoo.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.PointUtils;

import edu.sharif.ce.nipl.dmoo.entity.Cat;

public class Functions {

	private Benchmark benchmark;
	private Random rnd;
	private int nVar;
	private int nVar1;
	private int nVar2;
	private double minVar1;
	private double maxVar1;
	private double minVar2;
	private double maxVar2;
	private int func;

	public Functions(int nVar1 , int nVar2 , double minVar1 , double maxVar1 , double minVar2 , double maxVar2 , int func , int towet , int nt) {

		this.nVar = nVar1 + nVar2;
		this.nVar1 = nVar1;
		this.nVar2 = nVar2;

		this.minVar1 = minVar1;
		this.maxVar1 = maxVar1;

		this.minVar2 = minVar2;
		this.maxVar2 = maxVar2;
		this.func = func;

		benchmark = new Benchmark(this.nVar1 , this.nVar2 , this.minVar1 , this.maxVar1 , this.minVar2 , this.maxVar2 , func , towet , nt);
		rnd = new Random();

	}

	public void setDominatedCounterForPopulation(List<Cat> rep , int iter) {
		for (Cat cat : rep) {
			cat.setDominatedCounter(0);
		}
		for(int i = 0 ; i < rep.size() ; i++){
			double x1 = benchmark.evaluate(rep.get(i), 0 , iter);
			double y1 = benchmark.evaluate(rep.get(i), 1 , iter);
			//System.out.println("For Cat i : " + i);
			for(int j = i + 1 ; j < rep.size() ; j++){
				double x2 = benchmark.evaluate(rep.get(j), 0 , iter);
				double y2 = benchmark.evaluate(rep.get(j), 1 , iter);
				//System.out.println("For Cat j : " + j);
				//System.out.println(x1 + " " + y1 + " " + x2 + " " + y2);
				if(x1 <= x2 && y1 <= y2){
					if(x1 < x2 || y1 < y2){
						rep.get(j).setDominatedCounter(rep.get(j).getDominatedCounter() + 1);
					}
				}
				if(x2 <= x1 && y2 <= y1){
					if(x2 < x1 || y2 < y1){
						rep.get(i).setDominatedCounter(rep.get(i).getDominatedCounter() + 1);
					}
				}

			}
		}


	}

	public void calcCrowdingDistance(List<Cat> rep , int iter){
		for (Cat cat : rep) {
			cat.setCrowdingDistance(0);
		}
		for(int numFunc = 0 ; numFunc < 2 ; numFunc++){
			for(int i = 0 ; i < rep.size() ; i++)
				rep.get(i).setCost(benchmark.evaluate(rep.get(i), numFunc , iter));

			try{
				Collections.sort(rep , new Comparator<Object>() {
					public int compare(Object o1, Object o2) {

						if(((Cat)o1).getCost() > ((Cat)o2).getCost())
							return -1;
						else if(((Cat)o1).getCost() < ((Cat)o2).getCost())
							return 1;
						return 0;

					}
				});
			}catch(Exception ex){
				for(int i = 0 ; i < rep.size() ; i++)
					System.out.println(rep.get(i).getCost());
				ex.printStackTrace();
			}

			double[] cdInDim = new double[rep.size()];
			cdInDim[0] = Double.POSITIVE_INFINITY;
			for(int i = 1 ; i < cdInDim.length - 1 ; i++){
				cdInDim[i] = Math.abs(rep.get(i+1).getCost()-rep.get(i-1).getCost())/Math.abs(rep.get(0).getCost() - rep.get(rep.size() - 1).getCost());
			}
			cdInDim[rep.size() - 1] = Double.POSITIVE_INFINITY;

			for(int i = 0 ; i < rep.size() ; i++){
				rep.get(i).setCrowdingDistance(rep.get(i).getCrowdingDistance() + cdInDim[i]);
			}
		}

	}

	public void sortPopulationByDominatationAndCrowdingDistance(List<Cat> rep){

		Collections.sort(rep , new Comparator<Object>() {
			public int compare(Object o1, Object o2) {

				if(((Cat)o1).getDominatedCounter() > ((Cat)o2).getDominatedCounter())
					return 1;
				else if(((Cat)o1).getDominatedCounter() < ((Cat)o2).getDominatedCounter())
					return -1;
				else{
					if(((Cat)o1).getCrowdingDistance() > ((Cat)o2).getCrowdingDistance())
						return -1;
					else if(((Cat)o1).getCrowdingDistance() < ((Cat)o2).getCrowdingDistance())
						return 1;
				}
				return 0;

			}
		});

	}

	public void sortPopulationByCost(ArrayList<Cat> rep){

		Collections.sort(rep , new Comparator<Object>() {
			public int compare(Object o1, Object o2) {

				if(((Cat)o1).getCost() > ((Cat)o2).getCost())
					return -1;
				else if(((Cat)o1).getCost() < ((Cat)o2).getCost())
					return 1;
				return 0;

			}
		});

	}

	public Cat selectBestCat(ArrayList<Cat> population) {
		return population.get(Math.abs(rnd.nextInt()) % population.size());
	}

	public List<Integer> rankAggeragationAlgorithm(List<Cat> population1 , List<Cat> population2) {
		List<Integer> finalScore = new ArrayList<Integer>();
		for (int i = 0 ; i < population1.size() ; i++) {
			int scoreInPop1 = 0;
			for (Cat cat : population1) {
				if(cat.getId() == i){
					scoreInPop1 = cat.getRank();
				}
			}
			int scoreInPop2 = 0;
			for (Cat cat : population2) {
				if(cat.getId() == i){
					scoreInPop2 = cat.getRank();
				}
			}
			finalScore.add(scoreInPop1 + scoreInPop2);
		}

		ArrayIndexComparator comparator = new ArrayIndexComparator(finalScore);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);

		return Arrays.asList(indexes);
	}


	public double spacing(Front PFKnown){
		ArrayList<Double> distances = new ArrayList<Double>();
		double di = 0;
		double sum = 0;
		for(int i = 0 ; i < PFKnown.getNumberOfPoints() ; i++){
			double min = Double.MAX_VALUE;
			for(int j = 0 ; j < PFKnown.getNumberOfPoints() ; j++){
				if(j == i)
					continue;
				di = PointUtils.harmonicDistance(PFKnown.getPoint(i) , (PFKnown.getPoint(j)));

				if(di < min){
					min = di;
				}
			}
			sum += min;
			distances.add(min);
		}
		double avg = sum / PFKnown.getNumberOfPoints();
		double expSum = 0;
		for(Double d : distances){
			expSum += Math.pow(d - avg ,2);
		}
		return ( Math.sqrt(expSum / PFKnown.getNumberOfPoints()) ) / PFKnown.getNumberOfPoints() ;
	}

	public double VGD(Front PFKnown, Front PFTrue) {
		double di;
		double sum = 0;
		int len = PFKnown.getNumberOfPoints();

		for(int i = 0 ; i < len ; i++){
			Point point = PFKnown.getPoint(i);
			di = PointUtils.euclideanDistance(point , PFTrue.getPoint(0));
			double min = di;
			for(int j = 1 ; j < PFTrue.getNumberOfPoints() ; j++){
				di = PointUtils.euclideanDistance(point , PFTrue.getPoint(j));
				if(di < min){
					min = di;
				}
			}
			sum += Math.pow(min , 2);
		}
		return Math.sqrt(sum * PFKnown.getNumberOfPoints())  / PFKnown.getNumberOfPoints();
	}

	public double MS(Front pFKnown, Front pFTrue) {

		double maxPFKnownFirstObjective = Double.MIN_VALUE;
		double minPFKnownFirstObjective = Double.MAX_VALUE;
		double maxPFKnownSecondObjective = Double.MIN_VALUE;
		double minPFKnownSecondObjective = Double.MAX_VALUE;
		for(int i = 0 ; i < pFKnown.getNumberOfPoints() ; i++){
			Point point = pFKnown.getPoint(i);
			// First Objective
			if(point.getDimensionValue(0) > maxPFKnownFirstObjective)
				maxPFKnownFirstObjective = point.getDimensionValue(0);
			if(point.getDimensionValue(0) < minPFKnownFirstObjective){
				minPFKnownFirstObjective = point.getDimensionValue(0);
			}


			// Second Objective
			if(point.getDimensionValue(1) > maxPFKnownSecondObjective)
				maxPFKnownSecondObjective = point.getDimensionValue(1);
			if(point.getDimensionValue(1) < minPFKnownSecondObjective){
				minPFKnownSecondObjective = point.getDimensionValue(1);
			}		
		}


		double maxPFTrueFirstObjective = Double.MIN_VALUE;
		double minPFTrueFirstObjective = Double.MAX_VALUE;
		double maxPFTrueSecondObjective = Double.MIN_VALUE;
		double minPFTrueSecondObjective = Double.MAX_VALUE;
		for(int i = 0 ; i < pFTrue.getNumberOfPoints() ; i++){
			Point point = pFTrue.getPoint(i);
			// First Objective
			if(point.getDimensionValue(0) > maxPFTrueFirstObjective)
				maxPFTrueFirstObjective = point.getDimensionValue(0);
			if(point.getDimensionValue(0) < minPFTrueFirstObjective){
				minPFTrueFirstObjective = point.getDimensionValue(0);
			}


			// Second Objective
			if(point.getDimensionValue(1) > maxPFTrueSecondObjective)
				maxPFTrueSecondObjective = point.getDimensionValue(1);
			if(point.getDimensionValue(1) < minPFTrueSecondObjective){
				minPFTrueSecondObjective = point.getDimensionValue(1);
			}
		}

		double firstResult = (Math.min(maxPFKnownFirstObjective, maxPFTrueFirstObjective) - Math.max(minPFTrueFirstObjective, minPFKnownFirstObjective)) / ( maxPFTrueFirstObjective - minPFTrueFirstObjective );  
		double secondResult = (Math.min(maxPFKnownSecondObjective, maxPFTrueSecondObjective) - Math.max(minPFTrueSecondObjective, minPFKnownSecondObjective)) / ( maxPFTrueSecondObjective - minPFTrueSecondObjective );

		double result = Math.sqrt(0.5 * ( Math.pow(firstResult , 2 )  + Math.pow(secondResult , 2)));

		return result;
	}


}

class ArrayIndexComparator implements Comparator<Integer>
{
	private final List<Integer> array;

	public ArrayIndexComparator(List<Integer> array)
	{
		this.array = array;
	}

	public Integer[] createIndexArray()
	{
		Integer[] indexes = new Integer[array.size()];
		for (int i = 0; i < array.size(); i++)
		{
			indexes[i] = i; // Autoboxing
		}
		return indexes;
	}

	@Override
	public int compare(Integer index1, Integer index2)
	{
		// Autounbox from Integer to int to use as array indexes
		return array.get(index1).compareTo(array.get(index2));
	}
}