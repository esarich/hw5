import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * 
 * @author Kent Ratliff and Emily Sarich
 *Generates text by character that approximates user input training data. 
 *The training file needs to be of type .txt and located in the same directory as the program
 * 
 */
public class textGenerator {
	
	public static void main(String[] args) throws IOException {

		Scanner scan = new Scanner(System.in);

		String file="";
		

		while(!file.equals("q")){

			//get info on what we're reading and how we'll process
			System.out.print("Please enter a training file, q to quit: ");
			file = scan.nextLine();
			System.out.println(FileScan(file));


			System.out.print("Please enter the k value: ");
			int k = Integer.parseInt(scan.nextLine());
			
			//generate the training Hashmap
			HashMap<String, ArrayList<String>> map = train(file,k);
			
			
			int numGen = 0;
			while(numGen <=0 ){
				System.out.print("Enter number of characters to generate: ");
				numGen = Integer.parseInt(scan.nextLine());
			}
			//generate text based off our training data
			generateText(map, numGen, k);

		}
		scan.close();
	}
	
	/**
	 * Reads from a file and returns an ArrayList of each line in that file, 
	 * stripping out things we don't want (assuming data are tweets)
	 * @param txt
	 * @return
	 */
	public static ArrayList<String> FileScan(String txt) {
		
		//initialize data structure to be built and returned
		ArrayList<String> text = new ArrayList<String>();
		
		//read from file
		File file = new File(txt);
		
		try{
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine()){
				String s = scan.nextLine();
				
				//strip all punctuation
				s = s.replaceAll("[\\,\\.\\?\\!]", "");
				
				//create a string to store the edited tweets
				String edited = "";
				
				//get an array of each word from a tweet to 
				String[] words = s.split(" ");
				
				//for all words in a tweet check if it is a hastag, retweet, username, or web address
				for (int i = 0; i < words.length; i ++){		
					
					//only add the normal text (exluded all prementioned)
					if (!words[i].contains("@") && !words[i].contains("#") && !words[i].contains("/") && !words[i].contains("RT")){ 
						
						//if it is not the last word, put a space after it
						if (i != words.length -1){
							edited += words[i]+ " ";
						}
						else{
							edited += words[i];
						}
					}
				}
				//add the edited text to our returnable
				text.add(edited);
			}
			scan.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return text;
		
	}

	
	/**
	 * Trainer: sets up the transitional HashMap for the given k and returns it.
	 * @param file
	 * @param k
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> train(String file, int k){
		/*Method:
		 * 1) Read a k-gram, X. 
		 *2) Create the next k-gram S by concatenating the next character in the text to the last k-1 characters of X. 
		 *3) If X is not in the table, create a new ArrayList for X; add S to X’s ArrayList.
		 *4) If X is in the table, add S to X’s ArrayList. 
		 */
		
		ArrayList<String> tweets = FileScan(file);
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		
		for (int i = 0; i < tweets.size(); i ++){
			
			//iterate through all tweets from a twitter to look at specific tweets
			String tweet = tweets.get(i);
			
			for (int j = 0; j < tweet.length()-k; j ++){
				//for each tweet, follow training method above
				String kgram = tweet.substring(j,j+k);
				String next = tweet.substring(j+1, j+k+1);
				
				if (!map.keySet().contains(kgram)) {
					ArrayList<String> to_add = new ArrayList<String>();
					to_add.add(next);
					map.put(kgram, to_add);
					
				}else{
					ArrayList<String> value = map.get(kgram);
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
	public static void generateText(HashMap<String,ArrayList<String>> map, int numGen, int k) throws IOException{
		//write files char-outk.txt
		/*Method
		 *1) Start with a random k-gram, X, and output X
		 *2) Repeat: 
		 *a. Retrieve X’s list of transitional k-grams from the hash-table. 
		 *b. Randomly select a k-gram, S, from X’s list.
		 *c. Output c the last character of S.
		 *d. Set X to the next k-gram which is the last k-1 characters of X concatenated with c.
		 */
		
		Random rand = new Random();
		ArrayList<Object> keys = new ArrayList<Object>();
		
		//Use a for loop to put elements from the set of Keys into and ArrayList
		//for ease of use
		for (int j = 0; j < map.keySet().size(); j++){
			keys.add(map.keySet().toArray()[j]);
		}
		
		int leng = keys.size();
		
		//Must cast from Object to String, because we are familiar with our data, they will always be strings
		String x = (String) keys.get(rand.nextInt(leng));
		
		String output = x;
		
		//Execute the above method
		while (output.length() < numGen){
			
			if (keys.contains(x)){
				ArrayList<String> trans = map.get(x);
				
				//Select a random transitional k-gram
				String s = trans.get(rand.nextInt(trans.size()));
				//get the last element of said k-gram
				String c = s.substring(k-1);
				
				//add last element to output
				output+= c;
				//update x
				x = x.substring(1) + c;
			}else{
				break;
			}
		}
		System.out.println(output);
		
		File our_file = new File("char-out"+k+".txt");
		 
		//if there is no file at that location
		if (!our_file.exists()) {
			
			our_file.createNewFile();
		}

		FileWriter write = new FileWriter(our_file.getAbsoluteFile());
		BufferedWriter buff_write = new BufferedWriter(write);
		buff_write.write(output);
		buff_write.close();
		
	}

}