package edu.sharif.ce.nipl.dmoo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.uma.jmetal.util.front.Front;

import edu.sharif.ce.nipl.dmoo.business.CSOBusiness;
import edu.sharif.ce.nipl.dmoo.entity.Cat;


/**
 * 
 */

/**
 * @author Yasin
 *
 */
public class CSO {

	private static double percOfReinitialization = 0.2;
	private static int NUMCYCLE = 5;
	public static int FDA1 = 1;
	public static int FDA3 = 2;
	public static int DMOP1 = 3;
	public static int DMOP2 = 4;
	public static int DIMP2 = 5;
	public static int DMOP2ISO = 6;
	public static int DMOP2DEC = 7;
	public static int DMOP3 = 8;
	public static int HE2 = 9;
	public static int HE7 = 10;
	public static int HE9 = 11;

	private static int nt = 10;
	private static int towet = 10;

	public static int BORDA = 12;
	public static int RANDOM = 13;
	public static int MUTATE = 14;
	public static int REINIT = 15;

	public static Map<Integer , String> IntegerToString;
	
	public static int func;
	public static int selectionAlgo;
	public static int reinitAlgo;

	private static List<Double> spacings = new ArrayList<Double>();
	private static List<Double> GDs = new ArrayList<Double>();
	private static List<Double> IGDs = new ArrayList<Double>();
	private static List<Double> VGDs = new ArrayList<Double>();
	private static List<Double> MSs = new ArrayList<Double>();
	private static List<Double> HVRs = new ArrayList<Double>();
	private static List<Double> Accs = new ArrayList<Double>();
	private static List<Double> AccAlts = new ArrayList<Double>();
	private static List<Double> Stabs = new ArrayList<Double>();

