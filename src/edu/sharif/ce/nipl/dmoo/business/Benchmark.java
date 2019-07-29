package edu.sharif.ce.nipl.dmoo.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import edu.sharif.ce.nipl.dmoo.CSO;
import edu.sharif.ce.nipl.dmoo.entity.Cat;


public class Benchmark {

	private int nVar;
	private double minVar;
	private double maxVar;
	private double nt;
	private double towet;
	private int nVar1;
	private double minVar1;
	private int nVar2;
	private double maxVar1;
	private double minVar2;
	private double maxVar2;
	private int func;
	private static int r;
	private static boolean flag;

	private Random rnd;

	private static final double step = 0.01;

	public Benchmark(int nVar1 , int nVar2 , double minVar1 , double maxVar1 , double minVar2 , double maxVar2 , int func , int towet , int nt) {
		super();

		this.nVar = nVar1 + nVar2;
		this.nVar1 = nVar1;
		this.nVar2 = nVar2;

		this.minVar1 = minVar1;
		this.maxVar1 = maxVar1;

		this.minVar2 = minVar2;
		this.maxVar2 = maxVar2;

		this.func = func;

		rnd = new Random();

		r = -1;
		flag = false;


		this.towet = towet;
		this.nt = nt;
	}


	public double evaluate(Cat cat, int relatedFunction , int iter){
		if(func == CSO.FDA1){
			return FDA1(cat, relatedFunction, iter);
		}
		else if(func == CSO.FDA3){
			return FDA3(cat, relatedFunction, iter);
		}
		else if(func == CSO.DMOP1){
			return DMOP1(cat, relatedFunction, iter);
		}
		else if(func == CSO.DMOP2){
			return DMOP2(cat, relatedFunction, iter);
		}
		else if(func == CSO.DIMP2){
			return DIMP2(cat, relatedFunction, iter);
		}
		else if(func == CSO.DMOP2DEC){
			return DMOP2DEC(cat, relatedFunction, iter);
		}
		else if(func == CSO.DMOP2ISO){
			return DMOP2ISO(cat, relatedFunction, iter);
		}
		else if(func == CSO.DMOP3){
			return DMOP3(cat, relatedFunction, iter);
		}
		else if(func == CSO.HE2){
			return HE2(cat, relatedFunction, iter);
		}
		else if(func == CSO.HE7){
			return HE7(cat, relatedFunction, iter);
		}
		else if(func == CSO.HE9){
			return HE9(cat, relatedFunction, iter);
		}
		else
			return 0;

	}


	//FDA1
	public double FDA1(Cat cat, int relatedFunction , int iter){
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = Math.sin(0.5 * Math.PI * t);
		double[] position = cat.getPosition();

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				temp[1] += Math.pow(position[i] - G , 2);
			}
			double g = 1 + temp[1];
			double h = 1 - (Math.sqrt((double)(position[0]) / (double)(g))) ;

