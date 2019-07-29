package edu.sharif.ce.nipl.dmoo.business;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.R2;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import edu.sharif.ce.nipl.dmoo.CSO;
import edu.sharif.ce.nipl.dmoo.entity.Cat;


public class CSOBusiness {

	private static int SMP = 5;
	private static double SRD = 0.3;
	private static double CDC = 0.8;
	private static boolean SPC = true;
	private static double C = 2;
	private static double MR =0.95 ; // must be 0.98
	private static double W = 1;
	private static double WDAMP = 0.99;

	private static int NUM_OF_FUNCTIONS = 2;


	private int nPopulation;
	private int nVar;

	private Benchmark benchmark;

	private ArrayList<Cat> population;
	ArrayList<Cat> resultOfSeekingMode;
	ArrayList<Cat> resultOfTracingMode;
	ArrayList<Cat> seekingCats;
	ArrayList<Cat> tracingCats;

	private Cat bestCat;

	private double[][] testDimValues;
	boolean firstTesting;

	private Random rnd;
	private Functions functions;

	private List<Double> archiveSpacings;
	private List<Double> archiveGD;
	private List<Double> archiveIGD;
	private List<Double> archiveVGD;
	private List<Double> archiveMS;
	private List<Double> archiveHVR;
	private List<Double> archiveHV;
	private List<Double> archiveAcc;
	private List<Double> archiveAccTotal;
	private List<Double> archiveR2;
	private List<Double> archiveHVInChanges;
	private List<Double> archiveAccAlt;
	private List<Double> archiveStab;


	private double minVar1;
	private double maxVar1;
	private double minVar2;
	private double maxVar2;
	private int nVar1;
	private int nVar2;
	private double[] referencedPoint;
	private int func;

	private int cycle;
	private List<Cat> nonDominatedSolutions;
	
	private int towet;
	private int selectionAlgo;
	private int reinitAlgo;
	

	public CSOBusiness(int nPop , int nVar1 , int nVar2 , double minVar1 , double maxVar1 , double minVar2 , double maxVar2 , int func , int cycle , int towet , int nt, int selectionAlgo, int reinitAlgo){
		this.nPopulation = nPop;

		this.nVar = nVar1 + nVar2;
		this.nVar1 = nVar1;
		this.nVar2 = nVar2;

		this.minVar1 = minVar1;
		this.maxVar1 = maxVar1;

		this.minVar2 = minVar2;
		this.maxVar2 = maxVar2;

		benchmark = new Benchmark(this.nVar1 , this.nVar2 , this.minVar1 , this.maxVar1 , this.minVar2 , this.maxVar2 , func , towet , nt);
		functions = new Functions(this.nVar1 , this.nVar2 , this.minVar1 , this.maxVar1 , this.minVar2 , this.maxVar2 , func , towet , nt);

		population = new ArrayList<Cat>(nPopulation);
		resultOfSeekingMode = new ArrayList<Cat>();
		resultOfTracingMode = new ArrayList<Cat>();
		seekingCats = new ArrayList<Cat>();
		tracingCats = new ArrayList<Cat>();

		rnd = new Random();

		int numberOfTests = (int)( 0.1 * nPop);
		testDimValues = new double[NUM_OF_FUNCTIONS][numberOfTests];


		firstTesting = true;

		archiveSpacings = new ArrayList<Double>();
		archiveGD = new ArrayList<Double>();
		archiveIGD = new ArrayList<Double>();
		archiveVGD = new ArrayList<Double>();
		archiveMS = new ArrayList<Double>();
		archiveHVR = new ArrayList<Double>();
		archiveHV = new ArrayList<Double>();
		archiveAcc = new ArrayList<Double>();
		archiveAccTotal = new ArrayList<Double>();
		archiveR2 = new ArrayList<Double>();
		archiveHVInChanges = new ArrayList<Double>();
		archiveAccAlt = new ArrayList<Double>();
		archiveStab = new ArrayList<Double>();
		
		this.func = func;
		
		this.cycle = cycle;
		this.towet = towet;
		
		this.selectionAlgo = selectionAlgo;
		this.reinitAlgo = reinitAlgo;
		
	}

