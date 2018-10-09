package competition.uhu.anagodoy;

import java.util.Random;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public final class Evaluation {

	public static void main(String[] args) {
		Random rnd = new Random();
		rnd.setSeed(Params.TRAINING_SEED);
		
		int wins = 0;
		
		final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
	    final BasicTask basicTask = new BasicTask(marioAIOptions);
	    marioAIOptions.setVisualization(false);
	    marioAIOptions.setZLevelEnemies(Params.ZOOM_LEVEL);
	    marioAIOptions.setZLevelScene(Params.ZOOM_LEVEL);
	    //marioAIOptions.setLevelRandSeed(Params.EVALUATION_SEED);
	    Resumen r = new Resumen();
	    
	    try {
			//((AnaGodoy) marioAIOptions.getAgent()).getQtable().loadQTable();
	    	((AnaGodoy) marioAIOptions.getAgent()).getQtable().load();
			//System.out.println(((AnaGodoy) marioAIOptions.getAgent()).getQtable().getTable().size());
	    	for(int it = 0; it < Params.EVALUATION_ITERATIONS; it++) {
	    		marioAIOptions.setLevelRandSeed(rnd.nextInt(Params.TRAINING_SEED));
		    	marioAIOptions.setLevelType(rnd.nextInt(Params.LEVEL_TYPES));	 		
	    		basicTask.doEpisodes(1, false, 1);
	    		if(basicTask.getEnvironment().getMarioStatus() == Mario.STATUS_WIN) {
	    			System.out.println("WINS");
	    			wins++;
	    		}    		
		    	System.out.println("Iteración: " + it);
		    	
		    	int dist = basicTask.getEvaluationInfo().distancePassedCells;
		    	int coins = basicTask.getEvaluationInfo().coinsGained;
		    	int time = basicTask.getEvaluationInfo().timeSpent;
		    	int win = basicTask.getEvaluationInfo().marioStatus;
		    	int size = ((AnaGodoy) marioAIOptions.getAgent()).getQtable().getTable().size();
		    	
				r.concat(dist, coins, time, size, win);
				
		    }
		    
		    r.close();
		    
		    System.out.println("Partidas ganadas: " + wins);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    System.exit(0);

	}

}