	public static void main(String[] args) throws Exception {


		IntegerToString = new HashMap<Integer , String>();
		IntegerToString.put(FDA1, "FDA1");
		IntegerToString.put(FDA3, "FDA3");
		IntegerToString.put(DMOP1, "DMOP1");
		IntegerToString.put(DMOP2, "DMOP2");
		IntegerToString.put(DIMP2, "DIMP2");
		IntegerToString.put(DMOP2ISO, "DMOP2ISO");
		IntegerToString.put(DMOP2DEC, "DMOP2DEC");
		IntegerToString.put(DMOP3, "DMOP3");
		IntegerToString.put(HE2, "HE2");
		IntegerToString.put(HE7, "HE7");
		IntegerToString.put(HE9, "HE9");
		IntegerToString.put(BORDA, "BORDA");
		IntegerToString.put(RANDOM, "RANDOM");
		IntegerToString.put(MUTATE, "MUTATE");
		IntegerToString.put(REINIT, "REINIT");

		int nPop = 100;
		int iter = 1000;
		int nVar1 = 0;
		int nVar2 = 0;

		double minVar1 = 0;
		double maxVar1 = 0;
		double minVar2 = 0;
		double maxVar2 = 0;

		func = FDA1;
		selectionAlgo = BORDA;
		reinitAlgo = REINIT;

		if(func == FDA1){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = -1;
			maxVar2 = 1;
		}
		else if(func == FDA3){
			nVar1 = 5;
			nVar2 = 5;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = -1;
			maxVar2 = 1;

		}
		else if(func == DMOP1){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == DMOP2){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == DIMP2){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = -2;
			maxVar2 = 2;
		}
		else if(func == DMOP2ISO){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == DMOP2DEC){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == DMOP3){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == HE2){
			nVar1 = 1;
			nVar2 = 29;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}
		else if(func == HE7){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = -1;
			maxVar2 = 1;
		}
		else if(func == HE9){
			nVar1 = 1;
			nVar2 = 9;

			minVar1 = 0;
			maxVar1 = 1;

			minVar2 = 0;
			maxVar2 = 1;
		}

		int nVar = nVar1 + nVar2;

		Map<String , Double> finalResult = new HashMap<String , Double>();

		finalResult.put("Spacing", new Double(0));
		finalResult.put("GD", new Double (0));
		finalResult.put("IGD", new Double (0));
		finalResult.put("VGD", new Double (0));
		finalResult.put("MS", new Double (0));
		finalResult.put("HVR", new Double (0));
		finalResult.put("Acc", new Double (0));
		finalResult.put("AccAlt", new Double (0));
		finalResult.put("Stab1", new Double (0));
		finalResult.put("Stab2", new Double (0));
		finalResult.put("R2", new Double (0));

		int numOfChanges = iter / towet;
		List<Double> zero = new ArrayList<Double>();
		for(int i = 0 ; i < numOfChanges ; i++)
			zero.add(new Double(0));
		
		List<Double> vgdList = new ArrayList<Double>(zero);
		List<Double> spacingList = new ArrayList<Double>(zero);
		List<Double> HVRList = new ArrayList<Double>(zero);
		List<Double> ACCALTList = new ArrayList<Double>(zero);
		List<Double> StabList = new ArrayList<Double>(zero);
		
		Map<String , List<Double>> allMeasures = new HashMap<String , List<Double>>();
		allMeasures.put("VGD", vgdList);
		allMeasures.put("Spacing", spacingList);
		allMeasures.put("HVR", HVRList);
		allMeasures.put("ACCALT", ACCALTList);
		allMeasures.put("Stab", StabList);

		for(int cycle = 0 ; cycle < NUMCYCLE ; cycle++){
			System.out.println("Cycle is : " + cycle);
			Random rnd = new Random();

			CSOBusiness buss = new CSOBusiness(nPop , nVar1 , nVar2 , minVar1 , maxVar1 , minVar2 , maxVar2 , func , cycle , towet , nt , selectionAlgo , reinitAlgo);
			buss.CSOInitialization();


			List<Cat> recognizableCats = new ArrayList<Cat>();
			int len = (int)(0.1 * nPop);
			for(int i = 0 ; i  < len ; i++){
				double[] rndPos = new double[nVar];
				for(int j = 0 ; j < rndPos.length ; j++){
					if (j < nVar1 ){
						rndPos[j] = minVar1 + (maxVar1 - minVar1) * rnd.nextDouble();
					}
					else{
						rndPos[j] = minVar2 + (maxVar2 - minVar2) * rnd.nextDouble();
					}
				}

				double[] rndVel = new double[nVar];
				recognizableCats.add(new Cat(i , rndPos, rndVel, Double.MAX_VALUE , false , 0 , 0));

			}



			for(int i = 1 ; i < iter  ; i++){
				System.out.println("iter is:" + i);

				boolean isChanged = buss.IsEnvChanged(recognizableCats , i);
				if(isChanged && i != iter){

					Front PFTrue = buss.generatePFTrue(i - 1, func);
					Front PFKnown = buss.calcPFKnown(i - 1);

					buss.calcSpacing(PFKnown);
					buss.calcGD(PFKnown , PFTrue);
					buss.calcIGD(PFKnown, PFTrue);
					buss.calcVGD(PFKnown , PFTrue);
					buss.calcMS(PFKnown , PFTrue);
					buss.calcHVR(PFKnown , PFTrue);
					buss.calcAcc(PFKnown , PFTrue);
					buss.calcR(PFKnown , PFTrue);

					List<Integer> ranks = buss.calcNewFitness(i);
					buss.reinitializePopulation(ranks , percOfReinitialization , i);

				}

				buss.CSOMovement(i);
			}
			System.out.println("Finish");
			buss.CSOResultPlotting(iter);

			Map<String , Double> result = buss.printMetrics(iter);
			Map<String , List<Double>> measures = buss.getMeasures();
			addListToMeasures(allMeasures , measures);

			spacings.add(result.get("Spacing"));
			finalResult.put("Spacing", finalResult.get("Spacing") + result.get("Spacing"));
			
			GDs.add(result.get("GD"));
			finalResult.put("GD", finalResult.get("GD") + result.get("GD"));
			
			IGDs.add(result.get("IGD"));
			finalResult.put("IGD", finalResult.get("IGD") + result.get("IGD"));
			
			VGDs.add(result.get("VGD"));
			finalResult.put("VGD", finalResult.get("VGD") + result.get("VGD"));
			
			MSs.add(result.get("MS"));
			finalResult.put("MS", finalResult.get("MS") + result.get("MS"));
			
			HVRs.add(result.get("HVR"));
			finalResult.put("HVR", finalResult.get("HVR") + result.get("HVR"));
			
			Accs.add(result.get("Acc"));
			finalResult.put("Acc", finalResult.get("Acc") + result.get("Acc"));
			
			AccAlts.add(result.get("AccAlt"));
			finalResult.put("AccAlt", finalResult.get("AccAlt") + result.get("AccAlt"));
			
			Stabs.add(result.get("Stab1"));
			finalResult.put("Stab1", finalResult.get("Stab1") + result.get("Stab1"));
			
		}

		printFinalMeanResult(finalResult);
		printFinalVarianceMeans();
		write(allMeasures);

		//combineFiles(func , NUMCYCLE , iter);

	}
	
