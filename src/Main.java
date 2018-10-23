import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		//import both .dat file
		ArrayList<String[]> data = GetData("C:\\\\Users\\Daniel\\Documents\\Programming Projects\\Java\\Workspace\\CS4375HW2P2\\src\\train.dat");
		ArrayList<String[]> data2 = GetData("C:\\\\Users\\Daniel\\Documents\\Programming Projects\\Java\\Workspace\\CS4375HW2P2\\src\\test.dat");
		int size = data.get(0).length;
		//use the testing data to create the priors 
		double classZeroPrior;
		double classOnePrior;
		//the class value arrays are indexed by multiplying the corresponding data position by 2 and adding it's value, either 1 or 0
		//so, to find the conditional probability of position 3 when it is 0 and the class value is 1, you access classValueOne[3*2 + 0].
		double[] classValueZero = new double[(size-1)*2]; //this contains a spot for the given class prior and it's corresponding class conditional probabilities
		double[] classValueOne = new double[(size-1)*2];	
		
		//loop that finds the the two class priors
		int temp = 0;
		for(int i = 1; i < data.size(); i++){
			if(data.get(i)[size-1].compareTo("1") == 0){
				temp++;
			}
		}
		classOnePrior = (double)temp/(double)(data.size() - 1);
		classZeroPrior = 1 - classOnePrior;
		//finds the corresponding conditional probabilities
		for(int i = 0; i < (size-1); i++){	//decides variablePos
			for(int j = 0; j < 2; j++){		//decides varValue
				for(int k = 0; k < 2; k++){	//decides classValue
					if(j == 0){
						if(k == 0){
							//System.out.println(i*2+j);
							classValueZero[i*2 + j] = GetCCP(data, i, "0", "0");
						}else{
							classValueOne[i*2 + j] = GetCCP(data, i, "0", "1");
						}
					}else{
						if(k == 0){
							classValueZero[i*2 + j] = GetCCP(data, i, "1", "0");
						}else{
							classValueOne[i*2 + j] = GetCCP(data, i, "1", "1");
						}
					}
				}
			}
		}
		System.out.format("P(class = 0) = %.2f%%, ", classZeroPrior*100);
		for(int i = 0; i < classValueZero.length; i++){
			System.out.format("P(%s = %d|%s) = %.2f%%, ", data.get(0)[i/2], i%2, "0", classValueZero[i]*100);
		}
		System.out.println("");
		System.out.format("P(class = 1) = %.2f%%, ", classOnePrior*100);
		for(int i = 0; i < classValueOne.length; i++){
			System.out.format("P(%s = %d|%s) = %.2f%%, ", data.get(0)[i/2], i%2, "1", classValueOne[i]*100);
		}
		System.out.println("");
		
		System.out.format("Accuracy on training set (%d instances): %.2f%%%n", data.size()-1, NaiveBayesLearning(data, classZeroPrior, classOnePrior, classValueZero, classValueOne)*100);
		System.out.format("Accuracy on test set (%d instances): %.2f%%%n", data2.size()-1, NaiveBayesLearning(data2, classZeroPrior, classOnePrior, classValueZero, classValueOne)*100);
	}
	//performs the bayes learning algorithm returning the percent correct
	public static double NaiveBayesLearning(ArrayList<String[]> data, double p0, double p1, double[] ccp0, double[] ccp1){
		int correctness = 0;
		int size = data.get(0).length-1;
		double[] arrayTemp = {p0, p1};
		for(int i = 1; i < data.size(); i++){
			//takes the product sums of the corresponding probabilities
			for(int j = 0; j < size; j++){
				if(data.get(i)[j].compareTo("0") == 0){
					arrayTemp[0] *= ccp0[j*2];
					arrayTemp[1] *= ccp1[j*2];
				}else{
					arrayTemp[0] *= ccp0[j*2 + 1];
					arrayTemp[1] *= ccp1[j*2 + 1];
				}
				
			}
			//compares to decide if the guess is the same as the actual class value
			if(arrayTemp[0] > arrayTemp[1] && data.get(i)[size].compareTo("0") == 0){
				correctness++;
			}else if(arrayTemp[0] < arrayTemp[1] && data.get(i)[size].compareTo("1") == 0){
				correctness++;
			}
			//reset the array
			arrayTemp[0] = p0;
			arrayTemp[1] = p1;
		}
		
		return (double)correctness/(double)(data.size()-1);
	}
	//This function takes the data, the desired variable, it's desired value and the corresponding classValue and returns a class conditional probability
	public static double GetCCP(ArrayList<String[]> data, int variablePos, String varValue, String classValue){ 
		double conProb = 0;
		int temp = 0;
		int sumOfClass = 0;
		int size = data.get(0).length;
		for(int i = 1; i < data.size(); i++){
			if(data.get(i)[size-1].compareTo(classValue) == 0){
				sumOfClass++;
				if(data.get(i)[variablePos].compareTo(varValue) == 0){
					temp++;
				}
			}
		}
		conProb = (double)temp/(double)sumOfClass;
		return conProb;
	}
	public static ArrayList<String[]> GetData(String file) throws FileNotFoundException{
		File f = new File(file);
		Scanner sc = new Scanner(f);
		String placeholder = "";
		int size = 0;
		ArrayList<String[]> info;
		while(sc.hasNext()){
			placeholder = sc.next();
			
			if(placeholder.compareTo("1") == 0 || placeholder.compareTo("0") == 0)
				break;
			else
				size++;
		};
		info = new ArrayList<String[]>();
		String[] temp = new String[size];
		temp[0] = placeholder;
		for(int i = 1; i < size; i++){
			if(sc.hasNext()){
				temp[i] = sc.next();
			}
		}
		info.add(temp);
		temp = new String[size];
		while(sc.hasNext()){
			for(int i = 0; i < size; i++){
				if(sc.hasNext()){
					temp[i] = sc.next();
				}
			}
			info.add(temp);
			temp = new String[size];
		}
		sc.close();
		sc = new Scanner(f);
		for(int i = 0; i < size; i++){
			if(sc.hasNext()){
				temp[i] = sc.next();
			}
		}
		info.add(0, temp);
		sc.close();
		return info;
	}
}