	public void CSOInitialization(){

		for(int i = 0 ; i < nPopulation ; i++){

			double[] rndPos = new double[this.nVar];
			double[] oppRndPos = new double[this.nVar];
			for(int j = 0 ; j < rndPos.length ; j++){
				if (j < nVar1){
					rndPos[j] = minVar1 + (maxVar1 - minVar1) * rnd.nextDouble();
					oppRndPos[j] = minVar1 + maxVar1 - rndPos[j];
				}
				else{
					rndPos[j] = minVar2 + (maxVar2 - minVar2) * rnd.nextDouble();
					oppRndPos[j] = minVar2 + maxVar2 - rndPos[j];
				}
			}

			double[] rndVel = new double[this.nVar];
			double[] oppRndVel = new double[this.nVar];
			for(int j = 0 ; j < rndVel.length ; j++){

				if (j < nVar1){
					rndVel[j] = 0;
					oppRndVel[j] = 0;
				}
				else{
					rndVel[j] = 0;
					oppRndVel[j] = 0;
				}
			}


			population.add(new Cat(i , rndPos, rndVel, Double.MAX_VALUE , false , 0 , 0));
			population.add(new Cat(i , oppRndPos, oppRndVel, Double.MAX_VALUE , false , 0 , 0) );
		}

		functions.setDominatedCounterForPopulation(population , 0);
		functions.calcCrowdingDistance(population , 0);
		functions.sortPopulationByDominatationAndCrowdingDistance(population);
		population.subList(nPopulation , population.size()).clear();
	}

	public void CSOMovement(int iter) throws IOException{
		
		for(Cat cat : population){
			cat.setStFlag(false);
		}

		ArrayList<Cat> integratedCats = new ArrayList<Cat>();

		for(int i = 0 ; i < 0.01 * population.size() ; i++){
			integratedCats.add((Cat)population.get(i).clone());
		}


		bestCat = functions.selectBestCat(integratedCats);


		ArrayList<Integer> rndIndicesOfCats = new ArrayList<Integer>();
		int sizeOfRandomArray = (int)(MR * population.size());
		for(int i = 0 ; i < sizeOfRandomArray ;){
			int index = Math.abs(rnd.nextInt()) % sizeOfRandomArray;
			if(!rndIndicesOfCats.contains(index)){
				rndIndicesOfCats.add(index);
				i++;
			}
		}

		for(int i = 0 ; i < rndIndicesOfCats.size() ; i++){
			population.get(i).setStFlag(true);
		}

		seekingCats.clear();
		tracingCats.clear();
		for(Cat cat : population){
			if(cat.isStFlag())
				seekingCats.add((Cat)(cat.clone()));
			else
				tracingCats.add((Cat)(cat.clone()));
		}

		resultOfSeekingMode.clear();
		resultOfTracingMode.clear();

		resultOfSeekingMode = CSOSeekingModeForCats(seekingCats);

		functions.setDominatedCounterForPopulation(resultOfSeekingMode , iter);
		functions.calcCrowdingDistance(resultOfSeekingMode , iter);
		functions.sortPopulationByDominatationAndCrowdingDistance(resultOfSeekingMode);

		for(int i = 0 ; i < resultOfSeekingMode.size() ; i++){

			integratedCats.add((Cat)resultOfSeekingMode.get(i).clone());
		}

		resultOfTracingMode = CSOTracingModeForCats(tracingCats);		
		for(int i = 0 ; i < resultOfTracingMode.size() ; i++){
			integratedCats.add((Cat)resultOfTracingMode.get(i).clone());
		}

		functions.setDominatedCounterForPopulation(integratedCats , iter);
		functions.calcCrowdingDistance(integratedCats , iter);
		functions.sortPopulationByDominatationAndCrowdingDistance(integratedCats);


		population.clear();
		for(int i = 0 ; i < nPopulation ; i++){
			population.add((Cat)integratedCats.get(i).clone());
		}

		Front pf = calcPFKnown(iter);
		//System.out.println(population.size() + " " + pf.getNumberOfPoints());
		Front pfTrue = generatePFTrue(iter, func);

		referencedPoint = new double[2];

		double[] referencedPoint1 = FrontUtils.getMaximumValues(pfTrue);;
		double[] referencedPoint2 = FrontUtils.getMaximumValues(pf);
		referencedPoint[0] = Math.max(referencedPoint1[0], referencedPoint2[0]);
		referencedPoint[1] = Math.max(referencedPoint1[1], referencedPoint2[1]);


		calcHV(pf , pfTrue);
		calcAccTotal();
		if(iter % towet == towet-1)
			archiveHVInChanges.add(archiveHV.get(archiveHV.size() - 1));
		
		//writeValue(CSO.IntegerToString.get(func) + "-" + String.valueOf(cycle) + "-" + String.valueOf(iter) + ".txt" , pf);

		W *= WDAMP;

	}


