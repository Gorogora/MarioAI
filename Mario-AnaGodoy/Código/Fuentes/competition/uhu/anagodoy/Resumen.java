package competition.uhu.anagodoy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Write a csv file with the most important information about the evaluation.
 * @author ana
 *
 */
public class Resumen {
	
	private BufferedWriter bw;
	private final String header = "distancia recorrida,monedas,tiempo empleado,estados generados,ganada";
	private final String separator = ",";
	
	public Resumen() {
		try {
			File f = new File(Params.RESUMEN_PATH);
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(header);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void concat(int ditance, int coins, int time_spent, int size, int win) throws IOException {
		bw.write(Integer.toString(ditance) + separator);
		bw.write(Integer.toString(coins) + separator);
		bw.write(Integer.toString(time_spent) + separator);
		bw.write(Integer.toString(size) + separator);
		bw.write(Integer.toString(win) + separator);
		bw.newLine();
	}
	
	public void close() throws IOException {
		bw.close();
	}

}
