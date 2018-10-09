package competition.uhu.anagodoy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A table of action values indexed by state and action, initially in a optimistic way (bigger values 
 * than rewards expected)
 * @author ana
 *
 */
public class QTable {
	
	/**
	 * Table to store q-values. Each state has an array of q-values for all the actions.
	 */
	private Hashtable<State, double[]> table;
	
	/**
	 * The state of the world when the action has been performed.
	 */
	private State prevState;
	
	/**
	 * The index of the action with which the current state has been reached.
	 */
	private int prevAction;
	
	/**
	 * Probability for e-greedy policy to choose the next action to perform.
	 */
	private double epsilon;
	
	/**
	 * The discount rate denotes how much future state is taken into account [0,1].
	 */
	private double gamma;
	
	/**
	 * The learning rate determines how new information affects accumulated 
	 * information from previous instances.
	 */
	private double alpha;
	
	
	private Random rnd;
	
	private File f;
	
	
	public QTable() {
		table = new Hashtable<State, double[]>();
		prevState = new State();
		epsilon = Params.EPSILON;
		gamma = Params.GAMMA;
		alpha = Params.ALPHA;
		rnd = new Random(Params.TRAINING_SEED);
		f = new File(Params.QTABLE_PATH);
	}
	
	/**
	 * Update the (prevState, prevAction) entry in the qtable.
	 * @param reward The reward at the current state.
	 * @param currentState The current perception of the Mario's world.
	 */
	public void updateQvalue(double reward, State currentState) {
		//Q(s,a) = Q(s,a) + alpha * (reward + gamma + maxQ(s',a')- Q(s,a))		
		double[] prevQvalues = new double[Params.TOTAL_ACTIONS];
		prevQvalues = getQvalues(prevState);
		double prevQ = prevQvalues[prevAction];
	
		int bestAction = getBestAction(currentState);
		double bestQvalue = getQvalues(currentState)[bestAction];
		
		double newQ =  prevQ + alpha * (reward + gamma*bestQvalue - prevQ);
		
		prevQvalues[prevAction] =  newQ;	//así está en la de standford
		table.put(prevState, prevQvalues); 	//linea 72 + esta
		//table.replace(prevState, prevQvalues);	//linea 72 + esta
		
	}
	
	
	/**
	 * Through an e-greedy policy selects the next action.
	 * @param currentState The current perception of the Mario's world.
	 * @return The index of the action that will be perform.
	 */
	public int getNextAction(State currentState) {
		
		prevState = new State(currentState);
		
		prevAction = (rnd.nextDouble()<epsilon)?rnd.nextInt(Params.TOTAL_ACTIONS):getBestAction(currentState);
		
		return prevAction;		
	}
	
	public int getNextActionEv(State currentState) {
		
		prevState = new State(currentState);
		
		prevAction = getBestActionEv(currentState);
		
		return prevAction;		
	}

	
	/**
	 * Find the best action to take. If all q-values for the current state are equal,
	 * then the function will always choose the same action.
	 * @param currentState The current perception of the Mario's world.
	 * @return The index of the action that will be perform.
	 */
	private int getBestAction(State currentState) {
		double[] qvalues = getQvalues(currentState);
		
		if(qvalues == null) {
			System.err.println("No hay valores asociados a este estado");
			return 0;
		}
		else {
			double maxQ = qvalues[0];
			int indexMaxQ = 0;
			
			for(int i=1; i<qvalues.length; i++) {
				if(maxQ < qvalues[i]) {
					maxQ = qvalues[i];
					indexMaxQ = i;
				}
			}
			return indexMaxQ;
		}
	}
	
	private int getBestActionEv(State currentState) {
		double[] qvalues = getQvaluesEv(currentState);
		
		if(qvalues == null) {
			//System.err.println("No hay valores asociados a este estado");
			return 6;	//saltar a la derecha
		}
		else {
			double maxQ = qvalues[0];
			int indexMaxQ = 0;
			
			for(int i=1; i<qvalues.length; i++) {
				if(maxQ < qvalues[i]) {
					maxQ = qvalues[i];
					indexMaxQ = i;
				}
			}
			return indexMaxQ;
		}
	}
	
	
	/**
	 * Returns an array of Q values for all the actions available at given state. 
	 * If the current state doesn't already exist in the q_table, then it is initiated
	 * with Q values of 0 for all of the available actions.
	 * @param state The state from we wanna know the Q values.
	 * @return An array of Q values for all the actions available at given state.
	 */
	private double[] getQvalues(State state) {
		if(!table.containsKey(state)) {
			double[] initialQvalues = getInitialQvalues();
			table.put(state, initialQvalues);
			return initialQvalues;
		}
		
		return table.get(state);		
	}