	private ArrayList<Cat> CSOSeekingModeForCats(List<Cat> seekingCats2) {
		ArrayList<Cat> allCopies = new ArrayList<Cat>();
		if(SPC){
			for(Cat cat : seekingCats2){
				ArrayList<Cat> copies = new ArrayList<Cat>();
				for(int i = 0 ; i < SMP - 1 ; i++){
					copies.add((Cat)cat.clone());
				}

				int dimToChange = (int)(nVar * CDC);

				for(Cat copyCat : copies){

					int[] changedDims = new int[dimToChange];
					for(int i = 0 ; i < changedDims.length ; i++){
						changedDims[i] = Math.abs(rnd.nextInt() % nVar);
					}
					for(int dim : changedDims){
						double multiplier =  (rnd.nextBoolean()) ? ( 1 + SRD ) : (1 - SRD);
						double result = ((cat.getPosition()[dim]) ) * multiplier;
						if (dim < nVar1){
							if(result > maxVar1){
								copyCat.getPosition()[dim] = maxVar1;
							}
							else if (result < minVar1 ){
								copyCat.getPosition()[dim] = minVar1;
							}
							else{
								copyCat.getPosition()[dim] = result;
							}
						}
						else{
							if(result > maxVar2){
								copyCat.getPosition()[dim] = maxVar2;
							}
							else if (result < minVar2 ){
								copyCat.getPosition()[dim] = minVar2;
							}
							else{
								copyCat.getPosition()[dim] = result;
							}
						}

					}

				}
				copies.add((Cat)cat.clone());
				for(int index = 0 ; index < copies.size() ; index++)
					allCopies.add((Cat)copies.get(index).clone());
			}

			return allCopies;
		}
		return null;

	}


	private ArrayList<Cat> CSOTracingModeForCats(ArrayList<Cat> tracingCats) {
		// in evaluation we must have best cat for each function.
		ArrayList<Cat> result = new ArrayList<Cat>();
		for(int i = 0 ; i < tracingCats.size() ; i++){
			Cat cat = tracingCats.get(i);
			double[] tempVel = new double[cat.getVel().length];
			double[] randomDouble = new double[cat.getVel().length];
			//double[] random2Double = new double[cat.getVel().length];
			for(int counter = 0 ; counter < randomDouble.length; counter++){
				randomDouble[counter] = 0.0 + (1.0 - 0.0) * rnd.nextDouble();
				//    random2Double[counter] = 0.0 + (1.0 - 0.0) * rnd.nextDouble();
			}

			for(int counter = 0 ; counter < cat.getVel().length ; counter++){
				tempVel[counter] = cat.getVel()[counter];
			}

			for(int counter = 0 ; counter < cat.getVel().length ; counter++){
				double temp = W * tempVel[counter] + 
						C * randomDouble[counter] * (bestCat.getPosition()[counter] - cat.getPosition()[counter]) ;
				tempVel[counter] = temp;
			}
			cat.setVel(tempVel);

			// End Update of Velocity
			// Update Position
			double[] tempPos = new double[cat.getPosition().length];
			for(int counter = 0 ; counter < cat.getPosition().length ; counter++){
				tempPos[counter] = cat.getPosition()[counter];
			}
			for(int counter = 0 ; counter < tempPos.length ; counter++){
				double temp = tempPos[counter] + tempVel[counter];
				if (counter < nVar1){
					if(temp > maxVar1){
						temp = maxVar1;
					}
					else if (temp < minVar1 ){
						temp = minVar1;
					}
					tempPos[counter] = temp;
				}
				else{
					if(temp > maxVar2){
						temp = maxVar2;
					}
					else if (temp < minVar2 ){
						temp = minVar2;
					}
					tempPos[counter] = temp;
				}
			}
			cat.setPosition(tempPos);
			// End Update Position

			result.add((Cat)cat.clone());


		}
		return result;

	}