			result = (double)(g * h);
		}
		return result;
	}

	public Front generatePFTrueForFDA1(){
		Front PFTrue = new ArrayFront(100 , 2);

		double i = 0;
		int index = 0;
		while(i <= 1){
			double x = i;
			double y = 1 - Math.sqrt(i);
			PFTrue.setPoint(index, new ArrayPoint(new double[]{x,y}));
			index++;
			i += step;
		}
		return PFTrue;
	}



	//FDA3
	public double FDA3(Cat cat, int relatedFunction , int iter){
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = Math.abs(Math.sin(0.5 * Math.PI * t));
		double F = Math.pow(10 , 2 * Math.sin(0.5 * Math.PI * t) );
		double[] position = cat.getPosition();

		if(relatedFunction == 0){
			for(int i = 0 ; i < nVar1 ; i++){
				temp[0] += Math.pow(position[i] , F);
			}
			result = temp[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				temp[1] += Math.pow(position[i] - G , 2);
			}
			double g = 1 + G + temp[1];

			for(int i = 0 ; i < nVar1 ; i++){
				temp[2] += Math.pow(position[i] , F);
			}
			double h = 1 - (Math.sqrt((double)(temp[2]) / (double)(g)));

			result = g * h;
		}
		return result;
	}


	public Front generatePFTrueForFDA3(int iter) {
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = Math.abs(Math.sin(0.5 * Math.PI * t));
		double F = Math.pow(10 , 2 * Math.sin(0.5 * Math.PI * t) );

		Front PFTrue = new ArrayFront(100, 2);

		double i = 0;

		int index = 0;
		while(i <= 1){
			double x = 5 * Math.pow(i , F);
			double y = (1 + G) * (1 - Math.sqrt(x /(1+G)));
			PFTrue.setPoint(index, new ArrayPoint(new double[]{x,y}));
			index++;
			i += step;
		}
		return PFTrue;
	}


	//DMOP1
	public double DMOP1(Cat cat, int relatedFunction , int iter){
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;
		double[] position = cat.getPosition();

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				temp[1] += Math.pow(position[i] , 2);
			}
			double g = 1 + 9 * temp[1];
			double h = 1 - (Math.pow((double)(position[0]) / (double)(g) , H));

			result = g * h;
		}
		return result;
	}

	public Front generatePFTrueForDMOP1(int iter) {
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;

		Front PFTrue = new ArrayFront(100 , 2);

		double i = 0;
		int index = 0;
		while(i <= 1){
			double x = i;
			double y = 1 - Math.pow(x, H);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{x, y}));
			i += step;
			index++;
		}
		return PFTrue;
	}


	//DMOP2
	public double DMOP2(Cat cat, int relatedFunction , int iter){
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = Math.sin(0.5 * Math.PI * t);
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				temp[1] += Math.pow(position[i] - G , 2);
			}
			double g = 1 + temp[1];
			double h = 1 - (Math.pow((double)(position[0]) / (double)(g) , H)) ;

			result = (g * h);
		}
		return result;
	}

	public Front generatePFTrueForDMOP2(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.pow(p, H);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;
	}

	//DIMP2	
	private double DIMP2(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double[] position = cat.getPosition();
		int n = nVar1 + nVar2;

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){
			temp[1] = 0;
			for(int i = nVar1 ; i < n ; i++){
				double Gi = Math.sin(0.5 * Math.PI * t + 2 * Math.PI * (Math.pow((double)(i+1) / (n+1) , 2)));
				temp[1] += (Math.pow(position[i] - Gi , 2)) - 2 * Math.cos(3 * Math.PI * (position[i] - Gi));
			}
			double g = 1 + 2 * (n - 1) + temp[1];
			double h = 1 - (Math.sqrt((double)(position[0]) / (double)(g)));

			result = (g * h);
		}
		return result;
	}

	public Front generatePFTrueForDIMP2(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);

		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.sqrt(p);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;

	}


	//DMOP2Dec
	private double DMOP2DEC(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = (Math.sin(0.5 * Math.PI * t));
		double A = 0;
		double B = 0.001;
		 if (G >= 0.7) {
             A = 0.7 - B;
         } else if (G > 0.35) {
             A = G;
         } else {
             A = 0.35;
         }
		
		double C = 0.05;
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				double y = ydec(position[i] , A , B , C);
				temp[1] += Math.pow(y - G , 2);
			}
			double g = 1 + temp[1];
			double h = 1 - Math.pow((double)(position[0]) / (double)(g) , H) ;
			if(g * h > 100){
				System.out.println();
				for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
					double y = ydec(position[i] , A , B , C);
					temp[2] += Math.pow(y - G , 2);
				}
			}

			result = (g * h);
		}
		return result;

	}

	public Front generatePFTrueForDMOP2DEC(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.pow(p, H);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;
	}


	//DMOP2ISO
	private double DMOP2ISO(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = (Math.sin(0.5 * Math.PI * t));
		double A = G;
		double B = 0.001;
		double C = 0.05;
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				double y = ystar(position[i] , A , B , C);
				temp[1] += Math.pow(y - G , 2);
			}
			double g = 1 + temp[1];
			double h = 1 - Math.pow((double)(position[0]) / (double)(g) , H) ;

			result = (g * h);
		}
		return result;
	}

	public Front generatePFTrueForDMOP2ISO(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.pow(p, H);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;

	}


	//DMOP3
	private double DMOP3(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double G = Math.sin(0.5 * Math.PI * t);
		double[] position = cat.getPosition();
		int n = nVar1 + nVar2;
		if(Math.abs(iter % towet) == 1 || r == -1){
			flag = false;
		}
		if(relatedFunction == 0){
			if(iter % towet == 0 || r == -1){
				if(!flag){
					r = Math.abs(rnd.nextInt() % n);
					flag = true;
				}
			}
			result = position[r];
		}
		else if (relatedFunction == 1){

			for(int i = 0 ; i < n ; i++){
				if(i == r)
					continue;
				temp[1] += Math.pow(position[i] - G , 2);
			}
			double g = 1 + temp[1];
			double h = 1 - Math.sqrt((double)(position[r]) / (double)(g)) ;

			result = (g * h);
		}
		return result;
	}

	public Front generatePFTrueForDMOP3(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);

		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.sqrt(p);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;


	}


	//HE2
	private double HE2(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;

		if(relatedFunction == 0){
			result = position[0];
		}
		else if (relatedFunction == 1){

			for(int i = nVar1 ; i < nVar1 + nVar2 ; i++){
				temp[1] += position[i];
			}
			double g = 1 + ((double)(9 / (nVar1 + nVar2 - 1))) * temp[1];
			double h = 1 - Math.pow(Math.sqrt((double)(position[0]) / (double)(g)) , H) - ((Math.pow((double)(position[0]) / (double)(g) , H)) * (Math.sin(10 * Math.PI * position[0])));

			result = (g * h);
		}
		return result;
	}

	public Front generatePFTrueForHE2(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = 1 - Math.pow(Math.sqrt(p) , H) - Math.pow(p , H) * Math.sin(0.5 * Math.PI * p);
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;
	}


	//HE7
	private double HE7(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;
		int n = nVar1 + nVar2;

		List<Integer> odd = new ArrayList<Integer>();
		List<Integer> even = new ArrayList<Integer>();

		for(int i = 1 ; i < nVar1 + nVar2 ; i++){
			if(i % 2 == 0){
				odd.add(i);
			}
			else{
				even.add(i);
			}
		}

		if(relatedFunction == 0){
			temp[0] = 0;
			for(int i : odd){
				temp[0] += Math.pow(position[i] - (0.3 * Math.pow(position[0], 2) * Math.cos(24 * Math.PI * position[0] + (4 * Math.PI * (i+1)) / n ) + 0.6 * position[0] ) * Math.cos(6 * Math.PI * position[0] + (Math.PI * (i+1)) / n ) , 2);
			}
			result = position[0] + ((double)(2 / odd.size())) * temp[0];
		}
		else if (relatedFunction == 1){
			temp[1] = 0;
			for(int i : even){
				temp[1] += Math.pow(position[i] - (0.3 * Math.pow(position[0], 2) * Math.cos(24 * Math.PI * position[0] + (4 * Math.PI * (i+1)) / n ) + 0.6 * position[0] ) * Math.sin(6 * Math.PI * position[0] + (Math.PI * (i+1)) / n ) , 2);
			}

			double g = 2 - Math.sqrt(position[0]) + ((double)(2 / even.size())) * temp[1];
			double h = 1 - Math.pow((double)(position[0] / g), H);
			result = (g * h);
		}
		return result;

	}

	public Front generatePFTrueForHE7(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = (2 - Math.sqrt(p)) * (1 - Math.pow((double)(p) / (2 - Math.sqrt(p)), H));
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;
	}


	//HE9
	private double HE9(Cat cat, int relatedFunction, int iter) {
		double result = 0;
		double[] temp = new double[4];
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double[] position = cat.getPosition();
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;
		int n = nVar1 + nVar2;

		List<Integer> odd = new ArrayList<Integer>();
		List<Integer> even = new ArrayList<Integer>();

		for(int i = 1 ; i < nVar1 + nVar2 ; i++){
			if(i % 2 == 0){
				odd.add(i);
			}
			else{
				even.add(i);
			}
		}

		if(relatedFunction == 0){
			temp[0] = 0;
			for(int i : odd){
				temp[0] += Math.pow(position[i] - Math.sin(6 * Math.PI * position[0] + (Math.PI * (i+1) / n)) , 2);
			}
			result = position[0] + ((double)(2 / odd.size())) * temp[0];
		}
		else if (relatedFunction == 1){
			temp[1] = 0;
			for(int i : odd){
				temp[1] += Math.pow(position[i] - Math.sin(6 * Math.PI * position[0] + (Math.PI * (i+1) / n)) , 2);
			}

			double g = 2 - Math.pow(position[0] , 2) + ((double)(2 / even.size())) * temp[1];
			double h = 1 - Math.pow((double)(position[0] / g), H);
			result = (g * h);
		}
		return result;

	}

	public Front generatePFTrueForHE9(int iter) {
		Front PFTrue = new ArrayFront(100 , 2);
		double t = ((double)1 / nt) * (double)Math.floor((double)(iter) / (double)towet);
		double H = 0.75 * Math.sin(0.5 * Math.PI * t) + 1.25;


		double i = 0;
		int index = 0;
		while(i <= 1){
			double p = i;
			double q = (2 - Math.sqrt(p)) * (1 - Math.pow((double)(p) / (2 - Math.sqrt(p)), H));
			PFTrue.setPoint(index , new ArrayPoint(new double[]{p , q}));
			i += step;
			index++;
		}
		return PFTrue;

	}

	private double ystar(double x , double a , double b , double c){
		return a + Math.min(0, Math.floor(x - b)) * (a * (b - x) / b) - Math.min(0, Math.floor(c - x)) * ((1 - a) * (x - c) / (1 - c)); 
	}

	private double ydec(double x , double a , double b , double c){
		return ( ((double)(Math.floor(x - a + b) * (1 - c + (a - b) / b)) / (a - b) + (double)(1) / b + (double)(Math.floor(a + b - x) * (1 - c + (1 - a - b) / b)) / (1 - a - b))) * (Math.abs(x - a) - b) + 1; 
	}


}