	private double[] getQvaluesEv(State state) {
		/*if(!table.containsKey(state)) {
			double[] initialQvalues = null;
			//table.put(state, initialQvalues);
			return initialQvalues;
		}*/
		
		return table.get(state);		
	}
	
	/**
	 * Return an array with Q values of 0 for all of the available actions, 
	 * that's means the values associated to a state that never has been visited. 
	 * @return Return an array with initials Q values.
	 */
	private double[] getInitialQvalues() {
		double[] initialQvalues = new double[Params.TOTAL_ACTIONS];
		
		//double min = -1080, max = 1160;
		double min = 0, max = 1935;
		
		for(int i=0; i<initialQvalues.length; i++) {			
			//initialQvalues[i] = ThreadLocalRandom.current().nextDouble(min, max + 1);
			//initialQvalues[i] = ThreadLocalRandom.current().nextDouble(0, max + 1);
			initialQvalues[i] = 0;
		}
		
		return initialQvalues;
	}
	
	
	public void saveQtable() throws IOException {
						
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		int s = 1;
		
		for(State key : table.keySet()) {
			bw.write("State " + s);
			bw.newLine();
			String state = key.parse();
			bw.write(state);
			//bw.write(Arrays.toString(table.get(key)));
			for(double value : table.get(key)) {
				//bw.write(String.valueOf(value) + ",");
				bw.write(new BigDecimal(value).toPlainString() + ",");
			}
			bw.newLine();
			s++;
		}
		
		bw.close();
		System.out.println("Tabla guardada");
		
	}
	
	public void loadQTable() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String line = br.readLine();	//contiene state i
		
		while(line != null) {
			State s = new State();
			
			s.setStuckCount(Integer.parseInt(br.readLine()));
			s.setIsStuck(Integer.parseInt(br.readLine()));
			s.setKillsTotal(Integer.parseInt(br.readLine()));
			s.setLastKillsTotal(Integer.parseInt(br.readLine()));
			s.setCollisionsWithCreatures(Integer.parseInt(br.readLine()));
			s.setLastCollisionsWithCreatures(Integer.parseInt(br.readLine()));
			s.setdDistance(Integer.parseInt(br.readLine()));
			s.setLastDistance(Integer.parseInt(br.readLine()));
			s.setMode(Integer.parseInt(br.readLine()));
			s.setLastMode(Integer.parseInt(br.readLine()));
			s.setStatus(Integer.parseInt(br.readLine()));
			s.setCanJump(Boolean.parseBoolean(br.readLine()));
			s.setOnGround(Boolean.parseBoolean(br.readLine()));
			// OBSTACLES
			line = br.readLine();
			String[] data = line.substring(1, line.length()-1).split(", ");
			boolean[] obstacles = new boolean[4];
			int i=0;
			for(String o : data) {
				obstacles[i] = Boolean.parseBoolean(o);
				i++;
			}
			s.setObstacles(obstacles);
						
			//nearbyEnemies
			boolean[][] nearbyEnemies = new boolean[4][4];
			data = br.readLine().split(",");
			int k=0;
			for(i=0; i<4; i++) {
				for(int j=0; j<4; j++) {
					nearbyEnemies[i][j] = Boolean.parseBoolean(data[k]);
					k++;
				}				
			}
			s.setNearbyEnemies(nearbyEnemies);
			
			//Q-values
			/*line = br.readLine();
			data = line.substring(1, line.length()-1).split(", ");
			double[] values = new double[Params.TOTAL_ACTIONS];
			k=0;
			for(String v : data) {				
				values[k] = (double)Double.parseDouble(v);
				k++;
			}*/
			String[] qvalues = br.readLine().split(",");
			double[] values = new double[Params.TOTAL_ACTIONS];
			k=0;
			for(String value : qvalues) {
				values[k] = Double.parseDouble(value);
				k++;
			}
			
			table.put(s, values);
			
			//state i
			line = br.readLine();
			
		}
		
