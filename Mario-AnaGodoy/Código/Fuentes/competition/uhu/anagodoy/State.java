package competition.uhu.anagodoy;

import java.io.Serializable;
import java.util.Arrays;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Describe one Mario frame.
 * @author ana
 *
 */
@SuppressWarnings("serial")
public class State implements Serializable{
	
	private int stuckCount, isStuck;
	
	private int killsTotal, lastKillsTotal;
	
	private boolean[] obstacles;
	
	private boolean[][] nearbyEnemies;
	
	private int collisionsWithCreatures, lastCollisionsWithCreatures;
	
	private int dDistance, lastDistance;
	
	private int mode, lastMode;
	
	private int status;
	
	private boolean canJump, onGround;
	
	public State() {
		stuckCount = isStuck = 0;
		killsTotal = lastKillsTotal = 0;
		obstacles = new boolean[4];
		nearbyEnemies = new boolean[4][4];		
		collisionsWithCreatures = lastCollisionsWithCreatures = 0;
		dDistance = lastDistance = 0;
		mode = lastMode = 2;
		status = 2;
		canJump = onGround = false;
	}
	
	public State(State s) {
		this.stuckCount = s.getStuckCount();
		this.isStuck = s.getIsStuck();
		this.killsTotal = s.getKillsTotal();
		this.lastKillsTotal = s.getLastKillsTotal();
		this.obstacles = s.getObstacles();
		this.nearbyEnemies = s.getNearbyEnemies();
		this.collisionsWithCreatures = s.getCollisionsWithCreatures();
		this.lastCollisionsWithCreatures = s.getLastCollisionsWithCreatures();
		this.dDistance = s.getdDistance();
		this.lastDistance = s.getLastDistance();
		this.mode = s.getMode();
		this.status = s.getStatus();
		this.canJump = s.isCanJump();
		this.onGround = s.isOnGround();
	}


