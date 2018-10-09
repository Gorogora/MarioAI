package competition.uhu.anagodoy;

public enum Phase {
	INITIAL, TRAINING, EVALUATION;
	
	public static void main(String[] args) {
		Phase phase = Phase.INITIAL;
		System.out.println(phase);
		
		int i =2;
		
		if(i==2) {
			phase = Phase.TRAINING;
		}
		System.out.println(phase);
	}
}