		br.close();
		System.out.println("Tabla cargada");
	}
	
	public void save() {
	    try {
	        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Params.QTABLE_PATH));
	        oos.writeObject(table);
	        oos.close();
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public void load() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Params.QTABLE_PATH));
			table = (Hashtable<State, double[]>)ois.readObject();
			ois.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Hashtable<State, double[]> getTable() {
		return table;
	}


	public void setTable(Hashtable<State, double[]> table) {
		this.table = table;
	}


	public State getPrevState() {
		return prevState;
	}


	public void setPrevState(State prevState) {
		this.prevState = prevState;
	}


	public int getPrevAction() {
		return prevAction;
	}


	public void setPrevAction(int prevAction) {
		this.prevAction = prevAction;
	}


	public double getEpsilon() {
		return epsilon;
	}


	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}


	public double getGamma() {
		return gamma;
	}


	public void setGamma(double gamma) {
		this.gamma = gamma;
	}


	public double getAlpha() {
		return alpha;
	}


	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}	
	
	
	public static void main(String[] args) {
		/*State s = new State();
		QTable table = new QTable();
		
		table.updateQvalue(s.getReward(), s);		
		for(State state : table.getTable().keySet()) {
			System.out.println("Key");
			for(double value : table.getTable().get(state)) {
				System.out.print(value + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("Previous state: " + table.getNextAction(s));
		s.setdDistance(1);
		table.updateQvalue(s.getReward(), s);			
		for(State state : table.getTable().keySet()) {
			System.out.println("Key");
			for(double value : table.getTable().get(state)) {
				System.out.print(value + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("Previous state: " + table.getNextAction(s));
		s.setCanJump(true);
		s.setCollisionsWithCreatures(10);
		s.setdDistance(2);
		table.updateQvalue(s.getReward(), s);
		
		for(State state : table.getTable().keySet()) {
			System.out.println("Key");
			for(double value : table.getTable().get(state)) {
				System.out.print(value + " ");
			}
			System.out.println();
		}*/
		
		/* PARTE 2
		Hashtable<State, double[]> table = new Hashtable<State, double[]>();
		double[] values = new double[4];
		State prevState = new State();
		State s = new State();
		System.out.println(s.equals(prevState) + "(true)");
		table.put(prevState, values);
		table.put(s, values);
		System.out.println("size: " + table.size() + "(1)");
		prevState = new State(s);
		System.out.println(s.equals(prevState) + "(true)");
		s = new State(s);
		s.setdDistance(1);
		System.out.println(s.equals(prevState) + "(false)");
		table.put(prevState, values);
		System.out.println("size: " + table.size() + "(1)");
		table.put(s, values);
		System.out.println("size: " + table.size() + "(2)");
		prevState = new State(s);
		System.out.println(s.equals(prevState) + "(true)");
		s = new State(s);
		s.setdDistance(2);
		for(State key : table.keySet()) {
			if(key == s) {
				System.out.println("Misma referencia");
			}
		}	
		System.out.println(s.equals(prevState) + "(false)");
		table.put(prevState, values);
		System.out.println("size: " + table.size() + "(2)");
		table.put(s, values);
		System.out.println("size: " + table.size() + "(3)");
		
		*/
		
		// TEST SAVE QTABLE
		/*QTable table = new QTable();
		State s = new State();
		s.setdDistance(5);
		table.updateQvalue(s.getReward(), s);
		System.out.println("Prev: " + table.getNextAction(s));
		s = new State(s);
		s.setCollisionsWithCreatures(4);
		table.updateQvalue(s.getReward(), s);
		System.out.println("Prev: " + table.getNextAction(s));
		s = new State(s);
		boolean[][] nearbyEnemies = new boolean[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nearbyEnemies[i][j] = true;
			}
		}
		nearbyEnemies[0][1] = false;
		nearbyEnemies[2][3] = false;
		s.setNearbyEnemies(nearbyEnemies);
		table.updateQvalue(s.getReward(), s);
		System.out.println("Prev: " + table.getNextAction(s));
		
		try {
			table.save();
			System.out.println("Size: " + table.getTable().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		/*QTable qtable = new QTable();
		try {
			qtable.load();
			
			for(State state : qtable.getTable().keySet()) {
				System.out.println(state.toString());
				//System.out.println("Key");
				for(double value : qtable.getTable().get(state)) {
					System.out.print(value + " ");
				}
				System.out.println();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

}
