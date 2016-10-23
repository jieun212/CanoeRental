/* TCSS 343, Summer 2016
 * HW 4 Group:
 *    Jieun Lee (jieun212@uw.edu)
 *    Peter Phe (peterphe@uw.edu)
 *    Jabo Johnigan (jabojohnigan93@yahoo.com)
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


/**
 * This class finds a solution that computes the cheapest sequence of rental
 * taking from post 1 all the way down to post n. This class prints the solution
 * with 3 different approaches, Brute force, Divide-and-conquer, and Dynamic
 * programming. It prints execution times for those approaches and the paths of
 * the cheapest costs from 1 to given post number n.
 * 
 * 
 * @author Jieun Lee (jieun212@uw.edu): Divide-and Conquer, generating input 2-D array
 * @author Peter Phe (peterphe@uw.edu): Brute force
 * @author Jabo Johnigan (jabojohnigan93@yahoo.com): Dynamic Programming
 * 
 */
public class CanoeRental {
	
    
    /**
     * Run this program.
     * 
     * @param args The args
     */
    public static void main(String[] args) {
    	
    	// small input
    	execute("input_1.txt");
    	execute("small_n5.txt");
    	execute("small_n7.txt");
    	execute("small_n10.txt");
    	execute("small_n15.txt");
    	execute("small_n20.txt");

    	
    	// large input
    	execute("input_2.txt");
    	execute("input_3.txt");
//    	execute("input_4.txt");
//    	execute("input_5.txt");
//    	execute("input_6.txt");
    	
    	// removed codes that calls generating input file.
	}



