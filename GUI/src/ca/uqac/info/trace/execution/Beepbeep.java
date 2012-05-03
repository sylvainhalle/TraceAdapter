package ca.uqac.info.trace.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class Beepbeep extends Execution{

	private String homeDir = "/home/aouatef/beepbeep";
	private String command ="java -jar ";
	private String pointJar = "BeepBeepValidator.jar " ;
	
	
	/**
	 * Run the traces with their LTL properties
	 * @return The execution time and memory used  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		
		
		
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(0);
			vectFiles = (Vector<String>) inputLists.get(1);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = command + homeDir + pointJar + vectFiles.get(i) +" " + vectProp.get(0) ;
				arrayResultat.add(this.timeAndMemoryExecution(strCommand));
			}
			
		}
		return arrayResultat;
	}

		/**
		 * Calculate the time execution and used memory for command
		 * @param command
		 * @return  a table that contains time execution and used memory
		 */	
	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */
	public int parseReturnValue( String value) {
		
		int out = -1 ;
		
		if (value.contains("Outcome: TRUE"))
			out = 1;
		else if (value.contains("Outcome: FALSE"))
			out = 0;
		
		return out;

	}

	@Override
	public int[] timeAndMemoryExecution(String command) {
		StringBuilder sb = new StringBuilder();
		StringBuffer out = new StringBuffer();
		int[] result = new int[3];

		long startTime =  System.currentTimeMillis();
		try {
//			Process process = Runtime.getRuntime().exec("java -jar /home/aouatef/beepbeep/BeepBeepValidator.jar  /home/aouatef/beepbeep/random0.xml  /home/aouatef/beepbeep/formule.txt");//command);
			final   Process process = Runtime.getRuntime().exec("ls");//command);
			long endTime = System.currentTimeMillis();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			System.out.println(" resultat du processus : "+ process.exitValue());
			String line = null;
			while ((line = buf.readLine()) != null) {
				sb.append(line);
				sb.append("\n");

			}
			buf.close();
			String retour = sb.toString();
			long time = endTime - startTime;
			out.append("Total elapsed time in execution of runcommand() is :")
					.append(time).append("\n");

			// Get the memory consumption
			Runtime runtime = Runtime.getRuntime();

			// run the garbage collactor
			runtime.gc();
			long memory = runtime.totalMemory() - runtime.freeMemory();
			out.append("Used memory is bytes : ").append(memory).append("\n");
			// System.out.print(out);
			// System.out.println(parseReturnValue( retour));

			result[0] = (int) time;
			result[1] = (int) memory;
			result[2] =  parseReturnValue( retour);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