	public void CSOResultPlotting(int iter) {


		for(int numFunc = 0 ; numFunc < NUM_OF_FUNCTIONS ; numFunc++){
			for(int i = 0 ; i < nPopulation ; i++){
				population.get(i).setCost(benchmark.evaluate(population.get(i) , numFunc , iter));
				System.out.format("%4f\n" , population.get(i).getCost());
			}
			System.out.println();
		}
	}

	public boolean IsEnvChanged(List<Cat> recognizableCats , int iter) {
		if(firstTesting){
			firstTesting = false;
			for(int numFunc = 0 ; numFunc < NUM_OF_FUNCTIONS ; numFunc++){
				for(int i = 0 ; i < recognizableCats.size() ; i++){
					double cost = benchmark.evaluate(recognizableCats.get(i) , numFunc , iter);
					testDimValues[numFunc][i] = cost;
				}
			}
			return false;
		}

		for(int numFunc = 0 ; numFunc < NUM_OF_FUNCTIONS ; numFunc++){
			for(int i = 0 ; i < recognizableCats.size() ; i++){
				double cost = benchmark.evaluate(recognizableCats.get(i) , numFunc , iter);
				if (cost != testDimValues[numFunc][i]){
					firstTesting = true;
					return true;
				}
			}
		}
		return false;

	}
	
	public Cat mutation(Cat origCat){
		if(SPC){
			ArrayList<Cat> copies = new ArrayList<Cat>();
			for(int i = 0 ; i < SMP-1 ; i++){
				copies.add((Cat)origCat.clone());
			}

			int dimToChange = (int)(nVar * CDC);

			for(Cat copyCat : copies){

				int[] changedDims = new int[dimToChange];
				for(int i = 0 ; i < changedDims.length ; i++){
					changedDims[i] = Math.abs(rnd.nextInt() % nVar);
				}
				for(int dim : changedDims){
					double multiplier =  (rnd.nextBoolean()) ? ( 1 + SRD ) : (1 - SRD);
					double result = ((origCat.getPosition()[dim]) ) * multiplier;
					if (dim < nVar1){
						if(result > maxVar1){
							copyCat.getPosition()[dim] = maxVar1;
						}
						else if (result < minVar1 ){
							copyCat.getPosition()[dim] = minVar1;
						}
						else{
							copyCat.getPosition()[dim] = result;
						}
					}
					else{
						if(result > maxVar2){
							copyCat.getPosition()[dim] = maxVar2;
						}
						else if (result < minVar2 ){
							copyCat.getPosition()[dim] = minVar2;
						}
						else{
							copyCat.getPosition()[dim] = result;
						}
					}
				}

			}
			copies.add((Cat)origCat.clone());
			int index = Math.abs(rnd.nextInt() % copies.size());
			return (Cat)copies.get(index).clone();
		}
		return null;

	}



	public void reinitializePopulation(List<Integer> ranks, double percentage , int iter) {

		int start = (int)((1 - percentage) * ranks.size());
		
		functions.setDominatedCounterForPopulation(population, iter - 1);
		functions.calcCrowdingDistance(population, iter - 1);
		functions.sortPopulationByDominatationAndCrowdingDistance(population);

		List<Cat> reinitCats = new ArrayList<Cat>();
		if(reinitAlgo == CSO.REINIT)
			reinitCats = basicReinit(ranks , start);
		else if (reinitAlgo == CSO.MUTATE)
			reinitCats = mutationReinit(ranks , start , percentage);
		
		functions.setDominatedCounterForPopulation(reinitCats, iter);
		functions.calcCrowdingDistance(reinitCats, iter);
		functions.sortPopulationByDominatationAndCrowdingDistance(reinitCats);

		int index = 0;
		for(int i = start ; i < ranks.size() ; i++){
			for(int j = 0 ; j < population.size() ; j++)
				if(population.get(j).getId() == ranks.get(i)){
					population.set(j , reinitCats.get(index));
					index++;
					break;
				}
		}


	}


	private List<Cat> mutationReinit(List<Integer> ranks, int start , double percentage) {
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i=0; i< start; i++) {
			list.add(new Integer(i));
		}
		Collections.shuffle(list);
		