	/**
	 * Update de environment with the given environment
	 * @param environment The actual perception of the environment
	 */
	public void update(Environment environment) {
		
		byte[][] enemies = environment.getEnemiesObservationZ(Params.ZOOM_LEVEL);
		byte[][] levelScene = environment.getLevelSceneObservationZ(Params.ZOOM_LEVEL);
		
		// IS STUCK
		int distance = environment.getEvaluationInfo().distancePassedPhys;
		dDistance = distance - lastDistance;
		if (Math.abs(dDistance) <= Params.MIN_DISTANCE) {
			dDistance = 0;
		}
		lastDistance = distance;
		
		if (dDistance == 0) {
			stuckCount++;
		} 
		else {
			stuckCount = 0;
			isStuck = 0;
		}
		if (stuckCount >= Params.NUM_STUCK_FRAMES) {
			isStuck = 1;
		}
		
		// ENEMIES KILLED
		killsTotal = environment.getKillsTotal() - lastKillsTotal;
		lastKillsTotal = environment.getKillsTotal();
				
		
		// COLLISIONS WITH CREATURES
		collisionsWithCreatures = environment.getEvaluationInfo().collisionsWithCreatures - lastCollisionsWithCreatures;
		lastCollisionsWithCreatures = environment.getEvaluationInfo().collisionsWithCreatures;
		
		// OBSTACLES IN FRONT OF MARIO
		obstacles = new boolean[4];
		for (int i=0; i<obstacles.length; i++) {
		      if (isObstacle(Params.MARIO_X + 1, Params.MARIO_Y - i + 1, levelScene)) {
		        obstacles[i] = true;
		      }
		}
		
		// ENEMIES AROUND MARIO
		nearbyEnemies = new boolean[4][4];
		int col = -1, row = -2;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nearbyEnemies[i][j] = isEnemy(Params.MARIO_X + col, Params.MARIO_Y + row, enemies);
				col++;
			}
			System.out.println();
			col = -1;
			row++;
		}
		
		// canJump & onGround
		canJump = environment.isMarioAbleToJump();
		onGround = environment.isMarioOnGround();
		
		// Mario mode & status
		lastMode = mode;
		mode = environment.getMarioMode();			
		status = environment.getMarioStatus();
	}
	
	/**
	 * 
	 * @param x The x coordinate of the obstacle.
	 * @param y The y coordinate of the obstacle.
	 * @return True whether there are an enemy false if not.
	 */
	private boolean isObstacle(int x, int y, byte[][] levelScene) {
	    switch(levelScene[y][x]) {
	      case GeneralizerLevelScene.BRICK:
	      case GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH:
	      case GeneralizerLevelScene.FLOWER_POT_OR_CANNON:
	      case GeneralizerLevelScene.LADDER:
	        return true;
	    }
	    return false;
	}
	
	private boolean isEnemy(int x, int y, byte[][] enemies) {
		switch (enemies[y][x]) {
		case Sprite.KIND_FIRE_FLOWER:
		case Sprite.KIND_GOOMBA:
		case Sprite.KIND_SPIKY:			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Calculate the reward of take an action in a state.
	 * @return The reward for the action.
	 */
	public double getReward() {
		
		int collisionReward = (collisionsWithCreatures==0)?(Params.REWARDS.COLLISION):(Params.REWARDS.COLLISION * (-1));
		//int collisionReward = (collisionsWithCreatures==0)?50:(50 * (-1) * collisionsWithCreatures);
		int modeReward = (mode > lastMode)?Params.REWARDS.MODE:0;
		int statusReward = 0;
		switch (status) {
			case 0: statusReward = Params.REWARDS.STATUS * (-1);
					break;
			case 1: statusReward = Params.REWARDS.STATUS;
					break;
		}
		
		double reward = collisionReward + 
				modeReward + 
				statusReward + 
				(dDistance * Params.REWARDS.DISTANCE) + 
				(isStuck * Params.REWARDS.STUCK) + 
				(killsTotal * Params.REWARDS.ENEMIES_KILLED);
		
		return reward;
	}


	public int getStuckCount() {
		return stuckCount;
	}


	public void setStuckCount(int stuckCount) {
		this.stuckCount = stuckCount;
	}


	public int getIsStuck() {
		return isStuck;
	}


	public void setIsStuck(int isStuck) {
		this.isStuck = isStuck;
	}


	public int getKillsTotal() {
		return killsTotal;
	}


	public void setKillsTotal(int killsTotal) {
		this.killsTotal = killsTotal;
	}


	public int getLastKillsTotal() {
		return lastKillsTotal;
	}


	public void setLastKillsTotal(int lastKillsTotal) {
		this.lastKillsTotal = lastKillsTotal;
	}


	public boolean[] getObstacles() {
		return obstacles;
	}


	public void setObstacles(boolean[] obstacles) {
		this.obstacles = obstacles;
	}


	public boolean[][] getNearbyEnemies() {
		return nearbyEnemies;
	}


	public void setNearbyEnemies(boolean[][] nearbyEnemies) {
		this.nearbyEnemies = nearbyEnemies;
	}


	public int getCollisionsWithCreatures() {
		return collisionsWithCreatures;
	}


	public void setCollisionsWithCreatures(int collisionsWithCreatures) {
		this.collisionsWithCreatures = collisionsWithCreatures;
	}


	public int getLastCollisionsWithCreatures() {
		return lastCollisionsWithCreatures;
	}


	public void setLastCollisionsWithCreatures(int lastCollisionsWithCreatures) {
		this.lastCollisionsWithCreatures = lastCollisionsWithCreatures;
	}


	public int getdDistance() {
		return dDistance;
	}


	public void setdDistance(int dDistance) {
		this.dDistance = dDistance;
	}


	public int getLastDistance() {
		return lastDistance;
	}


	public void setLastDistance(int lastDistance) {
		this.lastDistance = lastDistance;
	}

	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isCanJump() {
		return canJump;
	}

	public void setCanJump(boolean canJump) {
		this.canJump = canJump;
	}
	
	
	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getLastMode() {
		return lastMode;
	}

	public void setLastMode(int lastMode) {
		this.lastMode = lastMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canJump ? 1231 : 1237);
		result = prime * result + collisionsWithCreatures;
		result = prime * result + dDistance;
		result = prime * result + isStuck;
		result = prime * result + killsTotal;
		/*result = prime * result + lastCollisionsWithCreatures;
		result = prime * result + lastDistance;
		result = prime * result + stuckCount;
		result = prime * result + lastKillsTotal;*/
    	result = prime * result + Arrays.deepHashCode(nearbyEnemies);
		result = prime * result + Arrays.hashCode(obstacles);
		result = prime * result + (onGround ? 1231 : 1237);
		result = prime * result + mode;
		result = prime * result + status;
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof State))
			return false;
		
		State other = (State) obj;
		if (canJump != other.canJump)
			return false;
		if (collisionsWithCreatures != other.collisionsWithCreatures)
			return false;
		if (dDistance != other.dDistance)
			return false;
		if (isStuck != other.isStuck)
			return false;
		if (killsTotal != other.killsTotal)
			return false;
		//aquí empieza
		/*if (lastCollisionsWithCreatures != other.lastCollisionsWithCreatures)
			return false;
		if (lastDistance != other.lastDistance)
			return false;
		if (lastKillsTotal != other.lastKillsTotal)
			return false;
		if (stuckCount != other.stuckCount)
			return false;*/
		//aquí termina
		if (!Arrays.deepEquals(nearbyEnemies, other.nearbyEnemies))
			return false;
		if (!Arrays.equals(obstacles, other.obstacles))
			return false;
		if (onGround != other.onGround)
			return false;
		if (mode != other.mode)
			return false;
		if (status != other.status)
			return false;
		
		return true;
	}
	
	public String parse() {
		String newLine = "\n";
		String state = "";
		
		state += Integer.toString(stuckCount) + newLine;
		state += Integer.toString(isStuck) + newLine;
		state += Integer.toString(killsTotal) + newLine;
		state += Integer.toString(lastKillsTotal) + newLine;
		state += Integer.toString(collisionsWithCreatures) + newLine;
		state += Integer.toString(lastCollisionsWithCreatures) + newLine;
		state += Integer.toString(dDistance) + newLine;
		state += Integer.toString(lastDistance) + newLine;
		state += Integer.toString(mode) + newLine;
		state += Integer.toString(lastMode) + newLine;
		state += Integer.toString(status) + newLine;
		state += Boolean.toString(canJump) + newLine;		
		state += Boolean.toString(onGround) + newLine;	
		state += Arrays.toString(obstacles) + newLine;
		
		/*String aux = "";
		for(int i=0; i<obstacles.length; i++) {
			aux += obstacles[i] + ",";
		}
		aux += newLine;
		state += aux;*/
		
		String aux = "";
		for(int i=0; i<nearbyEnemies.length; i++) {
			for(int j=0; j<nearbyEnemies[i].length; j++) {
				aux += nearbyEnemies[i][j] + ",";
			}
		}
		
		aux += newLine;
		state += aux;	
		
		return state;
	}
	
	

	@Override
	public String toString() {
		return "State [stuckCount=" + stuckCount + ", isStuck=" + isStuck + ", killsTotal=" + killsTotal
				+ ", lastKillsTotal=" + lastKillsTotal + ", obstacles=" + Arrays.toString(obstacles)
				+ ", nearbyEnemies=" + Arrays.toString(nearbyEnemies) + ", collisionsWithCreatures="
				+ collisionsWithCreatures + ", lastCollisionsWithCreatures=" + lastCollisionsWithCreatures
				+ ", dDistance=" + dDistance + ", lastDistance=" + lastDistance + ", mode=" + mode + ", lastMode="
				+ lastMode + ", status=" + status + ", canJump=" + canJump + ", onGround=" + onGround + "]";
	}

	public static void main(String[] args) {
		/*State s1 = new State();
		State s2 = new State();*/
		
		/*State prev = new State();
		
		
		prev = new State(s1);
		System.out.println((s1 == prev) + "(false)");	//compara referencias no valores
		System.out.println(s1.equals(prev)+ "(true)");
		
		
		
		
		System.out.println((s1 == s2) + "(false)");	//compara referencias no valores
		System.out.println(s1.equals(s2)+ "(true)");
		
		s1.setCanJump(true);
		System.out.println((s1 == s2) + "(false)");
		System.out.println(s1.equals(s2) + "(false)");		
		
		s2.setCanJump(true);
		System.out.println((s1 == s2) + "(false)");	//compara referencias no valores
		System.out.println(s1.equals(s2)+ "(true)");
		
		System.out.println("s1 hashCode: " + s1.hashCode());
		System.out.println("s2 hashCode: " + s2.hashCode());
		System.out.println((s1.hashCode() == s2.hashCode()) + "(true)");
		
		s1.setCollisionsWithCreatures(1);
		System.out.println("s1 reward: " + s1.getReward());		
		System.out.println("s2 reward: " + s2.getReward());
		*/		
		
	}
	
	
}