	private static double getMean(List<Double> data)
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        int size = data.size();
        return sum/size;
    }

    private static double getVariance(List<Double> data)
    {
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        int size = data.size();
        return temp/size;
    }

	private static void printFinalVarianceMeans() {
		System.out.println("Variance:");
		System.out.println("Spacing :" + getVariance(spacings) );
		System.out.println("GD : " + getVariance(GDs) );
		System.out.println("IGD : " + getVariance(IGDs) );
		System.out.println("VGD : " + getVariance(VGDs) );
		System.out.println("MS : " + getVariance(MSs) );
		System.out.println("HVR : " + getVariance(HVRs) );
		System.out.println("Acc : " + getVariance(Accs) );
		System.out.println("AccAlt : " + getVariance(AccAlts) );
		System.out.println("Stab1 : " + getVariance(Stabs) );
	}

	private static void write(Map<String, List<Double>> allMeasures) throws IOException {
		// VGD
		List<Double> vgdMainList = allMeasures.get("VGD");
		for(int i = 0 ; i < vgdMainList.size() ; i++){
			vgdMainList.set(i, vgdMainList.get(i) / NUMCYCLE);
		}

		// Spacing
		List<Double> spacingMainList = allMeasures.get("Spacing");
		for(int i = 0 ; i < spacingMainList.size() ; i++){
			spacingMainList.set(i, spacingMainList.get(i) / NUMCYCLE);
		}


		// HVR
		List<Double> hvrMainList = allMeasures.get("HVR");
		for(int i = 0 ; i < hvrMainList.size() ; i++){
			hvrMainList.set(i, hvrMainList.get(i) / NUMCYCLE);
		}


		// ACCALT
		List<Double> accAltMainList = allMeasures.get("ACCALT");
		for(int i = 0 ; i < accAltMainList.size() ; i++){
			accAltMainList.set(i, accAltMainList.get(i) / NUMCYCLE);
		}

		// Stab
		List<Double> stabMainList = allMeasures.get("Stab");
		for(int i = 0 ; i < stabMainList.size() ; i++){
			stabMainList.set(i, stabMainList.get(i) / NUMCYCLE);
		}

		writemeasure("VGD" , vgdMainList);
		writemeasure("Spacing" , spacingMainList);
		writemeasure("HVR" , hvrMainList);
		writemeasure("ACCALT" , accAltMainList);
		writemeasure("Stab" , stabMainList);
		
	}

	private static void writemeasure(String measure, List<Double> measureList) throws IOException {
		String filename = String.valueOf(measure + "-" + CSO.IntegerToString.get(func) + "-" + IntegerToString.get(selectionAlgo)+ "-" + IntegerToString.get(reinitAlgo) + "-nt-" + String.valueOf(nt) + "-towet-" + String.valueOf(towet) + ".txt");
		
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
		
		for(int i = 0 ; i < measureList.size() ; i++){
			double cost1 = measureList.get(i);
			outputWriter.write(String.valueOf(cost1));

			outputWriter.newLine();
		}

		outputWriter.flush();  
		outputWriter.close();

	}

	private static void addListToMeasures( Map<String, List<Double>> allMeasures, Map<String, List<Double>> measures) {
		// VGD
		List<Double> vgdMainList = allMeasures.get("VGD");
		List<Double> vgdList = measures.get("VGD");
		for(int i = 0 ; i < vgdList.size() ; i++){
			vgdMainList.set(i, vgdMainList.get(i) + vgdList.get(i));
		}

		// Spacing
		List<Double> spacingMainList = allMeasures.get("Spacing");
		List<Double> spacingList = measures.get("Spacing");
		for(int i = 0 ; i < spacingList.size() ; i++){
			spacingMainList.set(i, spacingMainList.get(i) + spacingList.get(i));
		}


		// HVR
		List<Double> hvrMainList = allMeasures.get("HVR");
		List<Double> hvrList = measures.get("HVR");
		for(int i = 0 ; i < hvrList.size() ; i++){
			hvrMainList.set(i, hvrMainList.get(i) + hvrList.get(i));
		}


		// ACCALT
		List<Double> accAltMainList = allMeasures.get("ACCALT");
		List<Double> accAltList = measures.get("ACCALT");
		for(int i = 0 ; i < accAltList.size() ; i++){
			accAltMainList.set(i, accAltMainList.get(i) + accAltList.get(i));
		}

		// Stab
		List<Double> stabMainList = allMeasures.get("Stab");
		List<Double> stabList = measures.get("Stab");
		for(int i = 0 ; i < stabList.size() ; i++){
			stabMainList.set(i, stabMainList.get(i) + stabList.get(i));
		}

		allMeasures.put("VGD", vgdMainList);
		allMeasures.put("Spacing", spacingMainList);
		allMeasures.put("HVR", hvrMainList);
		allMeasures.put("ACCALT", accAltMainList);
		allMeasures.put("Stab", stabMainList);

	}

	private static void combineFiles(int func , int cycles , int maxIter)throws Exception{

		String pofName = String.valueOf("pof-" + CSO.IntegerToString.get(func) + "-nt-" + String.valueOf(nt) + "-towet-" + String.valueOf(towet) + ".txt");
		String posName = String.valueOf("pos-" + CSO.IntegerToString.get(func) + "-nt-" + String.valueOf(nt) + "-towet-" + String.valueOf(towet) + ".txt");
		for(int iter = 1 ; iter < maxIter + 1 ; iter++){
			String pofCycleInfo = "";
			String posCycleInfo = "";
			for(int cycle = 0 ; cycle < cycles ; cycle++){
				// POF
				pofCycleInfo = pofCycleInfo.concat("[");
				String pofFilename = "pof-" + IntegerToString.get(func) + "-" + String.valueOf(cycle) + "-" + String.valueOf(iter) + ".txt";
				String pofContent = readFile(pofFilename);
				pofCycleInfo = pofCycleInfo.concat(pofContent).concat("]");

				// POS
				posCycleInfo = posCycleInfo.concat("[");
				String posFilename = "pos-" + IntegerToString.get(func) + "-" + String.valueOf(cycle) + "-" + String.valueOf(iter) + ".txt";
				String posContent = readFile(posFilename);
				posCycleInfo = posCycleInfo.concat(posContent).concat("]");

			}

			PrintWriter pofOut = new PrintWriter(new BufferedWriter(new FileWriter("./final/" + pofName , true)));
			pofOut.println(String.valueOf(iter));
			pofOut.println(pofCycleInfo);
			pofOut.close();

			PrintWriter posOut = new PrintWriter(new BufferedWriter(new FileWriter("./final/" + posName , true)));
			posOut.println(String.valueOf(iter));
			posOut.println(posCycleInfo);
			posOut.close();

		}



		for(int iter = 1 ; iter < maxIter + 1 ; iter++){
			for(int cycle = 0 ; cycle < cycles ; cycle++){
				String pofFilename = "pof-" + IntegerToString.get(func) + "-" + String.valueOf(cycle) + "-" + String.valueOf(iter) + ".txt";
				String posFilename = "pos-" + IntegerToString.get(func) + "-" + String.valueOf(cycle) + "-" + String.valueOf(iter) + ".txt";

				File pofFile = new File(pofFilename);
				pofFile.delete();

				File posFile = new File(posFilename);
				posFile.delete();

			}

		}



	}

	private static String readFile(String filename) throws Exception{
		String contents = "";
		FileInputStream inputStream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));

		String line = bf.readLine();
		while(line != null){
			contents = contents.concat("[").concat(line).concat("]");
			line = bf.readLine();
			if(line != null)
				contents = contents.concat(",");
		}
		//bf.close();
		return contents;

	}
	private static void printFinalMeanResult(Map<String, Double> finalResult) {
		System.out.println("Means");
		System.out.println("Spacing :" + finalResult.get("Spacing") / NUMCYCLE);
		System.out.println("GD : " + finalResult.get("GD")/ NUMCYCLE);
		System.out.println("IGD : " + finalResult.get("IGD")/ NUMCYCLE);
		System.out.println("VGD : " + finalResult.get("VGD")/ NUMCYCLE);
		System.out.println("MS : " + finalResult.get("MS")/ NUMCYCLE);
		System.out.println("HVR : " + finalResult.get("HVR")/ NUMCYCLE);
		System.out.println("Acc : " + finalResult.get("Acc")/ NUMCYCLE);
		System.out.println("AccAlt : " + finalResult.get("AccAlt")/ NUMCYCLE);
		System.out.println("Stab1 : " + finalResult.get("Stab1")/ NUMCYCLE);
	}


	private static void write(Front pof , int iter , String filename) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename + String.valueOf(iter) + ".txt"));
		for (int i = 0; i < pof.getNumberOfPoints() ; i++) {
			double cost1 = pof.getPoint(i).getDimensionValue(0);
			outputWriter.write(String.valueOf(cost1));
			outputWriter.write(",");
			double cost2 = pof.getPoint(i).getDimensionValue(1);
			outputWriter.write(String.valueOf(cost2));
			outputWriter.newLine();
		}
		outputWriter.flush();  
		outputWriter.close();  
	}

}