		int len = (int)((percentage) * ranks.size());
		list.subList(len + 1, list.size()).clear();

		List<Cat> reinitCats = new ArrayList<Cat>();
		int index = 0;
		for(int i = start ; i < ranks.size() ; i++ , index++){
			Cat reinitCat = mutation(population.get(list.get(index)));
			reinitCats.add(reinitCat);
		}
		return reinitCats;
	}

	private List<Cat> basicReinit(List<Integer> ranks, int start) {
		Random rnd = new Random();

		List<Cat> reinitCats = new ArrayList<Cat>();

		for(int i = start ; i < ranks.size() ; i++){
			double[] rndPos = new double[this.nVar];
			double[] oppRndPos = new double[this.nVar];

			for(int j = 0 ; j < rndPos.length ; j++){
				if (j < nVar1){
					rndPos[j] = minVar1 + (maxVar1 - minVar1) * rnd.nextDouble();
					oppRndPos[j] = minVar1 + maxVar1 - rndPos[j];
				}
				else{
					rndPos[j] = minVar2 + (maxVar2 - minVar2) * rnd.nextDouble();
					oppRndPos[j] = minVar2 + maxVar2 - rndPos[j];
				}
			}

			double[] rndVel = new double[this.nVar];
			double[] oppRndVel = new double[this.nVar];
			for(int j = 0 ; j < rndVel.length ; j++){
				if (j < nVar1){
					rndVel[j] = 0;
					oppRndVel[j] = 0;
				}
				else{
					rndVel[j] = 0;
					oppRndVel[j] = 0;
				}
			}

			Cat rndCat = new Cat(ranks.get(i) , rndPos, rndVel, Double.MAX_VALUE , false , 0 , 0);
			reinitCats.add(rndCat);

			Cat oppRndCat = new Cat(ranks.get(i) , oppRndPos, oppRndVel, Double.MAX_VALUE , false , 0 , 0);
			reinitCats.add(oppRndCat);
		}
		
		return reinitCats;
	}

	public List<Integer> calcNewFitness(int iter) {
		for(int i = 0 ; i < population.size() ; i++)
			population.get(i).setId(i);
		
		for(int i = 0 ; i < population.size() ; i++){
			population.get(i).setRank(i);
		}
		List<Integer> rankAggeragetedIndices = new ArrayList<Integer>();
		if(selectionAlgo == CSO.BORDA)
			rankAggeragetedIndices = bordaCount(iter);
		else if (selectionAlgo == CSO.RANDOM)
			rankAggeragetedIndices = randomSelection(); 
		return rankAggeragetedIndices;
	}

	private List<Integer> randomSelection() {
		List<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0 ; i < population.size() ; i++){
			numbers.add(i);
		}
		Collections.shuffle(numbers);
		return numbers;
	}

	private List<Integer> bordaCount(int iter) {
		List<Cat> afterChangeCats = new ArrayList<Cat>();
		for(int i = 0 ; i < population.size() ; i++){
			afterChangeCats.add((Cat)population.get(i).clone());
		}
		functions.setDominatedCounterForPopulation(afterChangeCats, iter);
		functions.calcCrowdingDistance(afterChangeCats, iter);

		functions.sortPopulationByDominatationAndCrowdingDistance(afterChangeCats);
		for(int i = 0 ; i < afterChangeCats.size() ; i++){
			afterChangeCats.get(i).setRank(i);
		}

		functions.setDominatedCounterForPopulation(population, iter - 1);
		functions.calcCrowdingDistance(population, iter - 1);
		functions.sortPopulationByDominatationAndCrowdingDistance(population);

		List<Integer> rankAggregatedIndices = functions.rankAggeragationAlgorithm(population , afterChangeCats);
		return rankAggregatedIndices;

	}

	public void calcSpacing(Front PFKnown) {
		double spacing = functions.spacing(PFKnown);
		archiveSpacings.add(spacing);
	}

	public void calcGD(Front PFKnown, Front pFTrue) {

		GenerationalDistance gd = new GenerationalDistance();
		double gdValue = gd.execute(PFKnown, pFTrue);
		archiveGD.add(gdValue);

	}

	public void calcIGD(Front PFKnown, Front pFTrue) {

		InvertedGenerationalDistance igd = new InvertedGenerationalDistance();
		double igdValue = igd.execute(PFKnown, pFTrue);
		archiveIGD.add(igdValue);

	}

	public void calcVGD(Front PFKnown, Front pFTrue) {

		double VGD = functions.VGD(PFKnown , pFTrue);
		archiveVGD.add(VGD);
	}

	public void calcMS(Front PFKnown, Front pFTrue) {
		double msValue = functions.MS(PFKnown , pFTrue);
		archiveMS.add(msValue);

	}

	public void calcHV(Front PFKnown , Front pFTrue) {
		Hypervolume hv = new Hypervolume();
		double hvValue = hv.execute(PFKnown , referencedPoint);
		archiveHV.add(hvValue);

	}

	public void calcHVR(Front PFKnown, Front pFTrue) {

		double[] referencedPoint1 = FrontUtils.getMaximumValues(PFKnown);
		double[] referencedPoint2 = FrontUtils.getMaximumValues(pFTrue);
		referencedPoint[0] = Math.max(referencedPoint1[0], referencedPoint2[0]);
		referencedPoint[1] = Math.max(referencedPoint1[1], referencedPoint2[1]);

		Hypervolume hv = new Hypervolume();
		double hvKnown = hv.execute(PFKnown , referencedPoint);
		double hvTrue = hv.execute(pFTrue , referencedPoint);

		double hvValue = hvKnown / hvTrue;
		archiveHVR.add(hvValue);

		double accAlt = Math.abs(hvKnown - hvTrue);
		archiveAccAlt.add(accAlt);

	}

	public void calcAcc(Front PFKnown, Front pFTrue) {

		double accValue = archiveHVInChanges.get(archiveHVInChanges.size() - 1)/ Collections.max(archiveHVInChanges);
		archiveAcc.add(accValue);
	}

	public void calcAccTotal() {

		double accValue = archiveHV.get(archiveHV.size() - 1)/ Collections.max(archiveHV);
		archiveAccTotal.add(accValue);
	}

	public double calcStab(Front PFKnown, Front pFTrue , int iter) throws IOException {
		double sum = 0;
		double acct_1;
		double acct;

		double stabValue;
		List<Double> stabs = new ArrayList<Double>();

		for(int i = towet ; i < iter ; i += towet){
			acct_1 = archiveAccTotal.get(i - 1);
			acct = archiveAccTotal.get(i);

			stabValue = Math.max(0, acct_1 - acct);
			stabs.add(stabValue);
			
			archiveStab.add(stabValue);

		}

		for (Double d : stabs) {
			sum += d;
		}

		return sum / stabs.size();

	}

	private void writeValue(String filename, Front pf) throws IOException {
		String pofFilename = "pof-" + filename;
		String posFilename = "pos-" + filename;
		
		BufferedWriter pofOutputWriter = new BufferedWriter(new FileWriter(pofFilename));
		BufferedWriter posOutputWriter = new BufferedWriter(new FileWriter(posFilename));
		
		if(nonDominatedSolutions.size() != pf.getNumberOfPoints())
			System.out.println();
		
		for(int i = 0 ; i < pf.getNumberOfPoints() ; i++){
			double cost1 = pf.getPoint(i).getDimensionValue(0);
			pofOutputWriter.write(String.valueOf(cost1));

			pofOutputWriter.write(",");

			double cost2 = pf.getPoint(i).getDimensionValue(1);
			pofOutputWriter.write(String.valueOf(cost2));

			pofOutputWriter.newLine();
			
			Cat cat = nonDominatedSolutions.get(i);
			for(int j = 0 ; j < nVar ; j++){
				posOutputWriter.write(String.valueOf(cat.getPosition()[j]));
				if( j != nVar - 1)
					posOutputWriter.write(",");
			}
			posOutputWriter.newLine();
		}

		pofOutputWriter.flush();  
		pofOutputWriter.close();
		
		posOutputWriter.flush();  
		posOutputWriter.close();

	}

	public double calcStab2(Front PFKnown, Front pFTrue , int iter) {
		double sum = 0;
		double acct_1;
		double acct;

		double stabValue;
		List<Double> stabs = new ArrayList<Double>();

		for(int i = 19 ; i < iter ; i += 10){
			acct_1 = archiveAccTotal.get(i - 10);
			acct = archiveAccTotal.get(i);

			stabValue = Math.max(0, acct_1 - acct);

			stabs.add(stabValue);
		}

		for (Double d : stabs) {
			sum += d;
		}
		return sum / stabs.size();

	}

	public void calcR(Front PFKnown, Front pFTrue) {

		R2 r2 = new R2();
		double r2Value = r2.execute(PFKnown, pFTrue);
		archiveR2.add(r2Value);

	}

	public Front calcPFKnown(int iter) {
		functions.setDominatedCounterForPopulation(population , iter);
		nonDominatedSolutions = new ArrayList<Cat>();
		for(int i = 0 ; i < population.size() ; i++){
			if(population.get(i).getDominatedCounter() == 0){
				nonDominatedSolutions.add((Cat)population.get(i).clone());
			}
		}

		Front PFKnown = new ArrayFront(nonDominatedSolutions.size() , 2);
		for(int i = 0 ; i < nonDominatedSolutions.size() ; i++){
			double cost1 = benchmark.evaluate(nonDominatedSolutions.get(i) , 0 , iter);
			double cost2 = benchmark.evaluate(nonDominatedSolutions.get(i) , 1 , iter);
			PFKnown.setPoint(i, new ArrayPoint(new double[]{cost1 , cost2}));
		}

		return PFKnown;

	}

	public double averageList(List<Double> list){
		double sum = 0;
		for(Double l : list)
			sum += l;
		return sum / list.size();
	}

	public double averageList(List<Double> list , int iter){
		double sum = 0;
		for(Double l : list)
			sum += l;
		return sum / iter;
	}

	public Map<String , Double> printMetrics(int iteration) throws IOException {
		
		Map<String , Double> result = new HashMap<String , Double>();
		
		double as = averageList(archiveSpacings);
		result.put("Spacing" , as);

		double ag = averageList(archiveGD);
		result.put("GD", ag);

		double ai = averageList(archiveIGD);
		result.put("IGD", ai);

		double av = averageList(archiveVGD);
		result.put("VGD", av);

		double am = averageList(archiveMS);
		result.put("MS", am);

		double ah = averageList(archiveHVR);
		result.put("HVR", ah);

		double ac = averageList(archiveAcc);
		result.put("Acc", ac);

		double aaa = averageList(archiveAccAlt);
		result.put("AccAlt", aaa);

		double astab1 = calcStab(null, null, iteration);
		result.put("Stab1", astab1);

		double astab2 = 0;//calcStab2(null, null, iteration);
		result.put("Stab2", astab2);

		double ar = averageList(archiveR2);
		result.put("R2", ar);
		
		return result;

	}

	public Front generatePFTrue(int iter , int func) {
		if(func == CSO.FDA1)
			return benchmark.generatePFTrueForFDA1();
		else if (func == CSO.FDA3)
			return benchmark.generatePFTrueForFDA3(iter);
		else if (func == CSO.DMOP1)
			return benchmark.generatePFTrueForDMOP1(iter);
		else if (func == CSO.DMOP2)
			return benchmark.generatePFTrueForDMOP2(iter);
		else if (func == CSO.DIMP2)
			return benchmark.generatePFTrueForDIMP2(iter);
		else if (func == CSO.DMOP2DEC)
			return benchmark.generatePFTrueForDMOP2DEC(iter);
		else if (func == CSO.DMOP2ISO)
			return benchmark.generatePFTrueForDMOP2ISO(iter);
		else if (func == CSO.DMOP3)
			return benchmark.generatePFTrueForDMOP3(iter);
		else if (func == CSO.HE2)
			return benchmark.generatePFTrueForHE2(iter);
		else if (func == CSO.HE7)
			return benchmark.generatePFTrueForHE7(iter);
		else if (func == CSO.HE9)
			return benchmark.generatePFTrueForHE9(iter);
		else
			return null;
	}

	public Map<String, List<Double>> getMeasures() {
		Map<String , List<Double>> result = new HashMap<String , List<Double>>();
		result.put("VGD", archiveVGD);
		result.put("Spacing", archiveSpacings);
		result.put("HVR", archiveHVR);
		result.put("ACCALT", archiveAccAlt);
		result.put("Stab", archiveStab);
		return result;
	}



}

