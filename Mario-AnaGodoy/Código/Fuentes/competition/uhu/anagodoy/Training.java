package competition.uhu.anagodoy;

import java.util.Random;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public final class Training {
	
	public static void main(String[] args) {
		Random rnd = new Random();
		rnd.setSeed(Params.TRAINING_SEED);
		
		int wins = 0;
		//int itDecrease = Params.TRAINING_ITERATIONS / 15; 	 
		//int count = 0;
		
		final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
	    final BasicTask basicTask = new BasicTask(marioAIOptions);
	    marioAIOptions.setVisualization(false);
	    marioAIOptions.setZLevelEnemies(Params.ZOOM_LEVEL);
	    marioAIOptions.setZLevelScene(Params.ZOOM_LEVEL);
	    //marioAIOptions.setLevelRandSeed(Params.TRAINING_SEED);
	    
	    for(int it = 0; it < Params.TRAINING_ITERATIONS; it++) {
	    	marioAIOptions.setLevelRandSeed(rnd.nextInt(Params.TRAINING_SEED));
	    	marioAIOptions.setLevelType(rnd.nextInt(Params.LEVEL_TYPES));	    	
	    	for(int mode = 0; mode < Params.NUM_MARIO_MODES; mode++) {
	    		marioAIOptions.setMarioMode(mode);
	    		basicTask.doEpisodes(1, false, 1);
	    		if(basicTask.getEnvironment().getMarioStatus() == Mario.STATUS_WIN) {
	    			System.out.println("WINS");
	    			wins++;
	    		}	    		
	    	}
	    	
	    	System.out.println("Iteración: " + it);
	    }
	    
	    System.out.println("Partidas ganadas: " + wins);
	    
	    
	    try {
			//((AnaGodoyRL) marioAIOptions.getAgent()).getQtable().saveQtable();
	    	((AnaGodoyRL) marioAIOptions.getAgent()).getQtable().save();
			System.out.println(((AnaGodoyRL) marioAIOptions.getAgent()).getQtable().getTable().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
	    System.exit(0);
	}

}
