package competition.uhu.anagodoy;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class AnaGodoyRL extends BasicMarioAIAgent implements Agent{

	private State currentState;
	private Phase currentPhase;
	QTable qtable;
	
	public AnaGodoyRL() {
		super("AnaGodoyRL");
		qtable = new QTable();
		reset();
	}
	
	public void integrateObservation(Environment environment) {
		currentState = new State(currentState);	//si no hago esto en la tabla se modifica el objeto y da problemas (mirar main de Qtable parte2)
		//currentState = new State();	//esto no funcionaría porque perderíamos los valores de last..
		currentState.update(environment);
		
		if(currentPhase == Phase.TRAINING) {
			qtable.updateQvalue(currentState.getReward(), currentState);
		}
		else if(currentPhase == Phase.INITIAL && currentState.isOnGround()) {
			currentPhase = Phase.TRAINING;
			qtable.setPrevState(new State(currentState));
		}
	}
	
	public boolean[] getAction() {
		int actionNumber = qtable.getNextAction(currentState);
		action = Action.getAction(actionNumber);
		boolean chorizo = currentState.isCanJump() || !currentState.isOnGround();
		
		//System.out.println(Action.values()[actionNumber].toString());
		
		if(Action.values()[actionNumber].toString().contains("JUMP") &&
				Action.values()[actionNumber].toString().contains("FIRE") && chorizo) {
			action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = currentState.isCanJump() || !currentState.isOnGround();
			return action;
		}
		else if(Action.values()[actionNumber].toString().contains("JUMP") && chorizo) {
			action[Mario.KEY_JUMP] = currentState.isCanJump() || !currentState.isOnGround();
			return action;
		}
		else if(Action.values()[actionNumber].toString().contains("FIRE") && chorizo) {
			action[Mario.KEY_SPEED] = currentState.isCanJump() || !currentState.isOnGround();
			return action;
		}
		else if((Action.values()[actionNumber].toString().contains("JUMP") ||
				Action.values()[actionNumber].toString().contains("FIRE")) && !chorizo) {
			//System.out.println("Tengo que elegir otra opción");
			return this.getAction();
		}
		
		return action;
		
	    /*action[Mario.KEY_RIGHT] = true;
	    if((currentState.isCanJump() || !currentState.isOnGround()) == true) {
	    	System.out.println("asdasd");
	    	action[Mario.KEY_JUMP] = currentState.isCanJump() || !currentState.isOnGround();
	    }
	    else {
	    	System.out.println("...........................");
	    	action[Mario.KEY_JUMP] = currentState.isCanJump() || !currentState.isOnGround();
	    }
	    //action[Mario.KEY_JUMP] = currentState.isCanJump() || !isMarioOnGround;
	    return action;*/
	}

	public void reset()	{
	    action = new boolean[Environment.numberOfKeys];
	    currentState = new State();
	    currentPhase = Phase.INITIAL;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public Phase getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(Phase currentPhase) {
		this.currentPhase = currentPhase;
	}

	public QTable getQtable() {
		return qtable;
	}

	public void setQtable(QTable qtable) {
		this.qtable = qtable;
	}
	
	public static void main(String[] args) {
		
	}

}