	/**
     * Helper method to run each test based off of input file name.
     */
    private static void execute(String inputFile) {

		// creates 2-D input list from the given text file.
		int[][] costs = createInputArray(inputFile);


		int n = costs.length;
		System.out.println("=== input size = " + n + " x " + n 
				+ "=========================================================\n");
		
		
		// prints costs list - just for testing
		if (n <= 20) {
			printTable(costs, inputFile);
		}
		
		
		// 3.1. executes Brute force algorithm
		if (costs.length <= 10) {
			bruteForce(costs);
		} else {
			System.out.println("Brute Force:");
			System.out.println("\t---Out Of Memory Error---");
		}
		System.out.println();

		
		// 3.2. executes Divide-and-conquer algorithm
		if (costs.length <= 20) {
			executeDivideAndConquer(costs);
		} else {
			System.out.println("Divide & Conquer:");
			System.out.println("\t---Takes too long time---");
		}
		System.out.println();

		
		// 3.3. executes Dynamic Programming algorithm
		dynamicProgramming(costs);

		System.out.println();
		for (int i = 0; i < 80; i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println();

    }
    
    
    
    
    /*
     * 3.1. BRUTE FORCE approach (by Peter Phe)
     */
    
    /**
     * 3.1. BRUTE FORCE 
     * 
     * (written by Peter Phe)
     * 
     * This method attempts a brute force approach for computing a minimum cost path.
     * Note that an "OutOfMemoryError" occurs for moderate input sizes of n.
     * The asymptotic complexity is Θ(n*2^n). The exponential comes from computing
     * the Power Set for the input size 1 to n, while the linear n comes from computing 
     * each set's minimum cost.
     * 
     * @param costs The 2-D cost array.
     */
	public static void bruteForce(int[][] costs) {
		System.out.println("Brute Force:");
		long startTime = System.currentTimeMillis();
		int n = costs.length;
		ArrayList<Set<Integer>> solutionSet = buildSolutionSet(n); // get viable
																	// solution
																	// subsets
		HashMap<Set<Integer>, Integer> costMap = new HashMap<>();

		// find the cost for each possible solution
		for (Set<Integer> s : solutionSet) {
			Iterator<Integer> iter = s.iterator();

			// j: first element (1) in set is consumed (starting post implied in
			// solution)
			int cost = 0, i = 0, j = iter.next();

			// the core for computing the minimum cost of each set from the list
			while (iter.hasNext()) {
				j = iter.next();
				cost += costs[i][--j];
				i = j;
			}

			costMap.put(s, cost);
		}

		// get cheapest path by value
		int minCost = Collections.min(costMap.values());

		Set<Integer> solution = new HashSet<>();
		for (Set<Integer> s : solutionSet) {
			if (minCost == costMap.get(s)) {
				solution = s;
				break;
			}
		}

		// output Brute Force information
		long endTime = System.currentTimeMillis();
		System.out.println("\tExecution Time: " + (endTime - startTime) + " ms");
		System.out.println("\tThe cheapest cost from post 1 to post " + n + ": " + minCost);
		System.out.println("\tThe sequence of the cheapest cost from post1 to post" + costs.length + ": " + solution);

	}

    /**
     * Helper method for BruteForce that builds a list of all possible solutions. Conceptually,
     * the bit strings of values 0-n, left-padded with zeroes as needed, will map to the 
     * original set of 1-n positions. A value of '1' will determine if we select a post as part
     * of a viable solution, whereas a '0' represents a post that is not selected.
     * 
     * (written by Peter Phe)
     * 
     * @param n number of posts (size)
     * @return ArrayList of the viable solutions
     */
	private static ArrayList<Set<Integer>> buildSolutionSet(int n) {
		// generate power set as n-length bit strings for selecting positions
		int pSetSize = (int) Math.pow(2, n);
		ArrayList<String> bitStrings = new ArrayList<>(pSetSize);
		for (int i = 0; i < pSetSize; i++) {
			// left-pad string with zeroes up to n digits as needed
			String s = String.format("%0" + n + "d", Integer.parseInt(Integer.toBinaryString(i)));

			// add only if 1 and n are included (start and end positions)
			if (s.charAt(0) == '1' && s.charAt(n - 1) == '1')
				bitStrings.add(s);
		}

		// create full set of 1 to n elements for mapping bit strings
		Set<Integer> original = new HashSet<Integer>(n);
		for (int i = 1; i <= n; i++)
			original.add(i);

		// map bit strings to original set for solution subset
		ArrayList<Set<Integer>> solutionSet = new ArrayList<>(bitStrings.size());
		for (String s : bitStrings) {
			Set<Integer> t = new HashSet<>();
			Iterator<Integer> iter = original.iterator();

			for (int i = 0; i < s.length(); i++) {
				int p = iter.next();
				if (s.charAt(i) == '1')
					t.add(p);
			}

			solutionSet.add(t);
		}

		// sort solutions by size of set
		Collections.sort(solutionSet, new Comparator<Set<?>>() {
			@Override
			public int compare(Set<?> o1, Set<?> o2) {
				return Integer.valueOf(o1.size()).compareTo(o2.size());
			}
		});

		return solutionSet;
	}
    
    
    
    
    
    

    /*
     * 3.2. DIVIDE-AND-CONQUER (by Jieun Lee)
     */

    /**
     * Executes divide and conquer algorithm.
     * 
     * (written by Jieun Lee)
     * 
     * @param costs The 2-D cost array.
     */
    public static void executeDivideAndConquer(int[][] costs) {
    	
		System.out.println("Divide & Conquer:");

		int n = costs.length;
		int[] sequence = new int[n];

		// records the start time
		long startTime = System.currentTimeMillis();

		int cheapest = divideAndConquer(costs, n - 1, costs[0][n-1], sequence);

		// record the finish time
		long endTime = System.currentTimeMillis();

		// prints the results
		sequence[0] = 1;
		sequence[n - 1] = n;
		System.out.println("\tExecution Time: " + (endTime - startTime) + " ms");
		System.out.println("\tThe cheapest cost from post 1 to post " + n + ": " + cheapest);
		System.out.print("\tThe sequence of the cheapest cost from post1 to post" + costs.length + ": [" + sequence[0]);
		for (int i = 1; i < sequence.length; i++) {
			if (sequence[i] != 0) {
				System.out.print(", " + sequence[i]);
			}
		}
		System.out.println("]");

    }
    
    /**
     * Recursive Divide-and-Conquer().
     * 
     * (Jieun Lee)
     * 
     * @param costs The rental cost table.
     * @param n The number of posts.
     * @param sequence The sequence of the cheapest cost from 1 to n.
     * 
     * @return The cheapest rental cost of given number of posts.
     */
	private static int divideAndConquer(int[][] costs, int n, int cheapest, int[] sequence) {

		// if there is only one post, return 0
		if (n <= 0) {
			return 0;
		}

		// if there is 2 posts p1---p2, return cost from p1 to p2
		if (n == 1) {
			return costs[0][1];
		}

		for (int i = 1; i <= n; i++) {

			int c = costs[n - i][n] + divideAndConquer(costs, n - i, costs[0][n - 1], sequence);
			cheapest = Math.min(cheapest, c);
			
			// gets the sequence of the cheapest cost
			if (c == cheapest) {
				for (int j = n - i + 1; j < n; j++) {
					sequence[j] = 0;
				}
				sequence[0] = 1;
				sequence[n - i] = n - i + 1;
			}

		}

		return cheapest;
	}



    


    /*
     * 3.3. DYNAMIC PROGRAMMING (by Jabo Johnigan)
     */

    /**
     * 3.3. DYNAMIC PROGRAMMING (Unfinished )
     * 
     * (written by Jabo Johnigan)
     * 
     * This method uses the dynamic approach of storing the optimal intermediate results
     * in order to compute the minimum cost path. ADD STUFF HERE
     * 
     * @param costs The 2-D cost array.
     */
	public static void dynamicProgramming(int[][] costs) {
		System.out.println("Dynamic Programming:");
		long startTime = System.currentTimeMillis();

		int min[] = new int[costs.length];
		min[0] = 0;

		ArrayList<Integer> s = new ArrayList<>();

		for (int i = 1; i < costs.length; i++) {
			min[i] = costs[i - 1][i] + min[i - 1];
			s.add(i + 1);

			for (int j = i - 1; j > -1; j--) {
				if ((min[j] + costs[j][i]) < min[i]) {
					min[i] = min[j] + costs[j][i];
					s.set(j, i - 1);
				}
			}
		}

		long endTime = System.currentTimeMillis();

		System.out.println("\tExecution Time: " + (endTime - startTime) + " ms");
		System.out.println("\tThe cheapest cost from post 1 to post " + costs.length + ": " + min[costs.length - 1]);
		
		
		// this sequence has an error !!!
		
//		System.out.println("\tThe sequence of the cheapest cost from post1 to post" + costs.length + ": "
//				+ Arrays.toString(s.toArray()));

	}

	

    /*
     * Generate input 2-D table  from given .txt file
     */

    /**
     * Reads the given text file and creates cost table.
     * 
     * (written by Jieun Lee)
     * 
     * @param text The text file
     * @return 2-D array of costs.
     */
	public static int[][] createInputArray(String text) {

		int[][] costs = null;

		// gets all the costs and stores them into the result[][]
		try {
			Scanner scan = new Scanner(new File(text));
			String token = "";

			// gets input size
			int inputSize = 0;
			while (scan.hasNext()) {
				token = scan.next();
				if (token.equalsIgnoreCase("NA")) {
					break;
				}
				inputSize++;
			}

			// creates costs[][] with the input size
			costs = new int[inputSize][inputSize];

			// stores all the costs to the cost[][]
			scan = new Scanner(new File(text));
			for (int i = 0; i < inputSize; i++) {
				for (int j = 0; j < inputSize; j++) {
					token = scan.next();
					if (token.equalsIgnoreCase("na")) {
						costs[i][j] = -1;
					} else {
						costs[i][j] = Integer.parseInt(token);
					}
				}
			}
			scan.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return costs;
	}
    
    

    /*
     * HELPER METHOD to check the input.
     */

    /**
     * Prints given 2-D array with the table name. 
     * 
     * (written by Jieun Lee)
     * 
     * @param a The 2-D array.
     * @param name The name of the array.
     */
	private static void printTable(int[][] a, String name) {
		if (a.length > 0) {

			System.out.println("<" + name + ">");
			int n = a.length;

			for (int i = 0; i < 8 * n + n; i++) {
				System.out.print("=");
			}

			System.out.print("\n\t" + 1);

			for (int j = 2; j <= n; j++) {
				System.out.print("\t" + j);
			}
			System.out.println();

			for (int i = 0; i < 8 * n + n; i++) {
				System.out.print("-");
			}
			System.out.println();

			for (int i = 0; i < n; i++) {
				System.out.print((i + 1) + " |\t");
				for (int j = 0; j < n; j++) {
					System.out.print(a[i][j] + "\t");
				}
				System.out.println();
			}

			for (int i = 0; i < 8 * n + n; i++) {
				System.out.print("=");
			}
			System.out.println("\n");
		}
	}


}
