package edu.sharif.ce.nipl.dmoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FDA3Measure {

	public static void main(String[] args) throws Exception {
		int noPoints = 100;
		double[][] mat = readFromFile("pftrue.txt", 100, 2);
		double[] referencedPoint = new double[2];
		referencedPoint[0] = maxInColumn(mat , 0); 
		referencedPoint[1] = maxInColumn(mat , 1);
		
		Matrix m = new Matrix(100 , 2);
		m.element = mat;
		
		Matrix sortedMatrix = m.sort();
		double volume = 0;
		
		double[][] elements = sortedMatrix.element;
		double[] area = new double[noPoints]; 
		for(int i = 0 ; i < noPoints ; i++){
			volume += Math.abs((referencedPoint[0] - elements[i][0]) * (referencedPoint[1] - elements[i][1]));
			area[i] = Math.abs((referencedPoint[0] - elements[i][0]) * (referencedPoint[1] - elements[i][1]));
			referencedPoint[1] =  elements[i][1];
		}
		
		writeValue(area);
		System.out.println(volume);
	}
	
	private static void writeValue(double[] area) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter("area"));
		for(int i = 0 ; i < area.length ; i++){
			double cost1 = area[i];
			outputWriter.write(String.valueOf(cost1));
			outputWriter.newLine();
		}

		outputWriter.flush();  
		outputWriter.close();  
	}
	
	private static double maxInColumn(double[][] mat, int col) {
		double max = Double.MIN_VALUE;
		for(int i = 0 ; i < mat.length ; i++){
			if(mat[i][col] > max){
				max = mat[i][col];
			}
		}
		return max;
	}

	public static double[][] readFromFile(String filename , int row , int col) throws Exception{
		int lineCount = 0;
		FileInputStream inputStream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));

		String line = "";
		double[][] matrix = new double[row][col];
		while ((line = bf.readLine()) != null)
		{
			String[] numbers = line.split(",");
			for ( int i = 0 ; i < col ; i++) 
				matrix[lineCount][i] = Double.parseDouble(numbers[i]);
			lineCount++;
		}
		return matrix;
	}
}
