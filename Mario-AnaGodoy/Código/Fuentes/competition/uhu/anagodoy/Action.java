package competition.uhu.anagodoy;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

/**
 * Mario actions descriptions.
 * @author ana
 *
 */
public enum Action {
	
	RIGHT_JUMP_FIRE(0, Mario.KEY_RIGHT, Mario.KEY_JUMP, Mario.KEY_SPEED),
	RIGHT_JUMP(1, Mario.KEY_RIGHT, Mario.KEY_JUMP),
	RIGHT_FIRE(2, Mario.KEY_RIGHT, Mario.KEY_SPEED),
	JUMP_FIRE(3, Mario.KEY_JUMP, Mario.KEY_SPEED),
	JUMP(4, Mario.KEY_JUMP),	
	FIRE(5, Mario.KEY_SPEED),
	RIGHT(6, Mario.KEY_RIGHT),
	LEFT_JUMP_FIRE(7, Mario.KEY_LEFT, Mario.KEY_JUMP, Mario.KEY_SPEED),
	LEFT_JUMP(8, Mario.KEY_LEFT, Mario.KEY_JUMP),
	LEFT_FIRE(9, Mario.KEY_LEFT, Mario.KEY_SPEED),
	LEFT(10, Mario.KEY_LEFT);	
	
	
	/**
	 * Number matching the action  
	 */
	private final int actionNumber;
	
	/**
	 * Array containing True in those cells corresponding to the "keys" that you have to press
	 * to perform the action actionNumber
	 */
	private final boolean[] action;

	private Action(int actionNumber, int... keys) {
	    this.actionNumber = actionNumber;
	    
	    this.action = new boolean[6];
	    for (int key : keys) {
	      this.action[key] = true;
	    }
	  }

	public int getActionNumber() {
		return actionNumber;
	}

	public boolean[] getAction() {
		return action;
	}

	public static boolean[] getAction(int actionNumber) {
		return Action.values()[actionNumber].getAction();
	}
	
	public static void main(String[] args) {
		
		System.out.println(Action.values()[4]);
		
		/*for(boolean value : Action.values()[4].getAction()) {
			System.out.print(value + " ");
		}*/
		
		
	}

}
