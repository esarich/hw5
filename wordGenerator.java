import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//import java.math.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;

/**
 * 
 * @author Kent Ratliff and Emily Sarich
 *Generates text by word that approximates user input training data. 
 *The training file needs to be of type .txt and located in the same directory as the program
 */
public class wordGenerator {

	//static HashMap<String, ArrayList<String>> global_map = new HashMap<String, ArrayList<String>>();
	//static Object[] keys =  global_map.keySet().toArray();
	
	public static void main(String[] args) throws IOException {

		Scanner scan = new Scanner(System.in);

		String file="";
		
		

		while(!file.equals("q")){

			//get info on what we're reading and how we'll process
			//System.out.print("Please enter a training file, q to quit: ");
			//file = scan.nextLine();
			//System.out.println(FileScan(file));
			file = "test.txt";


			System.out.print("Please enter the k value: ");
			int k = Integer.parseInt(scan.nextLine());
			//int k = 3;
			
			HashMap<List<String>, ArrayList<List<String>>> map = train(file,k);
			System.out.println("TRAIN: " + map);
			
			int numGen = 0;
			while(numGen <=0 ){
				System.out.print("Enter number of words to generate: ");
				numGen = Integer.parseInt(scan.nextLine());
			}
			generateText(map, numGen, k);
		
		}
		scan.close();
	}
	
	
	/**
	 * Reads from a file and returns an ArrayList of each line in that file, stripping out things we don't want
	 * @param txt
	 * @return
	 */
	public static ArrayList<ArrayList<String>> FileScan(String txt) {
		ArrayList<ArrayList<String>> text = new ArrayList<ArrayList<String>>();
		File file = new File(txt);
		
		try{
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine()){
				String s = scan.nextLine();
				s = s.replaceAll("[\\,\\.\\?\\!]", "");

				ArrayList<String> edited =  new ArrayList<String>();
				String[] words = s.split(" ");
				for (int i = 0; i < words.length; i ++){					 
					if (!words[i].contains("@") && !words[i].contains("#") && !words[i].contains("/") && !words[i].contains("RT")){ 
						edited.add(words[i]);
					}
				}
				text.add(edited);
				//System.out.println("text: " + text);
			}
			scan.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return text;
		
	}

	
	/**
	 * Trainer: sets up the transitional probabilities for the given k. You may modify this method header.
	 * @param file
	 * @param k
	 * @return
	 */
	public static HashMap<List<String>, ArrayList<List<String>>> train(String file, int k){
		//this is where we're training
		//generate hash table
		/*
		 * 1) Read a k-gram, X. 
		 *2) Create the next k-gram S by concatenating the next character in the text to the last k-1 characters of X. 
		 *2) If X is not in the table, create a new ArrayList for X; add S to X’s ArrayList.
		 *3) If X is in the table, add S to X’s ArrayList. 
		 */
		ArrayList<ArrayList<String>> tweets = FileScan(file);
		HashMap<List<String>, ArrayList<List<String>>> map = new HashMap<List<String>, ArrayList<List<String>>>();
		for (int i = 0; i < tweets.size(); i ++){
			//COMMMENT THIS
			ArrayList<String> tweet = tweets.get(i);
			for (int j = 0; j < tweet.size()-k; j ++){
				List<String> kgram = tweet.subList(j,j+k);
				List<String> next = tweet.subList(j+1, j+k+1);
				if (!map.keySet().contains(kgram)) {
					//System.out.println("add a new key: " + kgram);
					ArrayList<List<String>> to_add = new ArrayList<List<String>>();
					to_add.add(next);
					map.put(kgram, to_add);
				}else{
					
					ArrayList<List<String>> value = map.get(kgram);
					value.add(next);
					map.put(kgram, value);
				}
			}
			
		}
		return map;
	
	}



	/**
	 * Generator: Generates numGen characters based on the transitional probabilities estimated by the trainer 
	 * @param map
	 * @param numGen
	 * @param k
	 * @throws IOException 
	 */
	public static void generateText(HashMap<List<String>, ArrayList<List<String>>> map, int numGen, int k) throws IOException{
		//write files char-outk.txt
		/*
		 *1) Start with a random k-gram, X, and output X
		 *2) Repeat: 
		 *a. Retrieve X’s list of transitional k-grams from the hash-table. 
		 *b. Randomly select a k-gram, S, from X’s list.
		 *c. Output c the last character of S.
		 *d. Set X to the next k-gram which is the last k-1 characters of X concatenated with c.
		 */
		
		Random rand = new Random();
		ArrayList<Object> keys = new ArrayList<Object>();
		for (int j = 0; j < map.keySet().size(); j++){
			keys.add(map.keySet().toArray()[j]);
		}
		
		int leng = keys.size();
		
		@SuppressWarnings("unchecked")
		List<String> x = (List<String>) keys.get(rand.nextInt(leng));
		
		List<String> output = new ArrayList<String>(x);
		
		System.out.println("output tpye: " + output.getClass().getName());
		
		
		while (output.size() < numGen){
			
			System.out.println("our key (x): " + x);
			String c;
			
			if (keys.contains(x)){
				ArrayList<List<String>> trans = map.get(x);
				
				int trans_size = trans.size();
				
				int randnum = rand.nextInt(trans_size);
				
				List<String> s = trans.get(randnum);

				c = s.get(k-1);

				output.add(c);
				
				x = x.subList(1, k);
				x.add(c);

				
				
			}else{
				break;
			}
			
		}
		String stringOut= "";
		for (int p =0; p< output.size(); p++){
			//if it is not the last word, put a space after it
			if (p != output.size() -1){
				stringOut += output.get(p) + " ";
			}
			else{
				stringOut += output.get(p);
			}
			
		}
		
		System.out.println(stringOut);
		
		File our_file = new File("word-out"+k+".txt");
		 
		//if there is no file at that location
		if (!our_file.exists()) {
			
			our_file.createNewFile();
		}

		FileWriter write = new FileWriter(our_file.getAbsoluteFile());
		BufferedWriter buff_write = new BufferedWriter(write);
		buff_write.write(stringOut);
		buff_write.close();
	}

}