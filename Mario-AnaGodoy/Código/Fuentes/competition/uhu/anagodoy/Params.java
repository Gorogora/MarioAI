package competition.uhu.anagodoy;

import java.nio.file.Paths;

public class Params {
	
	public static final int TOTAL_ACTIONS = 11;
	public static final int LEVEL_TYPES = 3;
	public static final int NUM_MARIO_MODES = 3;
	
	// STATE PARAMETERS	
	public static final int MIN_DISTANCE = 2;
	public static final int NUM_STUCK_FRAMES = 25;
	public static final int MARIO_X = 9;
	public static final int MARIO_Y = 9;
	
	public static final int ZOOM_LEVEL = 1;
	
	public static final class REWARDS{	         
		public static final int DISTANCE = 5;	//5
        //public static final int HEIGHT = 2;
        public static final int COLLISION = 800;	//800
        public static final int ENEMIES_KILLED = 60;
        //public static final int KILLED_BY_FIRE = 60;	//20
        //public static final int KILLED_BY_STOMP = 60;	//20
        public static final int STUCK = -250;	//-20
        public static final int MODE = 50;	//10
        public static final int STATUS = 1000;
	};
	
	
	// QTABLE PARAMETERS
	
	/*
	 * If the gammaValue is 0 then the AI will only consider immediate rewards, 
	 * while with a gammaValue near 1 (but below 1) the AI will try to maximize 
	 * the long-term reward even if it is many moves away.
	 */
	public static final double GAMMA = 0.6;		//0.6
	
	/**
	 * If the learningRate is 1, then the new information completely overrides 
	 * any previous information.
	 */
	public static final double ALPHA = 0.25;		//0.25
	
	public static final double EPSILON = 0.3;	//0.3
	
	private static String fileName = "QTable-"+Params.ALPHA+"-"+"-"+Params.GAMMA+"-"+Params.EPSILON;
	//private static String fileName = "QTable-"+0.3+"-"+"-"+0.6+"-"+0.3;
	//private static String fileName = "QTable-"+0+"-"+"-"+0+"-"+0;
	public static final String QTABLE_PATH = "competition/uhu/anagodoy/QTables/" + fileName + ".ser";
	
	
	// TRAINING PARAMETERS
	public static final int TRAINING_ITERATIONS = 40000;	//10000(media hora)		40000 (2 horas)
	public static final int TRAINING_SEED = 1000000;
	public static final double DECREMENT = 0.05;
	
	
	// EVALUATION PARAMETERS
	public static final int EVALUATION_ITERATIONS = 1000;
	public static final int EVALUATION_SEED = 0;
	
	
	// RESUMEN PARAMETERS
	public static final String resFileName = "QTable-"+Params.ALPHA+"-"+"-"+Params.GAMMA+"-"+Params.EPSILON;
	public static final String RESUMEN_PATH = "src/competition/uhu/anagodoy/Resultados/" + fileName + ".csv";

}
